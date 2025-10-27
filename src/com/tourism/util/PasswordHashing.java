package com.tourism.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class to handle password hashing and checking using jBCrypt.
 *
 * IMPORTANT: You must add the jbcrypt-0.4.jar (or newer) to your project's
 * libraries/classpath for this to work.
 */
public class PasswordHashing {

    /**
     * Hashes a plain text password.
     *
     * @param plainTextPassword The password to hash.
     * @return A securely hashed password string.
     */
    public static String hashPassword(String plainTextPassword) {
        // gensalt() automatically handles the salt
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if a plain text password matches a stored hashed password.
     *
     * @param plainTextPassword The password to check.
     * @param hashedPassword    The stored hash from the database.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            // Handle edge case or invalid hash
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            // Handle errors (e.g., invalid hash format)
            System.err.println("Error checking password: " + e.getMessage());
            return false;
        }
    }
}
