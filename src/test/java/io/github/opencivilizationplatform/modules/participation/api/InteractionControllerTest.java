package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
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
class InteractionControllerTest {

    private MockMvc mockMvc;
    private InteractionService interactionService;

    @BeforeEach
    void setUp() {
        interactionService = mock(InteractionService.class);
        mockMvc = standaloneSetup(new InteractionController(interactionService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


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
                                    "type": "INNOVATION",
                                    "content": "Vote on new policy",
                                    "region": "Sector-7",
                                    "citizenId": "C-001",
                                    "status": "PENDING"
                                }
                                """))
                .andExpect(status().isOk());
    }
}