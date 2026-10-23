import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    public static void resetDB() {
        String createTable = "CREATE TABLE BOOKS ("
                + "ID INT PRIMARY KEY NOT NULL, "
                + "TITLE CHAR(100) NOT NULL, "
                + "TOPIC CHAR(50) NOT NULL, "
                + "STOCK INT NOT NULL, "
                + "PRICE INT NOT NULL)";
                // using INT for price because I got weird floating point prices
                // remember to divide by 100 when displaying the price

        try (Connection conn = DriverManager.getConnection(db);
            Statement statement = conn.createStatement()) {

            statement.executeUpdate("DROP TABLE IF EXISTS BOOKS");
            statement.executeUpdate(createTable);

            addItem(conn, 53477,
                    "Achieve Less Bugs and More Hugs in CSCI 339",
                    "distributed systems", 2999, 10);

            addItem(conn, 53573,
                    "Distributed Systems for Dummies",
                    "distributed systems", 2499, 10);

            addItem(conn, 12365,
                    "Surviving College",
                    "college life", 1999, 10);

            addItem(conn, 12498,
                    "Cooking for the Impatient Undergraduate",
                    "college life", 1499, 10);

            System.out.println("Database reset complete.");

        } catch (SQLException e) {
            System.out.println("Could not reset the database:");
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        resetDB();
    }
}
