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
 *   alchemyQiCostMultiplier(Player)D    (added 2026-09-01)
 *       ... ALCHEMY_HEART_BODY -> 0.5, else 1.0 (dconst_1, not a literal match) -
 *       only one 0.5 literal in this method
 *   alchemySuccessChanceBonus(Player)D  (added 2026-09-01)
 *       ... ALCHEMY_HEART_BODY -> 0.1, else 0.0 (dconst_0, not a literal match) -
 *       only one 0.1 literal in this method
 *
 * Note: the spellDamageMultiplier(Player, Spell) overload does NOT contain the
 * 0.2 literal in this build - it routes through applyPlayerOnlySpellDamageRules
 * and never applies the Innate Sword Body non-sword penalty. That is original
 * mod behaviour and is deliberately left alone.
 *
 * Effects/inverse five elements section (added 2026-09-01), also verified
 * against the same disassembly - the real target is this class, NOT the
 * empty InverseFiveElementsEffect marker MobEffect (that class is just a
 * bare constructor with no logic):
 *
 *   applySharedSpellDamageRules(Physique, Spell, D)D
 *       ... INVERSE_FIVE_ELEMENTS_BODY && isBasicFiveElementSpell -> mult *= 1.1
 *       (sole occurrence of 1.1d in this method; the neighboring CHAOS_BODY
 *       1.3d literal above is the pre-existing handler for that field).
 *   spellQiCostMultiplier(Physique, Spell)D
 *       ... INVERSE_FIVE_ELEMENTS_BODY && isBasicFiveElementSpell -> mult *= 0.9
 *       (sole occurrence of 0.9d in this overload; the method is overloaded
 *       with spellQiCostMultiplier(Player, Spell) so both handlers below use
 *       explicit descriptors to avoid touching the wrong overload).
 *   spellQiCostMultiplier(Player, Spell)D
 *       ... stacks > 0 -> mult *= Math.max(0.0, 1.0 - stacks * 0.25)
 *       (sole occurrence of 0.25d in this overload). Note this 0.25d shares
 *       its constant-pool index with the 0.25d in applyPlayerOnlySpellDamageRules
 *       below - both trace back to one named constant in the original source
 *       reused for two purposes - but since the two config fields are scoped
 *       to their own distinct methods here, each can still be tuned
 *       independently without cross-contamination.
 *   applyPlayerOnlySpellDamageRules(Player, Physique, Spell, D)D
 *       ... stacks > 0 -> mult *= (1.0 + stacks * 0.25)
 *       (sole occurrence of 0.25d in this method).
 *   lambda$onSpellCast$1(Spell, ServerPlayer, CultivationData)V
 *       ... applies/refreshes the inverse-mark MobEffectInstance and extends
 *       both the stack-timeout (setInverseFiveElementStacks) and the mark
 *       expiry (setInverseFiveElementMark) by the same duration. Confirmed
 *       via a class-wide grep that "600" appears exactly 3 times in the
 *       whole class, all inside this one lambda: twice as "long 600l"
 *       (same constant-pool index, both currentTick + 600 expiry pushes)
 *       and once as "sipush 600" (the MobEffectInstance duration argument,
 *       not a constant-pool literal since sipush embeds small ints inline).
 *       All 3 represent EFFECT_INVERSE_MARK_DURATION_TICKS and are handled
 *       by two @ModifyConstant methods below (one long, one int) both
 *       scoped to this lambda method by name.
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

    // ALCHEMY_HEART_BODY's alchemy qi cost multiplier = 0.5. Verified (2026-09-01)
    // that this method is called from network/ExecuteAlchemyPacket while actually
    // computing an alchemy craft's qi cost, not just cosmetic - so this reaches
    // real gameplay. Gated on ENABLE_ALCHEMY_OVERRIDES (not ENABLE_PHYSIQUE_
    // OVERRIDES) since both this field and its neighbor below live in the
    // "alchemy" config section, matching the toggle AlchemyCoreBlockEntityMixin
    // already uses for the rest of that section.
    @ModifyConstant(
            method = "alchemyQiCostMultiplier",
            constant = @Constant(doubleValue = 0.5),
            require = 0)
    private static double configExt$alchemyHeartQiCostMult(double original) {
        if (!ExtendedConfig.ENABLE_ALCHEMY_OVERRIDES.get()) return original;
        return ExtendedConfig.ALCHEMY_HEART_QI_COST_MULT.get();
    }

    // ALCHEMY_HEART_BODY's alchemy success chance bonus = 0.1. Verified
    // (2026-09-01) that this method is called from network/ExecuteAlchemyPacket
    // while resolving an alchemy craft's success roll, not just cosmetic.
    @ModifyConstant(
            method = "alchemySuccessChanceBonus",
            constant = @Constant(doubleValue = 0.1),
            require = 0)
    private static double configExt$alchemyHeartSuccessBonus(double original) {
        if (!ExtendedConfig.ENABLE_ALCHEMY_OVERRIDES.get()) return original;
        return ExtendedConfig.ALCHEMY_HEART_SUCCESS_BONUS.get();
    }

    // EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT = 1.1 (inverse five elements
    // damage bonus when the active spell's element matches the chain).
    @ModifyConstant(
            method = "applySharedSpellDamageRules",
            constant = @Constant(doubleValue = 1.1),
            require = 0)
    private static double configExt$inverseBaseFiveElementDmgMult(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT.get();
    }

    // EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT = 0.9. Explicit descriptor
    // since spellQiCostMultiplier is overloaded (Physique,Spell) vs
    // (Player,Spell) - only the Physique overload contains this literal.
    @ModifyConstant(
            method = "spellQiCostMultiplier(Lcom/xiaoxiang/cultivation/cultivation/Physique;Lcom/xiaoxiang/cultivation/cultivation/spell/Spell;)D",
            constant = @Constant(doubleValue = 0.9),
            require = 0)
    private static double configExt$inverseBaseFiveElementCostMult(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT.get();
    }

    // EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER = 0.25 (applyPlayerOnlySpellDamageRules,
    // per-stack damage bonus for the inverse-mark chain).
    @ModifyConstant(
            method = "applyPlayerOnlySpellDamageRules",
            constant = @Constant(doubleValue = 0.25),
            require = 0)
    private static double configExt$inverseStackDamagePerLayer(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER.get();
    }

    // EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER = 0.25. Explicit
    // descriptor for the same overload-ambiguity reason as the 0.9 handler
    // above - only the (Player,Spell) overload contains this literal.
    @ModifyConstant(
            method = "spellQiCostMultiplier(Lnet/minecraft/world/entity/player/Player;Lcom/xiaoxiang/cultivation/cultivation/spell/Spell;)D",
            constant = @Constant(doubleValue = 0.25),
            require = 0)
    private static double configExt$inverseStackCostReductionPerLayer(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER.get();
    }

    // EFFECT_INVERSE_MARK_DURATION_TICKS = 600, long form: both
    // setInverseFiveElementStacks and setInverseFiveElementMark expiry
    // pushes inside lambda$onSpellCast$1 (currentTick + 600).
    @ModifyConstant(
            method = "lambda$onSpellCast$1",
            constant = @Constant(longValue = 600L),
            require = 0)
    private static long configExt$inverseMarkDurationTicksLong(long original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_MARK_DURATION_TICKS.get().longValue();
    }

    // EFFECT_INVERSE_MARK_DURATION_TICKS = 600, int form: the
    // MobEffectInstance duration argument (sipush 600) in the same lambda.
    @ModifyConstant(
            method = "lambda$onSpellCast$1",
            constant = @Constant(intValue = 600),
            require = 0)
    private static int configExt$inverseMarkDurationTicksInt(int original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_INVERSE_MARK_DURATION_TICKS.get();
    }
}
