package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.ai.CultivatorEmergencyRetreatGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the retreat half of the "npcAi" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/entity/npc/ai/CultivatorEmergencyRetreatGoal.class.
 * Every one of these literals lives in the private moveAwayFrom(LivingEntity)
 * method:
 *
 *   RETREAT_DISTANCE     = 18.0 -> scale of the retreat direction vector
 *   RETREAT_RANDOM_SPREAD= 0.75 -> yRot jitter, in RADIANS (not blocks)
 *   GROUND_RETREAT_SPEED = 1.25 -> speed arg of PathNavigation.moveTo
 *   AIR_RETREAT_SPEED    = 0.46 -> applied to BOTH the x and z components of
 *                                  setDeltaMovement, so both occurrences are
 *                                  intentionally rewritten by one injector
 */
@Mixin(CultivatorEmergencyRetreatGoal.class)
public abstract class CultivatorEmergencyRetreatGoalMixin {

    /** RETREAT_DISTANCE. */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 18.0), remap = false, require = 0)
    private double configExt$retreatDistance(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_RETREAT_DISTANCE.get();
    }

    /** RETREAT_RANDOM_SPREAD (radians of yaw jitter). */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.75), remap = false, require = 0)
    private double configExt$retreatRandomSpread(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_RETREAT_RANDOM_SPREAD.get();
    }

    /** GROUND_RETREAT_SPEED. */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 1.25), remap = false, require = 0)
    private double configExt$retreatGroundSpeed(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_RETREAT_GROUND_SPEED.get();
    }

    /** AIR_RETREAT_SPEED (both the x and z components). */
    @ModifyConstant(method = "moveAwayFrom", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.46), remap = false, require = 0)
    private double configExt$retreatAirSpeed(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_RETREAT_AIR_SPEED.get();
    }
}
