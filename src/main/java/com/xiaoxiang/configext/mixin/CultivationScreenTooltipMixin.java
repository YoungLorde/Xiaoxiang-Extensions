package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.client.CustomIdentityManager;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.client.screen.CultivationScreen;
import com.xiaoxiang.cultivation.cultivation.CultivationCapability;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.cultivation.Identity;
import com.xiaoxiang.cultivation.cultivation.sect.SectRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds hover tooltips to the character info fields on the Cultivation panel (G key).
 *
 * The original mod (CultivationScreen.render -> m_88315_) already renders tooltips for a
 * handful of hot spots, verified by reading the compiled bytecode of
 * com/xiaoxiang/cultivation/client/screen/CultivationScreen.class:
 *
 *   - genderCellRect        -> "attr.id.gender" + "gender.edit_hint"
 *   - identityRowRect       -> identity name + description, but ONLY when
 *                              data.hasChosenIdentity() || data.isSoulReaperIdentity()
 *   - spiritRootRowRect     -> spirit root name + rarity + tooltipKey()
 *   - physiqueRowRect       -> physique name + rarity + tooltipKey()
 *   - sectEntryArrowRect    -> "open sect" hint
 *   - zhenyuanLabelRects[]  -> attribute point tooltips (Attributes tab)
 *
 * Nothing is drawn for Name, Race, Realm, Morality, Bone Age, Lifespan, or the three
 * status bars (HP / Cultivation / Qi). This mixin fills those gaps, plus supplies a
 * fallback description for the Identity row while no identity has been chosen yet.
 *
 * LAYOUT (all values traced out of renderLeftPanel's bytecode, offsets 0..1170):
 *
 *   guiLeft L = (screenWidth  - 320) / 2      (render(), sipush 320)
 *   guiTop  T = (screenHeight - 200) / 2      (render(), sipush 200)
 *
 *   halfW   = 160                     (local 8)
 *   col gap = 6                       (local 15)
 *   colW    = (160 - 24 - 6) / 2 = 65 (local 16)
 *   col1    = [L+12, L+77]            (locals 17, 18)
 *   col2    = [L+83, L+148]           (locals 19, 20)
 *   rowY    = T+76, stepping by 10    (locals 21, 22)
 *
 *   T+76   Name          | Gender
 *   T+86   Race          | Realm
 *   T+96   Identity      | Spirit Root
 *   T+106  Physique      | Morality
 *   T+116  Bone Age      | Lifespan
 *   (rowY += 10, then += 3)
 *   T+129  HP bar          x = L+12, width = 160-24 = 136  -> right edge L+148
 *   T+140  Cultivation bar (rowY += 11)
 *   T+151  Qi bar         (rowY += 11)
 *
 * These match the rects the original mod stores itself: nameCellRect / genderCellRect are
 * [col, rowY-1, col, rowY+9] and identityRowRect / spiritRootRowRect / physiqueRowRect are
 * [col, rowY, col, rowY+10] at exactly the rows above, which is how the layout was
 * cross-checked.
 *
 * Injected at TAIL of render so the tooltip paints over the panel. The original mod draws
 * its popups (bonus settings, breakthrough history, dropdowns) after its own tooltips, so
 * this mixin suppresses itself whenever a popup is open to avoid painting on top of one.
 *
 * Guarded by ExtendedConfig.CLIENT_ENABLE_CULTIVATION_PANEL_TOOLTIPS.
 *
 * ── Custom identity name/description fix (configExt$formatCustomIdentityValue /
 * configExt$redirectIdentityDescriptionKey below) ──
 *
 * Separately from the tooltip gap-filling above: once an identity IS chosen, this
 * screen's Identity row (both its plain field text AND the first line of its own
 * tooltip) is built by the original mod's own private formatIdentityValue(CultivationData)
 * method, and the tooltip's second line (the description) is built inline in render()
 * (SRG m_88315_) via Identity.byId(data.getIdentityId()).descriptionKey(). Both go
 * through Identity.byId(String) - bytecode-confirmed (javap on Identity.class) to NEVER
 * return null, falling back to LONE_CULTIVATOR for any unrecognized id string, including
 * every "custom_..." id a duplicated identity ever gets. Verified end-to-end against the
 * actual installed jar's lang file (assets/xiaoxiang_cultivation/lang/en_us.json):
 * "identity.xiaoxiang_cultivation.lone_cultivator" = "Fallen Rogue Cultivator", with a
 * description reading "Already touched cultivation, but lacks resources..." - i.e. this
 * screen was showing that exact fixed name+description for EVERY custom identity, no
 * matter which one was actually chosen or what the player named/described it as. This is
 * the root cause of the reported "it's giving me the identity of the original duplicate
 * I made" - it isn't actually re-showing a specific old duplicate, it's always showing
 * Lone Cultivator's real flavor text, unconditionally, for any custom id.
 */
@Mixin(CultivationScreen.class)
public abstract class CultivationScreenTooltipMixin {

    // Column bounds, relative to guiLeft.
    private static final int CE_COL1_X1 = 12;
    private static final int CE_COL1_X2 = 77;
    private static final int CE_COL2_X1 = 83;
    private static final int CE_COL2_X2 = 148;

    // Row tops, relative to guiTop.
    private static final int CE_ROW_NAME = 76;
    private static final int CE_ROW_RACE = 86;
    private static final int CE_ROW_IDENTITY = 96;
    private static final int CE_ROW_PHYSIQUE = 106;
    private static final int CE_ROW_BONE_AGE = 116;
    private static final int CE_ROW_H = 10;

    // Status bars, relative to guiLeft / guiTop.
    private static final int CE_BAR_X1 = 12;
    private static final int CE_BAR_X2 = 148;
    private static final int CE_BAR_HP = 129;
    private static final int CE_BAR_CULT = 140;
    private static final int CE_BAR_QI = 151;
    private static final int CE_BAR_H = 10;

    private static final int CE_WRAP_WIDTH = 170;

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void configExt$renderFieldTooltips(GuiGraphics gfx, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        try {
            if (!ExtendedConfig.CLIENT_ENABLE_CULTIVATION_PANEL_TOOLTIPS.get()) return;

            CultivationScreen self = (CultivationScreen) (Object) this;
            net.minecraft.client.gui.screens.Screen screen = (net.minecraft.client.gui.screens.Screen) (Object) self;

            Minecraft mc = Minecraft.getInstance();
            if (mc == null) return;
            LocalPlayer player = mc.player;
            if (player == null) return;
            Font font = mc.font;
            if (font == null) return;

            // Do not paint over the mod's own popups (they render after its tooltips).
            if (configExt$popupOpen(self)) return;

            int left = (screen.width - 320) / 2;
            int top = (screen.height - 200) / 2;

            String titleKey = null;
            String body = null;

            // ---- Left column ----
            if (configExt$in(mouseX, mouseY, left + CE_COL1_X1, top + CE_ROW_NAME, left + CE_COL1_X2, top + CE_ROW_NAME + CE_ROW_H)) {
                if (!configExt$isEditingName(self)) {
                    titleKey = "Name";
                    body = "The name your cultivator is known by. It appears on the panel and to other players. Click this cell to rename yourself.";
                }
            } else if (configExt$in(mouseX, mouseY, left + CE_COL1_X1, top + CE_ROW_RACE, left + CE_COL1_X2, top + CE_ROW_RACE + CE_ROW_H)) {
                titleKey = "Race";
                body = "The bodily form you currently inhabit. If your body is destroyed at a high enough realm you drop into Soul State, "
                        + "surviving without a body until you can seize or rebuild one.";
            } else if (configExt$in(mouseX, mouseY, left + CE_COL1_X1, top + CE_ROW_PHYSIQUE, left + CE_COL1_X2, top + CE_ROW_PHYSIQUE + CE_ROW_H)) {
                // The original mod already draws a full physique tooltip here; nothing to add.
                return;
            } else if (configExt$in(mouseX, mouseY, left + CE_COL1_X1, top + CE_ROW_BONE_AGE, left + CE_COL1_X2, top + CE_ROW_BONE_AGE + CE_ROW_H)) {
                titleKey = "Bone Age";
                body = "How many years your cultivator has lived. It climbs as time passes and is measured against your lifespan cap - "
                        + "reach the cap and you die of old age.";
            } else if (configExt$in(mouseX, mouseY, left + CE_COL1_X1, top + CE_ROW_IDENTITY, left + CE_COL1_X2, top + CE_ROW_IDENTITY + CE_ROW_H)) {
                // The original only draws an identity tooltip once an identity exists. Fill the gap.
                CultivationData data = configExt$data(player);
                if (data == null) return;
                if (data.hasChosenIdentity() || data.isSoulReaperIdentity() || data.hasSectDisplay()) return;
                titleKey = "Identity";
                body = "The cultivator's background: martial, scholar, cultivator, or abandoned. Affects starting stats. "
                        + "Draw an identity to settle your origin.";
            }

            // ---- Right column ----
            else if (configExt$in(mouseX, mouseY, left + CE_COL2_X1, top + CE_ROW_RACE, left + CE_COL2_X2, top + CE_ROW_RACE + CE_ROW_H)) {
                titleKey = "Realm";
                body = "Your overall stage of power (Qi Refining, Foundation Building, Golden Core, Nascent Soul, and so on). "
                        + "Advancing a realm requires a breakthrough, and each realm raises your qi capacity and lifespan.";
            } else if (configExt$in(mouseX, mouseY, left + CE_COL2_X1, top + CE_ROW_PHYSIQUE, left + CE_COL2_X2, top + CE_ROW_PHYSIQUE + CE_ROW_H)) {
                titleKey = "Morality";
                body = "A measure of your alignment: righteous, neutral, or evil. It shifts with your deeds and affects "
                        + "tribulation difficulty and how NPCs treat you.";
            } else if (configExt$in(mouseX, mouseY, left + CE_COL2_X1, top + CE_ROW_BONE_AGE, left + CE_COL2_X2, top + CE_ROW_BONE_AGE + CE_ROW_H)) {
                titleKey = "Lifespan";
                body = "How long you can live. It increases dramatically with each realm breakthrough; when your bone age reaches it, you die.";
            }

            // ---- Status bars ----
            else if (configExt$in(mouseX, mouseY, left + CE_BAR_X1, top + CE_BAR_HP, left + CE_BAR_X2, top + CE_BAR_HP + CE_BAR_H)) {
                titleKey = "Health";
                body = "Your body's vitality. Reaching zero kills you (or forces you into Soul State at high realms). "
                        + "Higher realms and stronger physiques raise your maximum health.";
            } else if (configExt$in(mouseX, mouseY, left + CE_BAR_X1, top + CE_BAR_CULT, left + CE_BAR_X2, top + CE_BAR_CULT + CE_BAR_H)) {
                titleKey = "Cultivation";
                body = "Progress toward your next breakthrough. It fills as you meditate and absorb ambient qi. "
                        + "Once it is full you can attempt to advance to the next realm or sub-stage.";
            } else if (configExt$in(mouseX, mouseY, left + CE_BAR_X1, top + CE_BAR_QI, left + CE_BAR_X2, top + CE_BAR_QI + CE_BAR_H)) {
                titleKey = "Qi";
                body = "The fundamental energy of the universe, stored in your dantian. Spells and techniques spend it, "
                        + "and it regenerates over time - faster while meditating or in qi-rich terrain.";
            }

            if (titleKey == null || body == null) return;

            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal(titleKey).withStyle(ChatFormatting.GOLD));
            configExt$wrap(font, body, CE_WRAP_WIDTH, lines);
            gfx.renderComponentTooltip(font, lines, mouseX, mouseY);
        } catch (Throwable ignored) {
            // Tooltips are purely cosmetic - never break the screen over them.
        }
    }

    /**
     * Overrides formatIdentityValue's result (the Identity row's field text, and the
     * first line of its tooltip - both draw through this one method, verified via
     * javap: render() calls formatIdentityValue(data) at the exact bytecode offset that
     * builds the tooltip's name line) for a custom identity, substituting the custom
     * identity's own display name in place of whatever Identity.byId's LONE_CULTIVATOR
     * fallback would otherwise show. Every guard here mirrors a branch the ORIGINAL
     * method itself checks first (soul reaper display, sect role display, no identity
     * chosen yet) so this never fires when one of those should legitimately take
     * priority - it only replaces the final "show the origin identity's real name"
     * fallthrough case.
     */
    @Inject(method = "formatIdentityValue", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$formatCustomIdentityValue(CultivationData data, CallbackInfoReturnable<Component> cir) {
        try {
            if (data == null) return;
            if (!data.hasChosenIdentity()) return; // original shows "?" - untouched
            if (data.isSoulReaperIdentity()) return; // soul reaper display takes priority - untouched
            if (data.hasSectDisplay()) {
                SectRole role = SectRole.byId(data.getSectRoleId());
                if (role != null && role != SectRole.NONE) return; // sect role display takes priority - untouched
            }
            String identityId = data.getIdentityId();
            if (identityId == null || !identityId.startsWith("custom_")) return; // real base identity - untouched

            CustomIdentityManager.CustomIdentity custom = CustomIdentityManager.getById(identityId);
            if (custom == null) return; // stale/unknown custom id - let the original's LONE_CULTIVATOR fallback show

            String name = custom.displayName != null && !custom.displayName.isEmpty() ? custom.displayName : custom.id;
            cir.setReturnValue(Component.literal(name));
        } catch (Exception e) {
            // fall through to the original
        }
    }

    /**
     * Redirects Identity.descriptionKey() - called EXACTLY ONCE in the whole compiled
     * class (bytecode-verified via javap: a single invokevirtual call site, inside
     * render()/m_88315_, immediately feeding the tooltip's description line), so this
     * redirect is unambiguous with no ordinal needed and no risk of hitting an unrelated
     * call site elsewhere in this large method.
     *
     * For a custom identity, this returns the player's own free-typed description text
     * directly in place of a real translation key. That works because
     * Component.translatable(...) (the very next call the original method makes with
     * this return value) falls back to rendering an unmapped key verbatim when no lang
     * entry matches it - standard, documented Minecraft i18n behavior - and an arbitrary
     * player-typed sentence is never going to collide with a real translation key.
     */
    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/Identity;descriptionKey()Ljava/lang/String;"),
            require = 0)
    private String configExt$redirectIdentityDescriptionKey(Identity identity) {
        try {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return identity.descriptionKey();
            CultivationData data = configExt$data(player);
            if (data == null) return identity.descriptionKey();

            String identityId = data.getIdentityId();
            if (identityId == null || !identityId.startsWith("custom_")) return identity.descriptionKey();

            CustomIdentityManager.CustomIdentity custom = CustomIdentityManager.getById(identityId);
            if (custom == null || custom.description == null || custom.description.isEmpty()) {
                return identity.descriptionKey();
            }
            return custom.description;
        } catch (Exception e) {
            return identity.descriptionKey();
        }
    }

    /** Point-in-rect test, matching the original mod's own half-open bounds check. */
    private static boolean configExt$in(int mx, int my, int x1, int y1, int x2, int y2) {
        return mx >= x1 && mx < x2 && my >= y1 && my < y2;
    }

    /** Word-wrap a plain string into gray tooltip lines no wider than maxWidth pixels. */
    private static void configExt$wrap(Font font, String text, int maxWidth, List<Component> out) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (font.width(candidate) > maxWidth && line.length() > 0) {
                out.add(Component.literal(line.toString()).withStyle(ChatFormatting.GRAY));
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            out.add(Component.literal(line.toString()).withStyle(ChatFormatting.GRAY));
        }
    }

    private static CultivationData configExt$data(LocalPlayer player) {
        try {
            return CultivationCapability.get(player).orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * True when the mod is showing a popup that renders after its tooltips
     * (bonusSettingsPopupOpen / breakthroughHistoryPopupOpen / openDropdown != 0).
     * Read reflectively so a field rename in the original mod degrades to "no tooltip"
     * rather than a mixin apply failure.
     */
    private static boolean configExt$popupOpen(Object screen) {
        try {
            java.lang.reflect.Field a = configExt$field(screen.getClass(), "bonusSettingsPopupOpen");
            if (a != null && a.getBoolean(screen)) return true;
            java.lang.reflect.Field b = configExt$field(screen.getClass(), "breakthroughHistoryPopupOpen");
            if (b != null && b.getBoolean(screen)) return true;
            java.lang.reflect.Field c = configExt$field(screen.getClass(), "openDropdown");
            if (c != null && c.getInt(screen) != 0) return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean configExt$isEditingName(Object screen) {
        try {
            java.lang.reflect.Field f = configExt$field(screen.getClass(), "editingName");
            return f != null && f.getBoolean(screen);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Cached reflective field lookups (misses are cached as absent too). */
    private static final java.util.Map<String, java.lang.reflect.Field> CE_FIELD_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Set<String> CE_FIELD_MISSES =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static java.lang.reflect.Field configExt$field(Class<?> clazz, String name) {
        String key = clazz.getName() + "#" + name;
        java.lang.reflect.Field cached = CE_FIELD_CACHE.get(key);
        if (cached != null) return cached;
        if (CE_FIELD_MISSES.contains(key)) return null;
        Class<?> c = clazz;
        while (c != null) {
            try {
                java.lang.reflect.Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                CE_FIELD_CACHE.put(key, f);
                return f;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            } catch (Throwable t) {
                break;
            }
        }
        CE_FIELD_MISSES.add(key);
        return null;
    }
}
