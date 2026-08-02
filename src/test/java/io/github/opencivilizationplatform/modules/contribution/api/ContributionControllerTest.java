package io.github.opencivilizationplatform.modules.contribution.api;

import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
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
class ContributionControllerTest {

    private MockMvc mockMvc;
    private ContributionService contributionService;

    @BeforeEach
    void setUp() {
        contributionService = mock(ContributionService.class);
        mockMvc = standaloneSetup(new ContributionController(contributionService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllContributions() throws Exception {
        mockMvc.perform(get("/api/v1/purpose/contributions"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCitizens() throws Exception {
        mockMvc.perform(get("/api/v1/purpose/citizens"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllProjects() throws Exception {
        mockMvc.perform(get("/api/v1/purpose/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void testRecordContribution() throws Exception {
        mockMvc.perform(post("/api/v1/purpose/contribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "citizen": {"id": 1},
                                    "project": {"id": 1},
                                    "role": "Engineer",
                                    "impactScore": 10.0
                                }
                                """))
                .andExpect(status().isOk());
    }
}