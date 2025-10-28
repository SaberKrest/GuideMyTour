package com.tourism.gui;

import com.tourism.user.UserService;
import com.tourism.main.Main;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel for changing application settings, like theme.
 * MODIFIED:
 * - Applied Samarkan font to text components.
 * - Set combo box background to white.
 */
public class SettingsPanel extends JPanel {

    private MainFrame mainFrame;
    private UserService userService;
    private JComboBox<String> themeComboBox;
    private Image backgroundImage;

    public SettingsPanel(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        this.backgroundImage = Main.loadBackgroundImage();
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Settings");
        // --- MODIFICATION: Apply Samarkan font ---
        titleLabel.setFont(new Font("Baskerville Old Face",Font.BOLD, 48));
        // --- End of Modification ---
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel themeLabel = new JLabel("Application Theme:");
        themeLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
        formPanel.add(themeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        String[] themes = {"Light", "Dark"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem(userService.getTheme());
        themeComboBox.setFont(new Font("Arial", Font.PLAIN, 18)); // Keep Arial for combo box
        // --- MODIFICATION: Set background to white ---
        themeComboBox.setBackground(Color.WHITE);
        // --- End of Modification ---
        themeComboBox.setPreferredSize(new Dimension(200, 40));
        formPanel.add(themeComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Save & Apply");
        saveButton.setBackground(MainFrame.BUTTON_BG);
        saveButton.setForeground(MainFrame.BUTTON_FG);
        saveButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(180, 50));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton("Back");
        // --- MODIFICATION: Apply Samarkan font ---
        backButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        // --- End of Modification ---
        backButton.setPreferredSize(new Dimension(150, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));

        saveButton.addActionListener(e -> {
            String selectedTheme = (String) themeComboBox.getSelectedItem();
            userService.setTheme(selectedTheme);
            mainFrame.updateTheme();
            JOptionPane.showMessageDialog(this,
                    "Theme updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });
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

