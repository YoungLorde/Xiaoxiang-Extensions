package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_EVENT_LIMIT.
 *
 * Verified via javap -p -c -s against SectSavedData$SectRecord.class
 * (2026-09-01): addEvent(SectEventRecord) appends to the events list, then
 * trims from the front (oldest first) while size() > 128. Config's default
 * of 100 never matched - corrected to 128.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData$SectRecord", remap = false)
public abstract class SectRecordEventLimitMixin {

    @ModifyConstant(method = "addEvent", constant = @Constant(intValue = 128), remap = false, require = 0)
    private int configExt$eventLimit(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_EVENT_LIMIT.get();
    }
}
