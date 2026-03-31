
package mypack.model;

import mypack.interfaces.Trackable;

public class Application implements Trackable {

    private int id;
    private String status;

    public Application(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public void trackStatus() {
        System.out.println("Status: " + status);
    }
}