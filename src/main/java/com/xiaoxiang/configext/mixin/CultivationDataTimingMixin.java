package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to CultivationData time acceleration and tribulation timing.
 */
@Mixin(CultivationData.class)
public abstract class CultivationDataTimingMixin {

    /**
     * Override the max time acceleration multiplier check.
     * The original method checks if a multiplier is <= MAX_TIME_ACCELERATION_MULTIPLIER.
     * We re-check with our config value.
     */
    @Inject(method = "isAllowedTimeAccelerationMultiplier", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$isAllowedTimeAccelerationMultiplier(int mult, CallbackInfoReturnable<Boolean> cir) {
        int max = ExtendedConfig.CULTIVATION_MAX_TIME_ACCELERATION_MULTIPLIER.get();
        boolean result = mult >= 1 && mult <= max;
        if (result != cir.getReturnValue()) cir.setReturnValue(result);
    }
}
