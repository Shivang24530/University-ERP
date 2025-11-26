package edu.univ.erp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List; 
import org.mindrot.jbcrypt.BCrypt;

import edu.univ.erp.data.ConfigLoader;
import edu.univ.erp.data.DatabaseConnector;
import edu.univ.erp.data.InstructorQuery; 
import edu.univ.erp.data.SectionQuery; 
import edu.univ.erp.data.SettingsQuery; // <-- IMPORT ADDED
import edu.univ.erp.domain.InstructorProfile; 
import edu.univ.erp.domain.UnassignedSection; 
import java.io.File; // <-- IMPORT ADDED
import java.io.IOException; // <-- IMPORT ADDED
import java.util.Map; // <-- IMPORT ADDED

// This is the "brain" for the Admin
public class adminService {
    
    private SectionQuery sectionDAO;
    private InstructorQuery instructorDAO;
    private SettingsQuery settingsDAO; // <-- ADDED

    public adminService() {
        this.sectionDAO = new SectionQuery();
        this.instructorDAO = new InstructorQuery();
        this.settingsDAO = new SettingsQuery(); // <-- ADDED
    }
    
    // --- NEW METHODS FOR MAINTENANCE MODE ---

    /**
     * --- NEW METHOD ---
     * Checks the current status of maintenance mode.
     * @return true if ON, false if OFF.
     */
    public boolean isMaintenanceModeOn() {
        return settingsDAO.isMaintenanceModeOn();
    }

    /**
     * --- NEW METHOD ---
     * Sets the maintenance mode status.
     * @param isOn true to turn ON, false to turn OFF.
     * @return A status message.
     */
    public String setMaintenanceMode(boolean isOn) {
        boolean success = settingsDAO.setMaintenanceMode(isOn);
        if (success) {
            return "Maintenance mode is now " + (isOn ? "ON" : "OFF");
        } else {
            return "Error: Failed to update maintenance mode status.";
        }
    }

    // --- User Management (Unchanged) ---
    public String createNewUser(String username, String password, String role, String profileData) {
        Connection authConn = null, erpConn = null;
        int newUserId = -1;

        // --- Step 1: Create user in Auth DB ---
        try {
            authConn = DatabaseConnector.getAuthConnection();
            authConn.setAutoCommit(false);

            String authSql = "INSERT INTO users_auth (username, password_hash, role) VALUES (?, ?, ?)";
            try (PreparedStatement authStmt = authConn.prepareStatement(authSql, Statement.RETURN_GENERATED_KEYS)) {
                authStmt.setString(1, username);
                authStmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(12)));
                authStmt.setString(3, role);
                if (authStmt.executeUpdate() == 0) {
                    throw new SQLException("Creating user failed, no rows affected in auth_db.");
                }
                try (ResultSet generatedKeys = authStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newUserId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained from auth_db.");
                    }
                }
            }
            authConn.commit(); // Commit the first transaction immediately
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (authConn != null) authConn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            if (e.getMessage().contains("Duplicate entry")) return "Error: Username '" + username + "' already exists.";
            return "Error creating auth user: " + e.getMessage();
        } finally {
            try { if (authConn != null) { authConn.setAutoCommit(true); authConn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }

        if (newUserId == -1) {
            return "Error: Failed to create user in authentication system.";
        }

        // --- Step 2: Create profile in ERP DB ---
        try {
            erpConn = DatabaseConnector.getErpConnection();
            erpConn.setAutoCommit(false);

            if ("student".equals(role)) {
                String erpSql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
                try (PreparedStatement erpStmt = erpConn.prepareStatement(erpSql)) {
                    erpStmt.setInt(1, newUserId);
                    erpStmt.setString(2, profileData);
                    erpStmt.setString(3, "B.Tech");
                    erpStmt.setInt(4, 1);
                    if (erpStmt.executeUpdate() == 0) throw new SQLException("Creating student profile failed.");
                }
            } else if ("instructor".equals(role)) {
                String erpSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
                try (PreparedStatement erpStmt = erpConn.prepareStatement(erpSql)) {
                    erpStmt.setInt(1, newUserId);
                    erpStmt.setString(2, profileData);
                    if (erpStmt.executeUpdate() == 0) throw new SQLException("Creating instructor profile failed.");
                }
            } else {
                 throw new SQLException("Invalid role specified.");
            }

            erpConn.commit(); // Commit the second transaction
            return null; // Success
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (erpConn != null) erpConn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // Implement a cleanup mechanism to delete the user from auth_db if this part fails.
            return "Error creating ERP profile: " + e.getMessage() + ". User auth record was created but profile failed.";
        } finally {
            try { if (erpConn != null) { erpConn.setAutoCommit(true); erpConn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // --- Course Management  ---
    public String createNewCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getErpConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code); stmt.setString(2, title); stmt.setInt(3, credits);
            if (stmt.executeUpdate() == 1) return null; 
            else return "Error: Course creation failed.";
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) return "Error: Course code '" + code + "' already exists.";
            e.printStackTrace(); return "Error: " + e.getMessage();
        }
    }
    public String createNewSection(int courseId, String dayTime, String room, int capacity, String semester, int year) {
        String sql = "INSERT INTO sections (course_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getErpConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId); stmt.setString(2, dayTime); stmt.setString(3, room);
            stmt.setInt(4, capacity); stmt.setString(5, semester); stmt.setInt(6, year);
            if (stmt.executeUpdate() == 1) return null; 
            else return "Error: Section creation failed.";
        } catch (SQLException e) {
            e.printStackTrace(); return "Error: " + e.getMessage();
        }
    }
    
    // --- Assignment Management  ---
    public List<UnassignedSection> getUnassignedSections() {
        return sectionDAO.getUnassignedSections();
    }
    public List<InstructorProfile> getAllInstructors() {
        return instructorDAO.getAllInstructors();
    }
    public String assignInstructor(int sectionId, int instructorId) {
        if (sectionDAO.assignInstructorToSection(sectionId, instructorId)) return null; 
        else return "Error: Database update failed.";
    }
    // ... (inside adminService class)

    // --- Database Backup / Restore Methods ---

    /**
     * --- NEW METHOD ---
     * Gets the database credentials from the DatabaseConnector file.
     * We need this so we don't hardcode passwords in this file.
     * (This is a simplified approach)
     */
    private Map<String, String> getDbCredentials() {
        // Now reads credentials safely
        return Map.of(
            "DB_USER", ConfigLoader.getProperty("DB_USER"),
            "DB_PASS", ConfigLoader.getProperty("DB_PASS"),
            "DB_HOST", ConfigLoader.getProperty("DB_HOST"),
            "DB_PORT", ConfigLoader.getProperty("DB_PORT"),
            "DB_NAME", ConfigLoader.getProperty("ERP_DB_NAME")
        );
    }

    /**
     * --- NEW METHOD ---
     * Backs up the erp_db database using the 'mysqldump' command-line tool.
     *
     * @param backupFile The file to save the backup to.
     * @return null on success, or an error message on failure.
     */
    public String backupDatabase(File backupFile) {
        Map<String, String> creds = getDbCredentials();
        String dbName = creds.get("DB_NAME");
        String dbUser = creds.get("DB_USER");
        String dbPass = creds.get("DB_PASS");

        // Build the command: mysqldump -u root -p"mdo)&&*" erp_db -r "path/to/backup.sql"
        ProcessBuilder pb = new ProcessBuilder(
            "mysqldump",
            "--user=" + dbUser,
            // The --password argument is removed
            dbName,
            "--result-file=" + backupFile.getAbsolutePath()
        );
        // This line securely provides the password via an environment variable
        pb.environment().put("MYSQL_PWD", dbPass);

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return null; // Success
            } else {
                // Get error output
                String error = new String(process.getErrorStream().readAllBytes());
                return "Backup Failed (Exit Code " + exitCode + "):\n" + error;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error starting process: " + e.getMessage();
        }
    }

    /**
     * --- NEW METHOD ---
     * Restores the erp_db from a .sql backup file using 'mysql'.
     *
     * @param backupFile The .sql file to restore from.
     * @return null on success, or an error message on failure.
     */
    public String restoreDatabase(File backupFile) {
        Map<String, String> creds = getDbCredentials();
        String dbName = creds.get("DB_NAME");
        String dbUser = creds.get("DB_USER");
        String dbPass = creds.get("DB_PASS");

        // Build the command: mysql -u root -p"mdo)&&*" erp_db < "path/to/backup.sql"
        ProcessBuilder pb = new ProcessBuilder(
            "mysql",
            "--user=" + dbUser,
            // The --password argument is removed
            dbName
        );
        // This line securely provides the password
        pb.environment().put("MYSQL_PWD", dbPass);
        
        // This is how we redirect the file < input to the command
        pb.redirectInput(backupFile);

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return null; // Success
            } else {
                String error = new String(process.getErrorStream().readAllBytes());
                return "Restore Failed (Exit Code " + exitCode + "):\n" + error;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error starting process: " + e.getMessage();
        }
    }
}