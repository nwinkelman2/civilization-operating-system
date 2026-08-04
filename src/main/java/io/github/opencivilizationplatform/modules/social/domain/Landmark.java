package io.github.opencivilizationplatform.modules.social.domain;

public record Landmark(
    String landmarkId,
    String name,
    String regionId,
    double attractionPower
) {
    public Landmark {
        attractionPower = Math.max(0.0, attractionPower);
    }
}
