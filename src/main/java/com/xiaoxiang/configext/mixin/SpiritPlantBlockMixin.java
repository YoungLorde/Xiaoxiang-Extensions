package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.block.SpiritPlantCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder mixin for spirit plant overrides.
 * Not currently registered in mixins.json - spirit plant max age requires
 * constructor redirection which is complex. Growth tick base is handled
 * via config in the plant's randomTick logic.
 */
@Mixin(SpiritPlantCropBlock.class)
public abstract class SpiritPlantBlockMixin {
    // Intentionally empty - growth overrides would require redirecting
    // the maxAge property in the block constructor.
}
