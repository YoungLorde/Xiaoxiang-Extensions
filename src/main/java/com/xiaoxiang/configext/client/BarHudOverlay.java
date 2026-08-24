package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Renders the player's health, hunger, and air as bar-style bars
 * (matching the cultivation mod's aesthetic) instead of vanilla hearts/food.
 *
 * The bars are positioned at the bottom-left of the screen, above the hotbar.
 * The air bar only appears when the player is underwater.
 */
public class BarHudOverlay {

    public static final IGuiOverlay OVERLAY = BarHudOverlay::render;

    private static void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (!ExtendedConfig.CLIENT_ENABLE_BAR_HUD.get()) return;
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.player.isCreative()) return; // don't show in creative

        LocalPlayer player = mc.player;
        Font font = mc.font;

        // Position: bottom-left, above the hotbar
        // The hotbar is at y = screenHeight - 22, so we place bars above it
        int barW = Math.max(80, ExtendedConfig.CLIENT_HUD_BAR_WIDTH.get());
        int barH = 6;
        int gap = 3;
        int labelH = 9;

        // Start from bottom up: health, hunger, air (only if underwater)
        int x = 4;
        int bottomY = screenHeight - 36; // above hotbar + experience bar

        // Count how many bars we need to figure out the starting Y
        int barCount = 2; // health + hunger
        boolean isUnderwater = player.getAirSupply() < player.getMaxAirSupply() || player.isUnderWater();
        if (isUnderwater) barCount = 3;

        int totalH = barCount * (labelH + barH + gap) - gap;
        int y = bottomY - totalH;

        // ── Health bar ──
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float displayHealth = Math.max(0, health);
        // Account for absorption hearts
        float absorption = player.getAbsorptionAmount();
        float totalMaxHealth = maxHealth + absorption;

        // Poison effect makes health bar greenish
        boolean poisoned = player.hasEffect(MobEffects.POISON);
        int healthTop = poisoned ? 0xFF40C040 : ExtendedConfig.CLIENT_HEALTH_BAR_TOP_COLOR.get();
        int healthBottom = poisoned ? 0xFF208020 : ExtendedConfig.CLIENT_HEALTH_BAR_BOTTOM_COLOR.get();

        // Wither effect makes it dark
        if (player.hasEffect(MobEffects.WITHER)) {
            healthTop = 0xFF606060;
            healthBottom = 0xFF303030;
        }

        String healthText = (int) displayHealth + "/" + (int) maxHealth;
        if (absorption > 0) healthText += " (+" + (int) absorption + ")";
        g.drawString(font, "\u00A7cHP " + healthText, x, y, 0xFFFFFF);
        y += labelH;
        renderBar(g, x, y, barW, barH, displayHealth / maxHealth, healthTop, healthBottom);
        if (absorption > 0) {
            // Draw absorption portion in gold
            float absorbFrac = absorption / totalMaxHealth;
            int absorbW = (int) (barW * (displayHealth / maxHealth + absorbFrac));
            int healthW = (int) (barW * (displayHealth / maxHealth));
            g.fill(x + healthW, y, x + absorbW, y + barH / 2, 0xFFFFE040);
            g.fill(x + healthW, y + barH / 2, x + absorbW, y + barH, 0xFFC0A020);
        }
        y += barH + gap;

        // ── Hunger bar ──
        FoodData food = player.getFoodData();
        int foodLevel = food.getFoodLevel();
        float saturation = food.getSaturationLevel();
        int maxFood = 20;

        // Hunger effect makes it greenish
        boolean hungry = player.hasEffect(MobEffects.HUNGER);
        int hungerTop = hungry ? 0xFF80C040 : ExtendedConfig.CLIENT_HUNGER_BAR_TOP_COLOR.get();
        int hungerBottom = hungry ? 0xFF406020 : ExtendedConfig.CLIENT_HUNGER_BAR_BOTTOM_COLOR.get();

        String hungerText = foodLevel + "/" + maxFood;
        if (saturation > 0) hungerText += " (S:" + String.format("%.1f", saturation) + ")";
        g.drawString(font, "\u00A76Food " + hungerText, x, y, 0xFFFFFF);
        y += labelH;
        renderBar(g, x, y, barW, barH, (float) foodLevel / maxFood, hungerTop, hungerBottom);
        // Saturation overlay
        if (saturation > 0) {
            int satW = (int) (barW * Math.min(1.0, saturation / maxFood));
            g.fill(x, y, x + satW, y + barH / 2, 0x80FFD040);
            g.fill(x, y + barH / 2, x + satW, y + barH, 0x80C0A020);
        }
        y += barH + gap;

        // ── Air bar (only when underwater) ──
        if (isUnderwater) {
            int air = player.getAirSupply();
            int maxAir = player.getMaxAirSupply();
            // Conduit power gives water breathing
            boolean hasWaterBreathing = player.hasEffect(MobEffects.WATER_BREATHING) ||
                    player.hasEffect(MobEffects.CONDUIT_POWER);

            String airText = air + "/" + maxAir;
            if (hasWaterBreathing) airText += " (Water Breathing)";
            g.drawString(font, "\u00A7bAir " + airText, x, y, 0xFFFFFF);
            y += labelH;
            renderBar(g, x, y, barW, barH, (float) air / maxAir,
                    ExtendedConfig.CLIENT_AIR_BAR_TOP_COLOR.get(),
                    ExtendedConfig.CLIENT_AIR_BAR_BOTTOM_COLOR.get());
        }
    }

    /** Render a single bar with a background, border, and gradient fill. */
    private static void renderBar(GuiGraphics g, int x, int y, int w, int h, float fillFrac, int topColor, int bottomColor) {
        fillFrac = Math.max(0, Math.min(1, fillFrac));
        // Background
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0x80000000);
        // Empty bar
        g.fill(x, y, x + w, y + h, 0x40000000);
        // Fill
        int fillW = (int) (w * fillFrac);
        if (fillW > 0) {
            g.fill(x, y, x + fillW, y + h / 2, topColor);
            g.fill(x, y + h / 2, x + fillW, y + h, bottomColor);
        }
        // Border
        g.renderOutline(x - 1, y - 1, w + 2, h + 2, 0xFF404040);
    }
}
