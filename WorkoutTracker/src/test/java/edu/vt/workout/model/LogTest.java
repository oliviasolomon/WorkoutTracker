package edu.vt.workout.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Log entity.
 */
public class LogTest {

    @Test
    void testDefaultValuesAfterConstruction() {
        Log log = new Log();

        // By default everything should be null until we set it
        assertNull(log.getId());
        assertNull(log.getWorkoutId());
        assertNull(log.getUserId());
        assertNull(log.getSets());
        assertNull(log.getReps());
        assertNull(log.getWeight());
        assertNull(log.getDate());
        assertNull(log.getWorkout());
    }

    @Test
    void testSettersAndGetters() {
        Log log = new Log();

        Long id = 1L;
        Long workoutId = 10L;
        Long userId = 42L;
        Integer sets = 4;
        Integer reps = 12;
        Double weight = 135.0;
        LocalDateTime date = LocalDateTime.of(2025, 11, 1, 14, 30);

        log.setId(id);
        log.setWorkoutId(workoutId);
        log.setUserId(userId);
        log.setSets(sets);
        log.setReps(reps);
        log.setWeight(weight);
        log.setDate(date);

        assertEquals(id, log.getId());
        assertEquals(workoutId, log.getWorkoutId());
        assertEquals(userId, log.getUserId());
        assertEquals(sets, log.getSets());
        assertEquals(reps, log.getReps());
        assertEquals(weight, log.getWeight());
        assertEquals(date, log.getDate());
    }

    @Test
    void testWorkoutTransientField() {
        Log log = new Log();

        Workout workout = new Workout();
        // we don't care about Workout internals here, just that
        // the reference is stored and returned correctly.
        log.setWorkout(workout);

        assertSame(workout, log.getWorkout());
    }

    @Test
    void testCanUpdateValues() {
        Log log = new Log();

        log.setSets(3);
        log.setReps(8);
        log.setWeight(100.0);

        assertEquals(3, log.getSets());
        assertEquals(8, log.getReps());
        assertEquals(100.0, log.getWeight());

        // update them
        log.setSets(5);
        log.setReps(10);
        log.setWeight(120.0);

        assertEquals(5, log.getSets());
        assertEquals(10, log.getReps());
        assertEquals(120.0, log.getWeight());
    }

    @Test
    void testDateNotModifiedAccidentally() {
        Log log = new Log();
        LocalDateTime date = LocalDateTime.now();

        log.setDate(date);
        assertEquals(date, log.getDate());

        // make sure we can change it intentionally
        LocalDateTime later = date.plusDays(1);
        log.setDate(later);
        assertEquals(later, log.getDate());
    }
}

