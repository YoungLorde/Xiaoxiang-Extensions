package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_WAREHOUSE_SLOT_LIMIT, SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT,
 * SECT_DISTANT_CATCH_UP_DAY_CAP, SECT_PERFORMANCE_QUEUE_LIMIT, and
 * SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT.
 *
 * All verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 *
 *   trimWarehouse(List&lt;ItemStack&gt;) compares list.size() against the
 *   literal 324 twice in the same method (an early-exit check, then the
 *   actual trim-loop condition) - both are the same concept, so one
 *   handler with no ordinal covers both occurrences. Config's default of
 *   54 never matched - corrected to 324.
 *
 *   insertPersonalStackWithOverflow(List&lt;ItemStack&gt;, ItemStack) stops
 *   adding new personal-inventory stacks once list.size() >= 18 (a single
 *   bipush 18). Config's default of 27 never matched - corrected to 18.
 *
 *   catchUpDistantSect(ServerLevel, SectRecord, long) clamps the simulated
 *   catch-up window to a literal 300L (long), used three times in the same
 *   method for the same concept (the clamp itself, and twice more computing
 *   the "retrospective" days beyond the cap) - one handler with no ordinal
 *   covers all three. Config's default of 7 never matched - corrected to
 *   300.
 *
 *   enqueuePerformance(...) caps each sect's performance-request deque at
 *   a literal bipush 8, evicting the oldest request once full. Config's
 *   default of 8 already matched exactly - wired as-is, no correction
 *   needed.
 *
 *   admitPhysicalJourney(...) gates a new physical journey on
 *   physicalJourneyCount(sectRecord) &lt; 3 (iconst_3). Config's default of
 *   2 never matched - corrected to 3.
 *
 * SECT_PERFORMANCE_TIMEOUT_TICKS is NOT wired here: SectPerformanceRequest
 * carries no start-time/deadline field at all, and no dedicated
 * performance-timeout method or entity-side goal class was located this
 * session. Left unresearched rather than guessed at.
 *
 * UPDATED 2026-09-02 for base mod 0.1.1479: re-verified against the new jar
 * (javap -v -p -c). Two real changes found:
 *
 *   (1) trimWarehouse was deleted entirely; its logic was folded into a new
 *   addToWarehouse(SectRecord, List&lt;ItemStack&gt;) method (called from a new
 *   insertWarehouseStack/sortWarehouseStacks pair). The base mod even added a
 *   named `public static final int SECT_WAREHOUSE_SLOT_LIMIT` constant (=324,
 *   confirmed via its ConstantValue attribute) - but since it's a compile-time
 *   constant, javac still inlines the literal 324 at the one remaining use
 *   site inside addToWarehouse (a single `sipush 324`, not two occurrences
 *   like the old trimWarehouse had) rather than making it a real redirectable
 *   field read. The @ModifyConstant method target below was updated from
 *   "trimWarehouse" to "addToWarehouse" accordingly; the 324 value itself is
 *   unchanged and the single-occurrence match still needs no ordinal.
 *
 *   (2) admitPhysicalJourney(...) gained a second, unrelated iconst_3 later
 *   in the same method: a new failure-path log call
 *   (`LOGGER.error("...", new Object[3])`) builds a 3-element varargs array
 *   via `anewarray` sized with the same iconst_3 opcode as the gate check.
 *   Without an ordinal, @ModifyConstant would now also resize that logging
 *   array to SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT's configured value, which
 *   would throw an ArrayIndexOutOfBoundsException on the log call whenever
 *   that value is set below 3. Fixed by pinning `ordinal = 0` so only the
 *   first iconst_3 (the actual physicalJourneyCount(...) &lt; 3 gate) is
 *   touched. insertPersonalStackWithOverflow, catchUpDistantSect, and
 *   enqueuePerformance were all re-checked too and are byte-for-byte
 *   unchanged (same single/triple occurrence counts as before) - no fix
 *   needed for those three.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectOperationsLimitsMixin {

    @ModifyConstant(method = "addToWarehouse", constant = @Constant(intValue = 324), remap = false, require = 0)
    private static int configExt$warehouseSlotLimit(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_WAREHOUSE_SLOT_LIMIT.get();
    }

    @ModifyConstant(method = "insertPersonalStackWithOverflow", constant = @Constant(intValue = 18), remap = false, require = 0)
    private static int configExt$memberPersonalInventorySlotLimit(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT.get();
    }

    @ModifyConstant(method = "catchUpDistantSect", constant = @Constant(longValue = 300L), remap = false, require = 0)
    private long configExt$distantCatchUpDayCap(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int days = ExtendedConfig.SECT_DISTANT_CATCH_UP_DAY_CAP.get();
        return days <= 0 ? original : (long) days;
    }

    @ModifyConstant(method = "enqueuePerformance", constant = @Constant(intValue = 8), remap = false, require = 0)
    private int configExt$performanceQueueLimit(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PERFORMANCE_QUEUE_LIMIT.get();
    }

    @ModifyConstant(method = "admitPhysicalJourney", constant = @Constant(intValue = 3, ordinal = 0), remap = false, require = 0)
    private int configExt$maxPhysicalJourneysPerSect(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT.get();
    }
}
