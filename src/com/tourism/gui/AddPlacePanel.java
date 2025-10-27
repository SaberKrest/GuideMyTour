package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel with a form to add a new destination to the database.
 * Now supports multi-image upload and string price range.
 */
public class AddPlacePanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;

    // Form components
    private JTextField nameField;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JTextField priceField; // Stays JTextField, but will store a string
    private JTextField popularityField;
    private JButton saveButton;
    private JButton backButton;

    // --- New Image Components ---
    private JPanel imageListPanel;
    private JButton addImageButton;
    private List<String> newImagePaths;

    public AddPlacePanel(MainFrame mainFrame, DatabaseManager dbManager) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.newImagePaths = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 100, 20, 100));
        setBackground(UIManager.getColor("Panel.background"));

        JLabel titleLabel = new JLabel("Add a New Place");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.1;
        formPanel.add(createFormLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.9;
        nameField = new JTextField(30);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(nameField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createFormLabel("Location:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        locationField = new JTextField(30);
        locationField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(locationField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        // --- MODIFICATION: Label text changed ---
        formPanel.add(createFormLabel("Price Range (₹):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        priceField = new JTextField(15);
        priceField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(priceField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(createFormLabel("Popularity (1-10):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        popularityField = new JTextField(15);
        popularityField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(popularityField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(createFormLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.4;
        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // --- New Image Panel ---
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(createFormLabel("Images:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.6;

        JPanel imageUploadPanel = new JPanel(new BorderLayout(5, 5));
        imageUploadPanel.setOpaque(false);

        addImageButton = new JButton("Add Images...");
        addImageButton.setFont(new Font("Arial", Font.BOLD, 14));
        imageUploadPanel.add(addImageButton, BorderLayout.NORTH);

        imageListPanel = new JPanel();
        imageListPanel.setLayout(new BoxLayout(imageListPanel, BoxLayout.Y_AXIS));
        imageListPanel.setBackground(Color.WHITE);
        JScrollPane imgScrollPane = new JScrollPane(imageListPanel);
        imgScrollPane.setPreferredSize(new Dimension(0, 100));
        imageUploadPanel.add(imgScrollPane, BorderLayout.CENTER);

        formPanel.add(imageUploadPanel, gbc);
        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        saveButton = new JButton("Save Place");
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(150, 50));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setPreferredSize(new Dimension(150, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        addImageButton.addActionListener(e -> pickImage());
        saveButton.addActionListener(e -> savePlace());
    }

    /**
     * Opens a JFileChooser to select one or more images.
     */
    private void pickImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select one or more images");
        // --- MODIFICATION: Allow multi-selection ---
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // --- MODIFICATION: Get all selected files ---
            File[] selectedFiles = fileChooser.getSelectedFiles();

            try {
                File storageDir = new File(System.getProperty("user.dir") + File.separator + "app_images");
                if (!storageDir.exists()) {
                    storageDir.mkdir();
                }

                // --- MODIFICATION: Loop through all files ---
                for (File selectedFile : selectedFiles) {
                    String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    String newPath = storageDir.getAbsolutePath() + File.separator + newFileName;

                    Files.copy(selectedFile.toPath(), new File(newPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                    newImagePaths.add(newPath);
                    JLabel imageLabel = new JLabel(newFileName);
                    imageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    imageListPanel.add(imageLabel);
                }

                imageListPanel.revalidate();
                imageListPanel.repaint();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving images: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gathers form data and saves the new place.
     */
    private void savePlace() {
        try {
            String name = nameField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();
            // --- MODIFICATION: Get price as a string ---
            String price = priceField.getText();
            double popularity = Double.parseDouble(popularityField.getText());

            // --- MODIFICATION: Update validation ---
            if (name.isEmpty() || location.isEmpty() || price.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Location, and Price are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newImagePaths.isEmpty()) {
                JOptionPane.showMessageDialog(this, "At least one image is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Destination newDest = new Destination(0,
                    name,
                    location,
                    description,
                    newImagePaths,
                    price, // Pass the price string
                    popularity,
                    false
            );

            dbManager.addDestination(newDest);

            JOptionPane.showMessageDialog(this, "Place added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            mainFrame.showPanel("dashboard");

        } catch (NumberFormatException ex) {
            // --- MODIFICATION: Price is no longer checked here ---
            JOptionPane.showMessageDialog(this, "Popularity must be a valid number (e.g., 8.5).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving place: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private void clearForm() {
        nameField.setText("");
        locationField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        popularityField.setText("");
        newImagePaths.clear();
        imageListPanel.removeAll();
        imageListPanel.revalidate();
        imageListPanel.repaint();
    }
}

