package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_LIFE_TICK_INTERVAL.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 *
 *   public static void tickSectLife(MinecraftServer server) iterates every
 *   ServerLevel and only proceeds when level.getGameTime() % 1200L == 0L,
 *   then calls the instance overload tickSectLife(ServerLevel, 0.05d, null).
 *   The 1200L is the inlined form of SectSavedData's own
 *   `public static final int SECT_LIFE_TICK_INTERVAL` field (ConstantValue
 *   1200 - confirmed via javap -v's constant pool dump), which is itself
 *   never read via getstatic anywhere in the jar (same "dead field, real
 *   work done through the inlined literal" pattern already documented for
 *   ANCESTOR_LOOSE_IMMORTAL_CHANCE and the sect defense fields below).
 *
 *   IMPORTANT SIDE EFFECT: the 0.05d passed alongside 1200L is
 *   "days of sect-life progress per tick-event" and is NOT itself
 *   config-backed (no matching ExtendedConfig field exists for it).
 *   At the real default, 24000 game ticks (1 Minecraft day) produces
 *   24000/1200 = 20 tick-events * 0.05 days/event = 1.0 sect-life day per
 *   real Minecraft day. Lowering SECT_LIFE_TICK_INTERVAL makes tick-events
 *   fire more often WITHOUT reducing the 0.05d-per-event amount, so sect
 *   life (aging, journeys, department shifts, etc.) will progress
 *   proportionally FASTER than real time - and a larger interval slows it
 *   down. This is an inherent consequence of wiring the one config field
 *   that exists for this and is documented here rather than silently
 *   rescaling 0.05d, which the original config schema never asked for.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectLifeTickIntervalMixin {

    @ModifyConstant(method = "tickSectLife(Lnet/minecraft/server/MinecraftServer;)V",
            constant = @Constant(longValue = 1200L), remap = false, require = 0)
    private static long configExt$lifeTickInterval(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int interval = ExtendedConfig.SECT_LIFE_TICK_INTERVAL.get();
        return interval <= 0 ? original : (long) interval;
    }
}
