package com.tourism.gui.components;

import com.tourism.gui.MainFrame;
import com.tourism.model.Destination;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A factory class to create standardized destination "cards".
 * Now displays price as a string.
 */
public class CardFactory {

    public static JPanel createDestinationCard(Destination dest, MainFrame mainFrame) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(300, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(UIManager.getColor("Panel.background"));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- 1. Image ---
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(280, 200));
        String imagePath = dest.getPrimaryImagePath();
        ImageIcon icon = createScaledIcon(imagePath, 280, 200);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("Image not found");
        }
        card.add(imageLabel, BorderLayout.NORTH);

        // --- 2. Title ---
        JLabel titleLabel = new JLabel(dest.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(titleLabel, BorderLayout.CENTER);

        // --- 3. Bottom Panel (Location & Price) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JLabel locationLabel = new JLabel(dest.getLocation());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        locationLabel.setForeground(Color.GRAY);

        // --- MODIFICATION: Display price string ---
        JLabel priceLabel = new JLabel("₹ " + dest.getPrice());
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));

        bottomPanel.add(locationLabel, BorderLayout.WEST);
        bottomPanel.add(priceLabel, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // --- Click Listener ---
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showDetailPanel(dest);
            }
        });

        return card;
    }

    private static ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            File f = new File(path);
            URL url = f.toURI().toURL();
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (MalformedURLException e) {
            // This is for local file paths
        } catch (Exception e) {
            // Fallback for resource paths (like placeholders)
            try {
                URL resourceUrl = CardFactory.class.getResource(path);
                if (resourceUrl != null) {
                    ImageIcon icon = new ImageIcon(resourceUrl);
                    Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(img);
                }
            } catch (Exception ex) {
                // Both failed
            }
        }
        return createPlaceholderIcon(width, height);
    }

    private static ImageIcon createPlaceholderIcon(int width, int height) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.DARK_GRAY);
        g.drawString("No Image", width / 2 - 30, height / 2);
        g.dispose();
        return new ImageIcon(img);
    }
}
