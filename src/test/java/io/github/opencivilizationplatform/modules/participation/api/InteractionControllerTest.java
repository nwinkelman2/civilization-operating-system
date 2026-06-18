package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
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

@WebMvcTest(InteractionController.class)
class InteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InteractionService interactionService;

    @Test
    void testGetAllInteractions() throws Exception {
        mockMvc.perform(get("/api/v1/interactions"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveInteraction() throws Exception {
        mockMvc.perform(post("/api/v1/interactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "type": "POLL",
                                    "description": "Vote on new policy"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
