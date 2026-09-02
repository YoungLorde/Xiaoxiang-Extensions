package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires PROGRESSION_GENDER_EDITS_DEFAULT.
 *
 * The stat-point-effectiveness-from-difficulty-presets feature this mixin
 * used to be reserved for was removed from the base mod, so it was a no-op
 * placeholder until now.
 *
 * Verified via javap -p -c -s against CultivationData.class (2026-09-01):
 * genderEditsLeft has exactly two places that initialize it to the literal 5:
 *
 *   1. The no-arg constructor: a plain "iconst_5; putfield genderEditsLeft"
 *      pair - the only iconst_5 anywhere in that constructor's bytecode, so
 *      no ordinal is needed.
 *
 *   2. deserializeNBT(CompoundTag): an "iconst_5" fallback used only when the
 *      tag does NOT contain a "genderEditsLeft" key (i.e. loading an old save
 *      that predates this field). "iconst_5" appears TWICE in this method -
 *      once here (ordinal 0, offset 330) and once far later (offset 1420) as
 *      an unrelated Math.min/Math.max clamp bound for a completely different
 *      field, bloodKillIntentStacks. Only ordinal 0 is targeted below; the
 *      other is deliberately left untouched.
 */
@Mixin(CultivationData.class)
public abstract class CultivationDataMixin {

    /** New CultivationData instances (new players/NPCs). */
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 5), remap = false, require = 0)
    private int configExt$genderEditsDefaultCtor(int original) {
        if (!ExtendedConfig.ENABLE_PROGRESSION_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_GENDER_EDITS_DEFAULT.get();
    }

    /** Old saves loaded from NBT that predate the genderEditsLeft field. */
    @ModifyConstant(method = "deserializeNBT", constant = @Constant(intValue = 5, ordinal = 0), remap = false, require = 0)
    private int configExt$genderEditsDefaultNbtFallback(int original) {
        if (!ExtendedConfig.ENABLE_PROGRESSION_OVERRIDES.get()) return original;
        return ExtendedConfig.PROGRESSION_GENDER_EDITS_DEFAULT.get();
    }
}
