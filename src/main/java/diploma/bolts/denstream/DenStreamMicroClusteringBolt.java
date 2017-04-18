package diploma.bolts.denstream;

import diploma.bolts.mydenstream.MyDenStreamMicroClusteringBolt;
import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.MapUtil;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.points.SimplifiedDbscanStatusesCluster;
import diploma.statistics.dao.TweetDao;
import org.apache.commons.lang.SerializationUtils;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.shade.org.apache.commons.exec.util.MapUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Никита
 */
public class DenStreamMicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MyDenStreamMicroClusteringBolt.class);
    private DenStream denStream;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        denStream = new DenStream(10, 10, 5.0, 0.0000001, 0.4);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            // освобождаем чуток памяти
            for (StatusesCluster cluster : denStream.getPotentialMicroClustering().getClusters()) {
                if (cluster.getTfIdf().getTermFrequencyMap().size() > 1000)
                    cluster.getTfIdf().setTermFrequencyMap(MapUtil.putFirstEntries(1000, MapUtil.sortByValue(cluster.getTfIdf().getTermFrequencyMap())));
            }

            for (StatusesCluster cluster : denStream.getOutlierMicroClustering().getClusters()) {
                if (cluster.getTfIdf().getTermFrequencyMap().size() > 1000)
                    cluster.getTfIdf().setTermFrequencyMap(MapUtil.putFirstEntries(1000, MapUtil.sortByValue(cluster.getTfIdf().getTermFrequencyMap())));
            }

            for (StatusesCluster cluster : denStream.getPotentialMicroClustering().getClusters()) {
                StatusesCluster clusterCopy = (StatusesCluster) SerializationUtils.clone(cluster);
                clusterCopy.getTfIdf().setTermFrequencyMap(
                        // there is no need to sort, because it was already done above
                        MapUtil.putFirstEntries(20, clusterCopy.getTfIdf().getTermFrequencyMap()));
                collector.emit(new Values(clusterCopy));
                cluster.resetProcessedPerTimeUnit();
            }

            for (StatusesCluster cluster : denStream.getOutlierMicroClustering().getClusters())
                cluster.resetProcessedPerTimeUnit();
        }
        else denStream.processNext((EnhancedStatus) tuple.getValueByField("status"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("microClusters"));
    }
}
