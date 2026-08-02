package io.github.opencivilizationplatform.modules.leaderboard.api;

import io.github.opencivilizationplatform.modules.leaderboard.application.LeaderboardService;
import io.github.opencivilizationplatform.modules.leaderboard.domain.CivilizationScore;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {

    private MockMvc mockMvc;
    private LeaderboardService service;

    @BeforeEach
    void setUp() {
        service = mock(LeaderboardService.class);
        mockMvc = standaloneSetup(new LeaderboardController(service)).build();
    }

    @Test
    void shouldReturnLeaderboard() throws Exception {
        var score = new CivilizationScore(1L, "TestCiv", 75.0, 100, 3, 2);
        when(service.getLeaderboard()).thenReturn(List.of(score));
        mockMvc.perform(get("/api/v1/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].civilizationId").value(1))
            .andExpect(jsonPath("$[0].name").value("TestCiv"))
            .andExpect(jsonPath("$[0].totalScore").isNumber());
    }
    @Test
    void shouldReturnEmptyList() throws Exception {
        when(service.getLeaderboard()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}