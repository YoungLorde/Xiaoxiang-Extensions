package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.qi.SpiritVeinCoreTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides spirit vein core tier values (maxQi, orbGain, supplyPerSecond).
 */
@Mixin(SpiritVeinCoreTier.class)
public abstract class SpiritVeinCoreTierMixin {

    @Inject(method = "maxQi", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$maxQi(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_VEIN_OVERRIDES.get()) {
            return;
        }
        SpiritVeinCoreTier self = (SpiritVeinCoreTier) (Object) this;
        long val;
        if (self == SpiritVeinCoreTier.MID) {
            val = ExtendedConfig.SPIRIT_VEIN_MAX_QI_MID.get();
        } else if (self == SpiritVeinCoreTier.HIGH) {
            val = ExtendedConfig.SPIRIT_VEIN_MAX_QI_HIGH.get();
        } else if (self == SpiritVeinCoreTier.SUPREME) {
            val = ExtendedConfig.SPIRIT_VEIN_MAX_QI_SUPREME.get();
        } else if (self == SpiritVeinCoreTier.IMMORTAL) {
            val = ExtendedConfig.SPIRIT_VEIN_MAX_QI_IMMORTAL.get();
        } else {
            val = ExtendedConfig.SPIRIT_VEIN_MAX_QI_LOW.get();
        }
        cir.setReturnValue(val);
    }

    @Inject(method = "orbGain", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$orbGain(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_VEIN_OVERRIDES.get()) {
            return;
        }
        SpiritVeinCoreTier self = (SpiritVeinCoreTier) (Object) this;
        long val;
        if (self == SpiritVeinCoreTier.MID) {
            val = ExtendedConfig.SPIRIT_VEIN_ORB_GAIN_MID.get();
        } else if (self == SpiritVeinCoreTier.HIGH) {
            val = ExtendedConfig.SPIRIT_VEIN_ORB_GAIN_HIGH.get();
        } else if (self == SpiritVeinCoreTier.SUPREME) {
            val = ExtendedConfig.SPIRIT_VEIN_ORB_GAIN_SUPREME.get();
        } else if (self == SpiritVeinCoreTier.IMMORTAL) {
            val = ExtendedConfig.SPIRIT_VEIN_ORB_GAIN_IMMORTAL.get();
        } else {
            val = ExtendedConfig.SPIRIT_VEIN_ORB_GAIN_LOW.get();
        }
        cir.setReturnValue(val);
    }

    @Inject(method = "supplyPerSecond", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$supplyPerSecond(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_VEIN_OVERRIDES.get()) {
            return;
        }
        SpiritVeinCoreTier self = (SpiritVeinCoreTier) (Object) this;
        long val;
        if (self == SpiritVeinCoreTier.MID) {
            val = ExtendedConfig.SPIRIT_VEIN_SUPPLY_MID.get();
        } else if (self == SpiritVeinCoreTier.HIGH) {
            val = ExtendedConfig.SPIRIT_VEIN_SUPPLY_HIGH.get();
        } else if (self == SpiritVeinCoreTier.SUPREME) {
            val = ExtendedConfig.SPIRIT_VEIN_SUPPLY_SUPREME.get();
        } else if (self == SpiritVeinCoreTier.IMMORTAL) {
            val = ExtendedConfig.SPIRIT_VEIN_SUPPLY_IMMORTAL.get();
        } else {
            val = ExtendedConfig.SPIRIT_VEIN_SUPPLY_LOW.get();
        }
        cir.setReturnValue(val);
    }
}
