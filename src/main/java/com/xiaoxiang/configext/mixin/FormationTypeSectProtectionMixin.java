package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.qiPerDamage.* and formations.barrierDamageImmortal.
 *
 * FormationType$2 is the SECT_PROTECTION enum constant body (see
 * FormationTypeQiGatheringMixin for the constant -&gt; inner class mapping proof).
 * Verified bytecode:
 *
 *   sectProtectionQiPerDamage(Lcom/xiaoxiang/cultivation/cultivation/ItemTier;)J
 *       LOW 100L, MID 50L, HIGH 20L, SUPREME 5L, IMMORTAL 1L
 *       (the base FormationType impl just throws UnsupportedOperationException,
 *        so this override is the only live implementation)
 *
 *   sectProtectionBarrierDamagePerSecond(Lcom/.../ItemTier;)D
 *       tier == IMMORTAL ? 20.0 : 0.0
 *
 * Config defaults reproduce both exactly, so a stock config is a no-op.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.qi.formation.FormationType$2", remap = false)
public abstract class FormationTypeSectProtectionMixin {

    @Inject(method = "sectProtectionQiPerDamage", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$sectProtectionQiPerDamage(ItemTier tier, CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        long value;
        if (tier == ItemTier.MID) {
            value = ExtendedConfig.FORMATION_QI_PER_DAMAGE_MID.get();
        } else if (tier == ItemTier.HIGH) {
            value = ExtendedConfig.FORMATION_QI_PER_DAMAGE_HIGH.get();
        } else if (tier == ItemTier.SUPREME) {
            value = ExtendedConfig.FORMATION_QI_PER_DAMAGE_SUPREME.get();
        } else if (tier == ItemTier.IMMORTAL) {
            value = ExtendedConfig.FORMATION_QI_PER_DAMAGE_IMMORTAL.get();
        } else {
            value = ExtendedConfig.FORMATION_QI_PER_DAMAGE_LOW.get();
        }
        cir.setReturnValue(value);
    }

    @Inject(method = "sectProtectionBarrierDamagePerSecond", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$sectProtectionBarrierDamagePerSecond(ItemTier tier, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        if (tier != ItemTier.IMMORTAL) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.FORMATION_BARRIER_DAMAGE_IMMORTAL.get().doubleValue());
    }
}
