package recipe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/RecipeApp";
    private static final String DB_USER = "root"; // Replace with your DB username
    private static final String DB_PASSWORD = "root"; // Replace with your DB password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL JDBC driver is loaded
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
