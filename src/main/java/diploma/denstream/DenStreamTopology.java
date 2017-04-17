package diploma.denstream;

import diploma.StartupType;
import diploma.bolts.MyDenStreamMacroClusteringWindowBolt;
import diploma.bolts.MyDenStreamMicroClusteringBolt;
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
public class DenStreamTopology {
    private static final Logger LOG = LoggerFactory.getLogger(DenStreamTopology.class);
    private int numWorkers;
    private StartupType startupType;
    private SpoutCreator spoutCreator;

    public DenStreamTopology(int numWorkers, StartupType startupType, SpoutCreator spoutCreator) {
        this.numWorkers = numWorkers;
        this.startupType = startupType;
        this.spoutCreator = spoutCreator;
    }

    public void submit() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        IRichSpout spout = spoutCreator.createSpout();
        topologyBuilder.setSpout("spout", spout, numWorkers);
        topologyBuilder.setBolt("microClusteringBolt", new MyDenStreamMicroClusteringBolt(), numWorkers).shuffleGrouping("spout");
        topologyBuilder.setBolt("macroClusteringBolt", new MyDenStreamMacroClusteringWindowBolt()
                .withWindow(
                        new BaseWindowedBolt.Duration(60, TimeUnit.SECONDS),
                        new BaseWindowedBolt.Duration(60, TimeUnit.SECONDS))
                // parallelism hint ставим равным 1, чтобы все микрокластера обрабатывались в одном месте
                , 1).shuffleGrouping("microClusteringBolt");
        topologyBuilder.setBolt("statisticsBolt", new DenStreamStatisticsBolt(), 1).shuffleGrouping("macroClusteringBolt");

        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxSpoutPending(4000);
        conf.setNumWorkers(numWorkers);
        conf.setMessageTimeoutSecs(240);
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
