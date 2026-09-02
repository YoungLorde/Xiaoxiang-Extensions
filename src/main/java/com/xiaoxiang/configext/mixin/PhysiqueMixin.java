package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.Physique.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides physique rarity weights with config-driven ones.
 */
@Mixin(Physique.class)
public abstract class PhysiqueMixin {

    @Inject(method = "weightOf", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$weightOf(Rarity rarity, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) {
            return;
        }
        int val = switch (rarity) {
            default -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_LOW.get();
            case MID -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_MID.get();
            case HIGH -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_HIGH.get();
            case SUPREME -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_IMMORTAL.get();
            case SPECIAL -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_SPECIAL.get();
        };
        cir.setReturnValue(val);
    }
}
