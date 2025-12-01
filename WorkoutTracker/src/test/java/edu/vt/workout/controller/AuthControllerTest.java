package edu.vt.workout.controller;

import edu.vt.workout.model.User;
import edu.vt.workout.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Plain unit test: no Spring context, no Mockito
public class AuthControllerTest {

    private MockMvc mockMvc;
    private FakeUserService fakeUserService;

    // Simple fake implementation of UserService for testing
    static class FakeUserService extends UserService {
        boolean authenticateResult = false;

        public FakeUserService() {
            super(null); // we won't use the repository in tests
        }

        @Override
        public User register(String username, String rawPassword) {
            User u = new User();
            u.setUsername(username);
            u.setPassword(rawPassword);
            return u;
        }

        @Override
        public boolean authenticate(String username, String rawPassword) {
            return authenticateResult;
        }
    }

    @BeforeEach
    public void setup() {
        fakeUserService = new FakeUserService();
        AuthController controller = new AuthController(fakeUserService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // GET /api/auth/ping
    @Test
    public void testPing() throws Exception {
        mockMvc.perform(get("/api/auth/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    // POST /api/auth/signup success
    @Test
    public void testSignupSuccess() throws Exception {
        String body = """
                {"username": "john", "password": "pass123"}
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("registered"));
    }

    // POST /api/auth/signup missing fields
    @Test
    public void testSignupMissingFields() throws Exception {
        String body = """
                {"username": null, "password": null}
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("missing"));
    }

    // POST /api/auth/login success
    @Test
    public void testLoginSuccess() throws Exception {
        fakeUserService.authenticateResult = true;

        String body = """
                {"username": "john", "password": "pass123"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    // POST /api/auth/login invalid credentials
    @Test
    public void testLoginInvalid() throws Exception {
        fakeUserService.authenticateResult = false;

        String body = """
                {"username": "john", "password": "wrong"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("invalid"));
    }

    // POST /api/auth/login missing fields
    @Test
    public void testLoginMissingFields() throws Exception {
        String body = """
                {"username": null, "password": null}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("missing"));
    }
}
