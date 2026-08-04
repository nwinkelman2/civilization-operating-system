package io.github.opencivilizationplatform.modules.life.application;

import io.github.opencivilizationplatform.modules.life.domain.AgentSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AgentKnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(AgentKnowledgeService.class);
    private static final double DEFAULT_TRANSFER_RATE = 0.15;
    private static final double DEFAULT_TICK_PROGRESSION = 0.5;

    public record KnowledgeCycleResult(
        int skillsProcessed,
        double totalProficiencyGained
    ) {}

    public AgentSkill improveSkill(AgentSkill currentSkill, double progressAmount) {
        if (currentSkill == null) {
            return new AgentSkill("UNKNOWN", Math.max(0.0, progressAmount));
        }
        if (progressAmount <= 0) {
            return currentSkill;
        }
        double newLevel = currentSkill.proficiencyLevel() + progressAmount;
        return currentSkill.withProficiency(newLevel);
    }

    public AgentSkill shareKnowledge(AgentSkill mentorSkill, AgentSkill studentSkill, double transferRate) {
        if (mentorSkill == null && studentSkill == null) {
            return new AgentSkill("UNKNOWN", 0.0);
        }
        if (studentSkill == null) {
            return new AgentSkill(mentorSkill.skillName(), mentorSkill.proficiencyLevel() * transferRate);
        }
        if (mentorSkill == null) {
            return studentSkill;
        }

        if (!Objects.equals(mentorSkill.skillName(), studentSkill.skillName())) {
            log.warn("Knowledge sharing between mismatching skills: {} vs {}", mentorSkill.skillName(), studentSkill.skillName());
        }

        if (mentorSkill.proficiencyLevel() > studentSkill.proficiencyLevel()) {
            double diff = mentorSkill.proficiencyLevel() - studentSkill.proficiencyLevel();
            double gain = diff * Math.max(0.0, transferRate);
            return studentSkill.withProficiency(studentSkill.proficiencyLevel() + gain);
        }
        return studentSkill;
    }

    public AgentSkill shareKnowledge(AgentSkill mentorSkill, AgentSkill studentSkill) {
        return shareKnowledge(mentorSkill, studentSkill, DEFAULT_TRANSFER_RATE);
    }

    public KnowledgeCycleResult processKnowledgeCycle(List<AgentSkill> skills, double progressionRate) {
        if (skills == null || skills.isEmpty()) {
            return new KnowledgeCycleResult(0, 0.0);
        }
        double gained = 0.0;
        int count = 0;
        for (AgentSkill skill : skills) {
            if (skill != null) {
                double oldLevel = skill.proficiencyLevel();
                AgentSkill updated = improveSkill(skill, progressionRate);
                gained += (updated.proficiencyLevel() - oldLevel);
                count++;
            }
        }
        return new KnowledgeCycleResult(count, gained);
    }

    public void processKnowledgeTick() {
        log.info("[KNOWLEDGE ENGINE] Processing skill progression and knowledge sharing tick...");
    }
}
