package io.github.opencivilizationplatform.modules.simulation.api;

import io.github.opencivilizationplatform.modules.simulation.application.CortexEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimulationController.class)
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CortexEngineService cortexEngineService;

    @Test
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/v1/simulation/status"))
                .andExpect(status().isOk());
    }
}
