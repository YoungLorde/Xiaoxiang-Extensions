package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.cultivation.cultivation.CultivationData;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Previously applied stat point effectiveness multipliers from difficulty presets.
 * The preset system has been removed, so this mixin is now a no-op placeholder.
 */
@Mixin(CultivationData.class)
public abstract class CultivationDataMixin {
}
