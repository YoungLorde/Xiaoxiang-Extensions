package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses vanilla heart/food/air rendering when the bar HUD is enabled.
 * The bar-style HUD is rendered separately via BarHudOverlay.
 */
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true, require = 0)
    private void configExt$suppressPlayerHealth(GuiGraphics g, CallbackInfo ci) {
        if (ExtendedConfig.CLIENT_ENABLE_BAR_HUD.get()) {
            ci.cancel();
        }
    }
}
