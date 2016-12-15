package diploma.bolts;


import diploma.clustering.clusters.Cluster;
import diploma.clustering.clusters.Clustering;
import diploma.clustering.dbscan.ClustersDbscan;
import diploma.clustering.dbscan.points.DbscanPoint;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
    private ClustersDbscan clustersDbscan;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        clustersDbscan = new ClustersDbscan(3, 0.4);
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<DbscanPoint> incomingPoints = new ArrayList<>();
        for (Tuple tuple : inputWindow.get()) {
            incomingPoints.add((DbscanPoint) tuple.getValue(0));
        }
        clustersDbscan.run(incomingPoints);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
