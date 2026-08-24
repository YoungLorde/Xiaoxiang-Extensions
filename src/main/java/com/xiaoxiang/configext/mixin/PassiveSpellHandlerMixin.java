package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.PassiveSpellHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Applies config overrides to PassiveSpellHandler constants.
 * The original mod has private static final constants for passive spell intervals,
 * Qi costs, and radii. These are inlined as bytecode constants.
 *
 * Note: Only unique constant values are safely intercepted. Common values like
 * 20 (ticks) appear multiple times for different purposes and are not modified
 * to avoid breaking unrelated logic.
 */
@Mixin(value = PassiveSpellHandler.class, remap = false)
public abstract class PassiveSpellHandlerMixin {

    // SLOW_REGEN_INTERVAL = 100 (unique in its method context)
    @ModifyConstant(method = "onPlayerTick", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 100), require = 0)
    private static int configExt$slowRegenInterval(int original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_SLOW_REGEN_INTERVAL.get();
    }

    // SLOW_REGEN_QI_COST = 5L (long)
    @ModifyConstant(method = "onPlayerTick", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 5L), require = 0)
    private static long configExt$slowRegenQiCost(long original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_SLOW_REGEN_QI_COST.get();
    }

    // BIGU_INTERVAL = 1200L (long, unique)
    @ModifyConstant(method = "handleBigu", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 1200L), require = 0)
    private static long configExt$biguInterval(long original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_BIGU_INTERVAL.get();
    }

    // BIGU_QI_COST = 10L (long)
    @ModifyConstant(method = "handleBigu", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 10L), require = 0)
    private static long configExt$biguQiCost(long original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_BIGU_QI_COST.get();
    }

    // QI_FLIGHT_DRAIN_PER_SECOND = 25L (long, unique)
    @ModifyConstant(method = "handleQiFlight", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 25L), require = 0)
    private static long configExt$qiFlightDrainPerSecond(long original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_QI_FLIGHT_DRAIN_PER_SECOND.get();
    }

    // ITEM_ATTRACTION_RADIUS = 20.0 (double)
    @ModifyConstant(method = "handleItemAttraction", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 20.0), require = 0)
    private static double configExt$itemAttractionRadius(double original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_ITEM_ATTRACTION_RADIUS.get();
    }

    // TREASURE_SEIZING_RADIUS = 20.0 (double)
    @ModifyConstant(method = "handleTreasureSeizing", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 20.0), require = 0)
    private static double configExt$treasureSeizingRadius(double original) {
        if (!ExtendedConfig.ENABLE_PASSIVE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PASSIVE_TREASURE_SEIZING_RADIUS.get();
    }
}
