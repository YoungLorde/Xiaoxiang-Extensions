package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_DEPARTMENT_FIRST_SHIFT_START/END and SECT_DEPARTMENT_SECOND_
 * SHIFT_START/END.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 * departmentShiftScheduled(UUID member, long dayTime) assigns each member to
 * shift 0 or 1 via Math.floorMod(member.hashCode(), 2), then returns:
 *
 *   shift 0: dayTime >= DEPARTMENT_FIRST_SHIFT_START  (1000)
 *         && dayTime <  DEPARTMENT_FIRST_SHIFT_END    (6000)
 *   shift 1: dayTime >= DEPARTMENT_SECOND_SHIFT_START (6000)
 *         && dayTime <  DEPARTMENT_SECOND_SHIFT_END   (11000)
 *
 * All 4 fields are `long` (compile-time-inlined). FIRST_SHIFT_END and
 * SECOND_SHIFT_START share the same literal value (6000) and both occur in
 * this one method, so `ordinal` disambiguates: ordinal 0 is the first 6000
 * encountered in bytecode order (the shift-0 branch, FIRST_SHIFT_END),
 * ordinal 1 is the second (the shift-1 branch, SECOND_SHIFT_START).
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectDepartmentShiftMixin {

    @ModifyConstant(method = "departmentShiftScheduled", constant = @Constant(longValue = 1000L), remap = false, require = 0)
    private static long configExt$firstShiftStart(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEPARTMENT_FIRST_SHIFT_START.get();
    }

    @ModifyConstant(method = "departmentShiftScheduled", constant = @Constant(longValue = 6000L, ordinal = 0), remap = false, require = 0)
    private static long configExt$firstShiftEnd(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEPARTMENT_FIRST_SHIFT_END.get();
    }

    @ModifyConstant(method = "departmentShiftScheduled", constant = @Constant(longValue = 6000L, ordinal = 1), remap = false, require = 0)
    private static long configExt$secondShiftStart(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEPARTMENT_SECOND_SHIFT_START.get();
    }

    @ModifyConstant(method = "departmentShiftScheduled", constant = @Constant(longValue = 11000L), remap = false, require = 0)
    private static long configExt$secondShiftEnd(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEPARTMENT_SECOND_SHIFT_END.get();
    }
}
