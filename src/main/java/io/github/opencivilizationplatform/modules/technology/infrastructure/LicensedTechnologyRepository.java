package io.github.opencivilizationplatform.modules.technology.infrastructure;

import io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LicensedTechnologyRepository extends JpaRepository<LicensedTechnology, Long> {
    List<LicensedTechnology> findByLicenseeId(Long licenseeId);
    List<LicensedTechnology> findByLicensorId(Long licensorId);
}
