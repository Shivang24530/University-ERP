package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.univ.erp.domain.ClassStatistic;
import edu.univ.erp.domain.GradeItem; // Import the new domain object
import edu.univ.erp.domain.TranscriptItem;
import edu.univ.erp.domain.GradebookEntry;

public class GradeQuery {

    /**
     * Fetches all grade components for a specific student.
     *
     * @param studentId The user_id of the student.
     * @return A list of GradeItem objects.
     */
    public List<GradeItem> getGrades(int studentId) {
        List<GradeItem> grades = new ArrayList<>();
        
        // This query joins grades, enrollments, sections, and courses
        // to get all info needed for the UI.
        String sql = "SELECT c.code, g.component, g.score, g.final_grade FROM grades g JOIN enrollments e ON g.enrollment_id = e.enrollment_id JOIN sections s ON e.section_id = s.section_id JOIN courses c ON s.course_id = c.course_id WHERE e.student_id = ? ORDER BY c.code, g.component";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("code");
                    String component = rs.getString("component");
                    
                    // Handle score formatting (from DECIMAL(5, 2))
                    double scoreValue = rs.getDouble("score");
                    String score = rs.wasNull() ? "" : String.format("%.2f", scoreValue);

                    String finalGrade = rs.getString("final_grade");
                    if (finalGrade == null) {
                        finalGrade = ""; // Use empty string instead of "null"
                    }

                    grades.add(new GradeItem(courseCode, component, score, finalGrade));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching grades: " + e.getMessage());
            e.printStackTrace();
        }
        
        return grades;
    }
    /**
     * Fetches only the completed, final grades for a student's transcript.
     * It looks for rows where 'final_grade' is not null or empty.
     *
     * @param studentId The user_id of the student.
     * @return A list of TranscriptItem objects.
     */
    public List<TranscriptItem> getTranscriptData(int studentId) {
        List<TranscriptItem> items = new ArrayList<>();
        
        // This query joins 4 tables and groups by course
        // to get the final grade for each completed course.
        String sql = "SELECT c.code, c.title, c.credits, g.final_grade FROM grades g JOIN enrollments e ON g.enrollment_id = e.enrollment_id JOIN sections s ON e.section_id = s.section_id JOIN courses c ON s.course_id = c.course_id WHERE e.student_id = ? AND g.final_grade IS NOT NULL AND g.final_grade != '' GROUP BY c.course_id, g.final_grade ORDER BY c.code";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("code");
                    String courseTitle = rs.getString("title");
                    int credits = rs.getInt("credits");
                    String finalGrade = rs.getString("final_grade");
                    
                    items.add(new TranscriptItem(courseCode, courseTitle, credits, finalGrade));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching transcript data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    // --- Make sure to add these imports at the top of GradeDAO.java ---
    // import edu.univ.erp.domain.GradebookEntry;
    // (List, ArrayList, SQLException, etc. should already be imported)

    /**
     * Fetches all individual grade components for all students in a section.
     *
     * @param sectionId The section_id.
     * @return A list of all GradebookEntry objects for that section.
     */
    public List<GradebookEntry> getGradesForSection(int sectionId) {
        List<GradebookEntry> entries = new ArrayList<>();
        
        String sql = "SELECT g.grade_id, g.enrollment_id, g.component, g.score " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "WHERE e.section_id = ? " +
                     "AND (g.final_grade IS NULL OR g.final_grade = '')"; // Only get components, not final grades

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int gradeId = rs.getInt("grade_id");
                    int enrollmentId = rs.getInt("enrollment_id");
                    String component = rs.getString("component");
                    double score = rs.getDouble("score");
                    boolean scoreIsNull = rs.wasNull(); // Check if the score was NULL

                    entries.add(new GradebookEntry(gradeId, enrollmentId, component, score, scoreIsNull));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching section grades: " + e.getMessage());
            e.printStackTrace();
        }
        return entries;
    }

    /**
     * Creates or updates a grade component in the database.
     * It uses INSERT ... ON DUPLICATE KEY UPDATE.
     * We need a UNIQUE key on (enrollment_id, component) for this to work.
     *
     * Let's add that to the 'grades' table definition in erp_db.sql:
     * * CREATE TABLE IF NOT EXISTS grades (
     * grade_id INT PRIMARY KEY AUTO_INCREMENT,
     * enrollment_id INT,
     * component VARCHAR(50),
     * score DECIMAL(5, 2),
     * final_grade VARCHAR(2),
     * FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
     * UNIQUE KEY uk_enroll_comp (enrollment_id, component) -- <-- ADD THIS LINE
     * );
     *
     * @param enrollmentId The student's enrollment ID.
     * @param component The name of the grade (e.g., "Midterm").
     * @param score The score to save.
     * @return true on success, false on failure.
     */
    public boolean saveGrade(int enrollmentId, String component, double score) {
        // This SQL will insert a new row. If a row with the same
        // (enrollment_id, component) already exists, it will update the score.
        String sql = "INSERT INTO grades (enrollment_id, component, score) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            stmt.setString(2, component);
            stmt.setDouble(3, score);

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0); // 1 = insert, 2 = update

        } catch (SQLException e) {
            System.err.println("Database error saving grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the final calculated grade for a student.
     * This creates a *new* grade entry.
     *
     * @param enrollmentId The student's enrollment ID.
     * @param finalGrade The final grade (e.g., "A-").
     * @return true on success, false on failure.
     */
    public boolean saveFinalGrade(int enrollmentId, String finalGrade) {
        // This query finds an existing "Final Grade" row and updates it,
        // or inserts a new one if it doesn't exist.
        // updates the 'final grade' if the column 'enrollment_id' and 'component' already exists.
        String sql = "INSERT INTO grades (enrollment_id, component, final_grade) " +
                     "VALUES (?, 'Final Grade', ?) " +
                     "ON DUPLICATE KEY UPDATE final_grade = VALUES(final_grade)";
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // In saveFinalGrade method, replace the stmt.set* calls with:
            stmt.setInt(1, enrollmentId);
            stmt.setString(2, finalGrade);

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0);

        } catch (SQLException e) {
            System.err.println("Database error saving final grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // --- Make sure to add these imports at the top of GradeDAO.java ---
    // import edu.univ.erp.domain.ClassStatistic;
    // (List, ArrayList, SQLException, etc. should already be imported)

    /**
     * Calculates statistics for all grade components in a specific section.
     *
     * @param sectionId The section_id.
     * @return A list of ClassStatistic objects, one for each component.
     */
    public List<ClassStatistic> getSectionStatistics(int sectionId) {
        List<ClassStatistic> stats = new ArrayList<>();
        
        // This query joins grades and enrollments, filters by section,
        // and uses aggregate functions grouped by the grade component.
        String sql = "SELECT " +
                     "  g.component, " +
                     "  AVG(g.score) AS avg_score, " +
                     "  MIN(g.score) AS min_score, " +
                     "  MAX(g.score) AS max_score, " +
                     "  COUNT(g.score) AS count_score " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "WHERE e.section_id = ? " +
                     "  AND e.status = 'Enrolled' " + // Only for enrolled students
                     "  AND g.score IS NOT NULL " +   // Only for graded components
                     "  AND (g.final_grade IS NULL OR g.final_grade = '') " + // Exclude 'Final Grade' rows
                     "GROUP BY g.component " +
                     "ORDER BY g.component";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String component = rs.getString("component");
                    double average = rs.getDouble("avg_score");
                    double min = rs.getDouble("min_score");
                    double max = rs.getDouble("max_score");
                    int count = rs.getInt("count_score");
                    
                    stats.add(new ClassStatistic(component, average, min, max, count));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error calculating statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    /**
     * --- NEW SECURITY METHOD ---
     * Verifies that a specific enrollment record belongs to a section
     * taught by the specified instructor. This prevents an instructor
     * from modifying grades in another instructor's section.
     *
     * @param enrollmentId The enrollment_id to check.
     * @param instructorId The user_id of the instructor.
     * @return true if the instructor owns this enrollment, false otherwise.
     */
    public boolean isEnrollmentOwnedByInstructor(int enrollmentId, int instructorId) {
        // This query joins enrollments to sections to check the instructor_id
        String sql = "SELECT 1 FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "WHERE e.enrollment_id = ? AND s.instructor_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            stmt.setInt(2, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                // If a record is found, rs.next() is true, meaning they own it.
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Security check failed in isEnrollmentOwnedByInstructor: " + e.getMessage());
            return false; // Failsafe
        }
    }
}