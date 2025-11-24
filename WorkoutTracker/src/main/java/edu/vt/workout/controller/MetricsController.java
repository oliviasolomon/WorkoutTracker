package edu.vt.workout.controller;

import edu.vt.workout.model.LogGraph;
import edu.vt.workout.model.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.TimeZone;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
public class MetricsController {

    @GetMapping(value = "/metrics/chart", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMetricsChart() {
        try {
            // Generate dummy data (you can later replace this with DB logs)
            Log[] logbook = generateSampleLogs(50);

            // Use Graph to create a chart image in memory
            LogGraph graph = new LogGraph(TimeZone.getDefault());
            BufferedImage chartImage = graph.createChartImage(logbook); // new helper method below

            // Convert BufferedImage -> byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);
            return ResponseEntity.ok(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private Log[] generateSampleLogs(int n) {
        Random rand = new Random();
        Log[] logs = new Log[n];
        for (int i = 0; i < n; i++) {
            Log l = new Log();
            l.setDate(LocalDateTime.now().minusDays(rand.nextInt(30)));
            l.setSets(rand.nextInt(5) + 3);
            l.setReps(rand.nextInt(10) + 5);
            l.setWeight(Double.valueOf(rand.nextInt(100) + 100)); //double expected for weight 
            logs[i] = l;
        }
        return logs;
    }
}
