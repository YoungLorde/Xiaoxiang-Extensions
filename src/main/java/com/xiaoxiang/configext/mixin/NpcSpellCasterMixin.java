package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.world.NpcCombatContext;
import com.xiaoxiang.cultivation.cultivation.spell.Spell;
import com.xiaoxiang.cultivation.entity.npc.NpcSpellCaster;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sets the NPC combat context flag when an NPC casts a spell.
 * This allows SpellMixin to apply different damage multipliers
 * for player vs NPC spells.
 *
 * Also applies an NPC-specific spell damage multiplier on top of
 * the global spell damage multiplier, so NPCs can have different
 * spell damage scaling than the player.
 *
 * IMPORTANT: All methods in NpcSpellCaster return a value (boolean/Object),
 * so we must use CallbackInfoReturnable, NOT CallbackInfo.
 * Using CallbackInfo on a non-void method causes InvalidInjectionException
 * which crashes world generation at 5% ("Preparing spawn area: 5%").
 */
@Mixin(NpcSpellCaster.class)
public abstract class NpcSpellCasterMixin {

    @Inject(method = "cast", at = @At("HEAD"), remap = false, require = 0)
    private static void configExt$setNpcCastingStart(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                      CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(true);
    }

    @Inject(method = "cast", at = @At("RETURN"), remap = false, require = 0)
    private static void configExt$setNpcCastingEnd(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                    CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(false);
    }

    @Inject(method = "castWithOutcome", at = @At("HEAD"), remap = false, require = 0)
    private static void configExt$setNpcCastingStartOutcome(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                             CallbackInfoReturnable<Object> cir) {
        NpcCombatContext.setNpcCasting(true);
    }

    @Inject(method = "castWithOutcome", at = @At("RETURN"), remap = false, require = 0)
    private static void configExt$setNpcCastingEndOutcome(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                           CallbackInfoReturnable<Object> cir) {
        NpcCombatContext.setNpcCasting(false);
    }

    @Inject(method = "castMega", at = @At("HEAD"), remap = false, require = 0)
    private static void configExt$setNpcCastingStartMega(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                          CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(true);
    }

    @Inject(method = "castMega", at = @At("RETURN"), remap = false, require = 0)
    private static void configExt$setNpcCastingEndMega(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                        CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(false);
    }

    @Inject(method = "completeMegaCastPrepaid", at = @At("HEAD"), remap = false, require = 0)
    private static void configExt$setNpcCastingStartComplete(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                              CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(true);
    }

    @Inject(method = "completeMegaCastPrepaid", at = @At("RETURN"), remap = false, require = 0)
    private static void configExt$setNpcCastingEndComplete(WanderingCultivatorEntity caster, Spell spell, LivingEntity target,
                                                            CallbackInfoReturnable<Boolean> cir) {
        NpcCombatContext.setNpcCasting(false);
    }
}
