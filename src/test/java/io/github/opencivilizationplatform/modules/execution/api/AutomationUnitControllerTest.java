package io.github.opencivilizationplatform.modules.execution.api;

import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
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

@WebMvcTest(AutomationUnitController.class)
class AutomationUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutomationUnitService automationUnitService;

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
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
