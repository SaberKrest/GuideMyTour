package com.tourism.gui.components;

import com.tourism.gui.MainFrame;
import com.tourism.model.Destination;
import com.tourism.main.Main;

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
 * MODIFIED:
 * - Applied Samarkan font to text components (except numbers).
 */
public class CardFactory {

    public static ImageIcon starIcon;
    static {
        try {
            URL imgUrl = CardFactory.class.getResource("/com/tourism/resources/assets/star.png");
            if (imgUrl != null) {
                starIcon = new ImageIcon(imgUrl);
                Image img = starIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                starIcon = new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Could not load star icon: " + e.getMessage());
            starIcon = null;
        }
    }


    public static JPanel createDestinationCard(Destination dest, MainFrame mainFrame) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(300, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(280, 200));
        String imagePath = dest.getPrimaryImagePath();
        ImageIcon icon = createScaledIcon(imagePath, 280, 200);
        imageLabel.setIcon(icon);
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(dest.getName());
        nameLabel.setFont(new Font(Main.SAMARKAN_FONT_NAME, Font.BOLD, 22));
        infoPanel.add(nameLabel);

        JLabel locationLabel = new JLabel(dest.getLocation());
        // --- MODIFICATION: Apply Samarkan font ---
        locationLabel.setFont(MainFrame.getSamarkanFont(Font.PLAIN, 16));
        // --- End of Modification ---
        locationLabel.setForeground(Color.DARK_GRAY);
        infoPanel.add(locationLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);

        JLabel priceLabel = new JLabel("₹"+dest.getPrice());
        // --- MODIFICATION: Keep Arial for numbers/price ---
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        // --- End of Modification ---
        footerPanel.add(priceLabel, BorderLayout.WEST);

        JLabel popLabel = new JLabel(String.format("%.1f", dest.getPopularity()));
        // --- MODIFICATION: Keep Arial for numbers/rating ---
        popLabel.setFont(new Font("Arial", Font.BOLD, 14));
        // --- End of Modification ---
        if (starIcon != null) {
            popLabel.setIcon(starIcon);
        } else {
            popLabel.setText(String.format("⭐ %.1f", dest.getPopularity()));
        }
        footerPanel.add(popLabel, BorderLayout.EAST);

        card.add(footerPanel, BorderLayout.SOUTH);

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
            if (f.exists()) {
                URL url = f.toURI().toURL();
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            // Not a file path, will try resource
        }

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

        return createPlaceholderIcon(width, height);
    }

    public static ImageIcon createPlaceholderIcon(int width, int height) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        String text = "No Image";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(text, x, y);
        g.dispose();
        return new ImageIcon(img);
    }
}

