package io.github.opencivilizationplatform.modules.region;

import io.github.opencivilizationplatform.modules.region.application.TerritoryControlService;
import io.github.opencivilizationplatform.modules.region.application.TerritoryControlService.TerritoryExpansionResult;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TerritoryControlServiceTest {

    @Mock
    private ResourceRegionRepository repository;

    private TerritoryControlService territoryControlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        territoryControlService = new TerritoryControlService(repository);
    }

    @Test
    void testEvaluateClaimEligibility() {
        ResourceRegion region = new ResourceRegion();
        region.setId(1L);
        region.setClaimed(false);

        assertTrue(territoryControlService.evaluateClaimEligibility(region, 10L, 80.0, 50.0));
        assertFalse(territoryControlService.evaluateClaimEligibility(region, 10L, 40.0, 50.0));

        region.setClaimed(true);
        region.setClaimedByCivilizationId(10L);
        assertTrue(territoryControlService.evaluateClaimEligibility(region, 10L, 80.0, 50.0));

        region.setClaimedByCivilizationId(99L);
        assertFalse(territoryControlService.evaluateClaimEligibility(region, 10L, 80.0, 50.0));
    }

    @Test
    void testProcessClaimEvaluationSuccess() {
        ResourceRegion region = new ResourceRegion();
        region.setId(1L);
        region.setClaimed(false);

        when(repository.save(any(ResourceRegion.class))).thenAnswer(i -> i.getArgument(0));

        ResourceRegion updated = territoryControlService.processClaimEvaluation(region, 10L, 80.0, 50.0);
        assertTrue(updated.getClaimed());
        assertEquals(10L, updated.getClaimedByCivilizationId());
        verify(repository, times(1)).save(region);
    }

    @Test
    void testCalculateExpansion() {
        ResourceRegion region = new ResourceRegion();
        region.setId(1L);
        region.setName("Amazonia");
        region.setRadiusKm(10.0);

        TerritoryExpansionResult result = territoryControlService.calculateExpansion(region, 2.0, 1000);
        assertTrue(result.expanded());
        assertEquals(10.0, result.oldRadiusKm());
        assertTrue(result.newRadiusKm() > 10.0);
        assertEquals(region.getId(), result.regionId());
        assertEquals("Amazonia", result.regionName());
    }

    @Test
    void testProcessTerritoryTick() {
        ResourceRegion region1 = new ResourceRegion();
        region1.setId(1L);
        region1.setName("Region 1");
        region1.setClaimed(true);
        region1.setClaimedByCivilizationId(5L);
        region1.setRadiusKm(15.0);

        when(repository.findAll()).thenReturn(List.of(region1));
        when(repository.save(any(ResourceRegion.class))).thenAnswer(i -> i.getArgument(0));

        List<TerritoryExpansionResult> results = territoryControlService.processTerritoryTick(1.5);
        assertEquals(1, results.size());
        assertTrue(results.get(0).expanded());
        verify(repository, times(1)).save(region1);
    }
}
