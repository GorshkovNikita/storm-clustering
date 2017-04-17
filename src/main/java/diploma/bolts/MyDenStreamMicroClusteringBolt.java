package diploma.bolts;

import diploma.clustering.DenStream;
import diploma.clustering.EnhancedStatus;
import diploma.clustering.clusters.StatusesCluster;
import diploma.clustering.clusters.StatusesClustering;
import diploma.statistics.dao.TweetDao;
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

import java.util.*;

/**
 * Обработчик, создающий микрокластера из сообщений, поступающих конкретно ему
 * TODO: сделать отдельный bolt для получения статуса
 * @author Никита
 */
public class MyDenStreamMicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MyDenStreamMicroClusteringBolt.class);
    private StatusesClustering microClustering;
    private static final int MIN_POINTS = 30;
    private static final int MAX_CLUSTERS = 100;
    private DenStream denStream;
    private TweetDao tweetDao;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        denStream = new DenStream(10, 10, 3.0, -Math.log(3.0) / Math.log(2)/(double) 400, 0.07);
        tweetDao = new TweetDao();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            for (StatusesCluster cluster : denStream.getPotentialMicroClustering().getClusters()) {
                cluster.getTfIdf().sortTermFrequencyMap();
                cluster.getTfIdf().limitTermFrequencyMap(25);
                collector.emit(new Values(cluster));
            }
            Collections.sort(denStream.getOutlierMicroClustering().getClusters(), new Comparator<StatusesCluster>() {
                @Override
                public int compare(final StatusesCluster object1, final StatusesCluster object2) {
                    return ((Integer)object2.getSize()).compareTo(object1.getSize());
                }
            });
            denStream.getPotentialMicroClustering().getClusters().clear();
        }
        else {
            // для KafkaSpout field name = str
            String tweetJson = tuple.getStringByField("str");
            Integer msgId = tuple.getIntegerByField("msgId");
            if (tweetJson != null) {
                try {
                    Status status = TwitterObjectFactory.createStatus(tweetJson);
//                    tweetDao.saveTweet(status);
                    EnhancedStatus enhancedStatus = new EnhancedStatus(status);
                    if (!"".equals(enhancedStatus.getNormalizedText())) {
                        long start = System.currentTimeMillis();
                        denStream.processNext(enhancedStatus);
                        Thread.sleep(100 - (System.currentTimeMillis() - start));
                    }
                }
                catch (TwitterException | InterruptedException ignored) {}
                catch (IllegalArgumentException ex) {
                    System.out.println("Не успеваю!");
                }
            }
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 30);
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
