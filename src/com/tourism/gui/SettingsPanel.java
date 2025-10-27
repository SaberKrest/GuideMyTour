package com.tourism.gui;

import com.tourism.user.UserService;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel for changing application settings, like theme.
 */
public class SettingsPanel extends JPanel {

    private MainFrame mainFrame;
    private UserService userService;
    private JComboBox<String> themeComboBox;

    public SettingsPanel(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(40, 40, 40, 40));
        setBackground(UIManager.getColor("Panel.background"));

        // --- Title ---
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Settings Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Theme Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel themeLabel = new JLabel("Application Theme:");
        themeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        formPanel.add(themeLabel, gbc);

        // Theme ComboBox
        gbc.gridx = 1;
        String[] themes = {"Light", "Dark"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        themeComboBox.setSelectedItem(userService.getTheme()); // Set to saved theme
        formPanel.add(themeComboBox, gbc);

        // Add glue to push everything to the top
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        formPanel.add(new JPanel(), gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel (South) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Save & Apply");
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(180, 50));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setPreferredSize(new Dimension(150, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));

        saveButton.addActionListener(e -> {
            // 1. Get selected theme
            String selectedTheme = (String) themeComboBox.getSelectedItem();

            // 2. Save it to the user service
            userService.setTheme(selectedTheme);

            // 3. Tell MainFrame to apply the theme
            mainFrame.updateTheme();

            // 4. Show success
            JOptionPane.showMessageDialog(this,
                    "Theme updated successfully!",
                    "Settings Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
