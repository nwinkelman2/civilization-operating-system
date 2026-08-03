package io.github.opencivilizationplatform.modules.nexus;

import io.github.opencivilizationplatform.core.eventbus.DomainEvent;
import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.NexusMessageSentEvent;
import io.github.opencivilizationplatform.modules.nexus.application.AgentCommunicationService;
import io.github.opencivilizationplatform.modules.nexus.application.AgentCommunicationService.AgentDialog;
import io.github.opencivilizationplatform.modules.nexus.application.AgentCommunicationService.DialogueIntent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class AgentCommunicationServiceTest {

    private AgentCommunicationService communicationService;
    private TestEventBus testEventBus;

    static class TestEventBus implements EventBus {
        final List<DomainEvent> publishedEvents = new ArrayList<>();

        @Override
        public <T extends DomainEvent> void publish(T event) {
            publishedEvents.add(event);
        }

        @Override
        public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {}

        @Override
        public <T extends DomainEvent> void unsubscribe(Class<T> eventType, Consumer<T> handler) {}
    }

    @BeforeEach
    void setUp() {
        testEventBus = new TestEventBus();
        communicationService = new AgentCommunicationService(testEventBus);
    }

    @Test
    void testClassifyIntent() {
        assertEquals(DialogueIntent.TRADE_NEGOTIATION, communicationService.classifyIntent("We want to trade 100 wood for iron"));
        assertEquals(DialogueIntent.ALLIANCE_PROPOSAL, communicationService.classifyIntent("Proposing a non-aggression alliance pact"));
        assertEquals(DialogueIntent.KNOWLEDGE_SHARE, communicationService.classifyIntent("Sharing research findings on renewable energy"));
    }

    @Test
    void testProcessDialogAutoClassificationAndPublishEvent() {
        String sender = "agent-alpha";
        String receiver = "agent-beta";
        String content = "We propose an alliance between our settlements.";

        AgentDialog dialog = communicationService.processDialog(sender, receiver, content);

        assertNotNull(dialog);
        assertNotNull(dialog.dialogId());
        assertEquals(sender, dialog.senderAgentId());
        assertEquals(receiver, dialog.receiverAgentId());
        assertEquals(DialogueIntent.ALLIANCE_PROPOSAL, dialog.intent());
        assertEquals(content, dialog.content());

        // Verify EventBus received event
        assertEquals(1, testEventBus.publishedEvents.size());
        DomainEvent event = testEventBus.publishedEvents.get(0);
        assertTrue(event instanceof NexusMessageSentEvent);

        NexusMessageSentEvent nse = (NexusMessageSentEvent) event;
        assertEquals("ALLIANCE_PROPOSAL", nse.getMessageType());
        assertEquals(content, nse.getContent());
    }

    @Test
    void testProcessDialogExplicitIntent() {
        String sender = "agent-1";
        String receiver = "agent-2";
        String content = "Here is the raw blueprint.";

        AgentDialog dialog = communicationService.processDialog(sender, receiver, DialogueIntent.KNOWLEDGE_SHARE, content);

        assertEquals(DialogueIntent.KNOWLEDGE_SHARE, dialog.intent());
        assertEquals(1, communicationService.getDialogHistory(sender).size());
        assertEquals(1, communicationService.getDialogHistory(receiver).size());
    }
}
