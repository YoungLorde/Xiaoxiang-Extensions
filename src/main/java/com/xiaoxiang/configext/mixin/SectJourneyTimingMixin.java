package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_JOURNEY_STUCK_TICKS, SECT_JOURNEY_RETURN_FALLBACK_TICKS,
 * SECT_JOURNEY_ENTITY_MISSING_GRACE_TICKS, SECT_JOURNEY_DATA_PHASE_TICKS,
 * SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS, and SECT_JOURNEY_QUEUE_FALLBACK_
 * TICKS.
 *
 * All verified via javap -p -c -s against SectSavedData.class (2026-09-01),
 * traced by following each literal's surrounding getfield/lsub/lcmp
 * bytecode to confirm which JourneyState timestamp field it's actually
 * measured against (several journey-timing methods share the exact same
 * literal *value*, e.g. two different 100L checks, so value alone wasn't
 * enough to disambiguate - the field being compared against was):
 *
 *   updateJourneyProgress(...) compares (gameTime - lastProgressGameTime)
 *   against a literal 600L to flag a journey as stuck. Config default (200)
 *   never matched - corrected to 600.
 *
 *   runJourneyDataLayerPass(ServerLevel) is the per-member data-layer state
 *   machine and contains FOUR distinct long literals, each compared against
 *   a different JourneyState timestamp field (method-scoping plus distinct
 *   values means one handler per value, no ordinals needed):
 *     - (gameTime - returnStartedGameTime) vs 1200L, gating recovery during
 *       the return phase - SECT_JOURNEY_RETURN_FALLBACK_TICKS. Config
 *       default (1200) already matched exactly.
 *     - (gameTime - missingSinceGameTime) vs 200L, gating recovery while
 *       the journey entity is missing - SECT_JOURNEY_ENTITY_MISSING_GRACE_
 *       TICKS. Config default (100) never matched - corrected to 200.
 *     - (gameTime - unobservedSinceGameTime) vs 100L, the threshold before
 *       calling demoteJourneyToDataLayer(...) - SECT_JOURNEY_DATA_PHASE_
 *       TICKS. Config default (20) never matched - corrected to 100.
 *     - (gameTime - startedGameTime) vs 36000L, the threshold before
 *       force-returning a journey stalled in the data layer -
 *       SECT_JOURNEY_QUEUE_FALLBACK_TICKS. Config default (36000) already
 *       matched exactly.
 *
 *   tryRehydrateJourneyActor(...) compares (gameTime -
 *   actorReloadRequestedGameTime) against a literal 100L before respawning
 *   the NPC actor - SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS. Config default
 *   (20) never matched - corrected to 100. (This is a different method from
 *   the DATA_PHASE_TICKS check above, so the shared 100L value doesn't
 *   collide - method-scoping already keeps the two handlers separate.)
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectJourneyTimingMixin {

    @ModifyConstant(method = "updateJourneyProgress", constant = @Constant(longValue = 600L), remap = false, require = 0)
    private long configExt$journeyStuckTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_STUCK_TICKS.get();
        return ticks <= 0 ? original : (long) ticks;
    }

    @ModifyConstant(method = "runJourneyDataLayerPass", constant = @Constant(longValue = 1200L), remap = false, require = 0)
    private long configExt$journeyReturnFallbackTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_RETURN_FALLBACK_TICKS.get();
        return ticks <= 0 ? original : (long) ticks;
    }

    @ModifyConstant(method = "runJourneyDataLayerPass", constant = @Constant(longValue = 200L), remap = false, require = 0)
    private long configExt$journeyEntityMissingGraceTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_ENTITY_MISSING_GRACE_TICKS.get();
        return ticks < 0 ? original : (long) ticks;
    }

    @ModifyConstant(method = "runJourneyDataLayerPass", constant = @Constant(longValue = 100L), remap = false, require = 0)
    private long configExt$journeyDataPhaseTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_DATA_PHASE_TICKS.get();
        return ticks <= 0 ? original : (long) ticks;
    }

    @ModifyConstant(method = "runJourneyDataLayerPass", constant = @Constant(longValue = 36000L), remap = false, require = 0)
    private long configExt$journeyQueueFallbackTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_QUEUE_FALLBACK_TICKS.get();
        return ticks <= 0 ? original : (long) ticks;
    }

    @ModifyConstant(method = "tryRehydrateJourneyActor", constant = @Constant(longValue = 100L), remap = false, require = 0)
    private long configExt$journeyEntityReloadWaitTicks(long original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS.get();
        return ticks <= 0 ? original : (long) ticks;
    }
}
