package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.domain.NeedCategory;
import io.github.opencivilizationplatform.modules.needs.domain.NeedStatus;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
public class RedisCacheIntegrationTest {

    static {
        System.setProperty("api.version", "1.44");
    }

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private NeedRepository needRepository;

    @Test
    void shouldVerifyRedisCacheManagerActive() {
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
        
        Cache cache = cacheManager.getCache("balance");
        assertThat(cache).isNotNull();
        cache.clear();
        
        cache.put("testKey", "testValue");
        assertThat(cache.get("testKey", String.class)).isEqualTo("testValue");
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
    void shouldCacheBalanceReportAndSucceedSerialization() {
        Cache cache = cacheManager.getCache("balance");
        assertThat(cache).isNotNull();
        cache.clear();

        // 1. Create and save a Need to trigger balance calculations
        Need need = new Need();
        need.setCategory(NeedCategory.FOOD);
        need.setRegion("Region Alpha");
        need.setDescription("Food requirements");
        need.setQuantity(500.0);
        need.setUnit("units");
        need.setPriority(1);
        need.setStatus(NeedStatus.UNMET);
        needRepository.save(need);

        // 2. Call service first time to populate the cache
        List<BalanceDTO> firstReport = balanceService.getBalanceReport();
        assertThat(firstReport).isNotEmpty();

        // 3. Verify that the list was successfully cached in Redis (proving serialization succeeded)
        Cache.ValueWrapper wrapper = cache.get(SimpleKey.EMPTY);
        assertThat(wrapper).isNotNull();
        List<BalanceDTO> cachedReport = (List<BalanceDTO>) wrapper.get();
        assertThat(cachedReport).isNotEmpty();

        // 4. Modify the cached value directly in Redis to prove subsequent calls are served from the cache
        BalanceDTO modifiedDto = new BalanceDTO("SPECIAL_CACHED_CATEGORY", 999.0, 111.0, "units", 900.0, "STABLE");
        cache.put(SimpleKey.EMPTY, List.of(modifiedDto));

        // 5. Call service second time and verify it returns the cached modified value
        List<BalanceDTO> secondReport = balanceService.getBalanceReport();
        assertThat(secondReport).hasSize(1);
        assertThat(secondReport.get(0).getCategory()).isEqualTo("SPECIAL_CACHED_CATEGORY");
        assertThat(secondReport.get(0).getStatus()).isEqualTo("STABLE");
    }
}
