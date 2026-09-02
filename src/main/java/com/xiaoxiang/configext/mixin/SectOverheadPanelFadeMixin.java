package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_OVERHEAD_PANEL_FADE_IN_TICKS / SECT_OVERHEAD_PANEL_FADE_OUT_TICKS.
 *
 * Client-only class, distinct from SectOverheadPanelRulesMixin (which wires
 * the server-safe SectOverheadPanelRules distance/render fields).
 *
 * Verified via javap -p -c -s against
 * SectOverheadPanelVisibilityClient.class (2026-09-01):
 * onClientTick() advances each entity's FadeState.currentAlpha once per
 * client tick by a fixed per-tick step:
 *
 *   shouldShow == true  -> currentAlpha += 0.2f   (5 ticks to fully show)
 *   shouldShow == false -> currentAlpha -= 0.125f (8 ticks to fully hide)
 *
 * then clamps to [0, 1] via Mth.clamp. Rewiring the two float literals to
 * 1.0f / configuredTicks reproduces the same linear ramp at the configured
 * duration instead of the hardcoded 5/8 ticks.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.client.SectOverheadPanelVisibilityClient", remap = false)
public abstract class SectOverheadPanelFadeMixin {

    @ModifyConstant(method = "onClientTick", constant = @Constant(floatValue = 0.2f), remap = false, require = 0)
    private static float configExt$fadeInStep(float original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_OVERHEAD_PANEL_FADE_IN_TICKS.get();
        return ticks > 0 ? 1.0f / ticks : original;
    }

    @ModifyConstant(method = "onClientTick", constant = @Constant(floatValue = 0.125f), remap = false, require = 0)
    private static float configExt$fadeOutStep(float original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_OVERHEAD_PANEL_FADE_OUT_TICKS.get();
        return ticks > 0 ? 1.0f / ticks : original;
    }
}
