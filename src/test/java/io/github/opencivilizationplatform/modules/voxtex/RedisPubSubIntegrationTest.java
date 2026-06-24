package io.github.opencivilizationplatform.modules.voxtex;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessageType;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNode;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeType;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisPubSubIntegrationTest {

    @Autowired
    private VoxtexMeshService meshService;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
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

        // 2. Register two VoxtexNodes via meshService
        VoxtexNode node1 = meshService.registerNode(
            "Node 1", VoxtexNodeType.PRIMARY, "Region A", civ.getId(), "Knowledge A"
        );
        VoxtexNode node2 = meshService.registerNode(
            "Node 2", VoxtexNodeType.CITY, "Region B", civ.getId(), "Knowledge B"
        );

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<VoxtexMessage> receivedMessage = new AtomicReference<>();

        meshService.addMessageListener(msg -> {
            receivedMessage.set(msg);
            latch.countDown();
        });

        // 3. Send a message using the newly created node IDs (which will publish to Redis)
        meshService.sendMessage(node1.getId(), node2.getId(), VoxtexMessageType.NEURAL_SYNC, "Test Cluster Broadcast");

        boolean received = latch.await(5, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getContent()).isEqualTo("Test Cluster Broadcast");
    }
}
