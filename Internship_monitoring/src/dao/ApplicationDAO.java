package dao;

import db.DBConnection;
import java.sql.*;

public class ApplicationDAO {

    public void getStudents() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM students";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            System.out.println("Students List:");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " " +
                    rs.getString("name") + " " +
                    rs.getDouble("cgpa")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void applyInternship(int studentId, int internshipId) {
    try {
        Connection conn = DBConnection.getConnection();

        String query = "{CALL apply_internship(?, ?)}";
        CallableStatement cs = conn.prepareCall(query);

        cs.setInt(1, studentId);
        cs.setInt(2, internshipId);

        cs.execute();

        System.out.println("Applied via procedure!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void recommendInternships(int studentId) {
    try {
        Connection conn = DBConnection.getConnection();

        String query =
            "SELECT i.title " +
            "FROM internships i, students s " +
            "WHERE s.student_id = ? " +
            "AND s.cgpa >= i.min_cgpa";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, studentId);

        ResultSet rs = ps.executeQuery();

        System.out.println("\nRecommended Internships:");

        while (rs.next()) {
            System.out.println(rs.getString("title"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}