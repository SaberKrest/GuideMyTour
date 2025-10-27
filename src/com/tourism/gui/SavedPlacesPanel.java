package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.user.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel to show a table of all "Saved" destinations
 * FOR THE CURRENTLY LOGGED-IN USER.
 * Now displays price as a string.
 */
public class SavedPlacesPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTable destinationsTable;
    private DefaultTableModel tableModel;
    private List<Destination> currentSavedList;

    public SavedPlacesPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        setBackground(UIManager.getColor("Panel.background"));

        // --- Top Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        topPanel.add(backButton, BorderLayout.WEST);
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        JLabel titleLabel = new JLabel("My Saved Places");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Table of Destinations ---
        String[] columnNames = {"Name", "Location", "Price (₹)", "Popularity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        destinationsTable = new JTable(tableModel);
        destinationsTable.setFont(new Font("Arial", Font.PLAIN, 16));
        destinationsTable.setRowHeight(30);
        destinationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        destinationsTable.setFillsViewportHeight(true);
        destinationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        destinationsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = destinationsTable.getSelectedRow();
                    if (selectedRow >= 0 && currentSavedList != null && selectedRow < currentSavedList.size()) {
                        Destination selectedDest = currentSavedList.get(selectedRow);
                        mainFrame.showDetailPanel(selectedDest);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(destinationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadSavedDestinations(int userId) {
        tableModel.setRowCount(0);
        if (userId == -1) {
            tableModel.addRow(new Object[]{"Please log in to see saved places.", "", "", ""});
            return;
        }

        currentSavedList = dbManager.getSavedDestinations(userId);
        if (currentSavedList.isEmpty()) {
            tableModel.addRow(new Object[]{"No saved places yet.", "", "", ""});
        } else {
            for (Destination dest : currentSavedList) {
                tableModel.addRow(new Object[]{
                        dest.getName(),
                        dest.getLocation(),
                        // --- MODIFICATION: Display price string ---
                        dest.getPrice(),
                        String.format("%.1f", dest.getPopularity())
                });
            }
        }
    }
}

