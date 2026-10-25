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
        Connection conn = connect(bookdb);
        // SELECT * FROM BOOKS WHERE TOPIC = ?
        return "";
    }

    public String lookup(int item_number) {
        Connection conn = connect(bookdb);
        // SELECT * FROM BOOKS WHERE ID = ?
        return "";
    }

    public String buy(int item_number) {
        Connection conn = connect(bookdb);
        Connection logConn = connect(logdb);
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // UPDATE BOOKS SET STOCK = STOCK - 1 WHERE ID = ?
        // INSERT INTO LOG (DATE, ID, REMAINING) VALUES (?, ?, ?, ?, ?)

        return "Purchased book";
    }

    public static Connection connect(String db) {
        try {
            return DriverManager.getConnection(db);
        } catch (SQLException e) {
            System.out.println("DB Connection Failed:" + e);
            System.err.println(e);
        }
        return null;
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
