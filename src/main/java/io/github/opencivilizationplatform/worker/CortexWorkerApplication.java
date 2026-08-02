package io.github.opencivilizationplatform.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Profile("worker")
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EntityScan(basePackages = {
    "io.github.opencivilizationplatform.modules.civilization.domain",
    "io.github.opencivilizationplatform.modules.region.domain",
    "io.github.opencivilizationplatform.modules.cortex.domain"
})
@EnableJpaRepositories(basePackages = {
    "io.github.opencivilizationplatform.modules.civilization.infrastructure",
    "io.github.opencivilizationplatform.modules.region.infrastructure"
})
public class CortexWorkerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CortexWorkerApplication.class, args);
    }
}
