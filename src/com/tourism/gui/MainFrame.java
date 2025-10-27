package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.user.UserService;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * The main window (JFrame) of the application.
 * Holds all other panels using a CardLayout.
 * Manages user session state.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DashboardPanel dashboardPanel;
    private AddPlacePanel addPlacePanel;
    private SettingsPanel settingsPanel;
    private DestinationDetailPanel detailPanel;
    private SavedPlacesPanel savedPlacesPanel;
    private SignInPanel signInPanel; // Now needs to be class-level
    private SignUpPanel signUpPanel; // New panel

    private UserService userService;
    private DatabaseManager dbManager; // Store dbManager

    public MainFrame(DatabaseManager dbManager, UserService userService) {
        this.userService = userService;
        this.dbManager = dbManager; // Store for later use

        updateTheme();

        setTitle("Tourism Guide Application");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Initialize all panels ---
        // WelcomePanel no longer needs UserService
        WelcomePanel welcomePanel = new WelcomePanel(this);

        // Panels that need services
        signInPanel = new SignInPanel(this, dbManager, userService);
        signUpPanel = new SignUpPanel(this, dbManager, userService); // New
        dashboardPanel = new DashboardPanel(this, dbManager, userService);
        addPlacePanel = new AddPlacePanel(this, dbManager);
        settingsPanel = new SettingsPanel(this, userService);
        detailPanel = new DestinationDetailPanel(this, dbManager, userService);
        savedPlacesPanel = new SavedPlacesPanel(this, dbManager, userService);

        // --- Add panels to the CardLayout ---
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(signInPanel, "signIn");
        mainPanel.add(signUpPanel, "signUp"); // New
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(settingsPanel, "settings");
        mainPanel.add(detailPanel, "detail");
        mainPanel.add(savedPlacesPanel, "saved");
        mainPanel.add(addPlacePanel, "addPlace");

        add(mainPanel);

        // Show the initial panel
        cardLayout.show(mainPanel, "welcome");
    }

    /**
     * Shows a specific panel by its name.
     */
    public void showPanel(String panelName) {
        // When showing dashboard, reload destinations and configure UI for user
        if (panelName.equals("dashboard")) {
            dashboardPanel.configureForUser(); // Configure buttons
            dashboardPanel.loadDestinations("default"); // Reload destinations
        }
        // When showing saved places, reload them for the current user
        if (panelName.equals("saved")) {
            savedPlacesPanel.loadSavedDestinations(userService.getUserId());
        }
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * A specific method to show the detail panel for a destination.
     */
    public void showDetailPanel(Destination destination) {
        detailPanel.setDestination(destination); // This will also configure UI
        showPanel("detail");
    }

    /**
     * Handles user sign-out.
     */
    public void performSignOut() {
        userService.logout();
        showPanel("welcome"); // Return to welcome screen
    }

    /**
     * Gets the dashboard panel instance.
     */
    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

    /**
     * Gets the saved places panel instance.
     */
    public SavedPlacesPanel getSavedPlacesPanel() {
        return savedPlacesPanel;
    }

    /**
     * Applies the theme from the UserService.
     */
    public void updateTheme() {
        try {
            if ("Dark".equals(userService.getTheme())) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to set theme: " + ex.getMessage());
        }
    }
}
