/*
 * Admin Dashboard
 * Main admin UI to manage users, courses, maintenance, backups and other
 * system-level actions.
 */
package edu.univ.erp.ui.admin;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.auth.UserSession; 
import edu.univ.erp.ui.auth.loginPage;
import edu.univ.erp.ui.auth.ChangePasswordFrame; // <-- IMPORT ADDED

public class adminDashboard {

    private JFrame dashboardFrame;
    
    public adminDashboard() {
        dashboardFrame = new JFrame("Admin Dashboard");
        dashboardFrame.setSize(500, 680); // <-- Made height taller for new button
        dashboardFrame.setLayout(null);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UITheme.styleFrame(dashboardFrame);

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setBounds(120, 10, 300, 40);
        UITheme.styleLabel(titleLabel, UITheme.TITLE_FONT);
        dashboardFrame.add(titleLabel);

        JButton userManagement = new JButton("User Management");
        userManagement.setBounds(100, 60, 300, 50); // <-- Adjusted height/spacing
        dashboardFrame.add(userManagement);

        JButton courseManagement = new JButton("Course & Section Management");
        courseManagement.setBounds(100, 120, 300, 50); // <-- Adjusted height/spacing
        dashboardFrame.add(courseManagement);
    
        JButton instructorAssignment = new JButton("Assign Instructor to Section");
        instructorAssignment.setBounds(100, 180, 300, 50); // <-- Adjusted height/spacing
        dashboardFrame.add(instructorAssignment);

        JButton maintenanceMode = new JButton("Toggle Maintenance Mode");
        maintenanceMode.setBounds(100, 240, 300, 50); // <-- Adjusted height/spacing
        dashboardFrame.add(maintenanceMode);
        
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setBounds(100, 300, 300, 50); // <-- Adjusted height/spacing
        dashboardFrame.add(changePassBtn);

        // --- NEW BUTTON ADDED ---
        JButton backupBtn = new JButton("Database Backup / Restore");
        backupBtn.setBounds(100, 360, 300, 50);
        dashboardFrame.add(backupBtn);
        // --- END OF NEW BUTTON ---
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(175, 440, 150, 40); // <-- Adjusted position
        dashboardFrame.add(logoutBtn);

        // Style buttons
        UITheme.styleButton(userManagement);
        UITheme.styleButton(courseManagement);
        UITheme.styleButton(instructorAssignment);
        UITheme.styleButton(maintenanceMode);
        UITheme.styleButton(changePassBtn);
        UITheme.styleButton(backupBtn);
        UITheme.styleDangerButton(logoutBtn);

        dashboardFrame.setVisible(true);

        // --- Action Listeners ---
        userManagement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserManagementFrame();
                dashboardFrame.dispose(); 
            }
        });

        courseManagement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CourseManagementFrame();
                dashboardFrame.dispose();
            }
        });

        instructorAssignment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AssignmentFrame();
                dashboardFrame.dispose();
            }
        });

        maintenanceMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MaintenanceFrame();
                dashboardFrame.dispose();
            }
        });

        // --- NEW ACTION LISTENER ---
        changePassBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the new frame
                new ChangePasswordFrame();
            }
        });

        // --- NEW ACTION LISTENER ---
        backupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the new frame
                new BackupFrame(); 
                dashboardFrame.dispose();
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dashboardFrame, "Logged out.");
                UserSession.clearSession(); 
                dashboardFrame.dispose();
                new loginPage();
            }
        });
    }
}