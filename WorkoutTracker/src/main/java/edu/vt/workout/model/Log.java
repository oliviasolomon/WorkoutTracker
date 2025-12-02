package edu.vt.workout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// -------------------------------------------------------------------------
/**
 * Combined Log + Workout model. Each record represents a performed exercise
 * with both metadata (exercise_name, muscle_group, units, favorite) and
 * the recorded instance (sets, reps, weight, date).
 *
 * Maps to the "logs" table in the database.
 *
 * @authors oliviasolomon + jbrent22
 * @version 12/2/2025
 */
@Entity
@Table(name = "logs")
public class Log {
    // ~ Fields ................................................................
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary ID of the log

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID of the user who recorded the workout

    @Column(name = "exercise_name", nullable = false)
    private String exerciseName;

    @Column(name = "muscle_group")
    private String muscleGroup;

    @Column(name = "date", nullable = false)
    private LocalDateTime date; // Date/time of this log

    @Column(name = "sets", nullable = false)
    private Integer sets;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "units")
    private String units;

    @Column(name = "favorite", nullable = false)
    private Boolean favorite = Boolean.FALSE;

    // ~ Getters & Setters .....................................................
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    public Boolean getFavorite() { return favorite; }
    public void setFavorite(Boolean favorite) { this.favorite = favorite; }
}
