package diploma;

import diploma.points.PointsTopology;
import diploma.spouts.creators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static diploma.StartupType.*;

/**
 * @author Никита
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            LOG.error("You must enter the startup type: local or cluster, and number of workers");
            return;
        }
        StartupType startupType = valueOf(args[0].toUpperCase());
        Integer numWorkers;
        try {
            numWorkers = Integer.valueOf(args[1]);
            if (numWorkers <= 0) throw new NumberFormatException();
        }
        catch (NumberFormatException ex) {
            LOG.error("Wrong number of workers");
            return;
        }
        SpoutCreator spoutCreator;
        switch (startupType) {
            case LOCAL:
                if (args.length < 3) {
//                    spoutCreator = new TwitterStreamingApiSpoutCreator();
                    spoutCreator = new PointsSpoutCreator();
                }
                else {
                    // параметры: local 1 D:\MSU\diploma\tweets-sets\full-random.txt
                    Path filePath = Paths.get(args[2]);
//                    spoutCreator = new FileReaderSpoutCreator(filePath);
                    spoutCreator = new KafkaSpoutCreator();
                }
                break;
            case CLUSTER:
//                spoutCreator = new PointsSpoutCreator();
                spoutCreator = new KafkaSpoutCreator();
                break;
            default:
                LOG.error("Wrong startup type. It must be local or cluster");
                return;
        }
//        Topology topology = new Topology(1, startupType, spoutCreator);
//        PointsTopology topology = new PointsTopology(numWorkers, startupType, spoutCreator);
        DenStreamTopology topology = new DenStreamTopology(numWorkers, startupType, spoutCreator);
        try {
            topology.submit();
        } catch (Exception ex) {
            LOG.error(ex.getCause().toString());
            ex.printStackTrace();
        }
    }
}
