import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(db);
        } catch (SQLException e) {
            System.out.println("DB Connection Failed");
            System.err.println(e);
        }



    }
}
