package diploma.bolts.denstream;

import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.MapUtil;
import diploma.clustering.clusters.StatusesCluster;
import org.apache.commons.lang.SerializationUtils;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Никита
 */
public class DenStreamMicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMicroClusteringBolt.class);
    private DenStream denStream;
    private long timeOfFirstTweet = 0;
    private long lastEmitTime = 0;
    private int msgProcessedPerTimeUnit = 0;
    private int taskId;
    private int currentTimestamp;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        denStream = new DenStream(10, 10, 5.0, 0.000005, 0.4);
        this.taskId = context.getThisTaskId();
        super.prepare(stormConf, context);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        EnhancedStatus status = (EnhancedStatus) tuple.getValueByField("status");
        if (timeOfFirstTweet == 0) timeOfFirstTweet = status.getCreationDate().getTime(); // status.getStatus().getCreatedAt().getTime();
//        if (isTickTuple(tuple)) {
        if (checkEmitTime(status.getCreationDate().getTime())) { //status.getStatus().getCreatedAt().getTime())) {
            List<StatusesCluster> microClusters = new ArrayList<>();
            LOG.info(new Date(lastEmitTime).toString());
            LOG.info("Messages processed = " + msgProcessedPerTimeUnit);
            msgProcessedPerTimeUnit = 0;
            for (StatusesCluster cluster : denStream.getPotentialMicroClustering().getClusters()) {
                // free some memory
                if (cluster.getTfIdf().getTermFrequencyMap().size() > 1000)
                    cluster.getTfIdf().setTermFrequencyMap(MapUtil.putFirstEntries(1000, MapUtil.sortByValue(cluster.getTfIdf().getTermFrequencyMap())));
                StatusesCluster clusterCopy = (StatusesCluster) SerializationUtils.clone(cluster);
                clusterCopy.getTfIdf().setTermFrequencyMap(
                        MapUtil.putFirstEntries(20, MapUtil.sortByValue(clusterCopy.getTfIdf().getTermFrequencyMap())));
                clusterCopy.setId(clusterCopy.getId() + this.taskId * 1000);
                microClusters.add(clusterCopy);
//                collector.emit(new Values(clusterCopy));
                cluster.resetProcessedPerTimeUnit();
            }

            for (StatusesCluster cluster : denStream.getOutlierMicroClustering().getClusters()) {
                if (cluster.getTfIdf().getTermFrequencyMap().size() > 1000)
                    cluster.getTfIdf().setTermFrequencyMap(MapUtil.putFirstEntries(1000, MapUtil.sortByValue(cluster.getTfIdf().getTermFrequencyMap())));
                cluster.resetProcessedPerTimeUnit();
            }

            collector.emit(new Values(microClusters));
        }
        else {
            currentTimestamp++;
            msgProcessedPerTimeUnit++;
            denStream.processNext(status);
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
//        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    /**
     * Проверка времени создания твита, если разница между этим временем
     * и временем первого пришедшего твита кратна 5 минутам, то вернуть true
     * Дается 10 секундный запас, если не было твитов какое-то время (т.е максимум 10 секунд)
     * @param statusTime - время создания твита
     * @return - true/false
     */
    private boolean checkEmitTime(long statusTime) {
        boolean result = false;
        if ((statusTime - timeOfFirstTweet) % 60000 >= 0 && (statusTime - timeOfFirstTweet) % 60000 <= 4000 &&
                (statusTime - lastEmitTime > 4000)) {
            result = true;
            lastEmitTime = statusTime;
        }
        return result;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("microClusters"));
    }
}
