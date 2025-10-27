package com.tourism.model;

/**
 * Model class (POJO) to represent a single User.
 */
public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String role; // "user" or "admin"

    public User(int id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}
