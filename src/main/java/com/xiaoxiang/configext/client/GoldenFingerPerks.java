package com.xiaoxiang.configext.client;

import java.util.*;

/**
 * 99 "Golden Finger" perks that players can start with, especially for higher difficulties.
 * These use actual content from the original Xiaoxiang Cultivation World mod:
 * - Physiques (Immortal Body, Sword Body, Fire Body, Ice Body, Chaos Body, Broken Vein)
 * - Spirit Roots (Heavenly, Dual, Mutant, Sword, Hidden Element)
 * - Pills (Qi Recovery, Spirit Stone Qi, Blood Burn, Clear Mind, Divine Stride)
 * - Realms (start at higher realm, sealed cultivation)
 * - Techniques, spells, weapons, formations, etc.
 *
 * Each perk has: id, name, description, category, and a "tier" (1-5) indicating power level.
 */
public class GoldenFingerPerks {

    public static class Perk {
        public final int id;
        public final String name;
        public final String description;
        public final String category;
        public final int tier; // 1=minor, 5=legendary

        public Perk(int id, String name, String description, String category, int tier) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.tier = tier;
        }
    }

    public static final List<Perk> ALL_PERKS = new ArrayList<>();

    static {
        int id = 1;

        // ── Physique Perks (1-15) ──
        ALL_PERKS.add(new Perk(id++, "Immortal Body Physique",
            "Start with the Immortal Body physique - all spell types are boosted. The strongest physique tier.",
            "Physique", 5));
        ALL_PERKS.add(new Perk(id++, "Sword Body Physique",
            "Start with the Sword Body physique - sword spells are significantly boosted.",
            "Physique", 4));
        ALL_PERKS.add(new Perk(id++, "Fire Body Physique",
            "Start with the Fire Body physique - fire spells are significantly boosted.",
            "Physique", 4));
        ALL_PERKS.add(new Perk(id++, "Ice Body Physique",
            "Start with the Ice Body physique - ice spells are significantly boosted.",
            "Physique", 4));
        ALL_PERKS.add(new Perk(id++, "Chaos Body Physique",
            "Start with the Chaos Body physique - all spells boosted but cultivation is unstable.",
            "Physique", 5));
        ALL_PERKS.add(new Perk(id++, "Broken Vein Body",
            "Start with Broken Vein physique - cannot cultivate normally but has unique advantages.",
            "Physique", 2));
        ALL_PERKS.add(new Perk(id++, "Supreme Physique Roll",
            "Guaranteed Supreme-tier physique on spawn instead of random roll.",
            "Physique", 4));
        ALL_PERKS.add(new Perk(id++, "Immortal Physique Roll",
            "Guaranteed Immortal-tier physique on spawn. Extremely rare normally.",
            "Physique", 5));
        ALL_PERKS.add(new Perk(id++, "Physique Rarity Boost",
            "All physique rolls are shifted one tier higher (Low->Mid, Mid->High, etc.)",
            "Physique", 3));
        ALL_PERKS.add(new Perk(id++, "Dual Physique",
            "Roll two physiques and keep the better one.",
            "Physique", 3));
        ALL_PERKS.add(new Perk(id++, "Stable Chaos Body",
            "Chaos Body without the instability - all spells boosted, no downsides.",
            "Physique", 5));
        ALL_PERKS.add(new Perk(id++, "Lightning Body",
            "Start with a custom Lightning Body - lightning/thunder spells boosted.",
            "Physique", 4));
        ALL_PERKS.add(new Perk(id++, "Wind Body",
            "Start with a custom Wind Body - wind spells boosted, increased move speed.",
            "Physique", 3));
        ALL_PERKS.add(new Perk(id++, "Earth Body",
            "Start with an Earth Body - earth spells boosted, increased defense.",
            "Physique", 3));
        ALL_PERKS.add(new Perk(id++, "Heavenly Constitution",
            "All physique effects are doubled in power.",
            "Physique", 5));

        // ── Spirit Root Perks (16-30) ──
        ALL_PERKS.add(new Perk(id++, "Heavenly Spirit Root",
            "Start with a Heavenly Spirit Root - the best qi absorption multiplier.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "Dual Spirit Root",
            "Start with a Dual Spirit Root - two elements, good absorption.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "Mutant Spirit Root",
            "Start with a Mutant Spirit Root - unique elemental combination.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "Sword Spirit Root",
            "Start with a Sword Spirit Root - sword spells are vastly stronger.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "Hidden Element Spirit Root",
            "Start with a Hidden Element Spirit Root - rare and powerful.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "Triple Spirit Root",
            "Start with three spirit roots instead of the usual one or two.",
            "Spirit Root", 3));
        ALL_PERKS.add(new Perk(id++, "Heavenly Fire Root",
            "Heavenly Spirit Root with Fire element - maximum fire cultivation.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "Heavenly Ice Root",
            "Heavenly Spirit Root with Ice element - maximum ice cultivation.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "Heavenly Sword Root",
            "Heavenly Spirit Root with Sword element - the ultimate sword cultivator.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "SSR Qi Absorption",
            "SSR-tier qi absorption multiplier regardless of spirit root quality.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "SR Qi Absorption",
            "SR-tier qi absorption multiplier.",
            "Spirit Root", 3));
        ALL_PERKS.add(new Perk(id++, "Root Enhancement",
            "All spirit root multipliers are increased by 50%.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "Perfect Root",
            "Spirit root has no elemental weakness - all elements at full power.",
            "Spirit Root", 4));
        ALL_PERKS.add(new Perk(id++, "Primal Root",
            "Start with a Primal Spirit Root - all elements at Heavenly tier.",
            "Spirit Root", 5));
        ALL_PERKS.add(new Perk(id++, "Root of Chaos",
            "All elements are available, all at Dual tier. Ultimate versatility.",
            "Spirit Root", 5));

        // ── Cultivation Perks (31-45) ──
        ALL_PERKS.add(new Perk(id++, "Sealed Nascent Soul",
            "Start as a sealed Nascent Soul cultivator. Must break the seal to access your power. " +
            "Once unsealed, you are vastly stronger than peers.",
            "Cultivation", 5));
        ALL_PERKS.add(new Perk(id++, "Sealed Golden Core",
            "Start as a sealed Golden Core cultivator. Break the seal to access Golden Core power.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Sealed Foundation Building",
            "Start as a sealed Foundation Building cultivator. Break the seal to access your cultivation.",
            "Cultivation", 3));
        ALL_PERKS.add(new Perk(id++, "Head Start: Qi Refining Peak",
            "Start at Qi Refining Peak realm instead of Qi Refining Early.",
            "Cultivation", 2));
        ALL_PERKS.add(new Perk(id++, "Head Start: Foundation Building",
            "Start at Foundation Building realm. Skip the Qi Refining stage entirely.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Accelerated Cultivation",
            "Cultivation speed is permanently doubled.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Triple Cultivation Speed",
            "Cultivation speed is permanently tripled.",
            "Cultivation", 5));
        ALL_PERKS.add(new Perk(id++, "Qi Devourer",
            "Absorb 3x more Qi from the environment. Biome Qi density effectively tripled for you.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Meditation Master",
            "Meditation efficiency is permanently doubled.",
            "Cultivation", 3));
        ALL_PERKS.add(new Perk(id++, "Tribulation Immunity",
            "Tribulation damage is reduced by 80%. Heart demons are weakened.",
            "Cultivation", 5));
        ALL_PERKS.add(new Perk(id++, "Tribulation Resistance",
            "Tribulation damage is reduced by 50%.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Extended Lifespan",
            "Your lifespan is permanently doubled. More time to cultivate.",
            "Cultivation", 4));
        ALL_PERKS.add(new Perk(id++, "Eternal Youth",
            "Your lifespan is permanently tripled. Aging is drastically slowed.",
            "Cultivation", 5));
        ALL_PERKS.add(new Perk(id++, "Qi Shield Mastery",
            "Your Qi shield effectiveness is permanently doubled.",
            "Cultivation", 3));
        ALL_PERKS.add(new Perk(id++, "Breakthrough Gift",
            "First three breakthroughs have no tribulation. Free passage to Foundation Building.",
            "Cultivation", 4));

        // ── Spell & Combat Perks (46-60) ──
        ALL_PERKS.add(new Perk(id++, "Spell Damage Boost",
            "All your spells deal 50% more damage permanently.",
            "Combat", 3));
        ALL_PERKS.add(new Perk(id++, "Double Spell Damage",
            "All your spells deal double damage permanently.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Spell Cost Reduction",
            "All spell Qi costs are reduced by 50% permanently.",
            "Combat", 3));
        ALL_PERKS.add(new Perk(id++, "Free Spells",
            "All spell Qi costs are reduced by 80%.",
            "Combat", 5));
        ALL_PERKS.add(new Perk(id++, "Spell Charge Speed",
            "Spell charge time is halved. Cast faster in combat.",
            "Combat", 3));
        ALL_PERKS.add(new Perk(id++, "Elemental Mastery",
            "All elemental spell damage is doubled.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Sword Saint",
            "Sword spell damage is tripled.",
            "Combat", 5));
        ALL_PERKS.add(new Perk(id++, "Fire Sage",
            "Fire spell damage is tripled.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Ice Sovereign",
            "Ice spell damage is tripled.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Thunder Lord",
            "Lightning/thunder spell damage is tripled.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Dodge Master",
            "Your dodge chance is permanently doubled.",
            "Combat", 3));
        ALL_PERKS.add(new Perk(id++, "Iron Body Defense",
            "Your HP and defense are permanently doubled.",
            "Combat", 4));
        ALL_PERKS.add(new Perk(id++, "Critical Cultivator",
            "Your critical hit chance and damage are permanently doubled.",
            "Combat", 3));
        ALL_PERKS.add(new Perk(id++, "Movement Technique",
            "Your movement speed is permanently increased by 50%.",
            "Combat", 2));
        ALL_PERKS.add(new Perk(id++, "Battle Saint",
            "All combat multipliers (attack, defense, HP, crit) increased by 50%.",
            "Combat", 5));

        // ── Pill & Alchemy Perks (61-70) ──
        ALL_PERKS.add(new Perk(id++, "Pill Arsenal",
            "Start with 99 Qi Recovery Pills, 99 Clear Mind Pills, and 99 Divine Stride Pills.",
            "Alchemy", 3));
        ALL_PERKS.add(new Perk(id++, "Spirit Stone Hoard",
            "Start with 99 Spirit Stones full of Qi.",
            "Alchemy", 2));
        ALL_PERKS.add(new Perk(id++, "Blood Burn Pills",
            "Start with 50 Blood Burn Pills for emergency power boosts.",
            "Alchemy", 2));
        ALL_PERKS.add(new Perk(id++, "Master Alchemist",
            "Alchemy XP gain is permanently tripled. Level up alchemy faster.",
            "Alchemy", 3));
        ALL_PERKS.add(new Perk(id++, "Pill Effectiveness",
            "All pill effects are 50% stronger when you consume them.",
            "Alchemy", 3));
        ALL_PERKS.add(new Perk(id++, "Furnace Enhancement",
            "Your alchemy furnace has triple the Qi capacity.",
            "Alchemy", 2));
        ALL_PERKS.add(new Perk(id++, "Instant Pills",
            "Pill crafting time is halved.",
            "Alchemy", 2));
        ALL_PERKS.add(new Perk(id++, "Divine Pill Recipe",
            "Start with knowledge of all pill recipes.",
            "Alchemy", 4));
        ALL_PERKS.add(new Perk(id++, "Pill Immunity",
            "You are immune to pill side effects and toxicity.",
            "Alchemy", 3));
        ALL_PERKS.add(new Perk(id++, "Elixir of Life",
            "Start with 3 Elixers of Life that permanently extend lifespan by 100 years each.",
            "Alchemy", 4));

        // ── Refining & Weapon Perks (71-80) ──
        ALL_PERKS.add(new Perk(id++, "Master Refiner",
            "Refining XP gain is permanently tripled.",
            "Refining", 3));
        ALL_PERKS.add(new Perk(id++, "Heavenly Sword",
            "Start with ONE Heavenly-tier Spirit Sword (random element) with maximum qi cost reduction.",
            "Refining", 4));
        ALL_PERKS.add(new Perk(id++, "Spirit Sword Arsenal",
            "Start with ONE Spirit Sword of a random tier (Wood, Iron, Silver, Gold, Jade, or Heavenly). Only one sword, not multiple.",
            "Refining", 4));
        ALL_PERKS.add(new Perk(id++, "Storage Bag",
            "Start with a large Storage Bag (maximum columns and rows).",
            "Refining", 2));
        ALL_PERKS.add(new Perk(id++, "Furnace of Heaven",
            "Your refining furnace has triple the Qi capacity.",
            "Refining", 2));
        ALL_PERKS.add(new Perk(id++, "Instant Refining",
            "Refining time is halved.",
            "Refining", 2));
        ALL_PERKS.add(new Perk(id++, "Tier-Up Master",
            "Refining tier-up chances are doubled.",
            "Refining", 3));
        ALL_PERKS.add(new Perk(id++, "Weapon Mastery",
            "All weapon damage is permanently increased by 50%.",
            "Refining", 3));
        ALL_PERKS.add(new Perk(id++, "Qi Cost Free Weapons",
            "All weapon spell Qi costs are reduced by 80%.",
            "Refining", 4));
        ALL_PERKS.add(new Perk(id++, "Divine Weapon Recipe",
            "Start with knowledge of all weapon refining recipes.",
            "Refining", 4));

        // ── World & Resource Perks (81-90) ──
        ALL_PERKS.add(new Perk(id++, "Spirit Vein Heart",
            "A Spirit Vein forms near your spawn point with maximum Qi capacity.",
            "World", 4));
        ALL_PERKS.add(new Perk(id++, "Qi Rich Biome",
            "The biome around your spawn has triple Qi density.",
            "World", 3));
        ALL_PERKS.add(new Perk(id++, "Formation Master",
            "Formation power is permanently doubled for your formations.",
            "World", 3));
        ALL_PERKS.add(new Perk(id++, "Lucky Loot",
            "Chest loot quality and quantity are permanently doubled for you.",
            "World", 3));
        ALL_PERKS.add(new Perk(id++, "Spirit Plant Garden",
            "Start with a garden of fully grown spirit plants near your spawn.",
            "World", 3));
        ALL_PERKS.add(new Perk(id++, "Qi Stone Mine",
            "A Qi Stone Ore vein generates near your spawn point.",
            "World", 2));
        ALL_PERKS.add(new Perk(id++, "Spirit Herb Knowledge",
            "Start with knowledge of all spirit plant locations and effects.",
            "World", 2));
        ALL_PERKS.add(new Perk(id++, "Formation Blueprint",
            "Start with blueprints for all formation types.",
            "World", 3));
        ALL_PERKS.add(new Perk(id++, "Beast Tamer",
            "Start with a tamed beast at Foundation Building realm.",
            "World", 4));
        ALL_PERKS.add(new Perk(id++, "Beast Companion",
            "Start with a tamed beast at Qi Refining Peak that cultivates 3x faster.",
            "World", 3));

        // ── Sect & Identity Perks (91-99) ──
        ALL_PERKS.add(new Perk(id++, "Sect Elder",
            "Start as an elder of a powerful sect with access to the sect shop at discount.",
            "Sect", 4));
        ALL_PERKS.add(new Perk(id++, "Sect Disciple",
            "Start as a disciple of a sect with basic shop access.",
            "Sect", 2));
        ALL_PERKS.add(new Perk(id++, "Shop Discount",
            "All sect shop prices are halved for you permanently.",
            "Sect", 3));
        ALL_PERKS.add(new Perk(id++, "Cultivator Identity",
            "Start with the Cultivator identity - extended starting lifespan.",
            "Identity", 2));
        ALL_PERKS.add(new Perk(id++, "Scholar Identity",
            "Start with the Scholar identity - bonus to alchemy and refining.",
            "Identity", 2));
        ALL_PERKS.add(new Perk(id++, "Martial Identity",
            "Start with the Martial identity - bonus to combat stats.",
            "Identity", 2));
        ALL_PERKS.add(new Perk(id++, "Abandoned Prodigy",
            "Start with the Abandoned identity but with triple cultivation speed to compensate.",
            "Identity", 4));
        ALL_PERKS.add(new Perk(id++, "Sect Ancestor's Blessing",
            "A sect ancestor's blessing grants you permanent +50% to all techniques.",
            "Sect", 5));
        ALL_PERKS.add(new Perk(id++, "Destined One",
            "You are the Destined One. ALL golden finger effects are doubled. " +
            "This is the ultimate perk - only choose it if you have no other perks.",
            "Destiny", 5));
    }

    /** Get perks by category. */
    public static List<Perk> getByCategory(String category) {
        List<Perk> result = new ArrayList<>();
        for (Perk p : ALL_PERKS) {
            if (p.category.equals(category)) result.add(p);
        }
        return result;
    }

    /** Get all category names. */
    public static List<String> getCategories() {
        LinkedHashSet<String> cats = new LinkedHashSet<>();
        for (Perk p : ALL_PERKS) cats.add(p.category);
        return new ArrayList<>(cats);
    }

    /** Get a perk by ID. */
    public static Perk getById(int id) {
        for (Perk p : ALL_PERKS) {
            if (p.id == id) return p;
        }
        return null;
    }

    /** Get the tier color code for display. */
    public static String getTierColor(int tier) {
        switch (tier) {
            case 1: return "\u00A77"; // gray - minor
            case 2: return "\u00A7b"; // aqua - lesser
            case 3: return "\u00A7a"; // green - moderate
            case 4: return "\u00A7d"; // light purple - great
            case 5: return "\u00A76"; // gold - legendary
            default: return "\u00A7f";
        }
    }

    /** Get the tier name. */
    public static String getTierName(int tier) {
        switch (tier) {
            case 1: return "Minor";
            case 2: return "Lesser";
            case 3: return "Moderate";
            case 4: return "Great";
            case 5: return "Legendary";
            default: return "Unknown";
        }
    }
}
