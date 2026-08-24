package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.growth.* (crop growth acceleration).
 *
 * FormationType$3 is the WITHER_GROWTH enum constant body. Verified bytecode:
 *   growthMultiplier(Lcom/xiaoxiang/cultivation/cultivation/ItemTier;)D
 *       LOW 2.0, MID 4.0, HIGH 6.0, SUPREME 8.0, IMMORTAL 10.0
 * FormationCorePlateBlockEntity.accelerateGrowth(...) calls
 * FormationType.WITHER_GROWTH.growthMultiplier(state.flagTier) and derives the
 * per-tick random-tick budget from it, so this is the single source of truth.
 *
 * Config defaults reproduce the original exactly, so a stock config is a no-op.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.qi.formation.FormationType$3", remap = false)
public abstract class FormationTypeWitherGrowthMixin {

    @Inject(method = "growthMultiplier", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$growthMultiplier(ItemTier tier, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        double value;
        if (tier == ItemTier.MID) {
            value = ExtendedConfig.FORMATION_GROWTH_MULT_MID.get();
        } else if (tier == ItemTier.HIGH) {
            value = ExtendedConfig.FORMATION_GROWTH_MULT_HIGH.get();
        } else if (tier == ItemTier.SUPREME) {
            value = ExtendedConfig.FORMATION_GROWTH_MULT_SUPREME.get();
        } else if (tier == ItemTier.IMMORTAL) {
            value = ExtendedConfig.FORMATION_GROWTH_MULT_IMMORTAL.get();
        } else {
            value = ExtendedConfig.FORMATION_GROWTH_MULT_LOW.get();
        }
        cir.setReturnValue(value);
    }
}
