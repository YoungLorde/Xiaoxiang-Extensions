package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.configext.world.SectScaleState;
import com.xiaoxiang.cultivation.worldgen.SectSettlementFeature;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the sect scale system from 3 tiers (0=small, 1=medium, 2=large) to
 * 13 tiers (0=humble to 12=grand immortal sect).
 *
 * How it works:
 * - chooseScale() is replaced to return 0-12 based on 13 configurable spawn chances
 * - The original building methods use if-else on scale. For scales > 2, they
 *   fall to the "default" (scale 2) branch, giving the largest original counts.
 * - We redirect RandomSource.nextInt(bound) calls within building methods to
 *   multiply the bound based on the actual scale, giving more buildings.
 * - The scale is also used in createPlan for position offsets (scale * 8, etc.),
 *   so larger scales naturally spread buildings further apart.
 */
@Mixin(SectSettlementFeature.class)
public abstract class SectSizeMixin {

    /** Stores the current sect scale (0-12) for use in nextInt redirects. */
    private static int currentSectScale = 0;

    /**
     * Replace chooseScale() with a weighted random selection from 13 tiers.
     * The "spawn chance" values are relative — higher = more likely to appear.
     */
    @Inject(method = "chooseScale", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$chooseScale(RandomSource random, CallbackInfoReturnable<Integer> cir) {
        // Check if a forced scale is pending (e.g., for the initial sect)
        if (SectScaleState.hasForcedScale()) {
            int scale = SectScaleState.consumeForcedScale();
            currentSectScale = scale;
            cir.setReturnValue(scale);
            return;
        }

        List<Double> chances = new ArrayList<>();
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_0.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_1.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_2.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_3.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_4.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_5.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_6.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_7.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_8.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_9.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_10.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_11.get());
        chances.add(ExtendedConfig.SECT_SIZE_SPAWN_CHANCE_12.get());

        double total = 0;
        for (double c : chances) total += c;
        if (total <= 0) {
            currentSectScale = 0;
            cir.setReturnValue(0);
            return;
        }

        double roll = random.nextDouble() * total;
        double cumulative = 0;
        for (int i = 0; i < chances.size(); i++) {
            cumulative += chances.get(i);
            if (roll < cumulative) {
                currentSectScale = i;
                cir.setReturnValue(i);
                return;
            }
        }
        currentSectScale = 12;
        cir.setReturnValue(12);
    }

    /**
     * Get the building count multiplier for the current scale.
     * For scales 0-2: 1.0 (original behavior)
     * For scales 3-12: 1.0 + (scale - 2) * configMultiplier
     */
    private static float getBuildingMultiplier() {
        if (currentSectScale <= 2) return 1.0f;
        float mult = ExtendedConfig.SECT_BUILDING_COUNT_MULTIPLIER.get().floatValue();
        return 1.0f + (currentSectScale - 2) * mult;
    }

    /**
     * Redirect nextInt calls in addResidences to scale building counts.
     * require = 0 so it doesn't crash if the method signature changes.
     */
    @Redirect(method = "addResidences", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;m_188503_(I)I"), remap = false, require = 0)
    private static int configExt$scaleResidencesNextInt(RandomSource random, int bound) {
        return random.nextInt(Math.max(1, (int)(bound * getBuildingMultiplier())));
    }

    /**
     * Redirect nextInt calls in addGates to scale gate counts.
     */
    @Redirect(method = "addGates", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;m_188503_(I)I"), remap = false, require = 0)
    private static int configExt$scaleGatesNextInt(RandomSource random, int bound) {
        return random.nextInt(Math.max(1, (int)(bound * getBuildingMultiplier())));
    }

    /**
     * Redirect nextInt calls in addPavilions to scale pavilion counts.
     */
    @Redirect(method = "addPavilions", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;m_188503_(I)I"), remap = false, require = 0)
    private static int configExt$scalePavilionsNextInt(RandomSource random, int bound) {
        return random.nextInt(Math.max(1, (int)(bound * getBuildingMultiplier())));
    }

    /**
     * Redirect nextInt calls in shouldAddProtectionArray to increase
     * protection array chance for larger sects.
     */
    @Redirect(method = "shouldAddProtectionArray", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;m_188503_(I)I"), remap = false, require = 0)
    private static int configExt$scaleProtectionArrayNextInt(RandomSource random, int bound) {
        float mult = getBuildingMultiplier();
        if (mult > 1.0f && bound == 100) {
            return random.nextInt(Math.max(1, (int)(bound / mult)));
        }
        return random.nextInt(bound);
    }
}
