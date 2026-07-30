package io.github.opencivilizationplatform.modules.contribution.infrastructure;

import io.github.opencivilizationplatform.modules.contribution.domain.DelegateVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DelegateVoteRepository extends JpaRepository<DelegateVote, Long> {
    List<DelegateVote> findByCivilizationIdAndSector(Long civilizationId, String sector);
    Optional<DelegateVote> findByVoterCitizenIdAndSectorAndCivilizationId(Long voterId, String sector, Long civilizationId);
    void deleteByCivilizationIdAndSector(Long civilizationId, String sector);
}
