package io.github.opencivilizationplatform.core.eventbus;

import java.util.function.Consumer;

public interface EventBus {
    <T extends DomainEvent> void publish(T event);
    <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler);
    <T extends DomainEvent> void unsubscribe(Class<T> eventType, Consumer<T> handler);
}
