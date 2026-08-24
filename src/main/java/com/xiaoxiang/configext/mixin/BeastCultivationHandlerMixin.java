package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.BiomeQiProfile;
import com.xiaoxiang.cultivation.cultivation.beast.BeastCultivationData;
import com.xiaoxiang.cultivation.event.BeastCultivationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Overrides the original beast cultivation handler's hardcoded qi gain
 * threshold and multiplier with config-driven values.
 */
@Mixin(BeastCultivationHandler.class)
public abstract class BeastCultivationHandlerMixin {

    @Shadow(remap = false)
    private static void announceAdvancement(ServerLevel level, LivingEntity entity, BeastCultivationData data) {
        // Shadowed - body replaced at runtime
    }

    @Inject(method = "updateBeast", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$updateBeast(
            ServerLevel level,
            LivingEntity entity,
            BeastCultivationData data,
            CallbackInfo ci) {

        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        BiomeQiProfile profile = BiomeQiProfile.of(level.getBiome(pos));

        double threshold = ExtendedConfig.BEAST_QI_DENSITY_THRESHOLD.get();
        if (profile.density() < threshold) {
            ci.cancel();
            return;
        }

        double multiplier = ExtendedConfig.BEAST_QI_GAIN_MULTIPLIER.get();
        long gained = Math.max(1L, Math.round(profile.density() * multiplier));
        data.addQi(gained);

        if (data.canAdvance()) {
            data.advance();
            announceAdvancement(level, entity, data);
        }
        ci.cancel();
    }

    @ModifyConstant(method = "onLivingTick", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 100), remap = false)
    private static int configExt$checkInterval(int original) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return original;
        }
        return ExtendedConfig.BEAST_CHECK_INTERVAL_TICKS.get();
    }
}
