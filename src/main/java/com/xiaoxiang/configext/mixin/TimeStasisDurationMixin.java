package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires TRIAL_INNER_WORLD_TIME_STASIS_DURATION to its real backing field.
 *
 * Verified via javap -p -c -s against
 * com/xiaoxiang/cultivation/event/TimeStasisHandler.class (2026-09-01):
 * despite the config field's "TRIAL_INNER_WORLD_" prefix, its real
 * counterpart is TimeStasisHandler.DURATION_TICKS (int, ConstantValue 600),
 * NOT anything on InnerWorldTrialManager (that class has no such field at
 * all - a previous version of InnerWorldTrialManagerMixin targeted it there
 * and was silently inert; see that mixin's class doc).
 *
 * IMPORTANT: TimeStasisHandler is a general-purpose ability/mechanic
 * handler, not exclusive to the Inner World Trial - onChargeStarted(),
 * castSingleOrRelease(), releaseStoppedEntity(), and castDomain() are all
 * entry points for casting/releasing the Time Stasis effect wherever it's
 * used in the mod, not just inside a trial. The literal 600 is the sole
 * occurrence of that value in each of these 4 methods (confirmed
 * individually), so one handler per method with no ordinal is correct.
 * Wiring this config field changes Time Stasis duration everywhere the
 * ability is used, despite its name suggesting trial-only scope - documented
 * here rather than silently narrowed to something the bytecode doesn't
 * support.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.event.TimeStasisHandler", remap = false)
public abstract class TimeStasisDurationMixin {

    @ModifyConstant(
            method = {"onChargeStarted", "castSingleOrRelease", "releaseStoppedEntity", "castDomain"},
            constant = @Constant(intValue = 600), remap = false, require = 0)
    private static int configExt$timeStasisDurationTicks(int original) {
        if (!ExtendedConfig.ENABLE_TRIAL_OVERRIDES.get()) return original;
        return ExtendedConfig.TRIAL_INNER_WORLD_TIME_STASIS_DURATION.get();
    }
}
