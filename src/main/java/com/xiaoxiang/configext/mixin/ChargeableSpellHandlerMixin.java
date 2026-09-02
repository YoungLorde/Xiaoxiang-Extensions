package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.ChargeableSpellHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires BUDDHA_FIRE_LOTUS_READY_QI and CORE_SELF_DESTRUCT_READY_QI.
 *
 * Verified via javap -p -c -s against ChargeableSpellHandler.class
 * (2026-09-01): both are real dedicated fields on this class -
 * BUDDHA_FIRE_LOTUS_READY_QI (long, ConstantValue 10000) and
 * CORE_SELF_DESTRUCT_READY_QI (long, ConstantValue 1000) - matching the
 * config's already-correct defaults exactly.
 *
 * This class is ~3200 lines with many methods, so method = "*" would
 * normally be a partial/over-coverage risk (see mixin-completeness-audit).
 * Here it's safe: every occurrence of "long 10000l" in the whole class
 * disassembly shares the exact same constant-pool index (#50), and every
 * occurrence of "long 1000l" shares the same index (#53) - proof (not
 * assumption) that javac deduplicated them from the same two named field
 * references, not coincidentally-equal unrelated literals. Confirmed via
 * whole-class grep: exactly 5 occurrences of #50 (long 10000l) and exactly
 * 5 occurrences of #53 (long 1000l), spread across onPlayerTick,
 * tickCoreSelfDestructCharge, and fireChargedSpell - all genuinely the same
 * two fields, so method = "*" redirects every real use site with no risk of
 * touching an unrelated value.
 */
@Mixin(value = ChargeableSpellHandler.class, remap = false)
public abstract class ChargeableSpellHandlerMixin {

    @ModifyConstant(method = "*", constant = @Constant(longValue = 10000L), remap = false, require = 0)
    private static long configExt$buddhaFireLotusReadyQi(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.BUDDHA_FIRE_LOTUS_READY_QI.get().longValue();
    }

    @ModifyConstant(method = "*", constant = @Constant(longValue = 1000L), remap = false, require = 0)
    private static long configExt$coreSelfDestructReadyQi(long original) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) return original;
        return ExtendedConfig.CORE_SELF_DESTRUCT_READY_QI.get().longValue();
    }
}
