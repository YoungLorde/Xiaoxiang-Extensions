package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.cultivation.technique.TechniqueLoadoutHelper;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes a real, player-reported bug (2026-09-01): "died, respawned,
 * reincarnated, chose an origin again, died a second time, and NOTHING
 * happened - no Difu/underworld prompt at all, just a normal vanilla death."
 * This needs to keep happening on every death outside the Underworld, not
 * just the first.
 *
 * Root cause, verified via javap -p -c -s against the real jar
 * (xiaoxiang_cultivation-0.1.1302.jar):
 *
 * SoulStateHandler.onLivingDeath(LivingDeathEvent) gates the ENTIRE
 * Difu/reincarnation flow behind CultivationData.hasEquippedTechnique():
 *   invokevirtual CultivationData.hasEquippedTechnique:()Z
 *   ifne -> continue to the real soul-state/Difu path (event cancelled)
 *   else -> CorpseDeathHandler.spawnForPlayerDeath(...); return (event NOT
 *           cancelled, vanilla death proceeds, no Difu at all)
 * hasEquippedTechnique() is simply "!getEquippedTechniqueId().isEmpty()".
 *
 * ReincarnationManager.doReincarnate(ServerPlayer, CultivationData) wipes the
 * player's cultivation state on reincarnation:
 *   data.copyFrom(new CultivationData())
 * and the no-arg CultivationData constructor initializes
 * equippedTechniqueId = "" (empty). doReincarnate restores
 * DifuReincarnationEntries and flight state afterward, but NEVER touches
 * equippedTechniqueId again. Its sibling method, doReturnIntact (used for the
 * "return to body intact" outcome, not full reincarnation), DOES call:
 *   TechniqueLoadoutHelper.normalizeForCurrentState(data, player.getRandom())
 * right after its own state reset - and that method (verified via javap)
 * itself calls CultivationData.setEquippedTechniqueId(String) internally, so
 * it genuinely re-equips a technique appropriate to the player's current
 * state. doReincarnate has no equivalent call anywhere in its bytecode.
 *
 * Net effect: after a FULL reincarnation (as opposed to "return intact"),
 * equippedTechniqueId stays permanently blank until the player manually
 * equips a technique/skill book - so hasEquippedTechnique() is false on their
 * very next death, and SoulStateHandler routes them through vanilla death
 * instead of Difu. This is a real asymmetry bug in the base mod, not
 * something this config mod introduced.
 *
 * Fix: inject at the TAIL of doReincarnate and call the exact same
 * normalizeForCurrentState + notifyNormalization pair doReturnIntact already
 * calls, so a reincarnated player also leaves doReincarnate with a real
 * equipped technique, and the Difu flow correctly triggers again on their
 * next death.
 *
 * Caveat: player.getRandom() (returning net.minecraft.util.RandomSource) is
 * standard, extremely well-established Mojang-mapped Minecraft API - but
 * unlike every other claim in this file, it is NOT bytecode-verified in this
 * sandbox (no Minecraft/Forge jars are available here to javap against,
 * same limitation already documented for IdentityDrawScreenMixin's GUI
 * stubs). Everything else in this mixin (doReincarnate's target signature,
 * TechniqueLoadoutHelper's two method signatures, and the fact that
 * normalizeForCurrentState really does call setEquippedTechniqueId) IS
 * verified directly against the real compiled jar.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.event.ReincarnationManager", remap = false)
public abstract class ReincarnationManagerMixin {

    @Inject(
            method = "doReincarnate(Lnet/minecraft/server/level/ServerPlayer;Lcom/xiaoxiang/cultivation/cultivation/CultivationData;)V",
            at = @At("TAIL"),
            remap = false,
            require = 0
    )
    private static void configExt$restoreTechniqueAfterReincarnation(
            ServerPlayer player, CultivationData data, CallbackInfo ci) {
        if (!ExtendedConfig.ENABLE_REINCARNATION_TECHNIQUE_FIX.get()) {
            return;
        }
        TechniqueLoadoutHelper.NormalizationResult result =
                TechniqueLoadoutHelper.normalizeForCurrentState(data, player.getRandom());
        TechniqueLoadoutHelper.notifyNormalization(player, data, result);
    }
}
