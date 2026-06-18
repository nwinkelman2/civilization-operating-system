package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rules")
@Tag(name = "Rules", description = "Rule management endpoints")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    @Operation(summary = "List all rules", description = "Returns a paginated list of rules")
    public Page<Rule> getAllRules(Pageable pageable) {
        return ruleService.getAllRules(pageable);
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Vote on a rule", description = "Records a vote for a rule by ID")
    public Rule voteRule(@PathVariable Long id) {
        return ruleService.voteRule(id);
    }

    @PostMapping
    @Operation(summary = "Propose a rule", description = "Creates a new rule proposal")
    public Rule proposeRule(@Valid @RequestBody Rule rule) {
        return ruleService.proposeRule(rule);
    }
}
