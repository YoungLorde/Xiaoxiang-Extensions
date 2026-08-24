package com.xiaoxiang.configext.api;

import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for expansion mods to register their ForgeConfigSpec instances.
 *
 * Expansion mods should call {@link #register} during their mod initialization
 * to make their config values visible to the Xiaoxiang Config Extension system.
 * This enables:
 * - Automatic inclusion in the config screen (CustomConfigScreen)
 * - Automatic perk effect application (PerkApplier)
 *
 * Expansion mods do NOT need to depend on this extension mod at compile time.
 * They can call this via reflection or by including this API class.
 *
 * Usage from an expansion mod:
 * <pre>
 * ExpansionConfigRegistry.register("my_expansion", "My Expansion", myForgeConfigSpec);
 * </pre>
 *
 * The extension mod will automatically detect the expansion via ModList and
 * include its configs in the UI.
 */
public class ExpansionConfigRegistry {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Information about a registered expansion's config. */
    public static class ExpansionConfig {
        public final String modId;
        public final String displayName;
        public final ForgeConfigSpec spec;
        public final List<String> configPaths;

        public ExpansionConfig(String modId, String displayName, ForgeConfigSpec spec, List<String> configPaths) {
            this.modId = modId;
            this.displayName = displayName;
            this.spec = spec;
            this.configPaths = configPaths;
        }
    }

    /** All registered expansion configs, keyed by mod ID. */
    private static final Map<String, ExpansionConfig> REGISTERED = new ConcurrentHashMap<>();

    /** All registered ForgeConfigSpecs (including the base extension's own spec). */
    private static final List<ForgeConfigSpec> ALL_SPECS = new ArrayList<>();

    /** All registered config paths mapped to their spec. */
    private static final Map<String, ForgeConfigSpec> PATH_TO_SPEC = new ConcurrentHashMap<>();

    static {
        // Register the base extension's own config
        // This is done lazily when the base mod initializes
    }

    /**
     * Register an expansion mod's config spec.
     *
     * @param modId       The expansion mod's ID (e.g., "xiaoxiang_expansion")
     * @param displayName Human-readable name for display in the config screen
     * @param spec        The ForgeConfigSpec containing the expansion's config values
     */
    public static void register(String modId, String displayName, ForgeConfigSpec spec) {
        if (REGISTERED.containsKey(modId)) {
            LOGGER.warn("[XiaoxiangConfigExt] Expansion '{}' is already registered, skipping duplicate.", modId);
            return;
        }

        List<String> paths = extractConfigPaths(spec);
        ExpansionConfig config = new ExpansionConfig(modId, displayName, spec, paths);

        REGISTERED.put(modId, config);
        ALL_SPECS.add(spec);

        for (String path : paths) {
            PATH_TO_SPEC.put(path, spec);
        }

        LOGGER.info("[XiaoxiangConfigExt] Registered expansion config: '{}' ({} config values)",
                displayName, paths.size());
    }

    /**
     * Check if an expansion mod is registered.
     */
    public static boolean isRegistered(String modId) {
        return REGISTERED.containsKey(modId);
    }

    /**
     * Get all registered expansion configs.
     */
    public static Collection<ExpansionConfig> getAll() {
        return Collections.unmodifiableCollection(REGISTERED.values());
    }

    /**
     * Get all registered ForgeConfigSpecs (including the base extension's).
     */
    public static List<ForgeConfigSpec> getAllSpecs() {
        return Collections.unmodifiableList(ALL_SPECS);
    }

    /**
     * Get the ForgeConfigSpec that contains a given config path.
     * Returns null if the path is not found in any registered spec.
     */
    public static ForgeConfigSpec getSpecForPath(String path) {
        return PATH_TO_SPEC.get(path);
    }

    /**
     * Get all config paths from all registered specs.
     */
    public static List<String> getAllPaths() {
        return new ArrayList<>(PATH_TO_SPEC.keySet());
    }

    /**
     * Extract all config paths from a ForgeConfigSpec by traversing its config tree.
     */
    private static List<String> extractConfigPaths(ForgeConfigSpec spec) {
        List<String> paths = new ArrayList<>();
        try {
            // ForgeConfigSpec stores its values in a Config that we can traverse
            // The spec's values map contains all ConfigValue entries
            // We access them via reflection since there's no public API
            java.lang.reflect.Field valuesField = ForgeConfigSpec.class.getDeclaredField("values");
            valuesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<List<String>, ?> values = (Map<List<String>, ?>) valuesField.get(spec);
            if (values != null) {
                for (List<String> path : values.keySet()) {
                    paths.add(String.join(".", path));
                }
            }
        } catch (Exception e) {
            // Fallback: try the config's entry set
            try {
                java.lang.reflect.Field configField = ForgeConfigSpec.class.getDeclaredField("childConfig");
                configField.setAccessible(true);
                Object config = configField.get(spec);
                if (config instanceof com.electronwill.nightconfig.core.Config) {
                    extractPathsFromConfig((com.electronwill.nightconfig.core.Config) config, "", paths);
                }
            } catch (Exception e2) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to extract config paths from spec: {}", e.getMessage());
            }
        }
        return paths;
    }

    /**
     * Recursively extract paths from a NightConfig Config object.
     */
    private static void extractPathsFromConfig(com.electronwill.nightconfig.core.Config config, String prefix, List<String> paths) {
        for (com.electronwill.nightconfig.core.Config.Entry entry : config.entrySet()) {
            String key = entry.getKey();
            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = entry.getValue();
            if (value instanceof com.electronwill.nightconfig.core.Config) {
                extractPathsFromConfig((com.electronwill.nightconfig.core.Config) value, fullPath, paths);
            } else {
                paths.add(fullPath);
            }
        }
    }

    /**
     * Register the base extension's own config spec.
     * Called by XiaoxiangConfigExt during initialization.
     */
    public static void registerBase(ForgeConfigSpec spec) {
        register("xiaoxiang_config_ext", "Xiaoxiang Config Extension", spec);
    }
}
