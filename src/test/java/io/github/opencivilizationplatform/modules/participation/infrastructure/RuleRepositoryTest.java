package io.github.opencivilizationplatform.modules.participation.infrastructure;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RuleRepositoryTest {

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    void testSaveAndFindWithEnums() {
        Rule rule = new Rule();
        rule.setTitle("Test Rule");
        rule.setDescription("A rule with enum fields");
        rule.setLogicCode("{\"type\": \"THRESHOLD_TRIGGER\"}");
        rule.setStatus(RuleStatus.ACTIVE);
        rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
        rule.setVotesCount(100);

        Rule saved = ruleRepository.save(rule);
        assertNotNull(saved.getId());

        Rule found = ruleRepository.findById(saved.getId()).orElseThrow();
        assertEquals(RuleStatus.ACTIVE, found.getStatus());
        assertEquals(ValidationStatus.SCIENTIFICALLY_VALIDATED, found.getValidationStatus());
        assertEquals(100, found.getVotesCount());
    }

    @Test
    void testFindAllWithDifferentStatuses() {
        Rule active = new Rule();
        active.setTitle("Active Rule");
        active.setDescription("Active rule");
        active.setLogicCode("{\"type\": \"RESERVE_CHECK\"}");
        active.setStatus(RuleStatus.ACTIVE);
        active.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);

        Rule proposed = new Rule();
        proposed.setTitle("Proposed Rule");
        proposed.setDescription("Proposed rule");
        proposed.setLogicCode("{\"type\": \"THRESHOLD_TRIGGER\"}");
        proposed.setStatus(RuleStatus.PROPOSED);
        proposed.setValidationStatus(ValidationStatus.PENDING);

        ruleRepository.saveAll(List.of(active, proposed));

        List<Rule> all = ruleRepository.findAll();
        assertEquals(2, all.size());
    }
}
