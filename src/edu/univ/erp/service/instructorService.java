package edu.univ.erp.service;

import java.util.List;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.domain.ClassStatistic;
import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.domain.InstructorSectionItem;
import edu.univ.erp.domain.StudentRosterItem;
import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import edu.univ.erp.auth.UserSession;

public class instructorService {

    private InstructorDAO instructorDAO;
    private GradeDAO gradeDAO;
    private SettingsDAO settingsDAO;

    public instructorService() {
        this.instructorDAO = new InstructorDAO();
        this.gradeDAO = new GradeDAO();
        this.settingsDAO = new SettingsDAO();
    }

    /**
     * Gets the list of sections taught by a specific instructor.
     */
    public List<InstructorSectionItem> getMySections(int instructorId) {
        return instructorDAO.getSectionsByInstructor(instructorId);
    }

    /**
     * Gets the class roster (list of students) for a given section.
     */
    public List<StudentRosterItem> getSectionRoster(int sectionId) {
        return instructorDAO.getSectionRoster(sectionId);
    }

    /**
     * Gets all the individual grade components for a section.
     */
    public List<GradebookEntry> getGradesForSection(int sectionId) {
        return gradeDAO.getGradesForSection(sectionId);
    }

    /**
     * Saves (or updates) a single grade component for a student.
     * Includes IDOR check.
     */
    public String saveGrade(int enrollmentId, String component, double score) {
        // --- START IDOR FIX ---
        // 1. Get the logged-in instructor's ID
        int instructorId = UserSession.getUserId();
        if (instructorId == 0) {
            return "Error: User session not found.";
        }
        
        // 2. Verify ownership *before* checking maintenance or saving
        if (!gradeDAO.isEnrollmentOwnedByInstructor(enrollmentId, instructorId)) {
            return "Error: You do not have permission to grade this student.";
        }
        // --- END IDOR FIX ---

        // 3. Maintenance check
        if (settingsDAO.isMaintenanceModeOn()) {
            return "Error: Maintenance Mode is ON. Cannot save grades.";
        }

        // 4. Save the grade (this is now safe)
        boolean success = gradeDAO.saveGrade(enrollmentId, component, score);
        if (success) {
            return null; // Success
        } else {
            return "Error: Database save failed.";
        }
    }

    /**
     * Saves a final grade for a student.
     * Includes IDOR check.
     */
    public String saveFinalGrade(int enrollmentId, String finalGrade) {
        // --- START IDOR FIX ---
        // 1. Get the logged-in instructor's ID
        int instructorId = UserSession.getUserId();
        if (instructorId == 0) {
            return "Error: User session not found.";
        }

        // 2. Verify ownership
        if (!gradeDAO.isEnrollmentOwnedByInstructor(enrollmentId, instructorId)) {
            return "Error: You do not have permission to assign a final grade to this student.";
        }
        // --- END IDOR FIX ---

        // 3. Maintenance check
        if (settingsDAO.isMaintenanceModeOn()) {
            return "Error: Maintenance Mode is ON. Cannot save final grades.";
        }
        
        // 4. Save the grade (this is now safe)
        boolean success = gradeDAO.saveFinalGrade(enrollmentId, finalGrade);
        if (success) {
            return null; // Success
        } else {
            return "Error: Database save failed.";
        }
    }

    /**
     * Calculates the final *letter grade* based on a weighting rule.
     */
    public String calculateFinalGrade(double quiz1, double midterm, double finalExam) {
        if (quiz1 < 0 || midterm < 0 || finalExam < 0) {
            return "--";
        }
        double finalScore = (quiz1 * 0.20) + (midterm * 0.30) + (finalExam * 0.50);

        if (finalScore >= 93) return "A"; if (finalScore >= 90) return "A-";
        if (finalScore >= 87) return "B+"; if (finalScore >= 83) return "B";
        if (finalScore >= 80) return "B-"; if (finalScore >= 77) return "C+";
        if (finalScore >= 73) return "C"; if (finalScore >= 70) return "C-";
        if (finalScore >= 60) return "D"; else return "F";
    }

    /**
     * Gets the calculated statistics for a given section.
     */
    public List<ClassStatistic> getClassStats(int sectionId) {
        return gradeDAO.getSectionStatistics(sectionId);
    }

    /**
     * Generates a CSV string of the entire gradebook for a section.
     */
    public String generateGradebookCSV(int sectionId) {
        StringBuilder csv = new StringBuilder();
        
        String[] columns = {"Enrollment ID", "Roll No", "Name", "Quiz 1", "Midterm", "Final Exam", "Final Grade"};
        csv.append(String.join(",", columns)).append("\n");

        List<StudentRosterItem> roster = getSectionRoster(sectionId);
        List<GradebookEntry> grades = getGradesForSection(sectionId);
        
        Map<Integer, Map<String, Double>> gradeMap = new HashMap<>();
        for (GradebookEntry entry : grades) {
            int eId = entry.getEnrollmentId();
            gradeMap.putIfAbsent(eId, new HashMap<>());
            if (!entry.isScoreNull()) {
                gradeMap.get(eId).put(entry.getComponent(), entry.getScore());
            }
        }

        for (StudentRosterItem student : roster) {
            int enrollmentId = student.getEnrollmentId();
            Map<String, Double> studentGrades = gradeMap.get(enrollmentId);

            csv.append(student.getEnrollmentId()).append(",");
            csv.append("\"").append(student.getRollNo()).append("\",");
            csv.append("\"").append(student.getStudentName()).append("\",");

            String quiz1 = (studentGrades != null && studentGrades.get("Quiz 1") != null) ? String.valueOf(studentGrades.get("Quiz 1")) : "";
            String midterm = (studentGrades != null && studentGrades.get("Midterm") != null) ? String.valueOf(studentGrades.get("Midterm")) : "";
            String finalExam = (studentGrades != null && studentGrades.get("Final Exam") != null) ? String.valueOf(studentGrades.get("Final Exam")) : "";
            String finalGrade = student.getFinalGrade().equals("--") ? "" : student.getFinalGrade();
            
            csv.append(quiz1).append(",");
            csv.append(midterm).append(",");
            csv.append(finalExam).append(",");
            csv.append(finalGrade).append("\n");
        }
        
        return csv.toString();
    }

    /**
     * Writes a given string content to a file at the specified path.
     * Error handling is corrected.
     */
    public String writeCsvToFile(String csvContent, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent);
            return null; // Success
        } catch (IOException e) {
            // --- START TIER 3 FIX: Error Handling ---
            System.err.println("Error writing file: " + e.getMessage());
            // e.printStackTrace(); // REMOVED
            // --- END TIER 3 FIX ---
            return "Error writing file: " + e.getMessage();
        }
    }
    
    /**
     * --- NEW SECURITY HELPER ---
     * Helper to validate a final grade string against an allow-list.
     */
    private boolean isValidGrade(String grade) {
        if (grade == null || grade.isEmpty()) {
            return false;
        }
        // Use a Set for efficient lookup. Add all valid grades.
        java.util.Set<String> validGrades = java.util.Set.of(
            "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"
        );
        return validGrades.contains(grade);
    }

    /**
     * Processes a CSV file and updates the gradebook.
     * Includes IDOR check, Input Validation, and corrected Error Handling.
     */
    public String importGradebookCSV(File csvFile, int sectionId) {
        // --- START IDOR FIX ---
        // 1. Get the logged-in instructor's ID
        int instructorId = UserSession.getUserId();
        if (instructorId == 0) {
            return "Error: User session not found.";
        }

        // 2. Verify ownership of the entire section *before* any action
        if (!instructorDAO.isSectionOwnedByInstructor(sectionId, instructorId)) {
            return "Error: You do not have permission to import grades for this section.";
        }
        // --- END IDOR FIX ---

        // 3. Maintenance check
        if (settingsDAO.isMaintenanceModeOn()) {
            return "Error: Maintenance Mode is ON. Cannot import grades.";
        }

        // 4. Get the current roster (this is safe now)
        List<StudentRosterItem> roster = getSectionRoster(sectionId);
        Map<String, Integer> rollNoToEnrollmentIdMap = new HashMap<>();
        for (StudentRosterItem student : roster) {
            rollNoToEnrollmentIdMap.put(student.getRollNo(), student.getEnrollmentId());
        }

        int successCount = 0;
        int failCount = 0;
        String line = "";

        // 5. Read the CSV file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            br.readLine(); // Skip the header row
            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    String rollNo = parts[1].replace("\"", "");
                    
                    Integer enrollmentId = rollNoToEnrollmentIdMap.get(rollNo);

                    if (enrollmentId == null) {
                        failCount++;
                        continue; 
                    }
                    
                    parseAndSaveScore(enrollmentId, "Quiz 1", parts[3]);
                    parseAndSaveScore(enrollmentId, "Midterm", parts[4]);
                    parseAndSaveScore(enrollmentId, "Final Exam", parts[5]);
                    
                    if (parts.length > 6 && !parts[6].isEmpty()) {
                        String finalGrade = parts[6].trim().toUpperCase(); // Standardize

                        // --- START TIER 3 FIX: Validate Grade ---
                        if (isValidGrade(finalGrade)) {
                            this.saveFinalGrade(enrollmentId, finalGrade);
                        } else if (!finalGrade.equals("--") && !finalGrade.isEmpty()) {
                            // Log the invalid grade but don't stop the import
                            System.err.println("Skipping invalid final grade: " + finalGrade);
                        }
                        // --- END TIER 3 FIX ---
                    }
                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    // --- START TIER 3 FIX: Error Handling ---
                    System.err.println("Failed to parse line: " + line + " | Error: " + e.getMessage());
                    // e.printStackTrace(); // REMOVED
                    // --- END TIER 3 FIX ---
                }
            }
        } catch (IOException e) {
            // --- START TIER 3 FIX: Error Handling ---
            System.err.println("Error reading file: " + e.getMessage());
            // e.printStackTrace(); // REMOVED
            // --- END TIER 3 FIX ---
            return "Error reading file: " + e.getMessage();
        }

        return String.format("Import complete.\n\nSuccessfully updated: %d students.\nFailed or skipped: %d rows.", successCount, failCount);
    }

    /**
     * A small helper to parse a score and save it, skipping if invalid.
     */
    private void parseAndSaveScore(int enrollmentId, String component, String scoreStr) {
        if (scoreStr == null || scoreStr.isEmpty()) {
            return; // Skip empty scores
        }
        try {
            double score = Double.parseDouble(scoreStr);
            // This call is now safe due to the IDOR check in saveGrade
            this.saveGrade(enrollmentId, component, score);
        } catch (NumberFormatException e) {
            // Do nothing if the score is not a valid number
        }
    }
}