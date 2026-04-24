package mypack.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mypack.db.DBConnection;
import mypack.exception.ApplicationException;
import mypack.exception.InvalidInputException;
import mypack.exception.UnauthorizedActionException;
import mypack.interfaces.Trackable;
import mypack.model.*;

public class CompanyDAO implements Trackable {

    @Override
    public void trackStatus() {
        System.out.println("Tracking CompanyDAO operations...");
    }

    public void log(String msg) {
        System.out.println("[LOG]: " + msg);
    }

    public void log(int id) {
        System.out.println("[LOG ID]: " + id);
    }

    public void getCompanyProfile(int companyId) {
        try {

            trackStatus();

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

                User u = new Company(companyId, name);
                u.displayRole();

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
            e.printStackTrace();
            System.out.println("Database Error: " + e.getMessage());
        }
    }

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

                User u = new Student(1, studentName, "email@test.com", 0.0);
                u.displayRole();

                System.out.println(
                    studentName + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("status")
                );

            } while (rs.next());

            log(companyId);

        } catch (ApplicationException e) {
            System.out.println("Custom Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public String[][] getApplicantsTableData(int companyId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT a.application_id, u.name, i.title, a.status " +
                "FROM applications a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN users u ON s.student_id = u.user_id " +
                "JOIN internships i ON a.internship_id = i.internship_id " +
                "WHERE i.company_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);

            ResultSet rs = ps.executeQuery();

            List<String[]> list = new ArrayList<>();

            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("application_id")),
                    rs.getString("name"),
                    rs.getString("title"),
                    rs.getString("status")
                };
                list.add(row);
            }

            return list.toArray(new String[0][0]);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getApplicantsTableData Error: " + e.getMessage());
            return new String[0][0];
        }
    }

    public String[][] getInternshipsTableData(int companyId) {
        try {
            Connection conn = DBConnection.getConnection();

            String query =
                "SELECT internship_id, title, min_cgpa, stipend " +
                "FROM internships WHERE company_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);

            ResultSet rs = ps.executeQuery();

            List<String[]> list = new ArrayList<>();

            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getInt("internship_id")),
                    rs.getString("title"),
                    String.valueOf(rs.getDouble("min_cgpa")),
                    String.valueOf(rs.getInt("stipend"))
                };
                list.add(row);
            }

            return list.toArray(new String[0][0]);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getInternshipsTableData Error: " + e.getMessage());
            return new String[0][0];
        }
    }

    public String addInternship(int companyId, String title, double cgpa, int stipend) {
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new ApplicationException("Title cannot be empty!");
            }

            if (cgpa < 0 || cgpa > 10) {
                throw new ApplicationException("CGPA must be between 0 and 10!");
            }

            if (stipend < 0) {
                throw new ApplicationException("Stipend cannot be negative!");
            }

            Connection conn = DBConnection.getConnection();

            String query = "INSERT INTO internships(company_id, title, min_cgpa, stipend) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, companyId);
            ps.setString(2, title.trim());
            ps.setDouble(3, cgpa);
            ps.setInt(4, stipend);

            ps.executeUpdate();

            log("Internship added for company: " + companyId);

            return "Internship added successfully!";

        } catch (ApplicationException e) {
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }

    public String updateApplicationStatus(int applicationId, String status) {
        try {
            
            if (applicationId <= 0) {
                throw new InvalidInputException("Invalid Application ID!");
            }

            if (!(status.equals("shortlisted") || status.equals("rejected"))) {
                throw new UnauthorizedActionException("Invalid status!");
            }

            Connection conn = DBConnection.getConnection();

            String query = "UPDATE applications SET status = ? WHERE application_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, applicationId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                return "No application found!";
            }

            // ✅ FIXED: moved before return
            log("Status updated: appId=" + applicationId + " status=" + status);

            return "Status updated successfully!";

        } catch (Exception e) {   // ✅ FIXED
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }

    public String updateStatusByInternship(int internshipId, String status) {
        try {
            if (internshipId <= 0) {
                throw new ApplicationException("Invalid Internship ID!");
            }

            Connection conn = DBConnection.getConnection();

            String query = "UPDATE applications SET status = ? WHERE internship_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, internshipId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                return "No applicants found for this internship!";
            }

            log("Updated " + rows + " applicants to: " + status);

            return rows + " applicant(s) marked as " + status + "!";

        } catch (ApplicationException e) {
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }

    public String autoShortlistByInternship(int internshipId) {
        try {
            Connection conn = DBConnection.getConnection();

            String shortlistQuery =
                "UPDATE applications a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN internships i ON a.internship_id = i.internship_id " +
                "SET a.status = 'shortlisted' " +
                "WHERE a.internship_id = ? AND s.cgpa >= i.min_cgpa";

            PreparedStatement ps1 = conn.prepareStatement(shortlistQuery);
            ps1.setInt(1, internshipId);
            int shortlisted = ps1.executeUpdate();

            String rejectQuery =
                "UPDATE applications a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN internships i ON a.internship_id = i.internship_id " +
                "SET a.status = 'rejected' " +
                "WHERE a.internship_id = ? AND s.cgpa < i.min_cgpa";

            PreparedStatement ps2 = conn.prepareStatement(rejectQuery);
            ps2.setInt(1, internshipId);
            int rejected = ps2.executeUpdate();

            return shortlisted + " shortlisted, " + rejected + " rejected based on CGPA!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    public String deleteInternship(int internshipId) {
    try {

        if (internshipId <= 0) {
            throw new ApplicationException("Invalid Internship ID!");
        }

        Connection conn = DBConnection.getConnection();

        // 🔴 If you DON'T have ON DELETE CASCADE, delete dependent records first
        // (recommended if FK constraints block deletion)
        String delSkills = "DELETE FROM internship_skills WHERE internship_id = ?";
        PreparedStatement ps1 = conn.prepareStatement(delSkills);
        ps1.setInt(1, internshipId);
        ps1.executeUpdate();

        String delApps = "DELETE FROM applications WHERE internship_id = ?";
        PreparedStatement ps2 = conn.prepareStatement(delApps);
        ps2.setInt(1, internshipId);
        ps2.executeUpdate();

        // 🔴 Finally delete internship
        String query = "DELETE FROM internships WHERE internship_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, internshipId);

        int rows = ps.executeUpdate();

        if (rows == 0) {
            return "No internship found!";
        }

        log("Internship deleted: " + internshipId);

        return "Internship deleted successfully!";

    } catch (ApplicationException e) {
        return e.getMessage();
    } catch (Exception e) {
        e.printStackTrace();
        return "Database Error: " + e.getMessage();
    }
}
}
    