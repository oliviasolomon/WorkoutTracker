package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.repo.LogRowMapper;
import edu.vt.workout.model.LogGraph;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/api/metrics")
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

    // GET /api/metrics/chart?user_id=1
    @GetMapping(value = "/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMetricsChart(
            @RequestParam(value = "user_id", required = false) Long userId) {
        try {
            // fetch more in case many are filtered out
            Log[] logs = fetchLogsFromDb(200, userId);

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
     * Debug endpoint returning raw rows directly from JDBC.
     */
    @GetMapping(value = "/debug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> debugRows(
            @RequestParam(value = "user_id", required = false) Long userId) {
        try {
            String base = """
                SELECT *
                FROM logs
            """;

            StringBuilder sql = new StringBuilder(base);
            List<Object> params = new ArrayList<>();

            if (userId != null) {
                sql.append(" WHERE user_id = ?");
                params.add(userId);
            }

            sql.append(" ORDER BY date DESC LIMIT 50");

            List<Map<String, Object>> rows =
                    jdbcTemplate.queryForList(sql.toString(), params.toArray());

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

    private Log[] fetchLogsFromDb(int limit, Long userId) {
        String base = """
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
        """;

        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" WHERE user_id = ?");
            params.add(userId);
        }

        sql.append(" ORDER BY date DESC LIMIT ?");
        params.add(limit);

        List<Log> raw = jdbcTemplate.query(sql.toString(), params.toArray(), logRowMapper);

        List<Log> cleaned = new ArrayList<>();
        for (Log l : raw) {
            if (l == null) continue;
            if (l.getDate() == null) continue;

            boolean hasMetric =
                    (l.getSets() != null && l.getSets() > 0) ||
                    (l.getReps() != null && l.getReps() > 0) ||
                    (l.getWeight() != null && l.getWeight() > 0.0);

            if (!hasMetric) continue;

            cleaned.add(l);
        }

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
