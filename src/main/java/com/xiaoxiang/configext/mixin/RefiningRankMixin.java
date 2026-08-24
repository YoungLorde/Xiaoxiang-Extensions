package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.refining.RefiningRank;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides refining XP gain values with config-driven ones.
 */
@Mixin(RefiningRank.class)
public abstract class RefiningRankMixin {

    @Inject(method = "xpGainFor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$xpGainFor(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REFINING_OVERRIDES.get()) {
            return;
        }
        int val = switch (tier) {
            default -> ExtendedConfig.REFINING_XP_GAIN_LOW.get();
            case MID -> ExtendedConfig.REFINING_XP_GAIN_MID.get();
            case HIGH -> ExtendedConfig.REFINING_XP_GAIN_HIGH.get();
            case SUPREME -> ExtendedConfig.REFINING_XP_GAIN_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.REFINING_XP_GAIN_IMMORTAL.get();
        };
        cir.setReturnValue(val);
    }

    @Inject(method = "xpGainForFailure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$xpGainForFailure(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REFINING_OVERRIDES.get()) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.REFINING_XP_GAIN_FAILURE.get());
    }
}
