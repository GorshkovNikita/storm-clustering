package diploma.dao;

import twitter4j.Status;

import java.sql.*;

/**
 * @author Никита
 */
public class TweetDao extends BaseDao {
    public void saveTweet(Status status) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        String insertTweet = "INSERT INTO tweets (tweetId, tweetText) VALUES (?,?)";
        if (connection != null) {
            try {
                preparedStatement = connection.prepareStatement(insertTweet);
                preparedStatement.setString(1, String.valueOf(status.getId()));
                preparedStatement.setString(2, status.getText());
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

    public static void main(String[] args) {
        TweetDao tweetDao = new TweetDao();
    }
}
