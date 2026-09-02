package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Wires SECT_DISCIPLE_REALM_GATE / SECT_DISCIPLE_SUB_STAGE_GATE.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01):
 * DISCIPLE_REALM_GATE (Realm) and DISCIPLE_SUB_STAGE_GATE (SubStage) are
 * public static final fields, but - unlike primitives - enum-typed static
 * finals are NOT compile-time-inlined by javac, so every use site is a real
 * getstatic that can be redirected. Found exactly 4 methods with getstatic
 * reads of these two fields:
 *
 *   - meetsDiscipleRealmGate(Realm, SubStage): the actual gate check, used
 *     by the other two overloads of the same name. Computes
 *     candidate.progressIndex(subStage) >= DISCIPLE_REALM_GATE.progressIndex(
 *     DISCIPLE_SUB_STAGE_GATE) - both fields read once each here.
 *   - createDataLayerRecruit(SectRecord, MemberRecord, RandomSource, long,
 *     boolean, boolean): when a boolean flag is set, a freshly-recruited
 *     disciple's starting realm/sub-stage id string is read directly off
 *     these same two fields (so new disciples start exactly at the gate
 *     threshold) instead of the MORTAL/EARLY default used for ordinary
 *     recruits.
 *   - lambda$tryRecruitLoadedLoneCultivator$75(WanderingCultivatorEntity,
 *     WanderingCultivatorEntity) and
 *     lambda$acquireJourneyTarget$55(WanderingCultivatorEntity,
 *     WanderingCultivatorEntity): both read DISCIPLE_REALM_GATE only
 *     (not the sub-stage field) as part of comparator/filter logic when
 *     picking which loaded lone cultivator to recruit.
 *
 * All 4 sites are covered by one @Redirect per field (Mixin's `method`
 * array applies the same redirect to every listed target), so the config
 * value is the single source of truth everywhere the base mod reads the
 * gate, not just the boolean check.
 *
 * The @At(value = "FIELD", opcode = GETSTATIC) redirect mechanism itself is
 * well-established, widely-documented Mixin/ASM API - not something this
 * project has used before now (all prior fixes were INVOKE-based @Redirect
 * or @ModifyConstant), and not bytecode-verifiable against the real
 * Mixin/ASM library in this sandbox (only this mod's own compiled classes
 * are available to javap here). Flagged honestly per this project's
 * convention for that class of claim.
 *
 * UPDATED 2026-09-02 for base mod 0.1.1479: re-verified against the new jar
 * (javap -p -c -s). DISCIPLE_REALM_GATE / DISCIPLE_SUB_STAGE_GATE themselves,
 * meetsDiscipleRealmGate(Realm, SubStage), and the 6-arg createDataLayerRecruit
 * overload are all byte-for-byte unchanged (a new delegating 5-arg
 * createDataLayerRecruit overload was added that just calls the 6-arg one
 * with a hardcoded extra `true`, so it's already covered transitively - no
 * separate redirect needed for it). The two lambda names DID shift because an
 * extra lambda was introduced earlier in each enclosing method: the
 * boolean(WanderingCultivatorEntity, WanderingCultivatorEntity) lambda in
 * tryRecruitLoadedLoneCultivator moved from index 75 to 76, and the
 * corresponding lambda in acquireJourneyTarget moved from index 55 to 56
 * (confirmed by matching each lambda's parameter signature, not just its
 * index, between the old and new jars - the old jar's plain "$55" name in
 * the new jar now belongs to a completely different double-returning
 * lambda).
 *
 * CORRECTED 2026-09-02 (second pass, after a real launch crash): "byte-for-
 * byte unchanged" above was wrong on one axis - method BODY/descriptor was
 * unchanged, but createDataLayerRecruit(6-arg) and the new
 * lambda$acquireJourneyTarget$56 both changed from static to INSTANCE
 * methods in 0.1.1479 (confirmed via javap -p: both now lack the `static`
 * keyword, and their call sites use invokevirtual instead of invokestatic).
 * meetsDiscipleRealmGate(Realm, SubStage) and
 * lambda$tryRecruitLoadedLoneCultivator$76 are still static. Mixin's
 * @Redirect requires the handler's own static modifier to match every
 * target method it's applied to via `method = {...}` - mixing static and
 * instance targets under one handler throws MixinApplyError /
 * InvalidInjectionException ("'static' modifier of handler method does not
 * match target") at game launch, and require = 0 does NOT soften this
 * (require only covers "target not found by name", not "found but wrong
 * staticness" - see CONFIG_AUDIT.md). Fixed by splitting each of the two
 * original handlers into a static variant (for the still-static targets)
 * and a new non-static "Instance" variant (for the now-instance targets);
 * the instance variants don't use `this` at all, they just need to not be
 * `static` so Mixin will accept them for an instance-method target.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectDiscipleGateMixin {

    @Shadow
    public static Realm DISCIPLE_REALM_GATE;
    @Shadow
    public static SubStage DISCIPLE_SUB_STAGE_GATE;

    private static Realm configExt$gateRealm() {
        Realm[] realms = Realm.values();
        int ordinal = ExtendedConfig.SECT_DISCIPLE_REALM_GATE.get();
        if (ordinal < 0) ordinal = 0;
        if (ordinal >= realms.length) ordinal = realms.length - 1;
        return realms[ordinal];
    }

    private static SubStage configExt$gateSubStage() {
        SubStage[] subStages = SubStage.values();
        int ordinal = ExtendedConfig.SECT_DISCIPLE_SUB_STAGE_GATE.get();
        if (ordinal < 0) ordinal = 0;
        if (ordinal >= subStages.length) ordinal = subStages.length - 1;
        return subStages[ordinal];
    }

    @Redirect(
            method = {
                    "meetsDiscipleRealmGate(Lcom/xiaoxiang/cultivation/cultivation/realm/Realm;Lcom/xiaoxiang/cultivation/cultivation/realm/SubStage;)Z",
                    "lambda$tryRecruitLoadedLoneCultivator$76"
            },
            at = @At(value = "FIELD",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData;DISCIPLE_REALM_GATE:Lcom/xiaoxiang/cultivation/cultivation/realm/Realm;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC),
            remap = false, require = 0)
    private static Realm configExt$redirectRealmGate() {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return DISCIPLE_REALM_GATE;
        return configExt$gateRealm();
    }

    /**
     * Same override as configExt$redirectRealmGate, but non-static: both
     * createDataLayerRecruit(6-arg) and lambda$acquireJourneyTarget$56
     * became instance methods in base mod 0.1.1479 (see class doc), so
     * Mixin requires a non-static handler for them specifically. Doesn't
     * use `this` - it's non-static purely to satisfy Mixin's
     * static-modifier-must-match-target check.
     */
    @Redirect(
            method = {
                    "createDataLayerRecruit(Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$SectRecord;Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;Lnet/minecraft/util/RandomSource;JZZ)Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;",
                    "lambda$acquireJourneyTarget$56"
            },
            at = @At(value = "FIELD",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData;DISCIPLE_REALM_GATE:Lcom/xiaoxiang/cultivation/cultivation/realm/Realm;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC),
            remap = false, require = 0)
    private Realm configExt$redirectRealmGateInstance() {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return DISCIPLE_REALM_GATE;
        return configExt$gateRealm();
    }

    @Redirect(
            method = {
                    "meetsDiscipleRealmGate(Lcom/xiaoxiang/cultivation/cultivation/realm/Realm;Lcom/xiaoxiang/cultivation/cultivation/realm/SubStage;)Z"
            },
            at = @At(value = "FIELD",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData;DISCIPLE_SUB_STAGE_GATE:Lcom/xiaoxiang/cultivation/cultivation/realm/SubStage;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC),
            remap = false, require = 0)
    private static SubStage configExt$redirectSubStageGate() {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return DISCIPLE_SUB_STAGE_GATE;
        return configExt$gateSubStage();
    }

    /**
     * Same override as configExt$redirectSubStageGate, but non-static: see
     * configExt$redirectRealmGateInstance's doc - createDataLayerRecruit
     * (6-arg) became an instance method in 0.1.1479.
     */
    @Redirect(
            method = {
                    "createDataLayerRecruit(Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$SectRecord;Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;Lnet/minecraft/util/RandomSource;JZZ)Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;"
            },
            at = @At(value = "FIELD",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData;DISCIPLE_SUB_STAGE_GATE:Lcom/xiaoxiang/cultivation/cultivation/realm/SubStage;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC),
            remap = false, require = 0)
    private SubStage configExt$redirectSubStageGateInstance() {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return DISCIPLE_SUB_STAGE_GATE;
        return configExt$gateSubStage();
    }
}
