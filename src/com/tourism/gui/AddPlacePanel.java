package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.gui.components.WrapLayout;
import com.tourism.main.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Panel with a form to add a new destination.
 * MODIFIED:
 * - Applied Samarkan font to text components.
 * - Set text field/area backgrounds to white.
 */
public class AddPlacePanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private Image backgroundImage;

    private JTextField nameField, locationField, priceField, popularityField;
    private JTextArea descriptionArea, touristSpotsArea, localSpotsArea, shopsArea;
    private JButton saveButton, backButton, addImageButton;
    private JPanel imageListPanel;
    private List<String> newImagePaths;

    public AddPlacePanel(MainFrame mainFrame, DatabaseManager dbManager) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.newImagePaths = new ArrayList<>();
        this.backgroundImage = Main.loadBackgroundImage();
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 100, 20, 100));

        JLabel titleLabel = new JLabel("Add New Destination");
        titleLabel.setFont(new Font("Baskerville Old Face", Font.BOLD, 48));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // --- MODIFICATION: Set background to white for all fields ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formPanel.add(createFormLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        nameField = new JTextField(20);
        nameField.setBackground(Color.WHITE);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        locationField = new JTextField(20);
        locationField.setBackground(Color.WHITE);
        formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Avg.Price (3 Days Trip):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        priceField = new JTextField(22);
        priceField.setBackground(Color.WHITE);
        formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Popularity (1-10):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        popularityField = new JTextField(20);
        popularityField.setBackground(Color.WHITE);
        formPanel.add(popularityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(createFormLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setBackground(Color.WHITE);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.getViewport().setBackground(Color.WHITE);
        formPanel.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weighty = 0.2;
        formPanel.add(createFormLabel("Tourist Spots:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        touristSpotsArea = new JTextArea(3, 20);
        touristSpotsArea.setBackground(Color.WHITE);
        JScrollPane touristScroll = new JScrollPane(touristSpotsArea);
        touristScroll.getViewport().setBackground(Color.WHITE);
        formPanel.add(touristScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(createFormLabel("Local Spots:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        localSpotsArea = new JTextArea(3, 20);
        localSpotsArea.setBackground(Color.WHITE);
        JScrollPane localScroll = new JScrollPane(localSpotsArea);
        localScroll.getViewport().setBackground(Color.WHITE);
        formPanel.add(localScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(createFormLabel("Shopping:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        shopsArea = new JTextArea(3, 20);
        shopsArea.setBackground(Color.WHITE);
        JScrollPane shopScroll = new JScrollPane(shopsArea);
        shopScroll.getViewport().setBackground(Color.WHITE);
        formPanel.add(shopScroll, gbc);
        // --- End of Modification ---

        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        formPanel.add(createFormLabel("Images:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        addImageButton = new JButton("Select Images...");
        addImageButton.setBackground(MainFrame.BUTTON_BG);
        addImageButton.setForeground(MainFrame.BUTTON_FG);
        // --- MODIFICATION: Apply Samarkan font ---
        addImageButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        addImageButton.addActionListener(e -> selectImages());
        formPanel.add(addImageButton, gbc);

        gbc.gridx = 1; gbc.gridy = 9; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.2;
        imageListPanel = new JPanel(new WrapLayout(WrapLayout.LEFT, 5, 5));
        imageListPanel.setOpaque(false);
        JScrollPane imageListScroll = new JScrollPane(imageListPanel);
        imageListScroll.setOpaque(false);
        imageListScroll.getViewport().setOpaque(false);
        formPanel.add(imageListScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        backButton = new JButton("Back to Dashboard");
        // --- MODIFICATION: Apply Samarkan font ---
        backButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        buttonPanel.add(backButton);

        saveButton = new JButton("Save Place");
        saveButton.setBackground(MainFrame.BUTTON_BG);
        saveButton.setForeground(MainFrame.BUTTON_FG);
        // --- MODIFICATION: Apply Samarkan font ---
        saveButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        saveButton.addActionListener(e -> savePlace());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void selectImages() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File file : chooser.getSelectedFiles()) {
                newImagePaths.add(file.getAbsolutePath());
                JLabel imgLabel = new JLabel(file.getName());
                // --- MODIFICATION: Apply Samarkan font ---
                imgLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 14));
                // --- End of Modification ---
                imgLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
                imageListPanel.add(imgLabel);
            }
            imageListPanel.revalidate();
            imageListPanel.repaint();
        }
    }

    private void savePlace() {
        String name = nameField.getText();
        String location = locationField.getText();
        String description = descriptionArea.getText();
        String price = priceField.getText();
        String popStr = popularityField.getText();
        String touristSpots = touristSpotsArea.getText();
        String localSpots = localSpotsArea.getText();
        String shops = shopsArea.getText();

        if (name.isEmpty() || location.isEmpty() || price.isEmpty() || popStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Location, Price, and Popularity are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double popularity;
        try {
            popularity = Double.parseDouble(popStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Popularity must be a valid number (e.g., 8.5).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<String> destinationImagePaths = new ArrayList<>();
            String imgDir = System.getProperty("user.dir") + File.separator + "images";
            new File(imgDir).mkdirs();

            for (String sourcePathStr : newImagePaths) {
                File sourceFile = new File(sourcePathStr);
                String newFileName = UUID.randomUUID().toString() + "_" + sourceFile.getName();
                Path destPath = Paths.get(imgDir, newFileName);

                Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                destinationImagePaths.add(destPath.toString());
            }

            Destination newDest = new Destination(
                    0, name, location, description,
                    destinationImagePaths, price, popularity, false,
                    touristSpots, localSpots, shops
            );

            dbManager.addDestination(newDest);

            JOptionPane.showMessageDialog(this, "Place added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            mainFrame.getDashboardPanel().loadDestinations("default");
            mainFrame.showPanel("dashboard");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving place: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        // --- MODIFICATION: Apply Samarkan font ---
        label.setFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        // --- End of Modification ---
        return label;
    }

    private void clearForm() {
        nameField.setText("");
        locationField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        popularityField.setText("");
        touristSpotsArea.setText("");
        localSpotsArea.setText("");
        shopsArea.setText("");
        newImagePaths.clear();
        imageListPanel.removeAll();
        imageListPanel.revalidate();
        imageListPanel.repaint();
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

