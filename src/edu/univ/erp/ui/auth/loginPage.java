/*
 * Login Page
 * Main login UI for the application; authenticates users and redirects
 * them to role-specific dashboards.
 */
package edu.univ.erp.ui.auth;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.ui.instructor.instructorDashboard;
import edu.univ.erp.ui.student.studentDashboard;
import edu.univ.erp.ui.admin.adminDashboard; // <-- FIX: ADDED IMPORT

import java.awt.event.ActionEvent;   
import java.awt.event.ActionListener;  

public class loginPage {
    public loginPage() {
        JFrame loginFrame = new JFrame("University ERP Login"); 

        loginFrame.setSize(450, 300); 
        loginFrame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 60, 100, 30);
        loginFrame.add(userLabel);
        
        JTextField userText = new JTextField();
        userText.setBounds(150, 60, 250, 30);
        loginFrame.add(userText);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 110, 100, 30);
        loginFrame.add(passLabel);
        
        JPasswordField passText = new JPasswordField();
        passText.setBounds(150, 110, 250, 30);
        loginFrame.add(passText);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 170, 100, 30);
        loginFrame.add(loginBtn);

        // Apply theme
        UITheme.styleFrame(loginFrame);
        UITheme.styleLabel(userLabel, UITheme.REGULAR_FONT);
        UITheme.styleLabel(passLabel, UITheme.REGULAR_FONT);
        UITheme.styleTextField(userText);
        UITheme.styleTextField(passText);
        UITheme.styleButton(loginBtn);

        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null); 
        loginFrame.setVisible(true);

        // Inside loginPage.java
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Get text from the fields
                String username = userText.getText();
                String password = new String(passText.getPassword());

                // 2. Create the login "brain"
                AuthService authService = new AuthService();

                // 3. Call login() ONLY ONCE and save the result
                String role = authService.login(username, password);

                // 4. Use "if-else if-else" and .equals() to check the role
                if ("student".equals(role)) {
                    // Success!
                    System.out.println("Opening student dashboard!");
                    new studentDashboard(); // Open student dashboard
                    loginFrame.dispose();   // Close login window

                } else if ("instructor".equals(role)) {
                    // Success!
                    System.out.println("Opening instructor dashboard!");
                    new instructorDashboard(); // Open instructor dashboard
                    loginFrame.dispose(); // Close login window
                
                } else if ("admin".equals(role)) {
                    // Success!
                    System.out.println("Opening admin dashboard!");
                    // --- This line is now fixed thanks to the import ---
                    new adminDashboard(); // Open admin dashboard
                    loginFrame.dispose(); // Close login window

                } else {
                    // Failure - 'role' contains the error message
                    System.out.println(role); // This will print the error message from AuthService
                    JOptionPane.showMessageDialog(loginFrame, role); // Show pop-up
                }
            }
        });
    }

    public static void main(String[] args) {
        new loginPage();
    }
}