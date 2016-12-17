package diploma.spouts.creators;

import diploma.config.TwitterConfig;
import diploma.spouts.TwitterStreamingApiSpout;
import org.apache.storm.topology.IRichSpout;

/**
 * @author Никита
 */
public class TwitterStreamingApiSpoutCreator extends SpoutCreator {
    @Override
    public IRichSpout createSpout() {
        return new TwitterStreamingApiSpout();
    }
}
