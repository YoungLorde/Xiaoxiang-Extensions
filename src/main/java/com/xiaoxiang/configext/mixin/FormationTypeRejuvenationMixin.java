package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.rejuvenationAmplifier.* (regeneration effect amplifier).
 *
 * FormationType$4 is the REJUVENATION enum constant body. Verified bytecode:
 *   rejuvenationAmplifier(Lcom/xiaoxiang/cultivation/cultivation/ItemTier;)I
 *       LOW 0, MID 1, HIGH 2, SUPREME 3, IMMORTAL 4
 * (the base FormationType implementation returns -1 = "no effect").
 * Consumed by FormationCorePlateBlockEntity.applyRejuvenation(...).
 *
 * Config defaults reproduce the original exactly, so a stock config is a no-op.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.qi.formation.FormationType$4", remap = false)
public abstract class FormationTypeRejuvenationMixin {

    @Inject(method = "rejuvenationAmplifier", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$rejuvenationAmplifier(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        int value;
        if (tier == ItemTier.MID) {
            value = ExtendedConfig.FORMATION_REJUVENATION_AMP_MID.get();
        } else if (tier == ItemTier.HIGH) {
            value = ExtendedConfig.FORMATION_REJUVENATION_AMP_HIGH.get();
        } else if (tier == ItemTier.SUPREME) {
            value = ExtendedConfig.FORMATION_REJUVENATION_AMP_SUPREME.get();
        } else if (tier == ItemTier.IMMORTAL) {
            value = ExtendedConfig.FORMATION_REJUVENATION_AMP_IMMORTAL.get();
        } else {
            value = ExtendedConfig.FORMATION_REJUVENATION_AMP_LOW.get();
        }
        cir.setReturnValue(value);
    }
}
