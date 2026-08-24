package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.alchemy.AlchemyRank;
import com.xiaoxiang.cultivation.cultivation.alchemy.PillTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides alchemy XP gain values with config-driven ones.
 */
@Mixin(AlchemyRank.class)
public abstract class AlchemyRankMixin {

    @Inject(method = "xpGainFor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$xpGainFor(PillTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_ALCHEMY_OVERRIDES.get()) {
            return;
        }
        int val = switch (tier) {
            default -> ExtendedConfig.ALCHEMY_XP_GAIN_LOW.get();
            case MID -> ExtendedConfig.ALCHEMY_XP_GAIN_MID.get();
            case HIGH -> ExtendedConfig.ALCHEMY_XP_GAIN_HIGH.get();
            case SUPREME -> ExtendedConfig.ALCHEMY_XP_GAIN_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.ALCHEMY_XP_GAIN_IMMORTAL.get();
        };
        cir.setReturnValue(val);
    }

    @Inject(method = "xpGainForFailure", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$xpGainForFailure(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_ALCHEMY_OVERRIDES.get()) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.ALCHEMY_XP_GAIN_FAILURE.get());
    }
}
