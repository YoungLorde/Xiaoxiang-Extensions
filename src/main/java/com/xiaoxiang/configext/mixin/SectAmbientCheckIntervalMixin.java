package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_AMBIENT_CHECK_INTERVAL_TICKS.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 * tickDailyLife(MinecraftServer) gates its entire body behind
 * Math.floorMod(level.getGameTime(), 20) == 0 (a bipush 20). Config's
 * default of 100 never matched - corrected to 20.
 *
 * IMPORTANT SIDE EFFECT: this same 20-tick gate also controls
 * tickSectISystems(ServerLevel), an unrelated subsystem call made right
 * alongside tickAmbientInteractionDataLayer(...) in the same gated block.
 * The real mod does not separate these two concerns - there is only one
 * literal to redirect - so changing SECT_AMBIENT_CHECK_INTERVAL_TICKS will
 * also change how often tickSectISystems runs, not just ambient
 * interactions, despite the config field's name suggesting it's ambient-
 * only. Documented here rather than silently narrowed to "ambient" only,
 * which the bytecode does not actually support.
 *
 * Discovered while investigating this field: the "ambient" config section
 * (SECT_AMBIENT_MAX_SCENES / SECT_AMBIENT_MIN_COOLDOWN_TICKS /
 * SECT_AMBIENT_MAX_COOLDOWN_TICKS) is a genuine duplicate of the already-
 * wired SECT_AMBIENT_MAX_ACTIVE_SCENES_PER_LEVEL / _MIN_SECT_COOLDOWN_TICKS
 * / _MAX_SECT_COOLDOWN_TICKS fields that SectAmbientInteractionRulesMixin
 * (a pre-existing mixin from earlier in this session) already wires -
 * same title, same defaults, only one set has a real handler. Flagged in
 * ExtendedConfig with NOTE comments rather than silently left alone.
 *
 * SECT_AMBIENT_MAX_SPECTATORS and SECT_AMBIENT_NPC_COOLDOWN_TICKS are NOT
 * wired here:
 *   - MAX_SPECTATORS's real consumer was located (addSparringSpectators()
 *     builds a fixed 4-element array of hardcoded {dx,dz} offset pairs -
 *     N/E/S/W standing positions - and loops while counter < array.length).
 *     This isn't a simple int comparison; making the spectator count
 *     configurable would mean replacing the whole fixed offset-array
 *     literal with a generated ring of N positions, a full algorithm
 *     rewrite far outside a config-wiring pass. Config's default (4)
 *     already matches the array's fixed size, so nothing is currently
 *     wrong - it's just structurally unwireable as a constant redirect.
 *   - NPC_COOLDOWN_TICKS's literal 3600 does not appear anywhere in
 *     SectSavedData.class at all (a whole-class literal search came back
 *     empty), so its real consumer lives elsewhere and wasn't located this
 *     session. Config's default (3600) already matches the field's own
 *     declared value, so there's no known mismatch to flag - just an
 *     unlocated consumer.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectAmbientCheckIntervalMixin {

    @ModifyConstant(method = "tickDailyLife(Lnet/minecraft/server/MinecraftServer;)V",
            constant = @Constant(intValue = 20), remap = false, require = 0)
    private static int configExt$ambientCheckIntervalTicks(int original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        int ticks = ExtendedConfig.SECT_AMBIENT_CHECK_INTERVAL_TICKS.get();
        return ticks <= 0 ? original : ticks;
    }
}
