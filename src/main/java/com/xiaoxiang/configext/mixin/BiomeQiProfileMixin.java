package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.BiomeQiProfile;
import com.xiaoxiang.cultivation.cultivation.QiElement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides biome qi density values with config-driven ones.
 * The element assignment per biome is preserved; only the density scalar is overridden.
 */
@Mixin(BiomeQiProfile.class)
public abstract class BiomeQiProfileMixin {

    @Inject(method = "of", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$of(Holder<Biome> biome, CallbackInfoReturnable<BiomeQiProfile> cir) {
        if (!ExtendedConfig.ENABLE_QI_OVERRIDES.get()) {
            return;
        }
        BiomeQiProfile original = cir.getReturnValue();
        if (original == null) {
            return;
        }

        // Determine which density category the original profile belongs to and override
        double newDensity = original.density();

        // Match by reference to the original static profiles
        if (original.density() == 0.1 && original.element() == QiElement.EARTH) {
            newDensity = ExtendedConfig.QI_DENSITY_SPARSE.get();
        } else if (original.density() == 0.35 && original.element() == QiElement.PURE) {
            newDensity = ExtendedConfig.QI_DENSITY_NORMAL.get();
        } else if (original.density() == 0.55 && original.element() == QiElement.WOOD) {
            newDensity = ExtendedConfig.QI_DENSITY_WOOD_RICH.get();
        } else if (original.density() == 0.5 && original.element() == QiElement.WATER) {
            newDensity = ExtendedConfig.QI_DENSITY_WATER_RICH.get();
        } else if (original.density() == 0.6 && original.element() == QiElement.FIRE) {
            newDensity = ExtendedConfig.QI_DENSITY_FIRE_RICH.get();
        } else if (original.density() == 0.45 && original.element() == QiElement.EARTH) {
            newDensity = ExtendedConfig.QI_DENSITY_EARTH_RICH.get();
        } else if (original.density() == 0.5 && original.element() == QiElement.ICE) {
            newDensity = ExtendedConfig.QI_DENSITY_ICE_RICH.get();
        } else if (original.density() == 0.45 && original.element() == QiElement.PURE) {
            newDensity = ExtendedConfig.QI_DENSITY_END_PURE.get();
        }

        if (newDensity != original.density()) {
            cir.setReturnValue(new BiomeQiProfile(newDensity, original.element()));
        }
    }
}
