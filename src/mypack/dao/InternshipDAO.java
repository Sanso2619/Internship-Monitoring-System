package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;
import mypack.exception.DataNotFoundException;

public class InternshipDAO {

    //  View all internships
    public String getAllInternshipsString() {

        StringBuilder sb = new StringBuilder();

        try {

            Connection conn = DBConnection.getConnection();

            String query = "SELECT i.title, c.company_name, i.stipend FROM internships i JOIN companies c ON i.company_id = c.company_id";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new ApplicationException("No internships available!");
            }

            do {
                sb.append(rs.getString("title"))
                  .append(" | ")
                  .append(rs.getString("company_name"))
                  .append("\n");
            } while (rs.next());

        } catch (ApplicationException e) {
            return "Custom Error: " + e.getMessage();
        } catch (Exception e) {
            return "Database Error!";
        }

        return sb.toString();
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

    //  Table data (without ID)
    public String[][] getInternshipsTableData() {

        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT i.title, c.company_name, i.stipend " +
                "FROM internships i JOIN companies c ON i.company_id = c.company_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            java.util.ArrayList<String[]> list = new java.util.ArrayList<>();

            while (rs.next()) {
                String[] row = new String[3];
                row[0] = rs.getString("title");
                row[1] = rs.getString("company_name");
                row[2] = "₹" + rs.getInt("stipend");

                list.add(row);
            }

            String[][] data = new String[list.size()][3];
            for (int i = 0; i < list.size(); i++) {
                data[i] = list.get(i);
            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[0][0];
    }

    //  Table data (with ID)
    public String[][] getInternshipsTableDataWithId() {

        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT i.internship_id, i.title, c.company_name, i.stipend " +
                "FROM internships i LEFT JOIN companies c ON i.company_id = c.company_id";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            java.util.ArrayList<String[]> list = new java.util.ArrayList<>();

            while (rs.next()) {

                String[] row = new String[4];
                row[0] = String.valueOf(rs.getInt("internship_id"));
                row[1] = rs.getString("title");
                row[2] = rs.getString("company_name");
                row[3] = "₹" + rs.getInt("stipend");

                list.add(row);
            }

            if (list.size() == 0) {
                throw new DataNotFoundException("No internships available!");
            }

            String[][] data = new String[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                data[i] = list.get(i);
            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[0][0];
    }
}