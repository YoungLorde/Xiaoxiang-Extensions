package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.sect.SectDepartmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Wires the "sectDepartments" config section to real behaviour.
 *
 * SectDepartmentType is an enum whose per-constant production numbers are
 * stored in instance fields assigned by the enum constructor and exposed via
 * three simple accessors:
 *
 *   public int workPointsPerOutput()  -> ()I
 *   public int dailyOutputCap()       -> ()I
 *   public int inputBufferTarget()    -> ()I
 *
 * Verified against the real bytecode of
 * com/xiaoxiang/cultivation/cultivation/sect/SectDepartmentType.class:
 *
 *   ENFORCEMENT : 0,   0,  0
 *   ALCHEMY     : 120, 24, 4
 *   REFINING    : 180, 12, 4
 *   HERBAL      : 0,   0,  0
 *   MINING      : 0,   0,  0
 *   TREASURY    : 0,   0,  0
 *
 * Because these are plain accessors returning an instance field, a HEAD
 * @Inject that cancels with a config value is exact and cannot mis-target.
 */
@Mixin(SectDepartmentType.class)
public abstract class SectDepartmentTypeMixin {

    @Inject(method = "workPointsPerOutput", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$workPointsPerOutput(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return;
        SectDepartmentType self = (SectDepartmentType) (Object) this;
        if (self == SectDepartmentType.ENFORCEMENT) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ENFORCEMENT_WORK_POINTS_PER_OUTPUT.get());
        } else if (self == SectDepartmentType.ALCHEMY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ALCHEMY_WORK_POINTS_PER_OUTPUT.get());
        } else if (self == SectDepartmentType.REFINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_REFINING_WORK_POINTS_PER_OUTPUT.get());
        } else if (self == SectDepartmentType.HERBAL) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_HERBAL_WORK_POINTS_PER_OUTPUT.get());
        } else if (self == SectDepartmentType.MINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_MINING_WORK_POINTS_PER_OUTPUT.get());
        } else if (self == SectDepartmentType.TREASURY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_TREASURY_WORK_POINTS_PER_OUTPUT.get());
        }
    }

    @Inject(method = "dailyOutputCap", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$dailyOutputCap(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return;
        SectDepartmentType self = (SectDepartmentType) (Object) this;
        if (self == SectDepartmentType.ENFORCEMENT) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ENFORCEMENT_DAILY_OUTPUT_CAP.get());
        } else if (self == SectDepartmentType.ALCHEMY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ALCHEMY_DAILY_OUTPUT_CAP.get());
        } else if (self == SectDepartmentType.REFINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_REFINING_DAILY_OUTPUT_CAP.get());
        } else if (self == SectDepartmentType.HERBAL) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_HERBAL_DAILY_OUTPUT_CAP.get());
        } else if (self == SectDepartmentType.MINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_MINING_DAILY_OUTPUT_CAP.get());
        } else if (self == SectDepartmentType.TREASURY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_TREASURY_DAILY_OUTPUT_CAP.get());
        }
    }

    @Inject(method = "inputBufferTarget", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$inputBufferTarget(CallbackInfoReturnable<Integer> cir) {
        if (!ExtendedConfig.ENABLE_SECT_OVERRIDES.get()) return;
        SectDepartmentType self = (SectDepartmentType) (Object) this;
        if (self == SectDepartmentType.ENFORCEMENT) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ENFORCEMENT_INPUT_BUFFER_TARGET.get());
        } else if (self == SectDepartmentType.ALCHEMY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_ALCHEMY_INPUT_BUFFER_TARGET.get());
        } else if (self == SectDepartmentType.REFINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_REFINING_INPUT_BUFFER_TARGET.get());
        } else if (self == SectDepartmentType.HERBAL) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_HERBAL_INPUT_BUFFER_TARGET.get());
        } else if (self == SectDepartmentType.MINING) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_MINING_INPUT_BUFFER_TARGET.get());
        } else if (self == SectDepartmentType.TREASURY) {
            cir.setReturnValue(ExtendedConfig.SECT_DEPT_TREASURY_INPUT_BUFFER_TARGET.get());
        }
    }
}
