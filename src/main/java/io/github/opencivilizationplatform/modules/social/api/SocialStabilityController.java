package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
public class SocialStabilityController {

    private final SocialStabilityService socialStabilityService;

    public SocialStabilityController(SocialStabilityService socialStabilityService) {
        this.socialStabilityService = socialStabilityService;
    }

    @GetMapping("/incidents")
    public Page<Incident> getAllIncidents(Pageable pageable) {
        return socialStabilityService.getAllIncidents(pageable);
    }

    @GetMapping("/cases")
    public Page<Case> getAllCases(Pageable pageable) {
        return socialStabilityService.getAllCases(pageable);
    }

    @PostMapping("/incidents")
    public Incident reportIncident(@Valid @RequestBody Incident incident) {
        return socialStabilityService.reportIncident(incident);
    }

    @GetMapping("/assessments/{citizenId}")
    public java.util.List<BehaviorAssessment> getAssessments(@PathVariable String citizenId) {
        return socialStabilityService.getAssessmentsForCitizen(citizenId);
    }
}
