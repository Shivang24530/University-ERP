package edu.univ.erp.domain;

// This class is a "POJO" (Plain Old Java Object).
// Its only job is to hold data.
public class Course {
    // --- ADDED courseId ---
    private int courseId; 
    private String code;
    private String title;
    private int credits;

    // --- Constructor Updated ---
    public Course(int courseId, String code, String title, int credits) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    // "Getter" methods so the UI can read the data
    public int getCourseId() { return courseId; } // <-- ADDED
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    
    // --- ADDED: This is what the JComboBox will show ---
    @Override
    public String toString() {
        return code + " - " + title;
    }
}