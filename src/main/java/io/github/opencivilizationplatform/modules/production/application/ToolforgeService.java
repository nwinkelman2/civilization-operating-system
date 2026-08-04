package io.github.opencivilizationplatform.modules.production.application;

import io.github.opencivilizationplatform.modules.production.domain.CraftedTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ToolforgeService {

    private static final Logger log = LoggerFactory.getLogger(ToolforgeService.class);
    private final Map<String, CraftedTool> toolRecipes = new HashMap<>();

    public ToolforgeService() {
        // Register default recipes
        toolRecipes.put("PICKAXE", new CraftedTool("RECIPE-01", "Steel Pickaxe", 1.5, 100.0));
        toolRecipes.put("AXE", new CraftedTool("RECIPE-02", "Iron Axe", 1.3, 80.0));
        toolRecipes.put("PLOW", new CraftedTool("RECIPE-03", "Advanced Plow", 2.0, 150.0));
    }

    public CraftedTool craftTool(String recipeKey, String customName, double efficiencyBonus) {
        CraftedTool base = toolRecipes.get(recipeKey != null ? recipeKey.toUpperCase() : "");
        if (base == null) {
            String name = customName != null ? customName : "Basic Tool";
            return new CraftedTool("TOOL-CUSTOM", name, 1.0 + Math.max(0.0, efficiencyBonus), 50.0);
        }

        String name = customName != null ? customName : base.toolName();
        double efficiency = base.efficiencyMultiplier() + Math.max(0.0, efficiencyBonus);
        return new CraftedTool(base.toolId(), name, efficiency, base.durability());
    }

    public double calculateExtractionMultiplier(CraftedTool tool) {
        if (tool == null || tool.isBroken()) {
            return 1.0;
        }
        return tool.efficiencyMultiplier();
    }

    public CraftedTool applyToolWear(CraftedTool tool, double wearAmount) {
        if (tool == null) {
            return new CraftedTool("TOOL-NONE", "None", 1.0, 0.0);
        }
        if (wearAmount <= 0) {
            return tool;
        }
        double newDurability = Math.max(0.0, tool.durability() - wearAmount);
        return tool.withDurability(newDurability);
    }

    public Map<String, CraftedTool> getToolRecipes() {
        return Map.copyOf(toolRecipes);
    }

    public void processToolforgeTick() {
        log.info("[TOOLFORGE ENGINE] Managing tool crafting recipes and extraction multipliers tick...");
    }
}
