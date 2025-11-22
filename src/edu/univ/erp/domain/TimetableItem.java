package edu.univ.erp.domain;

// This simple class holds the raw data for one timetable entry
public class TimetableItem {
    private String courseCode;
    private String dayTime; // e.g., "Tue/Thu 15:00"
    private String room;

    public TimetableItem(String courseCode, String dayTime, String room) {
        this.courseCode = courseCode;
        this.dayTime = dayTime;
        this.room = room;
    }

    public String getCourseCode() { return courseCode; }
    public String getDayTime() { return dayTime; }
    public String getRoom() { return room; }
}