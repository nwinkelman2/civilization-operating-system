package io.github.opencivilizationplatform.modules.nexus.domain;

public record CivilizationPlugin(
    String pluginId,
    String pluginName,
    String version,
    boolean active
) {
    public CivilizationPlugin withActive(boolean newActive) {
        return new CivilizationPlugin(pluginId, pluginName, version, newActive);
    }
}
