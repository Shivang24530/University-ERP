package edu.univ.erp.domain;

// This class holds data for one row in the "My Sections" list
public class InstructorSectionItem {
    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private String dayTime;
    private String room;
    private int enrolledCount;
    private int capacity;

    // Constructor
    public InstructorSectionItem(int sectionId, String courseCode, String courseTitle, String dayTime, String room, int enrolledCount, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.dayTime = dayTime;
        this.room = room;
        this.enrolledCount = enrolledCount;
        this.capacity = capacity;
    }

    // Getters
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getDayTime() { return dayTime; }
    public String getRoom() { return room; }
    
    // This getter formats the enrollment string for the table
    public String getEnrollment() {
        return enrolledCount + " / " + capacity;
    }
}