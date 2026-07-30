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
    public LockProvider lockProvider(DataSource dataSource, org.springframework.core.env.Environment env) {
        JdbcTemplateLockProvider.Configuration.Builder builder = JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(new JdbcTemplate(dataSource));

        // H2 in PostgreSQL mode (used in tests) has compatibility and timezone issues with ShedLock's DB time queries.
        // We only use central DB time in production/non-test profiles.
        boolean isTestProfile = java.util.Arrays.asList(env.getActiveProfiles()).contains("test");
        if (!isTestProfile) {
            builder.usingDbTime();
        }

        return new JdbcTemplateLockProvider(builder.build());
    }
}
