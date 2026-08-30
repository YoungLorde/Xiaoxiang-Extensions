package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.CultivatorTrades;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.mojang.logging.LogUtils;

/**
 * Wires up the previously-orphaned npcTrades.infiniteUses / npcTrades.priceMult
 * config options. Verified against javap disassembly of CultivatorTrades.class
 * from xiaoxiang_cultivation-0.1.1302.jar: every trade offer in the original mod
 * is built via one of two MerchantOffer constructors -
 *   (ItemStack cost, ItemStack result, int maxUses, int xp, float demand)
 *   (ItemStack cost1, ItemStack cost2, ItemStack result, int maxUses, int xp, float demand)
 * - across exactly 5 call sites in 4 private helper methods. addTechniqueBookOffers
 * and addSpellBookOffers already pass Integer.MAX_VALUE for maxUses (confirmed via
 * the ldc #10 // int 2147483647 constant at both sites), so those two are already
 * effectively infinite and are left untouched here; only addInventoryOffers (maxUses
 * = the NPC's actual stock count) and addHeldSwordOffer (maxUses hardcoded to 1) are
 * ever limited, so those are the two infiniteUses actually needs to touch.
 *
 * priceMult scales the cost ItemStack's count directly (the constructor's own
 * trailing float parameter is vanilla's per-use "demand" price creep, which the
 * original mod already always passes as 0.0f at every site - unrelated to our
 * priceMult setting, and intentionally left alone here).
 */
@Mixin(CultivatorTrades.class)
public abstract class CultivatorTradesMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    // ── infinite uses: addInventoryOffers (2 call sites: 5-arg, 6-arg ctor) ──

    @ModifyArg(method = "addInventoryOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 2, remap = false, require = 0)
    private static int configExt$inventoryMaxUses5(int original) {
        return configExt$infiniteUses(original);
    }

    @ModifyArg(method = "addInventoryOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 3, remap = false, require = 0)
    private static int configExt$inventoryMaxUses6(int original) {
        return configExt$infiniteUses(original);
    }

    // ── infinite uses: addHeldSwordOffer (2 call sites: 5-arg, 6-arg ctor) ──

    @ModifyArg(method = "addHeldSwordOffer",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 2, remap = false, require = 0)
    private static int configExt$swordMaxUses5(int original) {
        return configExt$infiniteUses(original);
    }

    @ModifyArg(method = "addHeldSwordOffer",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 3, remap = false, require = 0)
    private static int configExt$swordMaxUses6(int original) {
        return configExt$infiniteUses(original);
    }

    // ── price multiplier: addInventoryOffers cost item(s) ──

    @ModifyArg(method = "addInventoryOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$inventoryPrice5(ItemStack original) {
        return configExt$scalePrice(original);
    }

    @ModifyArg(method = "addInventoryOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$inventoryPrice6a(ItemStack original) {
        return configExt$scalePrice(original);
    }

    @ModifyArg(method = "addInventoryOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 1, remap = false, require = 0)
    private static ItemStack configExt$inventoryPrice6b(ItemStack original) {
        return configExt$scalePrice(original);
    }

    // ── price multiplier: addHeldSwordOffer cost item(s) ──

    @ModifyArg(method = "addHeldSwordOffer",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$swordPrice5(ItemStack original) {
        return configExt$scalePrice(original);
    }

    @ModifyArg(method = "addHeldSwordOffer",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$swordPrice6a(ItemStack original) {
        return configExt$scalePrice(original);
    }

    @ModifyArg(method = "addHeldSwordOffer",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 1, remap = false, require = 0)
    private static ItemStack configExt$swordPrice6b(ItemStack original) {
        return configExt$scalePrice(original);
    }

    // ── price multiplier: addTechniqueBookOffers / addSpellBookOffers cost item ──

    @ModifyArg(method = "addTechniqueBookOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$techniqueBookPrice(ItemStack original) {
        return configExt$scalePrice(original);
    }

    @ModifyArg(method = "addSpellBookOffers",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/item/trading/MerchantOffer;<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;IIF)V",
                        ordinal = 0),
               index = 0, remap = false, require = 0)
    private static ItemStack configExt$spellBookPrice(ItemStack original) {
        return configExt$scalePrice(original);
    }

    // ── shared logic ──

    private static int configExt$infiniteUses(int original) {
        if (!ExtendedConfig.NPC_TRADES_INFINITE_USES.get()) return original;
        return Integer.MAX_VALUE;
    }

    private static ItemStack configExt$scalePrice(ItemStack original) {
        double mult = ExtendedConfig.NPC_TRADES_PRICE_MULT.get();
        if (mult == 1.0 || original == null || original.isEmpty()) return original;
        int scaled = (int) Math.max(1, Math.round(original.getCount() * mult));
        ItemStack copy = original.copy();
        copy.setCount(scaled);
        return copy;
    }
}
