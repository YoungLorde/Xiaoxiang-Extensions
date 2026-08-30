package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

/**
 * Plays the config screen's four custom UI sound slots (tab hover, entry
 * hover, tab click, config open) - see {@link UiSounds} for the registered
 * sound events themselves, {@link SoundPresets} for the id-to-name cycling
 * list shown in the Sound Cycler, and ExtendedConfig's "uiSound" section for
 * the per-slot id / volume / enabled settings this class reads.
 *
 * Playback uses {@code Minecraft.getInstance().getSoundManager().play(
 * SimpleSoundInstance.forUI(soundEvent, pitch, volume))} rather than the
 * {@code mc.level.playLocalSound(...)} pattern {@link HoverSoundHelper}
 * (this mod's other, proven, sound helper) uses for its in-game hover
 * chimes. That is a deliberate choice, not an oversight: {@code mc.level}
 * and {@code mc.player} are both null when the config screen is opened from
 * the main menu's mod list (no world loaded yet), which is exactly the
 * situation the "config open" sound has to work in.
 * {@code SimpleSoundInstance.forUI(...)} is the standard Forge/vanilla
 * factory for a non-positional UI sound and does not require a level.
 *
 * HONEST CAVEAT: unlike the DeferredRegister registration mechanics in
 * UiSounds.java (bytecode-verified against the base mod's own ModItems
 * class), this exact playback call - both {@code SimpleSoundInstance.forUI}
 * and {@code SoundManager.play} - could NOT be bytecode-verified in this
 * sandbox: no vanilla/Forge Minecraft jar is present to javap
 * {@code net.minecraft.client.resources.sounds.SimpleSoundInstance} or
 * {@code net.minecraft.client.sounds.SoundManager}, and no other code in
 * either codebase calls them. This is the standard, widely-documented idiom
 * for playing a one-shot UI sound in Forge 1.20.1, but it rests on general
 * API knowledge rather than in-project proof - flagging this plainly rather
 * than presenting it as verified. (This is the second such unverified
 * surface in the sound system; the first is
 * {@code SoundEvent.createVariableRangeEvent} in UiSounds.java.)
 */
public final class UiSoundHelper {

    private UiSoundHelper() {}

    // Edge-detection state for the two hover slots, mirroring
    // HoverSoundHelper's own lastHoverIndex/lastSoundTime pattern so
    // re-hovering the same tab/row doesn't spam the sound. Tab hover and
    // entry hover get their own independent index so hovering a tab and then
    // an entry row never suppresses each other's sound. Callers are
    // responsible for encoding a hover target as a stable int (e.g. a tab
    // index, or tabIndex * 10000 + rowIndex for entries so switching tabs
    // always counts as a fresh hover even if the row index repeats).
    private static int lastTabHoverIndex = -1;
    private static int lastEntryHoverIndex = -1;

    /** Call when a hover edge is detected on the main tab strip; pass -1 when the mouse leaves it. */
    public static void onTabHover(int tabIndex) {
        if (tabIndex == lastTabHoverIndex) return;
        lastTabHoverIndex = tabIndex;
        if (tabIndex < 0) return; // don't play a sound for the mouse *leaving* a tab
        play(ExtendedConfig.CLIENT_UI_SOUND_TAB_HOVER.get(), 1.0f);
    }

    /** Call when a hover edge is detected on a configurable entry row; pass -1 when the mouse leaves it. */
    public static void onEntryHover(int entryIndex) {
        if (entryIndex == lastEntryHoverIndex) return;
        lastEntryHoverIndex = entryIndex;
        if (entryIndex < 0) return;
        play(ExtendedConfig.CLIENT_UI_SOUND_ENTRY_HOVER.get(), 1.0f);
    }

    /** Call once per actual click on a main tab or sub-tab. Not hover-edge-guarded - every click plays. */
    public static void onTabClick() {
        play(ExtendedConfig.CLIENT_UI_SOUND_TAB_CLICK.get(), 1.0f);
    }

    /**
     * Call exactly once per config-screen open (e.g. from the screen's
     * constructor, which - unlike {@code init()} - genuinely runs only once
     * per screen instance even across window resizes).
     */
    public static void onConfigOpen() {
        play(ExtendedConfig.CLIENT_UI_SOUND_CONFIG_OPEN.get(), 1.0f);
    }

    /**
     * Reset hover-edge tracking. Call when the config screen closes (or from
     * its constructor before {@link #onConfigOpen()}) so stale hover state
     * from a previous screen instance can't suppress the first real hover
     * sound of a fresh one.
     */
    public static void resetHoverState() {
        lastTabHoverIndex = -1;
        lastEntryHoverIndex = -1;
    }

    /**
     * Play an arbitrary preset sound id at a given volume multiplier
     * (0.0-1.0), ignoring the per-slot config entirely. Used by the Sound
     * Cycler's own preview/play button so players can sample a sound before
     * assigning it to a slot, independent of whether that slot (or sounds
     * overall) is currently enabled.
     */
    public static void preview(String soundId, float volumeMultiplier) {
        try {
            if (soundId == null || soundId.isEmpty()) return;
            RegistryObject<SoundEvent> ro = UiSounds.BY_ID.get(soundId);
            if (ro == null || !ro.isPresent()) return;
            float volume = Math.max(0.0f, Math.min(1.0f, volumeMultiplier));
            Minecraft mc = Minecraft.getInstance();
            mc.getSoundManager().play(SimpleSoundInstance.forUI(ro.get(), 1.0f, volume));
        } catch (Throwable ignored) {
            // Sound system not available - never let a preview failure break the popup.
        }
    }

    private static void play(String soundId, float pitch) {
        try {
            if (!ExtendedConfig.CLIENT_UI_SOUND_ENABLED.get()) return;
            if (soundId == null || soundId.isEmpty()) return;
            RegistryObject<SoundEvent> ro = UiSounds.BY_ID.get(soundId);
            if (ro == null || !ro.isPresent()) return;
            int volumePercent = ExtendedConfig.CLIENT_UI_SOUND_VOLUME_PERCENT.get();
            if (volumePercent <= 0) return;
            float volume = volumePercent / 100.0f;
            Minecraft mc = Minecraft.getInstance();
            mc.getSoundManager().play(SimpleSoundInstance.forUI(ro.get(), pitch, volume));
        } catch (Throwable ignored) {
            // Sound system not available (e.g. very early client startup) - never let a UI
            // sound failure break the config screen itself.
        }
    }
}
