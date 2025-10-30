package edu.vt.workout.model;

// Drawn from an online example:
// https://codingtechroom.com/question/plot-graphs-in-java
// https://codingtechroom.com/question/why-is-my-jfreechart-program-running-without-displaying-the-window

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartPanel;
import javax.swing.*;

// -------------------------------------------------------------------------
/**
 * Working example for plotting simple bar graph
 * 
 * @author jbrent22
 * @version Oct 28, 2025
 */
public class Graph_Example
{
    // ~Public Methods ........................................................
    /**
     * Main method that shows an example of how to create a bar graph using
     * jfree chart
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // Create & Fill dataset with dummy values
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "Series1", "Category1");
        dataset.addValue(2.0, "Series1", "Category2");
        dataset.addValue(3.0, "Series1", "Category3");

        // Create bar graph object and stores it in larger ChartPanel object
        JFreeChart bar = ChartFactory
            .createBarChart("Example", "Category", "Value", dataset);
        ChartPanel display = new ChartPanel(bar);

        // Creates display window and puts all the components together
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(display);
        frame.pack();
        frame.setVisible(true);

    }

}
