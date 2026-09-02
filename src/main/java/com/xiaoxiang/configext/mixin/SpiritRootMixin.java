package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.SpiritRoot;
import com.xiaoxiang.cultivation.cultivation.SpiritRootBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the per-root entries of the "spiritRoots" config section.
 *
 * (The qiAbsorption sub-section of that same config category is already handled
 * by SpiritRootBonusHelperMixin, which targets a different class -
 * SpiritRootBonusHelper.qiAbsorptionMultiplier - so there is no overlap.)
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/SpiritRoot.class:
 *
 *   public SpiritRootBonus bonus() -> ()Lcom/xiaoxiang/cultivation/cultivation/SpiritRootBonus;
 *
 * The numbers live in the enum constants built in SpiritRoot.&lt;clinit&gt; via the
 * private helpers heavenlyRoot(..) / dualRoot(..) and inline builder chains, so
 * the fields themselves cannot be mixed. Intercepting bonus() at RETURN and
 * rebuilding the record is exact, keeps config edits live, and stays away from
 * &lt;clinit&gt; (which runs before the ForgeConfigSpec is available).
 *
 * Real values read out of the jar (used to confirm the scaffolded defaults are
 * behaviour-identical):
 *
 *   heavenlyRoot(..)              : primaryElementMult 1.5, counterElementMult 0.5,
 *                                   extraZhenyuanPerSubLevel 1
 *                                   -> HEAVENLY_METAL/WOOD/WATER/FIRE/EARTH
 *   dualRoot(..)                  : primaryElementMult 1.2, secondaryElementMult 1.2,
 *                                   offElementMult 0.9   -> all ten DUAL_* roots
 *   MUTANT_ICE / MUTANT_LIGHTNING : primaryElementMult 1.6, environmentBuff true
 *   HEAVENLY_SWORD                : swordDmgMult 2.0, nonElementSpellMult 0.5
 *   HEAVENLY_HIDDEN               : nonElementSpellMult 1.5
 *
 * SpiritRootBonus is a record; its canonical constructor is
 * (LQiElement;LQiElement;LQiElement;DDDDDDDDIZZ) = (primaryElement,
 * secondaryElement, counterElement, primaryElementMult, secondaryElementMult,
 * counterElementMult, offElementMult, swordDmgMult, meleeDmgMult,
 * nonElementSpellMult, hpMult, extraZhenyuanPerSubLevel, environmentBuff,
 * cannotCultivate).
 *
 * Roots with no config entries (NONE, TRIPLE, QUADRUPLE, FIVE_ROOT,
 * FIVE_ELEMENT_CHAOS, BROKEN_VEIN_BODY) are passed straight through untouched.
 */
@Mixin(SpiritRoot.class)
public abstract class SpiritRootMixin {

    @Inject(method = "bonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$bonus(CallbackInfoReturnable<SpiritRootBonus> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_ROOT_OVERRIDES.get()) {
            return;
        }
        SpiritRootBonus original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        SpiritRoot self = (SpiritRoot) (Object) this;

        double primaryElementMult = original.primaryElementMult();
        double secondaryElementMult = original.secondaryElementMult();
        double counterElementMult = original.counterElementMult();
        double offElementMult = original.offElementMult();
        double swordDmgMult = original.swordDmgMult();
        double nonElementSpellMult = original.nonElementSpellMult();
        int extraZhenyuanPerSubLevel = original.extraZhenyuanPerSubLevel();

        if (self == SpiritRoot.HEAVENLY_METAL
                || self == SpiritRoot.HEAVENLY_WOOD
                || self == SpiritRoot.HEAVENLY_WATER
                || self == SpiritRoot.HEAVENLY_FIRE
                || self == SpiritRoot.HEAVENLY_EARTH) {
            primaryElementMult = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_PRIMARY_ELEMENT_MULT.get();
            counterElementMult = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_COUNTER_ELEMENT_MULT.get();
            extraZhenyuanPerSubLevel = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_EXTRA_ZHENYUAN_PER_SUB_LEVEL.get();
        } else if (self == SpiritRoot.HEAVENLY_SWORD) {
            swordDmgMult = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_SWORD_SWORD_DMG_MULT.get();
            nonElementSpellMult = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_SWORD_NON_ELEMENT_SPELL_MULT.get();
        } else if (self == SpiritRoot.HEAVENLY_HIDDEN) {
            nonElementSpellMult = ExtendedConfig.SPIRIT_ROOT_HEAVENLY_HIDDEN_NON_ELEMENT_SPELL_MULT.get();
        } else if (self == SpiritRoot.MUTANT_ICE || self == SpiritRoot.MUTANT_LIGHTNING) {
            primaryElementMult = ExtendedConfig.SPIRIT_ROOT_MUTANT_PRIMARY_ELEMENT_MULT.get();
        } else if (self == SpiritRoot.DUAL_METAL_WOOD
                || self == SpiritRoot.DUAL_METAL_WATER
                || self == SpiritRoot.DUAL_METAL_FIRE
                || self == SpiritRoot.DUAL_METAL_EARTH
                || self == SpiritRoot.DUAL_WOOD_WATER
                || self == SpiritRoot.DUAL_WOOD_FIRE
                || self == SpiritRoot.DUAL_WOOD_EARTH
                || self == SpiritRoot.DUAL_WATER_FIRE
                || self == SpiritRoot.DUAL_WATER_EARTH
                || self == SpiritRoot.DUAL_FIRE_EARTH) {
            primaryElementMult = ExtendedConfig.SPIRIT_ROOT_DUAL_PRIMARY_ELEMENT_MULT.get();
            secondaryElementMult = ExtendedConfig.SPIRIT_ROOT_DUAL_SECONDARY_ELEMENT_MULT.get();
            offElementMult = ExtendedConfig.SPIRIT_ROOT_DUAL_OFF_ELEMENT_MULT.get();
        } else {
            return;
        }

        cir.setReturnValue(new SpiritRootBonus(
                original.primaryElement(),
                original.secondaryElement(),
                original.counterElement(),
                primaryElementMult,
                secondaryElementMult,
                counterElementMult,
                offElementMult,
                swordDmgMult,
                original.meleeDmgMult(),
                nonElementSpellMult,
                original.hpMult(),
                extraZhenyuanPerSubLevel,
                original.environmentBuff(),
                original.cannotCultivate()));
    }
}
