package com.xiaoxiang.configext.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Empty placeholder - we no longer use an accessor for addRenderableWidget.
 * Button registration is done via reflection in the screen mixins.
 */
@Mixin(Screen.class)
public interface ScreenAccessor {
    // No-op. Button adding is handled via reflection in CreateWorldScreenMixin
    // and IdentityDrawScreenMixin to avoid @Invoker signature issues.
}
