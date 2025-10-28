package com.tourism.gui;

import com.tourism.main.Main; // Import Main

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The initial welcome screen of the application.
 * MODIFIED:
 * - Fixed font loading.
 * - Loads correct background image from Main.java
 */
public class WelcomePanel extends JPanel {

    private MainFrame mainFrame;
    private Image backgroundImage;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // --- MODIFICATION: Load background image from Main ---
        try {
            this.backgroundImage = Main.loadBackgroundImage2(); // Loads welcome-bg.png
            if (this.backgroundImage == null) {
                System.err.println("Could not find background image (welcome-bg.png). Using fallback color.");
                setBackground(new Color(0, 20, 40)); // Dark blue fallback
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            setBackground(new Color(0, 20, 40));
        }
        // --- End of Modification ---


        // --- Main Content Panel (using GridBagLayout) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Let the image show through

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Spacer
        gbc.insets = new Insets(100, 10, 10, 10);

        // Sign In Button
        JButton signInButton = new JButton("Sign In");
        styleButton(signInButton);
        signInButton.addActionListener(e -> mainFrame.showPanel("signIn"));
        contentPanel.add(signInButton, gbc);

        // Sign Up Button
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton);
        signUpButton.addActionListener(e -> mainFrame.showPanel("signUp"));
        contentPanel.add(signUpButton, gbc);

        // --- MODIFICATION: Guest Button ---
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton guestButton = new JButton("Continue as Guest");
        styleButton(guestButton);
        guestButton.addActionListener(e -> {
            mainFrame.getDashboardPanel().loadDestinations("default");
            mainFrame.showPanel("dashboard");
        });
        contentPanel.add(guestButton, gbc);
        // --- End of Modification ---

        add(contentPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setBackground(MainFrame.BUTTON_BG); // Brand color
        button.setForeground(MainFrame.BUTTON_FG); // Brand color
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 60));
    }

    /**
     * Overrides paintComponent to draw the background image.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Scale image to fill panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

