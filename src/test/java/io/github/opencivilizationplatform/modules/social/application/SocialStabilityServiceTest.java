package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.Case;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.infrastructure.CaseRepository;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SocialStabilityServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private io.github.opencivilizationplatform.core.eventbus.EventBus eventBus;

    @InjectMocks
    private SocialStabilityService socialStabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllIncidents() {
        when(incidentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(new Incident(), new Incident())));
        Page<Incident> result = socialStabilityService.getAllIncidents(Pageable.unpaged());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testGetAllCases() {
        when(caseRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(new Case())));
        Page<Case> result = socialStabilityService.getAllCases(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSaveIncident() {
        Incident i = new Incident();
        i.setDescription("Resource Distribution Lag");
        when(incidentRepository.save(i)).thenReturn(i);
        Incident saved = socialStabilityService.reportIncident(i);
        assertNotNull(saved);
        assertEquals("Resource Distribution Lag", saved.getDescription());
    }

    @Test
    void testMediateIncident() {
        Incident i = new Incident();
        i.setId(5L);
        when(incidentRepository.findById(5L)).thenReturn(java.util.Optional.of(i));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident mediated = socialStabilityService.mediateIncident(5L);
        assertNotNull(mediated);
        verify(eventBus, times(1)).publish(any(io.github.opencivilizationplatform.core.eventbus.events.IncidentResolvedEvent.class));
    }
}
