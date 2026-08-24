package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.ai.CombatStalemateRetreatGoal;
import com.xiaoxiang.configext.ai.WildMeditationGoal;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * Injects custom AI goals into the wandering cultivator's goal selector.
 *
 * 1. WildMeditationGoal (priority 7): Makes wild NPCs periodically stop and meditate.
 * 2. CombatStalemateRetreatGoal (priority 0): Makes NPCs retreat from stalemate combat.
 *
 * Uses reflection to access the inherited goalSelector field from Mob class,
 * since @Shadow on inherited fields can fail in production with SRG names.
 */
@Mixin(WanderingCultivatorEntity.class)
public abstract class WildMeditationMixin {

    private static volatile Field goalSelectorField;

    @Inject(method = "registerGoals", at = @At("TAIL"), require = 0)
    private void configExt$addCustomGoals(CallbackInfo ci) {
        WanderingCultivatorEntity self = (WanderingCultivatorEntity) (Object) this;
        GoalSelector selector = getGoalSelector(self);
        if (selector == null) return;
        // Add stalemate retreat at high priority (0 = highest, runs before combat goals)
        selector.addGoal(0, new CombatStalemateRetreatGoal(self));
        // Add meditation goal at low priority (7 = after most other goals)
        selector.addGoal(7, new WildMeditationGoal(self));
    }

    private static GoalSelector getGoalSelector(WanderingCultivatorEntity entity) {
        if (goalSelectorField != null) {
            try {
                return (GoalSelector) goalSelectorField.get(entity);
            } catch (Exception ignored) {}
        }
        // Search class hierarchy for the goalSelector field (Mojang name or SRG name)
        for (Class<?> cls = entity.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (String name : new String[]{"goalSelector", "f_21345_"}) {
                try {
                    Field f = cls.getDeclaredField(name);
                    f.setAccessible(true);
                    goalSelectorField = f;
                    return (GoalSelector) f.get(entity);
                } catch (NoSuchFieldException ignored) {
                    // Try next name
                } catch (Exception ignored2) {
                    // Try next
                }
            }
        }
        return null;
    }
}
