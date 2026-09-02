package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.event.QiShieldHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Wires QI_SHIELD_QI_PER_DAMAGE and QI_SHIELD_PERFECT_REDUCTION.
 *
 * Verified via javap -p -c -s against QiShieldHandler.class (2026-09-01):
 * there are two private static overloads of qiPerDamage - one for
 * ServerPlayer, one for WanderingCultivatorEntity (NPC) - each computing
 * Math.max(1.0, 10.0 * generalQiCostMultiplier(...)). Both have "10.0" as
 * the sole occurrence of that literal in their own method; each also
 * separately has a "dconst_1" (1.0) used BOTH as the perfect-shield early
 * return AND as the Math.max floor, so that constant is deliberately left
 * untouched (ambiguous which of the two it would mean).
 *
 * Both overloads share the name "qiPerDamage", so each injector below uses
 * a full method descriptor to disambiguate.
 *
 * The config field is a LongValue (legacy default 10L) even though the real
 * target is a double literal - kept as-is for backward compatibility and
 * converted with .doubleValue() at the injection site.
 *
 * QI_SHIELD_PERFECT_REDUCTION was previously marked NOT WIRED because
 * qiPerDamage's own "return 1.0" for a perfect shield is a qi-cost-per-
 * damage value, not a damage-reduction fraction, and how that fed into
 * actual damage reduction wasn't traced. Traced further and found 2026-09-01:
 * there is no damage-reduction *literal* to redirect at all - the real
 * mechanism is structural. maxAbsorbableDamage(ServerPlayer/NPC, float
 * incomingDamage, Realm, int) checks grantsPerfectQiShield(...) first; if
 * true, it returns Math.max(0.0f, incomingDamage) - i.e. the ENTIRE incoming
 * hit becomes absorbable (subject only to available qi via
 * absorbedDamageFromQi/qiPerDamage elsewhere), which is what "perfect"
 * means here: 100% of the damage is eligible for absorption, not "0
 * literal damage". Both overloads have exactly one Math.max(float,float)
 * call each, at that exact branch, confirmed via full-class disassembly (no
 * other Math.max(F,F) call site in either method). Wired by redirecting
 * that call so the fraction of incomingDamage made eligible for absorption
 * is configurable instead of hardcoded to 100%: default 1.0 reproduces
 * vanilla's "fully absorbable" behavior exactly; a lower value caps how
 * much of a hit "perfect" qi shield can ever cover, still gated by the
 * player's/NPC's available qi exactly as before.
 */
@Mixin(value = QiShieldHandler.class, remap = false)
public abstract class QiShieldHandlerMixin {

    @ModifyConstant(method = "qiPerDamage(Lnet/minecraft/server/level/ServerPlayer;)D",
            constant = @Constant(doubleValue = 10.0), remap = false, require = 0)
    private static double configExt$qiPerDamagePlayer(double original) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return original;
        return ExtendedConfig.QI_SHIELD_QI_PER_DAMAGE.get().doubleValue();
    }

    @ModifyConstant(method = "qiPerDamage(Lcom/xiaoxiang/cultivation/entity/npc/WanderingCultivatorEntity;)D",
            constant = @Constant(doubleValue = 10.0), remap = false, require = 0)
    private static double configExt$qiPerDamageNpc(double original) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return original;
        return ExtendedConfig.QI_SHIELD_QI_PER_DAMAGE.get().doubleValue();
    }

    @Redirect(method = "maxAbsorbableDamage(Lnet/minecraft/server/level/ServerPlayer;FLcom/xiaoxiang/cultivation/cultivation/realm/Realm;I)F",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), remap = false, require = 0)
    private static float configExt$perfectReductionPlayer(float floor, float incomingDamage) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return Math.max(floor, incomingDamage);
        double reduction = ExtendedConfig.QI_SHIELD_PERFECT_REDUCTION.get();
        return Math.max(floor, incomingDamage * (float) reduction);
    }

    @Redirect(method = "maxAbsorbableDamage(Lcom/xiaoxiang/cultivation/entity/npc/WanderingCultivatorEntity;FLcom/xiaoxiang/cultivation/cultivation/realm/Realm;I)F",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), remap = false, require = 0)
    private static float configExt$perfectReductionNpc(float floor, float incomingDamage) {
        if (!ExtendedConfig.ENABLE_QI_SYSTEM_OVERRIDES.get()) return Math.max(floor, incomingDamage);
        double reduction = ExtendedConfig.QI_SHIELD_PERFECT_REDUCTION.get();
        return Math.max(floor, incomingDamage * (float) reduction);
    }
}
