package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.SpiritRootBonusHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides spirit root qi absorption multipliers.
 * Applies config multipliers on top of the original calculation.
 */
@Mixin(SpiritRootBonusHelper.class)
public abstract class SpiritRootBonusHelperMixin {

    @Inject(method = "qiAbsorptionMultiplier", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$qiAbsorptionMultiplier(Player player, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) {
            return;
        }
        // The original method already computed the base multiplier.
        // We apply a scaling factor based on the SSR/SR config values relative to defaults.
        double original = cir.getReturnValue();
        // Scale: if original was 1.5 (SSR default) and config is 2.0, multiply by 2.0/1.5
        if (original == 1.5) {
            cir.setReturnValue(original * (ExtendedConfig.SPIRIT_ROOT_QI_ABSORB_SSR_MULT.get() / 1.5));
        } else if (original == 1.25) {
            cir.setReturnValue(original * (ExtendedConfig.SPIRIT_ROOT_QI_ABSORB_SR_MULT.get() / 1.25));
        } else if (original == 1.5 && ExtendedConfig.SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT.get() != 1.5) {
            // Environment buff case - original returns 1.5 for mutant environment buff
            cir.setReturnValue(original * (ExtendedConfig.SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT.get() / 1.5));
        }
    }
}
