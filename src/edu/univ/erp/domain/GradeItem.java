package edu.univ.erp.domain;

// This class holds data for one row in the "My Grades" table
public class GradeItem {
    private String courseCode;
    private String component; // e.g., "Quiz 1", "Midterm"
    private String score; // e.g., "85.00"
    private String finalGrade; // e.g., "A-"

    // Constructor
    public GradeItem(String courseCode, String component, String score, String finalGrade) {
        this.courseCode = courseCode;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getComponent() { return component; }
    public String getScore() { return score; }
    public String getFinalGrade() { return finalGrade; }
}