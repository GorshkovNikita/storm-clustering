package diploma.points;

import diploma.clustering.Point;
import diploma.clustering.VectorOperations;
import diploma.clustering.clusters.PointsCluster;
import diploma.clustering.clusters.PointsClustering;
import diploma.clustering.clusters.StatusesClustering;
import diploma.clustering.dbscan.points.DbscanSimplePoint;
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
 * @author Никита
 */
public class PointsMicroClusteringBolt extends BaseBasicBolt {
    private final Logger LOG = LoggerFactory.getLogger(PointsMicroClusteringBolt.class);
    private PointsClustering microClustering;
    private static final int MIN_POINTS = 30;
    private static final int MAX_CLUSTERS = 100;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        microClustering = new PointsClustering(2500.0);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        Point p = (Point) tuple.getValueByField("point");
        Point zeroPoint = new Point(new Double[p.getCoordinatesVector().length]);
        for (int i = 0; i < zeroPoint.getCoordinatesVector().length; i++)
            zeroPoint.getCoordinatesVector()[i] = 0.0;
        LOG.info("euclidean distance = " + VectorOperations.euclideanDistance(p.getCoordinatesVector(), zeroPoint.getCoordinatesVector()));
//        if (isTickTuple(tuple)) {
//            List<PointsCluster> clustersForRemove = new ArrayList<>();
//            // По факту это выделение potential micro clusters, как в статье, только
//            // не учитывается вес кластера в зависимости от времени.
//            // Остальные кластера можно считать outlier micro clusters
//            List<PointsCluster> bigClusters = microClustering.getClusters()
//                    .stream()
//                    .filter((cluster) -> cluster.getAssignedPoints().size() > MIN_POINTS)
//                    .collect(Collectors.toList());
//            int numberOfClusters = 0;
//            for (PointsCluster cluster: microClustering.getClusters()) {
//                // все potential micro clusters не отправляются дальше в целях оптимизации
//                if (numberOfClusters < MAX_CLUSTERS && bigClusters.contains(cluster)) {
//                    numberOfClusters++;
//                    // считаем центр масс перед удалением точек
//                    cluster.getCenterOfMass();
//                    cluster.getAssignedPoints().clear();
//                    collector.emit(new Values(cluster));
//                    clustersForRemove.add(cluster);
//                }
//                // удаляются старые микрокластера
//                else if (microClustering.getTimestamp() - cluster.getLastUpdateTime() > 25000)
//                    clustersForRemove.add(cluster);
//            }
//            for (PointsCluster cluster: clustersForRemove)
//                microClustering.getClusters().remove(cluster);
//        }
//        else {
//            // для KafkaSpout field name = str
//            DbscanSimplePoint point = (DbscanSimplePoint) tuple.getValueByField("str");
//            if (point != null) microClustering.processNext(point);
//        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
//        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 30);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        declarer.declare(new Fields("microClusters"));
    }
}
