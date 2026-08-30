package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.XiaoxiangConfigExt;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Registers and handles a "Crouch Meditate" keybind.
 *
 * When the player is crouching (shift) and presses this key, they enter
 * crouch-meditation mode. This sends a CrouchMeditationPacket to the server,
 * which sets CultivationData.setMeditating(true) and tracks the player as
 * crouch-meditating.
 *
 * Crouch-meditation has the SAME effect as cushion meditation (cultivation
 * progress advances), but with a LOWER multiplier (configurable, default 3x
 * instead of 10x for cushion meditation).
 *
 * When the key is released, the player stops crouching, or the player moves,
 * crouch-meditation ends.
 *
 * DEDICATED-SERVER-CRASH FIX: register(RegisterKeyMappingsEvent) used to be
 * wired up via a plain context.getModEventBus().addListener(...) call in
 * XiaoxiangConfigExt's constructor, which runs on BOTH client and dedicated
 * server. RegisterKeyMappingsEvent lives in Forge's client-only
 * net.minecraftforge.client.event package, so registering a listener for it
 * unconditionally from common code is the same class of bug that was
 * crashing the mod on a dedicated server via ConfigScreenHandler (see
 * ClientModEvents' class doc for the full explanation) - just a second
 * instance of it. The class-level @Mod.EventBusSubscriber(value =
 * Dist.CLIENT, ...) annotation below is the fix: FML checks it during mod
 * discovery and skips this class entirely on a dedicated server, before it
 * is ever classloaded there, exactly like the nested ClientHandler class
 * below already (correctly) did for the FORGE-bus tick event.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrouchMeditationKeybind {

    public static final String CATEGORY = "key.categories.xiaoxiang_cultivation";

    public static KeyMapping CROUCH_MEDITATE;

    private static boolean wasCrouchMeditating = false;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        CROUCH_MEDITATE = new KeyMapping(
                "key.xiaoxiang_config_ext.crouch_meditate",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        );
        event.register(CROUCH_MEDITATE);
    }

    @Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientHandler {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null || mc.level == null) return;

            // Check if crouch-meditation is enabled in config
            if (!ExtendedConfig.ENABLE_CROUCH_MEDITATION.get()) {
                if (wasCrouchMeditating) {
                    stopCrouchMeditation();
                }
                return;
            }

            // Crouch-meditation requires: key held + player crouching + player not moving
            boolean keyHeld = CROUCH_MEDITATE.isDown();
            boolean isCrouching = player.isShiftKeyDown();
            boolean isMoving = player.getDeltaMovement().horizontalDistanceSqr() > 0.01;

            boolean shouldMeditate = keyHeld && isCrouching && !isMoving;

            if (shouldMeditate && !wasCrouchMeditating) {
                startCrouchMeditation();
            } else if (!shouldMeditate && wasCrouchMeditating) {
                stopCrouchMeditation();
            }
        }
    }

    private static void startCrouchMeditation() {
        wasCrouchMeditating = true;
        CrouchMeditationState.setCrouchMeditating(true);
        PerkNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                new CrouchMeditationPacket(true)
        );
    }

    private static void stopCrouchMeditation() {
        wasCrouchMeditating = false;
        CrouchMeditationState.setCrouchMeditating(false);
        PerkNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                new CrouchMeditationPacket(false)
        );
    }
}
