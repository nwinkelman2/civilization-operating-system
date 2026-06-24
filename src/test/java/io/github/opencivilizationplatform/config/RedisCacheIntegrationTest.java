package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheIntegrationTest {

    static {
        try {
            Process process = new ProcessBuilder("docker", "compose", "up", "-d", "redis").start();
            process.waitFor();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("Failed to start Redis via docker compose: " + e.getMessage());
        }
    }

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldVerifyRedisCacheManagerActive() {
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
        
        Cache cache = cacheManager.getCache("balance");
        assertThat(cache).isNotNull();
        
        cache.put("testKey", "testValue");
        assertThat(cache.get("testKey", String.class)).isEqualTo("testValue");
    }
}
