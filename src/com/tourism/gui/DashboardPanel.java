package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.gui.components.CardFactory;
import com.tourism.gui.components.WrapLayout;
import com.tourism.model.Destination;
import com.tourism.user.UserService;
import com.tourism.main.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * The main dashboard panel.
 * MODIFIED:
 * - Applied Samarkan font to text components.
 * - Set search field background to white.
 */
public class DashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;
    private Image backgroundImage;

    private JLabel userGreetingLabel;
    private JTextField searchField;
    private JPanel cardsPanel;
    private JScrollPane scrollPane;
    private JButton addPlaceButton;
    private JButton savedPlacesButton;
    private JButton settingsButton;
    private JButton signOutButton;
    private JButton popularButton;
    private JButton priceButton;

    public DashboardPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        this.backgroundImage = Main.loadBackgroundImage();
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setOpaque(false);
        headerPanel.add(createSearchPanel(), BorderLayout.CENTER);
        headerPanel.add(createSortPanel(), BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new WrapLayout(WrapLayout.LEFT, 20, 20));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(createSidebar(), BorderLayout.WEST);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("Component.borderColor")));
        sidebar.setPreferredSize(new Dimension(300, 0));

        userGreetingLabel = new JLabel("Welcome, Guest!");
        userGreetingLabel.setFont(new Font("Baskerville Old Face", Font.PLAIN, 28));
        userGreetingLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        userGreetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userGreetingLabel);
        sidebar.add(Box.createVerticalStrut(20));

        addPlaceButton = createSidebarButton("Add New Place");
        addPlaceButton.addActionListener(e -> mainFrame.showPanel("add"));
        sidebar.add(addPlaceButton);

        savedPlacesButton = createSidebarButton("My Saved Places");
        savedPlacesButton.addActionListener(e -> mainFrame.showPanel("saved"));
        sidebar.add(savedPlacesButton);

        settingsButton = createSidebarButton("Settings");
        settingsButton.addActionListener(e -> mainFrame.showPanel("settings"));
        sidebar.add(settingsButton);

        sidebar.add(Box.createVerticalGlue());

        signOutButton = new JButton("Sign In / Sign Up");
        signOutButton.setFont(new Font("Baskerville Old Face", Font.BOLD, 24));
        signOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signOutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        signOutButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        signOutButton.setOpaque(false);
        signOutButton.setContentAreaFilled(false);
        signOutButton.addActionListener(e -> mainFrame.performSignOut());

        signOutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                signOutButton.setContentAreaFilled(true);
                signOutButton.setBackground(UIManager.getColor("Button.hoverBackground"));
            }
            public void mouseExited(MouseEvent evt) {
                signOutButton.setContentAreaFilled(false);
                signOutButton.setBackground(UIManager.getColor("Button.background"));
            }
        });

        JPanel signOutWrapper = new JPanel(new BorderLayout());
        signOutWrapper.setOpaque(false);
        signOutWrapper.setBorder(new EmptyBorder(10, 20, 20, 20));
        signOutWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        signOutWrapper.add(signOutButton, BorderLayout.CENTER);
        sidebar.add(signOutWrapper);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Baskerville Old Face",Font.BOLD, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(UIManager.getColor("Button.hoverBackground"));
            }
            public void mouseExited(MouseEvent evt) {
                button.setContentAreaFilled(false);
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
        return button;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField("Search destinations...");
        searchField.setFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        searchField.setForeground(UIManager.getColor("Text.disabledText"));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search destinations...")) {
                    searchField.setText("");
                    searchField.setForeground(UIManager.getColor("Text.foreground"));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search destinations...");
                    searchField.setForeground(UIManager.getColor("Text.disabledText"));
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchDestinations(); }
            public void removeUpdate(DocumentEvent e) { searchDestinations(); }
            public void changedUpdate(DocumentEvent e) { searchDestinations(); }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    private JPanel createSortPanel() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        sortPanel.setOpaque(false);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 24));
        sortPanel.add(sortLabel);

        popularButton = new JButton("Popularity");
        priceButton = new JButton("Price");

        styleSortButton(popularButton);
        styleSortButton(priceButton);

        popularButton.addActionListener(e -> loadDestinations("popularity"));
        sortPanel.add(popularButton);

        priceButton.addActionListener(e -> loadDestinations("price"));
        sortPanel.add(priceButton);

        return sortPanel;
    }

    private void styleSortButton(JButton button) {
        button.setBackground(MainFrame.BUTTON_BG);
        button.setForeground(MainFrame.BUTTON_FG);
        button.setFont(new Font("Baskerville Old Face",Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void loadDestinations(String sortBy) {
        boolean loggedIn = userService.isLoggedIn();
        userGreetingLabel.setText("Welcome, " + userService.getUsername());

        addPlaceButton.setVisible(userService.isAdmin());
        savedPlacesButton.setVisible(loggedIn && !userService.isAdmin());

        settingsButton.setVisible(loggedIn);
        signOutButton.setVisible(true);

        if (loggedIn) {
            signOutButton.setText("Sign Out");
            signOutButton.setForeground(new Color(217, 83, 79));
        } else {
            signOutButton.setText("Sign In / Sign Up");
            signOutButton.setForeground(new Color(59, 130, 246));
        }

        cardsPanel.removeAll();
        try {
            List<Destination> destinations = dbManager.getAllDestinations(userService.getUserId(), sortBy);
            if (destinations.isEmpty()) {
                JLabel noPlacesLabel = new JLabel("No destinations found. Admin can add new places.");
                noPlacesLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
                cardsPanel.add(noPlacesLabel);
            } else {
                for (Destination dest : destinations) {
                    JPanel card = CardFactory.createDestinationCard(dest, mainFrame);
                    cardsPanel.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading destinations: " + e.getMessage());
            errorLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
            cardsPanel.add(errorLabel);
        }
        refreshCardsPanel();
    }

    private void searchDestinations() {
        String query = searchField.getText();
        if (query.equals("Search destinations...")) {
            query = "";
        }

        if (query.trim().isEmpty()) {
            loadDestinations("default");
            return;
        }

        cardsPanel.removeAll();
        try {
            List<Destination> destinations = dbManager.searchDestinations(query, userService.getUserId());
            if (destinations.isEmpty()) {
                JLabel noMatchLabel = new JLabel("No destinations found matching '" + query + "'.");
                noMatchLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
                cardsPanel.add(noMatchLabel);
            } else {
                for (Destination dest : destinations) {
                    JPanel card = CardFactory.createDestinationCard(dest, mainFrame);
                    cardsPanel.add(card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error searching destinations: " + e.getMessage());
            errorLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
            cardsPanel.add(errorLabel);
        }
        refreshCardsPanel();
    }

    private void refreshCardsPanel() {
        cardsPanel.revalidate();
        cardsPanel.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            if (imgWidth <= 0 || imgHeight <= 0) return;
            for (int y = 0; y < getHeight(); y += imgHeight) {
                for (int x = 0; x < getWidth(); x += imgWidth) {
                    g.drawImage(backgroundImage, x, y, this);
                }
            }
        } else {
            g.setColor(UIManager.getColor("Panel.background"));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

