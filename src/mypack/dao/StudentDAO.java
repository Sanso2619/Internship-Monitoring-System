package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;
import mypack.exception.DataNotFoundException;
import mypack.interfaces.Trackable;
import mypack.model.*;

// ✅ Interface implementation
public class StudentDAO implements Trackable {

    // ✅ Runtime Polymorphism (override)
    @Override
    public void trackStatus() {
        System.out.println("Tracking StudentDAO operations...");
    }

    // ✅ Compile-time Polymorphism (overloading)
    public void log(String msg) {
        System.out.println("[LOG]: " + msg);
    }

    public void log(int id) {
        System.out.println("[LOG ID]: " + id);
    }

    // 🔥 GET STUDENT PROFILE
    public String getStudentProfile(int studentId) {

        StringBuilder sb = new StringBuilder();

        try {

            trackStatus();

            if (studentId <= 0) {
                throw new ApplicationException("Invalid Student ID!");
            }

            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name, s.cgpa, s.college " +
                "FROM students s JOIN users u ON s.student_id = u.user_id " +
                "WHERE s.student_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new DataNotFoundException("Student not found!");
                }
            

            String name = rs.getString("name");

            // ✅ Using hierarchy (User → Student)
            User u = new Student(studentId, name, "email@test.com", rs.getDouble("cgpa"));
            u.displayRole(); // runtime polymorphism

            sb.append("Name: ").append(name).append("\n");
            sb.append("CGPA: ").append(rs.getDouble("cgpa")).append("\n");
            sb.append("College: ").append(rs.getString("college")).append("\n");

            log("Student profile fetched");

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }

        return sb.toString();
    }

    // 🔥 VIEW STUDENT SKILLS
    public String getStudentSkillsString(int studentId) {

        StringBuilder sb = new StringBuilder("Skills:\n\n");

        try {

            trackStatus();

            if (studentId <= 0) {
                throw new ApplicationException("Invalid Student ID!");
            }

            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT sk.skill_name, ss.level " +
                "FROM student_skills ss " +
                "JOIN skills sk ON ss.skill_id = sk.skill_id " +
                "WHERE ss.student_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No skills found!");
            }

            do {
                sb.append(rs.getString("skill_name"))
                  .append(" (Level ")
                  .append(rs.getInt("level"))
                  .append(")\n");
            } while (rs.next());

            // ✅ Using hierarchy again
            User u = new Student(studentId, "Student", "email@test.com", 0.0);
            u.displayRole();

            log(studentId);

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }

        return sb.toString();
    }
}