/*
 * AssignmentFrame
 * Admin UI for assigning instructors to unassigned sections.
 */
package edu.univ.erp.ui.admin;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import edu.univ.erp.service.adminService;
import edu.univ.erp.domain.UnassignedSection;
import edu.univ.erp.domain.InstructorProfile;

public class AssignmentFrame {

    private JFrame frame;
    private adminService service;

    private JTable sectionsTable;
    private DefaultTableModel sectionsModel;
    private JComboBox<InstructorProfile> instructorCombo;

    public AssignmentFrame() {
        this.service = new adminService();

        frame = new JFrame("Assign Instructor to Section");
        frame.setSize(900, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.styleFrame(frame);

        JButton backButton = new JButton("Back");
        backButton.setBounds(810, 10, 70, 25);
        UITheme.styleButton(backButton);
        frame.add(backButton);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new adminDashboard();
                frame.dispose();
            }
        });

        // --- Table of Unassigned Sections ---
        JLabel sectionLabel = new JLabel("1. Select an Unassigned Section:");
        sectionLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        sectionLabel.setBounds(20, 20, 300, 20);
        frame.add(sectionLabel);
        
        String[] columns = {"Section ID", "Course", "Title", "Schedule", "Semester", "Year"};
        sectionsModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionsTable = new JTable(sectionsModel);
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide Section ID column
        sectionsTable.getColumnModel().getColumn(0).setMinWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBounds(20, 50, 840, 300);
        frame.add(scrollPane);

        // --- Dropdown of Instructors ---
        JLabel instLabel = new JLabel("2. Select an Instructor to Assign:");
        instLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        instLabel.setBounds(20, 380, 300, 30);
        frame.add(instLabel);
        
        instructorCombo = new JComboBox<InstructorProfile>();
        instructorCombo.setBounds(290, 380, 300, 30);
        frame.add(instructorCombo);

        // --- Assign Button ---
        JButton assignButton = new JButton("3. Assign");
        assignButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        assignButton.setBounds(610, 380, 250, 30);
        frame.add(assignButton);
        
        frame.setVisible(true);

        // --- Action Listeners ---
        assignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get selected section
                int selectedRow = sectionsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a section from the table.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int sectionId = (int) sectionsModel.getValueAt(selectedRow, 0);
                String courseInfo = sectionsModel.getValueAt(selectedRow, 1) + " (" + sectionsModel.getValueAt(selectedRow, 4) + ")";

                // 2. Get selected instructor
                InstructorProfile instructor = (InstructorProfile) instructorCombo.getSelectedItem();
                if (instructor == null || instructor.getInstructorId() == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select an instructor.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int instructorId = instructor.getInstructorId();

                // 3. Confirm
                int confirm = JOptionPane.showConfirmDialog(frame,
                    "Assign " + instructor.getUsername() + " to " + courseInfo + "?",
                    "Confirm Assignment", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // 4. Call service
                String error = service.assignInstructor(sectionId, instructorId);
                
                // 5. Show result
                if (error == null) {
                    JOptionPane.showMessageDialog(frame, "Assignment successful!");
                    // Refresh the table of unassigned sections
                    populateUnassignedSections();
                } else {
                    JOptionPane.showMessageDialog(frame, error, "Assignment Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- Load Initial Data ---
        populateUnassignedSections();
        populateInstructorComboBox();
    }

    /**
     * Fetches unassigned sections and loads them into the JTable.
     */
    private void populateUnassignedSections() {
        List<UnassignedSection> sections = service.getUnassignedSections();
        sectionsModel.setRowCount(0); // Clear old data

        for (UnassignedSection section : sections) {
            Object[] row = new Object[6];
            row[0] = section.getSectionId(); // Hidden
            row[1] = section.getCourseCode();
            row[2] = section.getCourseTitle();
            row[3] = section.getDayTime();
            row[4] = section.getSemester();
            row[5] = section.getYear();
            sectionsModel.addRow(row);
        }
    }

    /**
     * Fetches all instructors and loads them into the JComboBox.
     */
    private void populateInstructorComboBox() {
        List<InstructorProfile> instructors = service.getAllInstructors();
        instructorCombo.removeAllItems(); // Clear old data

        if (instructors.isEmpty()) {
            instructorCombo.addItem(new InstructorProfile(-1, "No instructors found", ""));
        } else {
            for (InstructorProfile instructor : instructors) {
                instructorCombo.addItem(instructor);
            }
        }
    }
}