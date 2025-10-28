package com.tourism.gui;

import com.tourism.database.DatabaseManager;
import com.tourism.model.Destination;
import com.tourism.user.UserService;
import com.tourism.main.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel to show a table of saved destinations.
 * MODIFIED:
 * - Applied Samarkan font to text components.
 * - ADD: Added icon to "Back" button.
 */
public class SavedPlacesPanel extends JPanel {

    private MainFrame mainFrame;
    private DatabaseManager dbManager;
    private UserService userService;

    private JTable destinationsTable;
    private DefaultTableModel tableModel;
    private List<Destination> currentSavedList;
    private Image backgroundImage;

    public SavedPlacesPanel(MainFrame mainFrame, DatabaseManager dbManager, UserService userService) {
        this.mainFrame = mainFrame;
        this.dbManager = dbManager;
        this.userService = userService;
        this.backgroundImage = Main.loadBackgroundImage();
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // --- MODIFICATION: Add icon to Back button ---
        JButton backButton = new JButton(" Back to Dashboard"); // Removed arrow
        if (MainFrame.backIcon != null) {
            backButton.setIcon(MainFrame.backIcon);
        } else {
            backButton.setText("← Back to Dashboard"); // Fallback
        }
        // --- End of Modification ---

        // --- MODIFICATION: Apply Samarkan font ---
        backButton.setFont(new Font("Baskerville Old Face",Font.BOLD, 16));
        // --- End of Modification ---
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("My Saved Places");
        // --- MODIFICATION: Apply Samarkan font ---
        titleLabel.setFont(new Font("Baskerville Old Face",Font.BOLD, 48));
        // --- End of Modification ---
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Location", "Price", "Popularity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        destinationsTable = new JTable(tableModel);
        // --- MODIFICATION: Apply Samarkan font to table ---
        destinationsTable.setFont(new Font("Baskerville Old Face",Font.PLAIN, 16));
        destinationsTable.getTableHeader().setFont(MainFrame.getSamarkanFont(Font.BOLD, 18));
        // --- End of Modification ---
        destinationsTable.setRowHeight(30);
        destinationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // --- MODIFICATION: Set background to white ---
        destinationsTable.setBackground(Color.WHITE);
        destinationsTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        // --- End of Modification ---

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
        // --- MODIFICATION: Set background to white ---
        scrollPane.getViewport().setBackground(Color.WHITE);
        // --- End of Modification ---
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
                        dest.getPrice(),
                        String.format("%.1f", dest.getPopularity()) // Keep numbers as Arial
                });
            }
        }
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
