package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.spirit.SpiritVeinCoreBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the two "spiritVeins" radius fields not covered by
 * SpiritVeinCoreTierMixin (which handles the per-tier maxQi/orbGain/
 * supplyPerSecond fields on the separate SpiritVeinCoreTier enum).
 *
 * Verified via javap -p -c -s on SpiritVeinCoreBlockEntity.class:
 *
 *   public double attractRadius()  -> "ldc2_w 14.0d; dreturn" (single literal,
 *     clean @Inject(RETURN, require = 0) target). Called from the live
 *     findNearestConsumer(ServerLevel, Vec3, double) qi-distribution lookup.
 *
 *   private QiStorageTarget findLowestQiTarget() -> stores "bipush 16" into a
 *     local once, then reuses that local for both the squared-distance
 *     threshold and the box-scan bounds (pos.offset(-r,-r,-r) to
 *     pos.offset(r,r,r)) - a single literal occurrence, so one
 *     @ModifyConstant covers the whole method. Called from
 *     supplyLowestQiBlock(), called from serverTick() - real per-tick
 *     gameplay code, not cosmetic.
 */
@Mixin(value = SpiritVeinCoreBlockEntity.class, remap = false)
public abstract class SpiritVeinCoreBlockEntityMixin {

    @Inject(method = "attractRadius", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$attractRadius(CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_SPIRIT_VEIN_OVERRIDES.get()) return;
        double override = ExtendedConfig.SPIRIT_VEIN_ATTRACT_RADIUS.get();
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @ModifyConstant(method = "findLowestQiTarget",
            constant = @Constant(intValue = 16), require = 0)
    private int configExt$supplyRadius(int original) {
        if (!ExtendedConfig.ENABLE_SPIRIT_VEIN_OVERRIDES.get()) return original;
        return ExtendedConfig.SPIRIT_VEIN_SUPPLY_RADIUS.get();
    }
}
