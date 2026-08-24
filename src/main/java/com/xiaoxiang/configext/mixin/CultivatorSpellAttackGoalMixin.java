package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.ai.CultivatorSpellAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the spell-attack half of the "npcAi" config section to real behaviour.
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/entity/npc/ai/CultivatorSpellAttackGoal.class:
 *
 *   MAX_RANGE = 40.0 -> m_8036_, compared against m_20270_ (plain distance);
 *                       single occurrence in that method
 *   HIGH_IMPACT_MEGA_COOLDOWN_TICKS = 500 -> m_8056_, the base of
 *                       `highImpactMegaCooldown = 500 + rand(200)`; the
 *                       random 200 spread is deliberately left alone
 *
 * This goal class has no tick()/canContinueToUse() of its own - m_8056_
 * (Goal.start) carries the casting logic, which is why the cooldown literal
 * lives there.
 */
@Mixin(CultivatorSpellAttackGoal.class)
public abstract class CultivatorSpellAttackGoalMixin {

    /** MAX_RANGE: how far an NPC will start a spell attack from. */
    @ModifyConstant(method = "m_8036_", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 40.0), remap = false, require = 0)
    private double configExt$spellMaxRange(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_SPELL_MAX_RANGE.get();
    }

    /** HIGH_IMPACT_MEGA_COOLDOWN_TICKS: base cooldown after a high-impact mega cast. */
    @ModifyConstant(method = "m_8056_", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 500), remap = false, require = 0)
    private int configExt$highImpactMegaCooldown(int original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_AI_HIGH_IMPACT_MEGA_COOLDOWN_TICKS.get();
    }
}
