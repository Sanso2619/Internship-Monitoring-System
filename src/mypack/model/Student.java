
package mypack.model;

public class Student extends User {

    public Student(int id, String name) {
        super(id, name);
    }

    @Override
    public void displayRole() {
        System.out.println("Student User");
    }
}