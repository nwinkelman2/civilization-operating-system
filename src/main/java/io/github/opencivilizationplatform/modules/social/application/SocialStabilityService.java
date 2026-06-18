package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Incident> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable);
    }

    public Page<Case> getAllCases(Pageable pageable) {
        return caseRepository.findAll(pageable);
    }

    public Incident reportIncident(Incident incident) {
        incident.setStatus(IncidentStatus.REPORTED);
        return incidentRepository.save(incident);
    }

    public java.util.List<BehaviorAssessment> getAssessmentsForCitizen(String citizenId) {
        return assessmentRepository.findByCitizenId(citizenId);
    }
}
