package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Wires all 15 SECT_SHOP_* config fields (technique/spell/weapon prices per
 * tier, plus sellPercent) to the real sect-shop pricing logic.
 *
 * REPLACES a prior version of this file that was fundamentally broken: it
 * injected at tierPrice()'s RETURN and derived a single ratio from
 * SECT_SHOP_TECHNIQUE_PRICE_LOW alone (configLow / 100), then applied that
 * ONE ratio uniformly to every tierPrice() call - technique, spell, AND
 * weapon alike. That meant 14 of the 15 configured price fields did
 * literally nothing (only technique-low's ratio mattered), and it was
 * impossible to price spells/weapons differently from techniques, which is
 * exactly the kind of "config value silently ignored" bug this audit exists
 * to catch. Caught by re-deriving the real call graph via javap rather than
 * trusting the prior mixin's own comment.
 *
 * Verified via javap -p -c -s against SectShopPricing.class (2026-09-01):
 *
 * - basePrice(Item) calls the private static
 *   tierPrice(ItemTier, int low, int mid, int high, int supreme, int immortal)
 *   from exactly 4 call sites, each with a distinct hardcoded 5-int tuple:
 *     technique:   (100, 1000, 10000, 80000, 320000)
 *     spell:       (50, 500, 5000, 30000, 160000)
 *     weapon:      (200, 1000, 5000, 40000, 160000)
 *     storage_bag: (80, 300, 2000, 20000, 120000)
 *   Every one of those literals exactly matches this mod's own
 *   SECT_SHOP_*_PRICE_* config defaults for technique/spell/weapon - strong
 *   confirmation the tuple-identity match below is correct, not a guess.
 *   storage_bag has no corresponding config section in ExtendedConfig (a
 *   real, separate gap - not fixed here, left passing through unmodified
 *   rather than silently mis-pricing it against the wrong category).
 *
 * - tierPrice() itself just returns whichever of the 5 params matches the
 *   ItemTier's position (tier==null returns low). Rather than re-implement
 *   that tier-index selection ourselves (risking a wrong guess at how
 *   javac's switch-on-enum ordinal table was generated), this mixin
 *   redirects the 4 call sites to swap in config-sourced ints for the
 *   matched category and then calls the REAL tierPrice via @Shadow, so
 *   vanilla's own (verified-correct) tier selection still runs unchanged.
 *
 * - quote(ItemStack) computes the sell price as
 *   Math.max(1, buyContribution * 60 / 100) - the literal 60 is
 *   SectShopPricing.SELL_PERCENT inlined at compile time (confirmed via
 *   javap -v: SELL_PERCENT carries a ConstantValue attribute, so a
 *   field-redirect on the field itself would never fire - there is no
 *   getstatic to redirect, only baked-in literals at every use site,
 *   including this one inside SectShopPricing's own quote() method).
 *   Wired via @ModifyConstant instead, matching this project's established
 *   convention for compile-time-inlined constants (see LifespanHandlerMixin).
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectShopPricing", remap = false)
public abstract class SectShopPricingMixin {

    @Shadow(remap = false)
    private static int tierPrice(ItemTier tier, int low, int mid, int high, int supreme, int immortal) {
        // Shadowed - body replaced at runtime with the real tierPrice().
        return 0;
    }

    @Redirect(
            method = "basePrice",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/SectShopPricing;tierPrice(Lcom/xiaoxiang/cultivation/cultivation/ItemTier;IIIII)I"),
            remap = false, require = 0)
    private static int configExt$redirectTierPrice(ItemTier tier, int low, int mid, int high, int supreme, int immortal) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) {
            return tierPrice(tier, low, mid, high, supreme, immortal);
        }
        if (low == 100 && mid == 1000 && high == 10000 && supreme == 80000 && immortal == 320000) {
            // technique
            return tierPrice(tier,
                    ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_LOW.get(),
                    ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_MID.get(),
                    ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_HIGH.get(),
                    ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_SUPREME.get(),
                    ExtendedConfig.SECT_SHOP_TECHNIQUE_PRICE_IMMORTAL.get());
        }
        if (low == 50 && mid == 500 && high == 5000 && supreme == 30000 && immortal == 160000) {
            // spell
            return tierPrice(tier,
                    ExtendedConfig.SECT_SHOP_SPELL_PRICE_LOW.get(),
                    ExtendedConfig.SECT_SHOP_SPELL_PRICE_MID.get(),
                    ExtendedConfig.SECT_SHOP_SPELL_PRICE_HIGH.get(),
                    ExtendedConfig.SECT_SHOP_SPELL_PRICE_SUPREME.get(),
                    ExtendedConfig.SECT_SHOP_SPELL_PRICE_IMMORTAL.get());
        }
        if (low == 200 && mid == 1000 && high == 5000 && supreme == 40000 && immortal == 160000) {
            // weapon
            return tierPrice(tier,
                    ExtendedConfig.SECT_SHOP_WEAPON_PRICE_LOW.get(),
                    ExtendedConfig.SECT_SHOP_WEAPON_PRICE_MID.get(),
                    ExtendedConfig.SECT_SHOP_WEAPON_PRICE_HIGH.get(),
                    ExtendedConfig.SECT_SHOP_WEAPON_PRICE_SUPREME.get(),
                    ExtendedConfig.SECT_SHOP_WEAPON_PRICE_IMMORTAL.get());
        }
        // storage_bag, or any other future call site: no config section exists
        // for it yet - pass the original hardcoded values through unchanged
        // rather than guess.
        return tierPrice(tier, low, mid, high, supreme, immortal);
    }

    @ModifyConstant(method = "quote", constant = @Constant(intValue = 60), remap = false, require = 0)
    private static int configExt$sellPercent(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_SHOP_SELL_PERCENT.get();
    }
}
