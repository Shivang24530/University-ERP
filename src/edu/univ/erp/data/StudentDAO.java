package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.univ.erp.domain.StudentProfile; // We need this to return the profile object

/**
 * Data Access Object for Student-related database operations.
 */
public class StudentDAO {

    /**
     * Fetches a single student's profile by their user_id.
     * This query joins with the auth_db to get the student's name.
     *
     * @param studentId The user_id of the student.
     * @return a StudentProfile object, or null if not found.
     */
    public StudentProfile getStudentProfile(int studentId) {
        StudentProfile profile = null;
        
        // This SQL query joins the students table (in erp_db)
        // with the users_auth table (in auth_db) to get all profile info.
        String sql = "SELECT " +
                     "  a.username, " +
                     "  s.roll_no, " +
                     "  s.program, " +
                     "  s.year " +
                     "FROM students s " +
                     "JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                     "WHERE s.user_id = ?";

        try (Connection conn = DatabaseConnector.getErpConnection(); // Use erp_db connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Read the data from the result set
                    String username = rs.getString("username");
                    String rollNo = rs.getString("roll_no");
                    String program = rs.getString("program");
                    int year = rs.getInt("year");
                    
                    // Create the profile object
                    profile = new StudentProfile(username, rollNo, program, year);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching student profile: " + e.getMessage());
            e.printStackTrace();
        }
        
        return profile;
    }
}