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
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * MetricsController: load logs from db and optionally by user_id,
 * feeds them into loggraph, and returns PNG. also exposes /metrics/debug 
 * to see the log objects as JSON.
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

    // GET /metrics/chart?user_id=1
    @GetMapping(value = "/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMetricsChart(
            @RequestParam(value = "user_id", required = false) Long userId) {

        try {
            List<Log> logs = loadLogs(userId);
            System.out.println("METRICS: /metrics/chart userId=" + userId +
                    " rows=" + logs.size());

            LogGraph graph = new LogGraph(TimeZone.getTimeZone("America/New_York"));
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

    // GET /metrics/debug?user_id=1
    @GetMapping(value = "/debug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Log>> debugLogs(
            @RequestParam(value = "user_id", required = false) Long userId) {

        List<Log> logs = loadLogs(userId);
        System.out.println("METRICS: /metrics/debug userId=" + userId +
                " rows=" + logs.size());

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

    /**
     * load logs from DB, optionally filtered by user_id, ordered by date ASC.
     * only drops rows where date is null since loggraph requires a date.
     */
    private List<Log> loadLogs(Long userId) {
        String sqlAll =
                "SELECT * FROM logs ORDER BY date ASC";
        String sqlByUser =
                "SELECT * FROM logs WHERE user_id = ? ORDER BY date ASC";

        List<Log> raw;
        if (userId != null) {
            raw = jdbc.query(sqlByUser, new Object[]{userId}, mapper);
        } else {
            raw = jdbc.query(sqlAll, mapper);
        }

        List<Log> filtered = new ArrayList<>();
        for (Log l : raw) {
            if (l == null) continue;
            if (l.getDate() == null) continue; // loggraph needs dates
            filtered.add(l);
        }
        return filtered;
    }
}
