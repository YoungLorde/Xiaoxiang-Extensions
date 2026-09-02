package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.QiElement;
import com.xiaoxiang.cultivation.cultivation.qi.BlockQiSpec;
import com.xiaoxiang.cultivation.cultivation.qi.BlockQiSpecs;
import com.xiaoxiang.cultivation.registry.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wires the 10 QI_STONE_ORE_MAX_QI_* and QI_STONE_ORE_REGEN_* fields.
 *
 * Verified via javap -p -c -s against BlockQiSpecs.class (2026-09-01):
 * BlockQiSpecs.applyHardcodedDefaults() is a large ~1600-line static method
 * that populates a Block -> BlockQiSpec map for dozens of vanilla and mod
 * blocks. Most entries route through small named helper methods
 * (wood_micro(), earth_weak(), pure_artifact(), etc.) that return a shared
 * BlockQiSpec, but the 5 spirit-stone-ore blocks are the exception: each is
 * registered with its BlockQiSpec.of(...) args inlined directly at the call
 * site, confirmed exactly matching the config's already-correct defaults:
 *
 *   LOW_SPIRIT_STONE_ORE:      of(PURE, 2000,  5.0, 0.1)
 *   MID_SPIRIT_STONE_ORE:      of(PURE, 4000, 10.0, 0.15)
 *   HIGH_SPIRIT_STONE_ORE:     of(PURE, 8000, 20.0, 0.2)
 *   SUPREME_SPIRIT_STONE_ORE:  of(PURE, 20000, 50.0, 0.3)
 *   SPIRIT_VEIN_SPRING:        of(PURE, 50000, 80.0, 0.45)
 *
 * Deliberately NOT wired via @ModifyConstant/ordinal: applyHardcodedDefaults
 * shares common literal values (5.0, 10.0, 20.0, 50.0, 0.1, 0.15, 0.2, 0.3,
 * etc.) across dozens of unrelated block entries throughout its ~1600 lines,
 * so an ordinal-based constant redirect would be extremely fragile - one
 * off-by-one in the ordinal count silently reconfigures the wrong,
 * completely unrelated block's qi spec instead. Instead this @Inject's at
 * the TAIL of applyHardcodedDefaults() (after every hardcoded entry,
 * including the 5 above, has already been put() into the map) and calls the
 * class's own public BlockQiSpecs.override(Block, BlockQiSpec) to replace
 * just those 5 entries with config-driven values. override() is a simple
 * null-checked SPECS.put(...) (confirmed via javap), so this exactly
 * reproduces what the hardcoded entries would have done, just with the
 * maxQi/regen args swapped for the config values. Injecting at the TAIL of
 * applyHardcodedDefaults() itself (rather than some external init hook)
 * ensures the override re-applies correctly even if resetToDefaults() is
 * invoked at runtime, since resetToDefaults() clears the map and calls
 * applyHardcodedDefaults() again.
 *
 * The per-tier emitRate (0.1/0.15/0.2/0.3/0.45) has no corresponding config
 * field, so the real verified constant is kept as-is per tier rather than
 * guessed at or left at some other tier's value.
 */
@Mixin(value = BlockQiSpecs.class, remap = false)
public abstract class BlockQiSpecsMixin {

    @Inject(method = "applyHardcodedDefaults", at = @At("TAIL"), remap = false, require = 0)
    private static void configExt$overrideSpiritStoneOreSpecs(CallbackInfo ci) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return;

        BlockQiSpecs.override(ModBlocks.LOW_SPIRIT_STONE_ORE.get(), BlockQiSpec.of(
                QiElement.PURE,
                ExtendedConfig.QI_STONE_ORE_MAX_QI_LOW.get().intValue(),
                ExtendedConfig.QI_STONE_ORE_REGEN_LOW.get(),
                0.1));
        BlockQiSpecs.override(ModBlocks.MID_SPIRIT_STONE_ORE.get(), BlockQiSpec.of(
                QiElement.PURE,
                ExtendedConfig.QI_STONE_ORE_MAX_QI_MID.get().intValue(),
                ExtendedConfig.QI_STONE_ORE_REGEN_MID.get(),
                0.15));
        BlockQiSpecs.override(ModBlocks.HIGH_SPIRIT_STONE_ORE.get(), BlockQiSpec.of(
                QiElement.PURE,
                ExtendedConfig.QI_STONE_ORE_MAX_QI_HIGH.get().intValue(),
                ExtendedConfig.QI_STONE_ORE_REGEN_HIGH.get(),
                0.2));
        BlockQiSpecs.override(ModBlocks.SUPREME_SPIRIT_STONE_ORE.get(), BlockQiSpec.of(
                QiElement.PURE,
                ExtendedConfig.QI_STONE_ORE_MAX_QI_SUPREME.get().intValue(),
                ExtendedConfig.QI_STONE_ORE_REGEN_SUPREME.get(),
                0.3));
        BlockQiSpecs.override(ModBlocks.SPIRIT_VEIN_SPRING.get(), BlockQiSpec.of(
                QiElement.PURE,
                ExtendedConfig.QI_STONE_ORE_MAX_QI_SPIRIT_VEIN_SPRING.get().intValue(),
                ExtendedConfig.QI_STONE_ORE_REGEN_SPRING.get(),
                0.45));
    }
}
