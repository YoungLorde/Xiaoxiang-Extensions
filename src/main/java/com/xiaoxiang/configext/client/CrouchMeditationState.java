package com.xiaoxiang.configext.client;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players are crouch-meditating (vs cushion-meditating).
 *
 * This is used by PassiveCultivationHandler and QiTransferTickHandlerMixin
 * to apply a lower multiplier for crouch-meditation compared to cushion
 * meditation.
 *
 * - Client-side: tracks the local player's crouch-meditation state
 * - Server-side: tracks all players' crouch-meditation state (set by
 *   CrouchMeditationPacket.handle)
 */
public class CrouchMeditationState {

    // Client-side state
    private static volatile boolean clientCrouchMeditating = false;

    // Server-side state: tracks which players are crouch-meditating
    private static final Set<UUID> serverCrouchMeditating = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // ── Client-side ──

    public static void setCrouchMeditating(boolean meditating) {
        clientCrouchMeditating = meditating;
    }

    public static boolean isClientCrouchMeditating() {
        return clientCrouchMeditating;
    }

    // ── Server-side ──

    public static void setServerCrouchMeditating(UUID playerUuid, boolean meditating) {
        if (meditating) {
            serverCrouchMeditating.add(playerUuid);
        } else {
            serverCrouchMeditating.remove(playerUuid);
        }
    }

    public static boolean isCrouchMeditating(UUID playerUuid) {
        return serverCrouchMeditating.contains(playerUuid);
    }

    public static void clearServer(UUID playerUuid) {
        serverCrouchMeditating.remove(playerUuid);
    }
}
