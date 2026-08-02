package io.github.opencivilizationplatform.core.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class SpringEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(SpringEventBus.class);
    private final ApplicationEventPublisher publisher;
    private final Map<Class<?>, List<Consumer<?>>> localHandlers = new ConcurrentHashMap<>();

    public SpringEventBus(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        log.debug("Publishing event: {} (id={})", event.getType(), event.getEventId());
        publisher.publishEvent(event);
        notifyLocal(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        localHandlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
            .add(handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        List<Consumer<?>> handlers = localHandlers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void notifyLocal(T event) {
        List<Consumer<?>> handlers = localHandlers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<?> handler : handlers) {
                try {
                    ((Consumer<T>) handler).accept(event);
                } catch (Exception e) {
                    log.error("Error in local event handler for {}: {}", event.getType(), e.getMessage());
                }
            }
        }
    }
}
