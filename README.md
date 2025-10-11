# Workout Tracker 

Project Structure
workout-tracker/
├── Dockerfile
├── .dockerignore
├── .gitignore
├── .gitlab-ci.yml
├── README.md
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/vt/workout/
│   │   │       ├── WorkoutTrackerApplication.java       # main app
│   │   │       ├── controller/
│   │   │       │   ├── ExerciseController.java         # handles /api/exercises
│   │   │       │   └── WorkoutController.java          # handles /api/workouts
│   │   │       └── model/
│   │   │           └── Workout.java                    # workout entity/model
|   |   |           └── WorkoutRowMapper.java           #
│   │   └── resources/
│   │       ├── application.properties                  # spring boot config
│   │       └── static/
│   │           ├── tracker.html                        # frontend html
│   │           ├── style.css                           # CSS
│   │           └── index.html                          # frontend html
