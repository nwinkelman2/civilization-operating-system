package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRules() {
        Rule rule1 = new Rule();
        Rule rule2 = new Rule();
        when(ruleRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(rule1, rule2)));

        Page<Rule> result = ruleService.getAllRules(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(ruleRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetValidatedRules() {
        Rule activeValidated = new Rule();
        activeValidated.setStatus(RuleStatus.ACTIVE);
        activeValidated.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);

        Rule deprecated = new Rule();
        deprecated.setStatus(RuleStatus.DEPRECATED);
        deprecated.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);

        Rule unvalidated = new Rule();
        unvalidated.setStatus(RuleStatus.ACTIVE);
        unvalidated.setValidationStatus(ValidationStatus.PENDING);

        List<Rule> allRules = List.of(activeValidated, deprecated, unvalidated);
        when(ruleRepository.findAll()).thenReturn(allRules);

        List<Rule> result = ruleService.getValidatedRules();

        assertEquals(1, result.size());
        assertEquals(RuleStatus.ACTIVE, result.get(0).getStatus());
        assertEquals(ValidationStatus.SCIENTIFICALLY_VALIDATED, result.get(0).getValidationStatus());
    }

    @Test
    void testSaveRule() {
        Rule rule = new Rule();
        rule.setTitle("Ocean Preservation Rule");
        when(ruleRepository.save(any(Rule.class))).thenReturn(rule);

        Rule saved = ruleService.saveRule(rule);

        assertNotNull(saved);
        assertEquals("Ocean Preservation Rule", saved.getTitle());
    }
}
