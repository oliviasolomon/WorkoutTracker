# Workout Tracker

A Spring Boot web application for tracking and managing workout sessions with metrics visualization.

## Render URL

https://workouttracker-d5wa.onrender.com

## Project Structure

```
workouttracker/
├── WorkoutTracker/                    # Main Spring Boot project
│   ├── pom.xml                        # Maven configuration (Java 21)
│   ├── Dockerfile                     # Docker build configuration (Temurin 21)
│   ├── src/
│   │   └── main/
│   │       ├── java/edu/vt/workout/
│   │       │   ├── WorkoutTrackerApplication.java
│   │       │   ├── controller/
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── ExerciseController.java
│   │       │   │   ├── HomeController.java
│   │       │   │   ├── LogController.java
│   │       │   │   ├── MetricsController.java
│   │       │   │   ├── UserController.java
│   │       │   │   └── WorkoutController.java
│   │       │   ├── model/
│   │       │   │   ├── User.java
│   │       │   │   ├── Workout.java
│   │       │   │   ├── Log.java
│   │       │   │   ├── Metric.java
│   │       │   │   ├── MetricCalculator.java
│   │       │   │   ├── LogGraph.java
│   │       │   │   ├── MetricGraph.java
│   │       │   │   └── Graph_Example.java
│   │       │   ├── repo/
│   │       │   │   ├── UserRepository.java
│   │       │   │   ├── WorkoutRowMapper.java
│   │       │   │   └── LogRowMapper.java
│   │       │   └── service/
│   │       │       └── UserService.java
│   │       └── resources/
│   │           ├── application.properties
│   │           ├── schema.sql
│   │           └── static/
│   │               ├── index.html
│   │               ├── login.html
│   │               ├── signup.html
│   │               ├── confirmation.html
│   │               ├── tracker.html
│   │               ├── metrics.html
│   │               └── style.css
│   ├── bin/                           # Build artifacts
│   └── target/                        # Compiled classes and JAR
│
├── mock_api.py                        # Mock API server for frontend testing (Flask)
├── README.md                          # This file
└── .gitignore
```

## Features

- **User Authentication**: Sign up and login with username/password
- **Workout Tracking**: Log exercises with sets, reps, and weight
- **Sorting & Filtering**: Sort by exercise, sets, reps, weight, or date; filter by exercise
- **Favorites**: Mark workouts as favorites for quick access
- **Metrics**: View workout metrics with chart visualization
- **Navigation**: Consistent navigation bar across tracker and metrics pages
- **Sign Out**: Logout functionality available in navigation

## Tech Stack

- **Backend**: Spring Boot 3.3.4, Java 21, Spring Data JPA, H2 Database
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Build**: Maven 3.9
- **Containerization**: Docker with Temurin 21 JRE
- **Testing**: Mock API server (Flask) for frontend development

## Getting Started

### Prerequisites

- JDK 21 (Temurin/Adoptium recommended)
- Maven 3.9+
- Docker (optional, for containerized deployment)

### Local Development

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd workouttracker
   ```

2. **Build the project**:
   ```bash
   cd WorkoutTracker
   mvn clean package
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
   
   The app will be available at `http://localhost:8080`

4. **Access pages**:
   - Login/Signup: `http://localhost:8080/login.html` or `/signup.html`
   - Tracker: `http://localhost:8080/tracker.html`
   - Metrics: `http://localhost:8080/metrics.html`

### Frontend Testing with Mock API

If you want to test the frontend without running the full Spring Boot backend:

1. **Install dependencies**:
   ```bash
   pip install flask pillow
   ```

2. **Run the mock API server**:
   ```bash
   python mock_api.py
   ```
   
   The server will be available at `http://localhost:5000`

3. **Access the app**:
   - `http://localhost:5000/tracker.html`
   - `http://localhost:5000/metrics.html`

The mock server provides:
- Authentication endpoints (`/api/auth/signup`, `/api/auth/login`)
- Workout API (`/api/workouts`)
- Exercise list (`/api/exercises`)
- Metrics chart placeholder (`/metrics/chart`)

### Docker Build

```bash
cd WorkoutTracker
docker build -t workout-tracker:latest .
docker run -p 8080:8080 workout-tracker:latest
```

## Recent Updates

- **Java 21 Upgrade**: Updated to Java 21 LTS version
- **Navigation Bar**: Added consistent navigation bar to tracker and metrics pages
- **Logout Button**: Moved logout button to navigation bar for better UX
- **Mock API**: Added Flask-based mock API server for frontend development and testing

## License

See LICENSE file for details
