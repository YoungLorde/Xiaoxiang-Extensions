package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.GoldenCoreDao;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to Golden Core Dao bonuses.
 * The original mod's GoldenCoreDao enum has accessor methods for each bonus
 * (lifespanBonus, spellDamageMult, spellQiCostMult, hpMult, bloodSpellDamageMult,
 * bloodSpellQiCostMult, bodyDefenseBonus, cultivationEfficiencyBonus,
 * qiRecoveryPerSecondBonus, meleeDamageBonus, tribulationStrikes, tribulationDamage,
 * shatterCoreTrialMaxHealth, shatterCoreTrialRegenPerSecond).
 */
@Mixin(GoldenCoreDao.class)
public abstract class GoldenCoreDaoMixin {

    @Inject(method = "lifespanBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$lifespanBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_LIFESPAN_BONUS.get();
        } else if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_LIFESPAN_BONUS.get();
        } else if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_LIFESPAN_BONUS.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_LIFESPAN_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "spellDamageMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$spellDamageMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_SPELL_DAMAGE_MULT.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_SPELL_DAMAGE_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "spellQiCostMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$spellQiCostMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_SPELL_QI_COST_MULT.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_SPELL_QI_COST_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "hpMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$hpMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_HP_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "bloodSpellDamageMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$bloodSpellDamageMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_DAMAGE_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "bloodSpellQiCostMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$bloodSpellQiCostMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_QI_COST_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "tribulationStrikes", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$tribulationStrikes(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_TRIBULATION_STRIKES.get();
        } else if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_TRIBULATION_STRIKES.get();
        } else if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_TRIBULATION_STRIKES.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_TRIBULATION_STRIKES.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "tribulationDamage", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$tribulationDamage(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        if (self != GoldenCoreDao.NONE) {
            int override = ExtendedConfig.GOLDEN_CORE_DAO_TRIBULATION_DAMAGE.get();
            if (override != cir.getReturnValue()) cir.setReturnValue(override);
        }
    }

    @Inject(method = "shatterCoreTrialMaxHealth", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$shatterCoreTrialMaxHealth(CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.HUMAN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_MAX_HEALTH.get();
        } else if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_MAX_HEALTH.get();
        } else if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_MAX_HEALTH.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_MAX_HEALTH.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue((float) override);
    }

    @Inject(method = "shatterCoreTrialRegenPerSecond", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$shatterCoreTrialRegenPerSecond(CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.HUMAN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REGEN.get();
        } else if (self == GoldenCoreDao.BLOOD) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REGEN.get();
        } else if (self == GoldenCoreDao.EARTH) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REGEN.get();
        } else if (self == GoldenCoreDao.HEAVEN) {
            override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REGEN.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue((float) override);
    }

    @Inject(method = "bodyDefenseBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$bodyDefenseBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_BODY_DEFENSE_BONUS.get();
        else if (self == GoldenCoreDao.BLOOD) override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_BODY_DEFENSE_BONUS.get();
        else if (self == GoldenCoreDao.EARTH) override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_BODY_DEFENSE_BONUS.get();
        else if (self == GoldenCoreDao.HEAVEN) override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_BODY_DEFENSE_BONUS.get();
        else return;
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "cultivationEfficiencyBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$cultivationEfficiencyBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_CULTIVATION_EFFICIENCY_BONUS.get();
        else if (self == GoldenCoreDao.BLOOD) override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_CULTIVATION_EFFICIENCY_BONUS.get();
        else if (self == GoldenCoreDao.EARTH) override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS.get();
        else if (self == GoldenCoreDao.HEAVEN) override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS.get();
        else return;
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "qiRecoveryPerSecondBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$qiRecoveryPerSecondBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_QI_RECOVERY_BONUS.get();
        else if (self == GoldenCoreDao.BLOOD) override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_QI_RECOVERY_BONUS.get();
        else if (self == GoldenCoreDao.EARTH) override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_QI_RECOVERY_BONUS.get();
        else if (self == GoldenCoreDao.HEAVEN) override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_QI_RECOVERY_BONUS.get();
        else return;
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "meleeDamageBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$meleeDamageBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        int override;
        if (self == GoldenCoreDao.HUMAN) override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_MELEE_DAMAGE_BONUS.get();
        else if (self == GoldenCoreDao.BLOOD) override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_MELEE_DAMAGE_BONUS.get();
        else if (self == GoldenCoreDao.EARTH) override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_MELEE_DAMAGE_BONUS.get();
        else if (self == GoldenCoreDao.HEAVEN) override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_MELEE_DAMAGE_BONUS.get();
        else return;
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "shatterCoreTrialReflectionRatio", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$shatterCoreTrialReflectionRatio(CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        GoldenCoreDao self = (GoldenCoreDao) (Object) this;
        double override;
        if (self == GoldenCoreDao.HUMAN) override = ExtendedConfig.GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REFLECTION.get();
        else if (self == GoldenCoreDao.BLOOD) override = ExtendedConfig.GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REFLECTION.get();
        else if (self == GoldenCoreDao.EARTH) override = ExtendedConfig.GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REFLECTION.get();
        else if (self == GoldenCoreDao.HEAVEN) override = ExtendedConfig.GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REFLECTION.get();
        else return;
        if (override != cir.getReturnValue()) cir.setReturnValue((float) override);
    }
}
