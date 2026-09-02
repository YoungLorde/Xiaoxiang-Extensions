package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.talisman.TalismanGlyphCatalog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Wave 2 of the Talisman glyph-crafting audit (see CONFIG_AUDIT.md, "Base mod
 * migration to 0.1.1479"): wires 85 of TalismanLimits' ~90 per-glyph *_QI_COST
 * constants (82 distinct config fields - 4 legacy trigger glyphs share one
 * generic TRIGGER_QI_COST constant).
 *
 * Unlike TalismanCompilerMixin (which targets a real *consumer* of a cost
 * value), this targets the *source*: TalismanGlyphCatalog's 7 private
 * register*(Map) methods, each of which calls one of 3 private factory
 * methods (trigger/evolution/manifestation) once per glyph, passing that
 * glyph's Qi cost as the FINAL int argument (confirmed via javap -p -c -s:
 * trigger(RL,String,int,TriggerType,EnumSet,int cost) and
 * evolution(RL,String,int,Function,EvolutionType,int cost) both have the cost
 * at argument index 5; manifestation(RL,String,int,Category,Element,Function,
 * EnumSet,ManifestationType,int,int cost) has it at index 9).
 *
 * Every one of the 85 (register method, factory, ordinal, cost value) tuples
 * below was extracted programmatically from the real disassembly (tracking
 * each getstatic read of a glyph's own ResourceLocation constant, then the
 * final int literal pushed before the matching trigger/evolution/manifestation
 * invokestatic) and then cross-checked against TalismanLimits' own declared
 * field values by name (e.g. TalismanGlyphCatalog's FIREBALL ResourceLocation
 * + a manifestation() call -&gt; expected name FIREBALL_QI_COST -&gt; TalismanLimits
 * says 17 -&gt; the extracted literal at that call site is also 17). All 85
 * matched exactly except the naming convention differs for 9 "legacy" glyphs,
 * which use shorter, more generic TalismanLimits names instead of a
 * per-glyph-prefixed one (verified the same way, just against the generic
 * name): ON_HURT/LOW_HEALTH/NEARBY_LIVING/CONTACT (trigger) all share
 * TRIGGER_QI_COST=11; DAMAGE_X2/DESTRUCTION_X2/PIERCE_PLUS_ONE/
 * POTENCY_PLUS_ONE/DURATION_X2 (evolution) use DAMAGE_QI_COST=15,
 * DESTRUCTION_QI_COST=16, PIERCE_QI_COST=20, POTENCY_QI_COST=16, and
 * DURATION_QI_COST=15 respectively.
 *
 * Each @ModifyArg is scoped to one specific call site via `ordinal` (the Nth
 * call to that exact factory method within that one register method, 0-based,
 * in source order) - required because every register method calls
 * trigger/evolution/manifestation many times, once per glyph, with different
 * literal arguments each time.
 *
 * NOT wired here (tracked in CONFIG_AUDIT.md as remaining backlog): the ~21
 * structural TalismanLimits caps (MAX_EXECUTABLE_SLOTS etc. - already has 2
 * of them, the explosion radius pair, wired via TalismanCompilerMixin), and a
 * handful of QI_COST-named fields not found in this catalog's call sites at
 * all (e.g. MODE_QI_COST, which TalismanQiCost.total() reads via
 * TalismanUseMode.qiCost() - a different, not-yet-traced source entirely).
 */
@Mixin(TalismanGlyphCatalog.class)
public abstract class TalismanGlyphCatalogMixin {

    /** ON_HURT (trigger, in registerLegacyGlyphs) - TalismanLimits.TRIGGER_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_on_hurt_trigger_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_TRIGGER_QI_COST.get();
    }

    /** LOW_HEALTH (trigger, in registerLegacyGlyphs) - TalismanLimits.TRIGGER_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_low_health_trigger_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_TRIGGER_QI_COST.get();
    }

    /** NEARBY_LIVING (trigger, in registerLegacyGlyphs) - TalismanLimits.TRIGGER_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_nearby_living_trigger_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_TRIGGER_QI_COST.get();
    }

    /** CONTACT (trigger, in registerLegacyGlyphs) - TalismanLimits.TRIGGER_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_contact_trigger_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_TRIGGER_QI_COST.get();
    }

    /** FIREBALL (manifestation, in registerLegacyGlyphs) - TalismanLimits.FIREBALL_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_fireball_manifestation_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_FIREBALL_QI_COST.get();
    }

    /** EXPLOSION (manifestation, in registerLegacyGlyphs) - TalismanLimits.EXPLOSION_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_explosion_manifestation_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_EXPLOSION_QI_COST.get();
    }

    /** DAMAGE_X2 (evolution, in registerLegacyGlyphs) - TalismanLimits.DAMAGE_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_damage_x2_evolution_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DAMAGE_QI_COST.get();
    }

    /** DESTRUCTION_X2 (evolution, in registerLegacyGlyphs) - TalismanLimits.DESTRUCTION_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_destruction_x2_evolution_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DESTRUCTION_QI_COST.get();
    }

    /** SPLIT (evolution, in registerLegacyGlyphs) - TalismanLimits.SPLIT_QI_COST. */
    @ModifyArg(method = "registerLegacyGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_split_evolution_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SPLIT_QI_COST.get();
    }

    /** DIVINE_SENSE (trigger, in registerNewTriggersAndManifestations) - TalismanLimits.DIVINE_SENSE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_divine_sense_trigger_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DIVINE_SENSE_TRIGGER_QI_COST.get();
    }

    /** ANTI_AIR (trigger, in registerNewTriggersAndManifestations) - TalismanLimits.ANTI_AIR_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_anti_air_trigger_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_ANTI_AIR_TRIGGER_QI_COST.get();
    }

    /** LIGHTNING_ARC (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.LIGHTNING_ARC_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_lightning_arc_manifestation_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_LIGHTNING_ARC_QI_COST.get();
    }

    /** LIGHTNING_STRIKE (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.LIGHTNING_STRIKE_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_lightning_strike_manifestation_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_LIGHTNING_STRIKE_QI_COST.get();
    }

    /** ICE_SWORD (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.ICE_SWORD_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_ice_sword_manifestation_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_ICE_SWORD_QI_COST.get();
    }

    /** REGENERATION (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.REGENERATION_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_regeneration_manifestation_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_REGENERATION_QI_COST.get();
    }

    /** SWIFTNESS (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.SWIFTNESS_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 4),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_swiftness_manifestation_4(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SWIFTNESS_QI_COST.get();
    }

    /** POISON (manifestation, in registerNewTriggersAndManifestations) - TalismanLimits.POISON_QI_COST. */
    @ModifyArg(method = "registerNewTriggersAndManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 5),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_poison_manifestation_5(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_POISON_QI_COST.get();
    }

    /** DELAY (evolution, in registerNewEvolutions) - TalismanLimits.DELAY_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_delay_evolution_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DELAY_QI_COST.get();
    }

    /** HOMING (evolution, in registerNewEvolutions) - TalismanLimits.HOMING_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_homing_evolution_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HOMING_QI_COST.get();
    }

    /** PIERCE_PLUS_ONE (evolution, in registerNewEvolutions) - TalismanLimits.PIERCE_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_pierce_plus_one_evolution_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_PIERCE_QI_COST.get();
    }

    /** POTENCY_PLUS_ONE (evolution, in registerNewEvolutions) - TalismanLimits.POTENCY_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_potency_plus_one_evolution_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_POTENCY_QI_COST.get();
    }

    /** DURATION_X2 (evolution, in registerNewEvolutions) - TalismanLimits.DURATION_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 4),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_duration_x2_evolution_4(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DURATION_QI_COST.get();
    }

    /** PULL (evolution, in registerNewEvolutions) - TalismanLimits.PULL_QI_COST. */
    @ModifyArg(method = "registerNewEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 5),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_pull_evolution_5(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_PULL_QI_COST.get();
    }

    /** MECHANISM (trigger, in registerExpansionCampaignGlyphs) - TalismanLimits.MECHANISM_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerExpansionCampaignGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_mechanism_trigger_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_MECHANISM_TRIGGER_QI_COST.get();
    }

    /** TAMPER (trigger, in registerExpansionCampaignGlyphs) - TalismanLimits.TAMPER_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerExpansionCampaignGlyphs",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_tamper_trigger_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_TAMPER_TRIGGER_QI_COST.get();
    }

    /** MELEE_STRIKE (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.MELEE_STRIKE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_melee_strike_trigger_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_MELEE_STRIKE_TRIGGER_QI_COST.get();
    }

    /** RANGED_STRIKE (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.RANGED_STRIKE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_ranged_strike_trigger_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_RANGED_STRIKE_TRIGGER_QI_COST.get();
    }

    /** SUCCESSFUL_GUARD (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.SUCCESSFUL_GUARD_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_successful_guard_trigger_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SUCCESSFUL_GUARD_TRIGGER_QI_COST.get();
    }

    /** SPELLCAST (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.SPELLCAST_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_spellcast_trigger_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SPELLCAST_TRIGGER_QI_COST.get();
    }

    /** VANQUISH (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.VANQUISH_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 4),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_vanquish_trigger_4(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_VANQUISH_TRIGGER_QI_COST.get();
    }

    /** LOW_QI (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.LOW_QI_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 5),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_low_qi_trigger_5(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_LOW_QI_TRIGGER_QI_COST.get();
    }

    /** AFFLICTED (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.AFFLICTED_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 6),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_afflicted_trigger_6(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_AFFLICTED_TRIGGER_QI_COST.get();
    }

    /** IGNITED (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.IGNITED_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 7),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_ignited_trigger_7(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_IGNITED_TRIGGER_QI_COST.get();
    }

    /** HARD_LANDING (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.HARD_LANDING_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 8),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_hard_landing_trigger_8(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HARD_LANDING_TRIGGER_QI_COST.get();
    }

    /** SHIELD_BROKEN (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.SHIELD_BROKEN_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 9),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_shield_broken_trigger_9(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SHIELD_BROKEN_TRIGGER_QI_COST.get();
    }

    /** LEFT_CLICK_SUPPORT (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.LEFT_CLICK_SUPPORT_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 10),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_left_click_support_trigger_10(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_LEFT_CLICK_SUPPORT_TRIGGER_QI_COST.get();
    }

    /** NEARBY_BLOCK_BREAK (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.NEARBY_BLOCK_BREAK_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 11),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_nearby_block_break_trigger_11(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_NEARBY_BLOCK_BREAK_TRIGGER_QI_COST.get();
    }

    /** HOSTILE_PROJECTILE (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.HOSTILE_PROJECTILE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 12),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_hostile_projectile_trigger_12(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HOSTILE_PROJECTILE_TRIGGER_QI_COST.get();
    }

    /** BOUNDARY_CROSSING (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.BOUNDARY_CROSSING_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 13),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_boundary_crossing_trigger_13(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_BOUNDARY_CROSSING_TRIGGER_QI_COST.get();
    }

    /** HOSTILE_GAZE (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.HOSTILE_GAZE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 14),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_hostile_gaze_trigger_14(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HOSTILE_GAZE_TRIGGER_QI_COST.get();
    }

    /** HOSTILE_SPELLCAST (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.HOSTILE_SPELLCAST_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 15),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_hostile_spellcast_trigger_15(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HOSTILE_SPELLCAST_TRIGGER_QI_COST.get();
    }

    /** NEARBY_EXPLOSION (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.NEARBY_EXPLOSION_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 16),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_nearby_explosion_trigger_16(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_NEARBY_EXPLOSION_TRIGGER_QI_COST.get();
    }

    /** REDSTONE_FALLING_EDGE (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.REDSTONE_FALLING_EDGE_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 17),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_redstone_falling_edge_trigger_17(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_REDSTONE_FALLING_EDGE_TRIGGER_QI_COST.get();
    }

    /** DAWN (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.DAWN_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 18),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_dawn_trigger_18(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_DAWN_TRIGGER_QI_COST.get();
    }

    /** NEARBY_NATURAL_LIGHTNING (trigger, in registerSixtyGlyphExpansionTriggers) - TalismanLimits.NEARBY_NATURAL_LIGHTNING_TRIGGER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionTriggers",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;trigger(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanTriggerType;Ljava/util/EnumSet;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 19),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_nearby_natural_lightning_trigger_19(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_NEARBY_NATURAL_LIGHTNING_TRIGGER_QI_COST.get();
    }

    /** STONE_ORB (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.STONE_ORB_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_stone_orb_manifestation_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_STONE_ORB_QI_COST.get();
    }

    /** HEAVEN_DRILL (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.HEAVEN_DRILL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_heaven_drill_manifestation_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_HEAVEN_DRILL_QI_COST.get();
    }

    /** CRIMSON_LOTUS_DOMAIN (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.CRIMSON_LOTUS_DOMAIN_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_crimson_lotus_domain_manifestation_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_CRIMSON_LOTUS_DOMAIN_QI_COST.get();
    }

    /** THUNDER_ORB (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.THUNDER_ORB_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_thunder_orb_manifestation_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_THUNDER_ORB_QI_COST.get();
    }

    /** STARFALL (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.STARFALL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 4),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_starfall_manifestation_4(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_STARFALL_QI_COST.get();
    }

    /** SWORDMARK (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.SWORDMARK_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 5),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_swordmark_manifestation_5(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SWORDMARK_QI_COST.get();
    }

    /** SURGING_TIDE (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.SURGING_TIDE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 6),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_surging_tide_manifestation_6(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SURGING_TIDE_QI_COST.get();
    }

    /** VERDANT_SEED (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.VERDANT_SEED_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 7),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_verdant_seed_manifestation_7(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_VERDANT_SEED_QI_COST.get();
    }

    /** WATER_PRISON (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.WATER_PRISON_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 8),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_water_prison_manifestation_8(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_WATER_PRISON_QI_COST.get();
    }

    /** FROST_WALL (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.FROST_WALL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 9),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_frost_wall_manifestation_9(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_FROST_WALL_QI_COST.get();
    }

    /** POLARITY_FIELD (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.POLARITY_FIELD_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 10),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_polarity_field_manifestation_10(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_POLARITY_FIELD_QI_COST.get();
    }

    /** SPELLRETURN_MIRROR (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.SPELLRETURN_MIRROR_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 11),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_spellreturn_mirror_manifestation_11(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SPELLRETURN_MIRROR_QI_COST.get();
    }

    /** FLAME_STEP (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.FLAME_STEP_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 12),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_flame_step_manifestation_12(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_FLAME_STEP_QI_COST.get();
    }

    /** BLOOD_SPIKE (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.BLOOD_SPIKE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 13),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_blood_spike_manifestation_13(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_BLOOD_SPIKE_QI_COST.get();
    }

    /** THUNDERSTEP (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.THUNDERSTEP_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 14),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_thunderstep_manifestation_14(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_THUNDERSTEP_QI_COST.get();
    }

    /** MIRRORWATER_CLONE (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.MIRRORWATER_CLONE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 15),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_mirrorwater_clone_manifestation_15(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_MIRRORWATER_CLONE_QI_COST.get();
    }

    /** FROSTMIRROR_AEGIS (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.FROSTMIRROR_AEGIS_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 16),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_frostmirror_aegis_manifestation_16(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_FROSTMIRROR_AEGIS_QI_COST.get();
    }

    /** WOODEN_DAO_SOLDIER (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.WOODEN_DAO_SOLDIER_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 17),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_wooden_dao_soldier_manifestation_17(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_WOODEN_DAO_SOLDIER_QI_COST.get();
    }

    /** REJUVENATION_ALTAR (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.REJUVENATION_ALTAR_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 18),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_rejuvenation_altar_manifestation_18(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_REJUVENATION_ALTAR_QI_COST.get();
    }

    /** FIVE_PEAKS_SEAL (manifestation, in registerSixtyGlyphExpansionManifestations) - TalismanLimits.FIVE_PEAKS_SEAL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionManifestations",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;manifestation(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCategory;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphElement;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Ljava/util/EnumSet;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanManifestationType;II)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 19),
            index = 9, remap = false, require = 0)
    private static int configExt$glyphCost_five_peaks_seal_manifestation_19(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_FIVE_PEAKS_SEAL_QI_COST.get();
    }

    /** AREA_PLUS_HALF (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.AREA_PLUS_HALF_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 0),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_area_plus_half_evolution_0(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_AREA_PLUS_HALF_QI_COST.get();
    }

    /** RANGE_PLUS_HALF (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.RANGE_PLUS_HALF_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 1),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_range_plus_half_evolution_1(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_RANGE_PLUS_HALF_QI_COST.get();
    }

    /** PROJECTILE_SPEED_PLUS_HALF (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.PROJECTILE_SPEED_PLUS_HALF_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 2),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_projectile_speed_plus_half_evolution_2(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_PROJECTILE_SPEED_PLUS_HALF_QI_COST.get();
    }

    /** SCALE_PLUS_HALF (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.SCALE_PLUS_HALF_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 3),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_scale_plus_half_evolution_3(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_SCALE_PLUS_HALF_QI_COST.get();
    }

    /** ECHO (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.ECHO_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 4),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_echo_evolution_4(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_ECHO_QI_COST.get();
    }

    /** REPEL (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.REPEL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 5),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_repel_evolution_5(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_REPEL_QI_COST.get();
    }

    /** LIFEDRAIN (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.LIFEDRAIN_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 6),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_lifedrain_evolution_6(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_LIFEDRAIN_QI_COST.get();
    }

    /** ARMORBREAK (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.ARMORBREAK_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 7),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_armorbreak_evolution_7(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_ARMORBREAK_QI_COST.get();
    }

    /** MERIDIAN_SEAL (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.MERIDIAN_SEAL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 8),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_meridian_seal_evolution_8(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_MERIDIAN_SEAL_QI_COST.get();
    }

    /** BARRIER_DAMAGE_X2 (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.BARRIER_DAMAGE_X2_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 9),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_barrier_damage_x2_evolution_9(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_BARRIER_DAMAGE_X2_QI_COST.get();
    }

    /** CLEANSE (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.CLEANSE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 10),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_cleanse_evolution_10(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_CLEANSE_QI_COST.get();
    }

    /** PROLIFERATE (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.PROLIFERATE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 11),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_proliferate_evolution_11(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_PROLIFERATE_QI_COST.get();
    }

    /** PRESERVE_TERRAIN (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.PRESERVE_TERRAIN_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 12),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_preserve_terrain_evolution_12(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_PRESERVE_TERRAIN_QI_COST.get();
    }

    /** RELOCATE (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.RELOCATE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 13),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_relocate_evolution_13(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_RELOCATE_QI_COST.get();
    }

    /** IGNITE (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.IGNITE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 14),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_ignite_evolution_14(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_IGNITE_QI_COST.get();
    }

    /** CHILL (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.CHILL_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 15),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_chill_evolution_15(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_CHILL_QI_COST.get();
    }

    /** EXECUTE (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.EXECUTE_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 16),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_execute_evolution_16(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_EXECUTE_QI_COST.get();
    }

    /** KNOCKUP (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.KNOCKUP_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 17),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_knockup_evolution_17(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_KNOCKUP_QI_COST.get();
    }

    /** WEAKNESS (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.WEAKNESS_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 18),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_weakness_evolution_18(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_WEAKNESS_QI_COST.get();
    }

    /** GLOWING_MARK (evolution, in registerSixtyGlyphExpansionEvolutions) - TalismanLimits.GLOWING_MARK_QI_COST. */
    @ModifyArg(method = "registerSixtyGlyphExpansionEvolutions",
            at = @At(value = "INVOKE",
                    target = "Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphCatalog;evolution(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;ILcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphFunction;Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanEvolutionType;I)Lcom/xiaoxiang/cultivation/cultivation/talisman/TalismanGlyphDefinition;",
                    ordinal = 19),
            index = 5, remap = false, require = 0)
    private static int configExt$glyphCost_glowing_mark_evolution_19(int original) {
        if (!ExtendedConfig.ENABLE_TALISMAN_OVERRIDES.get()) return original;
        return ExtendedConfig.TALISMAN_GLOWING_MARK_QI_COST.get();
    }
}
