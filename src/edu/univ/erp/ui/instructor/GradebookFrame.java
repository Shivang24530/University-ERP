package edu.univ.erp.ui.instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import edu.univ.erp.service.instructorService;
import edu.univ.erp.domain.StudentRosterItem;
import edu.univ.erp.domain.GradebookEntry;

public class GradebookFrame {

    private JFrame frame;
    private instructorService service;
    private JTable gradeTable;
    private DefaultTableModel gradeModel;
    private int sectionId;
    private String courseInfo;
    private List<StudentRosterItem> roster;
    private List<GradebookEntry> grades;
    private final String[] columns = {"Enrollment ID", "Roll No", "Name", "Quiz 1", "Midterm", "Final Exam", "Final Grade"};
    private boolean isUpdatingByCode = false;

    public GradebookFrame(int sectionId, String courseInfo) {
        this.service = new instructorService();
        this.sectionId = sectionId;
        this.courseInfo = courseInfo;

        frame = new JFrame("Gradebook - " + courseInfo);
        frame.setSize(900, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // UI components
        JLabel titleLabel = new JLabel("Gradebook: " + courseInfo);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        titleLabel.setBounds(20, 15, 400, 20);
        frame.add(titleLabel);
        
        gradeModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3 && column <= 5;
            }
        };
        
        gradeTable = new JTable(gradeModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel tcm = gradeTable.getColumnModel();
        tcm.getColumn(0).setMinWidth(0); 
        tcm.getColumn(0).setMaxWidth(0); 
        tcm.getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBounds(20, 50, 840, 450);
        frame.add(scrollPane);
        
        JButton calcFinalButton = new JButton("Calculate Final Grades");
        calcFinalButton.setBounds(20, 520, 200, 30);
        frame.add(calcFinalButton);
        
        JButton exportButton = new JButton("Export as CSV");
        exportButton.setBounds(230, 520, 150, 30);
        frame.add(exportButton);
        
        JButton importButton = new JButton("Import from CSV");
        importButton.setBounds(390, 520, 150, 30);
        frame.add(importButton);
        
        JButton backButton = new JButton("Back to My Sections");
        backButton.setBounds(710, 520, 150, 30);
        frame.add(backButton);
        
        frame.setVisible(true);

        // Action Listeners
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MySectionsFrame(); 
                frame.dispose(); 
            }
        });

        // Table Edit Listener
        gradeModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && !isUpdatingByCode) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();

                    int enrollmentId = (int) gradeModel.getValueAt(row, 0);
                    String component = gradeModel.getColumnName(col);
                    String valueStr = (String) gradeModel.getValueAt(row, col);

                    try {
                        double score = Double.parseDouble(valueStr);
                        String error = service.saveGrade(enrollmentId, component, score);
                        
                        if (error != null) {
                            JOptionPane.showMessageDialog(frame, error, "Save Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid score. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Calculate Final Button Listener
        calcFinalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                    "This will calculate and save final grades based on the 20/30/50 rule.\nThis action cannot be undone. Continue?",
                    "Confirm Final Grade Calculation", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                String firstError = null;
                isUpdatingByCode = true;
                
                for (int i = 0; i < gradeModel.getRowCount(); i++) {
                    int enrollmentId = (int) gradeModel.getValueAt(i, 0);
                    double quiz1 = parseScore(gradeModel.getValueAt(i, 3));
                    double midterm = parseScore(gradeModel.getValueAt(i, 4));
                    double finalExam = parseScore(gradeModel.getValueAt(i, 5));

                    String letterGrade = service.calculateFinalGrade(quiz1, midterm, finalExam);

                    if (!letterGrade.equals("--")) {
                        String error = service.saveFinalGrade(enrollmentId, letterGrade);
                        if (error != null && firstError == null) {
                            firstError = error;
                        }
                    }
                    gradeModel.setValueAt(letterGrade, i, 6);
                }
                
                isUpdatingByCode = false;

                if (firstError != null) {
                    JOptionPane.showMessageDialog(frame, "Error during calculation: " + firstError, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Final grades calculated and saved.");
                }
            }
        });

        // Export Button Listener
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String csvContent = service.generateGradebookCSV(sectionId);
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Gradebook CSV");
                
                String suggestedName = "Gradebook_" + courseInfo.replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
                fileChooser.setSelectedFile(new File(suggestedName));

                int userSelection = fileChooser.showSaveDialog(frame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    if (!filePath.endsWith(".csv")) {
                        filePath += ".csv";
                    }

                    String writeError = service.writeCsvToFile(csvContent, filePath);
                    
                    if (writeError == null) {
                        JOptionPane.showMessageDialog(frame, "Gradebook exported successfully to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, writeError, "Export Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Import Button Listener
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Gradebook CSV to Import");
                fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
                
                int userSelection = fileChooser.showOpenDialog(frame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToImport = fileChooser.getSelectedFile();
                    String result = service.importGradebookCSV(fileToImport, sectionId);
                    
                    JOptionPane.showMessageDialog(frame, result, "Import Status", JOptionPane.INFORMATION_MESSAGE);
                    loadGradebook();
                }
            }
        });

        loadGradebook();
    }
    
    private double parseScore(Object value) {
        if (value == null || value.toString().isEmpty()) return -1.0;
        try { 
            return Double.parseDouble(value.toString()); 
        } catch (Exception e) { 
            return -1.0; 
        }
    }
    
    private void loadGradebook() {
        this.roster = service.getSectionRoster(sectionId);
        this.grades = service.getGradesForSection(sectionId);
        
        Map<Integer, Map<String, Double>> gradeMap = new HashMap<>();
        for (GradebookEntry entry : grades) {
            int eId = entry.getEnrollmentId();
            gradeMap.putIfAbsent(eId, new HashMap<>());
            if (!entry.isScoreNull()) {
                gradeMap.get(eId).put(entry.getComponent(), entry.getScore());
            }
        }
        
        gradeModel.setRowCount(0);
        for (StudentRosterItem student : roster) {
            int enrollmentId = student.getEnrollmentId();
            Map<String, Double> studentGrades = gradeMap.get(enrollmentId);
            Object[] row = new Object[columns.length];
            row[0] = student.getEnrollmentId();
            row[1] = student.getRollNo();
            row[2] = student.getStudentName();
            row[3] = studentGrades != null ? studentGrades.getOrDefault("Quiz 1", null) : null;
            row[4] = studentGrades != null ? studentGrades.getOrDefault("Midterm", null) : null;
            row[5] = studentGrades != null ? studentGrades.getOrDefault("Final Exam", null) : null;
            row[6] = student.getFinalGrade();
            gradeModel.addRow(row);
        }
    }
}