package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.effect.BloodBerserkEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Overrides BloodBerserkEffect attribute modifier values with config.
 * The original constructor calls addAttributeModifier (m_19472_) with hardcoded
 * 0.2 for both attack speed and movement speed (MULTIPLY_TOTAL operation).
 *
 * Uses remap = false with SRG name since the target is a Minecraft method
 * and we're building for production (SRG runtime).
 */
@Mixin(BloodBerserkEffect.class)
public abstract class BloodBerserkEffectMixin {

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 0),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$attackSpeedMult(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_BLOOD_BERSERK_ATTACK_SPEED_MULT.get();
    }

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 1),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$moveSpeedMult(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_BLOOD_BERSERK_MOVE_SPEED_MULT.get();
    }
}
