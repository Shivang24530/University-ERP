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
USE erp_db;
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

USE erp_db;
SELECT * FROM students;
SELECT * FROM instructors;
SELECT * FROM courses;  
SELECT * FROM sections;
SELECT * FROM enrollments;
SELECT * FROM grades;
SELECT * FROM settings;


DELETE FROM sections WHERE section_id = 7;

DELETE FROM enrollments WHERE enrollment_id = 3;

CREATE TABLE IF NOT EXISTS grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT,
    component VARCHAR(50),  -- e.g., 'Midterm', 'Final', 'Assignment 1'
    score FLOAT,
    final_grade VARCHAR(2),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(100)
);

INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_mode', 'false');

UPDATE students SET year = 2025 WHERE year = 1;
DELETE FROM sections where section_id=4;
DELETE FROM sections;

-- delete all rows with grade_id greater than 4.
DELETE FROM grades WHERE grade_id = 4;

DROP DATABASE IF EXISTS erp_db;


SELECT s.section_id, c.code, c.title, s.day_time, s.room, s.capacity, (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status = 'Enrolled') AS enrolled_count  FROM sections s  JOIN courses c ON s.course_id = c.course_id  WHERE s.instructor_id = 2;

