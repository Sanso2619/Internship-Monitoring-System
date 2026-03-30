package dao;

import db.DBConnection;
import java.sql.*;

public class ApplicationDAO {

    //  1. GET ALL STUDENTS
    public void getStudents() {
        try {
            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM students";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nStudents List:");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("student_id") + " | " +
                        rs.getDouble("cgpa")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  2. APPLY INTERNSHIP (USING PROCEDURE)
    public void applyInternship(int studentId, int internshipId) {
    try {
        Connection conn = DBConnection.getConnection();

        // Check if already applied
        String check = "SELECT COUNT(*) FROM applications WHERE student_id=? AND internship_id=?";
        PreparedStatement checkPs = conn.prepareStatement(check);
        checkPs.setInt(1, studentId);
        checkPs.setInt(2, internshipId);
        ResultSet rs = checkPs.executeQuery();
        rs.next();
        if (rs.getInt(1) > 0) {
            System.out.println("\nAlready applied for this internship!");
            return;
        }

        // Apply via procedure
        String query = "{CALL apply_internship(?, ?)}";
        CallableStatement cs = conn.prepareCall(query);
        cs.setInt(1, studentId);
        cs.setInt(2, internshipId);
        cs.execute();

        System.out.println("\nApplied via procedure!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    //  3. ADVANCED RECOMMENDATION (WINDOW FUNCTION)
    public void recommendInternships(int studentId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                    "SELECT i.title, COUNT(*) AS match_score, " +
                    "RANK() OVER (ORDER BY COUNT(*) DESC) AS rank_no " +
                    "FROM internships i " +
                    "JOIN internship_skills ik ON i.internship_id = ik.internship_id " +
                    "JOIN student_skills ss ON ik.skill_id = ss.skill_id " +
                    "WHERE ss.student_id = ? " +
                    "GROUP BY i.internship_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nRecommended Internships:");

            while (rs.next()) {
                System.out.println(
                        rs.getString("title") +
                        " | Score: " + rs.getInt("match_score") +
                        " | Rank: " + rs.getInt("rank_no")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  4. VIEW APPLICATIONS (JOIN QUERY)
    public void viewApplications() {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                    "SELECT u.name, i.title, a.status " +
                    "FROM applications a " +
                    "JOIN students s ON a.student_id = s.student_id " +
                    "JOIN users u ON s.student_id = u.user_id " +
                    "JOIN internships i ON a.internship_id = i.internship_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nApplications:");

            while (rs.next()) {
                System.out.println(
                        rs.getString("name") + " | " +
                        rs.getString("title") + " | " +
                        rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  5. VIEW STATUS HISTORY
    public void viewStatusHistory() {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                    "SELECT application_id, status, change_date " +
                    "FROM application_status_history";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nStatus History:");

            while (rs.next()) {
                System.out.println(
                        "App ID: " + rs.getInt("application_id") +
                        " | Status: " + rs.getString("status") +
                        " | Date: " + rs.getTimestamp("change_date")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  6. VIEW NOTIFICATIONS
    public void viewNotifications(int userId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                    "SELECT message, created_at " +
                    "FROM notifications WHERE user_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nNotifications:");

            while (rs.next()) {
                System.out.println(
                        rs.getString("message") +
                        " | " + rs.getTimestamp("created_at")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}