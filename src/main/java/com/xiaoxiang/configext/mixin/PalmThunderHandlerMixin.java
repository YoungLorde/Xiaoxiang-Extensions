package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.PalmThunderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires PALM_THUNDER_ARMING_TICKS and PALM_THUNDER_CHANNEL_QI_PER_SECOND.
 *
 * Verified via javap -p -c -s against PalmThunderHandler.class (2026-09-01):
 * ARMING_TICKS (int, ConstantValue 40) is inlined as "bipush 40" at exactly
 * 3 call sites, all inside tickChannel() (the preparing-ticks window check
 * and the Math.min cap, twice) - all 3 redirected together (no ordinal)
 * since they're the same concept and must stay in sync; confirmed via
 * whole-class grep that no other "bipush 40" exists anywhere else in the
 * class.
 *
 * CHANNEL_DRAIN_BASE_QI (int, ConstantValue 10) is inlined as "long 10l" at
 * the single qi-drain call site in tickChannel(), gated by a separate fixed
 * "tick % 4 == 0" interval check (CHANNEL_DRAIN_INTERVAL_TICKS = 4, not
 * itself configurable - no config field exists for the drain interval, only
 * for the resulting per-second rate). The config field
 * PALM_THUNDER_CHANNEL_QI_PER_SECOND already correctly represents the
 * EFFECTIVE per-second rate (10 qi every 4 ticks = 10 * 20/4 = 50/sec,
 * matching the pre-existing default of 50 exactly), so the redirect
 * converts back: perDrainAmount = configuredPerSecond / 5 (since 20 ticks/
 * sec / 4 ticks/drain = 5 drains/sec). Default 50/5 = 10, exactly
 * reproducing the vanilla drain amount.
 */
@Mixin(value = PalmThunderHandler.class, remap = false)
public abstract class PalmThunderHandlerMixin {

    @ModifyConstant(method = "tickChannel", constant = @Constant(intValue = 40), remap = false, require = 0)
    private static int configExt$armingTicks(int original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.PALM_THUNDER_ARMING_TICKS.get();
    }

    @ModifyConstant(method = "tickChannel", constant = @Constant(longValue = 10L), remap = false, require = 0)
    private static long configExt$channelDrainBaseQi(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        long perSecond = ExtendedConfig.PALM_THUNDER_CHANNEL_QI_PER_SECOND.get().longValue();
        return Math.max(0L, Math.round(perSecond / 5.0));
    }
}
