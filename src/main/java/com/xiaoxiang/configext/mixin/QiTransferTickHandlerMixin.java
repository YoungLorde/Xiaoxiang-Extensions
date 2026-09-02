package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.client.CrouchMeditationState;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.event.QiTransferTickHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Multiplies Qi drain from environment when the player is meditating.
 *
 * - Cushion meditation: 10x Qi drain (full bonus)
 * - Crouch meditation: configurable multiplier (default 3x, lower bonus)
 *
 * This makes active meditation more effective than passive cultivation,
 * with cushion meditation being the most effective.
 */
@Mixin(value = QiTransferTickHandler.class, remap = false)
public abstract class QiTransferTickHandlerMixin {

    private static final ThreadLocal<Double> meditationMultiplier = new ThreadLocal<>();

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private static void configExt$captureMeditationState(ServerPlayer player, CultivationData data, CallbackInfo ci) {
        if (data != null && data.isMeditating()) {
            // Check if crouch-meditating (lower multiplier) vs cushion-meditating (10x)
            boolean crouchMeditating = CrouchMeditationState.isCrouchMeditating(player.getUUID());
            double mult = crouchMeditating
                    ? ExtendedConfig.CROUCH_MEDITATION_QI_MULT.get()
                    : 10.0;
            meditationMultiplier.set(mult);
        } else {
            meditationMultiplier.set(null);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 0)
    private static void configExt$clearMeditationState(ServerPlayer player, CultivationData data, CallbackInfo ci) {
        meditationMultiplier.remove();
    }

    @Inject(method = "computeDrain", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void configExt$multiplyDrainWhenMeditating(int chargingTicks, CallbackInfoReturnable<Long> cir) {
        Double mult = meditationMultiplier.get();
        if (mult != null && mult != 1.0) {
            long original = cir.getReturnValue();
            cir.setReturnValue((long) (original * mult));
        }
    }
}
