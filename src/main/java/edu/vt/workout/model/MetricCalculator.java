package edu.vt.workout.model;

// -------------------------------------------------------------------------
/**
 *  Class for calculating different body metrics
 * 
 *  @author jbrent22
 *  @version Nov 3, 2025
 */
public class MetricCalculator
{
    // ~Public Methods ........................................................
    // ----------------------------------------------------------
    /**
     * Calculates BMI based off of height and weight using the formula found
     * here: https://www.calculator.net/bmi-calculator.html
     * 
     * @param height
     *            in inches
     * @param weight
     *            in pounds
     * @return double of BMI
     */
    public double calculateBMI(double height, double weight)
    {
        return 703 * weight / Math.pow(height, 2);
    }


    // ----------------------------------------------------------
    /**
     * Calculates fat percentage based on metrics listed below. Hip measurements
     * are only used when sex = "female". All units are in pounds or inches
     * 
     * @param height
     * @param weight
     * @param sex
     * @param neck
     * @param waist
     * @param hips
     * @return double of percentage body fat (between 0 and 100)
     */
    public double calculateFatPercent(
        double height,
        double weight,
        String sex,
        double neck,
        double waist,
        Double hips)
    {
        // Checks sex, and chooses equation accordingly. If different sex
        // specified, averages the two equations
        if (sex.equalsIgnoreCase("male"))
        {
            return fatPercentMale(height, weight, neck, waist);
        }
        else if (sex.equalsIgnoreCase("female"))
        {
            return fatPercentFemale(height, weight, neck, waist, hips);
        }
        else
        {
            double fpm = fatPercentMale(height, weight, neck, waist);
            double fpf = fatPercentFemale(height, weight, neck, waist, hips);
            return (fpm + fpf) / 2;
        }
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * 
     * @param height
     * @param weight
     * @param sex
     * @param neck
     * @param waist
     * @param hips
     * @return double pounds mass of everything except fat
     */
    public double leanBodyMass(
        double height,
        double weight,
        String sex,
        double neck,
        double waist,
        Double hips)
    {
        double percentLean =
            1 - (calculateFatPercent(height, weight, sex, neck, waist, hips)
                / 100);
        return percentLean * weight;
    }


    // Used the bfp formula for males in USC units found here:
    // https://www.calculator.net/body-fat-calculator.html
    private
        double
        fatPercentMale(double height, double weight, double neck, double waist)
    {
        double term1 = 86.010 * Math.log10(waist - neck);
        double term2 = 70.041 * Math.log10(height);
        return term1 - term2 + 36.76;
    }


    // Used the bfp formula for females in USC units found here:
    // https://www.calculator.net/body-fat-calculator.html
    private double fatPercentFemale(
        double height,
        double weight,
        double neck,
        double waist,
        double hips)
    {
        double term1 = 163.205 * Math.log10(waist + hips - neck);
        double term2 = 97.684 * Math.log10(height);
        return term1 - term2 - 78.387;
    }
}
