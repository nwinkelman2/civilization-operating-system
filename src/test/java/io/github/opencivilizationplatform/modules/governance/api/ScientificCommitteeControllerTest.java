package io.github.opencivilizationplatform.modules.governance.api;

import io.github.opencivilizationplatform.modules.governance.application.ScientificCommitteeService;
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
class ScientificCommitteeControllerTest {

    private MockMvc mockMvc;
    private ScientificCommitteeService scientificCommitteeService;

    @BeforeEach
    void setUp() {
        scientificCommitteeService = mock(ScientificCommitteeService.class);
        mockMvc = standaloneSetup(new ScientificCommitteeController(scientificCommitteeService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllCommittees() throws Exception {
        mockMvc.perform(get("/api/v1/governance"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveCommittee() throws Exception {
        mockMvc.perform(post("/api/v1/governance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Ethics Committee",
                                    "area": "SOCIAL",
                                    "validationLevel": "COMMUNITY_VALIDATED"
                                }
                                """))
                .andExpect(status().isOk());
    }
}