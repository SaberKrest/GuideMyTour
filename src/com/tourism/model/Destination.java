package com.tourism.model;

import java.util.List;

/**
 * Model class (POJO) to represent a single destination.
 * 'isSaved' is now user-specific and set at runtime.
 * 'price' is now a String to support ranges.
 */
public class Destination {

    private int id;
    private String name;
    private String location;
    private String description;
    private List<String> imagePaths;
    private String price; // --- MODIFICATION: Changed from double to String ---
    private double popularity;
    private boolean isSaved; // Represents if the *current user* has saved this

    public Destination(int id, String name, String location, String description,
                       List<String> imagePaths, String price, double popularity, boolean isSaved) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imagePaths = imagePaths;
        this.price = price; // --- MODIFICATION ---
        this.popularity = popularity;
        this.isSaved = isSaved;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public String getPrimaryImagePath() {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return "placeholder.jpg";
        }
        return imagePaths.get(0);
    }

    // --- MODIFICATION: Getter returns String ---
    public String getPrice() {
        return price;
    }

    public double getPopularity() {
        return popularity;
    }

    public boolean isSaved() {
        return isSaved;
    }

    // --- Setters ---

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void addImagePath(String path) {
        if (this.imagePaths != null) {
            this.imagePaths.add(path);
        }
    }
}

