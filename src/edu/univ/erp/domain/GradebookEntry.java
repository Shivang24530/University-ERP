package edu.univ.erp.domain;

// This class represents one entry in the 'grades' table
public class GradebookEntry {
    private int gradeId;
    private int enrollmentId;
    private String component; // e.g., "Quiz 1", "Midterm"
    private double score;
    private boolean scoreIsNull; // To track if the score is NULL in the DB

    // Constructor
    public GradebookEntry(int gradeId, int enrollmentId, String component, double score, boolean scoreIsNull) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.scoreIsNull = scoreIsNull;
    }

    // Getters
    public int getGradeId() { return gradeId; }
    public int getEnrollmentId() { return enrollmentId; }
    public String getComponent() { return component; }
    public double getScore() { return score; }
    public boolean isScoreNull() { return scoreIsNull; }

    // Setter for score
    public void setScore(double score) {
        this.score = score;
        this.scoreIsNull = false;
    }
}