package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    @Operation(summary = "Vote on a rule (weighted by role & sector)",
               description = "Delegate on matching sector = 5pts, Founder = 3pts, Coordinator = 4pts, Citizen = 1pt. Rule activates at 10pts.")
    public Rule voteRule(@PathVariable Long id, HttpServletRequest request) {
        String citizenId = resolveCitizenId(request);
        return ruleService.voteRule(id, citizenId);
    }

    @PostMapping
    @Operation(summary = "Propose a rule", description = "Creates a new rule proposal")
    public Rule proposeRule(@Valid @RequestBody Rule rule) {
        return ruleService.proposeRule(rule);
    }

    private String resolveCitizenId(HttpServletRequest request) {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId != null) return clientId;
        String token = request.getHeader("X-Client-Token");
        if (token != null && !token.isBlank()) return token;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return request.getRemoteAddr() + ":" + request.getRemotePort();
    }
}
