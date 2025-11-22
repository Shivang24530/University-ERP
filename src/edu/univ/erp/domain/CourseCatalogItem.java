// //Now you can finally connect your RegistrationFrame to the database. We will follow the UI -> Service -> Data pattern.

// A. The "Data Object" (To hold the data)

// Create a new class to hold the information for one row of the course catalog.
package edu.univ.erp.domain;

// This class holds the data for one row in the course catalog table
public class CourseCatalogItem {
    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private int capacity;
    private String instructorName;
    private String dateTime;
    private String room;

    // Constructor
    public CourseCatalogItem(int sectionId, String courseCode, String courseTitle, int capacity, String instructorName, String dateTime, String room) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.capacity = capacity;
        this.instructorName = instructorName;
        this.dateTime = dateTime;
        this.room = room;
    }

    // Getter methods for the JTable
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCapacity() { return capacity; }
    public String getInstructorName() { return instructorName; }
    public String getDateTime() { return dateTime; }
    public String getRoom() { return room; }
}