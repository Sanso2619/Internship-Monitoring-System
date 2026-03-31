package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;
import mypack.interfaces.Trackable;
import mypack.model.*; // using hierarchy classes

class SpecialApplication extends Application {

    private String type;

    public SpecialApplication(int id, String name, String status, String type) {
        super(id, name, status);
        this.type = type;
    }

    @Override
    public void displayRole() {
        System.out.println("Special Application: " + type);
    }
}

// ✅ Implements interface (OOP)
public class ApplicationDAO implements Trackable {

    // ✅ Polymorphism (method override)
    @Override
    public void trackStatus() {
        System.out.println("Tracking DAO operations...");
    }

    // ✅ Polymorphism using method overloading
    public void log(String msg) {
        System.out.println("[LOG]: " + msg);
    }

    public void log(int id) {
        System.out.println("[LOG ID]: " + id);
    }

    // 1. GET ALL STUDENTS
    public void getStudents() {
        try {

            trackStatus(); // interface usage

            Connection conn = DBConnection.getConnection();

            String query = "SELECT * FROM students";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No students found!");
            }

            do {
                int id = rs.getInt("student_id");
                double cgpa = rs.getDouble("cgpa");

                // ✅ Using inheritance hierarchy
                User u = new Student(id, "Student", "email@test.com", cgpa);
                u.displayRole(); // runtime polymorphism
                User u2 = new Application(2, "AppUser", "Pending");
                u2.displayRole();
                System.out.println(id + " | " + cgpa);

            } while (rs.next());

            log("Students fetched");
            
            Application app = new SpecialApplication(1, "TestApp", "Pending", "Premium");

            app.displayRole(); // polymorphism
        } catch (ApplicationException e) {
            System.out.println("Custom Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Database Error!");
        }
    }

    // 2. APPLY INTERNSHIP
    public String applyInternshipString(int studentId, int internshipId) {
        try {

            trackStatus();

            if (studentId <= 0 || internshipId <= 0) {
                throw new ApplicationException("Invalid input values!");
            }

            Connection conn = DBConnection.getConnection();

            String query = "{CALL apply_internship(?, ?)}";
            CallableStatement cs = conn.prepareCall(query);

            cs.setInt(1, studentId);
            cs.setInt(2, internshipId);

            boolean hasResult = cs.execute();

            log(studentId); // polymorphism

            if (hasResult) {
                ResultSet rs = cs.getResultSet();
                if (rs.next()) return rs.getString(1);
            }

            return "Applied successfully!";

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }
    }

    // 3. RECOMMENDATIONS
    public String getRecommendationsString(int studentId) {

        StringBuilder sb = new StringBuilder("Recommendations:\n\n");

        try {

            trackStatus();

            if (studentId <= 0) {
                throw new ApplicationException("Invalid Student ID!");
            }

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

            if (!rs.next()) {
                throw new ApplicationException("No recommendations found!");
            }

            do {
                sb.append(rs.getString("title"))
                  .append(" | Score: ")
                  .append(rs.getInt("match_score"))
                  .append("\n");
            } while (rs.next());

            log("Recommendations fetched");

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }

        return sb.toString();
    }

    // 4. VIEW APPLICATIONS
    public String getApplicationsString(int studentId) {

        StringBuilder sb = new StringBuilder("Applications:\n\n");

        try {

            trackStatus();

            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name, i.title, a.status " +
                "FROM applications a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN users u ON s.student_id = u.user_id " +
                "JOIN internships i ON a.internship_id = i.internship_id" +
                "WHERE s.student_id = ?";


            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No applications found!");
            }

            do {
                sb.append(rs.getString("name"))
                  .append(" | ")
                  .append(rs.getString("title"))
                  .append(" | ")
                  .append(rs.getString("status"))
                  .append("\n");
            } while (rs.next());

            log("Applications fetched");

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }

        return sb.toString();
    }
}