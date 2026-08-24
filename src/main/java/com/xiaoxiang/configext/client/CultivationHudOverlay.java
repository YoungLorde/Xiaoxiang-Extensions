package com.xiaoxiang.configext.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Renders a compact cultivation info panel at the bottom-left of the screen,
 * above the hotbar, replacing the old custom food/health bar.
 *
 * Shows: Realm, Sub-stage, Qi, Cultivation progress, Spirit Root, Physique.
 * Uses cached reflection for performance — reflection lookups happen once,
 * not every frame.
 */
public class CultivationHudOverlay {

    public static final IGuiOverlay OVERLAY = CultivationHudOverlay::render;

    // ── Cached reflection objects (resolved once on first use) ──
    private static Class<?> cultivationCapabilityClass;
    private static Method capabilityGetMethod;
    private static boolean reflectionInitialized = false;

    // CultivationData field/method caches
    private static Method getRealmMethod;
    private static Method getSubStageMethod;
    private static Method getCurrentQiMethod;
    private static Method getMaxQiMethod;
    private static Method getCultivationProgressMethod;
    private static Method getMaxCultivationMethod;
    private static Method getSpiritRootMethod;
    private static Method getPhysiqueMethod;

    // SpiritRoot/Physique display name methods
    private static Method spiritRootDisplayNameMethod;
    private static Method physiqueDisplayNameMethod;

    private static void initReflection() {
        if (reflectionInitialized) return;
        reflectionInitialized = true;
        try {
            cultivationCapabilityClass = Class.forName("com.xiaoxiang.cultivation.cultivation.CultivationCapability");
            capabilityGetMethod = cultivationCapabilityClass.getMethod("get", net.minecraft.world.entity.player.Player.class);

            Class<?> cultDataClass = Class.forName("com.xiaoxiang.cultivation.cultivation.CultivationData");
            getRealmMethod = cultDataClass.getMethod("getRealm");
            getSubStageMethod = cultDataClass.getMethod("getSubStage");
            getCurrentQiMethod = cultDataClass.getMethod("getCurrentQi");
            getMaxQiMethod = cultDataClass.getMethod("getMaxQi");
            getCultivationProgressMethod = cultDataClass.getMethod("getCultivationProgress");
            getMaxCultivationMethod = cultDataClass.getMethod("getMaxCultivation");
            getSpiritRootMethod = cultDataClass.getMethod("getSpiritRoot");
            getPhysiqueMethod = cultDataClass.getMethod("getPhysique");

            Class<?> spiritRootClass = Class.forName("com.xiaoxiang.cultivation.cultivation.SpiritRoot");
            spiritRootDisplayNameMethod = spiritRootClass.getMethod("id");

            Class<?> physiqueClass = Class.forName("com.xiaoxiang.cultivation.cultivation.Physique");
            physiqueDisplayNameMethod = physiqueClass.getMethod("id");
        } catch (Exception e) {
            // Mod not loaded
        }
    }

    private static void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.player.isCreative()) return;

        initReflection();
        if (capabilityGetMethod == null) return;

        LocalPlayer player = mc.player;
        Font font = mc.font;

        // Get cultivation data via cached reflection
        Object cultData = getCultivationData(player);
        if (cultData == null) return;

        // ── Read all data once per frame ──
        String realmName = safeToString(getRealmMethod, cultData);
        String subStageName = safeToString(getSubStageMethod, cultData);
        long currentQi = safeGetLong(getCurrentQiMethod, cultData);
        long maxQi = safeGetLong(getMaxQiMethod, cultData);
        long cultProgress = safeGetLong(getCultivationProgressMethod, cultData);
        long maxCult = safeGetLong(getMaxCultivationMethod, cultData);

        // Spirit Root and Physique display names
        String spiritRoot = "--";
        String physique = "--";
        try {
            Object root = getSpiritRootMethod.invoke(cultData);
            if (root != null) spiritRoot = (String) spiritRootDisplayNameMethod.invoke(root);
        } catch (Exception e) { }
        try {
            Object phys = getPhysiqueMethod.invoke(cultData);
            if (phys != null) physique = (String) physiqueDisplayNameMethod.invoke(phys);
        } catch (Exception e) { }

        // ── Layout ──
        int panelW = 182;
        int panelH = 52;
        int x = 4;
        int y = screenHeight - 36 - panelH;

        // Background plate
        g.fill(x - 2, y - 2, x + panelW + 2, y + panelH + 2, 0xD0000000);
        g.renderOutline(x - 2, y - 2, panelW + 4, panelH + 4, 0xFF303040);

        int textX = x + 4;
        int textY = y + 3;
        int lineH = 9;

        // ── Realm and Sub-stage ──
        if (realmName == null) realmName = "Unknown";
        if (subStageName == null) subStageName = "";
        String realmDisplay = subStageName.isEmpty() ? realmName : realmName + " (" + subStageName + ")";
        // Truncate if too long
        realmDisplay = truncate(font, realmDisplay, panelW - 8);
        g.drawString(font, "\u00A7b\u00A7l" + realmDisplay, textX, textY, 0xFFFFFF);
        textY += lineH + 1;

        // ── Qi / MaxQi with bar ──
        if (maxQi > 0) {
            String qiText = "\u00A7aQi: \u00A7f" + formatNumber(currentQi) + "/" + formatNumber(maxQi);
            qiText = truncate(font, qiText, panelW - 8);
            g.drawString(font, qiText, textX, textY, 0xFFFFFF);
            int barY = textY + lineH;
            int barW = panelW - 8;
            g.fill(textX, barY, textX + barW, barY + 2, 0x40000000);
            int fillW = (int) (barW * Math.max(0, Math.min(1, (double) currentQi / maxQi)));
            g.fill(textX, barY, textX + fillW, barY + 2, 0xFF40C040);
            textY += lineH + 3;
        } else {
            g.drawString(font, "\u00A7aQi: \u00A7f--", textX, textY, 0xFFFFFF);
            textY += lineH + 1;
        }

        // ── Cultivation Progress with bar ──
        if (maxCult > 0) {
            String cultText = "\u00A76Prog: \u00A7f" + formatNumber(cultProgress) + "/" + formatNumber(maxCult);
            cultText = truncate(font, cultText, panelW - 8);
            g.drawString(font, cultText, textX, textY, 0xFFFFFF);
            int barY = textY + lineH;
            int barW = panelW - 8;
            g.fill(textX, barY, textX + barW, barY + 2, 0x40000000);
            int fillW = (int) (barW * Math.max(0, Math.min(1, (double) cultProgress / maxCult)));
            g.fill(textX, barY, textX + fillW, barY + 2, 0xFFC0A020);
            textY += lineH + 3;
        } else {
            g.drawString(font, "\u00A76Prog: \u00A7f--", textX, textY, 0xFFFFFF);
            textY += lineH + 1;
        }

        // ── Spirit Root & Physique on separate lines, truncated ──
        String rootLine = "\u00A7dRoot: \u00A7f" + truncate(font, spiritRoot, panelW - 8 - font.width("\u00A7dRoot: "));
        g.drawString(font, rootLine, textX, textY, 0xFFFFFF);
        textY += lineH;
        String bodyLine = "\u00A7eBody: \u00A7f" + truncate(font, physique, panelW - 8 - font.width("\u00A7eBody: "));
        g.drawString(font, bodyLine, textX, textY, 0xFFFFFF);
    }

    // ── Helpers ──

    private static String truncate(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text;
        while (font.width(text + "...") > maxWidth && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
        return text + "...";
    }

    private static Object getCultivationData(LocalPlayer player) {
        try {
            java.util.Optional<?> opt = (java.util.Optional<?>) capabilityGetMethod.invoke(null, player);
            return opt.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static String safeToString(Method m, Object obj) {
        if (m == null || obj == null) return null;
        try {
            Object result = m.invoke(obj);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static long safeGetLong(Method m, Object obj) {
        if (m == null || obj == null) return 0;
        try {
            Object result = m.invoke(obj);
            if (result instanceof Long) return (Long) result;
            if (result instanceof Integer) return (Integer) result;
        } catch (Exception e) { }
        return 0;
    }

    private static String formatNumber(long n) {
        if (n >= 1_000_000_000) return String.format("%.1fB", n / 1_000_000_000.0);
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000) return String.format("%.1fK", n / 1_000.0);
        return String.valueOf(n);
    }
}
