package io.github.opencivilizationplatform.core.eventbus;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
@ConditionalOnProperty(name = "civos.eventbus.type", havingValue = "kafka", matchIfMissing = false)
public class KafkaEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventBus.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<Class<?>, List<Consumer<?>>> handlers = new ConcurrentHashMap<>();

    public KafkaEventBus(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        try {
            ObjectNode wrapper = objectMapper.createObjectNode();
            wrapper.put("eventType", event.getClass().getName());
            wrapper.put("eventId", event.getEventId().toString());
            wrapper.put("occurredOn", event.getOccurredOn().toString());
            wrapper.put("source", event.getSource());
            wrapper.set("data", objectMapper.valueToTree(event));

            String json = objectMapper.writeValueAsString(wrapper);
            String topic = "civos." + event.getModule() + "." + event.getEventName();

            kafkaTemplate.send(new ProducerRecord<>(topic, event.getEventId().toString(), json));
            log.debug("Published event to Kafka topic {}: {}", topic, event.getEventId());
            notifyLocal(event);
        } catch (Exception e) {
            log.error("Failed to serialize event {}: {}", event.getType(), e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
            .add(handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        List<Consumer<?>> h = handlers.get(eventType);
        if (h != null) h.remove(handler);
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void notifyLocal(T event) {
        List<Consumer<?>> h = handlers.get(event.getClass());
        if (h != null) {
            for (Consumer<?> handler : h) {
                try {
                    ((Consumer<T>) handler).accept(event);
                } catch (Exception e) {
                    log.error("Error in Kafka event handler: {}", e.getMessage());
                }
            }
        }
    }
}
