package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;

/**
 * Central colour palette + small colour helpers for the config screen chrome,
 * now backing a switchable multi-theme system.
 *
 * Before this class existed the same handful of ARGB literals were re-typed at
 * dozens of call sites in CustomConfigScreen (0xFF303040, 0xFF606080, 0xFFFFD700,
 * ...), which is how the tab bar, sub-tab bar, group headers and entry rows all
 * ended up subtly different shades of "nearly the same" colour. Every piece of
 * screen chrome names the role it is painting instead of a raw hex value.
 *
 * Every field below used to be {@code public static final}. Making them plain
 * {@code public static} (still the same names, same type, same call sites
 * throughout CustomConfigScreen.java) is what lets {@link #applyTheme(ThemeId)}
 * repaint the whole config screen at runtime: every existing {@code Theme.XXX}
 * read elsewhere in the mod becomes a live field read instead of a compile-time
 * inlined constant, so flipping the active theme takes effect immediately
 * without touching any of those call sites.
 *
 * Design notes on how each theme's palette was produced (not fabricated by
 * hand-picking 60+ unrelated hex values per theme, which is how real UI kits
 * drift out of visual coherence): each theme starts from a small set of seed
 * colours (background, panel, panel border, window border, 4 accent hues, 2-3
 * text tones) and every hover/dim/bg/active/border variant is mechanically
 * derived from those seeds with the same lighten/darken (HLS-space, so hue and
 * saturation are preserved instead of washing out toward grey) and blend
 * operations the classic palette's own hand-tuned relationships were checked
 * against. SUCCESS/SUCCESS_BG/ERROR/ERROR_BG/LOCKED, the scrollbar colours, and
 * the utility-row "on" state are intentionally left identical across every
 * theme - those are affordance colours (green = good/on, red = bad/locked)
 * that need to stay recognizable no matter which visual skin is active.
 *
 * NOTE: popup classes (ItemPickerPopup, ColorWheelPopup, ...) intentionally keep
 * their own colours - this class only covers CustomConfigScreen's own chrome.
 */
public final class Theme {

    private Theme() {}

    /**
     * Structural border/frame treatment a theme paints its window/panel outlines
     * with - see ThemeFrameRenderer for what each style actually draws. This is
     * the direct answer to "different theme means the UI has slightly different
     * changes to its structure... maybe the box outlines": themes sharing a
     * FrameStyle share one drawing routine (an honest scaling tradeoff across 30
     * themes - families of real structural variety rather than 30 bespoke
     * one-off outlines - documented at ThemeFrameRenderer's own class doc).
     */
    public enum FrameStyle {
        /** The original double-line rectangle outline (renderOutline twice). */
        DOUBLE_LINE,
        /** Torn/burnt scroll edges with roller bars top and bottom. */
        SCROLL,
        /** Sci-fi HUD corner brackets, no full border. */
        NEON,
        /** Double-ruled framing with small corner serifs, like ruled paper. */
        MANUSCRIPT,
        /** Jagged/spiky border ticks. */
        JAGGED,
        /** Smooth rounded-look corners (corner-cut double outline). */
        SMOOTH,
        /** Formation-array tick marks perpendicular to the border + corner circles. */
        TALISMAN,
        /** Soft layered outer glow, no hard edge. */
        GLOW,
        /** Thick chunky pixelated/segmented border. */
        RETRO,
        /** Pointed-arch mitred corner accents. */
        GOTHIC,
        /** Triangle/zigzag pattern ticks along the border. */
        TRIBAL
    }

    /**
     * Animation "feel" a theme uses for its entrance transitions and accents -
     * see AnimationState for how each family changes easing/pacing. Direct
     * answer to "different themes gonna have different animations... spacey
     * feel, futuristic, old school, medieval...".
     */
    public enum AnimFamily {
        /** The original easeOutCubic slide-and-fade. */
        STANDARD,
        /** Gentler, slightly slower ease - ink settling onto paper. */
        FLOWING,
        /** Snappy, quick-settle - electric/neon energy. */
        PULSE_FAST,
        /** Slow, deliberate, weighty. */
        STEADY,
        /** Slight overshoot/jitter before settling - unstable, demonic. */
        ERRATIC,
        /** Very slow, minimal motion - serene/immortal. */
        SERENE,
        /** Strong per-row stagger, geometric snap - formation array assembling. */
        FORMATION,
        /** Pure fade, no slide - drifting into being. */
        DRIFT
    }

    /** Every visual skin the config screen chrome can be painted with. */
    public enum ThemeId {
        CLASSIC("classic", "Classic (Jade & Gold)", FrameStyle.DOUBLE_LINE, AnimFamily.STANDARD),
        CULTIVATION_SCROLL("cultivation_scroll", "Cultivation Scroll", FrameStyle.SCROLL, AnimFamily.FLOWING),
        CELESTIAL_NEON("celestial_neon", "Celestial Neon", FrameStyle.NEON, AnimFamily.PULSE_FAST),
        MEDIEVAL_MANUSCRIPT("medieval_manuscript", "Medieval Manuscript", FrameStyle.MANUSCRIPT, AnimFamily.STEADY),
        DEMONIC_BLOOD("demonic_blood", "Demonic Blood Path", FrameStyle.JAGGED, AnimFamily.ERRATIC),
        CELESTIAL_JADE("celestial_jade", "Immortal Jade Court", FrameStyle.SMOOTH, AnimFamily.SERENE),
        FORMATION_TALISMAN("formation_talisman", "Formation Talisman", FrameStyle.TALISMAN, AnimFamily.FORMATION),
        VOID_ABYSS("void_abyss", "Void Tribulation Abyss", FrameStyle.GLOW, AnimFamily.DRIFT),
        STELLAR_FORGE("stellar_forge", "Stellar Forge", FrameStyle.NEON, AnimFamily.PULSE_FAST),
        QUANTUM_LOTUS("quantum_lotus", "Quantum Lotus", FrameStyle.NEON, AnimFamily.DRIFT),
        NEBULA_DRIFT("nebula_drift", "Nebula Drift", FrameStyle.GLOW, AnimFamily.DRIFT),
        CHRONO_CIRCUIT("chrono_circuit", "Chrono Circuit", FrameStyle.TALISMAN, AnimFamily.FORMATION),
        EIGHT_BIT_SECT("eight_bit_sect", "8-Bit Sect", FrameStyle.RETRO, AnimFamily.STANDARD),
        ARCADE_CULTIVATOR("arcade_cultivator", "Arcade Cultivator", FrameStyle.RETRO, AnimFamily.PULSE_FAST),
        PARCHMENT_PIXEL("parchment_pixel", "Parchment Pixel", FrameStyle.RETRO, AnimFamily.STEADY),
        GOTHIC_CATHEDRAL("gothic_cathedral", "Gothic Cathedral", FrameStyle.GOTHIC, AnimFamily.STEADY),
        KNIGHTS_LEDGER("knights_ledger", "Knight's Ledger", FrameStyle.MANUSCRIPT, AnimFamily.STANDARD),
        DRAGONS_HOARD("dragons_hoard", "Dragon's Hoard", FrameStyle.GOTHIC, AnimFamily.SERENE),
        PORCELAIN_BLUE_WHITE("porcelain_blue_white", "Porcelain Blue & White", FrameStyle.SCROLL, AnimFamily.FLOWING),
        INK_WASH("ink_wash", "Ink Wash", FrameStyle.SCROLL, AnimFamily.SERENE),
        CHERRY_BLOSSOM("cherry_blossom", "Cherry Blossom", FrameStyle.SMOOTH, AnimFamily.FLOWING),
        IMPERIAL_VERMILLION("imperial_vermillion", "Imperial Vermillion", FrameStyle.MANUSCRIPT, AnimFamily.STEADY),
        BAMBOO_GROVE("bamboo_grove", "Bamboo Grove", FrameStyle.SCROLL, AnimFamily.FLOWING),
        SAVANNA_DUSK("savanna_dusk", "Savanna Dusk", FrameStyle.TRIBAL, AnimFamily.DRIFT),
        MASK_AND_DRUM("mask_and_drum", "Mask & Drum", FrameStyle.TRIBAL, AnimFamily.FORMATION),
        ANCESTRAL_FIRE("ancestral_fire", "Ancestral Fire", FrameStyle.TRIBAL, AnimFamily.PULSE_FAST),
        KENTE_WEAVE("kente_weave", "Kente Weave", FrameStyle.TRIBAL, AnimFamily.STANDARD),
        DESERT_SHAMAN("desert_shaman", "Desert Shaman", FrameStyle.TRIBAL, AnimFamily.SERENE),
        FROST_SANCTUM("frost_sanctum", "Frost Sanctum", FrameStyle.GLOW, AnimFamily.SERENE),
        OBSIDIAN_EMBER("obsidian_ember", "Obsidian Ember", FrameStyle.JAGGED, AnimFamily.PULSE_FAST),
        // Genuinely multi-hue rather than one-hue-plus-shades - see applyPrismSpectrum()'s
        // own doc for why, direct answer to "themes... that can have ten different colors".
        PRISM_SPECTRUM("prism_spectrum", "Prism Spectrum", FrameStyle.GLOW, AnimFamily.DRIFT),
        // ── Accessibility themes ── requested for a visually-impaired player and a
        // player sensitive to bright/flashy colour: "let's give them some high
        // contrast or low contrast and black and white... so there's no
        // discomfort". All three use a simple, uncluttered FrameStyle and a calm
        // AnimFamily (STEADY/SERENE) - deliberately never PULSE_FAST, ERRATIC or
        // FORMATION, which are exactly the fast/jittery/snappy animation feels a
        // motion- or light-sensitive player would want to avoid.
        MONOCHROME("monochrome", "Monochrome", FrameStyle.DOUBLE_LINE, AnimFamily.STEADY),
        HIGH_CONTRAST("high_contrast", "High Contrast", FrameStyle.DOUBLE_LINE, AnimFamily.STEADY),
        GENTLE_FOCUS("gentle_focus", "Gentle Focus (Low Contrast)", FrameStyle.SMOOTH, AnimFamily.SERENE);

        public final String configId;
        public final String displayName;
        public final FrameStyle frameStyle;
        public final AnimFamily animFamily;

        ThemeId(String configId, String displayName, FrameStyle frameStyle, AnimFamily animFamily) {
            this.configId = configId;
            this.displayName = displayName;
            this.frameStyle = frameStyle;
            this.animFamily = animFamily;
        }

        public static ThemeId byConfigId(String id) {
            if (id != null) {
                for (ThemeId t : values()) {
                    if (t.configId.equalsIgnoreCase(id.trim())) return t;
                }
            }
            return CLASSIC;
        }

        public ThemeId next() {
            ThemeId[] all = values();
            return all[(this.ordinal() + 1) % all.length];
        }
    }

    /** The theme currently painted. Kept in sync with {@code ExtendedConfig.CLIENT_UI_THEME}. */
    public static ThemeId current = ThemeId.CLASSIC;

    /**
     * Loads the persisted theme choice from config and applies it. Call once
     * when the config screen is first opened (idempotent - safe to call more
     * than once, e.g. every time the screen is (re)constructed).
     */
    public static void loadFromConfig() {
        ThemeId id = ThemeId.CLASSIC;
        try {
            id = ThemeId.byConfigId(ExtendedConfig.CLIENT_UI_THEME.get());
        } catch (Exception e) {
            // Config not loaded yet / read failed - fall back to classic rather than crash the screen.
        }
        applyTheme(id);
    }

    /** Switches the live palette to {@code id} and persists the choice to config. */
    public static void applyTheme(ThemeId id) {
        if (id == null) id = ThemeId.CLASSIC;
        current = id;
        switch (id) {
            case CULTIVATION_SCROLL: applyCultivationScroll(); break;
            case CELESTIAL_NEON: applyCelestialNeon(); break;
            case MEDIEVAL_MANUSCRIPT: applyMedievalManuscript(); break;
            case DEMONIC_BLOOD: applyDemonicBlood(); break;
            case CELESTIAL_JADE: applyCelestialJade(); break;
            case FORMATION_TALISMAN: applyFormationTalisman(); break;
            case VOID_ABYSS: applyVoidAbyss(); break;
            case STELLAR_FORGE: applyStellarForge(); break;
            case QUANTUM_LOTUS: applyQuantumLotus(); break;
            case NEBULA_DRIFT: applyNebulaDrift(); break;
            case CHRONO_CIRCUIT: applyChronoCircuit(); break;
            case EIGHT_BIT_SECT: applyEightBitSect(); break;
            case ARCADE_CULTIVATOR: applyArcadeCultivator(); break;
            case PARCHMENT_PIXEL: applyParchmentPixel(); break;
            case GOTHIC_CATHEDRAL: applyGothicCathedral(); break;
            case KNIGHTS_LEDGER: applyKnightsLedger(); break;
            case DRAGONS_HOARD: applyDragonsHoard(); break;
            case PORCELAIN_BLUE_WHITE: applyPorcelainBlueWhite(); break;
            case INK_WASH: applyInkWash(); break;
            case CHERRY_BLOSSOM: applyCherryBlossom(); break;
            case IMPERIAL_VERMILLION: applyImperialVermillion(); break;
            case BAMBOO_GROVE: applyBambooGrove(); break;
            case SAVANNA_DUSK: applySavannaDusk(); break;
            case MASK_AND_DRUM: applyMaskAndDrum(); break;
            case ANCESTRAL_FIRE: applyAncestralFire(); break;
            case KENTE_WEAVE: applyKenteWeave(); break;
            case DESERT_SHAMAN: applyDesertShaman(); break;
            case FROST_SANCTUM: applyFrostSanctum(); break;
            case OBSIDIAN_EMBER: applyObsidianEmber(); break;
            case PRISM_SPECTRUM: applyPrismSpectrum(); break;
            case MONOCHROME: applyMonochrome(); break;
            case HIGH_CONTRAST: applyHighContrast(); break;
            case GENTLE_FOCUS: applyGentleFocus(); break;
            case CLASSIC:
            default: applyClassic(); break;
        }
        applyAccentOverrideIfEnabled();
        try {
            ExtendedConfig.CLIENT_UI_THEME.set(id.configId);
        } catch (Exception e) {
            // Best-effort persistence; the in-memory palette is already switched either way.
        }
    }

    /**
     * Re-applies the user's custom accent override (config screen's own "config
     * for the config" panel) on top of whatever theme is currently active, if
     * enabled. Safe to call any time the theme or the override setting changes.
     */
    public static void applyAccentOverrideIfEnabled() {
        try {
            if (ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.get()) {
                int c = ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_COLOR.get();
                ACCENT = c;
                ACCENT_HOVER = lerp(c, 0xFFFFFFFF, 0.35f);
                ACCENT_DIM = lerp(c, 0xFF000000, 0.55f);
                HEADER_TEXT = lerp(c, 0xFFFFFFFF, 0.15f);
                WARNING = c;
                FAVORITE = c;
            }
        } catch (Exception e) {
            // Config not loaded yet - leave the theme's own accent untouched.
        }
    }

    // ── Window / surfaces ────────────────────────────────────────────────
    /** Full-screen window plate behind everything. */
    public static int BACKGROUND         = 0xE00E0E18;
    /** Outer window border (bright). */
    public static int WINDOW_BORDER      = 0xFFA0A060;
    /** Inner window border (dim). */
    public static int WINDOW_BORDER_DIM  = 0xFF5A5A3C;

    /** Default panel fill. */
    public static int PANEL              = 0xFF181820;
    /** Slightly lighter panel fill (alternating rows, headers). */
    public static int PANEL_RAISED       = 0xFF23232E;
    /** Panel border. */
    public static int PANEL_BORDER       = 0xFF3C3C56;
    /** Panel border, brighter variant. */
    public static int PANEL_BORDER_LIGHT = 0xFF5C5C7E;

    // ── Primary accent: gold (top-level tabs, titles) ────────────────────
    public static int ACCENT             = 0xFFFFD700;
    public static int ACCENT_HOVER       = 0xFFFFF080;
    public static int ACCENT_DIM         = 0xFF806000;
    public static int ACCENT_BG          = 0xFF2A2000;
    public static int ACCENT_BG_ACTIVE   = 0xFF3D2B00;
    public static int ACCENT_BG_HOVER    = 0xFF5A4A00;

    // ── Secondary accent: jade (sub-tabs) ────────────────────────────────
    public static int ACCENT2            = 0xFF30A040;
    public static int ACCENT2_HOVER      = 0xFF70C088;
    public static int ACCENT2_BG         = 0xFF141820;
    public static int ACCENT2_BG_ACTIVE  = 0xFF153020;
    public static int ACCENT2_BG_HOVER   = 0xFF25404E;

    // ── Tertiary accent: cinnabar (group buttons / 3rd level nav) ────────
    public static int ACCENT3            = 0xFFFF8060;
    public static int ACCENT3_HOVER      = 0xFFFFA080;
    public static int ACCENT3_BG         = 0xFF2A0000;
    public static int ACCENT3_BG_ACTIVE  = 0xFF3D0000;
    public static int ACCENT3_BG_HOVER   = 0xFF5A0800;
    public static int ACCENT3_BORDER     = 0xFF804040;
    public static int ACCENT3_BORDER_ON  = 0xFFFF3030;

    // ── Custom-tab accent: amethyst ────────────────────────────────────────
    // A different mod's tab, so it gets a visually distinct identity from the
    // gold/jade/cinnabar chrome every base-mod standard tab shares - used for
    // the top-level tab button, its sub-tab bar, and its group bar alike (one
    // consistent color across all three tiers of a custom tab), for any tab
    // CustomTabManager.isCustomTab() returns true for (Realm Expansion today,
    // any future "Xiao"-branded mod's tab the same way). Same animation calls
    // as the standard tabs (drawBreathingGlow/drawFlameBorder/drawEmbers/taiji
    // spin) - only the color arguments passed to them differ.
    public static int CUSTOM_ACCENT           = 0xFFB080FF;
    public static int CUSTOM_ACCENT_HOVER     = 0xFFD8C0FF;
    public static int CUSTOM_ACCENT_DIM       = 0xFF6A4A99;
    public static int CUSTOM_ACCENT_BG        = 0xFF1C1428;
    public static int CUSTOM_ACCENT_BG_ACTIVE = 0xFF2E1F45;
    public static int CUSTOM_ACCENT_BG_HOVER  = 0xFF3A2860;
    public static int CUSTOM_ACCENT_BORDER    = 0xFF6A4A99;
    public static int CUSTOM_ACCENT_BORDER_ON = 0xFFC080FF;

    // ── Group headers ───────────────────────────────────────────────────
    public static int HEADER_BG          = 0xFF2A2A3E;
    public static int HEADER_BG_HOVER    = 0xFF35354E;
    public static int HEADER_BORDER      = 0xFF50507E;
    public static int HEADER_TEXT        = 0xFFFFE060;
    public static int HEADER_ARROW       = 0xFF60D0FF;

    // ── Entry rows ──────────────────────────────────────────────────────
    public static int ROW_BG_EVEN        = 0x30202020;
    public static int ROW_BG_ODD         = 0x30303030;
    public static int ROW_BG_HOVER       = 0x20FFFF80;
    public static int ROW_BORDER_HOVER   = 0xFFA0A060;
    public static int ROW_BG_SELECTED    = 0x4030A0FF;
    public static int ROW_BORDER_SEL     = 0xFF30A0FF;
    public static int ROW_BORDER_SEL_IN  = 0xFF60C0FF;

    // ── Text ────────────────────────────────────────────────────────────
    public static int TEXT_PRIMARY       = 0xFFFFFFFF;
    public static int TEXT_MUTED         = 0xFFAAAAAA;
    public static int TEXT_FAINT         = 0xFF808080;
    public static int TEXT_ON_ACCENT     = 0xFFFFE040;

    // ── Semantic ────────────────────────────────────────────────────────
    public static int SUCCESS            = 0xFF55FF55;
    public static int SUCCESS_BG         = 0xFF205020;
    public static int WARNING            = 0xFFFFD700;
    public static int ERROR              = 0xFFE34234;
    public static int ERROR_BG           = 0xFF502020;
    public static int FAVORITE           = 0xFFFFD700;
    public static int LOCKED             = 0xFFE34234;
    public static int INFO               = 0xFF40C0FF;

    // ── Scrollbar ───────────────────────────────────────────────────────
    public static int SCROLL_TRACK       = 0x40404040;
    public static int SCROLL_THUMB       = 0x90909090;
    public static int SCROLL_THUMB_HOT   = 0xC0C0C0C0;

    // ── Utility button row (bottom-left) ────────────────────────────────
    public static int UTIL_BG            = 0xFF202030;
    public static int UTIL_BG_HOVER      = 0xFF303050;
    public static int UTIL_BORDER        = 0xFF404060;
    public static int UTIL_BORDER_HOVER  = 0xFF80A0FF;
    public static int UTIL_TEXT          = 0xFFA0A0C0;
    public static int UTIL_ON_BG         = 0xFF203020;
    public static int UTIL_ON_BG_HOVER   = 0xFF305030;
    public static int UTIL_ON_BORDER     = 0xFF60FF80;
    public static int UTIL_ON_TEXT       = 0xFF80FFA0;

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

    // ── Theme palettes ──────────────────────────────────────────────────
    // Every field above gets reassigned by exactly one of these methods when
    // its theme becomes active. applyClassic() restores the exact original
    // (pre-theming) hand-tuned palette this class shipped with. The other
    // seven were generated from the seed-colour + HLS-derivation scheme
    // documented in the class doc comment above.

    private static void applyClassic() {
        BACKGROUND = 0xE00E0E18;
        WINDOW_BORDER = 0xFFA0A060;
        WINDOW_BORDER_DIM = 0xFF5A5A3C;
        PANEL = 0xFF181820;
        PANEL_RAISED = 0xFF23232E;
        PANEL_BORDER = 0xFF3C3C56;
        PANEL_BORDER_LIGHT = 0xFF5C5C7E;
        ACCENT = 0xFFFFD700;
        ACCENT_HOVER = 0xFFFFF080;
        ACCENT_DIM = 0xFF806000;
        ACCENT_BG = 0xFF2A2000;
        ACCENT_BG_ACTIVE = 0xFF3D2B00;
        ACCENT_BG_HOVER = 0xFF5A4A00;
        ACCENT2 = 0xFF30A040;
        ACCENT2_HOVER = 0xFF70C088;
        ACCENT2_BG = 0xFF141820;
        ACCENT2_BG_ACTIVE = 0xFF153020;
        ACCENT2_BG_HOVER = 0xFF25404E;
        ACCENT3 = 0xFFFF8060;
        ACCENT3_HOVER = 0xFFFFA080;
        ACCENT3_BG = 0xFF2A0000;
        ACCENT3_BG_ACTIVE = 0xFF3D0000;
        ACCENT3_BG_HOVER = 0xFF5A0800;
        ACCENT3_BORDER = 0xFF804040;
        ACCENT3_BORDER_ON = 0xFFFF3030;
        CUSTOM_ACCENT = 0xFFB080FF;
        CUSTOM_ACCENT_HOVER = 0xFFD8C0FF;
        CUSTOM_ACCENT_DIM = 0xFF6A4A99;
        CUSTOM_ACCENT_BG = 0xFF1C1428;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2E1F45;
        CUSTOM_ACCENT_BG_HOVER = 0xFF3A2860;
        CUSTOM_ACCENT_BORDER = 0xFF6A4A99;
        CUSTOM_ACCENT_BORDER_ON = 0xFFC080FF;
        HEADER_BG = 0xFF2A2A3E;
        HEADER_BG_HOVER = 0xFF35354E;
        HEADER_BORDER = 0xFF50507E;
        HEADER_TEXT = 0xFFFFE060;
        HEADER_ARROW = 0xFF60D0FF;
        ROW_BG_EVEN = 0x30202020;
        ROW_BG_ODD = 0x30303030;
        ROW_BG_HOVER = 0x20FFFF80;
        ROW_BORDER_HOVER = 0xFFA0A060;
        ROW_BG_SELECTED = 0x4030A0FF;
        ROW_BORDER_SEL = 0xFF30A0FF;
        ROW_BORDER_SEL_IN = 0xFF60C0FF;
        TEXT_PRIMARY = 0xFFFFFFFF;
        TEXT_MUTED = 0xFFAAAAAA;
        TEXT_FAINT = 0xFF808080;
        TEXT_ON_ACCENT = 0xFFFFE040;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFD700;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFD700;
        LOCKED = 0xFFE34234;
        INFO = 0xFF40C0FF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF202030;
        UTIL_BG_HOVER = 0xFF303050;
        UTIL_BORDER = 0xFF404060;
        UTIL_BORDER_HOVER = 0xFF80A0FF;
        UTIL_TEXT = 0xFFA0A0C0;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Aged rice-paper scroll: cream parchment, ink-brush black text, vermillion wax-seal accent. */
    private static void applyCultivationScroll() {
        BACKGROUND = 0xE0E6D6B0;
        WINDOW_BORDER = 0xFFA67C3E;
        WINDOW_BORDER_DIM = 0xFF5B4422;
        PANEL = 0xFFF1E3C2;
        PANEL_RAISED = 0xFFF5EBD4;
        PANEL_BORDER = 0xFF967446;
        PANEL_BORDER_LIGHT = 0xFFC3A67E;
        ACCENT = 0xFFC43C2C;
        ACCENT_HOVER = 0xFFDF7B70;
        ACCENT_DIM = 0xFF581B14;
        ACCENT_BG = 0xFFECD2B3;
        ACCENT_BG_ACTIVE = 0xFFE8C2A4;
        ACCENT_BG_HOVER = 0xFFE3AE92;
        ACCENT2 = 0xFF5A8246;
        ACCENT2_HOVER = 0xFF90B87C;
        ACCENT2_BG = 0xFFE2D9B6;
        ACCENT2_BG_ACTIVE = 0xFFD3D0A9;
        ACCENT2_BG_HOVER = 0xFFC1C49A;
        ACCENT3 = 0xFF78466E;
        ACCENT3_HOVER = 0xFFAC72A1;
        ACCENT3_BG = 0xFFE5D3BA;
        ACCENT3_BG_ACTIVE = 0xFFD9C4B1;
        ACCENT3_BG_HOVER = 0xFFCAB1A7;
        ACCENT3_BORDER = 0xFF875D5A;
        ACCENT3_BORDER_ON = 0xFF96588A;
        CUSTOM_ACCENT = 0xFFC49C46;
        CUSTOM_ACCENT_HOVER = 0xFFD9BF87;
        CUSTOM_ACCENT_DIM = 0xFF654F20;
        CUSTOM_ACCENT_BG = 0xFFECDCB6;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFFE8D5A9;
        CUSTOM_ACCENT_BG_HOVER = 0xFFE3CC9A;
        CUSTOM_ACCENT_BORDER = 0xFFAD8846;
        CUSTOM_ACCENT_BORDER_ON = 0xFFCDAB62;
        HEADER_BG = 0xFFF2E0CA;
        HEADER_BG_HOVER = 0xFFEED2BC;
        HEADER_BORDER = 0xFFC38665;
        HEADER_TEXT = 0xFF3C2818;
        HEADER_ARROW = 0xFF467896;
        ROW_BG_EVEN = 0x30EBD6A6;
        ROW_BG_ODD = 0x30EFE0BB;
        ROW_BG_HOVER = 0x22E9A49C;
        ROW_BORDER_HOVER = 0xFFA67C3E;
        ROW_BG_SELECTED = 0x40C49C46;
        ROW_BORDER_SEL = 0xFFC49C46;
        ROW_BORDER_SEL_IN = 0xFFD6BA7E;
        TEXT_PRIMARY = 0xFF1E1812;
        TEXT_MUTED = 0xFF6E604E;
        TEXT_FAINT = 0xFF7B705E;
        TEXT_ON_ACCENT = 0xFFFFF8EB;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC49C46;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC49C46;
        LOCKED = 0xFFE34234;
        INFO = 0xFF467896;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFFEFDCBC;
        UTIL_BG_HOVER = 0xFFECD2B3;
        UTIL_BORDER = 0xFFA26640;
        UTIL_BORDER_HOVER = 0xFFDA675A;
        UTIL_TEXT = 0xFF6E604E;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Deep-space futuristic: near-black void, cyan/magenta/pink neon, electric-lime custom accent. */
    private static void applyCelestialNeon() {
        BACKGROUND = 0xE0080A14;
        WINDOW_BORDER = 0xFF3CDCFF;
        WINDOW_BORDER_DIM = 0xFF008EAD;
        PANEL = 0xFF0E1220;
        PANEL_RAISED = 0xFF384881;
        PANEL_BORDER = 0xFF32466E;
        PANEL_BORDER_LIGHT = 0xFF637FB8;
        ACCENT = 0xFF00E6FF;
        ACCENT_HOVER = 0xFF59EFFF;
        ACCENT_DIM = 0xFF006873;
        ACCENT_BG = 0xFF0D2736;
        ACCENT_BG_ACTIVE = 0xFF0B3C4D;
        ACCENT_BG_HOVER = 0xFF0A5667;
        ACCENT2 = 0xFFC83CFF;
        ACCENT2_HOVER = 0xFFDB80FF;
        ACCENT2_BG = 0xFF211636;
        ACCENT2_BG_ACTIVE = 0xFF331A4D;
        ACCENT2_BG_HOVER = 0xFF4A1F67;
        ACCENT3 = 0xFFFF3C8C;
        ACCENT3_HOVER = 0xFFFF77AE;
        ACCENT3_BG = 0xFF26162B;
        ACCENT3_BG_ACTIVE = 0xFF3E1A36;
        ACCENT3_BG_HOVER = 0xFF5B1F43;
        ACCENT3_BORDER = 0xFF98417D;
        ACCENT3_BORDER_ON = 0xFFFF599D;
        CUSTOM_ACCENT = 0xFF78FF8C;
        CUSTOM_ACCENT_HOVER = 0xFFA7FFB4;
        CUSTOM_ACCENT_DIM = 0xFF00BC1C;
        CUSTOM_ACCENT_BG = 0xFF192A2B;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF234136;
        CUSTOM_ACCENT_BG_HOVER = 0xFF305E43;
        CUSTOM_ACCENT_BORDER = 0xFF55A27D;
        CUSTOM_ACCENT_BORDER_ON = 0xFF8CFF9D;
        HEADER_BG = 0xFF355189;
        HEADER_BG_HOVER = 0xFF305E93;
        HEADER_BORDER = 0xFF459ECD;
        HEADER_TEXT = 0xFFC8FAFF;
        HEADER_ARROW = 0xFF00E6FF;
        ROW_BG_EVEN = 0x300B0D18;
        ROW_BG_ODD = 0x301F2847;
        ROW_BG_HOVER = 0x228CF4FF;
        ROW_BORDER_HOVER = 0xFF3CDCFF;
        ROW_BG_SELECTED = 0x4078FF8C;
        ROW_BORDER_SEL = 0xFF78FF8C;
        ROW_BORDER_SEL_IN = 0xFFA0FFAF;
        TEXT_PRIMARY = 0xFFEBF5FF;
        TEXT_MUTED = 0xFF8CA0BE;
        TEXT_FAINT = 0xFF61728D;
        TEXT_ON_ACCENT = 0xFF060A10;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFB428;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFB428;
        LOCKED = 0xFFE34234;
        INFO = 0xFF00E6FF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0D1A29;
        UTIL_BG_HOVER = 0xFF0D2736;
        UTIL_BORDER = 0xFF266E92;
        UTIL_BORDER_HOVER = 0xFF40ECFF;
        UTIL_TEXT = 0xFF8CA0BE;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Illuminated manuscript: dark oak brown, wax-seal crimson, forest-heraldic green, gold leaf. */
    private static void applyMedievalManuscript() {
        BACKGROUND = 0xE0120C08;
        WINDOW_BORDER = 0xFF966E3C;
        WINDOW_BORDER_DIM = 0xFF523D21;
        PANEL = 0xFF1A120C;
        PANEL_RAISED = 0xFF7B5539;
        PANEL_BORDER = 0xFF503A26;
        PANEL_BORDER_LIGHT = 0xFFAD7D52;
        ACCENT = 0xFFAA1E1E;
        ACCENT_HOVER = 0xFFE15454;
        ACCENT_DIM = 0xFF4C0D0D;
        ACCENT_BG = 0xFF28130E;
        ACCENT_BG_ACTIVE = 0xFF371410;
        ACCENT_BG_HOVER = 0xFF481612;
        ACCENT2 = 0xFF326E3C;
        ACCENT2_HOVER = 0xFF63B871;
        ACCENT2_BG = 0xFF1C1B11;
        ACCENT2_BG_ACTIVE = 0xFF1F2416;
        ACCENT2_BG_HOVER = 0xFF222F1B;
        ACCENT3 = 0xFF6E788C;
        ACCENT3_HOVER = 0xFF99A0AF;
        ACCENT3_BG = 0xFF221C19;
        ACCENT3_BG_ACTIVE = 0xFF2B2626;
        ACCENT3_BG_HOVER = 0xFF353335;
        ACCENT3_BORDER = 0xFF5F5959;
        ACCENT3_BORDER_ON = 0xFF838C9E;
        CUSTOM_ACCENT = 0xFFC8A032;
        CUSTOM_ACCENT_HOVER = 0xFFDDC278;
        CUSTOM_ACCENT_DIM = 0xFF645019;
        CUSTOM_ACCENT_BG = 0xFF2B2010;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF3D2E14;
        CUSTOM_ACCENT_BG_HOVER = 0xFF523F18;
        CUSTOM_ACCENT_BORDER = 0xFF8C6D2C;
        CUSTOM_ACCENT_BORDER_ON = 0xFFD3AF4E;
        HEADER_BG = 0xFF7E5237;
        HEADER_BG_HOVER = 0xFF824D35;
        HEADER_BORDER = 0xFFAC6042;
        HEADER_TEXT = 0xFFDCB45A;
        HEADER_ARROW = 0xFF5A96BE;
        ROW_BG_EVEN = 0x30130E09;
        ROW_BG_ODD = 0x30412D1E;
        ROW_BG_HOVER = 0x22EA8888;
        ROW_BORDER_HOVER = 0xFF966E3C;
        ROW_BG_SELECTED = 0x40C8A032;
        ROW_BORDER_SEL = 0xFFC8A032;
        ROW_BORDER_SEL_IN = 0xFFDBBD6D;
        TEXT_PRIMARY = 0xFFE6DCC8;
        TEXT_MUTED = 0xFFAA9B82;
        TEXT_FAINT = 0xFF7C705D;
        TEXT_ON_ACCENT = 0xFFF5EEDE;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8A032;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8A032;
        LOCKED = 0xFFE34234;
        INFO = 0xFF5A96BE;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF20120D;
        UTIL_BG_HOVER = 0xFF28130E;
        UTIL_BORDER = 0xFF663324;
        UTIL_BORDER_HOVER = 0xFFDC3939;
        UTIL_TEXT = 0xFFAA9B82;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Heart-demon / blood cultivation path: near-black blood-red, bruised-violet dao-heart wound, pale ash bone. */
    private static void applyDemonicBlood() {
        BACKGROUND = 0xE00E0404;
        WINDOW_BORDER = 0xFFDC1E1E;
        WINDOW_BORDER_DIM = 0xFF791010;
        PANEL = 0xFF140606;
        PANEL_RAISED = 0xFF842828;
        PANEL_BORDER = 0xFF5A1414;
        PANEL_BORDER_LIGHT = 0xFFCD2D2D;
        ACCENT = 0xFFE61414;
        ACCENT_HOVER = 0xFFF16464;
        ACCENT_DIM = 0xFF670909;
        ACCENT_BG = 0xFF290707;
        ACCENT_BG_ACTIVE = 0xFF3E0909;
        ACCENT_BG_HOVER = 0xFF570A0A;
        ACCENT2 = 0xFF8C28A0;
        ACCENT2_HOVER = 0xFFC35ED7;
        ACCENT2_BG = 0xFF200915;
        ACCENT2_BG_ACTIVE = 0xFF2C0D25;
        ACCENT2_BG_HOVER = 0xFF3A1137;
        ACCENT3 = 0xFFD2C8BE;
        ACCENT3_HOVER = 0xFFE0D8D2;
        ACCENT3_BG = 0xFF271918;
        ACCENT3_BG_ACTIVE = 0xFF3A2D2B;
        ACCENT3_BG_HOVER = 0xFF514441;
        ACCENT3_BORDER = 0xFF966E69;
        ACCENT3_BORDER_ON = 0xFFD9D0C8;
        CUSTOM_ACCENT = 0xFF6EA03C;
        CUSTOM_ACCENT_HOVER = 0xFFA1CC76;
        CUSTOM_ACCENT_DIM = 0xFF37501E;
        CUSTOM_ACCENT_BG = 0xFF1D150B;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF262511;
        CUSTOM_ACCENT_BG_HOVER = 0xFF313717;
        CUSTOM_ACCENT_BORDER = 0xFF645A28;
        CUSTOM_ACCENT_BORDER_ON = 0xFF84BC4C;
        HEADER_BG = 0xFF8A2727;
        HEADER_BG_HOVER = 0xFF922525;
        HEADER_BORDER = 0xFFD42626;
        HEADER_TEXT = 0xFFFF5050;
        HEADER_ARROW = 0xFF826EDC;
        ROW_BG_EVEN = 0x300F0505;
        ROW_BG_ODD = 0x30411313;
        ROW_BG_HOVER = 0x22F69393;
        ROW_BORDER_HOVER = 0xFFDC1E1E;
        ROW_BG_SELECTED = 0x406EA03C;
        ROW_BORDER_SEL = 0xFF6EA03C;
        ROW_BORDER_SEL_IN = 0xFF9AC86B;
        TEXT_PRIMARY = 0xFFE1D7CD;
        TEXT_MUTED = 0xFF967878;
        TEXT_FAINT = 0xFF866565;
        TEXT_ON_ACCENT = 0xFF140404;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFF09628;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFF09628;
        LOCKED = 0xFFE34234;
        INFO = 0xFF826EDC;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1C0707;
        UTIL_BG_HOVER = 0xFF290707;
        UTIL_BORDER = 0xFF7D1414;
        UTIL_BORDER_HOVER = 0xFFEF4C4C;
        UTIL_TEXT = 0xFF967878;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Ascended immortal court: pale jade, soft celestial gold, lavender qi-wisp, sapphire. */
    private static void applyCelestialJade() {
        BACKGROUND = 0xE0061010;
        WINDOW_BORDER = 0xFF96DCBE;
        WINDOW_BORDER_DIM = 0xFF33996D;
        PANEL = 0xFF0A1818;
        PANEL_RAISED = 0xFF347D7D;
        PANEL_BORDER = 0xFF3C6E64;
        PANEL_BORDER_LIGHT = 0xFF70B1A4;
        ACCENT = 0xFF6EDCAA;
        ACCENT_HOVER = 0xFFA1E8C8;
        ACCENT_DIM = 0xFF1D784E;
        ACCENT_BG = 0xFF142C27;
        ACCENT_BG_ACTIVE = 0xFF1E3F35;
        ACCENT_BG_HOVER = 0xFF2A5747;
        ACCENT2 = 0xFFE6D28C;
        ACCENT2_HOVER = 0xFFEFE2B4;
        ACCENT2_BG = 0xFF202B24;
        ACCENT2_BG_ACTIVE = 0xFF363D2F;
        ACCENT2_BG_HOVER = 0xFF50543D;
        ACCENT3 = 0xFFD2C8EB;
        ACCENT3_HOVER = 0xFFDFD8F1;
        ACCENT3_BG = 0xFF1E2A2D;
        ACCENT3_BG_ACTIVE = 0xFF323B42;
        ACCENT3_BG_HOVER = 0xFF4A505C;
        ACCENT3_BORDER = 0xFF879BA8;
        ACCENT3_BORDER_ON = 0xFFD9D0EE;
        CUSTOM_ACCENT = 0xFF468CDC;
        CUSTOM_ACCENT_HOVER = 0xFF87B4E8;
        CUSTOM_ACCENT_DIM = 0xFF17457A;
        CUSTOM_ACCENT_BG = 0xFF10242C;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF162F3F;
        CUSTOM_ACCENT_BG_HOVER = 0xFF1D3D57;
        CUSTOM_ACCENT_BORDER = 0xFF417DA0;
        CUSTOM_ACCENT_BORDER_ON = 0xFF629DE1;
        HEADER_BG = 0xFF378380;
        HEADER_BG_HOVER = 0xFF3C8A83;
        HEADER_BORDER = 0xFF6FBEA6;
        HEADER_TEXT = 0xFFF0E1AA;
        HEADER_ARROW = 0xFF468CDC;
        ROW_BG_EVEN = 0x30081212;
        ROW_BG_ODD = 0x301B4040;
        ROW_BG_HOVER = 0x22BEEFD9;
        ROW_BORDER_HOVER = 0xFF96DCBE;
        ROW_BG_SELECTED = 0x40468CDC;
        ROW_BORDER_SEL = 0xFF468CDC;
        ROW_BORDER_SEL_IN = 0xFF7DAEE6;
        TEXT_PRIMARY = 0xFFE1F5EE;
        TEXT_MUTED = 0xFF8CAFA5;
        TEXT_FAINT = 0xFF5F7D76;
        TEXT_ON_ACCENT = 0xFF061812;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFE6D28C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFE6D28C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF468CDC;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0E201E;
        UTIL_BG_HOVER = 0xFF142C27;
        UTIL_BORDER = 0xFF488A76;
        UTIL_BORDER_HOVER = 0xFF92E5BF;
        UTIL_TEXT = 0xFF8CAFA5;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Formation array / spirit talisman: amber talisman-paper, cyan array-circuit lines, violet-blue formation glow. */
    private static void applyFormationTalisman() {
        BACKGROUND = 0xE0100C04;
        WINDOW_BORDER = 0xFFE66E28;
        WINDOW_BORDER_DIM = 0xFF853B0F;
        PANEL = 0xFF161108;
        PANEL_RAISED = 0xFF80632E;
        PANEL_BORDER = 0xFF5A421E;
        PANEL_BORDER_LIGHT = 0xFFC08D41;
        ACCENT = 0xFFF08C28;
        ACCENT_HOVER = 0xFFF5B473;
        ACCENT_DIM = 0xFF763F08;
        ACCENT_BG = 0xFF2C1D0B;
        ACCENT_BG_ACTIVE = 0xFF422A0E;
        ACCENT_BG_HOVER = 0xFF5C3812;
        ACCENT2 = 0xFF3CD2DC;
        ACCENT2_HOVER = 0xFF80E2E8;
        ACCENT2_BG = 0xFF1A241D;
        ACCENT2_BG_ACTIVE = 0xFF1E3832;
        ACCENT2_BG_HOVER = 0xFF224F4C;
        ACCENT3 = 0xFFC83228;
        ACCENT3_HOVER = 0xFFDF6962;
        ACCENT3_BG = 0xFF28140B;
        ACCENT3_BG_ACTIVE = 0xFF3A180E;
        ACCENT3_BG_HOVER = 0xFF4F1C12;
        ACCENT3_BORDER = 0xFF913A23;
        ACCENT3_BORDER_ON = 0xFFD94940;
        CUSTOM_ACCENT = 0xFF6E6EE6;
        CUSTOM_ACCENT_HOVER = 0xFFA1A1EF;
        CUSTOM_ACCENT_DIM = 0xFF191991;
        CUSTOM_ACCENT_BG = 0xFF1F1A1E;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF282434;
        CUSTOM_ACCENT_BG_HOVER = 0xFF322F4F;
        CUSTOM_ACCENT_BORDER = 0xFF645882;
        CUSTOM_ACCENT_BORDER_ON = 0xFF8484EA;
        HEADER_BG = 0xFF87652E;
        HEADER_BG_HOVER = 0xFF90692D;
        HEADER_BORDER = 0xFFCE8D3A;
        HEADER_TEXT = 0xFFFABE64;
        HEADER_ARROW = 0xFF3CD2DC;
        ROW_BG_EVEN = 0x30100D06;
        ROW_BG_ODD = 0x30403217;
        ROW_BG_HOVER = 0x22F8CB9E;
        ROW_BORDER_HOVER = 0xFFE66E28;
        ROW_BG_SELECTED = 0x406E6EE6;
        ROW_BORDER_SEL = 0xFF6E6EE6;
        ROW_BORDER_SEL_IN = 0xFF9A9AEE;
        TEXT_PRIMARY = 0xFFE6D7BE;
        TEXT_MUTED = 0xFFA58C69;
        TEXT_FAINT = 0xFF806E53;
        TEXT_ON_ACCENT = 0xFF180E04;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFF08C28;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFF08C28;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3CD2DC;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1F1609;
        UTIL_BG_HOVER = 0xFF2C1D0B;
        UTIL_BORDER = 0xFF805420;
        UTIL_BORDER_HOVER = 0xFFF4A95E;
        UTIL_TEXT = 0xFFA58C69;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Void tribulation abyss: deep purple-black, tribulation-lightning violet, void-glow teal, dark-star crimson. */
    private static void applyVoidAbyss() {
        BACKGROUND = 0xE0080410;
        WINDOW_BORDER = 0xFFAA5AFF;
        WINDOW_BORDER_DIM = 0xFF5C00BE;
        PANEL = 0xFF0C0816;
        PANEL_RAISED = 0xFF462E80;
        PANEL_BORDER = 0xFF3C2864;
        PANEL_BORDER_LIGHT = 0xFF7653BA;
        ACCENT = 0xFFB464FF;
        ACCENT_HOVER = 0xFFCE9AFF;
        ACCENT_DIM = 0xFF5200A0;
        ACCENT_BG = 0xFF1D112D;
        ACCENT_BG_ACTIVE = 0xFF2E1A45;
        ACCENT_BG_HOVER = 0xFF422561;
        ACCENT2 = 0xFF3CC8BE;
        ACCENT2_HOVER = 0xFF80DBD5;
        ACCENT2_BG = 0xFF111B27;
        ACCENT2_BG_ACTIVE = 0xFF162E38;
        ACCENT2_BG_HOVER = 0xFF1B454C;
        ACCENT3 = 0xFFDC325A;
        ACCENT3_HOVER = 0xFFE6708C;
        ACCENT3_BG = 0xFF210C1D;
        ACCENT3_BG_ACTIVE = 0xFF361024;
        ACCENT3_BG_HOVER = 0xFF4F152C;
        ACCENT3_BORDER = 0xFF8C2D5F;
        ACCENT3_BORDER_ON = 0xFFE15173;
        CUSTOM_ACCENT = 0xFFAABEE6;
        CUSTOM_ACCENT_HOVER = 0xFFC8D5EF;
        CUSTOM_ACCENT_DIM = 0xFF2D529B;
        CUSTOM_ACCENT_BG = 0xFF1C1A2B;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2C2C40;
        CUSTOM_ACCENT_BG_HOVER = 0xFF3F4259;
        CUSTOM_ACCENT_BORDER = 0xFF7373A5;
        CUSTOM_ACCENT_BORDER_ON = 0xFFB7C8EA;
        HEADER_BG = 0xFF4D3188;
        HEADER_BG_HOVER = 0xFF553692;
        HEADER_BORDER = 0xFF8958CF;
        HEADER_TEXT = 0xFFD7B4FF;
        HEADER_ARROW = 0xFF3CC8BE;
        ROW_BG_EVEN = 0x30090610;
        ROW_BG_ODD = 0x30231740;
        ROW_BG_HOVER = 0x22DDB9FF;
        ROW_BORDER_HOVER = 0xFFAA5AFF;
        ROW_BG_SELECTED = 0x40AABEE6;
        ROW_BORDER_SEL = 0xFFAABEE6;
        ROW_BORDER_SEL_IN = 0xFFC3D2EE;
        TEXT_PRIMARY = 0xFFDCD7EB;
        TEXT_MUTED = 0xFF8C82A5;
        TEXT_FAINT = 0xFF716888;
        TEXT_ON_ACCENT = 0xFF0A0414;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFE68C3C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFE68C3C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3CC8BE;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF130C1F;
        UTIL_BG_HOVER = 0xFF1D112D;
        UTIL_BORDER = 0xFF5A378B;
        UTIL_BORDER_HOVER = 0xFFC78BFF;
        UTIL_TEXT = 0xFF8C82A5;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyStellarForge() {
        BACKGROUND = 0xE0040614;
        WINDOW_BORDER = 0xFF78B4FF;
        WINDOW_BORDER_DIM = 0xFF005CCE;
        PANEL = 0xFF080C1C;
        PANEL_RAISED = 0xFF283B8B;
        PANEL_BORDER = 0xFF283C6E;
        PANEL_BORDER_LIGHT = 0xFF5373C1;
        ACCENT = 0xFF8CC8FF;
        ACCENT_HOVER = 0xFFB4DBFF;
        ACCENT_DIM = 0xFF005DB2;
        ACCENT_BG = 0xFF151F33;
        ACCENT_BG_ACTIVE = 0xFF223249;
        ACCENT_BG_HOVER = 0xFF324865;
        ACCENT2 = 0xFFFF8C3C;
        ACCENT2_HOVER = 0xFFFFB480;
        ACCENT2_BG = 0xFF21191F;
        ACCENT2_BG_ACTIVE = 0xFF392622;
        ACCENT2_BG_HOVER = 0xFF573526;
        ACCENT3 = 0xFFFF50B4;
        ACCENT3_HOVER = 0xFFFF84CA;
        ACCENT3_BG = 0xFF21132B;
        ACCENT3_BG_ACTIVE = 0xFF391A3A;
        ACCENT3_BG_HOVER = 0xFF57224D;
        ACCENT3_BORDER = 0xFF944691;
        ACCENT3_BORDER_ON = 0xFFFF6ABF;
        CUSTOM_ACCENT = 0xFFB4FFDC;
        CUSTOM_ACCENT_HOVER = 0xFFCEFFE8;
        CUSTOM_ACCENT_DIM = 0xFF00DA74;
        CUSTOM_ACCENT_BG = 0xFF19242F;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2A3D42;
        CUSTOM_ACCENT_BG_HOVER = 0xFF3F5A59;
        CUSTOM_ACCENT_BORDER = 0xFF6E9EA5;
        CUSTOM_ACCENT_BORDER_ON = 0xFFBFFFE1;
        HEADER_BG = 0xFF2E4392;
        HEADER_BG_HOVER = 0xFF364F9B;
        HEADER_BORDER = 0xFF648CD4;
        HEADER_TEXT = 0xFFC8E1FF;
        HEADER_ARROW = 0xFF8CC8FF;
        ROW_BG_EVEN = 0x30060915;
        ROW_BG_ODD = 0x30151F48;
        ROW_BG_HOVER = 0x22CBE6FF;
        ROW_BORDER_HOVER = 0xFF78B4FF;
        ROW_BG_SELECTED = 0x40B4FFDC;
        ROW_BORDER_SEL = 0xFFB4FFDC;
        ROW_BORDER_SEL_IN = 0xFFCBFFE6;
        TEXT_PRIMARY = 0xFFE1EBFF;
        TEXT_MUTED = 0xFF8C9BBE;
        TEXT_FAINT = 0xFF636D8E;
        TEXT_ON_ACCENT = 0xFF060A14;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFC83C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFC83C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF8CC8FF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0D1425;
        UTIL_BG_HOVER = 0xFF151F33;
        UTIL_BORDER = 0xFF415F92;
        UTIL_BORDER_HOVER = 0xFFA9D6FF;
        UTIL_TEXT = 0xFF8C9BBE;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyQuantumLotus() {
        BACKGROUND = 0xE006040E;
        WINDOW_BORDER = 0xFFFF3CDC;
        WINDOW_BORDER_DIM = 0xFFAD008E;
        PANEL = 0xFF0A0814;
        PANEL_RAISED = 0xFF3E317B;
        PANEL_BORDER = 0xFF461E5A;
        PANEL_BORDER_LIGHT = 0xFF9541C0;
        ACCENT = 0xFF00FFDC;
        ACCENT_HOVER = 0xFF59FFE8;
        ACCENT_DIM = 0xFF007363;
        ACCENT_BG = 0xFF092128;
        ACCENT_BG_ACTIVE = 0xFF08393C;
        ACCENT_BG_HOVER = 0xFF075754;
        ACCENT2 = 0xFFFF3CDC;
        ACCENT2_HOVER = 0xFFFF80E8;
        ACCENT2_BG = 0xFF220D28;
        ACCENT2_BG_ACTIVE = 0xFF3B123C;
        ACCENT2_BG_HOVER = 0xFF581954;
        ACCENT3 = 0xFF96FF50;
        ACCENT3_HOVER = 0xFFB5FF84;
        ACCENT3_BG = 0xFF18211A;
        ACCENT3_BG_ACTIVE = 0xFF263920;
        ACCENT3_BG_HOVER = 0xFF375727;
        ACCENT3_BORDER = 0xFF6E8E55;
        ACCENT3_BORDER_ON = 0xFFA6FF6A;
        CUSTOM_ACCENT = 0xFFFFDC50;
        CUSTOM_ACCENT_HOVER = 0xFFFFE88D;
        CUSTOM_ACCENT_DIM = 0xFFA88600;
        CUSTOM_ACCENT_BG = 0xFF221D1A;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF3B3220;
        CUSTOM_ACCENT_BG_HOVER = 0xFF584C27;
        CUSTOM_ACCENT_BORDER = 0xFFA27D55;
        CUSTOM_ACCENT_BORDER_ON = 0xFFFFE16A;
        HEADER_BG = 0xFF3A3D81;
        HEADER_BG_HOVER = 0xFF354E89;
        HEADER_BORDER = 0xFF687AC8;
        HEADER_TEXT = 0xFFFFC8FA;
        HEADER_ARROW = 0xFF00FFDC;
        ROW_BG_EVEN = 0x3008060F;
        ROW_BG_ODD = 0x301F193D;
        ROW_BG_HOVER = 0x228CFFEF;
        ROW_BORDER_HOVER = 0xFFFF3CDC;
        ROW_BG_SELECTED = 0x40FFDC50;
        ROW_BORDER_SEL = 0xFFFFDC50;
        ROW_BORDER_SEL_IN = 0xFFFFE684;
        TEXT_PRIMARY = 0xFFE6E1F5;
        TEXT_MUTED = 0xFFA08CB4;
        TEXT_FAINT = 0xFF75668B;
        TEXT_ON_ACCENT = 0xFF0A040E;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFDC50;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFDC50;
        LOCKED = 0xFFE34234;
        INFO = 0xFF00FFDC;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0A121C;
        UTIL_BG_HOVER = 0xFF092128;
        UTIL_BORDER = 0xFF34567A;
        UTIL_BORDER_HOVER = 0xFF40FFE5;
        UTIL_TEXT = 0xFFA08CB4;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyNebulaDrift() {
        BACKGROUND = 0xE00A0418;
        WINDOW_BORDER = 0xFFBE6EFF;
        WINDOW_BORDER_DIM = 0xFF6F00C9;
        PANEL = 0xFF100820;
        PANEL_RAISED = 0xFF482491;
        PANEL_BORDER = 0xFF46286E;
        PANEL_BORDER_LIGHT = 0xFF8253C1;
        ACCENT = 0xFFC878FF;
        ACCENT_HOVER = 0xFFDBA7FF;
        ACCENT_DIM = 0xFF6400A9;
        ACCENT_BG = 0xFF221336;
        ACCENT_BG_ACTIVE = 0xFF351E4D;
        ACCENT_BG_HOVER = 0xFF4B2C67;
        ACCENT2 = 0xFFFF78BE;
        ACCENT2_HOVER = 0xFFFFA7D5;
        ACCENT2_BG = 0xFF281330;
        ACCENT2_BG_ACTIVE = 0xFF401E40;
        ACCENT2_BG_HOVER = 0xFF5C2C53;
        ACCENT3 = 0xFF6EC8FF;
        ACCENT3_HOVER = 0xFF9AD8FF;
        ACCENT3_BG = 0xFF191B36;
        ACCENT3_BG_ACTIVE = 0xFF232E4D;
        ACCENT3_BG_HOVER = 0xFF2E4567;
        ACCENT3_BORDER = 0xFF5A78B6;
        ACCENT3_BORDER_ON = 0xFF84D0FF;
        CUSTOM_ACCENT = 0xFFFFD28C;
        CUSTOM_ACCENT_HOVER = 0xFFFFE2B4;
        CUSTOM_ACCENT_DIM = 0xFFC67800;
        CUSTOM_ACCENT_BG = 0xFF281C2B;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF403036;
        CUSTOM_ACCENT_BG_HOVER = 0xFF5C4943;
        CUSTOM_ACCENT_BORDER = 0xFFA27D7D;
        CUSTOM_ACCENT_BORDER_ON = 0xFFFFD99D;
        HEADER_BG = 0xFF502998;
        HEADER_BG_HOVER = 0xFF5A30A0;
        HEADER_BORDER = 0xFF975ED4;
        HEADER_TEXT = 0xFFE6BEFF;
        HEADER_ARROW = 0xFF6EC8FF;
        ROW_BG_EVEN = 0x300C0618;
        ROW_BG_ODD = 0x3027134D;
        ROW_BG_HOVER = 0x22E6C2FF;
        ROW_BORDER_HOVER = 0xFFBE6EFF;
        ROW_BG_SELECTED = 0x40FFD28C;
        ROW_BORDER_SEL = 0xFFFFD28C;
        ROW_BORDER_SEL_IN = 0xFFFFE0AE;
        TEXT_PRIMARY = 0xFFE6DCF5;
        TEXT_MUTED = 0xFF9B8CB4;
        TEXT_FAINT = 0xFF736890;
        TEXT_ON_ACCENT = 0xFF0A0414;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFD28C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFD28C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF6EC8FF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF170C29;
        UTIL_BG_HOVER = 0xFF221336;
        UTIL_BORDER = 0xFF663C92;
        UTIL_BORDER_HOVER = 0xFFD69AFF;
        UTIL_TEXT = 0xFF9B8CB4;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyChronoCircuit() {
        BACKGROUND = 0xE0040C0C;
        WINDOW_BORDER = 0xFF50E6C8;
        WINDOW_BORDER_DIM = 0xFF15957C;
        PANEL = 0xFF081414;
        PANEL_RAISED = 0xFF317B7B;
        PANEL_BORDER = 0xFF1E504B;
        PANEL_BORDER_LIGHT = 0xFF44B6AA;
        ACCENT = 0xFF50E6C8;
        ACCENT_HOVER = 0xFF8DEFDB;
        ACCENT_DIM = 0xFF117A65;
        ACCENT_BG = 0xFF0F2926;
        ACCENT_BG_ACTIVE = 0xFF163E38;
        ACCENT_BG_HOVER = 0xFF1F574E;
        ACCENT2 = 0xFFFFB43C;
        ACCENT2_HOVER = 0xFFFFCE80;
        ACCENT2_BG = 0xFF212418;
        ACCENT2_BG_ACTIVE = 0xFF39341C;
        ACCENT2_BG_HOVER = 0xFF574721;
        ACCENT3 = 0xFFFF5A5A;
        ACCENT3_HOVER = 0xFFFF8C8C;
        ACCENT3_BG = 0xFF211B1B;
        ACCENT3_BG_ACTIVE = 0xFF392222;
        ACCENT3_BG_HOVER = 0xFF572A2A;
        ACCENT3_BORDER = 0xFF8E5552;
        ACCENT3_BORDER_ON = 0xFFFF7373;
        CUSTOM_ACCENT = 0xFF8CC8FF;
        CUSTOM_ACCENT_HOVER = 0xFFB4DBFF;
        CUSTOM_ACCENT_DIM = 0xFF0067C6;
        CUSTOM_ACCENT_BG = 0xFF15262C;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF223843;
        CUSTOM_ACCENT_BG_HOVER = 0xFF324E5F;
        CUSTOM_ACCENT_BORDER = 0xFF558CA5;
        CUSTOM_ACCENT_BORDER_ON = 0xFF9DD0FF;
        HEADER_BG = 0xFF338180;
        HEADER_BG_HOVER = 0xFF358A86;
        HEADER_BORDER = 0xFF48C4B3;
        HEADER_TEXT = 0xFFB4F0DC;
        HEADER_ARROW = 0xFF50E6C8;
        ROW_BG_EVEN = 0x30060F0F;
        ROW_BG_ODD = 0x30193D3D;
        ROW_BG_HOVER = 0x22B0F4E6;
        ROW_BORDER_HOVER = 0xFF50E6C8;
        ROW_BG_SELECTED = 0x408CC8FF;
        ROW_BORDER_SEL = 0xFF8CC8FF;
        ROW_BORDER_SEL_IN = 0xFFAED8FF;
        TEXT_PRIMARY = 0xFFD7EBE6;
        TEXT_MUTED = 0xFF8CAAA5;
        TEXT_FAINT = 0xFF5C7672;
        TEXT_ON_ACCENT = 0xFF040E0C;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFB43C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFB43C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF50E6C8;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0B1C1B;
        UTIL_BG_HOVER = 0xFF0F2926;
        UTIL_BORDER = 0xFF2A766A;
        UTIL_BORDER_HOVER = 0xFF7CECD6;
        UTIL_TEXT = 0xFF8CAAA5;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyEightBitSect() {
        BACKGROUND = 0xE0080808;
        WINDOW_BORDER = 0xFFF8D000;
        WINDOW_BORDER_DIM = 0xFF887200;
        PANEL = 0xFF101010;
        PANEL_RAISED = 0xFF585858;
        PANEL_BORDER = 0xFF505050;
        PANEL_BORDER_LIGHT = 0xFF8D8D8D;
        ACCENT = 0xFFF8D000;
        ACCENT_HOVER = 0xFFFFE455;
        ACCENT_DIM = 0xFF705E00;
        ACCENT_BG = 0xFF27230E;
        ACCENT_BG_ACTIVE = 0xFF3E360D;
        ACCENT_BG_HOVER = 0xFF5A4D0B;
        ACCENT2 = 0xFF00A844;
        ACCENT2_HOVER = 0xFF21FF7B;
        ACCENT2_BG = 0xFF0E1F15;
        ACCENT2_BG_ACTIVE = 0xFF0D2E1A;
        ACCENT2_BG_HOVER = 0xFF0B4121;
        ACCENT3 = 0xFFD82800;
        ACCENT3_HOVER = 0xFFFF5731;
        ACCENT3_BG = 0xFF24120E;
        ACCENT3_BG_ACTIVE = 0xFF38150D;
        ACCENT3_BG_HOVER = 0xFF50180B;
        ACCENT3_BORDER = 0xFF943C28;
        ACCENT3_BORDER_ON = 0xFFFF3305;
        CUSTOM_ACCENT = 0xFF0078F8;
        CUSTOM_ACCENT_HOVER = 0xFF55A7FF;
        CUSTOM_ACCENT_DIM = 0xFF003C7C;
        CUSTOM_ACCENT_BG = 0xFF0E1A27;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF0D253E;
        CUSTOM_ACCENT_BG_HOVER = 0xFF0B315A;
        CUSTOM_ACCENT_BORDER = 0xFF2864A4;
        CUSTOM_ACCENT_BORDER_ON = 0xFF208CFF;
        HEADER_BG = 0xFF625F53;
        HEADER_BG_HOVER = 0xFF6E694C;
        HEADER_BORDER = 0xFFADA163;
        HEADER_TEXT = 0xFFF8D000;
        HEADER_ARROW = 0xFF0078F8;
        ROW_BG_EVEN = 0x300C0C0C;
        ROW_BG_ODD = 0x302D2D2D;
        ROW_BG_HOVER = 0x22FFEC89;
        ROW_BORDER_HOVER = 0xFFF8D000;
        ROW_BG_SELECTED = 0x400078F8;
        ROW_BORDER_SEL = 0xFF0078F8;
        ROW_BORDER_SEL_IN = 0xFF48A0FF;
        TEXT_PRIMARY = 0xFFF8F8F8;
        TEXT_MUTED = 0xFFA0A0A0;
        TEXT_FAINT = 0xFF707070;
        TEXT_ON_ACCENT = 0xFF080808;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFD82800;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFD82800;
        LOCKED = 0xFFE34234;
        INFO = 0xFF0078F8;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF19180F;
        UTIL_BG_HOVER = 0xFF27230E;
        UTIL_BORDER = 0xFF7A703C;
        UTIL_BORDER_HOVER = 0xFFFFDF3B;
        UTIL_TEXT = 0xFFA0A0A0;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyArcadeCultivator() {
        BACKGROUND = 0xE00A0414;
        WINDOW_BORDER = 0xFFFF00B4;
        WINDOW_BORDER_DIM = 0xFF8C0063;
        PANEL = 0xFF10081C;
        PANEL_RAISED = 0xFF4F288B;
        PANEL_BORDER = 0xFF50145A;
        PANEL_BORDER_LIGHT = 0xFFB62DCD;
        ACCENT = 0xFFFF00B4;
        ACCENT_HOVER = 0xFFFF59CE;
        ACCENT_DIM = 0xFF730051;
        ACCENT_BG = 0xFF28072B;
        ACCENT_BG_ACTIVE = 0xFF40063A;
        ACCENT_BG_HOVER = 0xFF5C054D;
        ACCENT2 = 0xFF00E6FF;
        ACCENT2_HOVER = 0xFF59EFFF;
        ACCENT2_BG = 0xFF0E1E33;
        ACCENT2_BG_ACTIVE = 0xFF0D3449;
        ACCENT2_BG_HOVER = 0xFF0B4F65;
        ACCENT3 = 0xFFFFE600;
        ACCENT3_HOVER = 0xFFFFED4D;
        ACCENT3_BG = 0xFF281E19;
        ACCENT3_BG_ACTIVE = 0xFF403416;
        ACCENT3_BG_HOVER = 0xFF5C4F13;
        ACCENT3_BORDER = 0xFFA87D2D;
        ACCENT3_BORDER_ON = 0xFFFFEA26;
        CUSTOM_ACCENT = 0xFF8C50FF;
        CUSTOM_ACCENT_HOVER = 0xFFB48DFF;
        CUSTOM_ACCENT_DIM = 0xFF3900A8;
        CUSTOM_ACCENT_BG = 0xFF1C0F33;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF291649;
        CUSTOM_ACCENT_BG_HOVER = 0xFF381F65;
        CUSTOM_ACCENT_BORDER = 0xFF6E32AC;
        CUSTOM_ACCENT_BORDER_ON = 0xFF9D6AFF;
        HEADER_BG = 0xFF5A268D;
        HEADER_BG_HOVER = 0xFF682291;
        HEADER_BORDER = 0xFFCC20C6;
        HEADER_TEXT = 0xFF00E6FF;
        HEADER_ARROW = 0xFF00E6FF;
        ROW_BG_EVEN = 0x300C0615;
        ROW_BG_ODD = 0x30291548;
        ROW_BG_HOVER = 0x22FF8CDD;
        ROW_BORDER_HOVER = 0xFFFF00B4;
        ROW_BG_SELECTED = 0x408C50FF;
        ROW_BORDER_SEL = 0xFF8C50FF;
        ROW_BORDER_SEL_IN = 0xFFAE84FF;
        TEXT_PRIMARY = 0xFFEBE1F5;
        TEXT_MUTED = 0xFFA08CB4;
        TEXT_FAINT = 0xFF76678C;
        TEXT_ON_ACCENT = 0xFF0A0410;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFE600;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFE600;
        LOCKED = 0xFFE34234;
        INFO = 0xFF00E6FF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1A0822;
        UTIL_BG_HOVER = 0xFF28072B;
        UTIL_BORDER = 0xFF7C0F70;
        UTIL_BORDER_HOVER = 0xFFFF40C7;
        UTIL_TEXT = 0xFFA08CB4;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyParchmentPixel() {
        BACKGROUND = 0xE0D6BE96;
        WINDOW_BORDER = 0xFF78562C;
        WINDOW_BORDER_DIM = 0xFF422F18;
        PANEL = 0xFFE2CCA8;
        PANEL_RAISED = 0xFFEBDBC2;
        PANEL_BORDER = 0xFF8C683A;
        PANEL_BORDER_LIGHT = 0xFFC49F70;
        ACCENT = 0xFF965A28;
        ACCENT_HOVER = 0xFFD3925B;
        ACCENT_DIM = 0xFF442812;
        ACCENT_BG = 0xFFDAC19B;
        ACCENT_BG_ACTIVE = 0xFFD3B58E;
        ACCENT_BG_HOVER = 0xFFCAA87F;
        ACCENT2 = 0xFF5A6E3C;
        ACCENT2_HOVER = 0xFF97B170;
        ACCENT2_BG = 0xFFD4C39D;
        ACCENT2_BG_ACTIVE = 0xFFC7B992;
        ACCENT2_BG_HOVER = 0xFFB6AE85;
        ACCENT3 = 0xFF963228;
        ACCENT3_HOVER = 0xFFD05A4E;
        ACCENT3_BG = 0xFFDABD9B;
        ACCENT3_BG_ACTIVE = 0xFFD3AD8E;
        ACCENT3_BG_HOVER = 0xFFCA9B7F;
        ACCENT3_BORDER = 0xFF914D31;
        ACCENT3_BORDER_ON = 0xFFBC3F32;
        CUSTOM_ACCENT = 0xFFBE963C;
        CUSTOM_ACCENT_HOVER = 0xFFD6BB7F;
        CUSTOM_ACCENT_DIM = 0xFF5F4B1E;
        CUSTOM_ACCENT_BG = 0xFFDEC79D;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFFDBC192;
        CUSTOM_ACCENT_BG_HOVER = 0xFFD6BB85;
        CUSTOM_ACCENT_BORDER = 0xFFA57F3B;
        CUSTOM_ACCENT_BORDER_ON = 0xFFCAA757;
        HEADER_BG = 0xFFE6D3B9;
        HEADER_BG_HOVER = 0xFFDFC9AC;
        HEADER_BORDER = 0xFFB68A5A;
        HEADER_TEXT = 0xFF463018;
        HEADER_ARROW = 0xFF466482;
        ROW_BG_EVEN = 0x30DABE90;
        ROW_BG_ODD = 0x30E0C9A2;
        ROW_BG_HOVER = 0x22E1B38D;
        ROW_BORDER_HOVER = 0xFF78562C;
        ROW_BG_SELECTED = 0x40BE963C;
        ROW_BORDER_SEL = 0xFFBE963C;
        ROW_BORDER_SEL_IN = 0xFFD3B675;
        TEXT_PRIMARY = 0xFF302212;
        TEXT_MUTED = 0xFF68553B;
        TEXT_FAINT = 0xFF6F624C;
        TEXT_ON_ACCENT = 0xFFFAF0DC;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFBE963C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFBE963C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF466482;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFFDFC7A3;
        UTIL_BG_HOVER = 0xFFDAC19B;
        UTIL_BORDER = 0xFF8E6436;
        UTIL_BORDER_HOVER = 0xFFCC8142;
        UTIL_TEXT = 0xFF655339;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyGothicCathedral() {
        BACKGROUND = 0xE00A0A0E;
        WINDOW_BORDER = 0xFF8C6E32;
        WINDOW_BORDER_DIM = 0xFF4D3D1C;
        PANEL = 0xFF121216;
        PANEL_RAISED = 0xFF515164;
        PANEL_BORDER = 0xFF3C3C46;
        PANEL_BORDER_LIGHT = 0xFF7A7A8D;
        ACCENT = 0xFF96141E;
        ACCENT_HOVER = 0xFFE53C49;
        ACCENT_DIM = 0xFF44090D;
        ACCENT_BG = 0xFF1F1217;
        ACCENT_BG_ACTIVE = 0xFF2C1218;
        ACCENT_BG_HOVER = 0xFF3C1319;
        ACCENT2 = 0xFF3C5A8C;
        ACCENT2_HOVER = 0xFF7290C3;
        ACCENT2_BG = 0xFF161922;
        ACCENT2_BG_ACTIVE = 0xFF1A202E;
        ACCENT2_BG_HOVER = 0xFF1F293C;
        ACCENT3 = 0xFF8C6E32;
        ACCENT3_HOVER = 0xFFC4A15A;
        ACCENT3_BG = 0xFF1E1B19;
        ACCENT3_BG_ACTIVE = 0xFF2A241C;
        ACCENT3_BG_HOVER = 0xFF392F1F;
        ACCENT3_BORDER = 0xFF64553C;
        ACCENT3_BORDER_ON = 0xFFAF8A3F;
        CUSTOM_ACCENT = 0xFF783C8C;
        CUSTOM_ACCENT_HOVER = 0xFFAE72C3;
        CUSTOM_ACCENT_DIM = 0xFF3C1E46;
        CUSTOM_ACCENT_BG = 0xFF1C1622;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF261A2E;
        CUSTOM_ACCENT_BG_HOVER = 0xFF331F3C;
        CUSTOM_ACCENT_BORDER = 0xFF5A3C69;
        CUSTOM_ACCENT_BORDER_ON = 0xFF944AAD;
        HEADER_BG = 0xFF554D60;
        HEADER_BG_HOVER = 0xFF5B485A;
        HEADER_BORDER = 0xFF825B6C;
        HEADER_TEXT = 0xFFC8AA5A;
        HEADER_ARROW = 0xFF3C5A8C;
        ROW_BG_EVEN = 0x300D0D10;
        ROW_BG_ODD = 0x302B2B35;
        ROW_BG_HOVER = 0x22ED7881;
        ROW_BORDER_HOVER = 0xFF8C6E32;
        ROW_BG_SELECTED = 0x40783C8C;
        ROW_BORDER_SEL = 0xFF783C8C;
        ROW_BORDER_SEL_IN = 0xFFA867BE;
        TEXT_PRIMARY = 0xFFD7D2CD;
        TEXT_MUTED = 0xFF969191;
        TEXT_FAINT = 0xFF747070;
        TEXT_ON_ACCENT = 0xFF0A0A0C;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFF8C6E32;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFF8C6E32;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3C5A8C;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF171216;
        UTIL_BG_HOVER = 0xFF1F1217;
        UTIL_BORDER = 0xFF52323C;
        UTIL_BORDER_HOVER = 0xFFE11E2D;
        UTIL_TEXT = 0xFF969191;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyKnightsLedger() {
        BACKGROUND = 0xE00E1014;
        WINDOW_BORDER = 0xFFA0AAB9;
        WINDOW_BORDER_DIM = 0xFF515C6D;
        PANEL = 0xFF14171C;
        PANEL_RAISED = 0xFF4E596D;
        PANEL_BORDER = 0xFF464E5C;
        PANEL_BORDER_LIGHT = 0xFF7F8A9D;
        ACCENT = 0xFF8C1428;
        ACCENT_HOVER = 0xFFE33854;
        ACCENT_DIM = 0xFF3F0912;
        ACCENT_BG = 0xFF20171D;
        ACCENT_BG_ACTIVE = 0xFF2C161E;
        ACCENT_BG_HOVER = 0xFF3A1620;
        ACCENT2 = 0xFF788CA5;
        ACCENT2_HOVER = 0xFFA7B4C4;
        ACCENT2_BG = 0xFF1E232A;
        ACCENT2_BG_ACTIVE = 0xFF282E37;
        ACCENT2_BG_HOVER = 0xFF343C48;
        ACCENT3 = 0xFFC8AF6E;
        ACCENT3_HOVER = 0xFFD9C79A;
        ACCENT3_BG = 0xFF262624;
        ACCENT3_BG_ACTIVE = 0xFF38352C;
        ACCENT3_BG_HOVER = 0xFF4E4836;
        ACCENT3_BORDER = 0xFF877E65;
        ACCENT3_BORDER_ON = 0xFFD0BB84;
        CUSTOM_ACCENT = 0xFF5A6E8C;
        CUSTOM_ACCENT_HOVER = 0xFF90A0B8;
        CUSTOM_ACCENT_DIM = 0xFF2D3746;
        CUSTOM_ACCENT_BG = 0xFF1B2027;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF222832;
        CUSTOM_ACCENT_BG_HOVER = 0xFF2A3340;
        CUSTOM_ACCENT_BORDER = 0xFF505E74;
        CUSTOM_ACCENT_BORDER_ON = 0xFF6E83A2;
        HEADER_BG = 0xFF525569;
        HEADER_BG_HOVER = 0xFF574F63;
        HEADER_BORDER = 0xFF83677A;
        HEADER_TEXT = 0xFFC8CDD7;
        HEADER_ARROW = 0xFF788CA5;
        ROW_BG_EVEN = 0x300F1115;
        ROW_BG_ODD = 0x302B323C;
        ROW_BG_HOVER = 0x22EB7589;
        ROW_BORDER_HOVER = 0xFFA0AAB9;
        ROW_BG_SELECTED = 0x405A6E8C;
        ROW_BORDER_SEL = 0xFF5A6E8C;
        ROW_BORDER_SEL_IN = 0xFF8899B2;
        TEXT_PRIMARY = 0xFFDCDEE4;
        TEXT_MUTED = 0xFF969BA5;
        TEXT_FAINT = 0xFF6E747F;
        TEXT_ON_ACCENT = 0xFF0C0E12;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8AF6E;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8AF6E;
        LOCKED = 0xFFE34234;
        INFO = 0xFF788CA5;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF19171C;
        UTIL_BG_HOVER = 0xFF20171D;
        UTIL_BORDER = 0xFF58404F;
        UTIL_BORDER_HOVER = 0xFFD91F3E;
        UTIL_TEXT = 0xFF969BA5;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyDragonsHoard() {
        BACKGROUND = 0xE00C0A04;
        WINDOW_BORDER = 0xFFC89628;
        WINDOW_BORDER_DIM = 0xFF6E5216;
        PANEL = 0xFF141008;
        PANEL_RAISED = 0xFF7B6331;
        PANEL_BORDER = 0xFF5A441A;
        PANEL_BORDER_LIGHT = 0xFFC59539;
        ACCENT = 0xFFDCAA28;
        ACCENT_HOVER = 0xFFE8C873;
        ACCENT_DIM = 0xFF654D10;
        ACCENT_BG = 0xFF281F0B;
        ACCENT_BG_ACTIVE = 0xFF3C2F0E;
        ACCENT_BG_HOVER = 0xFF544112;
        ACCENT2 = 0xFF285A32;
        ACCENT2_HOVER = 0xFF54B367;
        ACCENT2_BG = 0xFF16170C;
        ACCENT2_BG_ACTIVE = 0xFF181F10;
        ACCENT2_BG_HOVER = 0xFF1A2815;
        ACCENT3 = 0xFFA03C1E;
        ACCENT3_HOVER = 0xFFDC6642;
        ACCENT3_BG = 0xFF22140A;
        ACCENT3_BG_ACTIVE = 0xFF30190C;
        ACCENT3_BG_HOVER = 0xFF411E0F;
        ACCENT3_BORDER = 0xFF7D401C;
        ACCENT3_BORDER_ON = 0xFFC84B26;
        CUSTOM_ACCENT = 0xFF8C28A0;
        CUSTOM_ACCENT_HOVER = 0xFFC35ED7;
        CUSTOM_ACCENT_DIM = 0xFF461450;
        CUSTOM_ACCENT_BG = 0xFF201217;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2C1526;
        CUSTOM_ACCENT_BG_HOVER = 0xFF3A1839;
        CUSTOM_ACCENT_BORDER = 0xFF73365D;
        CUSTOM_ACCENT_BORDER_ON = 0xFFAD31C5;
        HEADER_BG = 0xFF816730;
        HEADER_BG_HOVER = 0xFF896D30;
        HEADER_BORDER = 0xFFCC9B34;
        HEADER_TEXT = 0xFFF0C85A;
        HEADER_ARROW = 0xFF285A32;
        ROW_BG_EVEN = 0x300F0C06;
        ROW_BG_ODD = 0x303D3119;
        ROW_BG_HOVER = 0x22EFD99E;
        ROW_BORDER_HOVER = 0xFFC89628;
        ROW_BG_SELECTED = 0x408C28A0;
        ROW_BORDER_SEL = 0xFF8C28A0;
        ROW_BORDER_SEL_IN = 0xFFBE51D4;
        TEXT_PRIMARY = 0xFFE6D7B4;
        TEXT_MUTED = 0xFFA08C64;
        TEXT_FAINT = 0xFF7F6E4E;
        TEXT_ON_ACCENT = 0xFF0C0A04;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFA03C1E;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFA03C1E;
        LOCKED = 0xFFE34234;
        INFO = 0xFF285A32;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1C1609;
        UTIL_BG_HOVER = 0xFF281F0B;
        UTIL_BORDER = 0xFF7A5E1E;
        UTIL_BORDER_HOVER = 0xFFE5BF5E;
        UTIL_TEXT = 0xFFA08C64;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyPorcelainBlueWhite() {
        BACKGROUND = 0xE0E8EEF4;
        WINDOW_BORDER = 0xFF28508C;
        WINDOW_BORDER_DIM = 0xFF162C4D;
        PANEL = 0xFFF4F8FC;
        PANEL_RAISED = 0xFFF7FAFD;
        PANEL_BORDER = 0xFF5A82B4;
        PANEL_BORDER_LIGHT = 0xFF94AECE;
        ACCENT = 0xFF1E5096;
        ACCENT_HOVER = 0xFF4C88DB;
        ACCENT_DIM = 0xFF0E2443;
        ACCENT_BG = 0xFFDFE7F2;
        ACCENT_BG_ACTIVE = 0xFFC9D6E8;
        ACCENT_BG_HOVER = 0xFFB0C2DB;
        ACCENT2 = 0xFF78A0BE;
        ACCENT2_HOVER = 0xFFA7C1D5;
        ACCENT2_BG = 0xFFE8EFF6;
        ACCENT2_BG_ACTIVE = 0xFFDBE6F0;
        ACCENT2_BG_HOVER = 0xFFCCDCE8;
        ACCENT3 = 0xFFB43C3C;
        ACCENT3_HOVER = 0xFFD07171;
        ACCENT3_BG = 0xFFEEE5E9;
        ACCENT3_BG_ACTIVE = 0xFFE7D2D6;
        ACCENT3_BG_HOVER = 0xFFE0BCBF;
        ACCENT3_BORDER = 0xFF875F78;
        ACCENT3_BORDER_ON = 0xFFC65353;
        CUSTOM_ACCENT = 0xFF3C6E5A;
        CUSTOM_ACCENT_HOVER = 0xFF70B197;
        CUSTOM_ACCENT_DIM = 0xFF1E372D;
        CUSTOM_ACCENT_BG = 0xFFE2EAEC;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFFCFDCDC;
        CUSTOM_ACCENT_BG_HOVER = 0xFFB9CCC8;
        CUSTOM_ACCENT_BORDER = 0xFF4B7887;
        CUSTOM_ACCENT_BORDER_ON = 0xFF4E8F75;
        HEADER_BG = 0xFFEAF0F7;
        HEADER_BG_HOVER = 0xFFD9E2EF;
        HEADER_BORDER = 0xFF7192BD;
        HEADER_TEXT = 0xFF143264;
        HEADER_ARROW = 0xFF78A0BE;
        ROW_BG_EVEN = 0x30D5E4F3;
        ROW_BG_ODD = 0x30ECF3FA;
        ROW_BG_HOVER = 0x2283ACE6;
        ROW_BORDER_HOVER = 0xFF28508C;
        ROW_BG_SELECTED = 0x403C6E5A;
        ROW_BORDER_SEL = 0xFF3C6E5A;
        ROW_BORDER_SEL_IN = 0xFF65AB8F;
        TEXT_PRIMARY = 0xFF141E2D;
        TEXT_MUTED = 0xFF5A697D;
        TEXT_FAINT = 0xFF717F93;
        TEXT_ON_ACCENT = 0xFFF5F8FC;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFB43C3C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFB43C3C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF78A0BE;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFFEBF1F8;
        UTIL_BG_HOVER = 0xFFDFE7F2;
        UTIL_BORDER = 0xFF4B76AC;
        UTIL_BORDER_HOVER = 0xFF3175D6;
        UTIL_TEXT = 0xFF5A697D;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyInkWash() {
        BACKGROUND = 0xE0E1DED6;
        WINDOW_BORDER = 0xFF282828;
        WINDOW_BORDER_DIM = 0xFF161616;
        PANEL = 0xFFECE9E0;
        PANEL_RAISED = 0xFFF2F0E9;
        PANEL_BORDER = 0xFF5A5850;
        PANEL_BORDER_LIGHT = 0xFF97948A;
        ACCENT = 0xFF28282A;
        ACCENT_HOVER = 0xFF717177;
        ACCENT_DIM = 0xFF121213;
        ACCENT_BG = 0xFFD8D6CE;
        ACCENT_BG_ACTIVE = 0xFFC5C2BC;
        ACCENT_BG_HOVER = 0xFFADABA6;
        ACCENT2 = 0xFF828078;
        ACCENT2_HOVER = 0xFFAEADA7;
        ACCENT2_BG = 0xFFE1DED6;
        ACCENT2_BG_ACTIVE = 0xFFD7D4CB;
        ACCENT2_BG_HOVER = 0xFFCAC7BF;
        ACCENT3 = 0xFFB42828;
        ACCENT3_HOVER = 0xFFDA5959;
        ACCENT3_BG = 0xFFE6D6CE;
        ACCENT3_BG_ACTIVE = 0xFFE1C2BB;
        ACCENT3_BG_HOVER = 0xFFDAABA5;
        ACCENT3_BORDER = 0xFF87403C;
        ACCENT3_BORDER_ON = 0xFFD23535;
        CUSTOM_ACCENT = 0xFF465A50;
        CUSTOM_ACCENT_HOVER = 0xFF7F9B8D;
        CUSTOM_ACCENT_DIM = 0xFF232D28;
        CUSTOM_ACCENT_BG = 0xFFDBDBD2;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFFCBCCC3;
        CUSTOM_ACCENT_BG_HOVER = 0xFFB7BBB2;
        CUSTOM_ACCENT_BORDER = 0xFF505950;
        CUSTOM_ACCENT_BORDER_ON = 0xFF5D786A;
        HEADER_BG = 0xFFE6E4DE;
        HEADER_BG_HOVER = 0xFFD6D4CE;
        HEADER_BORDER = 0xFF76746D;
        HEADER_TEXT = 0xFF1E1E1E;
        HEADER_ARROW = 0xFF3C5A82;
        ROW_BG_EVEN = 0x30DED9C9;
        ROW_BG_ODD = 0x30E9E5DA;
        ROW_BG_HOVER = 0x229C9CA1;
        ROW_BORDER_HOVER = 0xFF282828;
        ROW_BG_SELECTED = 0x40465A50;
        ROW_BORDER_SEL = 0xFF465A50;
        ROW_BORDER_SEL_IN = 0xFF759484;
        TEXT_PRIMARY = 0xFF191919;
        TEXT_MUTED = 0xFF5F5C55;
        TEXT_FAINT = 0xFF78746B;
        TEXT_ON_ACCENT = 0xFFF0EEE6;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFB42828;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFB42828;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3C5A82;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFFE4E1D9;
        UTIL_BG_HOVER = 0xFFD8D6CE;
        UTIL_BORDER = 0xFF4E4C46;
        UTIL_BORDER_HOVER = 0xFF5C5C61;
        UTIL_TEXT = 0xFF5F5C55;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyCherryBlossom() {
        BACKGROUND = 0xE0FAEBEE;
        WINDOW_BORDER = 0xFFDC8CA5;
        WINDOW_BORDER_DIM = 0xFF982E4F;
        PANEL = 0xFFFFF4F6;
        PANEL_RAISED = 0xFFFFF7F9;
        PANEL_BORDER = 0xFFE6AABE;
        PANEL_BORDER_LIGHT = 0xFFEFC8D5;
        ACCENT = 0xFFE6789B;
        ACCENT_HOVER = 0xFFEFA7BE;
        ACCENT_DIM = 0xFF85193B;
        ACCENT_BG = 0xFFFCE8ED;
        ACCENT_BG_ACTIVE = 0xFFFADBE4;
        ACCENT_BG_HOVER = 0xFFF7CCD9;
        ACCENT2 = 0xFF78AA8C;
        ACCENT2_HOVER = 0xFFA7C8B4;
        ACCENT2_BG = 0xFFF2EDEB;
        ACCENT2_BG_ACTIVE = 0xFFE4E5E1;
        ACCENT2_BG_HOVER = 0xFFD4DCD4;
        ACCENT3 = 0xFFC8506E;
        ACCENT3_HOVER = 0xFFD8849A;
        ACCENT3_BG = 0xFFFAE4E8;
        ACCENT3_BG_ACTIVE = 0xFFF4D3DB;
        ACCENT3_BG_HOVER = 0xFFEDC0CA;
        ACCENT3_BORDER = 0xFFD77D96;
        ACCENT3_BORDER_ON = 0xFFD06A84;
        CUSTOM_ACCENT = 0xFFAA8CC8;
        CUSTOM_ACCENT_HOVER = 0xFFC8B4DB;
        CUSTOM_ACCENT_DIM = 0xFF553773;
        CUSTOM_ACCENT_BG = 0xFFF6EAF1;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFFEEDFED;
        CUSTOM_ACCENT_BG_HOVER = 0xFFE4D3E7;
        CUSTOM_ACCENT_BORDER = 0xFFC89BC3;
        CUSTOM_ACCENT_BORDER_ON = 0xFFB79DD0;
        HEADER_BG = 0xFFFEEFF3;
        HEADER_BG_HOVER = 0xFFFCE5EC;
        HEADER_BORDER = 0xFFECB0C4;
        HEADER_TEXT = 0xFFA03C5F;
        HEADER_ARROW = 0xFF78AA8C;
        ROW_BG_EVEN = 0x30FFCCD5;
        ROW_BG_ODD = 0x30FFEAEE;
        ROW_BG_HOVER = 0x22F4C2D2;
        ROW_BORDER_HOVER = 0xFFDC8CA5;
        ROW_BG_SELECTED = 0x40AA8CC8;
        ROW_BORDER_SEL = 0xFFAA8CC8;
        ROW_BORDER_SEL_IN = 0xFFC3AED9;
        TEXT_PRIMARY = 0xFF3C232A;
        TEXT_MUTED = 0xFF8B6872;
        TEXT_FAINT = 0xFF9B727D;
        TEXT_ON_ACCENT = 0xFFFFFAFB;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFAA8CC8;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFAA8CC8;
        LOCKED = 0xFFE34234;
        INFO = 0xFF78AA8C;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFFFEEFF2;
        UTIL_BG_HOVER = 0xFFFCE8ED;
        UTIL_BORDER = 0xFFE69EB5;
        UTIL_BORDER_HOVER = 0xFFEC9AB4;
        UTIL_TEXT = 0xFF886670;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyImperialVermillion() {
        BACKGROUND = 0xE0100606;
        WINDOW_BORDER = 0xFFD2A032;
        WINDOW_BORDER_DIM = 0xFF75591A;
        PANEL = 0xFF1A0A0A;
        PANEL_RAISED = 0xFF813131;
        PANEL_BORDER = 0xFF6E321E;
        PANEL_BORDER_LIGHT = 0xFFCB6442;
        ACCENT = 0xFFC8281E;
        ACCENT_HOVER = 0xFFE76961;
        ACCENT_DIM = 0xFF5A120D;
        ACCENT_BG = 0xFF2B0D0C;
        ACCENT_BG_ACTIVE = 0xFF3D100E;
        ACCENT_BG_HOVER = 0xFF521410;
        ACCENT2 = 0xFFD2A032;
        ACCENT2_HOVER = 0xFFE2C17A;
        ACCENT2_BG = 0xFF2C190E;
        ACCENT2_BG_ACTIVE = 0xFF3F2812;
        ACCENT2_BG_HOVER = 0xFF553A17;
        ACCENT3 = 0xFF8C283C;
        ACCENT3_HOVER = 0xFFCC4B65;
        ACCENT3_BG = 0xFF250D0F;
        ACCENT3_BG_ACTIVE = 0xFF311014;
        ACCENT3_BG_HOVER = 0xFF3E141A;
        ACCENT3_BORDER = 0xFF7D2D2D;
        ACCENT3_BORDER_ON = 0xFFB3334C;
        CUSTOM_ACCENT = 0xFF3C5A3C;
        CUSTOM_ACCENT_HOVER = 0xFF73A173;
        CUSTOM_ACCENT_DIM = 0xFF1E2D1E;
        CUSTOM_ACCENT_BG = 0xFF1D120F;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF211A14;
        CUSTOM_ACCENT_BG_HOVER = 0xFF25241A;
        CUSTOM_ACCENT_BORDER = 0xFF55462D;
        CUSTOM_ACCENT_BORDER_ON = 0xFF527A52;
        HEADER_BG = 0xFF853030;
        HEADER_BG_HOVER = 0xFF8B302E;
        HEADER_BORDER = 0xFFCA5237;
        HEADER_TEXT = 0xFFF0C364;
        HEADER_ARROW = 0xFF5082A0;
        ROW_BG_EVEN = 0x30130808;
        ROW_BG_ODD = 0x30431A1A;
        ROW_BG_HOVER = 0x22EF9791;
        ROW_BORDER_HOVER = 0xFFD2A032;
        ROW_BG_SELECTED = 0x403C5A3C;
        ROW_BORDER_SEL = 0xFF3C5A3C;
        ROW_BORDER_SEL_IN = 0xFF689A68;
        TEXT_PRIMARY = 0xFFEBD7C3;
        TEXT_MUTED = 0xFFA5826E;
        TEXT_FAINT = 0xFF86695B;
        TEXT_ON_ACCENT = 0xFF100606;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFD2A032;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFD2A032;
        LOCKED = 0xFFE34234;
        INFO = 0xFF5082A0;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF210B0B;
        UTIL_BG_HOVER = 0xFF2B0D0C;
        UTIL_BORDER = 0xFF84301E;
        UTIL_BORDER_HOVER = 0xFFE45248;
        UTIL_TEXT = 0xFFA5826E;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyBambooGrove() {
        BACKGROUND = 0xE00A100A;
        WINDOW_BORDER = 0xFF649646;
        WINDOW_BORDER_DIM = 0xFF375227;
        PANEL = 0xFF101810;
        PANEL_RAISED = 0xFF486D48;
        PANEL_BORDER = 0xFF32502D;
        PANEL_BORDER_LIGHT = 0xFF69A55F;
        ACCENT = 0xFF6EAA50;
        ACCENT_HOVER = 0xFFA0C98C;
        ACCENT_DIM = 0xFF324C24;
        ACCENT_BG = 0xFF192716;
        ACCENT_BG_ACTIVE = 0xFF23351D;
        ACCENT_BG_HOVER = 0xFF2E4724;
        ACCENT2 = 0xFF967846;
        ACCENT2_HOVER = 0xFFC3A97E;
        ACCENT2_BG = 0xFF1D2215;
        ACCENT2_BG_ACTIVE = 0xFF2B2B1B;
        ACCENT2_BG_HOVER = 0xFF3B3721;
        ACCENT3 = 0xFF5A8C96;
        ACCENT3_HOVER = 0xFF89B0B8;
        ACCENT3_BG = 0xFF17241D;
        ACCENT3_BG_ACTIVE = 0xFF1F2F2B;
        ACCENT3_BG_HOVER = 0xFF283D3B;
        ACCENT3_BORDER = 0xFF466E62;
        ACCENT3_BORDER_ON = 0xFF709FA9;
        CUSTOM_ACCENT = 0xFFC8BE78;
        CUSTOM_ACCENT_HOVER = 0xFFDBD5A7;
        CUSTOM_ACCENT_DIM = 0xFF72692E;
        CUSTOM_ACCENT_BG = 0xFF22291A;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF353925;
        CUSTOM_ACCENT_BG_HOVER = 0xFF4B4D31;
        CUSTOM_ACCENT_BORDER = 0xFF7D8752;
        CUSTOM_ACCENT_BORDER_ON = 0xFFD0C88C;
        HEADER_BG = 0xFF4A7148;
        HEADER_BG_HOVER = 0xFF4D7649;
        HEADER_BORDER = 0xFF6AA65A;
        HEADER_TEXT = 0xFFBEE196;
        HEADER_ARROW = 0xFF5A8C96;
        ROW_BG_EVEN = 0x300C120C;
        ROW_BG_ODD = 0x30273A27;
        ROW_BG_HOVER = 0x22BDDAAF;
        ROW_BORDER_HOVER = 0xFF649646;
        ROW_BG_SELECTED = 0x40C8BE78;
        ROW_BORDER_SEL = 0xFFC8BE78;
        ROW_BORDER_SEL_IN = 0xFFD9D2A0;
        TEXT_PRIMARY = 0xFFD7E6CD;
        TEXT_MUTED = 0xFF8CA082;
        TEXT_FAINT = 0xFF677862;
        TEXT_ON_ACCENT = 0xFF0A100A;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8BE78;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8BE78;
        LOCKED = 0xFFE34234;
        INFO = 0xFF5A8C96;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF141E13;
        UTIL_BG_HOVER = 0xFF192716;
        UTIL_BORDER = 0xFF416636;
        UTIL_BORDER_HOVER = 0xFF92C17A;
        UTIL_TEXT = 0xFF8CA082;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applySavannaDusk() {
        BACKGROUND = 0xE0140A06;
        WINDOW_BORDER = 0xFFE67828;
        WINDOW_BORDER_DIM = 0xFF85410F;
        PANEL = 0xFF1E100A;
        PANEL_RAISED = 0xFF88482D;
        PANEL_BORDER = 0xFF6E3C1E;
        PANEL_BORDER_LIGHT = 0xFFCB7642;
        ACCENT = 0xFFE67828;
        ACCENT_HOVER = 0xFFEFA773;
        ACCENT_DIM = 0xFF6D350D;
        ACCENT_BG = 0xFF321A0D;
        ACCENT_BG_ACTIVE = 0xFF462510;
        ACCENT_BG_HOVER = 0xFF5E3114;
        ACCENT2 = 0xFFC89632;
        ACCENT2_HOVER = 0xFFDDBB78;
        ACCENT2_BG = 0xFF2F1D0E;
        ACCENT2_BG_ACTIVE = 0xFF402B12;
        ACCENT2_BG_HOVER = 0xFF543B17;
        ACCENT3 = 0xFF963C28;
        ACCENT3_HOVER = 0xFFD0664E;
        ACCENT3_BG = 0xFF2A140D;
        ACCENT3_BG_ACTIVE = 0xFF361910;
        ACCENT3_BG_HOVER = 0xFF441E14;
        ACCENT3_BORDER = 0xFF823C23;
        ACCENT3_BORDER_ON = 0xFFBC4B32;
        CUSTOM_ACCENT = 0xFF5A3250;
        CUSTOM_ACCENT_HOVER = 0xFFA96498;
        CUSTOM_ACCENT_DIM = 0xFF2D1928;
        CUSTOM_ACCENT_BG = 0xFF241311;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2A1718;
        CUSTOM_ACCENT_BG_HOVER = 0xFF311B20;
        CUSTOM_ACCENT_BORDER = 0xFF643737;
        CUSTOM_ACCENT_BORDER_ON = 0xFF7E4670;
        HEADER_BG = 0xFF8E4B2D;
        HEADER_BG_HOVER = 0xFF954F2C;
        HEADER_BORDER = 0xFFD3773A;
        HEADER_TEXT = 0xFFF0AA5A;
        HEADER_ARROW = 0xFF5A3250;
        ROW_BG_EVEN = 0x30160C08;
        ROW_BG_ODD = 0x30482718;
        ROW_BG_HOVER = 0x22F4C29E;
        ROW_BORDER_HOVER = 0xFFE67828;
        ROW_BG_SELECTED = 0x405A3250;
        ROW_BORDER_SEL = 0xFF5A3250;
        ROW_BORDER_SEL_IN = 0xFFA15A8F;
        TEXT_PRIMARY = 0xFFEBD2B4;
        TEXT_MUTED = 0xFFA58769;
        TEXT_FAINT = 0xFF886C55;
        TEXT_ON_ACCENT = 0xFF120804;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC89632;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC89632;
        LOCKED = 0xFFE34234;
        INFO = 0xFF5A3250;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF26140B;
        UTIL_BG_HOVER = 0xFF321A0D;
        UTIL_BORDER = 0xFF8C4B20;
        UTIL_BORDER_HOVER = 0xFFEC9A5E;
        UTIL_TEXT = 0xFFA58769;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyMaskAndDrum() {
        BACKGROUND = 0xE00C0606;
        WINDOW_BORDER = 0xFFC8A028;
        WINDOW_BORDER_DIM = 0xFF6E5816;
        PANEL = 0xFF140A0A;
        PANEL_RAISED = 0xFF743A3A;
        PANEL_BORDER = 0xFF5A281E;
        PANEL_BORDER_LIGHT = 0xFFC05641;
        ACCENT = 0xFFB41E1E;
        ACCENT_HOVER = 0xFFE35858;
        ACCENT_DIM = 0xFF510D0D;
        ACCENT_BG = 0xFF240C0C;
        ACCENT_BG_ACTIVE = 0xFF340E0E;
        ACCENT_BG_HOVER = 0xFF471010;
        ACCENT2 = 0xFFC8A028;
        ACCENT2_HOVER = 0xFFE2C46D;
        ACCENT2_BG = 0xFF26190D;
        ACCENT2_BG_ACTIVE = 0xFF382810;
        ACCENT2_BG_HOVER = 0xFF4E3A14;
        ACCENT3 = 0xFF281E14;
        ACCENT3_HOVER = 0xFF826241;
        ACCENT3_BG = 0xFF160C0B;
        ACCENT3_BG_ACTIVE = 0xFF180E0C;
        ACCENT3_BG_HOVER = 0xFF1A100D;
        ACCENT3_BORDER = 0xFF412319;
        ACCENT3_BORDER_ON = 0xFF55402B;
        CUSTOM_ACCENT = 0xFF3C6E5A;
        CUSTOM_ACCENT_HOVER = 0xFF70B197;
        CUSTOM_ACCENT_DIM = 0xFF1E372D;
        CUSTOM_ACCENT_BG = 0xFF181412;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF1C1E1A;
        CUSTOM_ACCENT_BG_HOVER = 0xFF212A24;
        CUSTOM_ACCENT_BORDER = 0xFF4B4B3C;
        CUSTOM_ACCENT_BORDER_ON = 0xFF4E8F75;
        HEADER_BG = 0xFF783838;
        HEADER_BG_HOVER = 0xFF7D3636;
        HEADER_BORDER = 0xFFBC4536;
        HEADER_TEXT = 0xFFE6B450;
        HEADER_ARROW = 0xFF3C6E5A;
        ROW_BG_EVEN = 0x300F0808;
        ROW_BG_ODD = 0x303A1D1D;
        ROW_BG_HOVER = 0x22EC8B8B;
        ROW_BORDER_HOVER = 0xFFC8A028;
        ROW_BG_SELECTED = 0x403C6E5A;
        ROW_BORDER_SEL = 0xFF3C6E5A;
        ROW_BORDER_SEL_IN = 0xFF65AB8F;
        TEXT_PRIMARY = 0xFFE6D2BE;
        TEXT_MUTED = 0xFFA08269;
        TEXT_FAINT = 0xFF816A54;
        TEXT_ON_ACCENT = 0xFF0C0604;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8A028;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8A028;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3C6E5A;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1A0B0B;
        UTIL_BG_HOVER = 0xFF240C0C;
        UTIL_BORDER = 0xFF70261E;
        UTIL_BORDER_HOVER = 0xFFDF3E3E;
        UTIL_TEXT = 0xFFA08269;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyAncestralFire() {
        BACKGROUND = 0xE00A0404;
        WINDOW_BORDER = 0xFFFF6E1E;
        WINDOW_BORDER_DIM = 0xFF9D3800;
        PANEL = 0xFF120806;
        PANEL_RAISED = 0xFF7F392A;
        PANEL_BORDER = 0xFF6E2814;
        PANEL_BORDER_LIGHT = 0xFFD9542E;
        ACCENT = 0xFFFF6E1E;
        ACCENT_HOVER = 0xFFFFA16D;
        ACCENT_DIM = 0xFF802E00;
        ACCENT_BG = 0xFF2A1208;
        ACCENT_BG_ACTIVE = 0xFF411C0B;
        ACCENT_BG_HOVER = 0xFF5E290E;
        ACCENT2 = 0xFFFF3C1E;
        ACCENT2_HOVER = 0xFFFF806D;
        ACCENT2_BG = 0xFF2A0D08;
        ACCENT2_BG_ACTIVE = 0xFF41120B;
        ACCENT2_BG_HOVER = 0xFF5E190E;
        ACCENT3 = 0xFFFFC83C;
        ACCENT3_HOVER = 0xFFFFD877;
        ACCENT3_BG = 0xFF2A1B0B;
        ACCENT3_BG_ACTIVE = 0xFF412E11;
        ACCENT3_BG_HOVER = 0xFF5E4517;
        ACCENT3_BORDER = 0xFFB67828;
        ACCENT3_BORDER_ON = 0xFFFFD059;
        CUSTOM_ACCENT = 0xFF96321E;
        CUSTOM_ACCENT_HOVER = 0xFFDB644C;
        CUSTOM_ACCENT_DIM = 0xFF4B190F;
        CUSTOM_ACCENT_BG = 0xFF1F0C08;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2C100B;
        CUSTOM_ACCENT_BG_HOVER = 0xFF3C150E;
        CUSTOM_ACCENT_BORDER = 0xFF822D19;
        CUSTOM_ACCENT_BORDER_ON = 0xFFBF4026;
        HEADER_BG = 0xFF873C29;
        HEADER_BG_HOVER = 0xFF914028;
        HEADER_BORDER = 0xFFE45C29;
        HEADER_TEXT = 0xFFFF963C;
        HEADER_ARROW = 0xFFC85A32;
        ROW_BG_EVEN = 0x300E0604;
        ROW_BG_ODD = 0x303E1B15;
        ROW_BG_HOVER = 0x22FFBE9A;
        ROW_BORDER_HOVER = 0xFFFF6E1E;
        ROW_BG_SELECTED = 0x4096321E;
        ROW_BORDER_SEL = 0xFF96321E;
        ROW_BORDER_SEL_IN = 0xFFD9583F;
        TEXT_PRIMARY = 0xFFF0D7C3;
        TEXT_MUTED = 0xFFAA8269;
        TEXT_FAINT = 0xFF866750;
        TEXT_ON_ACCENT = 0xFF0A0404;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFC83C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFC83C;
        LOCKED = 0xFFE34234;
        INFO = 0xFFC85A32;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1B0C07;
        UTIL_BG_HOVER = 0xFF2A1208;
        UTIL_BORDER = 0xFF923A16;
        UTIL_BORDER_HOVER = 0xFFFF9256;
        UTIL_TEXT = 0xFFAA8269;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyKenteWeave() {
        BACKGROUND = 0xE00A0804;
        WINDOW_BORDER = 0xFFE6BE28;
        WINDOW_BORDER_DIM = 0xFF856C0F;
        PANEL = 0xFF120E08;
        PANEL_RAISED = 0xFF775C35;
        PANEL_BORDER = 0xFF5A4614;
        PANEL_BORDER_LIGHT = 0xFFCD9F2D;
        ACCENT = 0xFFE6BE28;
        ACCENT_HOVER = 0xFFEFD573;
        ACCENT_DIM = 0xFF6D590D;
        ACCENT_BG = 0xFF27200B;
        ACCENT_BG_ACTIVE = 0xFF3C310E;
        ACCENT_BG_HOVER = 0xFF564612;
        ACCENT2 = 0xFF1E8C46;
        ACCENT2_HOVER = 0xFF49D87D;
        ACCENT2_BG = 0xFF131B0E;
        ACCENT2_BG_ACTIVE = 0xFF142714;
        ACCENT2_BG_HOVER = 0xFF16361C;
        ACCENT3 = 0xFFC81E28;
        ACCENT3_HOVER = 0xFFE5555D;
        ACCENT3_BG = 0xFF24100B;
        ACCENT3_BG_ACTIVE = 0xFF36110E;
        ACCENT3_BG_HOVER = 0xFF4C1312;
        ACCENT3_BORDER = 0xFF91321E;
        ACCENT3_BORDER_ON = 0xFFE0303A;
        CUSTOM_ACCENT = 0xFF1E3C8C;
        CUSTOM_ACCENT_HOVER = 0xFF4970D8;
        CUSTOM_ACCENT_DIM = 0xFF0F1E46;
        CUSTOM_ACCENT_BG = 0xFF131315;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF141722;
        CUSTOM_ACCENT_BG_HOVER = 0xFF161D32;
        CUSTOM_ACCENT_BORDER = 0xFF3C4150;
        CUSTOM_ACCENT_BORDER_ON = 0xFF274EB6;
        HEADER_BG = 0xFF7E6234;
        HEADER_BG_HOVER = 0xFF876A33;
        HEADER_BORDER = 0xFFD4A82C;
        HEADER_TEXT = 0xFFF0C83C;
        HEADER_ARROW = 0xFF1E3C8C;
        ROW_BG_EVEN = 0x300E0B06;
        ROW_BG_ODD = 0x303A2D1A;
        ROW_BG_HOVER = 0x22F4E29E;
        ROW_BORDER_HOVER = 0xFFE6BE28;
        ROW_BG_SELECTED = 0x401E3C8C;
        ROW_BORDER_SEL = 0xFF1E3C8C;
        ROW_BORDER_SEL_IN = 0xFF3B65D5;
        TEXT_PRIMARY = 0xFFE6DCC8;
        TEXT_MUTED = 0xFFA09678;
        TEXT_FAINT = 0xFF776E57;
        TEXT_ON_ACCENT = 0xFF0A0804;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC81E28;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC81E28;
        LOCKED = 0xFFE34234;
        INFO = 0xFF1E3C8C;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1A1509;
        UTIL_BG_HOVER = 0xFF27200B;
        UTIL_BORDER = 0xFF7D6419;
        UTIL_BORDER_HOVER = 0xFFECCE5E;
        UTIL_TEXT = 0xFFA09678;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyDesertShaman() {
        BACKGROUND = 0xE01C140A;
        WINDOW_BORDER = 0xFF50B4AA;
        WINDOW_BORDER_DIM = 0xFF2B645E;
        PANEL = 0xFF261C10;
        PANEL_RAISED = 0xFF866339;
        PANEL_BORDER = 0xFF785A32;
        PANEL_BORDER_LIGHT = 0xFFBE9763;
        ACCENT = 0xFF50B4AA;
        ACCENT_HOVER = 0xFF8DCEC8;
        ACCENT_DIM = 0xFF23524D;
        ACCENT_BG = 0xFF2A2B1F;
        ACCENT_BG_ACTIVE = 0xFF2E3A2F;
        ACCENT_BG_HOVER = 0xFF334D41;
        ACCENT2 = 0xFFC8783C;
        ACCENT2_HOVER = 0xFFDBA780;
        ACCENT2_BG = 0xFF362514;
        ACCENT2_BG_ACTIVE = 0xFF462E19;
        ACCENT2_BG_HOVER = 0xFF5A391E;
        ACCENT3 = 0xFFD2B478;
        ACCENT3_HOVER = 0xFFE0CBA0;
        ACCENT3_BG = 0xFF372B1A;
        ACCENT3_BG_ACTIVE = 0xFF483A25;
        ACCENT3_BG_HOVER = 0xFF5D4D31;
        ACCENT3_BORDER = 0xFFA58755;
        ACCENT3_BORDER_ON = 0xFFD9BF8C;
        CUSTOM_ACCENT = 0xFF963C32;
        CUSTOM_ACCENT_HOVER = 0xFFCD7268;
        CUSTOM_ACCENT_DIM = 0xFF4B1E19;
        CUSTOM_ACCENT_BG = 0xFF311F13;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF3C2217;
        CUSTOM_ACCENT_BG_HOVER = 0xFF4A261B;
        CUSTOM_ACCENT_BORDER = 0xFF874B32;
        CUSTOM_ACCENT_BORDER_ON = 0xFFB94A3E;
        HEADER_BG = 0xFF836840;
        HEADER_BG_HOVER = 0xFF7E6E49;
        HEADER_BORDER = 0xFF9DA078;
        HEADER_TEXT = 0xFF8CDCCD;
        HEADER_ARROW = 0xFF50B4AA;
        ROW_BG_EVEN = 0x301C150C;
        ROW_BG_ODD = 0x304D3820;
        ROW_BG_HOVER = 0x22B0DDD9;
        ROW_BORDER_HOVER = 0xFF50B4AA;
        ROW_BG_SELECTED = 0x40963C32;
        ROW_BORDER_SEL = 0xFF963C32;
        ROW_BORDER_SEL_IN = 0xFFC9675C;
        TEXT_PRIMARY = 0xFFE6D7BE;
        TEXT_MUTED = 0xFFAA916E;
        TEXT_FAINT = 0xFF8C755B;
        TEXT_ON_ACCENT = 0xFF100C06;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8783C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8783C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF50B4AA;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF282216;
        UTIL_BG_HOVER = 0xFF2A2B1F;
        UTIL_BORDER = 0xFF6E7050;
        UTIL_BORDER_HOVER = 0xFF7CC7BF;
        UTIL_TEXT = 0xFFAA916E;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyFrostSanctum() {
        BACKGROUND = 0xE0080E14;
        WINDOW_BORDER = 0xFF96DCFF;
        WINDOW_BORDER_DIM = 0xFF0095DF;
        PANEL = 0xFF0E161E;
        PANEL_RAISED = 0xFF3A5C7D;
        PANEL_BORDER = 0xFF3C6482;
        PANEL_BORDER_LIGHT = 0xFF719CBD;
        ACCENT = 0xFF96DCFF;
        ACCENT_HOVER = 0xFFBBE8FF;
        ACCENT_DIM = 0xFF007AB6;
        ACCENT_BG = 0xFF1C2A34;
        ACCENT_BG_ACTIVE = 0xFF293E4B;
        ACCENT_BG_HOVER = 0xFF3A5566;
        ACCENT2 = 0xFFDCF0FF;
        ACCENT2_HOVER = 0xFFE8F5FF;
        ACCENT2_BG = 0xFF232C34;
        ACCENT2_BG_ACTIVE = 0xFF37424B;
        ACCENT2_BG_HOVER = 0xFF505C66;
        ACCENT3 = 0xFF5A96DC;
        ACCENT3_HOVER = 0xFF8CB5E6;
        ACCENT3_BG = 0xFF162331;
        ACCENT3_BG_ACTIVE = 0xFF1D3044;
        ACCENT3_BG_HOVER = 0xFF263F5B;
        ACCENT3_BORDER = 0xFF4B7DAF;
        ACCENT3_BORDER_ON = 0xFF73A6E1;
        CUSTOM_ACCENT = 0xFFB4A0E6;
        CUSTOM_ACCENT_HOVER = 0xFFCEC1EF;
        CUSTOM_ACCENT_DIM = 0xFF49299A;
        CUSTOM_ACCENT_BG = 0xFF1F2432;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF2F3246;
        CUSTOM_ACCENT_BG_HOVER = 0xFF43425E;
        CUSTOM_ACCENT_BORDER = 0xFF7882B4;
        CUSTOM_ACCENT_BORDER_ON = 0xFFBFAEEA;
        HEADER_BG = 0xFF406485;
        HEADER_BG_HOVER = 0xFF476E8F;
        HEADER_BORDER = 0xFF7CAFD1;
        HEADER_TEXT = 0xFFC8EBFF;
        HEADER_ARROW = 0xFF96DCFF;
        ROW_BG_EVEN = 0x300A1016;
        ROW_BG_ODD = 0x30203244;
        ROW_BG_HOVER = 0x22D0EFFF;
        ROW_BORDER_HOVER = 0xFF96DCFF;
        ROW_BG_SELECTED = 0x40B4A0E6;
        ROW_BORDER_SEL = 0xFFB4A0E6;
        ROW_BORDER_SEL_IN = 0xFFCABCEE;
        TEXT_PRIMARY = 0xFFE1F0FA;
        TEXT_MUTED = 0xFF8CAABE;
        TEXT_FAINT = 0xFF5C768A;
        TEXT_ON_ACCENT = 0xFF060C12;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFC864;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFC864;
        LOCKED = 0xFFE34234;
        INFO = 0xFF96DCFF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF131E27;
        UTIL_BG_HOVER = 0xFF1C2A34;
        UTIL_BORDER = 0xFF5282A1;
        UTIL_BORDER_HOVER = 0xFFB0E5FF;
        UTIL_TEXT = 0xFF8CAABE;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyObsidianEmber() {
        BACKGROUND = 0xE0060608;
        WINDOW_BORDER = 0xFFFF641E;
        WINDOW_BORDER_DIM = 0xFF9D3100;
        PANEL = 0xFF0C0A0C;
        PANEL_RAISED = 0xFF5C4D5C;
        PANEL_BORDER = 0xFF3C281E;
        PANEL_BORDER_LIGHT = 0xFF9E694F;
        ACCENT = 0xFFFF641E;
        ACCENT_HOVER = 0xFFFF9A6D;
        ACCENT_DIM = 0xFF802800;
        ACCENT_BG = 0xFF24130E;
        ACCENT_BG_ACTIVE = 0xFF3D1C10;
        ACCENT_BG_HOVER = 0xFF5A2712;
        ACCENT2 = 0xFF9628C8;
        ACCENT2_HOVER = 0xFFBD6DE2;
        ACCENT2_BG = 0xFF1A0D1F;
        ACCENT2_BG_ACTIVE = 0xFF281032;
        ACCENT2_BG_HOVER = 0xFF381448;
        ACCENT3 = 0xFFFFC83C;
        ACCENT3_HOVER = 0xFFFFD877;
        ACCENT3_BG = 0xFF241D11;
        ACCENT3_BG_ACTIVE = 0xFF3D3016;
        ACCENT3_BG_HOVER = 0xFF5A471B;
        ACCENT3_BORDER = 0xFF9E782D;
        ACCENT3_BORDER_ON = 0xFFFFD059;
        CUSTOM_ACCENT = 0xFF50505A;
        CUSTOM_ACCENT_HOVER = 0xFF8A8A97;
        CUSTOM_ACCENT_DIM = 0xFF28282D;
        CUSTOM_ACCENT_BG = 0xFF131114;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF1A181C;
        CUSTOM_ACCENT_BG_HOVER = 0xFF222025;
        CUSTOM_ACCENT_BORDER = 0xFF463C3C;
        CUSTOM_ACCENT_BORDER_ON = 0xFF686875;
        HEADER_BG = 0xFF664E58;
        HEADER_BG_HOVER = 0xFF735053;
        HEADER_BORDER = 0xFFBB6840;
        HEADER_TEXT = 0xFFFF8C3C;
        HEADER_ARROW = 0xFF9628C8;
        ROW_BG_EVEN = 0x30090809;
        ROW_BG_ODD = 0x302C252C;
        ROW_BG_HOVER = 0x22FFB99A;
        ROW_BORDER_HOVER = 0xFFFF641E;
        ROW_BG_SELECTED = 0x4050505A;
        ROW_BORDER_SEL = 0xFF50505A;
        ROW_BORDER_SEL_IN = 0xFF81818F;
        TEXT_PRIMARY = 0xFFDCD2CD;
        TEXT_MUTED = 0xFF968782;
        TEXT_FAINT = 0xFF766B68;
        TEXT_ON_ACCENT = 0xFF060608;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFFC83C;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFFC83C;
        LOCKED = 0xFFE34234;
        INFO = 0xFF9628C8;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF160E0D;
        UTIL_BG_HOVER = 0xFF24130E;
        UTIL_BORDER = 0xFF6D371E;
        UTIL_BORDER_HOVER = 0xFFFF8B56;
        UTIL_TEXT = 0xFF968782;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    /** Deliberately multi-hue rather than one-hue-plus-shades like every other theme here:
     * magenta, green, orange and cyan each anchor a different accent role (tabs/selection,
     * custom-mod tabs, warnings/favorites, headers/info) instead of one hue tinting everything.
     * Direct answer to "themes that are not just one color... can have ten different
     * colors within them" - within this Theme system's existing multi-role architecture
     * (ACCENT/ACCENT2/ACCENT3/CUSTOM_ACCENT), not a new per-tab-index colour engine. */
    private static void applyPrismSpectrum() {
        BACKGROUND = 0xE00C0A14;
        WINDOW_BORDER = 0xFFEE2BAD;
        WINDOW_BORDER_DIM = 0xFF6B134E;
        PANEL = 0xFF141220;
        PANEL_RAISED = 0xFF241F38;
        PANEL_BORDER = 0xFF3A3350;
        PANEL_BORDER_LIGHT = 0xFF5C4F78;
        ACCENT = 0xFFEE2BAD;
        ACCENT_HOVER = 0xFFF36BC6;
        ACCENT_DIM = 0xFF6B134E;
        ACCENT_BG = 0xFF2F142F;
        ACCENT_BG_ACTIVE = 0xFF41163A;
        ACCENT_BG_HOVER = 0xFF571949;
        ACCENT2 = 0xFF2BEE6C;
        ACCENT2_HOVER = 0xFF6BF398;
        ACCENT2_BG = 0xFF142F25;
        ACCENT2_BG_ACTIVE = 0xFF16412C;
        ACCENT2_BG_HOVER = 0xFF195734;
        ACCENT3 = 0xFFEE932B;
        ACCENT3_HOVER = 0xFFF3B36B;
        ACCENT3_BG = 0xFF2F221C;
        ACCENT3_BG_ACTIVE = 0xFF412D1E;
        ACCENT3_BG_HOVER = 0xFF573A1F;
        ACCENT3_BORDER = 0xFFA7671E;
        ACCENT3_BORDER_ON = 0xFFF3B36B;
        CUSTOM_ACCENT = 0xFF3EC7EA;
        CUSTOM_ACCENT_HOVER = 0xFF78D8F0;
        CUSTOM_ACCENT_DIM = 0xFF1C5A69;
        CUSTOM_ACCENT_BG = 0xFF162A37;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF1A3848;
        CUSTOM_ACCENT_BG_HOVER = 0xFF1F4B5D;
        CUSTOM_ACCENT_BORDER = 0xFF2B8BA4;
        CUSTOM_ACCENT_BORDER_ON = 0xFF78D8F0;
        HEADER_BG = 0xFF761D5F;
        HEADER_BG_HOVER = 0xFF972175;
        HEADER_BORDER = 0xFFF36BC6;
        HEADER_TEXT = 0xFF82DBF1;
        HEADER_ARROW = 0xFF2BEE6C;
        ROW_BG_EVEN = 0x300A0810;
        ROW_BG_ODD = 0x30201C32;
        ROW_BG_HOVER = 0x22F36BC6;
        ROW_BORDER_HOVER = 0xFFEE2BAD;
        ROW_BG_SELECTED = 0x402BEE6C;
        ROW_BORDER_SEL = 0xFF2BEE6C;
        ROW_BORDER_SEL_IN = 0xFF6BF398;
        TEXT_PRIMARY = 0xFFF0EDF8;
        TEXT_MUTED = 0xFFAFA8C4;
        TEXT_FAINT = 0xFF827AA0;
        TEXT_ON_ACCENT = 0xFF0A0812;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFEE932B;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFEE932B;
        LOCKED = 0xFFE34234;
        INFO = 0xFF3EC7EA;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF110F1B;
        UTIL_BG_HOVER = 0xFF1C3344;
        UTIL_BORDER = 0xFF288198;
        UTIL_BORDER_HOVER = 0xFF78D8F0;
        UTIL_TEXT = 0xFFAFA8C4;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyMonochrome() {
        BACKGROUND = 0xE0000000;
        WINDOW_BORDER = 0xFFFFFFFF;
        WINDOW_BORDER_DIM = 0xFF8C8C8C;
        PANEL = 0xFF121212;
        PANEL_RAISED = 0xFF595959;
        PANEL_BORDER = 0xFF5A5A5A;
        PANEL_BORDER_LIGHT = 0xFF949494;
        ACCENT = 0xFFFFFFFF;
        ACCENT_HOVER = 0xFFFFFFFF;
        ACCENT_DIM = 0xFF737373;
        ACCENT_BG = 0xFF2A2A2A;
        ACCENT_BG_ACTIVE = 0xFF414141;
        ACCENT_BG_HOVER = 0xFF5E5E5E;
        ACCENT2 = 0xFFC8C8C8;
        ACCENT2_HOVER = 0xFFDBDBDB;
        ACCENT2_BG = 0xFF242424;
        ACCENT2_BG_ACTIVE = 0xFF363636;
        ACCENT2_BG_HOVER = 0xFF4C4C4C;
        ACCENT3 = 0xFF8C8C8C;
        ACCENT3_HOVER = 0xFFAEAEAE;
        ACCENT3_BG = 0xFF1E1E1E;
        ACCENT3_BG_ACTIVE = 0xFF2A2A2A;
        ACCENT3_BG_HOVER = 0xFF393939;
        ACCENT3_BORDER = 0xFF737373;
        ACCENT3_BORDER_ON = 0xFF9D9D9D;
        CUSTOM_ACCENT = 0xFFE6E6E6;
        CUSTOM_ACCENT_HOVER = 0xFFEFEFEF;
        CUSTOM_ACCENT_DIM = 0xFF737373;
        CUSTOM_ACCENT_BG = 0xFF272727;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF3C3C3C;
        CUSTOM_ACCENT_BG_HOVER = 0xFF565656;
        CUSTOM_ACCENT_BORDER = 0xFFA0A0A0;
        CUSTOM_ACCENT_BORDER_ON = 0xFFEAEAEA;
        HEADER_BG = 0xFF636363;
        HEADER_BG_HOVER = 0xFF707070;
        HEADER_BORDER = 0xFFB4B4B4;
        HEADER_TEXT = 0xFFFFFFFF;
        HEADER_ARROW = 0xFFC8C8C8;
        ROW_BG_EVEN = 0x300E0E0E;
        ROW_BG_ODD = 0x302E2E2E;
        ROW_BG_HOVER = 0x22FFFFFF;
        ROW_BORDER_HOVER = 0xFFFFFFFF;
        ROW_BG_SELECTED = 0x40E6E6E6;
        ROW_BORDER_SEL = 0xFFE6E6E6;
        ROW_BORDER_SEL_IN = 0xFFEEEEEE;
        TEXT_PRIMARY = 0xFFFFFFFF;
        TEXT_MUTED = 0xFFBEBEBE;
        TEXT_FAINT = 0xFF787878;
        TEXT_ON_ACCENT = 0xFF000000;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFC8C8C8;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFC8C8C8;
        LOCKED = 0xFFE34234;
        INFO = 0xFFC8C8C8;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF1B1B1B;
        UTIL_BG_HOVER = 0xFF2A2A2A;
        UTIL_BORDER = 0xFF838383;
        UTIL_BORDER_HOVER = 0xFFFFFFFF;
        UTIL_TEXT = 0xFFBEBEBE;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyHighContrast() {
        BACKGROUND = 0xE0000000;
        WINDOW_BORDER = 0xFFFFFF00;
        WINDOW_BORDER_DIM = 0xFF8C8C00;
        PANEL = 0xFF000000;
        PANEL_RAISED = 0xFF4C4C4C;
        PANEL_BORDER = 0xFFFFFF00;
        PANEL_BORDER_LIGHT = 0xFFFFFF59;
        ACCENT = 0xFFFFFF00;
        ACCENT_HOVER = 0xFFFFFF59;
        ACCENT_DIM = 0xFF737300;
        ACCENT_BG = 0xFF1A1A00;
        ACCENT_BG_ACTIVE = 0xFF333300;
        ACCENT_BG_HOVER = 0xFF525200;
        ACCENT2 = 0xFF00FFFF;
        ACCENT2_HOVER = 0xFF59FFFF;
        ACCENT2_BG = 0xFF001A1A;
        ACCENT2_BG_ACTIVE = 0xFF003333;
        ACCENT2_BG_HOVER = 0xFF005252;
        ACCENT3 = 0xFFFFFFFF;
        ACCENT3_HOVER = 0xFFFFFFFF;
        ACCENT3_BG = 0xFF1A1A1A;
        ACCENT3_BG_ACTIVE = 0xFF333333;
        ACCENT3_BG_HOVER = 0xFF525252;
        ACCENT3_BORDER = 0xFFFFFF80;
        ACCENT3_BORDER_ON = 0xFFFFFFFF;
        CUSTOM_ACCENT = 0xFF00FF00;
        CUSTOM_ACCENT_HOVER = 0xFF59FF59;
        CUSTOM_ACCENT_DIM = 0xFF008000;
        CUSTOM_ACCENT_BG = 0xFF001A00;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF003300;
        CUSTOM_ACCENT_BG_HOVER = 0xFF005200;
        CUSTOM_ACCENT_BORDER = 0xFF80FF00;
        CUSTOM_ACCENT_BORDER_ON = 0xFF26FF26;
        HEADER_BG = 0xFF575747;
        HEADER_BG_HOVER = 0xFF656541;
        HEADER_BORDER = 0xFFFFFF3E;
        HEADER_TEXT = 0xFFFFFF00;
        HEADER_ARROW = 0xFF00FFFF;
        ROW_BG_EVEN = 0x30000000;
        ROW_BG_ODD = 0x301F1F1F;
        ROW_BG_HOVER = 0x22FFFF8C;
        ROW_BORDER_HOVER = 0xFFFFFF00;
        ROW_BG_SELECTED = 0x4000FF00;
        ROW_BORDER_SEL = 0xFF00FF00;
        ROW_BORDER_SEL_IN = 0xFF4DFF4D;
        TEXT_PRIMARY = 0xFFFFFFFF;
        TEXT_MUTED = 0xFFDCDCDC;
        TEXT_FAINT = 0xFFB4B4B4;
        TEXT_ON_ACCENT = 0xFF000000;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFFFF4646;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFFFF4646;
        LOCKED = 0xFFE34234;
        INFO = 0xFF00FFFF;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF0A0A00;
        UTIL_BG_HOVER = 0xFF1A1A00;
        UTIL_BORDER = 0xFFFFFF00;
        UTIL_BORDER_HOVER = 0xFFFFFF40;
        UTIL_TEXT = 0xFFDCDCDC;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

    private static void applyGentleFocus() {
        BACKGROUND = 0xE01E1C1A;
        WINDOW_BORDER = 0xFF605A52;
        WINDOW_BORDER_DIM = 0xFF35322D;
        PANEL = 0xFF282623;
        PANEL_RAISED = 0xFF6E6860;
        PANEL_BORDER = 0xFF48443D;
        PANEL_BORDER_LIGHT = 0xFF8F877A;
        ACCENT = 0xFF968C78;
        ACCENT_HOVER = 0xFFBBB4A7;
        ACCENT_DIM = 0xFF443F35;
        ACCENT_BG = 0xFF33302C;
        ACCENT_BG_ACTIVE = 0xFF3E3A34;
        ACCENT_BG_HOVER = 0xFF4B473E;
        ACCENT2 = 0xFF788780;
        ACCENT2_HOVER = 0xFFA7B1AC;
        ACCENT2_BG = 0xFF30302C;
        ACCENT2_BG_ACTIVE = 0xFF383936;
        ACCENT2_BG_HOVER = 0xFF424541;
        ACCENT3 = 0xFF967870;
        ACCENT3_HOVER = 0xFFB6A09B;
        ACCENT3_BG = 0xFF332E2B;
        ACCENT3_BG_ACTIVE = 0xFF3E3632;
        ACCENT3_BG_HOVER = 0xFF4B403C;
        ACCENT3_BORDER = 0xFF6F5E56;
        ACCENT3_BORDER_ON = 0xFFA68C85;
        CUSTOM_ACCENT = 0xFF828294;
        CUSTOM_ACCENT_HOVER = 0xFFAEAEB9;
        CUSTOM_ACCENT_DIM = 0xFF40404B;
        CUSTOM_ACCENT_BG = 0xFF312F2E;
        CUSTOM_ACCENT_BG_ACTIVE = 0xFF3A383A;
        CUSTOM_ACCENT_BG_HOVER = 0xFF454347;
        CUSTOM_ACCENT_BORDER = 0xFF656368;
        CUSTOM_ACCENT_BORDER_ON = 0xFF9595A4;
        HEADER_BG = 0xFF706A61;
        HEADER_BG_HOVER = 0xFF746D63;
        HEADER_BORDER = 0xFF918879;
        HEADER_TEXT = 0xFFC0B8A8;
        HEADER_ARROW = 0xFF788780;
        ROW_BG_EVEN = 0x301E1C1A;
        ROW_BG_ODD = 0x3044403B;
        ROW_BG_HOVER = 0x22D0CBC2;
        ROW_BORDER_HOVER = 0xFF605A52;
        ROW_BG_SELECTED = 0x40828294;
        ROW_BORDER_SEL = 0xFF828294;
        ROW_BORDER_SEL_IN = 0xFFA8A8B4;
        TEXT_PRIMARY = 0xFFCAC4BA;
        TEXT_MUTED = 0xFF918C82;
        TEXT_FAINT = 0xFF857F76;
        TEXT_ON_ACCENT = 0xFF161412;
        SUCCESS = 0xFF55FF55;
        SUCCESS_BG = 0xFF205020;
        WARNING = 0xFF967870;
        ERROR = 0xFFE34234;
        ERROR_BG = 0xFF502020;
        FAVORITE = 0xFF967870;
        LOCKED = 0xFFE34234;
        INFO = 0xFF788780;
        SCROLL_TRACK = 0x40404040;
        SCROLL_THUMB = 0x90909090;
        SCROLL_THUMB_HOT = 0xC0C0C0C0;
        UTIL_BG = 0xFF2C2A26;
        UTIL_BG_HOVER = 0xFF33302C;
        UTIL_BORDER = 0xFF5C564C;
        UTIL_BORDER_HOVER = 0xFFB0A99A;
        UTIL_TEXT = 0xFF959086;
        UTIL_ON_BG = 0xFF203020;
        UTIL_ON_BG_HOVER = 0xFF305030;
        UTIL_ON_BORDER = 0xFF60FF80;
        UTIL_ON_TEXT = 0xFF80FFA0;
    }

}
