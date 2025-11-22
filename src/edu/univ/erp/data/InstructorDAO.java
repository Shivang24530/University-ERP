package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.univ.erp.domain.InstructorProfile;
import edu.univ.erp.domain.InstructorSectionItem;
import edu.univ.erp.domain.StudentRosterItem;

public class InstructorDAO {

    /**
     * Fetches a list of all sections taught by a specific instructor.
     *
     * @param instructorId The user_id of the instructor.
     * @return A list of InstructorSectionItem objects.
     */
    public List<InstructorSectionItem> getSectionsByInstructor(int instructorId) {
        List<InstructorSectionItem> sections = new ArrayList<>();
        
        // This query joins sections and courses, and includes a subquery
        // to count the number of 'Enrolled' students.
        String sql = "SELECT " +
                     "  s.section_id, " +
                     "  c.code, " +
                     "  c.title, " +
                     "  s.day_time, " +
                     "  s.room, " +
                     "  s.capacity, " +
                     "  (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status = 'Enrolled') AS enrolled_count " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE s.instructor_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sectionId = rs.getInt("section_id");
                    String courseCode = rs.getString("code");
                    String courseTitle = rs.getString("title");
                    String dayTime = rs.getString("day_time");
                    String room = rs.getString("room");
                    int capacity = rs.getInt("capacity");
                    int enrolledCount = rs.getInt("enrolled_count");

                    InstructorSectionItem item = new InstructorSectionItem(
                        sectionId, courseCode, courseTitle, dayTime, room, enrolledCount, capacity
                    );
                    sections.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching instructor sections: " + e.getMessage());
            e.printStackTrace();
        }
        
        return sections;
    }
    // --- Make sure to add these imports at the top of InstructorDAO.java ---
    // import edu.univ.erp.domain.StudentRosterItem;
    // (List, ArrayList, SQLException, etc. should already be imported)

    /**
     * Fetches the roster (list of enrolled students) for a specific section.
     *
     * @param sectionId The section_id to get the roster for.
     * @return A list of StudentRosterItem objects.
     */
    public List<StudentRosterItem> getSectionRoster(int sectionId) {
        List<StudentRosterItem> roster = new ArrayList<>();
        
        // This query joins enrollments, students, and auth_db to get student info.
        // It also gets the 'final_grade' if one exists for that enrollment.
        String sql = "SELECT " +
                     "  e.enrollment_id, " +
                     "  s.user_id, " +
                     "  a.username AS student_name, " +
                     "  s.roll_no, " +
                     "  (SELECT g.final_grade FROM grades g WHERE g.enrollment_id = e.enrollment_id AND g.final_grade IS NOT NULL LIMIT 1) AS final_grade " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.user_id " +
                     "JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                     "WHERE e.section_id = ? AND e.status = 'Enrolled' " +
                     "ORDER BY s.roll_no";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int enrollmentId = rs.getInt("enrollment_id");
                    int studentId = rs.getInt("user_id");
                    String studentName = rs.getString("student_name");
                    String rollNo = rs.getString("roll_no");
                    String finalGrade = rs.getString("final_grade");

                    roster.add(new StudentRosterItem(enrollmentId, studentId, studentName, rollNo, finalGrade));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching section roster: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roster;
    }
    // --- Make sure to add these imports at the top of InstructorDAO.java ---
    // import edu.univ.erp.domain.InstructorProfile;
    // (List, ArrayList, SQLException, etc. should already be imported)

    /**
     * Fetches a list of all instructors for the admin assignment frame.
     *
     * @return A list of InstructorProfile objects.
     */
    public List<InstructorProfile> getAllInstructors() {
        List<InstructorProfile> instructors = new ArrayList<>();
        
        // This query joins instructors and auth_db to get their names
        String sql = "SELECT " +
                     "  i.user_id, " +
                     "  a.username, " +
                     "  i.department " +
                     "FROM instructors i " +
                     "JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                     "WHERE a.role = 'instructor' " +
                     "ORDER BY a.username";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int instructorId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String department = rs.getString("department");

                    instructors.add(new InstructorProfile(instructorId, username, department));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching all instructors: " + e.getMessage());
            e.printStackTrace();
        }
        
        return instructors;
    }
    /**
     * --- NEW SECURITY METHOD ---
     * Verifies that a specific section is taught by the specified instructor.
     * This is used to authorize high-level actions like CSV imports.
     *
     * @param sectionId The section_id to check.
     * @param instructorId The user_id of the instructor.
     * @return true if the instructor owns this section, false otherwise.
     */
    public boolean isSectionOwnedByInstructor(int sectionId, int instructorId) {
        String sql = "SELECT 1 FROM sections s " +
                     "WHERE s.section_id = ? AND s.instructor_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            stmt.setInt(2, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                // If a record is found, rs.next() is true.
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Security check failed in isSectionOwnedByInstructor: " + e.getMessage());
            return false; // Failsafe
        }
    }
}