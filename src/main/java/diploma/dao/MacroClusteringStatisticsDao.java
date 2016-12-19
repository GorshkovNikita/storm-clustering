package diploma.dao;

import diploma.MacroClusteringStatistics;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Никита
 */
public class MacroClusteringStatisticsDao extends BaseDao {
    public void saveStatistics(MacroClusteringStatistics statistics) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        String insertTweet = "INSERT INTO statistics (timeFactor, clusterId, numberOfDocuments) VALUES (?,?,?)";
        if (connection != null) {
            try {
                preparedStatement = connection.prepareStatement(insertTweet, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, statistics.getTimeFactor());
                preparedStatement.setInt(2, statistics.getClusterId());
                preparedStatement.setInt(3, statistics.getNumberOfDocuments());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0)
                    throw new SQLException("Statistics insertion fails, no rows affected.");

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        saveTopTerms(generatedKeys.getLong(1), statistics.getTopTerms());
                    else throw new SQLException("Statistics insertion fails, no id obtained");
                }

                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null)
                        preparedStatement.close();
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

    private void saveTopTerms(long statisticId, Map<String, Integer> topTerms) {
        Connection connection = getConnection();
        if (connection != null) {
            PreparedStatement preparedStatement = null;
            try {
                String insertTopTerm = "INSERT INTO topterms (statisticId, term, numberOfOccurences) VALUES (?,?,?)";
                preparedStatement = connection.prepareStatement(insertTopTerm);

                for (Map.Entry<String, Integer> term: topTerms.entrySet()) {
                    preparedStatement.clearParameters();
                    preparedStatement.setLong(1, statisticId);
                    preparedStatement.setString(2, term.getKey());
                    preparedStatement.setInt(3, term.getValue());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null)
                        preparedStatement.close();
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
}
