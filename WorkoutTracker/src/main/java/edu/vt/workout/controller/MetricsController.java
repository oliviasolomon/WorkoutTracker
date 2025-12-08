package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.repo.LogRowMapper;
import edu.vt.workout.model.LogGraph;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * MetricsController — serves chart PNG and a debug JSON view of raw DB rows.
 * Defensive: avoids NPEs, ignores rows missing dates, requires at least one
 * positive metric (sets>0 OR reps>0 OR weight>0.0) to include a point.
 */
@RestController
@RequestMapping("/metrics")
@CrossOrigin(origins = {
        "https://workouttracker-d5wa.onrender.com",
        "http://localhost:8080",
        "http://localhost:3000"
})
public class MetricsController {

    private final JdbcTemplate jdbcTemplate;
    private final LogRowMapper logRowMapper = new LogRowMapper();

    public MetricsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = "/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMetricsChart() {
        try {
            // fetch more in case many are filtered
            Log[] logs = fetchLogsFromDb(200);

            // use fixed timezone so chart is stable
            LogGraph graph = new LogGraph(TimeZone.getTimeZone("America/New_York"));
            BufferedImage chartImage = graph.createChartImage(logs);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Debug endpoint returning raw rows directly from JDBC (no RowMapper conversion).
     * Use in browser: /metrics/debug
     */
    @GetMapping(value = "/debug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> debugRows() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM logs ORDER BY date DESC LIMIT 50"
            );
            System.out.println("DEBUG: raw rows = " + rows.size());
            for (int i = 0; i < Math.min(10, rows.size()); i++) {
                System.out.println("DEBUG ROW " + i + " -> " + rows.get(i));
            }
            return ResponseEntity.ok(rows);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
        }
    }

    private Log[] fetchLogsFromDb(int limit) {
        String sql = """
            SELECT id,
                   user_id,
                   exercise_name,
                   muscle_group,
                   date,
                   sets,
                   reps,
                   weight,
                   units,
                   favorite
            FROM logs
            ORDER BY date DESC
            LIMIT ?
        """;

        List<Log> raw = jdbcTemplate.query(sql, new Object[]{limit}, logRowMapper);

        List<Log> cleaned = new ArrayList<>();
        for (Log l : raw) {
            if (l == null) continue;
            // require a valid timestamp
            if (l.getDate() == null) continue;

            // null-safe check for positive metric values
            boolean hasMetric =
                    (l.getSets() != null && l.getSets() > 0) ||
                    (l.getReps() != null && l.getReps() > 0) ||
                    (l.getWeight() != null && l.getWeight() > 0.0);

            if (!hasMetric) continue;

            cleaned.add(l);
        }

        // debug output to application logs
        System.out.println("DEBUG: fetched raw=" + raw.size() + ", cleaned=" + cleaned.size());
        if (!cleaned.isEmpty()) {
            for (int i = 0; i < Math.min(5, cleaned.size()); i++) {
                Log s = cleaned.get(i);
                System.out.println(
                        "DEBUG CLEAN " + i + " -> id:" + s.getId() +
                        " date:" + s.getDate() +
                        " sets:" + s.getSets() +
                        " reps:" + s.getReps() +
                        " weight:" + s.getWeight()
                );
            }
        }

        return cleaned.toArray(new Log[0]);
    }
}
