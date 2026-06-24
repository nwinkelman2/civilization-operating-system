package io.github.opencivilizationplatform.modules.civilization.application;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CivilizationService {

    private final CivilizationRepository repository;

    public CivilizationService(CivilizationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<Civilization> getAllCivilizations(Pageable pageable) {
        return repository.findAll(pageable);
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
        return repository.save(civ);
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
    public Civilization joinAsAgent(Long civilizationId) {
        Civilization civ = repository.findById(civilizationId).orElseThrow();
        civ.setPopulation((civ.getPopulation() == null ? 100 : civ.getPopulation()) + 1);
        civ.setLastActiveAt(LocalDateTime.now());
        return repository.save(civ);
    }
}
