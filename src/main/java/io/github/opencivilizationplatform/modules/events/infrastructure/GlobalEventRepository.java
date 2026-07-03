package io.github.opencivilizationplatform.modules.events.infrastructure;

import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GlobalEventRepository extends JpaRepository<GlobalEvent, Long> {
    List<GlobalEvent> findByActiveTrue();
}
