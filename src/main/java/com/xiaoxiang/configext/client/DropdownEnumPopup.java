package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Dropdown popup for enum-like config values.
 * Shows a list of valid options that the user can select from.
 */
public class DropdownEnumPopup {
    private boolean open = false;
    private String configPath = "";
    private List<String> options = new ArrayList<>();
    private int selectedIndex = 0;
    private int popupX, popupY, popupW, popupH;
    private int scrollOffset = 0;
    private static final int LINE_HEIGHT = 14;
    private static final int MAX_VISIBLE = 10;

    public void open(String configPath, List<String> options, String currentValue) {
        this.open = true;
        this.configPath = configPath;
        this.options = options != null ? options : new ArrayList<>();
        this.selectedIndex = 0;
        for (int i = 0; i < this.options.size(); i++) {
            if (this.options.get(i).equalsIgnoreCase(currentValue)) {
                this.selectedIndex = i;
                this.scrollOffset = Math.max(0, i - MAX_VISIBLE / 2);
                break;
            }
        }
    }

    public void close() { open = false; }
    public boolean isOpen() { return open; }

    public void render(GuiGraphics g, int screenWidth, int screenHeight, int mouseX, int mouseY, Font font) {
        if (!open) return;

        popupW = 160;
        popupH = Math.min(options.size(), MAX_VISIBLE) * LINE_HEIGHT + 16;
        popupX = screenWidth / 2 - popupW / 2;
        popupY = screenHeight / 2 - popupH / 2;

        // Background
        g.fill(popupX, popupY, popupX + popupW, popupY + popupH, 0xE0202020);
        g.renderOutline(popupX, popupY, popupW, popupH, 0xFF606080);

        // Title
        g.drawCenteredString(font, "\u00A7eSelect Value", popupX + popupW / 2, popupY + 4, 0xFFFFFF);

        // Options
        int listY = popupY + 14;
        int visibleCount = Math.min(options.size() - scrollOffset, MAX_VISIBLE);
        for (int i = 0; i < visibleCount; i++) {
            int idx = scrollOffset + i;
            String option = options.get(idx);
            int y = listY + i * LINE_HEIGHT;
            boolean hover = mouseX >= popupX + 4 && mouseX < popupX + popupW - 4 &&
                           mouseY >= y && mouseY < y + LINE_HEIGHT;
            boolean selected = idx == selectedIndex;

            if (selected) {
                g.fill(popupX + 2, y, popupX + popupW - 2, y + LINE_HEIGHT, 0xFF404060);
            } else if (hover) {
                g.fill(popupX + 2, y, popupX + popupW - 2, y + LINE_HEIGHT, 0xFF303040);
            }

            String display = option;
            if (font.width(display) > popupW - 12) {
                while (font.width(display + "...") > popupW - 12 && display.length() > 0) {
                    display = display.substring(0, display.length() - 1);
                }
                display = display + "...";
            }
            int color = selected ? 0xFFFFE040 : (hover ? 0xFFFFFFFF : 0xFFCCCCCC);
            g.drawString(font, display, popupX + 6, y + 3, color);
        }

        // Scroll indicator
        if (options.size() > MAX_VISIBLE) {
            int scrollBarH = MAX_VISIBLE * LINE_HEIGHT;
            int thumbH = Math.max(10, scrollBarH * MAX_VISIBLE / options.size());
            int thumbY = listY + (scrollBarH - thumbH) * scrollOffset / Math.max(1, options.size() - MAX_VISIBLE);
            g.fill(popupX + popupW - 4, listY, popupX + popupW - 2, listY + scrollBarH, 0xFF404040);
            g.fill(popupX + popupW - 4, thumbY, popupX + popupW - 2, thumbY + thumbH, 0xFF808080);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!open || button != 0) return false;
        if (mouseX < popupX || mouseX >= popupX + popupW || mouseY < popupY || mouseY >= popupY + popupH) {
            // Click outside closes
            close();
            return false;
        }

        int listY = popupY + 14;
        int visibleCount = Math.min(options.size() - scrollOffset, MAX_VISIBLE);
        for (int i = 0; i < visibleCount; i++) {
            int idx = scrollOffset + i;
            int y = listY + i * LINE_HEIGHT;
            if (mouseY >= y && mouseY < y + LINE_HEIGHT) {
                selectedIndex = idx;
                String selected = options.get(idx);
                ConfigValueAccessor.setValueFromString(configPath, selected);
                close();
                return true;
            }
        }
        return true; // consume clicks inside popup
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!open) return false;
        if (delta > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else {
            scrollOffset = Math.min(Math.max(0, options.size() - MAX_VISIBLE), scrollOffset + 1);
        }
        return true;
    }
}
