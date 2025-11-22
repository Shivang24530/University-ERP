package edu.univ.erp.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import edu.univ.erp.data.DatabaseConnector; 
import java.sql.Timestamp; // <-- IMPORT ADDED
import java.util.Date; // <-- IMPORT ADDED
// STRAY 'i' CHARACTER REMOVED FROM HERE

public class AuthService {

    // ADDED CONSTANT BASED ON PROJECT BRIEF
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * Attempts to log a user in by checking their username and password.
     */
    public String login(String username, String password) {
        String storedHash = null;
        String userRole = null;
        int userId = -1; 
        String usernameForSession = null; 
        String status = null;
        int failedAttempts = 0;

        // SQL now selects the new columns
        String sql = "SELECT user_id, username, password_hash, role, status, failed_attempts " +
                     "FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                    usernameForSession = rs.getString("username");
                    storedHash = rs.getString("password_hash");
                    userRole = rs.getString("role");
                    status = rs.getString("status");
                    failedAttempts = rs.getInt("failed_attempts");
                } else {
                    return "Login Error: Invalid username or password.";                
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Login Error: Database connection failed."; 
        }

        // --- NEW SECURITY LOGIC ---

        // 1. Check if account is already locked
        if ("locked".equalsIgnoreCase(status)) {
            return "Login Error: This account is locked due to too many failed attempts.";
        }

        if (storedHash == null || storedHash.isEmpty()) {
            return "Login Error: Password hash is empty."; 
        }

        // 2. Check password
        if (BCrypt.checkpw(password, storedHash)) {
            // SUCCESS! Create the session and reset failure count
            UserSession.createSession(userId, usernameForSession, userRole);
            
            // Reset failure count on successful login
            if (failedAttempts > 0) {
                resetFailedAttempts(username);
            }
            // (Optional) Update last_login timestamp
            updateLastLogin(username);

            return userRole; 
        } else {
            // FAILURE! Increment failure count and lock if needed
            int newAttemptCount = failedAttempts + 1;
            boolean lockAccount = (newAttemptCount >= MAX_LOGIN_ATTEMPTS); // <-- NOW WORKS
            
            incrementFailedAttempts(username, lockAccount);

            if (lockAccount) {
                return "Login Error: Incorrect password. Your account has been locked.";
            } else {
                return "Login Error: Invalid username or password.";            
            }
        }
    }

    // --- NEW HELPER METHODS ---

    /** Helper to increment failed attempts and optionally lock the account */
    private void incrementFailedAttempts(String username, boolean lockAccount) {
        String statusUpdate = lockAccount ? ", status = 'locked'" : "";
        String sql = "UPDATE users_auth SET failed_attempts = failed_attempts + 1" + statusUpdate + 
                     " WHERE username = ?";
        
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }

    /** Helper to reset failed attempts on successful login */
    private void resetFailedAttempts(String username) {
        String sql = "UPDATE users_auth SET failed_attempts = 0 WHERE username = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }

    /** Helper to update the last_login timestamp */
    private void updateLastLogin(String username) {
        String sql = "UPDATE users_auth SET last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }

    // --- (register and changePassword methods are unchanged) ---
    /**
     * Registers a new user in the database.
     */
    public boolean register(String username, String password, String role) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String sql = "INSERT INTO users_auth (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected == 1); 

        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Changes a user's password after verifying their old one.
     * @param username The user's username.
     * @param oldPassword The user's current password.
     * @param newPassword The new password to set.
     * @return null on success, or an error message on failure.
     */
    public String changePassword(String username, String oldPassword, String newPassword) {
        String storedHash = null;

        // 1. First, get the user's current stored hash
        String selectSql = "SELECT password_hash FROM users_auth WHERE username = ?";
        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    storedHash = rs.getString("password_hash");
                } else {
                    return "Error: User not found.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Database connection failed.";
        }

        // 2. Verify the old password
        if (storedHash == null || !BCrypt.checkpw(oldPassword, storedHash)) {
            return "Error: Old password was incorrect.";
        }

        // 3. If correct, update with the new password
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        String updateSql = "UPDATE users_auth SET password_hash = ? WHERE username = ?";

        try (Connection conn = DatabaseConnector.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, newHashedPassword);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                return null; // Success!
            } else {
                return "Error: Password update failed in database.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Database update failed.";
        }
    }
}