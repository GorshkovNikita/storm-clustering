package diploma.spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Источник данных из файла, отправляющий их построчно
 * @author Никита
 */
public class FileReaderSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(FileReaderSpout.class);
    private SpoutOutputCollector collector;
    // НЕ распределенная переменная, т.е для каждого потока из numWorkers будет свой счетчик
    private int msgId = 0;
    private String filePath;
    public static int numberOfEmittedMessages = 0;
    private BufferedReader reader;
    /**
     * Количество твитов, обрабатываемых spout в секунду
     */
    private double rate;
    private long startTime;

    public FileReaderSpout(Path filePath) {
        this.filePath = filePath.toString();
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            this.reader = new BufferedReader(new FileReader(this.filePath));
        } catch (FileNotFoundException e) {
            this.reader = null;
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public void nextTuple() {
//        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
//            String line = null;
//            do {
        try {
            String line = reader.readLine();
            if (line == null) {
//                deactivate();
                return;
            }
            collector.emit(new Values(line, ++msgId), msgId);
            numberOfEmittedMessages++;
            if (numberOfEmittedMessages % 5000 == 0) {
                rate = 5000 / (double)(System.currentTimeMillis() - startTime) * 1000;
                startTime = System.currentTimeMillis();
            }
            Thread.sleep(5);
//            } while (line != null);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("str", "msgId"));
    }
}
