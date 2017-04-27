package diploma.spouts;

import diploma.TwitterStreamConnection;
import diploma.clustering.Point;
import diploma.clustering.dbscan.points.DbscanSimplePoint;
import diploma.config.TwitterConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.Random;

/**
 * @author Никита
 */
public class PointsSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private int msgId = 0;
    private Random rnd;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        rnd = new Random();
    }

    @Override
    public void nextTuple() {
        Double[] randomPoint = new Double[2];
        for (int i = 0; i < 2; i++)
            randomPoint[i] = rnd.nextDouble() * 10000.0;
        collector.emit(new Values(new Point(randomPoint), ++msgId), msgId);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("str", "msgId"));
    }
}
