package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimap-style navigation panel that shows the user's position in the config tree.
 * Displays a compact tree view of tabs/sub-tabs with the current location highlighted.
 */
public class MinimapNav {
    private int x, y, w, h;
    private boolean visible = false;

    public void setPosition(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render(GuiGraphics g, Font font, String activeTopTab, String activeSubTab,
                       List<String> topTabs, java.util.Map<String, List<String>> tabToSubtabs,
                       int scrollOffset, int totalEntries, int mouseX, int mouseY) {
        if (!visible) return;

        // Background
        g.fill(x, y, x + w, y + h, 0xC0202020);
        g.renderOutline(x, y, w, h, 0xFF404060);

        // Title
        g.drawString(font, "\u00A7e\u00A7lNav", x + 4, y + 2, 0xFFFFFF);

        int lineY = y + 14;
        int indent = 8;

        for (String tab : topTabs) {
            boolean isActiveTab = tab.equals(activeTopTab);
            int tabCount = tabToSubtabs.getOrDefault(tab, new ArrayList<>()).size();

            // Tab name
            String tabLabel = (isActiveTab ? "\u00A7e\u25B8 " : "\u00A78\u25B8 ") + tab;
            if (font.width(tabLabel.replaceAll("\u00A7.", "")) > w - 8) {
                while (font.width(tabLabel.replaceAll("\u00A7.", "") + "...") > w - 8 && tabLabel.length() > 5) {
                    tabLabel = tabLabel.substring(0, tabLabel.length() - 1);
                }
                tabLabel = tabLabel + "...";
            }
            g.drawString(font, tabLabel, x + 4, lineY, isActiveTab ? 0xFFFFE040 : 0xFF808080);
            lineY += 10;

            // Show sub-tabs for active tab
            if (isActiveTab) {
                List<String> subs = tabToSubtabs.getOrDefault(tab, new ArrayList<>());
                for (String sub : subs) {
                    // Bounds check INSIDE the sub-tab loop too - the active tab can have
                    // more sub-tabs than fit, and only checking after the whole tab (as
                    // below) let a long sub-tab list spill text past the box's bottom
                    // border before the outer check ever ran.
                    if (lineY > y + h - 10) break;
                    boolean isActiveSub = sub.equals(activeSubTab);
                    String subLabel = (isActiveSub ? "\u00A7b  \u25AA " : "\u00A78  \u25AA ") + sub;
                    if (font.width(subLabel.replaceAll("\u00A7.", "")) > w - 8) {
                        while (font.width(subLabel.replaceAll("\u00A7.", "") + "...") > w - 8 && subLabel.length() > 5) {
                            subLabel = subLabel.substring(0, subLabel.length() - 1);
                        }
                        subLabel = subLabel + "...";
                    }
                    g.drawString(font, subLabel, x + 4, lineY, isActiveSub ? 0xFF40C0FF : 0xFF606060);
                    lineY += 9;
                }
            }

            if (lineY > y + h - 10) break; // Stop if we run out of space
        }

        // Scroll position indicator
        if (totalEntries > 0) {
            int scrollY = y + h - 8;
            int scrollBarW = w - 8;
            int scrollIndicatorX = x + 4 + (int)((float) scrollOffset / Math.max(1, totalEntries) * scrollBarW);
            g.fill(x + 4, scrollY, x + 4 + scrollBarW, scrollY + 2, 0xFF404060);
            g.fill(scrollIndicatorX, scrollY - 1, scrollIndicatorX + 3, scrollY + 3, 0xFF40C0FF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (mouseX < x || mouseX >= x + w || mouseY < y || mouseY >= y + h) return false;
        return true; // Consume clicks within minimap
    }
}
