package io.github.opencivilizationplatform.modules.contribution.infrastructure;

import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    List<Contribution> findByCitizen_CitizenId(String citizenId);
}
