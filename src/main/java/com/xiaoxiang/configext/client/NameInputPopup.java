package com.xiaoxiang.configext.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/**
 * A text input popup for entering a custom identity name and description.
 * Renders on top of everything with a dark overlay.
 * When confirmed, calls the provided callback with the entered name and description.
 *
 * Features:
 * - Two fields: Name and Description (Tab to switch)
 * - Cursor positioning with mouse click
 * - Arrow keys to move cursor
 * - Ctrl+A to select all (then Backspace to delete all)
 * - Ctrl+C/V to copy/paste (via clipboard)
 * - Home/End to jump to start/end
 */
public class NameInputPopup {

    private boolean open = false;
    private String text = "";
    private String description = "";
    private String prompt = "Enter name:";
    private String baseIdentityId = "";
    private String startingItems = "";
    private String baseDescription = "";
    private int minLifespan = 100;
    private int maxLifespan = 120;
    private NameConfirmCallback callback;
    private int popupX, popupY, popupW, popupH;
    private int cursorBlink = 0;
    private boolean editingDescription = false; // false = editing name, true = editing description
    private int nameCursorPos = 0;   // cursor position in name field
    private int descCursorPos = 0;   // cursor position in description field
    private int nameSelectionStart = -1; // -1 = no selection
    private int descSelectionStart = -1;

    public interface NameConfirmCallback {
        void onConfirm(String name, String description);
    }

    public boolean isOpen() { return open; }

    public void open(String baseIdentityId, String baseDisplayName, String baseDescription, String startingItems,
                     int minLifespan, int maxLifespan, NameConfirmCallback callback) {
        this.open = true;
        this.baseIdentityId = baseIdentityId;
        this.baseDescription = baseDescription != null ? baseDescription : "";
        this.startingItems = startingItems;
        this.minLifespan = minLifespan;
        this.maxLifespan = maxLifespan;
        this.callback = callback;
        this.text = baseDisplayName + " Copy";
        this.description = this.baseDescription;
        this.prompt = "Enter name for duplicated identity:";
        this.popupW = 360;
        this.popupH = 160;
        this.editingDescription = false;
        this.nameCursorPos = this.text.length();
        this.descCursorPos = this.description.length();
        this.nameSelectionStart = -1;
        this.descSelectionStart = -1;
    }

    public void close() {
        this.open = false;
    }

    public void updatePosition(int screenWidth, int screenHeight) {
        this.popupX = (screenWidth - popupW) / 2;
        this.popupY = (screenHeight - popupH) / 2;
    }

    private String getActiveText() {
        return editingDescription ? description : text;
    }

    private void setActiveText(String value) {
        if (editingDescription) description = value;
        else text = value;
    }

    private int getActiveCursor() {
        return editingDescription ? descCursorPos : nameCursorPos;
    }

    private void setActiveCursor(int pos) {
        if (editingDescription) descCursorPos = Math.max(0, Math.min(pos, description.length()));
        else nameCursorPos = Math.max(0, Math.min(pos, text.length()));
    }

    private int getActiveSelectionStart() {
        return editingDescription ? descSelectionStart : nameSelectionStart;
    }

    private void setActiveSelectionStart(int start) {
        if (editingDescription) descSelectionStart = start;
        else nameSelectionStart = start;
    }

    private void clearActiveSelection() {
        setActiveSelectionStart(-1);
    }

    /** Delete selected text (if any) and return true if something was deleted. */
    private boolean deleteSelection() {
        int selStart = getActiveSelectionStart();
        if (selStart < 0) return false;
        String current = getActiveText();
        int cursor = getActiveCursor();
        int min = Math.min(selStart, cursor);
        int max = Math.max(selStart, cursor);
        if (min == max) {
            clearActiveSelection();
            return false;
        }
        setActiveText(current.substring(0, min) + current.substring(max));
        setActiveCursor(min);
        clearActiveSelection();
        return true;
    }

    /** Insert text at cursor position, replacing any selection. */
    private void insertAtCursor(String inserted) {
        deleteSelection();
        String current = getActiveText();
        int cursor = getActiveCursor();
        int maxLen = editingDescription ? 200 : 40;
        // Truncate inserted text if it would exceed max length
        int remaining = maxLen - current.length();
        if (remaining <= 0) return;
        if (inserted.length() > remaining) inserted = inserted.substring(0, remaining);
        setActiveText(current.substring(0, cursor) + inserted + current.substring(cursor));
        setActiveCursor(cursor + inserted.length());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open) return false;

        // Enter = confirm
        if (keyCode == 257 || keyCode == 335) {
            confirmName();
            return true;
        }
        // Escape = cancel
        if (keyCode == 256) {
            close();
            return true;
        }
        // Tab = switch between name and description fields
        if (keyCode == 258) {
            editingDescription = !editingDescription;
            clearActiveSelection();
            return true;
        }

        boolean ctrl = Screen.hasControlDown();

        // Ctrl+A = select all
        if (ctrl && (keyCode == 65)) { // A key
            String current = getActiveText();
            if (!current.isEmpty()) {
                setActiveSelectionStart(0);
                setActiveCursor(current.length());
            }
            return true;
        }

        // Ctrl+C = copy selected text to clipboard
        if (ctrl && (keyCode == 67)) { // C key
            int selStart = getActiveSelectionStart();
            if (selStart >= 0) {
                String current = getActiveText();
                int cursor = getActiveCursor();
                int min = Math.min(selStart, cursor);
                int max = Math.max(selStart, cursor);
                if (min < max) {
                    String selected = current.substring(min, max);
                    try {
                        java.awt.Toolkit.getDefaultToolkit()
                                .getSystemClipboard()
                                .setContents(new java.awt.datatransfer.StringSelection(selected), null);
                    } catch (Exception e) { /* ignore */ }
                }
            }
            return true;
        }

        // Ctrl+V = paste from clipboard
        if (ctrl && (keyCode == 86)) { // V key
            try {
                java.awt.datatransfer.Transferable t = java.awt.Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getContents(null);
                if (t != null && t.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                    String pasted = (String) t.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                    if (pasted != null) {
                        // Remove newlines from pasted text
                        pasted = pasted.replaceAll("[\\r\\n]", " ");
                        insertAtCursor(pasted);
                    }
                }
            } catch (Exception e) { /* ignore */ }
            return true;
        }

        // Ctrl+X = cut (copy + delete selection)
        if (ctrl && (keyCode == 88)) { // X key
            int selStart = getActiveSelectionStart();
            if (selStart >= 0) {
                String current = getActiveText();
                int cursor = getActiveCursor();
                int min = Math.min(selStart, cursor);
                int max = Math.max(selStart, cursor);
                if (min < max) {
                    String selected = current.substring(min, max);
                    try {
                        java.awt.Toolkit.getDefaultToolkit()
                                .getSystemClipboard()
                                .setContents(new java.awt.datatransfer.StringSelection(selected), null);
                    } catch (Exception e) { /* ignore */ }
                    deleteSelection();
                }
            }
            return true;
        }

        // Backspace
        if (keyCode == 259) {
            if (!deleteSelection()) {
                int cursor = getActiveCursor();
                if (cursor > 0) {
                    String current = getActiveText();
                    setActiveText(current.substring(0, cursor - 1) + current.substring(cursor));
                    setActiveCursor(cursor - 1);
                }
            }
            clearActiveSelection();
            return true;
        }

        // Delete key (forward delete)
        if (keyCode == 261) {
            if (!deleteSelection()) {
                int cursor = getActiveCursor();
                String current = getActiveText();
                if (cursor < current.length()) {
                    setActiveText(current.substring(0, cursor) + current.substring(cursor + 1));
                }
            }
            clearActiveSelection();
            return true;
        }

        // Left arrow
        if (keyCode == 263) {
            int cursor = getActiveCursor();
            if (ctrl) {
                // Ctrl+Left = jump to previous word
                String current = getActiveText();
                int pos = cursor - 1;
                while (pos > 0 && current.charAt(pos - 1) == ' ') pos--;
                while (pos > 0 && current.charAt(pos - 1) != ' ') pos--;
                setActiveCursor(pos);
            } else {
                setActiveCursor(cursor - 1);
            }
            if (!Screen.hasShiftDown()) clearActiveSelection();
            else if (getActiveSelectionStart() < 0) setActiveSelectionStart(cursor);
            return true;
        }

        // Right arrow
        if (keyCode == 262) {
            int cursor = getActiveCursor();
            if (ctrl) {
                // Ctrl+Right = jump to next word
                String current = getActiveText();
                int pos = cursor;
                while (pos < current.length() && current.charAt(pos) != ' ') pos++;
                while (pos < current.length() && current.charAt(pos) == ' ') pos++;
                setActiveCursor(pos);
            } else {
                setActiveCursor(cursor + 1);
            }
            if (!Screen.hasShiftDown()) clearActiveSelection();
            else if (getActiveSelectionStart() < 0) setActiveSelectionStart(cursor);
            return true;
        }

        // Home = jump to start
        if (keyCode == 268) {
            int cursor = getActiveCursor();
            setActiveCursor(0);
            if (!Screen.hasShiftDown()) clearActiveSelection();
            else if (getActiveSelectionStart() < 0) setActiveSelectionStart(cursor);
            return true;
        }

        // End = jump to end
        if (keyCode == 269) {
            int cursor = getActiveCursor();
            setActiveCursor(getActiveText().length());
            if (!Screen.hasShiftDown()) clearActiveSelection();
            else if (getActiveSelectionStart() < 0) setActiveSelectionStart(cursor);
            return true;
        }

        return false;
    }

    public boolean charTyped(char c, int modifiers) {
        if (!open) return false;
        if (c >= 32 && c <= 126) {
            insertAtCursor(String.valueOf(c));
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!open || button != 0) return false;

        // Click outside the popup closes it. Without this, a click that misses every
        // field/button below falls through to "return true // Consume all clicks inside
        // popup" and the popup silently stays open forever, permanently eating every
        // future click on the whole screen (including the top-level tab bar).
        if (mouseX < popupX || mouseX >= popupX + popupW || mouseY < popupY || mouseY >= popupY + popupH) {
            close();
            return true;
        }

        int inputX = popupX + 10;
        int inputW = popupW - 20;

        // Name field
        int nameY = popupY + 24;
        int nameH = 18;
        if (mouseX >= inputX && mouseX < inputX + inputW &&
            mouseY >= nameY && mouseY < nameY + nameH) {
            editingDescription = false;
            clearActiveSelection();
            // Position cursor based on click X
            nameCursorPos = getCursorPosFromX(text, (int)mouseX - inputX - 3, Minecraft.getInstance().font);
            return true;
        }

        // Description field
        int descY = popupY + 64;
        int descH = 40;
        if (mouseX >= inputX && mouseX < inputX + inputW &&
            mouseY >= descY && mouseY < descY + descH) {
            editingDescription = true;
            clearActiveSelection();
            // For multi-line, approximate cursor position based on click
            descCursorPos = getCursorPosFromClickInWrappedText(description, (int)mouseX - inputX - 3,
                    (int)mouseY - descY - 3, inputW - 6, descH - 6, Minecraft.getInstance().font);
            return true;
        }

        // Confirm button
        int confirmW = 80, confirmH = 18;
        int confirmX = popupX + popupW / 2 - confirmW - 6;
        int confirmY = popupY + popupH - 28;
        if (mouseX >= confirmX && mouseX < confirmX + confirmW &&
            mouseY >= confirmY && mouseY < confirmY + confirmH) {
            confirmName();
            return true;
        }

        // Cancel button
        int cancelX = popupX + popupW / 2 + 6;
        int cancelW = 80;
        if (mouseX >= cancelX && mouseX < cancelX + cancelW &&
            mouseY >= confirmY && mouseY < confirmY + confirmH) {
            close();
            return true;
        }

        return true; // Consume all clicks inside popup
    }

    /** Get cursor position from a click X coordinate in a single-line text. */
    private int getCursorPosFromX(String text, int clickX, Font font) {
        if (clickX <= 0) return 0;
        int bestPos = 0;
        int bestDist = Math.abs(clickX);
        for (int i = 0; i <= text.length(); i++) {
            int w = font.width(text.substring(0, i));
            int dist = Math.abs(w - clickX);
            if (dist < bestDist) {
                bestDist = dist;
                bestPos = i;
            }
        }
        return bestPos;
    }

    /** Get cursor position from a click in wrapped (multi-line) text. */
    private int getCursorPosFromClickInWrappedText(String text, int clickX, int clickY,
                                                     int maxWidth, int maxHeight, Font font) {
        int lineH = font.lineHeight + 1;
        int targetLine = clickY / lineH;
        if (targetLine < 0) targetLine = 0;

        // Word-wrap to find which line was clicked
        String[] words = text.split(" ", -1);
        StringBuilder line = new StringBuilder();
        int lineNum = 0;
        int charCount = 0; // characters consumed before current line

        for (int wi = 0; wi < words.length; wi++) {
            String word = words[wi];
            String testLine = line.isEmpty() ? word : line + " " + word;
            if (font.width(testLine) > maxWidth && !line.isEmpty()) {
                if (lineNum == targetLine) {
                    // Click is on this line - find position within it
                    return charCount + getCursorPosFromX(line.toString(), clickX, font);
                }
                charCount += line.length() + 1; // +1 for the space
                lineNum++;
                if (lineNum * lineH >= maxHeight) return text.length();
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        // Last line
        if (lineNum == targetLine) {
            return charCount + getCursorPosFromX(line.toString(), clickX, font);
        }
        return text.length();
    }

    private void confirmName() {
        String name = text.trim();
        if (name.isEmpty()) name = "Custom Identity";
        String desc = description.trim();
        if (desc.isEmpty()) desc = baseDescription;
        if (callback != null) {
            callback.onConfirm(name, desc);
        }
        close();
    }

    public void render(GuiGraphics g, Font font, int mouseX, int mouseY, Screen screen) {
        if (!open) return;

        updatePosition(screen.width, screen.height);

        // Dark overlay
        g.fill(0, 0, screen.width, screen.height, 0x80000000);

        // Popup background
        g.fill(popupX, popupY, popupX + popupW, popupY + popupH, 0xFF1A1A2E);
        g.renderOutline(popupX, popupY, popupW, popupH, 0xFF6A5ACD);

        // Title
        g.drawCenteredString(font, prompt, popupX + popupW / 2, popupY + 8, 0xFFFFFF);

        // Name input field
        int inputX = popupX + 10;
        int inputY = popupY + 24;
        int inputW = popupW - 20;
        int inputH = 18;
        boolean nameHover = mouseX >= inputX && mouseX < inputX + inputW &&
                mouseY >= inputY && mouseY < inputY + inputH;
        g.fill(inputX, inputY, inputX + inputW, inputY + inputH, 0xFF0D0D1A);
        g.renderOutline(inputX, inputY, inputW, inputH,
                !editingDescription ? 0xFF8080FF : (nameHover ? 0xFF404060 : 0xFF303040));

        // Name label
        g.drawString(font, "\u00A7bName" + (!editingDescription ? " (editing)" : " (click to edit)"),
                inputX, inputY - 10, 0xFFFFFF);

        // Render name text with cursor and selection
        cursorBlink++;
        renderTextField(g, font, text, nameCursorPos, nameSelectionStart,
                inputX + 3, inputY + 5, inputW - 6, inputH - 6, !editingDescription, false);

        // Description input field
        int descY = popupY + 64;
        int descH = 40;
        boolean descHover = mouseX >= inputX && mouseX < inputX + inputW &&
                mouseY >= descY && mouseY < descY + descH;
        g.fill(inputX, descY, inputX + inputW, descY + descH, 0xFF0D0D1A);
        g.renderOutline(inputX, descY, inputW, descH,
                editingDescription ? 0xFF8080FF : (descHover ? 0xFF404060 : 0xFF303040));

        // Description label
        g.drawString(font, "\u00A7bDescription" + (editingDescription ? " (editing)" : " (click to edit)"),
                inputX, descY - 10, 0xFFFFFF);

        // Render description text with cursor and selection (wrapped)
        renderTextField(g, font, description, descCursorPos, descSelectionStart,
                inputX + 3, descY + 3, inputW - 6, descH - 6, editingDescription, true);

        // Confirm + Cancel buttons
        int confirmW = 80, confirmH = 18;
        int confirmX = popupX + popupW / 2 - confirmW - 6;
        int confirmY = popupY + popupH - 28;
        int cancelX = popupX + popupW / 2 + 6;
        int cancelW = 80;

        boolean confirmHover = mouseX >= confirmX && mouseX < confirmX + confirmW &&
                mouseY >= confirmY && mouseY < confirmY + confirmH;
        boolean cancelHover = mouseX >= cancelX && mouseX < cancelX + cancelW &&
                mouseY >= confirmY && mouseY < confirmY + confirmH;

        g.fill(confirmX, confirmY, confirmX + confirmW, confirmY + confirmH,
                confirmHover ? 0xFF204020 : 0xFF102018);
        g.renderOutline(confirmX, confirmY, confirmW, confirmH,
                confirmHover ? 0xFF40A040 : 0xFF306030);
        g.drawCenteredString(font, "\u00A7aConfirm", confirmX + confirmW / 2, confirmY + 5, 0xFFFFFF);

        g.fill(cancelX, confirmY, cancelX + cancelW, confirmY + confirmH,
                cancelHover ? 0xFF402020 : 0xFF201010);
        g.renderOutline(cancelX, confirmY, cancelW, confirmH,
                cancelHover ? 0xFFA04040 : 0xFF603030);
        g.drawCenteredString(font, "\u00A7cCancel", cancelX + cancelW / 2, confirmY + 5, 0xFFFFFF);
    }

    /** Render a text field with cursor and optional selection highlighting. */
    private void renderTextField(GuiGraphics g, Font font, String text, int cursorPos,
                                  int selectionStart, int x, int y, int w, int h,
                                  boolean active, boolean wrap) {
        if (wrap) {
            renderWrappedWithCursor(g, font, text, cursorPos, selectionStart, x, y, w, h, active);
        } else {
            renderSingleLineWithCursor(g, font, text, cursorPos, selectionStart, x, y, w, active);
        }
    }

    private void renderSingleLineWithCursor(GuiGraphics g, Font font, String text, int cursorPos,
                                             int selectionStart, int x, int y, int w, boolean active) {
        // Draw selection highlight
        if (selectionStart >= 0 && selectionStart != cursorPos) {
            int min = Math.min(selectionStart, cursorPos);
            int max = Math.max(selectionStart, cursorPos);
            int selX1 = x + font.width(text.substring(0, min));
            int selX2 = x + font.width(text.substring(0, max));
            g.fill(selX1, y - 1, selX2, y + font.lineHeight + 1, 0xFF4040A0);
        }

        // Draw text
        g.drawString(font, text, x, y, 0xFFFFFF);

        // Draw cursor
        if (active && (cursorBlink / 20) % 2 == 0) {
            int cursorX = x + font.width(text.substring(0, cursorPos));
            g.fill(cursorX, y - 1, cursorX + 1, y + font.lineHeight + 1, 0xFFFFFFFF);
        }
    }

    private void renderWrappedWithCursor(GuiGraphics g, Font font, String text, int cursorPos,
                                          int selectionStart, int x, int y, int w, int h, boolean active) {
        int lineH = font.lineHeight + 1;
        // Word-wrap
        String[] words = text.split(" ", -1);
        StringBuilder line = new StringBuilder();
        int lineY = y;
        int lineStartChar = 0; // char offset of start of current line

        for (int wi = 0; wi < words.length; wi++) {
            String word = words[wi];
            String testLine = line.isEmpty() ? word : line + " " + word;
            if (font.width(testLine) > w && !line.isEmpty()) {
                // Draw current line
                drawLineWithCursor(g, font, line.toString(), lineStartChar, cursorPos, selectionStart,
                        x, lineY, active);
                lineStartChar += line.length() + 1; // +1 for space
                lineY += lineH;
                if (lineY + lineH > y + h) return;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        // Draw remaining line
        if (lineY + lineH <= y + h) {
            drawLineWithCursor(g, font, line.toString(), lineStartChar, cursorPos, selectionStart,
                    x, lineY, active);
        }
    }

    private void drawLineWithCursor(GuiGraphics g, Font font, String lineText, int lineStartChar,
                                     int cursorPos, int selectionStart, int x, int y, boolean active) {
        int lineEndChar = lineStartChar + lineText.length();

        // Draw selection highlight on this line
        if (selectionStart >= 0 && selectionStart != cursorPos) {
            int selMin = Math.min(selectionStart, cursorPos);
            int selMax = Math.max(selectionStart, cursorPos);
            if (selMin < lineEndChar && selMax > lineStartChar) {
                int localSelStart = Math.max(0, selMin - lineStartChar);
                int localSelEnd = Math.min(lineText.length(), selMax - lineStartChar);
                int selX1 = x + font.width(lineText.substring(0, localSelStart));
                int selX2 = x + font.width(lineText.substring(0, localSelEnd));
                g.fill(selX1, y - 1, selX2, y + font.lineHeight + 1, 0xFF4040A0);
            }
        }

        // Draw text
        g.drawString(font, lineText, x, y, 0xFFFFFF);

        // Draw cursor on this line
        if (active && (cursorBlink / 20) % 2 == 0) {
            if (cursorPos >= lineStartChar && cursorPos <= lineEndChar) {
                int localCursor = cursorPos - lineStartChar;
                int cursorX = x + font.width(lineText.substring(0, localCursor));
                g.fill(cursorX, y - 1, cursorX + 1, y + font.lineHeight + 1, 0xFFFFFFFF);
            }
        }
    }
}
