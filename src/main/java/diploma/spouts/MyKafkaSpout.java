package diploma.spouts;

import diploma.statistics.dao.PendingDao;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Никита
 */
public class MyKafkaSpout extends KafkaSpout {
    private static final Logger LOG = LoggerFactory.getLogger(MyKafkaSpout.class);
    /**
     * Не равно количеству выпущенных кортежей!!
     */
    private int numberOfNextTupleInvoсations;
    private SpoutOutputCollector collector;
    private PendingDao dao;
    private int spout;

    public MyKafkaSpout(SpoutConfig spoutConf) {
        super(spoutConf);
        numberOfNextTupleInvoсations = 0;
        dao = new PendingDao();
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        super.open(conf, context, collector);
        this.collector = collector;
        spout = context.getThisTaskId();
    }

    @Override
    public void nextTuple() {
        super.nextTuple();
        if (++numberOfNextTupleInvoсations % 20000 == 0) {
            LOG.info("pending tuples = " + collector.getPendingCount());
            dao.savePending(collector.getPendingCount(), spout);
        }
    }
}
