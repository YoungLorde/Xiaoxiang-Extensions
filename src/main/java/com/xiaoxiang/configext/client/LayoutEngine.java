package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The single source of truth for every vertical measurement in the config screen.
 *
 * WHY THIS EXISTS
 * ---------------
 * CustomConfigScreen used to compute the same visual geometry in several
 * independent places:
 *
 *   * {@code getEntryHeight()} returned a fixed 22/24 px for every row, but group
 *     headers were drawn into the *previous* row's slot ({@code y - getEntryHeight() + 2}),
 *     so a header always overlapped whatever was above it - and a header whose text
 *     needed more than the assumed 14 px overlapped the row below it too.
 *   * {@code getSubTabRowCount()} approximated the sub-tab wrap count with
 *     {@code (maxRight - 8) / (SUBTAB_WIDTH + 3)} while {@code rebuildSubTabButtons()}
 *     ran the real wrap loop; when they disagreed the entry list was positioned a
 *     whole row off, and in the worst case the scissor rect inverted and the tab
 *     rendered blank.
 *   * The group-button row was laid out three times (render, click, tooltip) with
 *     three slightly different wrap loops (one of them even used {@code gx = 24}
 *     instead of {@code gx = 8} on wrap).
 *   * render(), mouseClicked() (twice), and mouseScrolled() each re-derived
 *     {@code y = entryYStart + (i - startIdx) * getEntryHeight()} independently.
 *
 * Any drift between those formulas produced exactly the two reported bug classes:
 * overlapping sub-headers, and rows that render but can't be clicked (or don't
 * render at all).
 *
 * WHAT THIS DOES
 * --------------
 * {@link #computeEntryLayout} walks the entry list ONCE and produces an ordered
 * {@link Layout} of {@link Row}s. Each row already knows its {@code y}, its
 * {@code height} (measured with the real {@link Font} and the real width budget,
 * never an assumed constant) and therefore its {@code bottom}. Rendering, click
 * hit-testing and scroll-wheel hover all read that same list; none of them is
 * allowed to recompute a Y or a height again.
 *
 * Scrolling works on the row list by cumulative Y, not by a fixed row count, so
 * variable-height rows (wrapped headers, two-line labels) scroll correctly.
 */
public final class LayoutEngine {

    private LayoutEngine() {}

    // ── Row metrics ─────────────────────────────────────────────────────
    /** Vertical advance of one text line inside a row. */
    public static final int LINE_H = 10;
    /** Padding above the first text line of an entry row. */
    public static final int ROW_PAD_TOP = 2;
    /** Padding below the last text line of an entry row. */
    public static final int ROW_PAD_BOTTOM = 1;
    /** Hard cap on how many lines an entry's display name may wrap to. */
    public static final int MAX_NAME_LINES = 2;
    /** Height of the coloured bar part of a group header. */
    public static final int HEADER_BAR_H = 15;
    /** Blank space reserved above each group header so it never touches the row above. */
    public static final int HEADER_GAP_TOP = 4;
    /** Blank space reserved below each group header. */
    public static final int HEADER_GAP_BOTTOM = 2;
    /** Hard cap on how many lines a group header's description may wrap to. */
    public static final int MAX_HEADER_DESC_LINES = 3;
    /** Horizontal space on the right of a row reserved for the value controls. */
    public static final int CONTROLS_RESERVE = 210;
    /** Never let a text budget collapse to nothing on very narrow screens. */
    public static final int MIN_TEXT_BUDGET = 60;
    /** X offset of the display name inside a row (after the type icon). */
    public static final int NAME_INDENT = 14;
    /** X offset of the description inside a row. */
    public static final int DESC_INDENT = 4;

    // ════════════════════════════════════════════════════════════════════
    //  Row
    // ════════════════════════════════════════════════════════════════════

    /** One laid-out visual row: either a group header or a config entry. */
    public static final class Row {
        /** True when this row is a group header rather than a config entry. */
        public final boolean header;
        /** Group name this row belongs to ("" when the entry has no group). */
        public final String group;
        /** The config entry, or null for a header row. */
        public final CustomConfigScreen.DisplayEntry entry;
        /** Index into the source entry list, or -1 for a header row. */
        public final int entryIndex;
        /** Absolute Y of the top of this row in unscrolled layout space. */
        public final int y;
        /** Full height of this row, including its own padding. */
        public final int height;
        /** How many lines the display name wraps to (entry rows only). */
        public final int nameLines;
        /** How many lines the description occupies (0 = none). */
        public final int descLines;
        /** Number of entries in this group (header rows only). */
        public final int groupCount;
        /** Whether this group is currently collapsed (header rows only). */
        public final boolean collapsed;
        /**
         * Measured height of the coloured header bar (header rows only). The
         * renderer MUST use this rather than HEADER_BAR_H directly - a long group
         * name makes the title wrap, which makes the bar taller, and an assumed
         * constant here would put the bar and the blurb back out of sync.
         */
        public final int barHeight;

        Row(boolean header, String group, CustomConfigScreen.DisplayEntry entry, int entryIndex,
            int y, int height, int nameLines, int descLines, int groupCount, boolean collapsed,
            int barHeight) {
            this.header = header;
            this.group = group;
            this.entry = entry;
            this.entryIndex = entryIndex;
            this.y = y;
            this.height = height;
            this.nameLines = nameLines;
            this.descLines = descLines;
            this.groupCount = groupCount;
            this.collapsed = collapsed;
            this.barHeight = barHeight;
        }

        /** Y just past the bottom edge of this row. */
        public int bottom() {
            return y + height;
        }

        /** Y of the coloured header bar (header rows only): below the top gap. */
        public int barTop() {
            return y + HEADER_GAP_TOP;
        }

        /** Y just past the coloured header bar (header rows only). */
        public int barBottom() {
            return y + HEADER_GAP_TOP + barHeight;
        }

        /**
         * Clickable/hoverable box of an entry row, in unscrolled layout space.
         * Header rows use {@link #barTop()}..{@link #barBottom()} instead.
         */
        public int hitTop() {
            return header ? barTop() : y;
        }

        public int hitBottom() {
            return header ? barBottom() : y + height;
        }

        public boolean containsY(int py) {
            return py >= hitTop() && py < hitBottom();
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Layout
    // ════════════════════════════════════════════════════════════════════

    /** The complete, ordered row list for one entry set, plus scroll helpers. */
    public static final class Layout {
        private final List<Row> rows;
        /** Y the first row starts at (== the screen's entry area top). */
        public final int startY;
        /** Total pixel height of every row in the list. */
        public final int totalHeight;
        /** Pixel budget used to wrap name text; exposed so the renderer matches it. */
        public final int nameBudget;
        /** Pixel budget used to wrap/truncate description text. */
        public final int descBudget;
        /** Left edge rows were laid out against. */
        public final int x;
        /** Right edge (exclusive) rows were laid out against. */
        public final int right;

        Layout(List<Row> rows, int startY, int totalHeight, int nameBudget, int descBudget, int x, int right) {
            this.rows = rows;
            this.startY = startY;
            this.totalHeight = totalHeight;
            this.nameBudget = nameBudget;
            this.descBudget = descBudget;
            this.x = x;
            this.right = right;
        }

        public int size() {
            return rows.size();
        }

        public boolean isEmpty() {
            return rows.isEmpty();
        }

        public Row get(int index) {
            return rows.get(index);
        }

        public List<Row> rows() {
            return rows;
        }

        /** Clamp an arbitrary row index into range (returns 0 for an empty layout). */
        public int clampRow(int index) {
            if (rows.isEmpty()) return 0;
            if (index < 0) return 0;
            if (index >= rows.size()) return rows.size() - 1;
            return index;
        }

        /**
         * How many pixels the whole list is shifted up when {@code firstRow} is the
         * top visible row. This is the ONLY place scroll is turned into pixels.
         */
        public int pixelOffset(int firstRow) {
            if (rows.isEmpty()) return 0;
            return rows.get(clampRow(firstRow)).y - startY;
        }

        /**
         * Highest row index that may be the top visible row without leaving blank
         * space at the bottom. Computed from cumulative Y so variable-height rows
         * scroll correctly (the old code used a fixed entries-per-page count).
         */
        public int maxFirstRow(int viewportHeight) {
            if (rows.isEmpty()) return 0;
            if (viewportHeight <= 0) return rows.size() - 1;
            int contentBottom = rows.get(rows.size() - 1).bottom();
            int limit = contentBottom - viewportHeight;
            if (limit <= startY) return 0;
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).y >= limit) return i;
            }
            return rows.size() - 1;
        }

        /** Last row index that has any pixel inside the viewport. */
        public int lastVisibleRow(int firstRow, int viewportHeight) {
            if (rows.isEmpty()) return -1;
            int first = clampRow(firstRow);
            int offset = pixelOffset(first);
            int bottomLimit = startY + viewportHeight + offset;
            int last = first;
            for (int i = first; i < rows.size(); i++) {
                if (rows.get(i).y >= bottomLimit) break;
                last = i;
            }
            return last;
        }

        /**
         * THE shared hit-test. Returns the index of the row whose hit box contains
         * {@code mouseY} given the current scroll, or -1. Every click / scroll /
         * hover path calls this instead of re-deriving a Y.
         */
        public int rowIndexAt(int firstRow, double mouseY, int viewportTop, int viewportBottom) {
            if (rows.isEmpty()) return -1;
            if (mouseY < viewportTop || mouseY >= viewportBottom) return -1;
            int first = clampRow(firstRow);
            int offset = pixelOffset(first);
            int layoutY = (int) Math.floor(mouseY) + offset;
            for (int i = first; i < rows.size(); i++) {
                Row r = rows.get(i);
                if (r.y > layoutY) break;
                if (r.containsY(layoutY)) return i;
            }
            return -1;
        }

        /** Screen-space Y for a row at the current scroll position. */
        public int screenY(int rowIndex, int firstRow) {
            return rows.get(clampRow(rowIndex)).y - pixelOffset(firstRow);
        }

        /** First row index that renders the given source entry, or -1. */
        public int rowForEntry(int entryIndex) {
            for (int i = 0; i < rows.size(); i++) {
                if (!rows.get(i).header && rows.get(i).entryIndex == entryIndex) return i;
            }
            return -1;
        }

        /** Source entry index of the top visible row (for the minimap/progress readout). */
        public int firstVisibleEntryIndex(int firstRow) {
            if (rows.isEmpty()) return 0;
            for (int i = clampRow(firstRow); i < rows.size(); i++) {
                if (!rows.get(i).header) return rows.get(i).entryIndex;
            }
            return 0;
        }

        /** How many rows are entry rows (headers excluded). */
        public int entryRowCount() {
            int n = 0;
            for (Row r : rows) if (!r.header) n++;
            return n;
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  The one-pass layout computation
    // ════════════════════════════════════════════════════════════════════

    /**
     * Walk the entry list once and produce the definitive row list.
     *
     * @param entries          the filtered entries, already in display order
     * @param startY           Y the first row starts at
     * @param x                left edge of the row area
     * @param right            right edge (exclusive) of the row area
     * @param font             the real font - used to MEASURE text, not to guess
     * @param baseRowHeight    minimum entry-row height (compact 22 / normal 24)
     * @param showGroupHeaders false in search mode, where grouping is meaningless
     * @param collapsedTest    given a group name, is that group collapsed?
     * @param groupDescriber   given a group name, its description (may be null)
     */
    public static Layout computeEntryLayout(List<CustomConfigScreen.DisplayEntry> entries,
                                            int startY,
                                            int x,
                                            int right,
                                            Font font,
                                            int baseRowHeight,
                                            boolean showGroupHeaders,
                                            Predicate<String> collapsedTest,
                                            Function<String, String> groupDescriber) {
        List<Row> rows = new ArrayList<>();
        int rowWidth = right - x;

        int nameBudget = rowWidth - NAME_INDENT - CONTROLS_RESERVE;
        if (nameBudget < MIN_TEXT_BUDGET) nameBudget = MIN_TEXT_BUDGET;
        int descBudget = rowWidth - DESC_INDENT - CONTROLS_RESERVE;
        if (descBudget < MIN_TEXT_BUDGET) descBudget = MIN_TEXT_BUDGET;
        int headerBudget = rowWidth - 12;
        if (headerBudget < MIN_TEXT_BUDGET) headerBudget = MIN_TEXT_BUDGET;

        if (entries == null || entries.isEmpty() || font == null) {
            return new Layout(rows, startY, 0, nameBudget, descBudget, x, right);
        }

        int y = startY;
        String currentGroup = null;
        boolean currentCollapsed = false;

        for (int i = 0; i < entries.size(); i++) {
            CustomConfigScreen.DisplayEntry entry = entries.get(i);
            String group = entry.group != null ? entry.group : "";

            // ── Group header, emitted whenever the group actually changes ──
            if (showGroupHeaders && !group.isEmpty() && !group.equals(currentGroup)) {
                currentGroup = group;
                currentCollapsed = collapsedTest != null && collapsedTest.test(group);

                int groupCount = 0;
                for (int j = i; j < entries.size(); j++) {
                    String g = entries.get(j).group != null ? entries.get(j).group : "";
                    if (g.equals(group)) groupCount++;
                    else break;
                }

                // Measure the header title against the REAL budget. The old code
                // hard-coded a 14px header, which is what made long group names
                // collide with the row underneath them.
                String title = "\u25BC " + group + "  (" + groupCount + ")";
                int titleLines = measureLines(font, title, headerBudget, 2);

                int descLines = 0;
                if (!currentCollapsed && groupDescriber != null) {
                    String gd = groupDescriber.apply(group);
                    if (gd != null && !gd.isBlank()) {
                        descLines = measureLines(font, firstParagraph(gd), headerBudget, MAX_HEADER_DESC_LINES);
                    }
                }

                int barHeight = Math.max(HEADER_BAR_H, titleLines * LINE_H + 4);
                int headerHeight = HEADER_GAP_TOP
                        + barHeight
                        + (descLines > 0 ? descLines * LINE_H + 1 : 0)
                        + HEADER_GAP_BOTTOM;

                rows.add(new Row(true, group, null, -1, y, headerHeight,
                        titleLines, descLines, groupCount, currentCollapsed, barHeight));
                y += headerHeight;
            } else if (group.isEmpty()) {
                currentGroup = "";
                currentCollapsed = false;
            } else if (!group.equals(currentGroup)) {
                // Search mode (headers suppressed) - still track the group so the
                // collapse test below stays coherent.
                currentGroup = group;
                currentCollapsed = false;
            }

            // Entries inside a collapsed group emit NO row at all. The old code
            // `continue`d after already advancing y, which left a blank gap the
            // size of the hidden entry - looking exactly like "content missing".
            if (currentCollapsed) continue;

            int nameLines = measureLines(font, safeText(entry.getDisplayName()), nameBudget, MAX_NAME_LINES);
            String desc = safeText(entry.getDescription());
            int descLines = desc.isBlank() ? 0 : 1; // descriptions render as one ellipsised line

            int height = ROW_PAD_TOP + nameLines * LINE_H + descLines * (LINE_H - 1) + ROW_PAD_BOTTOM;
            if (height < baseRowHeight) height = baseRowHeight;

            rows.add(new Row(false, group, entry, i, y, height, nameLines, descLines, 0, false, 0));
            y += height;
        }

        return new Layout(rows, startY, y - startY, nameBudget, descBudget, x, right);
    }

    /**
     * How many lines {@code text} wraps to inside {@code maxWidth}, clamped to
     * {@code maxLines}. Uses the font's real word-wrap measurement rather than a
     * character-count guess.
     */
    public static int measureLines(Font font, String text, int maxWidth, int maxLines) {
        if (font == null || text == null || text.isEmpty()) return 1;
        if (maxWidth <= 0) return 1;
        int h;
        try {
            h = font.wordWrapHeight(text, maxWidth);
        } catch (Exception e) {
            return 1;
        }
        int lineHeight = font.lineHeight > 0 ? font.lineHeight : 9;
        int lines = Math.max(1, (h + lineHeight - 1) / lineHeight);
        if (lines > maxLines) lines = maxLines;
        return lines;
    }

    /**
     * Trim {@code text} so it fits inside {@code maxWidth}, appending an ellipsis
     * when it had to be cut. Replaces the old hard-coded "truncate at 70 chars",
     * which could still overrun the value controls at small window widths.
     */
    public static String ellipsize(Font font, String text, int maxWidth) {
        if (font == null || text == null || text.isEmpty()) return text;
        if (maxWidth <= 0) return "";
        if (font.width(text) <= maxWidth) return text;
        String fitted = font.plainSubstrByWidth(text, Math.max(0, maxWidth - font.width("...")));
        return fitted + "...";
    }

    /** Split {@code text} into at most {@code maxLines} wrapped lines. */
    public static List<String> wrapLines(Font font, String text, int maxWidth, int maxLines) {
        List<String> out = new ArrayList<>();
        if (font == null || text == null || text.isEmpty() || maxWidth <= 0 || maxLines <= 0) return out;
        String remaining = text;
        while (!remaining.isEmpty() && out.size() < maxLines) {
            if (font.width(remaining) <= maxWidth) {
                out.add(remaining);
                // The whole remainder just fit on this line - it's fully
                // consumed. Without clearing it here, the overflow check below
                // saw the ORIGINAL (never-truncated) text still sitting in
                // `remaining` and, whenever this fit happened on the very
                // first pass (out.size() == maxLines == 1, the common case for
                // any short one-line entry name), appended that same full text
                // onto itself a second time - e.g. "Enable Alchemy Overrides"
                // rendering as "Enable Alchemy Overrides Enable Alchemy
                // Overrides". This is the exact bug players reported.
                remaining = "";
                break;
            }
            String head = font.plainSubstrByWidth(remaining, maxWidth);
            if (head.isEmpty()) head = remaining.substring(0, 1);
            int cut = head.length();
            if (cut < remaining.length()) {
                int space = head.lastIndexOf(' ');
                if (space > 0) cut = space + 1;
            }
            out.add(remaining.substring(0, cut).stripTrailing());
            remaining = remaining.substring(cut);
        }
        if (!remaining.isEmpty() && out.size() == maxLines && !out.isEmpty()) {
            int last = out.size() - 1;
            out.set(last, ellipsize(font, out.get(last) + " " + remaining, maxWidth));
        }
        return out;
    }

    private static String safeText(String s) {
        return s == null ? "" : s;
    }

    private static String firstParagraph(String s) {
        if (s == null) return "";
        int nl = s.indexOf('\n');
        return nl >= 0 ? s.substring(0, nl) : s;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Button-row layout (sub-tab bar and group bar)
    // ════════════════════════════════════════════════════════════════════

    /**
     * The result of laying out one wrapping row of buttons. {@code rects} and
     * {@code labels} are index-parallel; {@code rowCount} is the REAL number of
     * rows the wrap loop produced, not an estimate, and {@code bottomY} is the Y
     * just past the last row. Both the sub-tab bar and the group bar go through
     * this, so the render pass, the click pass and the tooltip pass can never
     * again disagree about where a button is.
     */
    public static final class ButtonRow {
        public final List<int[]> rects;
        public final List<String> labels;
        public final int rowCount;
        public final int bottomY;

        ButtonRow(List<int[]> rects, List<String> labels, int rowCount, int bottomY) {
            this.rects = rects;
            this.labels = labels;
            this.rowCount = rowCount;
            this.bottomY = bottomY;
        }

        public int size() {
            return rects.size();
        }

        /** Index of the button under the cursor, or -1. */
        public int indexAt(double mouseX, double mouseY) {
            for (int i = 0; i < rects.size(); i++) {
                int[] r = rects.get(i);
                if (mouseX >= r[0] && mouseX < r[0] + r[2] && mouseY >= r[1] && mouseY < r[1] + r[3]) {
                    return i;
                }
            }
            return -1;
        }
    }

    /** Lay out fixed-width buttons (the sub-tab bar), wrapping at {@code maxRight}. */
    public static ButtonRow layoutFixedButtons(List<String> labels, int startX, int startY,
                                               int maxRight, int buttonW, int buttonH,
                                               int gapX, int gapY) {
        List<int[]> rects = new ArrayList<>();
        List<String> out = new ArrayList<>();
        if (labels == null || labels.isEmpty()) {
            return new ButtonRow(rects, out, 1, startY + buttonH + gapY);
        }
        int cx = startX;
        int cy = startY;
        int rows = 1;
        for (String label : labels) {
            if (cx != startX && cx + buttonW > maxRight) {
                cx = startX;
                cy += buttonH + gapY;
                rows++;
            }
            rects.add(new int[]{cx, cy, buttonW, buttonH});
            out.add(label);
            cx += buttonW + gapX;
        }
        return new ButtonRow(rects, out, rows, cy + buttonH + gapY);
    }

    /** Lay out text-width buttons (the group bar), wrapping at {@code maxRight}. */
    public static ButtonRow layoutTextButtons(List<String> labels, Font font, int startX, int startY,
                                              int maxRight, int minW, int padX, int buttonH,
                                              int gapX, int gapY) {
        List<int[]> rects = new ArrayList<>();
        List<String> out = new ArrayList<>();
        if (labels == null || labels.isEmpty() || font == null) {
            return new ButtonRow(rects, out, 0, startY);
        }
        int cx = startX;
        int cy = startY;
        int rows = 1;
        for (String label : labels) {
            int w = Math.max(minW, font.width(label) + padX);
            if (cx != startX && cx + w > maxRight) {
                cx = startX;
                cy += buttonH + gapY;
                rows++;
            }
            rects.add(new int[]{cx, cy, w, buttonH});
            out.add(label);
            cx += w + gapX;
        }
        return new ButtonRow(rects, out, rows, cy + buttonH + gapY);
    }
}
