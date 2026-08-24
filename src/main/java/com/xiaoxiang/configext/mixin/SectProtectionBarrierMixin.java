package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.cultivation.block.formation.SectProtectionBarrierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes the invisible wall bug in sect protection barriers.
 *
 * When a sect's protection array is deactivated, the barrier blocks are removed
 * asynchronously. During the removal window, the barrier blocks still exist in
 * the world but the owning dome can no longer be found (placedBarriers was cleared).
 *
 * The original code returns Shapes.block() (solid) when findOwningDomePos() returns
 * null, creating an invisible wall that blocks everyone. This mixin changes that
 * to return Shapes.empty() (no collision) so players can pass through barriers
 * whose dome has been deactivated.
 */
@Mixin(SectProtectionBarrierBlock.class)
public abstract class SectProtectionBarrierMixin {

    /**
     * Override getCollisionShape (m_5939_) to return empty shape when the
     * owning dome cannot be found. This means: if the dome is deactivated,
     * the barrier should not block anyone, even if the physical block
     * hasn't been removed yet.
     */
    @Inject(method = "m_5939_", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$fixInvisibleWall(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx,
                                            CallbackInfoReturnable<VoxelShape> cir) {
        // Only handle server-side Level instances
        if (!(level instanceof Level)) return;

        Level world = (Level) level;

        // If this is client-side, let the original method handle it
        if (world.isClientSide()) return;

        // Check if the owning dome can be found
        // If not, the dome has been deactivated — return empty shape (no collision)
        try {
            // Use reflection to call the private findOwningDomePos method
            java.lang.reflect.Method findMethod = SectProtectionBarrierBlock.class
                    .getDeclaredMethod("findOwningDomePos", Level.class, BlockPos.class);
            findMethod.setAccessible(true);
            BlockPos domePos = (BlockPos) findMethod.invoke(null, world, pos);

            if (domePos == null) {
                // Dome not found — deactivated but barrier block still exists
                // Return empty shape so players can pass through
                cir.setReturnValue(Shapes.empty());
            }
            // If domePos is not null, let the original method run normally
        } catch (Exception e) {
            // If reflection fails, let the original method run
        }
    }
}
