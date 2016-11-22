package diploma.spouts.creators;

import org.apache.storm.topology.IRichSpout;

/**
 * Фабричный метод для создания источников данных.
 * @author Никита
 */
abstract public class SpoutCreator {
    public abstract IRichSpout createSpout();
}
