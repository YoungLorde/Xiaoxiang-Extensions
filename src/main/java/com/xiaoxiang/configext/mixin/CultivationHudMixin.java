package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.cultivation.client.CultivationHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Moves the original mod's CultivationHud from the top-right to the bottom-left
 * of the screen, above the hotbar.
 *
 * The original mod renders the HUD at (6, 6) or (screenWidth - 126, 6).
 * We redirect hudX() to always return 4 (left-aligned), and modify the
 * Y constant (6) to be screenHeight - 100.
 */
@Mixin(CultivationHud.class)
public abstract class CultivationHudMixin {

    /**
     * Redirect the hudX() call to always return left-aligned position (x=4).
     * hudX is a static method taking (int screenWidth) and returning int.
     * For a static method redirect, the handler signature matches the target
     * method's signature exactly (no `this` parameter).
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/xiaoxiang/cultivation/client/CultivationHud;hudX(I)I"), remap = false)
    private static int configExt$overrideHudX(int screenWidth) {
        return 4;
    }

    /**
     * Modify the Y position constant. The original code uses bipush 6 for Y.
     * We change it to screenHeight - 100 to place the HUD at the bottom-left.
     */
    @ModifyConstant(method = "render", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 6), remap = false, require = 1)
    private static int configExt$overrideY(int originalY) {
        try {
            int screenHeight = net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight();
            return screenHeight - 100;
        } catch (Exception e) {
            return originalY;
        }
    }
}
