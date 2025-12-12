package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.model.LogGraph;
import edu.vt.workout.repo.LogRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * MetricsController
 * - /metrics/chart  -> PNG chart built from all rows in logs table
 * - /metrics/debug  -> JSON list of Log objects used for the chart
 */
@RestController
@RequestMapping("/metrics")
@CrossOrigin(origins = {
        "https://workouttracker-d5wa.onrender.com",
        "http://localhost:8080",
        "http://localhost:3000"
})
public class MetricsController {

    private final JdbcTemplate jdbc;
    private final LogRowMapper mapper = new LogRowMapper();

    @Autowired
    public MetricsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // GET /metrics/chart
    @GetMapping(value = "/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> chart() {
        try {
            List<Log> logs = loadAllLogs();
            System.out.println("METRICS /metrics/chart rows=" + logs.size());

            LogGraph graph = new LogGraph(); // <-- removed timezone normalizer
            BufferedImage img = graph.createChartImage(logs.toArray(new Log[0]));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /metrics/debug
    @GetMapping(value = "/debug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Log>> debug() {
        List<Log> logs = loadAllLogs();
        System.out.println("METRICS /metrics/debug rows=" + logs.size());
        for (int i = 0; i < Math.min(5, logs.size()); i++) {
            Log l = logs.get(i);
            System.out.println("DEBUG LOG " + i + " -> id=" + l.getId()
                    + " user_id=" + l.getUserId()
                    + " date=" + l.getDate()
                    + " sets=" + l.getSets()
                    + " reps=" + l.getReps()
                    + " weight=" + l.getWeight());
        }
        return ResponseEntity.ok(logs);
    }

    private List<Log> loadAllLogs() {
        String sql = "SELECT * FROM logs ORDER BY date ASC";
        return jdbc.query(sql, mapper);
    }
}
