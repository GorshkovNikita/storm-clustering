package diploma.bolts.denstream;

import diploma.clustering.EnhancedStatus;
import diploma.clustering.MapUtil;
import diploma.statistics.dao.BaseDao;
import diploma.statistics.dao.TweetDao;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Никита
 */
public class TopTermsBolt extends BaseBasicBolt {
    private Map<String, Integer> topTerms;
    private int numberOfDocuments;
    private Dao dao;
    private Set<String> tweets;
    private TweetDao tweetDao;

    private static class Dao extends BaseDao {
        void saveTopTerms(Map<String, Integer> topTerms) {
            Connection connection = getConnection();
            PreparedStatement statement = null;
            try {
                String query = "INSERT INTO allTerms (term, frequency) VALUES (?,?)";
                statement = connection.prepareStatement(query);
                for (Map.Entry<String, Integer> entry : topTerms.entrySet()) {
                    statement.setString(1, entry.getKey());
                    statement.setInt(2, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
                statement.close();
                connection.close();
            }
            catch (SQLException ignored) {
                System.out.println(ignored.getMessage());
            }
            finally {
                try {
                    if (statement != null)
                        statement.close();
                }
                catch (SQLException ignored) {}
                try {
                    connection.close();
                }
                catch (SQLException ignored) {}
            }
        }

        public void saveTweets(Set<String> tweets) {
            Connection connection = getConnection();
            PreparedStatement statement = null;
            try {
                String query = "INSERT INTO manualclustering (tweetId) VALUES (?)";
                statement = connection.prepareStatement(query);
                for (String tweetId : tweets) {
                    statement.setString(1, tweetId);
                    statement.addBatch();
                }
                statement.executeBatch();
                statement.close();
                connection.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (statement != null)
                        statement.close();
                }
                catch (SQLException ignored) {}
                try {
                    connection.close();
                }
                catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.topTerms = new HashMap<>();
        this.numberOfDocuments = 0;
        this.dao = new Dao();
        this.tweets = new HashSet<>();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        numberOfDocuments++;
        EnhancedStatus status = (EnhancedStatus) tuple.getValueByField("status");
//        String[] terms = status.getNormalizedText().split(" ");
        tweets.add(Long.toString(status.getStatus().getId()));
//        for (String term : terms) {
//            if (this.topTerms.containsKey(term))
//                this.topTerms.put(term, this.topTerms.get(term) + 1);
//            else this.topTerms.put(term, 1);
//        }
        if (numberOfDocuments % 20000 == 0) {
            dao.saveTweets(tweets);
            tweets.clear();
        }
//        if (numberOfDocuments == 150000) {
//            this.topTerms = MapUtil.sortByValue(this.topTerms);
//            this.topTerms = MapUtil.putFirstEntries(1000, this.topTerms);
//            dao.saveTopTerms(this.topTerms);
//            System.out.println("smth");
//        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
