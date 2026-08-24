package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.PhysiqueBonusHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the remaining "physiques" config entries that are NOT stored on the
 * PhysiqueBonus record but are inlined literals inside PhysiqueBonusHelper.
 *
 * All targets verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/PhysiqueBonusHelper.class:
 *
 *   spellDamageMultiplier(Physique, Spell)D
 *       ... INNATE_SWORD_BODY && isNonSwordElementalSpell -> mult *= 0.2   (only 0.2 in method)
 *   spellDamageMultiplier(WanderingCultivatorEntity, Spell)D
 *       ... same rule, same single 0.2 literal
 *   applySharedSpellDamageRules(Physique, Spell, D)D
 *       ... CHAOS_BODY -> mult *= 1.3   (only 1.3 in method)
 *   cultivationRequirementMultiplier(Physique)D
 *       ... CHAOS_BODY -> 10.0          (only 10.0 in method)
 *
 * Note: the spellDamageMultiplier(Player, Spell) overload does NOT contain the
 * 0.2 literal in this build - it routes through applyPlayerOnlySpellDamageRules
 * and never applies the Innate Sword Body non-sword penalty. That is original
 * mod behaviour and is deliberately left alone.
 *
 * The constants are private static final primitives inlined by javac at every
 * call site, so mixing the field would be a no-op; @ModifyConstant on the
 * consuming methods is the only thing that works. require = 0 per house
 * convention so a missed target degrades to "no override" instead of a fatal
 * mixin-apply crash.
 */
@Mixin(value = PhysiqueBonusHelper.class, remap = false)
public abstract class PhysiqueBonusHelperMixin {

    // INNATE_SWORD_BODY non-sword elemental spell penalty (0.2), Physique overload.
    @ModifyConstant(
            method = "spellDamageMultiplier(Lcom/xiaoxiang/cultivation/cultivation/Physique;Lcom/xiaoxiang/cultivation/cultivation/spell/Spell;)D",
            constant = @Constant(doubleValue = 0.2),
            require = 0)
    private static double configExt$innateSwordNonSwordPenalty(double original) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) return original;
        return ExtendedConfig.PHYSIQUE_INNATE_SWORD_NON_SWORD_PENALTY.get();
    }

    // Same penalty on the NPC (WanderingCultivatorEntity) overload.
    @ModifyConstant(
            method = "spellDamageMultiplier(Lcom/xiaoxiang/cultivation/entity/npc/WanderingCultivatorEntity;Lcom/xiaoxiang/cultivation/cultivation/spell/Spell;)D",
            constant = @Constant(doubleValue = 0.2),
            require = 0)
    private static double configExt$innateSwordNonSwordPenaltyNpc(double original) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) return original;
        return ExtendedConfig.PHYSIQUE_INNATE_SWORD_NON_SWORD_PENALTY.get();
    }

    // CHAOS_BODY_SPELL_DAMAGE_MULTIPLIER = 1.3
    @ModifyConstant(
            method = "applySharedSpellDamageRules",
            constant = @Constant(doubleValue = 1.3),
            require = 0)
    private static double configExt$chaosBodySpellDamageMult(double original) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) return original;
        return ExtendedConfig.PHYSIQUE_CHAOS_BODY_SPELL_DAMAGE_MULT.get();
    }

    // CHAOS_BODY_CULTIVATION_REQUIREMENT_MULTIPLIER = 10.0
    @ModifyConstant(
            method = "cultivationRequirementMultiplier",
            constant = @Constant(doubleValue = 10.0),
            require = 0)
    private static double configExt$chaosBodyCultivationReqMult(double original) {
        if (!ExtendedConfig.ENABLE_PHYSIQUE_OVERRIDES.get()) return original;
        return ExtendedConfig.PHYSIQUE_CHAOS_BODY_CULTIVATION_REQ_MULT.get();
    }
}
