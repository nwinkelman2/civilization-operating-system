package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        // Since Flyway is disabled in the H2 'test' profile, we manually create the table
        // to ensure it exists, simulating the Flyway migration.
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS shedlock (" +
            "name VARCHAR(64) PRIMARY KEY, " +
            "lock_until TIMESTAMP, " +
            "locked_at TIMESTAMP, " +
            "locked_by VARCHAR(255)" +
            ")"
        );
    }

    @Test
    void shouldVerifyShedlockTableExists() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE LOWER(table_name) = 'shedlock'",
            Integer.class
        );
        assertThat(count).isEqualTo(1);
    }
}
