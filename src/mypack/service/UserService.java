package mypack.service;

import mypack.model.User;

public class UserService {

    public void show(User user) {
        user.displayRole(); // polymorphism
    }
}