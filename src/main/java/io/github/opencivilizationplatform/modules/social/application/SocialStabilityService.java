package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import org.springframework.stereotype.Service;

@Service
public class SocialStabilityService {

    private final IncidentRepository incidentRepository;
    private final BehaviorAssessmentRepository assessmentRepository;
    private final CaseRepository caseRepository;

    public SocialStabilityService(IncidentRepository incidentRepository,
                                  BehaviorAssessmentRepository assessmentRepository,
                                  CaseRepository caseRepository) {
        this.incidentRepository = incidentRepository;
        this.assessmentRepository = assessmentRepository;
        this.caseRepository = caseRepository;
    }

    public java.util.List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public java.util.List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Incident reportIncident(Incident incident) {
        incident.setStatus(IncidentStatus.REPORTED);
        return incidentRepository.save(incident);
    }

    public java.util.List<BehaviorAssessment> getAssessmentsForCitizen(String citizenId) {
        return assessmentRepository.findByCitizenId(citizenId);
    }
}
