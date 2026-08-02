package io.github.opencivilizationplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "civos.eventbus.type", havingValue = "kafka")
public class KafkaConfig {

    public static final String TOPIC_CIVILIZATION_CREATED = "civos.events.civilization_created";
    public static final String TOPIC_VOXTEX_MESSAGE_SENT = "civos.events.voxtex_message_sent";
    public static final String TOPIC_RESOURCE_TICK = "civos.events.resource_tick_processed";
    public static final String TOPIC_TRADE_AGREEMENT = "civos.events.trade_agreement_created";

    @Bean
    public NewTopic civilizationCreatedTopic() {
        return TopicBuilder.name(TOPIC_CIVILIZATION_CREATED)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic voxtexMessageSentTopic() {
        return TopicBuilder.name(TOPIC_VOXTEX_MESSAGE_SENT)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic resourceTickTopic() {
        return TopicBuilder.name(TOPIC_RESOURCE_TICK)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic tradeAgreementTopic() {
        return TopicBuilder.name(TOPIC_TRADE_AGREEMENT)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
