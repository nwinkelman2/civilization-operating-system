package io.github.opencivilizationplatform.modules.contribution.infrastructure;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByCitizenId(String citizenId);
}
