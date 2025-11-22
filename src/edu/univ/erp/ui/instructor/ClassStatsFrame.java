package edu.univ.erp.ui.instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import edu.univ.erp.service.instructorService;
import edu.univ.erp.domain.ClassStatistic;

public class ClassStatsFrame {

    private JFrame frame;
    private instructorService service;
    private JTable statsTable;
    private DefaultTableModel statsModel;
    private int sectionId;

    public ClassStatsFrame(int sectionId, String courseInfo) {
        this.service = new instructorService();
        this.sectionId = sectionId;

        frame = new JFrame("Class Statistics - " + courseInfo);
        frame.setSize(600, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("Class Statistics: " + courseInfo);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(20, 15, 400, 20);
        frame.add(titleLabel);

        // --- Statistics Table ---
        String[] columns = {"Assessment", "Average", "Min", "Max", "Count"};
        statsModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statsTable = new JTable(statsModel);
        statsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBounds(20, 50, 540, 250);
        frame.add(scrollPane);

        // --- Buttons ---
        JButton backButton = new JButton("Back to My Sections");
        backButton.setBounds(410, 320, 150, 30);
        frame.add(backButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MySectionsFrame(); // Open parent frame
                frame.dispose(); // Close this window
            }
        });

        // --- Load Data ---
        loadStatistics();
    }

    /**
     * Fetches stats from the service and loads them into the JTable.
     */
    private void loadStatistics() {
        // Get data from the "brain"
        List<ClassStatistic> statsList = service.getClassStats(sectionId);
        statsModel.setRowCount(0); // Clear old data

        if (statsList.isEmpty()) {
            statsModel.addRow(new Object[]{"No graded components found.", "", "", "", ""});
        }

        // Use US Locale to ensure '.' as decimal separator
        for (ClassStatistic stat : statsList) {
            Object[] row = new Object[5];
            row[0] = stat.getComponent();
            row[1] = String.format(Locale.US, "%.2f", stat.getAverage());
            row[2] = String.format(Locale.US, "%.2f", stat.getMin());
            row[3] = String.format(Locale.US, "%.2f", stat.getMax());
            row[4] = stat.getCount();
            statsModel.addRow(row);
        }
    }
}