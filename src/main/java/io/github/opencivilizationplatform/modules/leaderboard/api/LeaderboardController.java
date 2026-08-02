package io.github.opencivilizationplatform.modules.leaderboard.api;

import io.github.opencivilizationplatform.modules.leaderboard.application.LeaderboardService;
import io.github.opencivilizationplatform.modules.leaderboard.domain.CivilizationScore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@Tag(name = "Leaderboard", description = "Civilization ranking endpoints")
public class LeaderboardController {

    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get civilization leaderboard", description = "Returns civilizations ranked by total score")
    public List<CivilizationScore> getLeaderboard() {
        return service.getLeaderboard();
    }
}
