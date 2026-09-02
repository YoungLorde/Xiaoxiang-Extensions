package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.MoralityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to MoralityHelper methods.
 * The original mod's MoralityHelper has hardcoded constants for neutral thresholds
 * and tribulation damage scaling.
 */
@Mixin(MoralityHelper.class)
public abstract class MoralityHelperMixin {

    /**
     * Override the tribulation damage multiplier to use config coefficient and max.
     */
    @Inject(method = "tribulationDamageMultiplier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$tribulationDamageMultiplier(int morality, CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return;
        double coefficient = ExtendedConfig.MORALITY_TRIBULATION_DAMAGE_COEFFICIENT.get();
        double max = ExtendedConfig.MORALITY_TRIBULATION_DAMAGE_MAX.get();
        float computed = (float) Math.min(max, 1.0 + Math.abs(morality) * coefficient);
        if (computed != cir.getReturnValue()) cir.setReturnValue(computed);
    }

    /**
     * Override isRighteous to use config neutral max threshold.
     */
    @Inject(method = "isRighteous", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$isRighteous(int morality, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return;
        int neutralMax = ExtendedConfig.MORALITY_NEUTRAL_MAX.get();
        boolean result = morality > neutralMax;
        if (result != cir.getReturnValue()) cir.setReturnValue(result);
    }

    /**
     * Override isEvil to use config neutral min threshold.
     */
    @Inject(method = "isEvil", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$isEvil(int morality, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return;
        int neutralMin = ExtendedConfig.MORALITY_NEUTRAL_MIN.get();
        boolean result = morality < neutralMin;
        if (result != cir.getReturnValue()) cir.setReturnValue(result);
    }

    /**
     * Morality gap-fill: wires morality.righteousMin and morality.evilMax.
     *
     * Verified against the real bytecode of MoralityHelper.pathFor(I):
     *     if (morality &gt;  50)  return Path.RIGHTEOUS;
     *     if (morality &lt; -50)  return Path.EVIL;
     *     return Path.NEUTRAL;
     * The thresholds are the inlined NEUTRAL_MAX / NEUTRAL_MIN constants, so the whole
     * branch has to be replaced rather than patched.
     *
     * With the stock defaults (righteousMin 51, evilMax -51) the rewritten branch is
     * behaviourally identical to the original: m &gt;= 51 == m &gt; 50, and m &lt;= -51 == m &lt; -50.
     *
     * pathFor is the shared classifier behind areOpposed(II), valueComponent(I) and
     * fullLineComponent(I), so wiring it here is what makes righteousMin / evilMax
     * actually visible in game. Note the pre-existing isRighteous / isEvil injections
     * above run at RETURN and recompute from neutralMax / neutralMin, so for those two
     * methods specifically the neutral.* keys still win; keep the two pairs consistent
     * (righteousMin == neutralMax + 1, evilMax == neutralMin - 1) to avoid surprises.
     *
     * The two "great" thresholds (greatRighteousMin / greatEvilMax) have no counterpart
     * in MoralityHelper at all - MoralityHelper.Path is a three-value enum. They are
     * consumed only by HeartDemonTrialProfile.resolve(I) and are wired in
     * HeartDemonTrialProfileMixin.
     */
    @Inject(method = "pathFor", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$pathFor(int morality, CallbackInfoReturnable<MoralityHelper.Path> cir) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return;
        if (morality >= ExtendedConfig.MORALITY_RIGHTEOUS_MIN.get()) {
            cir.setReturnValue(MoralityHelper.Path.RIGHTEOUS);
        } else if (morality <= ExtendedConfig.MORALITY_EVIL_MAX.get()) {
            cir.setReturnValue(MoralityHelper.Path.EVIL);
        } else {
            cir.setReturnValue(MoralityHelper.Path.NEUTRAL);
        }
    }

    /**
     * MORALITY_MIN_VALUE / MORALITY_MAX_VALUE (added 2026-09-01).
     *
     * Verified via javap -p -c -s: clamp(int) does
     * Math.max(-1000000, Math.min(1000000, morality)) - one int "ldc -1000000"
     * and one int "ldc 1000000", each the sole occurrence of that literal/type
     * in the method. add(int, int) independently re-clamps its long-arithmetic
     * sum against BOTH a long comparison threshold (ldc2_w -1000000l /
     * 1000000l) AND an int literal returned on that branch (ldc -1000000 /
     * 1000000) - four distinct constant sites, each appearing exactly once.
     * All six are targeted below, scoped per-method and per-type so none of
     * them can bleed into an unrelated site.
     */
    @ModifyConstant(method = "clamp", constant = @Constant(intValue = -1000000), remap = false, require = 0)
    private static int configExt$clampMin(int original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MIN_VALUE.get();
    }

    @ModifyConstant(method = "clamp", constant = @Constant(intValue = 1000000), remap = false, require = 0)
    private static int configExt$clampMax(int original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MAX_VALUE.get();
    }

    @ModifyConstant(method = "add", constant = @Constant(intValue = -1000000), remap = false, require = 0)
    private static int configExt$addMinInt(int original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MIN_VALUE.get();
    }

    @ModifyConstant(method = "add", constant = @Constant(intValue = 1000000), remap = false, require = 0)
    private static int configExt$addMaxInt(int original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MAX_VALUE.get();
    }

    @ModifyConstant(method = "add", constant = @Constant(longValue = -1000000L), remap = false, require = 0)
    private static long configExt$addMinLong(long original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MIN_VALUE.get();
    }

    @ModifyConstant(method = "add", constant = @Constant(longValue = 1000000L), remap = false, require = 0)
    private static long configExt$addMaxLong(long original) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return original;
        return ExtendedConfig.MORALITY_MAX_VALUE.get();
    }
}
