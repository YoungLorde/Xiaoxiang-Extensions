package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_TASK_EXPEDITION_MIN_DAYS / MAX_DAYS and SECT_TASK_JOURNEY_MIN_
 * TIMEOUT_TICKS / MAX_TIMEOUT_TICKS.
 *
 * Verified via javap -p -c -s against SectTaskMarketRules.class (2026-09-01):
 *
 *   expeditionWorkDays(ItemStack, String tier)
 *     null/empty stack -> returns 0.5 directly (EXPEDITION_MIN_DAYS)
 *     otherwise -> 0.5 + 1.5 * max(countRatio, tierRatio)
 *       where 0.5 = EXPEDITION_MIN_DAYS (base) and
 *             1.5 = EXPEDITION_MAX_DAYS - EXPEDITION_MIN_DAYS (span)
 *     Both 0.5 occurrences share the same meaning (the floor value), so one
 *     handler without an ordinal restriction correctly covers both.
 *
 *   journeyTimeoutTicks(ItemStack, String tier)
 *     12000 + round(24000.0 * severity)
 *       where 12000 = JOURNEY_MIN_TIMEOUT_TICKS (base) and
 *             24000.0 = JOURNEY_MAX_TIMEOUT_TICKS - JOURNEY_MIN_TIMEOUT_TICKS
 *             (span)
 *
 * SECT_TASK_MAX_REQUIRED_COUNT, SECT_TASK_MAX_ESCROW_STACKS, and
 * SECT_TASK_MAX_SYSTEM_PURCHASE_TASKS all match real fields declared on
 * SectTaskMarketRules (MAX_REQUIRED_COUNT=256, MAX_ESCROW_STACKS=8,
 * MAX_SYSTEM_PURCHASE_TASKS=8), but none of their literal values appear
 * anywhere inside SectTaskMarketRules's own methods - they must be consumed
 * by a caller (most likely SectSavedData's task-creation/escrow logic).
 * That caller was not isolated this session; those 3 fields remain unwired
 * (their config defaults were still corrected to match the verified real
 * values - see ExtendedConfig.java).
 *
 * SECT_TASK_MAX_SYSTEM_PURCHASES (a DIFFERENT field from
 * SECT_TASK_MAX_SYSTEM_PURCHASE_TASKS, both commented "Max system purchase
 * tasks", both defaulting to 8) is a genuine duplicate/orphan - there is
 * only one real MAX_SYSTEM_PURCHASE_TASKS field on the target class, so
 * SECT_TASK_MAX_SYSTEM_PURCHASES has no possible backing field. Left
 * unwired and undocumented-as-fixable, same treatment as the
 * PROGRESSION_NPC_TRIBULATION_FAILURE_WEAKNESS_DAYS duplicate found earlier
 * this audit.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectTaskMarketRules", remap = false)
public abstract class SectTaskMarketMixin {

    @ModifyConstant(method = "expeditionWorkDays", constant = @Constant(doubleValue = 0.5), remap = false, require = 0)
    private static double configExt$expeditionMinDays(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_TASK_EXPEDITION_MIN_DAYS.get();
    }

    @ModifyConstant(method = "expeditionWorkDays", constant = @Constant(doubleValue = 1.5), remap = false, require = 0)
    private static double configExt$expeditionDaySpan(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        double span = ExtendedConfig.SECT_TASK_EXPEDITION_MAX_DAYS.get() - ExtendedConfig.SECT_TASK_EXPEDITION_MIN_DAYS.get();
        return span >= 0.0 ? span : 0.0;
    }

    @ModifyConstant(method = "journeyTimeoutTicks", constant = @Constant(intValue = 12000), remap = false, require = 0)
    private static int configExt$journeyMinTimeoutTicks(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS.get();
    }

    @ModifyConstant(method = "journeyTimeoutTicks", constant = @Constant(doubleValue = 24000.0), remap = false, require = 0)
    private static double configExt$journeyTimeoutSpan(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int span = ExtendedConfig.SECT_TASK_JOURNEY_MAX_TIMEOUT_TICKS.get() - ExtendedConfig.SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS.get();
        return span >= 0 ? (double) span : 0.0;
    }
}
