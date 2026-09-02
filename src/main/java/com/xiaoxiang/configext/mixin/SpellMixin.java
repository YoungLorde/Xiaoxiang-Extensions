package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.configext.world.NpcCombatContext;
import com.xiaoxiang.cultivation.cultivation.spell.Spell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies global multipliers to spell damage and qi cost.
 *
 * When an NPC is casting (tracked via NpcCombatContext), an additional
 * NPC-specific multiplier is applied on top of the global multiplier.
 * This allows different damage scaling for player vs NPC spells.
 *
 * For example, on Chaos difficulty:
 * - Global multiplier = 0.25 (player spells do 25% damage)
 * - NPC multiplier = 16.0 (NPC spells do 25% * 16 = 400% = 4x damage)
 * Net: player spells = 0.25x, NPC spells = 4.0x
 */
@Mixin(Spell.class)
public abstract class SpellMixin {

    @Inject(method = "damage", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$damage(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) {
            return;
        }
        double mult = ExtendedConfig.SPELL_DAMAGE_GLOBAL_MULTIPLIER.get();

        // If an NPC is casting, apply additional NPC spell damage multiplier
        if (NpcCombatContext.isNpcCasting()) {
            double npcMult = ExtendedConfig.NPC_SPELL_DAMAGE_MULTIPLIER.get();
            mult *= npcMult;
        }

        if (mult == 1.0) {
            return;
        }
        int base = cir.getReturnValue();
        cir.setReturnValue((int) Math.max(0, Math.round(base * mult)));
    }

    @Inject(method = "qiCost", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$qiCost(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SPELL_OVERRIDES.get()) {
            return;
        }
        double mult = ExtendedConfig.SPELL_QI_COST_GLOBAL_MULTIPLIER.get();

        // If an NPC is casting, apply NPC qi cost multiplier (lower = NPC casts more)
        if (NpcCombatContext.isNpcCasting()) {
            double npcCostMult = ExtendedConfig.NPC_SPELL_QI_COST_MULTIPLIER.get();
            mult *= npcCostMult;
        }

        if (mult == 1.0) {
            return;
        }
        int base = cir.getReturnValue();
        cir.setReturnValue((int) Math.max(0, Math.round(base * mult)));
    }
}
