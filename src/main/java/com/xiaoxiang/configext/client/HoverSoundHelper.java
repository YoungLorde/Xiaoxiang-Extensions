package com.xiaoxiang.configext.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Plays subtle hover sounds when scrolling over highlighted UI elements.
 * Different sound types for different UI sections to provide audio feedback.
 * Throttled to avoid sound spam when moving mouse rapidly.
 */
public class HoverSoundHelper {

    private static long lastSoundTime = 0;
    private static int lastHoverIndex = -1;
    private static final long MIN_SOUND_INTERVAL_MS = 80; // throttle

    /** Sound types for different UI sections. */
    public enum SoundType {
        CATEGORY,    // mod/category panel - low chime
        SUBCATEGORY, // sub-category panel - medium chime
        ITEM,        // item list - soft click
        SELECTED,    // selected items - bell
        TAB,         // top-level tab - chime
        BUTTON,      // clickable button - click
        PERK         // perk entry - magical chime
    }

    /**
     * Play a hover sound for the given index, but only if:
     * - The index changed (moved to a different item)
     * - Enough time has passed since the last sound (throttle)
     */
    public static void playHoverSound(SoundType type, int hoverIndex) {
        if (hoverIndex == lastHoverIndex) return;
        lastHoverIndex = hoverIndex;
        long now = System.currentTimeMillis();
        if (now - lastSoundTime < MIN_SOUND_INTERVAL_MS) return;
        lastSoundTime = now;
        playSound(type);
    }

    /** Reset the hover tracking (e.g. when leaving a panel). */
    public static void resetHover() {
        lastHoverIndex = -1;
    }

    private static void playSound(SoundType type) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            float volume = 0.15f;
            float pitch;
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();
            switch (type) {
                case CATEGORY:
                    pitch = 0.8f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume * 0.5f, pitch, false);
                    break;
                case SUBCATEGORY:
                    pitch = 1.0f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume * 0.4f, pitch, false);
                    break;
                case ITEM:
                    pitch = 1.2f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume * 0.2f, pitch, false);
                    break;
                case SELECTED:
                    pitch = 1.5f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, volume * 0.3f, pitch, false);
                    break;
                case TAB:
                    pitch = 1.1f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume * 0.4f, pitch, false);
                    break;
                case BUTTON:
                    pitch = 1.0f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume * 0.3f, pitch, false);
                    break;
                case PERK:
                    pitch = 1.3f;
                    mc.level.playLocalSound(x, y, z, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, volume * 0.2f, pitch, false);
                    break;
            }
        } catch (Throwable ignored) {
            // Sound system not available
        }
    }
}
