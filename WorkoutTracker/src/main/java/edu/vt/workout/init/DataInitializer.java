package edu.vt.workout.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    public DataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        ensureMockUserExists(1L);

        // only seed if there are no logs yet (prevents re-seeding every deploy)
        Integer existing = jdbc.queryForObject("SELECT COUNT(*) FROM logs", Integer.class);
        if (existing != null && existing > 0) return;

        seedLogs(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 12), 1L);
    }

    private void ensureMockUserExists(long userId) {
        Integer users = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (users != null && users > 0) return;

        // create a basic user row so FK(user_id) in logs works
        jdbc.update(
                "INSERT INTO users (id, username, password) VALUES (?, ?, ?)",
                userId,
                "demo",
                "demo"
        );
    }

    private void seedLogs(LocalDate start, LocalDate end, long userId) {
        Random rng = new Random(1201); // deterministic

        List<String[]> exercises = List.of(
                new String[]{"Bench Press", "Chest"},
                new String[]{"Squat", "Legs"},
                new String[]{"Deadlift", "Back"},
                new String[]{"Overhead Press", "Shoulders"},
                new String[]{"Barbell Row", "Back"},
                new String[]{"Incline DB Press", "Chest"},
                new String[]{"Lat Pulldown", "Back"},
                new String[]{"Leg Press", "Legs"}
        );

        String sql =
                "INSERT INTO logs (user_id, exercise_name, muscle_group, date, sets, reps, weight, units, favorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDate d = start;
        while (!d.isAfter(end)) {
            // 1–3 log entries per day
            int entries = 1 + rng.nextInt(3);

            for (int i = 0; i < entries; i++) {
                String[] ex = exercises.get(rng.nextInt(exercises.size()));
                String exerciseName = ex[0];
                String muscleGroup = ex[1];

                int sets = 3 + rng.nextInt(3);        // 3–5
                int reps = 6 + rng.nextInt(7);        // 6–12
                BigDecimal weight = BigDecimal.valueOf(95 + rng.nextInt(160)); // 95–254
                String units = "lb";
                boolean favorite = rng.nextInt(10) == 0; // ~10%

                // spread times within the day (morning/afternoon/evening)
                int hour = 7 + rng.nextInt(12); // 7..18
                int minute = rng.nextInt(60);

                LocalDateTime dt = d.atTime(hour, minute);
                Timestamp ts = Timestamp.valueOf(dt);

                jdbc.update(sql,
                        userId,
                        exerciseName,
                        muscleGroup,
                        ts,
                        sets,
                        reps,
                        weight,
                        units,
                        favorite
                );
            }

            d = d.plusDays(1);
        }
    }
}
