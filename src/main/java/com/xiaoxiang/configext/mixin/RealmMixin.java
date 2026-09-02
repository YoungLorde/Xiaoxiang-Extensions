package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides hardcoded realm power/strength values with config-driven ones.
 * Targets: maxQi, baseLifespan, qiShieldReductionPercent, tribulationStrikeDamage.
 */
@Mixin(Realm.class)
public abstract class RealmMixin {

    /**
     * Standing safety net added 2026-09-01 following the player-reported
     * "cultivation requirements decreased after reincarnation" bug (see the
     * Bug #1 writeup in CONFIG_AUDIT.md - that specific MAX_QI_GLOBAL_MULTIPLIER
     * bug is fixed by configExt$applyGlobalQiMult below). The user asked for a
     * standing guarantee, not just a one-off fix: every subsequent breakthrough
     * in the standard chain must always require strictly more cultivation than
     * every previous one, no matter what any individual config field is set to
     * (or, if realm overrides are disabled outright, no matter what the
     * original mod's own hardcoded values are - verified via javap that the
     * unmodified base mod itself has a real seam here: Realm.maxQi flat-returns
     * True Immortal=20900 then Loose Immortal=18000, an built-in decrease).
     *
     * configExt$maxQi now always runs (when either enableRealmOverrides or
     * enableMonotonicBreakthroughs is on) and routes through:
     *   1. configExt$rawMaxQi - the same per-realm/substage value as before
     *      (config-driven if overrides are on, otherwise the exact vanilla
     *      formula reproduced by hand from verified bytecode, so disabling
     *      overrides is still byte-for-byte vanilla unless monotonicity is
     *      also enabled).
     *   2. configExt$applyMonotonicClamp - walks the real breakthrough order
     *      (Mortal -> Qi Refining E/M/L/P -> Foundation Building E/M/L/P -> ...
     *      -> Tribulation Transcendence E/M/L/P, the same sequence
     *      CultivationProgressionRules.nextAfterSuccess actually advances
     *      through) and ONLY steps in when a step would otherwise fail to
     *      increase over the previous (already-clamped) step - in that case it
     *      repairs the step to previous + a configurable minimum increase
     *      (larger for major/realm-up steps than minor/substage steps, per the
     *      user's request that minor bumps stay smaller than major ones). A
     *      step that already increases on its own, even by less than that
     *      minimum, is left completely untouched - this is a repair for
     *      violations, not a blanket floor, so it can never nudge an
     *      already-correct default or a deliberately modest admin setting
     *      (verified numerically before shipping: with every field at its
     *      default, this produces byte-for-byte the same sequence as with
     *      monotonicity disabled).
     *
     * True Immortal and Loose Immortal are deliberately NOT folded into that
     * same linear chain relative to EACH OTHER: verified via javap that both
     * are excluded from the standard canBreakthrough()/nextAfterSuccess()
     * progression and are instead reached through a separate tribulation-count
     * promotion mechanic (LOOSE_IMMORTAL_BASE_REDUCTION_PERCENT /
     * LOOSE_IMMORTAL_FULL_REDUCTION_TRIBULATIONS) - real evidence this is an
     * intentional branch (e.g. a lesser "loose" ascension vs a full "true"
     * one), not a simple continuation of the ordinal sequence, so forcing
     * Loose Immortal > True Immortal (or vice versa) would be guessing at
     * design intent rather than fixing a verified bug. Each is only clamped
     * independently to stay above Tribulation Transcendence Peak, which is a
     * safe guarantee regardless of which interpretation is correct.
     */
    @Inject(method = "maxQi", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$maxQi(SubStage subStage, CallbackInfoReturnable<Integer> cir) {
        boolean overrides = ExtendedConfig.ENABLE_REALM_OVERRIDES.get();
        boolean monotonic = ExtendedConfig.ENABLE_MONOTONIC_BREAKTHROUGHS.get();
        if (!overrides && !monotonic) {
            // Both safety nets off: leave the original method body to run untouched.
            return;
        }
        Realm self = (Realm) (Object) this;
        int raw = configExt$rawMaxQi(self, subStage);
        int result = monotonic ? configExt$applyMonotonicClamp(self, subStage, raw) : raw;
        cir.setReturnValue(result);
    }

    /**
     * The per-(realm, subStage) value before any monotonicity clamping:
     * config-driven (with the global multiplier applied) when realm overrides
     * are enabled, otherwise the exact vanilla formula so that disabling
     * overrides but leaving monotonicity on doesn't silently pull in
     * config-file values the admin asked to bypass.
     */
    private static int configExt$rawMaxQi(Realm realm, SubStage subStage) {
        if (ExtendedConfig.ENABLE_REALM_OVERRIDES.get()) {
            return configExt$applyGlobalQiMult(configExt$configuredRawMaxQi(realm, subStage));
        }
        return configExt$vanillaRawMaxQi(realm, subStage);
    }

    private static int configExt$configuredRawMaxQi(Realm realm, SubStage subStage) {
        if (realm == Realm.MORTAL) {
            return ExtendedConfig.MORTAL_MAX_QI.get();
        }
        if (realm == Realm.TRUE_IMMORTAL) {
            return ExtendedConfig.TRUE_IMMORTAL_MAX_QI.get();
        }
        if (realm == Realm.LOOSE_IMMORTAL) {
            return ExtendedConfig.LOOSE_IMMORTAL_MAX_QI.get();
        }
        if (realm == Realm.QI_REFINING) {
            if (subStage == SubStage.MIDDLE) {
                return ExtendedConfig.QI_REFINING_MIDDLE_MAX_QI.get();
            } else if (subStage == SubStage.LATE) {
                return ExtendedConfig.QI_REFINING_LATE_MAX_QI.get();
            } else if (subStage == SubStage.PEAK) {
                return ExtendedConfig.QI_REFINING_PEAK_MAX_QI.get();
            }
            return ExtendedConfig.QI_REFINING_EARLY_MAX_QI.get();
        }

        // For other realms: prevPeak + delta
        // Original (verified via javap against the real jar): prevPeak = 500 + (ordinal - 2) * 1300
        // NOTE: that 500 is a separate hardcoded constant in the original mod, distinct from
        // Qi Refining's own peak value (400) - do NOT read it from QI_REFINING_PEAK_MAX_QI,
        // that was the bug that let a config-GUI edit to "Qi Refining Peak" also shove every
        // higher realm's qi cap around, and it also meant our two values could drift apart
        // from what the original actually hardcodes. REALM_PROGRESSION_BASE is its own knob.
        int prevPeak = ExtendedConfig.REALM_PROGRESSION_BASE.get()
                + (realm.ordinal() - 2) * 1300;
        int delta;
        if (subStage == SubStage.MIDDLE) {
            delta = ExtendedConfig.REALM_BASE_DELTA_MIDDLE.get();
        } else if (subStage == SubStage.LATE) {
            delta = ExtendedConfig.REALM_BASE_DELTA_LATE.get();
        } else if (subStage == SubStage.PEAK) {
            delta = ExtendedConfig.REALM_BASE_DELTA_PEAK.get();
        } else {
            delta = ExtendedConfig.REALM_BASE_DELTA_EARLY.get();
        }
        return prevPeak + delta;
    }

    /**
     * Reproduces com.xiaoxiang.cultivation.cultivation.realm.Realm#maxQi exactly
     * as verified via javap against the real jar (xiaoxiang_cultivation-0.1.1302.jar):
     * Mortal=100 flat; Qi Refining base=100 with deltas 100/200/300/400 for
     * Early/Middle/Late/Peak; every realm above Qi Refining uses
     * prevPeak=500+(ordinal-2)*1300 with deltas 1000/1100/1200/1300; True
     * Immortal=20900 flat; Loose Immortal=18000 flat. Used only as the
     * monotonicity clamp's "raw" input when realm overrides are disabled, so
     * the clamp still has a real baseline to compare against instead of
     * silently reading config values the admin turned off.
     */
    private static int configExt$vanillaRawMaxQi(Realm realm, SubStage subStage) {
        if (realm == Realm.MORTAL) {
            return 100;
        }
        if (realm == Realm.TRUE_IMMORTAL) {
            return 20900;
        }
        if (realm == Realm.LOOSE_IMMORTAL) {
            return 18000;
        }
        if (realm == Realm.QI_REFINING) {
            if (subStage == SubStage.MIDDLE) {
                return 300;
            } else if (subStage == SubStage.LATE) {
                return 400;
            } else if (subStage == SubStage.PEAK) {
                return 500;
            }
            return 200;
        }
        int prevPeak = 500 + (realm.ordinal() - 2) * 1300;
        int delta;
        if (subStage == SubStage.MIDDLE) {
            delta = 1100;
        } else if (subStage == SubStage.LATE) {
            delta = 1200;
        } else if (subStage == SubStage.PEAK) {
            delta = 1300;
        } else {
            delta = 1000;
        }
        return prevPeak + delta;
    }

    // The real standard breakthrough order (verified via javap against
    // CultivationProgressionRules.nextAfterSuccess: at Peak, Realm.next() is
    // taken and the substage resets to Early; otherwise SubStage.next() is
    // taken). Mortal is the base case before this chain; True Immortal and
    // Loose Immortal are deliberately excluded - see the doc comment above.
    private static final Realm[] configExt$STANDARD_CHAIN_REALMS = {
            Realm.QI_REFINING, Realm.FOUNDATION_BUILDING, Realm.GOLDEN_CORE, Realm.NASCENT_SOUL,
            Realm.SOUL_FORMATION, Realm.VOID_REFINING, Realm.BODY_INTEGRATION, Realm.MAHAYANA,
            Realm.TRIBULATION_TRANSCENDENCE
    };
    private static final SubStage[] configExt$SUB_STAGES = {
            SubStage.EARLY, SubStage.MIDDLE, SubStage.LATE, SubStage.PEAK
    };

    /**
     * Returns the monotonicity-clamped value for (realm, subStage). For the
     * standard chain this walks from Mortal forward, re-clamping every step up
     * to the requested one (cheap - at most 37 integer comparisons, no
     * allocations - so recomputing per call instead of caching avoids any risk
     * of a stale cache after a config edit). True Immortal / Loose Immortal are
     * each independently floored at Tribulation Transcendence Peak's clamped
     * value plus one major increase.
     */
    private static int configExt$applyMonotonicClamp(Realm realm, SubStage subStage, int raw) {
        if (realm == Realm.MORTAL) {
            return raw;
        }
        if (realm == Realm.TRUE_IMMORTAL || realm == Realm.LOOSE_IMMORTAL) {
            int ceiling = configExt$standardChainClampedValue(Realm.TRIBULATION_TRANSCENDENCE, SubStage.PEAK)
                    + ExtendedConfig.MONOTONIC_MIN_MAJOR_INCREASE.get();
            return Math.max(raw, ceiling);
        }

        int prevClamped = configExt$rawMaxQi(Realm.MORTAL, SubStage.EARLY);
        for (Realm r : configExt$STANDARD_CHAIN_REALMS) {
            for (SubStage s : configExt$SUB_STAGES) {
                boolean isMajor = (s == SubStage.EARLY);
                int minIncrease = isMajor
                        ? ExtendedConfig.MONOTONIC_MIN_MAJOR_INCREASE.get()
                        : ExtendedConfig.MONOTONIC_MIN_MINOR_INCREASE.get();
                int rawStep = configExt$rawMaxQi(r, s);
                // Only repair when the raw step wouldn't already increase - an already-fine
                // gap (even one smaller than minIncrease) is left completely untouched, so
                // this never second-guesses a working default or a deliberately modest admin
                // setting. minIncrease is the repair amount used ONLY when correcting an
                // actual violation, not a floor applied to every step unconditionally -
                // verified numerically (see the design notes referenced in CONFIG_AUDIT.md)
                // that treating it as an unconditional floor would have shoved the default
                // curve's own values around, which is exactly the kind of unwanted change
                // this safety net must never make.
                int clamped = (rawStep > prevClamped) ? rawStep : (prevClamped + minIncrease);
                if (r == realm && s == subStage) {
                    return clamped;
                }
                prevClamped = clamped;
            }
        }
        // Unreachable for any realm actually present in configExt$STANDARD_CHAIN_REALMS.
        return raw;
    }

    private static int configExt$standardChainClampedValue(Realm realm, SubStage subStage) {
        return configExt$applyMonotonicClamp(realm, subStage, configExt$rawMaxQi(realm, subStage));
    }

    /**
     * Fixed 2026-09-01: MAX_QI_GLOBAL_MULTIPLIER's own tooltip says "applied to
     * all max qi values", but until this fix the multiplier was only applied in
     * the generic (Foundation Building and above) branch below - Mortal, every
     * Qi Refining substage, and both Immortal tiers were left unscaled. That
     * mismatch is a real, reported bug: whenever a server sets this multiplier
     * away from 1.0 (a very ordinary balance tweak), the Qi Refining Peak -> the
     * next realm's prevPeak+delta calc still gets scaled, but the same player's
     * Mortal/Qi-Refining thresholds do not, breaking the strictly-increasing
     * cultivation-requirement curve right at that seam (and looking, from a
     * player's perspective, exactly like "the requirement went down when I
     * broke through"). Centralizing the multiplier application here makes it
     * apply uniformly, matching the documented behaviour and restoring
     * monotonicity regardless of what the multiplier is set to.
     */
    private static int configExt$applyGlobalQiMult(int base) {
        double mult = ExtendedConfig.MAX_QI_GLOBAL_MULTIPLIER.get();
        if (mult == 1.0) {
            return base;
        }
        return (int) Math.max(1, Math.round(base * mult));
    }

    @Inject(method = "baseLifespan", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$baseLifespan(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REALM_OVERRIDES.get()) {
            return;
        }
        Realm self = (Realm) (Object) this;
        int val;
        if (self == Realm.MORTAL) {
            val = 0;
        } else if (self == Realm.QI_REFINING) {
            val = ExtendedConfig.QI_REFINING_LIFESPAN.get();
        } else if (self == Realm.FOUNDATION_BUILDING) {
            val = ExtendedConfig.FOUNDATION_BUILDING_LIFESPAN.get();
        } else if (self == Realm.GOLDEN_CORE) {
            val = ExtendedConfig.GOLDEN_CORE_LIFESPAN.get();
        } else if (self == Realm.NASCENT_SOUL) {
            val = ExtendedConfig.NASCENT_SOUL_LIFESPAN.get();
        } else if (self == Realm.SOUL_FORMATION) {
            val = ExtendedConfig.SOUL_FORMATION_LIFESPAN.get();
        } else if (self == Realm.VOID_REFINING) {
            val = ExtendedConfig.VOID_REFINING_LIFESPAN.get();
        } else if (self == Realm.BODY_INTEGRATION) {
            val = ExtendedConfig.BODY_INTEGRATION_LIFESPAN.get();
        } else if (self == Realm.MAHAYANA) {
            val = ExtendedConfig.MAHAYANA_LIFESPAN.get();
        } else if (self == Realm.TRIBULATION_TRANSCENDENCE) {
            val = ExtendedConfig.TRIBULATION_TRANSCENDENCE_LIFESPAN.get();
        } else if (self == Realm.LOOSE_IMMORTAL) {
            val = ExtendedConfig.LOOSE_IMMORTAL_LIFESPAN.get();
        } else if (self == Realm.TRUE_IMMORTAL) {
            val = ExtendedConfig.TRUE_IMMORTAL_LIFESPAN.get();
        } else {
            val = 0;
        }
        double lifeMult = ExtendedConfig.LIFESPAN_GLOBAL_MULTIPLIER.get();
        if (lifeMult != 1.0) {
            val = (int) Math.max(0, Math.round(val * lifeMult));
        }
        cir.setReturnValue(val);
    }

    @Inject(method = "qiShieldReductionPercent()I", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$qiShieldReductionPercent(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REALM_OVERRIDES.get()) {
            return;
        }
        Realm self = (Realm) (Object) this;
        int val;
        if (self == Realm.MORTAL) {
            val = 0;
        } else if (self == Realm.QI_REFINING) {
            val = ExtendedConfig.QI_REFINING_SHIELD_PERCENT.get();
        } else if (self == Realm.FOUNDATION_BUILDING) {
            val = ExtendedConfig.FOUNDATION_BUILDING_SHIELD_PERCENT.get();
        } else if (self == Realm.GOLDEN_CORE) {
            val = ExtendedConfig.GOLDEN_CORE_SHIELD_PERCENT.get();
        } else if (self == Realm.NASCENT_SOUL) {
            val = ExtendedConfig.NASCENT_SOUL_SHIELD_PERCENT.get();
        } else if (self == Realm.SOUL_FORMATION) {
            val = ExtendedConfig.SOUL_FORMATION_SHIELD_PERCENT.get();
        } else if (self == Realm.VOID_REFINING) {
            val = ExtendedConfig.VOID_REFINING_SHIELD_PERCENT.get();
        } else if (self == Realm.BODY_INTEGRATION) {
            val = ExtendedConfig.BODY_INTEGRATION_SHIELD_PERCENT.get();
        } else if (self == Realm.MAHAYANA) {
            val = ExtendedConfig.MAHAYANA_SHIELD_PERCENT.get();
        } else if (self == Realm.TRIBULATION_TRANSCENDENCE) {
            val = ExtendedConfig.TRIBULATION_TRANSCENDENCE_SHIELD_PERCENT.get();
        } else if (self == Realm.TRUE_IMMORTAL) {
            val = ExtendedConfig.TRUE_IMMORTAL_SHIELD_PERCENT.get();
        } else if (self == Realm.LOOSE_IMMORTAL) {
            val = ExtendedConfig.LOOSE_IMMORTAL_SHIELD_PERCENT.get();
        } else {
            val = 0;
        }
        cir.setReturnValue(val);
    }

    @Inject(method = "tribulationStrikeDamage", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$tribulationStrikeDamage(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REALM_OVERRIDES.get()) {
            return;
        }
        Realm self = (Realm) (Object) this;
        int val;
        if (self == Realm.MORTAL) {
            val = 0;
        } else if (self == Realm.QI_REFINING) {
            val = ExtendedConfig.QI_REFINING_TRIB_DAMAGE.get();
        } else if (self == Realm.FOUNDATION_BUILDING) {
            val = ExtendedConfig.FOUNDATION_BUILDING_TRIB_DAMAGE.get();
        } else if (self == Realm.GOLDEN_CORE) {
            val = ExtendedConfig.GOLDEN_CORE_TRIB_DAMAGE.get();
        } else if (self == Realm.NASCENT_SOUL) {
            val = ExtendedConfig.NASCENT_SOUL_TRIB_DAMAGE.get();
        } else if (self == Realm.SOUL_FORMATION) {
            val = ExtendedConfig.SOUL_FORMATION_TRIB_DAMAGE.get();
        } else if (self == Realm.VOID_REFINING) {
            val = ExtendedConfig.VOID_REFINING_TRIB_DAMAGE.get();
        } else if (self == Realm.BODY_INTEGRATION) {
            val = ExtendedConfig.BODY_INTEGRATION_TRIB_DAMAGE.get();
        } else if (self == Realm.MAHAYANA) {
            val = 0;
        } else if (self == Realm.TRIBULATION_TRANSCENDENCE) {
            val = ExtendedConfig.TRIBULATION_TRANSCENDENCE_TRIB_DAMAGE.get();
        } else if (self == Realm.LOOSE_IMMORTAL) {
            val = 0;
        } else if (self == Realm.TRUE_IMMORTAL) {
            val = 0;
        } else {
            val = 0;
        }
        double tribMult = ExtendedConfig.TRIB_DAMAGE_GLOBAL_MULTIPLIER.get();
        if (tribMult != 1.0) {
            val = (int) Math.max(0, Math.round(val * tribMult));
        }
        cir.setReturnValue(val);
    }
}
