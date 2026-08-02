package io.github.opencivilizationplatform.modules.events.application;

import io.github.opencivilizationplatform.modules.events.domain.*;
import io.github.opencivilizationplatform.modules.events.infrastructure.GameEventRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final GameEventRepository eventRepository;
    private final CivilizationRepository civRepository;
    private final Random random = new Random();

    public EventService(GameEventRepository eventRepository, CivilizationRepository civRepository) {
        this.eventRepository = eventRepository;
        this.civRepository = civRepository;
    }

    @Transactional
    public GameEvent createEvent(String title, String description, EventType type,
                                  EventSeverity severity, Long targetCivId, String effectJson) {
        GameEvent event = new GameEvent();
        event.setTitle(title);
        event.setDescription(description);
        event.setType(type);
        event.setSeverity(severity);
        event.setTargetCivilizationId(targetCivId);
        event.setEffectJson(effectJson);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<GameEvent> getEventsForCivilization(Long civId) {
        return eventRepository.findByTargetCivilizationIdOrderByCreatedAtDesc(civId);
    }

    @Transactional(readOnly = true)
    public List<GameEvent> getUnresolvedEvents() {
        return eventRepository.findByResolvedFalse();
    }

    @Transactional
    public void resolveEvent(Long eventId) {
        eventRepository.findById(eventId).ifPresent(e -> {
            e.setResolved(true);
            eventRepository.save(e);
        });
    }

    @Transactional
    @Scheduled(fixedRate = 30000)
    public void generateRandomEvents() {
        var civilizations = civRepository.findAll();
        if (civilizations.isEmpty()) return;

        // 20% chance of an event each tick
        if (random.nextDouble() < 0.2) {
            var civ = civilizations.get(random.nextInt(civilizations.size()));
            var types = EventType.values();
            var severities = EventSeverity.values();
            var type = types[random.nextInt(types.length)];
            var severity = severities[random.nextInt(severities.length)];

            String title = switch (type) {
                case NATURAL_DISASTER -> severity == EventSeverity.CATASTROPHIC ?
                    "Catastrophic Earthquake" : "Minor Flooding";
                case DISCOVERY -> "Ancient Ruins Discovered";
                case DIPLOMATIC_CRISIS -> "Border Dispute Arises";
                case TECH_BREAKTHROUGH -> "Scientific Breakthrough";
                case RESOURCE_BOON -> "Rich Resource Vein Found";
                case TRADE_OPPORTUNITY -> "Trade Route Established";
                case BIOSPHERE_SHIFT -> "Climate Pattern Shift Detected";
                case SOCIAL_MOVEMENT -> "Community Initiative Forms";
            };

            String description = switch (type) {
                case NATURAL_DISASTER -> "A natural disaster has struck the region. Infrastructure damage reported.";
                case DISCOVERY -> "Citizens have discovered ancient technology that could advance research.";
                case DIPLOMATIC_CRISIS -> "Tensions rise as territorial boundaries are contested.";
                case TECH_BREAKTHROUGH -> "A new scientific discovery promises to boost resource efficiency.";
                case RESOURCE_BOON -> "Unexpected resource deposits found within your territory.";
                case TRADE_OPPORTUNITY -> "A neighboring civilization proposes a mutually beneficial trade.";
                case BIOSPHERE_SHIFT -> "Environmental changes require adaptation of your infrastructure.";
                case SOCIAL_MOVEMENT -> "Citizens organize a grassroots initiative to improve quality of life.";
            };

            createEvent(title, description, type, severity, civ.getId(),
                "{\"type\":\"" + type + "\",\"severity\":\"" + severity + "\"}");

            log.info("EVENT: {} - {} (severity: {})", type, title, severity);
        }
    }
}
