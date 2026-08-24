package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.harvestInterval.* (including immortalBatchSize).
 *
 * FormationType$7 is the FARM_HARVEST enum constant body. Verified bytecode:
 *   harvestIntervalTicks(Lcom/xiaoxiang/cultivation/cultivation/ItemTier;)I
 *       LOW 1200, MID 600, HIGH 200, SUPREME 20, IMMORTAL 20
 *       (SUPREME and IMMORTAL share one tableswitch branch - hence the config
 *        section has no separate "immortal" interval; IMMORTAL follows SUPREME.)
 *   harvestBatchSize(Lcom/.../ItemTier;)I
 *       tier == IMMORTAL ? 10 : 1
 *
 * Config defaults reproduce both exactly, so a stock config is a no-op.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.qi.formation.FormationType$7", remap = false)
public abstract class FormationTypeFarmHarvestMixin {

    @Inject(method = "harvestIntervalTicks", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$harvestIntervalTicks(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        int value;
        if (tier == ItemTier.MID) {
            value = ExtendedConfig.FORMATION_HARVEST_INTERVAL_MID.get();
        } else if (tier == ItemTier.HIGH) {
            value = ExtendedConfig.FORMATION_HARVEST_INTERVAL_HIGH.get();
        } else if (tier == ItemTier.SUPREME || tier == ItemTier.IMMORTAL) {
            value = ExtendedConfig.FORMATION_HARVEST_INTERVAL_SUPREME.get();
        } else {
            value = ExtendedConfig.FORMATION_HARVEST_INTERVAL_LOW.get();
        }
        cir.setReturnValue(value);
    }

    @Inject(method = "harvestBatchSize", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$harvestBatchSize(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        if (tier != ItemTier.IMMORTAL) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.FORMATION_HARVEST_BATCH_SIZE_IMMORTAL.get());
    }
}
