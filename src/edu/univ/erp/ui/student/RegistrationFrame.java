/*
 * RegistrationFrame
 * UI for browsing the course catalog, registering for sections, and
 * managing the student's enrolled sections (drop functionality).
 */
package edu.univ.erp.ui.student;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Import your service and domain classes
import edu.univ.erp.service.studentService;
import edu.univ.erp.domain.CourseCatalogItem;
import edu.univ.erp.domain.MySectionItem; 
import edu.univ.erp.auth.UserSession; 

public class RegistrationFrame {

    private JFrame frame;
    private studentService service; // The "brain"
    private JTable catalogTable;
    private DefaultTableModel catalogModel;
    
    private JTable mySectionsTable;
    private DefaultTableModel mySectionsModel;

    public RegistrationFrame() {
        this.service = new studentService(); 

        frame = new JFrame("Course Registration");
        frame.setSize(800, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.styleFrame(frame);

        // --- 1. Course Catalog (for Registering) ---
        JLabel catalogLabel = new JLabel("Available Courses (Course Catalog)");
        catalogLabel.setBounds(20, 20, 300, 20);
        UITheme.styleLabel(catalogLabel, UITheme.SUBTITLE_FONT);
        frame.add(catalogLabel);

        // --- Catalog Table Setup ---
        String[] catalogCols = {"Section ID", "Code", "Title", "Capacity", "Instructor", "Time", "Room"};
        catalogModel = new DefaultTableModel(null, catalogCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        catalogTable = new JTable(catalogModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        catalogTable.getColumnModel().getColumn(0).setMinWidth(0);
        catalogTable.getColumnModel().getColumn(0).setMaxWidth(0);
        catalogTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane catalogScrollPane = new JScrollPane(catalogTable);
        catalogScrollPane.setBounds(20, 50, 740, 200);
        frame.add(catalogScrollPane);
        
        JButton registerButton = new JButton("Register for Selected Section");
        registerButton.setBounds(20, 260, 250, 30);
        UITheme.styleButton(registerButton);
        frame.add(registerButton);

        // --- 2. My Sections (for Dropping) ---
        JLabel mySectionsLabel = new JLabel("My Registered Sections");
        mySectionsLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        mySectionsLabel.setBounds(20, 310, 300, 20);
        frame.add(mySectionsLabel);

        // --- "My Sections" Table Setup ---
        String[] mySectionsCols = {"Enrollment ID", "Code", "Title", "Instructor", "Time/Room"};
        mySectionsModel = new DefaultTableModel(null, mySectionsCols) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mySectionsTable = new JTable(mySectionsModel);
        mySectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mySectionsTable.getColumnModel().getColumn(0).setMinWidth(0);
        mySectionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        mySectionsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane mySectionsScrollPane = new JScrollPane(mySectionsTable);
        mySectionsScrollPane.setBounds(20, 340, 740, 150);
        frame.add(mySectionsScrollPane);
        
        JButton dropButton = new JButton("Drop Selected Section");
        dropButton.setBounds(20, 500, 250, 30);
        UITheme.styleButton(dropButton);
        frame.add(dropButton);

        // --- 3. Navigation ---
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBounds(610, 500, 150, 30);
        UITheme.styleButton(backButton);
        frame.add(backButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new studentDashboard(); 
                frame.dispose(); 
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = catalogTable.getSelectedRow();
                
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a section from the catalog first.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int sectionId = (int) catalogModel.getValueAt(selectedRow, 0);
                String courseInfo = catalogModel.getValueAt(selectedRow, 1) + " - " + catalogModel.getValueAt(selectedRow, 2);
                int studentId = UserSession.getUserId();

                if (studentId == 0) {
                    JOptionPane.showMessageDialog(frame, "Error: Could not find logged-in user.", "Session Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    frame, 
                    "Are you sure you want to register for:\n" + courseInfo, 
                    "Confirm Registration", 
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                String result = service.registerForSection(studentId, sectionId);

                if (result.contains("Successful")) {
                    JOptionPane.showMessageDialog(frame, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMySections(); 
                    loadCourseCatalog(); 
                } else {
                    JOptionPane.showMessageDialog(frame, result, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- UPDATED ACTION LISTENER FOR "DROP" BUTTON ---
        dropButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get selected row from the "My Sections" table
                int selectedRow = mySectionsTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a section from the 'My Registered Sections' table first.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 2. Get the hidden Enrollment ID from the selected row (column 0)
                int enrollmentId = (int) mySectionsModel.getValueAt(selectedRow, 0);
                String courseInfo = mySectionsModel.getValueAt(selectedRow, 1) + " - " + mySectionsModel.getValueAt(selectedRow, 2);

                // 3. Show a confirmation dialog
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to drop:\n" + courseInfo,
                    "Confirm Drop",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return; // User clicked "No"
                }

                // 4. Call the "brain" (service) to perform the drop
                String result = service.dropSection(enrollmentId);

                // 5. Show the result and refresh both tables
                if (result.contains("Successful")) {
                    JOptionPane.showMessageDialog(frame, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMySections(); // Refresh "My Sections"
                    loadCourseCatalog(); // Refresh catalog (to update capacity)
                } else {
                    JOptionPane.showMessageDialog(frame, result, "Drop Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- FINALLY, LOAD ALL THE DATA ---
        loadCourseCatalog();
        loadMySections(); 
    }

    /**
     * Fetches data from the service and loads the catalog JTable.
     */
    private void loadCourseCatalog() {
        // (This method is unchanged)
        List<CourseCatalogItem> catalogList = service.getCourseCatalog();
        catalogModel.setRowCount(0); 
        
        for (CourseCatalogItem item : catalogList) {
            Object[] row = new Object[7]; 
            row[0] = item.getSectionId(); 
            row[1] = item.getCourseCode();
            row[2] = item.getCourseTitle();
            row[3] = item.getCapacity();
            row[4] = item.getInstructorName();
            row[5] = item.getDateTime();
            row[6] = item.getRoom();
            catalogModel.addRow(row);
        }
    }

    /**
     * Fetches data from the service and loads the "My Sections" JTable.
     */
    private void loadMySections() {
        // (This method is unchanged)
        int studentId = UserSession.getUserId();
        if (studentId == 0) { return; } 

        List<MySectionItem> mySectionsList = service.getMyRegisteredSections(studentId);
        mySectionsModel.setRowCount(0); 
        
        for (MySectionItem item : mySectionsList) {
            Object[] row = new Object[5]; 
            row[0] = item.getEnrollmentId(); 
            row[1] = item.getCourseCode();
            row[2] = item.getCourseTitle();
            row[3] = item.getInstructorName();
            row[4] = item.getDateTimeRoom();
            mySectionsModel.addRow(row);
        }
    }
}