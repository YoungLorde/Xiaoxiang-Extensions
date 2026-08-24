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

    @Inject(method = "maxQi", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$maxQi(SubStage subStage, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_REALM_OVERRIDES.get()) {
            return;
        }
        Realm self = (Realm) (Object) this;

        if (self == Realm.MORTAL) {
            cir.setReturnValue(ExtendedConfig.MORTAL_MAX_QI.get());
            return;
        }
        if (self == Realm.TRUE_IMMORTAL) {
            cir.setReturnValue(ExtendedConfig.TRUE_IMMORTAL_MAX_QI.get());
            return;
        }
        if (self == Realm.LOOSE_IMMORTAL) {
            cir.setReturnValue(ExtendedConfig.LOOSE_IMMORTAL_MAX_QI.get());
            return;
        }

        if (self == Realm.QI_REFINING) {
            int val;
            if (subStage == SubStage.MIDDLE) {
                val = ExtendedConfig.QI_REFINING_MIDDLE_MAX_QI.get();
            } else if (subStage == SubStage.LATE) {
                val = ExtendedConfig.QI_REFINING_LATE_MAX_QI.get();
            } else if (subStage == SubStage.PEAK) {
                val = ExtendedConfig.QI_REFINING_PEAK_MAX_QI.get();
            } else {
                val = ExtendedConfig.QI_REFINING_EARLY_MAX_QI.get();
            }
            cir.setReturnValue(val);
            return;
        }

        // For other realms: prevPeak + delta
        // Original (verified via javap against the real jar): prevPeak = 500 + (ordinal - 2) * 1300
        // NOTE: that 500 is a separate hardcoded constant in the original mod, distinct from
        // Qi Refining's own peak value (400) - do NOT read it from QI_REFINING_PEAK_MAX_QI,
        // that was the bug that let a config-GUI edit to "Qi Refining Peak" also shove every
        // higher realm's qi cap around, and it also meant our two values could drift apart
        // from what the original actually hardcodes. REALM_PROGRESSION_BASE is its own knob.
        int prevPeak = ExtendedConfig.REALM_PROGRESSION_BASE.get()
                + (self.ordinal() - 2) * 1300;
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
        int result = prevPeak + delta;
        double mult = ExtendedConfig.MAX_QI_GLOBAL_MULTIPLIER.get();
        if (mult != 1.0) {
            result = (int) Math.max(1, Math.round(result * mult));
        }
        cir.setReturnValue(result);
    }

    @Inject(method = "baseLifespan", at = @At("HEAD"), cancellable = true, remap = false)
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

    @Inject(method = "qiShieldReductionPercent()I", at = @At("HEAD"), cancellable = true, remap = false)
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

    @Inject(method = "tribulationStrikeDamage", at = @At("HEAD"), cancellable = true, remap = false)
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
