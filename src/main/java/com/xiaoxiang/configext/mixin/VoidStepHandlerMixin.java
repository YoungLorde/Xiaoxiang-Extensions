package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.VoidStepHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires VOID_STEP_AIR_JUMP_QI_COST and VOID_STEP_DASH_QI_COST.
 *
 * Verified via javap -p -c -s against VoidStepHandler.class (2026-09-01):
 * the real JUMP_3_QI_COST field (int, ConstantValue 15) is inlined as
 * "long 15l" (getCurrentQi()/setCurrentQi() are long-typed) at exactly 2
 * call sites in handleJump3Blocks() - the qi-sufficiency check and the
 * deduction - both redirected together (no ordinal) since they represent
 * the same value and must stay in sync. handleDash() similarly inlines its
 * own (unnamed at the field level, but equally real) dash qi cost as
 * "long 60l" at exactly 2 call sites (check + deduct), confirmed the sole
 * occurrences of each literal in their respective methods.
 *
 * VOID_STEP_SLOW_FALL_QI_COST is NOT wired: a full disassembly of this
 * class found only 3 getCurrentQi()/setCurrentQi() pairs total, fully
 * accounted for by handleJump3Blocks (air jump) and handleDash (dash).
 * applyAutoSlowFall() - the method that actually implements auto slow-fall
 * - contains no qi check or deduction at all; the base mod's auto slow-fall
 * is a free passive safety feature, not a qi-costing ability. There is no
 * real consumer for this field to wire to; left honestly unwired in
 * ExtendedConfig.java rather than guessed at.
 */
@Mixin(value = VoidStepHandler.class, remap = false)
public abstract class VoidStepHandlerMixin {

    @ModifyConstant(method = "handleJump3Blocks", constant = @Constant(longValue = 15L), remap = false, require = 0)
    private static long configExt$airJumpQiCost(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_STEP_AIR_JUMP_QI_COST.get().longValue();
    }

    @ModifyConstant(method = "handleDash", constant = @Constant(longValue = 60L), remap = false, require = 0)
    private static long configExt$dashQiCost(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.VOID_STEP_DASH_QI_COST.get().longValue();
    }
}
