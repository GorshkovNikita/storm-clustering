package diploma;

import diploma.spouts.creators.FileReaderSpoutCreator;
import diploma.spouts.creators.KafkaSpoutCreator;
import diploma.spouts.creators.SpoutCreator;
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
                    LOG.error("You must enter a file path: local or cluster");
                    return;
                }
                Path filePath = Paths.get(args[1]);
                spoutCreator = new FileReaderSpoutCreator(filePath);
                break;
            case CLUSTER:
                spoutCreator = new KafkaSpoutCreator();
                break;
            default:
                LOG.error("Wrong startup type. It must be local or cluster");
                return;
        }
        Topology topology = new Topology(4, startupType, spoutCreator);
        try {
            topology.submit();
        } catch (Exception ex) {
            LOG.error(ex.getCause().toString());
            ex.printStackTrace();
        }
    }
}
