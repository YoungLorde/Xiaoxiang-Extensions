package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.BiomeQiProfile;
import com.xiaoxiang.cultivation.cultivation.beast.BeastCultivationData;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.event.BeastCultivationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Overrides the original beast cultivation handler's hardcoded qi gain
 * threshold and multiplier with config-driven values.
 *
 * CORRECTED 2026-09-02 (systematic sweep): the @Shadow below previously
 * targeted a method named announceAdvancement(ServerLevel, LivingEntity,
 * BeastCultivationData), which does not exist anywhere in base mod 0.1.1479
 * - confirmed via javap -p against the real class (no such name at all) and
 * independently via the user's own crash report
 * (InvalidMixinException: "@Shadow method announceAdvancement ... was not
 * located in the target class"). javap -p -c -s of updateBeast(...) shows
 * the real advancement call site is now:
 *   data.advance();
 *   emitAdvancementFeedback(level, entity, data.getRealm());
 * i.e. the method was renamed to emitAdvancementFeedback AND its third
 * parameter changed from the BeastCultivationData itself to the resulting
 * Realm (data.getRealm(), confirmed via javap -c on BeastCultivationData:
 * advance() mutates the realm field in a loop via
 * BeastProgressionRules.nextBeastRealm, and getRealm() is a plain getter).
 * @Shadow updated to the real name/signature; the call site in
 * configExt$updateBeast below now reads data.getRealm() after advance()
 * instead of passing data directly.
 *
 * Note (scope, flagged honestly): the real updateBeast also calls
 * syncBeastRealmAttributes(entity, data.getRealm()) and heals the entity to
 * full (entity.setHealth(entity.getMaxHealth())) as part of an advance -
 * this override does not replicate either side effect (only the
 * advancement announcement), matching this mixin's pre-existing scope. Not
 * changed here since it's a design-scope question, not a load-time crash.
 */
@Mixin(BeastCultivationHandler.class)
public abstract class BeastCultivationHandlerMixin {

    @Shadow(remap = false)
    private static void emitAdvancementFeedback(ServerLevel level, LivingEntity entity, Realm realm) {
        // Shadowed - body replaced at runtime
    }

    @Inject(method = "updateBeast", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$updateBeast(
            ServerLevel level,
            LivingEntity entity,
            BeastCultivationData data,
            CallbackInfo ci) {

        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        BiomeQiProfile profile = BiomeQiProfile.of(level.getBiome(pos));

        double threshold = ExtendedConfig.BEAST_QI_DENSITY_THRESHOLD.get();
        if (profile.density() < threshold) {
            ci.cancel();
            return;
        }

        double multiplier = ExtendedConfig.BEAST_QI_GAIN_MULTIPLIER.get();
        long gained = Math.max(1L, Math.round(profile.density() * multiplier));
        data.addQi(gained);

        if (data.canAdvance()) {
            data.advance();
            emitAdvancementFeedback(level, entity, data.getRealm());
        }
        ci.cancel();
    }

    @ModifyConstant(method = "onLivingTick", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 100), remap = false, require = 0)
    private static int configExt$checkInterval(int original) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return original;
        }
        return ExtendedConfig.BEAST_CHECK_INTERVAL_TICKS.get();
    }
}
