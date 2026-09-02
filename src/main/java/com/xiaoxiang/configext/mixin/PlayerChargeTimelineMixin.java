package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.spell.PlayerChargeTimeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires GLACIER_BURIAL_BASE_QI_PER_TICK, and keeps fullChargeTicks(Spell) -
 * a UI/progress-bar helper - consistent with the real per-spell channel
 * durations wired elsewhere (SEA_CHANNEL_TICKS in
 * Blood07FormationHandlerMixin, VOID_ESCAPE_CHARGE_TICKS in
 * VoidEscapeHandlerMixin, PALM_THUNDER_ARMING_TICKS in
 * PalmThunderHandlerMixin).
 *
 * Verified via javap -p -c -s against PlayerChargeTimeline.class
 * (2026-09-01):
 *
 * - glacierBurialQiCostAtTick(int) is an exponential ramp: starts at
 *   "long 5l" and compounds by 1.35x per elapsed second, floored at
 *   Math.max("long 5l", computed) at the end. Both occurrences of
 *   "long 5l" share the same constant-pool index and both represent
 *   GLACIER_BURIAL_BASE_QI_PER_TICK (the ramp's starting value doubles as
 *   its floor) - confirmed exactly 2 occurrences within this method.
 *   IMPORTANT: a private field BASE_DRAIN also happens to equal 5 and is
 *   used (as a third, unrelated "long 5l") inside genericDrain() - a
 *   completely different method for a completely different mechanic.
 *   Scoping this redirect to method = "glacierBurialQiCostAtTick"
 *   specifically (not "*") is what keeps this safe; a class-wide redirect
 *   would have silently corrupted genericDrain() too.
 * - fullChargeTicks(Spell) is a small switch that returns each spell's
 *   full charge-up duration for UI/progress-bar purposes, separate from
 *   (and duplicating) the constants the real gameplay handlers use.
 *   SEA_OF_OVERWHELMING_BLOOD returns "sipush 200" directly (same value as
 *   Blood07FormationHandler's real CHANNEL_TICKS). VOID_ESCAPE returns
 *   "bipush 100" directly (same value as VoidEscapeHandler's real charge-
 *   tick threshold). PALM_THUNDER returns "bipush 46" - not 40
 *   (ARMING_TICKS) directly, but 40 + PALM_THUNDER_TAP_TICKS(5) + 1, i.e.
 *   arming-then-tap-release plus one tick, folded to a single literal by
 *   javac. All 3 confirmed as the sole occurrence of their literal within
 *   this method. Without this, changing PALM_THUNDER_ARMING_TICKS or
 *   SEA_CHANNEL_TICKS or VOID_ESCAPE_CHARGE_TICKS would leave any UI that
 *   calls fullChargeTicks() showing the old vanilla duration while the
 *   actual channel now takes a different amount of time - wired here to
 *   keep the two in sync.
 */
@Mixin(value = PlayerChargeTimeline.class, remap = false)
public abstract class PlayerChargeTimelineMixin {

    @ModifyConstant(method = "glacierBurialQiCostAtTick", constant = @Constant(longValue = 5L), remap = false, require = 0)
    private static long configExt$glacierBurialBaseQiPerTick(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.GLACIER_BURIAL_BASE_QI_PER_TICK.get().longValue();
    }

    @ModifyConstant(method = "fullChargeTicks", constant = @Constant(intValue = 200), remap = false, require = 0)
    private static int configExt$fullChargeSeaTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.SEA_CHANNEL_TICKS.get();
    }

    @ModifyConstant(method = "fullChargeTicks", constant = @Constant(intValue = 100), remap = false, require = 0)
    private static int configExt$fullChargeVoidEscapeTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_ESCAPE_CHARGE_TICKS.get();
    }

    @ModifyConstant(method = "fullChargeTicks", constant = @Constant(intValue = 46), remap = false, require = 0)
    private static int configExt$fullChargePalmThunderTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        // 46 = ARMING_TICKS(40) + PALM_THUNDER_TAP_TICKS(5, unconfigured) + 1.
        return ExtendedConfig.PALM_THUNDER_ARMING_TICKS.get() + 5 + 1;
    }
}
