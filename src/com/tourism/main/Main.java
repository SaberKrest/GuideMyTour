package com.tourism.main;

import com.tourism.database.DatabaseManager;
import com.tourism.gui.MainFrame;
import com.tourism.user.UserService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL; // Import URL

/**
 * Main class to run the Tourism Guide application.
 * MODIFIED:
 * - Registers custom font from correct asset path.
 * - Stores font name for global use.
 * - Loads correct background image file (.jpg).
 */
public class Main {

    // --- MODIFICATION: Store font name globally ---
    public static String SAMARKAN_FONT_NAME = "Arial"; // Default fallback
    public static Font SAMARKAN_FONT;
    // --- End of Modification ---

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // --- MODIFICATION: Register custom font ---\
        registerCustomFont();
        // --- End of Modification ---

        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = new DatabaseManager();
            UserService userService = new UserService();
            MainFrame frame = new MainFrame(dbManager, userService);
            frame.setVisible(true);
        });
    }

    /**
     * MODIFIED: Registers the custom font from the /resources/assets/ folder.
     */
    private static void registerCustomFont() {
        try {
            // --- MODIFICATION: Updated font path ---
            String fontPath = "/com/tourism/resources/fonts/SAMAN___.TTF";
            // --- End of Modification ---

            InputStream is = Main.class.getResourceAsStream(fontPath);
            if (is == null) {
                System.err.println("Could not find font at: " + fontPath);
                return;
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // --- MODIFICATION: Store font for use ---
            SAMARKAN_FONT = customFont;
            SAMARKAN_FONT_NAME = customFont.getFontName();
            System.out.println("Registered font: " + SAMARKAN_FONT_NAME);
            // --- End of Modification ---

        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * MODIFIED: Helper to load the background image.
     * Fixed .png to .jpg
     */
    public static Image loadBackgroundImage() {
        try {
            // --- MODIFICATION: Updated to .jpg ---
            URL imgUrl = Main.class.getResource("/com/tourism/resources/assets/wallpaper-bg.png");
            // --- End of Modification ---

            if (imgUrl != null) {
                return new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Could not find background image: wallpaper-bg.png");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            return null;
        }
    }

    public static Image loadBackgroundImage2() {
        try {
            // This image is for the WelcomePanel
            URL imgUrl = Main.class.getResource("/com/tourism/resources/assets/welcome-bg.png");
            if (imgUrl != null) {
                return new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Could not find background image: welcome-bg.png");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            return null;
        }
    }
}

