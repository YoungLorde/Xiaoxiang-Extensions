package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.realm.BeastRealm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides beast realm advance costs with config-driven values.
 */
@Mixin(BeastRealm.class)
public abstract class BeastRealmMixin {

    @Inject(method = "advanceCost", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$advanceCost(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }
        BeastRealm self = (BeastRealm) (Object) this;
        // Use if-else instead of switch to avoid generating anonymous inner class
        // (BeastRealmMixin$1) which causes NoClassDefFoundError at runtime
        long val;
        if (self == BeastRealm.MORTAL_BEAST) {
            val = 0L;
        } else if (self == BeastRealm.SPIRIT_SOLDIER) {
            val = ExtendedConfig.SPIRIT_SOLDIER_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_GENERAL) {
            val = ExtendedConfig.SPIRIT_GENERAL_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_MARSHAL) {
            val = ExtendedConfig.SPIRIT_MARSHAL_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_KING) {
            val = ExtendedConfig.SPIRIT_KING_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_EMPEROR) {
            val = ExtendedConfig.SPIRIT_EMPEROR_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_LORD) {
            val = ExtendedConfig.SPIRIT_LORD_ADVANCE_COST.get();
        } else if (self == BeastRealm.SPIRIT_SAINT) {
            val = Long.MAX_VALUE;
        } else {
            val = 0L;
        }
        cir.setReturnValue(val);
    }
}
