package io.github.opencivilizationplatform.modules.execution.api;

import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
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
class AutomationUnitControllerTest {

    private MockMvc mockMvc;
    private AutomationUnitService automationUnitService;

    @BeforeEach
    void setUp() {
        automationUnitService = mock(AutomationUnitService.class);
        mockMvc = standaloneSetup(new AutomationUnitController(automationUnitService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllUnits() throws Exception {
        mockMvc.perform(get("/api/v1/automation"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveUnit() throws Exception {
        mockMvc.perform(post("/api/v1/automation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Drone-01",
                                    "type": "DRONE",
                                    "region": "Sector-7",
                                    "status": "ACTIVE",
                                    "currentTask": "Surveying perimeter"
                                }
                                """))
                .andExpect(status().isOk());
    }
}