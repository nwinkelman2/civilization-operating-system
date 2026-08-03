package io.github.opencivilizationplatform.modules.life;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.life.application.AgentMetabolismService;
import io.github.opencivilizationplatform.modules.life.application.AgentMetabolismService.MetabolismResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentMetabolismServiceTest {

    private AgentMetabolismService metabolismService;

    @BeforeEach
    void setUp() {
        metabolismService = new AgentMetabolismService();
    }

    @Test
    void testCalculateMetabolicRateByRole() {
        Citizen founder = new Citizen();
        founder.setRole(Role.FOUNDER);
        assertEquals(1.3, metabolismService.calculateMetabolicRate(founder));

        Citizen coordinator = new Citizen();
        coordinator.setRole(Role.NEXUS_COORDINATOR);
        assertEquals(1.2, metabolismService.calculateMetabolicRate(coordinator));

        Citizen delegate = new Citizen();
        delegate.setRole(Role.SECTOR_DELEGATE);
        assertEquals(1.1, metabolismService.calculateMetabolicRate(delegate));

        Citizen citizen = new Citizen();
        citizen.setRole(Role.CITIZEN);
        assertEquals(1.0, metabolismService.calculateMetabolicRate(citizen));

        assertEquals(1.0, metabolismService.calculateMetabolicRate(null));
    }

    @Test
    void testCalculateRequirements() {
        assertEquals(10.0, metabolismService.calculateFoodRequirement(10, 1.0));
        assertEquals(15.0, metabolismService.calculateWaterRequirement(10, 1.0));
        assertEquals(0.0, metabolismService.calculateFoodRequirement(0, 1.0));
    }

    @Test
    void testProcessMetabolismSufficientSupplies() {
        MetabolismResult result = metabolismService.processMetabolism(10, 20.0, 30.0);
        assertEquals(10.0, result.foodConsumed());
        assertEquals(15.0, result.waterConsumed());
        assertFalse(result.isStarving());
        assertEquals(0.0, result.starvationSeverity());
        assertEquals(0, result.starvingCitizensCount());
    }

    @Test
    void testProcessMetabolismDeficientSupplies() {
        MetabolismResult result = metabolismService.processMetabolism(10, 5.0, 15.0);
        assertEquals(5.0, result.foodConsumed());
        assertEquals(15.0, result.waterConsumed());
        assertTrue(result.isStarving());
        assertEquals(0.5, result.starvationSeverity(), 0.001);
        assertEquals(5, result.starvingCitizensCount());
    }

    @Test
    void testProcessMetabolismForCitizensList() {
        Citizen founder = new Citizen();
        founder.setRole(Role.FOUNDER); // metabolic rate 1.3
        Citizen delegate = new Citizen();
        delegate.setRole(Role.SECTOR_DELEGATE); // metabolic rate 1.1

        List<Citizen> citizens = List.of(founder, delegate);
        // Total metabolic units = 2.4
        MetabolismResult result = metabolismService.processMetabolismForCitizens(citizens, 10.0, 10.0);
        assertEquals(2.4, result.foodConsumed(), 0.001);
        assertEquals(3.6, result.waterConsumed(), 0.001);
        assertFalse(result.isStarving());
    }
}
