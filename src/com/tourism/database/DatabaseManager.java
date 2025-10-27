package com.tourism.database;

import com.tourism.model.Destination;
import com.tourism.model.Review;
import com.tourism.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File; // Import File utility

/**
 * Manages all database operations (CRUD) for destinations, users, and reviews.
 * Uses SQLite.
 */
public class DatabaseManager {

    private static final String DB_FILE_PATH = System.getProperty("user.dir") + File.separator + "tourism.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public DatabaseManager() {
        System.out.println("DatabaseManager connecting to: " + DB_URL);
        initializeDatabase();
    }

    /**
     * Creates the required tables if they don't already exist.
     */
    private void initializeDatabase() {
        // --- MODIFICATION: price is now TEXT ---
        String createDestinationsTableSql = "CREATE TABLE IF NOT EXISTS destinations ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "location TEXT NOT NULL,"
                + "description TEXT,"
                + "price TEXT NOT NULL," // Changed from REAL to TEXT
                + "popularity REAL NOT NULL"
                + ");";

        // SQL for images table (Unchanged)
        String createImagesTableSql = "CREATE TABLE IF NOT EXISTS images ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "destination_id INTEGER NOT NULL,"
                + "image_path TEXT NOT NULL,"
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE"
                + ");";

        // SQL for users table
        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password_hash TEXT NOT NULL,"
                + "role TEXT NOT NULL DEFAULT 'user'"
                + ");";

        // SQL for saved_links table
        String createSavedLinksTableSql = "CREATE TABLE IF NOT EXISTS saved_links ("
                + "user_id INTEGER NOT NULL,"
                + "destination_id INTEGER NOT NULL,"
                + "PRIMARY KEY (user_id, destination_id),"
                + "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,"
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE"
                + ");";

        // SQL for reviews table
        String createReviewsTableSql = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "destination_id INTEGER NOT NULL,"
                + "user_id INTEGER NOT NULL,"
                + "username TEXT NOT NULL,"
                + "rating INTEGER NOT NULL,"
                + "comment TEXT,"
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE,"
                + "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"
                + ");";


        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createDestinationsTableSql);
            stmt.execute(createImagesTableSql);
            stmt.execute(createUsersTableSql);
            stmt.execute(createSavedLinksTableSql);
            stmt.execute(createReviewsTableSql);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- User Management Methods (Unchanged) ---

    public void createUser(String username, String passwordHash, String role) {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --- Destination Management Methods ---

    public void addDestination(Destination destination) {
        // --- MODIFICATION: price is now TEXT ---
        String insertDestinationSql = "INSERT INTO destinations(name, location, description, price, popularity) VALUES(?,?,?,?,?)";
        String insertImageSql = "INSERT INTO images(destination_id, image_path) VALUES(?,?)";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDest = conn.prepareStatement(insertDestinationSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmtDest.setString(1, destination.getName());
                pstmtDest.setString(2, destination.getLocation());
                pstmtDest.setString(3, destination.getDescription());
                pstmtDest.setString(4, destination.getPrice()); // Changed to setString
                pstmtDest.setDouble(5, destination.getPopularity());
                pstmtDest.executeUpdate();

                int newDestinationId;
                try (ResultSet generatedKeys = pstmtDest.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newDestinationId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating destination failed, no ID obtained.");
                    }
                }

                if (destination.getImagePaths() != null && !destination.getImagePaths().isEmpty()) {
                    try (PreparedStatement pstmtImg = conn.prepareStatement(insertImageSql)) {
                        for (String imagePath : destination.getImagePaths()) {
                            pstmtImg.setInt(1, newDestinationId);
                            pstmtImg.setString(2, imagePath);
                            pstmtImg.addBatch();
                        }
                        pstmtImg.executeBatch();
                    }
                }
            }
            conn.commit();
            System.out.println("Successfully added new destination: " + destination.getName());
        } catch (SQLException e) {
            System.err.println("Transaction failed. Rolling back. Error: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { /* ignored */ }
            }
        }
    }

    public void deleteDestination(int destinationId) {
        String sql = "DELETE FROM destinations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            pstmt.executeUpdate();
            System.out.println("Successfully deleted destination ID: " + destinationId);
        } catch (SQLException e) {
            System.err.println("Error deleting destination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Destination> getAllDestinationsSorted(String sortBy, int userId) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT d.*, (s.user_id IS NOT NULL) as is_saved_by_user "
                + "FROM destinations d "
                + "LEFT JOIN saved_links s ON d.id = s.destination_id AND s.user_id = ? ";

        switch (sortBy) {
            case "popular":
                sql += " ORDER BY d.popularity DESC";
                break;
            case "price":
                // Can't numerically sort a text field, so we sort alphabetically
                sql += " ORDER BY d.price ASC";
                break;
            default:
                sql += " ORDER BY d.name ASC";
                break;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(mapResultSetToDestination(rs, rs.getBoolean("is_saved_by_user")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all destinations: " + e.getMessage());
            e.printStackTrace();
        }
        return destinations;
    }

    public List<Destination> searchDestinations(String query, int userId) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT d.*, (s.user_id IS NOT NULL) as is_saved_by_user "
                + "FROM destinations d "
                + "LEFT JOIN saved_links s ON d.id = s.destination_id AND s.user_id = ? "
                + "WHERE d.name LIKE ? OR d.location LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + query + "%");
            pstmt.setString(3, "%" + query + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(mapResultSetToDestination(rs, rs.getBoolean("is_saved_by_user")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching destinations: " + e.getMessage());
            e.printStackTrace();
        }
        return destinations;
    }


    public List<Destination> getSavedDestinations(int userId) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT d.* FROM destinations d "
                + "JOIN saved_links s ON d.id = s.destination_id "
                + "WHERE s.user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(mapResultSetToDestination(rs, true));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting saved destinations: " + e.getMessage());
        }
        return destinations;
    }

    public void updateSavedStatus(int destinationId, int userId, boolean isSaved) {
        String sql;
        if (isSaved) {
            sql = "INSERT OR IGNORE INTO saved_links (user_id, destination_id) VALUES (?, ?)";
        } else {
            sql = "DELETE FROM saved_links WHERE user_id = ? AND destination_id = ?";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, destinationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating saved status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to map a ResultSet row to a Destination object.
     */
    private Destination mapResultSetToDestination(ResultSet rs, boolean isSaved) throws SQLException {
        int id = rs.getInt("id");
        List<String> imagePaths = fetchImagePaths(id);
        return new Destination(
                id,
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("description"),
                imagePaths,
                rs.getString("price"), // Changed to getString
                rs.getDouble("popularity"),
                isSaved
        );
    }

    // --- Image Management Methods (Unchanged) ---

    private List<String> fetchImagePaths(int destinationId) {
        List<String> imagePaths = new ArrayList<>();
        String sql = "SELECT image_path FROM images WHERE destination_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    imagePaths.add(rs.getString("image_path"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching images: " + e.getMessage());
        }
        return imagePaths;
    }

    public void addImagesToDestination(int destinationId, List<String> newImagePaths) {
        String sql = "INSERT INTO images(destination_id, image_path) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String imagePath : newImagePaths) {
                pstmt.setInt(1, destinationId);
                pstmt.setString(2, imagePath);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error adding images to destination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Destination getDestinationById(int destinationId, int userId) {
        String sql = "SELECT d.*, (s.user_id IS NOT NULL) as is_saved_by_user "
                + "FROM destinations d "
                + "LEFT JOIN saved_links s ON d.id = s.destination_id AND s.user_id = ? "
                + "WHERE d.id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDestination(rs, rs.getBoolean("is_saved_by_user"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting destination by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    // --- Review Management Methods (Unchanged) ---

    public void addReview(Review review) {
        String sql = "INSERT INTO reviews(destination_id, user_id, username, rating, comment) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getDestinationId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setString(3, review.getUsername());
            pstmt.setInt(4, review.getRating());
            pstmt.setString(5, review.getComment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Review> getReviewsForDestination(int destinationId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE destination_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                            rs.getInt("id"),
                            rs.getInt("destination_id"),
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getInt("rating"),
                            rs.getString("comment")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return reviews;
    }
}

