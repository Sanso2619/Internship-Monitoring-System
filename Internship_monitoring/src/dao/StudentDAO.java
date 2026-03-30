package dao;

import db.DBConnection;
import java.sql.*;

public class StudentDAO {

    //  Get student profile
    public void getStudentProfile(int studentId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name, s.cgpa, s.college, s.graduation_year " +
                "FROM students s JOIN users u ON s.student_id = u.user_id " +
                "WHERE s.student_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nStudent Profile:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("name") + " | " +
                    rs.getDouble("cgpa") + " | " +
                    rs.getString("college")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  View student skills
    public void getStudentSkills(int studentId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT sk.skill_name, ss.level " +
                "FROM student_skills ss " +
                "JOIN skills sk ON ss.skill_id = sk.skill_id " +
                "WHERE ss.student_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nStudent Skills:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("skill_name") +
                    " | Level: " + rs.getInt("level")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}