package io.github.opencivilizationplatform.modules.nexus.infrastructure;

import io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeshTradeRepository extends JpaRepository<MeshTrade, Long> {
    List<MeshTrade> findAllByOrderByCreatedAtDesc();
}
