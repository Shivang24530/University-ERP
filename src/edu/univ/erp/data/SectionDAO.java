package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.domain.UnassignedSection;

public class SectionDAO {

    /**
     * Fetches a list of all sections that do not have an instructor assigned.
     *
     * @return A list of UnassignedSection objects.
     */
    public List<UnassignedSection> getUnassignedSections() {
        List<UnassignedSection> sections = new ArrayList<>();
        
        // Query selects sections where instructor_id is NULL
        String sql = "SELECT " +
                     "  s.section_id, " +
                     "  c.code, " +
                     "  c.title, " +
                     "  s.day_time, " +
                     "  s.semester, " +
                     "  s.year " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE s.instructor_id IS NULL " +
                     "ORDER BY c.code, s.year, s.semester";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sections.add(new UnassignedSection(
                    rs.getInt("section_id"),
                    rs.getString("code"),
                    rs.getString("title"),
                    rs.getString("day_time"),
                    rs.getString("semester"),
                    rs.getInt("year")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching unassigned sections: " + e.getMessage());
            e.printStackTrace();
        }
        return sections;
    }

    /**
     * Assigns an instructor to a section by updating the 'instructor_id' field.
     *
     * @param sectionId The section to be updated.
     * @param instructorId The instructor to assign.
     * @return true on success, false on failure.
     */
    public boolean assignInstructorToSection(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";
        
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);
            
            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected == 1); // Success

        } catch (SQLException e) {
            System.err.println("Database error assigning instructor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}