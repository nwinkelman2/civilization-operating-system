# Multi-Instance Cluster Coordination Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement full multi-instance coordination, synchronization, distributed locking, caching, and rate limiting in the Civilization Operating System using Redis and ShedLock.

**Architecture:** Leverage Redis as the centralized caching, pub/sub event broadcasting, and rate limiting layer, and ShedLock backed by PostgreSQL for distributed execution locks of scheduled tasks. Convert the maven wrapper script to LF line endings for compatibility.

**Tech Stack:** Java 25, Spring Boot 4.1.0, Redis, Lettuce, ShedLock, PostgreSQL, Flyway.

## Global Constraints

- Use Java 25 features and standard Spring Boot 4.x/Spring Framework 7 syntax.
- Maintain full compatibility with existing PostgreSQL and PostGIS data types.
- All tests must pass after each task implementation.

---

### Task 1: Infrastructure Setup & Compatibility

**Files:**
- Modify: `mvnw` (Line endings conversion to LF)
- Modify: `pom.xml` (Add dependencies)
- Modify: `docker-compose.yml` (Add Redis service and wire environment variables)
- Modify: `src/main/resources/application.yml` (Add Redis configurations)

**Interfaces:**
- None (infrastructure scaffolding)

- [ ] **Step 1: Convert `mvnw` to LF line endings**

Run:
```bash
sed -i 's/\r$//' mvnw
```

- [ ] **Step 2: Add Maven dependencies**

In `pom.xml`, add the following XML block inside `<dependencies>`:
```xml
		<!-- Redis -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>

		<!-- ShedLock -->
		<dependency>
			<groupId>net.javacrumbs.shedlock</groupId>
			<artifactId>shedlock-spring</artifactId>
			<version>5.13.0</version>
		</dependency>
		<dependency>
			<groupId>net.javacrumbs.shedlock</groupId>
			<artifactId>shedlock-provider-jdbc-template</artifactId>
			<version>5.13.0</version>
		</dependency>
```

- [ ] **Step 3: Update `docker-compose.yml`**

Add the `redis` service and update `app` and `app-primary` services.

Under `services:`, add:
```yaml
  redis:
    image: redis:7-alpine
    container_name: civos-redis
    ports:
      - "6379:6379"
    restart: unless-stopped
```

For both `app` and `app-primary` service blocks under `environment:`, add:
```yaml
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
```

- [ ] **Step 4: Update `src/main/resources/application.yml`**

Add Redis config under `spring:`:
```yaml
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
      port: ${SPRING_DATA_REDIS_PORT:6379}
```

- [ ] **Step 5: Verify build compiles and existing tests pass**

Run:
```bash
mvn clean test
```
Expected: BUILD SUCCESS (86 tests passed)

- [ ] **Step 6: Commit**

```bash
git add mvnw pom.xml docker-compose.yml src/main/resources/application.yml
git commit -m "infra: add redis and shedlock dependencies and docker-compose configurations"
```

---

### Task 2: Distributed Scheduling with ShedLock

**Files:**
- Create: `src/main/resources/db/migration/V7__add_shedlock.sql`
- Create: `src/main/java/io/github/opencivilizationplatform/config/ShedLockConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/cortex/cortex/CortexEngineService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/config/ShedLockIntegrationTest.java`

**Interfaces:**
- Consumes: Database connection
- Produces: Distributed locking on `@Scheduled` tasks

- [ ] **Step 1: Create ShedLock DB Table Migration**

Create `src/main/resources/db/migration/V7__add_shedlock.sql`:
```sql
CREATE TABLE shedlock (
    name VARCHAR(64) PRIMARY KEY,
    lock_until TIMESTAMP WITH TIME ZONE,
    locked_at TIMESTAMP WITH TIME ZONE,
    locked_by VARCHAR(255)
);
```

- [ ] **Step 2: Create ShedLock Java Configuration**

Create `src/main/java/io/github/opencivilizationplatform/config/ShedLockConfig.java`:
```java
package io.github.opencivilizationplatform.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()
                .build()
        );
    }
}
```

- [ ] **Step 3: Annotate Cortex Engine Simulation Tick**

Modify `src/main/java/io/github/opencivilizationplatform/modules/cortex/cortex/CortexEngineService.java`:
Add imports:
```java
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
```
Modify line 34:
```java
    @Scheduled(fixedRateString = "${cortex.engine.tick-rate-ms:30000}")
    @Transactional
    @SchedulerLock(name = "cortexEngineTick", lockAtMostFor = "25s", lockAtLeastFor = "10s")
    public void tick() {
```

- [ ] **Step 4: Annotate Voxtex Mesh Tick**

Modify `src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java`:
Add imports:
```java
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
```
Modify line 147:
```java
    @Transactional
    @Scheduled(fixedRate = 15000)
    @SchedulerLock(name = "voxtexMeshTick", lockAtMostFor = "12s", lockAtLeastFor = "5s")
    public void processMeshTick() {
```

- [ ] **Step 5: Write Integration Test for ShedLock**

Create `src/test/java/io/github/opencivilizationplatform/config/ShedLockIntegrationTest.java`:
```java
package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ShedLockIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldVerifyShedlockTableExists() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_name = 'shedlock'",
            Integer.class
        );
        assertThat(count).isEqualTo(1);
    }
}
```

- [ ] **Step 6: Run Tests to verify ShedLock works**

Run:
```bash
mvn test -Dtest=ShedLockIntegrationTest
```
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/resources/db/migration/V7__add_shedlock.sql \
        src/main/java/io/github/opencivilizationplatform/config/ShedLockConfig.java \
        src/main/java/io/github/opencivilizationplatform/modules/cortex/cortex/CortexEngineService.java \
        src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java \
        src/test/java/io/github/opencivilizationplatform/config/ShedLockIntegrationTest.java
git commit -m "feat: add distributed scheduler locks using ShedLock and PostgreSQL"
```

---

### Task 3: Real-Time Event Synchronization via Redis Pub/Sub

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/config/RedisEventConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/web/handler/VoxtexWebSocketHandler.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/voxtex/RedisPubSubIntegrationTest.java`

**Interfaces:**
- Consumes: `VoxtexMessage`
- Produces: Cluster-wide JSON broadcasting over Redis `voxtex-mesh-events` channel

- [ ] **Step 1: Create Redis Pub/Sub Configuration**

Create `src/main/java/io/github/opencivilizationplatform/config/RedisEventConfig.java`:
```java
package io.github.opencivilizationplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.web.handler.VoxtexWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisEventConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisEventConfig.class);
    public static final String CHANNEL_NAME = "voxtex-mesh-events";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(CHANNEL_NAME));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisEventSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "receiveMessage");
    }

    @org.springframework.stereotype.Component
    public static class RedisEventSubscriber {
        private final VoxtexMeshService meshService;
        private final VoxtexWebSocketHandler webSocketHandler;
        private final ObjectMapper objectMapper;

        public RedisEventSubscriber(VoxtexMeshService meshService,
                                    VoxtexWebSocketHandler webSocketHandler,
                                    ObjectMapper objectMapper) {
            this.meshService = meshService;
            this.webSocketHandler = webSocketHandler;
            this.objectMapper = objectMapper;
        }

        public void receiveMessage(String message) {
            try {
                VoxtexMessage msg = objectMapper.readValue(message, VoxtexMessage.class);
                log.info("Received event from Redis Pub/Sub: {} -> {}", msg.getSourceNode().getName(), msg.getTargetNode().getName());
                
                // 1. Trigger local SSE
                meshService.notifyListenersLocally(msg);
                
                // 2. Trigger local WebSockets
                webSocketHandler.broadcastMessageLocally(msg);
            } catch (Exception e) {
                log.error("Failed to process synchronized Redis event", e);
            }
        }
    }
}
```

- [ ] **Step 2: Update `VoxtexMeshService` for Redis publishing**

Modify `src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java`:
Update classes and methods to handle both local and cluster-wide notifications.

Add imports:
```java
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
```

Inject `StringRedisTemplate` and `ObjectMapper` into the constructor (ensure `@org.springframework.context.annotation.Lazy` is used for `StringRedisTemplate` if needed, but standard injection should be fine):
```java
    private final VoxtexNodeRepository nodeRepository;
    private final VoxtexMessageRepository messageRepository;
    private final VoxtexConnectionRepository connectionRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // SSE listeners local to this instance
    private final List<Consumer<VoxtexMessage>> messageListeners = new CopyOnWriteArrayList<>();

    public VoxtexMeshService(VoxtexNodeRepository nodeRepository,
                              VoxtexMessageRepository messageRepository,
                              VoxtexConnectionRepository connectionRepository,
                              StringRedisTemplate redisTemplate,
                              ObjectMapper objectMapper) {
        this.nodeRepository = nodeRepository;
        this.messageRepository = messageRepository;
        this.connectionRepository = connectionRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
```

Modify the `sendMessage` method around lines 80-98:
```java
    @Transactional
    public VoxtexMessage sendMessage(Long sourceNodeId, Long targetNodeId,
                                      VoxtexMessageType messageType, String content) {
        VoxtexNode source = nodeRepository.findById(sourceNodeId).orElseThrow();
        VoxtexNode target = nodeRepository.findById(targetNodeId).orElseThrow();

        VoxtexMessage msg = new VoxtexMessage();
        msg.setSourceNode(source);
        msg.setTargetNode(target);
        msg.setMessageType(messageType);
        msg.setContent(content);
        msg = messageRepository.save(msg);

        // Publish to Redis instead of notifying local listeners directly
        publishEventToRedis(msg);

        log.info("VOXTEX MESH: {} -> {} [{}]", source.getName(), target.getName(), messageType);
        return msg;
    }
```

Also, modify `processMeshTick` where `notifyListeners(msg)` is called. Replace all `notifyListeners(msg)` and `notifyListeners(autoMsg)` with `publishEventToRedis(msg)` / `publishEventToRedis(autoMsg)`.

Add these methods to the bottom of the class:
```java
    private void publishEventToRedis(VoxtexMessage msg) {
        try {
            String json = objectMapper.writeValueAsString(msg);
            redisTemplate.convertAndSend("voxtex-mesh-events", json);
        } catch (Exception e) {
            log.error("Failed to publish message event to Redis", e);
        }
    }

    public void notifyListenersLocally(VoxtexMessage msg) {
        for (var listener : messageListeners) {
            try {
                listener.accept(msg);
            } catch (Exception e) {
                messageListeners.remove(listener);
            }
        }
    }
```

- [ ] **Step 3: Update `VoxtexWebSocketHandler`**

Modify `src/main/java/io/github/opencivilizationplatform/web/handler/VoxtexWebSocketHandler.java`:

Replace the `handleTextMessage` switch-case block for `send_message`:
```java
            case "send_message" -> {
                Long sourceId = Long.valueOf(payload.get("sourceNodeId").toString());
                Long targetId = Long.valueOf(payload.get("targetNodeId").toString());
                String content = (String) payload.get("content");
                String typeStr = (String) payload.get("messageType");
                VoxtexMessageType msgType = VoxtexMessageType.valueOf(typeStr);
                // This calls meshService which publishes to Redis.
                // We do NOT call broadcastMessage here anymore, it will be called when the Redis subscriber receives the event!
                meshService.sendMessage(sourceId, targetId, msgType, content);
            }
```

Rename `broadcastMessage` to `broadcastMessageLocally`:
```java
    public void broadcastMessageLocally(VoxtexMessage msg) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                "type", "voxtex-message",
                "sourceNodeId", msg.getSourceNode().getId(),
                "targetNodeId", msg.getTargetNode().getId(),
                "messageType", msg.getMessageType().name(),
                "content", msg.getContent(),
                "hopCount", msg.getHopCount()
            ));
            TextMessage textMsg = new TextMessage(json);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(textMsg);
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast message locally", e);
        }
    }
```

- [ ] **Step 4: Write Integration Test for Redis Pub/Sub Event Synchronization**

Create `src/test/java/io/github/opencivilizationplatform/modules/voxtex/RedisPubSubIntegrationTest.java`:
```java
package io.github.opencivilizationplatform.modules.voxtex;

import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisPubSubIntegrationTest {

    @Autowired
    private VoxtexMeshService meshService;

    @Test
    void shouldPropagateEventThroughRedisPubSub() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<VoxtexMessage> receivedMessage = new AtomicReference<>();

        meshService.addMessageListener(msg -> {
            receivedMessage.set(msg);
            latch.countDown();
        });

        // Send a message using node IDs 1 and 2 (which exist in seeded data)
        meshService.sendMessage(1L, 2L, VoxtexMessageType.DATA_SYNC, "Test Cluster Broadcast");

        boolean received = latch.await(5, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getContent()).isEqualTo("Test Cluster Broadcast");
    }
}
```

- [ ] **Step 5: Run Tests**

Run:
```bash
mvn test -Dtest=RedisPubSubIntegrationTest,VoxtexControllerTest
```
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/io/github/opencivilizationplatform/config/RedisEventConfig.java \
        src/main/java/io/github/opencivilizationplatform/modules/voxtex/application/VoxtexMeshService.java \
        src/main/java/io/github/opencivilizationplatform/web/handler/VoxtexWebSocketHandler.java \
        src/test/java/io/github/opencivilizationplatform/modules/voxtex/RedisPubSubIntegrationTest.java
git commit -m "feat: synchronize WebSockets and SSE events across instances using Redis Pub/Sub"
```

---

### Task 4: Distributed Cache Configuration

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/config/CacheConfig.java`
- Create: `src/test/java/io/github/opencivilizationplatform/config/RedisCacheIntegrationTest.java`

**Interfaces:**
- Consumes: `@Cacheable` and `@CacheEvict` annotations
- Produces: Redis-backed shared cache manager for `resources` and `balance`

- [ ] **Step 1: Replace Caffeine Cache with Redis Cache Manager**

Modify `src/main/java/io/github/opencivilizationplatform/config/CacheConfig.java`:
Replace entire contents of file:
```java
package io.github.opencivilizationplatform.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration("resources", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("balance", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}
```

- [ ] **Step 2: Create Caching Integration Test**

Create `src/test/java/io/github/opencivilizationplatform/config/RedisCacheIntegrationTest.java`:
```java
package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheIntegrationTest {

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
```

- [ ] **Step 3: Run Caching Tests**

Run:
```bash
mvn test -Dtest=RedisCacheIntegrationTest
```
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/io/github/opencivilizationplatform/config/CacheConfig.java \
        src/test/java/io/github/opencivilizationplatform/config/RedisCacheIntegrationTest.java
git commit -m "feat: migrate spring cache from local Caffeine to distributed Redis"
```

---

### Task 5: Distributed Rate Limiting

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/config/RateLimitingFilter.java`
- Create: `src/test/java/io/github/opencivilizationplatform/config/RedisRateLimiterIntegrationTest.java`

**Interfaces:**
- Consumes: Servlet requests on `/api/v1/*`
- Produces: Distributed IP rate limiting (100 req/min) backed by Redis

- [ ] **Step 1: Rewrite Rate Limiting Filter to use Redis**

Modify `src/main/java/io/github/opencivilizationplatform/config/RateLimitingFilter.java`:
Replace entire contents of file:
```java
package io.github.opencivilizationplatform.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (path.startsWith("/api/v1/")) {
            String clientIp = httpRequest.getRemoteAddr();
            String redisKey = "rate:limit:" + clientIp;

            Long currentCount = redisTemplate.opsForValue().increment(redisKey);

            if (currentCount != null && currentCount == 1L) {
                redisTemplate.expire(redisKey, Duration.ofMinutes(1));
            }

            if (currentCount != null && currentCount > MAX_REQUESTS_PER_MINUTE) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("""
                        {"error":"Too many requests","message":"Rate limit exceeded. Try again later."}
                        """);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

- [ ] **Step 2: Create Integration Test for Distributed Rate Limiter**

Create `src/test/java/io/github/opencivilizationplatform/config/RedisRateLimiterIntegrationTest.java`:
```java
package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RedisRateLimiterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.delete("rate:limit:127.0.0.1");
    }

    @Test
    void shouldAllowRequestsUnderLimitAndBlockOverLimit() throws Exception {
        // Perform 100 successful requests
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/v1/regions"))
                    .andExpect(status().isOk());
        }

        // The 101st request must be blocked (HTTP 429)
        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isTooManyRequests());
    }
}
```

- [ ] **Step 3: Run Rate Limiting Tests**

Run:
```bash
mvn test -Dtest=RedisRateLimiterIntegrationTest
```
Expected: PASS

- [ ] **Step 4: Run all project tests**

Run:
```bash
mvn test
```
Expected: BUILD SUCCESS (All tests, including new ones, pass)

- [ ] **Step 5: Commit**

```bash
git add src/main/java/io/github/opencivilizationplatform/config/RateLimitingFilter.java \
        src/test/java/io/github/opencivilizationplatform/config/RedisRateLimiterIntegrationTest.java
git commit -m "feat: implement distributed IP rate limiting using Redis sliding window"
```
