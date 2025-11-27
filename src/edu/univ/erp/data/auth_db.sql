CREATE DATABASE IF NOT EXISTS auth_db;

USE auth_db;

CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT PRIMARY KEY AUTO_INCREMENT,  -- A unique ID for each user
    username VARCHAR(50) UNIQUE NOT NULL,    -- The username, must be unique
    role VARCHAR(20) NOT NULL,               -- 'student', 'instructor', or 'admin'
    password_hash VARCHAR(255) NOT NULL,     -- The secure hashed password
    status VARCHAR(20) DEFAULT 'active',     -- e.g., 'active', 'locked'
    failed_attempts INT DEFAULT 0,           -- <-- THIS IS THE NEW LINE
    last_login TIMESTAMP NULL                -- The last time they logged in
);
-- unlock user_id 1
UPDATE users_auth SET status='active', failed_attempts=0 WHERE user_id=1;
SELECT * FROM users_auth;
DELETE from users_auth where user_id=10;
USE erp_db;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;

USE auth_db;
DROP TABLE IF EXISTS users_auth;
-- Then re-run your auth_db.sql script to create it fresh