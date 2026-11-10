-- Run this script in MySQL Workbench using an administrator connection.
-- Replace the sample password before running it, then use that same password
-- in the SCHOOL_DB_PASSWORD environment variable for the application.

CREATE DATABASE IF NOT EXISTS school_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'school_app'@'127.0.0.1'
    IDENTIFIED BY 'replace-with-a-strong-password';

GRANT ALL PRIVILEGES ON school_management.* TO 'school_app'@'127.0.0.1';

USE school_management;

CREATE TABLE IF NOT EXISTS Teacher (
    teacher_id VARCHAR(13) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    qualification VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,
    teacher_role VARCHAR(30) NOT NULL,
    PRIMARY KEY (teacher_id)
);

CREATE TABLE IF NOT EXISTS Leaner (
    id VARCHAR(13) NOT NULL,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(10) NOT NULL,
    grade INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS StudentMark (
    mark_id INTEGER NOT NULL AUTO_INCREMENT,
    learner_id VARCHAR(13) NOT NULL,
    learner_name VARCHAR(100) NOT NULL,
    subject VARCHAR(60) NOT NULL,
    term VARCHAR(20) NOT NULL,
    mark_obtained DOUBLE NOT NULL,
    total_mark DOUBLE NOT NULL,
    teacher_id VARCHAR(13),
    teacher_comment VARCHAR(255),
    PRIMARY KEY (mark_id),
    CONSTRAINT fk_studentmark_learner FOREIGN KEY (learner_id)
        REFERENCES Leaner(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Attendance (
    attendance_id INTEGER NOT NULL AUTO_INCREMENT,
    learner_id VARCHAR(13) NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_status VARCHAR(10) NOT NULL,
    recorded_by VARCHAR(13) NOT NULL,
    attendance_comment VARCHAR(255),
    PRIMARY KEY (attendance_id),
    UNIQUE KEY uq_attendance_learner_date (learner_id, attendance_date),
    CONSTRAINT fk_attendance_learner FOREIGN KEY (learner_id)
        REFERENCES Leaner(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_attendance_teacher FOREIGN KEY (recorded_by)
        REFERENCES Teacher(teacher_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- The application currently creates missing tables at startup, so this account
-- needs CREATE permission during this setup stage. Tighten privileges after the
-- schema and migration approach have been finalized.
