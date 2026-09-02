package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Wires SECT_RECRUIT_DISCIPLE_CHANCE.
 *
 * Verified via javap -p -c -s against SectSavedData.class (2026-09-01): the
 * 0.3d literal is the ONLY occurrence of that value anywhere in the class,
 * found in the 6-argument overload of createDataLayerRecruit(SectRecord,
 * MemberRecord, RandomSource, long, boolean, boolean) - the same method
 * SectDiscipleGateMixin already redirects the DISCIPLE_REALM_GATE field
 * read in (a different instruction in the same method, so the two mixins
 * don't conflict). random.nextDouble() &lt; 0.3d decides whether a
 * data-layer recruit becomes an OUTER_DISCIPLE (matches this config field's
 * "disciple recruit chance" naming) or a SERVANT. Config's default of 0.1
 * never matched - corrected to 0.3, resolving the mismatch this audit
 * flagged earlier but couldn't confirm a use site for at the time.
 */
@Mixin(targets = "com.xiaoxiang.cultivation.cultivation.sect.SectSavedData", remap = false)
public abstract class SectRecruitChanceMixin {

    @ModifyConstant(
            method = "createDataLayerRecruit(Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$SectRecord;Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;Lnet/minecraft/util/RandomSource;JZZ)Lcom/xiaoxiang/cultivation/cultivation/sect/SectSavedData$MemberRecord;",
            constant = @Constant(doubleValue = 0.3), remap = false, require = 0)
    private double configExt$recruitDiscipleChance(double original) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return original;
        return ExtendedConfig.SECT_RECRUIT_DISCIPLE_CHANCE.get();
    }
}
