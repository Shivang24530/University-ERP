package edu.univ.erp.domain;

// This class holds the data for one student in a section's roster
public class StudentRosterItem {
    private int enrollmentId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private String finalGrade; // To store the final grade

    // Constructor
    public StudentRosterItem(int enrollmentId, int studentId, String studentName, String rollNo, String finalGrade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.finalGrade = finalGrade;
    }

    // Getters
    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getRollNo() { return rollNo; }
    
    // Getter and Setter for finalGrade, as this will be updated
    public String getFinalGrade() { 
        return (finalGrade == null || finalGrade.isEmpty()) ? "--" : finalGrade; 
    }
    public void setFinalGrade(String finalGrade) { this.finalGrade = finalGrade; }
}