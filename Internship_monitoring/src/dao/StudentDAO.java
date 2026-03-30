package dao;

import db.DBConnection;
import java.sql.*;

public class StudentDAO {

    //  Get student profile
   public String getStudentProfile(int studentId) {
    StringBuilder sb = new StringBuilder();

    try {
        Connection conn = DBConnection.getConnection();

        String query =
            "SELECT u.name, s.cgpa, s.college " +
            "FROM students s JOIN users u ON s.student_id = u.user_id " +
            "WHERE s.student_id = ?";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, studentId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            sb.append("Name: ").append(rs.getString("name")).append("\n");
            sb.append("CGPA: ").append(rs.getDouble("cgpa")).append("\n");
            sb.append("College: ").append(rs.getString("college")).append("\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return sb.toString();
}

    //  View student skills
    public String getStudentSkillsString(int studentId) {
    StringBuilder sb = new StringBuilder("Skills:\n\n");

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

        while (rs.next()) {
            sb.append(rs.getString("skill_name"))
              .append(" (Level ")
              .append(rs.getInt("level"))
              .append(")\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return sb.toString();
}
}