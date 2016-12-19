package diploma.bolts;

import diploma.MacroClusteringStatistics;
import diploma.clustering.MapUtil;
import diploma.clustering.dbscan.points.DbscanClustersCluster;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
import diploma.dao.MacroClusteringStatisticsDao;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Никита
 */
public class StatisticsBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsBolt.class);
    private OutputCollector collector;
    private MacroClusteringStatisticsDao macroClusteringStatisticsDao;
    private static int statisticsCounter = 1;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        macroClusteringStatisticsDao = new MacroClusteringStatisticsDao();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        List<DbscanClustersCluster> macroClusters = (List<DbscanClustersCluster>) input.getValue(0);
        for (DbscanClustersCluster cluster: macroClusters)
            macroClusteringStatisticsDao.saveStatistics(getClusterStatistics(cluster));
        statisticsCounter++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {}

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
