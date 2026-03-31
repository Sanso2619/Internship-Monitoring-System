
package mypack.model;

public class Company extends User {

    public Company(int id, String name) {
        super(id, name);
    }

    @Override
    public void displayRole() {
        System.out.println("Company User");
    }
}