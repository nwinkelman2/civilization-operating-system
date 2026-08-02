package io.github.opencivilizationplatform.modules.nexus.infrastructure;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeStatus;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NexusNodeRepository extends JpaRepository<NexusNode, Long> {
    List<NexusNode> findByCivilizationId(Long civilizationId);
    List<NexusNode> findByStatus(NexusNodeStatus status);
    List<NexusNode> findByCivilizationIdAndType(Long civilizationId, NexusNodeType type);
}

