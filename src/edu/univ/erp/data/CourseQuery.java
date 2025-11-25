package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.domain.CourseCatalogItem;
import edu.univ.erp.domain.Course; // <-- IMPORT ADDED

public class CourseQuery {

    /**
     * Fetches the full course catalog (all sections) for the registration frame.
     * (This method is unchanged)
     */
    public List<CourseCatalogItem> getCourseCatalog() {
        List<CourseCatalogItem> catalog = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  s.section_id, c.code, c.title, s.capacity, " +
                     "  s.day_time, s.room, a.username AS instructor_name " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "LEFT JOIN auth_db.users_auth a ON i.user_id = a.user_id";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                String courseCode = rs.getString("code");
                String courseTitle = rs.getString("title");
                int capacity = rs.getInt("capacity");
                String dateTime = rs.getString("day_time");
                String room = rs.getString("room");
                String instructorName = rs.getString("instructor_name");
                if (instructorName == null) instructorName = "TBA";

                CourseCatalogItem item = new CourseCatalogItem(
                    sectionId, courseCode, courseTitle, capacity, instructorName, dateTime, room
                );
                catalog.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching course catalog: " + e.getMessage());
            e.printStackTrace();
        }
        return catalog;
    }

    /**
     * --- NEW METHOD ---
     * Fetches a simple list of all base courses (ID, Code, Title).
     * This is used by the admin to populate a dropdown.
     * We reuse the 'Course' domain object.
     *
     * @return A list of Course objects.
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        // This query just gets the main course list
        String sql = "SELECT course_id, code, title, credits FROM courses ORDER BY code";
                     
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // ... inside getAllCourses() ...
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String code = rs.getString("code");
                String title = rs.getString("title");
                int credits = rs.getInt("credits"); // <-- Read credits
                
                // --- This is the corrected line ---
                Course course = new Course(courseId, code, title, credits);
                courses.add(course);
            }
// ...
        } catch (SQLException e) {
            System.out.println("Database error fetching all courses.");
            e.printStackTrace();
        }
        return courses; // Return the list of courses
    }
}