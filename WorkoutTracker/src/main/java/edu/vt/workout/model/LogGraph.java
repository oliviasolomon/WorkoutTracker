package edu.vt.workout.model;

import org.jfree.chart.ChartUtils;
import java.awt.image.BufferedImage;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.*;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;

// -------------------------------------------------------------------------
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

    private TimeZone zone;

    /**
     * Sole constructor for the graph class, sets the time zone the logbook is
     * stored in. The time zone is required because JFreeChart’s date axis
     * uses Calendar-based operations internally.
     *
     * @param zone
     *            Time zone in which the user resides
     */
    public LogGraph(TimeZone zone) {
        this.zone = zone;
    }


    /**
     * Plots the data stored within the logs of the logbook across time on
     * separate axes; one for sets and reps (low range), and the other for
     * weight completed in the workout.
     *
     * This version displays the JFrame. It is primarily kept for debugging
     * and local visualization. Modern use (web) should call createChartImage().
     *
     * @param logbook
     *            Set of logs that needs to be plotted
     */
    public void logGraphMaker(Log[] logbook) {
        TimeSeriesCollection setRepCol = makeLogDataset(logbook);

        // Create a separate collection for weight values
        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        weightCol.addSeries(setRepCol.getSeries(2));  // extract weight series
        setRepCol.removeSeries(2);                    // remove from set/rep dataset

        // Create chart (adapted from StackOverflow)
        XYPlot chart = new XYPlot();
        chart.setDataset(0, setRepCol);
        chart.setDataset(1, weightCol);

        // Get & set renderers (colors preserved from original)
        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        render0.setSeriesFillPaint(0, Color.BLUE);  // sets
        render0.setSeriesFillPaint(1, Color.RED);   // reps
        render1.setSeriesFillPaint(0, Color.BLACK); // weight
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        // Set axes & map datasets
        chart.setRangeAxis(0, new NumberAxis("Sets / Reps [Unitless]"));
        chart.setRangeAxis(1, new NumberAxis("Weight [lbs]"));
        chart.setDomainAxis(new DateAxis("Date of Completion"));
        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        // Construct final chart object
        JFreeChart graph =
            new JFreeChart("Plot", JFreeChart.DEFAULT_TITLE_FONT, chart, true);

        // Display in a JFrame (local debugging only)
        ChartPanel panel = new ChartPanel(graph);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * Internal helper method for converting the raw logbook entries into a
     * set of three TimeSeries objects: sets, reps, and weight.
     *
     * @param logbook
     *            Array of log entries containing workout information
     * @return
     *            A TimeSeriesCollection containing all three raw data series
     */
    private TimeSeriesCollection makeLogDataset(Log[] logbook) {

        // Initialize individual series
        TimeSeries sets = new TimeSeries("Sets");
        TimeSeries reps = new TimeSeries("Reps");
        TimeSeries weight = new TimeSeries("Weight");

        // Loop through logs and fill the series
        for (int i = 0; i < logbook.length; i++) {
            Log log = logbook[i];

            // Add values indexed by day
            sets.addOrUpdate(timeToDay(log.getDate()), log.getSets());
            reps.addOrUpdate(timeToDay(log.getDate()), log.getReps());
            weight.addOrUpdate(timeToDay(log.getDate()), log.getWeight());
        }

        // Package datasets into a collection
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
     * @param time
     *            LocalDateTime stored in Log objects
     * @return
     *            A Day object representing the same calendar date
     */
    public Day timeToDay(LocalDateTime time) {

        int yr = time.getYear();
        int mo = time.getMonthValue();
        int da = time.getDayOfMonth();

        // locale assumed since LocalDateTime doesn't store it
        Locale locale = Locale.US;
        Calendar cal = Calendar.getInstance(zone, locale);

        // NOTE: Calendar months are 0-indexed → subtract 1
        cal.set(yr, mo - 1, da);

        return new Day(cal.getTime());
    }


    /**
     * Server-side rendering method that produces a BufferedImage of the chart
     * instead of creating a GUI window. This is the version intended for use
     * in your Spring Boot API so graphs can be sent to the front-end.
     *
     * @param logbook
     *            Array of Log objects containing workout session data
     * @return
     *            BufferedImage of the graph rendered at 800x600 resolution
     */
    public BufferedImage createChartImage(Log[] logbook) {

        // Build datasets
        TimeSeriesCollection setRepCol = makeLogDataset(logbook);

        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        weightCol.addSeries(setRepCol.getSeries(2));  // weight series
        setRepCol.removeSeries(2);

        XYPlot chart = new XYPlot();
        chart.setDataset(0, setRepCol);
        chart.setDataset(1, weightCol);

        // Basic renderers (simplified, since this is for static images)
        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        // Axes
        chart.setRangeAxis(0, new NumberAxis("Sets / Reps"));
        chart.setRangeAxis(1, new NumberAxis("Weight (lbs)"));
        chart.setDomainAxis(new DateAxis("Date"));
        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        // Final chart wrapper
        JFreeChart graph = new JFreeChart("Workout Metrics",
                                          JFreeChart.DEFAULT_TITLE_FONT,
                                          chart,
                                          true);

        // Render chart to buffered image (web-safe)
        return graph.createBufferedImage(800, 600);
    }

}
