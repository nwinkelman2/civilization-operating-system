package io.github.opencivilizationplatform.modules.contribution.infrastructure;

import io.github.opencivilizationplatform.modules.contribution.domain.CitizenWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenWalletRepository extends JpaRepository<CitizenWallet, Long> {
    Optional<CitizenWallet> findByCitizenCitizenId(String citizenId);
}
