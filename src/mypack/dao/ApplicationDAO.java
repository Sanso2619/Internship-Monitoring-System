package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;

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
    public String applyInternshipString(int studentId, int internshipId) {
    try {
        if (studentId <= 0 || internshipId <= 0) {
            throw new ApplicationException("Invalid input values!");
        }
        Connection conn = DBConnection.getConnection();

        String query = "{CALL apply_internship(?, ?)}";
        CallableStatement cs = conn.prepareCall(query);

        cs.setInt(1, studentId);
        cs.setInt(2, internshipId);

        boolean hasResult = cs.execute();

        if (hasResult) {
            ResultSet rs = cs.getResultSet();
            if (rs.next()) return rs.getString(1);
        }

        return "Applied successfully!";

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "Error applying!";
}

    //  3. ADVANCED RECOMMENDATION (WINDOW FUNCTION)
    public String getRecommendationsString(int studentId) {
    StringBuilder sb = new StringBuilder("Recommendations:\n\n");

    try {
        Connection conn = DBConnection.getConnection();

        String query =
            "SELECT i.title, COUNT(*) AS match_score " +
            "FROM internships i " +
            "JOIN internship_skills ik ON i.internship_id = ik.internship_id " +
            "JOIN student_skills ss ON ik.skill_id = ss.skill_id " +
            "WHERE ss.student_id = ? " +
            "GROUP BY i.internship_id";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, studentId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            sb.append(rs.getString("title"))
              .append(" | Score: ")
              .append(rs.getInt("match_score"))
              .append("\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return sb.toString();
}

    //  4. VIEW APPLICATIONS (JOIN QUERY)
    public String getApplicationsString() {
    StringBuilder sb = new StringBuilder("Applications:\n\n");

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
        if (!rs.next()) {
            throw new ApplicationException("No applications found!");
        }
        while (rs.next()) {
            sb.append(rs.getString("name"))
              .append(" | ")
              .append(rs.getString("title"))
              .append(" | ")
              .append(rs.getString("status"))
              .append("\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return sb.toString();
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
            if (userId <= 0) {
                throw new ApplicationException("Invalid User ID!");
            }
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