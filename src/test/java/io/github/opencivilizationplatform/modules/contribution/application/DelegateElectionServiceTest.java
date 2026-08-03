package io.github.opencivilizationplatform.modules.contribution.application;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.DelegateVote;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.DelegateVoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DelegateElectionServiceTest {

    @Mock
    private CitizenRepository citizenRepository;
    @Mock
    private DelegateVoteRepository voteRepository;
    @Mock
    private CivilizationRepository civilizationRepository;
    @Mock
    private io.github.opencivilizationplatform.core.eventbus.EventBus eventBus;

    @InjectMocks
    private DelegateElectionService electionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEligibleCandidates() {
        Civilization civ = new Civilization();
        civ.setId(1L);

        Citizen c1 = new Citizen();
        c1.setId(10L);
        c1.setCivilization(civ);
        c1.setReputationScore(80.0);

        Citizen c2 = new Citizen();
        c2.setId(20L);
        c2.setCivilization(civ);
        c2.setReputationScore(20.0); // Below 40 threshold but should still show if candidates are limited

        when(citizenRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Citizen> result = electionService.getEligibleCandidates(1L);
        assertFalse(result.isEmpty());
        assertEquals(10L, result.get(0).getId()); // c1 has highest reputation
    }

    @Test
    void testVoteForDelegate() {
        Civilization civ = new Civilization();
        civ.setId(1L);

        Citizen voter = new Citizen();
        voter.setId(10L);
        voter.setCitizenId("VOTER-TOKEN");
        voter.setCivilization(civ);

        Citizen candidate = new Citizen();
        candidate.setId(20L);
        candidate.setCitizenId("CAND-TOKEN");
        candidate.setCivilization(civ);

        when(citizenRepository.findByCitizenId("VOTER-TOKEN")).thenReturn(Optional.of(voter));
        when(citizenRepository.findByCitizenId("CAND-TOKEN")).thenReturn(Optional.of(candidate));
        when(voteRepository.findByVoterCitizenIdAndSectorAndCivilizationId(10L, "FOOD", 1L))
            .thenReturn(Optional.empty());
        when(voteRepository.save(any(DelegateVote.class))).thenAnswer(i -> i.getArgument(0));

        DelegateVote vote = electionService.voteForDelegate("VOTER-TOKEN", "CAND-TOKEN", "FOOD", 1L);
        assertNotNull(vote);
        assertEquals(voter, vote.getVoter());
        assertEquals(candidate, vote.getCandidate());
        assertEquals("FOOD", vote.getSector());
        assertEquals(1L, vote.getCivilizationId());
    }

    @Test
    void testTallyVotesAndPromote() {
        Civilization civ = new Civilization();
        civ.setId(1L);

        Citizen winner = new Citizen();
        winner.setId(10L);
        winner.setCitizenId("WINNER-TOKEN");
        winner.setCivilization(civ);
        winner.setRole(Role.CITIZEN);

        Citizen voter = new Citizen();
        voter.setId(20L);
        voter.setCivilization(civ);

        DelegateVote vote = new DelegateVote();
        vote.setVoter(voter);
        vote.setCandidate(winner);
        vote.setSector("FOOD");
        vote.setCivilizationId(1L);

        when(voteRepository.findByCivilizationIdAndSector(1L, "FOOD")).thenReturn(Arrays.asList(vote));
        when(citizenRepository.findAll()).thenReturn(Arrays.asList(winner, voter));

        electionService.tallyVotesAndPromote(1L, "FOOD");

        assertEquals(Role.SECTOR_DELEGATE, winner.getRole());
        verify(citizenRepository, times(1)).save(winner);
        verify(voteRepository, times(1)).deleteByCivilizationIdAndSector(1L, "FOOD");
        verify(eventBus, times(1)).publish(any(io.github.opencivilizationplatform.core.eventbus.events.ElectionCompletedEvent.class));
    }
}
