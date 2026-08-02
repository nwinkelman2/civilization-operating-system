package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
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
class SocialStabilityControllerTest {

    private MockMvc mockMvc;
    private SocialStabilityService socialStabilityService;

    @BeforeEach
    void setUp() {
        socialStabilityService = mock(SocialStabilityService.class);
        mockMvc = standaloneSetup(new SocialStabilityController(socialStabilityService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


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
                                    "type": "CONFLICT",
                                    "description": "Resource theft reported",
                                    "location": "Sector-7",
                                    "riskLevel": "LOW",
                                    "status": "REPORTED"
                                }
                                """))
                .andExpect(status().isOk());
    }
}