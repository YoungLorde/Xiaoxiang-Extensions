package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.trial.InnerWorldTrialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Applies config overrides to InnerWorldTrialManager constants.
 * The original mod has public static final int constants for platform dimensions,
 * Y level, and soul wound duration. These are inlined as bytecode constants.
 *
 * CORRECTED 2026-09-01, re-verified with a full disassembly of this class
 * (3900+ lines) rather than the single-method spot-check the previous
 * version of this file relied on:
 *
 *   - PLATFORM_DIAMETER (48) and SOUL_WOUND_TICKS (1200) are each still
 *     confirmed as the SOLE occurrence of that literal anywhere in the
 *     class, so method = "*" is safe for both (equivalent to naming the
 *     one method that actually has it, just less brittle to future
 *     decompiles).
 *   - PLATFORM_Y (int 80, inlined as double 80.0d wherever it's used in a
 *     coordinate context) previously was only redirected inside begin() -
 *     but the real field is independently inlined at 13 separate call
 *     sites across begin/enforceHeartDemonBoundary/enforceBoundary/the 3
 *     startXTrial methods/resetCell/clearQuestionHeartCombatRuntime/
 *     runDeferredQuestionHeartCleanup (confirmed by checking every one of
 *     the 13 method names - all are platform-position/teleport/boundary
 *     logic, none unrelated). Scoping to just begin() meant a custom
 *     PLATFORM_Y would build the platform at the configured height but
 *     every boundary check, reset, and re-teleport elsewhere would still
 *     use the hardcoded 80 - a real, silent inconsistency bug. Now
 *     corrected to method = "*", verified safe since 80.0d does not appear
 *     anywhere in this class for an unrelated purpose.
 *   - TIME_STASIS_DURATION had NO real backing field on this class at all -
 *     confirmed via javap -v that InnerWorldTrialManager declares no such
 *     field, and the literal 600 does not appear as an int constant
 *     anywhere in its bytecode. The old handler here was silently inert
 *     (require = 0 means a missing target just never fires). The real
 *     field (DURATION_TICKS = 600) lives on the unrelated
 *     com.xiaoxiang.cultivation.event.TimeStasisHandler class - moved to
 *     TimeStasisDurationMixin, which targets the right class. See that
 *     mixin's doc for why this setting isn't actually trial-exclusive
 *     despite the config field's name.
 */
@Mixin(value = InnerWorldTrialManager.class, remap = false)
public abstract class InnerWorldTrialManagerMixin {

    // PLATFORM_DIAMETER = 48 (confirmed sole occurrence in the class)
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 48), require = 0)
    private static int configExt$platformDiameter(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_PLATFORM_DIAMETER.get();
    }

    // PLATFORM_Y = 80 - 13 confirmed occurrences across platform/boundary/
    // teleport methods; method = "*" now correctly covers all of them.
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 80.0), require = 0)
    private static double configExt$platformY(double original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_PLATFORM_Y.get();
    }

    // SOUL_WOUND_TICKS = 1200 (confirmed sole occurrence, in failTrial)
    @ModifyConstant(method = "*", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 1200), require = 0)
    private static int configExt$soulWoundTicks(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_SOUL_WOUND_TICKS.get();
    }

    // FAILURE_HEALTH_PENALTY_PERCENT - failTrial() sets health to
    // Math.max(1.0f, getMaxHealth() * 0.5f); the 0.5f is the sole float
    // literal in that method.
    @ModifyConstant(method = "failTrial", constant = @org.spongepowered.asm.mixin.injection.Constant(floatValue = 0.5f), require = 0)
    private static float configExt$failureHealthPenaltyPercent(float original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_FAILURE_HEALTH_PENALTY_PERCENT.get().floatValue();
    }
}
