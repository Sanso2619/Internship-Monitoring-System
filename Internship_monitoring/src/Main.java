import dao.ApplicationDAO;

public class Main {
    public static void main(String[] args) {

        ApplicationDAO dao = new ApplicationDAO();

        dao.getStudents();

        dao.recommendInternships(1);

        dao.applyInternship(1, 1);
    }
}