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
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Tool class for converting an array of logs to a plot with sets, reps, and
 * weight plotted all together. Designed originally for desktop use, later
 * extended to support server-side image generation for web delivery.
 *
 * This class generates a multi-axis time-series plot using JFreeChart, where:
 * - Sets and reps share the same (low-range) y-axis.
 * - Weight is plotted on its own (high-range) y-axis.
 *
 * All data is plotted against workout completion date.
 *
 * @author jbrent22
 * @version Oct 29, 2025
 */
public class LogGraph {

    private final TimeZone zone;

    /**
     * Sole constructor for the graph class, sets the time zone the logbook is
     * stored in. The time zone is required because JFreeChart’s date axis
     * uses Calendar-based operations internally.
     *
     * @param zone Time zone in which the user resides
     */
    public LogGraph(TimeZone zone) {
        this.zone = zone;
    }

    /**
     * Internal helper method for converting the raw logbook entries into a
     * set of three TimeSeries objects: sets, reps, and weight.
     *
     * Defensive: skips null logs and null dates; only adds non-null numeric
     * values so JFreeChart does not receive invalid datapoints.
     *
     * @param logbook Array of log entries containing workout information
     * @return A TimeSeriesCollection containing sets, reps and weight series
     */
    private TimeSeriesCollection makeLogDataset(Log[] logbook) {

        TimeSeries sets = new TimeSeries("Sets");
        TimeSeries reps = new TimeSeries("Reps");
        TimeSeries weight = new TimeSeries("Weight");

        if (logbook == null) {
            TimeSeriesCollection empty = new TimeSeriesCollection(zone);
            empty.addSeries(sets);
            empty.addSeries(reps);
            empty.addSeries(weight);
            return empty;
        }

        for (Log log : logbook) {
            if (log == null) continue;
            LocalDateTime dt = log.getDate();
            if (dt == null) continue;

            Day day;
            try {
                day = timeToDay(dt);
            } catch (Exception ex) {
                continue; // skip malformed dates
            }

            try {
                Integer s = log.getSets();
                if (s != null) sets.addOrUpdate(day, s);

                Integer r = log.getReps();
                if (r != null) reps.addOrUpdate(day, r);

                Double w = log.getWeight();
                if (w != null) weight.addOrUpdate(day, w);
            } catch (Exception ex) {
                // skip problematic datapoints
            }
        }

        TimeSeriesCollection collection = new TimeSeriesCollection(zone);
        collection.addSeries(sets);
        collection.addSeries(reps);
        collection.addSeries(weight);

        return collection;
    }

    /**
     * Adapter method that converts from LocalDateTime to a JFreeChart Day
     * object. JFreeChart's time-series API requires RegularTimePeriod types,
     * not Java time objects, so this wrapper is essential.
     *
     * NOTE: Calendar months are 0-indexed → subtract 1. This method sets the
     * calendar to midnight for the given date so the chart groups by day.
     *
     * @param time LocalDateTime stored in Log objects
     * @return A Day object representing the same calendar date (midnight)
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
     * Server-side rendering method that produces a BufferedImage of the chart
     * instead of creating a GUI window. This is the version intended for use
     * in your Spring Boot API so graphs can be sent to the front-end.
     *
     * This version formats the date axis to show days (MM-dd) using the
     * configured timezone so the chart groups points by day.
     *
     * @param logbook Array of Log objects containing workout session data
     * @return BufferedImage of the graph rendered at 800x600 resolution
     */
    public BufferedImage createChartImage(Log[] logbook) {

        TimeSeriesCollection setRepCol = makeLogDataset(logbook);

        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        if (setRepCol.getSeriesCount() >= 3) {
            weightCol.addSeries(setRepCol.getSeries(2)); // weight series
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

        // Use DateAxis formatted for days (MM-dd) and respect configured TimeZone
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
