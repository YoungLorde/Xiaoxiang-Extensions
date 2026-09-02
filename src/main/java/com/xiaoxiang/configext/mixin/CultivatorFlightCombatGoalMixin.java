package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.ai.CultivatorFlightCombatGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the flight half of the "npcAi" config section to real behaviour.
 *
 * All of CultivatorFlightCombatGoal's tuning values are `static final`
 * primitives, so javac inlines them at every use site and a mixin on the
 * FIELD would be a no-op. The literals therefore have to be modified where
 * they are actually consumed. Verified against the real bytecode of
 * com/xiaoxiang/cultivation/entity/npc/ai/CultivatorFlightCombatGoal.class:
 *
 *   RANGED_MIN_DISTANCE  = 8.0   (selectNextSegment, ldc2_w, ordinal 0)
 *   RANGED_MAX_DISTANCE  = 16.0  (selectNextSegment, encoded as the 8.0 SPAN
 *                                 at ordinal 1: `8.0 + rand()*8.0`)
 *   MELEE_DISTANCE       = 2.4   (selectNextSegment, single occurrence)
 *   MIN_SEGMENT_TICKS    = 9     (selectNextSegment, bipush 9)
 *   MAX_SEGMENT_TICKS    = 25    (selectNextSegment, encoded as bipush 17,
 *                                 i.e. `9 + rand(17)` -> [9, 25])
 *   MIN_HEIGHT           = 3.0   (combatY, both occurrences are groundY + 3.0)
 *   MAX_HEIGHT           = 15.0  (combatY, single occurrence)
 *   MAX_HORIZONTAL_SPEED = 0.52  (m_8037_, single occurrence)
 *   MAX_VERTICAL_SPEED   = 0.31  (m_8037_, +0.31 and -0.31 clamp pair)
 *
 * Every injector uses require = 0 (the convention used by the existing
 * PassiveSpellHandlerMixin) so a missing constant degrades to "no override"
 * instead of failing the mixin apply and taking the whole modpack down.
 *
 * UPDATED 2026-09-02 for base mod 0.1.1479: re-verified against the new jar
 * (javap -p -c) and confirmed combatY was split into two methods,
 * rangedCombatY(LivingEntity, double) and meleeCombatY(LivingEntity, double).
 * MIN_HEIGHT (3.0, both occurrences) and MAX_HEIGHT (15.0) both moved intact
 * into rangedCombatY - same values, same "groundY + N" shape, just renamed.
 * meleeCombatY is now a genuinely different, ground-hugging formula (built
 * from new 0.5/0.04/0.1 literals, no MIN_HEIGHT/MAX_HEIGHT concept at all) -
 * this is a real base-mod design change (melee NPCs now hug their target's
 * body height instead of clamping between a min/max flight altitude), not
 * something to force these two fields onto. The method target below was
 * updated from "combatY" to "rangedCombatY" accordingly; MIN_HEIGHT/MAX_
 * HEIGHT now only affect ranged flight positioning, matching the base mod's
 * own new behavior.
 */
@Mixin(CultivatorFlightCombatGoal.class)
public abstract class CultivatorFlightCombatGoalMixin {

    // ── selectNextSegment ────────────────────────────────────────────────

    /** RANGED_MIN_DISTANCE: base of `8.0 + rand()*8.0`. */
    @ModifyConstant(method = "selectNextSegment", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 8.0, ordinal = 0), remap = false, require = 0)
    private double configExt$flightRangedMinDistance(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_RANGED_MIN_DISTANCE.get();
    }

    /** RANGED_MAX_DISTANCE: the span of `8.0 + rand()*8.0` (max - min). */
    @ModifyConstant(method = "selectNextSegment", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 8.0, ordinal = 1), remap = false, require = 0)
    private double configExt$flightRangedSpan(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        double min = ExtendedConfig.NPC_AI_FLIGHT_RANGED_MIN_DISTANCE.get();
        double max = ExtendedConfig.NPC_AI_FLIGHT_RANGED_MAX_DISTANCE.get();
        return Math.max(0.0, max - min);
    }

    /** MELEE_DISTANCE. */
    @ModifyConstant(method = "selectNextSegment", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 2.4), remap = false, require = 0)
    private double configExt$flightMeleeDistance(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MELEE_DISTANCE.get();
    }

    /** MIN_SEGMENT_TICKS: base of `9 + rand(17)`. */
    @ModifyConstant(method = "selectNextSegment", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 9), remap = false, require = 0)
    private int configExt$flightMinSegmentTicks(int original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MIN_SEGMENT_TICKS.get();
    }

    /** MAX_SEGMENT_TICKS: the exclusive bound of `9 + rand(17)` (max - min + 1). */
    @ModifyConstant(method = "selectNextSegment", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 17), remap = false, require = 0)
    private int configExt$flightSegmentTickSpan(int original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        int min = ExtendedConfig.NPC_AI_FLIGHT_MIN_SEGMENT_TICKS.get();
        int max = ExtendedConfig.NPC_AI_FLIGHT_MAX_SEGMENT_TICKS.get();
        return Math.max(1, max - min + 1);
    }

    // ── rangedCombatY (was "combatY" pre-0.1.1479; melee's half of the old
    //    method split off into a separate meleeCombatY with no MIN/MAX_HEIGHT
    //    concept - see the class doc above) ─────────────────────────────────

    /** MIN_HEIGHT: both occurrences in rangedCombatY are `groundY + MIN_HEIGHT`. */
    @ModifyConstant(method = "rangedCombatY", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 3.0), remap = false, require = 0)
    private double configExt$flightMinHeight(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MIN_HEIGHT.get();
    }

    /** MAX_HEIGHT: `groundY + MAX_HEIGHT`. */
    @ModifyConstant(method = "rangedCombatY", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 15.0), remap = false, require = 0)
    private double configExt$flightMaxHeight(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MAX_HEIGHT.get();
    }

    // ── m_8037_ (Goal.tick) ──────────────────────────────────────────────

    /** MAX_HORIZONTAL_SPEED. */
    @ModifyConstant(method = "m_8037_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.52), remap = false, require = 0)
    private double configExt$flightMaxHorizontalSpeed(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MAX_HORIZONTAL_SPEED.get();
    }

    /** MAX_VERTICAL_SPEED, upper clamp. */
    @ModifyConstant(method = "m_8037_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.31), remap = false, require = 0)
    private double configExt$flightMaxVerticalSpeed(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_FLIGHT_MAX_VERTICAL_SPEED.get();
    }

    /** MAX_VERTICAL_SPEED, lower clamp (stored as the negated literal). */
    @ModifyConstant(method = "m_8037_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = -0.31), remap = false, require = 0)
    private double configExt$flightMinVerticalSpeed(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return -ExtendedConfig.NPC_AI_FLIGHT_MAX_VERTICAL_SPEED.get();
    }
}
