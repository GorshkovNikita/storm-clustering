package diploma.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Никита
 */
public class TwitterConfig {
    public static final String CONSUMER_KEY;
    public static final String CONSUMER_SECRET;
    public static final String TOKEN;
    public static final String TOKEN_SECRET;

    static {
        String consumerKey;
        String consumerSecret;
        String token;
        String tokenSecret;
        try {
            InputStream in = ClusterConfig.class.getResourceAsStream("/twitter-config.properties");
            Properties prop = new Properties();
            prop.load(in);
            consumerKey = prop.getProperty("twitter.consumer.key");
            consumerSecret = prop.getProperty("twitter.consumer.secret");
            token = prop.getProperty("twitter.token");
            tokenSecret = prop.getProperty("twitter.token.secret");
        } catch (Exception e) {
            consumerKey = "";
            consumerSecret = "";
            token = "";
            tokenSecret = "";
            e.printStackTrace();
        }
        CONSUMER_KEY = consumerKey;
        CONSUMER_SECRET = consumerSecret;
        TOKEN = token;
        TOKEN_SECRET = tokenSecret;
    }
}
