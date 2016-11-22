package diploma.bolts;


import diploma.clustering.tfidf.Clustering;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Обработчик, работающий как окно, собирающий все микрокластера всех
 * обработчиков {@link diploma.bolts.MicroClusteringBolt} и создающий новые глобальные кластера
 * По идее должен обновлять данные в in-memory data grid (hazelcast, например)
 * @author Никита
 */
public class MacroClusteringWindowBolt extends BaseWindowedBolt {
    private static final Logger LOG = LoggerFactory.getLogger(MacroClusteringWindowBolt.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        for (Tuple tuple : inputWindow.get()) {
            Clustering clustering = (Clustering) tuple.getValue(0);
            LOG.info(Integer.toString(clustering.getClusters().size()));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
