package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.entity.npc.CultivatorRealmRoller;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides NPC cultivator realm distribution weights with config-driven values.
 */
@Mixin(CultivatorRealmRoller.class)
public abstract class CultivatorRealmRollerMixin {

    @Inject(method = "roll", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$roll(RandomSource random, CallbackInfoReturnable<Realm> cir) {
        if (!ExtendedConfig.ENABLE_SPAWN_OVERRIDES.get()) {
            return;
        }

        int[] weights = new int[]{
                ExtendedConfig.NPC_WEIGHT_MORTAL.get(),
                ExtendedConfig.NPC_WEIGHT_QI_REFINING.get(),
                ExtendedConfig.NPC_WEIGHT_FOUNDATION_BUILDING.get(),
                ExtendedConfig.NPC_WEIGHT_GOLDEN_CORE.get(),
                ExtendedConfig.NPC_WEIGHT_NASCENT_SOUL.get(),
                ExtendedConfig.NPC_WEIGHT_SOUL_FORMATION.get(),
                ExtendedConfig.NPC_WEIGHT_VOID_REFINING.get(),
                ExtendedConfig.NPC_WEIGHT_BODY_INTEGRATION.get(),
                ExtendedConfig.NPC_WEIGHT_MAHAYANA.get(),
                ExtendedConfig.NPC_WEIGHT_TRIBULATION_TRANSCENDENCE.get(),
                ExtendedConfig.NPC_WEIGHT_LOOSE_IMMORTAL.get(),
                ExtendedConfig.NPC_WEIGHT_TRUE_IMMORTAL.get(),
        };

        int totalWeight = 0;
        for (int w : weights) {
            totalWeight += w;
        }

        if (totalWeight <= 0) {
            cir.setReturnValue(Realm.MORTAL);
            return;
        }

        int roll = random.nextInt(totalWeight);
        int acc = 0;
        Realm[] vals = Realm.values();
        for (int i = 0; i < weights.length && i < vals.length; i++) {
            if (roll >= (acc += weights[i])) continue;
            cir.setReturnValue(vals[i]);
            return;
        }
        cir.setReturnValue(Realm.MORTAL);
    }
}
