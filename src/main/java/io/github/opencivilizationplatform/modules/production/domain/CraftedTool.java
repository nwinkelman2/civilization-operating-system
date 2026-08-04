package io.github.opencivilizationplatform.modules.production.domain;

public record CraftedTool(
    String toolId,
    String toolName,
    double efficiencyMultiplier,
    double durability
) {
    public CraftedTool {
        if (toolId == null || toolId.isBlank()) {
            toolId = "TOOL-GENERIC";
        }
        if (toolName == null || toolName.isBlank()) {
            toolName = "Generic Tool";
        }
        efficiencyMultiplier = Math.max(1.0, efficiencyMultiplier);
        durability = Math.max(0.0, durability);
    }

    public CraftedTool withDurability(double newDurability) {
        return new CraftedTool(this.toolId, this.toolName, this.efficiencyMultiplier, newDurability);
    }

    public boolean isBroken() {
        return durability <= 0.0;
    }
}
