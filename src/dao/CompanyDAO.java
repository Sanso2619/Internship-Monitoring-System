package dao;

import db.DBConnection;
import java.sql.*;

public class CompanyDAO {

    //  View company profile
    public void getCompanyProfile(int companyId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT company_name, location, industry " +
                "FROM companies WHERE company_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nCompany Profile:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("company_name") + " | " +
                    rs.getString("location") + " | " +
                    rs.getString("industry")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  View applicants for company internships
    public void viewApplicants(int companyId) {
        try {
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

            System.out.println("\nApplicants:");

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
}