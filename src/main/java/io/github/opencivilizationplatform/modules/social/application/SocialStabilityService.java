package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.infrastructure.BehaviorAssessmentRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.IncidentResolvedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SocialStabilityService {

    private final IncidentRepository incidentRepository;
    private final BehaviorAssessmentRepository assessmentRepository;
    private final CaseRepository caseRepository;
    private final EventBus eventBus;

    public SocialStabilityService(IncidentRepository incidentRepository,
                                  BehaviorAssessmentRepository assessmentRepository,
                                  CaseRepository caseRepository,
                                  EventBus eventBus) {
        this.incidentRepository = incidentRepository;
        this.assessmentRepository = assessmentRepository;
        this.caseRepository = caseRepository;
        this.eventBus = eventBus;
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

    public java.util.List<Incident> getIncidentsForCivilization(Long civId) {
        return incidentRepository.findByCivilizationId(civId);
    }

    @org.springframework.transaction.annotation.Transactional
    public Incident mediateIncident(Long incidentId) {
        Incident inc = incidentRepository.findById(incidentId).orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));
        inc.setStatus(IncidentStatus.RESOLVED);
        Incident saved = incidentRepository.save(inc);

        eventBus.publish(new IncidentResolvedEvent(
            "SocialStabilityService",
            saved.getId(),
            saved.getCivilization() != null ? saved.getCivilization().getId() : null,
            "Incident mediated and resolved"
        ));
        return saved;
    }

    @org.springframework.transaction.annotation.Transactional
    public Incident createIncidentForCivilization(Incident incident, io.github.opencivilizationplatform.modules.civilization.domain.Civilization civ) {
        incident.setCivilization(civ);
        incident.setStatus(IncidentStatus.REPORTED);
        return incidentRepository.save(incident);
    }

    @org.springframework.transaction.annotation.Transactional
    public Incident assignBotsToIncident(Long incidentId, int ecoBots, int securityBots) {
        Incident inc = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));
        inc.setAssignedEcoBots(ecoBots);
        inc.setAssignedSecurityBots(securityBots);
        inc.setStatus(IncidentStatus.ANALYZING);
        return incidentRepository.save(inc);
    }
}
