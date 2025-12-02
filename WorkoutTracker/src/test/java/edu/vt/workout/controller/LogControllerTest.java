package edu.vt.workout.controller;

import edu.vt.workout.model.Log;
import edu.vt.workout.repo.LogRowMapper;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Unit-style tests that call LogController methods directly with a fake JdbcTemplate.
// No real DB, no Spring context.
public class LogControllerTest {

    /**
     * Simple fake JdbcTemplate that lets us control what the controller sees.
     */
    private static class FakeJdbcTemplate extends JdbcTemplate {

        List<Log> logsForAll = new ArrayList<>();
        List<Log> logsForUser = new ArrayList<>();
        Log logForGetOne;
        Log logForCreate;
        Long generatedId = 101L;

        // list(user_id == null)
        @Override
        public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
            // used by list(null)
            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) logsForAll;
            return result;
        }

        // list(user_id != null) and getOne(id)
        @Override
        public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
            if (args != null && args.length == 1) {
                Object arg = args[0];
                if (arg instanceof Integer) {
                    // list(user_id)
                    @SuppressWarnings("unchecked")
                    List<T> result = (List<T>) logsForUser;
                    return result;
                } else if (arg instanceof Long) {
                    // getOne(id)
                    if (logForGetOne == null) {
                        return Collections.emptyList();
                    }
                    @SuppressWarnings("unchecked")
                    List<T> result = (List<T>) Collections.singletonList(logForGetOne);
                    return result;
                }
            }
            return Collections.emptyList();
        }

        // create() – insert row + set generated key
        @Override
        public int update(PreparedStatementCreator psc, KeyHolder keyHolder) {
            // pretend insert succeeded and set generated id
            if (keyHolder instanceof GeneratedKeyHolder gkh) {
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("GENERATED_KEY", generatedId);
                gkh.getKeyList().clear();
                gkh.getKeyList().add(keyMap);
            }
            return 1;
        }

        // create() – select inserted row by id
        @Override
        public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) {
            @SuppressWarnings("unchecked")
            T result = (T) logForCreate;
            return result;
        }

        // avoid actually touching a real Connection
        @Override
        public int update(PreparedStatementCreator psc) {
            return 1;
        }

        @Override
        public int update(String sql, Object... args) {
            return 1;
        }
    }

    private LogController newControllerWithFake(FakeJdbcTemplate fake) {
        return new LogController(fake);
    }

    private Log sampleLog(long id, long userId, String exerciseName) {
        Log l = new Log();
        l.setId(id);
        l.setUserId(userId);
        l.setSets(3);
        l.setReps(10);
        l.setWeight(135.0);
        l.setDate(LocalDateTime.now());
        l.setExerciseName(exerciseName);
        return l;
    }

    @Test
    public void testListAllWithoutUserId() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        Log l1 = sampleLog(1L, 100L, "Bench Press");
        Log l2 = sampleLog(2L, 101L, "Squat");
        fake.logsForAll = Arrays.asList(l1, l2);

        LogController controller = newControllerWithFake(fake);

        List<Log> result = controller.list(null);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testListFilteredByUserId() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        Log l1 = sampleLog(1L, 42L, "Deadlift");
        fake.logsForUser = Collections.singletonList(l1);

        LogController controller = newControllerWithFake(fake);

        List<Log> result = controller.list(42);

        assertEquals(1, result.size());
        assertEquals(42L, result.get(0).getUserId());
    }

    @Test
    public void testGetOneFound() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        Log l = sampleLog(5L, 7L, "Overhead Press");
        fake.logForGetOne = l;

        LogController controller = newControllerWithFake(fake);

        ResponseEntity<Log> resp = controller.getOne(5L);

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(5L, resp.getBody().getId());
    }

    @Test
    public void testGetOneNotFound() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        fake.logForGetOne = null; // will cause query() to return empty list

        LogController controller = newControllerWithFake(fake);

        ResponseEntity<Log> resp = controller.getOne(999L);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    public void testCreateMissingExerciseReturnsBadRequest() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        LogController controller = newControllerWithFake(fake);

        Map<String, Object> body = new HashMap<>();
        body.put("sets", 3);
        body.put("reps", 8);

        ResponseEntity<?> resp = controller.create(body);

        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Map);
        Map<?,?> respBody = (Map<?,?>) resp.getBody();
        assertEquals("invalid input", respBody.get("error"));
    }

    @Test
    public void testCreateSuccess() {
        FakeJdbcTemplate fake = new FakeJdbcTemplate();
        fake.generatedId = 123L;

        Log created = sampleLog(123L, 9L, "Bench Press");
        fake.logForCreate = created;

        LogController controller = newControllerWithFake(fake);

        Map<String, Object> body = new HashMap<>();
        body.put("exercise", "Bench Press");
        body.put("user_id", 9);
        body.put("sets", 4);
        body.put("reps", 12);
        body.put("weight", 155.0);
        body.put("date", "2025-01-01T10:00:00");

        ResponseEntity<?> resp = controller.create(body);

        assertEquals(201, resp.getStatusCodeValue());
        assertTrue(resp.getBody() instanceof Log);
        Log respLog = (Log) resp.getBody();
        assertEquals(123L, respLog.getId());
        assertEquals(9L, respLog.getUserId());
        assertEquals("Bench Press", respLog.getExerciseName());
    }
}
