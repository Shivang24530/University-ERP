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

-- Use password 'erp'

INSERT INTO users_auth (user_id, username, role, password_hash) 
VALUES (3, 'admin1', 'admin', '$2a$10$V91uvwSiRtKDwaTIO0KFaeYaA8d9Y042sHz2rvud3gI9p5omvzyOi');

INSERT INTO users_auth (user_id, username, role, password_hash)
VALUES (1, 'student1', 'student', '$2a$10$V91uvwSiRtKDwaTIO0KFaeYaA8d9Y042sHz2rvud3gI9p5omvzyOi');

INSERT INTO users_auth (user_id, username, role, password_hash)
VALUES (4, 'student2', 'student', '$2a$10$V91uvwSiRtKDwaTIO0KFaeYaA8d9Y042sHz2rvud3gI9p5omvzyOi');

INSERT INTO users_auth (user_id, username, role, password_hash) 
VALUES (2, 'instructor1', 'instructor', '$2a$10$V91uvwSiRtKDwaTIO0KFaeYaA8d9Y042sHz2rvud3gI9p5omvzyOi');

SELECT * FROM users_auth;

CREATE DATABASE IF NOT EXISTS erp_db;

USE erp_db;

CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,                 -- This MUST match the user_id from auth_db
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100),
    year INT,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) -- Link to the other DB
);

CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,                 -- This also matches the user_id from auth_db
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) -- Link to the other DB
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(100),
    credits INT
);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT,
    instructor_id INT,
    day_time VARCHAR(50),
    room VARCHAR(20),
    capacity INT NOT NULL,
    semester VARCHAR(20),
    year INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

-- adding the CHECK constraint:
ALTER TABLE sections ADD CONSTRAINT chk_section_capacity CHECK (capacity > 0);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    section_id INT,
    student_id INT,
    status VARCHAR(20), -- e.g., 'Enrolled', 'Waitlisted', 'Dropped'
    FOREIGN KEY (section_id) REFERENCES sections(section_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id)
);

CREATE TABLE IF NOT EXISTS grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT,
    component VARCHAR(50) ,
    score FLOAT,
    final_grade VARCHAR(2),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
    UNIQUE KEY uk_enroll_comp (enrollment_id, component)
);

CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(100)
);

INSERT INTO instructors (user_id, department) 
VALUES (2, 'Computer Science');

INSERT INTO students (user_id, roll_no, program, year)
VALUES (1, '2024001', 'Advance Programming', 2025); 
INSERT INTO students (user_id, roll_no, program, year)
VALUES (4, '2024002', 'Maths III', 2025);

INSERT INTO courses (code, title, credits) 
VALUES 
("CSE201", "Advance Programming", 4),
("MTH203", 'Maths III', 4),
("ECE250", 'Signals and Systems', 4);

INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) 
VALUES 
(1, 2, 'Tue/Thu 15:00', 'C102', 250, 'Monsoon', 2025),
(2, 2, 'Tue/Thu 10:00', 'MTH203', 1, 'Monsoon', 2025); 


SELECT * FROM students;
SELECT * FROM instructors;
SELECT * FROM courses;
SELECT * FROM sections;
SELECT * FROM enrollments;
SELECT * FROM grades;
SELECT * FROM settings;