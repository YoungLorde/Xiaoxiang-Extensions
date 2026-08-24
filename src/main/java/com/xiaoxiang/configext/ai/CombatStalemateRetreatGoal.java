package com.xiaoxiang.configext.ai;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * A goal that makes NPCs retreat from combat when the fight has been going on
 * for too long without significant progress (stalemate detection).
 *
 * This prevents hour-long battles where neither side can damage the other
 * significantly. After a configurable timeout, the NPC will attempt to flee.
 *
 * The goal activates when:
 * 1. The NPC has a combat target
 * 2. Combat has lasted longer than the configured timeout
 * 3. The NPC is not clearly winning (target health hasn't dropped significantly)
 */
public class CombatStalemateRetreatGoal extends Goal {
    private final WanderingCultivatorEntity npc;
    private int retreatTicks = 0;
    private static final int RETREAT_DURATION = 100; // 5 seconds of retreating

    public CombatStalemateRetreatGoal(WanderingCultivatorEntity npc) {
        this.npc = npc;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = npc.getTarget();
        if (target == null) return false;

        int combatTicks = npc.getCombatTicks();
        int stalemateTimeout = ExtendedConfig.NPC_COMBAT_STALEMATE_TIMEOUT.get();

        if (stalemateTimeout <= 0) return false;
        if (combatTicks < stalemateTimeout) return false;

        // Check if this is actually a stalemate - the target is still alive
        // and at relatively high health
        float targetHealth = target.getHealth();
        float targetMaxHealth = target.getMaxHealth();
        float healthRatio = targetMaxHealth > 0 ? targetHealth / targetMaxHealth : 1.0f;

        // If target is below 30% health, keep fighting (we're winning)
        if (healthRatio < 0.3f) return false;

        // If NPC is below 20% qi, it should have already triggered emergency retreat
        // But if not, this is a good time to retreat
        long currentQi = npc.getCurrentQi();
        long maxQi = npc.getMaxQi();
        if (maxQi > 0 && currentQi < maxQi * 0.1) return true;

        // Stalemate: combat has gone on too long with target still at high health
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return retreatTicks > 0 && npc.getTarget() != null;
    }

    @Override
    public void start() {
        retreatTicks = RETREAT_DURATION;
        // Clear the target to stop combat
        npc.setTarget(null);
        // Force the NPC to stop attacking
        npc.getNavigation().stop();
    }

    @Override
    public void stop() {
        retreatTicks = 0;
    }

    @Override
    public void tick() {
        if (retreatTicks > 0) {
            retreatTicks--;
            // Move away from the last known target position
            // The target was cleared in start(), but we can still move in a direction
            // away from where the combat was happening
            if (retreatTicks % 20 == 0) {
                // Pick a random direction to flee
                double angle = npc.getRandom().nextDouble() * Math.PI * 2;
                double dist = 20.0;
                double dx = Math.cos(angle) * dist;
                double dz = Math.sin(angle) * dist;
                npc.getNavigation().moveTo(
                        npc.getX() + dx,
                        npc.getY(),
                        npc.getZ() + dz,
                        1.2 // Slightly faster than normal to escape
                );
            }
        }
    }
}
