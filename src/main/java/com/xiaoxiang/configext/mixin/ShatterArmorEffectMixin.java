package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.effect.ShatterArmorEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Overrides ShatterArmorEffect attribute modifier values with config.
 * The original constructor calls addAttributeModifier (m_19472_) with hardcoded
 * -0.99 for both armor and toughness (MULTIPLY_TOTAL operation).
 */
@Mixin(ShatterArmorEffect.class)
public abstract class ShatterArmorEffectMixin {

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 0),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$armorPenalty(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_SHATTER_ARMOR_ARMOR_PENALTY.get();
    }

    @ModifyArg(method = "<init>",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/effect/MobEffect;m_19472_(Lnet/minecraft/world/entity/ai/attributes/Attribute;Ljava/lang/String;DLnet/minecraft/world/entity/ai/attributes/AttributeModifier$Operation;)Lnet/minecraft/world/effect/MobEffect;",
                        ordinal = 1),
               index = 2,
               remap = false,
               require = 0)
    private double configExt$toughnessPenalty(double original) {
        if (!ExtendedConfig.ENABLE_EFFECT_OVERRIDES.get()) return original;
        return ExtendedConfig.EFFECT_SHATTER_ARMOR_TOUGHNESS_PENALTY.get();
    }
}
