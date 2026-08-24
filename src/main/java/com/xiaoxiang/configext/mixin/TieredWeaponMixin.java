package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.ItemTier;
import com.xiaoxiang.cultivation.item.weapon.SpiritSwordItem;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Overrides spell qi cost reduction for spirit sword weapons.
 * spellQiCostReductionPct is a default method in the TieredWeapon interface
 * that SpiritSwordItem does not override, so we add the override here.
 */
@Mixin(SpiritSwordItem.class)
public abstract class TieredWeaponMixin {

    /**
     * Override the default interface method with config-driven values.
     * This method is added to SpiritSwordItem by the mixin, effectively
     * overriding TieredWeapon.spellQiCostReductionPct().
     */
    public int spellQiCostReductionPct() {
        SpiritSwordItem self = (SpiritSwordItem) (Object) this;
        ItemTier tier = self.tier();
        if (!ExtendedConfig.ENABLE_WEAPON_OVERRIDES.get()) {
            // Match original mod defaults
            return switch (tier) {
                default -> 5;
                case MID -> 7;
                case HIGH -> 10;
                case SUPREME -> 15;
                case IMMORTAL -> 20;
            };
        }
        return switch (tier) {
            default -> ExtendedConfig.WEAPON_SPELL_QI_COST_REDUCTION_LOW.get();
            case MID -> ExtendedConfig.WEAPON_SPELL_QI_COST_REDUCTION_MID.get();
            case HIGH -> ExtendedConfig.WEAPON_SPELL_QI_COST_REDUCTION_HIGH.get();
            case SUPREME -> ExtendedConfig.WEAPON_SPELL_QI_COST_REDUCTION_SUPREME.get();
            case IMMORTAL -> ExtendedConfig.WEAPON_SPELL_QI_COST_REDUCTION_IMMORTAL.get();
        };
    }
}
