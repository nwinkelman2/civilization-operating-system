package io.github.opencivilizationplatform.modules.nexus.infrastructure;

import io.github.opencivilizationplatform.modules.nexus.domain.ElectionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ElectionVoteRepository extends JpaRepository<ElectionVote, Long> {
    List<ElectionVote> findByElectionId(Long electionId);
    boolean existsByElectionIdAndVoterName(Long electionId, String voterName);
}
