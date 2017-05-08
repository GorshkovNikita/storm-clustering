package diploma;

import diploma.bolts.StatusesCreatingBolt;
import diploma.bolts.StatusesFilteringBolt;
import diploma.bolts.denstream.DenStreamMacroClusteringBolt;
import diploma.bolts.denstream.DenStreamMacroClusteringWindowBolt;
import diploma.bolts.denstream.DenStreamMicroClusteringBolt;
import diploma.bolts.denstream.DenStreamStatisticsBolt;
import diploma.bolts.mydenstream.MyDenStreamMacroClusteringWindowBolt;
import diploma.bolts.mydenstream.MyDenStreamMicroClusteringBolt;
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
        topologyBuilder.setSpout("spout", spout, numWorkers)
                .addConfiguration("tags", "microClustering");

        topologyBuilder.setBolt("statusesCreatingBolt", new StatusesCreatingBolt(), numWorkers)
                .localOrShuffleGrouping("spout")
                .addConfiguration("tags", "microClustering");

        topologyBuilder.setBolt("statusesFilteringBolt", new StatusesFilteringBolt(), numWorkers)
                .localOrShuffleGrouping("statusesCreatingBolt")
                .addConfiguration("tags", "microClustering");

        topologyBuilder.setBolt("microClusteringBolt", new DenStreamMicroClusteringBolt(), numWorkers)
                .localOrShuffleGrouping("statusesFilteringBolt")
                .addConfiguration("tags", "microClustering");

        topologyBuilder.setBolt("macroClusteringBolt", new DenStreamMacroClusteringBolt(numWorkers), 1)
                .shuffleGrouping("microClusteringBolt")
                .addConfiguration("tags", "macroClustering");

        topologyBuilder.setBolt("statisticsBolt", new DenStreamStatisticsBolt(), 1)
                .shuffleGrouping("macroClusteringBolt")
                .addConfiguration("tags", "macroClustering");

//        topologyBuilder.setBolt("macroClusteringBolt", new DenStreamMacroClusteringWindowBolt()
//                .withWindow(
//                        new BaseWindowedBolt.Duration(60, TimeUnit.SECONDS),
//                        new BaseWindowedBolt.Duration(60, TimeUnit.SECONDS))
//                // parallelism hint ставим равным 1, чтобы все микрокластера обрабатывались в одном месте
//                , 1).shuffleGrouping("microClusteringBolt");

        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxSpoutPending(40000);
        // +1 потому что numWorkers указывает на сколько воркеров параллелить микрокластеризацию,
        // но всегда еще должен быть один воркер для макрокластеризации и записи статистики
        conf.setNumWorkers(numWorkers + 1);
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
