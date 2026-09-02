package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.qi.consumer.PlayerQiConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires QI_SYSTEM_ATTRACTION_RADIUS, QI_SYSTEM_MEDITATION_RANGE_BONUS, and
 * QI_SYSTEM_MEDITATION_EFFICIENCY_BONUS.
 *
 * PlayerQiConsumer declares BASE_ATTRACTION_RADIUS/MEDITATION_RANGE_BONUS/
 * MEDITATION_EFFICIENCY_BONUS as "static final double" fields, but javac
 * constant-folds primitive static finals at every USE site, so the fields
 * themselves are not live targets - the inlined literals in the methods that
 * actually consume them are. Verified via javap -p -c -s (2026-09-01):
 *
 *   attractRadius()          - one "ldc2_w double 14.0d" (base radius) and
 *                               one "ldc2_w double 10.0d" (meditation range
 *                               bonus, gated on CultivationData.isMeditating()).
 *   finalAbsorbMultiplier()  - a SEPARATE "ldc2_w double 10.0d" (meditation
 *                               efficiency bonus, also gated on isMeditating()) -
 *                               the same numeric value as the range bonus but a
 *                               different constant in a different method, so
 *                               scoping each injector to its own method name
 *                               keeps them apart without needing an ordinal.
 *
 * All three literals are the sole occurrence of their value within their own
 * method.
 */
@Mixin(value = PlayerQiConsumer.class, remap = false)
public abstract class PlayerQiConsumerMixin {

    @ModifyConstant(method = "attractRadius", constant = @Constant(doubleValue = 14.0), remap = false, require = 0)
    private double configExt$baseAttractionRadius(double original) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return original;
        return ExtendedConfig.QI_SYSTEM_ATTRACTION_RADIUS.get();
    }

    @ModifyConstant(method = "attractRadius", constant = @Constant(doubleValue = 10.0), remap = false, require = 0)
    private double configExt$meditationRangeBonus(double original) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return original;
        return ExtendedConfig.QI_SYSTEM_MEDITATION_RANGE_BONUS.get();
    }

    @ModifyConstant(method = "finalAbsorbMultiplier", constant = @Constant(doubleValue = 10.0), remap = false, require = 0)
    private static double configExt$meditationEfficiencyBonus(double original) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return original;
        return ExtendedConfig.QI_SYSTEM_MEDITATION_EFFICIENCY_BONUS.get();
    }
}
