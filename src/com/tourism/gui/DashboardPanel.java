package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.gui.components.CardFactory;
import com.tourism.gui.components.WrapLayout;
import com.tourism.model.Destination;
import com.tourism.user.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

/**
 * The main dashboard panel.
 * Shows destinations, search, and navigation.
 * UI is now role-based.
 */
public class DashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    // UI Components
    private JLabel userGreetingLabel;
    private JTextField searchField;
    private JPanel cardsPanel;
    private JScrollPane scrollPane;

    // Sidebar buttons
    private JButton addPlaceButton;
    private JButton savedPlacesButton;
    private JButton settingsButton;
    private JButton signOutButton; // New

    // Sort buttons
    private JButton popularButton;
    private JButton priceButton;

    public DashboardPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UIManager.getColor("Panel.background"));

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        cardsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setOpaque(false);

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the left-hand navigation sidebar.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIManager.getColor("control"));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel titleLabel = new JLabel("Tourism Guide");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Navigation Buttons
        addPlaceButton = createSidebarButton("Add New Place");
        savedPlacesButton = createSidebarButton("Saved Places");
        settingsButton = createSidebarButton("Settings");
        signOutButton = createSidebarButton("Sign Out"); // New

        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(addPlaceButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(savedPlacesButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(settingsButton);

        sidebar.add(Box.createVerticalGlue()); // Pushes sign out down
        sidebar.add(signOutButton); // Add to bottom

        // --- Sidebar Action Listeners ---
        addPlaceButton.addActionListener(e -> mainFrame.showPanel("addPlace"));
        savedPlacesButton.addActionListener(e -> mainFrame.showPanel("saved"));
        settingsButton.addActionListener(e -> mainFrame.showPanel("settings"));
        signOutButton.addActionListener(e -> mainFrame.performSignOut()); // New

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        userGreetingLabel = new JLabel("Welcome, Guest!");
        userGreetingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(userGreetingLabel, BorderLayout.WEST);

        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        eastPanel.setOpaque(false);

        popularButton = new JButton("Popular");
        popularButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        popularButton.addActionListener(e -> loadDestinations("popular"));
        eastPanel.add(popularButton);

        priceButton = new JButton("Price");
        priceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        priceButton.addActionListener(e -> loadDestinations("price"));
        eastPanel.add(priceButton);

        searchField = new JTextField("Search destinations...", 25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search destinations...")) {
                    searchField.setText("");
                    searchField.setForeground(UIManager.getColor("TextField.foreground"));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search destinations...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addActionListener(e -> searchDestinations());

        JButton searchButton = new JButton("Search");
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setPreferredSize(new Dimension(80, 35));
        searchButton.addActionListener(e -> searchDestinations());

        eastPanel.add(searchField);
        eastPanel.add(searchButton);
        headerPanel.add(eastPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Configures the UI based on the logged-in user's role.
     * This is called by MainFrame *before* showing the panel.
     */
    public void configureForUser() {
        if (userService.isLoggedIn()) {
            userGreetingLabel.setText("Welcome, " + userService.getUsername() + "!");
            String role = userService.getRole();
            // Show/hide admin-only buttons
            addPlaceButton.setVisible("admin".equals(role));
        } else {
            // This case shouldn't happen, but as a fallback
            userGreetingLabel.setText("Welcome, Guest!");
            addPlaceButton.setVisible(false);
        }
    }

    /**
     * Loads destinations, now checking against the logged-in user's ID.
     */
    public void loadDestinations(String sortBy) {
        cardsPanel.removeAll();
        int userId = userService.getUserId(); // Get current user

        try {
            List<Destination> destinations = dbManager.getAllDestinationsSorted(sortBy, userId);
            if (destinations.isEmpty()) {
                cardsPanel.add(new JLabel("No destinations found. Try adding a new place!"));
            } else {
                for (Destination dest : destinations) {
                    JPanel card = CardFactory.createDestinationCard(dest, mainFrame);
                    cardsPanel.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            cardsPanel.add(new JLabel("Error loading destinations: " + e.getMessage()));
        }
        refreshCardsPanel();
    }

    /**
     * Searches destinations, checking against the logged-in user's ID.
     */
    private void searchDestinations() {
        String query = searchField.getText();
        int userId = userService.getUserId();

        if (query.equals("Search destinations...") || query.trim().isEmpty()) {
            loadDestinations("default");
            return;
        }

        cardsPanel.removeAll();
        try {
            List<Destination> destinations = dbManager.searchDestinations(query, userId);
            if (destinations.isEmpty()) {
                cardsPanel.add(new JLabel("No destinations found matching '" + query + "'."));
            } else {
                for (Destination dest : destinations) {
                    JPanel card = CardFactory.createDestinationCard(dest, mainFrame);
                    cardsPanel.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            cardsPanel.add(new JLabel("Error searching destinations: " + e.getMessage()));
        }
        refreshCardsPanel();
    }

    private void refreshCardsPanel() {
        cardsPanel.revalidate();
        cardsPanel.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
}
