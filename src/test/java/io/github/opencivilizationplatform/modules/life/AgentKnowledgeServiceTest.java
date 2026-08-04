package io.github.opencivilizationplatform.modules.life;

import io.github.opencivilizationplatform.modules.life.application.AgentKnowledgeService;
import io.github.opencivilizationplatform.modules.life.application.AgentKnowledgeService.KnowledgeCycleResult;
import io.github.opencivilizationplatform.modules.life.domain.AgentSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentKnowledgeServiceTest {

    private AgentKnowledgeService knowledgeService;

    @BeforeEach
    void setUp() {
        knowledgeService = new AgentKnowledgeService();
    }

    @Test
    void testAgentSkillClamping() {
        AgentSkill negativeSkill = new AgentSkill("Agriculture", -15.0);
        assertEquals(0.0, negativeSkill.proficiencyLevel());

        AgentSkill overSkill = new AgentSkill("Engineering", 125.0);
        assertEquals(100.0, overSkill.proficiencyLevel());

        AgentSkill nullNameSkill = new AgentSkill(null, 50.0);
        assertEquals("UNKNOWN", nullNameSkill.skillName());
    }

    @Test
    void testImproveSkill() {
        AgentSkill skill = new AgentSkill("Metallurgy", 40.0);
        AgentSkill improved = knowledgeService.improveSkill(skill, 15.0);

        assertEquals(55.0, improved.proficiencyLevel());
        assertEquals("Metallurgy", improved.skillName());

        AgentSkill maxed = knowledgeService.improveSkill(improved, 60.0);
        assertEquals(100.0, maxed.proficiencyLevel());
    }

    @Test
    void testShareKnowledge() {
        AgentSkill mentor = new AgentSkill("Medicine", 80.0);
        AgentSkill student = new AgentSkill("Medicine", 20.0);

        AgentSkill updatedStudent = knowledgeService.shareKnowledge(mentor, student, 0.25);
        // diff = 60, gain = 60 * 0.25 = 15 -> student level becomes 35.0
        assertEquals(35.0, updatedStudent.proficiencyLevel());

        AgentSkill lowerMentor = new AgentSkill("Medicine", 10.0);
        AgentSkill unchangedStudent = knowledgeService.shareKnowledge(lowerMentor, updatedStudent, 0.25);
        assertEquals(35.0, unchangedStudent.proficiencyLevel());
    }

    @Test
    void testProcessKnowledgeCycle() {
        List<AgentSkill> skills = List.of(
            new AgentSkill("Mining", 10.0),
            new AgentSkill("Farming", 95.0)
        );

        KnowledgeCycleResult result = knowledgeService.processKnowledgeCycle(skills, 10.0);

        assertEquals(2, result.skillsProcessed());
        assertEquals(15.0, result.totalProficiencyGained()); // 10.0 for Mining + 5.0 for Farming (clamped at 100)
    }

    @Test
    void testProcessKnowledgeTick() {
        assertDoesNotThrow(() -> knowledgeService.processKnowledgeTick());
    }
}
