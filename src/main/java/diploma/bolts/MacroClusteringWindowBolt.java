package diploma.bolts;


import diploma.MacroClusteringStatistics;
import diploma.clustering.MapUtil;
import diploma.clustering.dbscan.ClustersDbscan;
import diploma.clustering.dbscan.points.DbscanClustersCluster;
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

import java.util.*;

/**
 * Обработчик, работающий как окно, собирающий все микрокластера всех
 * обработчиков {@link diploma.bolts.MicroClusteringBolt} и создающий новые глобальные кластера
 * По идее должен обновлять данные в in-memory data grid (hazelcast, например)
 * @author Никита
 */
public class MacroClusteringWindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MacroClusteringWindowBolt.class);
    private OutputCollector collector;
    private ClustersDbscan clustersDbscan;
    private static int executeCounter = 0;
    private static int statisticsCounter = 1;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        clustersDbscan = new ClustersDbscan(3, 0.4);
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<DbscanPoint> incomingPoints = new ArrayList<>();
        for (Tuple tuple : inputWindow.get()) {
            incomingPoints.add((DbscanPoint) tuple.getValue(0));
        }
        clustersDbscan.run(incomingPoints);
        // т.к окно вызывается каждые 30 секунд, то для сохранения статистики каждые 5 минут нужно каждые 10 раз вызывать emit
        if (++executeCounter % 10 == 0) {
            for (DbscanClustersCluster cluster: clustersDbscan.getClustering().getClusters())
                collector.emit(new Values(getClusterStatistics(cluster)));
            statisticsCounter++;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("statistics"));
    }

    private MacroClusteringStatistics getClusterStatistics(DbscanClustersCluster cluster) {
        MacroClusteringStatistics statistics = new MacroClusteringStatistics();
        int totalNumberOfDocuments = 0;
        Map<String, Integer> topTenTerms = new HashMap<>();
        for (DbscanStatusesCluster statusesCluster: cluster.getAssignedPoints()) {
            totalNumberOfDocuments += statusesCluster.getTfIdf().getDocumentNumber();
            for (Map.Entry<String, Integer> entry: statusesCluster.getTfIdf().getTermFrequencyMap().entrySet()) {
                topTenTerms.merge(entry.getKey(), entry.getValue(), (num1, num2) -> num1 + num2);
            }
        }
        topTenTerms = MapUtil.putFirstEntries(10, MapUtil.sortByValue(topTenTerms));
        statistics.setId(statisticsCounter);
        statistics.setClusterId(cluster.getId());
        statistics.setNumberOfDocuments(totalNumberOfDocuments);
        statistics.setTopTenTerms(topTenTerms);
        return statistics;
    }
}
