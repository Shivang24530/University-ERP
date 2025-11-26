/*
 * TranscriptFrame
 * Presents an unofficial transcript preview and allows downloading the
 * transcript as a CSV file for the logged-in student.
 */
package edu.univ.erp.ui.student;

import javax.swing.*;
import edu.univ.erp.ui.common.UITheme;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File; // <-- IMPORT ADDED
import edu.univ.erp.service.studentService; // <-- IMPORT ADDED
import edu.univ.erp.auth.UserSession; // <-- IMPORT ADDED
import edu.univ.erp.domain.StudentProfile; // <-- IMPORT ADDED
import edu.univ.erp.domain.TranscriptItem; // <-- IMPORT ADDED
import java.util.List; // <-- IMPORT ADDED

public class TranscriptFrame {

    private JFrame frame;
    private studentService service; // <-- ADDED
    private JTextArea transcriptArea; // <-- ADDED
    private int studentId; // <-- ADDED

    public TranscriptFrame() {
        this.service = new studentService(); // <-- ADDED
        this.studentId = UserSession.getUserId(); // <-- ADDED

        frame = new JFrame("Download Transcript");
        frame.setSize(600, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.styleFrame(frame);

        JLabel titleLabel = new JLabel("Unofficial Transcript");
        int fw = frame.getWidth();
        int tw = 300;
        titleLabel.setBounds((fw - tw) / 2, 20, tw, 24);
        UITheme.styleLabel(titleLabel, UITheme.SUBTITLE_FONT);
        frame.add(titleLabel);

        // --- Transcript Preview Area ---
        transcriptArea = new JTextArea();
        transcriptArea.setEditable(false);
        transcriptArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12)); // Use monospaced
        transcriptArea.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(transcriptArea);
        scrollPane.setBounds(20, 50, 540, 350);
        frame.add(scrollPane);

        // Buttons
        JButton downloadButton = new JButton("Download as CSV");
        downloadButton.setBounds(150, 420, 150, 30);
        frame.add(downloadButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBounds(320, 420, 150, 30);
        frame.add(backButton);

        frame.setVisible(true);

        // --- Action Listeners ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new studentDashboard(); // Open the dashboard
                frame.dispose(); // Close this window
            }
        });

        // --- UPDATED DOWNLOAD LISTENER ---
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (studentId == 0) {
                     JOptionPane.showMessageDialog(frame, "Error: Could not find logged-in user.", "Session Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                
                // 1. Generate the CSV content
                String csvContent = service.generateTranscriptCSV(studentId);
                if (csvContent.startsWith("Error:")) {
                    JOptionPane.showMessageDialog(frame, csvContent, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 2. Open a "Save" dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Transcript");
                // Suggest a filename
                fileChooser.setSelectedFile(new File("MyTranscript_" + UserSession.getUsername() + ".csv"));

                int userSelection = fileChooser.showSaveDialog(frame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    // Ensure it has a .csv extension
                    if (!filePath.endsWith(".csv")) {
                        filePath += ".csv";
                    }

                    // 3. Write the file using the service
                    String writeError = service.writeCsvToFile(csvContent, filePath);
                    
                    if (writeError == null) {
                        JOptionPane.showMessageDialog(frame, "Transcript downloaded successfully to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, writeError, "Download Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // --- Load the preview ---
        loadTranscriptPreview();
    }
    
    /**
     * --- NEW METHOD ---
     * Loads the student's transcript data and displays it in the
     * JTextArea as a formatted preview.
     */
    private void loadTranscriptPreview() {
        if (studentId == 0) {
            transcriptArea.setText("Error: Could not find logged-in user.");
            return;
        }

        // 1. Get Profile
        StudentProfile profile = service.getStudentProfile(studentId);
        if (profile == null) {
            transcriptArea.setText("Error: Could not load student profile.");
            return;
        }

        // 2. Get Grade Items
        List<TranscriptItem> items = service.getTranscriptItems(studentId);
        
        // 3. Build the preview string
        StringBuilder preview = new StringBuilder();
        preview.append("--- Unofficial Transcript ---\n\n");
        preview.append(String.format("%-15s %s\n", "Student Name:", profile.getUsername()));
        preview.append(String.format("%-15s %s\n", "Roll No:", profile.getRollNo()));
        preview.append(String.format("%-15s %s\n", "Program:", profile.getProgram()));
        preview.append("\n--- Completed Courses ---\n\n");
        
        // Header for the table
        preview.append(String.format("%-10s | %-25s | %-7s | %-5s\n", "Code", "Title", "Credits", "Grade"));
        preview.append("----------------------------------------------------------\n");

        if (items.isEmpty()) {
            preview.append("\nNo completed courses on record.\n");
        } else {
            for (TranscriptItem item : items) {
                preview.append(String.format("%-10s | %-25s | %-7d | %-5s\n",
                    item.getCourseCode(),
                    item.getCourseTitle(),
                    item.getCredits(),
                    item.getFinalGrade()
                ));
            }
        }
        
        preview.append("\n--- End of Transcript ---");
        transcriptArea.setText(preview.toString());
        transcriptArea.setCaretPosition(0); // Scroll to top
    }
}