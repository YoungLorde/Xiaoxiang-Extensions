package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.VoidEscapeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires VOID_ESCAPE_CHARGE_TICKS, VOID_ESCAPE_CHARGE_QI_PER_TICK, and
 * VOID_ESCAPE_ACTIVE_QI_PER_TICK.
 *
 * Verified via javap -p -c -s against VoidEscapeHandler.class (2026-09-01):
 * neither charge duration nor either qi rate has a dedicated named constant
 * field on this class - both are inlined directly at their single use site:
 *
 *   - tickCharge(ServerPlayer, CultivationData): deducts "long 10l" qi
 *     every tick while charging (sole occurrence in the method), then
 *     checks getChargingTicks() >= "bipush 100" to transition into the
 *     void (sole occurrence).
 *   - onPlayerTick(PlayerTickEvent): deducts "long 5l" qi every tick while
 *     void-escape is already active (sole occurrence in the method).
 *
 * All three confirmed as the sole occurrence of their literal within their
 * respective method.
 */
@Mixin(value = VoidEscapeHandler.class, remap = false)
public abstract class VoidEscapeHandlerMixin {

    @ModifyConstant(method = "tickCharge", constant = @Constant(intValue = 100), remap = false, require = 0)
    private static int configExt$chargeTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_ESCAPE_CHARGE_TICKS.get();
    }

    @ModifyConstant(method = "tickCharge", constant = @Constant(longValue = 10L), remap = false, require = 0)
    private static long configExt$chargeQiPerTick(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_ESCAPE_CHARGE_QI_PER_TICK.get().longValue();
    }

    @ModifyConstant(method = "onPlayerTick", constant = @Constant(longValue = 5L), remap = false, require = 0)
    private static long configExt$activeQiPerTick(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_ESCAPE_ACTIVE_QI_PER_TICK.get().longValue();
    }
}
