package com.tourism.model;

import java.util.List;

/**
 * Model class (POJO) to represent a single destination.
 * MODIFIED:
 * - Added touristSpots, localSpots, and shops fields.
 * - Updated constructor to 11 arguments.
 * - Added getters for new fields.
 * - Updated placeholder image path.
 */
public class Destination {

    private int id;
    private String name;
    private String location;
    private String description;
    private List<String> imagePaths;
    private String price;
    private double popularity;
    private boolean isSaved;

    // --- MODIFICATION: Added new fields ---
    private String touristSpots;
    private String localSpots;
    private String shops;
    // --- End of Modification ---

    // --- MODIFICATION: Updated to 11-argument constructor ---
    public Destination(int id, String name, String location, String description,
                       List<String> imagePaths, String price, double popularity, boolean isSaved,
                       String touristSpots, String localSpots, String shops) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imagePaths = imagePaths;
        this.price = price;
        this.popularity = popularity;
        this.isSaved = isSaved;
        this.touristSpots = touristSpots;
        this.localSpots = localSpots;
        this.shops = shops;
    }
    // --- End of Modification ---


    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public List<String> getImagePaths() { return imagePaths; }

    public String getPrimaryImagePath() {
        if (imagePaths == null || imagePaths.isEmpty()) {
            // --- MODIFICATION: Updated placeholder path ---
            return "/com/tourism/resources/assets/placeholder.jpg";
            // --- End of Modification ---
        }
        return imagePaths.get(0);
    }

    public String getPrice() { return price; }
    public double getPopularity() { return popularity; }
    public boolean isSaved() { return isSaved; }

    // --- MODIFICATION: Added new getters ---
    public String getTouristSpots() { return touristSpots; }
    public String getLocalSpots() { return localSpots; }
    public String getShops() { return shops; }
    // --- End of Modification ---


    // --- Setters ---
    public void setSaved(boolean saved) {
        this.isSaved = saved;
    }

    // Setters for admin editing (if needed, but constructor is used for now)
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public void setTouristSpots(String touristSpots) { this.touristSpots = touristSpots; }
    public void setLocalSpots(String localSpots) { this.localSpots = localSpots; }
    public void setShops(String shops) { this.shops = shops; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
}

