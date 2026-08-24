package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.entity.npc.WanderingCultivatorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes NPC spawning on superflat worlds.
 *
 * The original checkCultivatorSpawnRules calls Mob.m_217057_() (vanilla mob
 * spawn check) first, which requires specific light levels, block types, and
 * other conditions that are not met on superflat worlds. This causes wandering
 * cultivators to never spawn on superflat.
 *
 * This mixin bypasses the vanilla check on superflat worlds and uses only
 * the random chance check, allowing NPCs to spawn on any block type.
 */
@Mixin(WanderingCultivatorEntity.class)
public abstract class WanderingCultivatorSpawnMixin {

    @Inject(method = "checkCultivatorSpawnRules", at = @At("HEAD"), cancellable = true, remap = false)
    private static void configExt$fixSuperflatSpawn(EntityType<WanderingCultivatorEntity> entity,
                                                     ServerLevelAccessor level,
                                                     MobSpawnType spawnType,
                                                     BlockPos pos,
                                                     RandomSource random,
                                                     CallbackInfoReturnable<Boolean> cir) {
        // For non-natural spawns (spawn egg, command, etc.), always allow
        if (spawnType != MobSpawnType.NATURAL) {
            cir.setReturnValue(true);
            return;
        }

        // Only override for Overworld
        ServerLevel serverLevel = level.getLevel();
        if (serverLevel == null) return;
        if (serverLevel.dimension() != net.minecraft.world.level.Level.OVERWORLD) {
            cir.setReturnValue(true);
            return;
        }

        // Check if this is a superflat world
        ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
        if (!(generator instanceof FlatLevelSource)) {
            // Normal world — let the original method handle it
            return;
        }

        // Superflat world: bypass vanilla mob check, use only random chance
        // The original chances are 2.0E-4 (near structure) and 5.0E-5 (far)
        // Apply the configured multiplier
        double baseChance = 5.0E-5;
        double multiplier = ExtendedConfig.WANDERING_CULTIVATOR_SPAWN_MULTIPLIER.get();

        // Check if near a preferred structure (simplified check)
        double chance = baseChance * multiplier;

        // Also check if the position is valid (not in liquid, has a surface)
        net.minecraft.world.level.block.state.BlockState blockState = serverLevel.getBlockState(pos.below());
        if (blockState.isAir()) {
            cir.setReturnValue(false);
            return;
        }

        // Roll the dice
        if (random.nextDouble() < chance) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
