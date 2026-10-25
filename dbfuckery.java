import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbfuckery {
    static String db = "jdbc:sqlite:books.db";

    public static void addItem(Connection conn, int id, String title, String topic, float price, int stock)
            throws SQLException {
        String sql = "INSERT INTO BOOKS (ID, TITLE, TOPIC, STOCK, PRICE) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, title);
            statement.setString(3, topic);
            statement.setInt(4, stock);
            statement.setFloat(5, price);
            statement.executeUpdate();
        }
    }

    public static void resetDB(Connection conn) {
        //whole method is GPT6-Luna
        if (conn == null) {
            System.err.println("Reset DB Failed: no database connection");
            return;
        }
        try {
            try (Statement statement = conn.createStatement()) {
                statement.execute("PRAGMA busy_timeout=5000");
            }

            try (Statement statement = conn.createStatement();
                    ResultSet result = statement.executeQuery("PRAGMA journal_mode=WAL")) {
                if (!result.next() || !"wal".equalsIgnoreCase(result.getString(1))) {
                    throw new SQLException("Could not enable WAL mode");
                }
            }

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("DELETE FROM BOOKS");
            }

            addItem(conn, 53477, "Achieve Less Bugs and More Hugs in CSCI 339",
                    "distributed systems", 49.99f, 10);
            addItem(conn, 53573, "Distributed Systems for Dummies",
                    "distributed systems", 34.99f, 10);
            addItem(conn, 12365, "Surviving College", "college life", 19.99f, 10);
            addItem(conn, 12498, "Cooking for the Impatient Undergraduate",
                    "college life", 24.99f, 10);
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            System.out.println("Reset DB: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Reset DB Failed: " + e);
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db);
        } catch (SQLException e) {
            System.out.println("DB Connection Failed");
            System.err.println(e);
        }
        resetDB(conn);

    }
}
