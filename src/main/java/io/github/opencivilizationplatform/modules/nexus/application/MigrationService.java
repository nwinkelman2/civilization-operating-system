package io.github.opencivilizationplatform.modules.nexus.application;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.MigrationRequest;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MigrationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MigrationService {

    private final MigrationRequestRepository migrationRequestRepository;
    private final CivilizationRepository civilizationRepository;
    private final ObjectMapper objectMapper;

    public MigrationService(MigrationRequestRepository migrationRequestRepository,
                            CivilizationRepository civilizationRepository,
                            ObjectMapper objectMapper) {
        this.migrationRequestRepository = migrationRequestRepository;
        this.civilizationRepository = civilizationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MigrationRequest applyMigration(String citizenName, Long fromCivId, Long toCivId, String reason) {
        Civilization fromCiv = civilizationRepository.findById(fromCivId)
                .orElseThrow(() -> new IllegalArgumentException("Origem inválida"));
        Civilization toCiv = civilizationRepository.findById(toCivId)
                .orElseThrow(() -> new IllegalArgumentException("Destino inválido"));

        MigrationRequest req = new MigrationRequest();
        req.setCitizenName(citizenName);
        req.setFromCivilization(fromCiv);
        req.setToCivilization(toCiv);
        req.setStatus("PENDING");
        req.setReason(reason);

        return migrationRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public List<MigrationRequest> listPending(Long toCivId) {
        return migrationRequestRepository.findByToCivilizationIdAndStatus(toCivId, "PENDING");
    }

    @Transactional(readOnly = true)
    public List<MigrationRequest> listAll() {
        return migrationRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public MigrationRequest approveMigration(Long id) {
        MigrationRequest req = migrationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Requisição não encontrada"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("Esta migração já foi processada");
        }

        req.setStatus("APPROVED");

        Civilization fromCiv = req.getFromCivilization();
        Civilization toCiv = req.getToCivilization();

        fromCiv.setPopulation(Math.max(0, (fromCiv.getPopulation() != null ? fromCiv.getPopulation() : 10) - 1));
        toCiv.setPopulation((toCiv.getPopulation() != null ? toCiv.getPopulation() : 10) + 1);

        addLogToHistory(fromCiv, "[Migração] Cidadão '" + req.getCitizenName() + "' transferiu-se para a sociedade '" + toCiv.getName() + "'.");
        addLogToHistory(toCiv, "[Migração] Cidadão '" + req.getCitizenName() + "' foi recebido vindo da sociedade '" + fromCiv.getName() + "'.");

        civilizationRepository.save(fromCiv);
        civilizationRepository.save(toCiv);

        return migrationRequestRepository.save(req);
    }

    @Transactional
    public MigrationRequest rejectMigration(Long id) {
        MigrationRequest req = migrationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Requisição não encontrada"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("Esta migração já foi processada");
        }

        req.setStatus("REJECTED");
        return migrationRequestRepository.save(req);
    }

    private void addLogToHistory(Civilization civ, String logMsg) {
        try {
            ArrayNode historyArray;
            if (civ.getResourceHistory() == null || civ.getResourceHistory().isBlank() || civ.getResourceHistory().equals("[]")) {
                historyArray = objectMapper.createArrayNode();
            } else {
                historyArray = (ArrayNode) objectMapper.readTree(civ.getResourceHistory());
            }

            if (historyArray.size() > 0) {
                ObjectNode lastTick = (ObjectNode) historyArray.get(historyArray.size() - 1);
                ArrayNode logs;
                if (lastTick.has("logs")) {
                    logs = (ArrayNode) lastTick.get("logs");
                } else {
                    logs = objectMapper.createArrayNode();
                    lastTick.set("logs", logs);
                }
                logs.add(logMsg);
            } else {
                ObjectNode newTick = objectMapper.createObjectNode();
                newTick.put("tick", 1);
                newTick.put("pop", civ.getPopulation() != null ? civ.getPopulation() : 100);
                newTick.put("food", civ.getFood() != null ? civ.getFood() : 100.0);
                newTick.put("water", civ.getWater() != null ? civ.getWater() : 100.0);
                newTick.put("minerals", civ.getMinerals() != null ? civ.getMinerals() : 50.0);
                newTick.put("energy", civ.getEnergy() != null ? civ.getEnergy() : 75.0);
                newTick.put("housing", civ.getHousing() != null ? civ.getHousing() : 50.0);
                newTick.put("agriBots", 0);
                newTick.put("aquaBots", 0);
                newTick.put("exploreBots", 0);
                newTick.put("utilityBots", 0);

                ArrayNode logs = objectMapper.createArrayNode();
                logs.add(logMsg);
                newTick.set("logs", logs);
                historyArray.add(newTick);
            }
            civ.setResourceHistory(objectMapper.writeValueAsString(historyArray));
        } catch (Exception e) {
            // ignore
        }
    }
}
