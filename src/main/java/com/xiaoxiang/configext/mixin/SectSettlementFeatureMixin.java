package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.cultivation.worldgen.SectSettlementFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sect settlement feature mixin — now a no-op.
 *
 * The original mod's biome gate is preserved as-is. Sects will only spawn
 * in appropriate biomes, regardless of world type (default or superflat).
 *
 * Sect spawn rate is controlled via the config option
 * sects.generation.cellSpawnChance (default 0.51, which is 50% higher
 * than the original mod's 0.34).
 *
 * Sect sizes are controlled via SectSizeMixin and the sects.sizeTiers config.
 */
@Mixin(SectSettlementFeature.class)
public abstract class SectSettlementFeatureMixin {

    @Inject(method = "passesSectSiteBiomeGate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$noopBiomeGate(WorldGenLevel level, BlockPos pos, int radius, long seed,
                                                 CallbackInfoReturnable<Boolean> cir) {
        // No-op — let the original mod's biome gate run unmodified.
    }
}
