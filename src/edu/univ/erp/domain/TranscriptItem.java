package edu.univ.erp.domain;

// This class holds data for one row of the transcript
public class TranscriptItem {
    private String courseCode;
    private String courseTitle;
    private int credits;
    private String finalGrade;

    // Constructor
    public TranscriptItem(String courseCode, String courseTitle, int credits, String finalGrade) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.finalGrade = finalGrade;
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCredits() { return credits; }
    public String getFinalGrade() { return finalGrade; }
}