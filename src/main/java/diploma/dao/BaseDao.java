package diploma.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Никита
 */
public abstract class BaseDao {
    // TODO: сделать конфиг для БД
    protected static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    protected static final String DB_URL = "jdbc:mysql://localhost/clustering";
    protected static final String USER = "root";
    protected static final String PASS = "root";

    public Connection getConnection() {
        Connection connection;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            return connection;
        }
        catch (ClassNotFoundException | SQLException ex) {
            return null;
        }
    }
}
