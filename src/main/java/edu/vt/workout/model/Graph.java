package edu.vt.workout.model;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.*;
import org.jfree.chart.ChartPanel;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;

public class Graph
{
    private TimeZone zone;

    public Graph(TimeZone zone)
    {
        this.zone = zone;
    }


    public void graphMaker(Log[] logbook)
    {
        TimeSeriesCollection setRepCol = makeLogDataset(logbook);
        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        weightCol.addSeries(setRepCol.getSeries(2));
        setRepCol.removeSeries(2);

        // Create chart using renderer (obtained from stackOverflow)
        // Link:
        // https://stackoverflow.com/questions/29494440/setting-different-y-axis-for-two-series-with-jfreechart
        XYPlot chart = new XYPlot();
        chart.setDataset(0, setRepCol);
        chart.setDataset(1, weightCol);

        // Get & set renderer
        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        render0.setSeriesFillPaint(0, Color.BLUE);
        render0.setSeriesFillPaint(1, Color.RED);
        render1.setSeriesFillPaint(0, Color.BLACK);
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        // Set Axes & map data
        chart.setRangeAxis(0, new NumberAxis("Sets / Reps [Unitless]"));
        chart.setRangeAxis(1, new NumberAxis("Weight [lbs]"));
        chart.setDomainAxis(new DateAxis("Date of Completion"));
        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        // Finalization
        JFreeChart graph =
            new JFreeChart("Plot", JFreeChart.DEFAULT_TITLE_FONT, chart, true);
        ChartPanel panel = new ChartPanel(graph);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    private TimeSeriesCollection makeLogDataset(Log[] logbook)
    {
        // Initialize individual series
        TimeSeries sets = new TimeSeries("Sets");
        TimeSeries reps = new TimeSeries("Reps");
        TimeSeries weight = new TimeSeries("Weight");
        Log log;

        // Fill series with logbook data
        for (int i = 0; i < logbook.length; i++)
        {
            log = logbook[i];
            sets.addOrUpdate(timeToDay(log.getDate()), log.getSets());
            reps.addOrUpdate(timeToDay(log.getDate()), log.getReps());
            weight.addOrUpdate(timeToDay(log.getDate()), log.getWeight());
        }

        // Add series to collection
        TimeSeriesCollection collection = new TimeSeriesCollection(zone);
        collection.addSeries(sets);
        collection.addSeries(reps);
        collection.addSeries(weight);
        return collection;
    }

    // private void makeBMIDataset() { }


    // ----------------------------------------------------------
    /**
     * Adapter method that converts from LocalDateTime class to
     * RegularTImePeriod class
     * 
     * @param time
     *            Variable stored in "Log" class
     * @return RegularTimePeriod that's at the same instant as time
     */
    public Day timeToDay(LocalDateTime time)
    {
        // Strip vars from time
        int yr = time.getYear();
        int mo = time.getMonthValue();
        int da = time.getDayOfMonth();

        // locale assumed since LocalDateTime doesn't store it
        Locale locale = Locale.US;
        Calendar cal = Calendar.getInstance(zone, locale);
        cal.set(yr, mo, da);

        Day day = new Day(cal.getTime());
        return day;
    }

}
