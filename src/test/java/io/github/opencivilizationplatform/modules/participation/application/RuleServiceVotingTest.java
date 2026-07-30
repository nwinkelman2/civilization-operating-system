package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleServiceVotingTest {

    @Mock
    private RuleRepository ruleRepository;
    @Mock
    private CitizenRepository citizenRepository;

    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleService = new RuleService(ruleRepository, citizenRepository);
    }

    private Rule createProposedRule(String sector) {
        Rule rule = new Rule();
        rule.setId(1L);
        rule.setTitle("Test Rule");
        rule.setDescription("Test description");
        rule.setLogicCode("{}");
        rule.setStatus(RuleStatus.PROPOSED);
        rule.setValidationStatus(ValidationStatus.PENDING);
        rule.setVotesCount(0);
        rule.setSector(sector);
        return rule;
    }

    @Test
    void testCitizenVoteWeightIsOne() {
        Rule rule = createProposedRule("ENERGY");
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Citizen citizen = new Citizen();
        citizen.setCitizenId("cit-001");
        citizen.setRole(Role.CITIZEN);
        when(citizenRepository.findByCitizenId("cit-001")).thenReturn(Optional.of(citizen));

        Rule result = ruleService.voteRule(1L, "cit-001");
        assertEquals(1, result.getVotesCount(), "Citizen vote should add weight 1");
        assertEquals(RuleStatus.PROPOSED, result.getStatus()); // not yet activated
    }

    @Test
    void testFounderVoteWeightIsThree() {
        Rule rule = createProposedRule("FOOD");
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Citizen founder = new Citizen();
        founder.setCitizenId("founder-001");
        founder.setRole(Role.FOUNDER);
        when(citizenRepository.findByCitizenId("founder-001")).thenReturn(Optional.of(founder));

        Rule result = ruleService.voteRule(1L, "founder-001");
        assertEquals(3, result.getVotesCount(), "Founder vote should add weight 3");
    }

    @Test
    void testSectorDelegateMatchingSectorWeightIsFive() {
        Rule rule = createProposedRule("ENERGY");
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Citizen delegate = new Citizen();
        delegate.setCitizenId("delegate-001");
        delegate.setRole(Role.SECTOR_DELEGATE);
        delegate.setInterests(List.of("ENERGY", "LOGISTICS"));
        when(citizenRepository.findByCitizenId("delegate-001")).thenReturn(Optional.of(delegate));

        Rule result = ruleService.voteRule(1L, "delegate-001");
        assertEquals(5, result.getVotesCount(), "Sector delegate matching sector should add weight 5");
    }

    @Test
    void testSectorDelegateNonMatchingSectorWeightIsTwo() {
        Rule rule = createProposedRule("FOOD");
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Citizen delegate = new Citizen();
        delegate.setCitizenId("delegate-002");
        delegate.setRole(Role.SECTOR_DELEGATE);
        delegate.setInterests(List.of("ENERGY")); // delegate of ENERGY, not FOOD
        when(citizenRepository.findByCitizenId("delegate-002")).thenReturn(Optional.of(delegate));

        Rule result = ruleService.voteRule(1L, "delegate-002");
        assertEquals(2, result.getVotesCount(), "Sector delegate on non-matching sector should add weight 2");
    }

    @Test
    void testRuleActivatesWhenVotesCrossThreshold() {
        Rule rule = createProposedRule("TECH");
        rule.setVotesCount(7); // needs 3 more (from a Founder with weight 3)
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Citizen founder = new Citizen();
        founder.setCitizenId("founder-002");
        founder.setRole(Role.FOUNDER);
        when(citizenRepository.findByCitizenId("founder-002")).thenReturn(Optional.of(founder));

        Rule result = ruleService.voteRule(1L, "founder-002");
        assertEquals(10, result.getVotesCount());
        assertEquals(RuleStatus.ACTIVE, result.getStatus(), "Rule should be activated when votes reach 10");
        assertEquals(ValidationStatus.SCIENTIFICALLY_VALIDATED, result.getValidationStatus());
        assertEquals("Nexus Sectoral Consensus", result.getValidatedBy());
    }
}
