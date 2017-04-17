package diploma.denstream;

import diploma.bolts.MyDenStreamMicroClusteringBolt;
import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.clusters.StatusesCluster;
import diploma.statistics.dao.TweetDao;
import org.apache.commons.lang.SerializationUtils;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.Map;

/**
 * @author Никита
 */
public class DenStreamMicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MyDenStreamMicroClusteringBolt.class);
    private DenStream denStream;
    private static final int MIN_POINTS = 30;
    private static final int MAX_CLUSTERS = 100;
    private TweetDao tweetDao;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        denStream = new DenStream(10, 10, 5.0, 0.0000001, 0.4);
        tweetDao = new TweetDao();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            for (StatusesCluster cluster : denStream.getPotentialMicroClustering().getClusters()) {
                StatusesCluster clusterCopy = (StatusesCluster) SerializationUtils.clone(cluster);
                clusterCopy.getTfIdf().sortTermFrequencyMap();
                collector.emit(new Values(clusterCopy));
            }
        }
        else {
        // для KafkaSpout field name = str
            String tweetJson = tuple.getStringByField("str");
            Integer msgId = tuple.getIntegerByField("msgId");
            if (tweetJson != null) {
                try {
                    Status status = TwitterObjectFactory.createStatus(tweetJson);
//                    tweetDao.saveTweet(status);
                    denStream.processNext(new EnhancedStatus(status));
//                    denStream.processNext(status);
                } catch (TwitterException ignored) {}
            }
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("microClusters"));
    }
}
