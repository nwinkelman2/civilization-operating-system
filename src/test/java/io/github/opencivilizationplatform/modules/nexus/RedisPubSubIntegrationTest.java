package io.github.opencivilizationplatform.modules.nexus;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessage;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessageType;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import io.github.opencivilizationplatform.config.SharedRedisContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class RedisPubSubIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", SharedRedisContainer.redis::getHost);
        registry.add("spring.data.redis.port", SharedRedisContainer.redis::getFirstMappedPort);
    }

    @Autowired
    private NexusMeshService meshService;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPropagateEventThroughRedisPubSub() throws InterruptedException {
        assertThat(objectMapper).isNotNull(); // Verify Jackson 3.x ObjectMapper injection
        
        // 1. Create and save a Civilization to satisfy foreign key constraints
        Civilization civ = new Civilization();
        civ.setName("Test Civ " + System.currentTimeMillis());
        civ.setScale(io.github.opencivilizationplatform.config.seed.CivilizationScale.LOCAL);
        civ.setRegion("Test Region");
        civ.setOwnerToken("test-token");
        civ.setStatus(CivilizationStatus.EMERGING);
        civ = civilizationRepository.save(civ);

        // 2. Register two NexusNodes via meshService
        NexusNode node1 = meshService.registerNode(
            "Node 1", NexusNodeType.PRIMARY, "Region A", civ.getId(), "Knowledge A"
        );
        NexusNode node2 = meshService.registerNode(
            "Node 2", NexusNodeType.CITY, "Region B", civ.getId(), "Knowledge B"
        );

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<NexusMessage> receivedMessage = new AtomicReference<>();

        meshService.addMessageListener(msg -> {
            receivedMessage.set(msg);
            latch.countDown();
        });

        // 3. Send a message using the newly created node IDs (which will publish to Redis)
        meshService.sendMessage(node1.getId(), node2.getId(), NexusMessageType.NEURAL_SYNC, "Test Cluster Broadcast");

        boolean received = latch.await(5, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getContent()).isEqualTo("Test Cluster Broadcast");
    }
}

