package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.user.UserService;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.tourism.main.Main;

import javax.swing.*;
import java.awt.*;
import java.net.URL; // --- ADD: Import URL for icons ---

/**
 * The main window (JFrame) of the application.
 * MODIFIED:
 * - Added helper method to get the custom Samarkan font.
 * - ADD: Added static icons for buttons.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DashboardPanel dashboardPanel;
    private AddPlacePanel addPlacePanel;
    private SettingsPanel settingsPanel;
    private DestinationDetailPanel detailPanel;
    private SavedPlacesPanel savedPlacesPanel;
    private SignInPanel signInPanel;
    private SignUpPanel signUpPanel;
    private WelcomePanel welcomePanel;

    private UserService userService;
    private DatabaseManager dbManager;

    public static final Color BUTTON_BG = new Color(126, 51, 39); // #7e3327
    public static final Color BUTTON_FG = new Color(210, 195, 169); // #d2c3a9
    public static final Color ORANGE_COLOR = new Color(245, 158, 11); // For Edit

    // --- ADD: Static icons ---
    public static ImageIcon backIcon;
    public static ImageIcon savedIcon;

    static {
        try {
            URL backUrl = Main.class.getResource("/com/tourism/resources/assets/back-arrow.png");
            if (backUrl != null) {
                Image backImg = new ImageIcon(backUrl).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                backIcon = new ImageIcon(backImg);
            } else {
                System.err.println("Could not load back-arrow.png icon");
            }
        } catch (Exception e) {
            System.err.println("Error loading back-arrow.png icon: " + e.getMessage());
        }
        try {
            URL savedUrl = Main.class.getResource("/com/tourism/resources/assets/tick-mark.png");
            if (savedUrl != null) {
                Image savedImg = new ImageIcon(savedUrl).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                savedIcon = new ImageIcon(savedImg);
            } else {
                System.err.println("Could not load tick-mark.png icon");
            }
        } catch (Exception e) {
            System.err.println("Error loading tick-mark.png icon: " + e.getMessage());
        }
    }
    // --- End of ADD ---


    public MainFrame(DatabaseManager dbManager, UserService userService) {
        this.userService = userService;
        this.dbManager = dbManager;

        updateTheme();
        setTitle("Tour My Guide");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        welcomePanel = new WelcomePanel(this);
        signInPanel = new SignInPanel(this, dbManager, userService);
        signUpPanel = new SignUpPanel(this, dbManager, userService);
        dashboardPanel = new DashboardPanel(this, dbManager, userService);
        addPlacePanel = new AddPlacePanel(this, dbManager);
        settingsPanel = new SettingsPanel(this, userService);
        detailPanel = new DestinationDetailPanel(this, dbManager, userService);
        savedPlacesPanel = new SavedPlacesPanel(this, dbManager, userService);

        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(signInPanel, "signIn");
        mainPanel.add(signUpPanel, "signUp");
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(addPlacePanel, "add");
        mainPanel.add(settingsPanel, "settings");
        mainPanel.add(detailPanel, "detail");
        mainPanel.add(savedPlacesPanel, "saved");

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    public void showPanel(String panelName) {
        if (panelName.equals("dashboard")) {
            dashboardPanel.loadDestinations("default");
        }
        if (panelName.equals("saved")) {
            savedPlacesPanel.loadSavedDestinations(userService.getUserId());
        }
        cardLayout.show(mainPanel, panelName);
    }

    public void showDetailPanel(Destination destination) {
        detailPanel.setDestination(destination);
        showPanel("detail");
    }

    public void performSignOut() {
        userService.logout();
        showPanel("welcome");
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }

    public SavedPlacesPanel getSavedPlacesPanel() {
        return savedPlacesPanel;
    }

    public void updateTheme() {
        try {
            if ("Dark".equals(userService.getTheme())) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to set LaF: " + ex.getMessage());
        }
    }

    /**
     * MODIFICATION: Helper to get the custom font.
     * @param style Font.PLAIN, Font.BOLD, etc.
     * @param size  The desired font size.
     * @return The custom Samarkan font, or a fallback Serif font.
     */
    public static Font getSamarkanFont(int style, float size) {
        if (Main.SAMARKAN_FONT != null) {
            // Derive the font with the requested style and size
            return Main.SAMARKAN_FONT.deriveFont(style, size);
        }
        // Fallback font if Samarkan failed to load
        return new Font("Serif", style, (int) size);
    }

    // Helper to apply title font by name (used for main titles)
    public static void applyTitleFontByName(JLabel label, float size) {
        label.setFont(new Font(Main.SAMARKAN_FONT_NAME, Font.BOLD, (int) size));
    }
}
