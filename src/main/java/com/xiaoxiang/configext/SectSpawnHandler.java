package com.xiaoxiang.configext;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Sect spawn handler — now a no-op.
 *
 * The original mod (SectSettlementFeature) handles sect spawning naturally
 * through its own worldgen feature. This handler previously force-placed
 * sects near the player and teleported them, which caused world generation
 * freezes and crashes.
 *
 * Sect spawn rate is controlled via the config option
 * sects.generation.cellSpawnChance (default 0.51, which is 50% higher
 * than the original mod's 0.34).
 *
 * Sect sizes are controlled via SectSizeMixin and the sects.sizeTiers config.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SectSpawnHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // No-op — original mod handles sect spawning naturally.
        // Config controls spawn chance and sect sizes.
    }
}
