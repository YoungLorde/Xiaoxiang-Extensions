package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.SpiritPlantCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the "spiritPlants" config section for real (2026-09-01).
 *
 * This mixin previously shipped as an intentionally-empty placeholder
 * (its own comment claimed max age "requires constructor redirection which
 * is complex" and was never registered in mixins.json). Full javap -p -c -s
 * disassembly of SpiritPlantCropBlock.class shows that claim was wrong:
 * getMaxAge() (obfuscated m_7419_, CropBlock's own override point) is a
 * one-line "iconst_3; ireturn" - a completely ordinary single-target
 * @Inject(RETURN, require = 0), no constructor work needed at all. This also fixes
 * ENABLE_SPIRIT_PLANT_OVERRIDES, which had no mixin checking it anywhere.
 *
 * All twelve fields below were verified against real bytecode; each note
 * cites the exact method and, where a literal repeats, its occurrence count.
 *
 * CORRECTED 2026-09-02 (systematic sweep): spawnQiOrbIfNotCrowded silently
 * changed from a static to an instance method in base mod 0.1.1479 (same
 * descriptor and name, only the ACC_STATIC flag changed - confirmed via
 * javap -p, which now shows it without the `static` modifier). The three
 * @ModifyConstant handlers below that target it (configExt$qiOrbSkipCount,
 * configExt$qiOrbSkipRadius, configExt$qiOrbAmount) were still declared
 * static, which Mixin rejects with InvalidInjectionException ("'static'
 * modifier of handler method does not match target") at game launch -
 * require = 0 does not soften this (it only covers "target not found by
 * name"). Fixed by dropping `static` from all three handlers; none of them
 * use `this` or share a `method` list with any other (still-static)
 * target, so no split into separate static/instance variants was needed
 * here (unlike SectDiscipleGateMixin's redirects, which do share targets).
 */
@Mixin(value = SpiritPlantCropBlock.class, remap = false)
public abstract class SpiritPlantBlockMixin {

    // getMaxAge() (Forge-obfuscated m_7419_): single "iconst_3; ireturn".
    // Governs every vanilla grow-chance/bonemeal check via the normal
    // CropBlock virtual call, so one override here covers all of them.
    @Inject(method = "m_7419_", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$maxAge(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return;
        int override = ExtendedConfig.SPIRIT_PLANT_MAX_AGE.get();
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    // growWithoutLightOnSpiritField(...): single "ldc #34 // float 25.0f",
    // feeds 2*(25.0f/lightLevel)+1 as the growth-speed divisor. Bytecode
    // literal is a float; config field is a DoubleValue, cast on write/read.
    @ModifyConstant(method = "growWithoutLightOnSpiritField",
            constant = @Constant(floatValue = 25.0F), require = 0)
    private static float configExt$growthTickBase(float original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_GROWTH_TICK_BASE.get().floatValue();
    }

    // spawnQiOrbIfNotCrowded(...): three single-occurrence literals in one
    // method - "iconst_3" (skip-count crowd gate), "ldc2_w 6.0d" (AABB
    // inflate radius for the crowd check), "bipush 10" (QiOrbEntity amount).
    @ModifyConstant(method = "spawnQiOrbIfNotCrowded",
            constant = @Constant(intValue = 3), require = 0)
    private int configExt$qiOrbSkipCount(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_SKIP_COUNT.get();
    }

    @ModifyConstant(method = "spawnQiOrbIfNotCrowded",
            constant = @Constant(doubleValue = 6.0), require = 0)
    private double configExt$qiOrbSkipRadius(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_SKIP_RADIUS.get();
    }

    @ModifyConstant(method = "spawnQiOrbIfNotCrowded",
            constant = @Constant(intValue = 10), require = 0)
    private int configExt$qiOrbAmount(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_QI_ORB_AMOUNT.get();
    }

    // meltNearbySnowLayers(...): the melt box is pos.offset(-2,-1,-2) to
    // pos.offset(2,1,2) - y is hardcoded to +-1 (untouched, this field is a
    // horizontal radius only). "bipush -2" and "iconst_2" each appear
    // exactly twice (x and z); no ordinal needed since both occurrences of
    // each literal need the identical override.
    @ModifyConstant(method = "meltNearbySnowLayers",
            constant = @Constant(intValue = -2), require = 0)
    private static int configExt$flameMeltRadiusNeg(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return -ExtendedConfig.SPIRIT_PLANT_FLAME_MELT_RADIUS.get();
    }

    @ModifyConstant(method = "meltNearbySnowLayers",
            constant = @Constant(intValue = 2), require = 0)
    private static int configExt$flameMeltRadiusPos(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_FLAME_MELT_RADIUS.get();
    }

    // boostNearbyCropGrowth(...): gate "ldc2_w 0.5d" (single occurrence,
    // random.nextDouble() >= chance -> return), plus the identical
    // offset(-2,-1,-2)/offset(2,1,2) box pattern as the melt method above,
    // scoped to this separate method so there's no cross-field collision.
    @ModifyConstant(method = "boostNearbyCropGrowth",
            constant = @Constant(doubleValue = 0.5), require = 0)
    private static double configExt$earthMarrowGrowthChance(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_EARTH_MARROW_GROWTH_CHANCE.get();
    }

    @ModifyConstant(method = "boostNearbyCropGrowth",
            constant = @Constant(intValue = -2), require = 0)
    private static int configExt$earthMarrowGrowthRadiusNeg(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return -ExtendedConfig.SPIRIT_PLANT_EARTH_MARROW_GROWTH_RADIUS.get();
    }

    @ModifyConstant(method = "boostNearbyCropGrowth",
            constant = @Constant(intValue = 2), require = 0)
    private static int configExt$earthMarrowGrowthRadiusPos(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_EARTH_MARROW_GROWTH_RADIUS.get();
    }

    // dropGoldNugget(...): single "ldc2_w 0.08d" gate.
    @ModifyConstant(method = "dropGoldNugget",
            constant = @Constant(doubleValue = 0.08), require = 0)
    private static double configExt$goldenChrysanthemumDropChance(double original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_GOLDEN_CHRYSANTHEMUM_DROP_CHANCE.get();
    }

    // placeSparseSnowLayer(...): "iconst_4" (max-layers gate, single),
    // "bipush 8" (placement-attempt loop bound, single), and the scatter
    // position "random.nextInt(5)-2" for both x and z - "iconst_5" and
    // "iconst_2" each appear exactly twice with the identical relationship
    // (span = 2*radius+1, subtract = radius), so again no ordinal is needed.
    // NOTE: the separate countSnowLayers(...) helper this method calls has
    // its own fixed +-2 scan box for the "already crowded" check - that box
    // is NOT touched by this field (no config field names it, and changing
    // only the scatter radius while leaving the crowd-check area fixed is
    // the narrower, more predictable change - flagging this scope boundary
    // honestly rather than silently expanding what this field controls).
    @ModifyConstant(method = "placeSparseSnowLayer",
            constant = @Constant(intValue = 4), require = 0)
    private static int configExt$snowMaxLayers(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_SNOW_MAX_LAYERS.get();
    }

    @ModifyConstant(method = "placeSparseSnowLayer",
            constant = @Constant(intValue = 8), require = 0)
    private static int configExt$snowPlaceAttempts(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_SNOW_PLACE_ATTEMPTS.get();
    }

    @ModifyConstant(method = "placeSparseSnowLayer",
            constant = @Constant(intValue = 5), require = 0)
    private static int configExt$snowRadiusSpan(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return 2 * ExtendedConfig.SPIRIT_PLANT_SNOW_RADIUS.get() + 1;
    }

    @ModifyConstant(method = "placeSparseSnowLayer",
            constant = @Constant(intValue = 2), require = 0)
    private static int configExt$snowRadiusOffset(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_PLANT_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_PLANT_SNOW_RADIUS.get();
    }
}
