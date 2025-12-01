package edu.vt.workout.controller;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for ExerciseController without Spring or Mockito.
// We use a fake JdbcTemplate subclass to control the query result.
public class ExerciseControllerTest {

    /**
     * Simple fake JdbcTemplate that returns a fixed list for queryForList.
     */
    private static class FakeJdbcTemplate extends JdbcTemplate {
        private final List<String> result;
        private String lastSql;

        FakeJdbcTemplate(List<String> result) {
            this.result = result;
        }

        public String getLastSql() {
            return lastSql;
        }

        @Override
        public <T> List<T> queryForList(String sql, Class<T> elementType) {
            this.lastSql = sql;
            @SuppressWarnings("unchecked")
            List<T> castResult = (List<T>) result;
            return castResult;
        }
    }

    @Test
    void listExercises_returnsExerciseNamesFromJdbcTemplate() {
        // Arrange: fake DB result
        List<String> fakeExercises = Arrays.asList("Bench Press", "Deadlift", "Squat");
        FakeJdbcTemplate fakeJdbc = new FakeJdbcTemplate(fakeExercises);
        ExerciseController controller = new ExerciseController(fakeJdbc);

        // Act
        List<String> result = controller.listExercises();

        // Assert: same list returned
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(fakeExercises, result);

        // Also verify the SQL used
        assertEquals("SELECT name FROM exercises ORDER BY name ASC", fakeJdbc.getLastSql());
    }

    @Test
    void listExercises_handlesEmptyResultList() {
        // Arrange: no exercises in DB
        FakeJdbcTemplate fakeJdbc = new FakeJdbcTemplate(Collections.emptyList());
        ExerciseController controller = new ExerciseController(fakeJdbc);

        // Act
        List<String> result = controller.listExercises();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty list when DB returns no exercises");
    }
}

