package diploma;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Никита
 */
public class MacroClusteringStatistics implements Serializable {
    private int id;
    private int clusterId;
    private Map<String, Integer> topTenTerms;
    private int numberOfDocuments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Map<String, Integer> getTopTenTerms() {
        return topTenTerms;
    }

    public void setTopTenTerms(Map<String, Integer> topTenTerms) {
        this.topTenTerms = topTenTerms;
    }
}
