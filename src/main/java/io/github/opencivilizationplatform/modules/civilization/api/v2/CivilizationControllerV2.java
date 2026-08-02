package io.github.opencivilizationplatform.modules.civilization.api.v2;

import io.github.opencivilizationplatform.config.ApiVersion;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/civilizations")
@ApiVersion("v2")
@Tag(name = "Civilizations V2", description = "V2 Civilization management with enhanced features")
public class CivilizationControllerV2 {

    private final CivilizationService service;

    public CivilizationControllerV2(CivilizationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a civilization (V2)")
    public Map<String, Object> create(@RequestBody CreateCivilizationV2Request request,
                                       HttpServletRequest http) {
        String token = resolveClientId(http);
        Civilization civ = service.createCivilization(
            request.name(),
            request.scale() != null ? request.scale() : CivilizationScale.LOCAL,
            request.region() != null ? request.region() : "unknown",
            token
        );
        return Map.of(
            "id", civ.getId(),
            "name", civ.getName(),
            "scale", civ.getScale(),
            "region", civ.getRegion(),
            "status", civ.getStatus(),
            "resources", Map.of(
                "food", civ.getFood(),
                "water", civ.getWater(),
                "minerals", civ.getMinerals(),
                "energy", civ.getEnergy(),
                "housing", civ.getHousing()
            )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get civilization details (V2)")
    public Map<String, Object> getById(@PathVariable Long id) {
        Civilization civ = service.getCivilization(id);
        return Map.of(
            "civilization", Map.of(
                "id", civ.getId(),
                "name", civ.getName(),
                "scale", civ.getScale(),
                "status", civ.getStatus(),
                "resources", Map.of(
                    "food", civ.getFood(),
                    "water", civ.getWater(),
                    "minerals", civ.getMinerals(),
                    "energy", civ.getEnergy(),
                    "housing", civ.getHousing()
                ),
                "metrics", Map.of(
                    "population", civ.getPopulation(),
                    "reputation", civ.getReputationScore()
                )
            )
        );
    }

    private String resolveClientId(HttpServletRequest request) {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId != null) return clientId;
        String token = request.getHeader("X-Client-Token");
        if (token == null || token.isBlank()) {
            token = request.getRemoteAddr() + ":" + request.getRemotePort();
        }
        return token;
    }
}

record CreateCivilizationV2Request(String name, CivilizationScale scale, String region) {}
