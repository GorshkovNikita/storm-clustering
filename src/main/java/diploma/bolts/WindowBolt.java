package diploma.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.util.List;
import java.util.Map;

/**
 * @author Никита
 */
public class WindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(WindowBolt.class);
    private OutputCollector collector;
    private int count = 0;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        count++;
        LOG.info("in window " + count + " tuples count = " + inputWindow.get().size());
        for (Tuple tuple : inputWindow.get()) {
            List<Status> statuses = (List<Status>) tuple.getValue(0);
            LOG.info(Integer.toString(statuses.size()) + " from " + count);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //declarer.declare(new Fields("ngram", "count"));
    }
}
