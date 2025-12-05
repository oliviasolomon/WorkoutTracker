package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.repo.LogRowMapper;
import edu.vt.workout.model.LogGraph;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@RestController
public class MetricsController {

    private final JdbcTemplate jdbcTemplate;
    private final LogRowMapper logRowMapper = new LogRowMapper();

    public MetricsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = "/metrics/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMetricsChart() {
        try {
            Log[] logs = fetchLogsFromDb(50);

            LogGraph graph = new LogGraph(TimeZone.getDefault());
            BufferedImage chartImage = graph.createChartImage(logs);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);

            return ResponseEntity.ok(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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
            // require at least one non-zero metric (sets, reps or weight)
            boolean hasMetric = (l.getSets() != 0) || (l.getReps() != 0) || (l.getWeight() != 0.0);
            if (!hasMetric) continue;
            cleaned.add(l);
        }

        // debug: print counts to console so you can verify behavior
        System.out.println("DEBUG: raw logs = " + raw.size() + ", cleaned logs = " + cleaned.size());

        return cleaned.toArray(new Log[0]);
    }
}
