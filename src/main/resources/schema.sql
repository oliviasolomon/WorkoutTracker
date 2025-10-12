-- Users table for login/signup (store hashed password)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Predefined exercises
CREATE TABLE IF NOT EXISTS exercises (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    muscle_group VARCHAR(100) NOT NULL
);

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
