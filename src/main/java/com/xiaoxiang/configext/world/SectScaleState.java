package com.xiaoxiang.configext.world;

/**
 * Holds the forced sect scale state outside of the mixin package.
 * Mixin classes cannot have public static methods, so this utility
 * class stores the state that SectSizeMixin reads.
 *
 * IMPORTANT: This class MUST NOT be in the mixin package. The Mixin
 * subsystem forbids direct references to classes in mixin packages
 * from transformed target code, causing IllegalClassLoadError at
 * runtime during world generation.
 */
public final class SectScaleState {
    private static int forcedScale = -1;

    /** Force the next sect generation to use the given scale tier (0-12). */
    public static void forceNextSectScale(int scale) {
        forcedScale = Math.max(0, Math.min(12, scale));
    }

    /** Check if a forced scale is pending. */
    public static boolean hasForcedScale() {
        return forcedScale >= 0;
    }

    /** Consume and clear the forced scale. Returns -1 if none pending. */
    public static int consumeForcedScale() {
        int s = forcedScale;
        forcedScale = -1;
        return s;
    }

    private SectScaleState() {}
}
