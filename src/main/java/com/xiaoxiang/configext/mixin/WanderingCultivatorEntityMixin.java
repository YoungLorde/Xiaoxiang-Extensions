package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Overrides wandering cultivator spawn chances with config-driven values.
 * Uses @ModifyConstant to replace the hardcoded chance values in the original
 * checkCultivatorSpawnRules method, preserving all other spawn logic.
 */
@Mixin(WanderingCultivatorEntity.class)
public abstract class WanderingCultivatorEntityMixin {

    @ModifyConstant(method = "checkCultivatorSpawnRules", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 2.0E-4), remap = false)
    private static double configExt$nearChance(double original) {
        if (!ExtendedConfig.ENABLE_SPAWN_OVERRIDES.get()) {
            return original;
        }
        return ExtendedConfig.CULTIVATOR_SPAWN_CHANCE_NEAR.get();
    }

    @ModifyConstant(method = "checkCultivatorSpawnRules", constant = @org.spongepowered.asm.mixin.injection.Constant(doubleValue = 5.0E-5), remap = false)
    private static double configExt$farChance(double original) {
        if (!ExtendedConfig.ENABLE_SPAWN_OVERRIDES.get()) {
            return original;
        }
        return ExtendedConfig.CULTIVATOR_SPAWN_CHANCE_FAR.get();
    }
}
