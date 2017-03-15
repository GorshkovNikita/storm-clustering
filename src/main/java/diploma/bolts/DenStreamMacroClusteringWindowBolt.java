package diploma.bolts;


import diploma.clustering.clusters.ClustersCluster;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.ClustersDbscan;
import diploma.clustering.dbscan.points.DbscanPoint;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
import diploma.dao.TweetDao;
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
 * обработчиков {@link DenStreamMicroClusteringBolt} и создающий новые глобальные кластера
 * По идее должен обновлять данные в in-memory data grid (hazelcast, например)
 * @author Никита
 */
public class DenStreamMacroClusteringWindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMacroClusteringWindowBolt.class);
    private OutputCollector collector;
    private ClustersDbscan clustersDbscan;
    private static long executeCounter = 0;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        clustersDbscan = new ClustersDbscan(3, 0.7);
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<DbscanPoint> incomingPoints = new ArrayList<>();
        for (Tuple tuple : inputWindow.get()) {
            DbscanStatusesCluster cluster = ((DbscanStatusesCluster) tuple.getValue(0));
            incomingPoints.add(cluster);
            cluster.setLastUpdateTime(executeCounter);
        }
        // удаляем старые точки из кластеров
        for (ClustersCluster cluster : clustersDbscan.getClustering().getClusters()) {
            List<DbscanStatusesCluster> removalList = new ArrayList<>();
            for (DbscanStatusesCluster statusesCluster : cluster.getAssignedPoints()) {
                // 600 - примерно 5 часов
                if (statusesCluster.getLastUpdateTime() > executeCounter * 600)
                    removalList.add(statusesCluster);
            }
            for (DbscanStatusesCluster statusesCluster : removalList) {
                cluster.getAssignedPoints().remove(statusesCluster);
            }
        }
        long start = System.currentTimeMillis();
        clustersDbscan.run(incomingPoints);
        LOG.info("Количество микрокластеров = " + incomingPoints.size());
        LOG.info("Время выполнения dbscan на " + executeCounter + "-й итерации:" + ((double) System.currentTimeMillis() - (double) start) / 1000.0);
        // т.к окно вызывается каждые 30 секунд, то для сохранения статистики каждые 5 минут нужно каждые 10 раз вызывать emit
        if (++executeCounter % 20 == 0)
            collector.emit(new Values(new ArrayList<>(clustersDbscan.getClustering().getClusters())));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("macroClusters"));
    }
}
