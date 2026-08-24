package com.xiaoxiang.configext.api;

/**
 * Marker interface for Xiaoxiang Cultivation World expansion mods.
 *
 * An expansion mod should implement this interface on its main @Mod class
 * to signal that it has config values that should be registered with the
 * Xiaoxiang Config Extension system.
 *
 * When the extension mod detects a mod implementing this interface (or
 * detects the mod via ModList), it will attempt to call that mod's
 * {@link #getConfigSpec()} method to register its config.
 *
 * Example usage in an expansion mod:
 * <pre>
 * &#64;Mod("xiaoxiang_expansion")
 * public class XiaoxiangExpansion implements IXiaoxiangExpansion {
 *     public static final ForgeConfigSpec SPEC;
 *     // ... config definition ...
 *
 *     &#64;Override
 *     public ForgeConfigSpec getConfigSpec() {
 *         return SPEC;
 *     }
 *
 *     &#64;Override
 *     public String getDisplayName() {
 *         return "Xiaoxiang Expansion";
 *     }
 * }
 * </pre>
 *
 * Alternatively, an expansion mod can directly call
 * {@link ExpansionConfigRegistry#register} during its initialization
 * without implementing this interface.
 */
public interface IXiaoxiangExpansion {

    /**
     * Get the ForgeConfigSpec containing this expansion's config values.
     * These will be automatically included in the config screen.
     *
     * @return The expansion's ForgeConfigSpec
     */
    net.minecraftforge.common.ForgeConfigSpec getConfigSpec();

    /**
     * Get the human-readable display name for this expansion.
     * Used as a category name in the config screen.
     *
     * @return Display name (e.g., "Xiaoxiang Expansion: New Realms")
     */
    String getDisplayName();
}
