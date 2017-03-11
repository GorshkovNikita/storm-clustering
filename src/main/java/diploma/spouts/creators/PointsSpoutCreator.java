package diploma.spouts.creators;

import diploma.spouts.PointsSpout;
import org.apache.storm.topology.IRichSpout;

/**
 * @author Никита
 */
public class PointsSpoutCreator extends SpoutCreator {
    @Override
    public IRichSpout createSpout() {
        return new PointsSpout();
    }
}
