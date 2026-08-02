package io.github.opencivilizationplatform.modules.participation.infrastructure;

import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
