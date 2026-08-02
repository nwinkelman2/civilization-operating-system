package io.github.opencivilizationplatform.modules.simulation.api;

import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    private MockMvc mockMvc;
    private SimulationEngineService simulationEngineService;

    @BeforeEach
    void setUp() {
        simulationEngineService = mock(SimulationEngineService.class);
        mockMvc = standaloneSetup(new SimulationController(simulationEngineService)).build();
    }


    @Test
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/v1/simulation/status"))
                .andExpect(status().isOk());
    }
}