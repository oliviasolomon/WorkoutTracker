package edu.vt.workout.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutTest {

    @Test
    public void testSettersAndGetters() {
        Workout workout = new Workout();

        workout.setId(10L);
        workout.setUserId(5L);
        workout.setExerciseName("Bench Press");
        workout.setMuscleGroup("Chest");
        workout.setSets(4);
        workout.setReps(8);
        workout.setWeight(185.0);
        workout.setUnits("lbs");

        LocalDateTime now = LocalDateTime.now();
        workout.setDate(now);

        workout.setFavorite(true);

        assertEquals(10L, workout.getId());
        assertEquals(5L, workout.getUserId());
        assertEquals("Bench Press", workout.getExerciseName());
        assertEquals("Chest", workout.getMuscleGroup());
        assertEquals(4, workout.getSets());
        assertEquals(8, workout.getReps());
        assertEquals(185.0, workout.getWeight());
        assertEquals("lbs", workout.getUnits());
        assertEquals(now, workout.getDate());
        assertTrue(workout.getFavorite());
    }

    @Test
    public void testDefaultFavoriteIsFalse() {
        Workout workout = new Workout();
        assertNotNull(workout.getFavorite());
        assertFalse(workout.getFavorite(), "Favorite should default to FALSE");
    }

    @Test
    public void testNullOptionalFields() {
        Workout workout = new Workout();

        workout.setMuscleGroup(null);
        workout.setUnits(null);
        workout.setWeight(null);

        assertNull(workout.getMuscleGroup());
        assertNull(workout.getUnits());
        assertNull(workout.getWeight());
    }
}
