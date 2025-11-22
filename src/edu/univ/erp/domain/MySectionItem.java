package edu.univ.erp.domain;
//This is a new "domain" object, just like CourseCatalogItem, but it will hold the data for the "My Registered Sections" table.
// This class holds the data for one row in the "My Sections" table
public class MySectionItem {
    private int enrollmentId;
    private String courseCode;
    private String courseTitle;
    private String instructorName;
    private String dateTimeRoom;

    // Constructor
    public MySectionItem(int enrollmentId, String courseCode, String courseTitle, String instructorName, String dateTimeRoom) {
        this.enrollmentId = enrollmentId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.instructorName = instructorName;
        this.dateTimeRoom = dateTimeRoom;
    }

    // Getters for the JTable
    public int getEnrollmentId() { return enrollmentId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getInstructorName() { return instructorName; }
    public String getDateTimeRoom() { return dateTimeRoom; }
}