package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import com.xiaoxiang.cultivation.cultivation.qi.field.QiModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.qiGathering.* (per-tier multiplier + maxMult).
 *
 * FormationType is an abstract enum; every constant has its own body, compiled to
 * an anonymous subclass. Verified via javap on FormationType.class &lt;clinit&gt;:
 *   FormationType$1 = QI_GATHERING   FormationType$2 = SECT_PROTECTION
 *   FormationType$3 = WITHER_GROWTH  FormationType$4 = REJUVENATION
 *   FormationType$5 = FLIGHT_BAN     FormationType$6 = MAZE
 *   FormationType$7 = FARM_HARVEST   FormationType$8 = STORAGE
 * The real values therefore live on the subclasses, not on FormationType itself
 * (whose base implementations are stubs returning 1.0 / -1 / 0).
 *
 * FormationType$1.modifierForTier(ItemTier)QiModifier bytecode:
 *   switch (tier) { LOW -&gt; 2.0, MID -&gt; 2.5, HIGH -&gt; 3.0, SUPREME -&gt; 4.0, IMMORTAL -&gt; 5.0 }
 *   return new QiModifier(1.5, mult, mult, 1.0);
 * QiModifier is a record (double maxMult, regenMult, emitMult, drainMult), ctor (DDDD)V.
 *
 * Config defaults (low 2.0 / mid 2.5 / high 3.0 / supreme 4.0 / immortal 5.0,
 * maxMult 1.5) reproduce the original exactly, so a stock config is a no-op.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.qi.formation.FormationType$1", remap = false)
public abstract class FormationTypeQiGatheringMixin {

    @Inject(method = "modifierForTier", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$modifierForTier(ItemTier tier, CallbackInfoReturnable<QiModifier> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        double mult;
        if (tier == ItemTier.MID) {
            mult = ExtendedConfig.FORMATION_QI_GATHERING_MULT_MID.get();
        } else if (tier == ItemTier.HIGH) {
            mult = ExtendedConfig.FORMATION_QI_GATHERING_MULT_HIGH.get();
        } else if (tier == ItemTier.SUPREME) {
            mult = ExtendedConfig.FORMATION_QI_GATHERING_MULT_SUPREME.get();
        } else if (tier == ItemTier.IMMORTAL) {
            mult = ExtendedConfig.FORMATION_QI_GATHERING_MULT_IMMORTAL.get();
        } else {
            mult = ExtendedConfig.FORMATION_QI_GATHERING_MULT_LOW.get();
        }
        double maxMult = ExtendedConfig.FORMATION_QI_GATHERING_MAX_MULT.get();
        cir.setReturnValue(new QiModifier(maxMult, mult, mult, 1.0));
    }
}
