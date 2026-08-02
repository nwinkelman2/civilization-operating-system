package io.github.opencivilizationplatform.modules.events.infrastructure;
import io.github.opencivilizationplatform.modules.events.domain.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameEventRepository extends JpaRepository<GameEvent, Long> {
    List<GameEvent> findByTargetCivilizationIdOrderByCreatedAtDesc(Long civId);
    List<GameEvent> findByResolvedFalse();
}
