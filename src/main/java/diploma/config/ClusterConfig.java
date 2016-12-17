package diploma.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Никита
 */
public class ClusterConfig {
    public static final String ZOOKEEPER_IP;
    public static final String ZOOKEEPER_PORT;
    public static final String KAFKA_BROKER_LIST;
    public static final String KAFKA_BROKER_PORT;
    public static final String KAFKA_TOPIC;
    public static final String RATE;

    static {
        String zookeeperIp;
        String zookeeperPort;
        String kafkaBrokerList;
        String kafkaTopic;
        String kafkaBrokerPort;
        String rate;
        try {
            InputStream in = ClusterConfig.class.getResourceAsStream("/cluster-settings.properties");
            Properties prop = new Properties();
            prop.load(in);
            zookeeperIp = prop.getProperty("zookeeper.ip");
            zookeeperPort = prop.getProperty("zookeeper.port");
            kafkaBrokerList = prop.getProperty("kafka.broker.list");
            kafkaBrokerPort = prop.getProperty("kafka.broker.port");
            kafkaTopic = prop.getProperty("kafka.topic");
            rate = prop.getProperty("rate");
        } catch (Exception e) {
            zookeeperIp = "";
            zookeeperPort = "";
            kafkaBrokerList = "";
            kafkaTopic = "";
            kafkaBrokerPort = "";
            rate = "";
            e.printStackTrace();
        }
        ZOOKEEPER_IP = zookeeperIp;
        ZOOKEEPER_PORT = zookeeperPort;
        KAFKA_BROKER_LIST = kafkaBrokerList;
        KAFKA_TOPIC = kafkaTopic;
        KAFKA_BROKER_PORT = kafkaBrokerPort;
        RATE = rate;
    }
}
