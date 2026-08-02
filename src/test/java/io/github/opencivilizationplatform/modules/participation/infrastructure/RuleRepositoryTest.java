package io.github.opencivilizationplatform.modules.participation.infrastructure;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleRepositoryTest {

    private RuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        ruleRepository = mock(RuleRepository.class);
    }

    @Test
    void testSaveAndFindWithEnums() {
        Rule rule = new Rule();
        rule.setTitle("Test Rule");
        rule.setDescription("A rule with enum fields");
        rule.setLogicCode("{\"type\": \"THRESHOLD_TRIGGER\"}");
        rule.setStatus(RuleStatus.ACTIVE);
        rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
        rule.setVotesCount(100);

        when(ruleRepository.save(any(Rule.class))).thenAnswer(i -> {
            Rule r = i.getArgument(0);
            if (r.getId() == null) r.setId(1L);
            return r;
        });
        when(ruleRepository.findById(any())).thenAnswer(i -> {
            Rule r = new Rule();
            r.setId(i.getArgument(0));
            r.setTitle("Test Rule");
            r.setStatus(RuleStatus.ACTIVE);
            r.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
            r.setVotesCount(100);
            return Optional.of(r);
        });

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

        when(ruleRepository.saveAll(any())).thenReturn(List.of(active, proposed));
        when(ruleRepository.findAll()).thenReturn(List.of(active, proposed));

        ruleRepository.saveAll(List.of(active, proposed));

        List<Rule> all = ruleRepository.findAll();
        assertEquals(2, all.size());
    }
}
