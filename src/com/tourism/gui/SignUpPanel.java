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
 * MODIFIED: Reverted to previous UI style (blue buttons, light gray bg).
 */
public class SignUpPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField adminCodeField;

    // --- MODIFICATION: Removed brand colors ---
    // private final Color BUTTON_BG = ...
    // private final Color BUTTON_FG = ...

    public SignUpPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new GridBagLayout());

        // --- MODIFICATION: Reverted background color ---
        setBackground(new Color(241, 245, 249));
        // --- End of Modification ---

        JPanel signUpBox = new JPanel(new GridBagLayout());
        signUpBox.setBackground(Color.WHITE);
        signUpBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signUpBox.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        signUpBox.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        signUpBox.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        signUpBox.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridy++;
        signUpBox.add(new JLabel("Admin Code (optional):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        signUpBox.add(usernameField, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        signUpBox.add(passwordField, gbc);

        gbc.gridy++;
        confirmPasswordField = new JPasswordField(20);
        signUpBox.add(confirmPasswordField, gbc);

        gbc.gridy++;
        adminCodeField = new JTextField(20);
        signUpBox.add(adminCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- MODIFICATION: Apply original button style ---
        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton);
        signUpButton.addActionListener(e -> performSignUp());
        signUpBox.add(signUpButton, gbc);
        // --- End of Modification ---

        gbc.gridy++;
        JButton backButton = new JButton("Back to Welcome");
        // Back button uses default theme style
        backButton.addActionListener(e -> mainFrame.showPanel("welcome"));
        signUpBox.add(backButton, gbc);

        add(signUpBox);
    }

    /**
     * MODIFICATION: Reverted to original blue button style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(59, 130, 246)); // Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        // button.setBorderPainted(false); // Removed
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 50));
    }

    private void performSignUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String adminCode = adminCodeField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (except Admin Code) are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.getUser(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already taken.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = "user";
        if (adminCode.equals("ADMIN1S23")) { // Using the admin code from your previous file
            role = "admin";
        }

        String hashedPassword = PasswordHashing.hashPassword(password);

        try {
            dbManager.createUser(username, hashedPassword, role);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully! Please sign in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            adminCodeField.setText("");

            mainFrame.showPanel("signIn");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

