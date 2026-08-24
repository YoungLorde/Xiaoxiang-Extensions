package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.ai.CultivatorRangedKitingGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the kiting half of the "npcAi" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/entity/npc/ai/CultivatorRangedKitingGoal.class:
 *
 *   GIVE_UP_RANGE = 40.0 -> m_8036_, compared against m_20270_ (plain distance)
 *   MIN_RANGE     = 8.0  -> m_8037_ compares distanceToSqr against 64.0 (8^2)
 *   MAX_RANGE     = 16.0 -> m_8037_ compares distanceToSqr against 256.0 (16^2)
 *   MIN_RANGE     = 8.0  -> moveAwayFrom, step length
 *   PREFERRED_RANGE = 12.0 -> moveTowardPreferredRange, step length
 *   GROUND_WALK_SPEED_MODIFIER = 1.0 -> dconst_1 speed arg of moveTo(D,D,D,D)
 *                                       in both move* methods
 *
 * Note the two comparisons in m_8037_ are against SQUARED distances, so the
 * config value (a plain distance) is squared before being handed back.
 */
@Mixin(CultivatorRangedKitingGoal.class)
public abstract class CultivatorRangedKitingGoalMixin {

    /** GIVE_UP_RANGE, compared against a plain (non-squared) distance. */
    @ModifyConstant(method = "m_8036_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 40.0), remap = false, require = 0)
    private double configExt$kitingGiveUpRange(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_KITING_GIVE_UP_RANGE.get();
    }

    /** MIN_RANGE squared (64.0) - "target is too close, back off". */
    @ModifyConstant(method = "m_8037_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 64.0), remap = false, require = 0)
    private double configExt$kitingMinRangeSq(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        double r = ExtendedConfig.NPC_AI_KITING_MIN_RANGE.get();
        return r * r;
    }

    /** MAX_RANGE squared (256.0) - "target is too far, close in". */
    @ModifyConstant(method = "m_8037_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 256.0), remap = false, require = 0)
    private double configExt$kitingMaxRangeSq(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        double r = ExtendedConfig.NPC_AI_KITING_MAX_RANGE.get();
        return r * r;
    }

    /** MIN_RANGE used as the back-off step length. */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 8.0), remap = false, require = 0)
    private double configExt$kitingMinRange(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_KITING_MIN_RANGE.get();
    }

    /** PREFERRED_RANGE used as the close-in step length. */
    @ModifyConstant(method = "moveTowardPreferredRange", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 12.0), remap = false, require = 0)
    private double configExt$kitingPreferredRange(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_KITING_PREFERRED_RANGE.get();
    }

    /** GROUND_WALK_SPEED_MODIFIER, back-off path. */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 1.0), remap = false, require = 0)
    private double configExt$kitingWalkSpeedAway(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_KITING_GROUND_WALK_SPEED_MODIFIER.get();
    }

    /** GROUND_WALK_SPEED_MODIFIER, close-in path. */
    @ModifyConstant(method = "moveTowardPreferredRange", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 1.0), remap = false, require = 0)
    private double configExt$kitingWalkSpeedToward(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_KITING_GROUND_WALK_SPEED_MODIFIER.get();
    }
}
