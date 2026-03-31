package mypack.model;

public class Application extends User {

    protected String status;

    public Application(int id, String name, String status) {
        super(id, name);
        this.status = status;
    }

    @Override
    public void displayRole() {
        System.out.println("I am an Application");
    }
}