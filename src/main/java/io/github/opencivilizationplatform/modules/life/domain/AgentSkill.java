package io.github.opencivilizationplatform.modules.life.domain;

public record AgentSkill(
    String skillName,
    double proficiencyLevel
) {
    public AgentSkill {
        if (skillName == null || skillName.isBlank()) {
            skillName = "UNKNOWN";
        }
        proficiencyLevel = Math.max(0.0, Math.min(100.0, proficiencyLevel));
    }

    public AgentSkill withProficiency(double newProficiencyLevel) {
        return new AgentSkill(this.skillName, newProficiencyLevel);
    }
}
