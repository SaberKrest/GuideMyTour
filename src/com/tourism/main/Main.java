package com.tourism.main;

import com.tourism.database.DatabaseManager;
import com.tourism.gui.MainFrame;
import com.tourism.user.UserService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main class to run the Tourism Guide application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize the Look and Feel (FlatLaf Light)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Run the GUI setup on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Initialize the single instances of the services

            // 1. Database Manager (Manages tourism.db)
            // This will also create the file and tables if they don't exist
            DatabaseManager dbManager = new DatabaseManager();

            // 2. User Service (Manages username and theme)
            UserService userService = new UserService();

            // 3. Create the Main Window and pass the services to it
            MainFrame frame = new MainFrame(dbManager, userService);

            // 4. Make the window visible
            frame.setVisible(true);
        });
    }
}

