package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public Page<Rule> getAllRules(Pageable pageable) {
        return ruleService.getAllRules(pageable);
    }

    @PostMapping("/{id}/vote")
    public Rule voteRule(@PathVariable Long id) {
        return ruleService.voteRule(id);
    }

    @PostMapping
    public Rule proposeRule(@Valid @RequestBody Rule rule) {
        return ruleService.proposeRule(rule);
    }
}
