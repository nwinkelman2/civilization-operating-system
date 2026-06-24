package io.github.opencivilizationplatform.config;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedRedisContainer {
    public static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    static {
        System.setProperty("api.version", "1.44");
        redis.start();
    }
}
