package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the "sectAmbient" config section to real behaviour.
 *
 * SectAmbientInteractionRules exposes its tuning as `static final` primitives,
 * which javac inlines, so the literals are rewritten at their use sites.
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectAmbientInteractionRules.class:
 *
 *   canStart(Phase, int, int, long, long)
 *     ordinal 0 of iconst_2 -> MAX_ACTIVE_SCENES_PER_LEVEL (activeScenes < 2)
 *     ordinal 1 of iconst_2 -> a minimum-candidates gate, deliberately NOT
 *                              touched (it is not a config-backed value)
 *   nextSectCooldown(long) -> `1800 + bounded(mix, 2401)`, i.e.
 *     sipush 1800 -> MIN_SECT_COOLDOWN_TICKS
 *     sipush 2401 -> the exclusive span (max - min + 1) = 4200 - 1800 + 1
 *   withinPairSearchDistance(double) -> compares against PAIR_SEARCH_DISTANCE_SQ
 *     ldc2_w 100.0 -> PAIR_SEARCH_DISTANCE (10.0) squared
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectAmbientInteractionRules", remap = false)
public abstract class SectAmbientInteractionRulesMixin {

    /** MAX_ACTIVE_SCENES_PER_LEVEL - first iconst_2 in canStart. */
    @ModifyConstant(method = "canStart", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 2, ordinal = 0), remap = false, require = 0)
    private static int configExt$maxActiveScenesPerLevel(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_AMBIENT_MAX_ACTIVE_SCENES_PER_LEVEL.get();
    }

    /** MIN_SECT_COOLDOWN_TICKS - base of `1800 + bounded(mix, 2401)`. */
    @ModifyConstant(method = "nextSectCooldown", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 1800), remap = false, require = 0)
    private static int configExt$minSectCooldownTicks(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_AMBIENT_MIN_SECT_COOLDOWN_TICKS.get();
    }

    /** MAX_SECT_COOLDOWN_TICKS - exclusive span of `1800 + bounded(mix, 2401)`. */
    @ModifyConstant(method = "nextSectCooldown", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 2401), remap = false, require = 0)
    private static int configExt$sectCooldownSpan(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int min = ExtendedConfig.SECT_AMBIENT_MIN_SECT_COOLDOWN_TICKS.get();
        int max = ExtendedConfig.SECT_AMBIENT_MAX_SECT_COOLDOWN_TICKS.get();
        return Math.max(1, max - min + 1);
    }

    /** PAIR_SEARCH_DISTANCE_SQ - the config value is a plain distance. */
    @ModifyConstant(method = "withinPairSearchDistance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 100.0), remap = false, require = 0)
    private static double configExt$pairSearchDistanceSq(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        double d = ExtendedConfig.SECT_AMBIENT_PAIR_SEARCH_DISTANCE.get();
        return d * d;
    }
}
