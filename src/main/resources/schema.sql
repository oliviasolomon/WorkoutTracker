-- table to store user accounts
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, --unique identifier
    username VARCHAR(50) NOT NULL UNIQUE, -- username
    password VARCHAR(255) NOT NULL -- password
);

-- table to define workouts
CREATE TABLE IF NOT EXISTS workouts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- unique workout id
    user_id BIGINT NOT NULL, -- links workout to user
    name VARCHAR(100) NOT NULL, -- workout name
    muscle_group VARCHAR(100), -- targeted muscle group
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- logged workout date
    sets INT NOT NULL, -- number of sets
    reps INT NOT NULL, -- number of reps
    weight DECIMAL (6,2), -- weight used
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE -- deletes workout upon account deletion
);

-- table for exercises in a workout
CREATE TABLE IF NOT EXISTS exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- unique exercise id
    workout_id BIGINT NOT NULL, -- links exrecise to a workout
    name VARCHAR(100) NOT NULL, -- exercise name
    sets INT NOT NULL, -- number of sets
    reps INT NOT NULL, -- number of reps
    weight DECIMAL(6,2), -- weight used
    FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE -- deletes exercises upon workout deletion
);

-- table to store workout logs
CREATE TABLE IF NOT EXISTS logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, -- unique log id
  workout_id BIGINT NOT NULL, -- references workout performed
  user_id BIGINT NOT NULL, -- references user who performed workout
  sets INT NOT NULL, -- sets completed
  reps INT NOT NULL, -- reps completed
  weight DECIMAL(6,2), -- weight used
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- date/time of logged workout
  FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE, -- deletes logs upon workout deletion
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE -- deletes logs upon user deletion
);
