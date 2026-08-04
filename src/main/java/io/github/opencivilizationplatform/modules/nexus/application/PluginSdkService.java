package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.modules.nexus.domain.CivilizationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PluginSdkService {

    private static final Logger log = LoggerFactory.getLogger(PluginSdkService.class);
    private final Map<String, CivilizationPlugin> pluginsMap = new ConcurrentHashMap<>();

    public CivilizationPlugin registerPlugin(String pluginId, String pluginName, String version) {
        CivilizationPlugin plugin = new CivilizationPlugin(pluginId, pluginName, version, true);
        pluginsMap.put(pluginId, plugin);
        log.info("Registered plugin: {} ({}) v{}", pluginName, pluginId, version);
        return plugin;
    }

    public CivilizationPlugin registerPlugin(CivilizationPlugin plugin) {
        if (plugin == null || plugin.pluginId() == null) {
            throw new IllegalArgumentException("Plugin and pluginId must not be null");
        }
        pluginsMap.put(plugin.pluginId(), plugin);
        log.info("Registered plugin: {} ({}) v{}", plugin.pluginName(), plugin.pluginId(), plugin.version());
        return plugin;
    }

    public Optional<CivilizationPlugin> togglePluginState(String pluginId, boolean active) {
        CivilizationPlugin existing = pluginsMap.get(pluginId);
        if (existing == null) {
            return Optional.empty();
        }
        CivilizationPlugin updated = existing.withActive(active);
        pluginsMap.put(pluginId, updated);
        log.info("Plugin {} active state toggled to {}", pluginId, active);
        return Optional.of(updated);
    }

    public Optional<CivilizationPlugin> togglePluginState(String pluginId) {
        CivilizationPlugin existing = pluginsMap.get(pluginId);
        if (existing == null) {
            return Optional.empty();
        }
        return togglePluginState(pluginId, !existing.active());
    }

    public Optional<CivilizationPlugin> getPluginById(String pluginId) {
        if (pluginId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(pluginsMap.get(pluginId));
    }

    public List<CivilizationPlugin> getRegisteredPlugins() {
        return new ArrayList<>(pluginsMap.values());
    }

    public List<CivilizationPlugin> getActivePlugins() {
        return pluginsMap.values().stream()
                .filter(CivilizationPlugin::active)
                .toList();
    }

    public int executeSimulationHooks() {
        List<CivilizationPlugin> activePlugins = getActivePlugins();
        int executedCount = activePlugins.size();
        log.info("[PLUGIN SDK TICK] Executed simulation hooks for {} active plugins", executedCount);
        return executedCount;
    }

    public void processPluginTick() {
        executeSimulationHooks();
    }
}
