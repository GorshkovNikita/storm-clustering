package diploma.bolts;

import diploma.clustering.EnhancedStatus;
import diploma.clustering.statusesfilters.SportsBetsFilter;
import diploma.clustering.statusesfilters.StatusesFilter;
import diploma.clustering.statusesfilters.TweetLengthFilter;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Дополнительная фильтрация твитов по длине, нежелательным юзерам и тд.
 * @author Никита
 */
public class StatusesFilteringBolt extends BaseBasicBolt {
    List<StatusesFilter> filters;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        filters = Arrays.asList(new TweetLengthFilter(), new SportsBetsFilter());
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        EnhancedStatus status = (EnhancedStatus) tuple.getValueByField("status");
        for (StatusesFilter filter : filters)
            if (!filter.filter(status)) return;
        collector.emit(new Values(status));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declare(new Fields("status"));
    }
}
