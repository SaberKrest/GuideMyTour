package com.tourism.model;

/**
 * Model class (POJO) to represent a single Review.
 */
public class Review {

    private int id;
    private int destinationId;
    private int userId;
    private String username; // Store username to avoid extra lookups
    private int rating; // 1-5
    private String comment;

    public Review(int id, int destinationId, int userId, String username, int rating, String comment) {
        this.id = id;
        this.destinationId = destinationId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
