package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public List<Rule> getValidatedRules() {
        return ruleRepository.findAll().stream()
                .filter(rule -> RuleStatus.ACTIVE.equals(rule.getStatus()) && ValidationStatus.SCIENTIFICALLY_VALIDATED.equals(rule.getValidationStatus()))
                .toList();
    }

    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }
}
