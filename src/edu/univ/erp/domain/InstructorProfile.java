package edu.univ.erp.domain;

// This class holds an instructor's profile data for lists
public class InstructorProfile {
    private int instructorId;
    private String username;
    private String department;

    // Constructor
    public InstructorProfile(int instructorId, String username, String department) {
        this.instructorId = instructorId;
        this.username = username;
        this.department = department;
    }

    // Getters
    public int getInstructorId() { return instructorId; }
    public String getUsername() { return username; }
    public String getDepartment() { return department; }

    // This is what the JComboBox will show
    @Override
    public String toString() {
        return username + " (" + department + ")";
    }
}