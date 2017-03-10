package diploma.bolts;

import diploma.clustering.clusters.StatusesClustering;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
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
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик, создающий микрокластера из сообщений, поступающих конкретно ему
 * TODO: сделать отдельный bolt для получения статуса
 * @author Никита
 */
public class DenStreamMicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMicroClusteringBolt.class);
    private StatusesClustering microClustering;
    private static final int MIN_POINTS = 30;
    private static final int MAX_CLUSTERS = 100;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        microClustering = new StatusesClustering();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            List<DbscanStatusesCluster> clustersForRemove = new ArrayList<>();
            // По факту это выделение potential micro clusters, как в статье, только
            // не учитывается вес кластера в зависимости от времени.
            // Остальные кластера можно считать outlier micro clusters
            List<DbscanStatusesCluster> bigClusters = microClustering.getClusters()
                    .stream()
                    .filter((cluster) -> cluster.getAssignedPoints().size() > MIN_POINTS)
                    .collect(Collectors.toList());
            int numberOfClusters = 0;
            for (DbscanStatusesCluster cluster: microClustering.getClusters()) {
                // все potential micro clusters не отправляются дальше в целях оптимизации
                if (numberOfClusters < MAX_CLUSTERS && bigClusters.contains(cluster)) {
                    numberOfClusters++;
                    cluster.getTfIdf().sortTermFrequencyMap();
                    cluster.getAssignedPoints().clear();
                    collector.emit(new Values(cluster));
                    clustersForRemove.add(cluster);
                }
                // удаляются старые микрокластера
                else if (microClustering.getTimestamp() - cluster.getLastUpdateTime() > 25000)
                    clustersForRemove.add(cluster);
            }
            for (DbscanStatusesCluster cluster: clustersForRemove)
                microClustering.getClusters().remove(cluster);
        }
        else {
            // для KafkaSpout field name = str
            String tweetJson = tuple.getStringByField("str");
            Integer msgId = tuple.getIntegerByField("msgId");
            if (tweetJson != null) {
                try {
                    Status status = TwitterObjectFactory.createStatus(tweetJson);
                    microClustering.processNext(status);
                } catch (TwitterException ignored) {}
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
