package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.LifespanHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires LIFESPAN_AGE_PER_DAY.
 *
 * Verified via javap -p -c -s against LifespanHandler.class (2026-09-01):
 * advanceBoneAge(ServerPlayer, CultivationData, long elapsedTicks) computes
 * "dconst_1 * (double) elapsedTicks / 24000.0" and adds that to the player's
 * bone age - the literal 1.0d ("dconst_1") IS the configurable "age per
 * (in-game) day" rate, and it is the only occurrence of that constant in the
 * method.
 *
 * LIFESPAN_AGE_PER_DAY_MEDITATING has NO corresponding target: an exhaustive
 * search of this class (and LifespanHelper) for any "meditat"-related logic
 * found nothing - the base mod does not currently apply a different aging
 * rate while meditating. That field is documented as unwired in
 * ExtendedConfig.java rather than silently left with a misleading "wired"
 * claim.
 */
@Mixin(value = LifespanHandler.class, remap = false)
public abstract class LifespanHandlerMixin {

    @ModifyConstant(method = "advanceBoneAge", constant = @Constant(doubleValue = 1.0), remap = false, require = 0)
    private static double configExt$agePerDay(double original) {
        if (!ExtendedConfig.ENABLE_LIFESPAN_OVERRIDES.get()) return original;
        return ExtendedConfig.LIFESPAN_AGE_PER_DAY.get();
    }
}
