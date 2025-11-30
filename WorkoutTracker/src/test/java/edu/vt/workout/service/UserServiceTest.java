package edu.vt.workout.service;

import edu.vt.workout.model.User;
import edu.vt.workout.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final UserRepository repo = Mockito.mock(UserRepository.class);
    private final UserService service = new UserService(repo);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void testRegisterCreatesUserWithHashedPassword() {
        String username = "testuser";
        String rawPassword = "mypassword";

        // repository save should return the stored user
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = service.register(username, rawPassword);

        assertNotNull(created);
        assertEquals(username, created.getUsername());
        assertNotNull(created.getPassword());
        assertNotEquals(rawPassword, created.getPassword());  // password must be hashed
        assertTrue(created.getPassword().startsWith("$2"), "BCrypt hashes start with $2");
    }

    @Test
    public void testAuthenticateSuccess() {
        String username = "john";
        String rawPassword = "secret";

        User stored = new User();
        stored.setUsername(username);
        stored.setPassword(encoder.encode(rawPassword)); // stored hash

        when(repo.findByUsername(username)).thenReturn(Optional.of(stored));

        boolean result = service.authenticate(username, rawPassword);

        assertTrue(result, "Should authenticate when password matches");
    }

    @Test
    public void testAuthenticateFailsWrongPassword() {
        String username = "john";
        String rawPassword = "secret";

        User stored = new User();
        stored.setUsername(username);
        stored.setPassword(encoder.encode(rawPassword));

        when(repo.findByUsername(username)).thenReturn(Optional.of(stored));

        boolean result = service.authenticate(username, "wrongpass");

        assertFalse(result, "Authentication should fail with wrong password");
    }

    @Test
    public void testAuthenticateFailsUserNotFound() {
        when(repo.findByUsername("ghost")).thenReturn(Optional.empty());

        boolean result = service.authenticate("ghost", "anything");

        assertFalse(result, "Authentication must fail if user does not exist");
    }
}
