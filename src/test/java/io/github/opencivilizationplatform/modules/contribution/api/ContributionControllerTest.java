package io.github.opencivilizationplatform.modules.contribution.api;

import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
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

@WebMvcTest(ContributionController.class)
class ContributionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContributionService contributionService;

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
                                    "citizenId": "C-001",
                                    "projectId": "P-001",
                                    "hours": 10
                                }
                                """))
                .andExpect(status().isOk());
    }
}
