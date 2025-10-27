package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.User;
import com.tourism.user.UserService;
import com.tourism.util.PasswordHashing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * A sign-in panel for user authentication.
 * NOW HANDLES REAL LOGIN LOGIC.
 */
public class SignInPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel; // To show login errors

    public SignInPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new GridBagLayout()); // Center the content
        setBackground(new Color(241, 245, 249)); // Light gray background

        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(Color.WHITE);
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.ipady = 15; // Make components taller
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2; // Span 2 columns
        gbc.insets = new Insets(0, 0, 30, 0);
        loginBox.add(titleLabel, gbc);

        // Reset gbc
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 0, 0);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        loginBox.add(userLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 2;
        loginBox.add(usernameField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginBox.add(passLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 10, 0);
        loginBox.add(passwordField, gbc);

        // Error Label
        errorLabel = new JLabel(" "); // Placeholder for alignment
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setForeground(Color.RED);
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginBox.add(errorLabel, gbc);

        // Sign In Button
        JButton signInButton = new JButton("Sign In");
        styleButton(signInButton);
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.insets = new Insets(20, 0, 10, 0);
        signInButton.addActionListener(e -> performLogin());
        loginBox.add(signInButton, gbc);

        // Back Button
        JButton backButton = new JButton("Back to Welcome");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(new Color(59, 130, 246));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        backButton.addActionListener(e -> mainFrame.showPanel("welcome"));
        loginBox.add(backButton, gbc);

        add(loginBox); // Add the login box to the panel
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(59, 130, 246));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 50));
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and Password cannot be empty.");
            return;
        }

        // --- Real JDBC Validation ---
        User user = dbManager.getUser(username);

        if (user != null && PasswordHashing.checkPassword(password, user.getPasswordHash())) {
            // SUCCESS
            System.out.println("Login successful for user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
            errorLabel.setText(" "); // Clear error
            passwordField.setText(""); // Clear password field

            // 1. Log user into the service
            userService.login(user);

            // 2. Load dashboard data (will be configured for the user)
            mainFrame.getDashboardPanel().loadDestinations("default");

            // 3. Navigate to dashboard
            mainFrame.showPanel("dashboard");
        } else {
            // FAILURE
            System.out.println("Login failed for user: " + username);
            errorLabel.setText("Invalid username or password.");
        }
    }
}
