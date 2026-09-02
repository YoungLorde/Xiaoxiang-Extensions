package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the parts of "sectLife", "sectDefense", "sectDepartments" and
 * "sectJourney" whose constants could be pinned to a single, uniquely named
 * method inside SectSavedData.
 *
 * SectSavedData is a ~500 KB class and every one of its tuning values is a
 * `static final` primitive that javac has inlined at each use site. Most of
 * those literals recur in many methods and cannot be targeted safely, so only
 * the ones below - each verified as the ONLY occurrence of that literal in a
 * method whose name is unique within the class - are overridden here.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectSavedData.class:
 *
 *   populationFloor(SectRecord)                       0.4  -> POPULATION_FLOOR_INITIAL_SCALE
 *   runPopulationRecruitment(...)                     0.85 -> LOW_POPULATION_RECRUIT_CHANCE
 *   runElderRecruitment(...)                          0.45 -> ELDER_RECRUIT_CHANCE
 *   runServantApprenticeships(...)                    0.35 -> SERVANT_APPRENTICESHIP_CHANCE
 *   departmentRoleTarget(...)                         0.25 -> ROLE_ASSIGNMENT_RATIO
 *   handleSectMemberAttacked(...)                     0.25 -> DEFENSE_CRITICAL_HEALTH_RATIO
 *   runJourneyDataLayerPass(ServerLevel)            36000L -> JOURNEY_QUEUE_FALLBACK_TICKS
 *   shouldNpcTribulationDeath(...)                    0.02d -> PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE
 *   failMemberTribulation(...)                         3.0d -> PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS
 *
 * Note that 0.25 appears in three different methods as three DIFFERENT
 * constants (role ratio, defense health ratio, and a journey progress step);
 * scoping each injector to its own method is what keeps them apart.
 *
 * shouldNpcTribulationDeath/failMemberTribulation added 2026-09-01, verified
 * via javap -p -c -s: shouldNpcTribulationDeath(SectRecord, MemberRecord,
 * long) has a single "ldc2_w double 0.02d" (the dcmpg threshold against a
 * fresh RandomSource.m_188500_() roll - true if the roll is below it, i.e.
 * this literal IS the death chance); failMemberTribulation(...) has a single
 * "ldc2_w double 3.0d" (a direct putfield into MemberRecord.weaknessDays).
 * Both are the sole occurrence of their literal within their own method.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectSavedDataMixin {

    // ── sectLife ─────────────────────────────────────────────────────────

    /** POPULATION_FLOOR_INITIAL_SCALE. */
    @ModifyConstant(method = "populationFloor", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.4), remap = false, require = 0)
    private static double configExt$populationFloorInitialScale(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_POPULATION_FLOOR_INITIAL_SCALE.get();
    }

    /** LOW_POPULATION_RECRUIT_CHANCE. */
    @ModifyConstant(method = "runPopulationRecruitment", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.85), remap = false, require = 0)
    private double configExt$lowPopulationRecruitChance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_LOW_POPULATION_RECRUIT_CHANCE.get();
    }

    /** ELDER_RECRUIT_CHANCE. */
    @ModifyConstant(method = "runElderRecruitment", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.45), remap = false, require = 0)
    private double configExt$elderRecruitChance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_ELDER_RECRUIT_CHANCE.get();
    }

    /** SERVANT_APPRENTICESHIP_CHANCE. */
    @ModifyConstant(method = "runServantApprenticeships", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.35), remap = false, require = 0)
    private double configExt$servantApprenticeshipChance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_SERVANT_APPRENTICESHIP_CHANCE.get();
    }

    // ── sectDepartments ──────────────────────────────────────────────────

    /** ROLE_ASSIGNMENT_RATIO - how much of a department's roster is assigned a role. */
    @ModifyConstant(method = "departmentRoleTarget", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.25), remap = false, require = 0)
    private static double configExt$departmentRoleAssignmentRatio(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEPT_ROLE_ASSIGNMENT_RATIO.get();
    }

    // ── sectDefense ──────────────────────────────────────────────────────

    /** DEFENSE_CRITICAL_HEALTH_RATIO - health fraction that flags a critical incident. */
    @ModifyConstant(method = "handleSectMemberAttacked", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.25), remap = false, require = 0)
    private double configExt$defenseCriticalHealthRatio(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEFENSE_CRITICAL_HEALTH_RATIO.get();
    }

    // ── sectJourney ──────────────────────────────────────────────────────

    /** JOURNEY_QUEUE_FALLBACK_TICKS. */
    @ModifyConstant(method = "runJourneyDataLayerPass", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 36000L), remap = false, require = 0)
    private long configExt$journeyQueueFallbackTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_JOURNEY_QUEUE_FALLBACK_TICKS.get();
    }

    // ── progression (NPC tribulations) ──────────────────────────────────

    /**
     * PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE - probability that a sect NPC
     * dies outright when it fails a tribulation, gated on the same random
     * roll the base mod already makes.
     */
    @ModifyConstant(method = "shouldNpcTribulationDeath", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 0.02), remap = false, require = 0)
    private double configExt$npcTribulationDeathChance(double original) {
        // Gated on ENABLE_PROGRESSION_OVERRIDES, not ENABLE_SECT_OVERRIDES -
        // these two fields live in ExtendedConfig's "progressionRules"
        // section (PROGRESSION_NPC_TRIBULATION_*), not a "sect*" section, so
        // they should track the same toggle a user would expect to control
        // them, even though the mixin implementing them happens to live in
        // this sect-domain class (self-critique fix, 2026-09-01).
        if (!ExtendedConfig.ENABLE_PROGRESSION_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE.get();
    }

    /**
     * PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS - how many in-game days of
     * weakness a sect NPC is given after surviving a failed tribulation.
     */
    @ModifyConstant(method = "failMemberTribulation", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 3.0), remap = false, require = 0)
    private double configExt$npcTribulationWeaknessDays(double original) {
        if (!ExtendedConfig.ENABLE_PROGRESSION_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS.get();
    }

    // ── sect performance (player-reported "hard to run on servers" triage,
    //    2026-09-01) ──────────────────────────────────────────────────────

    /**
     * PERFORMANCE_PLAYER_RADIUS - wires ExtendedConfig's previously-dead
     * "sectPerformancePlayerRadius" (SECT_PERFORMANCE_PLAYER_RADIUS) field to
     * the real gate. Verified via javap -p -c -s: SectSavedData.
     * enqueuePerformance(...) calls hasNearbyPlayer(level, npcPos, 48.0) and
     * returns false (skips queueing the visible in-world "performance" -
     * pathfinding, animation sequence, corpse spawn, etc. for a sect member's
     * death/recruitment/expedition/tribulation events) whenever no player is
     * within that radius. This exact "ldc2_w 48.0d -> hasNearbyPlayer" pair
     * recurs, unchanged, in 5 more private methods (verified each
     * individually against the real bytecode - not assumed from the name
     * match alone): queueExpeditionReturnPerformance (twice in the same
     * method - both covered by this single @ModifyConstant, no ordinal
     * needed since both should track the same radius), queueRecruitBReturn
     * Performance, startTerminalDeathPerformance,
     * signalLoadedCultivationProgress, and tryRecruitLoadedLoneCultivator.
     * All 6 are wired below with one @ModifyConstant apiece.
     *
     * This does NOT touch the underlying sect simulation (breakthroughs,
     * tasks, deaths, chunk-loading for journeying members via
     * SectJourneyChunkTickets - that keeps running via the base mod's own
     * distant-catch-up system regardless of this radius) - only whether a
     * given event is worth spending entity/pathfinding/animation work on
     * because a player is actually close enough to see it. Lowering it is a
     * real, safe lever for a server admin to cut down that visible-work cost
     * without changing sect logic itself.
     *
     * SECT_PERFORMANCE_PLAYER_RADIUS is an IntValue (kept for backward
     * compatibility with any existing config file) but the real target is a
     * double - converted at the call site, same pattern used elsewhere in
     * this project (see QI_SHIELD_QI_PER_DAMAGE in ExtendedConfig).
     *
     * Note: ExtendedConfig also defines SECT_FULL_SIMULATION_PLAYER_RADIUS
     * (mapping to SectSavedData.FULL_SIMULATION_PLAYER_RADIUS = 192,
     * verified via javap -v's ConstantValue attribute) - searched every
     * class under cultivation/entity, cultivation/event, cultivation/
     * cultivation/sect, and cultivation/worldgen for the literal 192 via
     * javap and found ZERO consumers anywhere in the base mod. That field
     * appears to be genuinely dead in the original mod itself (not just in
     * this config mod), so there is nothing real to wire it to - left
     * unwired rather than fabricating a mixin against a target that does not
     * exist, and flagged to the user directly (see CONFIG_AUDIT.md).
     */
    @ModifyConstant(method = "enqueuePerformance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$enqueuePerformance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "queueExpeditionReturnPerformance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$queueExpeditionReturnPerformance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "queueRecruitBReturnPerformance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$queueRecruitBReturnPerformance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "startTerminalDeathPerformance", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$startTerminalDeathPerformance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "signalLoadedCultivationProgress", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$signalLoadedCultivationProgress(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "tryRecruitLoadedLoneCultivator", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 48.0), remap = false, require = 0)
    private double configExt$performanceRadius$tryRecruitLoadedLoneCultivator(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_PLAYER_RADIUS.get().doubleValue();
    }
}
