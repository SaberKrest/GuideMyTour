package com.tourism.gui;

import com.tourism.user.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * The initial welcome screen of the application.
 * Now shows Sign In and Sign Up buttons.
 */
public class WelcomePanel extends JPanel {

    private MainFrame mainFrame;
    // No longer needs UserService, as no user is logged in here
    private Image backgroundImage;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // --- Load background image ---
        try {
            URL imgUrl = getClass().getResource("/com/tourism/assets/welcome-bg.jpeg");
            if (imgUrl != null) {
                this.backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Could not find background image. Using fallback color.");
                setBackground(new Color(0, 20, 40)); // Dark blue fallback
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            setBackground(new Color(0, 20, 40)); // Dark blue fallback
        }

        // --- Main Content Panel (using GridBagLayout) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(100, 100, 100, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component on its own line
        gbc.anchor = GridBagConstraints.CENTER; // Center components
        gbc.fill = GridBagConstraints.NONE; // Don't stretch components

        // Title Label
        JLabel titleLabel = new JLabel("Explore The World");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, gbc);

        // Subtitle Label
        JLabel subtitleLabel = new JLabel("Please sign in or create an account to begin");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(subtitleLabel, gbc);

        // Spacer
        gbc.insets = new Insets(30, 10, 10, 10);

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

        add(contentPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(Color.BLACK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 60));
    }

    /**
     * Overrides paintComponent to draw the background image.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImage != null) {
            g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
