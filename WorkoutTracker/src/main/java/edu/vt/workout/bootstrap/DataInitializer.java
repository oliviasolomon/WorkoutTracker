package edu.vt.workout.bootstrap;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Simple DB initializer: if logs table is empty, insert a few valid test rows.
 */
@Component
public class DataInitializer {

    private final JdbcTemplate jdbc;

    public DataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedIfEmpty() {
        try {
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM logs", Integer.class);
            if (count == null || count > 0) {
                System.out.println("DataInitializer: logs count = " + count + " (no seeding)");
                return;
            }
        } catch (Exception ex) {
            System.out.println("DataInitializer: cannot read logs table (will skip seeding). detail: " + ex.getMessage());
            return;
        }

        try {
            System.out.println("DataInitializer: logs empty — inserting test rows");
            String sql = "INSERT INTO logs (user_id, exercise_name, muscle_group, date, sets, reps, weight, units, favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, 1, "Seed Bench", "chest", Timestamp.valueOf(LocalDateTime.now().minusDays(2)), 3, 8, 135.0, "lbs", false);
            jdbc.update(sql, 1, "Seed Squat", "legs", Timestamp.valueOf(LocalDateTime.now().minusDays(1)), 4, 6, 185.0, "lbs", false);
            jdbc.update(sql, 1, "Seed Press", "shoulders", Timestamp.valueOf(LocalDateTime.now()), 3, 10, 75.0, "lbs", false);
            System.out.println("DataInitializer: inserted seed rows");
        } catch (Exception e) {
            System.out.println("DataInitializer: seeding failed: " + e.getMessage());
        }
    }
}
