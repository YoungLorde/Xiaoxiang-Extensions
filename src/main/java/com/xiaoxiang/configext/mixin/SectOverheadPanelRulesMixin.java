package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the panel half of the "sectOverhead" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectOverheadPanelRules.class:
 *
 *   shouldShowDetailedPanel(double distSq)
 *     ldc2_w 4096.0 -> PANEL_RENDER_DISTANCE_SQ (64.0^2); the argument is a
 *                      SQUARED distance, so the config value is squared
 *   distanceAlpha(double distSq)
 *     ldc2_w 56.0 (x2) -> PANEL_FADE_START_DISTANCE, compared against the
 *                      sqrt of the argument, so a plain distance
 *     ldc2_w 8.0       -> the fade band width, which is
 *                      PANEL_RENDER_DISTANCE - PANEL_FADE_START_DISTANCE
 *
 * The 3.0 / 2.0 literals in distanceAlpha are the smoothstep polynomial
 * coefficients and are deliberately left alone.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectOverheadPanelRules", remap = false)
public abstract class SectOverheadPanelRulesMixin {

    /** PANEL_RENDER_DISTANCE_SQ. */
    @ModifyConstant(method = "shouldShowDetailedPanel", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 4096.0), remap = false, require = 0)
    private static double configExt$panelRenderDistanceSq(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        double d = ExtendedConfig.SECT_OVERHEAD_PANEL_RENDER_DISTANCE.get();
        return d * d;
    }

    /** PANEL_FADE_START_DISTANCE (both occurrences). */
    @ModifyConstant(method = "distanceAlpha", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 56.0), remap = false, require = 0)
    private static double configExt$panelFadeStartDistance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_OVERHEAD_PANEL_FADE_START_DISTANCE.get();
    }

    /** The fade band width: render distance minus fade-start distance. */
    @ModifyConstant(method = "distanceAlpha", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 8.0), remap = false, require = 0)
    private static double configExt$panelFadeBand(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        double start = ExtendedConfig.SECT_OVERHEAD_PANEL_FADE_START_DISTANCE.get();
        double end = ExtendedConfig.SECT_OVERHEAD_PANEL_RENDER_DISTANCE.get();
        double band = end - start;
        // Guard against a zero/negative band, which would divide by zero in
        // the original alpha ramp.
        return band > 0.0001 ? band : 0.0001;
    }
}
