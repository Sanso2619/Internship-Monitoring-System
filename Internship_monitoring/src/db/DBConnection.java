package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/internship_db",
                "root",
                "root" 
            );

            System.out.println("Connected!");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}