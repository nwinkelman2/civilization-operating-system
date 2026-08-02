package io.github.opencivilizationplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class CivilizationOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CivilizationOsApplication.class, args);
    }
}
