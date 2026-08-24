package com.xiaoxiang.configext;

import com.xiaoxiang.configext.client.AppliedPerkTracker;
import com.xiaoxiang.configext.client.GoldenFingerPerks;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationCapability;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Set;

/**
 * Gives passive cultivation progress per second based on active Golden Finger perks.
 *
 * Certain perks (Heavenly Spirit Root, Accelerated Cultivation, etc.) grant flat
 * cultivation per second even when the player is NOT meditating. When the player
 * IS actively meditating, the passive gain is multiplied by 10x.
 *
 * This uses CultivationData.absorbQi() which increments both totalQiAbsorbed and
 * cultivationProgress — the same method the original mod uses when absorbing Qi
 * from the environment.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PassiveCultivationHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TICK_INTERVAL = 20; // 1 second
    private static final int SYNC_INTERVAL_TICKS = 100; // sync every 5 seconds

    // Per-player tick counters to avoid multiplayer issues
    private static final java.util.Map<java.util.UUID, Integer> playerTickCounters = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<java.util.UUID, Integer> playerSyncCounters = new java.util.concurrent.ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        java.util.UUID uuid = player.getUUID();
        int tickCounter = playerTickCounters.getOrDefault(uuid, 0) + 1;
        if (tickCounter < TICK_INTERVAL) {
            playerTickCounters.put(uuid, tickCounter);
            return;
        }
        playerTickCounters.put(uuid, 0);

        // Get the player's cultivation data
        CultivationData data = CultivationCapability.get(player).orElse(null);
        if (data == null) return;
        if (!data.hasChosenIdentity()) return;

        // Don't give passive cultivation if at max
        if (data.getCultivationProgress() >= data.getMaxCultivation()) return;

        // Calculate flat cultivation per second based on active perks
        int flatPerSecond = calculateFlatCultivationPerSecond();
        if (flatPerSecond <= 0) return;

        // If the player is actively meditating, apply multiplier
        boolean meditating = data.isMeditating();
        int totalGain;
        if (meditating) {
            // Check if crouch-meditating (lower multiplier) vs cushion-meditating (10x)
            boolean crouchMeditating = com.xiaoxiang.configext.client.CrouchMeditationState.isCrouchMeditating(uuid);
            double mult = crouchMeditating
                    ? ExtendedConfig.CROUCH_MEDITATION_MULT.get()
                    : 10.0;
            totalGain = (int) Math.max(1, flatPerSecond * mult);
        } else {
            totalGain = flatPerSecond;
        }

        if (totalGain <= 0) return;

        // Apply the gain via absorbQi — the same method the original mod uses
        // for Qi absorption from the environment. This increments both
        // totalQiAbsorbed and cultivationProgress.
        data.absorbQi(totalGain);

        // Sync to client less frequently to avoid network spam
        // (every 5 seconds instead of every 1 second)
        int syncCounter = playerSyncCounters.getOrDefault(uuid, 0) + 1;
        if (syncCounter >= SYNC_INTERVAL_TICKS / TICK_INTERVAL) {
            com.xiaoxiang.cultivation.event.CapabilityEvents.syncToClient(player);
            playerSyncCounters.put(uuid, 0);
        } else {
            playerSyncCounters.put(uuid, syncCounter);
        }
    }

    /**
     * Calculate the flat cultivation per second based on active perks.
     *
     * Perk IDs and their flat cultivation contributions:
     * - Heavenly Spirit Root (16): 10/s — tier 5 spirit root
     * - Heavenly Fire Root (22): 10/s — tier 5 spirit root
     * - Heavenly Ice Root (23): 10/s — tier 5 spirit root
     * - Heavenly Sword Root (24): 10/s — tier 5 spirit root
     * - Primal Root (29): 10/s — tier 5 spirit root
     * - Root of Chaos (30): 10/s — tier 5 spirit root
     * - Accelerated Cultivation (36): 5/s — tier 4
     * - Triple Cultivation Speed (37): 10/s — tier 5
     * - Qi Devourer (38): 5/s — tier 4
     * - Meditation Master (39): 3/s — tier 3
     */
    private static int calculateFlatCultivationPerSecond() {
        Set<Integer> perkIds = AppliedPerkTracker.getAppliedPerkIds();
        if (perkIds.isEmpty()) return 0;

        int total = 0;
        for (int perkId : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(perkId);
            if (perk == null) continue;

            // Only cultivation-related perks give passive cultivation
            total += getFlatCultivationForPerk(perkId, perk);
        }
        return total;
    }

    private static int getFlatCultivationForPerk(int perkId, GoldenFingerPerks.Perk perk) {
        // Heavenly Spirit Root variants (tier 5) — 10/s each
        if (perkId == 16 || perkId == 22 || perkId == 23 || perkId == 24 ||
            perkId == 29 || perkId == 30) {
            return 10;
        }
        // Accelerated Cultivation (tier 4) — 5/s
        if (perkId == 36) {
            return 5;
        }
        // Triple Cultivation Speed (tier 5) — 10/s
        if (perkId == 37) {
            return 10;
        }
        // Qi Devourer (tier 4) — 5/s
        if (perkId == 38) {
            return 5;
        }
        // Meditation Master (tier 3) — 3/s
        if (perkId == 39) {
            return 3;
        }
        // SSR Qi Absorption (tier 4) — 5/s
        if (perkId == 25) {
            return 5;
        }
        // SR Qi Absorption (tier 3) — 3/s
        if (perkId == 26) {
            return 3;
        }
        // Root Enhancement (tier 4) — 5/s
        if (perkId == 27) {
            return 5;
        }
        // Perfect Root (tier 4) — 5/s
        if (perkId == 28) {
            return 5;
        }
        return 0;
    }
}
