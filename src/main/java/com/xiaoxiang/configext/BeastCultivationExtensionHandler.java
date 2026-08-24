package com.xiaoxiang.configext;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.BiomeQiProfile;
import com.xiaoxiang.cultivation.cultivation.beast.BeastCapability;
import com.xiaoxiang.cultivation.cultivation.beast.BeastCultivationData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

/**
 * Extends the beast cultivation system to monsters (or all mobs) based on config.
 * The original mod only attaches beast cultivation to Animal entities.
 * This handler adds it to Monster or all Mob entities when configured.
 */
public final class BeastCultivationExtensionHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }
        Entity entity = event.getObject();
        if (entity instanceof Player) {
            return;
        }

        boolean shouldAttach = false;
        if (ExtendedConfig.BEAST_CULTIVATION_FOR_ALL_MOBS.get()) {
            // All mobs except Animals (the original mod already handles Animals)
            shouldAttach = entity instanceof Mob && !(entity instanceof net.minecraft.world.entity.animal.Animal);
        } else if (ExtendedConfig.BEAST_CULTIVATION_FOR_MONSTERS.get()) {
            shouldAttach = entity instanceof Monster;
        }

        if (shouldAttach) {
            event.addCapability(BeastCapability.ID, BeastCapability.createProvider());
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }

        // Only process entities that wouldn't be handled by the original mod
        boolean isExtensionTarget = false;
        if (ExtendedConfig.BEAST_CULTIVATION_FOR_ALL_MOBS.get()) {
            isExtensionTarget = entity instanceof Mob && !(entity instanceof net.minecraft.world.entity.animal.Animal);
        } else if (ExtendedConfig.BEAST_CULTIVATION_FOR_MONSTERS.get()) {
            isExtensionTarget = entity instanceof Monster;
        }

        if (!isExtensionTarget) {
            return;
        }

        int interval = ExtendedConfig.BEAST_CHECK_INTERVAL_TICKS.get();
        if (entity.tickCount % interval != 0) {
            return;
        }

        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Optional<BeastCultivationData> dataOpt = BeastCapability.get(entity);
        if (dataOpt.isEmpty()) {
            return;
        }
        BeastCultivationData data = dataOpt.get();

        BlockPos pos = entity.blockPosition();
        BiomeQiProfile profile = BiomeQiProfile.of(level.getBiome(pos));

        double threshold = ExtendedConfig.BEAST_QI_DENSITY_THRESHOLD.get();
        if (profile.density() < threshold) {
            return;
        }

        double multiplier = ExtendedConfig.BEAST_QI_GAIN_MULTIPLIER.get();
        long gained = Math.max(1L, Math.round(profile.density() * multiplier));
        data.addQi(gained);

        if (data.canAdvance()) {
            data.advance();
            announceAdvancement(serverLevel, entity, data);
        }
    }

    private static void announceAdvancement(ServerLevel level, LivingEntity entity, BeastCultivationData data) {
        Component name = entity.getDisplayName();
        MutableComponent msg = Component.translatable(
                "message.xiaoxiang_cultivation.beast.advance", name, data.getRealm().displayName());
        level.players().forEach(p -> {
            if (p.distanceTo(entity) < 1024.0) {
                p.sendSystemMessage(msg);
            }
        });
    }
}
