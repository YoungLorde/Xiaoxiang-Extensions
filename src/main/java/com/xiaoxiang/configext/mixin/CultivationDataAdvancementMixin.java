package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Makes the zhenyuan (stat point) reward on realm advancement configurable.
 *
 * The original mod hardcodes ZHENYUAN_REWARD_MAJOR = 5 and inlines it as
 * iconst_5 in advanceOnSuccess(). We intercept the addUnallocatedZhenyuan
 * and addAllZhenyuanAttributes calls within advanceOnSuccess() and replace
 * the amount argument with the config-driven value.
 *
 * Config: ZHENYUAN_REWARD_AMOUNT (default 5) controls how many stat points
 * are awarded per major realm breakthrough.
 */
@Mixin(CultivationData.class)
public abstract class CultivationDataAdvancementMixin {

    /**
     * Modify the argument to addUnallocatedZhenyuan() in advanceOnSuccess()
     * to use the configured zhenyuan reward amount.
     */
    @ModifyArg(
        method = "advanceOnSuccess",
        at = @At(value = "INVOKE", target = "Lcom/xiaoxiang/cultivation/cultivation/CultivationData;addUnallocatedZhenyuan(I)V"),
        index = 0,
        remap = false,
        require = 0
    )
    private int configExt$scaleZhenyuanReward(int originalAmount) {
        int configAmount = ExtendedConfig.ZHENYUAN_REWARD_AMOUNT.get();
        if (configAmount != 5 && originalAmount == 5) {
            return configAmount;
        }
        int configMinor = ExtendedConfig.ZHENYUAN_MINOR_REWARD_AMOUNT.get();
        if (configMinor != 1 && originalAmount == 1) {
            return configMinor;
        }
        return originalAmount;
    }

    /**
     * Modify the argument to addAllZhenyuanAttributes() in advanceOnSuccess()
     * to use the configured zhenyuan reward amount.
     */
    @ModifyArg(
        method = "advanceOnSuccess",
        at = @At(value = "INVOKE", target = "Lcom/xiaoxiang/cultivation/cultivation/CultivationData;addAllZhenyuanAttributes(I)V"),
        index = 0,
        remap = false,
        require = 0
    )
    private int configExt$scaleAllAttributesReward(int originalAmount) {
        int configAmount = ExtendedConfig.ZHENYUAN_REWARD_AMOUNT.get();
        if (configAmount != 5 && originalAmount == 5) {
            return configAmount;
        }
        int configMinor = ExtendedConfig.ZHENYUAN_MINOR_REWARD_AMOUNT.get();
        if (configMinor != 1 && originalAmount == 1) {
            return configMinor;
        }
        return originalAmount;
    }
}
