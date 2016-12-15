package diploma.bolts;

import diploma.clustering.clusters.StatusesClustering;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик, создающий микрокластера из сообщений, поступающих конкретно ему
 * TODO: сделать отдельный bolt для получения статуса
 * @author Никита
 */
public class MicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MicroClusteringBolt.class);
    private StatusesClustering clustering = new StatusesClustering();
    private static final int MIN_POINTS = 25;

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            List<DbscanStatusesCluster> bigClusters = clustering.getClusters()
                    .stream()
                    .filter((cluster) -> cluster.getAssignedPoints().size() > MIN_POINTS)
                            // Если дополнительно для фильтра использовать: && !cluster.isVisited()), то
                            // тогда нельзя будет найти всех соседей
                    .collect(Collectors.toList());
//            clustersDbscan.run(bigClusters);
            for (DbscanStatusesCluster cluster: bigClusters) {
                cluster.getTfIdf().sortTermFrequencyMap();
                collector.emit(new Values(cluster));
                clustering.getClusters().remove(cluster);
            }
        }
        else {
            // для KafkaSpout field name = str
            String line = tuple.getStringByField("line");
            Integer msgId = tuple.getIntegerByField("msgId");
            try {
                Status status = TwitterObjectFactory.createStatus(line);
                clustering.processNext(status);
                LOG.info("msgId = " + msgId);
            } catch (TwitterException e) {
                LOG.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 30);
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
