import java.sql.*;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class zDEPRCadminServer {
        
    static String bookdb = "jdbc:sqlite:books.db";
    static String logdb = "jdbc:sqlite:log.db";
    
    public String log() {
        Connection logConn = connect(logdb);
        //SELECT * FROM LOG ORDER BY DATE DESC LIMIT 50
        return "";
    }

    public String restock(int item_number, int add_count) {
        Connection conn = connect(bookdb);
        //UPDATE BOOKS SET STOCK = STOCK + ? WHERE ID = ?
        return "";
    }

    public String update(int item_number, float price) {
        Connection conn = connect(bookdb);
        //UPDATE BOOKS SET PRICE = ? WHERE ID = ?

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
        try {
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            XmlRpcServer xmlRpcServer;
            WebServer server = new WebServer(8009);
            xmlRpcServer = server.getXmlRpcServer();
            phm.addHandler("adminBookstore", server.class);
            xmlRpcServer.setHandlerMapping(phm);
            System.out.println("Admin Server starting on port 8008");
            server.start();
        } catch (Exception exception) {
            System.err.println("Server: " + exception); 
        }
        
    }
}
