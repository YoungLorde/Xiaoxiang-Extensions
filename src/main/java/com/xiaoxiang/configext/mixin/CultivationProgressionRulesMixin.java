package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationProgressionRules;
import com.xiaoxiang.cultivation.cultivation.FoundationDao;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies the player cultivation speed multiplier to cultivation progression.
 *
 * maxCultivation() determines how much Qi a player needs to absorb before
 * they can attempt a breakthrough. Multiplying this by the cultivation speed
 * multiplier makes breakthroughs faster (lower max = faster) or slower
 * (higher max = slower).
 *
 * With playerCultivationSpeedMult = 0.2, maxCultivation is multiplied by
 * 1/0.2 = 5.0, meaning the player needs 5x more Qi to advance.
 *
 * With playerCultivationSpeedMult = 3.0, maxCultivation is multiplied by
 * 1/3.0 = 0.33, meaning the player needs only 1/3 the Qi to advance.
 *
 * baseAbsorbMultiplier() determines how efficiently Qi is absorbed.
 * We multiply this directly by the cultivation speed multiplier so
 * that higher speed values make the player absorb Qi faster.
 *
 * NOTE: The config defaults to 1.0, so this mixin is a no-op unless
 * the player manually changes the cultivation speed multiplier.
 */
@Mixin(CultivationProgressionRules.class)
public abstract class CultivationProgressionRulesMixin {

    /**
     * Scale the max cultivation requirement inversely to the cultivation speed.
     * Higher cultivation speed = lower max cultivation = faster progression.
     *
     * NOTE: We do NOT invert the multiplier here anymore. The baseAbsorbMultiplier
     * already scales absorption speed. If we also scale maxCultivation inversely,
     * we get a double-multiplier effect (e.g., 5x more Qi needed AND
     * 5x slower absorption = 25x slower total). Instead, we only scale
     * maxCultivation by the inverse square root to get a gentler curve.
     */
    @Inject(method = "maxCultivation", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$maxCultivation(Realm realm, SubStage subStage, Physique physique,
                                                  CallbackInfoReturnable<Long> cir) {
        double speedMult = ExtendedConfig.PLAYER_CULTIVATION_SPEED_MULT.get();
        if (speedMult == 1.0) return;

        // Use inverse square root for a gentler curve — avoids 25x slowdown
        // With 0.2: sqrt(1/0.2) = sqrt(5) ≈ 2.24x more Qi needed
        // With 3.0: sqrt(1/3.0) ≈ 0.577x less Qi needed
        double invSqrt = 1.0 / Math.sqrt(speedMult);
        long base = cir.getReturnValue();
        long scaled = Math.max(1L, Math.round(base * invSqrt));
        cir.setReturnValue(scaled);
    }

    /**
     * Scale the Qi absorption rate by the cultivation speed multiplier.
     * Higher cultivation speed = faster absorption.
     *
     * We use the square root of the speed multiplier for a gentler curve.
     * With 0.2: sqrt(0.2) ≈ 0.447x absorption speed
     * With 3.0: sqrt(3.0) ≈ 1.732x absorption speed
     *
     * Combined with maxCultivation scaling:
     * 0.2 total: 2.24 / 0.447 ≈ 5x slower (was 25x)
     * 3.0 total: 0.577 / 1.732 ≈ 0.33x = 3x faster
     */
    @Inject(method = "baseAbsorbMultiplier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$baseAbsorbMultiplier(Realm realm, boolean hasTechnique, boolean equippedTechnique,
                                                        CallbackInfoReturnable<Double> cir) {
        double speedMult = ExtendedConfig.PLAYER_CULTIVATION_SPEED_MULT.get();
        if (speedMult == 1.0) return;

        double base = cir.getReturnValue();
        cir.setReturnValue(base * Math.sqrt(speedMult));
    }

    /**
     * Foundation Dao tribulation wave counts (added 2026-09-01).
     *
     * Verified via javap -p -c -s: foundationTribulationWaves(FoundationDao)
     * is a static int method doing a lookupswitch on dao.ordinal() - only
     * EARTH (case 3, iconst_1) and HEAVEN (case 4, iconst_3) return a nonzero
     * value; every other dao (including NONE/HUMAN/BLOOD) falls to the
     * default case (iconst_0). Called from tribulationProfile(...) only when
     * Realm == QI_REFINING && SubStage == PEAK, i.e. at the actual Foundation
     * Building breakthrough attempt - a real gameplay call site, not cosmetic.
     */
    @Inject(method = "foundationTribulationWaves", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$foundationTribulationWaves(FoundationDao dao, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        int override;
        if (dao == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_TRIBULATION_WAVES.get();
        } else if (dao == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_TRIBULATION_WAVES.get();
        } else {
            return;
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    /**
     * Bone-age eligibility caps for the Heaven dao paths (added 2026-09-01).
     * Verified via javap -p -c -s: isEligibleFoundationDao(...) has a single
     * "bipush 21" literal (the HEAVEN-branch boneAge>=21 gate); similarly
     * isEligibleGoldenCoreDao(...) has a single "bipush 60". Both are plain
     * static eligibility checks, safe to @ModifyConstant directly.
     */
    @ModifyConstant(method = "isEligibleFoundationDao", constant = @Constant(intValue = 21), remap = false, require = 0)
    private static int configExt$foundationHeavenBoneAgeLimit(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.FOUNDATION_DAO_HEAVEN_BONE_AGE_LIMIT.get();
    }

    @ModifyConstant(method = "isEligibleGoldenCoreDao", constant = @Constant(intValue = 60), remap = false, require = 0)
    private static int configExt$goldenCoreHeavenBoneAgeLimit(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_BONE_AGE_LIMIT.get();
    }

    /**
     * Higher-route day estimates (added 2026-09-01).
     *
     * Verified via javap -p -c -s: estimateHigherFoundationRouteDays(FoundationDao)
     * is actually a 3-way branch, not the 2-way split originally assumed -
     * HEAVEN returns iconst_0 (0, nothing left to estimate), EARTH returns
     * "bipush 30", and everything else (HUMAN/BLOOD/NONE) returns "bipush 12".
     * Each bipush literal occurs exactly once in the method.
     *
     * The config fields predate this discovery and are named after the dao
     * they were GUESSED to represent, not the one they actually match. Rather
     * than rename them (breaking existing configs) we wire each field to
     * whichever real branch its own default value matches:
     *   PROGRESSION_FOUNDATION_HEAVEN_ESTIMATE_DAYS (default 30) -> EARTH branch
     *   PROGRESSION_FOUNDATION_EARTH_ESTIMATE_DAYS  (default 12) -> else branch
     * The true HEAVEN value (0) is not tunable - there is nothing to
     * estimate once a player is already on the Heaven route.
     * estimateHigherGoldenCoreRouteDays(GoldenCoreDao) follows the identical
     * pattern: HEAVEN->0, EARTH->"bipush 60", else->"bipush 24".
     */
    @ModifyConstant(method = "estimateHigherFoundationRouteDays", constant = @Constant(intValue = 30), remap = false, require = 0)
    private static int configExt$foundationEarthEstimateDays(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_FOUNDATION_HEAVEN_ESTIMATE_DAYS.get();
    }

    @ModifyConstant(method = "estimateHigherFoundationRouteDays", constant = @Constant(intValue = 12), remap = false, require = 0)
    private static int configExt$foundationElseEstimateDays(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_FOUNDATION_EARTH_ESTIMATE_DAYS.get();
    }

    @ModifyConstant(method = "estimateHigherGoldenCoreRouteDays", constant = @Constant(intValue = 60), remap = false, require = 0)
    private static int configExt$goldenCoreEarthEstimateDays(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_GOLDEN_CORE_HEAVEN_ESTIMATE_DAYS.get();
    }

    @ModifyConstant(method = "estimateHigherGoldenCoreRouteDays", constant = @Constant(intValue = 24), remap = false, require = 0)
    private static int configExt$goldenCoreElseEstimateDays(int original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_GOLDEN_CORE_EARTH_ESTIMATE_DAYS.get();
    }

    /**
     * PROGRESSION_NPC_HIGHER_DAO_LIFESPAN_RESERVE_THRESHOLD (added 2026-09-01).
     *
     * Verified via javap -p -c -s: hasEnoughLifespanReserve(double reserveDays,
     * int lifespanDays) returns false immediately if lifespanDays <= 0,
     * otherwise computes Math.max(0.0, reserveDays) / (double) lifespanDays
     * and compares it against a single "dconst_1" (1.0d) literal - the only
     * occurrence of that constant in the method. This field was never wired
     * before this session, so per this project's policy its default has been
     * corrected from 0.5 to the real value, 1.0 (see ExtendedConfig.java).
     */
    @ModifyConstant(method = "hasEnoughLifespanReserve", constant = @Constant(doubleValue = 1.0), remap = false, require = 0)
    private static double configExt$npcHigherDaoLifespanReserveThreshold(double original) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_NPC_HIGHER_DAO_LIFESPAN_RESERVE_THRESHOLD.get();
    }

    /**
     * PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT (added 2026-09-01).
     *
     * An earlier research pass on this field reported it as a dead exported
     * constant with no live reader anywhere in the jar. Re-verifying directly
     * against baseAbsorbMultiplier(Realm, boolean hasTechnique, boolean
     * equippedTechnique)'s own bytecode shows that report was wrong: when a
     * player/NPC has an equipped technique, the method returns
     * realm.baseAbsorbMult() as a double if that value is > 0, otherwise -
     * for MORTAL realm specifically - falls back to a single "dconst_1"
     * (1.0d) literal, the sole occurrence of that constant in the method.
     * That fallback IS this field. The config default (1.0) already matches
     * the real value, so no default correction was needed - only the wiring.
     * This composes safely with configExt$baseAbsorbMultiplier() above: that
     * method scales the already-computed @At("RETURN") value by
     * sqrt(speedMult), while this one intercepts an internal literal used to
     * produce that value in the first place - different mechanisms, no
     * conflict.
     */
    @ModifyConstant(method = "baseAbsorbMultiplier", constant = @Constant(doubleValue = 1.0), remap = false, require = 0)
    private static double configExt$mortalEquippedTechniqueBaseMult(double original) {
        if (!ExtendedConfig.ENABLE_PROGRESSION_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT.get();
    }
}
