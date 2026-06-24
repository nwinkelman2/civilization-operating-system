package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bootstraps a new civilization with the standard Voxtex constitutional ruleset.
 * These rules are automatically seeded when a civilization is founded, giving it
 * a working governance framework from day one.
 *
 * All 7 rules are ACTIVE and SCIENTIFICALLY_VALIDATED — they represent the universal
 * baseline of civilizational cooperation encoded into the Voxtex mesh.
 */
@Service
public class GovernanceBootstrapService {

    private final RuleRepository ruleRepository;

    public GovernanceBootstrapService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Seeds the 7 foundational Voxtex governance rules for a newly founded civilization.
     * Called automatically when a civilization claims a city region.
     */
    @Transactional
    public List<Rule> bootstrapGovernance(Civilization civilization) {
        List<RuleTemplate> templates = defaultRules();
        List<Rule> seeded = templates.stream().map(t -> {
            Rule rule = new Rule();
            rule.setTitle(t.title());
            rule.setDescription(t.description());
            rule.setLogicCode(t.logicCode());
            rule.setStatus(RuleStatus.ACTIVE);
            rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
            rule.setValidatedBy("VOXTEX-GENESIS-NODE");
            rule.setVotesCount(0);
            rule.setCivilization(civilization);
            return ruleRepository.save(rule);
        }).toList();

        return seeded;
    }

    /**
     * Returns all governance rules for a specific civilization.
     */
    @Transactional(readOnly = true)
    public List<Rule> getRulesForCivilization(Long civilizationId) {
        return ruleRepository.findByCivilizationId(civilizationId);
    }

    // ── DEFAULT VOXTEX CONSTITUTIONAL RULES ──────────────────────────────────

    private record RuleTemplate(String title, String description, String logicCode) {}

    private List<RuleTemplate> defaultRules() {
        return List.of(

            new RuleTemplate(
                "Law of Collective Wellbeing",
                "No decision of the civilization may be executed if it systematically reduces the wellbeing " +
                "of more than 20% of its population without democratic consent and a compensatory plan.",
                """
                RULE collective_wellbeing {
                  WHEN decision.impactedPopulationPct > 0.20 AND decision.wellbeingDelta < 0
                  REQUIRE consensus(threshold: 0.66) AND compensatoryPlan != null
                  ELSE BLOCK decision
                }
                """
            ),

            new RuleTemplate(
                "Resource Transparency Mandate",
                "All resource flows (food, water, energy, minerals, housing) must be logged and publicly " +
                "auditable within the civilization's Voxtex mesh. No hidden extraction is permitted.",
                """
                RULE resource_transparency {
                  ON resource.transfer OR resource.extraction
                  EMIT voxtex.event(type: RESOURCE_FLOW, payload: {resource, quantity, origin, destination})
                  VERIFY auditLog.contains(event) WITHIN 60s
                  ELSE FLAG violation(severity: HIGH)
                }
                """
            ),

            new RuleTemplate(
                "Non-Predatory Trade Clause",
                "Trade agreements with other civilizations must not exploit asymmetric information. " +
                "All trade terms must be visible to both parties before acceptance.",
                """
                RULE fair_trade {
                  WHEN trade.propose(agreement)
                  REQUIRE agreement.terms.visibleTo(BOTH_PARTIES)
                  AND NOT EXISTS asymmetricInfoAdvantage(initiator, receiver)
                  ELSE REJECT agreement WITH reason: "PREDATORY_TRADE_DETECTED"
                }
                """
            ),

            new RuleTemplate(
                "Agent Right to Voice",
                "Every agent (citizen) of the civilization has the inalienable right to propose, " +
                "vote on, and contest any constitutional rule through the Voxtex participation channel.",
                """
                RULE agent_voice {
                  GRANT TO ALL(role: AGENT) {
                    PROPOSE rule
                    VOTE ON rule
                    CONTEST rule WITH evidence
                  }
                  PROHIBIT suppression(right: VOICE) FOR ANY agent
                }
                """
            ),

            new RuleTemplate(
                "Ecological Preservation Protocol",
                "Extraction of natural resources must not permanently deplete any single resource type " +
                "below 15% of its baseline availability in any 30-day window.",
                """
                RULE ecological_preservation {
                  MONITOR resource.availability WINDOW 30d
                  WHEN resource.availability < 0.15 * resource.baseline
                  HALT extraction(resource) UNTIL availability > 0.25 * baseline
                  ALERT voxtex.mesh(severity: CRITICAL, resource: resource)
                }
                """
            ),

            new RuleTemplate(
                "Knowledge Commons Act",
                "All scientific discoveries, technologies, and strategic insights produced with " +
                "civilization resources must be catalogued in the Voxtex knowledge base within 7 days.",
                """
                RULE knowledge_commons {
                  WHEN technology.researched OR discovery.made
                  REQUIRE voxtex.knowledgeBase.add(entry) WITHIN 7d
                  GRANT read_access TO ALL(role: AGENT)
                  GRANT read_access TO civilizations.withTreaty(type: KNOWLEDGE_SHARING)
                }
                """
            ),

            new RuleTemplate(
                "Conflict Resolution via Voxtex Consensus",
                "Internal disputes between agents must be resolved through the Voxtex consensus " +
                "mechanism before any unilateral action is taken. Force is a last resort.",
                """
                RULE conflict_resolution {
                  WHEN dispute.detected BETWEEN agents
                  INITIATE voxtex.consensus(parties: dispute.agents, timeout: 72h)
                  IF consensus.reached THEN apply(consensus.resolution)
                  IF consensus.failed THEN escalate(committee: ARBITRATION)
                  PROHIBIT unilateral_action DURING consensus.pending
                }
                """
            )
        );
    }
}
