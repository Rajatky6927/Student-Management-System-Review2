import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connection to the student management system.
 */
public class dbConnect {

    // Database credentials
    private static final String DATABASE_NAME = "studata";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1236";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;

    /**
     * Establishes and returns a database connection.
     *
     * @return Connection object to interact with MySQL database.
     * @throws SQLException If a database access error occurs.
     * @throws ClassNotFoundException If MySQL JDBC driver class is not found.
     */
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Create and return a new connection
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
