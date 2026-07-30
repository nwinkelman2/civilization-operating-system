package io.github.opencivilizationplatform.modules.nexus.application;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.MigrationRequest;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MigrationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MigrationServiceTest {

    @Mock
    private MigrationRequestRepository migrationRequestRepository;
    @Mock
    private CivilizationRepository civilizationRepository;
    @Mock
    private io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository ruleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MigrationService migrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        migrationService = new MigrationService(migrationRequestRepository, civilizationRepository, objectMapper, ruleRepository);
    }

    @Test
    void testApplyMigration() {
        Civilization fromCiv = new Civilization();
        fromCiv.setId(1L);
        fromCiv.setName("Alexandria");

        Civilization toCiv = new Civilization();
        toCiv.setId(2L);
        toCiv.setName("Athens");

        when(civilizationRepository.findById(1L)).thenReturn(Optional.of(fromCiv));
        when(civilizationRepository.findById(2L)).thenReturn(Optional.of(toCiv));
        when(migrationRequestRepository.save(any(MigrationRequest.class))).thenAnswer(i -> i.getArgument(0));

        MigrationRequest result = migrationService.applyMigration("Citizen Alice", 1L, 2L, "Buscar novas pesquisas");

        assertNotNull(result);
        assertEquals("Citizen Alice", result.getCitizenName());
        assertEquals("Alexandria", result.getFromCivilization().getName());
        assertEquals("Athens", result.getToCivilization().getName());
        assertEquals("PENDING", result.getStatus());
        assertEquals("Buscar novas pesquisas", result.getReason());
    }

    @Test
    void testApproveMigrationUpdatesPopulations() {
        Civilization fromCiv = new Civilization();
        fromCiv.setId(1L);
        fromCiv.setName("Alexandria");
        fromCiv.setPopulation(100);

        Civilization toCiv = new Civilization();
        toCiv.setId(2L);
        toCiv.setName("Athens");
        toCiv.setPopulation(50);

        MigrationRequest req = new MigrationRequest();
        req.setId(10L);
        req.setCitizenName("Citizen Bob");
        req.setFromCivilization(fromCiv);
        req.setToCivilization(toCiv);
        req.setStatus("PENDING");

        when(migrationRequestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(migrationRequestRepository.save(any(MigrationRequest.class))).thenAnswer(i -> i.getArgument(0));

        MigrationRequest approved = migrationService.approveMigration(10L);

        assertEquals("APPROVED", approved.getStatus());
        assertEquals(99, fromCiv.getPopulation());
        assertEquals(51, toCiv.getPopulation());

        verify(civilizationRepository, times(1)).save(fromCiv);
        verify(civilizationRepository, times(1)).save(toCiv);
        verify(migrationRequestRepository, times(1)).save(req);
    }
}
