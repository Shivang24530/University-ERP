package edu.univ.erp.data;
//This new class is responsible for one thing: inserting a new enrollment record into the enrollments table.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import edu.univ.erp.domain.MySectionItem;
import edu.univ.erp.domain.TimetableItem;

public class EnrollmentQuery {

    /**
     * Enrolls a student in a specific section.
     * Assumes business logic (like capacity checks) is done in the service layer.
     *
     * @param studentId The user_id of the student.
     * @param sectionId The section_id of the section.
     * @return true if the insert was successful, false otherwise.
     */
    public boolean enrollStudent(int studentId, int sectionId) {
        // The 'status' is set to 'Enrolled' by default.
        String sql = 
        "INSERT INTO enrollments (student_id, section_id, status) " +
        "SELECT ?, ?, 'Enrolled' FROM DUAL " +
        "WHERE NOT EXISTS ( " +
        "  SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'Enrolled' " +
        ")";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setInt(3, studentId);
            stmt.setInt(4, sectionId);

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected == 1); // Success if 1 row was inserted

        } catch (SQLException e) {
            // This will fail if the UNIQUE(student_id, section_id) constraint is violated
            // (i.e., the student is already enrolled)
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    public List<MySectionItem> getEnrolledSections(int studentId) {
        List<MySectionItem> enrolledSections = new ArrayList<>();
        
        // This query joins 4 tables to get all the info needed for the UI
        String sql = "SELECT " +
                     "  e.enrollment_id, " +
                     "  c.code, " +
                     "  c.title, " +
                     "  a.username AS instructor_name, " +
                     "  CONCAT(s.day_time, ' / ', s.room) AS time_room " + 
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "LEFT JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                     "WHERE e.student_id = ? AND e.status = 'Enrolled'"; 

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int enrollmentId = rs.getInt("enrollment_id");
                    String courseCode = rs.getString("code");
                    String courseTitle = rs.getString("title");
                    String instructorName = rs.getString("instructor_name");
                    String timeRoom = rs.getString("time_room");

                    if (instructorName == null) {
                        instructorName = "TBA";
                    }

                    MySectionItem item = new MySectionItem(
                        enrollmentId, courseCode, courseTitle, instructorName, timeRoom
                    );
                    enrolledSections.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching enrolled sections: " + e.getMessage());
            e.printStackTrace();
        }
        
        return enrolledSections;
    }
    /**
     * Drops a student from a section by updating their enrollment status.
     *
     * @param enrollmentId The primary key (enrollment_id) of the enrollment record.
     * @return true if the update was successful, false otherwise.
     */
    // In EnrollmentDAO.java
    public boolean dropStudent(int enrollmentId, int studentId) {
        // We update the status instead of deleting the row

        // --- START FIX ---
        // The query now checks BOTH the enrollmentId AND the studentId
        String sql = "UPDATE enrollments SET status = 'Dropped' " + 
                    "WHERE enrollment_id = ? AND student_id = ?";
        // --- END FIX ---

        try (Connection conn = DatabaseConnector.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            stmt.setInt(2, studentId); // Add the studentId parameter

            int rowsAffected = stmt.executeUpdate();
            // It will return 0 if the enrollmentId doesn't belong to the student
            return (rowsAffected == 1); 

        } catch (SQLException e) {
            System.err.println("Error dropping student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches the raw course and time data for a student's timetable.
     *
     * @param studentId The user_id of the student.
     * @return A list of TimetableItem objects.
     */
    public List<TimetableItem> getTimetableData(int studentId) {
        List<TimetableItem> items = new ArrayList<>();
        
        // This query gets the data for "Enrolled" courses
        String sql = "SELECT c.code, s.day_time, s.room " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE e.student_id = ? AND e.status = 'Enrolled'";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("code");
                    String dayTime = rs.getString("day_time");
                    String room = rs.getString("room");
                    items.add(new TimetableItem(courseCode, dayTime, room));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching timetable data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }

    /**
     * --- NEW METHOD ---
     * Fetches the current enrollment count and capacity for a section.
     *
     * @param sectionId The section_id to check.
     * @return An array [current_enrollment, capacity], or [0, 0] if not found.
     */
    public int[] getSectionEnrollment(int sectionId) {
        // This query now correctly counts only 'Enrolled' students
        String sqlCheck = "SELECT s.capacity, " + 
                          "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status = 'Enrolled') AS current_count " +
                          "FROM sections s WHERE s.section_id = ?";
        
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
            
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int capacity = rs.getInt("capacity");
                    int currentEnrollment = rs.getInt("current_count");
                    return new int[]{currentEnrollment, capacity};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return 0,0 on error or if section not found
        return new int[]{0, 0}; 
    }
} 
