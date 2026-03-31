package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;
import mypack.interfaces.Trackable;
import mypack.model.*;

// ✅ Implements interface
public class CompanyDAO implements Trackable {

    // ✅ Runtime Polymorphism (override)
    @Override
    public void trackStatus() {
        System.out.println("Tracking CompanyDAO operations...");
    }

    // ✅ Compile-time Polymorphism (method overloading)
    public void log(String msg) {
        System.out.println("[LOG]: " + msg);
    }

    public void log(int id) {
        System.out.println("[LOG ID]: " + id);
    }

    // 🔥 VIEW COMPANY PROFILE
    public void getCompanyProfile(int companyId) {
        try {

            trackStatus(); // interface usage

            if (companyId <= 0) {
                throw new ApplicationException("Invalid Company ID!");
            }

            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT company_name, location, industry " +
                "FROM companies WHERE company_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No company found!");
            }

            do {
                String name = rs.getString("company_name");

                // ✅ Using hierarchy (User → Company)
                User u = new Company(companyId, name);
                u.displayRole(); // runtime polymorphism

                System.out.println(
                        name + " | " +
                        rs.getString("location") + " | " +
                        rs.getString("industry")
                );

            } while (rs.next());

            log("Company profile fetched");

        } catch (ApplicationException e) {
            System.out.println("Custom Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Database Error!");
        }
    }

    // 🔥 VIEW APPLICANTS
    public void viewApplicants(int companyId) {
        try {

            trackStatus();

            if (companyId <= 0) {
                throw new ApplicationException("Invalid Company ID!");
            }

            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name, i.title, a.status " +
                "FROM applications a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN users u ON s.student_id = u.user_id " +
                "JOIN internships i ON a.internship_id = i.internship_id " +
                "WHERE i.company_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No applicants found!");
            }

            System.out.println("\nApplicants:");

            do {
                String studentName = rs.getString("name");

                // ✅ Using hierarchy (User → Student)
                User u = new Student(1, studentName, "email@test.com", 0.0);
                u.displayRole(); // polymorphism

                System.out.println(
                        studentName + " | " +
                        rs.getString("title") + " | " +
                        rs.getString("status")
                );

            } while (rs.next());

            log(companyId); // method overloading

        } catch (ApplicationException e) {
            System.out.println("Custom Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Database Error!");
        }
    }
}