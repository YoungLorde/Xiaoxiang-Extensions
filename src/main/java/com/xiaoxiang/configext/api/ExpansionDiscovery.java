package com.xiaoxiang.configext.api;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.lang.reflect.Method;
import java.util.Optional;

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

        for (IModInfo modInfo : modList.getMods()) {
            String modId = modInfo.getModId();

            // Skip ourselves and the base mod
            if (modId.equals("xiaoxiang_config_ext") || modId.equals("xiaoxiang_cultivation")) continue;

            // Skip already registered
            if (ExpansionConfigRegistry.isRegistered(modId)) continue;

            Optional<? extends ModContainer> container = modList.getModContainerById(modId);
            if (container.isEmpty()) continue;

            Object modInstance = container.get().getMod();
            if (modInstance == null) continue;

            try {
                if (modInstance instanceof IXiaoxiangExpansion) {
                    IXiaoxiangExpansion expansion = (IXiaoxiangExpansion) modInstance;
                    ForgeConfigSpec spec = expansion.getConfigSpec();
                    if (spec != null) {
                        String displayName = expansion.getDisplayName();
                        if (displayName == null || displayName.isEmpty()) displayName = modInfo.getDisplayName();
                        ExpansionConfigRegistry.register(modId, displayName, spec);
                        count++;
                        continue;
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("[XiaoxiangConfigExt] Mod '{}' implements IXiaoxiangExpansion but threw "
                        + "getting its config spec: {}", modId, e.getMessage());
            }

            // Mods that don't implement the interface can still opt in with a static
            // getXiaoxiangConfigSpec() method, so they don't need this mod as a compile-time dependency.
            try {
                Method m = findStaticMethod(modInstance.getClass(), "getXiaoxiangConfigSpec");
                if (m != null) {
                    Object spec = m.invoke(null);
                    if (spec instanceof ForgeConfigSpec) {
                        ExpansionConfigRegistry.register(modId, modInfo.getDisplayName(), (ForgeConfigSpec) spec);
                        count++;
                    }
                }
            } catch (Exception e) {
                // Not an expansion mod, skip
            }
        }

        if (count > 0) {
            LOGGER.info("[XiaoxiangConfigExt] Auto-discovered and registered {} expansion mod(s).", count);
        }
    }

    private static Method findStaticMethod(Class<?> clazz, String name) {
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 0
                        && java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                    m.setAccessible(true);
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
