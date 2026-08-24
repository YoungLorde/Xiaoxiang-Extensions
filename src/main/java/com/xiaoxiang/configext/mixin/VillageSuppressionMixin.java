package com.xiaoxiang.configext.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

/**
 * Completely suppresses vanilla village structure generation.
 *
 * Villages and cultivation sects cannot coexist — they would overlap and
 * create conflicts. This mixin intercepts createStructures and removes
 * any structure set whose key contains "village" from the iteration,
 * preventing villages from generating in any world type.
 */
@Mixin(ChunkGenerator.class)
public abstract class VillageSuppressionMixin {

    @Inject(method = "createStructures", at = @At("HEAD"))
    private void configExt$suppressVillages(RegistryAccess registryAccess,
                                            ChunkGeneratorStructureState state,
                                            StructureManager structureManager,
                                            ChunkAccess chunk,
                                            StructureTemplateManager templateManager,
                                            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        // The actual suppression is handled by the tryGenerateStructure inject below.
    }

    /**
     * Intercept tryGenerateStructure to skip village structures entirely.
     * This is called for each structure set during chunk generation.
     */
    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
    private void configExt$skipVillageStructure(StructureSet.StructureSelectionEntry entry,
                                                 StructureManager structureManager,
                                                 RegistryAccess registryAccess,
                                                 RandomState randomState,
                                                 StructureTemplateManager templateManager,
                                                 long seed,
                                                 ChunkAccess chunk,
                                                 ChunkPos chunkPos,
                                                 net.minecraft.core.SectionPos sectionPos,
                                                 CallbackInfoReturnable<Boolean> cir) {
        // Check if this structure is a village
        Holder<Structure> structureHolder = entry.structure();
        if (structureHolder != null && structureHolder.isBound()) {
            // Get the structure's registry key
            java.util.Optional<net.minecraft.resources.ResourceKey<Structure>> keyOpt = structureHolder.unwrapKey();
            if (keyOpt.isPresent()) {
                net.minecraft.resources.ResourceLocation loc = keyOpt.get().location();
                String path = loc.getPath();
                String namespace = loc.getNamespace();
                // Suppress vanilla villages and pillager outposts (which contain villagers)
                if ("minecraft".equals(namespace) && (path.contains("village") || path.contains("pillager"))) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
