/*
 * Instructor Dashboard
 * Main dashboard UI for instructors to access My Sections, Grade Book,
 * Class Stats and account actions (logout/change password).
 */
package edu.univ.erp.ui.instructor;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;  
import edu.univ.erp.auth.UserSession; 
import edu.univ.erp.ui.auth.loginPage;
import edu.univ.erp.ui.auth.ChangePasswordFrame; // <-- IMPORT ADDED
import edu.univ.erp.ui.common.MaintenanceBanner;

public class instructorDashboard {
    
    private JFrame dashboardFrame; 

    public instructorDashboard() {
        dashboardFrame = new JFrame("Instructor Dashboard");
        dashboardFrame.setSize(500, 600); // Made height taller
        dashboardFrame.setLayout(null);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UITheme.styleFrame(dashboardFrame);

        // Title
        JLabel titleLabel = new JLabel("Instructor Dashboard");
        titleLabel.setBounds(120, 10, 300, 40);
        UITheme.styleLabel(titleLabel, UITheme.TITLE_FONT);
        dashboardFrame.add(titleLabel);

        // --- ADD THIS LINE ---
        // Checks if maintenance is on and adds the banner if needed
        MaintenanceBanner.checkAndAddBanner(dashboardFrame);
        // --- END OF ADDED LINE ---
        JButton mySections = new JButton("My Sections");
        mySections.setBounds(140, 80, 220, 50); // Moved up
        dashboardFrame.add(mySections);

        JButton gradeBook = new JButton("Grade Book");
        gradeBook.setBounds(140, 160, 220, 50); // Moved up
        dashboardFrame.add(gradeBook);
    
        JButton classStats = new JButton("Class Stats");
        classStats.setBounds(140, 240, 220, 50); // Moved up
        dashboardFrame.add(classStats);
        
        // --- NEW BUTTON ADDED ---
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setBounds(140, 320, 220, 50);
        dashboardFrame.add(changePassBtn);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(185, 400, 130, 40); // Moved down
        dashboardFrame.add(logoutBtn);

        // Style buttons
        UITheme.styleButton(mySections);
        UITheme.styleButton(gradeBook);
        UITheme.styleButton(classStats);
        UITheme.styleButton(changePassBtn);
        UITheme.styleDangerButton(logoutBtn);

        dashboardFrame.setVisible(true);

        // --- ACTION LISTENERS ---
        mySections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MySectionsFrame();
                dashboardFrame.dispose(); 
            }
        });
        
        gradeBook.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MySectionsFrame();
                dashboardFrame.dispose();
            }
        });
        
        classStats.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MySectionsFrame();
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