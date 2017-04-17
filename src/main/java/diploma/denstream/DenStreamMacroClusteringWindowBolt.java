package diploma.denstream;

import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.Clustering;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.Dbscan;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Никита
 */
public class DenStreamMacroClusteringWindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMacroClusteringWindowBolt.class);
    private OutputCollector collector;
    private static long executeCounter = 0;
    private Dbscan dbscan;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        dbscan = new Dbscan(3, 0.7);
        this.collector = collector;
//        clustersDbscan = new ClustersDbscan(3, 0.7);
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<DbscanStatusesCluster> incomingPoints = new ArrayList<>();
        for (Tuple tuple : inputWindow.get()) {
            StatusesCluster cluster = ((StatusesCluster) tuple.getValue(0));
            incomingPoints.add(new DbscanStatusesCluster(cluster));
        }
        long start = System.currentTimeMillis();
        dbscan.run(incomingPoints);
        LOG.info("Количество микрокластеров = " + incomingPoints.size());
        LOG.info("Время выполнения dbscan на " + executeCounter + "-й итерации:" + ((double) System.currentTimeMillis() - (double) start) / 1000.0);
        Clustering<Cluster<StatusesCluster>, StatusesCluster> macroClustering = new Clustering<>();
        for (DbscanStatusesCluster point: incomingPoints) {
            if (macroClustering.findClusterById(point.getClusterId()) == null) {
                Cluster<StatusesCluster> cluster = new Cluster<>(point.getClusterId(), 0.00001);
                cluster.assignPoint(point.getStatusesCluster());
                macroClustering.addCluster(cluster);
            }
            else {
                Cluster<StatusesCluster> cluster = macroClustering.findClusterById(point.getClusterId());
                cluster.assignPoint(point.getStatusesCluster());
            }
        }
        // т.к окно вызывается каждые 30 секунд, то для сохранения статистики каждые 5 минут нужно каждые 10 раз вызывать emit
//        if (++executeCounter % 10 == 0)
        collector.emit(new Values(new ArrayList<>(macroClustering.getClusters())));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("macroClusters"));
    }
}
