package edu.univ.erp.domain;

// This class holds the student's profile data
public class StudentProfile {
    private String username;
    private String rollNo;
    private String program;
    private int year;

    // Constructor
    public StudentProfile(String username, String rollNo, String program, int year) {
        this.username = username;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    // Getters
    public String getUsername() { return username; }
    public String getRollNo() { return rollNo; }
    public String getProgram() { return program; }
    public int getYear() { return year; }
}