package io.github.opencivilizationplatform.modules.contribution.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.DelegateVote;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.DelegateVoteRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.ElectionCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DelegateElectionService {

    private static final Logger log = LoggerFactory.getLogger(DelegateElectionService.class);

    private final CitizenRepository citizenRepository;
    private final DelegateVoteRepository voteRepository;
    private final CivilizationRepository civilizationRepository;
    private final EventBus eventBus;

    public DelegateElectionService(CitizenRepository citizenRepository,
                                   DelegateVoteRepository voteRepository,
                                   CivilizationRepository civilizationRepository,
                                   EventBus eventBus) {
        this.citizenRepository = citizenRepository;
        this.voteRepository = voteRepository;
        this.civilizationRepository = civilizationRepository;
        this.eventBus = eventBus;
    }

    @Transactional(readOnly = true)
    public List<Citizen> getEligibleCandidates(Long civilizationId) {
        // List citizens of this civilization, sorted by reputation score, filter candidates with score > 40.0 (or top 5 if empty)
        List<Citizen> allCitizens = citizenRepository.findAll().stream()
            .filter(c -> c.getCivilization() != null && civilizationId.equals(c.getCivilization().getId()))
            .sorted(Comparator.comparing(Citizen::getReputationScore, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());

        List<Citizen> candidates = allCitizens.stream()
            .filter(c -> c.getReputationScore() != null && c.getReputationScore() >= 40.0)
            .collect(Collectors.toList());

        return candidates.isEmpty() ? allCitizens.stream().limit(5).collect(Collectors.toList()) : candidates;
    }

    @Transactional
    public DelegateVote voteForDelegate(String voterToken, String candidateCitizenId, String sector, Long civilizationId) {
        Citizen voter = citizenRepository.findByCitizenId(voterToken)
            .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterToken));

        Citizen candidate = citizenRepository.findByCitizenId(candidateCitizenId)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateCitizenId));

        if (voter.getCivilization() == null || !civilizationId.equals(voter.getCivilization().getId())) {
            throw new IllegalStateException("Voter is not part of this civilization.");
        }
        if (candidate.getCivilization() == null || !civilizationId.equals(candidate.getCivilization().getId())) {
            throw new IllegalStateException("Candidate is not part of this civilization.");
        }

        // Allow changing vote: if already voted for this sector, update it
        Optional<DelegateVote> existingVote = voteRepository.findByVoterCitizenIdAndSectorAndCivilizationId(voter.getId(), sector, civilizationId);
        DelegateVote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            vote.setCandidate(candidate);
        } else {
            vote = new DelegateVote();
            vote.setVoter(voter);
            vote.setCandidate(candidate);
            vote.setSector(sector);
            vote.setCivilizationId(civilizationId);
        }
        return voteRepository.save(vote);
    }

    @Transactional
    public void tallyVotesAndPromote(Long civilizationId, String sector) {
        List<DelegateVote> votes = voteRepository.findByCivilizationIdAndSector(civilizationId, sector);
        if (votes.isEmpty()) {
            return;
        }

        // Tally votes
        Map<Citizen, Long> tally = votes.stream()
            .collect(Collectors.groupingBy(DelegateVote::getCandidate, Collectors.counting()));

        Citizen winner = tally.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        if (winner != null) {
            log.info("Election Tally resolved for Civ {} Sector {}: Winner is {} with {} votes",
                civilizationId, sector, winner.getName(), tally.get(winner));

            // Reset former delegate of this civilization to CITIZEN if they are not the founder
            citizenRepository.findAll().stream()
                .filter(c -> c.getCivilization() != null && civilizationId.equals(c.getCivilization().getId()))
                .filter(c -> c.getRole() == Role.SECTOR_DELEGATE)
                .forEach(c -> {
                    c.setRole(Role.CITIZEN);
                    citizenRepository.save(c);
                });

            // Promote winner to SECTOR_DELEGATE
            if (winner.getRole() != Role.FOUNDER) {
                winner.setRole(Role.SECTOR_DELEGATE);
                citizenRepository.save(winner);
            }

            // Clear votes to start next cycle
            voteRepository.deleteByCivilizationIdAndSector(civilizationId, sector);

            eventBus.publish(new ElectionCompletedEvent(
                "DelegateElectionService",
                votes.get(0).getId(),
                civilizationId,
                winner.getId()
            ));
        }
    }

    @Scheduled(fixedDelay = 60000) // Runs every 60 seconds
    @Transactional
    public void autoTallyElections() {
        log.debug("Auto tallying delegate elections...");
        civilizationRepository.findAll().forEach(civ -> {
            tallyVotesAndPromote(civ.getId(), "FOOD");
            tallyVotesAndPromote(civ.getId(), "ENERGY");
            tallyVotesAndPromote(civ.getId(), "TECH");
            tallyVotesAndPromote(civ.getId(), "LOGISTICS");
        });
    }
}
