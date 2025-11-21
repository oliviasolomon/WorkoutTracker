package edu.vt.workout.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.*;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author jbrent22
 * @version Nov 21, 2025
 */
public class MetricGraph
{
    private TimeZone zone;

    /**
     * Sole constructor for the graph class, sets the time zone the metbook is
     * stored in
     * 
     * @param zone
     *            Time zone in which the user resides
     */
    public MetricGraph(TimeZone zone)
    {
        this.zone = zone;
    }


    /**
     * Plots the data stored within the metrics of the metbook across time on
     * separate axes; one for sets and reps (low range), and the other for
     * weight the work out was completed in
     * 
     * @param metbook
     *            Set of metrics that needs to be plotted
     */
    public void metricGraphMaker(Metric[] metbook)
    {
        TimeSeriesCollection BMIFPCol = makeMetricDataset(metbook);
        TimeSeriesCollection LBMCol = new TimeSeriesCollection(zone);
        LBMCol.addSeries(BMIFPCol.getSeries(2));
        BMIFPCol.removeSeries(2);

        // Create chart (obtained from stackOverflow)
        // Link:
        // https://stackoverflow.com/questions/29494440/setting-different-y-axis-for-two-series-with-jfreechart
        XYPlot chart = new XYPlot();
        chart.setDataset(0, BMIFPCol);
        chart.setDataset(1, LBMCol);

        // Get & set renderer
        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        render0.setSeriesFillPaint(0, Color.BLUE);
        render0.setSeriesFillPaint(1, Color.RED);
        render1.setSeriesFillPaint(0, Color.BLACK);
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        // Set Axes & map data to said axes
        chart.setRangeAxis(0, new NumberAxis("BMI and Fat % [Unitless]"));
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


    private TimeSeriesCollection makeMetricDataset(Metric[] metbook)
    {
        // Initialize individual series
        TimeSeries BMI = new TimeSeries("Body Mass Index");
        TimeSeries FP = new TimeSeries("Fat Percent");
        TimeSeries LBM = new TimeSeries("Lean Body Mass");
        Metric met;

        // Initialize running vars
        double height = -1;
        double weight = -1;
        String sex = null;
        double neck = -1;
        double waist = -1;
        Double hips = null;

        // Fill series with metbook data
        for (int i = 0; i < metbook.length; i++)
        {
            met = metbook[i];
            if (met.getsex() != null)
            {
                sex = met.getsex();
            }
            height = check(height, met.getHeight());
            weight = check(weight, met.getWeight());
            neck = check(neck, met.getNeck());
            waist = check(waist, met.getWaist());
            
            if (met.getHips() != null)
            {
                hips = met.getHips();
            }

            if (weight != -1 && height != -1)
            {
                BMI.addOrUpdate(
                    timeToDay(met.getdate()),
                    MetricCalculator.calculateBMI(height, weight));
                if (sex != null && neck != -1 && waist != -1)
                {
                    FP.addOrUpdate(
                        timeToDay(met.getdate()),
                        MetricCalculator.calculateFatPercent(
                            height,
                            weight,
                            sex,
                            neck,
                            waist,
                            hips));
                    LBM.addOrUpdate(
                        timeToDay(met.getdate()),
                        MetricCalculator.leanBodyMass(
                            height,
                            weight,
                            sex,
                            neck,
                            waist,
                            hips));
                }
            }

        }

        // Add series to collection
        TimeSeriesCollection collection = new TimeSeriesCollection(zone);
        collection.addSeries(BMI);
        collection.addSeries(FP);
        collection.addSeries(LBM);
        return collection;
    }


    private double check(double metric, Double check)
    {
        if (check != null)
        {
            return check.doubleValue();
        }
        return -1;
    }


    /**
     * Adapter method that converts from LocalDateTime class to
     * RegularTImePeriod class
     * 
     * @param time
     *            Variable stored in "metric" class
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


    /**
     * This method turns the JFrame into a BufferedImage. Intended for web or
     * API use, where the chart needs to be rendered as an image instead of
     * shown in a window.
     * 
     * @param metbook
     *            An array of metric objects containing workout session data.
     *            Each metric provides the date, sets, reps, and weight for a
     *            workout entry.
     * @return A BufferedImage object containing the rendered chart.
     */
    public BufferedImage createChartImage(Metric[] metbook)
    {
        TimeSeriesCollection setRepCol = makeMetricDataset(metbook);
        TimeSeriesCollection weightCol = new TimeSeriesCollection(zone);
        weightCol.addSeries(setRepCol.getSeries(2));
        setRepCol.removeSeries(2);

        XYPlot chart = new XYPlot();
        chart.setDataset(0, setRepCol);
        chart.setDataset(1, weightCol);

        DefaultXYItemRenderer render0 = new DefaultXYItemRenderer();
        DefaultXYItemRenderer render1 = new DefaultXYItemRenderer();
        chart.setRenderer(0, render0);
        chart.setRenderer(1, render1);

        chart.setRangeAxis(0, new NumberAxis("Sets / Reps"));
        chart.setRangeAxis(1, new NumberAxis("Weight (lbs)"));
        chart.setDomainAxis(new DateAxis("Date"));
        chart.mapDatasetToRangeAxis(0, 0);
        chart.mapDatasetToRangeAxis(1, 1);

        JFreeChart graph = new JFreeChart(
            "Workout Metrics",
            JFreeChart.DEFAULT_TITLE_FONT,
            chart,
            true);

        // Convert chart to image (no frame)
        BufferedImage image = graph.createBufferedImage(800, 600);
        return image;
    }
}
