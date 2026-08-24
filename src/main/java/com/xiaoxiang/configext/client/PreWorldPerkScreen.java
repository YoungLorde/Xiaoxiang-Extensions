package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * Screen shown when the player clicks "Create" on the Create World screen.
 * Shows perk selection with attribute previews, then "Next" to proceed
 * to origin selection before world generation.
 */
public class PreWorldPerkScreen extends Screen {

    private final Screen createWorldScreen;
    private final Runnable onNextSettings;
    private int perkCategoryIndex = 0;
    private int perkScrollOffset = 0;
    private int selectedPerkForDetail = -1;
    private Button nextButton;
    private final String[] categories;

    // Scroll offset for the bottom cumulative panel
    private int bottomScrollOffset = 0;

    // Flag to indicate we're in pre-world mode (affects IdentityDrawScreenMixin behavior)
    public static boolean inPreWorldMode = false;

    public PreWorldPerkScreen(Screen createWorldScreen, Runnable onNextSettings) {
        super(Component.literal("Golden Finger - Choose Your Destiny"));
        this.createWorldScreen = createWorldScreen;
        this.onNextSettings = onNextSettings;
        List<String> cats = GoldenFingerPerks.getCategories();
        this.categories = cats.toArray(new String[0]);
    }

    @Override
    protected void init() {
        // Next button - grayed out until all perks selected
        nextButton = Button.builder(
                Component.literal("\u00A7aNext"),
                btn -> {
                    if (allPerksSelected()) {
                        // Apply perks to config
                        if (!PerkSelectionScreen.selectedPerkIds.isEmpty()) {
                            PerkApplier.applyPerks(new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds));
                            AppliedPerkTracker.onPerksApplied(new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds));
                        }
                        // Proceed to next step (origin selection)
                        onNextSettings.run();
                    }
                }
        ).bounds(this.width / 2 - 40, this.height - 28, 80, 20).build();

        nextButton.active = false;
        addRenderableWidget(nextButton);

        // Back button (cancel)
        addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> {
                    this.minecraft.setScreen(createWorldScreen);
                }
        ).bounds(this.width / 2 - 130, this.height - 28, 80, 20).build());

        // Clear Selection button
        addRenderableWidget(Button.builder(
                Component.literal("Clear"),
                btn -> {
                    PerkSelectionScreen.selectedPerkIds.clear();
                    updateNextButton();
                }
        ).bounds(this.width / 2 + 50, this.height - 28, 80, 20).build());

        // Loadouts button
        addRenderableWidget(Button.builder(
                Component.literal("\u00A7eLoadouts"),
                btn -> {
                    this.minecraft.setScreen(new LoadoutScreen(this, () -> updateNextButton()));
                }
        ).bounds(this.width / 2 + 136, this.height - 28, 70, 20).build());
    }

    private boolean allPerksSelected() {
        return PerkSelectionScreen.selectedPerkIds.size() >= PerkSelectionScreen.maxPerkCount;
    }

    private void updateNextButton() {
        if (nextButton != null) {
            nextButton.active = allPerksSelected();
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xD0101018);

        // Title
        g.drawCenteredString(this.font, "\u00A7l\u00A76\u00A7oGolden Finger - Choose Your Destiny",
                this.width / 2, 6, 0xFFFFFF);
        g.drawCenteredString(this.font, "\u00A77Select exactly " + PerkSelectionScreen.maxPerkCount +
                " perks  |  Selected: " + PerkSelectionScreen.selectedPerkIds.size() + "/" +
                PerkSelectionScreen.maxPerkCount,
                this.width / 2, 20, 0xAAAAFF);

        // Status message
        if (!allPerksSelected()) {
            g.drawCenteredString(this.font, "\u00A7c\u00A7oSelect " +
                    (PerkSelectionScreen.maxPerkCount - PerkSelectionScreen.selectedPerkIds.size()) +
                    " more perk(s) to continue!",
                    this.width / 2, 32, 0xFF6666);
        } else {
            g.drawCenteredString(this.font, "\u00A7a\u00A7oAll perks selected! Click Next to choose your origin.",
                    this.width / 2, 32, 0x66FF66);
        }

        // Layout: raised boxes, bottom partition for selected perks
        int topY = 44;
        // Bottom box is 110px + 4px gap + 28px button bar = 142px from bottom
        int bottomPartitionH = 142;
        int listBottom = this.height - 28 - bottomPartitionH - 4;
        int listH = listBottom - topY;

        // Category tabs (left side)
        int catX = 8;
        int catY = topY;
        g.drawString(this.font, "\u00A7lCategories:", catX, catY, 0xFFFFFF);
        catY += 12;
        for (int i = 0; i < categories.length; i++) {
            boolean selected = (i == perkCategoryIndex);
            String prefix = selected ? "\u00A7e> " : "\u00A77  ";
            g.drawString(this.font, prefix + categories[i], catX, catY, 0xFFFFFF);
            catY += 11;
        }

        // Perk list (left-center area) - raised
        int listX = 120;
        int listW = 200;
        g.fill(listX - 2, topY - 2, listX + listW + 2, topY + listH + 2, 0x40202040);
        g.fill(listX, topY, listX + listW, topY + listH, 0x80000010);

        List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
        int visiblePerks = listH / 12;
        int maxScroll = Math.max(0, perks.size() - visiblePerks);
        perkScrollOffset = Math.max(0, Math.min(perkScrollOffset, maxScroll));

        for (int i = perkScrollOffset; i < Math.min(perks.size(), perkScrollOffset + visiblePerks); i++) {
            GoldenFingerPerks.Perk perk = perks.get(i);
            int py = topY + (i - perkScrollOffset) * 12;
            boolean isSelected = PerkSelectionScreen.selectedPerkIds.contains(perk.id);
            boolean isHovered = mouseX >= listX && mouseX <= listX + listW && mouseY >= py && mouseY <= py + 11;
            if (isHovered) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.PERK, perk.id);

            String checkmark = isSelected ? "\u00A7a\u2714 " : "\u00A77\u2718 ";
            String tierStars = "\u00A76";
            for (int t = 0; t < perk.tier; t++) tierStars += "\u2605";

            String text = checkmark + "\u00A7f" + perk.name + " " + tierStars;
            if (isHovered) {
                g.fill(listX, py, listX + listW, py + 11, 0x60404080);
                selectedPerkForDetail = perk.id;
            }
            g.drawString(this.font, text, listX + 2, py + 1, 0xFFFFFF);
        }

        if (perks.size() > visiblePerks) {
            g.drawString(this.font, "\u00A77(" + (perkScrollOffset + 1) + "-" +
                    Math.min(perks.size(), perkScrollOffset + visiblePerks) + "/" + perks.size() + ")",
                    listX + listW - 40, topY - 10, 0x888888);
        }

        // Detail panel (right side) - raised
        int detailX = 340;
        int detailW = this.width - detailX - 10;
        g.fill(detailX - 2, topY - 2, detailX + detailW + 2, topY + listH + 2, 0x40202040);
        g.fill(detailX, topY, detailX + detailW, topY + listH, 0x80000010);

        if (selectedPerkForDetail > 0) {
            GoldenFingerPerks.Perk detail = GoldenFingerPerks.getById(selectedPerkForDetail);
            if (detail != null) {
                int dy = topY + 4;
                // Title
                g.drawString(this.font, "\u00A7l\u00A76" + detail.name, detailX + 4, dy, 0xFFFFFF);
                dy += 14;
                g.drawString(this.font, "\u00A77Category: " + detail.category + "  Tier: " + detail.tier + "/5",
                        detailX + 4, dy, 0xAAAAFF);
                dy += 14;

                // Get attribute changes for this perk (preview, don't apply)
                List<String> attrChanges = PerkApplier.previewPerk(detail);

                // Split the detail panel into two partitions:
                // Upper: Attribute changes (if any)
                // Lower: Description

                int descPartitionStart = dy;
                int attrPartitionH = 0;

                if (!attrChanges.isEmpty()) {
                    // Draw "Attributes" sub-header
                    dy += 4;
                    g.drawString(this.font, "\u00A7l\u00A7bAttributes:", detailX + 4, dy, 0xBBBBFF);
                    dy += 12;

                    // Draw attribute box background
                    int attrBoxY = dy;
                    int attrBoxH = attrChanges.size() * 11 + 6;
                    g.fill(detailX, attrBoxY - 2, detailX + detailW, attrBoxY + attrBoxH, 0x40002040);
                    g.fill(detailX + 1, attrBoxY - 1, detailX + detailW - 1, attrBoxY + attrBoxH - 1, 0x60101030);

                    dy += 3;
                    for (String change : attrChanges) {
                        // Simplify the path for display: show last meaningful part
                        String display = simplifyAttributePath(change);
                        g.drawString(this.font, display, detailX + 6, dy, 0xFFFFFF);
                        dy += 11;
                    }
                    dy += 5;
                    attrPartitionH = dy - descPartitionStart;
                }

                // Description partition
                descPartitionStart = dy;
                g.drawString(this.font, "\u00A7l\u00A7dDescription:", detailX + 4, dy, 0xDDAAFF);
                dy += 12;

                // Draw description box background
                int descBoxY = dy;
                String desc = detail.description;
                List<String> descLines = wrapText("\u00A7f" + desc, detailW - 12);
                int descBoxH = descLines.size() * 11 + 6;
                g.fill(detailX, descBoxY - 2, detailX + detailW, descBoxY + descBoxH, 0x40201810);
                g.fill(detailX + 1, descBoxY - 1, detailX + detailW - 1, descBoxY + descBoxH - 1, 0x60201008);

                dy += 3;
                for (String line : descLines) {
                    g.drawString(this.font, line, detailX + 6, dy, 0xFFFFFF);
                    dy += 11;
                }

                dy += 8;
                boolean isSelected = PerkSelectionScreen.selectedPerkIds.contains(detail.id);
                if (isSelected) {
                    g.drawString(this.font, "\u00A7a\u2714 Selected - click to deselect", detailX + 4, dy, 0x66FF66);
                } else if (PerkSelectionScreen.selectedPerkIds.size() < PerkSelectionScreen.maxPerkCount) {
                    g.drawString(this.font, "\u00A7eClick to select", detailX + 4, dy, 0xFFFF66);
                } else {
                    g.drawString(this.font, "\u00A7cMax perks selected - clear one first", detailX + 4, dy, 0xFF6666);
                }
            }
        } else {
            g.drawCenteredString(this.font, "\u00A77Hover over a perk to see details",
                    detailX + detailW / 2, topY + listH / 2, 0x888888);
        }

        // ═══ Bottom partition: Selected Perks + Cumulative Attributes ═══
        // This is a scrollable, clipped box that sits above the Back/Next/Clear buttons.
        // Buttons are at this.height - 28, so the box must end above this.height - 32.
        int btnBarH = 28; // button bar height from bottom
        int bottomBoxBottom = this.height - btnBarH - 4; // 4px gap above buttons
        int bottomBoxH = 110; // fixed height for the box
        int bottomBoxTop = bottomBoxBottom - bottomBoxH;
        int bottomBoxX = 10;
        int bottomBoxW = this.width - 20;

        // Draw box background
        g.fill(bottomBoxX - 2, bottomBoxTop - 2, bottomBoxX + bottomBoxW + 2, bottomBoxBottom + 2, 0x40404080);
        g.fill(bottomBoxX, bottomBoxTop, bottomBoxX + bottomBoxW, bottomBoxBottom, 0x80101020);

        // Build all content lines for the bottom panel
        java.util.List<String> bottomLines = new java.util.ArrayList<>();

        // Section 1: Selected Perks header + names
        bottomLines.add("\u00A7l\u00A7aSelected Perks (" +
                PerkSelectionScreen.selectedPerkIds.size() + "/" + PerkSelectionScreen.maxPerkCount + "):");

        if (PerkSelectionScreen.selectedPerkIds.isEmpty()) {
            bottomLines.add("\u00A77No perks selected yet. Click perks in the list above to select them.");
        } else {
            // Build perk name line(s), wrapping at box width
            StringBuilder perkLine = new StringBuilder();
            int maxWidth = bottomBoxW - 16;
            for (int id : PerkSelectionScreen.selectedPerkIds) {
                GoldenFingerPerks.Perk p = GoldenFingerPerks.getById(id);
                if (p == null) continue;
                String segment = "\u00A7a" + p.name + "\u00A77  |  ";
                if (this.font.width(perkLine.toString() + segment) > maxWidth && perkLine.length() > 0) {
                    bottomLines.add(perkLine.toString());
                    perkLine = new StringBuilder();
                }
                perkLine.append(segment);
            }
            if (perkLine.length() > 0) {
                bottomLines.add(perkLine.toString());
            }
        }

        // Blank separator
        bottomLines.add("");

        // Section 2: Cumulative Attribute Multipliers header
        bottomLines.add("\u00A7l\u00A7bCumulative Attribute Multipliers:");

        // Get combined multipliers
        java.util.Map<String, Double> combined = PerkApplier.previewAllPerksCombined(
                PerkSelectionScreen.selectedPerkIds);

        if (!combined.isEmpty()) {
            // Display in two columns
            java.util.List<String> leftCol = new java.util.ArrayList<>();
            java.util.List<String> rightCol = new java.util.ArrayList<>();
            int total = combined.size();
            int perCol = (total + 1) / 2;
            int idx = 0;
            for (java.util.Map.Entry<String, Double> entry : combined.entrySet()) {
                String path = entry.getKey();
                double mult = entry.getValue();
                String display = simplifyPathOnly(path);
                String color = mult > 1.0 ? "\u00A7a" : (mult < 1.0 ? "\u00A7c" : "\u00A77");
                String text = color + display + ": x" + String.format("%.2f", mult);
                if (idx < perCol) {
                    leftCol.add(text);
                } else {
                    rightCol.add(text);
                }
                idx++;
            }
            int rows = Math.max(leftCol.size(), rightCol.size());
            for (int r = 0; r < rows; r++) {
                String left = r < leftCol.size() ? leftCol.get(r) : "";
                String right = r < rightCol.size() ? rightCol.get(r) : "";
                // Combine with padding — we'll draw them at different X positions,
                // but for scissor scrolling we need them on one line.
                // Use a special separator we can split on when drawing.
                bottomLines.add(left + "\u00A7r" + right);
            }
        } else {
            bottomLines.add("\u00A77Select perks to see cumulative attribute effects.");
        }

        // Calculate scroll
        int lineH = 11;
        int contentH = bottomLines.size() * lineH;
        int innerH = bottomBoxH - 8; // padding
        int maxBottomScroll = Math.max(0, contentH - innerH);
        bottomScrollOffset = Math.max(0, Math.min(bottomScrollOffset, maxBottomScroll));

        // Clip rendering to the box interior
        int clipTop = bottomBoxTop + 2;
        int clipBottom = bottomBoxBottom - 2;
        int clipLeft = bottomBoxX + 2;
        int clipRight = bottomBoxX + bottomBoxW - 2;

        g.enableScissor(clipLeft, clipTop, clipRight, clipBottom);

        int drawY = bottomBoxTop + 4 - bottomScrollOffset;
        int colMidX = bottomBoxX + bottomBoxW / 2;

        for (String line : bottomLines) {
            if (drawY + lineH >= clipTop && drawY <= clipBottom) {
                // Check if line has the two-column separator
                int sepIdx = line.indexOf("\u00A7r");
                if (sepIdx >= 0) {
                    String left = line.substring(0, sepIdx);
                    String right = line.substring(sepIdx + 2);
                    g.drawString(this.font, left, bottomBoxX + 6, drawY, 0xFFFFFF);
                    if (!right.isEmpty()) {
                        g.drawString(this.font, right, colMidX, drawY, 0xFFFFFF);
                    }
                } else {
                    g.drawString(this.font, line, bottomBoxX + 6, drawY, 0xFFFFFF);
                }
            }
            drawY += lineH;
        }

        g.disableScissor();

        // Scroll indicator
        if (contentH > innerH) {
            // Draw scrollbar on the right edge
            int scrollBarX = bottomBoxX + bottomBoxW - 4;
            int scrollBarH = innerH;
            int scrollBarY = bottomBoxTop + 4;
            g.fill(scrollBarX, scrollBarY, scrollBarX + 2, scrollBarY + scrollBarH, 0x40808080);
            int thumbH = Math.max(10, scrollBarH * innerH / contentH);
            int thumbY = scrollBarY + (scrollBarH - thumbH) * bottomScrollOffset / maxBottomScroll;
            g.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, 0xC0AAAAAA);

            // Scroll hint
            g.drawString(this.font, "\u00A78\u00A7o(" + (bottomScrollOffset / lineH + 1) + "-" +
                    Math.min(bottomLines.size(), (bottomScrollOffset + innerH) / lineH) + "/" + bottomLines.size() + ")",
                    bottomBoxX + bottomBoxW - 60, bottomBoxTop - 10, 0x888888);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Category tab click
        int catY = 56;
        int catX = 8;
        for (int i = 0; i < categories.length; i++) {
            if (mouseX >= catX && mouseX <= catX + 110 && mouseY >= catY && mouseY <= catY + 11) {
                perkCategoryIndex = i;
                perkScrollOffset = 0;
                return true;
            }
            catY += 11;
        }

        // Perk list click
        int topY = 44;
        int bottomPartitionH = 50;
        int listBottom = this.height - 28 - bottomPartitionH - 4;
        int listH = listBottom - topY;
        int listX = 120;
        int listW = 200;
        List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
        int visiblePerks = listH / 12;

        for (int i = perkScrollOffset; i < Math.min(perks.size(), perkScrollOffset + visiblePerks); i++) {
            GoldenFingerPerks.Perk perk = perks.get(i);
            int py = topY + (i - perkScrollOffset) * 12;
            if (mouseX >= listX && mouseX <= listX + listW && mouseY >= py && mouseY <= py + 11) {
                selectedPerkForDetail = perk.id;
                if (PerkSelectionScreen.selectedPerkIds.contains(perk.id)) {
                    PerkSelectionScreen.selectedPerkIds.remove(perk.id);
                } else if (PerkSelectionScreen.selectedPerkIds.size() < PerkSelectionScreen.maxPerkCount) {
                    PerkSelectionScreen.selectedPerkIds.add(perk.id);
                }
                updateNextButton();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int topY = 44;
        int bottomPartitionH = 50;
        int listBottom = this.height - 28 - bottomPartitionH - 4;
        int listH = listBottom - topY;
        List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
        int visiblePerks = listH / 12;
        int maxScroll = Math.max(0, perks.size() - visiblePerks);

        // Perk list scroll
        if (mouseX >= 120 && mouseX <= 320) {
            perkScrollOffset = Math.max(0, Math.min(maxScroll, perkScrollOffset + (int) delta));
            return true;
        }

        // Bottom panel scroll — check if mouse is within the bottom box area
        int btnBarH = 28;
        int bottomBoxBottom = this.height - btnBarH - 4;
        int bottomBoxH = 110;
        int bottomBoxTop = bottomBoxBottom - bottomBoxH;
        int bottomBoxX = 10;
        int bottomBoxW = this.width - 20;

        if (mouseX >= bottomBoxX && mouseX <= bottomBoxX + bottomBoxW &&
            mouseY >= bottomBoxTop && mouseY <= bottomBoxBottom) {
            // Scroll the bottom panel
            bottomScrollOffset = Math.max(0, bottomScrollOffset - (int) delta * 11);
            // Clamp is done in render, but clamp here too to avoid overshoot
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String clean = text.replaceAll("\u00A7.", "");
        StringBuilder current = new StringBuilder();
        String[] words = clean.split(" ");
        for (String word : words) {
            if (current.length() == 0) {
                current.append(word);
            } else if (this.font.width(current.toString() + " " + word) <= maxWidth) {
                current.append(" ").append(word);
            } else {
                lines.add("\u00A7f" + current.toString());
                current = new StringBuilder(word);
            }
        }
        if (current.length() > 0) {
            lines.add("\u00A7f" + current.toString());
        }
        return lines;
    }

    /**
     * Simplify a config path change string for display.
     * E.g. "spells.damageGlobalMultiplier: 1.0 -> 1.5 (x1.50)"
     * becomes "Damage Global Mult: 1.0 -> 1.5 (x1.50)"
     */
    private String simplifyAttributePath(String change) {
        // The change string is like "\u00A7apath.to.value: oldVal -> newVal (xFactor)"
        // Extract the path part and the rest
        int colonIdx = change.indexOf(':');
        if (colonIdx < 0) return simplifyPathOnly(change);

        String prefix = change.substring(0, colonIdx);
        String rest = change.substring(colonIdx);

        // Get the last segment of the path (after the last dot)
        // But keep the category for context
        String[] parts = prefix.split("\\.");
        String display;
        if (parts.length >= 2) {
            String category = parts[0];
            String name = parts[parts.length - 1];
            // Camel case to words
            name = camelToWords(name);
            display = category + " " + name;
        } else {
            display = camelToWords(parts[0]);
        }

        // Re-add the color code from the original
        String colorCode = "";
        if (prefix.startsWith("\u00A7")) {
            colorCode = prefix.substring(0, 2);
        }

        return colorCode + display + rest;
    }

    /** Simplify just a config path (no colon/value) for display. */
    private String simplifyPathOnly(String path) {
        // Strip color codes
        String clean = path.replaceAll("\u00A7.", "");
        String[] parts = clean.split("\\.");
        String display;
        if (parts.length >= 2) {
            String category = parts[0];
            String name = parts[parts.length - 1];
            name = camelToWords(name);
            display = category + " " + name;
        } else {
            display = camelToWords(parts[0]);
        }
        return display;
    }

    /** Convert camelCase to Title Case words. */
    private String camelToWords(String camel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                sb.append(' ');
            }
            if (i == 0) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
