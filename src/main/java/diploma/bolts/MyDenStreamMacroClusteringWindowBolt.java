package diploma.bolts;


import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.Clustering;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.ClustersDbscan;
import diploma.clustering.dbscan.StatefulDbscan;
import diploma.clustering.dbscan.points.DbscanPoint;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
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
 * Обработчик, работающий как окно, собирающий все микрокластера всех
 * обработчиков {@link MyDenStreamMicroClusteringBolt} и создающий новые глобальные кластера
 * По идее должен обновлять данные в in-memory data grid (hazelcast, например)
 * @author Никита
 */
public class MyDenStreamMacroClusteringWindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MyDenStreamMacroClusteringWindowBolt.class);
    private OutputCollector collector;
    private StatefulDbscan dbscan;
    private static long executeCounter = 0;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.dbscan = new StatefulDbscan(3, 0.7);
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<DbscanStatusesCluster> incomingPoints = new ArrayList<>();
        for (Tuple tuple : inputWindow.get()) {
            StatusesCluster cluster = ((StatusesCluster) tuple.getValue(0));
            incomingPoints.add(new DbscanStatusesCluster(cluster));
        }

        // удаляем старые микрокластера
        List<DbscanPoint> removalList = new ArrayList<>();
        for (DbscanPoint statusesCluster : dbscan.getAllPoints())
            // каждый час
            if ((System.currentTimeMillis() - ((DbscanStatusesCluster)statusesCluster).getStatusesCluster().getLastUpdateTime()) > 3600 * 1000)
                removalList.add(statusesCluster);
        for (DbscanPoint statusesCluster : removalList)
            dbscan.getAllPoints().remove(statusesCluster);

        long start = System.currentTimeMillis();
        dbscan.run(incomingPoints);
        LOG.info("Количество микрокластеров = " + incomingPoints.size());
        LOG.info("Время выполнения dbscan на " + executeCounter + "-й итерации:" + ((double) System.currentTimeMillis() - (double) start) / 1000.0);

        // сбор кластеров по id
        Clustering<Cluster<StatusesCluster>, StatusesCluster> macroClustering = new Clustering<>();
        for (DbscanPoint point: dbscan.getAllPoints()) {
            if (point.isAssigned()) {
                if (macroClustering.findClusterById(point.getClusterId()) == null) {
                    Cluster<StatusesCluster> cluster = new Cluster<>(point.getClusterId(), 0.00001);
                    cluster.assignPoint(((DbscanStatusesCluster)point).getStatusesCluster());
                    macroClustering.addCluster(cluster);
                } else {
                    Cluster<StatusesCluster> cluster = macroClustering.findClusterById(point.getClusterId());
                    cluster.assignPoint(((DbscanStatusesCluster)point).getStatusesCluster());
                }
            }
        }
        // т.к окно вызывается каждую минуту, то для сохранения статистики каждые 5 минут нужно каждые 5 раз вызывать emit
        if (++executeCounter % 5 == 0)
            collector.emit(new Values(new ArrayList<>(macroClustering.getClusters())));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("macroClusters"));
    }
}
