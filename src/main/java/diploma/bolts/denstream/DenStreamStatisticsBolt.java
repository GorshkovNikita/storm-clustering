package diploma.bolts.denstream;

import diploma.clustering.MapUtil;
import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.StatusesCluster;
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
public class DenStreamStatisticsBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamStatisticsBolt.class);
    private OutputCollector collector;
    private MacroClusteringStatisticsDao macroClusteringStatisticsDao;
    private static int statisticsCounter = 0;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        macroClusteringStatisticsDao = new MacroClusteringStatisticsDao();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        statisticsCounter += 5;
        Timestamp time = new Timestamp(new Date().getTime());
        List<Cluster<StatusesCluster>> macroClusters = (List<Cluster<StatusesCluster>>) input.getValue(0);
        for (Cluster<StatusesCluster> cluster: macroClusters)
            macroClusteringStatisticsDao.saveStatistics(getClusterStatistics(cluster, time));
        // TODO: возможно очищать после сохранения статистики absorbedClusterIds
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {}

    /**
     * Получение статистики по кластеру (количество документов в нем, топ-10 ключевых слов)
     * @param cluster - кластер
     * @return статистика кластера
     */
    private MacroClusteringStatistics getClusterStatistics(Cluster<StatusesCluster> cluster, Timestamp time) {
        MacroClusteringStatistics statistics = new MacroClusteringStatistics();
        int totalNumberOfDocuments = 0;
        int totalProcessedPerTimeUnit = 0;
        Map<String, Integer> topTenTerms = new HashMap<>();
        for (StatusesCluster statusesCluster: cluster.getAssignedPoints()) {
            totalNumberOfDocuments += statusesCluster.getTfIdf().getDocumentNumber();
            totalProcessedPerTimeUnit += statusesCluster.getProcessedPerTimeUnit();
            for (Map.Entry<String, Integer> entry: statusesCluster.getTfIdf().getTermFrequencyMap().entrySet())
                topTenTerms.merge(entry.getKey(), entry.getValue(), (num1, num2) -> num1 + num2);
        }
        topTenTerms = MapUtil.putFirstEntries(10, MapUtil.sortByValue(topTenTerms));
        statistics.setTimestamp(time);
        statistics.setTimeFactor(statisticsCounter);
        statistics.setClusterId(cluster.getId());
        statistics.setNumberOfDocuments(totalNumberOfDocuments);
        statistics.setTopTerms(topTenTerms);
        statistics.setTotalProcessedPerTimeUnit(totalProcessedPerTimeUnit);
        statistics.setAbsorbedClusterIds(cluster.getAbsorbedClusterIds());
        return statistics;
    }
}
