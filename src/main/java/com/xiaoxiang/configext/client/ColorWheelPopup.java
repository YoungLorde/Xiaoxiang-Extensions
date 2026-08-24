package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * HSV color wheel popup for picking custom colors.
 * Shows a hue/saturation wheel and a value/brightness slider.
 */
public class ColorWheelPopup {
    private boolean open = false;
    private String configPath = "";
    private int currentColor = 0xFFFFFFFF;
    private int wheelX, wheelY;
    private int wheelRadius = 60;
    private int sliderX, sliderY, sliderW, sliderH;
    private boolean draggingWheel = false;
    private boolean draggingSlider = false;

    // HSV values
    private float hue = 0f;
    private float saturation = 1f;
    private float brightness = 1f;

    public void open(String configPath, int currentColor) {
        this.open = true;
        this.configPath = configPath;
        this.currentColor = currentColor;
        // Convert RGB to HSV
        int r = (currentColor >> 16) & 0xFF;
        int g = (currentColor >> 8) & 0xFF;
        int b = currentColor & 0xFF;
        float[] hsv = java.awt.Color.RGBtoHSB(r, g, b, null);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.brightness = hsv[2];
    }

    public void close() {
        open = false;
        draggingWheel = false;
        draggingSlider = false;
    }

    public boolean isOpen() { return open; }

    public void render(GuiGraphics g, int screenWidth, int screenHeight, int mouseX, int mouseY, Font font) {
        if (!open) return;

        int popupW = 180;
        int popupH = 200;
        int px = screenWidth / 2 - popupW / 2;
        int py = screenHeight / 2 - popupH / 2;

        // Background
        g.fill(px, py, px + popupW, py + popupH, 0xE0202020);
        g.renderOutline(px, py, popupW, popupH, 0xFF606080);

        // Title
        g.drawCenteredString(font, "\u00A7e\u00A7lColor Picker", px + popupW / 2, py + 4, 0xFFFFFF);

        // Color wheel
        wheelX = px + popupW / 2;
        wheelY = py + 70;
        drawColorWheel(g, wheelX, wheelY, wheelRadius);

        // Current color indicator on wheel
        float angle = hue * 360f;
        float dist = saturation * wheelRadius;
        int indicatorX = wheelX + (int)(Math.cos(Math.toRadians(angle - 90)) * dist);
        int indicatorY = wheelY + (int)(Math.sin(Math.toRadians(angle - 90)) * dist);
        g.fill(indicatorX - 3, indicatorY - 3, indicatorX + 3, indicatorY + 3, 0xFFFFFFFF);
        g.renderOutline(indicatorX - 3, indicatorY - 3, 6, 6, 0xFF000000);

        // Brightness slider
        sliderX = px + 20;
        sliderY = py + 160;
        sliderW = popupW - 40;
        sliderH = 12;
        drawBrightnessSlider(g, sliderX, sliderY, sliderW, sliderH);

        // Current color preview
        int previewX = px + popupW - 30;
        int previewY = py + 4;
        g.fill(previewX, previewY, previewX + 20, previewY + 12, currentColor | 0xFF000000);
        g.renderOutline(previewX, previewY, 20, 12, 0xFF808080);

        // Hex display
        String hex = String.format("#%06X", currentColor & 0xFFFFFF);
        g.drawCenteredString(font, "\u00A7f" + hex, px + popupW / 2, py + 180, 0xFFFFFF);

        // Close button hint
        g.drawString(font, "\u00A78[Esc to close]", px + 4, py + popupH - 12, 0xFF606060);
    }

    private void drawColorWheel(GuiGraphics g, int cx, int cy, int radius) {
        // Draw the hue/saturation wheel using concentric rings
        for (int r = radius; r > 0; r--) {
            float sat = (float) r / radius;
            for (int a = 0; a < 360; a += 2) {
                float h = a / 360f;
                int color = java.awt.Color.HSBtoRGB(h, sat, brightness);
                int x1 = cx + (int)(Math.cos(Math.toRadians(a - 90)) * (r - 1));
                int y1 = cy + (int)(Math.sin(Math.toRadians(a - 90)) * (r - 1));
                int x2 = cx + (int)(Math.cos(Math.toRadians(a - 90)) * r);
                int y2 = cy + (int)(Math.sin(Math.toRadians(a - 90)) * r);
                g.fill(x1, y1, x2 + 1, y2 + 1, color | 0xFF000000);
            }
        }
        g.renderOutline(cx - radius, cy - radius, radius * 2, radius * 2, 0xFF404060);
    }

    private void drawBrightnessSlider(GuiGraphics g, int x, int y, int w, int h) {
        for (int i = 0; i < w; i++) {
            float b = (float) i / w;
            int color = java.awt.Color.HSBtoRGB(hue, saturation, b);
            g.fill(x + i, y, x + i + 1, y + h, color | 0xFF000000);
        }
        g.renderOutline(x, y, w, h, 0xFF808080);
        // Slider indicator
        int indicatorX = x + (int)(brightness * w);
        g.fill(indicatorX - 1, y - 2, indicatorX + 2, y + h + 2, 0xFFFFFFFF);
        g.renderOutline(indicatorX - 1, y - 2, 3, h + 4, 0xFF000000);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int screenWidth, int screenHeight) {
        if (!open || button != 0) return false;

        int popupW = 180;
        int popupH = 200;
        int px = screenWidth / 2 - popupW / 2;
        int py = screenHeight / 2 - popupH / 2;

        // Click outside the popup closes it. Without this, a click that misses the
        // wheel/slider falls through to "return false" below and the popup silently
        // stays open forever, permanently eating every future click on the screen.
        if (mouseX < px || mouseX >= px + popupW || mouseY < py || mouseY >= py + popupH) {
            close();
            return true;
        }

        // Check wheel click
        double dx = mouseX - wheelX;
        double dy = mouseY - wheelY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= wheelRadius) {
            draggingWheel = true;
            updateFromWheel(mouseX, mouseY);
            return true;
        }

        // Check slider click
        if (mouseX >= sliderX && mouseX < sliderX + sliderW && mouseY >= sliderY && mouseY < sliderY + sliderH) {
            draggingSlider = true;
            updateFromSlider(mouseX);
            return true;
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!open) return false;
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

    public void mouseReleased(double mouseX, double mouseY, int button) {
        draggingWheel = false;
        draggingSlider = false;
    }

    private void updateFromWheel(double mouseX, double mouseY) {
        double dx = mouseX - wheelX;
        double dy = mouseY - wheelY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        hue = (float)(angle / 360.0);
        saturation = (float) Math.min(1.0, dist / wheelRadius);
        updateColor();
    }

    private void updateFromSlider(double mouseX) {
        brightness = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / sliderW));
        updateColor();
    }

    private void updateColor() {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        currentColor = 0xFF000000 | (rgb & 0xFFFFFF);
        // Apply to config
        ConfigValueAccessor.setValueFromString(configPath, String.valueOf(currentColor & 0xFFFFFF));
    }
}
