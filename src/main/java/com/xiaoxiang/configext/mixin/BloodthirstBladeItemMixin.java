package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.item.weapon.BloodthirstBladeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires up the three Bloodthirst Blade config fields that were sitting dead:
 * WEAPON_BLOOD_SPELL_DAMAGE_BONUS_MULT, WEAPON_BLOOD_SPELL_QI_REDUCTION_MULT,
 * WEAPON_BLOOD_CAPACITY_MULTIPLIER.
 *
 * VERIFIED (2026-09-01) via javap: BloodthirstBladeItem is a final class
 * (extends SwordItem directly, implements TieredWeapon) whose
 * bloodSpellDamageBonusPct()/bloodSpellQiReductionPct()/bloodCapacity() are
 * plain accessor methods each returning one `final` field that was set once
 * at item construction from the constructor's own int/int/double args -
 * nothing re-reads them afterward except this same item's own tooltip and
 * blood-value bookkeeping code (bloodValue/setBloodValue/addBloodValue, all
 * of which call bloodCapacity() live, not a cached copy - confirmed by
 * disassembly, so overriding the accessor here does reach real gameplay
 * logic, not just the tooltip).
 *
 * Each of the three config fields is a *multiplier* on the base mod's own
 * default value (matching their tooltips), applied here rather than in
 * ExtendedConfig.java so the base value only has to be known in one place
 * (the constructor argument the base mod passes to itself) and doesn't get
 * silently out of sync if a base-mod update changes it.
 */
@Mixin(BloodthirstBladeItem.class)
public abstract class BloodthirstBladeItemMixin {

    @Inject(method = "bloodSpellDamageBonusPct", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$bloodSpellDamageBonusPct(CallbackInfoReturnable<Integer> cir) {
        try {
            if (!ExtendedConfig.ENABLE_WEAPON_OVERRIDES.get()) return;
            double mult = ExtendedConfig.WEAPON_BLOOD_SPELL_DAMAGE_BONUS_MULT.get();
            if (mult == 1.0) return;
            int base = cir.getReturnValue();
            cir.setReturnValue((int) Math.round(base * mult));
        } catch (Exception ignored) {
            // Config not loaded yet - keep the base mod's own value.
        }
    }

    @Inject(method = "bloodSpellQiReductionPct", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$bloodSpellQiReductionPct(CallbackInfoReturnable<Integer> cir) {
        try {
            if (!ExtendedConfig.ENABLE_WEAPON_OVERRIDES.get()) return;
            double mult = ExtendedConfig.WEAPON_BLOOD_SPELL_QI_REDUCTION_MULT.get();
            if (mult == 1.0) return;
            int base = cir.getReturnValue();
            int scaled = (int) Math.round(base * mult);
            cir.setReturnValue(Math.max(0, Math.min(100, scaled)));
        } catch (Exception ignored) {
            // Config not loaded yet - keep the base mod's own value.
        }
    }

    @Inject(method = "bloodCapacity", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void configExt$bloodCapacity(CallbackInfoReturnable<Double> cir) {
        try {
            if (!ExtendedConfig.ENABLE_WEAPON_OVERRIDES.get()) return;
            double mult = ExtendedConfig.WEAPON_BLOOD_CAPACITY_MULTIPLIER.get();
            if (mult == 1.0) return;
            double base = cir.getReturnValue();
            cir.setReturnValue(Math.max(0.0, base * mult));
        } catch (Exception ignored) {
            // Config not loaded yet - keep the base mod's own value.
        }
    }
}
