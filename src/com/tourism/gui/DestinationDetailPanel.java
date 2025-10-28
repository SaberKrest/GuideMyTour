package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.model.Review;
import com.tourism.user.UserService;
import com.tourism.gui.components.CardFactory;
import com.tourism.main.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Panel to show full details of a destination.
 * MODIFIED:
 * - Fixed "Save Successful" loop by refactoring save logic.
 * - Applied Samarkan font to text components.
 * - Set text area and field backgrounds to white.
 * - FIX: Fixed "₹" and rating duplication bug in edit mode.
 * - ADD: Added icons to "Back" and "Saved" buttons.
 */
public class DestinationDetailPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;
    private Destination currentDestination;
    private Image backgroundImage;

    private JLabel nameLabel, locationLabel, priceLabel, popularityLabel, mainImageLabel;
    private JTextArea descriptionArea, touristSpotsArea, localSpotsArea, shopsArea;
    private JButton saveButton, backButton, deleteButton, editButton, addImageButton;
    private JPanel imagesPanel, addReviewPanel, reviewsPanel;
    private JTabbedPane tabbedPane;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewCommentArea;
    private JPanel topPanel;
    private JPanel headerPanel;

    private List<String> newImagePaths = new ArrayList<>();
    private JPanel editImagesPanel;
    private boolean isEditMode = false;

    public DestinationDetailPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        this.backgroundImage = Main.loadBackgroundImage();
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        nameLabel = new JLabel("Destination Name");
        nameLabel.setFont(new Font(Main.SAMARKAN_FONT_NAME, Font.BOLD, 48));
        headerPanel.add(nameLabel);

        locationLabel = new JLabel("Location");
        // --- MODIFICATION: Apply Samarkan font ---
        locationLabel.setFont(new Font(Main.SAMARKAN_FONT_NAME,Font.PLAIN, 24));
        // --- End of Modification ---
        headerPanel.add(locationLabel);

        panel.add(headerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);

        // --- MODIFICATION: Add icon to Back button ---
        backButton = new JButton(" Back"); // Remove arrow
        if (MainFrame.backIcon != null) {
            backButton.setIcon(MainFrame.backIcon);
        } else {
            backButton.setText("← Back"); // Fallback
        }
        backButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        buttonPanel.add(backButton);
        // --- End of Modification ---

        saveButton = new JButton("Save to My List");
        saveButton.setBackground(MainFrame.BUTTON_BG);
        saveButton.setForeground(MainFrame.BUTTON_FG);
        saveButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        saveButton.addActionListener(e -> toggleSaveDestination());
        buttonPanel.add(saveButton);

        editButton = new JButton("Edit");
        editButton.setBackground(MainFrame.ORANGE_COLOR);
        editButton.setForeground(Color.WHITE);
        // --- MODIFICATION: Apply Samarkan font ---
        editButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        editButton.addActionListener(e -> toggleEditMode());
        buttonPanel.add(editButton);

        deleteButton = new JButton("Delete Place");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        // --- MODIFICATION: Apply Samarkan font ---
        deleteButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        deleteButton.addActionListener(e -> deleteDestination());
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(400, 0));

        mainImageLabel = new JLabel("Loading image...");
        mainImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainImageLabel.setPreferredSize(new Dimension(400, 300));
        mainImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        // --- MODIFICATION: Set background to white ---
        mainImageLabel.setOpaque(true);
        mainImageLabel.setBackground(Color.WHITE);
        // --- End of Modification ---
        leftPanel.add(mainImageLabel, BorderLayout.NORTH);

        imagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        imagesPanel.setOpaque(false);
        JScrollPane imageScrollPane = new JScrollPane(imagesPanel);
        imageScrollPane.setOpaque(false);
        imageScrollPane.getViewport().setOpaque(false);
        imageScrollPane.setPreferredSize(new Dimension(400, 100));
        leftPanel.add(imageScrollPane, BorderLayout.CENTER);

        editImagesPanel = new JPanel(new BorderLayout());
        editImagesPanel.setOpaque(false);
        addImageButton = new JButton("Add Image...");
        addImageButton.setBackground(MainFrame.BUTTON_BG);
        addImageButton.setForeground(MainFrame.BUTTON_FG);
        // --- MODIFICATION: Apply Samarkan font ---
        addImageButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        addImageButton.addActionListener(e -> selectImages());
        editImagesPanel.add(addImageButton, BorderLayout.NORTH);
        editImagesPanel.setVisible(false);
        leftPanel.add(editImagesPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JTabbedPane createCenterPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        // --- MODIFICATION: Apply Samarkan font to tabs ---
        tabbedPane.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---

        JPanel overviewPanel = new JPanel(new BorderLayout(10, 10));
        overviewPanel.setOpaque(false);
        overviewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setOpaque(false);
        priceLabel = new JLabel("Price: N/A");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Keep Arial for numbers
        statsPanel.add(priceLabel);
        popularityLabel = new JLabel("Rating: N/A");
        popularityLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Keep Arial for numbers
        statsPanel.add(popularityLabel);
        overviewPanel.add(statsPanel, BorderLayout.NORTH);

        descriptionArea = createReadOnlyTextArea();
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setOpaque(false);
        // --- MODIFICATION: Set viewport background to white ---
        descScrollPane.getViewport().setBackground(Color.WHITE);
        // --- End of Modification ---
        overviewPanel.add(descScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Overview", overviewPanel);

        // --- Other Tabs ---
        touristSpotsArea = createReadOnlyTextArea();
        JScrollPane touristScrollPane = new JScrollPane(touristSpotsArea);
        touristScrollPane.getViewport().setBackground(Color.WHITE);
        tabbedPane.addTab("Tourist Spots", touristScrollPane);

        localSpotsArea = createReadOnlyTextArea();
        JScrollPane localScrollPane = new JScrollPane(localSpotsArea);
        localScrollPane.getViewport().setBackground(Color.WHITE);
        tabbedPane.addTab("Local Spots", localScrollPane);

        shopsArea = createReadOnlyTextArea();
        JScrollPane shopsScrollPane = new JScrollPane(shopsArea);
        shopsScrollPane.getViewport().setBackground(Color.WHITE);
        tabbedPane.addTab("Shopping", shopsScrollPane);

        // Make tab content (JScrollPane) transparent so viewport color shows
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tab = tabbedPane.getComponentAt(i);
            if (tab instanceof JScrollPane) {
                ((JScrollPane) tab).setOpaque(false);
            }
        }
        return tabbedPane;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(350, 0));

        TitledBorder reviewBorder = new TitledBorder("Leave a Review");
        // --- MODIFICATION: Apply Samarkan font to border ---
        reviewBorder.setTitleFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        // --- End of Modification ---
        addReviewPanel = new JPanel(new BorderLayout(5, 5));
        addReviewPanel.setOpaque(false);
        addReviewPanel.setBorder(reviewBorder);

        JPanel reviewInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reviewInputPanel.setOpaque(false);
        JLabel ratingLabel = new JLabel("Rating:");
        // --- MODIFICATION: Apply Samarkan font ---
        ratingLabel.setFont(new Font("Baskerville Old Face",Font.PLAIN, 16));
        // --- End of Modification ---
        reviewInputPanel.add(ratingLabel);
        ratingComboBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        ratingComboBox.setBackground(Color.WHITE); // Set background to white
        reviewInputPanel.add(ratingComboBox);

        JButton submitReviewButton = new JButton("Submit");
        submitReviewButton.setBackground(MainFrame.BUTTON_BG);
        submitReviewButton.setForeground(MainFrame.BUTTON_FG);
        // --- MODIFICATION: Apply Samarkan font ---
        submitReviewButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 14));
        // --- End of Modification ---
        submitReviewButton.addActionListener(e -> submitReview());
        reviewInputPanel.add(submitReviewButton);
        addReviewPanel.add(reviewInputPanel, BorderLayout.NORTH);

        reviewCommentArea = new JTextArea();
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);
        // --- MODIFICATION: Set background to white ---
        reviewCommentArea.setBackground(Color.WHITE);
        // --- End of Modification ---
        JScrollPane commentScrollPane = new JScrollPane(reviewCommentArea);
        commentScrollPane.setPreferredSize(new Dimension(0, 80));
        // --- MODIFICATION: Set viewport background to white ---
        commentScrollPane.getViewport().setBackground(Color.WHITE);
        // --- End of Modification ---
        addReviewPanel.add(commentScrollPane, BorderLayout.CENTER);
        rightPanel.add(addReviewPanel, BorderLayout.NORTH);

        TitledBorder reviewsDisplayBorder = new TitledBorder("Reviews");
        // --- MODIFICATION: Apply Samarkan font to border ---
        reviewsDisplayBorder.setTitleFont(new Font("Baskerville Old Face",Font.BOLD, 18));
        // --- End of Modification ---
        reviewsPanel = new JPanel();
        reviewsPanel.setOpaque(false);
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));

        JScrollPane reviewsScrollPane = new JScrollPane(reviewsPanel);
        reviewsScrollPane.setOpaque(false);
        reviewsScrollPane.getViewport().setOpaque(false);
        reviewsScrollPane.setBorder(reviewsDisplayBorder);
        rightPanel.add(reviewsScrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    public void setDestination(Destination destination) {
        this.currentDestination = destination;
        newImagePaths.clear();
        isEditMode = false;

        nameLabel.setText(destination.getName());
        locationLabel.setText(destination.getLocation());
        descriptionArea.setText(destination.getDescription());
        priceLabel.setText("Avg. Price (3 Days): ₹ " + destination.getPrice());
        popularityLabel.setText(String.format("Rating: %.1f", destination.getPopularity()));

        touristSpotsArea.setText(destination.getTouristSpots());
        localSpotsArea.setText(destination.getLocalSpots());
        shopsArea.setText(destination.getShops());

        loadMainImage(destination.getPrimaryImagePath());
        loadThumbnails(destination.getImagePaths());

        boolean loggedIn = userService.isLoggedIn();
        boolean isAdmin = userService.isAdmin();

        saveButton.setVisible(loggedIn && !isAdmin);
        addReviewPanel.setVisible(loggedIn && !isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);

        editImagesPanel.setVisible(false);
        setEditable(false); // Set text fields to read-only

        // --- MODIFICATION: Update Save button text/icon ---
        if (destination.isSaved()) {
            saveButton.setText("Saved ");
            if (MainFrame.savedIcon != null) {
                saveButton.setIcon(MainFrame.savedIcon);
            } else {
                saveButton.setText("Saved ✔"); // Fallback
            }
            saveButton.setBackground(Color.GRAY);
        } else {
            saveButton.setText("Save to My List");
            saveButton.setIcon(null); // Remove icon if not saved
            saveButton.setBackground(MainFrame.BUTTON_BG);
        }
        // --- End of Modification ---

        loadReviews();

        SwingUtilities.invokeLater(() -> {
            descriptionArea.setCaretPosition(0);
            touristSpotsArea.setCaretPosition(0);
            localSpotsArea.setCaretPosition(0);
            shopsArea.setCaretPosition(0);
        });
    }

    private void toggleSaveDestination() {
        if (currentDestination == null || !userService.isLoggedIn()) return;
        try {
            // --- MODIFICATION: Update Save button text/icon on toggle ---
            if (currentDestination.isSaved()) {
                dbManager.unsaveDestination(userService.getUserId(), currentDestination.getId());
                currentDestination.setSaved(false);
                saveButton.setText("Save to My List");
                saveButton.setIcon(null); // Remove icon
                saveButton.setBackground(MainFrame.BUTTON_BG);
            } else {
                dbManager.saveDestination(userService.getUserId(), currentDestination.getId());
                currentDestination.setSaved(true);
                saveButton.setText("Saved ");
                if (MainFrame.savedIcon != null) {
                    saveButton.setIcon(MainFrame.savedIcon);
                } else {
                    saveButton.setText("Saved ✔"); // Fallback
                }
                saveButton.setBackground(Color.GRAY);
            }
            // --- End of Modification ---
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating saved status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDestination() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this destination? This action is permanent.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // TODO: Delete associated images from the "images" folder
                for(String path : currentDestination.getImagePaths()) {
                    try {
                        Files.deleteIfExists(Paths.get(path));
                    } catch (Exception e) {
                        System.err.println("Failed to delete image file: " + path);
                    }
                }
                dbManager.deleteDestination(currentDestination.getId());
                JOptionPane.showMessageDialog(this, "Destination deleted successfully.");
                mainFrame.showPanel("dashboard");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting destination: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void submitReview() {
        int rating = (Integer) ratingComboBox.getSelectedItem();
        String comment = reviewCommentArea.getText();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Review review = new Review(0, currentDestination.getId(), userService.getUserId(), userService.getUsername(), rating, comment);
            dbManager.addReview(review);
            reviewCommentArea.setText("");
            loadReviews(); // Refresh review list
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error submitting review: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReviews() {
        reviewsPanel.removeAll();
        List<Review> reviews = dbManager.getReviewsForDestination(currentDestination.getId());
        if (reviews.isEmpty()) {
            JLabel noReviewsLabel = new JLabel("No reviews yet.");
            noReviewsLabel.setFont(new Font("Baskerville Old Face",Font.ITALIC, 16));
            reviewsPanel.add(noReviewsLabel);
        } else {
            for (Review review : reviews) {
                JPanel reviewCard = new JPanel(new BorderLayout(5, 5));
                reviewCard.setOpaque(false);
                reviewCard.setBorder(new EmptyBorder(10, 5, 10, 5));

                JPanel reviewHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                reviewHeader.setOpaque(false);
                JLabel userLabel = new JLabel(review.getUsername());
                userLabel.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
                reviewHeader.add(userLabel);

                for(int i = 0; i < review.getRating(); i++) {
                    JLabel starLabel = new JLabel();
                    if (CardFactory.starIcon != null) {
                        starLabel.setIcon(CardFactory.starIcon);
                    } else {
                        starLabel.setText("⭐");
                    }
                    reviewHeader.add(starLabel);
                }
                reviewCard.add(reviewHeader, BorderLayout.NORTH);

                JTextArea commentArea = new JTextArea(review.getComment());
                commentArea.setLineWrap(true);
                commentArea.setWrapStyleWord(true);
                commentArea.setEditable(false);
                commentArea.setOpaque(false);
                commentArea.setFont(new Font("Baskerville Old Face",Font.PLAIN, 14));
                reviewCard.add(commentArea, BorderLayout.CENTER);

                reviewsPanel.add(reviewCard);
            }
        }
        reviewsPanel.revalidate();
        reviewsPanel.repaint();
    }

    private void loadMainImage(String path) {
        ImageIcon icon = createScaledIcon(path, 400, 300);
        if (icon != null) {
            mainImageLabel.setIcon(icon);
            mainImageLabel.setText(null);
        } else {
            mainImageLabel.setIcon(null);
            mainImageLabel.setText("Image not found");
            mainImageLabel.setFont(new Font("Baskerville Old Face",Font.BOLD, 20));
        }
    }

    private void loadThumbnails(List<String> paths) {
        imagesPanel.removeAll();
        for (String path : paths) {
            ImageIcon thumbIcon = createThumbnailIcon(path, 80, 60);
            JLabel thumbLabel = new JLabel(thumbIcon);
            thumbLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            thumbLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    loadMainImage(path);
                }
            });

            if (isEditMode) {
                JLayeredPane layeredThumb = new JLayeredPane();
                layeredThumb.setPreferredSize(new Dimension(80, 60));
                thumbLabel.setBounds(0, 0, 80, 60);
                layeredThumb.add(thumbLabel, JLayeredPane.DEFAULT_LAYER);

                JButton deleteImgBtn = new JButton("X");
                deleteImgBtn.setMargin(new Insets(0,0,0,0));
                deleteImgBtn.setFont(new Font("Arial", Font.BOLD, 10));
                deleteImgBtn.setForeground(Color.WHITE);
                deleteImgBtn.setBackground(Color.RED);
                deleteImgBtn.setBounds(60, 0, 20, 20);
                deleteImgBtn.addActionListener(e -> deleteImage(path));
                layeredThumb.add(deleteImgBtn, JLayeredPane.PALETTE_LAYER);

                imagesPanel.add(layeredThumb);
            } else {
                imagesPanel.add(thumbLabel);
            }
        }
        imagesPanel.revalidate();
        imagesPanel.repaint();
    }

    private void deleteImage(String imagePath) {
        int choice = JOptionPane.showConfirmDialog(this, "Delete this image?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteImageByPath(imagePath);
                Files.deleteIfExists(Paths.get(imagePath));
                setDestination(dbManager.getDestinationById(currentDestination.getId(), userService.getUserId()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not delete image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private ImageIcon createThumbnailIcon(String path, int width, int height) {
        ImageIcon icon = createScaledIcon(path, width, height);
        if (icon == null) {
            return CardFactory.createPlaceholderIcon(width, height);
        }
        return icon;
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            File f = new File(path);
            if (f.exists()) {
                URL url = f.toURI().toURL();
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            // Error loading from file, will try resource
        }

        try {
            URL resourceUrl = getClass().getResource(path);
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ex) {
            // Both failed
        }
        return null;
    }

    private void selectImages() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File file : chooser.getSelectedFiles()) {
                newImagePaths.add(file.getAbsolutePath());
            }
            JOptionPane.showMessageDialog(this, newImagePaths.size() + " images selected. They will be added when you click 'Save Changes'.");
        }
    }

    /**
     * MODIFICATION: Refactored logic to prevent "Save successful" loop.
     */
    private void toggleEditMode() {
        isEditMode = !isEditMode;

        if (isEditMode) {
            // --- Entering Edit Mode ---
            setEditable(true);
            editImagesPanel.setVisible(true);
            editButton.setText("Save Changes");
            editButton.setBackground(new Color(34, 139, 34)); // Dark Green
        } else {
            // --- Exiting Edit Mode (Saving) ---
            saveEditedPlace(); // <-- Save is called *here*
            editButton.setText("Edit");
            editButton.setBackground(MainFrame.ORANGE_COLOR);
            // setEditable(false) is called inside saveEditedPlace() via setDestination()
        }
        // Reload thumbnails to show/hide delete buttons
        loadThumbnails(currentDestination.getImagePaths());
    }

    /**
     * MODIFICATION: Refactored logic to prevent "Save successful" loop.
     * Removed the "if(button == "Save")" check.
     */
    private void setEditable(boolean editable) {
        if (editable) {
            // --- Swap JLabels for JTextFields ---
            headerPanel.remove(nameLabel);
            headerPanel.remove(locationLabel);

            JTextField nameField = createEditableLabel(nameLabel.getText(), nameLabel.getFont());
            JTextField locationField = createEditableLabel(locationLabel.getText(), locationLabel.getFont());

            headerPanel.add(nameField, 0);
            headerPanel.add(locationField, 1);

            JPanel statsPanel = (JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(0);
            statsPanel.remove(priceLabel);
            statsPanel.remove(popularityLabel);

            // --- FIX: Get price from the object, not the label, to prevent "₹" duplication ---
            JTextField priceField = createEditableLabel(currentDestination.getPrice(), priceLabel.getFont());
            // --- End of Fix ---

            // --- FIX: Get popularity from the object, not the label ---
            JTextField popField = createEditableLabel(String.format("%.1f", currentDestination.getPopularity()), popularityLabel.getFont());
            // --- End of Fix ---

            JLabel priceDesc = new JLabel("Avg. Price (3 Days): ");
            priceDesc.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
            statsPanel.add(priceDesc, 0);
            statsPanel.add(priceField, 1);

            JLabel popDesc = new JLabel("Rating: ");
            popDesc.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
            statsPanel.add(popDesc, 2);
            statsPanel.add(popField, 3);

        } else {
            // --- Swap back to JLabels (handled by setDestination) ---
            // If the components are text fields, just remove them.
            // setDestination() will add the JLabels back.
            if(headerPanel.getComponent(0) instanceof JTextField) {
                headerPanel.removeAll();
                headerPanel.add(nameLabel);
                headerPanel.add(locationLabel);

                JPanel statsPanel = (JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(0);
                statsPanel.removeAll();
                statsPanel.add(priceLabel);
                statsPanel.add(popularityLabel);
            }
        }

        descriptionArea.setEditable(editable);
        touristSpotsArea.setEditable(editable);
        localSpotsArea.setEditable(editable);
        shopsArea.setEditable(editable);

        headerPanel.revalidate();
        headerPanel.repaint();
        ((JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(0)).revalidate();
        ((JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(0)).repaint();
    }

    /**
     * MODIFICATION: Refactored logic to prevent "Save successful" loop.
     * This method now sets isEditMode = false *before* calling setDestination.
     */
    private void saveEditedPlace() {
        try {
            // 1. Get data from editable components
            String name = ((JTextField) headerPanel.getComponent(0)).getText();
            String location = ((JTextField) headerPanel.getComponent(1)).getText();

            JPanel statsPanel = (JPanel) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(0);
            String price = ((JTextField) statsPanel.getComponent(1)).getText();
            double popularity = Double.parseDouble(((JTextField) statsPanel.getComponent(3)).getText());

            Destination editedDest = new Destination(
                    currentDestination.getId(),
                    name,
                    location,
                    descriptionArea.getText(),
                    new ArrayList<>(currentDestination.getImagePaths()),
                    price,
                    popularity,
                    currentDestination.isSaved(),
                    touristSpotsArea.getText(),
                    localSpotsArea.getText(),
                    shopsArea.getText()
            );

            // 3. Save new images
            if (!newImagePaths.isEmpty()) {
                String imgDir = System.getProperty("user.dir") + File.separator + "images";
                new File(imgDir).mkdirs();
                for (String sourcePathStr : newImagePaths) {
                    File sourceFile = new File(sourcePathStr);
                    String newFileName = UUID.randomUUID().toString() + "_" + sourceFile.getName();
                    Path destPath = Paths.get(imgDir, newFileName);
                    Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                    editedDest.getImagePaths().add(destPath.toString());
                    dbManager.addImageForDestination(editedDest.getId(), destPath.toString());
                }
            }

            dbManager.updateDestination(editedDest);

            // --- FIX: Show dialog *before* reloading ---
            JOptionPane.showMessageDialog(this, "Save successful!");
            isEditMode = false; // Set state
            // Reload this destination (this also reverts components to JLabels)
            setDestination(dbManager.getDestinationById(editedDest.getId(), userService.getUserId()));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Popularity must be a valid number (e.g., 8.5).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JTextField createEditableLabel(String text, Font font) {
        JTextField textField = new JTextField(text);
        textField.setFont(font);
        // --- MODIFICATION: Set background to white ---
        textField.setOpaque(true);
        textField.setBackground(Color.WHITE);
        // --- End of Modification ---
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return textField;
    }

    private JTextArea createReadOnlyTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Baskerville Old Face",Font.PLAIN, 18));
        textArea.setOpaque(true);
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        return textArea;
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
