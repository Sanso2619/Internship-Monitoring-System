package mypack;

import mypack.ui.Login;
import mypack.model.*;

public class Main {

    public static void main(String[] args) {

        // 🔥 ===== OOP DEMONSTRATION =====

        // ✅ Runtime Polymorphism (User reference)
        User u1 = new Student(1, "Sanchita", "s@gmail.com", 8.5);
        u1.displayRole();

        // ✅ Hierarchical Inheritance (User → Company)
        User u2 = new Company(2, "Google");
        u2.displayRole();

        // ✅ Multilevel Inheritance (User → Application → SpecialApplication)
        Application app = new Application(3, "AppUser", "Pending");
        app.displayRole();

        // (Optional but strong)
        // SpecialApplication is inside DAO, so we don't create here
        // already demonstrated in ApplicationDAO

        // 🔥 ===== START UI =====
        new Login();
    }
}