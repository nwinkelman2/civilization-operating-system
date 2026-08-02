package io.github.opencivilizationplatform.modules.monitoring.api;

import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class BiosphereMetricControllerTest {

    private MockMvc mockMvc;
    private BiosphereMetricService biosphereMetricService;

    @BeforeEach
    void setUp() {
        biosphereMetricService = mock(BiosphereMetricService.class);
        mockMvc = standaloneSetup(new BiosphereMetricController(biosphereMetricService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


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
                                    "name": "OXYGEN_LEVEL",
                                    "value": 21.0,
                                    "unit": "%",
                                    "safetyLimit": 19.5,
                                    "status": "NORMAL",
                                    "drift": 0.1
                                }
                                """))
                .andExpect(status().isOk());
    }
}