package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
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

@WebMvcTest(SocialStabilityController.class)
class SocialStabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SocialStabilityService socialStabilityService;

    @Test
    void testGetAllIncidents() throws Exception {
        mockMvc.perform(get("/api/v1/social/incidents"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCases() throws Exception {
        mockMvc.perform(get("/api/v1/social/cases"))
                .andExpect(status().isOk());
    }

    @Test
    void testReportIncident() throws Exception {
        mockMvc.perform(post("/api/v1/social/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "type": "THEFT",
                                    "description": "Resource theft reported"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
