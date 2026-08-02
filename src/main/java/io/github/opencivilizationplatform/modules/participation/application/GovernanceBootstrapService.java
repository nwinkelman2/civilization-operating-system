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
 * Bootstraps a new civilization with the standard Nexus constitutional ruleset.
 * These rules are automatically seeded when a civilization is founded, giving it
 * a working governance framework from day one.
 *
 * All 7 rules are ACTIVE and SCIENTIFICALLY_VALIDATED — they represent the universal
 * baseline of civilizational cooperation encoded into the Nexus mesh.
 */
@Service
public class GovernanceBootstrapService {

    private final RuleRepository ruleRepository;

    public GovernanceBootstrapService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Seeds the 7 foundational Nexus governance rules for a newly founded civilization.
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
            rule.setValidatedBy("Nexus-GENESIS-NODE");
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

    // ── DEFAULT Nexus CONSTITUTIONAL RULES ──────────────────────────────────

    private record RuleTemplate(String title, String description, String logicCode) {}

    private List<RuleTemplate> defaultRules() {
        return List.of(

            new RuleTemplate(
                "Law of Collective Wellbeing",
                "No decision of the civilization may be executed if it systematically reduces the wellbeing of more than 20% of its population without democratic consent and a compensatory plan.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"WELLBEING\", \"action\": \"BLOCK_DECISION\"}"
            ),

            new RuleTemplate(
                "Resource Transparency Mandate",
                "All resource flows (food, water, energy, minerals, housing) must be logged and publicly auditable within the civilization's Nexus mesh. No hidden extraction is permitted.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"RESOURCE_FLOW\", \"action\": \"AUDIT\"}"
            ),

            new RuleTemplate(
                "Non-Predatory Trade Clause",
                "Trade agreements with other civilizations must not exploit asymmetric information. All trade terms must be visible to both parties before acceptance.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"TRADE_BALANCE\", \"action\": \"MONITOR\"}"
            ),

            new RuleTemplate(
                "Agent Right to Voice",
                "Every agent (citizen) of the civilization has the inalienable right to propose, vote on, and contest any constitutional rule through the Nexus participation channel.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"PARTICIPATION\", \"action\": \"GRANT_ACCESS\"}"
            ),

            new RuleTemplate(
                "Ecological Preservation Protocol",
                "Extraction of natural resources must not permanently deplete any single resource type below 15% of its baseline availability in any 30-day window.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"BIOSPHERE_HEALTH\", \"action\": \"SUSPEND_PRODUCTION\"}"
            ),

            new RuleTemplate(
                "Knowledge Commons Act",
                "All scientific discoveries, technologies, and strategic insights produced with civilization resources must be catalogued in the Nexus knowledge base within 7 days.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"KNOWLEDGE\", \"action\": \"SHARE\"}"
            ),

            new RuleTemplate(
                "Conflict Resolution via Nexus Consensus",
                "Internal disputes between agents must be resolved through the Nexus consensus mechanism before any unilateral action is taken. Force is a last resort.",
                "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"SOCIAL_STABILITY\", \"action\": \"MEDIATE\"}"
            ),

            new RuleTemplate(
                "Birth Control Policy",
                "Se as reservas de alimento da civilização caírem abaixo de 30.0, o Cortex reduzirá a taxa de natalidade natural em 75% para conservar recursos.",
                "{\"type\": \"DEMOGRAPHIC_LIMIT\", \"metric\": \"FOOD\", \"threshold\": 30.0, \"action\": \"LIMIT_BIRTHS\"}"
            ),

            new RuleTemplate(
                "Agent Entry Cap Act",
                "Se a disponibilidade de moradia na região da civilização for menor que 15.0%, a admissão de novos agentes/jogadores será suspensa até que novos projetos habitacionais sejam construídos.",
                "{\"type\": \"DEMOGRAPHIC_LIMIT\", \"metric\": \"HOUSING\", \"threshold\": 15.0, \"action\": \"LOCK_ENTRY\"}"
            ),

            new RuleTemplate(
                "Emergency Agricultural Push",
                "Se as reservas de alimento caírem abaixo de 35.0, o Cortex ativará um subsídio agrícola de emergência adicionando +5.0 de comida por tick.",
                "{\"type\": \"PRODUCTION_BOOST\", \"metric\": \"FOOD\", \"threshold\": 35.0, \"action\": \"BOOST_AGRI\"}"
            ),

            new RuleTemplate(
                "Autonomous Robotic Labor Act",
                "Autoriza o Cortex a fabricar e operar robôs autônomos para exploração, agropecuária e manutenção de infraestrutura, gastando 15 minerais e 10 energia por robô, e consumindo 0.15 de energia por tick.",
                "{\"type\": \"AUTOMATION\", \"metric\": \"ENERGY\", \"action\": \"OPERATE_ROBOTS\"}"
            ),

            new RuleTemplate(
                "Inter-Cortex Mesh Barter Protocol",
                "Habilita o Cortex a realizar negociações e escambos autônomos com outros nós Cortex da rede mesh para importar recursos críticos em falta (< 30.0) em troca de excedentes físicos ou pessoal (população/trabalhadores).",
                "{\"type\": \"AUTOMATION\", \"metric\": \"RESOURCES\", \"action\": \"AUTONOMOUS_TRADE\"}"
            )
        );
    }
}

