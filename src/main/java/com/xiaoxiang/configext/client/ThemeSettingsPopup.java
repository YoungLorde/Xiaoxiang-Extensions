package com.xiaoxiang.configext.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * The "config for the config" panel: picks the config screen's own visual
 * theme (see {@link Theme}), an optional custom accent-color override on top
 * of whichever theme is active, and the tab-click entrance animation's style
 * and speed. Opened from the small gear button next to the config screen's
 * title.
 *
 * Modeled on {@link ColorWheelPopup}'s open/close/isOpen/render/mouseClicked
 * shape so CustomConfigScreen wires it in exactly the same way it wires in
 * colorWheel/dropdownEnum/multiLineEditor - click-outside closes it, Escape
 * closes it, and while it's open it takes priority over every other click.
 *
 * Every change here takes effect immediately (no separate "Apply" step) and
 * is persisted straight to ExtendedConfig, mirroring how the rest of the
 * config screen already behaves.
 */
public class ThemeSettingsPopup {

    private boolean open = false;

    // A small, hue-spread subset of ColorPresets - a full grid of 60+ presets
    // would make this popup enormous; ten swatches across the wheel plus the
    // three semantic accents already used elsewhere (gold, jade, cinnabar) is
    // enough to reskin the accent without leaving the popup.
    private static final String[] ACCENT_SWATCHES = {
            "Gold", "Amber", "Vermillion", "Crimson", "Rose",
            "Jade", "Emerald", "Turquoise", "Sky Blue", "Violet"
    };

    // ── Fixed "Classic Jade & Gold" palette for THIS popup's own chrome ──
    // The config-for-the-config panel needs a visual identity that stays put
    // while the user experiments with the main screen's theme - "the theme
    // for the config menu... should be different compared to everything
    // else... there should be separate themes." These are literal copies of
    // Theme.applyClassic()'s values, NOT live reads of the mutable Theme.*
    // fields (which track whatever theme is currently active on the main
    // screen) - so this popup renders identically no matter what Theme.current
    // is. Kept as a small local set rather than a new Theme.java concept
    // (a "palette that never changes") to avoid touching the shared Theme
    // class that every other screen element still depends on.
    private static final int MENU_PANEL = 0xFF181820;
    private static final int MENU_PANEL_BORDER = 0xFF3C3C56;
    private static final int MENU_PANEL_BORDER_LIGHT = 0xFF5C5C7E;
    private static final int MENU_WINDOW_BORDER = 0xFFA0A060;
    private static final int MENU_WINDOW_BORDER_DIM = 0xFF5A5A3C;
    private static final int MENU_ACCENT = 0xFFFFD700;
    private static final int MENU_ACCENT_BG_ACTIVE = 0xFF3D2B00;
    private static final int MENU_HEADER_TEXT = 0xFFFFE060;
    private static final int MENU_TEXT_PRIMARY = 0xFFFFFFFF;
    private static final int MENU_TEXT_MUTED = 0xFFAAAAAA;
    private static final int MENU_TEXT_FAINT = 0xFF808080;
    private static final int MENU_TEXT_ON_ACCENT = 0xFFFFE040;
    private static final int MENU_SCROLL_TRACK = 0x40404040;
    private static final int MENU_SCROLL_THUMB = 0x90909090;
    private static final int MENU_UTIL_BG = 0xFF202030;
    private static final int MENU_UTIL_BG_HOVER = 0xFF303050;
    private static final int MENU_UTIL_BORDER = 0xFF404060;
    private static final int MENU_UTIL_BORDER_HOVER = 0xFF80A0FF;
    private static final int MENU_UTIL_ON_BG = 0xFF203020;
    private static final int MENU_UTIL_ON_BORDER = 0xFF60FF80;
    private static final int MENU_UTIL_ON_TEXT = 0xFF80FFA0;

    // Populated by render(), consumed by mouseClicked() - same pattern the
    // rest of this mod's popups (and CustomConfigScreen itself) already use
    // for hit-testing custom-drawn rows rather than vanilla widgets.
    // ── Theme grid ── reverted from a dropdown back to the original
    // always-visible 2-column grid: the dropdown's expanded list had a
    // reported z-order bug (other popup sections rendering on top of it
    // instead of the reverse) that wasn't safe to chase blind in a sandbox
    // with no compiler and no way to actually see the game render a frame.
    // A flat grid has no separate floating layer to get the z-order of
    // wrong - every tile draws once, in place, inside the same scrollable
    // region as everything else in this popup.
    private final List<int[]> themeRects = new ArrayList<>();
    private final List<Theme.ThemeId> themeRectIds = new ArrayList<>();
    // Collapsed by default: a clickable header row toggles the grid open/shut,
    // same idea as the main entry list's own collapsible group headers (an
    // inline layout change, not a floating overlay - "make the theme button a
    // button to click... bring down the list... click again, it retracts").
    private boolean themeSectionCollapsed = true;
    private int[] themeSectionHeaderRect = new int[4];
    private int[] enableToggleRect = new int[4];
    // ── Custom accent color (color wheel), beyond the 10 preset swatches
    // below. Deliberately NOT built on top of ColorWheelPopup: that class
    // applies its pick through a generic ConfigValueAccessor.setValueFromString
    // (configPath, ...) call, which is the right fit for the main entry list's
    // per-row color pickers but doesn't know to also flip
    // CLIENT_UI_ACCENT_OVERRIDE_ENABLED on and re-run Theme.applyTheme() -
    // without that second step the live palette wouldn't actually update as
    // the wheel is dragged, only after the screen was reopened. Rendered
    // inline in this popup's own scrollable content (not a floating overlay)
    // for the same reason the theme grid above went flat: nothing after it in
    // the same frame can render on top of it by accident.
    private boolean customWheelOpen = false;
    private float customHue = 0f, customSat = 1f, customBright = 1f;
    private int wheelCenterX, wheelCenterY;
    private static final int WHEEL_RADIUS = 42;
    private int[] customWheelToggleRect = new int[4];
    private int briSliderX, briSliderY, briSliderW, briSliderH;
    private boolean draggingWheel = false;
    private boolean draggingSlider = false;
    private final List<int[]> swatchRects = new ArrayList<>();
    private int[] styleRect = new int[4];
    private int[] speedMinusRect = new int[4];
    private int[] speedPlusRect = new int[4];
    private int[] sizeMinusRect = new int[4];
    private int[] sizePlusRect = new int[4];
    private int[] intensityRect = new int[4];
    // ── UI Sounds ("Sound Cycler") ──
    private static final String[] SOUND_SLOT_LABELS = {"Tab Hover", "Entry Hover", "Tab Click", "Config Open"};
    private int[] soundEnableToggleRect = new int[4];
    private int[] soundVolMinusRect = new int[4];
    private int[] soundVolPlusRect = new int[4];
    // One prev/play/next rect per slot in SOUND_SLOT_LABELS order, rebuilt every render().
    private final List<int[]> soundPrevRects = new ArrayList<>();
    private final List<int[]> soundPlayRects = new ArrayList<>();
    private final List<int[]> soundNextRects = new ArrayList<>();
    private int popupX, popupY, popupW, popupH;

    // ── Scrolling ── the popup grew a lot once Text Size and UI Sounds were
    // added (theme grid alone is 33 entries), so rather than keep growing
    // popupH to fit everything at once, popupH is now a fixed, modest size and
    // everything between the fixed title bar and the fixed footer hint scrolls
    // as one clipped region - same idea as the pinned-tooltip windows'
    // scrollable text area elsewhere in this mod, just pixel-based (this
    // popup's content is buttons/toggles of varying height, not uniform text
    // lines) instead of line-based.
    private int scrollOffset = 0;
    // contentTop/contentBottom bound the scrollable viewport in real screen
    // coordinates - recorded every render() so mouseClicked() can refuse to
    // treat a control as clickable when it has scrolled up into the fixed
    // title band or down into the fixed footer band (its stored rect would
    // otherwise still mathematically match there even though the scissor
    // clip means it isn't actually visible - see render()'s clamp comment).
    private int contentTop, contentBottom;
    // The full (unclipped) height of everything laid out inside the scroll
    // region, and the viewport height it was clipped to, both measured during
    // the PREVIOUS render() call. Content height here never actually depends
    // on any dynamic state (the theme count, sound slot count etc. are all
    // fixed), so a one-frame-old measurement is exactly as accurate as a
    // fresh one, and this avoids a fragile hand-counted constant.
    private int lastContentHeight = 0;
    private int lastViewportHeight = 0;

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
        this.draggingWheel = false;
        this.draggingSlider = false;
    }

    public boolean isOpen() {
        return open;
    }

    private boolean accentOverrideEnabled() {
        try {
            return ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.get();
        } catch (Exception e) {
            return false;
        }
    }

    private String animationStyle() {
        try {
            String s = ExtendedConfig.CLIENT_UI_TAB_ANIMATION_STYLE.get();
            return s == null ? "slide" : s;
        } catch (Exception e) {
            return "slide";
        }
    }

    private String descHighlightIntensity() {
        try {
            String s = ExtendedConfig.CLIENT_DESC_HIGHLIGHT_INTENSITY.get();
            return s == null ? "full" : s;
        } catch (Exception e) {
            return "full";
        }
    }

    private int animationSpeedPercent() {
        try {
            return ExtendedConfig.CLIENT_UI_TAB_ANIMATION_SPEED_PERCENT.get();
        } catch (Exception e) {
            return 100;
        }
    }

    /**
     * The user's text-size setting (50-200, ExtendedConfig's own defineInRange
     * bounds), as a scale multiplier. Reuses CLIENT_FONT_SIZE_PERCENT - the same
     * field CustomConfigScreen.getFontScale() reads - so this popup's own "Text
     * Size" control and any future main-list text scaling share one setting.
     */
    private int textSizePercent() {
        try {
            return ExtendedConfig.CLIENT_FONT_SIZE_PERCENT.get();
        } catch (Exception e) {
            return 100;
        }
    }

    private float textScale() {
        return textSizePercent() / 100.0f;
    }

    private boolean soundEnabled() {
        try {
            return ExtendedConfig.CLIENT_UI_SOUND_ENABLED.get();
        } catch (Exception e) {
            return true;
        }
    }

    private int soundVolumePercent() {
        try {
            return ExtendedConfig.CLIENT_UI_SOUND_VOLUME_PERCENT.get();
        } catch (Exception e) {
            return 60;
        }
    }

    /** The configured sound id (possibly "") for one of the four Sound Cycler slots, in SOUND_SLOT_LABELS order. */
    private String soundSlotId(int slot) {
        try {
            switch (slot) {
                case 0: return orEmpty(ExtendedConfig.CLIENT_UI_SOUND_TAB_HOVER.get());
                case 1: return orEmpty(ExtendedConfig.CLIENT_UI_SOUND_ENTRY_HOVER.get());
                case 2: return orEmpty(ExtendedConfig.CLIENT_UI_SOUND_TAB_CLICK.get());
                case 3: return orEmpty(ExtendedConfig.CLIENT_UI_SOUND_CONFIG_OPEN.get());
                default: return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void setSoundSlotId(int slot, String id) {
        try {
            switch (slot) {
                case 0: ExtendedConfig.CLIENT_UI_SOUND_TAB_HOVER.set(id); break;
                case 1: ExtendedConfig.CLIENT_UI_SOUND_ENTRY_HOVER.set(id); break;
                case 2: ExtendedConfig.CLIENT_UI_SOUND_TAB_CLICK.set(id); break;
                case 3: ExtendedConfig.CLIENT_UI_SOUND_CONFIG_OPEN.set(id); break;
                default: break;
            }
        } catch (Exception e) { /* ignore */ }
    }

    private static String orEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * Draws left-anchored text scaled around (x, y) - same translate+scale+draw-
     * at-origin technique as ItemPickerPopup.drawScaledString, just parameterized
     * by an explicit scale instead of a computed fit-to-width scale. Box fills and
     * outlines are drawn at their normal unscaled positions elsewhere in render(),
     * so hit-testing in mouseClicked() (which only checks those box rects) is
     * unaffected by this - at extreme scales the text can visually spill past its
     * button's outline, but clicking the button itself never misaligns.
     */
    // Every draw call below explicitly disables Minecraft's default text
    // shadow (the 6-arg drawString(... , false) overload). That shadow is a
    // dark 1px-offset duplicate of the glyphs - fine on saturated, high-
    // contrast text, but on this popup's body text it read as a smudged
    // double image, and on bold headers/titles (already double-drawn 1px
    // apart to fake bold) it compounded into the same problem. Direct answer
    // to "not only the text to be crisp, I want the bolded letters to be
    // crisp, like the titles to be crisp as well" - every label in this
    // popup, headers included, goes through one of these two helpers.
    private void drawScaledText(GuiGraphics g, Font font, String text, int x, int y, int color) {
        float scale = textScale();
        if (scale == 1.0f) {
            g.drawString(font, text, x, y, color, false);
            return;
        }
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0f);
        g.drawString(font, text, 0, 0, color, false);
        pose.popPose();
    }

    /** Same as {@link #drawScaledText} but for horizontally-centered text. Centres
     *  by hand via a plain drawString rather than GuiGraphics.drawCenteredString,
     *  which has no no-shadow overload to opt out of the same default shadow. */
    private void drawScaledCenteredText(GuiGraphics g, Font font, String text, int centerX, int y, int color) {
        float scale = textScale();
        int textW = font.width(text);
        if (scale == 1.0f) {
            g.drawString(font, text, centerX - textW / 2, y, color, false);
            return;
        }
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(centerX - (textW * scale) / 2f, y, 0);
        pose.scale(scale, scale, 1.0f);
        g.drawString(font, text, 0, 0, color, false);
        pose.popPose();
    }

    /** Maps a virtual content-space Y (0 = top of the scrollable region) to a real screen Y. */
    private int sy(int contentY) {
        return contentTop - scrollOffset + contentY;
    }

    /** Hue/saturation wheel, same concentric-ring technique as ColorWheelPopup.drawColorWheel. */
    private void drawColorWheel(GuiGraphics g, int cx, int cy, int radius) {
        for (int r = radius; r > 0; r--) {
            float sat = (float) r / radius;
            for (int a = 0; a < 360; a += 2) {
                float h = a / 360f;
                int color = java.awt.Color.HSBtoRGB(h, sat, customBright);
                int x1 = cx + (int) (Math.cos(Math.toRadians(a - 90)) * (r - 1));
                int y1 = cy + (int) (Math.sin(Math.toRadians(a - 90)) * (r - 1));
                int x2 = cx + (int) (Math.cos(Math.toRadians(a - 90)) * r);
                int y2 = cy + (int) (Math.sin(Math.toRadians(a - 90)) * r);
                g.fill(x1, y1, x2 + 1, y2 + 1, color | 0xFF000000);
            }
        }
        g.renderOutline(cx - radius, cy - radius, radius * 2, radius * 2, MENU_PANEL_BORDER_LIGHT);
    }

    /** Horizontal brightness slider, same technique as ColorWheelPopup.drawBrightnessSlider. */
    private void drawBrightnessSlider(GuiGraphics g, int x, int y, int w, int h) {
        for (int i = 0; i < w; i++) {
            float b = (float) i / w;
            int color = java.awt.Color.HSBtoRGB(customHue, customSat, b);
            g.fill(x + i, y, x + i + 1, y + h, color | 0xFF000000);
        }
        g.renderOutline(x, y, w, h, MENU_PANEL_BORDER_LIGHT);
        int indicatorX = x + (int) (customBright * w);
        g.fill(indicatorX - 1, y - 2, indicatorX + 2, y + h + 2, 0xFFFFFFFF);
        g.renderOutline(indicatorX - 1, y - 2, 3, h + 4, 0xFF000000);
    }

    private void updateFromWheel(double mouseX, double mouseY) {
        double dx = mouseX - wheelCenterX;
        double dy = mouseY - wheelCenterY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        customHue = (float) (angle / 360.0);
        customSat = (float) Math.min(1.0, dist / WHEEL_RADIUS);
        applyCustomColor();
    }

    private void updateFromSlider(double mouseX) {
        customBright = (float) Math.max(0, Math.min(1, (mouseX - briSliderX) / (double) briSliderW));
        applyCustomColor();
    }

    /** Same 3-step pattern the preset swatches below already use: set the colour,
     *  force the override on, then re-run applyTheme() so the live palette
     *  (MENU_ACCENT and friends) actually updates this frame instead of only
     *  on the next full theme switch. */
    private void applyCustomColor() {
        int rgb = java.awt.Color.HSBtoRGB(customHue, customSat, customBright) & 0xFFFFFF;
        try {
            ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_COLOR.set(0xFF000000 | rgb);
            ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.set(true);
            Theme.applyTheme(Theme.current);
        } catch (Exception e) { /* ignore */ }
    }

    /** Drag support for the wheel/slider above. Called from CustomConfigScreen.mouseDragged(). */
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!open || !customWheelOpen) return false;
        if (draggingWheel) {
            updateFromWheel(mouseX, mouseY);
            return true;
        }
        if (draggingSlider) {
            updateFromSlider(mouseX);
            return true;
        }
        return false;
    }

    /** Called from CustomConfigScreen.mouseReleased(). */
    public void mouseReleased(double mouseX, double mouseY, int button) {
        draggingWheel = false;
        draggingSlider = false;
    }

    public void render(GuiGraphics g, int screenWidth, int screenHeight, int mouseX, int mouseY, Font font) {
        if (!open) return;

        popupW = 300;
        // Fixed, modest popup height - big enough to comfortably show a few rows
        // without scrolling, but never so tall it runs off a short window. Every
        // section below (theme grid, accent override, tab animation, text size,
        // UI sounds) now scrolls as one clipped region instead of the popup
        // itself growing every time a new section is added - "the windows
        // shouldn't have to be too long".
        popupH = Math.max(160, Math.min(320, screenHeight - 40));
        popupX = screenWidth / 2 - popupW / 2;
        popupY = screenHeight / 2 - popupH / 2;

        // Render at the same elevated Z-layer the hover/pinned tooltips use (see
        // CustomConfigScreen's renderEntryTooltip: Z=1000 for the live hover tooltip,
        // 500+i*10 for pinned ones) so this modal settings panel never has other UI
        // bleed through it. This popup itself is fixed-position (screen-centered) and
        // intentionally not draggable, unlike the pinned tooltip windows.
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(0, 0, 2000);

        g.fill(popupX, popupY, popupX + popupW, popupY + popupH, MENU_PANEL);
        g.renderOutline(popupX, popupY, popupW, popupH, MENU_WINDOW_BORDER);
        g.renderOutline(popupX + 1, popupY + 1, popupW - 2, popupH - 2, MENU_WINDOW_BORDER_DIM);

        drawScaledCenteredText(g, font, "§l" + "Config Menu Settings", popupX + popupW / 2, popupY + 5, MENU_TEXT_ON_ACCENT);

        // Fixed title band above, fixed footer-hint band below - only the
        // region between them (contentTop..contentBottom) scrolls. A thin
        // divider line marks where the fixed title ends and the scrollable
        // body begins, so the split reads clearly rather than looking like
        // content is just being cut off.
        int titleAreaH = 16;
        int footerAreaH = 13;
        contentTop = popupY + titleAreaH;
        contentBottom = popupY + popupH - footerAreaH;
        int contentH = contentBottom - contentTop;
        g.fill(popupX + 2, contentTop - 1, popupX + popupW - 2, contentTop, MENU_PANEL_BORDER);

        // Clamp against LAST frame's measured content height (see the field
        // comment on lastContentHeight) before drawing this frame, so the
        // clamp is already correct for the rects/hover state this frame computes.
        int maxScroll = Math.max(0, lastContentHeight - lastViewportHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        g.enableScissor(popupX, contentTop, popupX + popupW, contentBottom);

        int cy = 0;

        // Clickable header row: toggles the grid below open/shut, laid out
        // inline (collapsing it just skips drawing the grid and skips
        // reserving cy space for it) rather than as a floating overlay - see
        // the field comment on themeSectionCollapsed for why that matters.
        Theme.ThemeId[] themeIds = Theme.ThemeId.values();
        String themeHeaderArrow = themeSectionCollapsed ? "▶" : "▼";
        String themeHeaderLabel = themeHeaderArrow + " Theme: " + Theme.current.displayName;
        themeSectionHeaderRect = new int[]{popupX + 10, sy(cy), popupW - 20, 11};
        boolean themeHeaderHover = hit(themeSectionHeaderRect, mouseX, mouseY);
        drawScaledText(g, font, "§l" + themeHeaderLabel, popupX + 10, sy(cy),
                themeHeaderHover ? MENU_ACCENT : MENU_HEADER_TEXT);
        cy += 11;

        themeRects.clear();
        themeRectIds.clear();
        if (!themeSectionCollapsed) {
            // 2-column grid of every theme (see the field comment above for why
            // this replaced the dropdown). Scrolls with the rest of this
            // popup's content - with 30+ themes that's a fair bit of scrolling
            // to get past when expanded, but that's the tradeoff for a
            // selector with no separate floating layer to render incorrectly.
            int gridCols = 2;
            int gridGap = 4;
            int cellW = (popupW - 20 - gridGap * (gridCols - 1)) / gridCols;
            int cellH = 16;
            int rowGap = 2;
            for (int i = 0; i < themeIds.length; i++) {
                int col = i % gridCols;
                int gridRow = i / gridCols;
                int tx = popupX + 10 + col * (cellW + gridGap);
                int ty = sy(cy) + gridRow * (cellH + rowGap);
                int[] rect = {tx, ty, cellW, cellH};
                boolean active = Theme.current == themeIds[i];
                boolean tileHover = hit(rect, mouseX, mouseY);
                int tileBg = active ? MENU_ACCENT_BG_ACTIVE : (tileHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
                int tileBorder = active ? MENU_ACCENT : (tileHover ? MENU_UTIL_BORDER_HOVER : MENU_UTIL_BORDER);
                g.fill(rect[0], rect[1], rect[0] + rect[2], rect[1] + rect[3], tileBg);
                g.renderOutline(rect[0], rect[1], rect[2], rect[3], tileBorder);
                int tileTextColor = active ? MENU_ACCENT : MENU_TEXT_PRIMARY;
                String label = themeIds[i].displayName;
                while (font.width(label) > cellW - 8 && label.length() > 4) {
                    label = label.substring(0, label.length() - 2);
                }
                drawScaledText(g, font, label, rect[0] + 4, rect[1] + 4, tileTextColor);
                themeRects.add(rect);
                themeRectIds.add(themeIds[i]);
            }
            int themeGridRows = (themeIds.length + gridCols - 1) / gridCols;
            cy += themeGridRows * (cellH + rowGap) + 6;
        } else {
            cy += 5;
        }

        // ── Accent override ──
        drawScaledText(g, font, "§l" + "Accent Override", popupX + 10, sy(cy), MENU_HEADER_TEXT);
        cy += 11;
        boolean enabled = accentOverrideEnabled();
        enableToggleRect = new int[]{popupX + 10, sy(cy), 14, 14};
        int toggleBg = enabled ? MENU_UTIL_ON_BG : MENU_UTIL_BG;
        int toggleBorder = enabled ? MENU_UTIL_ON_BORDER : MENU_UTIL_BORDER;
        g.fill(enableToggleRect[0], enableToggleRect[1], enableToggleRect[0] + 14, enableToggleRect[1] + 14, toggleBg);
        g.renderOutline(enableToggleRect[0], enableToggleRect[1], 14, 14, toggleBorder);
        if (enabled) {
            // A filled inner square rather than a unicode checkmark glyph - this mod's own
            // icon convention elsewhere (CustomConfigScreen's inline-help "?" icon) sticks to
            // plain ASCII, and Minecraft's actual default-font glyph coverage can't be
            // bytecode-verified in this environment, so an unverified unicode symbol risks
            // rendering as a missing-glyph box in-game.
            g.fill(enableToggleRect[0] + 3, enableToggleRect[1] + 3, enableToggleRect[0] + 11, enableToggleRect[1] + 11, MENU_UTIL_ON_TEXT);
        }
        drawScaledText(g, font, "Use a custom accent color instead of the theme's own",
                enableToggleRect[0] + 20, enableToggleRect[1] + 3, enabled ? MENU_TEXT_PRIMARY : MENU_TEXT_MUTED);
        cy += 18;

        swatchRects.clear();
        int swX = popupX + 10;
        int swY = sy(cy);
        for (String name : ACCENT_SWATCHES) {
            Integer col = ColorPresets.PRESETS.get(name);
            if (col == null) continue;
            int argb = 0xFF000000 | (col & 0xFFFFFF);
            boolean hover = mouseX >= swX && mouseX < swX + 16 && mouseY >= swY && mouseY < swY + 16;
            int size = hover ? 16 : 14;
            int offset = (16 - size) / 2;
            g.fill(swX + offset, swY + offset, swX + offset + size, swY + offset + size, argb);
            g.renderOutline(swX + offset, swY + offset, size, size, enabled ? MENU_PANEL_BORDER_LIGHT : MENU_PANEL_BORDER);
            swatchRects.add(new int[]{swX, swY, 16, 16});
            swX += 18;
        }
        cy += 22;

        // ── Custom accent color (color wheel) ──
        String customLabel = customWheelOpen ? "▲ Custom Color" : "▼ Custom Color...";
        int customW = font.width(customLabel) + 12;
        customWheelToggleRect = new int[]{popupX + 10, sy(cy), customW, 14};
        boolean customHover = hit(customWheelToggleRect, mouseX, mouseY);
        g.fill(customWheelToggleRect[0], customWheelToggleRect[1],
                customWheelToggleRect[0] + customWheelToggleRect[2], customWheelToggleRect[1] + 14,
                customHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
        g.renderOutline(customWheelToggleRect[0], customWheelToggleRect[1], customWheelToggleRect[2], 14,
                customHover ? MENU_UTIL_BORDER_HOVER : MENU_UTIL_BORDER);
        drawScaledText(g, font, customLabel, customWheelToggleRect[0] + 6, customWheelToggleRect[1] + 3, MENU_TEXT_PRIMARY);
        cy += 18;

        if (customWheelOpen) {
            wheelCenterX = popupX + popupW / 2;
            wheelCenterY = sy(cy) + WHEEL_RADIUS;
            drawColorWheel(g, wheelCenterX, wheelCenterY, WHEEL_RADIUS);

            float wheelAngle = customHue * 360f;
            float wheelDist = customSat * WHEEL_RADIUS;
            int indicatorX = wheelCenterX + (int) (Math.cos(Math.toRadians(wheelAngle - 90)) * wheelDist);
            int indicatorY = wheelCenterY + (int) (Math.sin(Math.toRadians(wheelAngle - 90)) * wheelDist);
            g.fill(indicatorX - 2, indicatorY - 2, indicatorX + 2, indicatorY + 2, 0xFFFFFFFF);
            g.renderOutline(indicatorX - 2, indicatorY - 2, 4, 4, 0xFF000000);
            cy += WHEEL_RADIUS * 2 + 6;

            briSliderX = popupX + 10;
            briSliderY = sy(cy);
            briSliderW = popupW - 20;
            briSliderH = 12;
            drawBrightnessSlider(g, briSliderX, briSliderY, briSliderW, briSliderH);
            cy += 16;

            int curColor = java.awt.Color.HSBtoRGB(customHue, customSat, customBright) & 0xFFFFFF;
            g.fill(popupX + 10, sy(cy), popupX + 26, sy(cy) + 12, 0xFF000000 | curColor);
            g.renderOutline(popupX + 10, sy(cy), 16, 12, MENU_PANEL_BORDER_LIGHT);
            drawScaledText(g, font, String.format("#%06X", curColor), popupX + 30, sy(cy) + 2, MENU_TEXT_MUTED);
            cy += 16;
        }

        // ── Tab click animation ──
        drawScaledText(g, font, "§l" + "Tab Click Animation", popupX + 10, sy(cy), MENU_HEADER_TEXT);
        cy += 11;
        String style = animationStyle();
        String styleLabel = "Style: " + (style.substring(0, 1).toUpperCase() + style.substring(1));
        int styleW = font.width(styleLabel) + 12;
        styleRect = new int[]{popupX + 10, sy(cy), styleW, 16};
        boolean styleHover = mouseX >= styleRect[0] && mouseX < styleRect[0] + styleRect[2]
                && mouseY >= styleRect[1] && mouseY < styleRect[1] + styleRect[3];
        g.fill(styleRect[0], styleRect[1], styleRect[0] + styleRect[2], styleRect[1] + styleRect[3],
                styleHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
        g.renderOutline(styleRect[0], styleRect[1], styleRect[2], styleRect[3],
                styleHover ? MENU_UTIL_BORDER_HOVER : MENU_UTIL_BORDER);
        drawScaledText(g, font, styleLabel, styleRect[0] + 6, styleRect[1] + 4, MENU_TEXT_PRIMARY);
        cy += 20;

        int speedPercent = animationSpeedPercent();
        boolean speedDisabled = "none".equals(style);
        String speedLabel = "Speed: " + speedPercent + "%";
        int speedY = sy(cy);
        speedMinusRect = new int[]{popupX + 10, speedY, 16, 16};
        speedPlusRect = new int[]{popupX + 10 + 16 + 4 + font.width(speedLabel) + 8, speedY, 16, 16};
        boolean minusHover = !speedDisabled && mouseX >= speedMinusRect[0] && mouseX < speedMinusRect[0] + 16
                && mouseY >= speedMinusRect[1] && mouseY < speedMinusRect[1] + 16;
        boolean plusHover = !speedDisabled && mouseX >= speedPlusRect[0] && mouseX < speedPlusRect[0] + 16
                && mouseY >= speedPlusRect[1] && mouseY < speedPlusRect[1] + 16;
        int stepBg = speedDisabled ? MENU_PANEL : MENU_UTIL_BG;
        g.fill(speedMinusRect[0], speedMinusRect[1], speedMinusRect[0] + 16, speedMinusRect[1] + 16,
                minusHover ? MENU_UTIL_BG_HOVER : stepBg);
        g.renderOutline(speedMinusRect[0], speedMinusRect[1], 16, 16, speedDisabled ? MENU_PANEL_BORDER : MENU_UTIL_BORDER);
        drawScaledCenteredText(g, font, "-", speedMinusRect[0] + 8, speedMinusRect[1] + 4, speedDisabled ? MENU_TEXT_FAINT : MENU_TEXT_PRIMARY);
        drawScaledText(g, font, speedDisabled ? "§8" + speedLabel : speedLabel, speedMinusRect[0] + 20, speedY + 4,
                speedDisabled ? MENU_TEXT_FAINT : MENU_TEXT_PRIMARY);
        g.fill(speedPlusRect[0], speedPlusRect[1], speedPlusRect[0] + 16, speedPlusRect[1] + 16,
                plusHover ? MENU_UTIL_BG_HOVER : stepBg);
        g.renderOutline(speedPlusRect[0], speedPlusRect[1], 16, 16, speedDisabled ? MENU_PANEL_BORDER : MENU_UTIL_BORDER);
        drawScaledCenteredText(g, font, "+", speedPlusRect[0] + 8, speedPlusRect[1] + 4, speedDisabled ? MENU_TEXT_FAINT : MENU_TEXT_PRIMARY);
        cy += 20;
        if (speedDisabled) {
            drawScaledText(g, font, "§8(Style is None - no entrance animation plays)", popupX + 10, sy(cy), MENU_TEXT_FAINT);
        }
        cy += 12;

        // ── Description/tooltip keyword-highlighting intensity ── controls how much
        // of CultivationTextStyler's automatic bold/coloring (Qi, spell, damage,
        // realm, ...) shows up across the WHOLE config screen's descriptions and
        // tooltips - not just this popup's own text. Direct answer to "I wanna be
        // able to change the styling of the text for the entire config, not just
        // the accent override."
        drawScaledText(g, font, "§l" + "Text Styling", popupX + 10, sy(cy), MENU_HEADER_TEXT);
        cy += 11;
        String intensity = descHighlightIntensity();
        String intensityLabel = "Description Highlighting: " + (intensity.substring(0, 1).toUpperCase() + intensity.substring(1));
        int intensityW = font.width(intensityLabel) + 12;
        intensityRect = new int[]{popupX + 10, sy(cy), intensityW, 16};
        boolean intensityHover = hit(intensityRect, mouseX, mouseY);
        g.fill(intensityRect[0], intensityRect[1], intensityRect[0] + intensityRect[2], intensityRect[1] + intensityRect[3],
                intensityHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
        g.renderOutline(intensityRect[0], intensityRect[1], intensityRect[2], intensityRect[3],
                intensityHover ? MENU_UTIL_BORDER_HOVER : MENU_UTIL_BORDER);
        drawScaledText(g, font, intensityLabel, intensityRect[0] + 6, intensityRect[1] + 4, MENU_TEXT_PRIMARY);
        cy += 20;
        drawScaledText(g, font, "§8Full = every keyword styled, Reduced = one per line, Off = plain text",
                popupX + 10, sy(cy), MENU_TEXT_FAINT);
        cy += 12;

        // ── Text size (this popup's own text - see drawScaledText/drawScaledCenteredText
        // above for why box geometry stays unscaled while glyphs scale in place). This
        // row's own -/+/label text is deliberately drawn at NORMAL (unscaled) size, not
        // via drawScaledText, so the control used to change the setting stays a stable,
        // always-legible anchor even when the chosen scale is pushed to an extreme. ──
        int sizePercent = textSizePercent();
        String sizeLabel = "Text Size: " + sizePercent + "%";
        int sizeY = sy(cy);
        sizeMinusRect = new int[]{popupX + 10, sizeY, 16, 16};
        sizePlusRect = new int[]{popupX + 10 + 16 + 4 + font.width(sizeLabel) + 8, sizeY, 16, 16};
        boolean sizeMinusHover = mouseX >= sizeMinusRect[0] && mouseX < sizeMinusRect[0] + 16
                && mouseY >= sizeMinusRect[1] && mouseY < sizeMinusRect[1] + 16;
        boolean sizePlusHover = mouseX >= sizePlusRect[0] && mouseX < sizePlusRect[0] + 16
                && mouseY >= sizePlusRect[1] && mouseY < sizePlusRect[1] + 16;
        g.fill(sizeMinusRect[0], sizeMinusRect[1], sizeMinusRect[0] + 16, sizeMinusRect[1] + 16,
                sizeMinusHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
        g.renderOutline(sizeMinusRect[0], sizeMinusRect[1], 16, 16, MENU_UTIL_BORDER);
        g.drawString(font, "-", sizeMinusRect[0] + 8 - font.width("-") / 2, sizeMinusRect[1] + 4, MENU_TEXT_PRIMARY, false);
        g.drawString(font, sizeLabel, sizeMinusRect[0] + 20, sizeY + 4, MENU_TEXT_PRIMARY, false);
        g.fill(sizePlusRect[0], sizePlusRect[1], sizePlusRect[0] + 16, sizePlusRect[1] + 16,
                sizePlusHover ? MENU_UTIL_BG_HOVER : MENU_UTIL_BG);
        g.renderOutline(sizePlusRect[0], sizePlusRect[1], 16, 16, MENU_UTIL_BORDER);
        g.drawString(font, "+", sizePlusRect[0] + 8 - font.width("+") / 2, sizePlusRect[1] + 4, MENU_TEXT_PRIMARY, false);
        cy += 20;

        // UI Sounds ("Sound Cycler") section removed from the visible popup for
        // now - reported as "not working" and set aside for a later pass
        // (targeted ~1.0.9). The underlying config fields (ExtendedConfig's
        // CLIENT_UI_SOUND_*), UiSoundHelper, UiSounds' registered sound events,
        // SoundPresets, sounds.json, and the .ogg assets themselves are all left
        // completely untouched on disk so this section can be re-added here
        // later without redoing any of that groundwork - only this popup's
        // render()/mouseClicked() surface and CustomConfigScreen's trigger
        // wiring were removed. cy is intentionally left where it was right
        // after Text Size, so nothing below has to shift.

        g.disableScissor();

        // Record this frame's real content height and viewport height for next
        // frame's scroll clamp (see lastContentHeight's field comment), then
        // draw a scrollbar track+thumb along the content area's right edge if
        // there's actually anything to scroll - purely a visual indicator
        // (mouse-wheel drives the actual scrolling; see mouseScrolled()),
        // matching the pinned-tooltip windows' own non-interactive scrollbar.
        lastContentHeight = cy;
        lastViewportHeight = contentH;
        if (lastContentHeight > contentH) {
            int barX = popupX + popupW - 6;
            g.fill(barX, contentTop, barX + 3, contentBottom, MENU_SCROLL_TRACK);
            int thumbH = Math.max(10, contentH * contentH / lastContentHeight);
            int thumbTravel = contentH - thumbH;
            int thumbY = contentTop + (maxScroll <= 0 ? 0 : thumbTravel * scrollOffset / maxScroll);
            g.fill(barX, thumbY, barX + 3, thumbY + thumbH, MENU_SCROLL_THUMB);
        }

        drawScaledText(g, font, "§8[Esc/scroll wheel; click outside to close]", popupX + 4, popupY + popupH - 12, MENU_TEXT_FAINT);

        pose.popPose();
    }

    /** Mouse-wheel scroll while this popup is open. Called from CustomConfigScreen.mouseScrolled(). */
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!open) return false;

        if (mouseX < popupX || mouseX >= popupX + popupW || mouseY < popupY || mouseY >= popupY + popupH) {
            return false; // let the screen behind the popup handle wheel events outside it
        }
        int maxScroll = Math.max(0, lastContentHeight - lastViewportHeight);
        int step = 20;
        if (delta > 0) {
            scrollOffset = Math.max(0, scrollOffset - step);
        } else if (delta < 0) {
            scrollOffset = Math.min(maxScroll, scrollOffset + step);
        }
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int screenWidth, int screenHeight) {
        if (!open || button != 0) return false;

        if (mouseX < popupX || mouseX >= popupX + popupW || mouseY < popupY || mouseY >= popupY + popupH) {
            close();
            return true;
        }

        // A click in the fixed title or footer band can never legitimately hit
        // a control - all real controls are laid out between contentTop and
        // contentBottom via sy(). Without this guard, a control that scrolled
        // out of view could still leave a stale rect overlapping the title or
        // footer strip and eat a click meant for nothing (swallow-not-crash,
        // but still wrong). Consume the click so it doesn't fall through to
        // whatever's behind the popup, same as the rest of this method.
        if (mouseY < contentTop || mouseY >= contentBottom) {
            return true;
        }

        if (hit(themeSectionHeaderRect, mouseX, mouseY)) {
            themeSectionCollapsed = !themeSectionCollapsed;
            return true;
        }

        for (int i = 0; i < themeRects.size() && i < themeRectIds.size(); i++) {
            if (hit(themeRects.get(i), mouseX, mouseY)) {
                Theme.applyTheme(themeRectIds.get(i));
                return true;
            }
        }

        if (hit(enableToggleRect, mouseX, mouseY)) {
            try {
                boolean cur = ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.get();
                ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.set(!cur);
                Theme.applyTheme(Theme.current); // re-derive the whole palette, then re-apply/clear the override
            } catch (Exception e) { /* ignore */ }
            return true;
        }

        for (int i = 0; i < swatchRects.size() && i < ACCENT_SWATCHES.length; i++) {
            if (hit(swatchRects.get(i), mouseX, mouseY)) {
                Integer col = ColorPresets.PRESETS.get(ACCENT_SWATCHES[i]);
                if (col != null) {
                    try {
                        ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_COLOR.set(0xFF000000 | (col & 0xFFFFFF));
                        ExtendedConfig.CLIENT_UI_ACCENT_OVERRIDE_ENABLED.set(true);
                        Theme.applyTheme(Theme.current);
                    } catch (Exception e) { /* ignore */ }
                }
                return true;
            }
        }

        if (hit(customWheelToggleRect, mouseX, mouseY)) {
            customWheelOpen = !customWheelOpen;
            return true;
        }
        if (customWheelOpen) {
            double wdx = mouseX - wheelCenterX;
            double wdy = mouseY - wheelCenterY;
            double wdist = Math.sqrt(wdx * wdx + wdy * wdy);
            if (wdist <= WHEEL_RADIUS) {
                draggingWheel = true;
                updateFromWheel(mouseX, mouseY);
                return true;
            }
            if (mouseX >= briSliderX && mouseX < briSliderX + briSliderW
                    && mouseY >= briSliderY && mouseY < briSliderY + briSliderH) {
                draggingSlider = true;
                updateFromSlider(mouseX);
                return true;
            }
        }

        if (hit(styleRect, mouseX, mouseY)) {
            try {
                String cur = animationStyle();
                String next = "slide".equals(cur) ? "fade" : ("fade".equals(cur) ? "none" : "slide");
                ExtendedConfig.CLIENT_UI_TAB_ANIMATION_STYLE.set(next);
            } catch (Exception e) { /* ignore */ }
            return true;
        }

        if (hit(intensityRect, mouseX, mouseY)) {
            try {
                String cur = descHighlightIntensity();
                String next = "full".equals(cur) ? "reduced" : ("reduced".equals(cur) ? "off" : "full");
                ExtendedConfig.CLIENT_DESC_HIGHLIGHT_INTENSITY.set(next);
            } catch (Exception e) { /* ignore */ }
            return true;
        }

        if (!"none".equals(animationStyle())) {
            if (hit(speedMinusRect, mouseX, mouseY)) {
                adjustSpeed(-25);
                return true;
            }
            if (hit(speedPlusRect, mouseX, mouseY)) {
                adjustSpeed(25);
                return true;
            }
        }

        if (hit(sizeMinusRect, mouseX, mouseY)) {
            adjustTextSize(-10);
            return true;
        }
        if (hit(sizePlusRect, mouseX, mouseY)) {
            adjustTextSize(10);
            return true;
        }

        // UI Sounds click-handling removed along with the render block above -
        // see that comment for why. soundEnableToggleRect/soundVolMinusRect/
        // soundVolPlusRect/soundPrevRects/soundPlayRects/soundNextRects are no
        // longer populated by render() (all-zero/empty), so there is nothing
        // left for a stray click to hit here even without an explicit guard.

        return true; // swallow clicks inside the popup that missed every control
    }

    private void adjustSpeed(int delta) {
        try {
            int cur = animationSpeedPercent();
            int next = Math.max(10, Math.min(400, cur + delta));
            ExtendedConfig.CLIENT_UI_TAB_ANIMATION_SPEED_PERCENT.set(next);
        } catch (Exception e) { /* ignore */ }
    }

    private void adjustTextSize(int delta) {
        try {
            int cur = textSizePercent();
            // Clamped to CLIENT_FONT_SIZE_PERCENT's own defineInRange bounds (50-200).
            int next = Math.max(50, Math.min(200, cur + delta));
            ExtendedConfig.CLIENT_FONT_SIZE_PERCENT.set(next);
        } catch (Exception e) { /* ignore */ }
    }

    // adjustSoundVolume() removed along with its only caller (the UI Sounds
    // click-handling block) - ExtendedConfig.CLIENT_UI_SOUND_VOLUME_PERCENT
    // itself is untouched and still readable via soundVolumePercent() above
    // for whenever the sound UI comes back.

    private static boolean hit(int[] r, double mouseX, double mouseY) {
        return mouseX >= r[0] && mouseX < r[0] + r[2] && mouseY >= r[1] && mouseY < r[1] + r[3];
    }
}
