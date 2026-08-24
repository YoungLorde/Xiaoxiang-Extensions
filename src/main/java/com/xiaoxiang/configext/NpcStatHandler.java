package com.xiaoxiang.configext;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.registry.ModEntities;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies NPC stat multipliers (HP, attack, defense) to wandering cultivator
 * entities at attribute registration time.
 *
 * This works alongside NpcAttributeMixin to ensure NPC stats are scaled by
 * the difficulty config. The mixin modifies the base attribute supplier,
 * while this handler modifies the per-entity-type attribute map.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NpcStatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("XiaoxiangConfigExt");

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        double hpMult = ExtendedConfig.NPC_HP_MULTIPLIER.get();
        double attackMult = ExtendedConfig.NPC_ATTACK_MULTIPLIER.get();
        double defenseMult = ExtendedConfig.NPC_DEFENSE_MULTIPLIER.get();

        if (hpMult == 1.0 && attackMult == 1.0 && defenseMult == 1.0) return;

        try {
            var entityType = ModEntities.WANDERING_CULTIVATOR.get();
            event.add(entityType, Attributes.MAX_HEALTH, 20.0 * hpMult);
            event.add(entityType, Attributes.ATTACK_DAMAGE, 2.0 * attackMult);
            if (defenseMult != 1.0) {
                event.add(entityType, Attributes.ARMOR, (defenseMult - 1.0) * 15.0);
                event.add(entityType, Attributes.ARMOR_TOUGHNESS, (defenseMult - 1.0) * 10.0);
            }
            LOGGER.info("[XiaoxiangConfigExt] Applied NPC stat multipliers: HP={}x, Attack={}x, Defense={}x",
                    hpMult, attackMult, defenseMult);
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to apply NPC stat multipliers", e);
        }
    }
}
