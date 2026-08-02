package io.github.opencivilizationplatform.modules.civilization.application;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.CivilizationCreatedEvent;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.CitizenWallet;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenWalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CivilizationService {

    private final CivilizationRepository repository;
    private final EventBus eventBus;
    private final RuleRepository ruleRepository;
    private final CitizenRepository citizenRepository;
    private final CitizenWalletRepository citizenWalletRepository;

    public CivilizationService(CivilizationRepository repository,
                               EventBus eventBus,
                               RuleRepository ruleRepository,
                               CitizenRepository citizenRepository,
                               CitizenWalletRepository citizenWalletRepository) {
        this.repository = repository;
        this.eventBus = eventBus;
        this.ruleRepository = ruleRepository;
        this.citizenRepository = citizenRepository;
        this.citizenWalletRepository = citizenWalletRepository;
    }

    @Transactional(readOnly = true)
    public Page<Civilization> getAllCivilizations(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Civilization> getAllCivilizationsList() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Civilization> getCivilizationsByOwner(String ownerToken) {
        return repository.findByOwnerToken(ownerToken);
    }

    @Transactional
    public Civilization createCivilization(String name, CivilizationScale scale, String region, String ownerToken) {
        Civilization civ = new Civilization();
        civ.setName(name);
        civ.setScale(scale);
        civ.setRegion(region);
        civ.setOwnerToken(ownerToken);
        civ.setStatus(CivilizationStatus.EMERGING);

        Civilization savedCiv = repository.save(civ);

        eventBus.publish(new CivilizationCreatedEvent(
            "CivilizationService", savedCiv.getId(), savedCiv.getName(),
            savedCiv.getRegion(), savedCiv.getScale(), savedCiv.getOwnerToken()
        ));

        joinAsAgent(savedCiv.getId(), ownerToken);

        return savedCiv;
    }

    @Transactional
    public Civilization updateStatus(Long id, CivilizationStatus status) {
        Civilization civ = repository.findById(id).orElseThrow();
        civ.setStatus(status);
        civ.setLastActiveAt(LocalDateTime.now());
        return repository.save(civ);
    }

    @Transactional(readOnly = true)
    public Civilization getCivilization(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Civilization getCivilizationOrNull(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public void pingCivilization(Long id) {
        repository.findById(id).ifPresent(civ -> {
            civ.setLastActiveAt(LocalDateTime.now());
            repository.save(civ);
        });
    }

    @Transactional
    public Civilization joinAsAgent(Long civilizationId, String citizenId) {
        Civilization civ = repository.findById(civilizationId).orElseThrow();

        boolean isEntryCapActive = ruleRepository.findByCivilizationId(civilizationId).stream()
            .filter(r -> r.getStatus() == RuleStatus.ACTIVE)
            .anyMatch(r -> r.getLogicCode().contains("LOCK_ENTRY"));

        if (isEntryCapActive && civ.getHousing() != null && civ.getHousing() < 15.0) {
            throw new IllegalStateException("A admissão de novos agentes foi bloqueada temporariamente pelo Cortex devido a déficit crítico de moradia (< 15.0%).");
        }

        Citizen citizen = citizenRepository.findByCitizenId(citizenId)
            .orElseGet(() -> {
                Citizen c = new Citizen();
                c.setCitizenId(citizenId);
                c.setName("Agent " + citizenId.substring(Math.max(0, citizenId.length() - 6)));
                c.setReputationScore(50.0);
                return citizenRepository.save(c);
            });

        if (citizenId.equals(civ.getOwnerToken())) {
            citizen.setRole(Role.FOUNDER);
        } else if (citizen.getRole() == null || citizen.getRole() == Role.FOUNDER) {
            citizen.setRole(Role.CITIZEN);
        }

        boolean isFirstTimeJoin = citizen.getCivilization() == null || !civilizationId.equals(citizen.getCivilization().getId());
        citizen.setCivilization(civ);
        citizenRepository.save(citizen);

        if (citizen.getWallet() == null) {
            CitizenWallet wallet = new CitizenWallet();
            wallet.setCitizen(citizen);
            wallet.setFood(20.0);
            wallet.setWater(20.0);
            wallet.setMinerals(5.0);
            wallet.setEnergy(10.0);
            citizenWalletRepository.save(wallet);
            citizen.setWallet(wallet);
        }

        if (isFirstTimeJoin) {
            civ.setPopulation((civ.getPopulation() == null ? 100 : civ.getPopulation()) + 1);
        }
        civ.setLastActiveAt(LocalDateTime.now());
        return repository.save(civ);
    }
}
