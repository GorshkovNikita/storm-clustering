package diploma.bolts;

import diploma.clustering.MapUtil;
import diploma.clustering.dbscan.points.DbscanClustersCluster;
import diploma.statistics.MacroClusteringStatistics;
import diploma.statistics.dao.MacroClusteringStatisticsDao;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
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
        // TODO: возможно очищать после сохранения статистики absorbedClusterIds
        statisticsCounter++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {}

    /**
     * Получение статистики по кластеру (количество документов в нем, топ-10 ключевых слов)
     * @param cluster - кластер
     * @return статистика кластера
     */
    private MacroClusteringStatistics getClusterStatistics(DbscanClustersCluster cluster) {
        MacroClusteringStatistics statistics = new MacroClusteringStatistics();
//        int totalNumberOfDocuments = 0;
//        Map<String, Integer> topTenTerms = new HashMap<>();
//        for (DbscanStatusesCluster statusesCluster: cluster.getAssignedPoints()) {
//            totalNumberOfDocuments += statusesCluster.getTfIdf().getDocumentNumber();
//            for (Map.Entry<String, Integer> entry: statusesCluster.getTfIdf().getTermFrequencyMap().entrySet())
//                topTenTerms.merge(entry.getKey(), entry.getValue(), (num1, num2) -> num1 + num2);
//        }
//        topTenTerms = MapUtil.putFirstEntries(10, MapUtil.sortByValue(topTenTerms));
//        statistics.setTimeFactor(new Timestamp(new Date().getTime()));
//        statistics.setClusterId(cluster.getId());
//        statistics.setNumberOfDocuments(totalNumberOfDocuments);
//        statistics.setTopTerms(topTenTerms);
//        statistics.setAbsorbedClusterIds(cluster.getAbsorbedClusterIds());
        return statistics;
    }
}
