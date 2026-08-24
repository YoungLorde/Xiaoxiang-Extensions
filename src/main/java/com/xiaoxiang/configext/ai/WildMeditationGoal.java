package com.xiaoxiang.configext.ai;

import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * A goal that makes wild wandering cultivators periodically stop and meditate.
 *
 * When triggered, the NPC will:
 * 1. Stop moving
 * 2. Sit down (setPose or similar visual)
 * 3. Stay in place for a random duration (10-30 seconds)
 * 4. Then resume wandering
 *
 * This only applies to NPCs that are NOT in a sect (wild NPCs).
 * Sect NPCs have their own routine system (tickSectRoutine).
 */
public class WildMeditationGoal extends Goal {
    private final WanderingCultivatorEntity npc;
    private int meditationTimer = 0;
    private int nextMeditationCheck = 200;
    private boolean isMeditating = false;
    private BlockPos meditationPos = null;

    public WildMeditationGoal(WanderingCultivatorEntity npc) {
        this.npc = npc;
        this.nextMeditationCheck = 200 + npc.getRandom().nextInt(400); // 10-30 seconds until first check
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (isMeditating) return true;

        // Only meditate if not in combat, not in a sect, and on the ground
        if (npc.getTarget() != null) return false;
        if (isInSect()) return false;
        if (!npc.onGround()) return false;

        // Check periodically
        if (nextMeditationCheck > 0) {
            nextMeditationCheck--;
            return false;
        }

        // Random chance to start meditating (10% chance per check)
        if (npc.getRandom().nextFloat() < 0.1f) {
            nextMeditationCheck = 600 + npc.getRandom().nextInt(1200); // 30-90 seconds until next check
            return true;
        }

        nextMeditationCheck = 200 + npc.getRandom().nextInt(400);
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return isMeditating && meditationTimer > 0 && npc.getTarget() == null;
    }

    @Override
    public void start() {
        isMeditating = true;
        meditationTimer = 200 + npc.getRandom().nextInt(400); // 10-30 seconds
        meditationPos = npc.blockPosition();
        npc.getNavigation().stop();
        // Set the NPC to "sitting" visually
        npc.setShiftKeyDown(true);
    }

    @Override
    public void stop() {
        isMeditating = false;
        meditationTimer = 0;
        meditationPos = null;
        npc.setShiftKeyDown(false);
    }

    @Override
    public void tick() {
        if (isMeditating && meditationTimer > 0) {
            meditationTimer--;
            // Keep the NPC in place
            npc.getNavigation().stop();
            npc.setDeltaMovement(0, npc.getDeltaMovement().y, 0);

            // If on ground, try to stay put
            if (meditationPos != null && !npc.blockPosition().equals(meditationPos)) {
                npc.getNavigation().moveTo(meditationPos.getX() + 0.5, meditationPos.getY(), meditationPos.getZ() + 0.5, 0.0);
            }
        }
    }

    private boolean isInSect() {
        try {
            // Use the public getSectId() method to check if the NPC is in a sect
            String sectId = npc.getSectId();
            return sectId != null && !sectId.isEmpty();
        } catch (Exception e) {
            // If we can't determine, assume not in sect
            return false;
        }
    }
}
