package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import com.xiaoxiang.cultivation.item.StorageBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides storage bag grid dimensions (columns, rows) with config-driven ones.
 */
@Mixin(StorageBagItem.class)
public abstract class StorageBagItemMixin {

    @Inject(method = "columsFor", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$columsFor(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        int val = switch (tier) {
            default -> ExtendedConfig.STORAGE_BAG_COLUMNS_LOW.get();
            case MID -> ExtendedConfig.STORAGE_BAG_COLUMNS_MID.get();
            case HIGH -> ExtendedConfig.STORAGE_BAG_COLUMNS_HIGH.get();
            case SUPREME -> ExtendedConfig.STORAGE_BAG_COLUMNS_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.STORAGE_BAG_COLUMNS_IMMORTAL.get();
        };
        cir.setReturnValue(val);
    }

    @Inject(method = "rowsFor", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$rowsFor(ItemTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        int val = switch (tier) {
            default -> ExtendedConfig.STORAGE_BAG_ROWS_LOW.get();
            case MID -> ExtendedConfig.STORAGE_BAG_ROWS_MID.get();
            case HIGH -> ExtendedConfig.STORAGE_BAG_ROWS_HIGH.get();
            case SUPREME -> ExtendedConfig.STORAGE_BAG_ROWS_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.STORAGE_BAG_ROWS_IMMORTAL.get();
        };
        cir.setReturnValue(val);
    }
}
