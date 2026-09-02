package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.client.CultivationHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Moves the original mod's CultivationHud from the top-right to the bottom-left
 * of the screen, above the hotbar, and (added 2026-09-01 config audit) wires up
 * the client "hud" section's visibility/color/row-height fields, which had sat
 * dead in the config screen since they were added - editing them did nothing.
 *
 * The original mod renders the HUD at (6, 6) or (screenWidth - 126, 6).
 * We redirect hudX() to always return 4 (left-aligned), and modify the
 * Y constant (6) to be screenHeight - 100.
 *
 * VERIFIED FIX (2026-09-01): confirmed via javap bytecode disassembly of
 * CultivationHud.class that CLIENT_HUD_TEXT_COLOR, CLIENT_QI_BAR_TOP_COLOR,
 * CLIENT_QI_BAR_BOTTOM_COLOR, CLIENT_CULT_BAR_TOP_COLOR,
 * CLIENT_CULT_BAR_BOTTOM_COLOR and CLIENT_STATUS_BAR_ROW_HEIGHT all have
 * defaults that exactly match the base mod's own GOLD_TEXT/QI_TOP/QI_BOTTOM/
 * CULT_TOP/CULT_BOTTOM/BAR_H constants, and each of those color values (and
 * the bar-height literal, scoped to just drawBar's own body) appears exactly
 * once per targeted method - confirmed by grepping the full disassembly, not
 * assumed - so each @ModifyConstant below is unambiguous. CLIENT_HUD_VISIBLE
 * gets its own real "show/hide the HUD" effect via a cancelling head inject.
 *
 * NOT wired here, on purpose: CLIENT_HUD_X/Y, CLIENT_PORTRAIT_X/Y/SIZE,
 * CLIENT_REALM_NAME_X/Y, CLIENT_QI_BAR_X/Y, CLIENT_CULT_BAR_X/Y,
 * CLIENT_SPELL_GRID_X/Y, CLIENT_INFO_TEXT_X/Y. Bytecode-confirmed reasons,
 * logged in CONFIG_AUDIT.md so this isn't lost: the X-offset fields share one
 * literal (bipush 32) used at 3+ call sites in render(), so a blind
 * @ModifyConstant would either need per-call-site @Slice scoping (not
 * attempted yet - real risk of silently moving the wrong element on a
 * required:true mixin with no compiler available to verify it) or would move
 * every element by the same amount instead of independently, which isn't
 * what those fields promise. PORTRAIT_SIZE turned out to not even be read by
 * drawPortrait (which uses three separate hardcoded literals - 25/25 outer
 * ring, 23/23 inner ring, 16 face icon - none of them PORTRAIT_SIZE itself),
 * so "resizing the portrait" would need three literals scaled in proportion,
 * not one - a real design decision, not a one-line fix. CLIENT_INK_BLACK_COLOR
 * is worse than unwired - its value (-16448509) doesn't appear anywhere at
 * all in CultivationHud's bytecode, meaning the base mod's own INK_BLACK
 * constant field is itself unused dead code, so there is nothing real to wire
 * this to. Its tooltip has been corrected to say so rather than silently
 * left claiming an effect that was never real to begin with.
 */
@Mixin(CultivationHud.class)
public abstract class CultivationHudMixin {

    /**
     * Redirect the hudX() call to always return left-aligned position (x=4).
     * hudX is a static method taking (int screenWidth) and returning int.
     * For a static method redirect, the handler signature matches the target
     * method's signature exactly (no `this` parameter).
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/xiaoxiang/cultivation/client/CultivationHud;hudX(I)I"), remap = false, require = 0)
    private static int configExt$overrideHudX(int screenWidth) {
        return 4;
    }

    /**
     * Modify the Y position constant. The original code uses bipush 6 for Y.
     * We change it to screenHeight - 100 to place the HUD at the bottom-left.
     */
    @ModifyConstant(method = "render", constant = @Constant(intValue = 6), remap = false, require = 1)
    private static int configExt$overrideY(int originalY) {
        try {
            int screenHeight = net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight();
            return screenHeight - 100;
        } catch (Exception e) {
            return originalY;
        }
    }

    /**
     * "Show or hide the in-game cultivation HUD" - CLIENT_HUD_VISIBLE's own
     * comment. Was defined but never checked; render() ran unconditionally.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$hudVisible(net.minecraft.client.gui.GuiGraphics graphics, int screenWidth, CallbackInfo ci) {
        try {
            if (!ExtendedConfig.CLIENT_HUD_VISIBLE.get()) {
                ci.cancel();
            }
        } catch (Exception ignored) {
            // Config not loaded yet - let the HUD render as normal.
        }
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = -9583434), remap = false, require = 1)
    private static int configExt$qiBarTop(int original) {
        try { return ExtendedConfig.CLIENT_QI_BAR_TOP_COLOR.get(); } catch (Exception e) { return original; }
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = -13729678), remap = false, require = 1)
    private static int configExt$qiBarBottom(int original) {
        try { return ExtendedConfig.CLIENT_QI_BAR_BOTTOM_COLOR.get(); } catch (Exception e) { return original; }
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = -928374), remap = false, require = 1)
    private static int configExt$cultBarTop(int original) {
        try { return ExtendedConfig.CLIENT_CULT_BAR_TOP_COLOR.get(); } catch (Exception e) { return original; }
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = -3631046), remap = false, require = 1)
    private static int configExt$cultBarBottom(int original) {
        try { return ExtendedConfig.CLIENT_CULT_BAR_BOTTOM_COLOR.get(); } catch (Exception e) { return original; }
    }

    /** The realm-name line's text color (drawRealmText - one occurrence of GOLD_TEXT in this method). */
    @ModifyConstant(method = "drawRealmText", constant = @Constant(intValue = -1456016), remap = false, require = 1)
    private static int configExt$realmTextColor(int original) {
        try { return ExtendedConfig.CLIENT_HUD_TEXT_COLOR.get(); } catch (Exception e) { return original; }
    }

    /** The soul-state countdown text's color (renderSoulStatus - a separate method, its own occurrence of GOLD_TEXT). */
    @ModifyConstant(method = "renderSoulStatus", constant = @Constant(intValue = -1456016), remap = false, require = 1)
    private static int configExt$soulStatusTextColor(int original) {
        try { return ExtendedConfig.CLIENT_HUD_TEXT_COLOR.get(); } catch (Exception e) { return original; }
    }

    /** Bar height inside drawBar (shared helper for both the qi and cultivation bars) - one occurrence of bipush 6 in this method. */
    @ModifyConstant(method = "drawBar", constant = @Constant(intValue = 6), remap = false, require = 1)
    private static int configExt$barHeight(int original) {
        try { return ExtendedConfig.CLIENT_STATUS_BAR_ROW_HEIGHT.get(); } catch (Exception e) { return original; }
    }
}
