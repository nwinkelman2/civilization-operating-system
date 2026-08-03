package io.github.opencivilizationplatform.config;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.core.eventbus.events.CivilizationCreatedEvent;
import io.github.opencivilizationplatform.core.eventbus.events.ResourceTickProcessedEvent;
import io.github.opencivilizationplatform.core.eventbus.events.NexusMessageSentEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "civos.eventbus.type", havingValue = "kafka")
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    private final ObjectMapper objectMapper;

    public KafkaConsumerConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_CIVILIZATION_CREATED, groupId = "civos-core")
    public void onCivilizationCreated(ConsumerRecord<String, String> record) {
        log.info("CDC Event - Civilization created: key={}, value={}", record.key(), record.value());
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_VOXTEX_MESSAGE_SENT, groupId = "civos-core")
    public void onVoxtexMessageSent(ConsumerRecord<String, String> record) {
        log.info("CDC Event - Voxtex message sent: key={}", record.key());
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_RESOURCE_TICK, groupId = "civos-core")
    public void onResourceTick(ConsumerRecord<String, String> record) {
        log.debug("CDC Event - Resource tick: key={}", record.key());
    }

    @KafkaListener(topics = "civos.cdc.public.civilizations", groupId = "civos-cdc")
    public void onCdcCivilization(ConsumerRecord<String, String> record) {
        log.info("CDC DB change - Civilizations table: {}", record.value());
    }

    @KafkaListener(topics = "civos.cdc.public.voxtex_nodes", groupId = "civos-cdc")
    public void onCdcVoxtexNode(ConsumerRecord<String, String> record) {
        log.debug("CDC DB change - Voxtex nodes: {}", record.value());
    }

    @KafkaListener(topicPattern = "civos\\.cdc\\.public\\.(resources|resource_regions|trade_agreements|game_events)",
                   groupId = "civos-cdc")
    public void onCdcAny(ConsumerRecord<String, String> record) {
        log.debug("CDC DB change - topic={}, key={}", record.topic(), record.key());
    }
}
