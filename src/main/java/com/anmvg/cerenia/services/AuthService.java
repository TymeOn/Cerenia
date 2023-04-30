package com.anmvg.cerenia.services;

import com.anmvg.cerenia.models.User;


public class AuthService {

    // the singleton instance
    private static AuthService instance;
    private static User user = null;

    // the service constructor, fetching the initial data
    private AuthService() {}

    // the service instance getter, as it is a singleton
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    // checking the DB for the specified credentials
    public boolean login(String email, String password) {
        boolean isValid = false;
        for (User user : DataService.getInstance().getUserList()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                AuthService.user = user;
                isValid = true;
            }
        }
        return isValid;
    }

    // logging out
    public void logout() {
        AuthService.user = null;
    }

    // getting the stored user
    public User getUser() {
        return AuthService.user;
    }

}
