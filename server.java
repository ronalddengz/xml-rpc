import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.XmlRpcException;
import java.sql.*;;

public class server {

    static String bookdb = "jdbc:sqlite:books.db";
    static String logdb = "jdbc:sqlite:log.db";

    public String search(String topic) {
        return "";
    }

    public String lookup(int item_number) {
        return "";
    }

    public String buy(int item_number) {
        return "";
    }

    public String log() {
        return "";
    }

    public String restock(int item_number, int add_count) {
        return "";
    }

    public String update(int item_number, float price) {
        return "";
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
        
        connect(bookdb);
        
    }


}
