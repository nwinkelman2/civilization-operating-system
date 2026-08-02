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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContributionServiceTest {

    @Mock
    private CitizenRepository citizenRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ContributionRepository contributionRepository;
    @Mock
    private io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository;
    @Mock
    private io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenWalletRepository citizenWalletRepository;

    @InjectMocks
    private ContributionService contributionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCitizens() {
        when(citizenRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(new Citizen())));
        Page<Citizen> result = contributionService.getAllCitizens(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
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

    @Test
    void testDonateToCommunitySilos() {
        Citizen citizen = new Citizen();
        citizen.setCitizenId("CIT-TEST");
        citizen.setReputationScore(10.0);
        
        io.github.opencivilizationplatform.modules.civilization.domain.Civilization civ = new io.github.opencivilizationplatform.modules.civilization.domain.Civilization();
        civ.setFood(40.0);
        citizen.setCivilization(civ);

        io.github.opencivilizationplatform.modules.contribution.domain.CitizenWallet wallet = new io.github.opencivilizationplatform.modules.contribution.domain.CitizenWallet();
        wallet.setFood(30.0);
        citizen.setWallet(wallet);

        when(citizenRepository.findByCitizenId("CIT-TEST")).thenReturn(java.util.Optional.of(citizen));

        contributionService.donateToCommunitySilos("CIT-TEST", "FOOD", 10.0);

        assertEquals(20.0, wallet.getFood());
        assertEquals(50.0, civ.getFood());
        assertEquals(30.0, citizen.getReputationScore());
        
        verify(citizenWalletRepository, times(1)).save(wallet);
        verify(citizenRepository, times(1)).save(citizen);
        verify(civilizationRepository, times(1)).save(civ);
    }
}
