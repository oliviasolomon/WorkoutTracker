package edu.vt.workout.model;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

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
 */
public class LogGraph {

    private final TimeZone zone;

    // NEW: default constructor (no timezone normalizer)
    public LogGraph() {
        this.zone = TimeZone.getDefault();
    }

    // keep existing constructor for compatibility (optional)
    public LogGraph(TimeZone zone) {
        this.zone = (zone != null) ? zone : TimeZone.getDefault();
    }

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

        TreeMap<LocalDate, List<Log>> grouped = new TreeMap<>();
        for (Log log : logbook) {
            if (log == null) continue;
            LocalDateTime dt = log.getDate();
            if (dt == null) continue;
            LocalDate d = dt.toLocalDate();
            grouped.computeIfAbsent(d, k -> new ArrayList<>()).add(log);
        }

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

            Double avgSets = countSets > 0 ? (sumSets / countSets) : null;
            Double avgReps = countReps > 0 ? (sumReps / countReps) : null;
            Double avgWeight = countWeight > 0 ? (sumWeight / countWeight) : null;

            Day dayPeriod = timeToDay(dayKey.atStartOfDay());

            try {
                if (avgSets != null) setsSeries.addOrUpdate(dayPeriod, avgSets);
                if (avgReps != null) repsSeries.addOrUpdate(dayPeriod, avgReps);
                if (avgWeight != null) weightSeries.addOrUpdate(dayPeriod, avgWeight);
            } catch (Exception ignored) {}
        }

        TimeSeriesCollection collection = new TimeSeriesCollection(zone);
        collection.addSeries(setsSeries);
        collection.addSeries(repsSeries);
        collection.addSeries(weightSeries);
        return collection;
    }

    public Day timeToDay(LocalDateTime time) {
        int yr = time.getYear();
        int mo = time.getMonthValue();
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

        chart.setRenderer(0, new DefaultXYItemRenderer());
        chart.setRenderer(1, new DefaultXYItemRenderer());

        chart.setRangeAxis(0, new NumberAxis("Sets / Reps"));
        chart.setRangeAxis(1, new NumberAxis("Weight (lbs)"));

        DateAxis dateAxis = new DateAxis("Date");
        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd");
        fmt.setTimeZone(zone);
        dateAxis.setDateFormatOverride(fmt);

        DateTickUnit dayUnit = new DateTickUnit(DateTickUnitType.DAY, 1);
        dateAxis.setTickUnit(dayUnit);
        dateAxis.setAutoTickUnitSelection(false);

        long minMillis = Long.MAX_VALUE;
        long maxMillis = Long.MIN_VALUE;

        for (int s = 0; s < setRepCol.getSeriesCount(); s++) {
            TimeSeries ts = setRepCol.getSeries(s);
            for (int i = 0; i < ts.getItemCount(); i++) {
                TimeSeriesDataItem item = ts.getDataItem(i);
                long ms = item.getPeriod().getFirstMillisecond();
                if (ms < minMillis) minMillis = ms;
                if (ms > maxMillis) maxMillis = ms;
            }
        }

        for (int s = 0; s < weightCol.getSeriesCount(); s++) {
            TimeSeries ts = weightCol.getSeries(s);
            for (int i = 0; i < ts.getItemCount(); i++) {
                TimeSeriesDataItem item = ts.getDataItem(i);
                long ms = item.getPeriod().getFirstMillisecond();
                if (ms < minMillis) minMillis = ms;
                if (ms > maxMillis) maxMillis = ms;
            }
        }

        if (minMillis != Long.MAX_VALUE && maxMillis != Long.MIN_VALUE) {
            long halfDay = 12L * 60L * 60L * 1000L;
            dateAxis.setRange(minMillis - halfDay, maxMillis + halfDay);
        }

        dateAxis.setLowerMargin(0.01);
        dateAxis.setUpperMargin(0.01);

        chart.setDomainAxis(dateAxis);

        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        JFreeChart graph = new JFreeChart("Workout Metrics", JFreeChart.DEFAULT_TITLE_FONT, chart, true);
        return graph.createBufferedImage(800, 600);
    }
}
