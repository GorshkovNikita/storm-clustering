package diploma.spouts;

import diploma.TwitterStreamConnection;
import diploma.config.TwitterConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Источник твитов прямиком из Twitter Streaming Api
 * @author Никита
 */
public class TwitterStreamingApiSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamingApiSpout.class);
    private SpoutOutputCollector collector;
    private int msgId = 0;

    public TwitterStreamingApiSpout() {
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        // TEMP
        try {
            TwitterStreamConnection.getInstance(TwitterConfig.CONSUMER_KEY, TwitterConfig.CONSUMER_SECRET, TwitterConfig.TOKEN, TwitterConfig.TOKEN_SECRET).getClient().connect();
        }
        catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void nextTuple() {
        if (TwitterStreamConnection.getInstance().getClient().isDone()) {
            System.out.println("Client connection closed unexpectedly: " + TwitterStreamConnection.getInstance().getClient().getExitEvent().getMessage());
            // возможно нужно вызывать close и завершать работу
            return;
        }
        String msg = TwitterStreamConnection.getNextMessage();
        collector.emit(new Values(msg, ++msgId), msgId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("str", "msgId"));
    }

    @Override
    public void close() {
        TwitterStreamConnection.getInstance().getClient().stop();
    }
}
