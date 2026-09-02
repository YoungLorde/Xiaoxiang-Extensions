package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.Blood07FormationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SEA_CHANNEL_TICKS.
 *
 * "Sea of Overwhelming Blood" (message keys use
 * "sea_of_overwhelming_blood") is implemented by Blood07FormationHandler,
 * not any class with "Sea" in its own name - found via
 * ChargeableSpellHandler.onPlayerTick()'s per-Spell dispatch table, which
 * routes Spell.SEA_OF_OVERWHELMING_BLOOD to
 * Blood07FormationHandler.tickChannel(ServerPlayer, CultivationData).
 *
 * Verified via javap -p -c -s against Blood07FormationHandler.class
 * (2026-09-01): the real CHANNEL_TICKS field (int, ConstantValue 200,
 * matching the config's already-correct default) is inlined as
 * "sipush 200" at the single channel-duration comparison in tickChannel(),
 * confirmed the sole occurrence in that method.
 */
@Mixin(value = Blood07FormationHandler.class, remap = false)
public abstract class Blood07FormationHandlerMixin {

    @ModifyConstant(method = "tickChannel", constant = @Constant(intValue = 200), remap = false, require = 0)
    private static int configExt$seaChannelTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.SEA_CHANNEL_TICKS.get();
    }
}
