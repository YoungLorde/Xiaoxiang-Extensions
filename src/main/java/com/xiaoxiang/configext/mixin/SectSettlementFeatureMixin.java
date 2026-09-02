package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.cultivation.worldgen.SectSettlementFeature;
import com.xiaoxiang.cultivation.worldgen.SectSiteSelector;
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
 *
 * UPDATED 2026-09-02 for base mod 0.1.1479: passesSectSiteBiomeGate's
 * signature changed completely, from (WorldGenLevel, BlockPos, int radius,
 * long seed) to (SectSiteSelector.TerrainSampler, int, int, int, long,
 * double) - the base mod replaced direct WorldGenLevel/BlockPos access with
 * an abstracted TerrainSampler interface (confirmed public via javap -p on
 * SectSiteSelector$TerrainSampler.class in the 0.1.1479 jar). Confirmed via
 * javap -p -c -s on SectSettlementFeature.class that the sole call site
 * (in resolveCellSite) passes (sampler, pos.getX(), pos.getZ(),
 * sectRadius, cellPlan.seed(), thresholds.treedBiomeSpawnChance()) in that
 * order, so the params below are named x/z/radius/seed/treedBiomeSpawnChance
 * to match. This was a HARD crash at game launch (MixinApplyError /
 * InvalidInjectionException: "Invalid descriptor"), not something require=0
 * could soften - require=0 only suppresses a "target method not found by
 * name" failure; it does NOT cover "target found, but this handler's
 * parameter list doesn't match its actual descriptor", which Mixin treats
 * as a fatal configuration error regardless of require. A signature-level
 * base-mod change like this one still needs a source fix, not just
 * hardening.
 */
@Mixin(SectSettlementFeature.class)
public abstract class SectSettlementFeatureMixin {

    @Inject(method = "passesSectSiteBiomeGate", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$noopBiomeGate(SectSiteSelector.TerrainSampler sampler, int x, int z, int radius,
                                                 long seed, double treedBiomeSpawnChance,
                                                 CallbackInfoReturnable<Boolean> cir) {
        // No-op — let the original mod's biome gate run unmodified.
    }
}
