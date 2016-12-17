package diploma.dao;

import diploma.MacroClusteringStatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Никита
 */
public class MacroClusteringStatisticsDao extends BaseDao {
    public void saveStatistics(MacroClusteringStatistics statistics) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        String insertTweet = "INSERT INTO statistics (id, clusterId, numberOfDocuments, topTerms) VALUES (?,?,?,?)";
        if (connection != null) {
            try {
                preparedStatement = connection.prepareStatement(insertTweet);
                preparedStatement.setInt(1, statistics.getId());
                preparedStatement.setInt(2, statistics.getClusterId());
                preparedStatement.setInt(3, statistics.getNumberOfDocuments());
                if (statistics.getTopTenTerms() != null) {
                    preparedStatement.setString(4, statistics.getTopTenTerms().keySet().toString());
                }
                else preparedStatement.setString(4, "");
                preparedStatement.executeUpdate();
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
