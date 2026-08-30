package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Named UI-sound presets for the config screen's "Sound Cycler" (see
 * ThemeSettingsPopup) - structurally mirrors {@link ColorPresets} (same
 * next/prev-cycle shape, same "id -> display name" map, same "not found ->
 * fall back to the first entry" behaviour) since that class is this mod's
 * own proven pattern for a cyclable named-option list, just swapping ARGB
 * ints for the sound ids registered in {@link UiSounds}.
 *
 * "None" (id {@code ""}) is a real, always-present first entry meaning
 * silence - every one of the four sound slots (tab hover, entry hover,
 * click, config open) can be turned off independently without a separate
 * master mute, for the player who wants only one or two of the four.
 */
public final class SoundPresets {

    /** Ordered id -> display name. Order here is the cycle order. */
    public static final Map<String, String> PRESETS = new LinkedHashMap<>();

    static {
        PRESETS.put("", "None (silent)");
        PRESETS.put("lasso_fast_whip", "Lasso Whip (fast)");
        PRESETS.put("bonus_whip", "Bonus Whip");
        PRESETS.put("laser_game_whip", "Laser Game Whip");
        PRESETS.put("cinematic_laser_swoosh", "Cinematic Laser Swoosh");
        PRESETS.put("cinematic_whoosh_transition", "Cinematic Whoosh");
        PRESETS.put("flying_fast_swoosh", "Flying Fast Swoosh");
        PRESETS.put("tape_rewind_transition", "Tape Rewind Transition");
        PRESETS.put("futuristic_metal_sweep", "Futuristic Metal Sweep");
        PRESETS.put("drone_logo_presentation", "Drone Logo Presentation");
        PRESETS.put("magic_notification_ring", "Magic Notification Ring");
        PRESETS.put("small_electric_glitch", "Small Electric Glitch");
        PRESETS.put("toy_telephone_ring", "Toy Telephone Ring");
        PRESETS.put("office_telephone_ring", "Office Telephone Ring");
        PRESETS.put("camera_autofocus", "Camera Autofocus");
        PRESETS.put("camera_lens_shutter", "Camera Lens Shutter");
        PRESETS.put("camera_long_shutter", "Camera Long Shutter");
    }

    /** Display name for an id, or the id itself if somehow not in the map (defensive - shouldn't happen from the cycler). */
    public static String displayName(String id) {
        if (id == null) id = "";
        String n = PRESETS.get(id);
        return n != null ? n : id;
    }

    public static List<String> idsInOrder() {
        return new ArrayList<>(PRESETS.keySet());
    }

    public static String getNextId(String currentId) {
        List<String> ids = idsInOrder();
        int idx = ids.indexOf(currentId == null ? "" : currentId);
        if (idx == -1) return ids.get(0);
        return ids.get((idx + 1) % ids.size());
    }

    public static String getPrevId(String currentId) {
        List<String> ids = idsInOrder();
        int idx = ids.indexOf(currentId == null ? "" : currentId);
        if (idx == -1) return ids.get(0);
        return ids.get((idx - 1 + ids.size()) % ids.size());
    }
}
