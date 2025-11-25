package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsQuery {

    private static final String MAINTENANCE_KEY = "maintenance_mode";

    /**
     * Checks if maintenance mode is currently active.
     *
     * @return true if maintenance mode is ON, false otherwise.
     */
    public boolean isMaintenanceModeOn() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        
        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, MAINTENANCE_KEY);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // "true" in the database means it's on
                    return "true".equalsIgnoreCase(rs.getString("setting_value"));
                } else {
                    // If the key doesn't exist, assume it's OFF
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error checking maintenance mode: " + e.getMessage());
            e.printStackTrace();
            // Failsafe: If DB fails, assume maintenance is OFF
            return false;
        }
    }

    /**
     * Sets the maintenance mode status.
     * This uses INSERT ... ON DUPLICATE KEY UPDATE to create or update the key.
     *
     * @param isO N true to turn maintenance ON, false to turn it OFF.
     * @return true on success, false on failure.
     */
    public boolean setMaintenanceMode(boolean isOn) {
        String value = isOn ? "true" : "false";
        
        // This query will insert the key if it doesn't exist,
        // or update it if it does.
        String sql = "INSERT INTO settings (setting_key, setting_value) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (Connection conn = DatabaseConnector.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, MAINTENANCE_KEY);
            stmt.setString(2, value);
            
            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0); // 1 = insert, 2 = update

        } catch (SQLException e) {
            System.err.println("Database error setting maintenance mode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}