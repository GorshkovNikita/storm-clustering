package diploma.bolts;

import diploma.MacroClusteringStatistics;
import diploma.dao.MacroClusteringStatisticsDao;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Никита
 */
public class StatisticsBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MacroClusteringWindowBolt.class);
    private OutputCollector collector;
    private MacroClusteringStatisticsDao macroClusteringStatisticsDao;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        macroClusteringStatisticsDao = new MacroClusteringStatisticsDao();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        macroClusteringStatisticsDao.saveStatistics((MacroClusteringStatistics) input.getValue(0));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {}
}
