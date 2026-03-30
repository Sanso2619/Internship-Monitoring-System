package dao;

import db.DBConnection;
import java.sql.*;

public class InternshipDAO {

    //  View all internships
    public void getAllInternships() {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT i.title, c.company_name, i.stipend " +
                "FROM internships i " +
                "JOIN companies c ON i.company_id = c.company_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nInternships:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("title") + " | " +
                    rs.getString("company_name") + " | ₹" +
                    rs.getInt("stipend")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  View internship details with skills
    public void getInternshipDetails(int internshipId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT i.title, sk.skill_name " +
                "FROM internships i " +
                "JOIN internship_skills ik ON i.internship_id = ik.internship_id " +
                "JOIN skills sk ON ik.skill_id = sk.skill_id " +
                "WHERE i.internship_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, internshipId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nInternship Skills:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("title") + " | " +
                    rs.getString("skill_name")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}