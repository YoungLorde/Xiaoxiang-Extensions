package com.xiaoxiang.configext.mixin;
import com.xiaoxiang.configext.PreWorldState;

import com.xiaoxiang.configext.client.GoldenFingerPerks;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.cultivation.Identity;
import com.xiaoxiang.cultivation.cultivation.SpiritRoot;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.FoundationDao;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawDeck;
import com.xiaoxiang.cultivation.cultivation.draw.DrawCard;
import com.xiaoxiang.cultivation.event.CapabilityEvents;
import com.xiaoxiang.cultivation.event.IdentityDrawHandler;
import com.xiaoxiang.cultivation.inventory.PlayerItemReturn;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.logging.LogUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Prevents the original mod from giving the reincarnation fate plate
 * when the player already chose their origin pre-world.
 *
 * We intercept startInitialOriginIfNeeded at HEAD. If we have a pre-world origin,
 * we directly apply the identity/spirit root/physique on the CultivationData
 * (which is already loaded and passed as a parameter), give the identity's
 * starter items via PlayerItemReturn.deliverOrQueue, and sync to client.
 *
 * This bypasses handleChooseOrigin entirely, which avoids issues with
 * canUseIdentityFlow (offline auth check) and CultivationCapability.get
 * returning empty during early login.
 */
@Mixin(IdentityDrawHandler.class)
public abstract class IdentityDrawHandlerMixin {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RNG = new Random();

    @Inject(method = "startInitialOriginIfNeeded", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$applyPreWorldOrigin(ServerPlayer player, CultivationData data, CallbackInfo ci) {
        if (!PreWorldState.hasPreWorldOrigin) {
            return;
        }

        LOGGER.info("[XiaoxiangConfigExt] Pre-world origin detected - applying directly, skipping fate plate");
        ci.cancel();

        try {
            // Re-apply perks (config-based effects)
            if (PreWorldState.storedPerkIds != null && !PreWorldState.storedPerkIds.isEmpty()) {
                LOGGER.info("[XiaoxiangConfigExt] Re-applying {} perks", PreWorldState.storedPerkIds.size());
                com.xiaoxiang.configext.client.PerkApplier.applyPerks(
                        new java.util.LinkedHashSet<>(PreWorldState.storedPerkIds));
                com.xiaoxiang.configext.client.AppliedPerkTracker.onPerksApplied(
                        new java.util.LinkedHashSet<>(PreWorldState.storedPerkIds));
                givePerkItemsToPlayer(player, PreWorldState.storedPerkIds);
            }

            // Apply origin directly on the CultivationData we already have
            String identityId = PreWorldState.storedIdentityId;
            String spiritRootId = PreWorldState.storedSpiritRootId;
            String physiqueId = PreWorldState.storedPhysiqueId;

            if (identityId != null && spiritRootId != null && physiqueId != null && data != null) {
                LOGGER.info("[XiaoxiangConfigExt] Applying pre-world origin: identity={}, root={}, physique={}",
                        identityId, spiritRootId, physiqueId);

                SpiritRoot spiritRoot = SpiritRoot.byId(spiritRootId);
                Physique physique = Physique.byId(physiqueId);

                // Check if this is a custom identity. Testing the identityId STRING
                // directly (identityId.startsWith("custom_")) rather than "Identity.byId
                // (identityId) == null" is required here: Identity.byId() never actually
                // returns null (bytecode-confirmed - com.xiaoxiang.cultivation.cultivation
                // .Identity.byId(String) falls back to LONE_CULTIVATOR for ANY
                // unrecognized id string, including every "custom_..." id). The old
                // "identity == null && ..." check below could therefore never be true, so
                // this branch never ran, and every pre-world custom-identity origin
                // silently kept whatever Identity.byId's fallback picked (LONE_CULTIVATOR)
                // - both its id AND its real hardcoded starter items - completely ignoring
                // the actual custom identity, including any starting items the player had
                // edited for it. This is the exact bug reported: a custom identity
                // duplicated from Lone Cultivator and edited to grant 24 Supreme Spirit
                // Stones instead still granted the original 64 - not a coincidence, this
                // fallback path is why. configExt$handleChooseOriginCustom below (the
                // live/in-game re-choose path) already does this check the correct way -
                // this mirrors it.
                Identity identity;
                com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity customIdentity = null;
                if (identityId != null && identityId.startsWith("custom_")) {
                    // Look up the custom identity
                    customIdentity = com.xiaoxiang.configext.client.CustomIdentityManager.getById(identityId);
                    if (customIdentity != null) {
                        // Use the base identity for the Identity enum (for portrait, translation key, etc.)
                        identity = Identity.byId(customIdentity.baseId);
                        if (identity == null) {
                            LOGGER.warn("[XiaoxiangConfigExt] Base identity not found for custom: {}, defaulting to FARMER", customIdentity.baseId);
                            identity = Identity.FARMER;
                        }
                        LOGGER.info("[XiaoxiangConfigExt] Using custom identity: {} (base: {})", customIdentity.displayName, customIdentity.baseId);
                    } else {
                        LOGGER.warn("[XiaoxiangConfigExt] Custom identity not found: {}, defaulting to FARMER", identityId);
                        identity = Identity.FARMER;
                    }
                } else {
                    identity = Identity.byId(identityId);
                    if (identity == null) {
                        LOGGER.warn("[XiaoxiangConfigExt] Identity not found: {}, defaulting to FARMER", identityId);
                        identity = Identity.FARMER;
                    }
                }
                if (spiritRoot == null || !spiritRoot.isSelectableRoot()) {
                    LOGGER.warn("[XiaoxiangConfigExt] SpiritRoot not selectable: {}, defaulting to HEAVENLY_HIDDEN", spiritRootId);
                    spiritRoot = SpiritRoot.HEAVENLY_HIDDEN;
                }
                if (physique == null) {
                    LOGGER.warn("[XiaoxiangConfigExt] Physique not found: {}, defaulting to MORTAL_BODY", physiqueId);
                    physique = Physique.MORTAL_BODY;
                }

                // Set identity, spirit root, physique directly on the data
                // For custom identities, use the custom ID so it can be looked up later
                data.setIdentityId(customIdentity != null ? customIdentity.id : identity.id());
                data.setSpiritRoot(spiritRoot);
                data.setPhysique(physique);

                // Set lifespan from identity's lifespan range (or custom identity's range)
                int[] lifespanRange;
                if (customIdentity != null) {
                    lifespanRange = new int[]{customIdentity.minLifespan, customIdentity.maxLifespan};
                } else {
                    lifespanRange = identity.lifespanRange();
                }
                if (lifespanRange != null && lifespanRange.length >= 2) {
                    int min = Math.min(lifespanRange[0], lifespanRange[1]);
                    int max = Math.max(lifespanRange[0], lifespanRange[1]);
                    int lifespan = min + RNG.nextInt(max - min + 1);
                    data.setMortalLifespan(lifespan);
                    LOGGER.info("[XiaoxiangConfigExt] Set mortal lifespan to {} (range {}-{})", lifespan, min, max);
                }

                // Set bone age to 14-18
                data.setBoneAge(14 + RNG.nextInt(5));

                // Set foundation dao to NONE
                data.setFoundationDao(FoundationDao.NONE);

                // Give the identity's starter items
                // For standard identities, starterItems() now returns config items via IdentityMixin
                // For custom identities, we skip the base identity's items and give custom items below
                if (customIdentity == null) {
                    List<ItemStack> starterItems = identity.starterItems();
                    if (starterItems != null) {
                        for (ItemStack stack : starterItems) {
                            if (stack == null || stack.isEmpty()) continue;
                            PlayerItemReturn.deliverOrQueue(player, stack.copy());
                            LOGGER.info("[XiaoxiangConfigExt] Delivered starter item: {}x{} to player {}",
                                    stack.getCount(), stack.getItem(), player.getName().getString());
                        }
                    }
                }

                // Give configurable starting items for custom identities only
                // (standard identities get their config items via the starterItems() mixin)
                if (customIdentity != null) {
                    giveConfigurableStartingItems(player, customIdentity.id);
                }

                // Sync to client so the player sees their updated cultivation data
                CapabilityEvents.syncToClient(player);

                LOGGER.info("[XiaoxiangConfigExt] Origin applied successfully: {} with {} and {}",
                        identity.id(), spiritRoot.id(), physique.id());
            }

            PreWorldState.clearAll();

        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to apply stored origin", e);
            PreWorldState.clearAll();
        }
    }

    // ── Inject into the normal (non-pre-world) identity draw flow ──

    /**
     * Intercept handleConfirm to handle custom identity cards.
     * The original mod calls Identity.byId(card.identityId()) which returns null for custom IDs.
     * We intercept at HEAD, check if the card has a custom identity, and if so, apply the origin
     * directly using the base identity and give configurable items, then cancel the original method.
     */
    @Inject(method = "handleConfirm", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$handleCustomIdentityConfirm(ServerPlayer player, int cardIndex, CallbackInfo ci) {
        try {
            // Access the DECKS map via reflection
            java.lang.reflect.Field decksField = IdentityDrawHandler.class.getDeclaredField("DECKS");
            decksField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<UUID, IdentityDrawDeck> decks = (Map<UUID, IdentityDrawDeck>) decksField.get(null);
            if (decks == null) return;

            IdentityDrawDeck deck = decks.get(player.getUUID());
            if (deck == null) return;
            if (!deck.canConfirm(cardIndex)) return;

            DrawCard card = deck.cardAt(cardIndex);
            if (card == null) return;

            String identityId = card.identityId();
            if (identityId == null || !identityId.startsWith("custom_")) return;

            // This is a custom identity - handle it ourselves
            LOGGER.info("[XiaoxiangConfigExt] Handling custom identity in handleConfirm: {}", identityId);
            ci.cancel();

            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity customIdentity =
                    com.xiaoxiang.configext.client.CustomIdentityManager.getById(identityId);
            if (customIdentity == null) {
                LOGGER.warn("[XiaoxiangConfigExt] Custom identity not found: {}, defaulting to FARMER", identityId);
                customIdentity = new com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity(
                        identityId, "Custom Identity", "farmer", 100, 120, "", "");
            }

            Identity identity = Identity.byId(customIdentity.baseId);
            if (identity == null) identity = Identity.FARMER;

            SpiritRoot spiritRoot = SpiritRoot.byId(card.spiritRootId());
            if (spiritRoot == null || !spiritRoot.isSelectableRoot()) spiritRoot = SpiritRoot.HEAVENLY_HIDDEN;

            Physique physique = Physique.MORTAL_BODY;

            // Apply origin directly (don't call applyLifeChartOrigin because it would
            // give the BASE identity's starter items, not the custom identity's items)
            try {
                CultivationData data = com.xiaoxiang.cultivation.cultivation.CultivationCapability.get(player).orElse(null);
                if (data != null) {
                    // Set identity metadata using the base identity (for portrait, translation key, etc.)
                    data.setIdentityId(customIdentity.id);
                    data.setSpiritRoot(spiritRoot);
                    data.setPhysique(physique);

                    // Set lifespan from custom range
                    int min = Math.min(customIdentity.minLifespan, customIdentity.maxLifespan);
                    int max = Math.max(customIdentity.minLifespan, customIdentity.maxLifespan);
                    int lifespan = min + RNG.nextInt(max - min + 1);
                    data.setMortalLifespan(lifespan);

                    // Set bone age to 14-18
                    data.setBoneAge(14 + RNG.nextInt(5));

                    // Set foundation dao to NONE
                    data.setFoundationDao(FoundationDao.NONE);

                    LOGGER.info("[XiaoxiangConfigExt] Custom origin applied: {} with {} and {} (lifespan {})",
                            customIdentity.id, spiritRoot.id(), physique.id(), lifespan);
                }
            } catch (Exception e) {
                LOGGER.error("[XiaoxiangConfigExt] Failed to apply custom origin", e);
            }

            // Give the custom identity's configurable starting items
            giveConfigurableStartingItems(player, customIdentity.id);

            // Sync to client
            try {
                CapabilityEvents.syncToClient(player);
            } catch (Exception e) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to sync to client", e);
            }

            // Clean up the deck
            decks.remove(player.getUUID());

        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to handle custom identity in handleConfirm", e);
        }
    }

    /**
     * Handles the LIVE (server-connected) origin-confirm path for custom identities.
     * ChooseOriginPacket.handle() calls this 6-arg overload directly with the raw
     * identityId string the client sent (verified via javap disassembly of the
     * original mod's compiled classes - IdentityDrawScreen's own confirm button sends
     * a ChooseOriginPacket with selectedIdentity().id(), NOT the deck/DrawCard/
     * handleConfirm(cardIndex) system configExt$handleCustomIdentityConfirm above hooks
     * into; that system is a separate, differently-indexed mechanism this screen's real
     * confirm flow never actually uses). Without this hook, a custom id here falls
     * through to the original's own "not a recognized Identity -> FARMER" fallback
     * (Identity.byId returns null for "custom_..." strings, and the original already
     * guards that null-safely - no crash, just silently the wrong, non-custom origin).
     *
     * Mirrors configExt$handleCustomIdentityConfirm above exactly (same field-by-field
     * CultivationData application, same starting-items/sync sequence) rather than
     * calling into applyOrigin/applyLifeChartOrigin with the resolved base identity,
     * since those also run origin-protection/life-reset side effects tuned for a real
     * Identity that this mixin has no reliable way to selectively suppress just the
     * identity-string part of.
     */
    @Inject(method = "handleChooseOrigin(Lnet/minecraft/server/level/ServerPlayer;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$handleChooseOriginCustom(ServerPlayer player, boolean random, String identityId,
            String spiritRootId, String physiqueId, boolean reconfigureMode, CallbackInfo ci) {
        // Random re-rolls never carry a specific identity id (the client always sends ""
        // for identityId when random=true), and a non-custom id is a normal base
        // identity - in both cases let the original's already-correct handling run.
        if (random || identityId == null || !identityId.startsWith("custom_")) return;

        try {
            // Respect the same eligibility gate the original checks first (e.g. blocks
            // re-choosing an origin the player already has), so this hook can't grant
            // an origin change the original method itself would have refused.
            java.lang.reflect.Method canUseMethod =
                    IdentityDrawHandler.class.getDeclaredMethod("canUseIdentityFlow", ServerPlayer.class);
            canUseMethod.setAccessible(true);
            Boolean canUse = (Boolean) canUseMethod.invoke(null, player);
            if (canUse == null || !canUse) return;

            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity customIdentity =
                    com.xiaoxiang.configext.client.CustomIdentityManager.getById(identityId);
            if (customIdentity == null) {
                // Unknown/stale custom id (e.g. deleted since the client last loaded its
                // list) - let the original run, which safely falls back to FARMER.
                return;
            }

            LOGGER.info("[XiaoxiangConfigExt] handleChooseOrigin: custom identity selected: {}", identityId);
            ci.cancel();

            Identity identity = Identity.byId(customIdentity.baseId);
            if (identity == null) identity = Identity.FARMER;

            SpiritRoot spiritRoot = SpiritRoot.byId(spiritRootId);
            if (spiritRoot == null || !spiritRoot.isSelectableRoot()) spiritRoot = SpiritRoot.HEAVENLY_HIDDEN;

            Physique physique = Physique.byId(physiqueId);
            if (physique == null) physique = Physique.MORTAL_BODY;

            try {
                CultivationData data = com.xiaoxiang.cultivation.cultivation.CultivationCapability.get(player).orElse(null);
                if (data != null) {
                    data.setIdentityId(customIdentity.id);
                    data.setSpiritRoot(spiritRoot);
                    data.setPhysique(physique);

                    int min = Math.min(customIdentity.minLifespan, customIdentity.maxLifespan);
                    int max = Math.max(customIdentity.minLifespan, customIdentity.maxLifespan);
                    int lifespan = min + RNG.nextInt(max - min + 1);
                    data.setMortalLifespan(lifespan);

                    data.setBoneAge(14 + RNG.nextInt(5));
                    data.setFoundationDao(FoundationDao.NONE);

                    LOGGER.info("[XiaoxiangConfigExt] Custom origin applied via handleChooseOrigin: {} with {} and {} (lifespan {})",
                            customIdentity.id, spiritRoot.id(), physique.id(), lifespan);
                }
            } catch (Exception e) {
                LOGGER.error("[XiaoxiangConfigExt] Failed to apply custom origin in handleChooseOrigin", e);
            }

            giveConfigurableStartingItems(player, customIdentity.id);

            try {
                CapabilityEvents.syncToClient(player);
            } catch (Exception e) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to sync to client", e);
            }

        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to handle custom identity in handleChooseOrigin", e);
        }
    }

    // Note: applyLifeChartOrigin and applyOrigin no longer need TAIL injections
    // because Identity.starterItems() is now mixed in to return config items.
    // The original mod calls identity.starterItems() inside those methods,
    // so config items are automatically given.

    /**
     * Give item-based perks to the player (swords, storage bags, etc.)
     */
    private static void givePerkItemsToPlayer(ServerPlayer player, java.util.Set<Integer> perkIds) {
        for (int perkId : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(perkId);
            if (perk == null) continue;
            givePerkItems(player, perk);
        }
    }

    private static void givePerkItems(ServerPlayer player, GoldenFingerPerks.Perk perk) {
        try {
            java.util.Random rng = new java.util.Random();
            switch (perk.name) {
                case "Heavenly Sword":
                    // Give ONE immortal sword of random element
                    String[] immortalElements = {"chi_yan", "han_bing", "qing_mu"};
                    String chosenElement = immortalElements[rng.nextInt(immortalElements.length)];
                    giveItem(player, "xiaoxiang_cultivation:" + chosenElement + "_sword_immortal", 1);
                    break;
                case "Spirit Sword Arsenal":
                    // Give ONE sword of a random tier
                    String[] tiers = {"low", "mid", "high", "supreme", "immortal"};
                    String[] elements = {"chi_yan", "han_bing", "qing_mu"};
                    String tier = tiers[rng.nextInt(tiers.length)];
                    String element = elements[rng.nextInt(elements.length)];
                    giveItem(player, "xiaoxiang_cultivation:" + element + "_sword_" + tier, 1);
                    break;
                case "Storage Bag":
                    giveItem(player, "xiaoxiang_cultivation:storage_bag_immortal", 1);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to give items for perk {}: {}", perk.name, e.getMessage());
        }
    }

    /**
     * Give configurable starting items based on the specific identity.
     * Reads from the ExtendedConfig identity.startingItems.<identity> settings.
     */
    private static void giveConfigurableStartingItems(ServerPlayer player, String identityId) {
        try {
            String itemsConfig = getStartingItemsForIdentity(identityId);
            if (itemsConfig == null || itemsConfig.isEmpty()) return;

            // Parse "modid:item,count;modid:item,count" format
            String[] entries = itemsConfig.split(";");
            for (String entry : entries) {
                entry = entry.trim();
                if (entry.isEmpty()) continue;
                String[] parts = entry.split(",");
                String itemId = parts[0].trim();
                int count = 1;
                if (parts.length > 1) {
                    try { count = Integer.parseInt(parts[1].trim()); } catch (NumberFormatException e) { /* default 1 */ }
                }
                giveItem(player, itemId, count);
            }
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to give configurable starting items: {}", e.getMessage());
        }
    }

    /**
     * Get the starting items config string for a specific identity.
     * First checks custom identities, then falls back to per-identity configs,
     * then to the default.
     */
    private static String getStartingItemsForIdentity(String identityId) {
        if (identityId == null) return ExtendedConfig.IDENTITY_STARTING_ITEMS_DEFAULT.get();
        String id = identityId.toLowerCase();

        // Check custom identities first
        try {
            String customItems = ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.get();
            if (customItems != null && !customItems.isEmpty()) {
                // Format: "id1:item,count;item,count|id2:item,count"
                String[] customEntries = customItems.split("\\|");
                for (String entry : customEntries) {
                    int colonIdx = entry.indexOf(':');
                    if (colonIdx > 0) {
                        String customId = entry.substring(0, colonIdx).trim().toLowerCase();
                        if (customId.equals(id)) {
                            return entry.substring(colonIdx + 1).trim();
                        }
                    }
                }
            }
        } catch (Exception e) { /* ignore */ }

        // Per-identity configs
        switch (id) {
            case "lone_cultivator":   return ExtendedConfig.IDENTITY_STARTING_ITEMS_LONE_CULTIVATOR.get();
            case "merchant_son":      return ExtendedConfig.IDENTITY_STARTING_ITEMS_MERCHANT_SON.get();
            case "bandit_leader":     return ExtendedConfig.IDENTITY_STARTING_ITEMS_BANDIT_LEADER.get();
            case "hunter":            return ExtendedConfig.IDENTITY_STARTING_ITEMS_HUNTER.get();
            case "doctor_heir":       return ExtendedConfig.IDENTITY_STARTING_ITEMS_DOCTOR_HEIR.get();
            case "hermit_disciple":   return ExtendedConfig.IDENTITY_STARTING_ITEMS_HERMIT_DISCIPLE.get();
            case "fisherman":         return ExtendedConfig.IDENTITY_STARTING_ITEMS_FISHERMAN.get();
            case "farmer":            return ExtendedConfig.IDENTITY_STARTING_ITEMS_FARMER.get();
            case "abandoned_infant":  return ExtendedConfig.IDENTITY_STARTING_ITEMS_ABANDONED_INFANT.get();
            case "general_son":       return ExtendedConfig.IDENTITY_STARTING_ITEMS_GENERAL_SON.get();
            case "exiled_princess":   return ExtendedConfig.IDENTITY_STARTING_ITEMS_EXILED_PRINCESS.get();
            case "pirate":            return ExtendedConfig.IDENTITY_STARTING_ITEMS_PIRATE.get();
            case "beast_descendant":  return ExtendedConfig.IDENTITY_STARTING_ITEMS_BEAST_DESCENDANT.get();
            case "taoist":            return ExtendedConfig.IDENTITY_STARTING_ITEMS_TAOIST.get();
            case "monk":              return ExtendedConfig.IDENTITY_STARTING_ITEMS_MONK.get();
            case "academy_student":   return ExtendedConfig.IDENTITY_STARTING_ITEMS_ACADEMY_STUDENT.get();
            default:                  return ExtendedConfig.IDENTITY_STARTING_ITEMS_DEFAULT.get();
        }
    }

    private static void giveItem(ServerPlayer player, String registryName, int count) {
        try {
            net.minecraft.resources.ResourceLocation rl = new net.minecraft.resources.ResourceLocation(registryName);
            net.minecraft.world.item.Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(rl);
            if (item == null) {
                LOGGER.warn("[XiaoxiangConfigExt] Item not found: {}", registryName);
                return;
            }
            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, count);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
            LOGGER.info("[XiaoxiangConfigExt] Gave {}x{} to player {}", count, registryName, player.getName().getString());
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to give item {}: {}", registryName, e.getMessage());
        }
    }
}
