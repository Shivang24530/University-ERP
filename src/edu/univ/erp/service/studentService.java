package edu.univ.erp.service;

import java.util.List;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.CourseQuery;
import edu.univ.erp.data.EnrollmentQuery;
import edu.univ.erp.data.GradeQuery;
import edu.univ.erp.data.StudentQuery;
import edu.univ.erp.data.SettingsQuery; // <-- IMPORT ADDED
import edu.univ.erp.domain.CourseCatalogItem;
import edu.univ.erp.domain.MySectionItem;
import edu.univ.erp.domain.TimetableItem;
import edu.univ.erp.domain.GradeItem;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.TranscriptItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.univ.erp.data.DatabaseConnector;
import java.io.FileWriter;
import java.io.IOException;

public class studentService {
    
    private CourseQuery courseDAO;
    private EnrollmentQuery enrollmentDAO;
    private GradeQuery gradeDAO;
    private StudentQuery studentDAO;
    private SettingsQuery settingsDAO; // <-- ADDED

    public studentService() {
        this.courseDAO = new CourseQuery();
        this.enrollmentDAO = new EnrollmentQuery();
        this.gradeDAO = new GradeQuery();
        this.studentDAO = new StudentQuery();
        this.settingsDAO = new SettingsQuery(); // <-- ADDED
    }

    // --- Registration & Timetable Methods ---

    public List<CourseCatalogItem> getCourseCatalog() {
        return courseDAO.getCourseCatalog();
    }

    public String registerForSection(int studentId, int sectionId) {
        // --- MAINTENANCE MODE CHECK ---
        if (settingsDAO.isMaintenanceModeOn()) {
            return "Registration Failed: The system is in maintenance mode. Please try again later.";
        }
        
        // --- Business Logic Checks (Now using DAO) ---
        // The SQL query has been moved to EnrollmentDAO.getSectionEnrollment
        int[] enrollmentData = enrollmentDAO.getSectionEnrollment(sectionId);
        int currentEnrollment = enrollmentData[0];
        int capacity = enrollmentData[1];

        if (capacity == 0 && currentEnrollment == 0) { // Check if section was found
            return "Error: Section not found.";
        }

        if (currentEnrollment >= capacity) {
            return "Registration Failed: Section is full." + " (" + currentEnrollment + "/" + capacity + ")";
        }
        // --- End of refactored block ---
        
        boolean success = enrollmentDAO.enrollStudent(studentId, sectionId);
        if (success) return "Registration Successful!";
        else return "Registration Failed: You are already enrolled in this section.";
    }

    public List<MySectionItem> getMyRegisteredSections(int studentId) {
        return enrollmentDAO.getEnrolledSections(studentId);
    }

    // In studentService.java
    public String dropSection(int enrollmentId) {
        // --- START FIX ---
        // Get the logged-in student's ID
        int studentId = UserSession.getUserId();
        if (studentId == 0) {
            return "Drop Failed: User session not found.";
        }

        // --- MAINTENANCE MODE CHECK ---
        if (settingsDAO.isMaintenanceModeOn()) {
            return "Drop Failed: The system is in maintenance mode.";
        }

        // Pass BOTH IDs to the DAO for verification
        boolean success = enrollmentDAO.dropStudent(enrollmentId, studentId);
        // --- END FIX ---

        if (success) return "Drop Successful!";
        else return "Drop Failed: An error occurred or this is not your enrollment.";
    }
    
    // --- All other "getter" methods are unchanged ---

    public Object[][] getTimetableGrid(int studentId) {
        // (This is a "read" operation, so no maintenance check is needed)
        String[] timeSlots = {"09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"};
        Object[][] gridData = new Object[timeSlots.length][6];
        for (int i = 0; i < timeSlots.length; i++) {
            gridData[i][0] = timeSlots[i];
            for (int j = 1; j < 6; j++) gridData[i][j] = ""; 
        }
        List<TimetableItem> rawData = enrollmentDAO.getTimetableData(studentId);
        
        for (TimetableItem item : rawData) {
            String course = "<html><center>" + item.getCourseCode() + "<br>(" + item.getRoom() + ")</center></html>";
            String dayTime = item.getDayTime();
            
            try {
                // --- START OF FIX ---
                // Check if dayTime is valid before splitting
                if (dayTime == null || !dayTime.contains(" ")) {
                    System.err.println("Skipping malformed timetable item: " + dayTime);
                    continue; // Skip this item and move to the next
                }
                
                // Split into exactly 2 parts (days and time)
                String[] parts = dayTime.split(" ", 2); 
                String days = parts[0]; 
                String time = parts[1];
                // --- END OF FIX ---

                int row = -1;
                if (time.startsWith("09:00")) row = 0; if (time.startsWith("10:00")) row = 1; if (time.startsWith("11:00")) row = 2; if (time.startsWith("12:00")) row = 3;
                if (time.startsWith("13:00")) row = 4; if (time.startsWith("14:00")) row = 5; if (time.startsWith("15:00")) row = 6; if (time.startsWith("16:00")) row = 7;
                
                if (row != -1) {
                    if (days.contains("Mon")) gridData[row][1] = course; if (days.contains("Tue")) gridData[row][2] = course; if (days.contains("Wed")) gridData[row][3] = course;
                    if (days.contains("Thu")) gridData[row][4] = course; if (days.contains("Fri")) gridData[row][5] = course;
                }
            } catch (Exception e) { 
                // This will now catch any other unexpected parsing errors
                System.err.println("Could not parse timetable item: " + dayTime + " | Error: " + e.getMessage()); 
            }
        }
        return gridData;
    }
    
    public List<GradeItem> getMyGrades(int studentId) {
        return gradeDAO.getGrades(studentId);
    }
    
    public List<TranscriptItem> getTranscriptItems(int studentId) {
        return gradeDAO.getTranscriptData(studentId);
    }
    
    public StudentProfile getStudentProfile(int studentId) {
        return studentDAO.getStudentProfile(studentId);
    }
    
    public String generateTranscriptCSV(int studentId) {
        StringBuilder csv = new StringBuilder();
        StudentProfile profile = getStudentProfile(studentId);
        if (profile == null) return "Error: Could not find student profile.";
        csv.append("--- Unofficial Transcript ---\n\n");
        csv.append("Student Name:,").append(profile.getUsername()).append("\n");
        // --- THIS IS THE FIXED LINE ---
        csv.append("Roll No:,").append(profile.getRollNo()).append("\n");
        // --- END OF FIX ---
        csv.append("Program:,").append(profile.getProgram()).append("\n\n");
        csv.append("Course Code,Course Title,Credits,Grade\n");
        List<TranscriptItem> items = getTranscriptItems(studentId);
        if (items.isEmpty()) { csv.append("No completed courses on record.\n"); } 
        else {
            for (TranscriptItem item : items) {
                csv.append(item.getCourseCode()).append(",\"").append(item.getCourseTitle()).append("\",");
                csv.append(item.getCredits()).append(",").append(item.getFinalGrade()).append("\n");
            }
        }
        csv.append("\n--- End of Transcript ---");
        return csv.toString();
    }

    public String writeCsvToFile(String csvContent, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent);
            return null; // Success
        } catch (IOException e) {
            e.printStackTrace();
            return "Error writing file: " + e.getMessage();
        }
    }
}