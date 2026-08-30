package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.worldgen.SectSettlementFeature;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents ConcurrentModificationException-style crashes caused by the
 * original mod's DeferredSectNpcSpawner.onServerTick() — without blocking
 * the sect NPC spawn queue that shares the same method. This is the direct
 * fix for "sects are empty of NPCs": the previous version of this mixin was
 * silently disabling NPC spawning entirely, every tick, whenever the
 * SECT_SAFE_TICK safety option was on (its default).
 *
 * Full bytecode analysis of onServerTick(TickEvent.ServerTickEvent) in the
 * currently installed jar (javap -p -c against
 * SectSettlementFeature$DeferredSectNpcSpawner.class) shows it does five
 * things in this order, every tick:
 *
 *   1. Housekeeping (publish/wake/persist/hydrate/validate deferred chunk
 *      intents and tasks).
 *   2. Drain and process the deferred SECT INITIALIZATION queue — calls
 *      DeferredSectInitializationTask.tryInitialize(...), which places loot
 *      into sect chests/barrels. Confirmed wrapped in its own
 *      try/catch(Exception) in the original bytecode (exception table
 *      ranges covering offsets 383-482, handler at 485: reschedules the
 *      task and logs, does not crash).
 *   3. For every loaded ServerLevel, call
 *      SectSettlementFeature.tickSectIBackfill(ServerLevel). Confirmed BY
 *      DIRECT BYTECODE READ to have NO exception table coverage anywhere in
 *      onServerTick — the sole real, uncaught crash risk in this method.
 *   4. Drain and process the deferred NPC SPAWN/REPAIR queue — calls
 *      DeferredNpcSpawnTask.trySpawn(...), which force-loads chunks and
 *      queries/spawns entities. Confirmed BY DIRECT BYTECODE READ to be
 *      wrapped in its own try/catch(Exception) covering the trySpawn call,
 *      the result dispatch, AND every per-result branch (exception table
 *      ranges 846-997, handler at 1000: reschedules the task and logs,
 *      does not crash) — i.e. this step was never actually at risk.
 *   5. Return.
 *
 * An earlier version of this mixin cancelled onServerTick() at HEAD, which
 * also silently skipped step 2 (empty chests/barrels on new saves). That was
 * fixed by moving the cancellation point to just before step 3. But
 * cancelling at that point still aborts everything after it in the same
 * method call — which includes step 4, the actual NPC-spawning logic. Since
 * step 4 is already exception-safe in the original bytecode (see above),
 * cancelling it bought no additional safety and was the direct cause of
 * "solo cultivators spawn fine, but no sect NPCs ever appear."
 *
 * The correct, narrow fix: redirect only the step-3 call itself and wrap
 * JUST that one call in try/catch, matching the original mod's own
 * catch-and-reschedule-next-tick convention used everywhere else in this
 * method. Steps 1, 2, and 4 always run normally and untouched — including
 * NPC spawning. Only step 3, the one call with a genuine gap in the
 * original mod's own exception handling, gets the safety net, and only
 * that single level's backfill for that single tick is skipped on an
 * exception (it retries automatically next tick, since tickSectIBackfill
 * runs every tick for every loaded level).
 *
 * require = 0 means that if a future update to the original mod changes
 * this call site so it can no longer be found, this mixin silently becomes
 * a no-op (tickSectIBackfill runs unprotected, exactly as it would with no
 * mixin at all) rather than failing to load.
 */
@Mixin(value = SectSettlementFeature.DeferredSectNpcSpawner.class, remap = false)
public abstract class DeferredSectNpcSpawnerSafeTickMixin {

    @Redirect(
        method = "onServerTick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/xiaoxiang/cultivation/worldgen/SectSettlementFeature;tickSectIBackfill(Lnet/minecraft/server/level/ServerLevel;)V"
        ),
        remap = false,
        require = 0
    )
    private static void configExt$safeTickSectIBackfill(ServerLevel level) {
        if (ExtendedConfig.SECT_SAFE_TICK.get()) {
            try {
                SectSettlementFeature.tickSectIBackfill(level);
            } catch (Throwable t) {
                // Swallow and let it retry next tick, matching the original
                // mod's own catch-and-reschedule pattern for steps 2 and 4
                // of this same method. This is the only thing SECT_SAFE_TICK
                // now guards — it no longer touches NPC spawning at all.
            }
        } else {
            // Safety net disabled: call through unprotected, exactly as the
            // original mod does with no mixin present.
            SectSettlementFeature.tickSectIBackfill(level);
        }
    }
}
