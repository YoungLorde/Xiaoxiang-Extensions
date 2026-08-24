package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the "loot" config section (chest roll counts and item weights) to real
 * behaviour.
 *
 * There is no Forge GlobalLootModifier or datapack table behind these numbers:
 * the cultivation ruin chests are filled procedurally in
 * com.xiaoxiang.cultivation.worldgen.CultivationChestLoot, which is a
 * package-private final class (hence @Mixin(targets = ...) rather than a class
 * literal). AddItemLootModifier / ModLootModifiers exist in the jar but carry
 * none of the values in this config section.
 *
 * ---------------------------------------------------------------- roll counts
 * Verified bytecode of  static void fill(RandomizableContainerBlockEntity,
 * RandomSource, boolean ruined):
 *
 *   #9  iconst_2   ruined  ? randomBetween(r, 2, 4)   vanilla rolls min
 *   #10 iconst_4                                      vanilla rolls max
 *   #18 iconst_3   :else     randomBetween(r, 3, 6)   vanilla rolls min
 *   #19 bipush 6                                      vanilla rolls max
 *   #30 iconst_3   ruined  ? r.nextInt(3)             cultivation rolls (exclusive bound)
 *   #40 iconst_1   :else     randomBetween(r, 1, 3)   cultivation rolls min
 *   #41 iconst_3                                      cultivation rolls max
 *
 * So within fill() the constant ordinals are: 2 -> ordinal 0 (unique),
 * 4 -> ordinal 0 (unique), 6 -> ordinal 0 (unique), 1 -> ordinal 0 (unique),
 * and 3 -> ordinals 0, 1, 2 in that order. Every ordinal below was read
 * directly off the disassembly; require = 0 per house convention so a missed
 * target degrades to "no override" rather than a fatal mixin-apply crash.
 *
 * Note on ruinedCultivationMax: the original calls RandomSource.nextInt(3),
 * i.e. 0..2 with 3 as the exclusive bound. The config default (3) reproduces
 * that exactly. It is clamped to >= 1 here because nextInt(0) throws.
 *
 * -------------------------------------------------------------- item weights
 * Verified bytecode of  private static List&lt;LootEntry&gt; cultivationEntries(
 * RandomSource, boolean ruined). The weight is argument index 1 of
 *   private static void add(List, int weight, Item, int, int, RandomSource)
 * and the add(...) call sites appear in this fixed order:
 *
 *   ordinal 0  LOW_SPIRIT_STONE        weight 12
 *   ordinal 1  MID_SPIRIT_STONE        weight 6
 *   ordinal 2  HIGH_SPIRIT_STONE       weight 3
 *   ordinal 3  SUPREME_SPIRIT_STONE    weight 1
 *   ordinal 4  HERB                    weight 8
 *   ordinal 5  HERB_SEEDS              (no config entry)
 *   ordinal 6  SPIRIT_PLANT_ITEMS loop (no config entry)
 *   ordinal 7  TECHNIQUE_BOOK_FRAGMENT weight 5
 *   ordinal 8  ZHUJI_DAN               weight ruined ? 2 : 4
 *
 * Targeting the call sites rather than the literals avoids the extremely
 * ambiguous iconst_1/2/3 soup in that ~900-byte method.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.worldgen.CultivationChestLoot", remap = false)
public abstract class CultivationChestLootMixin {

    // ---------------------------------------------------------------- rolls

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 2, ordinal = 0), require = 0)
    private static int configExt$ruinedVanillaRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_RUINED_VANILLA_ROLLS_MIN.get();
    }

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 4, ordinal = 0), require = 0)
    private static int configExt$ruinedVanillaRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_RUINED_VANILLA_ROLLS_MAX.get();
    }

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 3, ordinal = 0), require = 0)
    private static int configExt$completeVanillaRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_VANILLA_ROLLS_MIN.get();
    }

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 6, ordinal = 0), require = 0)
    private static int configExt$completeVanillaRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_VANILLA_ROLLS_MAX.get();
    }

    /** Exclusive bound of RandomSource.nextInt(..) - must stay >= 1. */
    @ModifyConstant(method = "fill", constant = @Constant(intValue = 3, ordinal = 1), require = 0)
    private static int configExt$ruinedCultivationRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return Math.max(1, ExtendedConfig.LOOT_RUINED_CULTIVATION_ROLLS_MAX.get());
    }

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 1, ordinal = 0), require = 0)
    private static int configExt$completeCultivationRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_CULTIVATION_ROLLS_MIN.get();
    }

    @ModifyConstant(method = "fill", constant = @Constant(intValue = 3, ordinal = 2), require = 0)
    private static int configExt$completeCultivationRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_CULTIVATION_ROLLS_MAX.get();
    }

    // -------------------------------------------------------------- weights

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 0),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$lowSpiritStoneWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_LOW_SPIRIT_STONE_WEIGHT.get();
    }

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 1),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$midSpiritStoneWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_MID_SPIRIT_STONE_WEIGHT.get();
    }

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 2),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$highSpiritStoneWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_HIGH_SPIRIT_STONE_WEIGHT.get();
    }

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 3),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$supremeSpiritStoneWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_SUPREME_SPIRIT_STONE_WEIGHT.get();
    }

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 4),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$herbWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_HERB_WEIGHT.get();
    }

    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 7),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$techniqueFragmentWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        return ExtendedConfig.LOOT_TECHNIQUE_FRAGMENT_WEIGHT.get();
    }

    /**
     * The original passes {@code ruined ? 2 : 4} here, so the incoming value
     * identifies the chest variant exactly - those are the only two values this
     * call site can produce.
     */
    @ModifyArg(
            method = "cultivationEntries",
            at = @At(value = "INVOKE",
                     target = "Lcom/xiaoxiang/cultivation/worldgen/CultivationChestLoot;add(Ljava/util/List;ILnet/minecraft/world/item/Item;IILnet/minecraft/util/RandomSource;)V",
                     ordinal = 8),
            index = 1,
            remap = false,
            require = 0)
    private static int configExt$zhujiDanWeight(int weight) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return weight;
        if (weight == 2) {
            return ExtendedConfig.LOOT_ZHUJI_DAN_WEIGHT_RUINED.get();
        }
        if (weight == 4) {
            return ExtendedConfig.LOOT_ZHUJI_DAN_WEIGHT_COMPLETE.get();
        }
        return weight;
    }
}
