package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import edu.univ.erp.service.adminService;

public class BackupFrame {

    private JFrame frame;
    private adminService service;

    public BackupFrame() {
        this.service = new adminService();

        frame = new JFrame("Database Backup & Restore");
        frame.setSize(500, 350);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton backButton = new JButton("Back");
        backButton.setBounds(410, 10, 70, 25);
        frame.add(backButton);
        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new adminDashboard();
                frame.dispose();
            }
        });

        // --- Backup Section ---
        JLabel backupLabel = new JLabel("Backup the 'erp_db' database:");
        backupLabel.setFont(new Font("Arial", Font.BOLD, 14));
        backupLabel.setBounds(50, 60, 300, 30);
        frame.add(backupLabel);

        JButton backupButton = new JButton("Create Backup");
        backupButton.setBounds(50, 100, 400, 40);
        frame.add(backupButton);

        // --- Restore Section ---
        JLabel restoreLabel = new JLabel("Restore the 'erp_db' database from a file:");
        restoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        restoreLabel.setBounds(50, 180, 400, 30);
        frame.add(restoreLabel);

        JButton restoreButton = new JButton("Restore from Backup");
        restoreButton.setBounds(50, 220, 400, 40);
        frame.add(restoreButton);

        frame.setVisible(true);

        // --- Action Listeners ---

        // Backup Button
        backupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Backup File");
                fileChooser.setSelectedFile(new File("erp_db_backup.sql"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("SQL Files", "sql"));

                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    // --- THIS IS THE NEW CODE ---
                    // Ensure file has .sql extension
                    if (!fileToSave.getAbsolutePath().endsWith(".sql")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".sql");
                    }
                    
                    // Call the service
                    String error = service.backupDatabase(fileToSave);
                    
                    if (error == null) {
                        JOptionPane.showMessageDialog(frame, "Backup successful!\nSaved to: " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, error, "Backup Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    // --- END OF NEW CODE ---
                }
            }
        });
        // Restore Button
        // Restore Button
        restoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open Backup File to Restore");
                fileChooser.setFileFilter(new FileNameExtensionFilter("SQL Files", "sql"));

                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File fileToRestore = fileChooser.getSelectedFile();
                    int confirm = JOptionPane.showConfirmDialog(frame,
                        "WARNING:\nThis will overwrite the current database with the file:\n" + fileToRestore.getName() + "\nAre you absolutely sure?",
                        "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        // --- THIS IS THE NEW CODE ---
                        // Call the service
                        String error = service.restoreDatabase(fileToRestore);
                        
                        if (error == null) {
                            JOptionPane.showMessageDialog(frame, "Restore successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, error, "Restore Failed", JOptionPane.ERROR_MESSAGE);
                        }
                        // --- END OF NEW CODE ---
                    }
                }
            }
        });
    }
}