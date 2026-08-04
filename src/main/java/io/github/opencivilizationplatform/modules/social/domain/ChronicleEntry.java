package io.github.opencivilizationplatform.modules.social.domain;

public record ChronicleEntry(
    String entryId,
    long tick,
    String category,
    String description
) {}
