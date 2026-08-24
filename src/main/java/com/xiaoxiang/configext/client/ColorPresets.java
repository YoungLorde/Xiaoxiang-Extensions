package com.xiaoxiang.configext.client;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Named color presets for the cultivation panel UI configuration.
 * Instead of typing raw ARGB integers, users can pick from named colors.
 */
public final class ColorPresets {

    /** Maps a human-readable color name to its ARGB integer value. */
    public static final Map<String, Integer> PRESETS = new LinkedHashMap<>();

    static {
        // ── Reds ──
        PRESETS.put("Crimson", 0xFFDC143C);
        PRESETS.put("Firebrick", 0xFFB22222);
        PRESETS.put("Dark Red", 0xFF8B0000);
        PRESETS.put("Indian Red", 0xFFCD5C5C);
        PRESETS.put("Salmon", 0xFFFA8072);
        PRESETS.put("Vermillion", 0xFFE34234);
        PRESETS.put("Rose", 0xFFFF007F);

        // ── Oranges ──
        PRESETS.put("Orange", 0xFFFFA500);
        PRESETS.put("Dark Orange", 0xFFFF8C00);
        PRESETS.put("Coral", 0xFFFF7F50);
        PRESETS.put("Tomato", 0xFFFF6347);
        PRESETS.put("Amber", 0xFFFFBF00);
        PRESETS.put("Pumpkin", 0xFFFF7518);

        // ── Yellows ──
        PRESETS.put("Gold", 0xFFFFD700);
        PRESETS.put("Yellow", 0xFFFFFF00);
        PRESETS.put("Light Yellow", 0xFFFFFFE0);
        PRESETS.put("Khaki", 0xFFF0E68C);
        PRESETS.put("Goldenrod", 0xFFDAA520);

        // ── Greens ──
        PRESETS.put("Lime", 0xFF00FF00);
        PRESETS.put("Forest Green", 0xFF228B22);
        PRESETS.put("Sea Green", 0xFF2E8B57);
        PRESETS.put("Spring Green", 0xFF00FF7F);
        PRESETS.put("Emerald", 0xFF50C878);
        PRESETS.put("Olive", 0xFF808000);
        PRESETS.put("Jade", 0xFF00A86B);
        PRESETS.put("Mint", 0xFF98FF98);
        PRESETS.put("Sage", 0xFF9CAF88);

        // ── Cyans / Teals ──
        PRESETS.put("Cyan", 0xFF00FFFF);
        PRESETS.put("Teal", 0xFF008080);
        PRESETS.put("Turquoise", 0xFF40E0D0);
        PRESETS.put("Aquamarine", 0xFF7FFFD4);
        PRESETS.put("Dark Cyan", 0xFF008B8B);

        // ── Blues ──
        PRESETS.put("Blue", 0xFF0000FF);
        PRESETS.put("Royal Blue", 0xFF4169E1);
        PRESETS.put("Sky Blue", 0xFF87CEEB);
        PRESETS.put("Deep Sky Blue", 0xFF00BFFF);
        PRESETS.put("Steel Blue", 0xFF4682B4);
        PRESETS.put("Navy", 0xFF000080);
        PRESETS.put("Midnight Blue", 0xFF191970);
        PRESETS.put("Cobalt", 0xFF0047AB);
        PRESETS.put("Azure", 0xFF007FFF);

        // ── Purples / Violets ──
        PRESETS.put("Purple", 0xFF800080);
        PRESETS.put("Violet", 0xFFEE82EE);
        PRESETS.put("Indigo", 0xFF4B0082);
        PRESETS.put("Dark Violet", 0xFF9400D3);
        PRESETS.put("Orchid", 0xFFDA70D6);
        PRESETS.put("Plum", 0xFFDDA0DD);
        PRESETS.put("Magenta", 0xFFFF00FF);
        PRESETS.put("Lavender", 0xFFB57EDC);
        PRESETS.put("Amethyst", 0xFF9966CC);

        // ── Pinks ──
        PRESETS.put("Pink", 0xFFFFC0CB);
        PRESETS.put("Hot Pink", 0xFFFF69B4);
        PRESETS.put("Deep Pink", 0xFFFF1493);
        PRESETS.put("Blush", 0xFFDE5D83);

        // ── Browns / Earth Tones ──
        PRESETS.put("Brown", 0xFFA52A2A);
        PRESETS.put("Saddle Brown", 0xFF8B4513);
        PRESETS.put("Chocolate", 0xFFD2691E);
        PRESETS.put("Tan", 0xFFD2B48C);
        PRESETS.put("Wheat", 0xFFF5DEB3);
        PRESETS.put("Bronze", 0xFFCD7F32);
        PRESETS.put("Terracotta", 0xFFE2725B);

        // ── Whites / Grays / Blacks ──
        PRESETS.put("White", 0xFFFFFFFF);
        PRESETS.put("Snow", 0xFFFFFAFA);
        PRESETS.put("Ivory", 0xFFFFFFF0);
        PRESETS.put("Silver", 0xFFC0C0C0);
        PRESETS.put("Light Gray", 0xFFD3D3D3);
        PRESETS.put("Gray", 0xFF808080);
        PRESETS.put("Dim Gray", 0xFF696969);
        PRESETS.put("Dark Gray", 0xFF404040);
        PRESETS.put("Charcoal", 0xFF36454F);
        PRESETS.put("Black", 0xFF000000);

        // ── Cultivation-themed colors (defaults from original mod) ──
        PRESETS.put("Ink Black (Default)", -16448509);
        PRESETS.put("Soft Ink (Default)", -12766422);
        PRESETS.put("Gold Text (Default)", -1456016);
        PRESETS.put("Vermillion Accent (Default)", -4703686);
        PRESETS.put("Border Light (Default)", -2504802);
        PRESETS.put("Border Dark (Default)", -10859978);
        PRESETS.put("Page Bg (Default)", -923956);
        PRESETS.put("Panel Bg (Default)", -1517128);
    }

    /**
     * Find the closest named preset for a given ARGB color value.
     * Useful for showing the current color name in the UI.
     */
    public static String getClosestName(int argb) {
        String closest = "Custom";
        double minDist = Double.MAX_VALUE;
        for (Map.Entry<String, Integer> e : PRESETS.entrySet()) {
            double dist = colorDistance(argb, e.getValue());
            if (dist < minDist) {
                minDist = dist;
                closest = e.getKey();
            }
        }
        return closest;
    }

    private static double colorDistance(int c1, int c2) {
        int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
    }

    /** Convert an ARGB int to a hex string like #RRGGBB. */
    public static String toHex(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /** Convert a hex string like #RRGGBB to an ARGB int (with full alpha). */
    public static int fromHex(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() == 6) {
            return 0xFF000000 | Integer.parseInt(hex, 16);
        }
        return Integer.parseInt(hex, 16);
    }

    /** Find the exact preset name for a given ARGB value, or null if no exact match. */
    public static String findName(int argb) {
        for (Map.Entry<String, Integer> e : PRESETS.entrySet()) {
            if (e.getValue() == argb) return e.getKey();
        }
        return null;
    }

    /** Get the next preset color after the current one. Cycles back to first if at end. */
    public static int getNextPreset(int currentColor) {
        java.util.List<Integer> values = new java.util.ArrayList<>(PRESETS.values());
        // Find current index
        int idx = -1;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == currentColor) {
                idx = i;
                break;
            }
        }
        // If not found, return first preset
        if (idx == -1) return values.get(0);
        // Return next, or wrap to first
        int nextIdx = (idx + 1) % values.size();
        return values.get(nextIdx);
    }

    /** Get the previous preset color before the current one. */
    public static int getPrevPreset(int currentColor) {
        java.util.List<Integer> values = new java.util.ArrayList<>(PRESETS.values());
        int idx = -1;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == currentColor) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return values.get(0);
        int prevIdx = (idx - 1 + values.size()) % values.size();
        return values.get(prevIdx);
    }

    /** Get all preset names in order. */
    public static java.util.List<String> getPresetNames() {
        return new java.util.ArrayList<>(PRESETS.keySet());
    }

    /** Get the ARGB value for a preset name. */
    public static int getPresetValue(String name) {
        Integer val = PRESETS.get(name);
        return val != null ? val : -1;
    }
}
