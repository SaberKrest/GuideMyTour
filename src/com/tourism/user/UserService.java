package com.tourism.user;

import com.tourism.model.User;

/**
 * A service class to hold the *currently logged-in* user's state.
 * MODIFIED:
 * - Added isAdmin() method.
 */
public class UserService {

    private User currentUser;
    private String theme;

    public UserService() {
        this.currentUser = null;
        this.theme = "Light"; // Default theme
    }

    // --- User Session ---
    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public int getUserId() {
        return isLoggedIn() ? this.currentUser.getId() : -1;
    }

    public String getUsername() {
        return isLoggedIn() ? this.currentUser.getUsername() : "Guest";
    }

    // --- MODIFICATION: Added missing isAdmin() method ---
    /**
     * Checks if the currently logged-in user is an administrator.
     */
    public boolean isAdmin() {
        return isLoggedIn() && "admin".equals(this.currentUser.getRole());
    }
    // --- End of Modification ---


    // --- Theme Settings ---
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
