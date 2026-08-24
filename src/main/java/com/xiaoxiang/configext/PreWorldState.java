package com.xiaoxiang.configext;

import net.minecraft.client.gui.screens.Screen;

import java.util.Set;

/**
 * Holder for pre-world creation state shared between mixins.
 *
 * This class is NOT a mixin - it's a regular class that mixin classes
 * can access. It MUST NOT be in the mixin package, because Mixin claims
 * ownership of all classes in mixin packages and prevents direct references.
 */
public class PreWorldState {
    public static boolean bypassOnCreateInterceptor = false;
    public static Screen createWorldScreen = null;
    public static boolean pendingWorldCreation = false;
    public static String storedIdentityId = null;
    public static String storedSpiritRootId = null;
    public static String storedPhysiqueId = null;
    public static boolean hasPreWorldOrigin = false;
    public static Set<Integer> storedPerkIds = null;
    public static String pendingIdentityId = null;
    public static String pendingStartingItems = null;

    /** Reset transient state (keeps stored origin/perks for post-login). */
    public static void reset() {
        bypassOnCreateInterceptor = false;
        createWorldScreen = null;
        pendingWorldCreation = false;
    }

    /** Clear all state completely. */
    public static void clearAll() {
        reset();
        storedIdentityId = null;
        storedSpiritRootId = null;
        storedPhysiqueId = null;
        hasPreWorldOrigin = false;
        storedPerkIds = null;
        pendingIdentityId = null;
        pendingStartingItems = null;
    }
}
