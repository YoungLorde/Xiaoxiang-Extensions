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
                        continue;
                    }
                }
            } catch (Exception e) {
                // Not an expansion mod, skip
            }

            // Tier 3: zero-effort auto-adoption for "Xiao"-branded mods. Neither of the
            // two opt-in mechanisms above require any relationship to this mod's name -
            // they work for any mod at all, as long as it explicitly implements the
            // interface or exposes that one specially-named method. This tier is the
            // opposite: it requires NO code changes whatsoever in the other mod, but
            // only fires for mods whose ID or display name contains "xiao" - the
            // signature the user established for "this is one of ours." Most Forge
            // mods keep their ForgeConfigSpec in a plain static field somewhere on
            // their main @Mod class (commonly named SPEC, COMMON_SPEC, etc., but the
            // exact name varies project to project) - so instead of requiring a
            // specific method name, this just looks for ANY static field of type
            // ForgeConfigSpec on that class and uses whichever one it finds first.
            if (isXiaoxiangBranded(modId, modInfo.getDisplayName())) {
                try {
                    ForgeConfigSpec spec = findStaticForgeConfigSpecField(modInstance.getClass());
                    if (spec != null) {
                        ExpansionConfigRegistry.register(modId, modInfo.getDisplayName(), spec);
                        count++;
                        LOGGER.info("[XiaoxiangConfigExt] Auto-adopted Xiao-branded mod '{}' via its own "
                                + "static ForgeConfigSpec field - no integration code needed on its side.", modId);
                    }
                } catch (Exception e) {
                    LOGGER.debug("[XiaoxiangConfigExt] Mod '{}' looks Xiao-branded but no usable "
                            + "ForgeConfigSpec field was found on its main class: {}", modId, e.getMessage());
                }
            }
        }

        if (count > 0) {
            LOGGER.info("[XiaoxiangConfigExt] Auto-discovered and registered {} expansion mod(s).", count);
        }
    }

    /**
     * Whether a mod counts as "Xiao"-branded for the purposes of Tier 3 auto-adoption -
     * its mod ID or display name contains "xiao" (case-insensitive), matching either
     * the "xiaoxiang_" mod ID convention or a display name like "Xiaoxiang Realm
     * Expansion" even if some future mod's ID doesn't happen to start with it.
     */
    private static boolean isXiaoxiangBranded(String modId, String displayName) {
        if (modId != null && modId.toLowerCase(java.util.Locale.ROOT).contains("xiao")) return true;
        return displayName != null && displayName.toLowerCase(java.util.Locale.ROOT).contains("xiao");
    }

    /**
     * Look for a static field of type ForgeConfigSpec on the given class (not its
     * superclasses - a mod's own @Mod class is where this is almost always declared
     * directly). Returns the first one found with a non-null value, or null if none.
     */
    private static ForgeConfigSpec findStaticForgeConfigSpecField(Class<?> clazz) {
        for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
            if (ForgeConfigSpec.class.isAssignableFrom(f.getType())
                    && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(null);
                    if (value instanceof ForgeConfigSpec) {
                        return (ForgeConfigSpec) value;
                    }
                } catch (Exception ignored) {
                    // Inaccessible or unreadable - try the next candidate field.
                }
            }
        }
        return null;
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
