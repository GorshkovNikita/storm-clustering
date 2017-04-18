package diploma.bolts.denstream;

import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.MapUtil;
import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.Clustering;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.dbscan.Dbscan;
import diploma.clustering.dbscan.points.DbscanPoint;
import diploma.clustering.dbscan.points.SimplifiedDbscanStatusesCluster;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Никита
 */
public class DenStreamMacroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamMacroClusteringBolt.class);
    private int numWorkers;
    private List<SimplifiedDbscanStatusesCluster> microClusters;
    private int listsReceived = 0;
    private Dbscan dbscan;

    public DenStreamMacroClusteringBolt(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.microClusters = new ArrayList<>();
        this.dbscan = new Dbscan(numWorkers - 1, 0.7);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        for (StatusesCluster cluster : (List<StatusesCluster>) tuple.getValueByField("microClusters"))
            microClusters.add(new SimplifiedDbscanStatusesCluster(cluster, cluster.getMacroClusterId()));
        if (++listsReceived == numWorkers) {
            dbscan.run(microClusters);
            Clustering<Cluster<StatusesCluster>, StatusesCluster> macroClustering = new Clustering<>();
            for (SimplifiedDbscanStatusesCluster point: microClusters) {
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
            LOG.info("number of macro clusters = " + macroClustering.getClusters().size());
            listsReceived = 0;
            microClusters.clear();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("microCluster"));
    }
}
