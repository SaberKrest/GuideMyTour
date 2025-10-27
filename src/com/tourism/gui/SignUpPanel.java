package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.user.UserService;
import com.tourism.util.PasswordHashing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * A sign-up panel for new user registration.
 */
public class SignUpPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField adminCodeField; // For admin registration

    public SignUpPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new GridBagLayout()); // Center the content
        setBackground(new Color(241, 245, 249)); // Light gray background

        JPanel signUpBox = new JPanel(new GridBagLayout());
        signUpBox.setBackground(Color.WHITE);
        signUpBox.setBorder(BorderFactory.createCompoundBorder(
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
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2; // Span 2 columns
        gbc.insets = new Insets(0, 0, 30, 0);
        signUpBox.add(titleLabel, gbc);

        // Reset gbc
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 0, 0);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        signUpBox.add(userLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 2;
        signUpBox.add(usernameField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        signUpBox.add(passLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 10, 0);
        signUpBox.add(passwordField, gbc);

        // Confirm Password Label
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        signUpBox.add(confirmPassLabel, gbc);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 10, 0);
        signUpBox.add(confirmPasswordField, gbc);

        // Admin Code Label
        JLabel adminCodeLabel = new JLabel("Admin Secret Code (optional):");
        adminCodeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        signUpBox.add(adminCodeLabel, gbc);

        // Admin Code Field
        adminCodeField = new JTextField(20);
        adminCodeField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 10, 0);
        signUpBox.add(adminCodeField, gbc);


        // Sign Up Button
        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton);
        gbc.gridy = 9;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.insets = new Insets(20, 0, 10, 0);
        signUpButton.addActionListener(e -> performSignUp());
        signUpBox.add(signUpButton, gbc);

        // Back Button
        JButton backButton = new JButton("Back to Welcome");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(new Color(59, 130, 246));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        backButton.addActionListener(e -> mainFrame.showPanel("welcome"));
        signUpBox.add(backButton, gbc);

        add(signUpBox); // Add the sign-up box to the panel
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(59, 130, 246));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 50));
    }

    private void performSignUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String adminCode = adminCodeField.getText();

        // 1. Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Check if user exists
        if (dbManager.getUser(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already taken.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Determine role
        String role = "user";
        if (adminCode.equals("ADMIN123")) {
            role = "admin";
        }

        // 4. Hash password
        String hashedPassword = PasswordHashing.hashPassword(password);

        // 5. Create user
        try {
            dbManager.createUser(username, hashedPassword, role);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully! Please sign in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // Clear fields
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            adminCodeField.setText("");
            // Go to sign in panel
            mainFrame.showPanel("signIn");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
