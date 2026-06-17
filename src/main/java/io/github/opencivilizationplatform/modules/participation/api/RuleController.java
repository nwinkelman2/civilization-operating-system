package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @GetMapping
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @PostMapping("/{id}/vote")
    public Rule voteRule(@PathVariable Long id) {
        Rule rule = ruleRepository.findById(id).orElseThrow();
        rule.setVotesCount(rule.getVotesCount() + 1);
        return ruleRepository.save(rule);
    }

    @PostMapping
    public Rule proposeRule(@RequestBody Rule rule) {
        rule.setVotesCount(0);
        rule.setStatus(RuleStatus.PROPOSED);
        return ruleRepository.save(rule);
    }
}
