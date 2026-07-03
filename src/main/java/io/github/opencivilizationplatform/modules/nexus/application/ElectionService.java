package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.modules.nexus.domain.*;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.ElectionRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.ElectionVoteRepository;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElectionService {

    private static final Logger log = LoggerFactory.getLogger(ElectionService.class);

    private final ElectionRepository electionRepository;
    private final ElectionVoteRepository electionVoteRepository;
    private final CivilizationRepository civilizationRepository;

    public ElectionService(ElectionRepository electionRepository,
                           ElectionVoteRepository electionVoteRepository,
                           CivilizationRepository civilizationRepository) {
        this.electionRepository = electionRepository;
        this.electionVoteRepository = electionVoteRepository;
        this.civilizationRepository = civilizationRepository;
    }

    @Transactional
    public Election openElection(Long civId) {
        Optional<Election> existing = electionRepository.findByCivilizationIdAndStatus(civId, ElectionStatus.OPEN);
        if (existing.isPresent()) return existing.get();

        Election election = new Election();
        election.setCivilizationId(civId);
        election.setStatus(ElectionStatus.OPEN);
        election.setTicksRemaining(5);
        election = electionRepository.save(election);
        log.info("[Election] Nova eleição aberta para civilização {}", civId);
        return election;
    }

    @Transactional
    public ElectionVote castVote(Long electionId, String voterName, String candidateName) {
        Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));

        if (election.getStatus() != ElectionStatus.OPEN) {
            throw new IllegalStateException("Esta eleição não está mais aberta.");
        }
        if (electionVoteRepository.existsByElectionIdAndVoterName(electionId, voterName)) {
            throw new IllegalStateException("Cidadão já votou nesta eleição.");
        }

        ElectionVote vote = new ElectionVote();
        vote.setElectionId(electionId);
        vote.setVoterName(voterName);
        vote.setCandidateName(candidateName);
        return electionVoteRepository.save(vote);
    }

    @Transactional
    public void tickElections() {
        List<Election> openElections = electionRepository.findAll().stream()
            .filter(e -> e.getStatus() == ElectionStatus.OPEN)
            .toList();

        for (Election election : openElections) {
            int remaining = election.getTicksRemaining() - 1;
            election.setTicksRemaining(remaining);
            if (remaining <= 0) {
                closeElection(election);
            } else {
                electionRepository.save(election);
            }
        }
    }

    private void closeElection(Election election) {
        election.setStatus(ElectionStatus.CLOSED);

        List<ElectionVote> votes = electionVoteRepository.findByElectionId(election.getId());
        if (!votes.isEmpty()) {
            Map<String, Long> tally = votes.stream()
                .collect(Collectors.groupingBy(ElectionVote::getCandidateName, Collectors.counting()));
            String winner = tally.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
            election.setWinnerName(winner);
            log.info("[Election] Eleição {} encerrada. Vencedor: {}", election.getId(), winner);
        }
        electionRepository.save(election);
    }

    public List<Election> getElectionsForCiv(Long civId) {
        return electionRepository.findByCivilizationId(civId);
    }

    public List<ElectionVote> getVotesForElection(Long electionId) {
        return electionVoteRepository.findByElectionId(electionId);
    }
}
