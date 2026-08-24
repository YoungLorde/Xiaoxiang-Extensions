package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Multi-line editor popup for long string config values.
 * Allows editing strings that don't fit in a single-line edit box.
 */
public class MultiLineEditor {
    private boolean open = false;
    private String configPath = "";
    private String text = "";
    private EditBox editBox;
    private int popupX, popupY, popupW, popupH;

    public void open(String configPath, String currentValue) {
        this.open = true;
        this.configPath = configPath;
        this.text = currentValue != null ? currentValue : "";
    }

    public void close() {
        if (open && editBox != null) {
            String newText = editBox.getValue();
            if (!newText.equals(text)) {
                ConfigValueAccessor.setValueFromString(configPath, newText);
            }
        }
        open = false;
        editBox = null;
    }

    public boolean isOpen() { return open; }

    public void init(net.minecraft.client.gui.screens.Screen screen) {
        if (!open) return;
        popupW = Math.min(400, screen.width - 40);
        popupH = 100;
        popupX = screen.width / 2 - popupW / 2;
        popupY = screen.height / 2 - popupH / 2;

        editBox = new EditBox(net.minecraft.client.Minecraft.getInstance().font, popupX + 8, popupY + 20, popupW - 16, 16,
            Component.literal("Value"));
        editBox.setMaxLength(32767);
        editBox.setValue(text);
        editBox.setBordered(true);
    }

    public void render(GuiGraphics g, int mouseX, int mouseY, Font font) {
        if (!open) return;

        // Background
        g.fill(popupX, popupY, popupX + popupW, popupY + popupH, 0xE0202020);
        g.renderOutline(popupX, popupY, popupW, popupH, 0xFF606080);

        // Title
        g.drawCenteredString(font, "\u00A7e\u00A7lEdit Value", popupX + popupW / 2, popupY + 4, 0xFFFFFF);

        // Config path
        String pathDisplay = configPath;
        if (font.width(pathDisplay) > popupW - 16) {
            while (font.width(pathDisplay + "...") > popupW - 16 && pathDisplay.length() > 0) {
                pathDisplay = pathDisplay.substring(0, pathDisplay.length() - 1);
            }
            pathDisplay = pathDisplay + "...";
        }
        g.drawString(font, "\u00A78" + pathDisplay, popupX + 8, popupY + popupH - 14, 0xFF606060);

        // Edit box
        if (editBox != null) {
            editBox.render(g, mouseX, mouseY, 0);
        }

        // Save hint
        g.drawString(font, "\u00A78[Enter to save, Esc to cancel]", popupX + 8, popupY + popupH - 28, 0xFF606060);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!open || button != 0) return false;
        // Click outside the popup closes it (and commits the edit, same as close()
        // normally does). Without this bounds check, any click that isn't on the
        // edit box itself was silently ignored while `open` stayed true forever,
        // permanently swallowing every future click on the screen.
        if (mouseX < popupX || mouseX >= popupX + popupW || mouseY < popupY || mouseY >= popupY + popupH) {
            close();
            return true;
        }
        if (editBox != null) {
            editBox.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open) return false;
        if (keyCode == 256) { // ESC
            close();
            return true;
        }
        if (keyCode == 257 || keyCode == 335) { // Enter or numpad enter
            if (editBox != null) {
                text = editBox.getValue();
            }
            close();
            return true;
        }
        if (editBox != null && editBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return false;
    }

    public boolean charTyped(char c, int modifiers) {
        if (!open || editBox == null) return false;
        return editBox.charTyped(c, modifiers);
    }
}
