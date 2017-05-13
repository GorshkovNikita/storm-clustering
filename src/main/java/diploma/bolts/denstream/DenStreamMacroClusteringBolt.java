package diploma.bolts.denstream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.MapUtil;
import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.Clustering;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.Dbscan;
import diploma.clustering.dbscan.points.DbscanPoint;
import diploma.clustering.dbscan.points.DbscanStatusesCluster;
import diploma.clustering.dbscan.points.SimplifiedDbscanStatusesCluster;
import diploma.statistics.dao.MacroClusteringTaskDao;
import org.apache.commons.lang.SerializationUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author Никита
 */
public class DenStreamMacroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMacroClusteringBolt.class);
    private int numWorkers;
    private List<SimplifiedDbscanStatusesCluster> microClusters;
    /**
     * Переменная, равная количеству инстансов, на которые распараллелены микрокластера.
     * Нужна для того, чтобы понять, когда можно проводить макрокластеризацию
     */
    private int listsReceived = 0;
    private Dbscan dbscan;
    private Map<Integer, Integer> macroClusterIds;
    private int minNumberOfCommonTerms;
    private int totalProcessedTweets;
    private int totalNumberOfFiltered;
    private int totalProcessedTweetsBefore;
    private long lastTime;
    private int numberOfMicroClusters;
    private double rate;
    private int previousTaskId;
    private MacroClusteringTaskDao dao;

    public DenStreamMacroClusteringBolt(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.microClusters = new ArrayList<>();
        this.macroClusterIds = new HashMap<>();
        this.minNumberOfCommonTerms = 6;
        this.dbscan = new Dbscan(numWorkers - 1, 0.6);
        this.totalProcessedTweets = 0;
        this.previousTaskId = 0;
        this.dao = new MacroClusteringTaskDao();
        this.numberOfMicroClusters = 0;
        this.totalNumberOfFiltered = 0;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        List<StatusesCluster> newMicroClusters = (List<StatusesCluster>) tuple.getValueByField("microClusters");
        for (StatusesCluster cluster : newMicroClusters)
            microClusters.add(new SimplifiedDbscanStatusesCluster(cluster, minNumberOfCommonTerms,
                    macroClusterIds.get(cluster.getId()) == null ? 0 : macroClusterIds.get(cluster.getId())));
//            microClusters.add(new DbscanStatusesCluster(cluster,
//                    macroClusterIds.get(cluster.getId()) == null ? 0 : macroClusterIds.get(cluster.getId())));
        int numberOfTuples = (Integer) tuple.getValueByField("totalProcessedTweets");
        int numberOfFilteredTuples = (Integer) tuple.getValueByField("numberOfFiltered");
        totalProcessedTweets += numberOfTuples;
        totalNumberOfFiltered += numberOfFilteredTuples;
        dao.saveMacroClusteringTaskId(tuple.getSourceTask(), numberOfTuples);
        numberOfMicroClusters += newMicroClusters.size();
//        LOG.info("task id = " + tuple.getSourceTask());
        if (++listsReceived == numWorkers) {
            dbscan.run(microClusters);
            Clustering<Cluster<StatusesCluster>, StatusesCluster> macroClustering = new Clustering<>();
            for (SimplifiedDbscanStatusesCluster point: microClusters) {
                // поле clusterId от point записывается в dbscan.run()
                if (!point.isNoise()) {
                    Cluster<StatusesCluster> clusterById = macroClustering.findClusterById(point.getClusterId());
                    if (clusterById == null) {
                        Cluster<StatusesCluster> cluster = new Cluster<>(point.getClusterId(), 0.00001);
                        cluster.assignPoint(point.getStatusesCluster());
                        macroClustering.addCluster(cluster);
//                        macroClusterIds.put(cluster.getId(), tuple.getSourceTask(), point.getClusterId());
                        macroClusterIds.put(point.getStatusesCluster().getId(), point.getClusterId());
                    }
                    else {
                        macroClusterIds.put(point.getStatusesCluster().getId(), point.getClusterId());
                        clusterById.assignPoint(point.getStatusesCluster());
                    }
                }
            }
            rate = (totalProcessedTweets - totalProcessedTweetsBefore) / (double) (System.currentTimeMillis() - lastTime) * 1000;
            lastTime = System.currentTimeMillis();
            totalProcessedTweetsBefore = totalProcessedTweets;
            collector.emit(new Values(SerializationUtils.clone((Serializable) macroClustering.getClusters()), totalProcessedTweets, rate, numberOfMicroClusters, totalNumberOfFiltered));
            LOG.info("number of macro clusters = " + macroClustering.getClusters().size());
            listsReceived = 0;
            totalProcessedTweets = 0;
            microClusters.clear();
            previousTaskId = 0;
            numberOfMicroClusters = 0;
            totalNumberOfFiltered = 0;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("microCluster", "totalProcessedTweets", "rate", "numberOfMicroClusters", "totalNumberOfFiltered"));
    }
}
