package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class RedisCacheIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", SharedRedisContainer.redis::getHost);
        registry.add("spring.data.redis.port", SharedRedisContainer.redis::getFirstMappedPort);
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private BalanceService balanceService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService simulationEngineService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.nexus.application.NexusAIService nexusAIService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService nexusMeshService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.events.application.EventService eventService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService cortexEngineService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.github.opencivilizationplatform.modules.contribution.application.DelegateElectionService delegateElectionService;

    @Test
    void shouldVerifyRedisCacheManagerActive() {
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
        
        Cache cache = cacheManager.getCache("resources");
        assertThat(cache).isNotNull();
    }

    @Test
    void shouldCacheResourcePageAndSucceedSerialization() {
        // 1. Clear resources cache
        Cache cache = cacheManager.getCache("resources");
        assertThat(cache).isNotNull();
        cache.clear();

        // 2. Call service first time to populate the cache
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Resource> firstCall = resourceService.getAllResources(pageRequest);
        assertThat(firstCall).isNotEmpty();

        // 3. Verify that the page was successfully cached in Redis
        Cache.ValueWrapper wrapper = cache.get("0-10");
        assertThat(wrapper).isNotNull();

        // 4. Call service second time and verify cache hit equality
        Page<Resource> secondCall = resourceService.getAllResources(pageRequest);
        assertThat(secondCall.getContent()).hasSize(firstCall.getContent().size());
    }

    @Test
    void shouldCacheBalanceReport() {
        Cache cache = cacheManager.getCache("balance");
        assertThat(cache).isNotNull();
        cache.clear();

        List<BalanceDTO> firstCall = balanceService.getBalanceReport();
        assertThat(firstCall).isNotNull();

        List<BalanceDTO> secondCall = balanceService.getBalanceReport();
        assertThat(secondCall).hasSize(firstCall.size());
    }
}
