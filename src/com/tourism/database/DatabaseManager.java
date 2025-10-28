package com.tourism.database;

import com.tourism.model.Destination;
import com.tourism.model.Review;
import com.tourism.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Manages all database operations.
 * MODIFIED:
 * - Price sort now correctly casts the text price to a number.
 * - Search now sorts alphabetically by name.
 */
public class DatabaseManager {

    private static final String DB_FILE_PATH = System.getProperty("user.dir") + File.separator + "tourism.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public DatabaseManager() {
        System.out.println("DatabaseManager connecting to: " + DB_URL);
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createDestinationsTableSql = "CREATE TABLE IF NOT EXISTS destinations ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "location TEXT NOT NULL, "
                + "description TEXT, "
                + "price TEXT NOT NULL, "
                + "popularity REAL NOT NULL, "
                + "tourist_spots TEXT, "
                + "local_spots TEXT, "
                + "shops TEXT "
                + ");";

        String createImagesTableSql = "CREATE TABLE IF NOT EXISTS destination_images ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "destination_id INTEGER NOT NULL, "
                + "image_path TEXT NOT NULL, "
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE"
                + ");";

        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password_hash TEXT NOT NULL, "
                + "role TEXT NOT NULL"
                + ");";

        String createSavedPlacesTableSql = "CREATE TABLE IF NOT EXISTS saved_places ( "
                + "user_id INTEGER NOT NULL, "
                + "destination_id INTEGER NOT NULL, "
                + "PRIMARY KEY (user_id, destination_id), "
                + "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, "
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE"
                + ");";

        String createReviewsTableSql = "CREATE TABLE IF NOT EXISTS reviews ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "destination_id INTEGER NOT NULL, "
                + "user_id INTEGER NOT NULL, "
                + "username TEXT NOT NULL, "
                + "rating INTEGER NOT NULL, "
                + "comment TEXT, "
                + "FOREIGN KEY (destination_id) REFERENCES destinations (id) ON DELETE CASCADE, "
                + "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createDestinationsTableSql);
            stmt.execute(createImagesTableSql);
            stmt.execute(createUsersTableSql);
            stmt.execute(createSavedPlacesTableSql);
            stmt.execute(createReviewsTableSql);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addDestination(Destination dest) throws SQLException {
        String sql = "INSERT INTO destinations(name, location, description, price, popularity, tourist_spots, local_spots, shops) "
                + "VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, dest.getName());
            pstmt.setString(2, dest.getLocation());
            pstmt.setString(3, dest.getDescription());
            pstmt.setString(4, dest.getPrice());
            pstmt.setDouble(5, dest.getPopularity());
            pstmt.setString(6, dest.getTouristSpots());
            pstmt.setString(7, dest.getLocalSpots());
            pstmt.setString(8, dest.getShops());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int destinationId = generatedKeys.getInt(1);
                    for (String imgPath : dest.getImagePaths()) {
                        addImageForDestination(destinationId, imgPath);
                    }
                }
            }
        }
    }

    public void updateDestination(Destination dest) throws SQLException {
        String sql = "UPDATE destinations SET "
                + "name = ?, location = ?, description = ?, price = ?, popularity = ?, "
                + "tourist_spots = ?, local_spots = ?, shops = ? "
                + "WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dest.getName());
            pstmt.setString(2, dest.getLocation());
            pstmt.setString(3, dest.getDescription());
            pstmt.setString(4, dest.getPrice());
            pstmt.setDouble(5, dest.getPopularity());
            pstmt.setString(6, dest.getTouristSpots());
            pstmt.setString(7, dest.getLocalSpots());
            pstmt.setString(8, dest.getShops());
            pstmt.setInt(9, dest.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteDestination(int destinationId) throws SQLException {
        String sql = "DELETE FROM destinations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            pstmt.executeUpdate();
        }
    }


    /**
     * MODIFICATION: Fixed price sorting.
     * Extracts the first number from the price string (e.g., "$1,000" or "100 - 200")
     * and casts it as a number for correct sorting.
     */
    public List<Destination> getAllDestinations(int userId, String sortBy) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT * FROM destinations";

        if ("popularity".equals(sortBy)) {
            sql += " ORDER BY popularity DESC";
        } else if ("price".equals(sortBy)) {
            // --- FIX: Cast the cleaned price string to a number ---
            sql += " ORDER BY CAST(REPLACE(REPLACE(price, '$', ''), ',', '') AS REAL) ASC";
            // --- End of Fix ---
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                destinations.add(createDestinationFromResultSet(rs, userId));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all destinations: " + e.getMessage());
        }
        return destinations;
    }

    public Destination getDestinationById(int destinationId, int userId) {
        String sql = "SELECT * FROM destinations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createDestinationFromResultSet(rs, userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting destination by ID: " + e.getMessage());
        }
        return null;
    }

    private Destination createDestinationFromResultSet(ResultSet rs, int userId) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String location = rs.getString("location");
        String description = rs.getString("description");
        String price = rs.getString("price");
        double popularity = rs.getDouble("popularity");
        String touristSpots = rs.getString("tourist_spots");
        String localSpots = rs.getString("local_spots");
        String shops = rs.getString("shops");

        List<String> imagePaths = getImagesForDestination(id);
        boolean isSaved = isDestinationSaved(id, userId);

        return new Destination(
                id, name, location, description, imagePaths,
                price, popularity, isSaved,
                touristSpots, localSpots, shops
        );
    }

    public List<String> getImagesForDestination(int destinationId) {
        List<String> paths = new ArrayList<>();
        String sql = "SELECT image_path FROM destination_images WHERE destination_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paths.add(rs.getString("image_path"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting images: " + e.getMessage());
        }
        return paths;
    }

    public void addImageForDestination(int destinationId, String imagePath) throws SQLException {
        String sql = "INSERT INTO destination_images(destination_id, image_path) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, destinationId);
            pstmt.setString(2, imagePath);
            pstmt.executeUpdate();
        }
    }

    public void deleteImageByPath(String imagePath) throws SQLException {
        String sql = "DELETE FROM destination_images WHERE image_path = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imagePath);
            pstmt.executeUpdate();
        }
    }

    public boolean isDestinationSaved(int destinationId, int userId) {
        if (userId == -1) return false;
        String sql = "SELECT 1 FROM saved_places WHERE user_id = ? AND destination_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, destinationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking saved status: " + e.getMessage());
            return false;
        }
    }

    public void saveDestination(int userId, int destinationId) throws SQLException {
        String sql = "INSERT INTO saved_places(user_id, destination_id) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, destinationId);
            pstmt.executeUpdate();
        }
    }

    public void unsaveDestination(int userId, int destinationId) throws SQLException {
        String sql = "DELETE FROM saved_places WHERE user_id = ? AND destination_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, destinationId);
            pstmt.executeUpdate();
        }
    }

    public List<Destination> getSavedDestinations(int userId) {
        List<Destination> destinations = new ArrayList<>();
        String sql = "SELECT d.* FROM destinations d "
                + "JOIN saved_places s ON d.id = s.destination_id "
                + "WHERE s.user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(createDestinationFromResultSet(rs, userId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting saved destinations: " + e.getMessage());
        }
        return destinations;
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
        }
        return null;
    }

    public void createUser(String username, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        }
    }

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
        String sql = "SELECT * FROM reviews WHERE destination_id = ? ORDER BY id DESC";
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

    /**
     * MODIFICATION: Search results are now sorted alphabetically by name.
     */
    public List<Destination> searchDestinations(String query, int userId) {
        List<Destination> destinations = new ArrayList<>();
        // --- FIX: Order by name ASC ---
        String sql = "SELECT * FROM destinations WHERE LOWER(name) LIKE ? OR LOWER(location) LIKE ? ORDER BY name ASC";
        // --- End of Fix ---

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(createDestinationFromResultSet(rs, userId));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching destinations: " + e.getMessage());
        }
        return destinations;
    }
}

