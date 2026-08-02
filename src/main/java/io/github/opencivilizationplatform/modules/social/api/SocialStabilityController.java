package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
@Tag(name = "Social Stability", description = "Social stability management endpoints")
public class SocialStabilityController {

    private final SocialStabilityService socialStabilityService;

    public SocialStabilityController(SocialStabilityService socialStabilityService) {
        this.socialStabilityService = socialStabilityService;
    }

    @GetMapping("/incidents")
    @Operation(summary = "List all incidents", description = "Returns a paginated list of incidents")
    public Page<Incident> getAllIncidents(Pageable pageable) {
        return socialStabilityService.getAllIncidents(pageable);
    }

    @GetMapping("/cases")
    @Operation(summary = "List all cases", description = "Returns a paginated list of cases")
    public Page<Case> getAllCases(Pageable pageable) {
        return socialStabilityService.getAllCases(pageable);
    }

    @PostMapping("/incidents")
    @Operation(summary = "Report an incident", description = "Reports a new incident")
    public Incident reportIncident(@Valid @RequestBody Incident incident) {
        return socialStabilityService.reportIncident(incident);
    }

    @GetMapping("/assessments/{citizenId}")
    @Operation(summary = "Get behavior assessments", description = "Returns behavior assessments for a citizen")
    public java.util.List<BehaviorAssessment> getAssessments(@PathVariable String citizenId) {
        return socialStabilityService.getAssessmentsForCitizen(citizenId);
    }
}
