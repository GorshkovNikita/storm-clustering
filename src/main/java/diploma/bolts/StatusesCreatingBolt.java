package diploma.bolts;

import diploma.clustering.EnhancedStatus;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.Map;

/**
 * Обработчик, получающий на вход json, и отдающий на выход
 * созданный твит с его нормализованным текстом.
 * @author Никита
 */
public class StatusesCreatingBolt extends BaseBasicBolt {
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String tweetJson = tuple.getStringByField("str");
//        Integer msgId = tuple.getIntegerByField("msgId");
        if (tweetJson != null) {
            try {
                Status status = TwitterObjectFactory.createStatus(tweetJson);
                collector.emit(new Values(new EnhancedStatus(status)));
            } catch (TwitterException ignored) {}
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("status"));
    }
}
