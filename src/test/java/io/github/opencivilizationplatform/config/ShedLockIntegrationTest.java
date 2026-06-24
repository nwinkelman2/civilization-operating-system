package io.github.opencivilizationplatform.config;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "logging.level.net.javacrumbs.shedlock=DEBUG"
})
@ActiveProfiles("test")
public class ShedLockIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LockProvider lockProvider;

    @BeforeEach
    void setUp() {
        // Clear any existing locks before test run. The table is automatically initialized
        // on startup via Spring SQL initialization using the V7__add_shedlock.sql script.
        jdbcTemplate.execute("DELETE FROM shedlock");
    }

    @Test
    void shouldAcquireAndReleaseLockUsingProvider() {
        LockConfiguration config = new LockConfiguration(
            Instant.now(),
            "test-lock",
            Duration.ofSeconds(10),
            Duration.ZERO // lockAtLeastFor = 0 to allow immediate re-acquisition upon unlock
        );

        // 1. Acquire lock
        Optional<SimpleLock> lock = lockProvider.lock(config);
        assertThat(lock).isPresent();

        // 2. Try to acquire same lock concurrently - should fail (return empty)
        Optional<SimpleLock> secondLock = lockProvider.lock(config);
        assertThat(secondLock).isEmpty();

        // 3. Release lock (resets lock_until to lockAtLeastUntil, which is now immediately expired)
        lock.get().unlock();

        // 4. Acquire lock again - should succeed now
        Optional<SimpleLock> thirdLock = lockProvider.lock(config);
        assertThat(thirdLock).isPresent();
        thirdLock.get().unlock();
    }
}
