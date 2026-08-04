package io.github.opencivilizationplatform.modules.production;

import io.github.opencivilizationplatform.modules.production.application.ToolforgeService;
import io.github.opencivilizationplatform.modules.production.domain.CraftedTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolforgeServiceTest {

    private ToolforgeService toolforgeService;

    @BeforeEach
    void setUp() {
        toolforgeService = new ToolforgeService();
    }

    @Test
    void testCraftedToolValidation() {
        CraftedTool tool = new CraftedTool("T1", "Titanium Pick", 0.5, -10.0);
        assertEquals(1.0, tool.efficiencyMultiplier());
        assertEquals(0.0, tool.durability());
        assertTrue(tool.isBroken());
    }

    @Test
    void testCraftToolFromRecipe() {
        CraftedTool pickaxe = toolforgeService.craftTool("PICKAXE", "Super Pickaxe", 0.3);
        assertEquals("Super Pickaxe", pickaxe.toolName());
        assertEquals(1.8, pickaxe.efficiencyMultiplier(), 0.001);
        assertEquals(100.0, pickaxe.durability());
        assertFalse(pickaxe.isBroken());
    }

    @Test
    void testCraftCustomTool() {
        CraftedTool custom = toolforgeService.craftTool("UNKNOWN_RECIPE", "Hammer", 0.5);
        assertEquals("Hammer", custom.toolName());
        assertEquals(1.5, custom.efficiencyMultiplier(), 0.001);
    }

    @Test
    void testCalculateExtractionMultiplier() {
        CraftedTool tool = new CraftedTool("T1", "Axe", 2.5, 50.0);
        assertEquals(2.5, toolforgeService.calculateExtractionMultiplier(tool));

        CraftedTool brokenTool = new CraftedTool("T2", "Broken Axe", 2.5, 0.0);
        assertEquals(1.0, toolforgeService.calculateExtractionMultiplier(brokenTool));

        assertEquals(1.0, toolforgeService.calculateExtractionMultiplier(null));
    }

    @Test
    void testApplyToolWear() {
        CraftedTool tool = new CraftedTool("T1", "Plow", 2.0, 30.0);
        CraftedTool worn = toolforgeService.applyToolWear(tool, 10.0);

        assertEquals(20.0, worn.durability());
        assertFalse(worn.isBroken());

        CraftedTool completelyWorn = toolforgeService.applyToolWear(worn, 25.0);
        assertEquals(0.0, completelyWorn.durability());
        assertTrue(completelyWorn.isBroken());
    }

    @Test
    void testProcessToolforgeTick() {
        assertDoesNotThrow(() -> toolforgeService.processToolforgeTick());
    }
}
