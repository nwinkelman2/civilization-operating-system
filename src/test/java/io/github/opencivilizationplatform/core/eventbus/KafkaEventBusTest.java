package io.github.opencivilizationplatform.core.eventbus;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.core.eventbus.events.*;
import tools.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class KafkaEventBusTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private KafkaEventBus eventBus;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
        eventBus = new KafkaEventBus(kafkaTemplate, objectMapper);
    }

    @ParameterizedTest
    @MethodSource("provideEventsAndExpectedTopics")
    @SuppressWarnings("unchecked")
    void shouldPublishToCorrectTopicNameForDomainEvents(DomainEvent event, String expectedTopic) {
        eventBus.publish(event);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        assertThat(captor.getValue().topic()).isEqualTo(expectedTopic);
    }

    private static Stream<Arguments> provideEventsAndExpectedTopics() {
        return Stream.of(
            Arguments.of(
                new CivilizationCreatedEvent("test", 1L, "CivName", "RegionA", CivilizationScale.LOCAL, "token123"),
                "civos.civilization.created"
            ),
            Arguments.of(
                new NexusMessageSentEvent("test", 10L, 1L, 2L, "DIRECT", "Hello"),
                "civos.nexus.message_sent"
            ),
            Arguments.of(
                new ResourceTickProcessedEvent("test", 1L, 10.0, 5.0, 2.0, 1.0, 0.0, 1.0, 0.5),
                "civos.resources.tick_processed"
            ),
            Arguments.of(
                new TradeAgreementCreatedEvent("test", 100L, 1L, 2L, "FOOD", 50.0),
                "civos.trade.agreement_created"
            ),
            Arguments.of(
                new ContributionSubmittedEvent("test", 5L, 20L, 3L, 100.0),
                "civos.contribution.submitted"
            ),
            Arguments.of(
                new ElectionCompletedEvent("test", 7L, 1L, 42L),
                "civos.governance.election_completed"
            ),
            Arguments.of(
                new IncidentResolvedEvent("test", 15L, 1L, "Resolved amicably"),
                "civos.social.incident_resolved"
            ),
            Arguments.of(
                new ShipmentDeliveredEvent("test", 88L, "RegionA", "RegionB", 250.0),
                "civos.logistics.shipment_delivered"
            ),
            Arguments.of(
                new GlobalEventOccurredEvent("test", 99L, "Solar Flare", "NATURAL_DISASTER", "HIGH"),
                "civos.events.global_occurred"
            )
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldPublishCivilizationCreatedEventToCorrectTopic() {
        CivilizationCreatedEvent event = new CivilizationCreatedEvent("test", 1L, "Alpha", "Region1", CivilizationScale.LOCAL, "token");
        eventBus.publish(event);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        assertThat(captor.getValue().topic()).isEqualTo("civos.civilization.created");
    }

    @Test
    void shouldNotifyLocalSubscribersOnPublish() {
        java.util.concurrent.atomic.AtomicBoolean notified = new java.util.concurrent.atomic.AtomicBoolean(false);
        eventBus.subscribe(CivilizationCreatedEvent.class, e -> notified.set(true));

        CivilizationCreatedEvent event = new CivilizationCreatedEvent("test", 1L, "Alpha", "Region1", CivilizationScale.LOCAL, "token");
        eventBus.publish(event);

        assertThat(notified.get()).isTrue();
    }
}
