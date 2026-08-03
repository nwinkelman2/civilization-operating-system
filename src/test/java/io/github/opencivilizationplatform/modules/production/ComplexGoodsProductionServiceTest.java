package io.github.opencivilizationplatform.modules.production;

import io.github.opencivilizationplatform.modules.production.application.ComplexGoodsProductionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComplexGoodsProductionServiceTest {

    private ComplexGoodsProductionService productionService;

    @BeforeEach
    void setUp() {
        productionService = new ComplexGoodsProductionService();
    }

    @Test
    void testCalculateMaxSteel() {
        assertEquals(5.0, productionService.calculateMaxSteel(10.0, 10.0), 1e-6);
        assertEquals(2.5, productionService.calculateMaxSteel(5.0, 10.0), 1e-6);
        assertEquals(0.0, productionService.calculateMaxSteel(0.0, 10.0), 1e-6);
    }

    @Test
    void testCalculateMaxTools() {
        assertEquals(3.0, productionService.calculateMaxTools(6.0), 1e-6);
        assertEquals(0.0, productionService.calculateMaxTools(0.0), 1e-6);
    }

    @Test
    void testCalculateMaxElectronics() {
        assertEquals(2.0, productionService.calculateMaxElectronics(5.0, 4.0, 4.0), 1e-6);
        assertEquals(1.0, productionService.calculateMaxElectronics(1.0, 10.0, 10.0), 1e-6);
    }

    @Test
    void testProcessProductionCycleFullChain() {
        Map<String, Double> resources = new HashMap<>();
        resources.put("IRON_ORE", 20.0); // Can make 10 steel
        resources.put("COAL", 10.0);
        resources.put("SILICON", 4.0); // Can make 2 electronics (requires 2 steel, 4 silicon, 4 copper)
        resources.put("COPPER", 4.0);

        ComplexGoodsProductionService.ProductionResult result = productionService.processProductionCycle(resources);

        assertNotNull(result);
        assertEquals(10.0, result.steelProduced(), 1e-6);
        assertEquals(2.0, result.electronicsProduced(), 1e-6);
        // 10 steel - 2 used for electronics = 8 steel left -> converts to 4 tools
        assertEquals(4.0, result.toolsProduced(), 1e-6);

        Map<String, Double> remaining = result.remainingResources();
        assertEquals(0.0, remaining.get("IRON_ORE"), 1e-6);
        assertEquals(0.0, remaining.get("COAL"), 1e-6);
        assertEquals(0.0, remaining.get("SILICON"), 1e-6);
        assertEquals(0.0, remaining.get("COPPER"), 1e-6);
        assertEquals(4.0, remaining.get("TOOLS"), 1e-6);
        assertEquals(2.0, remaining.get("ELECTRONICS"), 1e-6);
    }
}
