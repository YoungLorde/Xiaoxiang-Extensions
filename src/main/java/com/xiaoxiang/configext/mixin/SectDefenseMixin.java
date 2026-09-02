package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_DEFENSE_CRITICAL_HEALTH_RATIO.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 *
 *   handleSectMemberAttacked(ServerLevel, LivingEntity, LivingEntity)
 *   computes attacker.getHealth() / attacker.getMaxHealth() and compares it
 *   against the literal 0.25d ("if ratio > 0.25, return early - not
 *   critical"); when the ratio is <= 0.25 it falls through to
 *   handleSectMemberEmergency(level, victim, attacker, false). The 0.25d
 *   matches SECT_DEFENSE_CRITICAL_HEALTH_RATIO's own config default and its
 *   dead SectSavedData.DEFENSE_CRITICAL_HEALTH_RATIO field's would-be value
 *   (same "dead field, real work via inlined literal" pattern as
 *   SectLifeTickIntervalMixin/SectAncestorProfileMixin) - a single
 *   occurrence in this method, no ordinal needed.
 *
 * SECT_DEFENSE_ESCAPE_RADIUS, SECT_DEFENSE_CRITICAL_RESPONDER_LIMIT, and
 * SECT_DEFENSE_DEATH_RESPONDER_LIMIT are NOT wired here - confirmed
 * unwireable, not merely unresearched:
 *
 *   - DEFENSE_ESCAPE_RADIUS (public double, ConstantValue-free) has ZERO
 *     getstatic references anywhere in the extracted jar. The AABB search
 *     box actually used by selectLocalDefenseResponders() is inflated by
 *     hardcoded (32.0, 16.0, 32.0) literals that read from local variables
 *     built off DEFENSE_RESPONDER_SEARCH_RADIUS/DEFENSE_RESPONDER_SEARCH_
 *     VERTICAL instead (two separate private fields with no config
 *     counterpart at all) - a genuinely different, non-config-backed
 *     concept that only coincidentally shares the value 32.0.
 *   - DEFENSE_CRITICAL_RESPONDER_LIMIT (public int, default 5) and
 *     DEFENSE_DEATH_RESPONDER_LIMIT (public int, default 8) likewise have
 *     ZERO getstatic references anywhere in the jar. The real responder
 *     cap comes from DefenseIncident.create(LivingEntity, BlockPos, long,
 *     int), whose 4th argument is hardcoded at its sole call site (inside
 *     handleSectMemberEmergency) to the literal 3 (critical) or 2
 *     (non-critical/death) - not read from either config-sized field - and
 *     is then clamped again to Math.max(1, Math.min(3, arg)) inside
 *     create() itself. Even if these two fields' literals were somehow
 *     located and redirected, the real cap is hard-limited to 3 responders
 *     by this double clamp, so the configured 5/8 defaults could never
 *     actually take effect as authored. Left unwired and documented rather
 *     than wired to a value the base mod would immediately clamp away.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectDefenseMixin {

    @ModifyConstant(method = "handleSectMemberAttacked", constant = @Constant(doubleValue = 0.25), remap = false, require = 0)
    private double configExt$criticalHealthRatio(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_DEFENSE_CRITICAL_HEALTH_RATIO.get();
    }
}
