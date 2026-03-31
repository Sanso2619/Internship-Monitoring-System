package mypack.model;

public class Student extends User {

    private String email;
    private double cgpa;

    public Student(int id, String name, String email, double cgpa) {
        super(id, name);
        this.email = email;
        this.cgpa = cgpa;
    }

    @Override
    public void displayRole() {
        System.out.println("I am a Student");
    }
}