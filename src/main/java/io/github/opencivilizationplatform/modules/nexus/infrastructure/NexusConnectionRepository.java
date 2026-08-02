package io.github.opencivilizationplatform.modules.nexus.infrastructure;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusConnection;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NexusConnectionRepository extends JpaRepository<NexusConnection, Long> {
    List<NexusConnection> findByNodeAOrNodeB(NexusNode nodeA, NexusNode nodeB);
    Optional<NexusConnection> findByNodeAAndNodeB(NexusNode nodeA, NexusNode nodeB);
    long countByNodeAOrNodeB(NexusNode nodeA, NexusNode nodeB);
}

