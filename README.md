# Workout Tracker (Spring Boot)

Minimal Spring Boot project serving static frontend under `/` and API under `/api/workouts`.

Important files:
- `pom.xml` - Maven build
- `src/main/java/...` - Java sources
- `src/main/resources/static` - index.html, tracker.html, style.css
- `.gitlab-ci.yml` - optional CI to trigger Render deploy (set RENDER_API_KEY & RENDER_SERVICE_ID in GitLab CI variables)

Local run:
1. mvn -B -DskipTests package
2. java -jar target/*.jar
3. Visit: http://localhost:5000/tracker.html

Notes:
- Do not commit `data/` (H2 DB files) or `target/`.
- For GitLab Pages: static files are in `src/main/resources/static` but Pages only serves static; deploy backend separately.
