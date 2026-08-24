package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Scales sect shop prices by the configured sect shop price multiplier.
 *
 * The original SectShopPricing.tierPrice() returns hardcoded prices (100, 1000, etc.)
 * This mixin multiplies the returned price by the config multiplier, so if
 * the config low price is set to 500, a 100-spirit-stone item costs 500.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectShopPricing", remap = false)
public abstract class SectShopPricingMixin {

    @Inject(method = "tierPrice", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$scaleTierPrice(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return;
        int base = cir.getReturnValue();
        if (base <= 0) return;

        // The sect shop price multiplier is applied to the config values in
        // sects.shop.techniquePrices etc. But the original mod uses hardcoded
        // values in SectShopPricing, not our config. So we need to scale the
        // returned price here.
        //
        // We use the technique price LOW as a reference to detect the multiplier:
        // if config low = 500 and hardcoded low = 100, multiplier = 5.0
        int configLow = ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_LOW.get();
        int hardcodedLow = 100;
        double mult = (double) configLow / hardcodedLow;
        if (mult == 1.0) return;

        int scaled = (int) Math.max(1, Math.round(base * mult));
        cir.setReturnValue(scaled);
    }
}
