package diploma;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Никита
 */
public class MacroClusteringStatistics implements Serializable {
    private int timeFactor;
    private int clusterId;
    private Map<String, Integer> topTerms;
    private int numberOfDocuments;

    public int getTimeFactor() {
        return timeFactor;
    }

    public void setTimeFactor(int timeFactor) {
        this.timeFactor = timeFactor;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public void setNumberOfDocuments(int numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }

    public Map<String, Integer> getTopTerms() {
        return topTerms;
    }

    public void setTopTerms(Map<String, Integer> topTerms) {
        this.topTerms = topTerms;
    }
}
