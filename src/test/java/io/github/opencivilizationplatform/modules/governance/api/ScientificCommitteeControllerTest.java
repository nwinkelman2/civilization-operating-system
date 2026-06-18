package io.github.opencivilizationplatform.modules.governance.api;

import io.github.opencivilizationplatform.modules.governance.application.ScientificCommitteeService;
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

@WebMvcTest(ScientificCommitteeController.class)
class ScientificCommitteeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScientificCommitteeService scientificCommitteeService;

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
                                    "field": "BIOLOGY"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
