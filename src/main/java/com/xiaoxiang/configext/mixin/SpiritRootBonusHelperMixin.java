package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.SpiritRootBonusHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Overrides spirit root qi absorption multipliers.
 *
 * REAL BUG FIX (2026-09-01): the previous version of this mixin injected at
 * @At("RETURN") and tried to tell SSR-rarity apart from the mutant
 * environment-buff case by checking "was the returned double exactly 1.5?" -
 * but javap -p -c -s on qiAbsorptionMultiplier(Player) shows the environment
 * buff is a MULTIPLICATIVE step applied on top of whatever the rarity switch
 * already produced (dmul against the running value), not a second place that
 * independently returns 1.5. That means "final value == 1.5" is ambiguous by
 * construction - it's also produced by a common-rarity root (base 1.0) with
 * an active cold/storm buff (1.0*1.5=1.5), which the old code would have
 * misattributed to the SSR branch and scaled by the wrong config field
 * (SPIRIT_ROOT_QI_ABSORB_SSR_MULT instead of
 * SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT). This only showed up once either
 * multiplier was changed away from its default (both default to 1.5, so a
 * stock config never exposed the bug) - since we're already doing this
 * category's audit pass, wiring it correctly rather than leaving this latent
 * mistake in place.
 *
 * Bytecode confirms the literal 1.5d (constant pool #255) appears exactly
 * three times in this one method: ordinal 0 is the SSR-rarity tableswitch
 * case, ordinal 1 is the MUTANT_ICE cold-biome multiply, ordinal 2 is the
 * MUTANT_LIGHTNING storm multiply - @Constant(ordinal=N) disambiguates all
 * three at the exact bytecode site, so there's no return-value guessing left.
 * 1.25d (SR-rarity case) is a single, unambiguous occurrence.
 */
@Mixin(value = SpiritRootBonusHelper.class, remap = false)
public abstract class SpiritRootBonusHelperMixin {

    @ModifyConstant(
            method = "qiAbsorptionMultiplier",
            constant = @Constant(doubleValue = 1.5, ordinal = 0),
            require = 0)
    private static double configExt$qiAbsorbSsrMult(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_ROOT_QI_ABSORB_SSR_MULT.get();
    }

    @ModifyConstant(
            method = "qiAbsorptionMultiplier",
            constant = @Constant(doubleValue = 1.25),
            require = 0)
    private static double configExt$qiAbsorbSrMult(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_ROOT_QI_ABSORB_SR_MULT.get();
    }

    @ModifyConstant(
            method = "qiAbsorptionMultiplier",
            constant = @Constant(doubleValue = 1.5, ordinal = 1),
            require = 0)
    private static double configExt$environmentBuffCold(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT.get();
    }

    @ModifyConstant(
            method = "qiAbsorptionMultiplier",
            constant = @Constant(doubleValue = 1.5, ordinal = 2),
            require = 0)
    private static double configExt$environmentBuffStorm(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT.get();
    }
}
