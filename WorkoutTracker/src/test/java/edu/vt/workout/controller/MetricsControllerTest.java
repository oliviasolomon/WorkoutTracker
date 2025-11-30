package edu.vt.workout.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void metricsChartEndpointReturnsPngImage() throws Exception {
        // Perform GET /metrics/chart
        MvcResult result = mockMvc.perform(get("/metrics/chart"))
                // HTTP 200 OK
                .andExpect(status().isOk())
                // Content-Type: image/png
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn();

        // Check that the response body is not empty
        byte[] body = result.getResponse().getContentAsByteArray();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.length > 0, "Response body should contain image bytes");
    }
}
