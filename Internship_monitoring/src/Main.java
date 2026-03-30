import dao.ApplicationDAO;
import dao.CompanyDAO;
import dao.InternshipDAO;
import dao.StudentDAO;

public class Main {

    public static void main(String[] args) {

        //  Create DAO objects
        StudentDAO studentDAO = new StudentDAO();
        CompanyDAO companyDAO = new CompanyDAO();
        InternshipDAO internshipDAO = new InternshipDAO();
        ApplicationDAO applicationDAO = new ApplicationDAO();

        //  STUDENT MODULE
        studentDAO.getStudentProfile(1);
        studentDAO.getStudentSkills(1);

        //  INTERNSHIP MODULE
        internshipDAO.getAllInternships();
        internshipDAO.getInternshipDetails(1);

        //  RECOMMENDATION SYSTEM
        applicationDAO.recommendInternships(1);

        //  APPLICATION SYSTEM
        applicationDAO.applyInternship(1, 1);

        //  VIEW APPLICATIONS
        applicationDAO.viewApplications();

        // COMPANY MODULE
        companyDAO.getCompanyProfile(3);
        companyDAO.viewApplicants(3);

        //  NOTIFICATIONS + HISTORY
        applicationDAO.viewStatusHistory();
        applicationDAO.viewNotifications(1);

        System.out.println("\n===== SYSTEM EXECUTION COMPLETE =====");
    }
}