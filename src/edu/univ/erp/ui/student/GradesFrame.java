package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List; // <-- IMPORT ADDED
import edu.univ.erp.service.studentService; // <-- IMPORT ADDED
import edu.univ.erp.domain.GradeItem; // <-- IMPORT ADDED
import edu.univ.erp.auth.UserSession; // <-- IMPORT ADDED

public class GradesFrame {

    private JFrame frame;
    private studentService service; // <-- ADDED
    private JTable gradesTable; // <-- ADDED
    private DefaultTableModel gradesModel; // <-- ADDED

    public GradesFrame() {
        this.service = new studentService(); // <-- ADDED

        frame = new JFrame("My Grades");
        frame.setSize(600, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(250, 20, 200, 20);
        frame.add(titleLabel);

        // --- DYNAMIC GRADES TABLE ---
        String[] columns = {"Course", "Assessment", "Score", "Final Grade"};
        
        // Make table non-editable
        gradesModel = new DefaultTableModel(null, columns) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        gradesTable = new JTable(gradesModel);
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBounds(20, 50, 540, 250);
        frame.add(scrollPane);
        // --- END OF DYNAMIC TABLE ---

        // Navigation
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBounds(225, 320, 150, 30);
        frame.add(backButton);

        frame.setVisible(true);

        // Action Listener
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new studentDashboard(); // Open the dashboard
                frame.dispose(); // Close this window
            }
        });

        // --- LOAD DATA ---
        loadGrades();
    }

    /**
     * A new method to fetch data from the service
     * and load it into the "My Grades" JTable.
     */
    private void loadGrades() {
        // 1. Get student ID from the session
        int studentId = UserSession.getUserId();
        if (studentId == 0) { 
            JOptionPane.showMessageDialog(frame, "Error: Could not find logged-in user.", "Session Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        // 2. Get data from the "brain"
        List<GradeItem> myGradesList = service.getMyGrades(studentId);
        
        // 3. Clear any old data from the table
        gradesModel.setRowCount(0); 
        
        // 4. Add each item to the table model
        String lastCourse = "";
        for (GradeItem item : myGradesList) {
            Object[] row = new Object[4];
            
            // Only show the course code on the first row for that course
            String courseDisplay = item.getCourseCode();
            if (courseDisplay.equals(lastCourse)) {
                courseDisplay = ""; // Don't repeat it
            } else {
                lastCourse = courseDisplay;
            }
            
            row[0] = courseDisplay;
            row[1] = item.getComponent();
            row[2] = item.getScore();
            row[3] = item.getFinalGrade(); // Show final grade if it exists

            gradesModel.addRow(row);
        }
    }
}