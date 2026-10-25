import java.sql.*;

public class adminServer {
        
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
        
        
    }
}
