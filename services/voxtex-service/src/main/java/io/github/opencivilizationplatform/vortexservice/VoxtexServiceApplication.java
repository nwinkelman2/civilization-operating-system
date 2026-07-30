package io.github.opencivilizationplatform.vortexservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VoxtexServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoxtexServiceApplication.class, args);
    }
}
