package io.github.opencivilizationplatform.civilizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CivilizationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CivilizationServiceApplication.class, args);
    }
}
