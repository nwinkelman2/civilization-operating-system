package io.github.opencivilizationplatform.modules.social.api;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.social.domain.EspionageOperation;
import io.github.opencivilizationplatform.modules.social.infrastructure.EspionageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/espionage")
@Tag(name = "Esp espionage operations", description = "Clandestine Operations & Espionage management endpoints")
public class EspionageController {

    private final EspionageRepository espionageRepository;
    private final CivilizationRepository civilizationRepository;

    public EspionageController(EspionageRepository espionageRepository,
                               CivilizationRepository civilizationRepository) {
        this.espionageRepository = espionageRepository;
        this.civilizationRepository = civilizationRepository;
    }

    @PostMapping("/launch")
    @Operation(summary = "Launch a clandestine espionage operation against a target civilization")
    public EspionageOperation launchOperation(
            @RequestParam Long initiatorId,
            @RequestParam Long targetId,
            @RequestParam String type,
            @RequestParam Integer spyBotsCount
    ) {
        Civilization initiator = civilizationRepository.findById(initiatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Initiator civilization not found"));
        Civilization target = civilizationRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target civilization not found"));

        if (initiatorId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot target own civilization");
        }

        double energy = initiator.getEnergy() != null ? initiator.getEnergy() : 0.0;
        double minerals = initiator.getMinerals() != null ? initiator.getMinerals() : 0.0;
        if (energy < 15.0 || minerals < 20.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient energy (15.0) or minerals (20.0) to launch espionage operation");
        }

        // Deduct launch costs
        initiator.setEnergy(energy - 15.0);
        initiator.setMinerals(minerals - 20.0);
        civilizationRepository.save(initiator);

        EspionageOperation op = new EspionageOperation();
        op.setInitiator(initiator);
        op.setTarget(target);
        op.setType(type);
        op.setSpyBotsCount(spyBotsCount);
        op.setRiskLevel(1.5 + (spyBotsCount * 0.1));
        op.setStatus("IN_PROGRESS");
        op.setCreatedAt(LocalDateTime.now());
        op.setTicksRemaining(4); // Completes after 4 simulation ticks

        return espionageRepository.save(op);
    }

    @GetMapping("/{civId}/operations")
    @Operation(summary = "Get list of espionage operations related to a civilization")
    public List<EspionageOperation> getOperations(@PathVariable Long civId) {
        return espionageRepository.findByInitiatorIdOrTargetId(civId, civId);
    }
}
