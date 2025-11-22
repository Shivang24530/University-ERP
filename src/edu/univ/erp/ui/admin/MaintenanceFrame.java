package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.service.adminService;

public class MaintenanceFrame {

    private JFrame frame;
    private adminService service;
    private JLabel statusLabel; // To show the current status

    public MaintenanceFrame() {
        this.service = new adminService();

        frame = new JFrame("Maintenance Mode Control");
        frame.setSize(450, 300);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton backButton = new JButton("Back");
        backButton.setBounds(360, 10, 70, 25);
        frame.add(backButton);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new adminDashboard();
                frame.dispose();
            }
        });

        // --- Status Display ---
        JLabel titleLabel = new JLabel("Maintenance Mode is Currently:");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setBounds(100, 60, 250, 30);
        frame.add(titleLabel);

        statusLabel = new JLabel("LOADING...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBounds(100, 100, 250, 40);
        frame.add(statusLabel);
        
        // --- Control Buttons ---
        JButton turnOnButton = new JButton("TURN ON");
        turnOnButton.setBackground(new Color(220, 50, 50)); // Red
        turnOnButton.setForeground(Color.RED);
        turnOnButton.setFont(new Font("Arial", Font.BOLD, 14));
        turnOnButton.setBounds(50, 180, 150, 50);
        frame.add(turnOnButton);

        JButton turnOffButton = new JButton("TURN OFF");
        turnOffButton.setBackground(new Color(50, 180, 50)); // Green
        turnOffButton.setForeground(Color.GREEN);
        turnOffButton.setFont(new Font("Arial", Font.BOLD, 14));
        turnOffButton.setBounds(250, 180, 150, 50);
        frame.add(turnOffButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        turnOnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                    "This will block all students and instructors from making changes.\nAre you sure you want to turn ON maintenance mode?",
                    "Confirm ON", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    String result = service.setMaintenanceMode(true); // Turn ON
                    JOptionPane.showMessageDialog(frame, result);
                    updateStatus(); // Refresh the label
                }
            }
        });
        
        turnOffButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = service.setMaintenanceMode(false); // Turn OFF
                JOptionPane.showMessageDialog(frame, result);
                updateStatus(); // Refresh the label
            }
        });

        // --- Load Initial Status ---
        updateStatus();
    }

    /**
     * Checks the service for the current mode and updates the UI label.
     */
    private void updateStatus() {
        if (service.isMaintenanceModeOn()) {
            statusLabel.setText("ON");
            statusLabel.setForeground(new Color(220, 50, 50)); // Red
        } else {
            statusLabel.setText("OFF");
            statusLabel.setForeground(new Color(50, 180, 50)); // Green
        }
    }
}