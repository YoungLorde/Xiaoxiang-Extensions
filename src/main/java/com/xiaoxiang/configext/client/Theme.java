package com.xiaoxiang.configext.client;

/**
 * Central colour palette + small colour helpers for the config screen chrome.
 *
 * Before this class existed the same handful of ARGB literals were re-typed at
 * dozens of call sites in CustomConfigScreen (0xFF303040, 0xFF606080, 0xFFFFD700,
 * ...), which is how the tab bar, sub-tab bar, group headers and entry rows all
 * ended up subtly different shades of "nearly the same" colour. Every piece of
 * screen chrome now names the role it is painting instead of a raw hex value.
 *
 * NOTE: popup classes (ItemPickerPopup, ColorWheelPopup, ...) intentionally keep
 * their own colours - this class only covers CustomConfigScreen's own chrome.
 */
public final class Theme {

    private Theme() {}

    // ── Window / surfaces ────────────────────────────────────────────────
    /** Full-screen window plate behind everything. */
    public static final int BACKGROUND         = 0xE00E0E18;
    /** Outer window border (bright). */
    public static final int WINDOW_BORDER      = 0xFFA0A060;
    /** Inner window border (dim). */
    public static final int WINDOW_BORDER_DIM  = 0xFF5A5A3C;

    /** Default panel fill. */
    public static final int PANEL              = 0xFF181820;
    /** Slightly lighter panel fill (alternating rows, headers). */
    public static final int PANEL_RAISED       = 0xFF23232E;
    /** Panel border. */
    public static final int PANEL_BORDER       = 0xFF3C3C56;
    /** Panel border, brighter variant. */
    public static final int PANEL_BORDER_LIGHT = 0xFF5C5C7E;

    // ── Primary accent: gold (top-level tabs, titles) ────────────────────
    public static final int ACCENT             = 0xFFFFD700;
    public static final int ACCENT_HOVER       = 0xFFFFF080;
    public static final int ACCENT_DIM         = 0xFF806000;
    public static final int ACCENT_BG          = 0xFF2A2000;
    public static final int ACCENT_BG_ACTIVE   = 0xFF3D2B00;
    public static final int ACCENT_BG_HOVER    = 0xFF5A4A00;

    // ── Secondary accent: jade (sub-tabs) ────────────────────────────────
    public static final int ACCENT2            = 0xFF30A040;
    public static final int ACCENT2_HOVER      = 0xFF70C088;
    public static final int ACCENT2_BG         = 0xFF141820;
    public static final int ACCENT2_BG_ACTIVE  = 0xFF153020;
    public static final int ACCENT2_BG_HOVER   = 0xFF25404E;

    // ── Tertiary accent: cinnabar (group buttons / 3rd level nav) ────────
    public static final int ACCENT3            = 0xFFFF8060;
    public static final int ACCENT3_HOVER      = 0xFFFFA080;
    public static final int ACCENT3_BG         = 0xFF2A0000;
    public static final int ACCENT3_BG_ACTIVE  = 0xFF3D0000;
    public static final int ACCENT3_BG_HOVER   = 0xFF5A0800;
    public static final int ACCENT3_BORDER     = 0xFF804040;
    public static final int ACCENT3_BORDER_ON  = 0xFFFF3030;

    // ── Group headers ───────────────────────────────────────────────────
    public static final int HEADER_BG          = 0xFF2A2A3E;
    public static final int HEADER_BG_HOVER    = 0xFF35354E;
    public static final int HEADER_BORDER      = 0xFF50507E;
    public static final int HEADER_TEXT        = 0xFFFFE060;
    public static final int HEADER_ARROW       = 0xFF60D0FF;

    // ── Entry rows ──────────────────────────────────────────────────────
    public static final int ROW_BG_EVEN        = 0x30202020;
    public static final int ROW_BG_ODD         = 0x30303030;
    public static final int ROW_BG_HOVER       = 0x20FFFF80;
    public static final int ROW_BORDER_HOVER   = 0xFFA0A060;
    public static final int ROW_BG_SELECTED    = 0x4030A0FF;
    public static final int ROW_BORDER_SEL     = 0xFF30A0FF;
    public static final int ROW_BORDER_SEL_IN  = 0xFF60C0FF;

    // ── Text ────────────────────────────────────────────────────────────
    public static final int TEXT_PRIMARY       = 0xFFFFFFFF;
    public static final int TEXT_MUTED         = 0xFFAAAAAA;
    public static final int TEXT_FAINT         = 0xFF808080;
    public static final int TEXT_ON_ACCENT     = 0xFFFFE040;

    // ── Semantic ────────────────────────────────────────────────────────
    public static final int SUCCESS            = 0xFF55FF55;
    public static final int SUCCESS_BG         = 0xFF205020;
    public static final int WARNING            = 0xFFFFD700;
    public static final int ERROR              = 0xFFE34234;
    public static final int ERROR_BG           = 0xFF502020;
    public static final int FAVORITE           = 0xFFFFD700;
    public static final int LOCKED             = 0xFFE34234;
    public static final int INFO               = 0xFF40C0FF;

    // ── Scrollbar ───────────────────────────────────────────────────────
    public static final int SCROLL_TRACK       = 0x40404040;
    public static final int SCROLL_THUMB       = 0x90909090;
    public static final int SCROLL_THUMB_HOT   = 0xC0C0C0C0;

    // ── Utility button row (bottom-left) ────────────────────────────────
    public static final int UTIL_BG            = 0xFF202030;
    public static final int UTIL_BG_HOVER      = 0xFF303050;
    public static final int UTIL_BORDER        = 0xFF404060;
    public static final int UTIL_BORDER_HOVER  = 0xFF80A0FF;
    public static final int UTIL_TEXT          = 0xFFA0A0C0;
    public static final int UTIL_ON_BG         = 0xFF203020;
    public static final int UTIL_ON_BG_HOVER   = 0xFF305030;
    public static final int UTIL_ON_BORDER     = 0xFF60FF80;
    public static final int UTIL_ON_TEXT       = 0xFF80FFA0;

    // ── Helpers ─────────────────────────────────────────────────────────

    /** Multiply an ARGB colour's alpha channel by {@code factor} (clamped to 0..1). */
    public static int withAlpha(int argb, float factor) {
        if (factor >= 1.0f) return argb;
        if (factor <= 0.0f) return argb & 0x00FFFFFF;
        int a = (argb >>> 24) & 0xFF;
        a = (int) (a * factor);
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    /** Replace an ARGB colour's alpha channel with an explicit 0..255 value. */
    public static int setAlpha(int argb, int alpha) {
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

    /** Linear blend between two ARGB colours. {@code t} of 0 returns {@code a}. */
    public static int lerp(int a, int b, float t) {
        if (t <= 0.0f) return a;
        if (t >= 1.0f) return b;
        int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int) (aa + (ba - aa) * t);
        int rr = (int) (ar + (br - ar) * t);
        int rg = (int) (ag + (bg - ag) * t);
        int rb = (int) (ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    /**
     * Fade a text colour towards fully transparent. Minecraft's font renderer
     * treats an alpha of 0 as "opaque white" in some paths, so never return a
     * completely zero alpha - clamp to a minimum of 4.
     */
    public static int textAlpha(int argb, float factor) {
        int a = (argb >>> 24) & 0xFF;
        if (a == 0) a = 0xFF; // callers often pass 0xRRGGBB literals
        int scaled = (int) (a * factor);
        if (scaled < 4) scaled = 4;
        if (scaled > 255) scaled = 255;
        return (scaled << 24) | (argb & 0x00FFFFFF);
    }
}
