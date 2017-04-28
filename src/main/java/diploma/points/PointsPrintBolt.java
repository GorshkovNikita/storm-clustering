package diploma.points;

import diploma.clustering.Point;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Никита
 */
public class PointsPrintBolt extends BaseBasicBolt {
    private final Logger LOG = LoggerFactory.getLogger(PointsPrintBolt.class);

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        Point p = (Point) input.getValueByField("str");
        LOG.info(Arrays.toString(p.getCoordinatesVector()));
        collector.emit(new Values(p));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("point"));
    }
}
