package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.beast.BeastProgressionRules;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the retired {@code BeastRealmMixin} (see that file - kept as an
 * inert placeholder, not deleted, since this sandbox cannot delete files on
 * the user's machine). Base mod 0.1.1479 removed the standalone 8-tier
 * {@code BeastRealm} enum entirely (confirmed: the class is simply absent
 * from the new jar, a genuine deletion not a rename - {@code BeastRealmMixin}
 * would fail to even compile against 0.1.1479 since its
 * {@code import com.xiaoxiang.cultivation.cultivation.realm.BeastRealm;}
 * cannot resolve) and replaced it with {@code BeastProgressionRules}, a
 * final utility class whose static {@code advanceCost(Realm)} routes beast
 * cultivators through the SAME 12-tier {@code Realm} ladder players use
 * (MORTAL -> QI_REFINING -> FOUNDATION_BUILDING -> GOLDEN_CORE ->
 * NASCENT_SOUL -> SOUL_FORMATION -> VOID_REFINING -> BODY_INTEGRATION ->
 * MAHAYANA -> TRIBULATION_TRANSCENDENCE -> TRUE_IMMORTAL -> LOOSE_IMMORTAL),
 * instead of a separate beast-only enum.
 *
 * Verified via javap -p -c -s against BeastProgressionRules.class AND its
 * synthetic BeastProgressionRules$1 switch-map class (2026-09-02) - decoded
 * per the $SwitchMap methodology (see the minecraft-mixin-bytecode-
 * verification skill): this particular switch-map's case numbers land in
 * plain Realm declaration order (case 1 = MORTAL ... case 12 = LOOSE_
 * IMMORTAL), so no surprise reordering here, but this was confirmed by
 * actually decoding the static initializer, not assumed. Real per-tier
 * defaults recovered from the tableswitch targets:
 *   MORTAL = 0 (starting realm, not configurable)
 *   QI_REFINING = 500L
 *   FOUNDATION_BUILDING = 5000L
 *   GOLDEN_CORE = 50000L
 *   NASCENT_SOUL = 500000L
 *   SOUL_FORMATION = 5000000L
 *   VOID_REFINING = 50000000L
 *   BODY_INTEGRATION = 500000000L
 *   MAHAYANA = 5000000000L
 *   TRIBULATION_TRANSCENDENCE = 50000000000L
 *   TRUE_IMMORTAL = 500000000000L
 *   LOOSE_IMMORTAL = Long.MAX_VALUE (final tier, not configurable, matches
 *     vanilla's own uncapped-final-realm convention used elsewhere)
 * The first 6 non-zero values are IDENTICAL to the old BeastRealm enum's
 * SPIRIT_SOLDIER..SPIRIT_LORD defaults - this is a clean extension of the
 * same x10-per-tier curve onto 4 new tiers, not a redesign of the numbers -
 * so the 6 pre-existing "spirit*" config fields keep their original names/
 * TOML keys/values (no player-facing config migration needed) and are simply
 * reinterpreted as "cost to reach the Nth Realm tier" instead of "cost to
 * reach the Nth BeastRealm tier". 4 new fields (BEAST_BODY_INTEGRATION_
 * ADVANCE_COST etc.) cover the 4 additional tiers.
 */
@Mixin(value = BeastProgressionRules.class, remap = false)
public abstract class BeastProgressionRulesMixin {

    @Inject(method = "advanceCost", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void configExt$advanceCost(Realm realm, CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_BEAST_OVERRIDES.get()) {
            return;
        }
        // Use if-else instead of switch, matching the retired BeastRealmMixin's
        // own established convention here: switching on an enum inside a mixin
        // method generates a synthetic $SwitchMap inner class that can cause a
        // NoClassDefFoundError at runtime once Mixin merges this class's bytecode
        // into the target, so if-else avoids that pitfall entirely.
        long val;
        if (realm == Realm.MORTAL) {
            val = 0L;
        } else if (realm == Realm.QI_REFINING) {
            val = ExtendedConfig.SPIRIT_SOLDIER_ADVANCE_COST.get();
        } else if (realm == Realm.FOUNDATION_BUILDING) {
            val = ExtendedConfig.SPIRIT_GENERAL_ADVANCE_COST.get();
        } else if (realm == Realm.GOLDEN_CORE) {
            val = ExtendedConfig.SPIRIT_MARSHAL_ADVANCE_COST.get();
        } else if (realm == Realm.NASCENT_SOUL) {
            val = ExtendedConfig.SPIRIT_KING_ADVANCE_COST.get();
        } else if (realm == Realm.SOUL_FORMATION) {
            val = ExtendedConfig.SPIRIT_EMPEROR_ADVANCE_COST.get();
        } else if (realm == Realm.VOID_REFINING) {
            val = ExtendedConfig.SPIRIT_LORD_ADVANCE_COST.get();
        } else if (realm == Realm.BODY_INTEGRATION) {
            val = ExtendedConfig.BEAST_BODY_INTEGRATION_ADVANCE_COST.get();
        } else if (realm == Realm.MAHAYANA) {
            val = ExtendedConfig.BEAST_MAHAYANA_ADVANCE_COST.get();
        } else if (realm == Realm.TRIBULATION_TRANSCENDENCE) {
            val = ExtendedConfig.BEAST_TRIBULATION_TRANSCENDENCE_ADVANCE_COST.get();
        } else if (realm == Realm.TRUE_IMMORTAL) {
            val = ExtendedConfig.BEAST_TRUE_IMMORTAL_ADVANCE_COST.get();
        } else if (realm == Realm.LOOSE_IMMORTAL) {
            val = Long.MAX_VALUE;
        } else {
            // Unknown/null Realm (e.g. a future tier this mixin doesn't know
            // about yet) - don't cancel, let vanilla's own default branch
            // (which throws IncompatibleClassChangeError) run unmodified.
            return;
        }
        cir.setReturnValue(val);
    }
}
