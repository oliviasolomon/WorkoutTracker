package edu.vt.workout.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

// -------------------------------------------------------------------------
/**
 * Storage class for user metrics such as age, height, weight, gender, neck, and
 * waist
 * 
 * @author jbrent22
 * @version Nov 3, 2025
 */
public class Metric
{
    // ~ Fields ................................................................
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary ID of the log
    @Column(name = "user_id", nullable = false)
    private Long userId; // ID of the user whom recorded the workout
    @Column(name = "age")
    private Integer age;
    @Column(name = "height")
    private Double height;
    @Column(name = "weight")
    private Double weight; // Current body weight
    @Column(name = "gender")
    private String gender;
    @Column(name = "neck")
    private Double neck;
    @Column(name = "waist")
    private Double waist;
    @Column(name = "date", nullable = false)
    private LocalDateTime date; // Date at which workout was completed

    // ~ Constructors ..........................................................
    /**
     * Create a new Metric object.
     * 
     * @param age
     *            age of the user
     * @param height
     *            height of the user
     * @param weight
     *            weight of the user
     * @param gender
     *            gender of the user
     * @param neck
     *            neck circumference of the user
     * @param waist
     *            waist circumference of the user
     */
    public Metric(
        Integer age,
        Double height,
        Double weight,
        String gender,
        Double neck,
        Double waist)
    {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.neck = neck;
        this.waist = waist;
        date = LocalDateTime.now();
    }


    // ~Public Methods ........................................................
    // ID
    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    // User ID
    public Long getUserId()
    {
        return userId;
    }


    public void setUserId(Long userId)
    {
        this.userId = userId;
    }


    // Age
    public Integer getAge()
    {
        return age;
    }


    public void setAge(Integer age)
    {
        this.age = age;
    }


    // Height
    public Double getHeight()
    {
        return height;
    }


    public void setHeight(Double height)
    {
        this.height = height;
    }


    // Weight
    public Double getWeight()
    {
        return weight;
    }


    public void setWeight(Double weight)
    {
        this.weight = weight;
    }


    // Gender
    public String getGender()
    {
        return gender;
    }


    public void setGender(String gender)
    {
        this.gender = gender;
    }


    // Neck Size
    public Double getNeck()
    {
        return neck;
    }


    public void setNeck(Double neck)
    {
        this.neck = neck;
    }


    // Waist Size
    public Double getWaist()
    {
        return waist;
    }


    public void setWaist(Double waist)
    {
        this.waist = waist;
    }


    // Date
    public LocalDateTime getdate()
    {
        return date;
    }


    public void setdate(LocalDateTime date)
    {
        this.date = date;
    }

}
