package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import edu.univ.erp.service.adminService; // Import the new service

public class UserManagementFrame {

    private JFrame frame;
    private adminService service;

    // UI Components
    private JTextField userText;
    private JPasswordField passText;
    private JComboBox<String> roleCombo;
    private JTextField profileText; // For Roll No or Department
    private JLabel profileLabel; // Label that will change

    public UserManagementFrame() {
        this.service = new adminService();

        frame = new JFrame("User Management");
        frame.setSize(450, 400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(150, 20, 200, 20);
        frame.add(titleLabel);

        // --- Form Fields ---
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 30);
        frame.add(userLabel);
        userText = new JTextField();
        userText.setBounds(150, 80, 250, 30);
        frame.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 130, 100, 30);
        frame.add(passLabel);
        passText = new JPasswordField();
        passText.setBounds(150, 130, 250, 30);
        frame.add(passText);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 180, 100, 30);
        frame.add(roleLabel);
        String[] roles = {"student", "instructor"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setBounds(150, 180, 250, 30);
        frame.add(roleCombo);

        // Dynamic label for profile data
        profileLabel = new JLabel("Roll No:");
        profileLabel.setBounds(50, 230, 100, 30);
        frame.add(profileLabel);
        profileText = new JTextField();
        profileText.setBounds(150, 230, 250, 30);
        frame.add(profileText);

        // --- Buttons ---
        JButton createButton = new JButton("Create User");
        createButton.setBounds(150, 300, 150, 30);
        frame.add(createButton);
        
        JButton backButton = new JButton("Back");
        backButton.setBounds(350, 10, 70, 25);
        frame.add(backButton);
        
        frame.setVisible(true);

        // --- Action Listeners ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new adminDashboard();
                frame.dispose();
            }
        });

        // Change the profile label based on the role selected
        roleCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleCombo.getSelectedItem();
                if ("student".equals(selectedRole)) {
                    profileLabel.setText("Roll No:");
                } else {
                    profileLabel.setText("Department:");
                }
            }
        });

        // "Create User" button logic
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get all data from form
                String username = userText.getText();
                String password = new String(passText.getPassword());
                String role = (String) roleCombo.getSelectedItem();
                String profileData = profileText.getText();

                // 2. Simple validation
                if (username.isEmpty() || password.isEmpty() || profileData.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. Call the "brain" (service)
                String error = service.createNewUser(username, password, role, profileData);

                // 4. Show result
                if (error == null) {
                    JOptionPane.showMessageDialog(frame, "User '" + username + "' created successfully!");
                    // Clear the form
                    userText.setText("");
                    passText.setText("");
                    profileText.setText("");
                } else {
                    // Show the error message from the service
                    JOptionPane.showMessageDialog(frame, error, "Creation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}