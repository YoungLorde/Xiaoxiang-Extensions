package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the "sectSchedule" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectDailyScheduleRules.class.
 * phaseAt(long) is the single decision point for the whole daily schedule:
 *
 *   long t = Math.floorMod(dayTime, 24000L);   // DAY_TICKS
 *   if (t < 1000L)  return MORNING_ASSEMBLY;   // MORNING_END_EXCLUSIVE
 *   if (t < 12000L) return DAY_DUTIES;         // NIGHT_START
 *   return NIGHT_MEDITATION;
 *
 * Each of the three literals occurs exactly once in phaseAt, so all three are
 * unambiguous. scheduledActivity() delegates to phaseAt, so overriding here
 * covers the whole schedule.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectDailyScheduleRules", remap = false)
public abstract class SectDailyScheduleRulesMixin {

    /** DAY_TICKS - the modulus of the sect day. */
    @ModifyConstant(method = "phaseAt", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 24000L), remap = false, require = 0)
    private static long configExt$dayTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_SCHEDULE_DAY_TICKS.get();
    }

    /** MORNING_END_EXCLUSIVE - end of the morning assembly phase. */
    @ModifyConstant(method = "phaseAt", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 1000L), remap = false, require = 0)
    private static long configExt$morningEndExclusive(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_SCHEDULE_MORNING_END_EXCLUSIVE.get();
    }

    /** NIGHT_START - start of the night meditation phase. */
    @ModifyConstant(method = "phaseAt", constant = @org.spongepowered.asm.mixin.injection.Constant(longValue = 12000L), remap = false, require = 0)
    private static long configExt$nightStart(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_SCHEDULE_NIGHT_START.get();
    }
}
