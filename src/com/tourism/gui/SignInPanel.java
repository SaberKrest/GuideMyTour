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
 * MODIFIED: Reverted to previous UI style (blue buttons, light gray bg).
 */
public class SignInPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    // --- MODIFICATION: Removed brand colors ---
    // private final Color BUTTON_BG = ...
    // private final Color BUTTON_FG = ...

    public SignInPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new GridBagLayout());

        // --- MODIFICATION: Reverted background color ---
        setBackground(new Color(241, 245, 249));
        // --- End of Modification ---

        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(Color.WHITE);
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginBox.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginBox.add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        loginBox.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        loginBox.add(usernameField, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> performLogin()); // Allow login on Enter
        loginBox.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        loginBox.add(errorLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- MODIFICATION: Apply original button style ---
        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton);
        loginButton.addActionListener(e -> performLogin());
        loginBox.add(loginButton, gbc);
        // --- End of Modification ---

        gbc.gridy++;
        JButton backButton = new JButton("Back to Welcome");
        // Back button uses default theme style
        backButton.addActionListener(e -> mainFrame.showPanel("welcome"));
        loginBox.add(backButton, gbc);

        add(loginBox);
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

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and Password cannot be empty.");
            return;
        }

        User user = dbManager.getUser(username);

        if (user != null && PasswordHashing.checkPassword(password, user.getPasswordHash())) {
            // SUCCESS
            System.out.println("Login successful for user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
            errorLabel.setText(" ");
            passwordField.setText("");

            userService.login(user);

            // Re-load theme in case user had a preference
            mainFrame.updateTheme();

            mainFrame.getDashboardPanel().loadDestinations("default");
            mainFrame.showPanel("dashboard");
        } else {
            // FAILURE
            System.out.println("Login failed for user: " + username);
            errorLabel.setText("Invalid username or password.");
        }
    }
}

