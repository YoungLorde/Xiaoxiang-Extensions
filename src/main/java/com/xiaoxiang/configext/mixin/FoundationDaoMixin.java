package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.FoundationDao;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to Foundation Dao bonuses.
 * The original mod's FoundationDao enum has accessor methods for each bonus
 * (lifespanBonus, spellDamageMult, spellQiCostMult, hpMult, bodyDefenseBonus,
 * cultivationEfficiencyBonus, qiRecoveryPerSecondBonus, meleeDamageBonus).
 * We inject at RETURN to override the values with config-driven ones.
 */
@Mixin(FoundationDao.class)
public abstract class FoundationDaoMixin {

    @Inject(method = "lifespanBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$lifespanBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        int override;
        if (self == FoundationDao.HUMAN) {
            override = ExtendedConfig.FOUNDATION_DAO_HUMAN_LIFESPAN_BONUS.get();
        } else if (self == FoundationDao.BLOOD) {
            override = ExtendedConfig.FOUNDATION_DAO_BLOOD_LIFESPAN_BONUS.get();
        } else if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_LIFESPAN_BONUS.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_LIFESPAN_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "spellDamageMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$spellDamageMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        double override;
        if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_SPELL_DAMAGE_MULT.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_SPELL_DAMAGE_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "spellQiCostMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$spellQiCostMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        double override;
        if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_SPELL_QI_COST_MULT.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_SPELL_QI_COST_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "hpMult", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$hpMult(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        double override;
        if (self == FoundationDao.BLOOD) {
            override = ExtendedConfig.FOUNDATION_DAO_BLOOD_HP_MULT.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "bodyDefenseBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$bodyDefenseBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        int override;
        if (self == FoundationDao.HUMAN) {
            override = ExtendedConfig.FOUNDATION_DAO_HUMAN_BODY_DEFENSE_BONUS.get();
        } else if (self == FoundationDao.BLOOD) {
            override = ExtendedConfig.FOUNDATION_DAO_BLOOD_BODY_DEFENSE_BONUS.get();
        } else if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_BODY_DEFENSE_BONUS.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_BODY_DEFENSE_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "cultivationEfficiencyBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$cultivationEfficiencyBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        int override;
        if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "qiRecoveryPerSecondBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$qiRecoveryPerSecondBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        int override;
        if (self == FoundationDao.EARTH) {
            override = ExtendedConfig.FOUNDATION_DAO_EARTH_QI_RECOVERY_BONUS.get();
        } else if (self == FoundationDao.HEAVEN) {
            override = ExtendedConfig.FOUNDATION_DAO_HEAVEN_QI_RECOVERY_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "meleeDamageBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$meleeDamageBonus(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_DAO_OVERRIDES.get()) return;
        FoundationDao self = (FoundationDao) (Object) this;
        int override;
        if (self == FoundationDao.BLOOD) {
            override = ExtendedConfig.FOUNDATION_DAO_BLOOD_MELEE_DAMAGE_BONUS.get();
        } else {
            override = cir.getReturnValue();
        }
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }
}
