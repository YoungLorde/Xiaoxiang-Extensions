package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;
import java.util.*;

/**
 * Applies Golden Finger perks to config values.
 * Each perk modifies specific config values to give the player advantages.
 *
 * The perks are applied by modifying the same ForgeConfigSpec values that
 * the mixins read, so the effects are immediate and persistent.
 */
public class PerkApplier {

    /** Store old values for undo. */
    private static final Map<String, String> perkUndoStack = new LinkedHashMap<>();
    private static boolean hasPerkUndoData = false;

    /**
     * Apply a set of perks to config values.
     * @param perkIds The IDs of perks to apply
     * @return List of changes made
     */
    public static List<String> applyPerks(Set<Integer> perkIds) {
        // Save state for undo
        saveStateForUndo();

        List<String> changes = new ArrayList<>();

        for (int perkId : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(perkId);
            if (perk == null) continue;

            List<String> perkChanges = applySinglePerk(perk);
            changes.addAll(perkChanges);
        }

        // Check for "Destined One" perk - doubles all effects
        if (perkIds.contains(99)) {
            changes.add("\u00A7d\u00A7lDestined One: All perk effects are doubled!");
        }

        // Track applied perks for display in cultivation screen
        AppliedPerkTracker.onPerksApplied(perkIds);

        return changes;
    }

    /** Apply a single perk's effects to config values. */
    private static List<String> applySinglePerk(GoldenFingerPerks.Perk perk) {
        return previewSinglePerk(perk, true);
    }

    /**
     * Preview what a perk would do without applying it.
     * Returns a list of attribute change descriptions.
     * @param perk The perk to preview
     * @return List of strings like "Spell Damage: x1.50 (1.0 → 1.5)"
     */
    public static List<String> previewPerk(GoldenFingerPerks.Perk perk) {
        return previewSinglePerk(perk, false);
    }

    /**
     * Preview the cumulative effects of ALL selected perks combined.
     * Returns a map of attribute name -> combined multiplier.
     * Multipliers are multiplied together (e.g. two x1.5 buffs = x2.25).
     */
    public static java.util.Map<String, Double> previewAllPerksCombined(java.util.Set<Integer> perkIds) {
        // Collect all individual perk changes, then combine by attribute path
        java.util.Map<String, Double> combinedMultipliers = new java.util.LinkedHashMap<>();

        for (int perkId : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(perkId);
            if (perk == null) continue;
            List<String> changes = previewPerk(perk);
            for (String change : changes) {
                // Parse the change string: "§apath: oldVal -> newVal (xFactor)"
                // Extract the path and the factor
                String clean = change.replaceAll("\u00A7.", "");
                int parenIdx = clean.lastIndexOf("(x");
                if (parenIdx < 0) continue;
                String factorStr = clean.substring(parenIdx + 2, clean.length() - 1).trim();
                try {
                    double factor = Double.parseDouble(factorStr);
                    // Extract the path (before the colon)
                    int colonIdx = clean.indexOf(':');
                    if (colonIdx < 0) continue;
                    String path = clean.substring(0, colonIdx).trim();
                    // Combine: multiply factors for same path
                    combinedMultipliers.merge(path, factor, (old, val) -> old * val);
                } catch (NumberFormatException e) { /* skip */ }
            }
        }

        return combinedMultipliers;
    }

    /**
     * Internal method that either previews or applies a perk.
     * @param apply If true, actually modifies config values. If false, just computes what would change.
     */
    private static List<String> previewSinglePerk(GoldenFingerPerks.Perk perk, boolean apply) {
        List<String> changes = new ArrayList<>();
        String n = perk.name.toLowerCase();
        previewOnly = !apply;

        // ── Physique perks ──
        if (n.contains("immortal body") && !n.contains("stable")) {
            // Boost all spell damage, reduce damage taken
            multiplyConfig("spells.damageGlobalMultiplier", 1.5, changes);
            multiplyConfig("physiques.immortalBody.damageTakenMult", 0.5, changes);
            multiplyConfig("physiques.immortalBody.qiCostMult", 0.5, changes);
            multiplyConfig("physiques.immortalBody.maxHpBonus", 2.0, changes);
            // Set rarity weight for immortal physique high
            setConfig("physiques.rarityWeights.immortal", 100, changes);
        }
        if (n.contains("sword body") || n.contains("innate sword")) {
            multiplyConfig("physiques.innateSwordBody.swordSpellMult", 2.0, changes);
            multiplyConfig("physiques.innateSwordBody.nonSwordPenalty", 0.5, changes);
        }
        if (n.contains("fire body") || n.contains("heavenly fire body")) {
            multiplyConfig("physiques.heavenlyFireBodyFireSpellMult", 2.0, changes);
        }
        if (n.contains("ice body") || n.contains("mystic ice")) {
            multiplyConfig("physiques.mysticIceBodyWaterSpellMult", 2.0, changes);
        }
        if (n.contains("chaos body") && n.contains("stable")) {
            multiplyConfig("physiques.chaosBody.spellDamageMult", 2.0, changes);
            multiplyConfig("physiques.chaosBody.cultivationReqMult", 0.5, changes);
        }
        if (n.contains("chaos body") && !n.contains("stable")) {
            multiplyConfig("physiques.chaosBody.spellDamageMult", 2.0, changes);
        }
        if (n.contains("supreme physique roll")) {
            setConfig("physiques.rarityWeights.supreme", 100, changes);
            setConfig("physiques.rarityWeights.low", 0, changes);
        }
        if (n.contains("immortal physique roll")) {
            setConfig("physiques.rarityWeights.immortal", 100, changes);
            setConfig("physiques.rarityWeights.low", 0, changes);
        }
        if (n.contains("physique rarity boost")) {
            multiplyConfig("physiques.rarityWeights.high", 2.0, changes);
            multiplyConfig("physiques.rarityWeights.supreme", 2.0, changes);
            multiplyConfig("physiques.rarityWeights.immortal", 2.0, changes);
        }
        if (n.contains("heavenly constitution")) {
            multiplyConfig("physiques.immortalBody.qiAbsorbMult", 2.0, changes);
            multiplyConfig("physiques.immortalBody.maxHpBonus", 2.0, changes);
        }

        // ── Spirit Root perks ──
        if (n.contains("heavenly spirit root") || n.contains("primal root")) {
            multiplyConfig("spiritRoots.heavenly.primaryElementMult", 2.0, changes);
            multiplyConfig("spiritRoots.qiAbsorption.ssrMult", 2.0, changes);
        }
        if (n.contains("dual spirit root") && !n.contains("triple")) {
            multiplyConfig("spiritRoots.dual.primaryElementMult", 1.5, changes);
            multiplyConfig("spiritRoots.dual.secondaryElementMult", 1.5, changes);
        }
        if (n.contains("sword spirit root") || n.contains("heavenly sword root")) {
            multiplyConfig("spiritRoots.heavenlySword.swordDmgMult", 3.0, changes);
        }
        if (n.contains("ssr qi absorption")) {
            multiplyConfig("spiritRoots.qiAbsorption.ssrMult", 2.0, changes);
        }
        if (n.contains("sr qi absorption")) {
            multiplyConfig("spiritRoots.qiAbsorption.srMult", 2.0, changes);
        }
        if (n.contains("root enhancement")) {
            multiplyConfig("spiritRoots.heavenly.primaryElementMult", 1.5, changes);
            multiplyConfig("spiritRoots.dual.primaryElementMult", 1.5, changes);
            multiplyConfig("spiritRoots.qiAbsorption.ssrMult", 1.5, changes);
        }

        // ── Cultivation perks ──
        if (n.contains("accelerated cultivation") || n.contains("double cultivation")) {
            multiplyConfig("general.playerCultivationSpeedMult", 2.0, changes);
            multiplyConfig("qiSystem.playerConsumer.meditationEfficiencyBonus", 2.0, changes);
        }
        if (n.contains("triple cultivation speed")) {
            multiplyConfig("general.playerCultivationSpeedMult", 3.0, changes);
            multiplyConfig("qiSystem.playerConsumer.meditationEfficiencyBonus", 3.0, changes);
        }
        if (n.contains("qi devourer")) {
            multiplyConfig("qiSystem.playerConsumer.attractionRadius", 3.0, changes);
            multiplyConfig("qiSystem.playerConsumer.meditationEfficiencyBonus", 3.0, changes);
        }
        if (n.contains("meditation master")) {
            multiplyConfig("qiSystem.playerConsumer.meditationEfficiencyBonus", 2.0, changes);
            multiplyConfig("qiSystem.playerConsumer.meditationRangeBonus", 2.0, changes);
        }
        if (n.contains("tribulation immunity")) {
            multiplyConfig("realms.tribulationDamage.globalMultiplier", 0.2, changes);
            multiplyConfig("trials.heartDemon.greatRighteousVitalityMult", 0.2, changes);
            multiplyConfig("trials.heartDemon.righteousVitalityMult", 0.2, changes);
            multiplyConfig("trials.heartDemon.neutralVitalityMult", 0.2, changes);
            multiplyConfig("trials.heartDemon.evilVitalityMult", 0.2, changes);
            multiplyConfig("trials.heartDemon.greatEvilVitalityMult", 0.2, changes);
        }
        if (n.contains("tribulation resistance")) {
            multiplyConfig("realms.tribulationDamage.globalMultiplier", 0.5, changes);
        }
        if (n.contains("extended lifespan")) {
            multiplyConfig("realms.lifespan.globalMultiplier", 2.0, changes);
        }
        if (n.contains("eternal youth")) {
            multiplyConfig("realms.lifespan.globalMultiplier", 3.0, changes);
            multiplyConfig("lifespanHelper.agePerDay", 0.33, changes);
        }
        if (n.contains("qi shield mastery")) {
            multiplyConfig("qiSystem.qiShield.qiPerDamage", 0.5, changes);
            multiplyConfig("qiSystem.qiShield.perfectReduction", 1.0, changes);
        }
        if (n.contains("breakthrough gift")) {
            multiplyConfig("realms.tribulationDamage.globalMultiplier", 0.0, changes);
        }

        // ── Spell & Combat perks ──
        if (n.contains("spell damage boost")) {
            multiplyConfig("spells.damageGlobalMultiplier", 1.5, changes);
        }
        if (n.contains("double spell damage")) {
            multiplyConfig("spells.damageGlobalMultiplier", 2.0, changes);
        }
        if (n.contains("spell cost reduction")) {
            multiplyConfig("spells.qiCostGlobalMultiplier", 0.5, changes);
        }
        if (n.contains("free spells")) {
            multiplyConfig("spells.qiCostGlobalMultiplier", 0.2, changes);
        }
        if (n.contains("spell charge speed")) {
            multiplyConfig("spells.chargeGlobalMultiplier", 0.5, changes);
        }
        if (n.contains("elemental mastery")) {
            multiplyConfig("techniques.elementSpellMultGlobal", 2.0, changes);
        }
        if (n.contains("sword saint")) {
            multiplyConfig("spiritRoots.heavenlySword.swordDmgMult", 3.0, changes);
            multiplyConfig("physiques.innateSwordBody.swordSpellMult", 2.0, changes);
        }
        if (n.contains("fire sage")) {
            multiplyConfig("physiques.heavenlyFireBodyFireSpellMult", 3.0, changes);
        }
        if (n.contains("ice sovereign")) {
            multiplyConfig("physiques.mysticIceBodyWaterSpellMult", 3.0, changes);
        }
        if (n.contains("dodge master")) {
            // This would need a player-specific dodge, but we can't do that via config
            // Instead, reduce NPC dodge to make combat easier
            multiplyConfig("npcCombat.hardDodgeCap", 0.5, changes);
        }
        if (n.contains("iron body") || n.contains("battle saint")) {
            multiplyConfig("techniques.maxHpBonusGlobal", 2.0, changes);
            multiplyConfig("techniques.defenseBonusGlobal", 2.0, changes);
        }
        if (n.contains("critical cultivator")) {
            multiplyConfig("techniques.critRateBonusGlobal", 2.0, changes);
        }
        if (n.contains("movement technique")) {
            multiplyConfig("techniques.moveSpeedBonusGlobal", 1.5, changes);
        }
        if (n.contains("battle saint")) {
            multiplyConfig("techniques.attackBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.defenseBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.maxHpBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.critRateBonusGlobal", 1.5, changes);
        }

        // ── Alchemy perks ──
        if (n.contains("master alchemist")) {
            multiplyConfig("alchemy.xpGains.low", 3.0, changes);
            multiplyConfig("alchemy.xpGains.mid", 3.0, changes);
            multiplyConfig("alchemy.xpGains.high", 3.0, changes);
            multiplyConfig("alchemy.xpGains.supreme", 3.0, changes);
            multiplyConfig("alchemy.xpGains.immortal", 3.0, changes);
        }
        if (n.contains("pill effectiveness")) {
            multiplyConfig("pills.qiRecovery.low", 1.5, changes);
            multiplyConfig("pills.qiRecovery.mid", 1.5, changes);
            multiplyConfig("pills.qiRecovery.high", 1.5, changes);
            multiplyConfig("pills.qiRecovery.supreme", 1.5, changes);
        }
        if (n.contains("furnace enhancement") || n.contains("furnace of heaven")) {
            multiplyConfig("alchemy.furnace.maxQi", 3.0, changes);
        }
        if (n.contains("instant pills")) {
            multiplyConfig("alchemy.furnace.ticksPerPill", 0.5, changes);
        }

        // ── Refining perks ──
        if (n.contains("master refiner")) {
            multiplyConfig("refining.xpGains.low", 3.0, changes);
            multiplyConfig("refining.xpGains.mid", 3.0, changes);
            multiplyConfig("refining.xpGains.high", 3.0, changes);
            multiplyConfig("refining.xpGains.supreme", 3.0, changes);
            multiplyConfig("refining.xpGains.immortal", 3.0, changes);
        }
        if (n.contains("furnace of heaven") || n.contains("furnance enhancement")) {
            multiplyConfig("refining.furnace.maxQi", 3.0, changes);
        }
        if (n.contains("instant refining")) {
            multiplyConfig("refining.furnace.ticksPerItem", 0.5, changes);
        }
        if (n.contains("tier-up master")) {
            multiplyConfig("refining.tierUpChanceDivineForge", 2.0, changes);
            multiplyConfig("refining.tierUpChanceHeavenlyElixir", 2.0, changes);
        }
        if (n.contains("weapon mastery")) {
            multiplyConfig("weapons.damageGlobalMultiplier", 1.5, changes);
        }
        if (n.contains("qi cost free weapons")) {
            multiplyConfig("weapons.spellQiCostReductionPercent.low", 5.0, changes);
            multiplyConfig("weapons.spellQiCostReductionPercent.mid", 5.0, changes);
            multiplyConfig("weapons.spellQiCostReductionPercent.high", 5.0, changes);
            multiplyConfig("weapons.spellQiCostReductionPercent.supreme", 5.0, changes);
            multiplyConfig("weapons.spellQiCostReductionPercent.immortal", 5.0, changes);
        }

        // ── World perks ──
        if (n.contains("spirit vein heart")) {
            multiplyConfig("spiritVeins.maxQi.immortal", 2.0, changes);
            multiplyConfig("spiritVeins.orbGain.immortal", 2.0, changes);
            multiplyConfig("spiritVeins.supplyPerSecond.immortal", 2.0, changes);
        }
        if (n.contains("qi rich biome")) {
            multiplyConfig("qiDensity.normal", 3.0, changes);
            multiplyConfig("qiDensity.woodRich", 3.0, changes);
        }
        if (n.contains("formation master")) {
            multiplyConfig("formations.qiGathering.low", 2.0, changes);
            multiplyConfig("formations.qiGathering.mid", 2.0, changes);
            multiplyConfig("formations.qiGathering.high", 2.0, changes);
            multiplyConfig("formations.qiGathering.supreme", 2.0, changes);
            multiplyConfig("formations.qiGathering.immortal", 2.0, changes);
        }
        if (n.contains("lucky loot")) {
            multiplyConfig("loot.weights.lowSpiritStone", 2.0, changes);
            multiplyConfig("loot.weights.midSpiritStone", 2.0, changes);
            multiplyConfig("loot.weights.highSpiritStone", 2.0, changes);
            multiplyConfig("loot.weights.supremeSpiritStone", 2.0, changes);
            multiplyConfig("loot.rolls.completeCultivationMax", 2.0, changes);
        }
        if (n.contains("spirit plant garden")) {
            multiplyConfig("spiritPlants.maxAge", 0.5, changes); // faster growth
        }
        if (n.contains("qi stone mine")) {
            multiplyConfig("qiSystem.spiritStoneOre.maxQiLow", 3.0, changes);
            multiplyConfig("qiSystem.spiritStoneOre.maxQiMid", 3.0, changes);
            multiplyConfig("qiSystem.spiritStoneOre.maxQiHigh", 3.0, changes);
        }

        // ── Sect perks ──
        if (n.contains("shop discount")) {
            multiplyConfig("sects.shop.techniquePrices.low", 0.5, changes);
            multiplyConfig("sects.shop.techniquePrices.mid", 0.5, changes);
            multiplyConfig("sects.shop.techniquePrices.high", 0.5, changes);
            multiplyConfig("sects.shop.techniquePrices.supreme", 0.5, changes);
            multiplyConfig("sects.shop.techniquePrices.immortal", 0.5, changes);
            multiplyConfig("sects.shop.spellPrices.low", 0.5, changes);
            multiplyConfig("sects.shop.spellPrices.mid", 0.5, changes);
            multiplyConfig("sects.shop.spellPrices.high", 0.5, changes);
            multiplyConfig("sects.shop.spellPrices.supreme", 0.5, changes);
            multiplyConfig("sects.shop.spellPrices.immortal", 0.5, changes);
            multiplyConfig("sects.shop.weaponPrices.low", 0.5, changes);
            multiplyConfig("sects.shop.weaponPrices.mid", 0.5, changes);
            multiplyConfig("sects.shop.weaponPrices.high", 0.5, changes);
            multiplyConfig("sects.shop.weaponPrices.supreme", 0.5, changes);
            multiplyConfig("sects.shop.weaponPrices.immortal", 0.5, changes);
        }
        if (n.contains("sect ancestor's blessing")) {
            multiplyConfig("techniques.qiAbsorbMultGlobal", 1.5, changes);
            multiplyConfig("techniques.attackBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.defenseBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.maxHpBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.critRateBonusGlobal", 1.5, changes);
            multiplyConfig("techniques.elementSpellMultGlobal", 1.5, changes);
            multiplyConfig("techniques.moveSpeedBonusGlobal", 1.5, changes);
        }

        // ── Beast perks ──
        if (n.contains("beast tamer") || n.contains("beast companion")) {
            multiplyConfig("beasts.qiGainMultiplier", 3.0, changes);
        }

        // ── Lifespan perks ──
        if (n.contains("cultivator identity")) {
            multiplyConfig("identity.lifespan.cultivator.min", 1.5, changes);
            multiplyConfig("identity.lifespan.cultivator.max", 1.5, changes);
        }
        if (n.contains("scholar identity")) {
            multiplyConfig("identity.lifespan.scholar.min", 1.5, changes);
            multiplyConfig("identity.lifespan.scholar.max", 1.5, changes);
        }
        if (n.contains("martial identity")) {
            multiplyConfig("identity.lifespan.martial.min", 1.5, changes);
            multiplyConfig("identity.lifespan.martial.max", 1.5, changes);
        }
        if (n.contains("abandoned prodigy")) {
            multiplyConfig("identity.lifespan.abandoned.min", 2.0, changes);
            multiplyConfig("identity.lifespan.abandoned.max", 2.0, changes);
            multiplyConfig("realms.maxQi.globalMultiplier", 3.0, changes);
        }

        return changes;
    }

    /** When true, multiplyConfig/setConfig only compute changes without applying them. */
    private static boolean previewOnly = false;

    /**
     * Multiply a config value by a factor.
     * Uses the DEFAULT value as the base, not the current value. This ensures
     * perks always apply on top of the default.
     * The final value = default * perkFactor.
     */
    private static void multiplyConfig(String path, double factor, List<String> changes) {
        String type = ConfigValueAccessor.getType(path);
        if (type.equals("unknown")) return;
        String valStr = ConfigValueAccessor.getValueString(path);
        // Get the DEFAULT value so perks apply on top of defaults
        double defaultVal = ConfigValueAccessor.getDefaultValueAsDouble(path);
        try {
            if (type.equals("int")) {
                int oldVal = Integer.parseInt(valStr.trim());
                // Use default * factor instead of current * factor
                int newVal = (int) Math.round(defaultVal * factor);
                if (defaultVal > 0 && newVal < 1) newVal = 1;
                // HP multipliers should never go below 1
                if (path.toLowerCase().contains("hpmult") && newVal < 1) newVal = 1;
                if (newVal != oldVal) {
                    if (!previewOnly) ConfigValueAccessor.setValueFromString(path, String.valueOf(newVal));
                    String color = factor > 1.0 ? "\u00A7a" : "\u00A7c";
                    changes.add(color + path + ": " + oldVal + " -> " + newVal + " (x" + String.format("%.2f", factor) + ")");
                }
            } else if (type.equals("double")) {
                double oldVal = Double.parseDouble(valStr.trim());
                // Use default * factor instead of current * factor
                double newVal = Math.round(defaultVal * factor * 100.0) / 100.0;
                // HP multipliers should never go below 1.0
                if (path.toLowerCase().contains("hpmult") && newVal < 1.0) newVal = 1.0;
                if (newVal != oldVal) {
                    if (!previewOnly) ConfigValueAccessor.setValueFromString(path, String.valueOf(newVal));
                    String color = factor > 1.0 ? "\u00A7a" : "\u00A7c";
                    changes.add(color + path + ": " + oldVal + " -> " + newVal + " (x" + String.format("%.2f", factor) + ")");
                }
            } else if (type.equals("long")) {
                long oldVal = Long.parseLong(valStr.trim());
                // Use default * factor instead of current * factor
                long newVal = (long) Math.round(defaultVal * factor);
                if (defaultVal > 0 && newVal < 1) newVal = 1;
                if (newVal != oldVal) {
                    if (!previewOnly) ConfigValueAccessor.setValueFromString(path, String.valueOf(newVal));
                    String color = factor > 1.0 ? "\u00A7a" : "\u00A7c";
                    changes.add(color + path + ": " + oldVal + " -> " + newVal + " (x" + String.format("%.2f", factor) + ")");
                }
            }
        } catch (NumberFormatException e) {
            // skip
        }
    }

    /** Set a config value to a specific value. */
    private static void setConfig(String path, int value, List<String> changes) {
        String type = ConfigValueAccessor.getType(path);
        if (type.equals("unknown")) return;
        String oldValStr = ConfigValueAccessor.getValueString(path);
        try {
            if (type.equals("int")) {
                int oldVal = Integer.parseInt(oldValStr.trim());
                if (oldVal != value) {
                    if (!previewOnly) ConfigValueAccessor.setValueFromString(path, String.valueOf(value));
                    changes.add("\u00A7e" + path + ": " + oldVal + " -> " + value + " (set)");
                }
            }
        } catch (NumberFormatException e) {
            // skip
        }
    }

    // ── Undo ──
    private static void saveStateForUndo() {
        perkUndoStack.clear();
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String type = ConfigValueAccessor.getType(path);
            if (type.equals("unknown")) continue;
            perkUndoStack.put(path, ConfigValueAccessor.getValueString(path));
        }
        hasPerkUndoData = true;
    }

    public static List<String> undo() {
        List<String> changes = new ArrayList<>();
        if (!hasPerkUndoData) return changes;
        for (Map.Entry<String, String> entry : perkUndoStack.entrySet()) {
            String path = entry.getKey();
            String oldVal = entry.getValue();
            String currentVal = ConfigValueAccessor.getValueString(path);
            if (!currentVal.equals(oldVal)) {
                ConfigValueAccessor.setValueFromString(path, oldVal);
                changes.add("\u00A7e" + path + ": reverted to " + oldVal);
            }
        }
        hasPerkUndoData = false;
        perkUndoStack.clear();
        return changes;
    }

    public static boolean canUndo() {
        return hasPerkUndoData;
    }
}
