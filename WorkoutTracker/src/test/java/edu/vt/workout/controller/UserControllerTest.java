package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserRepository userRepo;
    private UserController controller;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        controller = new UserController(userRepo);
    }

    @Test
    void signup_success_createsUser() {
        Map<String, String> body = Map.of(
                "username", "alice",
                "password", "secret"
        );

        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.signup(body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user created", response.getBody());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void signup_missingFields_returnsBadRequest() {
        Map<String, String> body = Collections.singletonMap("username", "alice");

        ResponseEntity<?> response = controller.signup(body);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("username and password required", response.getBody());
        verifyNoInteractions(userRepo);
    }

    @Test
    void signup_existingUsername_returnsConflict() {
        Map<String, String> body = Map.of(
                "username", "alice",
                "password", "secret"
        );

        User existing = new User();
        existing.setUsername("alice");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = controller.signup(body);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("username already exists", response.getBody());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void login_success_returnsOk() {
        Map<String, String> body = Map.of(
                "username", "bob",
                "password", "pw123"
        );

        User user = new User();
        user.setUsername("bob");
        user.setPassword("pw123");
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.login(body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("login success", response.getBody());
    }

    @Test
    void login_invalidPassword_returnsUnauthorized() {
        Map<String, String> body = Map.of(
                "username", "bob",
                "password", "wrong"
        );

        User user = new User();
        user.setUsername("bob");
        user.setPassword("correct");
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.login(body);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("invalid credentials", response.getBody());
    }

    @Test
    void login_userNotFound_returnsUnauthorized() {
        Map<String, String> body = Map.of(
                "username", "ghost",
                "password", "anything"
        );

        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.login(body);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("invalid credentials", response.getBody());
    }

    @Test
    void login_missingFields_returnsBadRequest() {
        Map<String, String> body = Collections.singletonMap("username", "bob");

        ResponseEntity<?> response = controller.login(body);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("username and password required", response.getBody());
        verifyNoInteractions(userRepo);
    }
}

