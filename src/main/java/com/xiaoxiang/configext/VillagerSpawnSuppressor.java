package com.xiaoxiang.configext;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Completely suppresses ALL vanilla villager and wandering trader spawns.
 * Vanilla villages and their NPCs are replaced by cultivation sects and
 * their NPCs. The two systems cannot coexist — vanilla villages would
 * overlap with sect settlements and create conflicts.
 *
 * This handler denies every natural villager spawn attempt and every
 * wandering trader spawn attempt, ensuring only cultivation NPCs populate
 * the world.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerSpawnSuppressor {

    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.PositionCheck event) {
        // Completely deny all villager spawns (natural, chunk gen, etc.)
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setResult(Event.Result.DENY);
        }
        // Completely deny wandering trader spawns
        if (event.getEntity().getType() == EntityType.WANDERING_TRADER) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        // Block any villager that somehow passed the position check
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setSpawnCancelled(true);
        }
        if (event.getEntity().getType() == EntityType.WANDERING_TRADER) {
            event.setSpawnCancelled(true);
        }
    }
}
