package io.github.opencivilizationplatform.modules.technology.infrastructure;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    List<Technology> findByCivilizationId(Long civilizationId);
    List<Technology> findByCivilizationIdAndStatus(Long civilizationId, TechnologyStatus status);
    List<Technology> findByCategory(TechnologyCategory category);
}
