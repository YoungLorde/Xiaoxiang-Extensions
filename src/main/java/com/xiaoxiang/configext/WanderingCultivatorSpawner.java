package com.xiaoxiang.configext;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import com.xiaoxiang.cultivation.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Custom spawner for wandering cultivator NPCs.
 *
 * The original mod registers WanderingCultivator with MobCategory.MISC, which
 * has a spawn cap of 0 in vanilla. This means the natural spawning system never
 * attempts to spawn them, regardless of the spawn predicate. This spawner
 * bypasses the vanilla system and directly spawns WanderingCultivators near
 * players at a configurable rate.
 *
 * Spawn logic:
 * - Runs every 100 ticks (5 seconds) per level
 * - For each player, rolls a chance based on the configured multiplier
 * - If successful, finds a valid spawn position within 24-64 blocks
 * - Spawns 1 WanderingCultivator at that position
 * - Limits total wandering cultivators near each player to 8
 * - Works in ALL dimensions (Overworld, Nether, End, modded dimensions)
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WanderingCultivatorSpawner {

    private static final int SPAWN_INTERVAL_TICKS = 100; // every 5 seconds
    private static final int SPAWN_MIN_DISTANCE = 24;
    private static final int SPAWN_MAX_DISTANCE = 64;
    private static final int MAX_CULTIVATORS_NEAR_PLAYER = 8;
    private static final double BASE_SPAWN_CHANCE_PER_TICK = 0.15; // 15% per interval at 1x multiplier

    // Per-level tick counters (WeakHashMap so levels are GC'd when unloaded)
    private static final WeakHashMap<ServerLevel, Integer> levelTicks = new WeakHashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;
        if (level.isClientSide()) return;

        // Get per-level tick counter
        int ticks = levelTicks.getOrDefault(level, 0) + 1;
        if (ticks < SPAWN_INTERVAL_TICKS) {
            levelTicks.put(level, ticks);
            return;
        }
        levelTicks.put(level, 0);

        double multiplier = ExtendedConfig.WANDERING_CULTIVATOR_SPAWN_MULTIPLIER.get();
        if (multiplier <= 0) return;

        double spawnChance = BASE_SPAWN_CHANCE_PER_TICK * multiplier;
        RandomSource random = level.getRandom();

        for (ServerPlayer player : level.players()) {
            // Check how many wandering cultivators are already near this player
            AABB searchArea = AABB.ofSize(player.position(), SPAWN_MAX_DISTANCE * 2, 64, SPAWN_MAX_DISTANCE * 2);
            List<WanderingCultivatorEntity> nearby = level.getEntitiesOfClass(
                    WanderingCultivatorEntity.class, searchArea);

            if (nearby.size() >= MAX_CULTIVATORS_NEAR_PLAYER) continue;

            // Roll for spawn
            if (random.nextDouble() >= spawnChance) continue;

            // Find a valid spawn position
            BlockPos spawnPos = findSpawnPosition(level, player.blockPosition(), random);
            if (spawnPos == null) continue;

            // Spawn the wandering cultivator
            try {
                WanderingCultivatorEntity cultivator = ModEntities.WANDERING_CULTIVATOR.get().create(
                        level, null, null, spawnPos, MobSpawnType.NATURAL, true, false);
                if (cultivator != null) {
                    cultivator.moveTo(
                            spawnPos.getX() + 0.5,
                            spawnPos.getY(),
                            spawnPos.getZ() + 0.5,
                            random.nextFloat() * 360.0F,
                            0.0F);
                    level.addFreshEntity(cultivator);
                }
            } catch (Exception e) {
                // Silently ignore spawn failures
            }
        }
    }

    /**
     * Find a valid spawn position on solid ground within range of the player.
     * Only checks loaded chunks to avoid ConcurrentModificationException in
     * DistanceManager when force-loading chunks during server tick.
     */
    private static BlockPos findSpawnPosition(ServerLevel level, BlockPos playerPos, RandomSource random) {
        for (int attempt = 0; attempt < 8; attempt++) {
            int angle = random.nextInt(360);
            double rad = Math.toRadians(angle);
            int dist = SPAWN_MIN_DISTANCE + random.nextInt(SPAWN_MAX_DISTANCE - SPAWN_MIN_DISTANCE);
            int x = playerPos.getX() + (int)(Math.cos(rad) * dist);
            int z = playerPos.getZ() + (int)(Math.sin(rad) * dist);

            // CRITICAL: Only check loaded chunks. Accessing unloaded chunks
            // during server tick causes ConcurrentModificationException in
            // DistanceManager's ticket map.
            if (!level.hasChunk(x >> 4, z >> 4)) continue;

            // Find surface height
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (y <= level.getMinBuildHeight()) continue;

            BlockPos pos = new BlockPos(x, y, z);

            // Check that the block below is solid and there's air above
            BlockState below = level.getBlockState(pos.below());
            BlockState feet = level.getBlockState(pos);
            BlockState head = level.getBlockState(pos.above());

            // Solid ground + air at feet and head level
            if (!below.isAir()
                    && !below.getCollisionShape(level, pos.below()).isEmpty()
                    && feet.isAir()
                    && head.isAir()) {
                // Also check the block isn't lava or fire
                if (!below.is(net.minecraft.world.level.block.Blocks.LAVA)
                        && !below.is(net.minecraft.world.level.block.Blocks.FIRE)) {
                    return pos;
                }
            }
        }
        return null;
    }
}
