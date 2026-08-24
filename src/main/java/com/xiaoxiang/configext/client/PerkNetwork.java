package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.XiaoxiangConfigExt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Network channel for sending perk selections from client to server.
 * The server then gives the player items for item-based perks.
 */
public class PerkNetwork {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(XiaoxiangConfigExt.MOD_ID, "perks"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, PerkSelectionPacket.class,
                PerkSelectionPacket::encode,
                PerkSelectionPacket::decode,
                PerkSelectionPacket::handle);
        CHANNEL.registerMessage(packetId++, CrouchMeditationPacket.class,
                CrouchMeditationPacket::encode,
                CrouchMeditationPacket::decode,
                CrouchMeditationPacket::handle);
    }

    /** Packet sent from client to server with selected perk IDs. */
    public static class PerkSelectionPacket {
        public int[] perkIds;

        public PerkSelectionPacket() {}

        public PerkSelectionPacket(Set<Integer> ids) {
            this.perkIds = new int[ids.size()];
            int i = 0;
            for (int id : ids) {
                this.perkIds[i++] = id;
            }
        }

        public static void encode(PerkSelectionPacket msg, net.minecraft.network.FriendlyByteBuf buf) {
            buf.writeVarIntArray(msg.perkIds);
        }

        public static PerkSelectionPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
            PerkSelectionPacket msg = new PerkSelectionPacket();
            msg.perkIds = buf.readVarIntArray();
            return msg;
        }

        public static void handle(PerkSelectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                LOGGER.info("[XiaoxiangConfigExt] Received perk selection from {}: {} perks",
                        player.getName().getString(), msg.perkIds.length);

                Set<Integer> perkIdSet = new HashSet<>();
                for (int id : msg.perkIds) {
                    perkIdSet.add(id);
                }

                // Track applied perks
                AppliedPerkTracker.onPerksApplied(perkIdSet);

                // Give items for item-based perks
                for (int perkId : msg.perkIds) {
                    GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(perkId);
                    if (perk == null) continue;
                    givePerkItems(player, perk);
                }
            });
            ctx.get().setPacketHandled(true);
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
                        // Most perks are config-based, not item-based
                        break;
                }
            } catch (Exception e) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to give items for perk {}: {}", perk.name, e.getMessage());
            }
        }

        private static void giveItem(ServerPlayer player, String registryName, int count) {
            try {
                ResourceLocation rl = new ResourceLocation(registryName);
                Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(rl);
                if (item == null) {
                    LOGGER.warn("[XiaoxiangConfigExt] Item not found: {}", registryName);
                    return;
                }
                ItemStack stack = new ItemStack(item, count);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                LOGGER.info("[XiaoxiangConfigExt] Gave {}x{} to player {}", count, registryName, player.getName().getString());
            } catch (Exception e) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to give item {}: {}", registryName, e.getMessage());
            }
        }
    }
}
