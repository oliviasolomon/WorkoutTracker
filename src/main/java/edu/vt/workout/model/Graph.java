package edu.vt.workout.model;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import org.jfree.chart.ChartPanel;
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
        TimeSeriesCollection collection = makeLogDataset(logbook);
        JFreeChart setsReps = ChartFactory.createTimeSeriesChart(
            "Sets and Reps",
            "Date",
            "Number",
            collection);
        ChartPanel panel = new ChartPanel(setsReps);
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
            sets.add(timeToPeriod(log.getDate()), log.getSets());
            reps.add(timeToPeriod(log.getDate()), log.getReps());
            weight.add(timeToPeriod(log.getDate()), log.getWeight());
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
    public RegularTimePeriod timeToPeriod(LocalDateTime time)
    {
        // locale assumed since LocalDateTime doesn't store it
        Locale locale = Locale.US;
        Calendar cal = Calendar.getInstance(zone, locale);

        return RegularTimePeriod.createInstance(
            RegularTimePeriod.class,
            cal.getTime(),
            zone,
            locale);
    }

}
