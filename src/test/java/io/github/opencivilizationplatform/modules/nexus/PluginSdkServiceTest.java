package io.github.opencivilizationplatform.modules.nexus;

import io.github.opencivilizationplatform.modules.nexus.application.PluginSdkService;
import io.github.opencivilizationplatform.modules.nexus.domain.CivilizationPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PluginSdkServiceTest {

    private PluginSdkService pluginSdkService;

    @BeforeEach
    void setUp() {
        pluginSdkService = new PluginSdkService();
    }

    @Test
    void testRegisterPlugin() {
        CivilizationPlugin plugin = pluginSdkService.registerPlugin("plugin-1", "Weather Engine", "1.0.0");
        assertNotNull(plugin);
        assertEquals("plugin-1", plugin.pluginId());
        assertEquals("Weather Engine", plugin.pluginName());
        assertEquals("1.0.0", plugin.version());
        assertTrue(plugin.active());

        Optional<CivilizationPlugin> fetched = pluginSdkService.getPluginById("plugin-1");
        assertTrue(fetched.isPresent());
        assertEquals("Weather Engine", fetched.get().pluginName());
    }

    @Test
    void testRegisterPluginObject() {
        CivilizationPlugin plugin = new CivilizationPlugin("plugin-2", "Economy Booster", "2.1.0", false);
        pluginSdkService.registerPlugin(plugin);

        Optional<CivilizationPlugin> fetched = pluginSdkService.getPluginById("plugin-2");
        assertTrue(fetched.isPresent());
        assertFalse(fetched.get().active());
    }

    @Test
    void testTogglePluginState() {
        pluginSdkService.registerPlugin("plugin-1", "Weather Engine", "1.0.0");

        Optional<CivilizationPlugin> updated = pluginSdkService.togglePluginState("plugin-1", false);
        assertTrue(updated.isPresent());
        assertFalse(updated.get().active());

        Optional<CivilizationPlugin> toggled = pluginSdkService.togglePluginState("plugin-1");
        assertTrue(toggled.isPresent());
        assertTrue(toggled.get().active());
    }

    @Test
    void testGetActivePluginsAndExecuteHooks() {
        pluginSdkService.registerPlugin("plugin-1", "Weather Engine", "1.0.0");
        pluginSdkService.registerPlugin("plugin-2", "Economy Booster", "2.0.0");
        pluginSdkService.togglePluginState("plugin-2", false);

        List<CivilizationPlugin> active = pluginSdkService.getActivePlugins();
        assertEquals(1, active.size());
        assertEquals("plugin-1", active.get(0).pluginId());

        int hooksExecuted = pluginSdkService.executeSimulationHooks();
        assertEquals(1, hooksExecuted);
    }
}
