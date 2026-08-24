package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.trial.InnerWorldTrialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Applies config overrides to InnerWorldTrialManager constants.
 * The original mod has public static final int constants for platform dimensions,
 * Y level, and soul wound duration. These are inlined as bytecode constants.
 */
@Mixin(value = InnerWorldTrialManager.class, remap = false)
public abstract class InnerWorldTrialManagerMixin {

    // PLATFORM_DIAMETER = 48 (unique)
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 48), require = 0)
    private static int configExt$platformDiameter(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_PLATFORM_DIAMETER.get();
    }

    // PLATFORM_Y = 80 (double 80.0d appears in begin method for Y coordinate)
    @ModifyConstant(method = "begin", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 80.0), require = 0)
    private static double configExt$platformY(double original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_PLATFORM_Y.get();
    }

    // SOUL_WOUND_TICKS = 1200 (unique long/int)
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 1200), require = 0)
    private static int configExt$soulWoundTicks(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_SOUL_WOUND_TICKS.get();
    }

    // TIME_STASIS_DURATION = 600 (unique)
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 600), require = 0)
    private static int configExt$timeStasisDuration(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_TIME_STASIS_DURATION.get();
    }
}
