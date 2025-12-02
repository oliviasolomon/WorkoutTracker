-- table to store user accounts
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, --unique identifier
    username VARCHAR(50) NOT NULL UNIQUE, -- username
    password VARCHAR(255) NOT NULL -- password
);

-- combined logs table (replaces separate workouts + logs model):
-- stores the performed exercise along with metadata (exercise_name, muscle_group, units, favorite)
CREATE TABLE IF NOT EXISTS logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    muscle_group VARCHAR(100),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sets INT NOT NULL,
    reps INT NOT NULL,
    weight DECIMAL(6,2),
    units VARCHAR(10),
    favorite BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
