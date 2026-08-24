package com.xiaoxiang.configext.api;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Auto-discovers and registers expansion mods that implement {@link IXiaoxiangExpansion}
 * or that have a static method {@code getXiaoxiangConfigSpec()} returning a ForgeConfigSpec.
 *
 * This runs during the extension mod's initialization, after all mods have been loaded.
 * It scans the ModList for any mod that:
 * 1. Implements IXiaoxiangExpansion interface, OR
 * 2. Has a static method "getXiaoxiangConfigSpec" returning ForgeConfigSpec, OR
 * 3. Has been manually registered via ExpansionConfigRegistry.register()
 *
 * When an expansion is found, its config spec is registered and all its config
 * values become visible in the config screen and subject to perk effects.
 */
public class ExpansionDiscovery {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Scan all loaded mods for expansion configs and register them.
     * Should be called after all mods have been initialized (FMLCommonSetupEvent or later).
     */
    public static void discoverAndRegister() {
        ModList modList = ModList.get();
        if (modList == null) return;

        int count = 0;

        for (ModFileScanData scanData : modList.getAllScanData()) {
            // Check for IXiaoxiangExpansion implementors via annotation data
            // The interface itself isn't annotated, but we can check class hierarchy
        }

        // Simpler approach: iterate all mods and try to find their main class
        for (net.minecraftforge.forgespi.language.IModInfo modInfo : modList.getMods()) {
            String modId = modInfo.getModId();

            // Skip ourselves and the base mod
            if (modId.equals("xiaoxiang_config_ext") || modId.equals("xiaoxiang_cultivation")) continue;

            // Skip already registered
            if (ExpansionConfigRegistry.isRegistered(modId)) continue;

            // Try to find the mod's main class
            try {
                String className = findModMainClass(modId);
                if (className == null) continue;

                Class<?> modClass = Class.forName(className);

                // Check if it implements IXiaoxiangExpansion
                if (IXiaoxiangExpansion.class.isAssignableFrom(modClass)) {
                    // Get the config spec from the interface method
                    try {
                        // Try static method first (in case the interface is implemented statically)
                        Method getConfigSpec = findMethod(modClass, "getConfigSpec");
                        if (getConfigSpec != null) {
                            Object spec = getConfigSpec.invoke(null);
                            if (spec instanceof net.minecraftforge.common.ForgeConfigSpec) {
                                Method getDisplayName = findMethod(modClass, "getDisplayName");
                                String displayName = modId;
                                if (getDisplayName != null) {
                                    Object name = getDisplayName.invoke(null);
                                    if (name instanceof String) displayName = (String) name;
                                }
                                ExpansionConfigRegistry.register(modId, displayName,
                                        (net.minecraftforge.common.ForgeConfigSpec) spec);
                                count++;
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        // Fall through to alternative detection
                    }
                }

                // Alternative: look for a static method "getXiaoxiangConfigSpec"
                try {
                    Method m = findMethod(modClass, "getXiaoxiangConfigSpec");
                    if (m != null) {
                        Object spec = m.invoke(null);
                        if (spec instanceof net.minecraftforge.common.ForgeConfigSpec) {
                            String displayName = modInfo.getDisplayName();
                            ExpansionConfigRegistry.register(modId, displayName,
                                    (net.minecraftforge.common.ForgeConfigSpec) spec);
                            count++;
                        }
                    }
                } catch (Exception e) {
                    // Not an expansion mod, skip
                }

            } catch (ClassNotFoundException e) {
                // Can't load the mod class, skip
            } catch (Exception e) {
                LOGGER.debug("[XiaoxiangConfigExt] Error scanning mod '{}' for expansion config: {}",
                        modId, e.getMessage());
            }
        }

        if (count > 0) {
            LOGGER.info("[XiaoxiangConfigExt] Auto-discovered and registered {} expansion mod(s).", count);
        }
    }

    /**
     * Find the main class of a mod by its mod ID.
     * Forge stores this in the mod's metadata.
     */
    private static String findModMainClass(String modId) {
        try {
            // Try to get the mod container and its main class
            Optional<? extends net.minecraftforge.fml.ModContainer> container =
                    ModList.get().getModContainerById(modId);
            if (container.isPresent()) {
                // The mod container's class is typically the @Mod annotated class
                Object modInstance = container.get().getModInfo();
                // Try to get the class from the container
                try {
                    java.lang.reflect.Field f = container.get().getClass().getDeclaredField("modInstance");
                    f.setAccessible(true);
                    Object instance = f.get(container.get());
                    if (instance != null) {
                        return instance.getClass().getName();
                    }
                } catch (Exception e) {
                    // Fall through
                }
            }
        } catch (Exception e) {
            // Fall through
        }

        // Fallback: try common naming conventions
        String[] candidates = {
            "com.xiaoxiang." + modId.replace("xiaoxiang_", "") + "." + toClassName(modId),
            "com.xiaoxiang.expansion." + toClassName(modId),
            modId + "." + toClassName(modId)
        };

        for (String candidate : candidates) {
            try {
                Class.forName(candidate);
                return candidate;
            } catch (ClassNotFoundException e) {
                // try next
            }
        }

        return null;
    }

    private static String toClassName(String modId) {
        StringBuilder sb = new StringBuilder();
        for (String part : modId.split("_")) {
            if (sb.length() > 0) sb.append("_");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    private static Method findMethod(Class<?> clazz, String name) {
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 0) {
                    m.setAccessible(true);
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
