package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.QiElement;
import com.xiaoxiang.cultivation.cultivation.technique.Technique;
import com.xiaoxiang.cultivation.cultivation.technique.TechniqueBonusHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the "techniques" config section (techniques.*) to real behaviour.
 *
 * All targets verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/technique/TechniqueBonusHelper.class
 * (javap -p -c -s), which exposes these static accessors:
 *
 *   attackBonus(Lnet/minecraft/world/entity/player/Player;)I
 *   defenseBonus(Lnet/minecraft/world/entity/player/Player;)I
 *   critRateBonus(Lnet/minecraft/world/entity/player/Player;)I
 *   maxHpBonus(Lnet/minecraft/world/entity/player/Player;)I
 *   qiAbsorbMultiplier(Lnet/minecraft/world/entity/player/Player;)D
 *   moveSpeedBonus(Lnet/minecraft/world/entity/player/Player;)D
 *   spellElementMultiplier(Lnet/minecraft/world/entity/player/Player;
 *                          Lcom/xiaoxiang/cultivation/cultivation/QiElement;)D
 *
 * Each simply reads the matching field off Technique$Bonus (attack, defense,
 * critRate, maxHp, qiAbsorbMult, moveSpeed, elementSpellMult[]) - there is no
 * per-category scaling hook in the original mod, so we apply the global
 * multiplier at RETURN.
 *
 * Every multiplier defaults to 1.0, so with a stock config every injection here
 * is a no-op and vanilla mod behaviour is preserved exactly.
 *
 * remap = false everywhere: these are the third-party mod's own methods, not
 * vanilla Minecraft overrides. require = 0 per house convention.
 */
@Mixin(value = TechniqueBonusHelper.class, remap = false)
public abstract class TechniqueBonusHelperMixin {

    @Inject(method = "qiAbsorbMultiplier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$qiAbsorbMultiplier(Player player, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_QI_ABSORB_MULT_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue(cir.getReturnValueD() * mult);
    }

    @Inject(method = "attackBonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$attackBonus(Player player, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_ATTACK_BONUS_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue((int) Math.round(cir.getReturnValueI() * mult));
    }

    @Inject(method = "defenseBonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$defenseBonus(Player player, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_DEFENSE_BONUS_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue((int) Math.round(cir.getReturnValueI() * mult));
    }

    @Inject(method = "maxHpBonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$maxHpBonus(Player player, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_MAX_HP_BONUS_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue((int) Math.round(cir.getReturnValueI() * mult));
    }

    @Inject(method = "critRateBonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$critRateBonus(Player player, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_CRIT_RATE_BONUS_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue((int) Math.round(cir.getReturnValueI() * mult));
    }

    @Inject(method = "spellElementMultiplier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$spellElementMultiplier(Player player, QiElement element, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_ELEMENT_SPELL_MULT_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue(cir.getReturnValueD() * mult);
    }

    @Inject(method = "moveSpeedBonus", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$moveSpeedBonus(Player player, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_TECHNIQUE_OVERRIDES.get()) return;
        double mult = ExtendedConfig.TECHNIQUE_MOVE_SPEED_BONUS_GLOBAL.get();
        if (mult == 1.0) return;
        cir.setReturnValue(cir.getReturnValueD() * mult);
    }

    // ── Refining section's two per-technique tier-up-chance fields (added 2026-09-01) ──
    //
    // refiningTierUpChance(Player)D and alchemyTierUpChance(Player)D both read
    // bonusOf(player) - i.e. whichever ONE technique the player has equipped
    // (Technique.bonus(), baked once per enum constant at class-init - verified
    // via javap that bonusOf() is a single equipped-technique lookup, not an
    // aggregate across several techniques) - so identity-checking the equipped
    // technique here is safe and unambiguous, the same pattern already used in
    // PillEffectSpecsMixin for matching individual items.
    //
    // IMPORTANT bytecode-verified finding: REFINING_TIER_UP_CHANCE_HEAVENLY_ELIXIR's
    // own name/section says "refining", but the base mod's HEAVENLY_ELIXIR technique
    // never calls .refiningTierUpChance() when building its Bonus record - it calls
    // .alchemyTierUpChance(0.25) instead. Heavenly Elixir's real bonus is an ALCHEMY
    // tier-up chance, not a refining one; its actual refiningTierUpChance is 0.0
    // (DIVINE_FORGE is the one that sets refiningTierUpChance, to 0.25). Rather than
    // wire this field to a mechanic it doesn't actually control (which would give
    // Heavenly Elixir a refining bonus the base mod never gave it, and make "Reset
    // All" restore a value that isn't the real original-mod default), this hooks it
    // to alchemyTierUpChance() instead - the field's own default (0.25) already
    // matches what that mechanic's real default is, so this makes the field mean
    // what its default value already implied. Its own tooltip in ExtendedConfig.java
    // has been corrected to say this plainly.
    @Inject(method = "refiningTierUpChance", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$refiningTierUpChance(Player player, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_REFINING_OVERRIDES.get()) return;
        if (TechniqueBonusHelper.equippedOf(player) == Technique.DIVINE_FORGE) {
            cir.setReturnValue(ExtendedConfig.REFINING_TIER_UP_CHANCE_DIVINE_FORGE.get());
        }
    }

    @Inject(method = "alchemyTierUpChance", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$alchemyTierUpChanceForHeavenlyElixir(Player player, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_REFINING_OVERRIDES.get()) return;
        if (TechniqueBonusHelper.equippedOf(player) == Technique.HEAVENLY_ELIXIR) {
            cir.setReturnValue(ExtendedConfig.REFINING_TIER_UP_CHANCE_HEAVENLY_ELIXIR.get());
        }
    }
}
