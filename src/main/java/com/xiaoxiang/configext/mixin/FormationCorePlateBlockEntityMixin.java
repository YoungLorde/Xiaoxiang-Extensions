package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.formation.FormationCorePlateBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the 12 "formationCore" timing/harvest/radius fields not already
 * covered by FormationCoreTierMixin (maxQi) or the FormationType* mixins
 * (per-tier multipliers) - all confirmed live via javap -p -c -s against
 * com/xiaoxiang/cultivation/block/formation/FormationCorePlateBlockEntity.class.
 *
 * IMPORTANT: the config defaults for 9 of these 12 fields previously did NOT
 * match the base mod's real hardcoded values (confirmed via javap -constants
 * on this class's own private static final int fields) - since none of
 * these 12 were wired before this pass (there was no prior live behavior to
 * preserve), ExtendedConfig.java's defaults were corrected to the real
 * values rather than shipped as a silent rebalance. See that file's comments
 * on each field for the exact before/after numbers.
 *
 * Verified target sites (method -> literal -> occurrence count), each
 * independently re-confirmed by re-reading the actual bytecode rather than
 * trusting a single pass:
 *
 *   serverTick(): "long 200l" (1x, gates configureGeneratedArrayCoreIfReady,
 *     IMMORTAL-tier only) and "long 10l" (1x, gates
 *     revalidateActiveFormationsAgainstFlags + the call into
 *     applyActiveFormationEffects).
 *   applyActiveFormationEffects(): a SECOND "long 10l" (1x, gates the
 *     REJUVENATION/FLIGHT_BAN/MAZE/SECT_PROTECTION block specifically), and
 *     "long 20l" TWICE - ordinal 0 gates accelerateGrowth, ordinal 1 gates
 *     maintainStorageCore. These need different override values, so unlike
 *     most repeated literals in this codebase they DO require ordinals.
 *   deferActiveFlagValidation(): "long 100l" (1x).
 *   applyFarmHarvest(): "sipush 4096" / "sipush 1024" / "sipush 16384"
 *     (1x each), composing Math.min(16384, Math.max(4096, batchSize*1024)).
 *   applyRejuvenation() / applyFlightBan(): "bipush 16" (1x each, the
 *     MobEffectInstance duration argument).
 *   applyMaze(): "bipush 16" appears THREE times, not two as a first pass
 *     might assume - ordinals 0 and 1 are the two MobEffectInstance
 *     durations, but ordinal 2 is an unrelated maze-grid loop bound
 *     (confirmed by the surrounding iload/istore loop-counter bytecode).
 *     Only ordinals 0 and 1 are touched here; touching ordinal 2 would have
 *     corrupted maze generation, so this required explicit per-ordinal
 *     targeting rather than a blanket method-wide constant patch.
 *   clampFlagEffectRadius(int): "iconst_1" (min bound) and "sipush 255"
 *     (max bound), both 1x, the shared radius-clamp helper.
 *   radiusForFlag(BlockPos): a SEPARATE, independent "sipush 255" fallback
 *     clamp (1x) plus "bipush 8" (1x, the "no radius set yet" default).
 */
@Mixin(value = FormationCorePlateBlockEntity.class, remap = false)
public abstract class FormationCorePlateBlockEntityMixin {

    @ModifyConstant(method = "serverTick", constant = @Constant(longValue = 200L), require = 0)
    private long configExt$arraySyncInterval(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_GENERATED_ARRAY_SYNC_INTERVAL_TICKS.get();
    }

    @ModifyConstant(method = "serverTick", constant = @Constant(longValue = 10L), require = 0)
    private long configExt$effectIntervalServerTick(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_INTERVAL_TICKS.get();
    }

    @ModifyConstant(method = "applyActiveFormationEffects", constant = @Constant(longValue = 10L), require = 0)
    private long configExt$effectIntervalActiveEffects(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_INTERVAL_TICKS.get();
    }

    @ModifyConstant(method = "applyActiveFormationEffects",
            constant = @Constant(longValue = 20L, ordinal = 0), require = 0)
    private long configExt$growthTickInterval(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_GROWTH_TICK_INTERVAL_TICKS.get();
    }

    @ModifyConstant(method = "applyActiveFormationEffects",
            constant = @Constant(longValue = 20L, ordinal = 1), require = 0)
    private long configExt$storageCoreInterval(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_STORAGE_CORE_INTERVAL_TICKS.get();
    }

    @ModifyConstant(method = "deferActiveFlagValidation", constant = @Constant(longValue = 100L), require = 0)
    private long configExt$reloadFlagValidationGrace(long original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_RELOAD_FLAG_VALIDATION_GRACE_TICKS.get();
    }

    @ModifyConstant(method = "applyFarmHarvest", constant = @Constant(intValue = 4096), require = 0)
    private int configExt$farmHarvestMinChecks(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_FARM_HARVEST_MIN_CHECKS.get();
    }

    @ModifyConstant(method = "applyFarmHarvest", constant = @Constant(intValue = 1024), require = 0)
    private int configExt$farmHarvestChecksPerTarget(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_FARM_HARVEST_CHECKS_PER_TARGET.get();
    }

    @ModifyConstant(method = "applyFarmHarvest", constant = @Constant(intValue = 16384), require = 0)
    private int configExt$farmHarvestMaxChecks(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_FARM_HARVEST_MAX_CHECKS.get();
    }

    @ModifyConstant(method = "applyRejuvenation", constant = @Constant(intValue = 16), require = 0)
    private int configExt$rejuvenationDuration(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_DURATION_TICKS.get();
    }

    @ModifyConstant(method = "applyFlightBan", constant = @Constant(intValue = 16), require = 0)
    private int configExt$flightBanDuration(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_DURATION_TICKS.get();
    }

    // applyMaze() has a THIRD, unrelated occurrence of the literal 16 (a
    // maze-grid loop bound) - ordinals 0/1 only, ordinal 2 deliberately left
    // untouched. See class doc above.
    @ModifyConstant(method = "applyMaze", constant = @Constant(intValue = 16, ordinal = 0), require = 0)
    private int configExt$mazeDurationA(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_DURATION_TICKS.get();
    }

    @ModifyConstant(method = "applyMaze", constant = @Constant(intValue = 16, ordinal = 1), require = 0)
    private int configExt$mazeDurationB(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_EFFECT_DURATION_TICKS.get();
    }

    @ModifyConstant(method = "clampFlagEffectRadius", constant = @Constant(intValue = 1), require = 0)
    private static int configExt$minFlagRadius(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_MIN_FLAG_EFFECT_RADIUS.get();
    }

    @ModifyConstant(method = "clampFlagEffectRadius", constant = @Constant(intValue = 255), require = 0)
    private static int configExt$maxFlagRadiusClamp(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_MAX_FLAG_EFFECT_RADIUS.get();
    }

    @ModifyConstant(method = "radiusForFlag", constant = @Constant(intValue = 255), require = 0)
    private int configExt$maxFlagRadiusFallback(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_MAX_FLAG_EFFECT_RADIUS.get();
    }

    @ModifyConstant(method = "radiusForFlag", constant = @Constant(intValue = 8), require = 0)
    private int configExt$defaultFlagRadius(int original) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) return original;
        return ExtendedConfig.FORMATION_DEFAULT_FLAG_EFFECT_RADIUS.get();
    }
}
