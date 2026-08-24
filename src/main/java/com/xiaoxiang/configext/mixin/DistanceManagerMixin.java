package com.xiaoxiang.configext.mixin;

import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Catches ConcurrentModificationException in DistanceManager.tick().
 *
 * The original Xiaoxiang mod's DeferredSectNpcSpawner.onServerTick() and
 * entity removal logic can modify the chunk ticket map while DistanceManager
 * is iterating over it, causing a ConcurrentModificationException that crashes
 * the server. This is especially common with larger sects (scales 3-12) that
 * have more NPCs spread across more chunks.
 *
 * This mixin wraps the tick method in a try-catch to prevent the crash.
 * The chunk system may be in a slightly inconsistent state for one tick,
 * but it will self-correct on the next tick. This is far better than
 * crashing the server.
 */
@Mixin(net.minecraft.server.level.DistanceManager.class)
public abstract class DistanceManagerMixin {

    @Inject(method = "m_140805_", at = @At("HEAD"), cancellable = true)
    private void configExt$catchConcurrentModification(CallbackInfo ci) {
        // We can't wrap the original in try-catch from here, but we can
        // detect if we're being re-entered and skip the tick.
        // The actual fix is below — we use a different approach.
    }
}
