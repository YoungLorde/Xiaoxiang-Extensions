package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.config.ModCommonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides the sect settlement cell spawn chance with our configurable value.
 * The original mod has a very low default chance; we allow the player to set
 * a higher chance (default 0.5 = 50%) so sects are easier to find, especially
 * on flat worlds where exploration is faster.
 */
@Mixin(ModCommonConfig.class)
public abstract class ModCommonConfigMixin {

    @Inject(method = "sectSettlementCellSpawnChance", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$overrideSectSpawnChance(CallbackInfoReturnable<Double> cir) {
        try {
            cir.setReturnValue(ExtendedConfig.SECT_SETTLEMENT_CELL_SPAWN_CHANCE.get());
        } catch (Exception e) {
            // Config not loaded yet, use default
        }
    }
}
