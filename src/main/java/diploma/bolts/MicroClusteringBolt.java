package diploma.bolts;

import diploma.clustering.tfidf.Clustering;
import org.apache.storm.Config;
import org.apache.storm.Constants;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Обработчик, создающий микрокластера из сообщений, поступающих конкретно ему
 * TODO: сделать отдельный bolt для получения статуса
 * @author Никита
 */
public class MicroClusteringBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MicroClusteringBolt.class);
    Clustering clustering = new Clustering();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (isTickTuple(tuple)) {
            collector.emit(new Values(clustering));
        }
        else {
            // для KafkaSpout field name = str
            String line = tuple.getStringByField("line");
            Integer msgId = tuple.getIntegerByField("msgId");
            try {
                Status status = TwitterObjectFactory.createStatus(line);
                clustering.processNext(status);
                LOG.info("msgId = " + msgId);
            } catch (TwitterException e) {
                LOG.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("statuses"));
    }
}
