package com.tourism.user;

import com.tourism.model.User;

/**
 * A service class to hold the *currently logged-in* user's state.
 */
public class UserService {

    private User currentUser;
    private String theme;

    public UserService() {
        // No user logged in by default
        this.currentUser = null;
        this.theme = "Light"; // Default theme
    }

    // --- User Session ---

    /**
     * Logs a user in by storing their data.
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Logs the current user out.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    /**
     * Gets the full User object for the logged-in user.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Gets the logged-in user's ID.
     *
     * @return User ID, or -1 if not logged in.
     */
    public int getUserId() {
        return isLoggedIn() ? this.currentUser.getId() : -1;
    }

    /**
     * Gets the logged-in user's username.
     */
    public String getUsername() {
        return isLoggedIn() ? this.currentUser.getUsername() : "Guest";
    }

    /**
     * Gets the logged-in user's role ("user" or "admin").
     */
    public String getRole() {
        return isLoggedIn() ? this.currentUser.getRole() : "guest";
    }


    // --- Theme ---

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
