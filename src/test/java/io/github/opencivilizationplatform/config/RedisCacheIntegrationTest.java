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
        Cache cache = cacheManager.getCache("resources");
        assertThat(cache).isNotNull();
        cache.clear();

        // 1. Create and save a Resource
        Resource resource = new Resource();
        resource.setName("Water Supply Test");
        resource.setType(ResourceType.WATER);
        resource.setDescription("Clean drinking water resource");
        resource.setQuantity(1000.0);
        resource.setUnit("Liters");
        resourceService.saveResource(resource);

        // 2. Call service first time to populate the cache
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Resource> firstCall = resourceService.getAllResources(pageRequest);
        assertThat(firstCall).isNotEmpty();

        // 3. Verify that the page was successfully cached in Redis (proving serialization succeeded)
        Cache.ValueWrapper wrapper = cache.get("0-10");
        assertThat(wrapper).isNotNull();
        Page<Resource> cachedPage = (Page<Resource>) wrapper.get();
        assertThat(cachedPage).isNotNull();

        // 4. Modify the cached value directly in Redis to prove subsequent calls are served from the cache
        Resource modifiedResource = new Resource();
        modifiedResource.setName("Cached Special Resource");
        modifiedResource.setType(ResourceType.WATER);
        modifiedResource.setDescription("Modified directly in Redis cache");
        modifiedResource.setQuantity(999.0);
        modifiedResource.setUnit("Liters");
        Page<Resource> modifiedPage = new PageImpl<>(List.of(modifiedResource), pageRequest, 1);
        cache.put("0-10", modifiedPage);

        // 5. Call service second time and verify it returns the cached modified value, not the database value
        Page<Resource> secondCall = resourceService.getAllResources(pageRequest);
        assertThat(secondCall.getContent()).hasSize(1);
        assertThat(secondCall.getContent().get(0).getName()).isEqualTo("Cached Special Resource");
        assertThat(secondCall.getContent().get(0).getDescription()).isEqualTo("Modified directly in Redis cache");
    }

    @Test
    void shouldCacheBalanceReport() {
        Cache cache = cacheManager.getCache("balance");
        assertThat(cache).isNotNull();
        cache.clear();

        BalanceDTO dummyDto = new BalanceDTO("ENERGY", 999.0, 999.0, "MWh", 100.0, "STABLE");
        cache.put("report", List.of(dummyDto));

        List<BalanceDTO> result = balanceService.getBalanceReport();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("ENERGY");
    }
}
