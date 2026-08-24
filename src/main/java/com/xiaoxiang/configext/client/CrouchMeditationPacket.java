package com.xiaoxiang.configext.client;

import com.xiaoxiang.cultivation.cultivation.CultivationCapability;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.event.CapabilityEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.function.Supplier;

/**
 * Packet sent from client to server to start/stop crouch-meditation.
 *
 * On the server, this sets CultivationData.setMeditating() and tracks
 * the player as crouch-meditating in CrouchMeditationState.
 *
 * Crouch-meditation gives the same cultivation progress as cushion
 * meditation, but with a lower multiplier (see CrouchMeditationState).
 */
public class CrouchMeditationPacket {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final boolean meditating;

    public CrouchMeditationPacket(boolean meditating) {
        this.meditating = meditating;
    }

    public static void encode(CrouchMeditationPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.meditating);
    }

    public static CrouchMeditationPacket decode(FriendlyByteBuf buf) {
        return new CrouchMeditationPacket(buf.readBoolean());
    }

    public static void handle(CrouchMeditationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            CultivationData data = CultivationCapability.get(player).orElse(null);
            if (data == null) return;

            // Don't interfere if the player is already cushion-meditating
            if (data.isMeditating() && !CrouchMeditationState.isCrouchMeditating(player.getUUID())) {
                // Player is on a cushion — don't override
                return;
            }

            // Set meditation state
            if (msg.meditating) {
                // Check if the player has an equipped technique (same as cushion)
                if (!data.hasEquippedTechnique()) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                            "message.xiaoxiang_cultivation.meditation.no_technique"));
                    return;
                }
                data.setMeditating(true);
                CrouchMeditationState.setServerCrouchMeditating(player.getUUID(), true);
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        "message.xiaoxiang_cultivation.meditation.start"));
            } else {
                data.setMeditating(false);
                CrouchMeditationState.setServerCrouchMeditating(player.getUUID(), false);
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        "message.xiaoxiang_cultivation.meditation.stop"));
            }

            // Sync to client
            CapabilityEvents.syncToClient(player);

            LOGGER.debug("[XiaoxiangConfigExt] Crouch meditation {} for {}",
                    msg.meditating ? "started" : "stopped", player.getName().getString());
        });
        ctx.get().setPacketHandled(true);
    }
}
