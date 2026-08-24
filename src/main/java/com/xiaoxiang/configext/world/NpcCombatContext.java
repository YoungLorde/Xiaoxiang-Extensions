package com.xiaoxiang.configext.world;

/**
 * ThreadLocal flag to track whether the current code execution is within
 * an NPC spell casting context. This allows SpellMixin to apply different
 * damage multipliers for player vs NPC spells.
 *
 * Set by NpcSpellCasterMixin when an NPC casts a spell, cleared after.
 *
 * IMPORTANT: This class MUST NOT be in the mixin package. The Mixin
 * subsystem forbids direct references to classes in mixin packages
 * from transformed target code, causing IllegalClassLoadError at
 * runtime.
 */
public final class NpcCombatContext {
    private static final ThreadLocal<Boolean> NPC_CASTING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static void setNpcCasting(boolean value) {
        NPC_CASTING.set(value);
    }

    public static boolean isNpcCasting() {
        return NPC_CASTING.get();
    }

    private NpcCombatContext() {}
}
