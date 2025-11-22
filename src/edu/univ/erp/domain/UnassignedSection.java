package edu.univ.erp.domain;

// This class holds data for one row in the "Unassigned Sections" list
public class UnassignedSection {
    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private String dayTime;
    private String semester;
    private int year;

    // Constructor
    public UnassignedSection(int sectionId, String courseCode, String courseTitle, String dayTime, String semester, int year) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.dayTime = dayTime;
        this.semester = semester;
        this.year = year;
    }

    // Getters
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getDayTime() { return dayTime; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
}