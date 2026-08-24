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
 *
 * Note that 0.25 appears in three different methods as three DIFFERENT
 * constants (role ratio, defense health ratio, and a journey progress step);
 * scoping each injector to its own method is what keeps them apart.
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
}
