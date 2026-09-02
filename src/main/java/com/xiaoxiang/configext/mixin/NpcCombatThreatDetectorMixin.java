package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.entity.npc.NpcCombatThreatDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides the hardcoded NPC combat profile (dodge chance, scan ticks,
 * reaction ticks, dodge cooldown) with config-driven values.
 *
 * The original NpcCombatThreatDetector.profile() creates ReactionProfile
 * records with hardcoded values per realm. This mixin intercepts that
 * method and returns config-driven profiles instead.
 */
@Mixin(NpcCombatThreatDetector.class)
public abstract class NpcCombatThreatDetectorMixin {

    @Inject(method = "profile", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$profile(Realm realm, CallbackInfoReturnable<Object> cir) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) {
            return;
        }

        Realm effectiveRealm = realm != null ? realm : Realm.MORTAL;

        // Read config values for this realm
        int scanTicks = getScanTicks(effectiveRealm);
        int reactionTicks = getReactionTicks(effectiveRealm);
        double dodgeChance = getDodgeChance(effectiveRealm);
        int dodgeCooldown = getDodgeCooldown(effectiveRealm);

        // Create a new ReactionProfile with config-driven values
        // ReactionProfile is a record: (scanIntervalTicks, reactionDelayTicks, dodgeChance, dodgeCooldownTicks)
        try {
            Class<?> profileClass = com.xiaoxiang.cultivation.entity.npc.NpcCombatThreatDetector.ReactionProfile.class;
            Object profile = profileClass.getDeclaredConstructor(int.class, int.class, double.class, int.class)
                    .newInstance(scanTicks, reactionTicks, dodgeChance, dodgeCooldown);
            cir.setReturnValue(profile);
        } catch (Exception e) {
            // If reflection fails, let the original method run
        }
    }

    @Inject(method = "cappedChance", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$cappedChance(double chance, CallbackInfoReturnable<Double> cir) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) {
            return;
        }
        double cap = ExtendedConfig.NPC_COMBAT_HARD_DODGE_CAP.get();
        cir.setReturnValue(Math.max(0.0, Math.min(cap, chance)));
    }

    /**
     * Added 2026-09-01. findThreat(WanderingCultivatorEntity, boolean) is the
     * per-scan candidate-gathering method (called every scan cycle from the
     * already-covered tick() path). Verified via javap -p -c -s: a single
     * "ldc2_w 13.0d" feeds an AABB.inflate(...) call that gathers every
     * candidate threat (projectile/collision/area/tnt alike - the field name
     * "PROJECTILE_SCAN_RADIUS" is a slight misnomer in the base mod, but
     * there's only one radius here to control), and a single "ldc2_w 48l"
     * feeds Stream.limit(...) capping how many sorted candidates survive.
     */
    @ModifyConstant(method = "findThreat", constant = @Constant(doubleValue = 13.0), remap = false, require = 0)
    private double configExt$projectileScanRadius(double original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_COMBAT_PROJECTILE_SCAN_RADIUS.get();
    }

    @ModifyConstant(method = "findThreat", constant = @Constant(longValue = 48L), remap = false, require = 0)
    private long configExt$maxCandidates(long original) {
        if (!ExtendedConfig.ENABLE_NPC_COMBAT_OVERRIDES.get()) return original;
        return ExtendedConfig.NPC_COMBAT_MAX_CANDIDATES.get();
    }

    private static int getScanTicks(Realm realm) {
        if (realm == Realm.MORTAL) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_MORTAL.get();
        } else if (realm == Realm.QI_REFINING) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_QI_REFINING.get();
        } else if (realm == Realm.FOUNDATION_BUILDING) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_FOUNDATION.get();
        } else if (realm == Realm.GOLDEN_CORE) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_GOLDEN_CORE.get();
        } else if (realm == Realm.NASCENT_SOUL) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_NASCENT_SOUL.get();
        } else if (realm == Realm.SOUL_FORMATION) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_SOUL_FORMATION.get();
        } else if (realm == Realm.VOID_REFINING) {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_VOID_REFINING.get();
        } else {
            return ExtendedConfig.NPC_COMBAT_SCAN_TICKS_HIGHER.get();
        }
    }

    private static int getReactionTicks(Realm realm) {
        if (realm == Realm.MORTAL) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_MORTAL.get();
        } else if (realm == Realm.QI_REFINING) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_QI_REFINING.get();
        } else if (realm == Realm.FOUNDATION_BUILDING) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_FOUNDATION.get();
        } else if (realm == Realm.GOLDEN_CORE) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_GOLDEN_CORE.get();
        } else if (realm == Realm.NASCENT_SOUL) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_NASCENT_SOUL.get();
        } else if (realm == Realm.SOUL_FORMATION) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_SOUL_FORMATION.get();
        } else if (realm == Realm.VOID_REFINING) {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_VOID_REFINING.get();
        } else {
            return ExtendedConfig.NPC_COMBAT_REACTION_TICKS_HIGHER.get();
        }
    }

    private static double getDodgeChance(Realm realm) {
        if (realm == Realm.MORTAL) {
            return ExtendedConfig.NPC_COMBAT_DODGE_MORTAL.get();
        } else if (realm == Realm.QI_REFINING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_QI_REFINING.get();
        } else if (realm == Realm.FOUNDATION_BUILDING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_FOUNDATION.get();
        } else if (realm == Realm.GOLDEN_CORE) {
            return ExtendedConfig.NPC_COMBAT_DODGE_GOLDEN_CORE.get();
        } else if (realm == Realm.NASCENT_SOUL) {
            return ExtendedConfig.NPC_COMBAT_DODGE_NASCENT_SOUL.get();
        } else if (realm == Realm.SOUL_FORMATION) {
            return ExtendedConfig.NPC_COMBAT_DODGE_SOUL_FORMATION.get();
        } else if (realm == Realm.VOID_REFINING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_VOID_REFINING.get();
        } else {
            return ExtendedConfig.NPC_COMBAT_DODGE_HIGHER.get();
        }
    }

    private static int getDodgeCooldown(Realm realm) {
        if (realm == Realm.MORTAL) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_MORTAL.get();
        } else if (realm == Realm.QI_REFINING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_QI_REFINING.get();
        } else if (realm == Realm.FOUNDATION_BUILDING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_FOUNDATION.get();
        } else if (realm == Realm.GOLDEN_CORE) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_GOLDEN_CORE.get();
        } else if (realm == Realm.NASCENT_SOUL) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_NASCENT_SOUL.get();
        } else if (realm == Realm.SOUL_FORMATION) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_SOUL_FORMATION.get();
        } else if (realm == Realm.VOID_REFINING) {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_VOID_REFINING.get();
        } else {
            return ExtendedConfig.NPC_COMBAT_DODGE_COOLDOWN_HIGHER.get();
        }
    }
}
