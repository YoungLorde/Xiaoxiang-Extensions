package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.worldgen.SectSettlementFeature;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents ConcurrentModificationException crashes caused by the original
 * mod's DeferredSectNpcSpawner.onServerTick() — without blocking the sect
 * chest/barrel loot-filling work that shares the same method.
 *
 * Bytecode analysis of the current jar's onServerTick(TickEvent.ServerTickEvent)
 * shows it does five things in this order, every tick:
 *
 *   1. Housekeeping (publish/wake/persist/hydrate/validate deferred chunk
 *      intents and tasks).
 *   2. Drain and process the deferred SECT INITIALIZATION queue — this is
 *      what calls DeferredSectInitializationTask.tryInitialize(...), which
 *      is what actually places loot into sect chests/barrels via
 *      SectSettlementFeature.fillSectContainers(...). Every call here is
 *      already individually wrapped in a try/catch by the original mod
 *      (falls back to a retry next tick on any Exception).
 *   3. For every loaded ServerLevel, call
 *      SectSettlementFeature.tickSectIBackfill(ServerLevel) — NOT wrapped
 *      in any try/catch by the original mod.
 *   4. Drain and process the deferred NPC SPAWN/REPAIR queue — this is what
 *      calls DeferredNpcSpawnTask.trySpawn(...), which force-loads chunks
 *      and queries/spawns entities. Individual calls here are also wrapped
 *      in try/catch, but the queue-iteration bookkeeping around them is not.
 *   5. Return.
 *
 * The previous version of this mixin cancelled the entire method at HEAD,
 * which also silently skipped step 2 — so freshly-generated sects never got
 * their chests/barrels filled. That is the empty-container bug reported by
 * players on new saves.
 *
 * The crash this mixin exists to prevent was always attributed (see the
 * original doc comment below, and DistanceManagerMixin's) to the NPC
 * repair/spawn logic force-loading chunks and touching the chunk ticket map
 * — i.e. steps 3 and 4, not step 2 (which only writes items into already-
 * loaded container block entities and touches no chunk-loading machinery).
 *
 * So instead of cancelling at HEAD, this mixin now injects immediately
 * before the first call to tickSectIBackfill (the start of step 3) and
 * cancels there when SECT_SAFE_TICK is enabled. Steps 1 and 2 — including
 * the loot-filling work — always run normally. Steps 3 and 4 (the actual
 * suspected crash source) are skipped exactly as before, with identical
 * protection to the previous version. require = 0 means that if a future
 * update to the original mod changes this call site so it can no longer be
 * found, this mixin silently becomes a no-op (onServerTick runs in full,
 * uncancelled) rather than failing to load — the same fail-open convention
 * the previous version of this mixin already used.
 */
@Mixin(value = SectSettlementFeature.DeferredSectNpcSpawner.class, remap = false)
public abstract class DeferredSectNpcSpawnerSafeTickMixin {

    @Inject(
        method = "onServerTick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/xiaoxiang/cultivation/worldgen/SectSettlementFeature;tickSectIBackfill(Lnet/minecraft/server/level/ServerLevel;)V"
        ),
        cancellable = true,
        remap = false,
        require = 0
    )
    private static void configExt$cancelUnsafeNpcRepairTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (ExtendedConfig.SECT_SAFE_TICK.get()) {
            ci.cancel();
        }
    }
}
