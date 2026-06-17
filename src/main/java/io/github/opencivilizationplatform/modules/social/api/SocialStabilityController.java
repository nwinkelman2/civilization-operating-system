package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
public class SocialStabilityController {

    private final IncidentRepository incidentRepository;
    private final BehaviorAssessmentRepository assessmentRepository;
    private final CaseRepository caseRepository;

    public SocialStabilityController(IncidentRepository incidentRepository,
                                     BehaviorAssessmentRepository assessmentRepository,
                                     CaseRepository caseRepository) {
        this.incidentRepository = incidentRepository;
        this.assessmentRepository = assessmentRepository;
        this.caseRepository = caseRepository;
    }

    @GetMapping("/incidents")
    public java.util.List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @GetMapping("/cases")
    public java.util.List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    @PostMapping("/incidents")
    public Incident reportIncident(@RequestBody Incident incident) {
        incident.setStatus(IncidentStatus.REPORTED);
        return incidentRepository.save(incident);
    }

    @GetMapping("/assessments/{citizenId}")
    public java.util.List<BehaviorAssessment> getAssessments(@PathVariable String citizenId) {
        return assessmentRepository.findByCitizenId(citizenId);
    }
}
