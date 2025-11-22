package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.auth.UserSession; // <-- IMPORT ADDED
import edu.univ.erp.ui.auth.ChangePasswordFrame; 
import edu.univ.erp.ui.common.MaintenanceBanner; // <-- IMPORT ADDED

public class studentDashboard {

    private JFrame dashboardFrame;

    public studentDashboard() {
        dashboardFrame = new JFrame("Student Dashboard");
        dashboardFrame.setSize(800, 600); 
        dashboardFrame.setLayout(null);
        dashboardFrame.setLocationRelativeTo(null); 

        // Checks if maintenance is on and adds the banner if needed
        MaintenanceBanner.checkAndAddBanner(dashboardFrame);
        // --- END OF ADDED LINE ---
        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setBounds(275, 30, 300, 30); 
        dashboardFrame.add(titleLabel);

        // --- Buttons ---
        JButton registrationBtn = new JButton("Course Catalog / Register");
        registrationBtn.setBounds(100, 120, 250, 100);
        dashboardFrame.add(registrationBtn);

        JButton timetableBtn = new JButton("My Time Table");
        timetableBtn.setBounds(450, 120, 250, 100);
        dashboardFrame.add(timetableBtn);

        JButton gradesBtn = new JButton("My Grades");
        gradesBtn.setBounds(100, 250, 250, 100);
        dashboardFrame.add(gradesBtn);

        JButton transcriptBtn = new JButton("Download Transcript");
        transcriptBtn.setBounds(450, 250, 250, 100);
        dashboardFrame.add(transcriptBtn);
        
        // --- UPDATED BUTTONS (Logout & Change Password) ---
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setBounds(100, 400, 250, 40); // Added
        dashboardFrame.add(changePassBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(450, 400, 250, 40); // Adjusted
        dashboardFrame.add(logoutBtn);

        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Changed to EXIT
        dashboardFrame.setVisible(true);

        // --- Action Listeners ---
        registrationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegistrationFrame(); 
                dashboardFrame.dispose(); 
            }
        });

        timetableBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TimetableFrame();
                dashboardFrame.dispose();
            }
        });

        gradesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new GradesFrame();
                dashboardFrame.dispose();
            }
        });

        transcriptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TranscriptFrame();
                dashboardFrame.dispose();
            }
        });

        // --- NEW ACTION LISTENER ---
        changePassBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the new frame
                // It does not need to dispose this frame
                new ChangePasswordFrame(); 
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dashboardFrame, "Logged out.");
                UserSession.clearSession(); // Clear the session
                dashboardFrame.dispose();
                // We would relaunch the login frame here
            }
        });
    }
}