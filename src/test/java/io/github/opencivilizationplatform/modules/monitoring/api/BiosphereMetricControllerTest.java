package io.github.opencivilizationplatform.modules.monitoring.api;

import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BiosphereMetricController.class)
class BiosphereMetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BiosphereMetricService biosphereMetricService;

    @Test
    void testGetAllMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/biosphere"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveMetric() throws Exception {
        mockMvc.perform(post("/api/v1/biosphere")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "metric": "OXYGEN_LEVEL",
                                    "value": 21.0,
                                    "unit": "%"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
