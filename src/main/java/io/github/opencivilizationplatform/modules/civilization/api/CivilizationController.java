package io.github.opencivilizationplatform.modules.civilization.api;

import io.github.opencivilizationplatform.config.JwtService;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.participation.application.GovernanceBootstrapService;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/civilizations")
@Tag(name = "Civilizations", description = "Multi-civilization management endpoints")
public class CivilizationController {

    private final CivilizationService service;
    private final ResourceRegionService regionService;
    private final VoxtexMeshService voxtexService;
    private final JwtService jwtService;
    private final GovernanceBootstrapService governanceBootstrap;

    public CivilizationController(CivilizationService service,
                                   ResourceRegionService regionService,
                                   VoxtexMeshService voxtexService,
                                   JwtService jwtService,
                                   GovernanceBootstrapService governanceBootstrap) {
        this.service = service;
        this.regionService = regionService;
        this.voxtexService = voxtexService;
        this.jwtService = jwtService;
        this.governanceBootstrap = governanceBootstrap;
    }

    @GetMapping
    @Operation(summary = "List all civilizations")
    public Page<Civilization> getAll(Pageable pageable) {
        return service.getAllCivilizations(pageable);
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my civilizations")
    public java.util.List<Civilization> getMine(HttpServletRequest request) {
        String token = resolveToken(request);
        return service.getCivilizationsByOwner(token);
    }

    @PostMapping
    @Operation(summary = "Found a new civilization")
    @ResponseStatus(HttpStatus.CREATED)
    public Civilization create(@RequestBody CreateCivilizationRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        return service.createCivilization(
            request.name(),
            request.scale() != null ? request.scale() : CivilizationScale.LOCAL,
            request.region() != null ? request.region() : "unknown",
            token
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get civilization by ID")
    public Civilization getById(@PathVariable Long id) {
        return service.getCivilization(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update civilization status")
    public Civilization updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @PostMapping("/found")
    @Operation(summary = "Found a civilization on a region", description = "Creates a civilization on a resource region and deploys a primary voxtex node")
    @ResponseStatus(HttpStatus.CREATED)
    public Civilization found(@RequestBody FoundCivilizationRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        Civilization civ = service.createCivilization(
            request.name(),
            request.scale() != null ? request.scale() : CivilizationScale.LOCAL,
            regionService.getRegion(request.regionId()).getName(),
            token
        );

        // Claim the region
        regionService.claimRegion(request.regionId(), civ.getId());

        // Update region link
        civ = service.getCivilization(civ.getId());
        var region = regionService.getRegion(request.regionId());
        // Deploy primary voxtex node
        voxtexService.registerNode(
            civ.getName() + "-Primary",
            VoxtexNodeType.PRIMARY,
            region.getName(),
            civ.getId(),
            "Primary neural node for " + civ.getName()
        );

        // Bootstrap default Voxtex governance rules
        governanceBootstrap.bootstrapGovernance(civ);

        return civ;
    }

    @GetMapping("/map-status")
    @Operation(summary = "Get world map claim status", description = "Returns total cities, claimed count, available count, and whether all cities are claimed")
    public MapStatusResponse mapStatus() {
        long total = regionService.countTotal();
        long available = regionService.countAvailable();
        long claimed = total - available;
        return new MapStatusResponse(total, claimed, available, available == 0);
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a civilization as an agent")
    public Civilization joinAsAgent(@PathVariable Long id) {
        return service.joinAsAgent(id);
    }

    private String resolveToken(HttpServletRequest request) {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId != null) return clientId;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return jwtService.extractClientId(authHeader.substring(7));
            } catch (Exception e) {
                // fall through
            }
        }
        String token = request.getHeader("X-Client-Token");
        if (token == null || token.isBlank()) {
            token = request.getRemoteAddr() + ":" + (request.getRemotePort());
        }
        return token;
    }
}

record CreateCivilizationRequest(String name, CivilizationScale scale, String region) {}
record UpdateStatusRequest(CivilizationStatus status) {}
record FoundCivilizationRequest(String name, CivilizationScale scale, Long regionId, String founderName) {}
record MapStatusResponse(long total, long claimed, long available, boolean allClaimed) {}
