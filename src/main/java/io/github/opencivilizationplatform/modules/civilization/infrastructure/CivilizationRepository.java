package io.github.opencivilizationplatform.modules.civilization.infrastructure;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
    List<Civilization> findByOwnerToken(String ownerToken);
    Optional<Civilization> findByName(String name);
    List<Civilization> findByStatus(CivilizationStatus status);
}
