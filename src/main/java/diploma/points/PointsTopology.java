package diploma.points;

import diploma.StartupType;
import diploma.spouts.creators.SpoutCreator;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Никита
 */
public class PointsTopology {
    private static final Logger LOG = LoggerFactory.getLogger(PointsTopology.class);
    private int numWorkers;
    private StartupType startupType;
    private SpoutCreator spoutCreator;

    public PointsTopology(int numWorkers, StartupType startupType, SpoutCreator spoutCreator) {
        this.numWorkers = numWorkers;
        this.startupType = startupType;
        this.spoutCreator = spoutCreator;
    }

    public void submit() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        IRichSpout spout = spoutCreator.createSpout();
        topologyBuilder.setSpout("spout", spout, numWorkers);

        topologyBuilder.setBolt("printer", new PointsPrintBolt(), numWorkers).shuffleGrouping("spout").addConfiguration("tags", "printer");
        topologyBuilder.setBolt("microClustering", new PointsMicroClusteringBolt(), numWorkers).shuffleGrouping("printer").addConfiguration("tags", "euclidean");
//        topologyBuilder.setBolt("macroClustering", new PointsMacroClusteringWindowBolt()
//                .withWindow(
//                    new BaseWindowedBolt.Duration(31, TimeUnit.SECONDS),
//                    new BaseWindowedBolt.Duration(31, TimeUnit.SECONDS)),
//                    1)
//                .shuffleGrouping("microClustering");

        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxSpoutPending(4000);
        conf.setNumWorkers(numWorkers);
        conf.setMessageTimeoutSecs(80);
        StormTopology topology = topologyBuilder.createTopology();

        if (startupType == StartupType.LOCAL) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("clustering", conf, topology);
            Utils.sleep(86400000);
            cluster.killTopology("clustering");
            cluster.shutdown();
        }
        else if (startupType == StartupType.CLUSTER)
            StormSubmitter.submitTopology("clustering", conf, topology);
    }
}
