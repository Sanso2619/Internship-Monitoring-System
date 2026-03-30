import db.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        try {
            Connection conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("✅ Connected to database successfully!");
            } else {
                System.out.println("❌ Connection failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}