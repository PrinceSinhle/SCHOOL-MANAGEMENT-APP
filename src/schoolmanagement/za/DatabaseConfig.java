package schoolmanagement.za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {
    private static final String DEFAULT_URL =
            "jdbc:mysql://127.0.0.1:3306/school_management";

    public static final String URL = setting("SCHOOL_DB_URL", DEFAULT_URL);
    public static final String USER = setting("SCHOOL_DB_USER", "school_app");
    public static final String PASSWORD = setting("SCHOOL_DB_PASSWORD", "");

    static {
        if (PASSWORD.isEmpty()) {
            throw new IllegalStateException("Set the SCHOOL_DB_PASSWORD environment variable before starting the app.");
        }
    }

    private DatabaseConfig() {
    }

    public static Connection openConnection() throws SQLException {
        DriverManager.setLoginTimeout(5);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }
}
