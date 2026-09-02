package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.refining.RefiningCoreBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides refining furnace max qi storage.
 */
@Mixin(RefiningCoreBlockEntity.class)
public abstract class RefiningCoreBlockEntityMixin {

    @Inject(method = "getMaxQi", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$getMaxQi(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_REFINING_OVERRIDES.get()) {
            return;
        }
        cir.setReturnValue(ExtendedConfig.REFINING_FURNACE_MAX_QI.get());
    }
}
