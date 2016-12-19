package diploma.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Nikita
 */
public class MysqlConfig {
    public static final String JDBC_DRIVER;
    public static final String DB_URL;
    public static final String USER;
    public static final String PASSWORD;

    static {
        String jdbcDriver;
        String databaseUrl;
        String databaseUser;
        String databasePassword;
        try {
            InputStream in = ClusterConfig.class.getResourceAsStream("/mysql-config.properties");
            Properties prop = new Properties();
            prop.load(in);
            jdbcDriver = prop.getProperty("database.jdbc.driver");
            databaseUrl = prop.getProperty("database.url");
            databaseUser = prop.getProperty("database.user");
            databasePassword = prop.getProperty("database.password");
        } catch (Exception e) {
            jdbcDriver = "";
            databaseUrl = "";
            databaseUser = "";
            databasePassword = "";
            e.printStackTrace();
        }
        JDBC_DRIVER = jdbcDriver;
        DB_URL = databaseUrl;
        USER = databaseUser;
        PASSWORD = databasePassword;
    }
}
