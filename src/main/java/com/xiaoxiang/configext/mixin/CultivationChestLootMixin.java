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
 *
 * CORRECTED 2026-09-02 (systematic sweep): the entire "ruined vs complete"
 * distinction described above was DELETED from base mod 0.1.1479. javap -p
 * confirms the class no longer has any method named fill(...) at all -
 * static void fill(RandomizableContainerBlockEntity, RandomSource, boolean)
 * was split/replaced by two new methods, neither of which takes a `ruined`
 * boolean:
 *   - static void fillSectContainer(RandomizableContainerBlockEntity,
 *     RandomSource): the new public entry point (was `fill`'s old role),
 *     but its body no longer branches on ruined/complete at all - it just
 *     delegates straight to fillSectLoot(Container, RandomSource).
 *   - private static void fillSectLoot(Container, RandomSource): unified
 *     roll logic, bytecode-verified (javap -p -c) to always run
 *     randomBetween(r, 3, 6) for vanilla rolls and randomBetween(r, 1, 3)
 *     for cultivation rolls - i.e. exactly the OLD "complete" branch,
 *     unconditionally. The old "ruined" branch (2..4 vanilla rolls,
 *     nextInt(3) cultivation rolls) is not present anywhere in the new
 *     bytecode - the ruined/complete chest variants apparently no longer
 *     get different roll counts upstream.
 * Since `fill` no longer exists as a method name, all 6 handlers below were
 * silently inert (require = 0 degrades an unmatched target to "no
 * override", not a crash - confirmed via the systematic checker, this was
 * NOT among the launch-crash bugs) rather than actually broken at load
 * time. Fixed by retargeting to fillSectLoot with re-verified ordinals
 * (only 2 int-3 occurrences remain there now, not 3 - offsets recomputed
 * via fresh javap -c):
 *   - completeVanillaRollsMin/Max (3, 6): still apply, now unconditionally
 *     (every chest uses these, not just "complete" ones). Ordinals
 *     unchanged (still ordinal 0 for both).
 *   - completeCultivationRollsMin (1): still applies unconditionally.
 *     Ordinal unchanged (0).
 *   - completeCultivationRollsMax (3): still applies, but its ordinal
 *     shifted from 2 to 1 - the old ordinal-1 occurrence of "3" (the ruined
 *     branch's nextInt bound) no longer exists, so the surviving two
 *     occurrences of "3" in fillSectLoot are now ordinal 0 (vanilla min,
 *     still completeVanillaRollsMin's target) and ordinal 1 (cultivation
 *     max, this field's target).
 *   - ruinedVanillaRollsMin/Max (2, 4): PERMANENTLY DEAD - their literals
 *     do not appear anywhere in fillSectLoot at all. Retargeted to
 *     fillSectLoot anyway (safe: no ordinal-0 occurrence of 2 or 4 exists
 *     there to collide with) purely so a future reader sees they at least
 *     resolve to the right class/method; they will simply never fire.
 *   - ruinedCultivationRollsMax (3, old ordinal 1): also PERMANENTLY DEAD,
 *     but deliberately left targeting the nonexistent "fill" rather than
 *     "fillSectLoot" - unlike the two fields above, fillSectLoot's "3"
 *     literal DOES still occur (twice), and both occurrences are real, live
 *     targets of other handlers (completeVanillaRollsMin at ordinal 0,
 *     completeCultivationRollsMax at ordinal 1). Retargeting this one to
 *     fillSectLoot at either ordinal would collide with one of those and
 *     silently fight over the same call site instead of being harmlessly
 *     inert, so it stays pointed at "fill" instead.
 * All three dead handlers degrade to a silent no-op under require = 0
 * (not a crash - confirmed via the systematic checker, none of these 6
 * were among the actual launch-crash bugs), which is the correct degraded
 * behavior given the base mod removed their upstream code path entirely.
 * Not deleted outright since ExtendedConfig still declares these three
 * fields and removing the handlers would silently orphan them with zero
 * explanation in this file.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.worldgen.CultivationChestLoot", remap = false)
public abstract class CultivationChestLootMixin {

    // ---------------------------------------------------------------- rolls

    /** Dead: literal 2 no longer occurs anywhere in fillSectLoot (see class doc). */
    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 2, ordinal = 0), require = 0)
    private static int configExt$ruinedVanillaRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_RUINED_VANILLA_ROLLS_MIN.get();
    }

    /** Dead: literal 4 no longer occurs anywhere in fillSectLoot (see class doc). */
    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 4, ordinal = 0), require = 0)
    private static int configExt$ruinedVanillaRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_RUINED_VANILLA_ROLLS_MAX.get();
    }

    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 3, ordinal = 0), require = 0)
    private static int configExt$completeVanillaRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_VANILLA_ROLLS_MIN.get();
    }

    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 6, ordinal = 0), require = 0)
    private static int configExt$completeVanillaRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_VANILLA_ROLLS_MAX.get();
    }

    /**
     * Dead, and deliberately left targeting the no-longer-existent "fill"
     * (NOT retargeted to fillSectLoot): the ruined branch's nextInt(3)
     * exclusive bound no longer exists anywhere in the new bytecode, and
     * fillSectLoot's only two "3" occurrences (ordinals 0 and 1) are both
     * real, live targets now (completeVanillaRollsMin and
     * completeCultivationRollsMax respectively - see class doc). Pointing
     * this handler at fillSectLoot with ANY ordinal would either miss
     * entirely or, worse, collide with one of those two real handlers and
     * silently fight over the same call site whenever both are enabled.
     * Left targeting "fill" so it stays unambiguously inert (require = 0 -
     * "fill" not found = no-op, same as before) with zero collision risk.
     */
    @ModifyConstant(method = "fill", constant = @Constant(intValue = 3, ordinal = 1), require = 0)
    private static int configExt$ruinedCultivationRollsMax(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return Math.max(1, ExtendedConfig.LOOT_RUINED_CULTIVATION_ROLLS_MAX.get());
    }

    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 1, ordinal = 0), require = 0)
    private static int configExt$completeCultivationRollsMin(int original) {
        if (!ExtendedConfig.ENABLE_LOOT_OVERRIDES.get()) return original;
        return ExtendedConfig.LOOT_COMPLETE_CULTIVATION_ROLLS_MIN.get();
    }

    /** Ordinal corrected from 2 to 1 - see class doc. */
    @ModifyConstant(method = "fillSectLoot", constant = @Constant(intValue = 3, ordinal = 1), require = 0)
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
