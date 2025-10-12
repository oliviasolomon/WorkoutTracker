-- workouts table
CREATE TABLE IF NOT EXISTS workouts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  exercise VARCHAR(255) NOT NULL,
  sets INT NOT NULL,
  reps INT NOT NULL,
  weight DOUBLE,
  muscle_group VARCHAR(100),
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- exercises table mapping exercise -> muscle group
CREATE TABLE IF NOT EXISTS exercises (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  muscle_group VARCHAR(100) NOT NULL
);

-- Seed a common exercise list
INSERT INTO exercises (name, muscle_group) VALUES
('Bench Press', 'Chest'),
('Squat', 'Legs'),
('Deadlift', 'Back'),
('Overhead Press', 'Shoulders'),
('Bicep Curl', 'Arms'),
('Tricep Extension', 'Arms'),
('Pull-Up', 'Back'),
('Lat Pulldown', 'Back'),
('Push-Up', 'Chest'),
('Lunge', 'Legs')
ON DUPLICATE KEY UPDATE name = name;
