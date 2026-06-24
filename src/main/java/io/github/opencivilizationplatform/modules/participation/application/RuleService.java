package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Page<Rule> getAllRules(Pageable pageable) {
        return ruleRepository.findAll(pageable);
    }

    public List<Rule> getValidatedRules() {
        return ruleRepository.findAll().stream()
                .filter(rule -> RuleStatus.ACTIVE.equals(rule.getStatus()) && ValidationStatus.SCIENTIFICALLY_VALIDATED.equals(rule.getValidationStatus()))
                .toList();
    }

    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public Rule voteRule(Long id) {
        Rule rule = ruleRepository.findById(id).orElseThrow();
        rule.setVotesCount((rule.getVotesCount() == null ? 0 : rule.getVotesCount()) + 1);
        if (RuleStatus.PROPOSED.equals(rule.getStatus()) && rule.getVotesCount() >= 3) {
            rule.setStatus(RuleStatus.ACTIVE);
            rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
            rule.setValidatedBy("Voxtex Consensus");
        }
        return ruleRepository.save(rule);
    }

    public Rule proposeRule(Rule rule) {
        rule.setVotesCount(0);
        rule.setStatus(RuleStatus.PROPOSED);
        return ruleRepository.save(rule);
    }

    public Rule proposeRuleForCivilization(Rule rule, io.github.opencivilizationplatform.modules.civilization.domain.Civilization civ) {
        rule.setCivilization(civ);
        rule.setVotesCount(0);
        rule.setStatus(RuleStatus.PROPOSED);
        rule.setValidationStatus(ValidationStatus.PENDING);
        return ruleRepository.save(rule);
    }
}
