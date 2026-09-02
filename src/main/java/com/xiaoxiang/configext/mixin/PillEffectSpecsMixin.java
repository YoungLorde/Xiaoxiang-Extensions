package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.alchemy.PillEffectSpecs;
import com.xiaoxiang.cultivation.cultivation.alchemy.PillTier;
import com.xiaoxiang.cultivation.registry.ModItems;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the "pills" config section's per-tier Qi-recovery and rejuvenation
 * values into the values the base mod's items actually read.
 *
 * VERIFIED FIX (2026-09-01 documentation/config audit): before this mixin,
 * PILL_QI_LOW/MID/HIGH/SUPREME/IMMORTAL and REJUVENATION_HEAL_LOW/MID and
 * REJUVENATION_REGEN_*_SUPREME / REJUVENATION_ABSORPTION_*_SUPREME were dead
 * - defined in ExtendedConfig and shown in the config screen, but never read
 * by any mixin or handler, so editing them in-game did nothing. Confirmed via
 * javap disassembly of the installed xiaoxiang_cultivation-0.1.1302.jar:
 * every PillItem subclass (CultivationPillItem/RejuvenationPillItem/etc)
 * resolves its actual effect through the base mod's own
 * PillEffectSpecs.qiAmount/rejuvenationHeal/regenerationTicks/
 * regenerationAmplifier/absorptionTicks/absorptionAmplifier static methods -
 * PillItem.finishUsingItem calls PillEffectSpecs.qiAmount(this, this.qiAmount)
 * and treats a negative result as "refill to max" (bytecode-confirmed, and
 * matches the base mod's own pill_qi_recovery_immortal.json datapack entry,
 * which is literally {"qi": -1} - the exact sentinel PILL_QI_IMMORTAL's
 * existing comment already claimed). The config's default values for every
 * field this mixin wires (10/100/1000/10000/-1 for qi, 4.0/10.0 for heal,
 * 2400/1/2400/0 for the supreme regen/absorption pair) exactly match the
 * base mod's own pill_effects/*.json datapack defaults, confirming they were
 * authored to mirror this exact override point and simply never got hooked
 * up to it.
 *
 * Only the specific ModItems.PILL_QI_RECOVERY_* / PILL_REJUVENATION_* items
 * are matched by identity - qiAmount() in particular is shared by every pill
 * type (blood burn, clear mind, divine stride, cultivation pills all have
 * their own qiAmount field and route through this same method), so matching
 * by PillTier alone would have wrongly applied the "qi pill" values to every
 * other kind of pill.
 *
 * Not every tier/effect combination has a config field (e.g. there is no
 * REJUVENATION_HEAL_HIGH or any IMMORTAL rejuvenation field) - those
 * untouched combinations simply fall through to this @Inject doing nothing,
 * leaving the base mod's own datapack-driven default in place, exactly like
 * every other partially-covered section in this mod's config.
 */
@Mixin(PillEffectSpecs.class)
public abstract class PillEffectSpecsMixin {

    @Inject(method = "qiAmount", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$qiAmount(Item item, int defaultValue, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_QI_RECOVERY_LOW.get()) {
            cir.setReturnValue(ExtendedConfig.PILL_QI_LOW.get());
        } else if (item == ModItems.PILL_QI_RECOVERY_MID.get()) {
            cir.setReturnValue(ExtendedConfig.PILL_QI_MID.get());
        } else if (item == ModItems.PILL_QI_RECOVERY_HIGH.get()) {
            cir.setReturnValue(ExtendedConfig.PILL_QI_HIGH.get());
        } else if (item == ModItems.PILL_QI_RECOVERY_SUPREME.get()) {
            cir.setReturnValue(ExtendedConfig.PILL_QI_SUPREME.get());
        } else if (item == ModItems.PILL_QI_RECOVERY_IMMORTAL.get()) {
            cir.setReturnValue(ExtendedConfig.PILL_QI_IMMORTAL.get());
        }
    }

    @Inject(method = "rejuvenationHeal", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$rejuvenationHeal(Item item, PillTier tier, float defaultValue,
                                                     CallbackInfoReturnable<Float> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_REJUVENATION_LOW.get()) {
            cir.setReturnValue((float) ExtendedConfig.REJUVENATION_HEAL_LOW.get().doubleValue());
        } else if (item == ModItems.PILL_REJUVENATION_MID.get()) {
            cir.setReturnValue((float) ExtendedConfig.REJUVENATION_HEAL_MID.get().doubleValue());
        }
        // No REJUVENATION_HEAL_HIGH/IMMORTAL config field exists - HIGH keeps its
        // datapack-defined heal_full behavior and IMMORTAL keeps its own default.
    }

    @Inject(method = "regenerationTicks", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$regenerationTicks(Item item, PillTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_REJUVENATION_SUPREME.get()) {
            cir.setReturnValue(ExtendedConfig.REJUVENATION_REGEN_TICKS_SUPREME.get());
        }
    }

    @Inject(method = "regenerationAmplifier", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$regenerationAmplifier(Item item, PillTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_REJUVENATION_SUPREME.get()) {
            cir.setReturnValue(ExtendedConfig.REJUVENATION_REGEN_AMP_SUPREME.get());
        }
    }

    @Inject(method = "absorptionTicks", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$absorptionTicks(Item item, PillTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_REJUVENATION_SUPREME.get()) {
            cir.setReturnValue(ExtendedConfig.REJUVENATION_ABSORPTION_TICKS_SUPREME.get());
        }
    }

    @Inject(method = "absorptionAmplifier", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$absorptionAmplifier(Item item, PillTier tier, CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_PILL_OVERRIDES.get()) {
            return;
        }
        if (item == ModItems.PILL_REJUVENATION_SUPREME.get()) {
            cir.setReturnValue(ExtendedConfig.REJUVENATION_ABSORPTION_AMP_SUPREME.get());
        }
    }
}
