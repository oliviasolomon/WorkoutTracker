package edu.vt.workout.model;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Tool class for converting an array of logs to a plot with sets, reps, and
 * weight plotted all together. Groups multiple logs that occur on the same
 * calendar day into a single plotted point (day-precision).
 *
 * Averages values per day so the chart produces exactly one tick per day.
 *
 * Author: jbrent + oliviasolomon
 * Version: December 5, 2025
 */
public class LogGraph {

    private final TimeZone zone;

    public LogGraph(TimeZone zone) {
        this.zone = zone;
    }

    /**
     * Build a TimeSeriesCollection where logs are first grouped by LocalDate
     * (calendar day). For each day we compute the average sets, average reps,
     * and average weight and add a single data-point per series at that Day.
     */
    private TimeSeriesCollection makeLogDataset(Log[] logbook) {

        TimeSeries setsSeries = new TimeSeries("Sets");
        TimeSeries repsSeries = new TimeSeries("Reps");
        TimeSeries weightSeries = new TimeSeries("Weight");

        if (logbook == null || logbook.length == 0) {
            TimeSeriesCollection empty = new TimeSeriesCollection(zone);
            empty.addSeries(setsSeries);
            empty.addSeries(repsSeries);
            empty.addSeries(weightSeries);
            return empty;
        }

        // Group logs by LocalDate (tree map to keep chronological order)
        TreeMap<LocalDate, List<Log>> grouped = new TreeMap<>();
        for (Log log : logbook) {
            if (log == null) continue;
            LocalDateTime dt = log.getDate();
            if (dt == null) continue;
            LocalDate d = dt.toLocalDate();
            grouped.computeIfAbsent(d, k -> new ArrayList<>()).add(log);
        }

        // For each day, compute averages and add one point per series
        for (Map.Entry<LocalDate, List<Log>> e : grouped.entrySet()) {
            LocalDate dayKey = e.getKey();
            List<Log> dayLogs = e.getValue();
            if (dayLogs.isEmpty()) continue;

            double sumSets = 0.0;
            double sumReps = 0.0;
            double sumWeight = 0.0;
            int countSets = 0;
            int countReps = 0;
            int countWeight = 0;

            for (Log l : dayLogs) {
                Integer s = l.getSets();
                if (s != null) { sumSets += s; countSets++; }
                Integer r = l.getReps();
                if (r != null) { sumReps += r; countReps++; }
                Double w = l.getWeight();
                if (w != null) { sumWeight += w; countWeight++; }
            }

            // compute averages (fall back to 0 if no values present)
            Double avgSets = countSets > 0 ? (sumSets / countSets) : null;
            Double avgReps = countReps > 0 ? (sumReps / countReps) : null;
            Double avgWeight = countWeight > 0 ? (sumWeight / countWeight) : null;

            // convert LocalDate -> Day (midnight)
            LocalDateTime atMidnight = dayKey.atStartOfDay();
            Day dayPeriod = timeToDay(atMidnight);

            try {
                if (avgSets != null) setsSeries.addOrUpdate(dayPeriod, avgSets);
                if (avgReps != null) repsSeries.addOrUpdate(dayPeriod, avgReps);
                if (avgWeight != null) weightSeries.addOrUpdate(dayPeriod, avgWeight);
            } catch (Exception ex) {
                // skip any day that fails to add (defensive)
            }
        }

        TimeSeriesCollection collection = new TimeSeriesCollection(zone);
        collection.addSeries(setsSeries);
        collection.addSeries(repsSeries);
        collection.addSeries(weightSeries);
        return collection;
    }

    /**
     * Convert LocalDateTime to JFreeChart Day set to midnight in the configured TimeZone.
     */
    public Day timeToDay(LocalDateTime time) {
        int yr = time.getYear();
        int mo = time.getMonthValue(); // 1..12
        int da = time.getDayOfMonth();

        Locale locale = Locale.US;
        Calendar cal = Calendar.getInstance(zone, locale);
        cal.set(Calendar.YEAR, yr);
        cal.set(Calendar.MONTH, mo - 1);
        cal.set(Calendar.DAY_OF_MONTH, da);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new Day(cal.getTime());
    }

    /**
     * Create a BufferedImage of the chart. Date axis is formatted to show days (MM-dd).
     */
    public BufferedImage createChartImage(Log[] logbook) {

        TimeSeriesCollection setRepCol = makeLogDataset(logbook);

        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        if (setRepCol.getSeriesCount() >= 3) {
            weightCol.addSeries(setRepCol.getSeries(2));
            setRepCol.removeSeries(2);
        }

        XYPlot chart = new XYPlot();
        chart.setDataset(0, setRepCol);
        chart.setDataset(1, weightCol);

        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        chart.setRangeAxis(0, new NumberAxis("Sets / Reps"));
        chart.setRangeAxis(1, new NumberAxis("Weight (lbs)"));

        DateAxis dateAxis = new DateAxis("Date");
        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd");
        fmt.setTimeZone(zone);
        dateAxis.setDateFormatOverride(fmt);
        chart.setDomainAxis(dateAxis);

        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        JFreeChart graph = new JFreeChart("Workout Metrics", JFreeChart.DEFAULT_TITLE_FONT, chart, true);
        return graph.createBufferedImage(800, 600);
    }
}
