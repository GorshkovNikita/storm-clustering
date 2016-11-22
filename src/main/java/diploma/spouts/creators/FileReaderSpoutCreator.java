package diploma.spouts.creators;

import diploma.spouts.FileReaderSpout;
import org.apache.storm.topology.IRichSpout;

import java.nio.file.Path;

/**
 * @author Никита
 */
public class FileReaderSpoutCreator extends SpoutCreator {
    private Path filePath;

    public FileReaderSpoutCreator(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public IRichSpout createSpout() {
        return new FileReaderSpout(filePath);
    }
}
