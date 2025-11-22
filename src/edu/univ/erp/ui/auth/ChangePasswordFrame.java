package edu.univ.erp.ui.auth; // Placing this in the 'auth' package

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;

public class ChangePasswordFrame {

    private JFrame frame;
    private AuthService authService;

    public ChangePasswordFrame() {
        this.authService = new AuthService();

        frame = new JFrame("Change Password");
        frame.setSize(450, 350);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't exit app

        JLabel titleLabel = new JLabel("Change Your Password");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(125, 20, 200, 20);
        frame.add(titleLabel);

        // --- Form Fields ---
        JLabel oldPassLabel = new JLabel("Old Password:");
        oldPassLabel.setBounds(50, 80, 150, 30);
        frame.add(oldPassLabel);
        JPasswordField oldPassText = new JPasswordField();
        oldPassText.setBounds(200, 80, 200, 30);
        frame.add(oldPassText);

        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(50, 130, 150, 30);
        frame.add(newPassLabel);
        JPasswordField newPassText = new JPasswordField();
        newPassText.setBounds(200, 130, 200, 30);
        frame.add(newPassText);

        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setBounds(50, 180, 150, 30);
        frame.add(confirmPassLabel);
        JPasswordField confirmPassText = new JPasswordField();
        confirmPassText.setBounds(200, 180, 200, 30);
        frame.add(confirmPassText);

        // --- Buttons ---
        JButton changeButton = new JButton("Change Password");
        changeButton.setBounds(150, 250, 150, 30);
        frame.add(changeButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        changeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get data from form
                String username = UserSession.getUsername(); // Get from session
                String oldPassword = new String(oldPassText.getPassword());
                String newPassword = new String(newPassText.getPassword());
                String confirmPassword = new String(confirmPassText.getPassword());

                // 2. Simple Validation
                if (username == null || username.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Error: Not logged in.", "Session Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(frame, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. Call the "brain" (service)
                String error = authService.changePassword(username, oldPassword, newPassword);

                // 4. Show result
                if (error == null) {
                    JOptionPane.showMessageDialog(frame, "Password changed successfully!");
                    frame.dispose(); // Close this window
                } else {
                    // Show the error from the service (e.g., "Old password was incorrect")
                    JOptionPane.showMessageDialog(frame, error, "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}