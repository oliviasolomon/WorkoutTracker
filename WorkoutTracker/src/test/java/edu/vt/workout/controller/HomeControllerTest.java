package edu.vt.workout.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HomeControllerTest {

    private final HomeController controller = new HomeController();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    void testIndexPage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index.html"));
    }

    @Test
    void testSignupPage() throws Exception {
        mvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup.html"));
    }

    @Test
    void testLoginPage() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login.html"));
    }

    @Test
    void testMetricsPage() throws Exception {
        mvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(view().name("metrics.html"));
    }
}

