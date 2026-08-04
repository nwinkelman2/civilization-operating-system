package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.ChronicleEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CivilizationChronicleService {

    private static final Logger log = LoggerFactory.getLogger(CivilizationChronicleService.class);
    private final Map<String, ChronicleEntry> chronicleMap = new ConcurrentHashMap<>();

    public ChronicleEntry recordEntry(String entryId, long tick, String category, String description) {
        if (entryId == null || entryId.isBlank()) {
            entryId = UUID.randomUUID().toString();
        }
        ChronicleEntry entry = new ChronicleEntry(entryId, tick, category, description);
        chronicleMap.put(entryId, entry);
        return entry;
    }

    public ChronicleEntry recordEntry(long tick, String category, String description) {
        String entryId = "chronicle-" + UUID.randomUUID().toString();
        return recordEntry(entryId, tick, category, description);
    }

    public Optional<ChronicleEntry> getEntryById(String entryId) {
        if (entryId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(chronicleMap.get(entryId));
    }

    public List<ChronicleEntry> getChronicleEntries() {
        return new ArrayList<>(chronicleMap.values());
    }

    public List<ChronicleEntry> getAllEntries() {
        return getChronicleEntries();
    }

    public List<ChronicleEntry> getEntriesByCategory(String category) {
        if (category == null) {
            return Collections.emptyList();
        }
        return chronicleMap.values().stream()
                .filter(e -> category.equalsIgnoreCase(e.category()))
                .toList();
    }

    public List<ChronicleEntry> getEntriesByTickRange(long startTick, long endTick) {
        return chronicleMap.values().stream()
                .filter(e -> e.tick() >= startTick && e.tick() <= endTick)
                .sorted((a, b) -> Long.compare(a.tick(), b.tick()))
                .toList();
    }

    public void processChronicleTick() {
        log.info("[CIVILIZATION CHRONICLE TICK] Total Chronicle Entries Recorded: {}", chronicleMap.size());
    }
}
