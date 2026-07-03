package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final CitizenRepository citizenRepository;

    public RuleService(RuleRepository ruleRepository, CitizenRepository citizenRepository) {
        this.ruleRepository = ruleRepository;
        this.citizenRepository = citizenRepository;
    }

    public Page<Rule> getAllRules(Pageable pageable) {
        return ruleRepository.findAll(pageable);
    }

    public List<Rule> getRulesByCivilization(Long civilizationId) {
        return ruleRepository.findByCivilizationId(civilizationId);
    }

    public List<Rule> getValidatedRules() {
        return ruleRepository.findAll().stream()
                .filter(rule -> RuleStatus.ACTIVE.equals(rule.getStatus()) && ValidationStatus.SCIENTIFICALLY_VALIDATED.equals(rule.getValidationStatus()))
                .toList();
    }

    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    /**
     * Weighted vote: SECTOR_DELEGATE voting on their matching sector = 5 pts.
     * FOUNDER = 3 pts. NEXUS_COORDINATOR = 4 pts. CITIZEN = 1 pt.
     * Rule activates when votesCount >= 10.
     */
    public Rule voteRule(Long id, String citizenId) {
        Rule rule = ruleRepository.findById(id).orElseThrow();

        int weight = resolveVoteWeight(citizenId, rule.getSector());
        int currentVotes = rule.getVotesCount() == null ? 0 : rule.getVotesCount();
        rule.setVotesCount(currentVotes + weight);

        if (RuleStatus.PROPOSED.equals(rule.getStatus()) && rule.getVotesCount() >= 10) {
            rule.setStatus(RuleStatus.ACTIVE);
            rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
            rule.setValidatedBy("Nexus Sectoral Consensus");
        }
        return ruleRepository.save(rule);
    }

    // Backward-compatible overload (anonymous vote = weight 1)
    public Rule voteRule(Long id) {
        return voteRule(id, null);
    }

    private int resolveVoteWeight(String citizenId, String sector) {
        if (citizenId == null || citizenId.isBlank()) return 1;
        Optional<Citizen> citizenOpt = citizenRepository.findByCitizenId(citizenId);
        if (citizenOpt.isEmpty()) return 1;

        Citizen citizen = citizenOpt.get();
        Role role = citizen.getRole();
        if (role == null) return 1;

        return switch (role) {
            case SECTOR_DELEGATE -> {
                // Full weight only if the delegate's area matches the rule sector
                List<String> interests = citizen.getInterests();
                boolean sectorMatch = interests != null &&
                        interests.stream().anyMatch(i -> i.equalsIgnoreCase(sector));
                yield sectorMatch ? 5 : 2;
            }
            case FOUNDER -> 3;
            case NEXUS_COORDINATOR -> 4;
            default -> 1;
        };
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
