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
        if (args.length < 1) {
            LOG.error("You must enter the startup type: local or cluster");
            return;
        }
        StartupType startupType = valueOf(args[0].toUpperCase());
        SpoutCreator spoutCreator;
        switch (startupType) {
            case LOCAL:
                if (args.length < 2) {
//                    spoutCreator = new TwitterStreamingApiSpoutCreator();
                    spoutCreator = new PointsSpoutCreator();
                }
                else {
                    // параметры: local D:\MSU\diploma\tweets-sets\full-random.txt
                    Path filePath = Paths.get(args[1]);
                    spoutCreator = new FileReaderSpoutCreator(filePath);
                }
                break;
            case CLUSTER:
                spoutCreator = new KafkaSpoutCreator();
                break;
            default:
                LOG.error("Wrong startup type. It must be local or cluster");
                return;
        }
//        Topology topology = new Topology(1, startupType, spoutCreator);
        PointsTopology topology = new PointsTopology(1, startupType, spoutCreator);
        try {
            topology.submit();
        } catch (Exception ex) {
            LOG.error(ex.getCause().toString());
            ex.printStackTrace();
        }
    }
}
