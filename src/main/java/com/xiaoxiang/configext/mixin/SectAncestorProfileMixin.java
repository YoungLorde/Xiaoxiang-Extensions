package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Wires SECT_PROFILE_MAX_POWER_SCORE, SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN,
 * the 5 SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_* fields, and
 * SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE.
 *
 * Verified via javap -p -c -s against GeneratedSectCultivationProfile.class
 * (2026-09-01):
 *
 *   forRole(sectId, powerScore, role)
 *     clampedPower = clamp(powerScore, 0, 4)     - the 4 is MAX_POWER_SCORE
 *     roleOffset = min(20, masterProg - MIN_CULTIVATOR_PROGRESS) - the 20 is
 *       MAX_ORDINARY_ROLE_SPAN
 *
 *   ancestorProfile(seed, clampedPower, masterProg)
 *     unitInterval(mix) < ANCESTOR_IMMORTAL_CHANCE_BY_POWER[clampedPower]
 *       -> becomes an immortal ancestor at all
 *     unitInterval(mix) < 0.24 -> LOOSE_IMMORTAL, else TRUE_IMMORTAL. This
 *       0.24 literal shares ExtendedConfig's own default for
 *       SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE and does not collide with any
 *       other constant inside this specific method (the other 0.24 use, in
 *       the ANCESTOR_IMMORTAL_CHANCE_BY_POWER array literal, lives in
 *       &lt;clinit&gt;, a different method, so @ModifyConstant's method-scoping
 *       already isolates the two - no ordinal needed). The declared
 *       ANCESTOR_LOOSE_IMMORTAL_CHANCE field itself is never read anywhere
 *       in this class's bytecode (no getstatic, no ConstantValue attribute
 *       either) - it looks like dead/vestigial source, with this inline
 *       0.24 doing the real work instead. Documented as an oddity rather
 *       than asserted with full certainty.
 *
 * SECT_PROFILE_MIN_CULTIVATOR_PROGRESS, SECT_PROFILE_MAX_MORTAL_REALM_
 * PROGRESS, and SECT_PROFILE_MIN_MASTER_PROGRESS are NOT wired here: the
 * config fields are DoubleValue (0.0-1.0, described as fractions), but the
 * real backing fields (MIN_CULTIVATOR_PROGRESS, MAX_MORTAL_REALM_PROGRESS,
 * MIN_MASTER_PROGRESS) are raw `int` progressIndex values computed from
 * Realm.progressIndex() - not fractions of anything. Inventing a
 * fraction-to-int mapping the original config never specified would be a
 * guess, not a verified fix, so these 3 stay unwired pending an actual
 * config-schema decision (change the field type, or redefine what the
 * fraction means) rather than fabricated math.
 *
 * SECT_MAX_POWER_SCORE (a separate, older config field, both literally
 * titled "Maximum sect power score" and both defaulting to 4) is a genuine
 * duplicate of SECT_PROFILE_MAX_POWER_SCORE - only the profile variant is
 * wired here, matching this project's established handling of duplicate
 * fields elsewhere in the audit.
 *
 * SECT_PROFILE_MIN_ORDINARY_ROLE_SPAN's real counterpart (7) was not
 * isolated: 7 does appear in scaledRoleOffset(), but as a per-SectRole rank
 * offset unrelated to a role-span duration - a coincidental value match,
 * not the real field. Left unwired rather than mis-target that method.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.GeneratedSectCultivationProfile", remap = false)
public abstract class SectAncestorProfileMixin {

    @Shadow(remap = false)
    private static double[] ANCESTOR_IMMORTAL_CHANCE_BY_POWER;

    @ModifyConstant(method = "forRole", constant = @Constant(intValue = 4), remap = false, require = 0)
    private static int configExt$maxPowerScore(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PROFILE_MAX_POWER_SCORE.get();
    }

    @ModifyConstant(method = "forRole", constant = @Constant(intValue = 20), remap = false, require = 0)
    private static int configExt$maxOrdinaryRoleSpan(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN.get();
    }

    @Redirect(
            method = "ancestorProfile",
            at = @At(value = "FIELD",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/GeneratedSectCultivationProfile;ANCESTOR_IMMORTAL_CHANCE_BY_POWER:[D",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC),
            remap = false, require = 0)
    private static double[] configExt$ancestorImmortalChanceByPower() {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return ANCESTOR_IMMORTAL_CHANCE_BY_POWER;
        return new double[] {
                ExtendedConfig.SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_0.get(),
                ExtendedConfig.SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_1.get(),
                ExtendedConfig.SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_2.get(),
                ExtendedConfig.SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_3.get(),
                ExtendedConfig.SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_4.get()
        };
    }

    @ModifyConstant(method = "ancestorProfile", constant = @Constant(doubleValue = 0.24), remap = false, require = 0)
    private static double configExt$looseImmortalChance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE.get();
    }
}
