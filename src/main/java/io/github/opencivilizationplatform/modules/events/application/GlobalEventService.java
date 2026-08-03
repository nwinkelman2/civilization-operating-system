package io.github.opencivilizationplatform.modules.events.application;

import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEventType;
import io.github.opencivilizationplatform.modules.events.infrastructure.GlobalEventRepository;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.GlobalEventOccurredEvent;

@Service
public class GlobalEventService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GlobalEventService.class);
    private final GlobalEventRepository globalEventRepository;
    private final EventBus eventBus;
    private final Random random = new Random();

    public GlobalEventService(GlobalEventRepository globalEventRepository, EventBus eventBus) {
        this.globalEventRepository = globalEventRepository;
        this.eventBus = eventBus;
    }

    public List<GlobalEvent> getActiveEvents() {
        return globalEventRepository.findByActiveTrue();
    }

    @Transactional
    public void tickEvents() {
        List<GlobalEvent> active = globalEventRepository.findByActiveTrue();
        for (GlobalEvent event : active) {
            int remaining = event.getTicksRemaining() - 1;
            event.setTicksRemaining(remaining);
            if (remaining <= 0) {
                event.setActive(false);
                log.info("[GlobalEvent] Evento '{}' expirou.", event.getType());
            }
            globalEventRepository.save(event);
        }
    }

    @Transactional
    public GlobalEvent maybeGenerateEvent(List<Long> availableCivIds) {
        if (random.nextDouble() > 0.08 || availableCivIds.isEmpty()) return null;

        GlobalEventType[] types = GlobalEventType.values();
        GlobalEventType type = types[random.nextInt(types.length)];

        int numAffected = Math.min(availableCivIds.size(), 1 + random.nextInt(3));
        List<Long> affected = availableCivIds.stream()
            .sorted((a, b) -> random.nextInt(3) - 1)
            .limit(numAffected)
            .collect(Collectors.toList());

        String affectedJson = "[" + affected.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";

        GlobalEvent event = new GlobalEvent();
        event.setType(type);
        event.setAffectedCivIds(affectedJson);
        event.setActive(true);
        event.setTicksRemaining(getDefaultDuration(type));
        event.setDescription(buildDescription(type));

        event = globalEventRepository.save(event);
        log.info("[GlobalEvent] Novo evento mundial gerado: {} afetando civs {}", type, affectedJson);

        eventBus.publish(new GlobalEventOccurredEvent(
            "GlobalEventService",
            event.getId(),
            event.getDescription(),
            event.getType() != null ? event.getType().name() : "UNKNOWN",
            "HIGH"
        ));
        return event;
    }

    public boolean isAffected(GlobalEvent event, Long civId) {
        if (event.getAffectedCivIds() == null) return false;
        return event.getAffectedCivIds().contains(String.valueOf(civId));
    }

    public void applyEventEffects(GlobalEvent event, Civilization civ, double[] resourceDelta, double[] modifiers) {
        switch (event.getType()) {
            case DROUGHT -> {
                modifiers[0] -= 0.30;
                modifiers[1] -= 0.30;
            }
            case EPIDEMIC -> {
                modifiers[4] -= 5.0;
                modifiers[2] = Math.max(modifiers[2], 0.80);
            }
            case TECH_DISCOVERY -> {
                modifiers[5] += 50.0;
            }
            case SOLAR_STORM -> {
                modifiers[2] = -1.0;
            }
            case TRADE_BOOM -> {
                modifiers[3] += 0.20;
            }
        }
    }

    private int getDefaultDuration(GlobalEventType type) {
        return switch (type) {
            case DROUGHT -> 3;
            case EPIDEMIC -> 5;
            case TECH_DISCOVERY -> 1;
            case SOLAR_STORM -> 2;
            case TRADE_BOOM -> 3;
        };
    }

    private String buildDescription(GlobalEventType type) {
        return switch (type) {
            case DROUGHT -> "Seca severa detectada. Produção de alimentos e água reduzida em 30% por " + getDefaultDuration(type) + " ticks.";
            case EPIDEMIC -> "Surto epidêmico em andamento. Crescimento populacional reduzido e reputação penalizada.";
            case TECH_DISCOVERY -> "Descoberta científica global! +50 pontos de pesquisa para tecnologias ativas.";
            case SOLAR_STORM -> "Tempestade solar severa. Sistemas robóticos offline por " + getDefaultDuration(type) + " ticks.";
            case TRADE_BOOM -> "Boom econômico global! Volume de todas as trocas mesh aumentado em 20%.";
        };
    }
}
