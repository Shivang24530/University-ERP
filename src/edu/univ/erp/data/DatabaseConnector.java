package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {
    private static HikariDataSource AuthDataSource;
    private static HikariDataSource ErpDataSource;

    static {
        try {
            initAuthDatabaseConnectionPool();
            initErpDatabaseConnectionPool();
        } catch (Exception e) {
            System.err.println("FATAL: Failed to initialize database connection pools!");
            e.printStackTrace();  // <--- THIS WILL SHOW THE REAL ERROR
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void initAuthDatabaseConnectionPool() {
        System.out.println("Initializing Auth DB pool...");
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl("jdbc:mysql://localhost:3306/auth_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&allowMultiQueries=true");
        config.setUsername("root");
        config.setPassword("MySQL_Workbench");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");  // VERY IMPORTANT: cj for MySQL 8+

        // Extra fixes for common MySQL 8+ issues
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setMaximumPoolSize(10);

        AuthDataSource = new HikariDataSource(config);
        System.out.println("Auth DB pool initialized successfully!");
    }

    private static void initErpDatabaseConnectionPool() {
        System.out.println("Initializing ERP DB pool...");
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl("jdbc:mysql://localhost:3306/erp_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&allowMultiQueries=true");
        config.setUsername("root");
        config.setPassword("MySQL_Workbench");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(10);

        ErpDataSource = new HikariDataSource(config);
        System.out.println("ERP DB pool initialized successfully!");
    }

    public static Connection getAuthConnection() throws SQLException {
        return AuthDataSource.getConnection();
    }

    public static Connection getErpConnection() throws SQLException {
        return ErpDataSource.getConnection();
    }
}