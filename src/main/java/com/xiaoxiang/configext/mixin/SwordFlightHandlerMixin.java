package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.SwordFlightHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SWORD_FLIGHT_UPKEEP_QI_PER_SECOND.
 *
 * Verified via javap -p -c -s against SwordFlightHandler.class (2026-09-01):
 * tick(ServerPlayer, CultivationData) gates on "playerTickCount % 20 == 0"
 * (once per real-life second) and then calls
 * TechniqueBonusHelper.applySpellQiCostMultiplier(player, Spell.SWORD_FLIGHT,
 * 20L) - the "long 20l" (ldc2_w) IS the per-second qi upkeep, distinct from
 * the unrelated "int 20" (bipush) used for the once-per-second tick gate a
 * few instructions earlier (different opcode/type, no collision risk).
 * Confirmed the sole "long 20l" occurrence in tick().
 */
@Mixin(value = SwordFlightHandler.class, remap = false)
public abstract class SwordFlightHandlerMixin {

    @ModifyConstant(method = "tick", constant = @Constant(longValue = 20L), remap = false, require = 0)
    private static long configExt$upkeepQiPerSecond(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.SWORD_FLIGHT_UPKEEP_QI_PER_SECOND.get().longValue();
    }
}
