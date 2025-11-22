package edu.univ.erp.ui.instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import edu.univ.erp.service.instructorService; 
import edu.univ.erp.domain.InstructorSectionItem; 
import edu.univ.erp.auth.UserSession; 

public class MySectionsFrame {

    private JFrame frame;
    private instructorService service;
    private JTable sectionsTable;
    private DefaultTableModel sectionsModel;

    public MySectionsFrame() {
        this.service = new instructorService();

        frame = new JFrame("My Sections");
        frame.setSize(800, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("My Assigned Sections");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(300, 20, 200, 20);
        frame.add(titleLabel);

        // --- Sections Table (Unchanged) ---
        String[] columns = {"Section ID", "Course", "Title", "Time", "Room", "Enrollment"};
        sectionsModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionsTable = new JTable(sectionsModel);
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.getColumnModel().getColumn(0).setMinWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBounds(20, 50, 740, 250);
        frame.add(scrollPane);

        // --- UPDATED BUTTONS ---
        JButton manageGradesButton = new JButton("Manage Gradebook");
        manageGradesButton.setBounds(20, 320, 180, 30);
        frame.add(manageGradesButton);

        // --- NEW BUTTON ADDED ---
        JButton viewStatsButton = new JButton("View Class Stats");
        viewStatsButton.setBounds(210, 320, 180, 30);
        frame.add(viewStatsButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBounds(610, 320, 150, 30);
        frame.add(backButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new instructorDashboard(); 
                frame.dispose(); 
            }
        });

        manageGradesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sectionsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a section to manage.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int sectionId = (int) sectionsModel.getValueAt(selectedRow, 0);
                String courseInfo = sectionsModel.getValueAt(selectedRow, 1) + " - " + sectionsModel.getValueAt(selectedRow, 2);
                
                new GradebookFrame(sectionId, courseInfo); 
                frame.dispose(); 
            }
        });
        
        // --- NEW ACTION LISTENER ---
        viewStatsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sectionsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a section to view its stats.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Get the hidden section ID
                int sectionId = (int) sectionsModel.getValueAt(selectedRow, 0);
                String courseInfo = sectionsModel.getValueAt(selectedRow, 1) + " - " + sectionsModel.getValueAt(selectedRow, 2);
                
                // --- OPEN THE NEW STATS FRAME ---
                new ClassStatsFrame(sectionId, courseInfo); 
                frame.dispose(); // Close this frame
            }
        });

        // --- Load Data (Unchanged) ---
        loadSections();
    }

    private void loadSections() {
        int instructorId = UserSession.getUserId();
        if (instructorId == 0) {
            JOptionPane.showMessageDialog(frame, "Error: Could not find logged-in user.", "Session Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<InstructorSectionItem> items = service.getMySections(instructorId);
        sectionsModel.setRowCount(0); 
        for (InstructorSectionItem item : items) {
            Object[] row = new Object[6];
            row[0] = item.getSectionId(); 
            row[1] = item.getCourseCode();
            row[2] = item.getCourseTitle();
            row[3] = item.getDayTime();
            row[4] = item.getRoom();
            row[5] = item.getEnrollment(); 
            sectionsModel.addRow(row);
        }
    }
}