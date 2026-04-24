package mypack.dao;

import java.sql.*;
import mypack.db.DBConnection;

public class AdminDAO {

    // 🔥 TOTAL STATS
    public String getStats() {

        StringBuilder sb = new StringBuilder("📊 SYSTEM STATS\n\n");

        try {
            Connection conn = DBConnection.getConnection();

            String q1 = "SELECT COUNT(*) FROM students";
            String q2 = "SELECT COUNT(*) FROM companies";
            String q3 = "SELECT COUNT(*) FROM internships";

            Statement st = conn.createStatement();

            ResultSet rs1 = st.executeQuery(q1);
            rs1.next();
            sb.append("Students: ").append(rs1.getInt(1)).append("\n");

            ResultSet rs2 = st.executeQuery(q2);
            rs2.next();
            sb.append("Companies: ").append(rs2.getInt(1)).append("\n");

            ResultSet rs3 = st.executeQuery(q3);
            rs3.next();
            sb.append("Internships: ").append(rs3.getInt(1)).append("\n");

        } catch (Exception e) {
            return "Error loading stats!";
        }

        return sb.toString();
    }

    // 🔥 UNPLACED STUDENTS
    public String getUnplacedStudents() {

        StringBuilder sb = new StringBuilder("🔴 Unplaced Students:\n\n");

        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name " +
                "FROM students s JOIN users u ON s.student_id = u.user_id " +
                "WHERE s.student_id NOT IN (" +
                "SELECT student_id FROM applications WHERE status='shortlisted')";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("name")).append("\n");
            }

        } catch (Exception e) {
            return "Error!";
        }

        return sb.toString();
    }

    // 🔥 RISK ALERT (too many applications)
    public String getRiskStudents() {

        StringBuilder sb = new StringBuilder("⚠️ High Application Students:\n\n");

        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT u.name, COUNT(*) as total " +
                "FROM applications a " +
                "JOIN users u ON a.student_id = u.user_id " +
                "GROUP BY a.student_id " +
                "HAVING COUNT(*) > 2";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("name"))
                  .append(" (").append(rs.getInt("total")).append(" applications)\n");
            }

        } catch (Exception e) {
            return "Error!";
        }

        return sb.toString();
    }

    // 🔥 INACTIVE COMPANIES
    public String getInactiveCompanies() {

        StringBuilder sb = new StringBuilder("💤 Inactive Companies:\n\n");

        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT c.company_name " +
                "FROM companies c " +
                "LEFT JOIN internships i ON c.company_id = i.company_id " +
                "WHERE i.internship_id IS NULL";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                sb.append(rs.getString("company_name")).append("\n");
            }

        } catch (Exception e) {
            return "Error!";
        }

        return sb.toString();
    }
    public String[][] getAllApplicationsTable() {

    try {
        Connection conn = DBConnection.getConnection();

        String query =
            "SELECT u.name AS student, " +
            "IFNULL(c.company_name, 'N/A') AS company_name, " +
            "IFNULL(i.title, 'No Application') AS title, " +
            "IFNULL(a.status, 'Not Applied') AS status " +
            "FROM students s " +
            "JOIN users u ON s.student_id = u.user_id " +
            "LEFT JOIN applications a ON s.student_id = a.student_id " +
            "LEFT JOIN internships i ON a.internship_id = i.internship_id " +
            "LEFT JOIN companies c ON i.company_id = c.company_id";
            

        PreparedStatement ps = conn.prepareStatement(
            query,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );

        ResultSet rs = ps.executeQuery();

        rs.last();
        int size = rs.getRow();
        rs.beforeFirst();

        String[][] data = new String[size][4];

        int i = 0;
        while (rs.next()) {
            data[i][0] = rs.getString("student");
            data[i][1] = rs.getString("company_name");
            data[i][2] = rs.getString("title");
            data[i][3] = rs.getString("status");
            i++;
        }

        return data;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new String[0][0];
}
}