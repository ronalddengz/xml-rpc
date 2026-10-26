import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.XmlRpcException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class server {

    static final String bookdb = "jdbc:sqlite:books.db";
    static final String logdb = "jdbc:sqlite:log.db";

    public String search(String topic) {
        String answer = "";
        String sql = "SELECT ID, TITLE FROM BOOKS WHERE TOPIC = ? ORDER BY ID";

        try (Connection conn = connect(bookdb);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, topic);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    answer += results.getInt("ID") + " - "
                            + results.getString("TITLE") + "\n";
                }
            }
        } catch (SQLException exception) {
            return "Search failed: " + exception.getMessage();
        }

        if (answer.equals("")) {
            return "No books found for topic: " + topic;
        }
        return answer;
    }

    public String lookup(int item_number) {
        String sql = "SELECT ID, TITLE, TOPIC, STOCK, PRICE FROM BOOKS WHERE ID = ?";

        try (Connection conn = connect(bookdb);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, item_number);

            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    return "Book not found: " + item_number;
                }

                String stockMessage;
                if (results.getInt("STOCK") > 0) {
                    stockMessage = "yes";
                } else {
                    stockMessage = "no";
                }

                return "Item number: " + results.getInt("ID") + "\n"
                        + "Title: " + results.getString("TITLE") + "\n"
                        + "Topic: " + results.getString("TOPIC") + "\n"
                        + "Price: $" + results.getFloat("PRICE") + "\n"
                        + "In stock: " + stockMessage + "\n"
                        + "Number in stock: " + results.getInt("STOCK");
            }
        } catch (SQLException exception) {
            return "Lookup failed: " + exception.getMessage();
        }
    }

    // synchronized means that two purchase requests handled by this server cannot change the same inventory at the same time
    public synchronized String buy(int item_number) {
        String updateSql = "UPDATE BOOKS SET STOCK = STOCK - 1 "
                + "WHERE ID = ? AND STOCK > 0";
        String stockSql = "SELECT STOCK FROM BOOKS WHERE ID = ?";
        String logSql = "INSERT INTO LOG (DATE, ID, REMAINING_STOCK) VALUES (?, ?, ?)";
        int remainingStock;

        try (Connection conn = connect(bookdb);
             PreparedStatement update = conn.prepareStatement(updateSql)) {

            update.setInt(1, item_number);
            int changedRows = update.executeUpdate();

            if (changedRows == 0) {
                // covers both an invalid item number and an empty stock.
                try (PreparedStatement findBook = conn.prepareStatement(stockSql)) {
                    findBook.setInt(1, item_number);
                    try (ResultSet results = findBook.executeQuery()) {
                        if (!results.next()) {
                            return "Purchase failed: book not found: " + item_number;
                        }
                        return "Purchase failed: book is out of stock";
                    }
                }
            }

            try (PreparedStatement findStock = conn.prepareStatement(stockSql)) {
                findStock.setInt(1, item_number);
                try (ResultSet results = findStock.executeQuery()) {
                    results.next();
                    remainingStock = results.getInt("STOCK");
                }
            }
        } catch (SQLException exception) {
            return "Purchase failed: " + exception.getMessage();
        }

        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (Connection logConn = connect(logdb);
             PreparedStatement logStatement = logConn.prepareStatement(logSql)) {

            logStatement.setString(1, dateTime);
            logStatement.setInt(2, item_number);
            logStatement.setInt(3, remainingStock);
            logStatement.executeUpdate();
        } catch (SQLException exception) {
            return "Purchase completed, but logging failed: " + exception.getMessage();
        }

        return "Purchase completed. Remaining stock: " + remainingStock;
    }

    public synchronized String log() {
        String answer = "";
        String sql = "SELECT DATE, ID, REMAINING_STOCK FROM LOG ORDER BY DATE";

        try (Connection conn = connect(logdb);
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                answer += results.getString("DATE") + " - item "
                        + results.getInt("ID") + " - remaining stock: "
                        + results.getInt("REMAINING_STOCK") + "\n";
            }
        } catch (SQLException exception) {
            return "Could not read log: " + exception.getMessage();
        }

        if (answer.equals("")) {
            return "No purchases have been made.";
        }
        return answer;
    }

    public String restock(int item_number, int add_count) {
        if (add_count <= 0) {
            return "Restock failed: amount must be greater than zero";
        }

        String sql = "UPDATE BOOKS SET STOCK = STOCK + ? WHERE ID = ?";
        try (Connection conn = connect(bookdb);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, add_count);
            statement.setInt(2, item_number);

            if (statement.executeUpdate() == 0) {
                return "Restock failed: book not found: " + item_number;
            }
            return "Restocked item " + item_number + " by " + add_count;
        } catch (SQLException exception) {
            return "Restock failed: " + exception.getMessage();
        }
    }

    public String update(int item_number, float price) {
        if (price < 0) {
            return "Price update failed: price cannot be negative";
        }

        String sql = "UPDATE BOOKS SET PRICE = ? WHERE ID = ?";
        try (Connection conn = connect(bookdb);
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setFloat(1, price);
            statement.setInt(2, item_number);

            if (statement.executeUpdate() == 0) {
                return "Price update failed: book not found: " + item_number;
            }
            return "Updated price for item " + item_number + " to $" + price;
        } catch (SQLException exception) {
            return "Price update failed: " + exception.getMessage();
        }
    }

    public static Connection connect(String db) {
        try {
            return DriverManager.getConnection(db);
        } catch (SQLException e) {
            System.out.println("DB Connection Failed:" + e);
            System.err.println(e);
        }
        throw new RuntimeException("Could not connect to database: " + db);
    }

    public static void main(String[] args) {
        try {
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            XmlRpcServer xmlRpcServer;
            WebServer server = new WebServer(8008);
            xmlRpcServer = server.getXmlRpcServer();
            phm.addHandler("bookstore", server.class);
            xmlRpcServer.setHandlerMapping(phm);
            System.out.println("Server starting on port 8008");
            server.start();
        } catch (Exception exception) {
            System.err.println("Server: " + exception); 
        }
    }

}
