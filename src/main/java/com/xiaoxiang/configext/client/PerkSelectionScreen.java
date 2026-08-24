package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * A standalone screen for selecting Golden Finger perks during character creation.
 * Shown from the IdentityDrawScreen when the player clicks the "Golden Finger" button.
 *
 * Layout:
 * - Left: category filter + scrollable perk list
 * - Right: selected perk details
 * - Bottom: Done button (returns to identity screen)
 *
 * The number of perks the player can select is fixed at 3.
 */
public class PerkSelectionScreen extends Screen {

    private final Screen parent;
    private int perkCategoryIndex = 0;
    private int perkScrollOffset = 0;
    private int selectedPerkForDetail = -1;
    private int perkDetailScrollOffset = 0;

    // Statically stored selected perks - shared with IdentityDrawScreenMixin
    public static final Set<Integer> selectedPerkIds = new LinkedHashSet<>();

    // The max number of perks allowed (fixed at 3)
    public static int maxPerkCount = 3;

    private final String[] categories;

    public PerkSelectionScreen(Screen parent) {
        super(Component.literal("Golden Finger"));
        this.parent = parent;
        List<String> cats = GoldenFingerPerks.getCategories();
        this.categories = cats.toArray(new String[0]);
    }

    @Override
    protected void init() {
        // Done button
        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            this.minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 50, this.height - 24, 100, 20).build());

        // Loadouts button
        addRenderableWidget(Button.builder(
                Component.literal("\u00A7eLoadouts"),
                btn -> this.minecraft.setScreen(new LoadoutScreen(this, null))
        ).bounds(this.width / 2 - 150, this.height - 24, 90, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Dark background
        g.fill(0, 0, this.width, this.height, 0xD0101018);

        // Title
        g.drawCenteredString(this.font, "\u00A7l\u00A76\u00A7oGolden Finger - Choose Your Destiny",
                this.width / 2, 6, 0xFFFFFF);
        g.drawCenteredString(this.font, "\u00A77Select up to " + maxPerkCount + " perks  |  Selected: " +
                selectedPerkIds.size() + "/" + maxPerkCount, this.width / 2, 18, 0xAAAAAA);

        int panelX = 10;
        int panelY = 30;
        int panelW = this.width - 20;
        int panelH = this.height - 60;

        int leftPanelW = 160;
        int rightPanelX = panelX + leftPanelW + 4;
        int rightPanelW = panelW - leftPanelW - 4;

        // ── Left panel: category filter + perk list ──
        g.fill(panelX, panelY, panelX + leftPanelW, panelY + panelH, 0x80000000);
        g.renderOutline(panelX, panelY, leftPanelW, panelH, 0xFF606080);

        // Category tabs
        g.drawString(this.font, "\u00A7lCategory", panelX + 4, panelY + 4, 0xFFFFFF);
        int catY = panelY + 16;
        int catH = 14;
        for (int i = 0; i < categories.length; i++) {
            int cy = catY + i * catH;
            boolean selected = (i == perkCategoryIndex);
            boolean hovered = mouseX >= panelX + 2 && mouseX < panelX + leftPanelW - 2 &&
                    mouseY >= cy && mouseY < cy + catH;
            int bg = selected ? 0xFF403020 : (hovered ? 0xFF303040 : 0x40202020);
            g.fill(panelX + 2, cy, panelX + leftPanelW - 2, cy + catH - 1, bg);
            String label = (selected ? "\u00A7e\u00A7l" : "\u00A77") + categories[i];
            g.drawString(this.font, label, panelX + 6, cy + 2, 0xFFFFFF);
        }

        // Perk list for selected category
        int perkListY = catY + categories.length * catH + 6;
        g.drawString(this.font, "\u00A7lPerks", panelX + 4, perkListY, 0xFFFFFF);
        perkListY += 12;

        List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
        int perkH = 22;
        int listH = panelY + panelH - perkListY - 4;
        int maxVisible = listH / perkH;

        // Adjust scroll
        if (perkScrollOffset > Math.max(0, perks.size() - maxVisible)) {
            perkScrollOffset = Math.max(0, perks.size() - maxVisible);
        }

        for (int i = 0; i < maxVisible && (i + perkScrollOffset) < perks.size(); i++) {
            GoldenFingerPerks.Perk perk = perks.get(i + perkScrollOffset);
            int py = perkListY + i * perkH;
            boolean isSelected = selectedPerkIds.contains(perk.id);
            boolean isHovered = mouseX >= panelX + 2 && mouseX < panelX + leftPanelW - 2 &&
                    mouseY >= py && mouseY < py + perkH;
            boolean isDetail = (selectedPerkForDetail == perk.id);

            int bg = isSelected ? 0xFF204020 : (isDetail ? 0xFF203040 : (isHovered ? 0xFF303050 : 0x40202020));
            g.fill(panelX + 2, py, panelX + leftPanelW - 2, py + perkH - 1, bg);
            int border = isSelected ? 0xFF40FF40 : (isDetail ? 0xFF40A0FF : 0xFF606080);
            g.renderOutline(panelX + 2, py, leftPanelW - 4, perkH - 1, border);

            String tierColor = GoldenFingerPerks.getTierColor(perk.tier);
            String tierName = GoldenFingerPerks.getTierName(perk.tier);
            g.drawString(this.font, tierColor + "\u00A7l" + perk.name, panelX + 6, py + 2, 0xFFFFFF);
            g.drawString(this.font, "\u00A78" + tierColor + tierName + " Tier  " +
                    (isSelected ? "\u00A7a\u2713 Selected" : ""), panelX + 6, py + 12, 0xAAAAAA);
        }

        // Scroll indicator
        if (perks.size() > maxVisible) {
            g.drawString(this.font, "\u00A78" + (perkScrollOffset + 1) + "-" +
                    Math.min(perkScrollOffset + maxVisible, perks.size()) + "/" + perks.size(),
                    panelX + 4, panelY + panelH - 10, 0x888888);
        }

        // ── Right panel: perk detail ──
        g.fill(rightPanelX, panelY, rightPanelX + rightPanelW, panelY + panelH, 0x80000000);
        g.renderOutline(rightPanelX, panelY, rightPanelW, panelH, 0xFF606080);

        if (selectedPerkForDetail >= 0) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(selectedPerkForDetail);
            if (perk != null) {
                int dx = rightPanelX + 8;
                int dy = panelY + 8;
                String tierColor = GoldenFingerPerks.getTierColor(perk.tier);
                String tierName = GoldenFingerPerks.getTierName(perk.tier);

                g.drawString(this.font, tierColor + "\u00A7l" + perk.name, dx, dy, 0xFFFFFF);
                dy += 12;
                g.drawString(this.font, "\u00A77Category: \u00A7f" + perk.category +
                        "  \u00A77Tier: " + tierColor + tierName + " (" + perk.tier + "/5)", dx, dy, 0xCCCCCC);
                dy += 16;

                // Description (wrapped)
                String desc = perk.description;
                int wrapW = rightPanelW - 16;
                List<String> wrapped = wrapText(desc, this.font, wrapW);
                for (String line : wrapped) {
                    g.drawString(this.font, "\u00A7f" + line, dx, dy, 0xDDDDDD);
                    dy += 10;
                }
                dy += 8;

                // Attribute changes preview (additive/multiplier stats)
                List<String> attrChanges = PerkApplier.previewPerk(perk);
                if (!attrChanges.isEmpty()) {
                    g.drawString(this.font, "\u00A7l\u00A7bAttribute Changes:", dx, dy, 0xBBBBFF);
                    dy += 12;
                    int attrBoxY = dy;
                    int attrBoxH = attrChanges.size() * 11 + 6;
                    g.fill(rightPanelX + 4, attrBoxY - 2, rightPanelX + rightPanelW - 4, attrBoxY + attrBoxH, 0x40002040);
                    g.fill(rightPanelX + 5, attrBoxY - 1, rightPanelX + rightPanelW - 5, attrBoxY + attrBoxH - 1, 0x60101030);
                    dy += 3;
                    for (String change : attrChanges) {
                        String display = simplifyAttributePath(change);
                        g.drawString(this.font, display, dx + 4, dy, 0xFFFFFF);
                        dy += 11;
                    }
                    dy += 8;
                }

                // Selected status
                boolean isSelected = selectedPerkIds.contains(perk.id);
                if (isSelected) {
                    g.drawString(this.font, "\u00A7a\u00A7l\u2713 This perk is selected", dx, dy, 0xFFFFFF);
                } else if (selectedPerkIds.size() >= maxPerkCount) {
                    g.drawString(this.font, "\u00A7c\u00A7lMaximum perks reached - deselect one to choose this", dx, dy, 0xFFFFFF);
                } else {
                    g.drawString(this.font, "\u00A7eClick in the left list to select this perk", dx, dy, 0xFFFFFF);
                }
            }
        } else {
            g.drawCenteredString(this.font, "\u00A78Click a perk on the left to see details",
                    rightPanelX + rightPanelW / 2, panelY + panelH / 2, 0x888888);
        }

        // Selected perks summary at bottom of right panel
        int selY = panelY + panelH - 60;
        g.drawString(this.font, "\u00A7l\u00A76Selected Perks:", rightPanelX + 8, selY, 0xFFFFFF);
        selY += 12;
        int idx = 0;
        for (int perkId : selectedPerkIds) {
            GoldenFingerPerks.Perk p = GoldenFingerPerks.getById(perkId);
            if (p != null) {
                String tierColor = GoldenFingerPerks.getTierColor(p.tier);
                g.drawString(this.font, tierColor + "\u2713 " + p.name, rightPanelX + 12, selY, 0xDDDDDD);
                selY += 10;
                idx++;
                if (idx >= 4) break;
            }
        }
        if (selectedPerkIds.isEmpty()) {
            g.drawString(this.font, "\u00A78None selected yet", rightPanelX + 12, selY, 0x888888);
        }

        // Render buttons
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelX = 10;
        int panelY = 30;
        int panelW = this.width - 20;
        int panelH = this.height - 60;
        int leftPanelW = 160;

        // Category tabs
        int catY = panelY + 16;
        int catH = 14;
        for (int i = 0; i < categories.length; i++) {
            int cy = catY + i * catH;
            if (mouseX >= panelX + 2 && mouseX < panelX + leftPanelW - 2 &&
                    mouseY >= cy && mouseY < cy + catH) {
                perkCategoryIndex = i;
                perkScrollOffset = 0;
                return true;
            }
        }

        // Perk list
        int perkListY = catY + categories.length * catH + 6 + 12;
        int perkH = 22;
        int listH = panelY + panelH - perkListY - 4;
        int maxVisible = listH / perkH;

        List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
        for (int i = 0; i < maxVisible && (i + perkScrollOffset) < perks.size(); i++) {
            GoldenFingerPerks.Perk perk = perks.get(i + perkScrollOffset);
            int py = perkListY + i * perkH;
            if (mouseX >= panelX + 2 && mouseX < panelX + leftPanelW - 2 &&
                    mouseY >= py && mouseY < py + perkH) {
                // Set as detail
                selectedPerkForDetail = perk.id;
                perkDetailScrollOffset = 0;

                // Toggle selection
                if (selectedPerkIds.contains(perk.id)) {
                    selectedPerkIds.remove(perk.id);
                } else if (selectedPerkIds.size() < maxPerkCount) {
                    selectedPerkIds.add(perk.id);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int panelX = 10;
        int panelY = 30;
        int panelW = this.width - 20;
        int panelH = this.height - 60;
        int leftPanelW = 160;

        // Scroll perk list if mouse is over left panel
        if (mouseX >= panelX && mouseX < panelX + leftPanelW) {
            int catY = panelY + 16;
            int catH = 14;
            int perkListY = catY + categories.length * catH + 6 + 12;
            int perkH = 22;
            int listH = panelY + panelH - perkListY - 4;
            int maxVisible = listH / perkH;
            List<GoldenFingerPerks.Perk> perks = GoldenFingerPerks.getByCategory(categories[perkCategoryIndex]);
            if (perks.size() > maxVisible) {
                if (delta > 0) perkScrollOffset = Math.max(0, perkScrollOffset - 3);
                else perkScrollOffset = Math.min(perks.size() - maxVisible, perkScrollOffset + 3);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** Simple text wrapping. */
    private List<String> wrapText(String text, net.minecraft.client.gui.Font font, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.length() > 0 && font.width(current + " " + word) > maxWidth) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (current.length() > 0) current.append(" ");
                current.append(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    /** Simplify an attribute change string for display. */
    private String simplifyAttributePath(String change) {
        int colonIdx = change.indexOf(':');
        if (colonIdx < 0) return change;
        String prefix = change.substring(0, colonIdx);
        String rest = change.substring(colonIdx);
        String[] parts = prefix.replaceAll("\u00A7.", "").split("\\.");
        String display;
        if (parts.length >= 2) {
            display = parts[0] + " " + camelToWords(parts[parts.length - 1]);
        } else {
            display = camelToWords(parts[0]);
        }
        String colorCode = "";
        if (prefix.startsWith("\u00A7")) colorCode = prefix.substring(0, 2);
        return colorCode + display + rest;
    }

    /** Convert camelCase to Title Case words. */
    private String camelToWords(String camel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) sb.append(' ');
            if (i == 0) sb.append(Character.toUpperCase(c));
            else sb.append(c);
        }
        return sb.toString();
    }
}
