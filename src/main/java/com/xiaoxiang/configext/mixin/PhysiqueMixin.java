package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.Physique.Rarity;
import com.xiaoxiang.cultivation.cultivation.PhysiqueBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides physique rarity weights and per-physique bonus values with
 * config-driven ones.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/Physique.class:
 *
 *   public PhysiqueBonus bonus()   -> ()Lcom/xiaoxiang/cultivation/cultivation/PhysiqueBonus;
 *   public static int weightOf(Physique$Rarity)
 *
 * The per-physique numbers are baked into the enum constants in Physique.&lt;clinit&gt;
 * through PhysiqueBonus.Builder, so the *field* cannot be mixed. Instead the
 * bonus() accessor is intercepted at RETURN and the returned record is rebuilt
 * with the config values. That keeps the override live (config edits apply
 * immediately) and avoids touching &lt;clinit&gt;, which runs long before the
 * ForgeConfigSpec is loaded.
 *
 * Real values read out of Physique.&lt;clinit&gt; (used to confirm the config
 * defaults are behaviour-identical):
 *
 *   BROKEN_VEIN_BODY   : hpMult 2.0, meleeDmgMult 2.0, maxQiMult 0.1, resistanceRegen
 *   INNATE_SWORD_BODY  : swordSpellMult 2.0
 *   IMMORTAL_BODY      : maxHpBonus 10, qiAbsorbMult 10.0, qiAbsorbRange 10,
 *                        qiCostMult 0.5, damageTakenMult 0.5
 *   HEAVENLY_FIRE_BODY : fireSpellMult 1.2
 *   MYSTIC_ICE_BODY    : waterSpellMult 1.2
 *   SWORD_BONE         : swordSpellMult 1.2
 *   IMMORTAL_BLOOD_BODY: hpMult 2.0
 *
 * PhysiqueBonus is a record; its canonical constructor is
 * (DIDDDDDDDDIDDDDZZ) = (hpMult, maxHpBonus, meleeDmgMult, swordSpellMult,
 * metalSpellMult, woodSpellMult, waterSpellMult, fireSpellMult, earthSpellMult,
 * pureSpellMult, qiAbsorbRange, qiAbsorbMult, qiCostMult, damageTakenMult,
 * maxQiMult, resistanceRegen, cannotCultivate).
 */
@Mixin(Physique.class)
public abstract class PhysiqueMixin {

    @Inject(method = "weightOf", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$weightOf(Rarity rarity, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) {
            return;
        }
        int val = switch (rarity) {
            default -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_LOW.get();
            case MID -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_MID.get();
            case HIGH -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_HIGH.get();
            case SUPREME -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_IMMORTAL.get();
            case SPECIAL -> ExtendedConfig.PHYSIQUE_RARITY_WEIGHT_SPECIAL.get();
        };
        cir.setReturnValue(val);
    }

    @Inject(method = "bonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$bonus(CallbackInfoReturnable<PhysiqueBonus> cir) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) {
            return;
        }
        PhysiqueBonus original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        Physique self = (Physique) (Object) this;

        double hpMult = original.hpMult();
        int maxHpBonus = original.maxHpBonus();
        double meleeDmgMult = original.meleeDmgMult();
        double swordSpellMult = original.swordSpellMult();
        double waterSpellMult = original.waterSpellMult();
        double fireSpellMult = original.fireSpellMult();
        int qiAbsorbRange = original.qiAbsorbRange();
        double qiAbsorbMult = original.qiAbsorbMult();
        double qiCostMult = original.qiCostMult();
        double damageTakenMult = original.damageTakenMult();

        if (self == Physique.IMMORTAL_BODY) {
            maxHpBonus = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_MAX_HP_BONUS.get();
            qiAbsorbMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_MULT.get();
            qiAbsorbRange = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_RANGE.get();
            qiCostMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_COST_MULT.get();
            damageTakenMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_DAMAGE_TAKEN_MULT.get();
        } else if (self == Physique.INNATE_SWORD_BODY) {
            swordSpellMult = ExtendedConfig.PHYSIQUE_INNATE_SWORD_BODY_SWORD_SPELL_MULT.get();
        } else if (self == Physique.SWORD_BONE) {
            swordSpellMult = ExtendedConfig.PHYSIQUE_SWORD_BONE_SWORD_SPELL_MULT.get();
        } else if (self == Physique.HEAVENLY_FIRE_BODY) {
            fireSpellMult = ExtendedConfig.PHYSIQUE_HEAVENLY_FIRE_BODY_FIRE_SPELL_MULT.get();
        } else if (self == Physique.MYSTIC_ICE_BODY) {
            waterSpellMult = ExtendedConfig.PHYSIQUE_MYSTIC_ICE_BODY_WATER_SPELL_MULT.get();
        } else if (self == Physique.BROKEN_VEIN_BODY) {
            hpMult = ExtendedConfig.PHYSIQUE_BROKEN_VEIN_HP_MULT.get();
            meleeDmgMult = ExtendedConfig.PHYSIQUE_BROKEN_VEIN_MELEE_DMG_MULT.get();
        } else if (self == Physique.IMMORTAL_BLOOD_BODY) {
            hpMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BLOOD_HP_MULT.get();
        } else {
            return;
        }

        cir.setReturnValue(new PhysiqueBonus(
                hpMult,
                maxHpBonus,
                meleeDmgMult,
                swordSpellMult,
                original.metalSpellMult(),
                original.woodSpellMult(),
                waterSpellMult,
                fireSpellMult,
                original.earthSpellMult(),
                original.pureSpellMult(),
                qiAbsorbRange,
                qiAbsorbMult,
                qiCostMult,
                damageTakenMult,
                original.maxQiMult(),
                original.resistanceRegen(),
                original.cannotCultivate()));
    }
}
