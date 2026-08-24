package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.alchemy.AlchemyCoreBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides alchemy furnace max qi storage.
 */
@Mixin(AlchemyCoreBlockEntity.class)
public abstract class AlchemyCoreBlockEntityMixin {

    @Inject(method = "getMaxQi", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$getMaxQi(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_ALCHEMY_OVERRIDES.get()) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.ALCHEMY_FURNACE_MAX_QI.get());
    }
}
