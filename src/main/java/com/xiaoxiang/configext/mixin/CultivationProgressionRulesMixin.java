package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationProgressionRules;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies the player cultivation speed multiplier to cultivation progression.
 *
 * maxCultivation() determines how much Qi a player needs to absorb before
 * they can attempt a breakthrough. Multiplying this by the cultivation speed
 * multiplier makes breakthroughs faster (lower max = faster) or slower
 * (higher max = slower).
 *
 * With playerCultivationSpeedMult = 0.2, maxCultivation is multiplied by
 * 1/0.2 = 5.0, meaning the player needs 5x more Qi to advance.
 *
 * With playerCultivationSpeedMult = 3.0, maxCultivation is multiplied by
 * 1/3.0 = 0.33, meaning the player needs only 1/3 the Qi to advance.
 *
 * baseAbsorbMultiplier() determines how efficiently Qi is absorbed.
 * We multiply this directly by the cultivation speed multiplier so
 * that higher speed values make the player absorb Qi faster.
 *
 * NOTE: The config defaults to 1.0, so this mixin is a no-op unless
 * the player manually changes the cultivation speed multiplier.
 */
@Mixin(CultivationProgressionRules.class)
public abstract class CultivationProgressionRulesMixin {

    /**
     * Scale the max cultivation requirement inversely to the cultivation speed.
     * Higher cultivation speed = lower max cultivation = faster progression.
     *
     * NOTE: We do NOT invert the multiplier here anymore. The baseAbsorbMultiplier
     * already scales absorption speed. If we also scale maxCultivation inversely,
     * we get a double-multiplier effect (e.g., 5x more Qi needed AND
     * 5x slower absorption = 25x slower total). Instead, we only scale
     * maxCultivation by the inverse square root to get a gentler curve.
     */
    @Inject(method = "maxCultivation", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$maxCultivation(Realm realm, SubStage subStage, Physique physique,
                                                  CallbackInfoReturnable<Long> cir) {
        double speedMult = ExtendedConfig.PLAYER_CULTIVATION_SPEED_MULT.get();
        if (speedMult == 1.0) return;

        // Use inverse square root for a gentler curve — avoids 25x slowdown
        // With 0.2: sqrt(1/0.2) = sqrt(5) ≈ 2.24x more Qi needed
        // With 3.0: sqrt(1/3.0) ≈ 0.577x less Qi needed
        double invSqrt = 1.0 / Math.sqrt(speedMult);
        long base = cir.getReturnValue();
        long scaled = Math.max(1L, Math.round(base * invSqrt));
        cir.setReturnValue(scaled);
    }

    /**
     * Scale the Qi absorption rate by the cultivation speed multiplier.
     * Higher cultivation speed = faster absorption.
     *
     * We use the square root of the speed multiplier for a gentler curve.
     * With 0.2: sqrt(0.2) ≈ 0.447x absorption speed
     * With 3.0: sqrt(3.0) ≈ 1.732x absorption speed
     *
     * Combined with maxCultivation scaling:
     * 0.2 total: 2.24 / 0.447 ≈ 5x slower (was 25x)
     * 3.0 total: 0.577 / 1.732 ≈ 0.33x = 3x faster
     */
    @Inject(method = "baseAbsorbMultiplier", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$baseAbsorbMultiplier(Realm realm, boolean hasTechnique, boolean equippedTechnique,
                                                        CallbackInfoReturnable<Double> cir) {
        double speedMult = ExtendedConfig.PLAYER_CULTIVATION_SPEED_MULT.get();
        if (speedMult == 1.0) return;

        double base = cir.getReturnValue();
        cir.setReturnValue(base * Math.sqrt(speedMult));
    }
}
