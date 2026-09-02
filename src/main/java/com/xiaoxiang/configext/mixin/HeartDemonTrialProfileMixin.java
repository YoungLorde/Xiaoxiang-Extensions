package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.MoralityHelper;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import com.xiaoxiang.cultivation.cultivation.trial.HeartDemonTrialProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to HeartDemonTrialProfile vitality multipliers.
 * The original mod's resolve(int) method creates profiles with hardcoded
 * vitality multipliers based on morality band:
 *   GREAT_RIGHTEOUS: 0.5, RIGHTEOUS: 0.75, NEUTRAL: 1.0, EVIL: 1.25, GREAT_EVIL: 1.5
 * We inject at RETURN on the accessor method to override with config values.
 */
@Mixin(value = HeartDemonTrialProfile.class, remap = false)
public abstract class HeartDemonTrialProfileMixin {

    @Inject(method = "vitalityMultiplier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$vitalityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return;
        HeartDemonTrialProfile self = (HeartDemonTrialProfile) (Object) this;
        float override = switch (self.band()) {
            case GREAT_RIGHTEOUS -> (float) ExtendedConfig.TRIAL_HEART_DEMON_GREAT_RIGHTEOUS_VITALITY_MULT.get().doubleValue();
            case RIGHTEOUS -> (float) ExtendedConfig.TRIAL_HEART_DEMON_RIGHTEOUS_VITALITY_MULT.get().doubleValue();
            case NEUTRAL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_NEUTRAL_VITALITY_MULT.get().doubleValue();
            case EVIL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_EVIL_VITALITY_MULT.get().doubleValue();
            case GREAT_EVIL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_GREAT_EVIL_VITALITY_MULT.get().doubleValue();
        };
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    /**
     * Morality gap-fill: wires morality.righteousMin / evilMax / greatRighteousMin /
     * greatEvilMax.
     *
     * MoralityHelper itself only knows a THREE-way split (Path.RIGHTEOUS / NEUTRAL /
     * EVIL, see pathFor(I) - handled in MoralityHelperMixin). The only place in the
     * original mod that knows a FIVE-way split - i.e. the only consumer of the two
     * "great" thresholds - is HeartDemonTrialProfile.resolve(I), verified by javap:
     *
     *   int m = MoralityHelper.clamp(morality);
     *   if (m &gt; 100)   -&gt; GREAT_RIGHTEOUS, 0.5f,  FOUNDATION_BUILDING, PEAK
     *   if (m &gt;= 51)   -&gt; RIGHTEOUS,       0.75f, GOLDEN_CORE,         PEAK
     *   if (m &gt;= -50)  -&gt; NEUTRAL,         1.0f,  NASCENT_SOUL,        PEAK
     *   if (m &gt;= -100) -&gt; EVIL,            1.25f, SOUL_FORMATION,      PEAK
     *   else           -&gt; GREAT_EVIL,      1.5f,  VOID_REFINING,       PEAK
     *
     * The comparisons below are written to reproduce those branches EXACTLY at the
     * stock config defaults (righteousMin 51, evilMax -51, greatRighteousMin 100,
     * greatEvilMax -100):
     *   m &gt;  greatRighteousMin  == m &gt;  100   (note: strict, matching the original;
     *                                          m == 100 stays RIGHTEOUS as in vanilla)
     *   m &gt;= righteousMin       == m &gt;= 51
     *   m &gt;  evilMax            == m &gt;= -50
     *   m &gt;= greatEvilMax       == m &gt;= -100
     *
     * Gated on ENABLE_MORALITY_OVERRIDES because these are morality thresholds.
     * The vitality multiplier baked into the record is taken from the trial config
     * when ENABLE_TRIAL_OVERRIDES is on - this matters because scaledHealth(F) and
     * scaledQi(J) read the vitalityMultiplier FIELD directly (getfield) rather than
     * calling the accessor, so the vitalityMultiplier() injection above cannot reach
     * them on its own.
     */
    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$resolve(int morality, CallbackInfoReturnable<HeartDemonTrialProfile> cir) {
        if (!ExtendedConfig.ENABLE_MORALITY_OVERRIDES.get()) return;
        int m = MoralityHelper.clamp(morality);
        if (m > ExtendedConfig.MORALITY_GREAT_RIGHTEOUS_MIN.get()) {
            cir.setReturnValue(new HeartDemonTrialProfile(HeartDemonTrialProfile.Band.GREAT_RIGHTEOUS,
                    configExt$vitality(HeartDemonTrialProfile.Band.GREAT_RIGHTEOUS),
                    Realm.FOUNDATION_BUILDING, SubStage.PEAK));
        } else if (m >= ExtendedConfig.MORALITY_RIGHTEOUS_MIN.get()) {
            cir.setReturnValue(new HeartDemonTrialProfile(HeartDemonTrialProfile.Band.RIGHTEOUS,
                    configExt$vitality(HeartDemonTrialProfile.Band.RIGHTEOUS),
                    Realm.GOLDEN_CORE, SubStage.PEAK));
        } else if (m > ExtendedConfig.MORALITY_EVIL_MAX.get()) {
            cir.setReturnValue(new HeartDemonTrialProfile(HeartDemonTrialProfile.Band.NEUTRAL,
                    configExt$vitality(HeartDemonTrialProfile.Band.NEUTRAL),
                    Realm.NASCENT_SOUL, SubStage.PEAK));
        } else if (m >= ExtendedConfig.MORALITY_GREAT_EVIL_MAX.get()) {
            cir.setReturnValue(new HeartDemonTrialProfile(HeartDemonTrialProfile.Band.EVIL,
                    configExt$vitality(HeartDemonTrialProfile.Band.EVIL),
                    Realm.SOUL_FORMATION, SubStage.PEAK));
        } else {
            cir.setReturnValue(new HeartDemonTrialProfile(HeartDemonTrialProfile.Band.GREAT_EVIL,
                    configExt$vitality(HeartDemonTrialProfile.Band.GREAT_EVIL),
                    Realm.VOID_REFINING, SubStage.PEAK));
        }
    }

    /** Vanilla vitality per band, replaced by the trial config when that toggle is on. */
    private static float configExt$vitality(HeartDemonTrialProfile.Band band) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) {
            return switch (band) {
                case GREAT_RIGHTEOUS -> 0.5F;
                case RIGHTEOUS -> 0.75F;
                case NEUTRAL -> 1.0F;
                case EVIL -> 1.25F;
                case GREAT_EVIL -> 1.5F;
            };
        }
        return switch (band) {
            case GREAT_RIGHTEOUS -> (float) ExtendedConfig.TRIAL_HEART_DEMON_GREAT_RIGHTEOUS_VITALITY_MULT.get().doubleValue();
            case RIGHTEOUS -> (float) ExtendedConfig.TRIAL_HEART_DEMON_RIGHTEOUS_VITALITY_MULT.get().doubleValue();
            case NEUTRAL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_NEUTRAL_VITALITY_MULT.get().doubleValue();
            case EVIL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_EVIL_VITALITY_MULT.get().doubleValue();
            case GREAT_EVIL -> (float) ExtendedConfig.TRIAL_HEART_DEMON_GREAT_EVIL_VITALITY_MULT.get().doubleValue();
        };
    }
}
