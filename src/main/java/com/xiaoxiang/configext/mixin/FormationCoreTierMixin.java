package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.qi.formation.CoreTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires formations.coreMaxQi.* to the formation core plate's qi capacity.
 *
 * Verified against com/xiaoxiang/cultivation/cultivation/qi/formation/CoreTier.class
 * (javap -p -c -constants): the enum stores a private final long maxQi assigned in
 * &lt;clinit&gt; as LOW=100, MID=1000, HIGH=10000, SUPREME=100000, IMMORTAL=1000000 and
 * exposes it via "public long maxQi()" ( ()J ).
 * FormationCorePlateBlockEntity.getMaxQi() is literally
 * "coreTier().maxQi()", so overriding maxQi() covers the whole formation system.
 *
 * Config defaults match the hardcoded values exactly, so a stock config is a no-op.
 */
@Mixin(value = CoreTier.class, remap = false)
public abstract class FormationCoreTierMixin {

    @Inject(method = "maxQi", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$maxQi(CallbackInfoReturnable<Long> cir) {
        if (!ExtendedConfig.ENABLE_FORMATION_OVERRIDES.get()) {
            return;
        }
        CoreTier self = (CoreTier) (Object) this;
        long value;
        if (self == CoreTier.MID) {
            value = ExtendedConfig.FORMATION_CORE_MAX_QI_MID.get();
        } else if (self == CoreTier.HIGH) {
            value = ExtendedConfig.FORMATION_CORE_MAX_QI_HIGH.get();
        } else if (self == CoreTier.SUPREME) {
            value = ExtendedConfig.FORMATION_CORE_MAX_QI_SUPREME.get();
        } else if (self == CoreTier.IMMORTAL) {
            value = ExtendedConfig.FORMATION_CORE_MAX_QI_IMMORTAL.get();
        } else {
            value = ExtendedConfig.FORMATION_CORE_MAX_QI_LOW.get();
        }
        cir.setReturnValue(value);
    }
}
