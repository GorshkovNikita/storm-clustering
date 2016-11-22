package diploma;

import diploma.bolts.PrinterBolt;
import diploma.bolts.WindowBolt;
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
 * Топология Apache Storm
 * @author Никита
 */
public class Topology {
    private static final Logger LOG = LoggerFactory.getLogger(Topology.class);
    private int numWorkers;
    private StartupType startupType;
    private SpoutCreator spoutCreator;

    public Topology(int numWorkers, StartupType startupType, SpoutCreator spoutCreator) {
        this.numWorkers = numWorkers;
        this.startupType = startupType;
        this.spoutCreator = spoutCreator;
    }

    public void submit() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        IRichSpout spout = spoutCreator.createSpout();
        topologyBuilder.setSpout("spout", spout, numWorkers);
        topologyBuilder.setBolt("printerBolt", new PrinterBolt(), numWorkers).shuffleGrouping("spout");
        topologyBuilder.setBolt("windowBolt", new WindowBolt()
                .withWindow(
                        // размер окна чуть-чуть больше, чем настройка TOPOLOGY_TICK_TUPLE_FREQ_SECS для болта,
                        // чтобы все микрокластера поппадали в него
                        new BaseWindowedBolt.Duration(11, TimeUnit.SECONDS),
                        new BaseWindowedBolt.Duration(11, TimeUnit.SECONDS))
                // parallelism hint ставим равным 1, чтобы все микрокластера обрабатывались в одном месте
                , 1).shuffleGrouping("printerBolt");

        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxSpoutPending(4000);
        conf.setNumWorkers(numWorkers);
        StormTopology topology = topologyBuilder.createTopology();

        if (startupType == StartupType.LOCAL) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("clustering", conf, topology);
            Utils.sleep(60000);
            cluster.killTopology("clustering");
            cluster.shutdown();
        }
        else if (startupType == StartupType.CLUSTER)
            StormSubmitter.submitTopology("clustering", conf, topology);
    }
}
