package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.model.Review;
import com.tourism.user.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel to show full details of a destination.
 * Now supports multi-image upload and string price display.
 */
public class DestinationDetailPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;
    private Destination currentDestination;

    // UI Components
    private JLabel nameLabel;
    private JLabel locationLabel;
    private JTextArea descriptionArea;
    private JLabel priceLabel;
    private JLabel popularityLabel;
    private JButton saveButton;
    private JButton backButton;
    private JPanel imagesPanel;
    private JLabel mainImageLabel;

    // --- New Feature Components ---
    private JButton deleteButton;
    private JPanel addReviewPanel;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewCommentArea;
    private JButton submitReviewButton;
    private JButton addImageButton;
    private JPanel reviewsDisplayPanel;
    private JScrollPane reviewsScrollPane;

    private int currentImageIndex = 0;

    public DestinationDetailPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        setBackground(UIManager.getColor("Panel.background"));

        // --- Top Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        topPanel.add(backButton, BorderLayout.WEST);

        nameLabel = new JLabel("Destination Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 36));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Image Panel (Left Side) ---
        JPanel imageContainer = new JPanel(new BorderLayout(10, 10));
        imageContainer.setOpaque(false);
        mainImageLabel = new JLabel();
        mainImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        mainImageLabel.setPreferredSize(new Dimension(500, 400));
        mainImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageContainer.add(mainImageLabel, BorderLayout.CENTER);

        imagesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        imagesPanel.setOpaque(false);
        JScrollPane thumbnailScrollPane = new JScrollPane(imagesPanel);
        thumbnailScrollPane.setPreferredSize(new Dimension(500, 100));
        thumbnailScrollPane.setBorder(null);
        thumbnailScrollPane.setOpaque(false);
        thumbnailScrollPane.getViewport().setOpaque(false);
        imageContainer.add(thumbnailScrollPane, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        centerPanel.add(imageContainer, gbc);

        // --- Details Panel (Right Side) ---
        JPanel detailsContainer = new JPanel();
        detailsContainer.setLayout(new BoxLayout(detailsContainer, BoxLayout.Y_AXIS));
        detailsContainer.setOpaque(false);
        detailsContainer.setBorder(new EmptyBorder(0, 20, 0, 0));

        locationLabel = new JLabel("Location: City, Country");
        locationLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        detailsContainer.add(locationLabel);

        priceLabel = new JLabel("Price: ₹0.00");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        detailsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsContainer.add(priceLabel);

        popularityLabel = new JLabel("Popularity: 0.0");
        popularityLabel.setFont(new Font("Arial", Font.BOLD, 20));
        detailsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsContainer.add(popularityLabel);

        JLabel descTitle = new JLabel("About this place:");
        descTitle.setFont(new Font("Arial", Font.BOLD, 18));
        descTitle.setBorder(new EmptyBorder(15, 0, 5, 0));
        detailsContainer.add(descTitle);

        descriptionArea = new JTextArea("Full description...");
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setFocusable(false);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(null);
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.getViewport().setOpaque(false);
        detailsContainer.add(descriptionScrollPane);
        detailsContainer.add(Box.createVerticalGlue());

        // --- Role-Based Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveButton = new JButton("Save Place");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(saveButton);

        deleteButton = new JButton("Delete Place");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setBackground(Color.RED.darker());
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);

        addImageButton = new JButton("Add More Images...");
        addImageButton.setFont(new Font("Arial", Font.BOLD, 16));
        addImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(addImageButton);

        detailsContainer.add(buttonPanel);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        centerPanel.add(detailsContainer, gbc);

        // --- Reviews Panel (South) ---
        JPanel southPanel = new JPanel(new BorderLayout(20, 20));
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        reviewsDisplayPanel = new JPanel();
        reviewsDisplayPanel.setLayout(new BoxLayout(reviewsDisplayPanel, BoxLayout.Y_AXIS));
        reviewsDisplayPanel.setOpaque(false);
        reviewsScrollPane = new JScrollPane(reviewsDisplayPanel);
        reviewsScrollPane.setPreferredSize(new Dimension(0, 200));
        reviewsScrollPane.setBorder(new TitledBorder("Reviews"));
        southPanel.add(reviewsScrollPane, BorderLayout.CENTER);

        addReviewPanel = new JPanel(new BorderLayout(5, 5));
        addReviewPanel.setOpaque(false);
        addReviewPanel.setBorder(new TitledBorder("Leave a Review"));
        JPanel reviewControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reviewControls.setOpaque(false);
        reviewControls.add(new JLabel("Rating:"));
        Integer[] ratings = {1, 2, 3, 4, 5};
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setSelectedItem(5);
        reviewControls.add(ratingComboBox);
        submitReviewButton = new JButton("Submit");
        reviewControls.add(submitReviewButton);
        addReviewPanel.add(reviewControls, BorderLayout.NORTH);
        reviewCommentArea = new JTextArea(3, 0);
        reviewCommentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);
        addReviewPanel.add(new JScrollPane(reviewCommentArea), BorderLayout.CENTER);
        southPanel.add(addReviewPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        saveButton.addActionListener(e -> saveOrUnsavePlace());
        deleteButton.addActionListener(e -> deletePlace());
        submitReviewButton.addActionListener(e -> submitReview());
        addImageButton.addActionListener(e -> pickUserImage());
    }

    public void setDestination(Destination dest) {
        this.currentDestination = dest;
        if (dest == null) return;
        currentImageIndex = 0;

        nameLabel.setText(dest.getName());
        locationLabel.setText("Location: " + dest.getLocation());
        descriptionArea.setText(dest.getDescription());
        descriptionArea.setCaretPosition(0);

        // --- MODIFICATION: Display price string ---
        priceLabel.setText("Price: ₹" + dest.getPrice());
        popularityLabel.setText(String.format("Popularity: %.1f / 10.0", dest.getPopularity()));

        // --- Load Images ---
        imagesPanel.removeAll();
        List<String> imagePaths = dest.getImagePaths();
        if (imagePaths == null || imagePaths.isEmpty()) {
            mainImageLabel.setIcon(null);
            mainImageLabel.setText("No Image Available");
        } else {
            for (int i = 0; i < imagePaths.size(); i++) {
                ImageIcon icon = createThumbnailIcon(imagePaths.get(i), 80, 80);
                JLabel thumbLabel = new JLabel(icon);
                thumbLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                thumbLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                final int index = i;
                thumbLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        currentImageIndex = index;
                        loadMainImage(imagePaths.get(currentImageIndex));
                    }
                });
                imagesPanel.add(thumbLabel);
            }
            loadMainImage(imagePaths.get(0));
        }

        updateSaveButton(dest.isSaved());
        loadReviews();
        configureForUser();

        imagesPanel.revalidate();
        imagesPanel.repaint();
    }

    private void saveOrUnsavePlace() {
        if (currentDestination == null || !userService.isLoggedIn()) return;
        boolean newSavedStatus = !currentDestination.isSaved();
        dbManager.updateSavedStatus(currentDestination.getId(), userService.getUserId(), newSavedStatus);
        currentDestination.setSaved(newSavedStatus);
        updateSaveButton(newSavedStatus);
        String message = newSavedStatus ? "Place saved!" : "Place unsaved.";
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSaveButton(boolean isSaved) {
        if (isSaved) {
            saveButton.setText("Unsave Place");
            saveButton.setBackground(new Color(255, 100, 100));
        } else {
            saveButton.setText("Save Place");
            saveButton.setBackground(new Color(100, 200, 100));
        }
    }

    private void configureForUser() {
        String role = userService.getRole();
        boolean isAdmin = "admin".equals(role);
        boolean isLoggedIn = userService.isLoggedIn();
        deleteButton.setVisible(isAdmin);
        addReviewPanel.setVisible(!isAdmin && isLoggedIn);
        addImageButton.setVisible(!isAdmin && isLoggedIn);
        saveButton.setVisible(isLoggedIn);
    }

    private void deletePlace() {
        if (currentDestination == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete '" + currentDestination.getName() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dbManager.deleteDestination(currentDestination.getId());
            JOptionPane.showMessageDialog(this, "Place deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showPanel("dashboard");
        }
    }

    private void submitReview() {
        if (currentDestination == null || !userService.isLoggedIn()) return;
        int rating = (Integer) ratingComboBox.getSelectedItem();
        String comment = reviewCommentArea.getText();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Review review = new Review(0, currentDestination.getId(), userService.getUserId(), userService.getUsername(), rating, comment);
        dbManager.addReview(review);
        reviewCommentArea.setText("");
        loadReviews();
    }

    private void loadReviews() {
        reviewsDisplayPanel.removeAll();
        if (currentDestination == null) return;
        List<Review> reviews = dbManager.getReviewsForDestination(currentDestination.getId());
        if (reviews.isEmpty()) {
            reviewsDisplayPanel.add(new JLabel("No reviews yet. Be the first!"));
        } else {
            for (Review review : reviews) {
                JPanel reviewCard = new JPanel(new BorderLayout(5, 5));
                reviewCard.setBorder(new EmptyBorder(10, 10, 10, 10));
                reviewCard.setOpaque(false);
                String title = String.format("<html><b>%s</b> rated it <b>%d/5</b> stars</html>", review.getUsername(), review.getRating());
                reviewCard.add(new JLabel(title), BorderLayout.NORTH);
                JTextArea commentText = new JTextArea(review.getComment());
                commentText.setLineWrap(true);
                commentText.setWrapStyleWord(true);
                commentText.setEditable(false);
                commentText.setOpaque(false);
                reviewCard.add(commentText, BorderLayout.CENTER);
                reviewsDisplayPanel.add(reviewCard);
                reviewsDisplayPanel.add(new JSeparator());
            }
        }
        reviewsDisplayPanel.revalidate();
        reviewsDisplayPanel.repaint();
        SwingUtilities.invokeLater(() -> reviewsScrollPane.getVerticalScrollBar().setValue(0));
    }

    // --- User Feature: Add Image ---
    private void pickUserImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select one or more images to upload");
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

                List<String> newPaths = new ArrayList<>();
                // --- MODIFICATION: Loop through all files ---
                for (File selectedFile : selectedFiles) {
                    String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    String newPath = storageDir.getAbsolutePath() + File.separator + newFileName;
                    Files.copy(selectedFile.toPath(), new File(newPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    newPaths.add(newPath);
                }

                // Add all new paths to the database in one go
                dbManager.addImagesToDestination(currentDestination.getId(), newPaths);

                JOptionPane.showMessageDialog(this, "Images added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh this panel to show new images
                this.setDestination(dbManager.getDestinationById(currentDestination.getId(), userService.getUserId()));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding images: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // --- Image Loading (Unchanged) ---
    private void loadMainImage(String path) {
        ImageIcon icon = createScaledIcon(path, 500, 400);
        if (icon != null) {
            mainImageLabel.setIcon(icon);
            mainImageLabel.setText(null);
        } else {
            mainImageLabel.setIcon(null);
            mainImageLabel.setText("Image not found");
        }
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            File f = new File(path);
            URL url = f.toURI().toURL();
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (MalformedURLException e) {
            System.err.println("Error loading image URL: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return null;
        }
    }

    private ImageIcon createThumbnailIcon(String path, int width, int height) {
        return createScaledIcon(path, width, height);
    }
}

