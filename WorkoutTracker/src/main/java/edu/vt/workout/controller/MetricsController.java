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
            Log[] logs = fetchLogsFromDb();
            // use graph to create chart image in memory
            LogGraph graph = new LogGraph(TimeZone.getDefault());
            BufferedImage chartImage = graph.createChartImage(logs);
            // convert buffered image > byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);

            return ResponseEntity.ok(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private Log[] fetchLogsFromDb() {
        String sql = """
            SELECT id, workout_id, user_id, sets, reps, weight, date
            FROM logs
            ORDER BY date DESC
            LIMIT 50
        """;

        List<Log> list = jdbcTemplate.query(sql, logRowMapper);
        return list.toArray(new Log[0]);
    }
}
