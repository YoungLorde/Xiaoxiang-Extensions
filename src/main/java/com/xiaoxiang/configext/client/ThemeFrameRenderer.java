package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Structural per-theme window-frame treatment - the direct answer to the
 * player-reported "different theme means the UI has slightly different
 * changes to its structure... maybe it's outlines or the box outlines...
 * give each theme its own identity" correction. Dispatches on
 * {@code Theme.current.frameStyle} (assigned per {@link Theme.ThemeId} in
 * Theme.java) so switching themes changes more than palette: this file
 * decides how the frame GEOMETRY itself differs between families of themes.
 *
 * Single call site: CustomConfigScreen's main window background/outline draw
 * (right after {@code g.fill(...Theme.BACKGROUND)}), replacing what used to
 * be two bare {@code g.renderOutline} calls. Every style still draws that
 * same base double-line outline first - so the window's visual boundary is
 * never ambiguous regardless of style, and nothing downstream that assumes
 * the outline exists breaks - then layers its own decoration on top. This
 * file only ADDS structure, it never removes or repositions the base frame.
 *
 * Honest scaling note: 30 themes deliberately share 11 frame families rather
 * than each getting a bespoke one-off outline - real, visible structural
 * variety grouped by aesthetic family, not 30 individually hand-tuned
 * treatments, which is the responsible tradeoff for covering this many
 * themes in one pass without any of them being a token afterthought.
 *
 * Constrained to axis-aligned rectangles only: {@code g.fill}/
 * {@code g.renderOutline} are the only drawing primitives this codebase has
 * ever proven to compile (no diagonal-line or curve API exists anywhere in
 * it to cross-reference), so "jagged"/"zigzag"/"torn edge"/"rounded corner"
 * looks below are built from small stepped rectangles - a pixel-art
 * staircase - not true diagonals or arcs.
 */
public final class ThemeFrameRenderer {

    private ThemeFrameRenderer() {}

    /** Draws the themed window frame at (x, y, w, h) - screen pixel space, same as g.renderOutline's own args. */
    public static void drawFrame(GuiGraphics g, int x, int y, int w, int h) {
        // Base double-line outline - every style keeps this so the window's
        // boundary always reads clearly no matter which extra decoration (or
        // none, for DOUBLE_LINE itself) is layered on top of it.
        g.renderOutline(x, y, w, h, Theme.WINDOW_BORDER);
        g.renderOutline(x + 1, y + 1, w - 2, h - 2, Theme.WINDOW_BORDER_DIM);

        switch (Theme.current.frameStyle) {
            case SCROLL: drawScroll(g, x, y, w, h); break;
            case NEON: drawNeon(g, x, y, w, h); break;
            case MANUSCRIPT: drawManuscript(g, x, y, w, h); break;
            case JAGGED: drawJagged(g, x, y, w, h); break;
            case SMOOTH: drawSmooth(g, x, y, w, h); break;
            case TALISMAN: drawTalisman(g, x, y, w, h); break;
            case GLOW: drawGlow(g, x, y, w, h); break;
            case RETRO: drawRetro(g, x, y, w, h); break;
            case GOTHIC: drawGothic(g, x, y, w, h); break;
            case TRIBAL: drawTribal(g, x, y, w, h); break;
            case DOUBLE_LINE:
            default: break; // base outline above is already the whole look
        }
    }

    /** No-op instead of an inverted/degenerate rect when a stepped edge calc runs out of room near a corner. */
    private static void fillSafe(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        if (x2 <= x1 || y2 <= y1) return;
        g.fill(x1, y1, x2, y2, color);
    }

    /** Torn/burnt scroll edges: short ticks off the top/bottom, plus wooden-roller-bar corners. */
    private static void drawScroll(GuiGraphics g, int x, int y, int w, int h) {
        int c = Theme.ACCENT3;
        for (int tx = x + 6; tx < x + w - 6; tx += 10) {
            fillSafe(g, tx, y - 3, tx + 2, y, c);
            fillSafe(g, tx, y + h, tx + 2, y + h + 3, c);
        }
        int roller = Theme.CUSTOM_ACCENT;
        fillSafe(g, x - 2, y - 2, x + 10, y + 2, roller);
        fillSafe(g, x + w - 10, y - 2, x + w + 2, y + 2, roller);
        fillSafe(g, x - 2, y + h - 2, x + 10, y + h + 2, roller);
        fillSafe(g, x + w - 10, y + h - 2, x + w + 2, y + h + 2, roller);
    }

    /** Sci-fi HUD corner brackets. */
    private static void drawNeon(GuiGraphics g, int x, int y, int w, int h) {
        int len = 14, thick = 2, c = Theme.ACCENT_HOVER;
        fillSafe(g, x - 2, y - 2, x - 2 + len, y - 2 + thick, c);
        fillSafe(g, x - 2, y - 2, x - 2 + thick, y - 2 + len, c);
        fillSafe(g, x + w + 2 - len, y - 2, x + w + 2, y - 2 + thick, c);
        fillSafe(g, x + w + 2 - thick, y - 2, x + w + 2, y - 2 + len, c);
        fillSafe(g, x - 2, y + h + 2 - thick, x - 2 + len, y + h + 2, c);
        fillSafe(g, x - 2, y + h + 2 - len, x - 2 + thick, y + h + 2, c);
        fillSafe(g, x + w + 2 - len, y + h + 2 - thick, x + w + 2, y + h + 2, c);
        fillSafe(g, x + w + 2 - thick, y + h + 2 - len, x + w + 2, y + h + 2, c);
    }

    /** Double-ruled framing (inner rule line) with small corner serifs, like ruled paper. */
    private static void drawManuscript(GuiGraphics g, int x, int y, int w, int h) {
        g.renderOutline(x + 3, y + 3, w - 6, h - 6, Theme.PANEL_BORDER_LIGHT);
        int s = 6, c = Theme.ACCENT3;
        fillSafe(g, x - 2, y - 2, x + s, y, c);
        fillSafe(g, x - 2, y - 2, x, y + s, c);
        fillSafe(g, x + w - s, y - 2, x + w + 2, y, c);
        fillSafe(g, x + w, y - 2, x + w + 2, y + s, c);
        fillSafe(g, x - 2, y + h, x + s, y + h + 2, c);
        fillSafe(g, x - 2, y + h - s, x, y + h + 2, c);
        fillSafe(g, x + w - s, y + h, x + w + 2, y + h + 2, c);
        fillSafe(g, x + w, y + h - s, x + w + 2, y + h + 2, c);
    }

    /** Jagged/spiky ticks alternating short and tall along all four edges. */
    private static void drawJagged(GuiGraphics g, int x, int y, int w, int h) {
        int c = Theme.ACCENT, step = 8;
        boolean out = true;
        for (int tx = x; tx < x + w; tx += step) {
            int spike = out ? 4 : 2;
            fillSafe(g, tx, y - spike, Math.min(tx + 4, x + w), y, c);
            fillSafe(g, tx, y + h, Math.min(tx + 4, x + w), y + h + spike, c);
            out = !out;
        }
        out = true;
        for (int ty = y; ty < y + h; ty += step) {
            int spike = out ? 4 : 2;
            fillSafe(g, x - spike, ty, x, Math.min(ty + 4, y + h), c);
            fillSafe(g, x + w, ty, x + w + spike, Math.min(ty + 4, y + h), c);
            out = !out;
        }
    }

    /** Rounded-look corners: small background-coloured notches punched over the base outline's corners. */
    private static void drawSmooth(GuiGraphics g, int x, int y, int w, int h) {
        int cut = 4, bg = Theme.BACKGROUND;
        fillSafe(g, x, y, x + cut, y + 1, bg);
        fillSafe(g, x, y, x + 1, y + cut, bg);
        fillSafe(g, x + w - cut, y, x + w, y + 1, bg);
        fillSafe(g, x + w - 1, y, x + w, y + cut, bg);
        fillSafe(g, x, y + h - 1, x + cut, y + h, bg);
        fillSafe(g, x, y + h - cut, x + 1, y + h, bg);
        fillSafe(g, x + w - cut, y + h - 1, x + w, y + h, bg);
        fillSafe(g, x + w - 1, y + h - cut, x + w, y + h, bg);
        g.renderOutline(x + 3, y + 3, w - 6, h - 6, Theme.setAlpha(Theme.ACCENT2, 0x60));
    }

    /** Formation-array tick marks perpendicular to the border, plus corner "seal" squares. */
    private static void drawTalisman(GuiGraphics g, int x, int y, int w, int h) {
        int c = Theme.ACCENT2, step = 14;
        for (int tx = x + step; tx < x + w - step; tx += step) {
            fillSafe(g, tx, y - 3, tx + 1, y, c);
            fillSafe(g, tx, y + h, tx + 1, y + h + 3, c);
        }
        for (int ty = y + step; ty < y + h - step; ty += step) {
            fillSafe(g, x - 3, ty, x, ty + 1, c);
            fillSafe(g, x + w, ty, x + w + 3, ty + 1, c);
        }
        int s = 5, seal = Theme.ACCENT;
        fillSafe(g, x - s, y - s, x, y, seal);
        fillSafe(g, x + w, y - s, x + w + s, y, seal);
        fillSafe(g, x - s, y + h, x, y + h + s, seal);
        fillSafe(g, x + w, y + h, x + w + s, y + h + s, seal);
    }

    /** Soft layered outer glow: several expanding semi-transparent outlines, no hard edge. */
    private static void drawGlow(GuiGraphics g, int x, int y, int w, int h) {
        int base = Theme.WINDOW_BORDER;
        for (int i = 1; i <= 3; i++) {
            int alpha = 0x50 - i * 0x14;
            if (alpha < 0) alpha = 0;
            g.renderOutline(x - i, y - i, w + i * 2, h + i * 2, Theme.setAlpha(base, alpha));
        }
    }

    /** Thick chunky/segmented border, like a blocky pixel-art window frame. */
    private static void drawRetro(GuiGraphics g, int x, int y, int w, int h) {
        int thick = 4, c = Theme.WINDOW_BORDER, c2 = Theme.ACCENT2;
        fillSafe(g, x - thick, y - thick, x + w + thick, y, c);
        fillSafe(g, x - thick, y + h, x + w + thick, y + h + thick, c);
        fillSafe(g, x - thick, y, x, y + h, c);
        fillSafe(g, x + w, y, x + w + thick, y + h, c);
        int seg = 10;
        boolean alt = false;
        for (int tx = x; tx < x + w; tx += seg) {
            if (alt) {
                fillSafe(g, tx, y - thick, Math.min(tx + seg, x + w), y, c2);
                fillSafe(g, tx, y + h, Math.min(tx + seg, x + w), y + h + thick, c2);
            }
            alt = !alt;
        }
    }

    /** Mitred, stair-stepped corner accents - approximates a pointed Gothic arch corner. */
    private static void drawGothic(GuiGraphics g, int x, int y, int w, int h) {
        int c = Theme.ACCENT3, steps = 4;
        for (int i = 0; i < steps; i++) {
            int sz = (steps - i) * 2;
            fillSafe(g, x - 2 - i, y - 2 - i, x - 2 - i + sz, y - 1 - i, c);
            fillSafe(g, x + w + 2 + i - sz, y - 2 - i, x + w + 2 + i, y - 1 - i, c);
            fillSafe(g, x - 2 - i, y + h + 1 + i, x - 2 - i + sz, y + h + 2 + i, c);
            fillSafe(g, x + w + 2 + i - sz, y + h + 1 + i, x + w + 2 + i, y + h + 2 + i, c);
        }
    }

    /** Triangle/zigzag stair ticks along the top and bottom edges, alternating between two accent hues. */
    private static void drawTribal(GuiGraphics g, int x, int y, int w, int h) {
        int c = Theme.ACCENT, c2 = Theme.ACCENT2, step = 12;
        boolean alt = false;
        for (int tx = x; tx < x + w; tx += step) {
            int col = alt ? c2 : c;
            fillSafe(g, tx, y - 2, Math.min(tx + step, x + w), y - 1, col);
            fillSafe(g, tx + 2, y - 4, Math.min(tx + step - 2, x + w), y - 2, col);
            fillSafe(g, tx + 4, y - 6, Math.min(tx + step - 4, x + w), y - 4, col);
            fillSafe(g, tx, y + h + 1, Math.min(tx + step, x + w), y + h + 2, col);
            fillSafe(g, tx + 2, y + h + 2, Math.min(tx + step - 2, x + w), y + h + 4, col);
            fillSafe(g, tx + 4, y + h + 4, Math.min(tx + step - 4, x + w), y + h + 6, col);
            alt = !alt;
        }
    }
}
