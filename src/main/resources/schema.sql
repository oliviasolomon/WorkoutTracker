-- MySQL schema (compatible with your Java code)
CREATE DATABASE IF NOT EXISTS workout_tracker;
USE workout_tracker;

-- Users table (store hashed password)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Exercises table (predefined exercises)
CREATE TABLE IF NOT EXISTS exercises (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    muscle_group VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Workouts table (exercise_name column matches Java model/rowmapper)
CREATE TABLE IF NOT EXISTS workouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    exercise_name VARCHAR(255) NOT NULL,
    sets INT NOT NULL,
    reps INT NOT NULL,
    weight DOUBLE,
    muscle_group VARCHAR(100),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date),
    INDEX idx_exercise_name (exercise_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed exercises (safe upsert for MySQL)
INSERT INTO exercises (name, muscle_group) VALUES
('Bench Press','Chest'),
('Squat','Legs'),
('Deadlift','Back'),
('Overhead Press','Shoulders'),
('Bicep Curl','Arms'),
('Tricep Extension','Arms'),
('Pull-Up','Back'),
('Lat Pulldown','Back'),
('Push-Up','Chest'),
('Lunge','Legs')
ON DUPLICATE KEY UPDATE name = name;
