package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.talisman.TalismanCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires the first 2 of the Talisman glyph-crafting system's ~110 candidate tunable
 * constants (base mod 0.1.1479's TalismanLimits.class - see CONFIG_AUDIT.md for the
 * full inventory and backlog; the rest are NOT wired yet).
 *
 * TalismanLimits' fields are all `public static final int`/`float` - true compile-time
 * constants, inlined at every use site by javac. Confirmed via javap disassembly of
 * every class under the talisman packages (166 classes) that there are ZERO getstatic
 * references to TalismanLimits anywhere outside its own declaration - every one of its
 * ~110 fields must be traced to its real, individual use site(s) the same way every
 * other constant in this project has been, which is why only these 2 (the clearest,
 * most unambiguous pair found so far) are wired in this pass.
 *
 * Verified via javap -p -c -s against TalismanCompiler.class in
 * xiaoxiang_cultivation-0.1.1479.jar: buildCompiledPlan(TalismanPlan, List, Map, List)
 * (private static, sole method of that name - no overloads) contains a tableswitch on
 * TalismanManifestationType.ordinal() (via the standard $SwitchMap indirection). Case 2
 * (the enum's 2nd declared constant, confirmed = EXPLOSION) does:
 *   fconst_2                                          // 2.0f = DEFAULT_EXPLOSION_RADIUS
 *   invokestatic evolvedAreaRadius(F, TalismanEvolutionBundle)F
 *   invokestatic Float.isFinite(F)Z
 *   ldc_w #353  // float 7.0f                          // MAX_EXPLOSION_RADIUS
 *   fcmpl / ifle -> ok, else raise EXPLOSION_RADIUS_LIMIT compile error
 * Both 2.0f and 7.0f appear exactly once each within this one method (confirmed by
 * grepping the full method body: one `fconst_2`, one `ldc_w ... float 7.0f`) so neither
 * handler needs an ordinal.
 */
@Mixin(TalismanCompiler.class)
public abstract class TalismanCompilerMixin {

    @ModifyConstant(method = "buildCompiledPlan", constant = @Constant(floatValue = 2.0f), remap = false, require = 0)
    private static float configExt$defaultExplosionRadius(float original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return (float) ExtendedConfig.TALISMAN_DEFAULT_EXPLOSION_RADIUS.get().doubleValue();
    }

    @ModifyConstant(method = "buildCompiledPlan", constant = @Constant(floatValue = 7.0f), remap = false, require = 0)
    private static float configExt$maxExplosionRadius(float original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return (float) ExtendedConfig.TALISMAN_MAX_EXPLOSION_RADIUS.get().doubleValue();
    }
}
