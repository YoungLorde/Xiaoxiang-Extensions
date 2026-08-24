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
 * mod's DeferredSectNpcSpawner.onServerTick().
 *
 * The original mod's sect NPC repair logic force-loads chunks and modifies
 * the DistanceManager's ticket map during the server tick, causing a
 * ConcurrentModificationException. This is especially common with larger
 * sects (scales 3-12) that have more NPCs spread across more chunks.
 *
 * When SECT_SAFE_TICK is enabled (default true), we cancel the original
 * mod's onServerTick to prevent the crash. Sect NPCs will still spawn
 * correctly when the player visits a sect — the repair logic is only
 * for fixing broken NPCs, which is a nice-to-have, not essential.
 */
@Mixin(value = SectSettlementFeature.DeferredSectNpcSpawner.class, remap = false)
public abstract class DeferredSectNpcSpawnerSafeTickMixin {

    @Inject(
        method = "onServerTick",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 0
    )
    private static void configExt$cancelUnsafeSectTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (ExtendedConfig.SECT_SAFE_TICK.get()) {
            ci.cancel();
        }
    }
}
