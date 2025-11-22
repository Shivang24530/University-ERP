package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer; // <-- IMPORT ADDED
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.service.studentService; // <-- IMPORT ADDED
import edu.univ.erp.auth.UserSession; // <-- IMPORT ADDED

public class TimetableFrame {

    private JFrame frame;
    private studentService service; // <-- ADDED

    public TimetableFrame() {
        this.service = new studentService(); // <-- ADDED

        frame = new JFrame("My Weekly Timetable");
        frame.setSize(800, 450); // Made frame taller for more time slots
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("My Weekly Timetable");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(300, 20, 200, 20);
        frame.add(titleLabel);

        // --- DYNAMIC TIMETABLE GRID ---
        
        // 1. Define Columns
        String[] columns = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        // 2. Get Data from the "brain"
        int studentId = UserSession.getUserId();
        Object[][] data = service.getTimetableGrid(studentId); // <-- DYNAMIC DATA
        
        // 3. Create the table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JTable timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(40); // Taller rows
        timetableTable.getTableHeader().setReorderingAllowed(false); // No column moving

        // --- ADDED: Center align all cells for timetable ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // --- End of alignment ---

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBounds(20, 50, 740, 340); // Taller
        frame.add(scrollPane);

        // Navigation
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBounds(325, 400, 150, 30); // Moved down
        frame.add(backButton);

        frame.setVisible(true);

        // Action Listener
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new studentDashboard(); // Open the dashboard
                frame.dispose(); // Close this window
            }
        });
    }
}