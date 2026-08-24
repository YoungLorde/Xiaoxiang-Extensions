package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.effect.DaoHeartWoundEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Overrides DaoHeartWoundEffect attribute modifier values with config.
 * The original constructor calls addAttributeModifier (m_19472_) with hardcoded
 * -4.0 for attack damage (ADDITION) and -0.15 for movement speed (MULTIPLY_TOTAL).
 */
@Mixin(DaoHeartWoundEffect.class)
public abstract class DaoHeartWoundEffectMixin {

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 0),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$attackPenalty(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_DAO_HEART_WOUND_ATTACK_PENALTY.get();
    }

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 1),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$moveSpeedPenalty(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_DAO_HEART_WOUND_MOVE_SPEED_PENALTY.get();
    }
}
