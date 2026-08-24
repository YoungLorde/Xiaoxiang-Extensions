package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Scales NPC entity attributes (max HP, attack damage, defense) based on
 * difficulty config multipliers.
 *
 * The original WanderingCultivatorEntity.createAttributes() sets:
 *   MAX_HEALTH = 20.0
 *   MOVEMENT_SPEED = 0.23
 *   FOLLOW_RANGE = 64.0
 *   ATTACK_DAMAGE = 2.0
 *
 * We intercept the returned AttributeSupplier.Builder and override the
 * attribute values with scaled versions.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity", remap = false)
public abstract class NpcAttributeMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$scaleNpcAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        if (builder == null) return;

        double hpMult = ExtendedConfig.NPC_HP_MULTIPLIER.get();
        double attackMult = ExtendedConfig.NPC_ATTACK_MULTIPLIER.get();
        double defenseMult = ExtendedConfig.NPC_DEFENSE_MULTIPLIER.get();

        // The builder.add() method internally stores attributes in a map.
        // Calling add() again with the same attribute overrides the previous value.
        // This is the standard way to modify attribute supplier builders.
        try {
            if (hpMult != 1.0) {
                builder.add(Attributes.MAX_HEALTH, 20.0 * hpMult);
            }
            if (attackMult != 1.0) {
                builder.add(Attributes.ATTACK_DAMAGE, 2.0 * attackMult);
            }
            if (defenseMult != 1.0) {
                // Add armor and toughness for defense scaling
                builder.add(Attributes.ARMOR_TOUGHNESS, (defenseMult - 1.0) * 10.0);
                builder.add(Attributes.ARMOR, (defenseMult - 1.0) * 15.0);
            }
        } catch (Exception e) {
            // If builder.add fails (e.g., immutable builder), ignore
        }
    }
}
