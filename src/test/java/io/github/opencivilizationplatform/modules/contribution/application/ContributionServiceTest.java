package io.github.opencivilizationplatform.modules.contribution.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectStatus;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ContributionRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContributionServiceTest {

    @Mock
    private CitizenRepository citizenRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ContributionRepository contributionRepository;

    @InjectMocks
    private ContributionService contributionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCitizens() {
        when(citizenRepository.findAll()).thenReturn(Arrays.asList(new Citizen()));
        assertEquals(1, contributionService.getAllCitizens().size());
    }

    @Test
    void testGetActiveProjects() {
        Project p = new Project();
        p.setStatus(ProjectStatus.ACTIVE);
        when(projectRepository.findByStatus(ProjectStatus.ACTIVE)).thenReturn(Arrays.asList(p));
        assertEquals(1, contributionService.getActiveProjects().size());
    }

    @Test
    void testRecordContribution() {
        Contribution c = new Contribution();
        c.setImpactScore(100.0);
        when(contributionRepository.save(c)).thenReturn(c);
        assertEquals(100.0, contributionService.recordContribution(c).getImpactScore());
    }
}
