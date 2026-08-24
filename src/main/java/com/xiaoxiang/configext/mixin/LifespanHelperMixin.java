package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.cultivation.LifespanHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to LifespanHelper methods.
 * The original mod's LifespanHelper has hardcoded constants for bone age,
 * age per day, near-immortal threshold, and death penalty.
 * We inject at RETURN to override the computed values.
 */
@Mixin(value = LifespanHelper.class, remap = false)
public abstract class LifespanHelperMixin {

    /**
     * Override the near-immortal check to use config threshold.
     */
    @Inject(method = "isNearImmortal", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$isNearImmortal(CultivationData data, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtendedConfig.ENABLE_LIFESPAN_OVERRIDES.get()) return;
        int threshold = ExtendedConfig.LIFESPAN_NEAR_IMMORTAL_THRESHOLD.get();
        double boneAge = data.getBoneAge();
        boolean result = boneAge >= threshold;
        if (result != cir.getReturnValue()) cir.setReturnValue(result);
    }

    /**
     * Override the ordinary death penalty to use config value.
     */
    @Inject(method = "applyOrdinaryDeathPenalty", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$applyOrdinaryDeathPenalty(CultivationData data, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_LIFESPAN_OVERRIDES.get()) return;
        int configPenalty = ExtendedConfig.LIFESPAN_ORDINARY_DEATH_PENALTY_YEARS.get();
        // The original method subtracts 1 year (default penalty). We undo that and
        // apply the config penalty instead.
        int currentResult = cir.getReturnValue();
        int adjusted = currentResult + 1 - configPenalty;
        if (adjusted != currentResult) cir.setReturnValue(adjusted);
    }
}
