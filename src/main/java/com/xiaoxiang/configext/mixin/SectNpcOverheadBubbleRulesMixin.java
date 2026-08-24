package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the bubble half of the "sectOverhead" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectNpcOverheadBubbleRules.class:
 *
 *   isWithinDisplayDistance(double distSq)
 *     ldc2_w 64.0 -> DISPLAY_DISTANCE_SQ (8.0^2); the argument is a SQUARED
 *                    distance, so the config value is squared
 *   fadeAlpha(...)
 *     ldc2_w 10.0 (x2) -> FADE_TICKS, the fade-in and fade-out ramp length
 *
 * MIN_DURATION_TICKS (200) and MAX_DURATION_TICKS (600) are NOT wired here:
 * the only place those literals appear is isValidDuration(), a pure validator,
 * and the code that actually picks a bubble duration could not be identified
 * with confidence. Rewriting only the validator would change nothing useful,
 * so those two config fields are intentionally left unwired.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectNpcOverheadBubbleRules", remap = false)
public abstract class SectNpcOverheadBubbleRulesMixin {

    /** DISPLAY_DISTANCE_SQ. */
    @ModifyConstant(method = "isWithinDisplayDistance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 64.0), remap = false, require = 0)
    private static double configExt$bubbleDisplayDistanceSq(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        double d = ExtendedConfig.SECT_BUBBLE_DISPLAY_DISTANCE.get();
        return d * d;
    }

    /** FADE_TICKS (both the fade-in and fade-out ramps). */
    @ModifyConstant(method = "fadeAlpha", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 10.0), remap = false, require = 0)
    private static double configExt$bubbleFadeTicks(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int t = ExtendedConfig.SECT_BUBBLE_FADE_TICKS.get();
        // A zero ramp would divide by zero in the original alpha calculation.
        return t > 0 ? (double) t : original;
    }
}
