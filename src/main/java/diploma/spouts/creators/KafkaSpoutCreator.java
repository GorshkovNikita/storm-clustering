package diploma.spouts.creators;

import diploma.ClusterConfig;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.IRichSpout;

/**
 * Создание источника данных из Apache Kafka
 * @author Никита
 */
public class KafkaSpoutCreator extends SpoutCreator {
    @Override
    public IRichSpout createSpout() {
        String topicName = ClusterConfig.KAFKA_TOPIC;
        // Указываем ip и порт zookeeper-сервера
        BrokerHosts hosts = new ZkHosts(ClusterConfig.ZOOKEEPER_IP + ":" + ClusterConfig.ZOOKEEPER_PORT);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" + topicName, "kafkastorm");
        // игнорируем смещение, записанное в zookeeper,
        // чтобы при каждом новом сабмите топологии сообщения читались заново
        spoutConfig.ignoreZkOffsets = true;
        // Указываем десериализатор
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(spoutConfig);
    }
}
