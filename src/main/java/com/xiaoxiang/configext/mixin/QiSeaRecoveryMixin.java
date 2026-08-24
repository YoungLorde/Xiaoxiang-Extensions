package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Makes Qi recovery / meditation progress update more frequently.
 *
 * The original QiSeaRecoveryHandler only processes Qi recovery every 20 ticks
 * (once per second). This mixin replaces the hardcoded 20 with a configurable
 * interval, allowing smoother progress bars and faster feedback during meditation.
 *
 * With meditationTickInterval=1, recovery updates every tick (20x smoother).
 */
@Mixin(targets = "com.xiaoxiang.cultivation.event.QiSeaRecoveryHandler", remap = false)
public abstract class QiSeaRecoveryMixin {

    @ModifyConstant(method = "onPlayerTick", constant = @Constant(intValue = 20), remap = false)
    private static int configExt$modifyTickInterval(int original) {
        return ExtendedConfig.MEDITATION_TICK_INTERVAL.get();
    }
}
