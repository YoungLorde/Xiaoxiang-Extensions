package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.PhysiqueBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the remaining per-physique entries of the "physiques" config section
 * that aren't already covered by PhysiqueBonusHelperMixin (Alchemy Heart,
 * Chaos Body spell damage/cultivation-req) or PhysiqueMixin (rarity weights).
 *
 * Same technique as SpiritRootMixin: PhysiqueBonus is a record baked once per
 * Physique enum constant at class-init (javap -p confirms
 * "public com.xiaoxiang.cultivation.cultivation.PhysiqueBonus bonus()" on
 * Physique.class, and PhysiqueBonus's 18-arg canonical constructor via
 * javap -p on PhysiqueBonus.class: (hpMult:D, maxHpBonus:I, meleeDmgMult:D,
 * swordSpellMult:D, metalSpellMult:D, woodSpellMult:D, waterSpellMult:D,
 * fireSpellMult:D, earthSpellMult:D, pureSpellMult:D, lightningSpellMult:D,
 * qiAbsorbRange:I, qiAbsorbMult:D, qiCostMult:D, damageTakenMult:D,
 * maxQiMult:D, resistanceRegen:Z, cannotCultivate:Z)). Injecting at
 * bonus()'s RETURN and rebuilding the record per-identity is exact and
 * avoids the constant-pool ordinal juggling the record's <clinit> would
 * otherwise require (0.5d is reused 7x, 2.0d 4x, 1.2d 3x across different
 * physiques in that initializer - confirmed via javap -p -c -s).
 *
 * UPDATED 2026-09-02 for base mod 0.1.1479: base mod added a new
 * "lightningSpellMult" double field to PhysiqueBonus (a new Lightning spell
 * element, inserted right after pureSpellMult and before qiAbsorbRange in
 * the record's canonical constructor - confirmed via javap -p -c -s on the
 * 0.1.1479 jar, which shows spellMultiplier(Spell) dispatching QiElement.
 * LIGHTNING to this field the same way METAL/WOOD/WATER/FIRE/EARTH/PURE
 * already dispatch to their own fields). This mixin doesn't configure
 * lightning-element bonuses (no physique in this file's if/else chain
 * touches it), so it's passed straight through via
 * original.lightningSpellMult() at the correct constructor position - not a
 * behavior change, just keeping the record rebuild in sync with the base
 * mod's now-18-arg constructor so the mod compiles again.
 */
@Mixin(Physique.class)
public abstract class PhysiqueBonusMixin {

    @Inject(method = "bonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
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
        double fireSpellMult = original.fireSpellMult();
        double waterSpellMult = original.waterSpellMult();
        int qiAbsorbRange = original.qiAbsorbRange();
        double qiAbsorbMult = original.qiAbsorbMult();
        double qiCostMult = original.qiCostMult();
        double damageTakenMult = original.damageTakenMult();

        if (self == Physique.IMMORTAL_BODY) {
            qiAbsorbMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_MULT.get();
            qiAbsorbRange = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_RANGE.get().intValue();
            qiCostMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_QI_COST_MULT.get();
            damageTakenMult = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_DAMAGE_TAKEN_MULT.get();
            maxHpBonus = ExtendedConfig.PHYSIQUE_IMMORTAL_BODY_MAX_HP_BONUS.get().intValue();
        } else if (self == Physique.INNATE_SWORD_BODY) {
            swordSpellMult = ExtendedConfig.PHYSIQUE_INNATE_SWORD_BODY_SWORD_SPELL_MULT.get();
        } else if (self == Physique.HEAVENLY_FIRE_BODY) {
            fireSpellMult = ExtendedConfig.PHYSIQUE_HEAVENLY_FIRE_BODY_FIRE_SPELL_MULT.get();
        } else if (self == Physique.MYSTIC_ICE_BODY) {
            waterSpellMult = ExtendedConfig.PHYSIQUE_MYSTIC_ICE_BODY_WATER_SPELL_MULT.get();
        } else if (self == Physique.SWORD_BONE) {
            swordSpellMult = ExtendedConfig.PHYSIQUE_SWORD_BONE_SWORD_SPELL_MULT.get();
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
                original.lightningSpellMult(),
                qiAbsorbRange,
                qiAbsorbMult,
                qiCostMult,
                damageTakenMult,
                original.maxQiMult(),
                original.resistanceRegen(),
                original.cannotCultivate()));
    }
}
