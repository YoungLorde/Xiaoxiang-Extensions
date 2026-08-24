package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.LooseImmortalBonusHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to LooseImmortalBonusHelper per-level bonuses.
 * The original mod has arrays for body defense, cultivation efficiency, qi recovery,
 * melee damage, spell damage, spell cost, max qi, free zhenyuan, and auto zhenyuan attr.
 * We inject at RETURN on the per-level accessor methods to override with config values.
 */
@Mixin(LooseImmortalBonusHelper.class)
public abstract class LooseImmortalBonusHelperMixin {

    private static int configExt$levelIndex(int level) {
        return Math.max(0, Math.min(3, level));
    }

    @Inject(method = "wavesForCurrentLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$wavesForCurrentLevel(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ExtendedConfig.LOOSE_IMMORTAL_WAVES_PER_TRIBULATION.get());
    }

    @Inject(method = "boltsPerWaveForCurrentLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$boltsPerWaveForCurrentLevel(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ExtendedConfig.LOOSE_IMMORTAL_BOLTS_PER_WAVE.get());
    }

    @Inject(method = "strikeDamageForCurrentLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$strikeDamageForCurrentLevel(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ExtendedConfig.LOOSE_IMMORTAL_STRIKE_DAMAGE.get());
    }

    @Inject(method = "bodyDefenseBonusForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$bodyDefenseBonusForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_BODY_DEFENSE.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_BODY_DEFENSE.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_BODY_DEFENSE.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_BODY_DEFENSE.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "cultivationEfficiencyBonusForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$cultivationEfficiencyBonusForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_CULTIVATION_EFFICIENCY.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_CULTIVATION_EFFICIENCY.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_CULTIVATION_EFFICIENCY.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_CULTIVATION_EFFICIENCY.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "qiRecoveryPerSecondBonusForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$qiRecoveryPerSecondBonusForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_QI_RECOVERY.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_QI_RECOVERY.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_QI_RECOVERY.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_QI_RECOVERY.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "meleeDamageBonusForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$meleeDamageBonusForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_MELEE_DAMAGE.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_MELEE_DAMAGE.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_MELEE_DAMAGE.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_MELEE_DAMAGE.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "spellDamageBonusPercentForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$spellDamageBonusPercentForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        // Config stores as double multiplier (0.1, 0.2, etc.), original returns int percent
        double override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_SPELL_DAMAGE.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_SPELL_DAMAGE.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_SPELL_DAMAGE.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_SPELL_DAMAGE.get();
        };
        cir.setReturnValue((int) Math.round(override * 100));
    }

    @Inject(method = "spellQiCostReductionPercentForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$spellQiCostReductionPercentForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        double override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_SPELL_COST.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_SPELL_COST.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_SPELL_COST.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_SPELL_COST.get();
        };
        cir.setReturnValue((int) Math.round(override * 100));
    }

    @Inject(method = "maxQiBonusForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$maxQiBonusForLevel(int level, CallbackInfoReturnable<Long> cir) {
        int idx = configExt$levelIndex(level);
        long override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_MAX_QI.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_MAX_QI.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_MAX_QI.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_MAX_QI.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "freeZhenyuanTotalForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$freeZhenyuanTotalForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_FREE_ZHENYUAN.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_FREE_ZHENYUAN.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_FREE_ZHENYUAN.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_FREE_ZHENYUAN.get();
        };
        cir.setReturnValue(override);
    }

    @Inject(method = "automaticZhenyuanAttributesTotalForLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$automaticZhenyuanAttributesTotalForLevel(int level, CallbackInfoReturnable<Integer> cir) {
        int idx = configExt$levelIndex(level);
        int override = switch (idx) {
            case 0 -> ExtendedConfig.LOOSE_IMMORTAL_L0_AUTO_ZHENYUAN_ATTR.get();
            case 1 -> ExtendedConfig.LOOSE_IMMORTAL_L1_AUTO_ZHENYUAN_ATTR.get();
            case 2 -> ExtendedConfig.LOOSE_IMMORTAL_L2_AUTO_ZHENYUAN_ATTR.get();
            default -> ExtendedConfig.LOOSE_IMMORTAL_L3_AUTO_ZHENYUAN_ATTR.get();
        };
        cir.setReturnValue(override);
    }
}
