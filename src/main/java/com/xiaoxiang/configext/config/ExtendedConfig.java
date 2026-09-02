package com.xiaoxiang.configext.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Extended configuration for the Xiaoxiang Cultivation World mod.
 * Exposes 300+ values that are hardcoded in the original mod so they can be
 * tuned without recompiling. Read by the Mixin classes in this companion mod.
 *
 * Organized into deeply-nested categories:
 *   general, realms, beasts, spawns, qiDensity, spells, weapons, pills,
 *   alchemy, refining, spiritPlants, spiritVeins, techniques, spiritRoots,
 *   physiques, foundationDao, goldenCoreDao, identity, progression,
 *   npcCombat, formations, sects, loot, trials, qiSystem, passiveSpells,
 *   effects, morality, lifespan, client (UI/accessibility)
 */
public final class ExtendedConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    // ══════════════════════════════════════════════════════════════════
    //  GENERAL TOGGLES
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.BooleanValue ENABLE_REALM_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BEAST_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPAWN_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_QI_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPELL_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_WEAPON_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PILL_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ALCHEMY_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_REFINING_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPIRIT_PLANT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPIRIT_VEIN_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TECHNIQUE_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPIRIT_ROOT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PHYSIQUE_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DAO_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_IDENTITY_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PROGRESSION_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_NPC_COMBAT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FORMATION_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SECT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LOOT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TRIAL_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_QI_SYSTEM_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PASSIVE_SPELL_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_EFFECT_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MORALITY_OVERRIDES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LIFESPAN_OVERRIDES;
    // New in base mod 0.1.1479: the Talisman glyph-crafting system (166 new classes).
    // Only 2 of its ~110 candidate tunable constants are wired so far (see
    // TalismanCompilerMixin) - this toggle covers everything wired for that system,
    // present and future waves alike, same convention as every other ENABLE_*_OVERRIDES.
    public static final ForgeConfigSpec.BooleanValue ENABLE_TALISMAN_OVERRIDES;
    public static final ForgeConfigSpec.DoubleValue TALISMAN_DEFAULT_EXPLOSION_RADIUS;
    public static final ForgeConfigSpec.DoubleValue TALISMAN_MAX_EXPLOSION_RADIUS;
    // Wave 2 (2026-09-02): 82 distinct per-glyph Qi cost fields, wired via
    // TalismanGlyphCatalogMixin - see CONFIG_AUDIT.md for the full derivation.
    public static final ForgeConfigSpec.IntValue TALISMAN_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_FIREBALL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_EXPLOSION_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DAMAGE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DESTRUCTION_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SPLIT_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DIVINE_SENSE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_ANTI_AIR_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_LIGHTNING_ARC_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_LIGHTNING_STRIKE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_ICE_SWORD_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_REGENERATION_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SWIFTNESS_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_POISON_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DELAY_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HOMING_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_PIERCE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_POTENCY_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DURATION_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_PULL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_MECHANISM_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_TAMPER_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_MELEE_STRIKE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_RANGED_STRIKE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SUCCESSFUL_GUARD_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SPELLCAST_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_VANQUISH_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_LOW_QI_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_AFFLICTED_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_IGNITED_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HARD_LANDING_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SHIELD_BROKEN_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_LEFT_CLICK_SUPPORT_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_NEARBY_BLOCK_BREAK_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HOSTILE_PROJECTILE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_BOUNDARY_CROSSING_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HOSTILE_GAZE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HOSTILE_SPELLCAST_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_NEARBY_EXPLOSION_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_REDSTONE_FALLING_EDGE_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_DAWN_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_NEARBY_NATURAL_LIGHTNING_TRIGGER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_STONE_ORB_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_HEAVEN_DRILL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_CRIMSON_LOTUS_DOMAIN_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_THUNDER_ORB_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_STARFALL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SWORDMARK_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SURGING_TIDE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_VERDANT_SEED_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_WATER_PRISON_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_FROST_WALL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_POLARITY_FIELD_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SPELLRETURN_MIRROR_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_FLAME_STEP_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_BLOOD_SPIKE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_THUNDERSTEP_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_MIRRORWATER_CLONE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_FROSTMIRROR_AEGIS_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_WOODEN_DAO_SOLDIER_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_REJUVENATION_ALTAR_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_FIVE_PEAKS_SEAL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_AREA_PLUS_HALF_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_RANGE_PLUS_HALF_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_PROJECTILE_SPEED_PLUS_HALF_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_SCALE_PLUS_HALF_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_ECHO_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_REPEL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_LIFEDRAIN_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_ARMORBREAK_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_MERIDIAN_SEAL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_BARRIER_DAMAGE_X2_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_CLEANSE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_PROLIFERATE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_PRESERVE_TERRAIN_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_RELOCATE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_IGNITE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_CHILL_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_EXECUTE_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_KNOCKUP_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_WEAKNESS_QI_COST;
    public static final ForgeConfigSpec.IntValue TALISMAN_GLOWING_MARK_QI_COST;

    // ══════════════════════════════════════════════════════════════════
    //  REALM POWER LEVELS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue MORTAL_MAX_QI;
    public static final ForgeConfigSpec.IntValue QI_REFINING_EARLY_MAX_QI;
    public static final ForgeConfigSpec.IntValue QI_REFINING_MIDDLE_MAX_QI;
    public static final ForgeConfigSpec.IntValue QI_REFINING_LATE_MAX_QI;
    public static final ForgeConfigSpec.IntValue QI_REFINING_PEAK_MAX_QI;
    public static final ForgeConfigSpec.IntValue REALM_PROGRESSION_BASE;
    public static final ForgeConfigSpec.IntValue REALM_BASE_DELTA_EARLY;
    public static final ForgeConfigSpec.IntValue REALM_BASE_DELTA_MIDDLE;
    public static final ForgeConfigSpec.IntValue REALM_BASE_DELTA_LATE;
    public static final ForgeConfigSpec.IntValue REALM_BASE_DELTA_PEAK;
    public static final ForgeConfigSpec.IntValue TRUE_IMMORTAL_MAX_QI;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_MAX_QI;
    public static final ForgeConfigSpec.DoubleValue MAX_QI_GLOBAL_MULTIPLIER;

    // Breakthrough monotonicity guarantee (added 2026-09-01, player-reported
    // "requirements decreased after reincarnation" bug triage - see RealmMixin.java)
    public static final ForgeConfigSpec.BooleanValue ENABLE_MONOTONIC_BREAKTHROUGHS;
    public static final ForgeConfigSpec.IntValue MONOTONIC_MIN_MINOR_INCREASE;
    public static final ForgeConfigSpec.IntValue MONOTONIC_MIN_MAJOR_INCREASE;

    // Reincarnation-not-retriggering-after-first-death fix (2026-09-01)
    public static final ForgeConfigSpec.BooleanValue ENABLE_REINCARNATION_TECHNIQUE_FIX;

    // Scrollable item/physique tooltips on the Identity Draw screen (2026-09-01)
    public static final ForgeConfigSpec.BooleanValue ENABLE_SCROLLABLE_ITEM_TOOLTIPS;
    public static final ForgeConfigSpec.IntValue SCROLLABLE_TOOLTIP_MAX_LINES;

    // Lifespan per realm
    public static final ForgeConfigSpec.IntValue QI_REFINING_LIFESPAN;
    public static final ForgeConfigSpec.IntValue FOUNDATION_BUILDING_LIFESPAN;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_LIFESPAN;
    public static final ForgeConfigSpec.IntValue NASCENT_SOUL_LIFESPAN;
    public static final ForgeConfigSpec.IntValue SOUL_FORMATION_LIFESPAN;
    public static final ForgeConfigSpec.IntValue VOID_REFINING_LIFESPAN;
    public static final ForgeConfigSpec.IntValue BODY_INTEGRATION_LIFESPAN;
    public static final ForgeConfigSpec.IntValue MAHAYANA_LIFESPAN;
    public static final ForgeConfigSpec.IntValue TRIBULATION_TRANSCENDENCE_LIFESPAN;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_LIFESPAN;
    public static final ForgeConfigSpec.IntValue TRUE_IMMORTAL_LIFESPAN;
    public static final ForgeConfigSpec.DoubleValue LIFESPAN_GLOBAL_MULTIPLIER;

    // Qi shield reduction percent per realm
    public static final ForgeConfigSpec.IntValue QI_REFINING_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue FOUNDATION_BUILDING_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue NASCENT_SOUL_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue SOUL_FORMATION_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue VOID_REFINING_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue BODY_INTEGRATION_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue MAHAYANA_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue TRIBULATION_TRANSCENDENCE_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue TRUE_IMMORTAL_SHIELD_PERCENT;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_SHIELD_PERCENT;

    // Tribulation strike damage per realm
    public static final ForgeConfigSpec.IntValue QI_REFINING_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue FOUNDATION_BUILDING_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue NASCENT_SOUL_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue SOUL_FORMATION_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue VOID_REFINING_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue BODY_INTEGRATION_TRIB_DAMAGE;
    public static final ForgeConfigSpec.IntValue TRIBULATION_TRANSCENDENCE_TRIB_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue TRIB_DAMAGE_GLOBAL_MULTIPLIER;

    // Loose immortal settings
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_BASE_REDUCTION_PERCENT;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_FULL_REDUCTION_TRIBULATIONS;

    // Tribulation timing
    public static final ForgeConfigSpec.IntValue TRIBULATION_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue TRIBULATION_CHARGE_TICKS;
    public static final ForgeConfigSpec.IntValue TRIBULATION_BOLT_COOLDOWN_TICKS;

    // Zhenyuan rewards
    public static final ForgeConfigSpec.IntValue ZHENYUAN_REWARD_MINOR;
    public static final ForgeConfigSpec.IntValue ZHENYUAN_REWARD_MAJOR;
    public static final ForgeConfigSpec.IntValue ZHENYUAN_ATTR_REWARD_MINOR;
    public static final ForgeConfigSpec.IntValue ZHENYUAN_ATTR_REWARD_MAJOR;
    public static final ForgeConfigSpec.DoubleValue ZHENYUAN_STAT_MULTIPLIER;

    // Time acceleration
    public static final ForgeConfigSpec.IntValue TIME_ACCELERATION_MIN;
    public static final ForgeConfigSpec.IntValue TIME_ACCELERATION_MAX;

    // ══════════════════════════════════════════════════════════════════
    //  BEAST CULTIVATION
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.BooleanValue BEAST_CULTIVATION_FOR_MONSTERS;
    public static final ForgeConfigSpec.BooleanValue BEAST_CULTIVATION_FOR_ALL_MOBS;
    public static final ForgeConfigSpec.IntValue BEAST_CHECK_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue BEAST_QI_DENSITY_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue BEAST_QI_GAIN_MULTIPLIER;
    public static final ForgeConfigSpec.LongValue SPIRIT_SOLDIER_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue SPIRIT_GENERAL_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue SPIRIT_MARSHAL_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue SPIRIT_KING_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue SPIRIT_EMPEROR_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue SPIRIT_LORD_ADVANCE_COST;
    // Added 2026-09-02 for base mod 0.1.1479's beast-progression redesign: beasts
    // now advance through the same 12-tier Realm ladder players use (via the new
    // BeastProgressionRules.advanceCost(Realm) static method) instead of the old,
    // separate 8-tier BeastRealm enum. The first 6 tiers (QI_REFINING through
    // VOID_REFINING) are already covered by the 6 fields above - their real
    // bytecode defaults (500/5000/50000/500000/5000000/50000000) are identical
    // in 0.1.1479, so those fields keep their existing names/TOML keys/values
    // unchanged (no config migration needed). These 4 new fields cover the 4
    // additional tiers the redesign added on top of the old ladder.
    public static final ForgeConfigSpec.LongValue BEAST_BODY_INTEGRATION_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue BEAST_MAHAYANA_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue BEAST_TRIBULATION_TRANSCENDENCE_ADVANCE_COST;
    public static final ForgeConfigSpec.LongValue BEAST_TRUE_IMMORTAL_ADVANCE_COST;

    // ══════════════════════════════════════════════════════════════════
    //  NPC SPAWNING
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue CULTIVATOR_SPAWN_CHANCE_NEAR;
    public static final ForgeConfigSpec.DoubleValue CULTIVATOR_SPAWN_CHANCE_FAR;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_MORTAL;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_QI_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_FOUNDATION_BUILDING;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_GOLDEN_CORE;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_NASCENT_SOUL;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_SOUL_FORMATION;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_VOID_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_BODY_INTEGRATION;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_MAHAYANA;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_TRIBULATION_TRANSCENDENCE;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_LOOSE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue NPC_WEIGHT_TRUE_IMMORTAL;

    // ══════════════════════════════════════════════════════════════════
    //  QI DENSITY PER BIOME
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_SPARSE;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_NORMAL;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_WOOD_RICH;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_WATER_RICH;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_FIRE_RICH;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_EARTH_RICH;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_ICE_RICH;
    public static final ForgeConfigSpec.DoubleValue QI_DENSITY_END_PURE;

    // ══════════════════════════════════════════════════════════════════
    //  SPELLS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue SPELL_DAMAGE_GLOBAL_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue SPELL_QI_COST_GLOBAL_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue SPELL_CHARGE_GLOBAL_MULTIPLIER;
    // NPC-specific spell multipliers (applied on top of global multipliers)
    public static final ForgeConfigSpec.DoubleValue NPC_SPELL_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue NPC_SPELL_QI_COST_MULTIPLIER;
    // Player cultivation speed multiplier
    public static final ForgeConfigSpec.DoubleValue PLAYER_CULTIVATION_SPEED_MULT;
    // NPC stat multipliers (HP, attack, defense)
    public static final ForgeConfigSpec.DoubleValue NPC_HP_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue NPC_ATTACK_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue NPC_DEFENSE_MULTIPLIER;
    // Meditation tick interval (lower = faster updates)
    public static final ForgeConfigSpec.IntValue MEDITATION_TICK_INTERVAL;
    // Zhenyuan (stat point) reward amounts
    public static final ForgeConfigSpec.IntValue ZHENYUAN_REWARD_AMOUNT;
    public static final ForgeConfigSpec.IntValue ZHENYUAN_MINOR_REWARD_AMOUNT;
    // NPC combat stalemate timeout
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_STALEMATE_TIMEOUT;

    public static final ForgeConfigSpec.IntValue SWORD_FLIGHT_UPKEEP_QI_PER_SECOND;
    public static final ForgeConfigSpec.IntValue VOID_STEP_AIR_JUMP_QI_COST;
    public static final ForgeConfigSpec.IntValue VOID_STEP_DASH_QI_COST;
    public static final ForgeConfigSpec.IntValue VOID_STEP_SLOW_FALL_QI_COST;
    public static final ForgeConfigSpec.IntValue PALM_THUNDER_CHANNEL_QI_PER_SECOND;
    public static final ForgeConfigSpec.IntValue PALM_THUNDER_ARMING_TICKS;
    public static final ForgeConfigSpec.IntValue VOID_ESCAPE_CHARGE_TICKS;
    public static final ForgeConfigSpec.IntValue VOID_ESCAPE_CHARGE_QI_PER_TICK;
    public static final ForgeConfigSpec.IntValue VOID_ESCAPE_ACTIVE_QI_PER_TICK;
    public static final ForgeConfigSpec.IntValue BUDDHA_FIRE_LOTUS_READY_QI;
    public static final ForgeConfigSpec.IntValue CORE_SELF_DESTRUCT_READY_QI;
    public static final ForgeConfigSpec.IntValue SEA_CHANNEL_TICKS;
    public static final ForgeConfigSpec.IntValue GLACIER_BURIAL_NPC_CHANNEL_TICKS;
    public static final ForgeConfigSpec.LongValue GLACIER_BURIAL_BASE_QI_PER_TICK;

    // ══════════════════════════════════════════════════════════════════
    //  WEAPONS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue WEAPON_DAMAGE_GLOBAL_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WEAPON_BLOOD_SPELL_DAMAGE_BONUS_MULT;
    public static final ForgeConfigSpec.DoubleValue WEAPON_BLOOD_SPELL_QI_REDUCTION_MULT;
    public static final ForgeConfigSpec.DoubleValue WEAPON_BLOOD_CAPACITY_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue CHIYAN_BURN_TICKS;
    public static final ForgeConfigSpec.IntValue HANBING_FROZEN_OVERFLOW_TICKS;
    public static final ForgeConfigSpec.IntValue QINGMU_POISON_TICKS;
    public static final ForgeConfigSpec.IntValue SOUL_HOOK_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue WEAPON_ATTACK_SPEED_MODIFIER;
    // Tiered weapon spell qi cost reduction percents
    public static final ForgeConfigSpec.IntValue WEAPON_SPELL_QI_COST_REDUCTION_LOW;
    public static final ForgeConfigSpec.IntValue WEAPON_SPELL_QI_COST_REDUCTION_MID;
    public static final ForgeConfigSpec.IntValue WEAPON_SPELL_QI_COST_REDUCTION_HIGH;
    public static final ForgeConfigSpec.IntValue WEAPON_SPELL_QI_COST_REDUCTION_SUPREME;
    public static final ForgeConfigSpec.IntValue WEAPON_SPELL_QI_COST_REDUCTION_IMMORTAL;

    // ══════════════════════════════════════════════════════════════════
    //  PILLS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue PILL_USE_TICKS;
    public static final ForgeConfigSpec.IntValue SPIRIT_STONE_USE_TICKS;
    public static final ForgeConfigSpec.IntValue PILL_QI_LOW;
    public static final ForgeConfigSpec.IntValue PILL_QI_MID;
    public static final ForgeConfigSpec.IntValue PILL_QI_HIGH;
    public static final ForgeConfigSpec.IntValue PILL_QI_SUPREME;
    public static final ForgeConfigSpec.IntValue PILL_QI_IMMORTAL;
    public static final ForgeConfigSpec.IntValue SPIRIT_STONE_QI_LOW;
    public static final ForgeConfigSpec.IntValue SPIRIT_STONE_QI_MID;
    public static final ForgeConfigSpec.IntValue SPIRIT_STONE_QI_HIGH;
    public static final ForgeConfigSpec.IntValue SPIRIT_STONE_QI_SUPREME;
    public static final ForgeConfigSpec.DoubleValue BLOOD_BURN_PILL_DAMAGE_LOW;
    public static final ForgeConfigSpec.DoubleValue BLOOD_BURN_PILL_DAMAGE_MID;
    public static final ForgeConfigSpec.DoubleValue BLOOD_BURN_PILL_DAMAGE_HIGH;
    public static final ForgeConfigSpec.DoubleValue BLOOD_BURN_PILL_DAMAGE_SUPREME;
    public static final ForgeConfigSpec.DoubleValue BLOOD_BURN_PILL_DAMAGE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue BLOOD_BURN_PILL_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue CLEAR_MIND_PILL_DURATION_HIGH;
    public static final ForgeConfigSpec.IntValue CLEAR_MIND_PILL_DURATION_SUPREME;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_DURATION_LOW;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_DURATION_MID;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_DURATION_HIGH;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_DURATION_SUPREME;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_SPEED_LOW;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_SPEED_MID;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_SPEED_HIGH;
    public static final ForgeConfigSpec.IntValue DIVINE_STRIDE_SPEED_SUPREME;
    public static final ForgeConfigSpec.IntValue SHADOW_STEP_PILL_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue YOUTH_PILL_MIN_BONE_AGE;
    public static final ForgeConfigSpec.DoubleValue YOUTH_PILL_BONE_AGE_REDUCTION;
    public static final ForgeConfigSpec.IntValue FOUNDATION_MATERIAL_USE_TICKS;
    public static final ForgeConfigSpec.LongValue FOUNDATION_MATERIAL_ZHUJI_DAN_QI;
    public static final ForgeConfigSpec.LongValue FOUNDATION_MATERIAL_DAO_FRUIT_QI;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_MATERIAL_USE_TICKS;
    public static final ForgeConfigSpec.LongValue GOLDEN_CORE_MATERIAL_JIEDAN_PILL_QI;
    public static final ForgeConfigSpec.LongValue GOLDEN_CORE_MATERIAL_CREATION_FRUIT_QI;
    // Rejuvenation pill
    public static final ForgeConfigSpec.DoubleValue REJUVENATION_HEAL_LOW;
    public static final ForgeConfigSpec.DoubleValue REJUVENATION_HEAL_MID;
    public static final ForgeConfigSpec.IntValue REJUVENATION_REGEN_TICKS_SUPREME;
    public static final ForgeConfigSpec.IntValue REJUVENATION_REGEN_AMP_SUPREME;
    public static final ForgeConfigSpec.IntValue REJUVENATION_ABSORPTION_TICKS_SUPREME;
    public static final ForgeConfigSpec.IntValue REJUVENATION_ABSORPTION_AMP_SUPREME;
    // Storage bag
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_COLUMNS_LOW;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_COLUMNS_MID;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_COLUMNS_HIGH;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_COLUMNS_SUPREME;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_COLUMNS_IMMORTAL;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_ROWS_LOW;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_ROWS_MID;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_ROWS_HIGH;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_ROWS_SUPREME;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_ROWS_IMMORTAL;
    public static final ForgeConfigSpec.IntValue STORAGE_BAG_VISIBLE_ROWS_MAX;

    // ══════════════════════════════════════════════════════════════════
    //  ALCHEMY
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.LongValue ALCHEMY_FURNACE_MAX_QI;
    public static final ForgeConfigSpec.IntValue ALCHEMY_TICKS_PER_PILL;
    public static final ForgeConfigSpec.IntValue ALCHEMY_MAX_PILLS_PER_BATCH;
    public static final ForgeConfigSpec.IntValue ALCHEMY_INPUT_SLOTS;
    public static final ForgeConfigSpec.IntValue ALCHEMY_OUTPUT_SLOTS;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_LOW;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_MID;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_HIGH;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_SUPREME;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_IMMORTAL;
    public static final ForgeConfigSpec.IntValue ALCHEMY_XP_GAIN_FAILURE;
    public static final ForgeConfigSpec.DoubleValue ALCHEMY_HEART_SUCCESS_BONUS;
    public static final ForgeConfigSpec.DoubleValue ALCHEMY_HEART_QI_COST_MULT;

    // ══════════════════════════════════════════════════════════════════
    //  REFINING
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.LongValue REFINING_FURNACE_MAX_QI;
    public static final ForgeConfigSpec.IntValue REFINING_TICKS_PER_ITEM;
    public static final ForgeConfigSpec.IntValue REFINING_MAX_ITEMS_PER_BATCH;
    public static final ForgeConfigSpec.IntValue REFINING_INPUT_SLOTS;
    public static final ForgeConfigSpec.IntValue REFINING_OUTPUT_SLOTS;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_LOW;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_MID;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_HIGH;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_SUPREME;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_IMMORTAL;
    public static final ForgeConfigSpec.IntValue REFINING_XP_GAIN_FAILURE;
    public static final ForgeConfigSpec.DoubleValue REFINING_TIER_UP_CHANCE_DIVINE_FORGE;
    public static final ForgeConfigSpec.DoubleValue REFINING_TIER_UP_CHANCE_HEAVENLY_ELIXIR;

    // ══════════════════════════════════════════════════════════════════
    //  SPIRIT PLANTS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_MAX_AGE;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_PLANT_GROWTH_TICK_BASE;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_QI_ORB_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_PLANT_SKIP_RADIUS;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_SKIP_COUNT;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_FLAME_MELT_RADIUS;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_PLANT_EARTH_MARROW_GROWTH_CHANCE;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_EARTH_MARROW_GROWTH_RADIUS;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_PLANT_GOLDEN_CHRYSANTHEMUM_DROP_CHANCE;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_SNOW_RADIUS;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_SNOW_MAX_LAYERS;
    public static final ForgeConfigSpec.IntValue SPIRIT_PLANT_SNOW_PLACE_ATTEMPTS;

    // ══════════════════════════════════════════════════════════════════
    //  SPIRIT VEINS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_MAX_QI_LOW;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_MAX_QI_MID;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_MAX_QI_HIGH;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_MAX_QI_SUPREME;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_MAX_QI_IMMORTAL;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_ORB_GAIN_LOW;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_ORB_GAIN_MID;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_ORB_GAIN_HIGH;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_ORB_GAIN_SUPREME;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_ORB_GAIN_IMMORTAL;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_SUPPLY_LOW;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_SUPPLY_MID;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_SUPPLY_HIGH;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_SUPPLY_SUPREME;
    public static final ForgeConfigSpec.LongValue SPIRIT_VEIN_SUPPLY_IMMORTAL;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_VEIN_ATTRACT_RADIUS;
    public static final ForgeConfigSpec.IntValue SPIRIT_VEIN_SUPPLY_RADIUS;

    // ══════════════════════════════════════════════════════════════════
    //  TECHNIQUES
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_QI_ABSORB_MULT_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_ATTACK_BONUS_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_DEFENSE_BONUS_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_MAX_HP_BONUS_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_CRIT_RATE_BONUS_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_ELEMENT_SPELL_MULT_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_MOVE_SPEED_BONUS_GLOBAL;

    // ══════════════════════════════════════════════════════════════════
    //  SPIRIT ROOTS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_HEAVENLY_PRIMARY_ELEMENT_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_HEAVENLY_COUNTER_ELEMENT_MULT;
    public static final ForgeConfigSpec.IntValue SPIRIT_ROOT_HEAVENLY_EXTRA_ZHENYUAN_PER_SUB_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_DUAL_PRIMARY_ELEMENT_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_DUAL_SECONDARY_ELEMENT_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_DUAL_OFF_ELEMENT_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_MUTANT_PRIMARY_ELEMENT_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_HEAVENLY_SWORD_SWORD_DMG_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_HEAVENLY_SWORD_NON_ELEMENT_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_HEAVENLY_HIDDEN_NON_ELEMENT_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_QI_ABSORB_SSR_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_QI_ABSORB_SR_MULT;
    public static final ForgeConfigSpec.DoubleValue SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT;

    // ══════════════════════════════════════════════════════════════════
    //  PHYSIQUES
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_MULT;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_RANGE;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_IMMORTAL_BODY_QI_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_IMMORTAL_BODY_DAMAGE_TAKEN_MULT;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_IMMORTAL_BODY_MAX_HP_BONUS;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_INNATE_SWORD_BODY_SWORD_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_INNATE_SWORD_NON_SWORD_PENALTY;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_HEAVENLY_FIRE_BODY_FIRE_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_MYSTIC_ICE_BODY_WATER_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_SWORD_BONE_SWORD_SPELL_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_CHAOS_BODY_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_CHAOS_BODY_CULTIVATION_REQ_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_BROKEN_VEIN_HP_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_BROKEN_VEIN_MELEE_DMG_MULT;
    public static final ForgeConfigSpec.DoubleValue PHYSIQUE_IMMORTAL_BLOOD_HP_MULT;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_LOW;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_MID;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_HIGH;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_SUPREME;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_IMMORTAL;
    public static final ForgeConfigSpec.IntValue PHYSIQUE_RARITY_WEIGHT_SPECIAL;

    // ══════════════════════════════════════════════════════════════════
    //  FOUNDATION DAO
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HUMAN_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_BLOOD_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_EARTH_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.DoubleValue FOUNDATION_DAO_EARTH_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue FOUNDATION_DAO_HEAVEN_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue FOUNDATION_DAO_EARTH_SPELL_QI_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue FOUNDATION_DAO_HEAVEN_SPELL_QI_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue FOUNDATION_DAO_BLOOD_HP_MULT;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HUMAN_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_BLOOD_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_EARTH_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_EARTH_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_BLOOD_MELEE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_BONE_AGE_LIMIT;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_HEAVEN_TRIBULATION_WAVES;
    public static final ForgeConfigSpec.IntValue FOUNDATION_DAO_EARTH_TRIBULATION_WAVES;

    // ══════════════════════════════════════════════════════════════════
    //  GOLDEN CORE DAO
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_LIFESPAN_BONUS;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_EARTH_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HEAVEN_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_EARTH_SPELL_QI_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HEAVEN_SPELL_QI_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_HP_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_DAMAGE_MULT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_QI_COST_MULT;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_TRIBULATION_STRIKES;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_TRIBULATION_STRIKES;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_TRIBULATION_STRIKES;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_TRIBULATION_STRIKES;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_TRIBULATION_DAMAGE;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_BONE_AGE_LIMIT;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REGEN;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REGEN;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REGEN;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REGEN;
    // Missing GoldenCoreDao bonuses
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_BODY_DEFENSE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_QI_RECOVERY_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HUMAN_MELEE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_BLOOD_MELEE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_EARTH_MELEE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.IntValue GOLDEN_CORE_DAO_HEAVEN_MELEE_DAMAGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REFLECTION;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REFLECTION;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REFLECTION;
    public static final ForgeConfigSpec.DoubleValue GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REFLECTION;

    // ══════════════════════════════════════════════════════════════════
    //  IDENTITY — per-identity lifespan + starting items
    // ══════════════════════════════════════════════════════════════════
    // Lifespan per identity (replaces old category-based lifespan)
    //
    // CORRECTED 2026-09-01: every default below was verified against a full
    // javap -p -c -s disassembly of Identity.class's lifespanRange() plus
    // Identity$1's $SwitchMap static initializer (the compiled form of the
    // real mod's switch-on-enum-constant statement). The real method does
    // NOT give every identity its own independent range - it groups 14
    // named identities plus a "default" bucket into 5 shared ranges:
    //   {60,85}   EXILED_PRINCESS, MERCHANT_SON, ACADEMY_STUDENT, TAOIST
    //   {90,110}  BEAST_DESCENDANT, GENERAL_SON, HUNTER, BANDIT_LEADER, PIRATE
    //   {80,105}  HERMIT_DISCIPLE, MONK, DOCTOR_HEIR, LONE_CULTIVATOR
    //   {55,90}   ABANDONED_INFANT (sole member)
    //   {70,100}  default bucket - every other Identity constant, including
    //             FISHERMAN, FARMER, MORTAL_CHILD, FALLEN_NOBLE,
    //             SMITH_APPRENTICE, QINGYUN/WANJIAN/DANDING_OUTER_DISCIPLE,
    //             and FORMATION_APPRENTICE
    // All 17 default pairs below had been guessed rather than verified and
    // were wrong across the board (typically 20-40 years too high); they are
    // now set to match their real bucket exactly. FISHERMAN and FARMER keep
    // their own dedicated config fields (IdentityMixin fully replaces the
    // method via @Inject/HEAD/cancellable, so per-identity granularity for
    // them is a legitimate enhancement over vanilla, not a bug) but their
    // DEFAULT values now match the real "default bucket" range they
    // actually fall into in the base mod. The 7 identities with no dedicated
    // config fields (MORTAL_CHILD, FALLEN_NOBLE, SMITH_APPRENTICE, the 3
    // OUTER_DISCIPLE identities, FORMATION_APPRENTICE) correctly fall
    // through IdentityMixin's else-branch to IDENTITY_LIFESPAN_DEFAULT_MIN/
    // MAX, which is exactly the real bucket they use in the base mod - no
    // gap here, just less per-identity granularity than the 16 identities
    // that do have their own fields.
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_LONE_CULTIVATOR_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_MERCHANT_SON_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_MERCHANT_SON_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_BANDIT_LEADER_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_BANDIT_LEADER_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_HUNTER_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_HUNTER_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_DOCTOR_HEIR_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_DOCTOR_HEIR_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_FISHERMAN_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_FISHERMAN_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_FARMER_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_FARMER_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_ABANDONED_INFANT_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_ABANDONED_INFANT_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_GENERAL_SON_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_GENERAL_SON_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_EXILED_PRINCESS_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_EXILED_PRINCESS_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_PIRATE_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_PIRATE_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_BEAST_DESCENDANT_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_BEAST_DESCENDANT_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_TAOIST_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_TAOIST_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_MONK_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_MONK_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_ACADEMY_STUDENT_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_ACADEMY_STUDENT_MAX;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_DEFAULT_MIN;
    public static final ForgeConfigSpec.IntValue IDENTITY_LIFESPAN_DEFAULT_MAX;

    // Identity starting items — per-identity (16 identities from the original mod)
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_LONE_CULTIVATOR;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_MERCHANT_SON;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_BANDIT_LEADER;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_HUNTER;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_DOCTOR_HEIR;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_HERMIT_DISCIPLE;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_FISHERMAN;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_FARMER;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_ABANDONED_INFANT;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_GENERAL_SON;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_EXILED_PRINCESS;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_PIRATE;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_BEAST_DESCENDANT;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_TAOIST;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_MONK;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_ACADEMY_STUDENT;
    // Default fallback for custom identities
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_STARTING_ITEMS_DEFAULT;
    // Custom identities (user-created) stored as a single string map
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_CUSTOM_IDENTITIES;
    public static final ForgeConfigSpec.ConfigValue<String> IDENTITY_CUSTOM_STARTING_ITEMS;

    // ══════════════════════════════════════════════════════════════════
    //  PROGRESSION
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue PROGRESSION_FOUNDATION_HEAVEN_BONE_AGE_LIMIT;
    public static final ForgeConfigSpec.IntValue PROGRESSION_GOLDEN_CORE_HEAVEN_BONE_AGE_LIMIT;
    public static final ForgeConfigSpec.IntValue PROGRESSION_FOUNDATION_HEAVEN_ESTIMATE_DAYS;
    public static final ForgeConfigSpec.IntValue PROGRESSION_FOUNDATION_EARTH_ESTIMATE_DAYS;
    public static final ForgeConfigSpec.IntValue PROGRESSION_GOLDEN_CORE_HEAVEN_ESTIMATE_DAYS;
    public static final ForgeConfigSpec.IntValue PROGRESSION_GOLDEN_CORE_EARTH_ESTIMATE_DAYS;
    public static final ForgeConfigSpec.DoubleValue PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE;
    public static final ForgeConfigSpec.IntValue PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS;
    public static final ForgeConfigSpec.IntValue PROGRESSION_GENDER_EDITS_DEFAULT;

    // ══════════════════════════════════════════════════════════════════
    //  NPC COMBAT
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_HARD_DODGE_CAP;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_PROJECTILE_SCAN_RADIUS;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_MAX_CANDIDATES;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_MORTAL;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_QI_REFINING;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_FOUNDATION;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_GOLDEN_CORE;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_NASCENT_SOUL;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_SOUL_FORMATION;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_VOID_REFINING;
    public static final ForgeConfigSpec.DoubleValue NPC_COMBAT_DODGE_HIGHER;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_MORTAL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_QI_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_FOUNDATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_GOLDEN_CORE;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_NASCENT_SOUL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_SOUL_FORMATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_VOID_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_SCAN_TICKS_HIGHER;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_MORTAL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_QI_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_FOUNDATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_GOLDEN_CORE;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_NASCENT_SOUL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_SOUL_FORMATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_VOID_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_REACTION_TICKS_HIGHER;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_MORTAL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_QI_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_FOUNDATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_GOLDEN_CORE;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_NASCENT_SOUL;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_SOUL_FORMATION;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_VOID_REFINING;
    public static final ForgeConfigSpec.IntValue NPC_COMBAT_DODGE_COOLDOWN_HIGHER;

    // ══════════════════════════════════════════════════════════════════
    //  FORMATIONS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.LongValue FORMATION_CORE_MAX_QI_LOW;
    public static final ForgeConfigSpec.LongValue FORMATION_CORE_MAX_QI_MID;
    public static final ForgeConfigSpec.LongValue FORMATION_CORE_MAX_QI_HIGH;
    public static final ForgeConfigSpec.LongValue FORMATION_CORE_MAX_QI_SUPREME;
    public static final ForgeConfigSpec.LongValue FORMATION_CORE_MAX_QI_IMMORTAL;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MULT_LOW;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MULT_MID;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MULT_HIGH;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MULT_SUPREME;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MULT_IMMORTAL;
    public static final ForgeConfigSpec.DoubleValue FORMATION_QI_GATHERING_MAX_MULT;
    public static final ForgeConfigSpec.DoubleValue FORMATION_GROWTH_MULT_LOW;
    public static final ForgeConfigSpec.DoubleValue FORMATION_GROWTH_MULT_MID;
    public static final ForgeConfigSpec.DoubleValue FORMATION_GROWTH_MULT_HIGH;
    public static final ForgeConfigSpec.DoubleValue FORMATION_GROWTH_MULT_SUPREME;
    public static final ForgeConfigSpec.DoubleValue FORMATION_GROWTH_MULT_IMMORTAL;
    public static final ForgeConfigSpec.LongValue FORMATION_QI_PER_DAMAGE_LOW;
    public static final ForgeConfigSpec.LongValue FORMATION_QI_PER_DAMAGE_MID;
    public static final ForgeConfigSpec.LongValue FORMATION_QI_PER_DAMAGE_HIGH;
    public static final ForgeConfigSpec.LongValue FORMATION_QI_PER_DAMAGE_SUPREME;
    public static final ForgeConfigSpec.LongValue FORMATION_QI_PER_DAMAGE_IMMORTAL;
    public static final ForgeConfigSpec.DoubleValue FORMATION_BARRIER_DAMAGE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue FORMATION_REJUVENATION_AMP_LOW;
    public static final ForgeConfigSpec.IntValue FORMATION_REJUVENATION_AMP_MID;
    public static final ForgeConfigSpec.IntValue FORMATION_REJUVENATION_AMP_HIGH;
    public static final ForgeConfigSpec.IntValue FORMATION_REJUVENATION_AMP_SUPREME;
    public static final ForgeConfigSpec.IntValue FORMATION_REJUVENATION_AMP_IMMORTAL;
    public static final ForgeConfigSpec.IntValue FORMATION_HARVEST_INTERVAL_LOW;
    public static final ForgeConfigSpec.IntValue FORMATION_HARVEST_INTERVAL_MID;
    public static final ForgeConfigSpec.IntValue FORMATION_HARVEST_INTERVAL_HIGH;
    public static final ForgeConfigSpec.IntValue FORMATION_HARVEST_INTERVAL_SUPREME;
    public static final ForgeConfigSpec.IntValue FORMATION_HARVEST_BATCH_SIZE_IMMORTAL;

    // ══════════════════════════════════════════════════════════════════
    //  SECTS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue SECT_MAX_POWER_SCORE;
    public static final ForgeConfigSpec.DoubleValue SECT_SETTLEMENT_CELL_SPAWN_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_0;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_1;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_2;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_3;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_4;
    public static final ForgeConfigSpec.DoubleValue SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MAX_SCENES;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MAX_SPECTATORS;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MIN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MAX_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_NPC_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SELL_PERCENT;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_TECHNIQUE_PRICE_LOW;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_TECHNIQUE_PRICE_MID;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_TECHNIQUE_PRICE_HIGH;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_TECHNIQUE_PRICE_SUPREME;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_TECHNIQUE_PRICE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SPELL_PRICE_LOW;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SPELL_PRICE_MID;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SPELL_PRICE_HIGH;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SPELL_PRICE_SUPREME;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_SPELL_PRICE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_WEAPON_PRICE_LOW;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_WEAPON_PRICE_MID;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_WEAPON_PRICE_HIGH;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_WEAPON_PRICE_SUPREME;
    public static final ForgeConfigSpec.IntValue SECT_SHOP_WEAPON_PRICE_IMMORTAL;
    public static final ForgeConfigSpec.IntValue SECT_TASK_MAX_REQUIRED_COUNT;
    public static final ForgeConfigSpec.IntValue SECT_TASK_MAX_SYSTEM_PURCHASES;
    public static final ForgeConfigSpec.DoubleValue SECT_TASK_EXPEDITION_MIN_DAYS;
    public static final ForgeConfigSpec.DoubleValue SECT_TASK_EXPEDITION_MAX_DAYS;

    // ── Sect Size Tiers (13 tiers: 0=humble to 12=grand) ──
    // Spawn chance for each tier. Higher = more likely to appear.
    // Think of it like a lottery: a sect with spawn chance 10 is twice as likely
    // to appear as one with spawn chance 5. All chances are relative to each other.
    // The original mod only has 3 tiers (0=small, 1=medium, 2=large).
    // We extend to 13 tiers. Tiers 3+ use scale 2 building logic but with
    // multiplied building counts via the building multiplier below.
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_0;  // Humble Cottage
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_1;  // Minor Gathering
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_2;  // Small Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_3;  // Modest Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_4;  // Established Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_5;  // Prominent Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_6;  // Renowned Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_7;  // Great Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_8;  // Ancient Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_9;  // Supreme Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_10; // Legendary Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_11; // Mythic Sect
    public static final ForgeConfigSpec.DoubleValue SECT_SIZE_SPAWN_CHANCE_12; // Grand Immortal Sect
    // Multiplier for building counts above tier 2. Each tier above 2 multiplies
    // the random building count bound by this value. E.g. 0.5 means tier 5 gets
    // 1.5x bounds, tier 12 gets 6.0x bounds.
    public static final ForgeConfigSpec.DoubleValue SECT_BUILDING_COUNT_MULTIPLIER;
    // When true, cancels the original mod's DeferredSectNpcSpawner.onServerTick
    // to prevent ConcurrentModificationException crashes caused by chunk
    // force-loading during the server tick.
    public static final ForgeConfigSpec.BooleanValue SECT_SAFE_TICK;
    // Crouch-meditation settings
    public static final ForgeConfigSpec.BooleanValue ENABLE_CROUCH_MEDITATION;
    public static final ForgeConfigSpec.DoubleValue CROUCH_MEDITATION_MULT;
    public static final ForgeConfigSpec.DoubleValue CROUCH_MEDITATION_QI_MULT;
    // Minimum distance in blocks between sect centers, scaled by tier.
    // Two sects of tier T1 and T2 must be at least (T1 + T2 + 2) * this value
    // blocks apart. Default 200 means two tier-0 sects need 400 blocks, two
    // tier-12 sects need 5200 blocks.
    public static final ForgeConfigSpec.IntValue SECT_MIN_SPACING_PER_TIER;
    // NPC spawn multiplier for wandering cultivators in the world
    public static final ForgeConfigSpec.DoubleValue WANDERING_CULTIVATOR_SPAWN_MULTIPLIER;

    // ══════════════════════════════════════════════════════════════════
    //  LOOT
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue LOOT_RUINED_VANILLA_ROLLS_MIN;
    public static final ForgeConfigSpec.IntValue LOOT_RUINED_VANILLA_ROLLS_MAX;
    public static final ForgeConfigSpec.IntValue LOOT_COMPLETE_VANILLA_ROLLS_MIN;
    public static final ForgeConfigSpec.IntValue LOOT_COMPLETE_VANILLA_ROLLS_MAX;
    public static final ForgeConfigSpec.IntValue LOOT_RUINED_CULTIVATION_ROLLS_MAX;
    public static final ForgeConfigSpec.IntValue LOOT_COMPLETE_CULTIVATION_ROLLS_MIN;
    public static final ForgeConfigSpec.IntValue LOOT_COMPLETE_CULTIVATION_ROLLS_MAX;
    public static final ForgeConfigSpec.IntValue LOOT_LOW_SPIRIT_STONE_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_MID_SPIRIT_STONE_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_HIGH_SPIRIT_STONE_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_SUPREME_SPIRIT_STONE_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_HERB_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_TECHNIQUE_FRAGMENT_WEIGHT;
    public static final ForgeConfigSpec.IntValue LOOT_ZHUJI_DAN_WEIGHT_RUINED;
    public static final ForgeConfigSpec.IntValue LOOT_ZHUJI_DAN_WEIGHT_COMPLETE;

    // ══════════════════════════════════════════════════════════════════
    //  TRIALS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue TRIAL_HEART_DEMON_GREAT_RIGHTEOUS_VITALITY_MULT;
    public static final ForgeConfigSpec.DoubleValue TRIAL_HEART_DEMON_RIGHTEOUS_VITALITY_MULT;
    public static final ForgeConfigSpec.DoubleValue TRIAL_HEART_DEMON_NEUTRAL_VITALITY_MULT;
    public static final ForgeConfigSpec.DoubleValue TRIAL_HEART_DEMON_EVIL_VITALITY_MULT;
    public static final ForgeConfigSpec.DoubleValue TRIAL_HEART_DEMON_GREAT_EVIL_VITALITY_MULT;
    public static final ForgeConfigSpec.IntValue TRIAL_INNER_WORLD_PLATFORM_DIAMETER;
    public static final ForgeConfigSpec.IntValue TRIAL_INNER_WORLD_PLATFORM_Y;
    public static final ForgeConfigSpec.IntValue TRIAL_INNER_WORLD_SOUL_WOUND_TICKS;
    public static final ForgeConfigSpec.DoubleValue TRIAL_INNER_WORLD_FAILURE_HEALTH_PENALTY_PERCENT;
    public static final ForgeConfigSpec.IntValue TRIAL_INNER_WORLD_TIME_STASIS_DURATION;

    // ══════════════════════════════════════════════════════════════════
    //  QI SYSTEM
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue QI_SYSTEM_ATTRACTION_RADIUS;
    public static final ForgeConfigSpec.DoubleValue QI_SYSTEM_MEDITATION_RANGE_BONUS;
    public static final ForgeConfigSpec.DoubleValue QI_SYSTEM_MEDITATION_EFFICIENCY_BONUS;
    public static final ForgeConfigSpec.LongValue QI_SHIELD_QI_PER_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue QI_SHIELD_PERFECT_REDUCTION;
    public static final ForgeConfigSpec.LongValue QI_STONE_ORE_MAX_QI_LOW;
    public static final ForgeConfigSpec.LongValue QI_STONE_ORE_MAX_QI_MID;
    public static final ForgeConfigSpec.LongValue QI_STONE_ORE_MAX_QI_HIGH;
    public static final ForgeConfigSpec.LongValue QI_STONE_ORE_MAX_QI_SUPREME;
    public static final ForgeConfigSpec.LongValue QI_STONE_ORE_MAX_QI_SPIRIT_VEIN_SPRING;
    public static final ForgeConfigSpec.DoubleValue QI_STONE_ORE_REGEN_LOW;
    public static final ForgeConfigSpec.DoubleValue QI_STONE_ORE_REGEN_MID;
    public static final ForgeConfigSpec.DoubleValue QI_STONE_ORE_REGEN_HIGH;
    public static final ForgeConfigSpec.DoubleValue QI_STONE_ORE_REGEN_SUPREME;
    public static final ForgeConfigSpec.DoubleValue QI_STONE_ORE_REGEN_SPRING;

    // ══════════════════════════════════════════════════════════════════
    //  PASSIVE SPELLS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue PASSIVE_SLOW_REGEN_INTERVAL;
    public static final ForgeConfigSpec.LongValue PASSIVE_SLOW_REGEN_QI_COST;
    public static final ForgeConfigSpec.IntValue PASSIVE_BIGU_INTERVAL;
    public static final ForgeConfigSpec.LongValue PASSIVE_BIGU_QI_COST;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_BIGU_SATURATION;
    public static final ForgeConfigSpec.IntValue PASSIVE_QI_MENDING_INTERVAL;
    public static final ForgeConfigSpec.LongValue PASSIVE_QI_MENDING_QI_PER_DURABILITY;
    public static final ForgeConfigSpec.IntValue PASSIVE_QI_FLIGHT_DRAIN_INTERVAL;
    public static final ForgeConfigSpec.LongValue PASSIVE_QI_FLIGHT_DRAIN_PER_SECOND;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_QI_FLIGHT_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_ITEM_ATTRACTION_RADIUS;
    public static final ForgeConfigSpec.LongValue PASSIVE_ITEM_ATTRACTION_QI_PER_ITEM;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_TREASURE_SEIZING_RADIUS;
    public static final ForgeConfigSpec.LongValue PASSIVE_TREASURE_SEIZING_QI_PER_STACK;
    public static final ForgeConfigSpec.IntValue PASSIVE_TREASURE_SEIZING_STACKS_PER_SECOND;

    // ══════════════════════════════════════════════════════════════════
    //  EFFECTS
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.DoubleValue EFFECT_BLOOD_BERSERK_ATTACK_SPEED_MULT;
    public static final ForgeConfigSpec.DoubleValue EFFECT_BLOOD_BERSERK_MOVE_SPEED_MULT;
    public static final ForgeConfigSpec.DoubleValue EFFECT_DAO_HEART_WOUND_ATTACK_PENALTY;
    public static final ForgeConfigSpec.DoubleValue EFFECT_DAO_HEART_WOUND_MOVE_SPEED_PENALTY;
    public static final ForgeConfigSpec.DoubleValue EFFECT_SHATTER_ARMOR_ARMOR_PENALTY;
    public static final ForgeConfigSpec.DoubleValue EFFECT_SHATTER_ARMOR_TOUGHNESS_PENALTY;
    public static final ForgeConfigSpec.IntValue EFFECT_INVERSE_MARK_DURATION_TICKS;
    public static final ForgeConfigSpec.DoubleValue EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT;
    public static final ForgeConfigSpec.DoubleValue EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT;
    public static final ForgeConfigSpec.DoubleValue EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER;
    public static final ForgeConfigSpec.DoubleValue EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER;

    // ══════════════════════════════════════════════════════════════════
    //  MORALITY
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue MORALITY_NEUTRAL_MIN;
    public static final ForgeConfigSpec.IntValue MORALITY_NEUTRAL_MAX;
    public static final ForgeConfigSpec.DoubleValue MORALITY_TRIBULATION_DAMAGE_COEFFICIENT;
    public static final ForgeConfigSpec.DoubleValue MORALITY_TRIBULATION_DAMAGE_MAX;
    public static final ForgeConfigSpec.IntValue MORALITY_RIGHTEOUS_MIN;
    public static final ForgeConfigSpec.IntValue MORALITY_EVIL_MAX;
    public static final ForgeConfigSpec.IntValue MORALITY_GREAT_RIGHTEOUS_MIN;
    public static final ForgeConfigSpec.IntValue MORALITY_GREAT_EVIL_MAX;

    // ══════════════════════════════════════════════════════════════════
    //  LIFESPAN HELPER
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue LIFESPAN_START_BONE_AGE_MIN;
    public static final ForgeConfigSpec.IntValue LIFESPAN_START_BONE_AGE_MAX;
    public static final ForgeConfigSpec.DoubleValue LIFESPAN_AGE_PER_DAY;
    public static final ForgeConfigSpec.DoubleValue LIFESPAN_AGE_PER_DAY_MEDITATING;
    public static final ForgeConfigSpec.IntValue LIFESPAN_NEAR_IMMORTAL_THRESHOLD;
    public static final ForgeConfigSpec.IntValue LIFESPAN_ORDINARY_DEATH_PENALTY_YEARS;

    // ═══ NEW: Sect Life & Population (SectSavedData) ═══
    public static final ForgeConfigSpec.IntValue SECT_LIFE_TICK_INTERVAL;
    public static final ForgeConfigSpec.IntValue SECT_FULL_SIMULATION_PLAYER_RADIUS;
    public static final ForgeConfigSpec.IntValue SECT_DISTANT_CATCH_UP_DAY_CAP;
    public static final ForgeConfigSpec.IntValue SECT_WAREHOUSE_SLOT_LIMIT;
    public static final ForgeConfigSpec.IntValue SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT;
    public static final ForgeConfigSpec.IntValue SECT_EVENT_LIMIT;
    public static final ForgeConfigSpec.IntValue SECT_PERFORMANCE_QUEUE_LIMIT;
    public static final ForgeConfigSpec.IntValue SECT_PERFORMANCE_TIMEOUT_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_PERFORMANCE_PLAYER_RADIUS;
    public static final ForgeConfigSpec.DoubleValue SECT_POPULATION_FLOOR_INITIAL_SCALE;
    public static final ForgeConfigSpec.IntValue SECT_ELDER_DISCIPLE_TARGET;
    public static final ForgeConfigSpec.DoubleValue SECT_LOW_POPULATION_RECRUIT_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SECT_ELDER_RECRUIT_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SECT_SERVANT_APPRENTICESHIP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SECT_RECRUIT_DISCIPLE_CHANCE;
    public static final ForgeConfigSpec.IntValue SECT_DISCIPLE_REALM_GATE;
    public static final ForgeConfigSpec.IntValue SECT_DISCIPLE_SUB_STAGE_GATE;

    // ═══ NEW: Sect Journey (SectSavedData) ═══
    public static final ForgeConfigSpec.IntValue SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_CHUNK_RADIUS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_STUCK_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_RETURN_FALLBACK_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_ENTITY_MISSING_GRACE_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_DATA_PHASE_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_JOURNEY_QUEUE_FALLBACK_TICKS;

    // ═══ NEW: Sect Defense (SectSavedData) ═══
    public static final ForgeConfigSpec.DoubleValue SECT_DEFENSE_ESCAPE_RADIUS;
    public static final ForgeConfigSpec.DoubleValue SECT_DEFENSE_CRITICAL_HEALTH_RATIO;
    public static final ForgeConfigSpec.IntValue SECT_DEFENSE_CRITICAL_RESPONDER_LIMIT;
    public static final ForgeConfigSpec.IntValue SECT_DEFENSE_DEATH_RESPONDER_LIMIT;

    // ═══ NEW: Sect Department Shifts (SectSavedData) ═══
    public static final ForgeConfigSpec.IntValue SECT_DEPARTMENT_FIRST_SHIFT_START;
    public static final ForgeConfigSpec.IntValue SECT_DEPARTMENT_FIRST_SHIFT_END;
    public static final ForgeConfigSpec.IntValue SECT_DEPARTMENT_SECOND_SHIFT_START;
    public static final ForgeConfigSpec.IntValue SECT_DEPARTMENT_SECOND_SHIFT_END;

    // ═══ NEW: Sect Task Market (SectTaskMarketRules) — additional fields ═══
    public static final ForgeConfigSpec.IntValue SECT_TASK_MAX_ESCROW_STACKS;
    public static final ForgeConfigSpec.IntValue SECT_TASK_MAX_SYSTEM_PURCHASE_TASKS;
    public static final ForgeConfigSpec.IntValue SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_TASK_JOURNEY_MAX_TIMEOUT_TICKS;

    // ═══ NEW: Sect Departments (SectDepartmentType) ═══
    public static final ForgeConfigSpec.IntValue SECT_DEPT_WORK_CREDIT_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue SECT_DEPT_ROLE_ASSIGNMENT_RATIO;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ENFORCEMENT_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ENFORCEMENT_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ENFORCEMENT_INPUT_BUFFER_TARGET;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ALCHEMY_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ALCHEMY_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_ALCHEMY_INPUT_BUFFER_TARGET;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_REFINING_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_REFINING_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_REFINING_INPUT_BUFFER_TARGET;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_HERBAL_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_HERBAL_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_HERBAL_INPUT_BUFFER_TARGET;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_MINING_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_MINING_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_MINING_INPUT_BUFFER_TARGET;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_TREASURY_WORK_POINTS_PER_OUTPUT;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_TREASURY_DAILY_OUTPUT_CAP;
    public static final ForgeConfigSpec.IntValue SECT_DEPT_TREASURY_INPUT_BUFFER_TARGET;

    // ═══ NEW: Sect Ambient Interactions (SectAmbientInteractionRules) — additional ═══
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_CHECK_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MAX_ACTIVE_SCENES_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MIN_SECT_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_AMBIENT_MAX_SECT_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue SECT_AMBIENT_PAIR_SEARCH_DISTANCE;

    // ═══ NEW: Sect Overhead Panel (SectOverheadPanelRules) ═══
    public static final ForgeConfigSpec.DoubleValue SECT_OVERHEAD_PANEL_FADE_START_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue SECT_OVERHEAD_PANEL_RENDER_DISTANCE;
    public static final ForgeConfigSpec.IntValue SECT_OVERHEAD_PANEL_FADE_IN_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_OVERHEAD_PANEL_FADE_OUT_TICKS;

    // ═══ NEW: Sect NPC Bubbles (SectNpcOverheadBubbleRules) ═══
    public static final ForgeConfigSpec.IntValue SECT_BUBBLE_MIN_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_BUBBLE_MAX_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_BUBBLE_FADE_TICKS;
    public static final ForgeConfigSpec.DoubleValue SECT_BUBBLE_DISPLAY_DISTANCE;

    // ═══ NEW: Sect Daily Schedule (SectDailyScheduleRules) ═══
    public static final ForgeConfigSpec.IntValue SECT_SCHEDULE_DAY_TICKS;
    public static final ForgeConfigSpec.IntValue SECT_SCHEDULE_MORNING_END_EXCLUSIVE;
    public static final ForgeConfigSpec.IntValue SECT_SCHEDULE_NIGHT_START;

    // ═══ NEW: Sect Generated Cultivation Profile (GeneratedSectCultivationProfile) ═══
    public static final ForgeConfigSpec.DoubleValue SECT_PROFILE_MIN_CULTIVATOR_PROGRESS;
    public static final ForgeConfigSpec.DoubleValue SECT_PROFILE_MAX_MORTAL_REALM_PROGRESS;
    public static final ForgeConfigSpec.IntValue SECT_PROFILE_MAX_POWER_SCORE;
    public static final ForgeConfigSpec.IntValue SECT_PROFILE_MIN_ORDINARY_ROLE_SPAN;
    public static final ForgeConfigSpec.IntValue SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN;
    public static final ForgeConfigSpec.DoubleValue SECT_PROFILE_MIN_MASTER_PROGRESS;

    // ═══ NEW: NPC AI Combat (CultivatorSpellAttackGoal) ═══
    public static final ForgeConfigSpec.DoubleValue NPC_AI_SPELL_MAX_RANGE;
    public static final ForgeConfigSpec.IntValue NPC_AI_HIGH_IMPACT_MEGA_COOLDOWN_TICKS;

    // ═══ NEW: NPC AI Flight Combat (CultivatorFlightCombatGoal) ═══
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_RANGED_MIN_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_RANGED_MAX_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_MELEE_DISTANCE;
    public static final ForgeConfigSpec.IntValue NPC_AI_FLIGHT_MIN_SEGMENT_TICKS;
    public static final ForgeConfigSpec.IntValue NPC_AI_FLIGHT_MAX_SEGMENT_TICKS;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_MIN_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_MAX_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_MAX_HORIZONTAL_SPEED;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_FLIGHT_MAX_VERTICAL_SPEED;

    // ═══ NEW: NPC AI Ranged Kiting (CultivatorRangedKitingGoal) ═══
    public static final ForgeConfigSpec.DoubleValue NPC_AI_KITING_MIN_RANGE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_KITING_PREFERRED_RANGE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_KITING_MAX_RANGE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_KITING_GIVE_UP_RANGE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_KITING_GROUND_WALK_SPEED_MODIFIER;

    // ═══ NEW: NPC AI Emergency Retreat (CultivatorEmergencyRetreatGoal) ═══
    public static final ForgeConfigSpec.DoubleValue NPC_AI_RETREAT_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_RETREAT_RANDOM_SPREAD;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_RETREAT_GROUND_SPEED;
    public static final ForgeConfigSpec.DoubleValue NPC_AI_RETREAT_AIR_SPEED;

    // ═══ NEW: NPC Combat Tactics (NpcCombatTactics) ═══
    public static final ForgeConfigSpec.DoubleValue NPC_AI_SURVIVAL_RESERVE_FRACTION;
    public static final ForgeConfigSpec.IntValue NPC_AI_DECISION_INTERVAL_TICKS;

    // ═══ NEW: NPC Trades (CultivatorTrades) ═══
    public static final ForgeConfigSpec.BooleanValue NPC_TRADES_INFINITE_USES;
    public static final ForgeConfigSpec.DoubleValue NPC_TRADES_PRICE_MULT;

    // ═══ NEW: Formation Core (FormationCorePlateBlockEntity) ═══
    public static final ForgeConfigSpec.IntValue FORMATION_EFFECT_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_GENERATED_ARRAY_SYNC_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_RELOAD_FLAG_VALIDATION_GRACE_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_EFFECT_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_GROWTH_TICK_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_STORAGE_CORE_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue FORMATION_FARM_HARVEST_MIN_CHECKS;
    public static final ForgeConfigSpec.IntValue FORMATION_FARM_HARVEST_CHECKS_PER_TARGET;
    public static final ForgeConfigSpec.IntValue FORMATION_FARM_HARVEST_MAX_CHECKS;
    public static final ForgeConfigSpec.IntValue FORMATION_MIN_FLAG_EFFECT_RADIUS;
    public static final ForgeConfigSpec.IntValue FORMATION_DEFAULT_FLAG_EFFECT_RADIUS;
    public static final ForgeConfigSpec.IntValue FORMATION_MAX_FLAG_EFFECT_RADIUS;

    // ═══ NEW: Loose Immortal (LooseImmortalBonusHelper) ═══
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_MAX_TRIBULATIONS;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_INTERVAL_YEARS;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_WARNING_TICKS;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_WAVES_PER_TRIBULATION;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_BOLTS_PER_WAVE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_STRIKE_DAMAGE;
    // Per-level bonuses (index 0-3 for 4 loose immortal levels)
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_BODY_DEFENSE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_BODY_DEFENSE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_BODY_DEFENSE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_BODY_DEFENSE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_CULTIVATION_EFFICIENCY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_CULTIVATION_EFFICIENCY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_CULTIVATION_EFFICIENCY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_CULTIVATION_EFFICIENCY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_QI_RECOVERY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_QI_RECOVERY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_QI_RECOVERY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_QI_RECOVERY;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_MELEE_DAMAGE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_MELEE_DAMAGE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_MELEE_DAMAGE;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_MELEE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L0_SPELL_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L1_SPELL_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L2_SPELL_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L3_SPELL_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L0_SPELL_COST;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L1_SPELL_COST;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L2_SPELL_COST;
    public static final ForgeConfigSpec.DoubleValue LOOSE_IMMORTAL_L3_SPELL_COST;
    public static final ForgeConfigSpec.LongValue LOOSE_IMMORTAL_L0_MAX_QI;
    public static final ForgeConfigSpec.LongValue LOOSE_IMMORTAL_L1_MAX_QI;
    public static final ForgeConfigSpec.LongValue LOOSE_IMMORTAL_L2_MAX_QI;
    public static final ForgeConfigSpec.LongValue LOOSE_IMMORTAL_L3_MAX_QI;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_FREE_ZHENYUAN;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_FREE_ZHENYUAN;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_FREE_ZHENYUAN;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_FREE_ZHENYUAN;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L0_AUTO_ZHENYUAN_ATTR;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L1_AUTO_ZHENYUAN_ATTR;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L2_AUTO_ZHENYUAN_ATTR;
    public static final ForgeConfigSpec.IntValue LOOSE_IMMORTAL_L3_AUTO_ZHENYUAN_ATTR;

    // ═══ NEW: Cultivation Data (CultivationData) ═══
    public static final ForgeConfigSpec.IntValue CULTIVATION_MAX_TIME_ACCELERATION_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue CULTIVATION_TRIBULATION_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue CULTIVATION_TRIBULATION_CHARGE_TICKS;
    public static final ForgeConfigSpec.IntValue CULTIVATION_EQUIPPED_SLOT_COUNT;
    public static final ForgeConfigSpec.IntValue CULTIVATION_ZHENYUAN_ATTR_REWARD_MINOR;
    public static final ForgeConfigSpec.IntValue CULTIVATION_ZHENYUAN_ATTR_REWARD_MAJOR;

    // ═══ NEW: Cultivation Progression Rules (CultivationProgressionRules) — additional ═══
    public static final ForgeConfigSpec.DoubleValue PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT;
    public static final ForgeConfigSpec.DoubleValue PROGRESSION_NPC_HIGHER_DAO_LIFESPAN_RESERVE_THRESHOLD;
    public static final ForgeConfigSpec.IntValue PROGRESSION_NPC_TRIBULATION_FAILURE_WEAKNESS_DAYS;

    // ═══ NEW: Identity Draw (IdentityDrawDeck) ═══
    public static final ForgeConfigSpec.IntValue IDENTITY_DRAW_DECK_SIZE;
    public static final ForgeConfigSpec.IntValue IDENTITY_DRAW_MAX_ROUNDS;

    // ═══ NEW: Golden Finger Perks ═══
    public static final ForgeConfigSpec.IntValue GOLDEN_FINGER_PERK_COUNT;

    // ═══ NEW: Morality (MoralityHelper) ═══
    public static final ForgeConfigSpec.IntValue MORALITY_MIN_VALUE;
    public static final ForgeConfigSpec.IntValue MORALITY_MAX_VALUE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // ════════════════════════════════════════════════════════════════
        //  GENERAL
        // ════════════════════════════════════════════════════════════════
        builder.comment("Xiaoxiang Config Extension - Expanded configurables for the Cultivation World mod")
               .push("general");

        ENABLE_REALM_OVERRIDES = builder.comment("Override realm power/strength values.").define("enableRealmOverrides", true);
        ENABLE_BEAST_OVERRIDES = builder.comment("Override beast cultivation values.").define("enableBeastOverrides", true);
        ENABLE_SPAWN_OVERRIDES = builder.comment("Override NPC spawn chances and realm weights.").define("enableSpawnOverrides", true);
        ENABLE_QI_OVERRIDES = builder.comment("Override biome qi density values.").define("enableQiOverrides", true);
        ENABLE_SPELL_OVERRIDES = builder.comment("Override spell damage, qi cost, and charge values.").define("enableSpellOverrides", true);
        ENABLE_WEAPON_OVERRIDES = builder.comment("Override weapon damage and special effect values.").define("enableWeaponOverrides", true);
        ENABLE_PILL_OVERRIDES = builder.comment("Override pill qi recovery and effect values (pills section below), " +
                "and also storage bag column/row counts (storageBag subsection) - both share this one toggle.")
                .define("enablePillOverrides", true);
        ENABLE_ALCHEMY_OVERRIDES = builder.comment("Override alchemy furnace and rank values.").define("enableAlchemyOverrides", true);
        ENABLE_REFINING_OVERRIDES = builder.comment("Override refining furnace and rank values.").define("enableRefiningOverrides", true);
        ENABLE_SPIRIT_PLANT_OVERRIDES = builder.comment("Override spirit plant growth and effect values.").define("enableSpiritPlantOverrides", true);
        ENABLE_SPIRIT_VEIN_OVERRIDES = builder.comment("Override spirit vein core tier values.").define("enableSpiritVeinOverrides", true);
        ENABLE_TECHNIQUE_OVERRIDES = builder.comment("Override technique bonus values.").define("enableTechniqueOverrides", true);
        ENABLE_SPIRIT_ROOT_OVERRIDES = builder.comment("Override spirit root bonus values.").define("enableSpiritRootOverrides", true);
        ENABLE_PHYSIQUE_OVERRIDES = builder.comment("Override physique bonus values.").define("enablePhysiqueOverrides", true);
        ENABLE_DAO_OVERRIDES = builder.comment("Override Foundation/Golden Core Dao values.").define("enableDaoOverrides", true);
        ENABLE_IDENTITY_OVERRIDES = builder.comment("Override identity starting lifespan values.").define("enableIdentityOverrides", true);
        ENABLE_PROGRESSION_OVERRIDES = builder.comment("Override cultivation progression requirements.").define("enableProgressionOverrides", true);
        ENABLE_NPC_COMBAT_OVERRIDES = builder.comment("Override NPC combat tactics and dodge profiles.").define("enableNpcCombatOverrides", true);
        ENABLE_FORMATION_OVERRIDES = builder.comment("Override formation power and effect values.").define("enableFormationOverrides", true);
        ENABLE_SECT_OVERRIDES = builder.comment("Override sect generation and shop pricing values.").define("enableSectOverrides", true);
        ENABLE_LOOT_OVERRIDES = builder.comment("Override chest loot drop weights and counts.").define("enableLootOverrides", true);
        ENABLE_TRIAL_OVERRIDES = builder.comment("Override trial and tribulation parameters.").define("enableTrialOverrides", true);
        ENABLE_QI_SYSTEM_OVERRIDES = builder.comment("Override block qi specs and player qi consumer values.").define("enableQiSystemOverrides", true);
        ENABLE_PASSIVE_SPELL_OVERRIDES = builder.comment("Override passive spell intervals and costs.").define("enablePassiveSpellOverrides", true);
        ENABLE_EFFECT_OVERRIDES = builder.comment("Override status effect values.").define("enableEffectOverrides", true);
        ENABLE_MORALITY_OVERRIDES = builder.comment("Override morality thresholds and tribulation scaling.").define("enableMoralityOverrides", true);
        ENABLE_LIFESPAN_OVERRIDES = builder.comment("Override lifespan helper constants.").define("enableLifespanOverrides", true);

        PLAYER_CULTIVATION_SPEED_MULT = builder.comment("Player cultivation speed multiplier. " +
                "Affects how fast the player absorbs Qi and how much Qi is needed for breakthroughs. " +
                "2.0 = twice as fast cultivation, 0.5 = half speed. " +
                "Manual config for player cultivation speed.").defineInRange("playerCultivationSpeedMult", 1.0, 0.01, 100.0);
        NPC_HP_MULTIPLIER = builder.comment("Multiplier for NPC max HP. 4.0 = NPCs have 4x normal HP. " +
                "Manual config for NPC toughness.").defineInRange("npcHpMultiplier", 1.0, 0.01, 100.0);
        NPC_ATTACK_MULTIPLIER = builder.comment("Multiplier for NPC melee attack damage. 4.0 = NPCs deal 4x melee damage. " +
                "Manual config for NPC attack power.").defineInRange("npcAttackMultiplier", 1.0, 0.01, 100.0);
        NPC_DEFENSE_MULTIPLIER = builder.comment("Multiplier for NPC defense. 4.0 = NPCs take 1/4 damage. " +
                "Manual config for NPC defense.").defineInRange("npcDefenseMultiplier", 1.0, 0.01, 100.0);
        MEDITATION_TICK_INTERVAL = builder.comment("How often (in ticks) meditation progress updates. " +
                "Default 20 (once per second). Lower = more frequent updates = smoother progress bar. " +
                "1 = every tick (20x smoother).").defineInRange("meditationTickInterval", 20, 1, 100);
        ZHENYUAN_REWARD_AMOUNT = builder.comment("Stat points (zhenyuan) awarded per major realm breakthrough. " +
                "Default 5 (same as original mod). Increase for faster character growth.").defineInRange("zhenyuanRewardAmount", 5, 1, 100);
        ZHENYUAN_MINOR_REWARD_AMOUNT = builder.comment("Stat points awarded per minor sub-stage advancement. " +
                "Default 1 (same as original mod).").defineInRange("zhenyuanMinorRewardAmount", 1, 0, 100);

        builder.pop();

        // ════════════════════════════════════════════════════════════════
        //  REALMS
        // ════════════════════════════════════════════════════════════════
        builder.comment("Player cultivation realm power levels. Adjust max qi, lifespan, shield reduction, tribulation damage.")
               .push("realms");

        builder.push("maxQi");
        // CORRECTED 2026-09-01: the values below (dated 2026-08-23, "Qi Refining substage
        // values were previously off by one tier (100 too high each)") were themselves
        // wrong - re-verified this session directly against Realm.maxQi()'s real bytecode
        // (javap -p -c -s, not paraphrased) and confirmed the method computes
        // base(100) + delta(100/200/300/400 for Early/Middle/Late/Peak) = 200/300/400/500,
        // NOT the flat 100/200/300/400 the 2026-08-23 pass shipped. That earlier "fix" was
        // itself a regression: with those wrong defaults, Mortal(100) -> Qi Refining Early
        // uses the SAME value (100), a plateau at the very first breakthrough - a milder
        // version of the exact "requirement didn't go up" symptom this whole triage exists
        // to fix. Restored to the bytecode-verified real values: Mortal=100, Qi Refining
        // Early/Middle/Late/Peak=200/300/400/500, deltas(realms above Qi Refining)=
        // 1000/1100/1200/1300, Loose Immortal=18000, True Immortal=20900. The original also
        // uses a *separate* hardcoded 500 (not tied to Qi Refining's own peak of 500) as the
        // base of the "prevPeak + delta" formula for every realm above Qi Refining - see
        // REALM_PROGRESSION_BASE below, used by RealmMixin instead of qiRefiningPeak.
        MORTAL_MAX_QI = builder.comment("Max qi for Mortal realm.").defineInRange("mortal", 100, 1, 10000000);
        QI_REFINING_EARLY_MAX_QI = builder.comment("Max qi for Qi Refining (Early).").defineInRange("qiRefiningEarly", 200, 1, 10000000);
        QI_REFINING_MIDDLE_MAX_QI = builder.comment("Max qi for Qi Refining (Middle).").defineInRange("qiRefiningMiddle", 300, 1, 10000000);
        QI_REFINING_LATE_MAX_QI = builder.comment("Max qi for Qi Refining (Late).").defineInRange("qiRefiningLate", 400, 1, 10000000);
        QI_REFINING_PEAK_MAX_QI = builder.comment("Max qi for Qi Refining (Peak).").defineInRange("qiRefiningPeak", 500, 1, 10000000);
        REALM_PROGRESSION_BASE = builder.comment("Base qi value used to calculate realms above Qi Refining (prevPeak = this + (realmOrdinal-2)*1300). "
                + "This is a separate constant from Qi Refining's own peak value in the original mod - do not tie it to qiRefiningPeak.")
                .defineInRange("realmProgressionBase", 500, 1, 10000000);
        REALM_BASE_DELTA_EARLY = builder.comment("Qi delta for Early substage (realms above Qi Refining). "
                + "WARNING: each realm's prevPeak only increases by 1300 per realm above Qi Refining, so if deltaPeak "
                + "is raised above 1300 (or the deltas are set out of Early<Middle<Late<Peak order), a realm's Peak "
                + "requirement can end up HIGHER than the next realm's Early requirement, i.e. the cultivation "
                + "requirement will appear to go DOWN when a player breaks through into the next realm. Keep "
                + "deltaEarly < deltaMiddle < deltaLate < deltaPeak <= 1300 to avoid this.")
                .defineInRange("deltaEarly", 1000, 1, 10000000);
        REALM_BASE_DELTA_MIDDLE = builder.comment("Qi delta for Middle substage. See deltaEarly's comment - keep this < deltaLate.").defineInRange("deltaMiddle", 1100, 1, 10000000);
        REALM_BASE_DELTA_LATE = builder.comment("Qi delta for Late substage. See deltaEarly's comment - keep this < deltaPeak.").defineInRange("deltaLate", 1200, 1, 10000000);
        REALM_BASE_DELTA_PEAK = builder.comment("Qi delta for Peak substage. See deltaEarly's comment - keep this <= 1300 or the next realm's cultivation requirement can drop below this one's.").defineInRange("deltaPeak", 1300, 1, 10000000);
        TRUE_IMMORTAL_MAX_QI = builder.comment("Max qi for True Immortal.").defineInRange("trueImmortal", 20900, 1, 100000000);
        LOOSE_IMMORTAL_MAX_QI = builder.comment("Max qi for Loose Immortal.").defineInRange("looseImmortal", 18000, 1, 100000000);
        MAX_QI_GLOBAL_MULTIPLIER = builder.comment("Global multiplier applied to all max qi values after base calculation. 1.0 = no change.").defineInRange("globalMultiplier", 1.0, 0.01, 1000.0);
        builder.pop();

        builder.push("monotonicity");
        // Added 2026-09-01, following the player-reported "cultivation requirements
        // decreased after reincarnation" bug (see RealmMixin.java's configExt$maxQi
        // and the Bug #1 writeup in CONFIG_AUDIT.md). That specific bug is fixed, but
        // it was still possible for an admin's own maxQi/delta settings above (or,
        // verified via javap, the ORIGINAL unmodified base mod's own hardcoded values -
        // Realm.maxQi flat-returns True Immortal=20900 then Loose Immortal=18000, a
        // real built-in decrease at that one seam) to produce a non-increasing
        // breakthrough sequence. This section is a standing safety net: it walks the
        // full standard breakthrough chain (Mortal -> Qi Refining E/M/L/P -> Foundation
        // Building E/M/L/P -> ... -> Tribulation Transcendence E/M/L/P, the same order
        // CultivationProgressionRules.nextAfterSuccess actually advances through, per
        // real bytecode) and clamps every step up to at least the previous step's
        // (already-clamped) requirement plus a minimum increase, so no config value or
        // future oversight can ever cause a breakthrough requirement to go down again.
        ENABLE_MONOTONIC_BREAKTHROUGHS = builder.comment(
                "If true, every breakthrough requirement in the standard chain (Mortal through "
                        + "Tribulation Transcendence Peak) is guaranteed to be strictly higher than every "
                        + "previous one, regardless of what the maxQi/delta settings above (or the "
                        + "original mod's own hardcoded values, if realm overrides are disabled) compute. "
                        + "This runs independently of enableRealmOverrides - it is a safety net, not a "
                        + "replacement for it. True Immortal and Loose Immortal are each independently "
                        + "clamped to stay above Tribulation Transcendence Peak (verified via javap that "
                        + "these two are reached through a separate tribulation-count promotion mechanic, "
                        + "not the standard chain, and are not necessarily meant to be ordered relative to "
                        + "EACH OTHER - only above the standard track's ceiling)."
        ).define("enableMonotonicBreakthroughs", true);
        MONOTONIC_MIN_MINOR_INCREASE = builder.comment(
                "Minimum qi increase enforced for a minor (sub-stage, e.g. Early->Middle) breakthrough "
                        + "step when monotonicity clamping has to correct a value. Keep this smaller than "
                        + "majorIncrease so minor steps stay smaller jumps than realm-up steps."
        ).defineInRange("minMinorIncrease", 50, 1, 10000000);
        MONOTONIC_MIN_MAJOR_INCREASE = builder.comment(
                "Minimum qi increase enforced for a major (realm-up, e.g. Qi Refining Peak->Foundation "
                        + "Building Early) breakthrough step when monotonicity clamping has to correct a "
                        + "value. Also used to keep True Immortal / Loose Immortal above Tribulation "
                        + "Transcendence Peak. Keep this larger than minMinorIncrease."
        ).defineInRange("minMajorIncrease", 300, 1, 10000000);
        builder.pop();

        builder.push("lifespan");
        QI_REFINING_LIFESPAN = builder.defineInRange("qiRefining", 120, 0, 1000000);
        FOUNDATION_BUILDING_LIFESPAN = builder.defineInRange("foundationBuilding", 150, 0, 1000000);
        GOLDEN_CORE_LIFESPAN = builder.defineInRange("goldenCore", 300, 0, 1000000);
        NASCENT_SOUL_LIFESPAN = builder.defineInRange("nascentSoul", 600, 0, 1000000);
        SOUL_FORMATION_LIFESPAN = builder.defineInRange("soulFormation", 1000, 0, 1000000);
        VOID_REFINING_LIFESPAN = builder.defineInRange("voidRefining", 1500, 0, 1000000);
        BODY_INTEGRATION_LIFESPAN = builder.defineInRange("bodyIntegration", 2500, 0, 1000000);
        MAHAYANA_LIFESPAN = builder.defineInRange("mahayana", 4000, 0, 1000000);
        TRIBULATION_TRANSCENDENCE_LIFESPAN = builder.defineInRange("tribulationTranscendence", 6000, 0, 1000000);
        LOOSE_IMMORTAL_LIFESPAN = builder.defineInRange("looseImmortal", 10000, 0, 1000000);
        TRUE_IMMORTAL_LIFESPAN = builder.defineInRange("trueImmortal", 10000, 0, 1000000);
        LIFESPAN_GLOBAL_MULTIPLIER = builder.comment("Global multiplier applied to all lifespan values. 1.0 = no change. " +
                "Wired 2026-09-01 via LifespanHelperMixin, scaling the whole computed result of " +
                "LifespanHelper.lifespanCap(CultivationData) at RETURN (that method has no single literal to " +
                "target - it sums several dao/realm-dependent terms).").defineInRange("globalMultiplier", 1.0, 0.01, 1000.0);
        builder.pop();

        builder.push("qiShieldReductionPercent");
        QI_REFINING_SHIELD_PERCENT = builder.defineInRange("qiRefining", 30, 0, 100);
        FOUNDATION_BUILDING_SHIELD_PERCENT = builder.defineInRange("foundationBuilding", 40, 0, 100);
        GOLDEN_CORE_SHIELD_PERCENT = builder.defineInRange("goldenCore", 50, 0, 100);
        NASCENT_SOUL_SHIELD_PERCENT = builder.defineInRange("nascentSoul", 60, 0, 100);
        SOUL_FORMATION_SHIELD_PERCENT = builder.defineInRange("soulFormation", 70, 0, 100);
        VOID_REFINING_SHIELD_PERCENT = builder.defineInRange("voidRefining", 80, 0, 100);
        BODY_INTEGRATION_SHIELD_PERCENT = builder.defineInRange("bodyIntegration", 85, 0, 100);
        MAHAYANA_SHIELD_PERCENT = builder.defineInRange("mahayana", 90, 0, 100);
        TRIBULATION_TRANSCENDENCE_SHIELD_PERCENT = builder.defineInRange("tribulationTranscendence", 95, 0, 100);
        TRUE_IMMORTAL_SHIELD_PERCENT = builder.defineInRange("trueImmortal", 100, 0, 100);
        LOOSE_IMMORTAL_SHIELD_PERCENT = builder.defineInRange("looseImmortal", 95, 0, 100);
        builder.pop();

        builder.push("tribulationDamage");
        QI_REFINING_TRIB_DAMAGE = builder.defineInRange("qiRefining", 30, 0, 10000);
        FOUNDATION_BUILDING_TRIB_DAMAGE = builder.defineInRange("foundationBuilding", 40, 0, 10000);
        GOLDEN_CORE_TRIB_DAMAGE = builder.defineInRange("goldenCore", 50, 0, 10000);
        NASCENT_SOUL_TRIB_DAMAGE = builder.defineInRange("nascentSoul", 60, 0, 10000);
        SOUL_FORMATION_TRIB_DAMAGE = builder.defineInRange("soulFormation", 70, 0, 10000);
        VOID_REFINING_TRIB_DAMAGE = builder.defineInRange("voidRefining", 80, 0, 10000);
        BODY_INTEGRATION_TRIB_DAMAGE = builder.defineInRange("bodyIntegration", 90, 0, 10000);
        TRIBULATION_TRANSCENDENCE_TRIB_DAMAGE = builder.defineInRange("tribulationTranscendence", 150, 0, 10000);
        TRIB_DAMAGE_GLOBAL_MULTIPLIER = builder.comment("Global multiplier for all tribulation damage. 1.0 = no change.").defineInRange("globalMultiplier", 1.0, 0.0, 100.0);
        builder.pop();

        builder.push("looseImmortal");
        LOOSE_IMMORTAL_BASE_REDUCTION_PERCENT = builder.defineInRange("baseReductionPercent", 95, 0, 100);
        LOOSE_IMMORTAL_FULL_REDUCTION_TRIBULATIONS = builder.defineInRange("fullReductionTribulations", 6, 1, 100);
        builder.pop();

        builder.push("tribulationTiming");
        TRIBULATION_INTERVAL_TICKS = builder.comment("Ticks between tribulation strikes.").defineInRange("intervalTicks", 20, 1, 200);
        TRIBULATION_CHARGE_TICKS = builder.comment("Ticks to charge before tribulation begins.").defineInRange("chargeTicks", 60, 1, 600);
        TRIBULATION_BOLT_COOLDOWN_TICKS = builder.comment("Cooldown ticks after each tribulation wave.").defineInRange("boltCooldownTicks", 20, 1, 200);
        builder.pop();

        builder.push("zhenyuanRewards");
        ZHENYUAN_REWARD_MINOR = builder.comment("Free zhenyuan points per minor breakthrough.").defineInRange("minor", 1, 0, 1000);
        ZHENYUAN_REWARD_MAJOR = builder.comment("Free zhenyuan points per major realm breakthrough.").defineInRange("major", 5, 0, 1000);
        ZHENYUAN_ATTR_REWARD_MINOR = builder.comment("Auto zhenyuan attribute points per minor breakthrough.").defineInRange("attrMinor", 1, 0, 1000);
        ZHENYUAN_ATTR_REWARD_MAJOR = builder.comment("Auto zhenyuan attribute points per major breakthrough.").defineInRange("attrMajor", 5, 0, 1000);
        ZHENYUAN_STAT_MULTIPLIER = builder.comment("Multiplier for zhenyuan stat point effectiveness. 10 = each point gives 10x the bonus. 1 = vanilla.").defineInRange("statMultiplier", 10.0, 0.01, 1000.0);
        builder.pop();

        builder.push("timeAcceleration");
        TIME_ACCELERATION_MIN = builder.comment("Minimum time acceleration multiplier.").defineInRange("min", 2, 1, 100000);
        TIME_ACCELERATION_MAX = builder.comment("Maximum time acceleration multiplier.").defineInRange("max", 10000, 1, 100000);
        builder.pop();

        builder.pop(); // realms

        // ════════════════════════════════════════════════════════════════
        //  REINCARNATION
        // ════════════════════════════════════════════════════════════════
        builder.comment("Death/reincarnation flow fixes.")
               .push("reincarnation");

        // Player-reported 2026-09-01: after dying once, reincarnating, and choosing an
        // origin again, dying a SECOND time did nothing at all - no Difu/underworld
        // prompt, just a normal vanilla death. Root-caused via javap: the entire
        // Difu/reincarnation flow (SoulStateHandler.onLivingDeath) is gated behind
        // CultivationData.hasEquippedTechnique(), and ReincarnationManager.doReincarnate
        // wipes cultivation state (copyFrom(new CultivationData())) including
        // equippedTechniqueId, WITHOUT ever restoring it - unlike its sibling
        // doReturnIntact, which does call TechniqueLoadoutHelper.normalizeForCurrentState
        // to restore a technique loadout after its own state reset. So after a
        // reincarnation, equippedTechniqueId stays blank until the player manually
        // equips a technique, and hasEquippedTechnique() silently blocks Difu on every
        // death until then. See ReincarnationManagerMixin.java and the "Bug #4" writeup
        // in CONFIG_AUDIT.md for the full bytecode citations.
        ENABLE_REINCARNATION_TECHNIQUE_FIX = builder.comment(
                "If true, mirrors doReturnIntact's own technique-loadout normalization at the "
                        + "end of doReincarnate, so a player who has reincarnated still has an "
                        + "equipped technique (matching their new state) and the Difu/reincarnation "
                        + "flow correctly triggers again on every subsequent death, not just the "
                        + "first. This is a bug fix for base-mod behavior verified via javap, not a "
                        + "balance choice - leave this on unless you have a specific reason not to."
        ).define("enableReincarnationTechniqueFix", true);

        builder.pop(); // reincarnation

        // ════════════════════════════════════════════════════════════════
        //  IDENTITY SCREEN TOOLTIPS
        // ════════════════════════════════════════════════════════════════
        builder.comment("Identity Draw screen tooltip overflow fixes.")
               .push("identityScreenTooltips");

        // Player-reported 2026-09-01, alongside the starter-items grid overflow (see
        // IdentityDrawScreenMixin's renderStarterItems doc): hovering a starter item or the
        // physique icon shows a normal Minecraft item/description tooltip
        // (GuiGraphics.renderTooltip, verified via javap - the Identity screen's own
        // hoveredStarter and hoveredPhysique fields both feed directly into it) that can grow
        // tall enough to overlap the rest of the screen when an item/physique has a long
        // description. Rather than truncating, this makes long tooltips scrollable with the
        // mouse wheel - shows the first N lines plus a "scroll for more" hint, and the wheel
        // shifts which slice of lines is shown, re-using the exact same vanilla tooltip-box
        // rendering call each time (just with a shorter list), so no custom clipping/scissor
        // code is needed. See IdentityDrawScreenMixin.java's doc comment on this feature for
        // the honest caveat: the vanilla tooltip-line-fetching API this relies on could not be
        // verified against a real Minecraft jar in the environment this was built in (only the
        // mod's own compiled classes were available to verify against, not vanilla
        // Minecraft/Forge) - flagged clearly rather than claimed as bytecode-verified.
        ENABLE_SCROLLABLE_ITEM_TOOLTIPS = builder.comment(
                "If true, starter-item and physique tooltips on the Identity Draw screen that "
                        + "exceed maxVisibleLines become mouse-wheel scrollable instead of growing "
                        + "tall enough to overlap the rest of the screen. Short tooltips are "
                        + "completely unaffected - this only changes behavior once a tooltip "
                        + "actually overflows."
        ).define("enableScrollableItemTooltips", true);
        SCROLLABLE_TOOLTIP_MAX_LINES = builder.comment(
                "Maximum tooltip lines shown at once before scrolling kicks in."
        ).defineInRange("maxVisibleLines", 8, 3, 30);

        builder.pop(); // identityScreenTooltips

        // ════════════════════════════════════════════════════════════════
        //  BEASTS
        // ════════════════════════════════════════════════════════════════
        builder.comment("Beast (mob) cultivation system. Mobs accumulate qi and advance through beast realms.")
               .push("beasts");

        BEAST_CULTIVATION_FOR_MONSTERS = builder.comment("If true, monsters also get beast cultivation.").define("cultivationForMonsters", false);
        BEAST_CULTIVATION_FOR_ALL_MOBS = builder.comment("If true, ALL living entities (except players) get beast cultivation.").define("cultivationForAllMobs", false);
        BEAST_CHECK_INTERVAL_TICKS = builder.comment("How often (in ticks) beasts accumulate qi. 20 ticks = 1 second.").defineInRange("checkIntervalTicks", 100, 1, 6000);
        BEAST_QI_DENSITY_THRESHOLD = builder.comment("Minimum biome qi density for beasts to gain qi.").defineInRange("qiDensityThreshold", 0.4, 0.0, 2.0);
        BEAST_QI_GAIN_MULTIPLIER = builder.comment("Multiplier for qi gained per check.").defineInRange("qiGainMultiplier", 5.0, 0.0, 1000.0);

        builder.push("advanceCost");
        // Wired 2026-09-02 via BeastProgressionRulesMixin against base mod 0.1.1479:
        // beasts now advance through the shared Realm ladder (MORTAL -> QI_REFINING
        // -> ... -> LOOSE_IMMORTAL) via BeastProgressionRules.advanceCost(Realm), a
        // static method, replacing the old BeastRealm enum's instance method that
        // BeastRealmMixin (now retired - see that file) used to target. These 6
        // "spirit*" fields keep their original names/TOML keys since their real
        // bytecode defaults are unchanged in the new version - they now gate the
        // cost to reach the first 6 configurable Realm tiers (QI_REFINING through
        // VOID_REFINING) instead of the old BeastRealm-specific tier names, but the
        // numbers and their relative position in the ladder are identical.
        SPIRIT_SOLDIER_ADVANCE_COST = builder.comment("Qi for a beast to advance to Qi Refining (1st beast-cultivator tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritSoldier", 500L, 1L, Long.MAX_VALUE);
        SPIRIT_GENERAL_ADVANCE_COST = builder.comment("Qi for a beast to advance to Foundation Building (2nd tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritGeneral", 5000L, 1L, Long.MAX_VALUE);
        SPIRIT_MARSHAL_ADVANCE_COST = builder.comment("Qi for a beast to advance to Golden Core (3rd tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritMarshal", 50000L, 1L, Long.MAX_VALUE);
        SPIRIT_KING_ADVANCE_COST = builder.comment("Qi for a beast to advance to Nascent Soul (4th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritKing", 500000L, 1L, Long.MAX_VALUE);
        SPIRIT_EMPEROR_ADVANCE_COST = builder.comment("Qi for a beast to advance to Soul Formation (5th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritEmperor", 5000000L, 1L, Long.MAX_VALUE);
        SPIRIT_LORD_ADVANCE_COST = builder.comment("Qi for a beast to advance to Void Refining (6th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("spiritLord", 50000000L, 1L, Long.MAX_VALUE);
        // New 2026-09-02: the 4 additional Realm tiers 0.1.1479 added to beast
        // progression (base mod defaults continue the same x10-per-tier curve).
        BEAST_BODY_INTEGRATION_ADVANCE_COST = builder.comment("Qi for a beast to advance to Body Integration (7th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("bodyIntegration", 500000000L, 1L, Long.MAX_VALUE);
        BEAST_MAHAYANA_ADVANCE_COST = builder.comment("Qi for a beast to advance to Mahayana (8th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("mahayana", 5000000000L, 1L, Long.MAX_VALUE);
        BEAST_TRIBULATION_TRANSCENDENCE_ADVANCE_COST = builder.comment("Qi for a beast to advance to Tribulation Transcendence (9th tier). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("tribulationTranscendence", 50000000000L, 1L, Long.MAX_VALUE);
        BEAST_TRUE_IMMORTAL_ADVANCE_COST = builder.comment("Qi for a beast to advance to True Immortal (10th tier, one below the final Loose Immortal cap which is not configurable - matches the base mod's own uncapped final realm). Wired 2026-09-02 via BeastProgressionRulesMixin.").defineInRange("trueImmortal", 500000000000L, 1L, Long.MAX_VALUE);
        builder.pop();

        builder.pop(); // beasts

        // ════════════════════════════════════════════════════════════════
        //  SPAWNS
        // ════════════════════════════════════════════════════════════════
        builder.comment("Wandering cultivator NPC spawn rates and realm distribution.")
               .push("spawns");

        CULTIVATOR_SPAWN_CHANCE_NEAR = builder.comment("Chance to spawn near preferred structures. 0.0003 = 0.03%. " +
                "Default is 50% higher than the original mod to populate the world with more NPCs.").defineInRange("cultivatorSpawnChanceNear", 0.0003, 0.0, 1.0);
        CULTIVATOR_SPAWN_CHANCE_FAR = builder.comment("Chance to spawn away from structures. 0.000075 = 0.0075%. " +
                "Default is 50% higher than the original mod to populate the world with more NPCs.").defineInRange("cultivatorSpawnChanceFar", 0.000075, 0.0, 1.0);

        builder.push("npcRealmWeights");
        NPC_WEIGHT_MORTAL = builder.comment("Spawn weight for Mortal cultivators.").defineInRange("mortal", 1000, 0, 1000000);
        NPC_WEIGHT_QI_REFINING = builder.defineInRange("qiRefining", 500, 0, 1000000);
        NPC_WEIGHT_FOUNDATION_BUILDING = builder.defineInRange("foundationBuilding", 250, 0, 1000000);
        NPC_WEIGHT_GOLDEN_CORE = builder.defineInRange("goldenCore", 125, 0, 1000000);
        NPC_WEIGHT_NASCENT_SOUL = builder.defineInRange("nascentSoul", 60, 0, 1000000);
        NPC_WEIGHT_SOUL_FORMATION = builder.defineInRange("soulFormation", 30, 0, 1000000);
        NPC_WEIGHT_VOID_REFINING = builder.defineInRange("voidRefining", 15, 0, 1000000);
        NPC_WEIGHT_BODY_INTEGRATION = builder.defineInRange("bodyIntegration", 8, 0, 1000000);
        NPC_WEIGHT_MAHAYANA = builder.defineInRange("mahayana", 4, 0, 1000000);
        NPC_WEIGHT_TRIBULATION_TRANSCENDENCE = builder.defineInRange("tribulationTranscendence", 2, 0, 1000000);
        NPC_WEIGHT_LOOSE_IMMORTAL = builder.comment("Spawn weight for Loose Immortal cultivators (failed ascension but still powerful).").defineInRange("looseImmortal", 1, 0, 1000000);
        NPC_WEIGHT_TRUE_IMMORTAL = builder.defineInRange("trueImmortal", 1, 0, 1000000);
        builder.pop();

        builder.pop(); // spawns

        // ════════════════════════════════════════════════════════════════
        //  QI DENSITY
        // ════════════════════════════════════════════════════════════════
        builder.comment("Biome qi density values. Higher = more qi available for cultivation.")
               .push("qiDensity");

        QI_DENSITY_SPARSE = builder.comment("Qi density for sparse biomes (desert, badlands).").defineInRange("sparse", 0.1, 0.0, 50.0);
        QI_DENSITY_NORMAL = builder.comment("Qi density for normal biomes (plains, etc.).").defineInRange("normal", 0.35, 0.0, 50.0);
        QI_DENSITY_WOOD_RICH = builder.comment("Qi density for wood-rich biomes (forest, jungle, taiga).").defineInRange("woodRich", 0.55, 0.0, 50.0);
        QI_DENSITY_WATER_RICH = builder.comment("Qi density for water-rich biomes (ocean, river, swamp).").defineInRange("waterRich", 0.5, 0.0, 50.0);
        QI_DENSITY_FIRE_RICH = builder.comment("Qi density for fire-rich biomes (nether).").defineInRange("fireRich", 0.6, 0.0, 50.0);
        QI_DENSITY_EARTH_RICH = builder.comment("Qi density for earth-rich biomes (mountain).").defineInRange("earthRich", 0.45, 0.0, 50.0);
        QI_DENSITY_ICE_RICH = builder.comment("Qi density for ice-rich biomes (snowy, frozen).").defineInRange("iceRich", 0.5, 0.0, 50.0);
        QI_DENSITY_END_PURE = builder.comment("Qi density for End biomes.").defineInRange("endPure", 0.45, 0.0, 50.0);

        builder.pop(); // qiDensity

        // ════════════════════════════════════════════════════════════════
        //  SPELLS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Spell damage, qi cost, and charge values.")
               .push("spells");

        SPELL_DAMAGE_GLOBAL_MULTIPLIER = builder.comment("Global multiplier for all spell damage. 1.0 = no change.").defineInRange("damageGlobalMultiplier", 1.0, 0.0, 100.0);
        SPELL_QI_COST_GLOBAL_MULTIPLIER = builder.comment("Global multiplier for all spell qi costs. 1.0 = no change.").defineInRange("qiCostGlobalMultiplier", 1.0, 0.0, 100.0);
        NPC_SPELL_DAMAGE_MULTIPLIER = builder.comment("Additional spell damage multiplier for NPC spells only. " +
                "Applied on top of the global multiplier. 1.0 = no change. 4.0 = NPC spells do 4x damage. " +
                "This allows NPCs to have different spell damage scaling than the player.").defineInRange("npcSpellDamageMultiplier", 1.0, 0.0, 100.0);
        NPC_SPELL_QI_COST_MULTIPLIER = builder.comment("Additional qi cost multiplier for NPC spells only. " +
                "Applied on top of the global multiplier. 1.0 = no change. 0.5 = NPC spells cost half qi. " +
                "Lower values let NPCs cast more spells before running out of qi.").defineInRange("npcSpellQiCostMultiplier", 1.0, 0.0, 100.0);
        SPELL_CHARGE_GLOBAL_MULTIPLIER = builder.comment("Global multiplier for spell charge requirements. 1.0 = no change.").defineInRange("chargeGlobalMultiplier", 1.0, 0.01, 100.0);
        SWORD_FLIGHT_UPKEEP_QI_PER_SECOND = builder.comment("Qi drain per second for sword flight. Wired 2026-09-01 " +
                "via SwordFlightHandlerMixin, targeting the single per-second drain literal in tick().")
                .defineInRange("swordFlightUpkeepQiPerSecond", 20, 0, 10000);
        VOID_STEP_AIR_JUMP_QI_COST = builder.comment("Qi cost for void step air jump. Wired 2026-09-01 via " +
                "VoidStepHandlerMixin, targeting the 2 real occurrences (check + deduct) in handleJump3Blocks().")
                .defineInRange("voidStepAirJumpQiCost", 15, 0, 10000);
        VOID_STEP_DASH_QI_COST = builder.comment("Qi cost for void step dash. Wired 2026-09-01 via " +
                "VoidStepHandlerMixin, targeting the 2 real occurrences (check + deduct) in handleDash().")
                .defineInRange("voidStepDashQiCost", 60, 0, 10000);
        VOID_STEP_SLOW_FALL_QI_COST = builder.comment("Qi cost for void step slow fall. NOT WIRED: a full " +
                "disassembly of VoidStepHandler.class found only 3 qi check/deduct pairs total, fully accounted " +
                "for by air jump and dash - applyAutoSlowFall() contains no qi cost at all. Auto slow-fall is a " +
                "free passive safety feature in the base mod, not a qi-costing ability; there is no real " +
                "consumer to wire this to.").defineInRange("voidStepSlowFallQiCost", 30, 0, 10000);
        PALM_THUNDER_CHANNEL_QI_PER_SECOND = builder.comment("Qi drain per second for palm thunder channeling. " +
                "Wired 2026-09-01 via PalmThunderHandlerMixin, converting to the real per-4-tick drain amount " +
                "(perSecond / 5) at the single drain call site in tickChannel().")
                .defineInRange("palmThunderChannelQiPerSecond", 50, 0, 10000);
        PALM_THUNDER_ARMING_TICKS = builder.comment("Ticks to arm palm thunder. Wired 2026-09-01 via " +
                "PalmThunderHandlerMixin, targeting the 3 real occurrences in tickChannel(); also kept in sync " +
                "with the fullChargeTicks() UI helper via PlayerChargeTimelineMixin.")
                .defineInRange("palmThunderArmingTicks", 40, 1, 600);
        VOID_ESCAPE_CHARGE_TICKS = builder.comment("Total ticks to charge void escape. Wired 2026-09-01 via " +
                "VoidEscapeHandlerMixin, targeting the single threshold check in tickCharge(); also kept in sync " +
                "with the fullChargeTicks() UI helper via PlayerChargeTimelineMixin.")
                .defineInRange("voidEscapeChargeTicks", 100, 1, 1200);
        VOID_ESCAPE_CHARGE_QI_PER_TICK = builder.comment("Qi cost per tick for void escape charging. Wired " +
                "2026-09-01 via VoidEscapeHandlerMixin, targeting the single drain literal in tickCharge().")
                .defineInRange("voidEscapeChargeQiPerTick", 10, 0, 1000);
        VOID_ESCAPE_ACTIVE_QI_PER_TICK = builder.comment("Qi cost per tick during void escape. Wired 2026-09-01 " +
                "via VoidEscapeHandlerMixin, targeting the single drain literal in onPlayerTick().")
                .defineInRange("voidEscapeActiveQiPerTick", 5, 0, 1000);
        BUDDHA_FIRE_LOTUS_READY_QI = builder.comment("Qi required to fire Buddha Fire Lotus. Wired 2026-09-01 via " +
                "ChargeableSpellHandlerMixin, targeting all 5 confirmed occurrences of the real field's inlined " +
                "value across the class.").defineInRange("buddhaFireLotusReadyQi", 10000, 1, 1000000);
        CORE_SELF_DESTRUCT_READY_QI = builder.comment("Qi required for core self destruct. Wired 2026-09-01 via " +
                "ChargeableSpellHandlerMixin, targeting all 5 confirmed occurrences of the real field's inlined " +
                "value across the class.").defineInRange("coreSelfDestructReadyQi", 1000, 1, 1000000);
        SEA_CHANNEL_TICKS = builder.comment("Ticks to channel Sea of Overwhelming Blood. Wired 2026-09-01 via " +
                "Blood07FormationHandlerMixin (the real implementing class, despite the name), targeting the " +
                "single threshold check in tickChannel(); also kept in sync with the fullChargeTicks() UI helper " +
                "via PlayerChargeTimelineMixin.").defineInRange("seaChannelTicks", 200, 1, 1200);
        GLACIER_BURIAL_NPC_CHANNEL_TICKS = builder.comment("NPC glacier burial channel ticks. NOT WIRED: the real " +
                "field of this exact name lives on PlayerChargeTimeline.class (ConstantValue 100, matching this " +
                "default) but a full search of that class and NpcCombatChannelController.class (the NPC channel " +
                "driver) found no getstatic reference to it and no inlined-100 occurrence tied to a channel-tick " +
                "cap - NPC glacier burial channel continuation is driven by target/state checks in canContinue(), " +
                "not a tick counter. The field appears to be declared but never actually read anywhere in the " +
                "compiled mod; left honestly unwired rather than guessed at.")
                .defineInRange("glacierBurialNpcChannelTicks", 100, 1, 600);
        GLACIER_BURIAL_BASE_QI_PER_TICK = builder.comment("Base qi per tick for glacier burial. Wired 2026-09-01 " +
                "via PlayerChargeTimelineMixin, targeting both occurrences (ramp start + floor) in " +
                "glacierBurialQiCostAtTick() specifically - NOT method = \"*\", since a same-valued but unrelated " +
                "BASE_DRAIN field also inlines to \"long 5l\" elsewhere in that class.")
                .defineInRange("glacierBurialBaseQiPerTick", 5L, 1L, 10000L);

        builder.pop(); // spells

        // ════════════════════════════════════════════════════════════════
        //  WEAPONS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Weapon damage and special effect values.")
               .push("weapons");

        WEAPON_DAMAGE_GLOBAL_MULTIPLIER = builder.comment("Global multiplier for all weapon attack damage. 1.0 = no change. " +
                "Applied live on every hit dealt with a spirit sword or the Bloodthirst Blade (melee only, not spells/projectiles) - " +
                "wired 2026-09-01 via a LivingHurtEvent handler, not a mixin, since the base mod bakes weapon attack " +
                "damage into a vanilla attribute at item construction with no live accessor to override.").defineInRange("damageGlobalMultiplier", 1.0, 0.0, 100.0);
        WEAPON_BLOOD_SPELL_DAMAGE_BONUS_MULT = builder.comment("Multiplier for the Bloodthirst Blade's blood spell damage bonus percent. " +
                "1.0 = no change. Wired 2026-09-01.").defineInRange("bloodSpellDamageBonusMult", 1.0, 0.0, 100.0);
        WEAPON_BLOOD_SPELL_QI_REDUCTION_MULT = builder.comment("Multiplier for the Bloodthirst Blade's blood spell qi reduction percent " +
                "(result is clamped to 0-100). 1.0 = no change. Wired 2026-09-01.").defineInRange("bloodSpellQiReductionMult", 1.0, 0.0, 100.0);
        WEAPON_BLOOD_CAPACITY_MULTIPLIER = builder.comment("Multiplier for the Bloodthirst Blade's max blood capacity. " +
                "1.0 = no change. Wired 2026-09-01.").defineInRange("bloodCapacityMultiplier", 1.0, 0.0, 10000.0);
        CHIYAN_BURN_TICKS = builder.comment("Burn duration in ticks for ChiYan sword on-hit effect.").defineInRange("chiyanBurnTicks", 100, 0, 1200);
        HANBING_FROZEN_OVERFLOW_TICKS = builder.comment("Frozen overflow ticks for HanBing sword on-hit effect.").defineInRange("hanbingFrozenOverflowTicks", 80, 0, 1200);
        QINGMU_POISON_TICKS = builder.comment("Poison duration in ticks for QingMu sword on-hit effect.").defineInRange("qingmuPoisonTicks", 200, 0, 1200);
        SOUL_HOOK_ATTACK_DAMAGE = builder.comment("Attack damage for Soul Hook weapon.").defineInRange("soulHookAttackDamage", 15, 0, 1000);
        WEAPON_ATTACK_SPEED_MODIFIER = builder.comment("Attack speed modifier for spirit swords (default -2.4). Not yet wired - " +
                "verified (2026-09-01) that this is baked into a vanilla weapon attribute at item construction with no live " +
                "accessor, and (unlike the damage multiplier above) there's no safe runtime event to intercept it through " +
                "either; changing this currently has no effect. Tracked in CONFIG_AUDIT.md.").defineInRange("attackSpeedModifier", -2.4, -10.0, 10.0);
        builder.push("spellQiCostReductionPercent");
        WEAPON_SPELL_QI_COST_REDUCTION_LOW = builder.comment("Spell qi cost reduction % for LOW tier weapons.").defineInRange("low", 5, 0, 100);
        WEAPON_SPELL_QI_COST_REDUCTION_MID = builder.comment("Spell qi cost reduction % for MID tier weapons.").defineInRange("mid", 7, 0, 100);
        WEAPON_SPELL_QI_COST_REDUCTION_HIGH = builder.comment("Spell qi cost reduction % for HIGH tier weapons.").defineInRange("high", 10, 0, 100);
        WEAPON_SPELL_QI_COST_REDUCTION_SUPREME = builder.comment("Spell qi cost reduction % for SUPREME tier weapons.").defineInRange("supreme", 15, 0, 100);
        WEAPON_SPELL_QI_COST_REDUCTION_IMMORTAL = builder.comment("Spell qi cost reduction % for IMMORTAL tier weapons.").defineInRange("immortal", 20, 0, 100);
        builder.pop();

        builder.pop(); // weapons

        // ════════════════════════════════════════════════════════════════
        //  PILLS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Pill qi recovery, effect durations, and item values.")
               .push("pills");

        PILL_USE_TICKS = builder.comment("Use animation ticks for pills.").defineInRange("useTicks", 20, 1, 200);
        SPIRIT_STONE_USE_TICKS = builder.comment("Use animation ticks for spirit stones.").defineInRange("spiritStoneUseTicks", 32, 1, 200);
        builder.push("qiRecovery");
        PILL_QI_LOW = builder.comment("Qi recovered by LOW tier qi pills.").defineInRange("low", 10, 0, 1000000);
        PILL_QI_MID = builder.comment("Qi recovered by MID tier qi pills.").defineInRange("mid", 100, 0, 1000000);
        PILL_QI_HIGH = builder.comment("Qi recovered by HIGH tier qi pills.").defineInRange("high", 1000, 0, 1000000);
        PILL_QI_SUPREME = builder.comment("Qi recovered by SUPREME tier qi pills.").defineInRange("supreme", 10000, 0, 1000000);
        PILL_QI_IMMORTAL = builder.comment("Qi recovered by IMMORTAL tier qi pills (-1 = full refill).").defineInRange("immortal", -1, -1, 1000000);
        builder.pop();
        builder.push("spiritStoneQi");
        SPIRIT_STONE_QI_LOW = builder.comment("Qi in LOW spirit stones.").defineInRange("low", 10, 0, 1000000);
        SPIRIT_STONE_QI_MID = builder.comment("Qi in MID spirit stones.").defineInRange("mid", 100, 0, 1000000);
        SPIRIT_STONE_QI_HIGH = builder.comment("Qi in HIGH spirit stones.").defineInRange("high", 1000, 0, 1000000);
        SPIRIT_STONE_QI_SUPREME = builder.comment("Qi in SUPREME spirit stones.").defineInRange("supreme", 10000, 0, 1000000);
        builder.pop();
        builder.push("bloodBurnPill");
        BLOOD_BURN_PILL_DAMAGE_LOW = builder.comment("Health cost for LOW blood burn pill.").defineInRange("damageLow", 4.0, 0.0, 1000.0);
        BLOOD_BURN_PILL_DAMAGE_MID = builder.comment("Health cost for MID blood burn pill.").defineInRange("damageMid", 6.0, 0.0, 1000.0);
        BLOOD_BURN_PILL_DAMAGE_HIGH = builder.comment("Health cost for HIGH blood burn pill.").defineInRange("damageHigh", 8.0, 0.0, 1000.0);
        BLOOD_BURN_PILL_DAMAGE_SUPREME = builder.comment("Health cost for SUPREME blood burn pill.").defineInRange("damageSupreme", 10.0, 0.0, 1000.0);
        BLOOD_BURN_PILL_DAMAGE_IMMORTAL = builder.comment("Health cost for IMMORTAL blood burn pill.").defineInRange("damageImmortal", 20.0, 0.0, 1000.0);
        BLOOD_BURN_PILL_DURATION_TICKS = builder.comment("Effect duration in ticks for blood burn pill.").defineInRange("durationTicks", 1200, 0, 6000);
        builder.pop();
        builder.push("clearMindPill");
        CLEAR_MIND_PILL_DURATION_HIGH = builder.comment("Clear mind duration in ticks for HIGH tier.").defineInRange("durationHigh", 1200, 0, 60000);
        CLEAR_MIND_PILL_DURATION_SUPREME = builder.comment("Clear mind duration in ticks for SUPREME tier.").defineInRange("durationSupreme", 3600, 0, 60000);
        builder.pop();
        builder.push("divineStridePill");
        DIVINE_STRIDE_DURATION_LOW = builder.comment("Duration in seconds for LOW divine stride pill.").defineInRange("durationLow", 180, 0, 60000);
        DIVINE_STRIDE_DURATION_MID = builder.comment("Duration in seconds for MID divine stride pill.").defineInRange("durationMid", 480, 0, 60000);
        DIVINE_STRIDE_DURATION_HIGH = builder.comment("Duration in seconds for HIGH divine stride pill.").defineInRange("durationHigh", 600, 0, 60000);
        DIVINE_STRIDE_DURATION_SUPREME = builder.comment("Duration in seconds for SUPREME divine stride pill.").defineInRange("durationSupreme", 1800, 0, 60000);
        DIVINE_STRIDE_SPEED_LOW = builder.comment("Speed amplifier for LOW divine stride pill.").defineInRange("speedLow", 0, 0, 10);
        DIVINE_STRIDE_SPEED_MID = builder.comment("Speed amplifier for MID divine stride pill.").defineInRange("speedMid", 1, 0, 10);
        DIVINE_STRIDE_SPEED_HIGH = builder.comment("Speed amplifier for HIGH divine stride pill.").defineInRange("speedHigh", 3, 0, 10);
        DIVINE_STRIDE_SPEED_SUPREME = builder.comment("Speed amplifier for SUPREME divine stride pill.").defineInRange("speedSupreme", 3, 0, 10);
        builder.pop();
        SHADOW_STEP_PILL_DURATION_TICKS = builder.comment("Duration in ticks for shadow step pill.").defineInRange("shadowStepDurationTicks", 6000, 0, 60000);
        YOUTH_PILL_MIN_BONE_AGE = builder.comment("Minimum bone age for youth pill to work.").defineInRange("youthPillMinBoneAge", 17, 0, 200);
        YOUTH_PILL_BONE_AGE_REDUCTION = builder.comment("Bone age reduction per youth pill.").defineInRange("youthPillBoneAgeReduction", 1.0, 0.0, 100.0);
        builder.push("foundationMaterial");
        FOUNDATION_MATERIAL_USE_TICKS = builder.comment("Use ticks for foundation material items.").defineInRange("useTicks", 32, 1, 200);
        FOUNDATION_MATERIAL_ZHUJI_DAN_QI = builder.comment("Cultivation gain from Zhuji Dan.").defineInRange("zhujiDanQi", 10L, 0L, 1000000L);
        FOUNDATION_MATERIAL_DAO_FRUIT_QI = builder.comment("Cultivation gain from Dao Fruit.").defineInRange("daoFruitQi", 10L, 0L, 1000000L);
        builder.pop();
        builder.push("goldenCoreMaterial");
        GOLDEN_CORE_MATERIAL_USE_TICKS = builder.comment("Use ticks for golden core material items.").defineInRange("useTicks", 32, 1, 200);
        GOLDEN_CORE_MATERIAL_JIEDAN_PILL_QI = builder.comment("Cultivation gain from Jiedan Pill.").defineInRange("jiedanPillQi", 100L, 0L, 1000000L);
        GOLDEN_CORE_MATERIAL_CREATION_FRUIT_QI = builder.comment("Cultivation gain from Creation Fruit.").defineInRange("creationFruitQi", 100L, 0L, 1000000L);
        builder.pop();
        builder.push("rejuvenation");
        REJUVENATION_HEAL_LOW = builder.comment("Heal amount for LOW rejuvenation pill.").defineInRange("healLow", 4.0, 0.0, 1000.0);
        REJUVENATION_HEAL_MID = builder.comment("Heal amount for MID rejuvenation pill.").defineInRange("healMid", 10.0, 0.0, 1000.0);
        REJUVENATION_REGEN_TICKS_SUPREME = builder.comment("Regeneration ticks for SUPREME rejuvenation pill.").defineInRange("regenTicksSupreme", 2400, 0, 60000);
        REJUVENATION_REGEN_AMP_SUPREME = builder.comment("Regeneration amplifier for SUPREME rejuvenation pill.").defineInRange("regenAmpSupreme", 1, 0, 10);
        REJUVENATION_ABSORPTION_TICKS_SUPREME = builder.comment("Absorption ticks for SUPREME rejuvenation pill.").defineInRange("absorptionTicksSupreme", 2400, 0, 60000);
        REJUVENATION_ABSORPTION_AMP_SUPREME = builder.comment("Absorption amplifier for SUPREME rejuvenation pill.").defineInRange("absorptionAmpSupreme", 0, 0, 10);
        builder.pop();
        builder.push("storageBag");
        STORAGE_BAG_COLUMNS_LOW = builder.comment("Columns for LOW storage bag.").defineInRange("columnsLow", 3, 1, 30);
        STORAGE_BAG_COLUMNS_MID = builder.comment("Columns for MID storage bag.").defineInRange("columnsMid", 6, 1, 30);
        STORAGE_BAG_COLUMNS_HIGH = builder.comment("Columns for HIGH storage bag.").defineInRange("columnsHigh", 9, 1, 30);
        STORAGE_BAG_COLUMNS_SUPREME = builder.comment("Columns for SUPREME storage bag.").defineInRange("columnsSupreme", 9, 1, 30);
        STORAGE_BAG_COLUMNS_IMMORTAL = builder.comment("Columns for IMMORTAL storage bag.").defineInRange("columnsImmortal", 9, 1, 30);
        STORAGE_BAG_ROWS_LOW = builder.comment("Rows for LOW storage bag.").defineInRange("rowsLow", 3, 1, 30);
        STORAGE_BAG_ROWS_MID = builder.comment("Rows for MID storage bag.").defineInRange("rowsMid", 3, 1, 30);
        STORAGE_BAG_ROWS_HIGH = builder.comment("Rows for HIGH storage bag.").defineInRange("rowsHigh", 3, 1, 30);
        STORAGE_BAG_ROWS_SUPREME = builder.comment("Rows for SUPREME storage bag.").defineInRange("rowsSupreme", 6, 1, 30);
        STORAGE_BAG_ROWS_IMMORTAL = builder.comment("Rows for IMMORTAL storage bag.").defineInRange("rowsImmortal", 12, 1, 30);
        STORAGE_BAG_VISIBLE_ROWS_MAX = builder.comment("Maximum visible rows at once (scroll for more).").defineInRange("visibleRowsMax", 6, 1, 30);
        builder.pop();

        builder.pop(); // pills

        // ════════════════════════════════════════════════════════════════
        //  ALCHEMY
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Alchemy furnace parameters and rank progression.")
               .push("alchemy");

        builder.push("furnace");
        ALCHEMY_FURNACE_MAX_QI = builder.comment("Max qi storage for alchemy furnace.").defineInRange("maxQi", 100000L, 1L, Long.MAX_VALUE);
        ALCHEMY_TICKS_PER_PILL = builder.comment("Ticks to craft one pill. Not yet wired - verified (2026-09-01) this " +
                "mirrors a public static final int constant on the base mod's AlchemyCoreBlockEntity, which javac bakes " +
                "as a literal into every place that reads it (not a live field read), so a mixin on the field itself " +
                "would be a no-op; making this live would need per-call-site bytecode work not attempted yet.").defineInRange("ticksPerPill", 100, 1, 6000);
        ALCHEMY_MAX_PILLS_PER_BATCH = builder.comment("Maximum pills per crafting batch.").defineInRange("maxPillsPerBatch", 64, 1, 256);
        ALCHEMY_INPUT_SLOTS = builder.comment("Number of input slots in alchemy furnace. Not yet wired - same " +
                "compile-time-constant situation as ticksPerPill above; changing this currently has no effect.").defineInRange("inputSlots", 6, 1, 36);
        ALCHEMY_OUTPUT_SLOTS = builder.comment("Number of output slots in alchemy furnace. Not yet wired - same " +
                "compile-time-constant situation as ticksPerPill above; changing this currently has no effect.").defineInRange("outputSlots", 1, 1, 36);
        builder.pop();
        builder.push("xpGains");
        ALCHEMY_XP_GAIN_LOW = builder.comment("XP gained for crafting a LOW tier pill.").defineInRange("low", 5, 0, 100000);
        ALCHEMY_XP_GAIN_MID = builder.comment("XP gained for crafting a MID tier pill.").defineInRange("mid", 15, 0, 100000);
        ALCHEMY_XP_GAIN_HIGH = builder.comment("XP gained for crafting a HIGH tier pill.").defineInRange("high", 50, 0, 100000);
        ALCHEMY_XP_GAIN_SUPREME = builder.comment("XP gained for crafting a SUPREME tier pill.").defineInRange("supreme", 150, 0, 100000);
        ALCHEMY_XP_GAIN_IMMORTAL = builder.comment("XP gained for crafting an IMMORTAL tier pill.").defineInRange("immortal", 500, 0, 100000);
        ALCHEMY_XP_GAIN_FAILURE = builder.comment("XP gained on crafting failure.").defineInRange("failure", 2, 0, 100000);
        builder.pop();
        ALCHEMY_HEART_SUCCESS_BONUS = builder.comment("Success chance bonus from Alchemy Heart physique.").defineInRange("heartSuccessBonus", 0.1, 0.0, 1.0);
        ALCHEMY_HEART_QI_COST_MULT = builder.comment("Qi cost multiplier from Alchemy Heart physique.").defineInRange("heartQiCostMult", 0.5, 0.0, 10.0);

        builder.pop(); // alchemy

        // ════════════════════════════════════════════════════════════════
        //  REFINING
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Refining furnace parameters and rank progression.")
               .push("refining");

        builder.push("furnace");
        REFINING_FURNACE_MAX_QI = builder.comment("Max qi storage for refining furnace.").defineInRange("maxQi", 100000L, 1L, Long.MAX_VALUE);
        REFINING_TICKS_PER_ITEM = builder.comment("Ticks to craft one item. Not yet wired - verified (2026-09-01) this " +
                "mirrors a public static final int constant on the base mod's RefiningCoreBlockEntity, which javac " +
                "bakes as a literal into every place that reads it (not a live field read), so a mixin on the field " +
                "itself would be a no-op; making this live would need per-call-site bytecode work not attempted yet.").defineInRange("ticksPerItem", 200, 1, 6000);
        REFINING_MAX_ITEMS_PER_BATCH = builder.comment("Maximum items per crafting batch.").defineInRange("maxItemsPerBatch", 1, 1, 64);
        REFINING_INPUT_SLOTS = builder.comment("Number of input slots in refining furnace. Not yet wired - same " +
                "compile-time-constant situation as ticksPerItem above; changing this currently has no effect.").defineInRange("inputSlots", 6, 1, 36);
        REFINING_OUTPUT_SLOTS = builder.comment("Number of output slots in refining furnace. Not yet wired - same " +
                "compile-time-constant situation as ticksPerItem above; changing this currently has no effect.").defineInRange("outputSlots", 1, 1, 36);
        builder.pop();
        builder.push("xpGains");
        REFINING_XP_GAIN_LOW = builder.comment("XP gained for crafting a LOW tier item.").defineInRange("low", 5, 0, 100000);
        REFINING_XP_GAIN_MID = builder.comment("XP gained for crafting a MID tier item.").defineInRange("mid", 15, 0, 100000);
        REFINING_XP_GAIN_HIGH = builder.comment("XP gained for crafting a HIGH tier item.").defineInRange("high", 50, 0, 100000);
        REFINING_XP_GAIN_SUPREME = builder.comment("XP gained for crafting a SUPREME tier item.").defineInRange("supreme", 150, 0, 100000);
        REFINING_XP_GAIN_IMMORTAL = builder.comment("XP gained for crafting an IMMORTAL tier item.").defineInRange("immortal", 500, 0, 100000);
        REFINING_XP_GAIN_FAILURE = builder.comment("XP gained on crafting failure.").defineInRange("failure", 2, 0, 100000);
        builder.pop();
        REFINING_TIER_UP_CHANCE_DIVINE_FORGE = builder.comment("Tier-up chance from Divine Forge technique.").defineInRange("tierUpChanceDivineForge", 0.25, 0.0, 1.0);
        REFINING_TIER_UP_CHANCE_HEAVENLY_ELIXIR = builder.comment("Tier-up chance from Heavenly Elixir technique. " +
                "Verified (2026-09-01) this field's name is a bit misleading: the base mod's Heavenly Elixir technique " +
                "doesn't actually grant a refining tier-up bonus at all - its real bonus is an ALCHEMY tier-up chance " +
                "of this same size. Kept under refining/this same setting name for backward compatibility, but it " +
                "controls Heavenly Elixir's alchemy tier-up chance, not a refining one.").defineInRange("tierUpChanceHeavenlyElixir", 0.25, 0.0, 1.0);

        builder.pop(); // refining

        // ════════════════════════════════════════════════════════════════
        //  SPIRIT PLANTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Spirit plant growth and special effect values.")
               .push("spiritPlants");

        SPIRIT_PLANT_MAX_AGE = builder.comment("Max growth age for spirit plants.").defineInRange("maxAge", 3, 1, 20);
        SPIRIT_PLANT_GROWTH_TICK_BASE = builder.comment("Base growth tick value.").defineInRange("growthTickBase", 25.0, 0.0, 1000.0);
        builder.push("spiritGatheringFlower");
        SPIRIT_PLANT_QI_ORB_AMOUNT = builder.comment("Qi orb amount spawned by spirit gathering flower.").defineInRange("qiOrbAmount", 10, 0, 1000);
        SPIRIT_PLANT_SKIP_RADIUS = builder.comment("Skip radius for qi orb spawning.").defineInRange("skipRadius", 6.0, 0.0, 100.0);
        SPIRIT_PLANT_SKIP_COUNT = builder.comment("Skip count - don't spawn if this many orbs nearby.").defineInRange("skipCount", 3, 0, 100);
        builder.pop();
        SPIRIT_PLANT_FLAME_MELT_RADIUS = builder.comment("Melt radius for flame pepper.").defineInRange("flameMeltRadius", 2, 0, 50);
        builder.push("earthMarrowGinseng");
        SPIRIT_PLANT_EARTH_MARROW_GROWTH_CHANCE = builder.comment("Growth boost chance for earth marrow ginseng.").defineInRange("growthChance", 0.5, 0.0, 1.0);
        SPIRIT_PLANT_EARTH_MARROW_GROWTH_RADIUS = builder.comment("Growth boost radius for earth marrow ginseng.").defineInRange("growthRadius", 2, 0, 50);
        builder.pop();
        SPIRIT_PLANT_GOLDEN_CHRYSANTHEMUM_DROP_CHANCE = builder.comment("Gold nugget drop chance for golden chrysanthemum.").defineInRange("goldenChrysanthemumDropChance", 0.08, 0.0, 1.0);
        builder.push("snowSoulLotus");
        SPIRIT_PLANT_SNOW_RADIUS = builder.comment("Snow placement radius for snow soul lotus.").defineInRange("snowRadius", 2, 0, 50);
        SPIRIT_PLANT_SNOW_MAX_LAYERS = builder.comment("Max snow layers for snow soul lotus.").defineInRange("snowMaxLayers", 4, 0, 8);
        SPIRIT_PLANT_SNOW_PLACE_ATTEMPTS = builder.comment("Snow place attempts for snow soul lotus.").defineInRange("snowPlaceAttempts", 8, 0, 100);
        builder.pop();

        builder.pop(); // spiritPlants

        // ════════════════════════════════════════════════════════════════
        //  SPIRIT VEINS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Spirit vein core tier values.")
               .push("spiritVeins");

        builder.push("maxQi");
        SPIRIT_VEIN_MAX_QI_LOW = builder.comment("Max qi for LOW spirit vein core.").defineInRange("low", 100L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_MAX_QI_MID = builder.comment("Max qi for MID spirit vein core.").defineInRange("mid", 1000L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_MAX_QI_HIGH = builder.comment("Max qi for HIGH spirit vein core.").defineInRange("high", 10000L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_MAX_QI_SUPREME = builder.comment("Max qi for SUPREME spirit vein core.").defineInRange("supreme", 100000L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_MAX_QI_IMMORTAL = builder.comment("Max qi for IMMORTAL spirit vein core.").defineInRange("immortal", 1000000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.push("orbGain");
        SPIRIT_VEIN_ORB_GAIN_LOW = builder.defineInRange("low", 1L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_ORB_GAIN_MID = builder.defineInRange("mid", 10L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_ORB_GAIN_HIGH = builder.defineInRange("high", 100L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_ORB_GAIN_SUPREME = builder.defineInRange("supreme", 1000L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_ORB_GAIN_IMMORTAL = builder.defineInRange("immortal", 10000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.push("supplyPerSecond");
        SPIRIT_VEIN_SUPPLY_LOW = builder.defineInRange("low", 1L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_SUPPLY_MID = builder.defineInRange("mid", 10L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_SUPPLY_HIGH = builder.defineInRange("high", 100L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_SUPPLY_SUPREME = builder.defineInRange("supreme", 1000L, 1L, Long.MAX_VALUE);
        SPIRIT_VEIN_SUPPLY_IMMORTAL = builder.defineInRange("immortal", 10000L, 1L, Long.MAX_VALUE);
        builder.pop();
        SPIRIT_VEIN_ATTRACT_RADIUS = builder.comment("Qi attraction radius for spirit vein core.").defineInRange("attractRadius", 14.0, 1.0, 200.0);
        SPIRIT_VEIN_SUPPLY_RADIUS = builder.comment("Supply radius for spirit vein core.").defineInRange("supplyRadius", 16, 1, 200);

        builder.pop(); // spiritVeins

        // ════════════════════════════════════════════════════════════════
        //  TECHNIQUES
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Technique bonus multipliers. These are global multipliers applied on top of technique base values.")
               .push("techniques");

        TECHNIQUE_QI_ABSORB_MULT_GLOBAL = builder.comment("Global multiplier for technique qi absorption bonuses. 1.0 = no change.").defineInRange("qiAbsorbMultGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_ATTACK_BONUS_GLOBAL = builder.comment("Global multiplier for technique attack bonuses.").defineInRange("attackBonusGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_DEFENSE_BONUS_GLOBAL = builder.comment("Global multiplier for technique defense bonuses.").defineInRange("defenseBonusGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_MAX_HP_BONUS_GLOBAL = builder.comment("Global multiplier for technique max HP bonuses.").defineInRange("maxHpBonusGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_CRIT_RATE_BONUS_GLOBAL = builder.comment("Global multiplier for technique crit rate bonuses.").defineInRange("critRateBonusGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_ELEMENT_SPELL_MULT_GLOBAL = builder.comment("Global multiplier for technique element spell multipliers.").defineInRange("elementSpellMultGlobal", 1.0, 0.0, 100.0);
        TECHNIQUE_MOVE_SPEED_BONUS_GLOBAL = builder.comment("Global multiplier for technique move speed bonuses.").defineInRange("moveSpeedBonusGlobal", 1.0, 0.0, 100.0);

        builder.pop(); // techniques

        // ════════════════════════════════════════════════════════════════
        //  SPIRIT ROOTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Spirit root bonus multipliers.")
               .push("spiritRoots");

        builder.push("heavenly");
        SPIRIT_ROOT_HEAVENLY_PRIMARY_ELEMENT_MULT = builder.comment("Primary element multiplier for heavenly roots.").defineInRange("primaryElementMult", 1.5, 0.0, 100.0);
        SPIRIT_ROOT_HEAVENLY_COUNTER_ELEMENT_MULT = builder.comment("Counter element multiplier for heavenly roots.").defineInRange("counterElementMult", 0.5, 0.0, 100.0);
        SPIRIT_ROOT_HEAVENLY_EXTRA_ZHENYUAN_PER_SUB_LEVEL = builder.comment("Extra zhenyuan per sub-level for heavenly roots.").defineInRange("extraZhenyuanPerSubLevel", 1, 0, 100);
        builder.pop();
        builder.push("dual");
        SPIRIT_ROOT_DUAL_PRIMARY_ELEMENT_MULT = builder.comment("Primary element multiplier for dual roots.").defineInRange("primaryElementMult", 1.2, 0.0, 100.0);
        SPIRIT_ROOT_DUAL_SECONDARY_ELEMENT_MULT = builder.comment("Secondary element multiplier for dual roots.").defineInRange("secondaryElementMult", 1.2, 0.0, 100.0);
        SPIRIT_ROOT_DUAL_OFF_ELEMENT_MULT = builder.comment("Off element multiplier for dual roots.").defineInRange("offElementMult", 0.9, 0.0, 100.0);
        builder.pop();
        SPIRIT_ROOT_MUTANT_PRIMARY_ELEMENT_MULT = builder.comment("Primary element multiplier for mutant roots.").defineInRange("mutantPrimaryElementMult", 1.6, 0.0, 100.0);
        builder.push("heavenlySword");
        SPIRIT_ROOT_HEAVENLY_SWORD_SWORD_DMG_MULT = builder.comment("Sword damage multiplier for heavenly sword root.").defineInRange("swordDmgMult", 2.0, 0.0, 100.0);
        SPIRIT_ROOT_HEAVENLY_SWORD_NON_ELEMENT_SPELL_MULT = builder.comment("Non-element spell multiplier for heavenly sword root.").defineInRange("nonElementSpellMult", 0.5, 0.0, 100.0);
        builder.pop();
        SPIRIT_ROOT_HEAVENLY_HIDDEN_NON_ELEMENT_SPELL_MULT = builder.comment("Non-element spell multiplier for heavenly hidden root.").defineInRange("heavenlyHiddenNonElementSpellMult", 1.5, 0.0, 100.0);
        builder.push("qiAbsorption");
        SPIRIT_ROOT_QI_ABSORB_SSR_MULT = builder.comment("Qi absorption multiplier for SSR rarity roots.").defineInRange("ssrMult", 1.5, 0.0, 100.0);
        SPIRIT_ROOT_QI_ABSORB_SR_MULT = builder.comment("Qi absorption multiplier for SR rarity roots.").defineInRange("srMult", 1.25, 0.0, 100.0);
        SPIRIT_ROOT_ENVIRONMENT_BUFF_MULT = builder.comment("Environment buff multiplier for mutant roots in favorable biomes.").defineInRange("environmentBuffMult", 1.5, 0.0, 100.0);
        builder.pop();

        builder.pop(); // spiritRoots

        // ════════════════════════════════════════════════════════════════
        //  PHYSIQUES
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Physique bonus values.")
               .push("physiques");

        builder.push("immortalBody");
        PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_MULT = builder.comment("Qi absorb multiplier for Immortal Body physique.").defineInRange("qiAbsorbMult", 10.0, 0.0, 1000.0);
        PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_RANGE = builder.comment("Qi absorb range for Immortal Body physique.").defineInRange("qiAbsorbRange", 10, 0, 200);
        PHYSIQUE_IMMORTAL_BODY_QI_COST_MULT = builder.comment("Qi cost multiplier for Immortal Body physique.").defineInRange("qiCostMult", 0.5, 0.0, 10.0);
        PHYSIQUE_IMMORTAL_BODY_DAMAGE_TAKEN_MULT = builder.comment("Damage taken multiplier for Immortal Body physique.").defineInRange("damageTakenMult", 0.5, 0.0, 10.0);
        PHYSIQUE_IMMORTAL_BODY_MAX_HP_BONUS = builder.comment("Max HP bonus for Immortal Body physique.").defineInRange("maxHpBonus", 10, 0, 10000);
        builder.pop();
        builder.push("innateSwordBody");
        PHYSIQUE_INNATE_SWORD_BODY_SWORD_SPELL_MULT = builder.comment("Sword spell multiplier for Innate Sword Body.").defineInRange("swordSpellMult", 2.0, 0.0, 100.0);
        PHYSIQUE_INNATE_SWORD_NON_SWORD_PENALTY = builder.comment("Non-sword spell penalty for Innate Sword Body.").defineInRange("nonSwordPenalty", 0.2, 0.0, 10.0);
        builder.pop();
        PHYSIQUE_HEAVENLY_FIRE_BODY_FIRE_SPELL_MULT = builder.comment("Fire spell multiplier for Heavenly Fire Body.").defineInRange("heavenlyFireBodyFireSpellMult", 1.2, 0.0, 100.0);
        PHYSIQUE_MYSTIC_ICE_BODY_WATER_SPELL_MULT = builder.comment("Water spell multiplier for Mystic Ice Body.").defineInRange("mysticIceBodyWaterSpellMult", 1.2, 0.0, 100.0);
        PHYSIQUE_SWORD_BONE_SWORD_SPELL_MULT = builder.comment("Sword spell multiplier for Sword Bone physique.").defineInRange("swordBoneSwordSpellMult", 1.2, 0.0, 100.0);
        builder.push("chaosBody");
        PHYSIQUE_CHAOS_BODY_SPELL_DAMAGE_MULT = builder.comment("Spell damage multiplier for Chaos Body with combo.").defineInRange("spellDamageMult", 1.3, 0.0, 100.0);
        PHYSIQUE_CHAOS_BODY_CULTIVATION_REQ_MULT = builder.comment("Cultivation requirement multiplier for Chaos Body.").defineInRange("cultivationReqMult", 10.0, 0.0, 1000.0);
        builder.pop();
        PHYSIQUE_BROKEN_VEIN_HP_MULT = builder.comment("HP multiplier for Broken Vein Body.").defineInRange("brokenVeinHpMult", 2.0, 0.0, 100.0);
        PHYSIQUE_BROKEN_VEIN_MELEE_DMG_MULT = builder.comment("Melee damage multiplier for Broken Vein Body.").defineInRange("brokenVeinMeleeDmgMult", 2.0, 0.0, 100.0);
        PHYSIQUE_IMMORTAL_BLOOD_HP_MULT = builder.comment("HP multiplier for Immortal Blood Body.").defineInRange("immortalBloodHpMult", 2.0, 0.0, 100.0);
        builder.push("rarityWeights");
        PHYSIQUE_RARITY_WEIGHT_LOW = builder.comment("Rarity weight for LOW physiques.").defineInRange("low", 70, 0, 100000);
        PHYSIQUE_RARITY_WEIGHT_MID = builder.comment("Rarity weight for MID physiques.").defineInRange("mid", 20, 0, 100000);
        PHYSIQUE_RARITY_WEIGHT_HIGH = builder.comment("Rarity weight for HIGH physiques.").defineInRange("high", 8, 0, 100000);
        PHYSIQUE_RARITY_WEIGHT_SUPREME = builder.comment("Rarity weight for SUPREME physiques.").defineInRange("supreme", 3, 0, 100000);
        PHYSIQUE_RARITY_WEIGHT_IMMORTAL = builder.comment("Rarity weight for IMMORTAL physiques.").defineInRange("immortal", 1, 0, 100000);
        PHYSIQUE_RARITY_WEIGHT_SPECIAL = builder.comment("Rarity weight for SPECIAL physiques.").defineInRange("special", 1, 0, 100000);
        builder.pop();

        builder.pop(); // physiques

        // ══════════════════════════════════════════════════════════════════
        //  FOUNDATION DAO
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Foundation Dao bonuses and requirements.")
               .push("foundationDao");

        builder.push("lifespanBonus");
        FOUNDATION_DAO_HUMAN_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Human Foundation Dao.").defineInRange("human", 30, 0, 100000);
        FOUNDATION_DAO_BLOOD_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Blood Foundation Dao.").defineInRange("blood", 60, 0, 100000);
        FOUNDATION_DAO_EARTH_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Earth Foundation Dao.").defineInRange("earth", 100, 0, 100000);
        FOUNDATION_DAO_HEAVEN_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Heaven Foundation Dao.").defineInRange("heaven", 150, 0, 100000);
        builder.pop();
        builder.push("spellMultipliers");
        FOUNDATION_DAO_EARTH_SPELL_DAMAGE_MULT = builder.comment("Spell damage multiplier for Earth Foundation Dao.").defineInRange("earthSpellDamageMult", 1.25, 0.0, 100.0);
        FOUNDATION_DAO_HEAVEN_SPELL_DAMAGE_MULT = builder.comment("Spell damage multiplier for Heaven Foundation Dao.").defineInRange("heavenSpellDamageMult", 1.5, 0.0, 100.0);
        FOUNDATION_DAO_EARTH_SPELL_QI_COST_MULT = builder.comment("Spell qi cost multiplier for Earth Foundation Dao.").defineInRange("earthSpellQiCostMult", 0.75, 0.0, 10.0);
        FOUNDATION_DAO_HEAVEN_SPELL_QI_COST_MULT = builder.comment("Spell qi cost multiplier for Heaven Foundation Dao.").defineInRange("heavenSpellQiCostMult", 0.5, 0.0, 10.0);
        builder.pop();
        FOUNDATION_DAO_BLOOD_HP_MULT = builder.comment("HP multiplier for Blood Foundation Dao.").defineInRange("bloodHpMult", 1.5, 0.0, 100.0);
        builder.push("bodyDefense");
        FOUNDATION_DAO_HUMAN_BODY_DEFENSE_BONUS = builder.comment("Body defense bonus for Human Foundation Dao.").defineInRange("human", 1, 0, 1000);
        FOUNDATION_DAO_BLOOD_BODY_DEFENSE_BONUS = builder.comment("Body defense bonus for Blood Foundation Dao.").defineInRange("blood", 4, 0, 1000);
        FOUNDATION_DAO_EARTH_BODY_DEFENSE_BONUS = builder.comment("Body defense bonus for Earth Foundation Dao.").defineInRange("earth", 3, 0, 1000);
        FOUNDATION_DAO_HEAVEN_BODY_DEFENSE_BONUS = builder.comment("Body defense bonus for Heaven Foundation Dao.").defineInRange("heaven", 5, 0, 1000);
        builder.pop();
        builder.push("cultivationEfficiency");
        FOUNDATION_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("earth", 2, 0, 1000);
        FOUNDATION_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("heaven", 3, 0, 1000);
        builder.pop();
        builder.push("qiRecovery");
        FOUNDATION_DAO_EARTH_QI_RECOVERY_BONUS = builder.defineInRange("earth", 2, 0, 1000);
        FOUNDATION_DAO_HEAVEN_QI_RECOVERY_BONUS = builder.defineInRange("heaven", 3, 0, 1000);
        builder.pop();
        FOUNDATION_DAO_BLOOD_MELEE_DAMAGE_BONUS = builder.comment("Melee damage bonus for Blood Foundation Dao.").defineInRange("bloodMeleeDamageBonus", 5, 0, 1000);
        FOUNDATION_DAO_HEAVEN_BONE_AGE_LIMIT = builder.comment("Max bone age for Heaven Foundation Dao. " +
                "Wired 2026-09-01 via CultivationProgressionRulesMixin, targeting the single \"bipush 21\" " +
                "eligibility-gate literal inside isEligibleFoundationDao(FoundationMaterialState, FoundationDao, " +
                "int) - lowering this raises how old (in bone age) a cultivator must be before the Heaven path " +
                "can be chosen at all.").defineInRange("heavenBoneAgeLimit", 21, 0, 200);
        builder.push("tribulationWaves");
        FOUNDATION_DAO_HEAVEN_TRIBULATION_WAVES = builder.comment("Tribulation waves for Heaven Foundation Dao. " +
                "Wired 2026-09-01 via CultivationProgressionRulesMixin, matching " +
                "CultivationProgressionRules.foundationTribulationWaves(FoundationDao) - only applies at the " +
                "Qi Refining (peak) -> Foundation Building breakthrough attempt, same as the base mod.").defineInRange("heaven", 3, 0, 20);
        FOUNDATION_DAO_EARTH_TRIBULATION_WAVES = builder.comment("Tribulation waves for Earth Foundation Dao. " +
                "Wired 2026-09-01, same method as the Heaven variant above.").defineInRange("earth", 1, 0, 20);
        builder.pop();

        builder.pop(); // foundationDao

        // ══════════════════════════════════════════════════════════════════
        //  GOLDEN CORE DAO
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Golden Core Dao bonuses and requirements.")
               .push("goldenCoreDao");

        builder.push("lifespanBonus");
        GOLDEN_CORE_DAO_HUMAN_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Human Golden Core Dao.").defineInRange("human", 80, 0, 100000);
        GOLDEN_CORE_DAO_BLOOD_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Blood Golden Core Dao.").defineInRange("blood", 120, 0, 100000);
        GOLDEN_CORE_DAO_EARTH_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Earth Golden Core Dao.").defineInRange("earth", 180, 0, 100000);
        GOLDEN_CORE_DAO_HEAVEN_LIFESPAN_BONUS = builder.comment("Lifespan bonus for Heaven Golden Core Dao.").defineInRange("heaven", 250, 0, 100000);
        builder.pop();
        builder.push("spellMultipliers");
        GOLDEN_CORE_DAO_EARTH_SPELL_DAMAGE_MULT = builder.defineInRange("earthSpellDamageMult", 1.5, 0.0, 100.0);
        GOLDEN_CORE_DAO_HEAVEN_SPELL_DAMAGE_MULT = builder.defineInRange("heavenSpellDamageMult", 2.0, 0.0, 100.0);
        GOLDEN_CORE_DAO_EARTH_SPELL_QI_COST_MULT = builder.defineInRange("earthSpellQiCostMult", 0.5, 0.0, 10.0);
        GOLDEN_CORE_DAO_HEAVEN_SPELL_QI_COST_MULT = builder.defineInRange("heavenSpellQiCostMult", 0.2, 0.0, 10.0);
        builder.pop();
        GOLDEN_CORE_DAO_BLOOD_HP_MULT = builder.comment("HP multiplier for Blood Golden Core Dao.").defineInRange("bloodHpMult", 2.0, 0.0, 100.0);
        GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_DAMAGE_MULT = builder.comment("Blood spell damage multiplier for Blood Golden Core Dao.").defineInRange("bloodBloodSpellDamageMult", 2.0, 0.0, 100.0);
        GOLDEN_CORE_DAO_BLOOD_BLOOD_SPELL_QI_COST_MULT = builder.comment("Blood spell qi cost multiplier for Blood Golden Core Dao.").defineInRange("bloodBloodSpellQiCostMult", 0.3, 0.0, 10.0);
        builder.push("tribulation");
        GOLDEN_CORE_DAO_HUMAN_TRIBULATION_STRIKES = builder.comment("Tribulation strikes for Human Golden Core Dao.").defineInRange("humanStrikes", 1, 0, 20);
        GOLDEN_CORE_DAO_BLOOD_TRIBULATION_STRIKES = builder.comment("Tribulation strikes for Blood Golden Core Dao.").defineInRange("bloodStrikes", 3, 0, 20);
        GOLDEN_CORE_DAO_EARTH_TRIBULATION_STRIKES = builder.comment("Tribulation strikes for Earth Golden Core Dao.").defineInRange("earthStrikes", 6, 0, 20);
        GOLDEN_CORE_DAO_HEAVEN_TRIBULATION_STRIKES = builder.comment("Tribulation strikes for Heaven Golden Core Dao.").defineInRange("heavenStrikes", 9, 0, 20);
        GOLDEN_CORE_DAO_TRIBULATION_DAMAGE = builder.comment("Tribulation damage for all Golden Core Dao types.").defineInRange("damage", 40, 0, 10000);
        builder.pop();
        GOLDEN_CORE_DAO_HEAVEN_BONE_AGE_LIMIT = builder.comment("Max bone age for Heaven Golden Core Dao. " +
                "Wired 2026-09-01, same technique as the Foundation Dao version of this setting - targets the " +
                "single \"bipush 60\" literal inside isEligibleGoldenCoreDao(...).").defineInRange("heavenBoneAgeLimit", 60, 0, 200);
        builder.push("shatterCoreTrial");
        GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_MAX_HEALTH = builder.comment("Max health for Human shatter core trial boss.").defineInRange("humanMaxHealth", 10000.0, 1.0, 1000000.0);
        GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_MAX_HEALTH = builder.comment("Max health for Blood shatter core trial boss.").defineInRange("bloodMaxHealth", 5000.0, 1.0, 1000000.0);
        GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_MAX_HEALTH = builder.comment("Max health for Earth shatter core trial boss.").defineInRange("earthMaxHealth", 2500.0, 1.0, 1000000.0);
        GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_MAX_HEALTH = builder.comment("Max health for Heaven shatter core trial boss.").defineInRange("heavenMaxHealth", 1000.0, 1.0, 1000000.0);
        GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REGEN = builder.comment("Regen per second for Human shatter core trial boss.").defineInRange("humanRegen", 100.0, 0.0, 100000.0);
        GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REGEN = builder.comment("Regen per second for Blood shatter core trial boss.").defineInRange("bloodRegen", 50.0, 0.0, 100000.0);
        GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REGEN = builder.comment("Regen per second for Earth shatter core trial boss.").defineInRange("earthRegen", 10.0, 0.0, 100000.0);
        GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REGEN = builder.comment("Regen per second for Heaven shatter core trial boss.").defineInRange("heavenRegen", 0.0, 0.0, 100000.0);

        builder.pop(); // shatterCoreTrial
        // (Historical note: this pop was once missing, which silently nested every
        // section from "identity" through "moralityBounds" under goldenCoreDao. Fixed -
        // verified balanced via a comment-stripped push/pop depth walk, final depth 0,
        // max depth 3. NOTE FOR FUTURE VERIFICATION SCRIPTS: strip `//` comments before
        // counting .push() / .pop() calls - an earlier draft of this very note contained the
        // literal text ".pop()" in prose, which fooled a naive regex-based checker into
        // reporting a false imbalance for the rest of the file.)

        // Missing GoldenCoreDao bonuses
        builder.push("bodyDefenseBonus");
        GOLDEN_CORE_DAO_HUMAN_BODY_DEFENSE_BONUS = builder.defineInRange("human", 0, 0, 10000);
        GOLDEN_CORE_DAO_BLOOD_BODY_DEFENSE_BONUS = builder.defineInRange("blood", 2, 0, 10000);
        GOLDEN_CORE_DAO_EARTH_BODY_DEFENSE_BONUS = builder.defineInRange("earth", 4, 0, 10000);
        GOLDEN_CORE_DAO_HEAVEN_BODY_DEFENSE_BONUS = builder.defineInRange("heaven", 6, 0, 10000);
        builder.pop();

        builder.push("cultivationEfficiencyBonus");
        GOLDEN_CORE_DAO_HUMAN_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("human", 0, 0, 10000);
        GOLDEN_CORE_DAO_BLOOD_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("blood", 0, 0, 10000);
        GOLDEN_CORE_DAO_EARTH_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("earth", 5, 0, 10000);
        GOLDEN_CORE_DAO_HEAVEN_CULTIVATION_EFFICIENCY_BONUS = builder.defineInRange("heaven", 10, 0, 10000);
        builder.pop();

        builder.push("qiRecoveryBonus");
        GOLDEN_CORE_DAO_HUMAN_QI_RECOVERY_BONUS = builder.defineInRange("human", 0, 0, 10000);
        GOLDEN_CORE_DAO_BLOOD_QI_RECOVERY_BONUS = builder.defineInRange("blood", 0, 0, 10000);
        GOLDEN_CORE_DAO_EARTH_QI_RECOVERY_BONUS = builder.defineInRange("earth", 2, 0, 10000);
        GOLDEN_CORE_DAO_HEAVEN_QI_RECOVERY_BONUS = builder.defineInRange("heaven", 5, 0, 10000);
        builder.pop();

        builder.push("meleeDamageBonus");
        GOLDEN_CORE_DAO_HUMAN_MELEE_DAMAGE_BONUS = builder.defineInRange("human", 0, 0, 10000);
        GOLDEN_CORE_DAO_BLOOD_MELEE_DAMAGE_BONUS = builder.defineInRange("blood", 4, 0, 10000);
        GOLDEN_CORE_DAO_EARTH_MELEE_DAMAGE_BONUS = builder.defineInRange("earth", 2, 0, 10000);
        GOLDEN_CORE_DAO_HEAVEN_MELEE_DAMAGE_BONUS = builder.defineInRange("heaven", 0, 0, 10000);
        builder.pop();

        builder.push("shatterTrialReflection");
        GOLDEN_CORE_DAO_HUMAN_SHATTER_TRIAL_REFLECTION = builder.defineInRange("human", 0.1, 0.0, 10.0);
        GOLDEN_CORE_DAO_BLOOD_SHATTER_TRIAL_REFLECTION = builder.defineInRange("blood", 0.2, 0.0, 10.0);
        GOLDEN_CORE_DAO_EARTH_SHATTER_TRIAL_REFLECTION = builder.defineInRange("earth", 0.3, 0.0, 10.0);
        GOLDEN_CORE_DAO_HEAVEN_SHATTER_TRIAL_REFLECTION = builder.defineInRange("heaven", 0.5, 0.0, 10.0);
        builder.pop();

        builder.pop(); // goldenCoreDao

        // ══════════════════════════════════════════════════════════════════
        //  IDENTITY — per-identity lifespan + starting items
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Per-identity lifespan ranges and starting items. Empty = use original mod default.")
               .push("identity");

        // Each identity has its own sub-section with lifespan min/max + starting items
        builder.push("lone_cultivator");
        IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN = builder.comment("Min starting lifespan for Lone Cultivator.").defineInRange("minLifespan", 80, 0, 10000);
        IDENTITY_LIFESPAN_LONE_CULTIVATOR_MAX = builder.comment("Max starting lifespan for Lone Cultivator.").defineInRange("maxLifespan", 105, 0, 10000);
        IDENTITY_STARTING_ITEMS_LONE_CULTIVATOR = builder.comment("Lone Cultivator starting items. Format: 'modid:item,count;modid:item,count'. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("merchant_son");
        IDENTITY_LIFESPAN_MERCHANT_SON_MIN = builder.comment("Min starting lifespan for Merchant Son.").defineInRange("minLifespan", 60, 0, 10000);
        IDENTITY_LIFESPAN_MERCHANT_SON_MAX = builder.comment("Max starting lifespan for Merchant Son.").defineInRange("maxLifespan", 85, 0, 10000);
        IDENTITY_STARTING_ITEMS_MERCHANT_SON = builder.comment("Merchant Son starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("bandit_leader");
        IDENTITY_LIFESPAN_BANDIT_LEADER_MIN = builder.comment("Min starting lifespan for Bandit Leader.").defineInRange("minLifespan", 90, 0, 10000);
        IDENTITY_LIFESPAN_BANDIT_LEADER_MAX = builder.comment("Max starting lifespan for Bandit Leader.").defineInRange("maxLifespan", 110, 0, 10000);
        IDENTITY_STARTING_ITEMS_BANDIT_LEADER = builder.comment("Bandit Leader starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("hunter");
        IDENTITY_LIFESPAN_HUNTER_MIN = builder.comment("Min starting lifespan for Hunter.").defineInRange("minLifespan", 90, 0, 10000);
        IDENTITY_LIFESPAN_HUNTER_MAX = builder.comment("Max starting lifespan for Hunter.").defineInRange("maxLifespan", 110, 0, 10000);
        IDENTITY_STARTING_ITEMS_HUNTER = builder.comment("Hunter starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("doctor_heir");
        IDENTITY_LIFESPAN_DOCTOR_HEIR_MIN = builder.comment("Min starting lifespan for Doctor Heir.").defineInRange("minLifespan", 80, 0, 10000);
        IDENTITY_LIFESPAN_DOCTOR_HEIR_MAX = builder.comment("Max starting lifespan for Doctor Heir.").defineInRange("maxLifespan", 105, 0, 10000);
        IDENTITY_STARTING_ITEMS_DOCTOR_HEIR = builder.comment("Doctor Heir starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("hermit_disciple");
        IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MIN = builder.comment("Min starting lifespan for Hermit Disciple.").defineInRange("minLifespan", 80, 0, 10000);
        IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MAX = builder.comment("Max starting lifespan for Hermit Disciple.").defineInRange("maxLifespan", 105, 0, 10000);
        IDENTITY_STARTING_ITEMS_HERMIT_DISCIPLE = builder.comment("Hermit Disciple starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("fisherman");
        IDENTITY_LIFESPAN_FISHERMAN_MIN = builder.comment("Min starting lifespan for Fisherman.").defineInRange("minLifespan", 70, 0, 10000);
        IDENTITY_LIFESPAN_FISHERMAN_MAX = builder.comment("Max starting lifespan for Fisherman.").defineInRange("maxLifespan", 100, 0, 10000);
        IDENTITY_STARTING_ITEMS_FISHERMAN = builder.comment("Fisherman starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("farmer");
        IDENTITY_LIFESPAN_FARMER_MIN = builder.comment("Min starting lifespan for Farmer.").defineInRange("minLifespan", 70, 0, 10000);
        IDENTITY_LIFESPAN_FARMER_MAX = builder.comment("Max starting lifespan for Farmer.").defineInRange("maxLifespan", 100, 0, 10000);
        IDENTITY_STARTING_ITEMS_FARMER = builder.comment("Farmer starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("abandoned_infant");
        IDENTITY_LIFESPAN_ABANDONED_INFANT_MIN = builder.comment("Min starting lifespan for Abandoned Infant.").defineInRange("minLifespan", 55, 0, 10000);
        IDENTITY_LIFESPAN_ABANDONED_INFANT_MAX = builder.comment("Max starting lifespan for Abandoned Infant.").defineInRange("maxLifespan", 90, 0, 10000);
        IDENTITY_STARTING_ITEMS_ABANDONED_INFANT = builder.comment("Abandoned Infant starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("general_son");
        IDENTITY_LIFESPAN_GENERAL_SON_MIN = builder.comment("Min starting lifespan for General Son.").defineInRange("minLifespan", 90, 0, 10000);
        IDENTITY_LIFESPAN_GENERAL_SON_MAX = builder.comment("Max starting lifespan for General Son.").defineInRange("maxLifespan", 110, 0, 10000);
        IDENTITY_STARTING_ITEMS_GENERAL_SON = builder.comment("General Son starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("exiled_princess");
        IDENTITY_LIFESPAN_EXILED_PRINCESS_MIN = builder.comment("Min starting lifespan for Exiled Princess.").defineInRange("minLifespan", 60, 0, 10000);
        IDENTITY_LIFESPAN_EXILED_PRINCESS_MAX = builder.comment("Max starting lifespan for Exiled Princess.").defineInRange("maxLifespan", 85, 0, 10000);
        IDENTITY_STARTING_ITEMS_EXILED_PRINCESS = builder.comment("Exiled Princess starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("pirate");
        IDENTITY_LIFESPAN_PIRATE_MIN = builder.comment("Min starting lifespan for Pirate.").defineInRange("minLifespan", 90, 0, 10000);
        IDENTITY_LIFESPAN_PIRATE_MAX = builder.comment("Max starting lifespan for Pirate.").defineInRange("maxLifespan", 110, 0, 10000);
        IDENTITY_STARTING_ITEMS_PIRATE = builder.comment("Pirate starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("beast_descendant");
        IDENTITY_LIFESPAN_BEAST_DESCENDANT_MIN = builder.comment("Min starting lifespan for Beast Descendant.").defineInRange("minLifespan", 90, 0, 10000);
        IDENTITY_LIFESPAN_BEAST_DESCENDANT_MAX = builder.comment("Max starting lifespan for Beast Descendant.").defineInRange("maxLifespan", 110, 0, 10000);
        IDENTITY_STARTING_ITEMS_BEAST_DESCENDANT = builder.comment("Beast Descendant starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("taoist");
        IDENTITY_LIFESPAN_TAOIST_MIN = builder.comment("Min starting lifespan for Taoist.").defineInRange("minLifespan", 60, 0, 10000);
        IDENTITY_LIFESPAN_TAOIST_MAX = builder.comment("Max starting lifespan for Taoist.").defineInRange("maxLifespan", 85, 0, 10000);
        IDENTITY_STARTING_ITEMS_TAOIST = builder.comment("Taoist starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("monk");
        IDENTITY_LIFESPAN_MONK_MIN = builder.comment("Min starting lifespan for Monk.").defineInRange("minLifespan", 80, 0, 10000);
        IDENTITY_LIFESPAN_MONK_MAX = builder.comment("Max starting lifespan for Monk.").defineInRange("maxLifespan", 105, 0, 10000);
        IDENTITY_STARTING_ITEMS_MONK = builder.comment("Monk starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("academy_student");
        IDENTITY_LIFESPAN_ACADEMY_STUDENT_MIN = builder.comment("Min starting lifespan for Academy Student.").defineInRange("minLifespan", 60, 0, 10000);
        IDENTITY_LIFESPAN_ACADEMY_STUDENT_MAX = builder.comment("Max starting lifespan for Academy Student.").defineInRange("maxLifespan", 85, 0, 10000);
        IDENTITY_STARTING_ITEMS_ACADEMY_STUDENT = builder.comment("Academy Student starting items. Empty = use original mod default.").define("startingItems", "");
        builder.pop();

        builder.push("default");
        IDENTITY_LIFESPAN_DEFAULT_MIN = builder.comment("Default min starting lifespan for custom/unknown identities.").defineInRange("minLifespan", 70, 0, 10000);
        IDENTITY_LIFESPAN_DEFAULT_MAX = builder.comment("Default max starting lifespan for custom/unknown identities.").defineInRange("maxLifespan", 100, 0, 10000);
        IDENTITY_STARTING_ITEMS_DEFAULT = builder.comment("Default starting items for custom/unknown identities.").define("startingItems", "");
        builder.pop();

        // Custom identities support
        builder.push("custom");
        IDENTITY_CUSTOM_IDENTITIES = builder.comment("Custom identities created by the user. Format: 'id1:Display Name 1;id2:Display Name 2'").define("identities", "");
        IDENTITY_CUSTOM_STARTING_ITEMS = builder.comment("Starting items for custom identities. Format: 'id1:modid:item,count;modid:item,count|id2:modid:item,count'").define("items", "");
        builder.pop(); // custom

        builder.pop(); // identity

        // ══════════════════════════════════════════════════════════════════
        //  PROGRESSION
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Cultivation progression requirements and NPC progression.")
               .push("progression");

        PROGRESSION_FOUNDATION_HEAVEN_BONE_AGE_LIMIT = builder.comment("Max bone age for Heaven Foundation Dao. " +
                "This duplicates FOUNDATION_DAO_HEAVEN_BONE_AGE_LIMIT in the foundationDao section above (same " +
                "default, same real game value) - verified (2026-09-01) both names point at the exact same " +
                "single bytecode literal, so only ONE of the two can safely own the live override (two mixins " +
                "fighting over one injection point would make whichever ran last silently win). The " +
                "foundationDao-section field is the one actually wired; this one is kept for backward " +
                "compatibility but has no independent effect - edit the other one instead.").defineInRange("foundationHeavenBoneAgeLimit", 21, 0, 200);
        PROGRESSION_GOLDEN_CORE_HEAVEN_BONE_AGE_LIMIT = builder.comment("Max bone age for Heaven Golden Core Dao. " +
                "Same duplicate situation as the Foundation version above - GOLDEN_CORE_DAO_HEAVEN_BONE_AGE_LIMIT " +
                "in the goldenCoreDao section is the one actually wired.").defineInRange("goldenCoreHeavenBoneAgeLimit", 60, 0, 200);
        PROGRESSION_FOUNDATION_HEAVEN_ESTIMATE_DAYS = builder.comment("Estimated days shown for the Heaven " +
                "Foundation route. Wired 2026-09-01 - bytecode note: estimateHigherFoundationRouteDays(dao) " +
                "actually returns 0 for HEAVEN (already the top path, nothing higher to estimate), 30 for EARTH, " +
                "and 12 for everything else. This field's default (30) matches the EARTH case, not HEAVEN's real " +
                "value (0) - its name is misleading in the base mod. Wired to the EARTH branch so the field " +
                "means what its own default already implied; HEAVEN's real 0 isn't user-configurable (there's " +
                "nothing to tune - it's just \"you're already there\").").defineInRange("foundationHeavenEstimateDays", 30, 0, 10000);
        PROGRESSION_FOUNDATION_EARTH_ESTIMATE_DAYS = builder.comment("Estimated days shown for every Foundation " +
                "route other than Heaven or Earth (i.e. Human/Blood/none). Wired 2026-09-01 to the real \"else\" " +
                "branch (12), which is what this field's default already matched - same mislabeling as above, " +
                "the real EARTH-specific value (30) is controlled by the HEAVEN field instead. Tooltip corrected " +
                "to describe what it actually controls rather than what its name implies.").defineInRange("foundationEarthEstimateDays", 12, 0, 10000);
        PROGRESSION_GOLDEN_CORE_HEAVEN_ESTIMATE_DAYS = builder.comment("Estimated days shown for the Heaven " +
                "Golden Core route. Wired 2026-09-01 - same mislabeling pattern as the Foundation version: " +
                "estimateHigherGoldenCoreRouteDays returns 0 for HEAVEN, 60 for EARTH, 24 for everything else. " +
                "This field (default 60) is wired to the real EARTH branch, matching its own default.").defineInRange("goldenCoreHeavenEstimateDays", 60, 0, 10000);
        PROGRESSION_GOLDEN_CORE_EARTH_ESTIMATE_DAYS = builder.comment("Estimated days shown for every Golden " +
                "Core route other than Heaven or Earth. Wired 2026-09-01 to the real \"else\" branch (24), " +
                "matching this field's own default.").defineInRange("goldenCoreEarthEstimateDays", 24, 0, 10000);
        PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE = builder.comment("Chance for NPC to die on tribulation failure. " +
                "Wired 2026-09-01 via SectSavedDataMixin, targeting SectSavedData.shouldNpcTribulationDeath - a " +
                "real per-NPC-tribulation-failure roll, not cosmetic.").defineInRange("npcTribulationDeathChance", 0.02, 0.0, 1.0);
        PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS = builder.comment("Days of weakness after NPC tribulation " +
                "failure. Wired 2026-09-01, same mixin, targeting SectSavedData.failMemberTribulation.").defineInRange("npcTribulationWeaknessDays", 3, 0, 365);
        PROGRESSION_GENDER_EDITS_DEFAULT = builder.comment("Default number of gender edits allowed. " +
                "Wired 2026-09-01 via CultivationDataMixin - two sites needed patching: the constructor's " +
                "initial value, and deserializeNBT's fallback default used when loading a save from before this " +
                "field existed on a character (ordinal 0 of two \"5\" literals in that method - the other is " +
                "unrelated, verified not touched).").defineInRange("genderEditsDefault", 5, 0, 1000);

        builder.pop(); // progression

        // ══════════════════════════════════════════════════════════════════
        //  NPC COMBAT
        // ══════════════════════════════════════════════════════════════════
        builder.comment("NPC combat tactics, dodge profiles, and threat detection.")
               .push("npcCombat");

        NPC_COMBAT_HARD_DODGE_CAP = builder.comment("Maximum dodge chance cap (0.7 = 70%).").defineInRange("hardDodgeCap", 0.7, 0.0, 1.0);
        NPC_COMBAT_PROJECTILE_SCAN_RADIUS = builder.comment("Radius to scan for incoming projectiles. " +
                "Wired 2026-09-01 via NpcCombatThreatDetectorMixin - governs the whole threat-candidate AABB " +
                "scan (projectile/collision/area/tnt threats alike, not just projectiles - the base mod's own " +
                "name for this radius is a bit narrower than what it actually does).").defineInRange("projectileScanRadius", 13.0, 1.0, 200.0);
        NPC_COMBAT_MAX_CANDIDATES = builder.comment("Max threat candidates per scan. " +
                "Wired 2026-09-01, same mixin/method as the radius above.").defineInRange("maxCandidates", 48, 1, 500);
        NPC_COMBAT_STALEMATE_TIMEOUT = builder.comment("Ticks before an NPC retreats from a stalemate combat (no progress). " +
                "1200 = 60 seconds. 0 = disabled (NPCs never retreat from stalemate). " +
                "Set this to prevent hour-long battles where neither side can damage the other. " +
                "Verified (2026-09-01) via full javap search of NpcCombatThreatDetector, NpcCombatTactics, " +
                "WanderingCultivatorEntity and every AI class in the entity/npc package, plus a jar-wide grep for " +
                "\"stalemate\" (zero hits): the base mod has no stalemate-detection or forced-retreat-on-no-progress " +
                "mechanic at all, in any form, so there is nothing here to override - a mixin would have to add " +
                "wholly new logic rather than intercept an existing check. Not currently wired; not a bug, just " +
                "a feature this config mod would need to build from scratch.").defineInRange("stalemateTimeout", 1200, 0, 12000);
        builder.push("dodgeChance");
        NPC_COMBAT_DODGE_MORTAL = builder.comment("Dodge chance for Mortal NPCs.").defineInRange("mortal", 0.12, 0.0, 1.0);
        NPC_COMBAT_DODGE_QI_REFINING = builder.comment("Dodge chance for Qi Refining NPCs.").defineInRange("qiRefining", 0.22, 0.0, 1.0);
        NPC_COMBAT_DODGE_FOUNDATION = builder.comment("Dodge chance for Foundation Building NPCs.").defineInRange("foundation", 0.34, 0.0, 1.0);
        NPC_COMBAT_DODGE_GOLDEN_CORE = builder.comment("Dodge chance for Golden Core NPCs.").defineInRange("goldenCore", 0.44, 0.0, 1.0);
        NPC_COMBAT_DODGE_NASCENT_SOUL = builder.comment("Dodge chance for Nascent Soul NPCs.").defineInRange("nascentSoul", 0.54, 0.0, 1.0);
        NPC_COMBAT_DODGE_SOUL_FORMATION = builder.comment("Dodge chance for Soul Formation NPCs.").defineInRange("soulFormation", 0.62, 0.0, 1.0);
        NPC_COMBAT_DODGE_VOID_REFINING = builder.comment("Dodge chance for Void Refining NPCs.").defineInRange("voidRefining", 0.67, 0.0, 1.0);
        NPC_COMBAT_DODGE_HIGHER = builder.comment("Dodge chance for higher realm NPCs.").defineInRange("higher", 0.7, 0.0, 1.0);
        builder.pop();
        builder.push("scanTicks");
        NPC_COMBAT_SCAN_TICKS_MORTAL = builder.comment("Scan ticks for Mortal NPCs (how often they scan for threats).").defineInRange("mortal", 12, 1, 200);
        NPC_COMBAT_SCAN_TICKS_QI_REFINING = builder.comment("Scan ticks for Qi Refining NPCs.").defineInRange("qiRefining", 10, 1, 200);
        NPC_COMBAT_SCAN_TICKS_FOUNDATION = builder.comment("Scan ticks for Foundation Building NPCs.").defineInRange("foundation", 8, 1, 200);
        NPC_COMBAT_SCAN_TICKS_GOLDEN_CORE = builder.comment("Scan ticks for Golden Core NPCs.").defineInRange("goldenCore", 7, 1, 200);
        NPC_COMBAT_SCAN_TICKS_NASCENT_SOUL = builder.comment("Scan ticks for Nascent Soul NPCs.").defineInRange("nascentSoul", 6, 1, 200);
        NPC_COMBAT_SCAN_TICKS_SOUL_FORMATION = builder.comment("Scan ticks for Soul Formation NPCs.").defineInRange("soulFormation", 5, 1, 200);
        NPC_COMBAT_SCAN_TICKS_VOID_REFINING = builder.comment("Scan ticks for Void Refining NPCs.").defineInRange("voidRefining", 4, 1, 200);
        NPC_COMBAT_SCAN_TICKS_HIGHER = builder.comment("Scan ticks for higher realm NPCs.").defineInRange("higher", 4, 1, 200);
        builder.pop();
        builder.push("reactionTicks");
        NPC_COMBAT_REACTION_TICKS_MORTAL = builder.comment("Reaction ticks for Mortal NPCs (delay before responding to a threat).").defineInRange("mortal", 10, 1, 200);
        NPC_COMBAT_REACTION_TICKS_QI_REFINING = builder.comment("Reaction ticks for Qi Refining NPCs.").defineInRange("qiRefining", 8, 1, 200);
        NPC_COMBAT_REACTION_TICKS_FOUNDATION = builder.comment("Reaction ticks for Foundation Building NPCs.").defineInRange("foundation", 6, 1, 200);
        NPC_COMBAT_REACTION_TICKS_GOLDEN_CORE = builder.comment("Reaction ticks for Golden Core NPCs.").defineInRange("goldenCore", 5, 1, 200);
        NPC_COMBAT_REACTION_TICKS_NASCENT_SOUL = builder.comment("Reaction ticks for Nascent Soul NPCs.").defineInRange("nascentSoul", 4, 1, 200);
        NPC_COMBAT_REACTION_TICKS_SOUL_FORMATION = builder.comment("Reaction ticks for Soul Formation NPCs.").defineInRange("soulFormation", 3, 1, 200);
        NPC_COMBAT_REACTION_TICKS_VOID_REFINING = builder.comment("Reaction ticks for Void Refining NPCs.").defineInRange("voidRefining", 2, 1, 200);
        NPC_COMBAT_REACTION_TICKS_HIGHER = builder.comment("Reaction ticks for higher realm NPCs.").defineInRange("higher", 2, 1, 200);
        builder.pop();
        builder.push("dodgeCooldown");
        NPC_COMBAT_DODGE_COOLDOWN_MORTAL = builder.comment("Dodge cooldown for Mortal NPCs (ticks between dodges).").defineInRange("mortal", 24, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_QI_REFINING = builder.comment("Dodge cooldown for Qi Refining NPCs.").defineInRange("qiRefining", 22, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_FOUNDATION = builder.comment("Dodge cooldown for Foundation Building NPCs.").defineInRange("foundation", 20, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_GOLDEN_CORE = builder.comment("Dodge cooldown for Golden Core NPCs.").defineInRange("goldenCore", 18, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_NASCENT_SOUL = builder.comment("Dodge cooldown for Nascent Soul NPCs.").defineInRange("nascentSoul", 17, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_SOUL_FORMATION = builder.comment("Dodge cooldown for Soul Formation NPCs.").defineInRange("soulFormation", 16, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_VOID_REFINING = builder.comment("Dodge cooldown for Void Refining NPCs.").defineInRange("voidRefining", 15, 1, 200);
        NPC_COMBAT_DODGE_COOLDOWN_HIGHER = builder.comment("Dodge cooldown for higher realm NPCs.").defineInRange("higher", 14, 1, 200);
        builder.pop();

        builder.pop(); // npcCombat

        // ══════════════════════════════════════════════════════════════════
        //  FORMATIONS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Formation power, qi gathering, growth, and barrier values.")
               .push("formations");

        builder.push("coreMaxQi");
        FORMATION_CORE_MAX_QI_LOW = builder.comment("Max qi for LOW formation core.").defineInRange("low", 100L, 1L, Long.MAX_VALUE);
        FORMATION_CORE_MAX_QI_MID = builder.defineInRange("mid", 1000L, 1L, Long.MAX_VALUE);
        FORMATION_CORE_MAX_QI_HIGH = builder.defineInRange("high", 10000L, 1L, Long.MAX_VALUE);
        FORMATION_CORE_MAX_QI_SUPREME = builder.defineInRange("supreme", 100000L, 1L, Long.MAX_VALUE);
        FORMATION_CORE_MAX_QI_IMMORTAL = builder.defineInRange("immortal", 1000000L, 1L, Long.MAX_VALUE);
        builder.pop();
        builder.push("qiGathering");
        FORMATION_QI_GATHERING_MULT_LOW = builder.comment("Qi gathering multiplier for LOW tier.").defineInRange("low", 2.0, 0.0, 1000.0);
        FORMATION_QI_GATHERING_MULT_MID = builder.defineInRange("mid", 2.5, 0.0, 1000.0);
        FORMATION_QI_GATHERING_MULT_HIGH = builder.defineInRange("high", 3.0, 0.0, 1000.0);
        FORMATION_QI_GATHERING_MULT_SUPREME = builder.defineInRange("supreme", 4.0, 0.0, 1000.0);
        FORMATION_QI_GATHERING_MULT_IMMORTAL = builder.defineInRange("immortal", 5.0, 0.0, 1000.0);
        FORMATION_QI_GATHERING_MAX_MULT = builder.comment("Max qi multiplier for qi gathering formations.").defineInRange("maxMult", 1.5, 0.0, 100.0);
        builder.pop();
        builder.push("growth");
        FORMATION_GROWTH_MULT_LOW = builder.comment("Growth multiplier for LOW tier growth formations.").defineInRange("low", 2.0, 0.0, 1000.0);
        FORMATION_GROWTH_MULT_MID = builder.defineInRange("mid", 4.0, 0.0, 1000.0);
        FORMATION_GROWTH_MULT_HIGH = builder.defineInRange("high", 6.0, 0.0, 1000.0);
        FORMATION_GROWTH_MULT_SUPREME = builder.defineInRange("supreme", 8.0, 0.0, 1000.0);
        FORMATION_GROWTH_MULT_IMMORTAL = builder.defineInRange("immortal", 10.0, 0.0, 1000.0);
        builder.pop();
        builder.push("qiPerDamage");
        FORMATION_QI_PER_DAMAGE_LOW = builder.comment("Qi per damage for LOW tier barrier formations.").defineInRange("low", 100L, 1L, Long.MAX_VALUE);
        FORMATION_QI_PER_DAMAGE_MID = builder.defineInRange("mid", 50L, 1L, Long.MAX_VALUE);
        FORMATION_QI_PER_DAMAGE_HIGH = builder.defineInRange("high", 20L, 1L, Long.MAX_VALUE);
        FORMATION_QI_PER_DAMAGE_SUPREME = builder.defineInRange("supreme", 5L, 1L, Long.MAX_VALUE);
        FORMATION_QI_PER_DAMAGE_IMMORTAL = builder.defineInRange("immortal", 1L, 1L, Long.MAX_VALUE);
        builder.pop();
        FORMATION_BARRIER_DAMAGE_IMMORTAL = builder.comment("Barrier damage per second for IMMORTAL tier.").defineInRange("barrierDamageImmortal", 20.0, 0.0, 10000.0);
        builder.push("rejuvenationAmplifier");
        FORMATION_REJUVENATION_AMP_LOW = builder.defineInRange("low", 0, 0, 10);
        FORMATION_REJUVENATION_AMP_MID = builder.defineInRange("mid", 1, 0, 10);
        FORMATION_REJUVENATION_AMP_HIGH = builder.defineInRange("high", 2, 0, 10);
        FORMATION_REJUVENATION_AMP_SUPREME = builder.defineInRange("supreme", 3, 0, 10);
        FORMATION_REJUVENATION_AMP_IMMORTAL = builder.defineInRange("immortal", 4, 0, 10);
        builder.pop();
        builder.push("harvestInterval");
        FORMATION_HARVEST_INTERVAL_LOW = builder.comment("Harvest interval in ticks for LOW tier.").defineInRange("low", 1200, 1, 6000);
        FORMATION_HARVEST_INTERVAL_MID = builder.defineInRange("mid", 600, 1, 6000);
        FORMATION_HARVEST_INTERVAL_HIGH = builder.defineInRange("high", 200, 1, 6000);
        FORMATION_HARVEST_INTERVAL_SUPREME = builder.defineInRange("supreme", 20, 1, 6000);
        FORMATION_HARVEST_BATCH_SIZE_IMMORTAL = builder.comment("Harvest batch size for IMMORTAL tier.").defineInRange("immortalBatchSize", 10, 1, 256);
        builder.pop();

        builder.pop(); // formations

        // ══════════════════════════════════════════════════════════════════
        //  SECTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect generation, ambient interactions, and shop pricing.")
               .push("sects");

        builder.push("generation");
        // Duplicate field, found 2026-09-01: this is the same concept as
        // SECT_PROFILE_MAX_POWER_SCORE (same key "maxPowerScore", same
        // default) - only the profile variant maps to a real field
        // (GeneratedSectCultivationProfile.MAX_POWER_SCORE), so it is the
        // one wired (see SectAncestorProfileMixin). This one is left as-is,
        // matching this project's established handling of other duplicate
        // fields found during the audit.
        SECT_MAX_POWER_SCORE = builder.comment("Maximum sect power score (0-N). NOTE: duplicate of sectProfile.maxPowerScore - this copy is unwired, has no effect.").defineInRange("maxPowerScore", 4, 0, 20);
        SECT_SETTLEMENT_CELL_SPAWN_CHANCE = builder.comment("Chance per cell for a sect settlement to generate. " +
                "1.0 = always, 0.1 = 10%. Default is 50% higher than the original mod (0.34 -> 0.51).").defineInRange("cellSpawnChance", 0.51, 0.0, 1.0);
        builder.pop();
        builder.push("ancestorChances");
        SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_0 = builder.comment("Ancestor immortal chance for power 0 sects.").defineInRange("power0", 0.02, 0.0, 1.0);
        SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_1 = builder.defineInRange("power1", 0.05, 0.0, 1.0);
        SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_2 = builder.defineInRange("power2", 0.12, 0.0, 1.0);
        SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_3 = builder.defineInRange("power3", 0.24, 0.0, 1.0);
        SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_4 = builder.defineInRange("power4", 0.38, 0.0, 1.0);
        SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE = builder.comment("Chance for ancestor to be a loose immortal.").defineInRange("looseImmortal", 0.24, 0.0, 1.0);
        builder.pop();
        builder.push("ambient");
        // NOTE (2026-09-01): maxScenes/minCooldownTicks/maxCooldownTicks
        // below are genuine duplicates of SECT_AMBIENT_MAX_ACTIVE_SCENES_
        // PER_LEVEL / _MIN_SECT_COOLDOWN_TICKS / _MAX_SECT_COOLDOWN_TICKS
        // further down (same titles, same defaults) - only the latter set
        // is wired, by the pre-existing SectAmbientInteractionRulesMixin.
        // maxSpectators' real consumer (addSparringSpectators) builds a
        // fixed 4-element hardcoded offset array, not a simple comparison -
        // structurally unwireable without rewriting the placement algorithm;
        // default already matches (4). npcCooldownTicks' literal (3600)
        // does not appear anywhere in SectSavedData.class - real consumer
        // not located this session; default already matches (3600). See
        // SectAmbientCheckIntervalMixin's class doc for the full trace.
        SECT_AMBIENT_MAX_SCENES = builder.comment("Max concurrent ambient scenes per level.", "NOTE: duplicate of sectAmbient.maxActiveScenesPerLevel - this copy is unwired, has no effect.").defineInRange("maxScenes", 2, 0, 20);
        SECT_AMBIENT_MAX_SPECTATORS = builder.comment("Max spectators per scene.", "NOTE: real consumer builds a fixed 4-element array, not a simple constant - currently unwired, has no effect.").defineInRange("maxSpectators", 4, 0, 50);
        SECT_AMBIENT_MIN_COOLDOWN_TICKS = builder.comment("Min cooldown between sect interactions.", "NOTE: duplicate of sectAmbient.minSectCooldownTicks - this copy is unwired, has no effect.").defineInRange("minCooldownTicks", 1800, 0, 60000);
        SECT_AMBIENT_MAX_COOLDOWN_TICKS = builder.comment("Max cooldown between sect interactions.", "NOTE: duplicate of sectAmbient.maxSectCooldownTicks - this copy is unwired, has no effect.").defineInRange("maxCooldownTicks", 4200, 0, 60000);
        SECT_AMBIENT_NPC_COOLDOWN_TICKS = builder.comment("NPC individual cooldown.", "NOTE: real backing literal not located anywhere in SectSavedData.class this session - currently unwired, has no effect.").defineInRange("npcCooldownTicks", 3600, 0, 60000);
        builder.pop();
        builder.push("shop");
        SECT_SHOP_SELL_PERCENT = builder.comment("Sell price as percentage of buy price.").defineInRange("sellPercent", 60, 0, 200);
        builder.push("techniquePrices");
        SECT_SHOP_TECHNIQUE_PRICE_LOW = builder.comment("Price for LOW tier technique.").defineInRange("low", 100, 0, 10000000);
        SECT_SHOP_TECHNIQUE_PRICE_MID = builder.defineInRange("mid", 1000, 0, 10000000);
        SECT_SHOP_TECHNIQUE_PRICE_HIGH = builder.defineInRange("high", 10000, 0, 10000000);
        SECT_SHOP_TECHNIQUE_PRICE_SUPREME = builder.defineInRange("supreme", 80000, 0, 10000000);
        SECT_SHOP_TECHNIQUE_PRICE_IMMORTAL = builder.defineInRange("immortal", 320000, 0, 10000000);
        builder.pop();
        builder.push("spellPrices");
        SECT_SHOP_SPELL_PRICE_LOW = builder.defineInRange("low", 50, 0, 10000000);
        SECT_SHOP_SPELL_PRICE_MID = builder.defineInRange("mid", 500, 0, 10000000);
        SECT_SHOP_SPELL_PRICE_HIGH = builder.defineInRange("high", 5000, 0, 10000000);
        SECT_SHOP_SPELL_PRICE_SUPREME = builder.defineInRange("supreme", 30000, 0, 10000000);
        SECT_SHOP_SPELL_PRICE_IMMORTAL = builder.defineInRange("immortal", 160000, 0, 10000000);
        builder.pop();
        builder.push("weaponPrices");
        SECT_SHOP_WEAPON_PRICE_LOW = builder.defineInRange("low", 200, 0, 10000000);
        SECT_SHOP_WEAPON_PRICE_MID = builder.defineInRange("mid", 1000, 0, 10000000);
        SECT_SHOP_WEAPON_PRICE_HIGH = builder.defineInRange("high", 5000, 0, 10000000);
        SECT_SHOP_WEAPON_PRICE_SUPREME = builder.defineInRange("supreme", 40000, 0, 10000000);
        SECT_SHOP_WEAPON_PRICE_IMMORTAL = builder.defineInRange("immortal", 160000, 0, 10000000);
        builder.pop();
        builder.pop();
        builder.push("tasks");
        SECT_TASK_MAX_REQUIRED_COUNT = builder.comment("Max items per task.").defineInRange("maxRequiredCount", 256, 1, 10000);
        SECT_TASK_MAX_SYSTEM_PURCHASES = builder.comment("Max system purchase tasks.").defineInRange("maxSystemPurchases", 8, 0, 100);
        SECT_TASK_EXPEDITION_MIN_DAYS = builder.comment("Min expedition duration in days.").defineInRange("expeditionMinDays", 0.5, 0.0, 365.0);
        SECT_TASK_EXPEDITION_MAX_DAYS = builder.comment("Max expedition duration in days.").defineInRange("expeditionMaxDays", 2.0, 0.0, 365.0);
        builder.pop(); // tasks

        // ── Sect Size Tiers ──
        builder.push("sizeTiers");
        builder.comment("Spawn chance for each sect size tier.",
                "Higher value = more likely to appear. All values are relative to each other.",
                "Example: tier0=5 and tier12=1 means humble cottages are 5x more common than grand immortal sects.",
                "Set a tier's chance to 0 to prevent it from spawning entirely.");
        SECT_SIZE_SPAWN_CHANCE_0 = builder.comment("Spawn chance for Humble Cottage (tier 0 - smallest).").defineInRange("tier0SpawnChance", 5.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_1 = builder.comment("Spawn chance for Minor Gathering (tier 1).").defineInRange("tier1SpawnChance", 8.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_2 = builder.comment("Spawn chance for Small Sect (tier 2).").defineInRange("tier2SpawnChance", 10.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_3 = builder.comment("Spawn chance for Modest Sect (tier 3).").defineInRange("tier3SpawnChance", 10.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_4 = builder.comment("Spawn chance for Established Sect (tier 4).").defineInRange("tier4SpawnChance", 9.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_5 = builder.comment("Spawn chance for Prominent Sect (tier 5).").defineInRange("tier5SpawnChance", 8.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_6 = builder.comment("Spawn chance for Renowned Sect (tier 6).").defineInRange("tier6SpawnChance", 7.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_7 = builder.comment("Spawn chance for Great Sect (tier 7).").defineInRange("tier7SpawnChance", 6.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_8 = builder.comment("Spawn chance for Ancient Sect (tier 8).").defineInRange("tier8SpawnChance", 5.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_9 = builder.comment("Spawn chance for Supreme Sect (tier 9).").defineInRange("tier9SpawnChance", 4.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_10 = builder.comment("Spawn chance for Legendary Sect (tier 10).").defineInRange("tier10SpawnChance", 3.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_11 = builder.comment("Spawn chance for Mythic Sect (tier 11).").defineInRange("tier11SpawnChance", 2.0, 0.0, 100.0);
        SECT_SIZE_SPAWN_CHANCE_12 = builder.comment("Spawn chance for Grand Immortal Sect (tier 12 - largest).").defineInRange("tier12SpawnChance", 1.0, 0.0, 100.0);
        SECT_BUILDING_COUNT_MULTIPLIER = builder.comment("Building count multiplier per tier above 2. " +
                "Each tier above 2 multiplies the number of buildings. " +
                "0.5 = tier 5 gets 1.5x buildings, tier 12 gets 6.0x buildings. " +
                "Higher = grand sects have more buildings, lower = they stay compact.").defineInRange("buildingCountMultiplier", 0.5, 0.0, 5.0);
        // NOTE (2026-09-01): unwired - its real consumer would live somewhere
        // in SectSettlementFeature.class and its 30+ nested classes (worldgen
        // placement code, 16,800+ lines disassembled for the outer class
        // alone), and a search of the outer class's own bytecode for a
        // spacing/min-distance concept came back empty. Deprioritized as a
        // costly needle-in-haystack rather than guessed at, same treatment as
        // the SECT_AMBIENT_* fields elsewhere in this audit.
        SECT_MIN_SPACING_PER_TIER = builder.comment("Minimum distance between sects, per tier. " +
                "Two sects must be at least (tier1 + tier2 + 2) * this value blocks apart. " +
                "200 = two small sects need 400 blocks, two grand sects need 5200 blocks. " +
                "Higher = sects are more spread out, lower = sects can be closer together.",
                "NOTE: real backing consumer not located this session - currently unwired, has no effect.").defineInRange("minSpacingPerTier", 200, 50, 2000);
        SECT_SAFE_TICK = builder.comment("Safety net around the original mod's sect-infrastructure-backfill " +
                "tick (SectSettlementFeature.tickSectIBackfill), the one call in DeferredSectNpcSpawner." +
                "onServerTick() with no exception handling of its own, matching a real crash pattern seen with " +
                "larger sects. CORRECTED (2026-09-01): this tooltip previously described an OLDER version of " +
                "this mixin that cancelled sect NPC spawning entirely whenever this was true - that is no " +
                "longer what this setting does. Verified via javap -p -c -s against onServerTick's own bytecode " +
                "that steps 2 (sect chest/barrel loot) and 4 (NPC spawn/repair) already have their own " +
                "catch-and-reschedule handling in the base mod and are never touched by this setting - sect NPCs " +
                "spawn and repair normally either way. When true (default), only the infrastructure-backfill " +
                "step is wrapped in try/catch and silently retried next tick on failure. Set to false only to " +
                "confirm whether a crash is really coming from this exact call (the tradeoff being that step " +
                "runs unprotected).").define("safeTick", true);
        builder.pop(); // sizeTiers

        // ── Crouch Meditation ──
        builder.push("crouchMeditation");
        ENABLE_CROUCH_MEDITATION = builder.comment("Enable crouch-meditation keybind (default: G key while crouching). " +
                "When enabled, players can crouch + press G to meditate without a cushion. " +
                "Crouch-meditation gives the same cultivation progress but with a lower multiplier.").define("enable", true);
        CROUCH_MEDITATION_MULT = builder.comment("Cultivation multiplier for crouch-meditation (vs 10x for cushion). " +
                "3.0 = crouch meditation gives 3x the passive cultivation. " +
                "Lower than cushion meditation (10x) to reward using a cushion.").defineInRange("cultivationMultiplier", 3.0, 0.1, 100.0);
        CROUCH_MEDITATION_QI_MULT = builder.comment("Qi drain multiplier for crouch-meditation (vs 10x for cushion). " +
                "3.0 = crouch meditation absorbs Qi 3x faster. " +
                "Lower than cushion meditation (10x) to reward using a cushion.").defineInRange("qiDrainMultiplier", 3.0, 0.1, 100.0);
        builder.pop(); // crouchMeditation

        // ── NPC Population ──
        builder.push("npcPopulation");
        WANDERING_CULTIVATOR_SPAWN_MULTIPLIER = builder.comment("Multiplier for wandering cultivator spawn rate. " +
                "2.0 = double the spawn rate, 0.5 = half. Higher = more NPCs in the world.").defineInRange("wanderingCultivatorSpawnMultiplier", 1.5, 0.0, 10.0);
        builder.pop(); // npcPopulation

        builder.pop(); // sects

        // ══════════════════════════════════════════════════════════════════
        //  LOOT
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Chest loot drop weights and roll counts.")
               .push("loot");

        builder.push("rolls");
        LOOT_RUINED_VANILLA_ROLLS_MIN = builder.comment("Min vanilla item rolls in ruined chests.").defineInRange("ruinedVanillaMin", 2, 0, 100);
        LOOT_RUINED_VANILLA_ROLLS_MAX = builder.comment("Max vanilla item rolls in ruined chests.").defineInRange("ruinedVanillaMax", 4, 0, 100);
        LOOT_COMPLETE_VANILLA_ROLLS_MIN = builder.comment("Min vanilla item rolls in complete chests.").defineInRange("completeVanillaMin", 3, 0, 100);
        LOOT_COMPLETE_VANILLA_ROLLS_MAX = builder.comment("Max vanilla item rolls in complete chests.").defineInRange("completeVanillaMax", 6, 0, 100);
        LOOT_RUINED_CULTIVATION_ROLLS_MAX = builder.comment("Max cultivation item rolls in ruined chests.").defineInRange("ruinedCultivationMax", 3, 0, 100);
        LOOT_COMPLETE_CULTIVATION_ROLLS_MIN = builder.comment("Min cultivation item rolls in complete chests.").defineInRange("completeCultivationMin", 1, 0, 100);
        LOOT_COMPLETE_CULTIVATION_ROLLS_MAX = builder.comment("Max cultivation item rolls in complete chests.").defineInRange("completeCultivationMax", 3, 0, 100);
        builder.pop();
        builder.push("weights");
        LOOT_LOW_SPIRIT_STONE_WEIGHT = builder.comment("Loot weight for low spirit stones.").defineInRange("lowSpiritStone", 12, 0, 1000);
        LOOT_MID_SPIRIT_STONE_WEIGHT = builder.comment("Loot weight for mid spirit stones.").defineInRange("midSpiritStone", 6, 0, 1000);
        LOOT_HIGH_SPIRIT_STONE_WEIGHT = builder.comment("Loot weight for high spirit stones.").defineInRange("highSpiritStone", 3, 0, 1000);
        LOOT_SUPREME_SPIRIT_STONE_WEIGHT = builder.comment("Loot weight for supreme spirit stones.").defineInRange("supremeSpiritStone", 1, 0, 1000);
        LOOT_HERB_WEIGHT = builder.comment("Loot weight for herbs.").defineInRange("herb", 8, 0, 1000);
        LOOT_TECHNIQUE_FRAGMENT_WEIGHT = builder.comment("Loot weight for technique fragments.").defineInRange("techniqueFragment", 5, 0, 1000);
        LOOT_ZHUJI_DAN_WEIGHT_RUINED = builder.comment("Loot weight for Zhuji Dan in ruined chests.").defineInRange("zhujiDanRuined", 2, 0, 1000);
        LOOT_ZHUJI_DAN_WEIGHT_COMPLETE = builder.comment("Loot weight for Zhuji Dan in complete chests.").defineInRange("zhujiDanComplete", 4, 0, 1000);
        builder.pop();

        builder.pop(); // loot

        // ══════════════════════════════════════════════════════════════════
        //  TRIALS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Heart demon trial and inner world trial parameters.")
               .push("trials");

        builder.push("heartDemon");
        TRIAL_HEART_DEMON_GREAT_RIGHTEOUS_VITALITY_MULT = builder.comment("Vitality multiplier for great righteous heart demon.").defineInRange("greatRighteousVitalityMult", 0.5, 0.0, 100.0);
        TRIAL_HEART_DEMON_RIGHTEOUS_VITALITY_MULT = builder.comment("Vitality multiplier for righteous heart demon.").defineInRange("righteousVitalityMult", 0.75, 0.0, 100.0);
        TRIAL_HEART_DEMON_NEUTRAL_VITALITY_MULT = builder.comment("Vitality multiplier for neutral heart demon.").defineInRange("neutralVitalityMult", 1.0, 0.0, 100.0);
        TRIAL_HEART_DEMON_EVIL_VITALITY_MULT = builder.comment("Vitality multiplier for evil heart demon.").defineInRange("evilVitalityMult", 1.25, 0.0, 100.0);
        TRIAL_HEART_DEMON_GREAT_EVIL_VITALITY_MULT = builder.comment("Vitality multiplier for great evil heart demon.").defineInRange("greatEvilVitalityMult", 1.5, 0.0, 100.0);
        builder.pop();
        builder.push("innerWorld");
        TRIAL_INNER_WORLD_PLATFORM_DIAMETER = builder.comment("Platform diameter in blocks for inner world trial.").defineInRange("platformDiameter", 48, 1, 500);
        TRIAL_INNER_WORLD_PLATFORM_Y = builder.comment("Platform Y level for inner world trial.").defineInRange("platformY", 80, 0, 320);
        TRIAL_INNER_WORLD_SOUL_WOUND_TICKS = builder.comment("Soul wound duration in ticks.").defineInRange("soulWoundTicks", 1200, 0, 60000);
        TRIAL_INNER_WORLD_FAILURE_HEALTH_PENALTY_PERCENT = builder.comment("Health penalty percentage on trial failure (0.5 = 50%).").defineInRange("failureHealthPenaltyPercent", 0.5, 0.0, 1.0);
        // NOTE (2026-09-01): real backing field lives on the unrelated
        // TimeStasisHandler class (a general ability handler, not trial-
        // exclusive), not on InnerWorldTrialManager - fixed by
        // TimeStasisDurationMixin after an earlier version of this wiring
        // silently targeted the wrong class. Changing this affects Time
        // Stasis duration everywhere it's cast, not just inside trials.
        TRIAL_INNER_WORLD_TIME_STASIS_DURATION = builder.comment("Time stasis duration in ticks.").defineInRange("timeStasisDuration", 600, 0, 60000);
        builder.pop();

        builder.pop(); // trials

        // ══════════════════════════════════════════════════════════════════
        //  QI SYSTEM
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Player qi consumer, qi shield, and block qi specs.")
               .push("qiSystem");

        builder.push("playerConsumer");
        QI_SYSTEM_ATTRACTION_RADIUS = builder.comment("Base qi attraction radius for players. Wired 2026-09-01 " +
                "via PlayerQiConsumerMixin, targeting PlayerQiConsumer.attractRadius()'s single 14.0 literal " +
                "(BASE_ATTRACTION_RADIUS is a compile-time-inlined constant, not a live field).").defineInRange("attractionRadius", 14.0, 1.0, 200.0);
        QI_SYSTEM_MEDITATION_RANGE_BONUS = builder.comment("Meditation range bonus. Wired 2026-09-01, same mixin, " +
                "same method, the separate 10.0 literal added while meditating.").defineInRange("meditationRangeBonus", 10.0, 0.0, 200.0);
        QI_SYSTEM_MEDITATION_EFFICIENCY_BONUS = builder.comment("Meditation efficiency bonus. Wired 2026-09-01, " +
                "same mixin, targeting the distinct 10.0 literal in finalAbsorbMultiplier() (same numeric value " +
                "as meditationRangeBonus but a different constant in a different method).").defineInRange("meditationEfficiencyBonus", 10.0, 0.0, 200.0);
        builder.pop();
        builder.push("qiShield");
        QI_SHIELD_QI_PER_DAMAGE = builder.comment("Qi required per point of damage absorbed by qi shield. Wired " +
                "2026-09-01 via QiShieldHandlerMixin, targeting both qiPerDamage(ServerPlayer) and " +
                "qiPerDamage(WanderingCultivatorEntity)'s single 10.0 literal each.").defineInRange("qiPerDamage", 10L, 1L, 100000L);
        QI_SHIELD_PERFECT_REDUCTION = builder.comment("Fraction of an incoming hit a \"perfect\" qi shield can " +
                "absorb (1.0 = 100%, vanilla behavior). Wired 2026-09-01 via QiShieldHandlerMixin, redirecting " +
                "the Math.max(0, incomingDamage) call in maxAbsorbableDamage(...) that makes the whole hit " +
                "eligible for absorption when grantsPerfectQiShield(...) is true; still gated by available qi " +
                "exactly as before. See QiShieldHandlerMixin's class doc for the full trace.")
                .defineInRange("perfectReduction", 1.0, 0.0, 1.0);
        builder.pop();
        builder.push("spiritStoneOre");
        QI_STONE_ORE_MAX_QI_LOW = builder.comment("Max qi for low spirit stone ore.").defineInRange("maxQiLow", 2000L, 1L, Long.MAX_VALUE);
        QI_STONE_ORE_MAX_QI_MID = builder.comment("Max qi for mid spirit stone ore.").defineInRange("maxQiMid", 4000L, 1L, Long.MAX_VALUE);
        QI_STONE_ORE_MAX_QI_HIGH = builder.comment("Max qi for high spirit stone ore.").defineInRange("maxQiHigh", 8000L, 1L, Long.MAX_VALUE);
        QI_STONE_ORE_MAX_QI_SUPREME = builder.comment("Max qi for supreme spirit stone ore.").defineInRange("maxQiSupreme", 20000L, 1L, Long.MAX_VALUE);
        QI_STONE_ORE_MAX_QI_SPIRIT_VEIN_SPRING = builder.comment("Max qi for spirit vein spring.").defineInRange("maxQiSpring", 50000L, 1L, Long.MAX_VALUE);
        QI_STONE_ORE_REGEN_LOW = builder.comment("Qi regen for low spirit stone ore.").defineInRange("regenLow", 5.0, 0.0, 10000.0);
        QI_STONE_ORE_REGEN_MID = builder.comment("Qi regen for mid spirit stone ore.").defineInRange("regenMid", 10.0, 0.0, 10000.0);
        QI_STONE_ORE_REGEN_HIGH = builder.comment("Qi regen for high spirit stone ore.").defineInRange("regenHigh", 20.0, 0.0, 10000.0);
        QI_STONE_ORE_REGEN_SUPREME = builder.comment("Qi regen for supreme spirit stone ore.").defineInRange("regenSupreme", 50.0, 0.0, 10000.0);
        QI_STONE_ORE_REGEN_SPRING = builder.comment("Qi regen for spirit vein spring.").defineInRange("regenSpring", 80.0, 0.0, 10000.0);
        builder.pop();

        builder.pop(); // qiSystem

        // ══════════════════════════════════════════════════════════════════
        //  PASSIVE SPELLS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Passive spell intervals, costs, and effect values.")
               .push("passiveSpells");

        builder.push("slowRegen");
        PASSIVE_SLOW_REGEN_INTERVAL = builder.comment("Interval in ticks for slow regeneration.").defineInRange("interval", 100, 1, 6000);
        PASSIVE_SLOW_REGEN_QI_COST = builder.comment("Qi cost per slow regen tick.").defineInRange("qiCost", 5L, 0L, 100000L);
        builder.pop();
        builder.push("bigu");
        PASSIVE_BIGU_INTERVAL = builder.comment("Interval in ticks for bigu (fasting) passive.").defineInRange("interval", 1200, 1, 60000);
        PASSIVE_BIGU_QI_COST = builder.comment("Qi cost per bigu tick.").defineInRange("qiCost", 10L, 0L, 100000L);
        PASSIVE_BIGU_SATURATION = builder.comment("Saturation restored per bigu tick.").defineInRange("saturation", 5.0, 0.0, 1000.0);
        builder.pop();
        builder.push("qiMending");
        PASSIVE_QI_MENDING_INTERVAL = builder.comment("Interval in ticks for qi mending.").defineInRange("interval", 20, 1, 6000);
        PASSIVE_QI_MENDING_QI_PER_DURABILITY = builder.comment("Qi cost per durability point mended.").defineInRange("qiPerDurability", 1L, 0L, 100000L);
        builder.pop();
        builder.push("qiFlight");
        PASSIVE_QI_FLIGHT_DRAIN_INTERVAL = builder.comment("Interval in ticks for qi flight drain.").defineInRange("drainInterval", 20, 1, 6000);
        PASSIVE_QI_FLIGHT_DRAIN_PER_SECOND = builder.comment("Qi drained per second for qi flight.").defineInRange("drainPerSecond", 25L, 0L, 100000L);
        PASSIVE_QI_FLIGHT_BASE_SPEED = builder.comment("Base flying speed for qi flight.").defineInRange("baseSpeed", 0.05, 0.0, 10.0);
        builder.pop();
        builder.push("itemAttraction");
        PASSIVE_ITEM_ATTRACTION_RADIUS = builder.comment("Radius for item attraction passive.").defineInRange("radius", 20.0, 0.0, 200.0);
        PASSIVE_ITEM_ATTRACTION_QI_PER_ITEM = builder.comment("Qi cost per item attracted per second.").defineInRange("qiPerItem", 1L, 0L, 100000L);
        builder.pop();
        builder.push("treasureSeizing");
        PASSIVE_TREASURE_SEIZING_RADIUS = builder.comment("Radius for treasure seizing passive.").defineInRange("radius", 20.0, 0.0, 200.0);
        PASSIVE_TREASURE_SEIZING_QI_PER_STACK = builder.comment("Qi cost per stack seized.").defineInRange("qiPerStack", 5L, 0L, 100000L);
        PASSIVE_TREASURE_SEIZING_STACKS_PER_SECOND = builder.comment("Stacks seized per second.").defineInRange("stacksPerSecond", 3, 0, 1000);
        builder.pop();

        builder.pop(); // passiveSpells

        // ══════════════════════════════════════════════════════════════════
        //  EFFECTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Status effect values (buffs and debuffs).")
               .push("effects");

        builder.push("bloodBerserk");
        EFFECT_BLOOD_BERSERK_ATTACK_SPEED_MULT = builder.comment("Attack speed multiplier for Blood Berserk effect.").defineInRange("attackSpeedMult", 0.2, -10.0, 10.0);
        EFFECT_BLOOD_BERSERK_MOVE_SPEED_MULT = builder.comment("Movement speed multiplier for Blood Berserk effect.").defineInRange("moveSpeedMult", 0.2, -10.0, 10.0);
        builder.pop();
        builder.push("daoHeartWound");
        EFFECT_DAO_HEART_WOUND_ATTACK_PENALTY = builder.comment("Attack damage penalty for Dao Heart Wound.").defineInRange("attackPenalty", -4.0, -1000.0, 0.0);
        EFFECT_DAO_HEART_WOUND_MOVE_SPEED_PENALTY = builder.comment("Movement speed penalty for Dao Heart Wound.").defineInRange("moveSpeedPenalty", -0.15, -10.0, 0.0);
        builder.pop();
        builder.push("shatterArmor");
        EFFECT_SHATTER_ARMOR_ARMOR_PENALTY = builder.comment("Armor penalty for Shatter Armor effect.").defineInRange("armorPenalty", -0.99, -10.0, 0.0);
        EFFECT_SHATTER_ARMOR_TOUGHNESS_PENALTY = builder.comment("Toughness penalty for Shatter Armor effect.").defineInRange("toughnessPenalty", -0.99, -10.0, 0.0);
        builder.pop();
        builder.push("inverseFiveElements");
        // Wired 2026-09-01 via PhysiqueBonusHelperMixin (configExt$inverseMarkDurationTicksLong
        // / configExt$inverseMarkDurationTicksInt): the real target is PhysiqueBonusHelper.class,
        // not the empty InverseFiveElementsEffect marker MobEffect. "600" appears exactly 3 times
        // in the whole class - all inside lambda$onSpellCast$1 - twice as "long 600l" (stack-timeout
        // and mark-expiry refresh) and once as "sipush 600" (the MobEffectInstance duration arg).
        EFFECT_INVERSE_MARK_DURATION_TICKS = builder.comment("Duration in ticks for inverse five elements mark.").defineInRange("markDurationTicks", 600, 0, 60000);
        // Wired 2026-09-01 via PhysiqueBonusHelperMixin (configExt$inverseBaseFiveElementDmgMult):
        // applySharedSpellDamageRules(Physique,Spell,D) - sole 1.1d in that method, gated on
        // INVERSE_FIVE_ELEMENTS_BODY && isBasicFiveElementSpell.
        EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT = builder.comment("Base five element damage multiplier for inverse effect.").defineInRange("baseDmgMult", 1.1, 0.0, 100.0);
        // Wired 2026-09-01 via PhysiqueBonusHelperMixin (configExt$inverseBaseFiveElementCostMult):
        // spellQiCostMultiplier(Physique,Spell) - sole 0.9d in that overload (explicit descriptor
        // used since the method is overloaded with spellQiCostMultiplier(Player,Spell)).
        EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT = builder.comment("Base five element cost multiplier for inverse effect.").defineInRange("baseCostMult", 0.9, 0.0, 10.0);
        // Wired 2026-09-01 via PhysiqueBonusHelperMixin (configExt$inverseStackDamagePerLayer):
        // applyPlayerOnlySpellDamageRules(Player,Physique,Spell,D) - sole 0.25d in that method,
        // formula mult *= (1.0 + stacks * 0.25).
        EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER = builder.comment("Damage bonus per stack layer for inverse effect.").defineInRange("stackDamagePerLayer", 0.25, 0.0, 100.0);
        // Wired 2026-09-01 via PhysiqueBonusHelperMixin (configExt$inverseStackCostReductionPerLayer):
        // spellQiCostMultiplier(Player,Spell) - sole 0.25d in that overload, formula
        // mult *= Math.max(0.0, 1.0 - stacks * 0.25). Shares its constant-pool index with the
        // 0.25d above (both trace to one named constant in the original source reused for two
        // purposes) but is scoped to its own method here so the two config fields tune independently.
        EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER = builder.comment("Cost reduction per stack layer for inverse effect.").defineInRange("stackCostReductionPerLayer", 0.25, 0.0, 100.0);
        builder.pop();

        builder.pop(); // effects

        // ══════════════════════════════════════════════════════════════════
        //  MORALITY
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Morality thresholds and tribulation damage scaling.")
               .push("morality");

        MORALITY_NEUTRAL_MIN = builder.comment("Minimum morality value for neutral alignment.").defineInRange("neutralMin", -50, -1000000, 1000000);
        MORALITY_NEUTRAL_MAX = builder.comment("Maximum morality value for neutral alignment.").defineInRange("neutralMax", 50, -1000000, 1000000);
        MORALITY_RIGHTEOUS_MIN = builder.comment("Minimum morality for righteous alignment.").defineInRange("righteousMin", 51, -1000000, 1000000);
        MORALITY_EVIL_MAX = builder.comment("Maximum morality for evil alignment (negative).").defineInRange("evilMax", -51, -1000000, 1000000);
        MORALITY_GREAT_RIGHTEOUS_MIN = builder.comment("Minimum morality for great righteous alignment.").defineInRange("greatRighteousMin", 100, -1000000, 1000000);
        MORALITY_GREAT_EVIL_MAX = builder.comment("Maximum morality for great evil alignment (negative).").defineInRange("greatEvilMax", -100, -1000000, 1000000);
        MORALITY_TRIBULATION_DAMAGE_COEFFICIENT = builder.comment("Coefficient for tribulation damage scaling based on abs(morality). Formula: 1.0 + abs(morality) * this.").defineInRange("tribulationDamageCoefficient", 0.001, 0.0, 1.0);
        MORALITY_TRIBULATION_DAMAGE_MAX = builder.comment("Maximum tribulation damage multiplier from morality.").defineInRange("tribulationDamageMax", 1000.0, 1.0, 100000.0);

        builder.pop(); // morality

        // ══════════════════════════════════════════════════════════════════
        //  LIFESPAN HELPER
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Lifespan helper constants for bone age and aging.")
               .push("lifespanHelper");

        LIFESPAN_START_BONE_AGE_MIN = builder.comment("Minimum starting bone age. Wired 2026-09-01 via " +
                "IdentityDrawHandlerMixin (both the base mod's own standard-identity draw, targeted directly " +
                "via bytecode, and this mod's own custom-identity code paths, which previously hardcoded " +
                "14 + RNG.nextInt(5) and ignored this field entirely).").defineInRange("startBoneAgeMin", 14, 0, 200);
        LIFESPAN_START_BONE_AGE_MAX = builder.comment("Maximum starting bone age. Wired 2026-09-01, same mixin " +
                "as startBoneAgeMin.").defineInRange("startBoneAgeMax", 18, 0, 200);
        LIFESPAN_AGE_PER_DAY = builder.comment("Age progression per in-game day. Wired 2026-09-01 via " +
                "LifespanHandlerMixin, targeting LifespanHandler.advanceBoneAge's single \"dconst_1\" rate " +
                "constant.").defineInRange("agePerDay", 1.0, 0.0, 100.0);
        LIFESPAN_AGE_PER_DAY_MEDITATING = builder.comment("Age progression per day while meditating. NOT WIRED: " +
                "an exhaustive search of LifespanHandler and LifespanHelper for any meditation-specific aging " +
                "logic (2026-09-01) found none - the base mod ages players at the same rate whether meditating " +
                "or not. This field currently has no effect.").defineInRange("agePerDayMeditating", 1.0, 0.0, 100.0);
        LIFESPAN_NEAR_IMMORTAL_THRESHOLD = builder.comment("Threshold for near-immortal aging slowdown.").defineInRange("nearImmortalThreshold", 10000, 0, 1000000);
        LIFESPAN_ORDINARY_DEATH_PENALTY_YEARS = builder.comment("Years of lifespan lost on ordinary death.").defineInRange("ordinaryDeathPenaltyYears", 1, 0, 10000);

        builder.pop(); // lifespanHelper

        // ══════════════════════════════════════════════════════════════════
        //  SECT LIFE & POPULATION
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect life simulation, population, recruitment, and warehouse limits.")
               .push("sectLife");

        SECT_LIFE_TICK_INTERVAL = builder.comment("How often sect life ticks (in ticks).").defineInRange("lifeTickInterval", 20, 1, 6000);
        SECT_FULL_SIMULATION_PLAYER_RADIUS = builder.comment(
                "Radius around player where full sect simulation runs (blocks).",
                "WARNING (2026-09-01 server-performance triage): verified via javap across the",
                "entire base-mod jar that the real constant this field was meant to wire",
                "(SectSavedData.FULL_SIMULATION_PLAYER_RADIUS = 192) has ZERO consumers anywhere",
                "in the compiled base mod - it is dead code in xiaoxiang_cultivation itself, not",
                "just unwired here. This config field is currently NOT connected to anything;",
                "changing it has no effect on server performance. Left in place only for",
                "config-file backward compatibility. See CONFIG_AUDIT.md 'Bug #3' for the full",
                "investigation. (Note this field's default of 64 never matched the real",
                "constant's 192 either - it appears to have been a placeholder guess.)"
        ).defineInRange("fullSimulationPlayerRadius", 64, 16, 1024);
        // Defaults below corrected 2026-09-01 against real values verified via
        // javap -p -c -s (SectSavedData.class / SectSavedData$SectRecord.class)
        // - see SectOperationsLimitsMixin/SectRecordEventLimitMixin class docs.
        SECT_DISTANT_CATCH_UP_DAY_CAP = builder.comment("Max days of catch-up simulation for distant sects.").defineInRange("distantCatchUpDayCap", 300, 1, 365);
        SECT_WAREHOUSE_SLOT_LIMIT = builder.comment("Max slots in sect warehouse.").defineInRange("warehouseSlotLimit", 324, 9, 512);
        SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT = builder.comment("Max personal inventory slots per sect member.").defineInRange("memberPersonalInventorySlotLimit", 18, 9, 256);
        SECT_EVENT_LIMIT = builder.comment("Max events recorded per sect.").defineInRange("eventLimit", 128, 10, 10000);
        SECT_PERFORMANCE_QUEUE_LIMIT = builder.comment("Max queued performances per sect.").defineInRange("performanceQueueLimit", 8, 1, 100);
        SECT_PERFORMANCE_TIMEOUT_TICKS = builder.comment("Timeout for performances in ticks.", "NOTE: real consumer not located this session - currently unwired, has no effect.").defineInRange("performanceTimeoutTicks", 200, 20, 6000);
        SECT_PERFORMANCE_PLAYER_RADIUS = builder.comment(
                "Radius for performance visibility (blocks).",
                "Verified via javap and now wired to a real, 7-times-used base-mod constant",
                "(SectSavedData's hasNearbyPlayer(..., 48.0) gate on death/recruitment/",
                "expedition/tribulation 'performance' events - pathfinding, animation, corpse",
                "spawns). Lowering this reduces visible-work cost from distant NPC sect events",
                "without touching the underlying sect simulation itself. See CONFIG_AUDIT.md",
                "'Bug #3' for the full investigation and the list of methods this now controls."
        ).defineInRange("performancePlayerRadius", 48, 8, 512);
        SECT_POPULATION_FLOOR_INITIAL_SCALE = builder.comment("Initial population scaling factor.").defineInRange("populationFloorInitialScale", 0.4, 0.1, 10.0);
        SECT_ELDER_DISCIPLE_TARGET = builder.comment("Target disciple count per elder.").defineInRange("elderDiscipleTarget", 3, 0, 50);
        SECT_LOW_POPULATION_RECRUIT_CHANCE = builder.comment("Recruit chance when population is low (0.5 = 50%).").defineInRange("lowPopulationRecruitChance", 0.85, 0.0, 1.0);
        SECT_ELDER_RECRUIT_CHANCE = builder.comment("Base recruit chance for elders (0.1 = 10%).").defineInRange("elderRecruitChance", 0.45, 0.0, 1.0);
        SECT_SERVANT_APPRENTICESHIP_CHANCE = builder.comment("Chance for servant to become apprentice (0.05 = 5%).").defineInRange("servantApprenticeshipChance", 0.35, 0.0, 1.0);
        // Default corrected 2026-09-01 (was 0.1, real value verified via
        // javap is 0.3 - createDataLayerRecruit's sole 0.3d literal) - see
        // SectRecruitChanceMixin's class doc.
        SECT_RECRUIT_DISCIPLE_CHANCE = builder.comment("Base disciple recruit chance (0.1 = 10%).").defineInRange("recruitDiscipleChance", 0.3, 0.0, 1.0);
        SECT_DISCIPLE_REALM_GATE = builder.comment("Minimum realm ordinal to become a disciple.").defineInRange("discipleRealmGate", 2, 0, 12);
        SECT_DISCIPLE_SUB_STAGE_GATE = builder.comment("Minimum sub-stage ordinal for discipleship.").defineInRange("discipleSubStageGate", 0, 0, 4);

        builder.pop(); // sectLife

        // ══════════════════════════════════════════════════════════════════
        //  SECT JOURNEYS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect NPC journey/expedition parameters.")
               .push("sectJourney");

        // Default corrected 2026-09-01 (was 2, real value verified via javap
        // is 3 - admitPhysicalJourney's iconst_3 gate) - see
        // SectOperationsLimitsMixin's class doc.
        SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT = builder.comment("Max concurrent physical journeys per sect.").defineInRange("maxPhysicalJourneysPerSect", 3, 0, 20);
        SECT_JOURNEY_CHUNK_RADIUS = builder.comment(
                "Chunk loading radius for journeys.",
                "NOT currently wired to the base mod (investigated 2026-09-01): the real",
                "SectJourneyChunkTickets.RADIUS=1 is baked into raw loop-bound bytecode",
                "(iconst_m1/iconst_1), not a clean getstatic target a mixin can safely retarget.",
                "The chunk force/release lifecycle (ForgeChunkManager-backed) was inspected and",
                "found to already be well-balanced (more release call sites than move call",
                "sites, no obvious leak) - left untouched rather than risk chunk-loading",
                "corruption. This field's default of 2 also does not match the real value of 1.",
                "See CONFIG_AUDIT.md 'Bug #3' for details."
        ).defineInRange("chunkRadius", 2, 0, 16);
        // Defaults below corrected 2026-09-01 against real values verified
        // via javap -p -c -s (SectSavedData.class) - see
        // SectJourneyTimingMixin's class doc for the full trace of which
        // JourneyState timestamp field each literal is measured against.
        SECT_JOURNEY_STUCK_TICKS = builder.comment("Ticks before journey NPC is considered stuck.").defineInRange("stuckTicks", 600, 20, 6000);
        SECT_JOURNEY_RETURN_FALLBACK_TICKS = builder.comment("Fallback timeout for return journey.").defineInRange("returnFallbackTicks", 1200, 100, 12000);
        SECT_JOURNEY_ENTITY_MISSING_GRACE_TICKS = builder.comment("Grace period for missing journey entity.").defineInRange("entityMissingGraceTicks", 200, 0, 6000);
        SECT_JOURNEY_DATA_PHASE_TICKS = builder.comment("Data phase duration in ticks.").defineInRange("dataPhaseTicks", 100, 1, 600);
        SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS = builder.comment("Wait for entity reload in ticks.").defineInRange("entityReloadWaitTicks", 100, 1, 600);
        SECT_JOURNEY_QUEUE_FALLBACK_TICKS = builder.comment("Queue fallback timeout in ticks.").defineInRange("queueFallbackTicks", 36000, 10, 60000);

        builder.pop(); // sectJourney

        // ══════════════════════════════════════════════════════════════════
        //  SECT DEFENSE
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect defense response parameters.")
               .push("sectDefense");

        // NOTE (verified via javap -p -c -s against SectSavedData.class,
        // 2026-09-01): escapeRadius/criticalResponderLimit/deathResponderLimit
        // below are unwired - the real base-mod fields they'd map to
        // (DEFENSE_ESCAPE_RADIUS, DEFENSE_CRITICAL_RESPONDER_LIMIT,
        // DEFENSE_DEATH_RESPONDER_LIMIT) have zero getstatic use sites
        // anywhere in the jar, and the actual responder cap is a hardcoded
        // literal (2 or 3) clamped again to max 3 inside
        // DefenseIncident.create() - the configured 5/8 could never take
        // effect even if wired. See SectDefenseMixin's class doc for the
        // full trace. Only criticalHealthRatio has a real, verified consumer
        // (a literal 0.25d comparison in handleSectMemberAttacked) and is
        // wired by SectDefenseMixin.
        SECT_DEFENSE_ESCAPE_RADIUS = builder.comment("Radius for defense escape (blocks).", "NOTE: real backing field has no consumer in the base mod - currently unwired, has no effect.").defineInRange("escapeRadius", 32.0, 1.0, 512.0);
        SECT_DEFENSE_CRITICAL_HEALTH_RATIO = builder.comment("Health ratio for critical defense response (0.3 = 30%).").defineInRange("criticalHealthRatio", 0.25, 0.0, 1.0);
        SECT_DEFENSE_CRITICAL_RESPONDER_LIMIT = builder.comment("Max responders for critical threat.", "NOTE: real responder cap is a hardcoded literal, clamped to max 3 regardless - currently unwired, has no effect.").defineInRange("criticalResponderLimit", 5, 0, 100);
        SECT_DEFENSE_DEATH_RESPONDER_LIMIT = builder.comment("Max responders for sect member death.", "NOTE: real responder cap is a hardcoded literal, clamped to max 3 regardless - currently unwired, has no effect.").defineInRange("deathResponderLimit", 8, 0, 100);

        builder.pop(); // sectDefense

        // ══════════════════════════════════════════════════════════════════
        //  SECT DEPARTMENT SHIFTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect department work shift schedules (in-game time, 0-24000).")
               .push("sectSchedule");

        // Defaults corrected 2026-09-01 (were 6000/12000/12000/18000, never
        // matching the real base-mod values below) when this field was wired
        // for the first time - verified via javap -p -c -s against
        // SectSavedData.departmentShiftScheduled(UUID, long): shift 0 is
        // [1000, 6000), shift 1 is [6000, 11000), each member's shift chosen
        // by Math.floorMod(member.hashCode(), 2).
        SECT_DEPARTMENT_FIRST_SHIFT_START = builder.comment("First shift start time (in-game time).").defineInRange("firstShiftStart", 1000, 0, 24000);
        SECT_DEPARTMENT_FIRST_SHIFT_END = builder.comment("First shift end time.").defineInRange("firstShiftEnd", 6000, 0, 24000);
        SECT_DEPARTMENT_SECOND_SHIFT_START = builder.comment("Second shift start time.").defineInRange("secondShiftStart", 6000, 0, 24000);
        SECT_DEPARTMENT_SECOND_SHIFT_END = builder.comment("Second shift end time.").defineInRange("secondShiftEnd", 11000, 0, 24000);
        SECT_SCHEDULE_DAY_TICKS = builder.comment("Ticks per sect day (usually 24000 = 1 Minecraft day).").defineInRange("dayTicks", 24000, 1000, 60000);
        SECT_SCHEDULE_MORNING_END_EXCLUSIVE = builder.comment("When morning phase ends (exclusive, in-game time).").defineInRange("morningEndExclusive", 1000, 0, 24000);
        SECT_SCHEDULE_NIGHT_START = builder.comment("When night phase starts (in-game time).").defineInRange("nightStart", 12000, 0, 24000);

        builder.pop(); // sectSchedule

        // ══════════════════════════════════════════════════════════════════
        //  SECT TASKS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect task market additional parameters (journey timeouts, escrow).")
               .push("sectTasks");

        // MAX_ESCROW_STACKS/JOURNEY_MIN/MAX_TIMEOUT_TICKS defaults corrected
        // 2026-09-01 (were 16/1200/4800) - verified via javap -v /-c against
        // SectTaskMarketRules.class real ConstantValues and journeyTimeoutTicks()'s
        // body (12000 base + up to 24000 more by severity = 12000-36000 range).
        // MAX_ESCROW_STACKS' own consuming call site was not isolated this
        // session (not referenced within SectTaskMarketRules itself, so it is
        // read from elsewhere, likely SectSavedData's task-escrow logic) -
        // still unwired, default corrected for honesty regardless.
        SECT_TASK_MAX_ESCROW_STACKS = builder.comment("Max escrow stacks per task.").defineInRange("maxEscrowStacks", 8, 1, 256);
        SECT_TASK_MAX_SYSTEM_PURCHASE_TASKS = builder.comment("Max system purchase tasks.").defineInRange("maxSystemPurchaseTasks", 8, 0, 100);
        SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS = builder.comment("Minimum journey timeout in ticks.").defineInRange("journeyMinTimeoutTicks", 12000, 20, 60000);
        SECT_TASK_JOURNEY_MAX_TIMEOUT_TICKS = builder.comment("Maximum journey timeout in ticks.").defineInRange("journeyMaxTimeoutTicks", 36000, 20, 60000);

        builder.pop(); // sectTasks

        // ══════════════════════════════════════════════════════════════════
        //  SECT DEPARTMENTS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect department work output and resource parameters.")
               .push("sectDepartments");

        SECT_DEPT_WORK_CREDIT_INTERVAL_TICKS = builder.comment("Interval for work credit accumulation (ticks).").defineInRange("workCreditIntervalTicks", 200, 1, 6000);
        SECT_DEPT_ROLE_ASSIGNMENT_RATIO = builder.comment("Ratio for role assignment.").defineInRange("roleAssignmentRatio", 0.25, 0.0, 1.0);

        SECT_DEPT_ENFORCEMENT_WORK_POINTS_PER_OUTPUT = builder.comment("Enforcement: work points per output.").defineInRange("enforcementWorkPointsPerOutput", 0, 0, 1000);
        SECT_DEPT_ENFORCEMENT_DAILY_OUTPUT_CAP = builder.comment("Enforcement: daily output cap.").defineInRange("enforcementDailyOutputCap", 0, 0, 10000);
        SECT_DEPT_ENFORCEMENT_INPUT_BUFFER_TARGET = builder.comment("Enforcement: input buffer target.").defineInRange("enforcementInputBufferTarget", 0, 0, 10000);
        SECT_DEPT_ALCHEMY_WORK_POINTS_PER_OUTPUT = builder.comment("Alchemy: work points per output.").defineInRange("alchemyWorkPointsPerOutput", 120, 0, 1000);
        SECT_DEPT_ALCHEMY_DAILY_OUTPUT_CAP = builder.comment("Alchemy: daily output cap.").defineInRange("alchemyDailyOutputCap", 24, 0, 10000);
        SECT_DEPT_ALCHEMY_INPUT_BUFFER_TARGET = builder.comment("Alchemy: input buffer target.").defineInRange("alchemyInputBufferTarget", 4, 0, 10000);
        SECT_DEPT_REFINING_WORK_POINTS_PER_OUTPUT = builder.comment("Refining: work points per output.").defineInRange("refiningWorkPointsPerOutput", 180, 0, 1000);
        SECT_DEPT_REFINING_DAILY_OUTPUT_CAP = builder.comment("Refining: daily output cap.").defineInRange("refiningDailyOutputCap", 12, 0, 10000);
        SECT_DEPT_REFINING_INPUT_BUFFER_TARGET = builder.comment("Refining: input buffer target.").defineInRange("refiningInputBufferTarget", 4, 0, 10000);
        SECT_DEPT_HERBAL_WORK_POINTS_PER_OUTPUT = builder.comment("Herbal: work points per output.").defineInRange("herbalWorkPointsPerOutput", 0, 0, 1000);
        SECT_DEPT_HERBAL_DAILY_OUTPUT_CAP = builder.comment("Herbal: daily output cap.").defineInRange("herbalDailyOutputCap", 0, 0, 10000);
        SECT_DEPT_HERBAL_INPUT_BUFFER_TARGET = builder.comment("Herbal: input buffer target.").defineInRange("herbalInputBufferTarget", 0, 0, 10000);
        SECT_DEPT_MINING_WORK_POINTS_PER_OUTPUT = builder.comment("Mining: work points per output.").defineInRange("miningWorkPointsPerOutput", 0, 0, 1000);
        SECT_DEPT_MINING_DAILY_OUTPUT_CAP = builder.comment("Mining: daily output cap.").defineInRange("miningDailyOutputCap", 0, 0, 10000);
        SECT_DEPT_MINING_INPUT_BUFFER_TARGET = builder.comment("Mining: input buffer target.").defineInRange("miningInputBufferTarget", 0, 0, 10000);
        SECT_DEPT_TREASURY_WORK_POINTS_PER_OUTPUT = builder.comment("Treasury: work points per output.").defineInRange("treasuryWorkPointsPerOutput", 0, 0, 1000);
        SECT_DEPT_TREASURY_DAILY_OUTPUT_CAP = builder.comment("Treasury: daily output cap.").defineInRange("treasuryDailyOutputCap", 0, 0, 10000);
        SECT_DEPT_TREASURY_INPUT_BUFFER_TARGET = builder.comment("Treasury: input buffer target.").defineInRange("treasuryInputBufferTarget", 0, 0, 10000);

        builder.pop(); // sectDepartments

        // ══════════════════════════════════════════════════════════════════
        //  SECT AMBIENT INTERACTIONS
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect ambient NPC social interaction additional parameters.")
               .push("sectAmbient");

        // Default corrected 2026-09-01 (was 100, real value verified via
        // javap is 20 - tickDailyLife's Math.floorMod gate). NOTE: this same
        // gate also controls tickSectISystems, an unrelated subsystem - see
        // SectAmbientCheckIntervalMixin's class doc.
        SECT_AMBIENT_CHECK_INTERVAL_TICKS = builder.comment("How often ambient interactions are checked (ticks).").defineInRange("checkIntervalTicks", 20, 1, 6000);
        SECT_AMBIENT_MAX_ACTIVE_SCENES_PER_LEVEL = builder.comment("Max concurrent ambient scenes per level.").defineInRange("maxActiveScenesPerLevel", 2, 0, 50);
        SECT_AMBIENT_MIN_SECT_COOLDOWN_TICKS = builder.comment("Minimum sect cooldown for ambient interactions.").defineInRange("minSectCooldownTicks", 1800, 0, 60000);
        SECT_AMBIENT_MAX_SECT_COOLDOWN_TICKS = builder.comment("Maximum sect cooldown for ambient interactions.").defineInRange("maxSectCooldownTicks", 4200, 0, 60000);
        SECT_AMBIENT_PAIR_SEARCH_DISTANCE = builder.comment("Distance to search for interaction pairs (blocks).").defineInRange("pairSearchDistance", 10.0, 1.0, 128.0);

        builder.pop(); // sectAmbient

        // ══════════════════════════════════════════════════════════════════
        //  SECT OVERHEAD UI
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Sect overhead panel and NPC bubble display parameters.")
               .push("sectOverhead");

        SECT_OVERHEAD_PANEL_FADE_START_DISTANCE = builder.comment("Distance where overhead panel starts fading (blocks).").defineInRange("panelFadeStartDistance", 56.0, 1.0, 256.0);
        SECT_OVERHEAD_PANEL_RENDER_DISTANCE = builder.comment("Max render distance for overhead panel (blocks).").defineInRange("panelRenderDistance", 64.0, 1.0, 512.0);
        // Defaults corrected 2026-09-01 (were 10/10) when this field was
        // wired for the first time - verified via javap -p -c -s against
        // SectOverheadPanelVisibilityClient.onClientTick(): currentAlpha
        // moves by +-0.2f/tick while shown (5 ticks to fully fade in) and
        // by -0.125f/tick while hidden (8 ticks to fully fade out).
        SECT_OVERHEAD_PANEL_FADE_IN_TICKS = builder.comment("Fade in animation duration (ticks).").defineInRange("panelFadeInTicks", 5, 1, 200);
        SECT_OVERHEAD_PANEL_FADE_OUT_TICKS = builder.comment("Fade out animation duration (ticks).").defineInRange("panelFadeOutTicks", 8, 1, 200);
        SECT_BUBBLE_MIN_DURATION_TICKS = builder.comment("Minimum bubble display duration (ticks).").defineInRange("bubbleMinDurationTicks", 60, 1, 6000);
        SECT_BUBBLE_MAX_DURATION_TICKS = builder.comment("Maximum bubble display duration (ticks).").defineInRange("bubbleMaxDurationTicks", 120, 1, 6000);
        SECT_BUBBLE_FADE_TICKS = builder.comment("Bubble fade time (ticks).").defineInRange("bubbleFadeTicks", 10, 0, 200);
        SECT_BUBBLE_DISPLAY_DISTANCE = builder.comment("Max distance to see NPC bubbles (blocks).").defineInRange("bubbleDisplayDistance", 8.0, 1.0, 256.0);

        builder.pop(); // sectOverhead

        // ══════════════════════════════════════════════════════════════════
        //  SECT CULTIVATION PROFILE
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Generated sect NPC cultivation profile parameters.")
               .push("sectProfile");

        // Field-type gap found 2026-09-01: the real backing fields
        // (GeneratedSectCultivationProfile.MIN_CULTIVATOR_PROGRESS/
        // MAX_MORTAL_REALM_PROGRESS/MIN_MASTER_PROGRESS) are raw int
        // Realm.progressIndex() values, not 0.0-1.0 fractions - these 3
        // DoubleValue fields cannot be correctly wired to them without
        // inventing an unverified fraction-to-int mapping, so they remain
        // unwired (see SectAncestorProfileMixin's class doc for the full
        // bytecode citation).
        SECT_PROFILE_MIN_CULTIVATOR_PROGRESS = builder.comment("Minimum cultivator progress. NOTE: field type mismatch with the real int-based value - currently unwired, has no effect.").defineInRange("minCultivatorProgress", 0.1, 0.0, 1.0);
        SECT_PROFILE_MAX_MORTAL_REALM_PROGRESS = builder.comment("Maximum mortal realm progress. NOTE: field type mismatch with the real int-based value - currently unwired, has no effect.").defineInRange("maxMortalRealmProgress", 0.8, 0.0, 1.0);
        // MAX_ORDINARY_ROLE_SPAN default corrected 2026-09-01 (was 365) when
        // wired for the first time - verified via javap -p -c -s against
        // GeneratedSectCultivationProfile.forRole(): the real cap is 20
        // (progressIndex units, not days - the "(days)" in these comments
        // appears to be a pre-existing scaffolding assumption, not verified).
        SECT_PROFILE_MAX_POWER_SCORE = builder.comment("Maximum sect power score.").defineInRange("maxPowerScore", 4, 0, 100);
        SECT_PROFILE_MIN_ORDINARY_ROLE_SPAN = builder.comment("Minimum ordinary role span (days). NOTE: real backing field not located this session - currently unwired, has no effect.").defineInRange("minOrdinaryRoleSpan", 30, 1, 10000);
        SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN = builder.comment("Maximum ordinary role span.").defineInRange("maxOrdinaryRoleSpan", 20, 1, 10000);
        SECT_PROFILE_MIN_MASTER_PROGRESS = builder.comment("Minimum progress for master role. NOTE: field type mismatch with the real int-based value - currently unwired, has no effect.").defineInRange("minMasterProgress", 0.5, 0.0, 1.0);

        builder.pop(); // sectProfile

        // ══════════════════════════════════════════════════════════════════
        //  NPC AI COMBAT
        // ══════════════════════════════════════════════════════════════════
        builder.comment("NPC AI combat behavior: spell attack, flight, kiting, retreat, tactics.")
               .push("npcAi");

        NPC_AI_SPELL_MAX_RANGE = builder.comment("Max spell attack range for NPCs (blocks).").defineInRange("spellMaxRange", 40.0, 1.0, 256.0);
        NPC_AI_HIGH_IMPACT_MEGA_COOLDOWN_TICKS = builder.comment("Cooldown for mega-impact spells (ticks).").defineInRange("highImpactMegaCooldownTicks", 500, 0, 6000);

        NPC_AI_FLIGHT_RANGED_MIN_DISTANCE = builder.comment("Minimum ranged combat distance during flight (blocks).").defineInRange("flightRangedMinDistance", 8.0, 0.0, 64.0);
        NPC_AI_FLIGHT_RANGED_MAX_DISTANCE = builder.comment("Maximum ranged combat distance during flight (blocks).").defineInRange("flightRangedMaxDistance", 16.0, 0.0, 64.0);
        NPC_AI_FLIGHT_MELEE_DISTANCE = builder.comment("Melee distance during flight (blocks).").defineInRange("flightMeleeDistance", 2.4, 0.0, 32.0);
        NPC_AI_FLIGHT_MIN_SEGMENT_TICKS = builder.comment("Minimum flight segment duration (ticks).").defineInRange("flightMinSegmentTicks", 9, 1, 600);
        NPC_AI_FLIGHT_MAX_SEGMENT_TICKS = builder.comment("Maximum flight segment duration (ticks).").defineInRange("flightMaxSegmentTicks", 25, 1, 600);
        NPC_AI_FLIGHT_MIN_HEIGHT = builder.comment("Minimum flight height (blocks above ground).").defineInRange("flightMinHeight", 3.0, 0.0, 64.0);
        NPC_AI_FLIGHT_MAX_HEIGHT = builder.comment("Maximum flight height (blocks above ground).").defineInRange("flightMaxHeight", 15.0, 0.0, 128.0);
        NPC_AI_FLIGHT_MAX_HORIZONTAL_SPEED = builder.comment("Maximum horizontal flight speed.").defineInRange("flightMaxHorizontalSpeed", 0.52, 0.0, 10.0);
        NPC_AI_FLIGHT_MAX_VERTICAL_SPEED = builder.comment("Maximum vertical flight speed.").defineInRange("flightMaxVerticalSpeed", 0.31, 0.0, 10.0);

        NPC_AI_KITING_MIN_RANGE = builder.comment("Minimum kiting distance (blocks).").defineInRange("kitingMinRange", 8.0, 0.0, 64.0);
        NPC_AI_KITING_PREFERRED_RANGE = builder.comment("Preferred kiting distance (blocks).").defineInRange("kitingPreferredRange", 12.0, 0.0, 64.0);
        NPC_AI_KITING_MAX_RANGE = builder.comment("Maximum kiting distance (blocks).").defineInRange("kitingMaxRange", 16.0, 0.0, 128.0);
        NPC_AI_KITING_GIVE_UP_RANGE = builder.comment("Distance where NPC gives up kiting (blocks).").defineInRange("kitingGiveUpRange", 40.0, 0.0, 256.0);
        NPC_AI_KITING_GROUND_WALK_SPEED_MODIFIER = builder.comment("Speed modifier when walking on ground during kiting.").defineInRange("kitingGroundWalkSpeedModifier", 1.0, 0.0, 10.0);

        NPC_AI_RETREAT_DISTANCE = builder.comment("How far NPC retreats (blocks).").defineInRange("retreatDistance", 18.0, 1.0, 256.0);
        NPC_AI_RETREAT_RANDOM_SPREAD = builder.comment("Random yaw jitter applied to the retreat direction, in RADIANS.").defineInRange("retreatRandomSpread", 0.75, 0.0, 64.0);
        NPC_AI_RETREAT_GROUND_SPEED = builder.comment("Retreat speed on ground.").defineInRange("retreatGroundSpeed", 1.25, 0.0, 10.0);
        NPC_AI_RETREAT_AIR_SPEED = builder.comment("Retreat speed in air.").defineInRange("retreatAirSpeed", 0.46, 0.0, 10.0);

        NPC_AI_SURVIVAL_RESERVE_FRACTION = builder.comment("Qi fraction reserved for survival (0.1 = 10% Qi kept).").defineInRange("survivalReserveFraction", 0.1, 0.0, 1.0);
        NPC_AI_DECISION_INTERVAL_TICKS = builder.comment("Ticks between combat decisions.").defineInRange("decisionIntervalTicks", 20, 1, 600);

        builder.pop(); // npcAi

        // ══════════════════════════════════════════════════════════════════
        //  NPC TRADES
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Wandering cultivator trade parameters.")
               .push("npcTrades");

        NPC_TRADES_INFINITE_USES = builder.comment("If true, NPC trades have infinite uses.").define("infiniteUses", false);
        NPC_TRADES_PRICE_MULT = builder.comment("Price multiplier for NPC trades (1.0 = normal).").defineInRange("priceMult", 1.0, 0.0, 100.0);

        builder.pop(); // npcTrades

        // ══════════════════════════════════════════════════════════════════
        //  FORMATION CORE
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Formation core plate block entity timing and harvest parameters.")
               .push("formationCore");

        FORMATION_EFFECT_INTERVAL_TICKS = builder.comment("How often formation effects tick. " +
                "Wired 2026-09-01 via FormationCorePlateBlockEntityMixin. Verified (javap -constants) the real " +
                "base-mod value is 10, not the 20 this field previously shipped with (it was never wired before, " +
                "so this corrects the default to the real original-mod value rather than leave a wrong one live).")
                .defineInRange("effectIntervalTicks", 10, 1, 6000);
        FORMATION_GENERATED_ARRAY_SYNC_INTERVAL_TICKS = builder.comment("Sync interval for generated arrays " +
                "(IMMORTAL-tier formation cores only). Wired 2026-09-01. Real base-mod value is 200, corrected " +
                "from this field's previous unwired default of 100.")
                .defineInRange("generatedArraySyncIntervalTicks", 200, 1, 6000);
        FORMATION_RELOAD_FLAG_VALIDATION_GRACE_TICKS = builder.comment("Grace period for flag validation on " +
                "reload. Wired 2026-09-01. Real base-mod value is 100, corrected from this field's previous " +
                "unwired default of 200.").defineInRange("reloadFlagValidationGraceTicks", 100, 0, 6000);
        FORMATION_EFFECT_DURATION_TICKS = builder.comment("Duration of formation effects (ticks). " +
                "Wired 2026-09-01 - applies to the REJUVENATION, FLIGHT_BAN, and MAZE effect types (4 call " +
                "sites total, all sharing this one literal). Real base-mod value is 16, corrected from this " +
                "field's previous unwired default of 200 (12.5x off).").defineInRange("effectDurationTicks", 16, 1, 6000);
        FORMATION_GROWTH_TICK_INTERVAL_TICKS = builder.comment("Growth formation tick cadence (distinct from " +
                "FORMATION_GROWTH_MULT_*, which controls the multiplier applied on each tick, not how often it " +
                "ticks). Wired 2026-09-01. Real base-mod value is 20, corrected from this field's previous " +
                "unwired default of 100.").defineInRange("growthTickIntervalTicks", 20, 1, 6000);
        FORMATION_STORAGE_CORE_INTERVAL_TICKS = builder.comment("Storage core tick interval. " +
                "Wired 2026-09-01. This default (20) already matched the real base-mod value.")
                .defineInRange("storageCoreIntervalTicks", 20, 1, 6000);
        FORMATION_FARM_HARVEST_MIN_CHECKS = builder.comment("Minimum farm-tile scan budget per harvest cycle " +
                "(bounds the batch-size-driven scan, doesn't change harvested yield - see FORMATION_HARVEST_ " +
                "BATCH_SIZE_IMMORTAL etc for that). Wired 2026-09-01. Real base-mod value is 4096, corrected " +
                "from this field's previous unwired default of 1 (and the range widened to fit - the old 0-100 " +
                "range couldn't even hold the real value).").defineInRange("farmHarvestMinChecks", 4096, 0, 20000);
        FORMATION_FARM_HARVEST_CHECKS_PER_TARGET = builder.comment("Farm-tile scan budget per harvest-batch " +
                "unit. Wired 2026-09-01. Real base-mod value is 1024, corrected from this field's previous " +
                "unwired default of 4 (range widened to fit).")
                .defineInRange("farmHarvestChecksPerTarget", 1024, 1, 5000);
        FORMATION_FARM_HARVEST_MAX_CHECKS = builder.comment("Maximum farm-tile scan budget per harvest cycle. " +
                "Wired 2026-09-01. Real base-mod value is 16384, corrected from this field's previous unwired " +
                "default of 64 (range widened to fit).").defineInRange("farmHarvestMaxChecks", 16384, 1, 50000);
        FORMATION_MIN_FLAG_EFFECT_RADIUS = builder.comment("Minimum flag effect radius (blocks). " +
                "Wired 2026-09-01. This default (1) already matched the real base-mod value.")
                .defineInRange("minFlagEffectRadius", 1, 1, 64);
        FORMATION_DEFAULT_FLAG_EFFECT_RADIUS = builder.comment("Default flag effect radius (blocks) used before " +
                "a per-flag radius is explicitly set. Wired 2026-09-01. Real base-mod value is 8, corrected " +
                "from this field's previous unwired default of 4.").defineInRange("defaultFlagEffectRadius", 8, 1, 64);
        FORMATION_MAX_FLAG_EFFECT_RADIUS = builder.comment("Maximum flag effect radius (blocks). " +
                "Wired 2026-09-01 - two call sites share this literal (the shared clamp helper and a second, " +
                "independent fallback clamp in radiusForFlag). Real base-mod value is 255, corrected from this " +
                "field's previous unwired default of 16 (range widened to fit).")
                .defineInRange("maxFlagEffectRadius", 255, 1, 512);

        builder.pop(); // formationCore

        // ══════════════════════════════════════════════════════════════════
        //  LOOSE IMMORTAL
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Loose Immortal tribulation and per-level bonus parameters.")
               .push("looseImmortal");

        LOOSE_IMMORTAL_MAX_TRIBULATIONS = builder.comment("Max tribulations a loose immortal can endure.").defineInRange("maxTribulations", 4, 1, 100);
        LOOSE_IMMORTAL_INTERVAL_YEARS = builder.comment("Years between loose immortal tribulations.").defineInRange("intervalYears", 500, 1, 100000);
        LOOSE_IMMORTAL_WARNING_TICKS = builder.comment("Warning time before tribulation (ticks).").defineInRange("warningTicks", 200, 0, 6000);
        LOOSE_IMMORTAL_WAVES_PER_TRIBULATION = builder.comment("Waves per tribulation.").defineInRange("wavesPerTribulation", 3, 1, 100);
        LOOSE_IMMORTAL_BOLTS_PER_WAVE = builder.comment("Lightning bolts per wave.").defineInRange("boltsPerWave", 3, 1, 100);
        LOOSE_IMMORTAL_STRIKE_DAMAGE = builder.comment("Damage per lightning strike.").defineInRange("strikeDamage", 50, 0, 10000);

        LOOSE_IMMORTAL_L0_BODY_DEFENSE = builder.comment("Level 0 body defense bonus.").defineInRange("l0BodyDefense", 2, 0, 1000);
        LOOSE_IMMORTAL_L1_BODY_DEFENSE = builder.comment("Level 1 body defense bonus.").defineInRange("l1BodyDefense", 4, 0, 1000);
        LOOSE_IMMORTAL_L2_BODY_DEFENSE = builder.comment("Level 2 body defense bonus.").defineInRange("l2BodyDefense", 8, 0, 1000);
        LOOSE_IMMORTAL_L3_BODY_DEFENSE = builder.comment("Level 3 body defense bonus.").defineInRange("l3BodyDefense", 16, 0, 1000);
        LOOSE_IMMORTAL_L0_CULTIVATION_EFFICIENCY = builder.comment("Level 0 cultivation efficiency bonus.").defineInRange("l0CultivationEfficiency", 5, 0, 1000);
        LOOSE_IMMORTAL_L1_CULTIVATION_EFFICIENCY = builder.comment("Level 1 cultivation efficiency bonus.").defineInRange("l1CultivationEfficiency", 10, 0, 1000);
        LOOSE_IMMORTAL_L2_CULTIVATION_EFFICIENCY = builder.comment("Level 2 cultivation efficiency bonus.").defineInRange("l2CultivationEfficiency", 20, 0, 1000);
        LOOSE_IMMORTAL_L3_CULTIVATION_EFFICIENCY = builder.comment("Level 3 cultivation efficiency bonus.").defineInRange("l3CultivationEfficiency", 40, 0, 1000);
        LOOSE_IMMORTAL_L0_QI_RECOVERY = builder.comment("Level 0 Qi recovery bonus.").defineInRange("l0QiRecovery", 5, 0, 1000);
        LOOSE_IMMORTAL_L1_QI_RECOVERY = builder.comment("Level 1 Qi recovery bonus.").defineInRange("l1QiRecovery", 10, 0, 1000);
        LOOSE_IMMORTAL_L2_QI_RECOVERY = builder.comment("Level 2 Qi recovery bonus.").defineInRange("l2QiRecovery", 20, 0, 1000);
        LOOSE_IMMORTAL_L3_QI_RECOVERY = builder.comment("Level 3 Qi recovery bonus.").defineInRange("l3QiRecovery", 40, 0, 1000);
        LOOSE_IMMORTAL_L0_MELEE_DAMAGE = builder.comment("Level 0 melee damage bonus.").defineInRange("l0MeleeDamage", 2, 0, 1000);
        LOOSE_IMMORTAL_L1_MELEE_DAMAGE = builder.comment("Level 1 melee damage bonus.").defineInRange("l1MeleeDamage", 4, 0, 1000);
        LOOSE_IMMORTAL_L2_MELEE_DAMAGE = builder.comment("Level 2 melee damage bonus.").defineInRange("l2MeleeDamage", 8, 0, 1000);
        LOOSE_IMMORTAL_L3_MELEE_DAMAGE = builder.comment("Level 3 melee damage bonus.").defineInRange("l3MeleeDamage", 16, 0, 1000);
        LOOSE_IMMORTAL_L0_SPELL_DAMAGE = builder.comment("Level 0 spell damage bonus multiplier.").defineInRange("l0SpellDamage", 0.1, 0.0, 100.0);
        LOOSE_IMMORTAL_L1_SPELL_DAMAGE = builder.comment("Level 1 spell damage bonus multiplier.").defineInRange("l1SpellDamage", 0.2, 0.0, 100.0);
        LOOSE_IMMORTAL_L2_SPELL_DAMAGE = builder.comment("Level 2 spell damage bonus multiplier.").defineInRange("l2SpellDamage", 0.4, 0.0, 100.0);
        LOOSE_IMMORTAL_L3_SPELL_DAMAGE = builder.comment("Level 3 spell damage bonus multiplier.").defineInRange("l3SpellDamage", 0.8, 0.0, 100.0);
        LOOSE_IMMORTAL_L0_SPELL_COST = builder.comment("Level 0 spell cost reduction multiplier.").defineInRange("l0SpellCost", 0.1, 0.0, 100.0);
        LOOSE_IMMORTAL_L1_SPELL_COST = builder.comment("Level 1 spell cost reduction multiplier.").defineInRange("l1SpellCost", 0.2, 0.0, 100.0);
        LOOSE_IMMORTAL_L2_SPELL_COST = builder.comment("Level 2 spell cost reduction multiplier.").defineInRange("l2SpellCost", 0.4, 0.0, 100.0);
        LOOSE_IMMORTAL_L3_SPELL_COST = builder.comment("Level 3 spell cost reduction multiplier.").defineInRange("l3SpellCost", 0.8, 0.0, 100.0);
        LOOSE_IMMORTAL_L0_MAX_QI = builder.comment("Level 0 max Qi bonus.").defineInRange("l0MaxQi", 10000L, 0L, 1000000000L);
        LOOSE_IMMORTAL_L1_MAX_QI = builder.comment("Level 1 max Qi bonus.").defineInRange("l1MaxQi", 50000L, 0L, 1000000000L);
        LOOSE_IMMORTAL_L2_MAX_QI = builder.comment("Level 2 max Qi bonus.").defineInRange("l2MaxQi", 200000L, 0L, 1000000000L);
        LOOSE_IMMORTAL_L3_MAX_QI = builder.comment("Level 3 max Qi bonus.").defineInRange("l3MaxQi", 1000000L, 0L, 1000000000L);
        LOOSE_IMMORTAL_L0_FREE_ZHENYUAN = builder.comment("Level 0 free zhenyuan stat points.").defineInRange("l0FreeZhenyuan", 5, 0, 1000);
        LOOSE_IMMORTAL_L1_FREE_ZHENYUAN = builder.comment("Level 1 free zhenyuan stat points.").defineInRange("l1FreeZhenyuan", 10, 0, 1000);
        LOOSE_IMMORTAL_L2_FREE_ZHENYUAN = builder.comment("Level 2 free zhenyuan stat points.").defineInRange("l2FreeZhenyuan", 20, 0, 1000);
        LOOSE_IMMORTAL_L3_FREE_ZHENYUAN = builder.comment("Level 3 free zhenyuan stat points.").defineInRange("l3FreeZhenyuan", 40, 0, 1000);
        LOOSE_IMMORTAL_L0_AUTO_ZHENYUAN_ATTR = builder.comment("Level 0 auto-allocated attribute points.").defineInRange("l0AutoZhenyuanAttr", 2, 0, 1000);
        LOOSE_IMMORTAL_L1_AUTO_ZHENYUAN_ATTR = builder.comment("Level 1 auto-allocated attribute points.").defineInRange("l1AutoZhenyuanAttr", 4, 0, 1000);
        LOOSE_IMMORTAL_L2_AUTO_ZHENYUAN_ATTR = builder.comment("Level 2 auto-allocated attribute points.").defineInRange("l2AutoZhenyuanAttr", 8, 0, 1000);
        LOOSE_IMMORTAL_L3_AUTO_ZHENYUAN_ATTR = builder.comment("Level 3 auto-allocated attribute points.").defineInRange("l3AutoZhenyuanAttr", 16, 0, 1000);

        builder.pop(); // looseImmortal

        // ══════════════════════════════════════════════════════════════════
        //  CULTIVATION DATA
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Core cultivation data constants: time acceleration, tribulation timing, equipped slots.")
               .push("cultivationData");

        CULTIVATION_MAX_TIME_ACCELERATION_MULTIPLIER = builder.comment("Max time acceleration multiplier.").defineInRange("maxTimeAccelerationMultiplier", 8, 1, 100);
        CULTIVATION_TRIBULATION_INTERVAL_TICKS = builder.comment("Interval between tribulations (ticks).").defineInRange("tribulationIntervalTicks", 200, 1, 60000);
        CULTIVATION_TRIBULATION_CHARGE_TICKS = builder.comment("Charge time for tribulation (ticks).").defineInRange("tribulationChargeTicks", 100, 0, 6000);
        CULTIVATION_EQUIPPED_SLOT_COUNT = builder.comment("Number of equipped item slots.").defineInRange("equippedSlotCount", 4, 1, 20);
        CULTIVATION_ZHENYUAN_ATTR_REWARD_MINOR = builder.comment("Auto-allocated attribute points per minor sub-stage advancement.").defineInRange("zhenyuanAttrRewardMinor", 1, 0, 100);
        CULTIVATION_ZHENYUAN_ATTR_REWARD_MAJOR = builder.comment("Auto-allocated attribute points per major realm breakthrough.").defineInRange("zhenyuanAttrRewardMajor", 3, 0, 100);

        builder.pop(); // cultivationData

        // ══════════════════════════════════════════════════════════════════
        //  PROGRESSION RULES (additional)
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Additional cultivation progression rules: technique base, lifespan reserve, weakness days.")
               .push("progressionRules");

        PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT = builder.comment("Base technique multiplier for mortals. " +
                "Wired 2026-09-01 via CultivationProgressionRulesMixin, targeting " +
                "CultivationProgressionRules.baseAbsorbMultiplier(Realm, boolean, boolean) - the fallback " +
                "absorption multiplier used for a MORTAL-realm player/NPC with an equipped technique, when " +
                "the realm itself defines no base absorb mult of its own. The default (1.0) already matches " +
                "the real value.").defineInRange("mortalEquippedTechniqueBaseMult", 1.0, 0.0, 100.0);
        PROGRESSION_NPC_HIGHER_DAO_LIFESPAN_RESERVE_THRESHOLD = builder.comment("Lifespan reserve threshold for " +
                "NPC Dao selection. Wired 2026-09-01 via CultivationProgressionRulesMixin, targeting " +
                "CultivationProgressionRules.hasEnoughLifespanReserve(double, int). This field was never wired " +
                "before this session, so its default has been corrected from 0.5 to the real value, 1.0 " +
                "(reserveDays / lifespanDays must be >= 1.0).").defineInRange("npcHigherDaoLifespanReserveThreshold", 1.0, 0.0, 100.0);
        PROGRESSION_NPC_TRIBULATION_FAILURE_WEAKNESS_DAYS = builder.comment("Days of weakness after NPC " +
                "tribulation failure. DUPLICATE FIELD (found 2026-09-01): this describes the exact same real " +
                "mechanic as progressionRules.npcTribulationWeaknessDays above (SectSavedData.failMemberTribulation, " +
                "real value 3, not 30 - this field's own default was never corrected because only one mixin can " +
                "safely own that bytecode site). Edit npcTribulationWeaknessDays instead; this field is not wired " +
                "and changing it does nothing.").defineInRange("npcTribulationFailureWeaknessDays", 30, 0, 3650);

        builder.pop(); // progressionRules

        // ══════════════════════════════════════════════════════════════════
        //  IDENTITY DRAW
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Identity draw deck parameters.")
               .push("identityDraw");

        IDENTITY_DRAW_DECK_SIZE = builder.comment("Number of cards in identity draw deck. Set to max (50) so all identities fit.").defineInRange("deckSize", 50, 1, 50);
        IDENTITY_DRAW_MAX_ROUNDS = builder.comment("Max reroll rounds for identity draw. Set to max (20) for maximum rerolls.").defineInRange("maxRounds", 20, 0, 20);

        builder.pop(); // identityDraw

        builder.comment("Golden Finger perk selection parameters.")
               .push("goldenFinger");

        GOLDEN_FINGER_PERK_COUNT = builder.comment("Number of Golden Finger perks players can select during world creation. Default is 3. Increase for more powerful starts, decrease for harder starts.").defineInRange("perkCount", 3, 1, 99);

        builder.pop(); // goldenFinger

        // ══════════════════════════════════════════════════════════════════
        //  MORALITY (additional)
        // ══════════════════════════════════════════════════════════════════
        builder.comment("Additional morality system constants: min/max value bounds.")
               .push("moralityBounds");

        MORALITY_MIN_VALUE = builder.comment("Minimum morality value. Wired 2026-09-01 via MoralityHelperMixin, " +
                "targeting the int AND long -1000000 literals in MoralityHelper.clamp(int) and .add(int, int) " +
                "(two overload-distinct constants per method). Never wired before this session, so the default " +
                "and range were corrected to match the real value (-1000000, was -1000).").defineInRange("minValue", -1000000, -100000000, 0);
        MORALITY_MAX_VALUE = builder.comment("Maximum morality value. Wired 2026-09-01, same mixin, targeting " +
                "the +1000000 literals. Default/range corrected from 1000 the same way.").defineInRange("maxValue", 1000000, 0, 100000000);

        builder.pop(); // moralityBounds

        // ══════════════════════════════════════════════════════════════════
        //  TALISMAN (new in base mod 0.1.1479)
        // ══════════════════════════════════════════════════════════════════
        // The 0.1.1479 update added a ~166-class Talisman glyph-crafting system
        // (com.xiaoxiang.cultivation.cultivation.talisman / .entity.talisman /
        // .talisman packages). Its central constants table, TalismanLimits.class,
        // holds ~110 public static final int/float fields (structural caps like
        // MAX_EXECUTABLE_SLOTS plus ~90 per-glyph *_QI_COST values) - but every one
        // of them is a compile-time constant inlined at every use site (confirmed:
        // zero getstatic references to TalismanLimits anywhere in the disassembled
        // package), so wiring each one requires individually locating and verifying
        // its real use site(s), exactly like every other fix in this project. Only
        // the two fields below have been traced and verified so far; the rest are
        // tracked as a backlog in CONFIG_AUDIT.md for future passes rather than
        // guessed at.
        builder.comment("Talisman glyph-crafting system (base mod 0.1.1479+). Only a first wave " +
                "of its constants are wired so far - see CONFIG_AUDIT.md for the full backlog.")
               .push("talisman");

        ENABLE_TALISMAN_OVERRIDES = builder.comment("Override talisman glyph-crafting values.").define("enableTalismanOverrides", true);

        // Wired 2026-09-02 via TalismanCompilerMixin (configExt$defaultExplosionRadius /
        // configExt$maxExplosionRadius): TalismanCompiler.buildCompiledPlan(...), the
        // EXPLOSION manifestation-type branch of a tableswitch on
        // TalismanManifestationType.ordinal() (case 2 = EXPLOSION, the 2nd declared
        // enum constant, confirmed via the real enum's declaration order). fconst_2
        // (2.0f = TalismanLimits.DEFAULT_EXPLOSION_RADIUS) is fed into
        // evolvedAreaRadius(F, TalismanEvolutionBundle) to compute the glyph's real
        // radius after evolution scaling, which is then compared via fcmpl/ifle
        // against ldc_w 7.0f (TalismanLimits.MAX_EXPLOSION_RADIUS) - exceeding it
        // raises TalismanCompileError.EXPLOSION_RADIUS_LIMIT and rejects the craft.
        // Both literals are single, unambiguous occurrences within this one method.
        TALISMAN_DEFAULT_EXPLOSION_RADIUS = builder.comment("Base explosion radius (before evolution scaling) for the EXPLOSION talisman glyph manifestation.").defineInRange("defaultExplosionRadius", 2.0, 0.1, 100.0);
        TALISMAN_MAX_EXPLOSION_RADIUS = builder.comment("Maximum allowed explosion radius (after evolution scaling) before talisman compilation rejects the glyph.").defineInRange("maxExplosionRadius", 7.0, 0.1, 100.0);

        builder.comment("Per-glyph Qi costs for the Talisman crafting system (base mod 0.1.1479). " +
                "Wave 2 of the audit in CONFIG_AUDIT.md - each field traced to its exact call site " +
                "in TalismanGlyphCatalog's register*(Map) methods via TalismanGlyphCatalogMixin.")
               .push("glyphCosts");

        TALISMAN_TRIGGER_QI_COST = builder.comment("Qi cost for the ON_HURT, LOW_HEALTH, NEARBY_LIVING, CONTACT talisman glyphs (they share one generic base-mod constant) (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.TRIGGER_QI_COST.")
                .defineInRange("triggerQiCost", 11, 0, 10000);
        TALISMAN_FIREBALL_QI_COST = builder.comment("Qi cost for the FIREBALL talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.FIREBALL_QI_COST.")
                .defineInRange("fireballQiCost", 17, 0, 10000);
        TALISMAN_EXPLOSION_QI_COST = builder.comment("Qi cost for the EXPLOSION talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.EXPLOSION_QI_COST.")
                .defineInRange("explosionQiCost", 16, 0, 10000);
        TALISMAN_DAMAGE_QI_COST = builder.comment("Qi cost for the DAMAGE_X2 talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DAMAGE_QI_COST.")
                .defineInRange("damageQiCost", 15, 0, 10000);
        TALISMAN_DESTRUCTION_QI_COST = builder.comment("Qi cost for the DESTRUCTION_X2 talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DESTRUCTION_QI_COST.")
                .defineInRange("destructionQiCost", 16, 0, 10000);
        TALISMAN_SPLIT_QI_COST = builder.comment("Qi cost for the SPLIT talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SPLIT_QI_COST.")
                .defineInRange("splitQiCost", 19, 0, 10000);
        TALISMAN_DIVINE_SENSE_TRIGGER_QI_COST = builder.comment("Qi cost for the DIVINE_SENSE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DIVINE_SENSE_TRIGGER_QI_COST.")
                .defineInRange("divineSenseTriggerQiCost", 13, 0, 10000);
        TALISMAN_ANTI_AIR_TRIGGER_QI_COST = builder.comment("Qi cost for the ANTI_AIR talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.ANTI_AIR_TRIGGER_QI_COST.")
                .defineInRange("antiAirTriggerQiCost", 16, 0, 10000);
        TALISMAN_LIGHTNING_ARC_QI_COST = builder.comment("Qi cost for the LIGHTNING_ARC talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.LIGHTNING_ARC_QI_COST.")
                .defineInRange("lightningArcQiCost", 24, 0, 10000);
        TALISMAN_LIGHTNING_STRIKE_QI_COST = builder.comment("Qi cost for the LIGHTNING_STRIKE talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.LIGHTNING_STRIKE_QI_COST.")
                .defineInRange("lightningStrikeQiCost", 28, 0, 10000);
        TALISMAN_ICE_SWORD_QI_COST = builder.comment("Qi cost for the ICE_SWORD talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.ICE_SWORD_QI_COST.")
                .defineInRange("iceSwordQiCost", 24, 0, 10000);
        TALISMAN_REGENERATION_QI_COST = builder.comment("Qi cost for the REGENERATION talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.REGENERATION_QI_COST.")
                .defineInRange("regenerationQiCost", 18, 0, 10000);
        TALISMAN_SWIFTNESS_QI_COST = builder.comment("Qi cost for the SWIFTNESS talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SWIFTNESS_QI_COST.")
                .defineInRange("swiftnessQiCost", 16, 0, 10000);
        TALISMAN_POISON_QI_COST = builder.comment("Qi cost for the POISON talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.POISON_QI_COST.")
                .defineInRange("poisonQiCost", 18, 0, 10000);
        TALISMAN_DELAY_QI_COST = builder.comment("Qi cost for the DELAY talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DELAY_QI_COST.")
                .defineInRange("delayQiCost", 13, 0, 10000);
        TALISMAN_HOMING_QI_COST = builder.comment("Qi cost for the HOMING talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HOMING_QI_COST.")
                .defineInRange("homingQiCost", 18, 0, 10000);
        TALISMAN_PIERCE_QI_COST = builder.comment("Qi cost for the PIERCE_PLUS_ONE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.PIERCE_QI_COST.")
                .defineInRange("pierceQiCost", 20, 0, 10000);
        TALISMAN_POTENCY_QI_COST = builder.comment("Qi cost for the POTENCY_PLUS_ONE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.POTENCY_QI_COST.")
                .defineInRange("potencyQiCost", 16, 0, 10000);
        TALISMAN_DURATION_QI_COST = builder.comment("Qi cost for the DURATION_X2 talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DURATION_QI_COST.")
                .defineInRange("durationQiCost", 15, 0, 10000);
        TALISMAN_PULL_QI_COST = builder.comment("Qi cost for the PULL talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.PULL_QI_COST.")
                .defineInRange("pullQiCost", 19, 0, 10000);
        TALISMAN_MECHANISM_TRIGGER_QI_COST = builder.comment("Qi cost for the MECHANISM talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.MECHANISM_TRIGGER_QI_COST.")
                .defineInRange("mechanismTriggerQiCost", 12, 0, 10000);
        TALISMAN_TAMPER_TRIGGER_QI_COST = builder.comment("Qi cost for the TAMPER talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.TAMPER_TRIGGER_QI_COST.")
                .defineInRange("tamperTriggerQiCost", 14, 0, 10000);
        TALISMAN_MELEE_STRIKE_TRIGGER_QI_COST = builder.comment("Qi cost for the MELEE_STRIKE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.MELEE_STRIKE_TRIGGER_QI_COST.")
                .defineInRange("meleeStrikeTriggerQiCost", 14, 0, 10000);
        TALISMAN_RANGED_STRIKE_TRIGGER_QI_COST = builder.comment("Qi cost for the RANGED_STRIKE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.RANGED_STRIKE_TRIGGER_QI_COST.")
                .defineInRange("rangedStrikeTriggerQiCost", 15, 0, 10000);
        TALISMAN_SUCCESSFUL_GUARD_TRIGGER_QI_COST = builder.comment("Qi cost for the SUCCESSFUL_GUARD talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SUCCESSFUL_GUARD_TRIGGER_QI_COST.")
                .defineInRange("successfulGuardTriggerQiCost", 16, 0, 10000);
        TALISMAN_SPELLCAST_TRIGGER_QI_COST = builder.comment("Qi cost for the SPELLCAST talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SPELLCAST_TRIGGER_QI_COST.")
                .defineInRange("spellcastTriggerQiCost", 18, 0, 10000);
        TALISMAN_VANQUISH_TRIGGER_QI_COST = builder.comment("Qi cost for the VANQUISH talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.VANQUISH_TRIGGER_QI_COST.")
                .defineInRange("vanquishTriggerQiCost", 17, 0, 10000);
        TALISMAN_LOW_QI_TRIGGER_QI_COST = builder.comment("Qi cost for the LOW_QI talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.LOW_QI_TRIGGER_QI_COST.")
                .defineInRange("lowQiTriggerQiCost", 15, 0, 10000);
        TALISMAN_AFFLICTED_TRIGGER_QI_COST = builder.comment("Qi cost for the AFFLICTED talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.AFFLICTED_TRIGGER_QI_COST.")
                .defineInRange("afflictedTriggerQiCost", 16, 0, 10000);
        TALISMAN_IGNITED_TRIGGER_QI_COST = builder.comment("Qi cost for the IGNITED talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.IGNITED_TRIGGER_QI_COST.")
                .defineInRange("ignitedTriggerQiCost", 12, 0, 10000);
        TALISMAN_HARD_LANDING_TRIGGER_QI_COST = builder.comment("Qi cost for the HARD_LANDING talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HARD_LANDING_TRIGGER_QI_COST.")
                .defineInRange("hardLandingTriggerQiCost", 12, 0, 10000);
        TALISMAN_SHIELD_BROKEN_TRIGGER_QI_COST = builder.comment("Qi cost for the SHIELD_BROKEN talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SHIELD_BROKEN_TRIGGER_QI_COST.")
                .defineInRange("shieldBrokenTriggerQiCost", 20, 0, 10000);
        TALISMAN_LEFT_CLICK_SUPPORT_TRIGGER_QI_COST = builder.comment("Qi cost for the LEFT_CLICK_SUPPORT talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.LEFT_CLICK_SUPPORT_TRIGGER_QI_COST.")
                .defineInRange("leftClickSupportTriggerQiCost", 14, 0, 10000);
        TALISMAN_NEARBY_BLOCK_BREAK_TRIGGER_QI_COST = builder.comment("Qi cost for the NEARBY_BLOCK_BREAK talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.NEARBY_BLOCK_BREAK_TRIGGER_QI_COST.")
                .defineInRange("nearbyBlockBreakTriggerQiCost", 16, 0, 10000);
        TALISMAN_HOSTILE_PROJECTILE_TRIGGER_QI_COST = builder.comment("Qi cost for the HOSTILE_PROJECTILE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HOSTILE_PROJECTILE_TRIGGER_QI_COST.")
                .defineInRange("hostileProjectileTriggerQiCost", 18, 0, 10000);
        TALISMAN_BOUNDARY_CROSSING_TRIGGER_QI_COST = builder.comment("Qi cost for the BOUNDARY_CROSSING talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.BOUNDARY_CROSSING_TRIGGER_QI_COST.")
                .defineInRange("boundaryCrossingTriggerQiCost", 16, 0, 10000);
        TALISMAN_HOSTILE_GAZE_TRIGGER_QI_COST = builder.comment("Qi cost for the HOSTILE_GAZE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HOSTILE_GAZE_TRIGGER_QI_COST.")
                .defineInRange("hostileGazeTriggerQiCost", 18, 0, 10000);
        TALISMAN_HOSTILE_SPELLCAST_TRIGGER_QI_COST = builder.comment("Qi cost for the HOSTILE_SPELLCAST talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HOSTILE_SPELLCAST_TRIGGER_QI_COST.")
                .defineInRange("hostileSpellcastTriggerQiCost", 20, 0, 10000);
        TALISMAN_NEARBY_EXPLOSION_TRIGGER_QI_COST = builder.comment("Qi cost for the NEARBY_EXPLOSION talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.NEARBY_EXPLOSION_TRIGGER_QI_COST.")
                .defineInRange("nearbyExplosionTriggerQiCost", 18, 0, 10000);
        TALISMAN_REDSTONE_FALLING_EDGE_TRIGGER_QI_COST = builder.comment("Qi cost for the REDSTONE_FALLING_EDGE talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.REDSTONE_FALLING_EDGE_TRIGGER_QI_COST.")
                .defineInRange("redstoneFallingEdgeTriggerQiCost", 12, 0, 10000);
        TALISMAN_DAWN_TRIGGER_QI_COST = builder.comment("Qi cost for the DAWN talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.DAWN_TRIGGER_QI_COST.")
                .defineInRange("dawnTriggerQiCost", 11, 0, 10000);
        TALISMAN_NEARBY_NATURAL_LIGHTNING_TRIGGER_QI_COST = builder.comment("Qi cost for the NEARBY_NATURAL_LIGHTNING talisman glyph (trigger). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.NEARBY_NATURAL_LIGHTNING_TRIGGER_QI_COST.")
                .defineInRange("nearbyNaturalLightningTriggerQiCost", 13, 0, 10000);
        TALISMAN_STONE_ORB_QI_COST = builder.comment("Qi cost for the STONE_ORB talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.STONE_ORB_QI_COST.")
                .defineInRange("stoneOrbQiCost", 21, 0, 10000);
        TALISMAN_HEAVEN_DRILL_QI_COST = builder.comment("Qi cost for the HEAVEN_DRILL talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.HEAVEN_DRILL_QI_COST.")
                .defineInRange("heavenDrillQiCost", 30, 0, 10000);
        TALISMAN_CRIMSON_LOTUS_DOMAIN_QI_COST = builder.comment("Qi cost for the CRIMSON_LOTUS_DOMAIN talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.CRIMSON_LOTUS_DOMAIN_QI_COST.")
                .defineInRange("crimsonLotusDomainQiCost", 32, 0, 10000);
        TALISMAN_THUNDER_ORB_QI_COST = builder.comment("Qi cost for the THUNDER_ORB talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.THUNDER_ORB_QI_COST.")
                .defineInRange("thunderOrbQiCost", 25, 0, 10000);
        TALISMAN_STARFALL_QI_COST = builder.comment("Qi cost for the STARFALL talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.STARFALL_QI_COST.")
                .defineInRange("starfallQiCost", 34, 0, 10000);
        TALISMAN_SWORDMARK_QI_COST = builder.comment("Qi cost for the SWORDMARK talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SWORDMARK_QI_COST.")
                .defineInRange("swordmarkQiCost", 27, 0, 10000);
        TALISMAN_SURGING_TIDE_QI_COST = builder.comment("Qi cost for the SURGING_TIDE talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SURGING_TIDE_QI_COST.")
                .defineInRange("surgingTideQiCost", 24, 0, 10000);
        TALISMAN_VERDANT_SEED_QI_COST = builder.comment("Qi cost for the VERDANT_SEED talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.VERDANT_SEED_QI_COST.")
                .defineInRange("verdantSeedQiCost", 23, 0, 10000);
        TALISMAN_WATER_PRISON_QI_COST = builder.comment("Qi cost for the WATER_PRISON talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.WATER_PRISON_QI_COST.")
                .defineInRange("waterPrisonQiCost", 26, 0, 10000);
        TALISMAN_FROST_WALL_QI_COST = builder.comment("Qi cost for the FROST_WALL talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.FROST_WALL_QI_COST.")
                .defineInRange("frostWallQiCost", 22, 0, 10000);
        TALISMAN_POLARITY_FIELD_QI_COST = builder.comment("Qi cost for the POLARITY_FIELD talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.POLARITY_FIELD_QI_COST.")
                .defineInRange("polarityFieldQiCost", 24, 0, 10000);
        TALISMAN_SPELLRETURN_MIRROR_QI_COST = builder.comment("Qi cost for the SPELLRETURN_MIRROR talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SPELLRETURN_MIRROR_QI_COST.")
                .defineInRange("spellreturnMirrorQiCost", 28, 0, 10000);
        TALISMAN_FLAME_STEP_QI_COST = builder.comment("Qi cost for the FLAME_STEP talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.FLAME_STEP_QI_COST.")
                .defineInRange("flameStepQiCost", 18, 0, 10000);
        TALISMAN_BLOOD_SPIKE_QI_COST = builder.comment("Qi cost for the BLOOD_SPIKE talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.BLOOD_SPIKE_QI_COST.")
                .defineInRange("bloodSpikeQiCost", 25, 0, 10000);
        TALISMAN_THUNDERSTEP_QI_COST = builder.comment("Qi cost for the THUNDERSTEP talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.THUNDERSTEP_QI_COST.")
                .defineInRange("thunderstepQiCost", 30, 0, 10000);
        TALISMAN_MIRRORWATER_CLONE_QI_COST = builder.comment("Qi cost for the MIRRORWATER_CLONE talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.MIRRORWATER_CLONE_QI_COST.")
                .defineInRange("mirrorwaterCloneQiCost", 27, 0, 10000);
        TALISMAN_FROSTMIRROR_AEGIS_QI_COST = builder.comment("Qi cost for the FROSTMIRROR_AEGIS talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.FROSTMIRROR_AEGIS_QI_COST.")
                .defineInRange("frostmirrorAegisQiCost", 26, 0, 10000);
        TALISMAN_WOODEN_DAO_SOLDIER_QI_COST = builder.comment("Qi cost for the WOODEN_DAO_SOLDIER talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.WOODEN_DAO_SOLDIER_QI_COST.")
                .defineInRange("woodenDaoSoldierQiCost", 30, 0, 10000);
        TALISMAN_REJUVENATION_ALTAR_QI_COST = builder.comment("Qi cost for the REJUVENATION_ALTAR talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.REJUVENATION_ALTAR_QI_COST.")
                .defineInRange("rejuvenationAltarQiCost", 26, 0, 10000);
        TALISMAN_FIVE_PEAKS_SEAL_QI_COST = builder.comment("Qi cost for the FIVE_PEAKS_SEAL talisman glyph (manifestation). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.FIVE_PEAKS_SEAL_QI_COST.")
                .defineInRange("fivePeaksSealQiCost", 32, 0, 10000);
        TALISMAN_AREA_PLUS_HALF_QI_COST = builder.comment("Qi cost for the AREA_PLUS_HALF talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.AREA_PLUS_HALF_QI_COST.")
                .defineInRange("areaPlusHalfQiCost", 17, 0, 10000);
        TALISMAN_RANGE_PLUS_HALF_QI_COST = builder.comment("Qi cost for the RANGE_PLUS_HALF talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.RANGE_PLUS_HALF_QI_COST.")
                .defineInRange("rangePlusHalfQiCost", 15, 0, 10000);
        TALISMAN_PROJECTILE_SPEED_PLUS_HALF_QI_COST = builder.comment("Qi cost for the PROJECTILE_SPEED_PLUS_HALF talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.PROJECTILE_SPEED_PLUS_HALF_QI_COST.")
                .defineInRange("projectileSpeedPlusHalfQiCost", 16, 0, 10000);
        TALISMAN_SCALE_PLUS_HALF_QI_COST = builder.comment("Qi cost for the SCALE_PLUS_HALF talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.SCALE_PLUS_HALF_QI_COST.")
                .defineInRange("scalePlusHalfQiCost", 18, 0, 10000);
        TALISMAN_ECHO_QI_COST = builder.comment("Qi cost for the ECHO talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.ECHO_QI_COST.")
                .defineInRange("echoQiCost", 24, 0, 10000);
        TALISMAN_REPEL_QI_COST = builder.comment("Qi cost for the REPEL talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.REPEL_QI_COST.")
                .defineInRange("repelQiCost", 17, 0, 10000);
        TALISMAN_LIFEDRAIN_QI_COST = builder.comment("Qi cost for the LIFEDRAIN talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.LIFEDRAIN_QI_COST.")
                .defineInRange("lifedrainQiCost", 22, 0, 10000);
        TALISMAN_ARMORBREAK_QI_COST = builder.comment("Qi cost for the ARMORBREAK talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.ARMORBREAK_QI_COST.")
                .defineInRange("armorbreakQiCost", 18, 0, 10000);
        TALISMAN_MERIDIAN_SEAL_QI_COST = builder.comment("Qi cost for the MERIDIAN_SEAL talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.MERIDIAN_SEAL_QI_COST.")
                .defineInRange("meridianSealQiCost", 20, 0, 10000);
        TALISMAN_BARRIER_DAMAGE_X2_QI_COST = builder.comment("Qi cost for the BARRIER_DAMAGE_X2 talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.BARRIER_DAMAGE_X2_QI_COST.")
                .defineInRange("barrierDamageX2QiCost", 22, 0, 10000);
        TALISMAN_CLEANSE_QI_COST = builder.comment("Qi cost for the CLEANSE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.CLEANSE_QI_COST.")
                .defineInRange("cleanseQiCost", 16, 0, 10000);
        TALISMAN_PROLIFERATE_QI_COST = builder.comment("Qi cost for the PROLIFERATE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.PROLIFERATE_QI_COST.")
                .defineInRange("proliferateQiCost", 22, 0, 10000);
        TALISMAN_PRESERVE_TERRAIN_QI_COST = builder.comment("Qi cost for the PRESERVE_TERRAIN talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.PRESERVE_TERRAIN_QI_COST.")
                .defineInRange("preserveTerrainQiCost", 8, 0, 10000);
        TALISMAN_RELOCATE_QI_COST = builder.comment("Qi cost for the RELOCATE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.RELOCATE_QI_COST.")
                .defineInRange("relocateQiCost", 23, 0, 10000);
        TALISMAN_IGNITE_QI_COST = builder.comment("Qi cost for the IGNITE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.IGNITE_QI_COST.")
                .defineInRange("igniteQiCost", 14, 0, 10000);
        TALISMAN_CHILL_QI_COST = builder.comment("Qi cost for the CHILL talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.CHILL_QI_COST.")
                .defineInRange("chillQiCost", 14, 0, 10000);
        TALISMAN_EXECUTE_QI_COST = builder.comment("Qi cost for the EXECUTE talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.EXECUTE_QI_COST.")
                .defineInRange("executeQiCost", 20, 0, 10000);
        TALISMAN_KNOCKUP_QI_COST = builder.comment("Qi cost for the KNOCKUP talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.KNOCKUP_QI_COST.")
                .defineInRange("knockupQiCost", 16, 0, 10000);
        TALISMAN_WEAKNESS_QI_COST = builder.comment("Qi cost for the WEAKNESS talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.WEAKNESS_QI_COST.")
                .defineInRange("weaknessQiCost", 15, 0, 10000);
        TALISMAN_GLOWING_MARK_QI_COST = builder.comment("Qi cost for the GLOWING_MARK talisman glyph (evolution). Wired 2026-09-02 via TalismanGlyphCatalogMixin, TalismanLimits.GLOWING_MARK_QI_COST.")
                .defineInRange("glowingMarkQiCost", 12, 0, 10000);
        builder.pop(); // glyphCosts

        builder.pop(); // talisman

        SPEC = builder.build();
    }

    // ══════════════════════════════════════════════════════════════════
    //  CLIENT CONFIG (UI / ACCESSIBILITY)
    // ══════════════════════════════════════════════════════════════════
    public static final ForgeConfigSpec.IntValue CLIENT_HUD_X;
    public static final ForgeConfigSpec.IntValue CLIENT_HUD_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_HUD_TEXT_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_REALM_TEXT_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_QI_BAR_TOP_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_QI_BAR_BOTTOM_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_CULT_BAR_TOP_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_CULT_BAR_BOTTOM_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_BG_PAGE_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_BG_PANEL_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_INK_BLACK_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_INK_SOFT_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_GOLD_TEXT_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_VERMILLION_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_BORDER_LIGHT_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_BORDER_DARK_COLOR;
    public static final ForgeConfigSpec.DoubleValue CLIENT_BG_OPACITY;
    public static final ForgeConfigSpec.BooleanValue CLIENT_HIGH_CONTRAST_MODE;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_TOOLTIP_MEMORY;
    public static final ForgeConfigSpec.BooleanValue CLIENT_REDUCE_MOTION;
    public static final ForgeConfigSpec.IntValue CLIENT_FONT_SIZE_PERCENT;
    public static final ForgeConfigSpec.BooleanValue CLIENT_COMPACT_LAYOUT;
    public static final ForgeConfigSpec.BooleanValue CLIENT_ENABLE_CULTIVATION_PANEL_TOOLTIPS;
    // Config-screen visual theme system
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_THEME;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_TAB_ANIMATION_STYLE;
    public static final ForgeConfigSpec.IntValue CLIENT_UI_TAB_ANIMATION_SPEED_PERCENT;
    public static final ForgeConfigSpec.BooleanValue CLIENT_UI_ACCENT_OVERRIDE_ENABLED;
    public static final ForgeConfigSpec.IntValue CLIENT_UI_ACCENT_OVERRIDE_COLOR;
    // How aggressively CultivationTextStyler bolds/colors keywords (Qi, spell,
    // damage, realm, ...) inside entry descriptions and tooltips across the
    // WHOLE config screen - not just this popup's own text. See that class's
    // doc for why "full" (the old, only, hardcoded behavior) reads as
    // over-bolded on a real description with several keywords in one sentence.
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_DESC_HIGHLIGHT_INTENSITY;
    // Config-screen UI sound effects (tab hover/click, entry hover, config-open)
    public static final ForgeConfigSpec.BooleanValue CLIENT_UI_SOUND_ENABLED;
    public static final ForgeConfigSpec.IntValue CLIENT_UI_SOUND_VOLUME_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_SOUND_TAB_HOVER;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_SOUND_ENTRY_HOVER;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_SOUND_TAB_CLICK;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_UI_SOUND_CONFIG_OPEN;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_FAVORITES;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_LOCKED_CONFIGS;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_CONFIG_HISTORY;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_SEARCH_HISTORY;
    public static final ForgeConfigSpec.BooleanValue CLIENT_REMOVE_VANILLA_DIFFICULTY_BUTTON;
    // Batch 11: Per-world overrides, custom tabs, tab reordering, notifications, inline help
    public static final ForgeConfigSpec.BooleanValue CLIENT_ENABLE_NOTIFICATIONS;
    public static final ForgeConfigSpec.BooleanValue CLIENT_ENABLE_INLINE_HELP;
    public static final ForgeConfigSpec.BooleanValue CLIENT_ENABLE_PER_WORLD_OVERRIDES;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_TAB_ORDER;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_CUSTOM_TABS;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_PER_WORLD_OVERRIDES;
    public static final ForgeConfigSpec.ConfigValue<String> CLIENT_CONFIG_DEPENDENCIES;
    public static final ForgeConfigSpec.BooleanValue CLIENT_HUD_VISIBLE;
    public static final ForgeConfigSpec.IntValue CLIENT_PORTRAIT_SIZE;
    public static final ForgeConfigSpec.IntValue CLIENT_STATUS_BAR_ROW_HEIGHT;
    public static final ForgeConfigSpec.IntValue CLIENT_HUD_BAR_WIDTH;
    // Bar-style health/hunger/air HUD
    public static final ForgeConfigSpec.BooleanValue CLIENT_ENABLE_BAR_HUD;
    public static final ForgeConfigSpec.IntValue CLIENT_HEALTH_BAR_TOP_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_HEALTH_BAR_BOTTOM_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_HUNGER_BAR_TOP_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_HUNGER_BAR_BOTTOM_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_AIR_BAR_TOP_COLOR;
    public static final ForgeConfigSpec.IntValue CLIENT_AIR_BAR_BOTTOM_COLOR;
    // Per-element positioning configs
    public static final ForgeConfigSpec.IntValue CLIENT_REALM_NAME_X;
    public static final ForgeConfigSpec.IntValue CLIENT_REALM_NAME_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_QI_BAR_X;
    public static final ForgeConfigSpec.IntValue CLIENT_QI_BAR_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_CULT_BAR_X;
    public static final ForgeConfigSpec.IntValue CLIENT_CULT_BAR_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_SPELL_GRID_X;
    public static final ForgeConfigSpec.IntValue CLIENT_SPELL_GRID_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_INFO_TEXT_X;
    public static final ForgeConfigSpec.IntValue CLIENT_INFO_TEXT_Y;
    public static final ForgeConfigSpec.IntValue CLIENT_PORTRAIT_X;
    public static final ForgeConfigSpec.IntValue CLIENT_PORTRAIT_Y;

    static {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

        clientBuilder.comment("Xiaoxiang Config Extension - Client UI / Accessibility options")
                     .push("client");

        // ═══ General Toggles - first thing users see ═══
        clientBuilder.comment("General toggles for the cultivation config extension.")
                      .push("general");
        CLIENT_REMOVE_VANILLA_DIFFICULTY_BUTTON = clientBuilder.comment(
                "Remove the vanilla Minecraft difficulty button on the Create World screen. "
                + "When enabled, the vanilla difficulty button is hidden. "
                + "When disabled (default), the vanilla difficulty button remains in the Game tab.")
                .define("removeVanillaDifficultyButton", false);
        clientBuilder.pop();

        clientBuilder.comment("HUD positioning and dimensions.")
                      .push("hud");
        CLIENT_HUD_VISIBLE = clientBuilder.comment("Show or hide the in-game cultivation HUD.").define("visible", true);
        CLIENT_HUD_X = clientBuilder.comment("HUD X position on screen. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("x", 6, 0, 10000);
        CLIENT_HUD_Y = clientBuilder.comment("HUD Y position on screen. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("y", 6, 0, 10000);
        CLIENT_HUD_BAR_WIDTH = clientBuilder.comment("Width of qi/cultivation bars in HUD.").defineInRange("barWidth", 72, 20, 500);

        // Bar-style health/hunger/air HUD
        CLIENT_ENABLE_BAR_HUD = clientBuilder.comment("Replace vanilla hearts/food with bar-style HUD matching the cultivation mod aesthetic.").define("enableBarHud", true);
        CLIENT_HEALTH_BAR_TOP_COLOR = clientBuilder.comment("Health bar top gradient color.").defineInRange("healthBarTop", 0xFFE03030, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_HEALTH_BAR_BOTTOM_COLOR = clientBuilder.comment("Health bar bottom gradient color.").defineInRange("healthBarBottom", 0xFF801010, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_HUNGER_BAR_TOP_COLOR = clientBuilder.comment("Hunger bar top gradient color.").defineInRange("hungerBarTop", 0xFFE0A030, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_HUNGER_BAR_BOTTOM_COLOR = clientBuilder.comment("Hunger bar bottom gradient color.").defineInRange("hungerBarBottom", 0xFF806010, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_AIR_BAR_TOP_COLOR = clientBuilder.comment("Air bar top gradient color.").defineInRange("airBarTop", 0xFF30B0E0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_AIR_BAR_BOTTOM_COLOR = clientBuilder.comment("Air bar bottom gradient color.").defineInRange("airBarBottom", 0xFF106080, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_PORTRAIT_SIZE = clientBuilder.comment("Size of the portrait icon in HUD. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("portraitSize", 23, 8, 100);
        clientBuilder.pop();

        // Per-element positioning within the cultivation panel
        clientBuilder.comment("Per-element positioning within the cultivation panel. Adjust X/Y offsets to move individual elements. 0 = default position.")
                      .push("elementPos");
        CLIENT_REALM_NAME_X = clientBuilder.comment("Realm name X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("realmNameX", 0, -200, 200);
        CLIENT_REALM_NAME_Y = clientBuilder.comment("Realm name Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("realmNameY", 0, -200, 200);
        CLIENT_QI_BAR_X = clientBuilder.comment("Qi bar X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("qiBarX", 0, -200, 200);
        CLIENT_QI_BAR_Y = clientBuilder.comment("Qi bar Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("qiBarY", 0, -200, 200);
        CLIENT_CULT_BAR_X = clientBuilder.comment("Cultivation bar X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("cultBarX", 0, -200, 200);
        CLIENT_CULT_BAR_Y = clientBuilder.comment("Cultivation bar Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("cultBarY", 0, -200, 200);
        CLIENT_SPELL_GRID_X = clientBuilder.comment("Spell grid X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("spellGridX", 0, -200, 200);
        CLIENT_SPELL_GRID_Y = clientBuilder.comment("Spell grid Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("spellGridY", 0, -200, 200);
        CLIENT_INFO_TEXT_X = clientBuilder.comment("Info text block X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("infoTextX", 0, -200, 200);
        CLIENT_INFO_TEXT_Y = clientBuilder.comment("Info text block Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("infoTextY", 0, -200, 200);
        CLIENT_PORTRAIT_X = clientBuilder.comment("Portrait X offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("portraitX", 0, -200, 200);
        CLIENT_PORTRAIT_Y = clientBuilder.comment("Portrait Y offset from default position. Not yet wired to the base mod's HUD rendering (position-offset fields need per-element bytecode slicing not yet done safely) - tracked in CONFIG_AUDIT.md; changing this currently has no visible effect.").defineInRange("portraitY", 0, -200, 200);
        clientBuilder.pop();

        clientBuilder.comment("Status bar layout in the panel.")
                      .push("statusBar");
        CLIENT_STATUS_BAR_ROW_HEIGHT = clientBuilder.comment("Row height for status bars in pixels.").defineInRange("rowHeight", 10, 5, 30);
        clientBuilder.pop();

        clientBuilder.comment("Color customization. Values are ARGB hex (e.g. -1 = white, -16777216 = black, -256 = yellow). Use a color picker to find values.")
                      .push("colors");
        CLIENT_HUD_TEXT_COLOR = clientBuilder.comment("Default HUD text color.").defineInRange("hudText", -1456016, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_REALM_TEXT_COLOR = clientBuilder.comment("Realm name text color in the panel.").defineInRange("realmText", -7644652, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_QI_BAR_TOP_COLOR = clientBuilder.comment("Qi bar top gradient color.").defineInRange("qiBarTop", -9583434, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_QI_BAR_BOTTOM_COLOR = clientBuilder.comment("Qi bar bottom gradient color.").defineInRange("qiBarBottom", -13729678, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_CULT_BAR_TOP_COLOR = clientBuilder.comment("Cultivation bar top gradient color.").defineInRange("cultBarTop", -928374, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_CULT_BAR_BOTTOM_COLOR = clientBuilder.comment("Cultivation bar bottom gradient color.").defineInRange("cultBarBottom", -3631046, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_GOLD_TEXT_COLOR = clientBuilder.comment("Gold text color used for headings and emphasis.").defineInRange("goldText", -1456016, Integer.MIN_VALUE, Integer.MAX_VALUE);
        // VERIFIED 2026-09-01: the base mod's own INK_BLACK constant that this field's
        // value was meant to match is not referenced anywhere in CultivationHud's
        // bytecode - it's dead in the base mod itself, not just unwired here. This
        // field currently has no effect; left in place (not removed) so a future
        // update to the base mod that starts using its own INK_BLACK constant has
        // somewhere ready to hook into, rather than deleting a slot that might
        // become real again.
        CLIENT_INK_BLACK_COLOR = clientBuilder.comment("Ink black color for primary text. Currently has no effect - " +
                "verified (2026-09-01) that the base mod's own matching constant is itself unused, so there is nothing to override.")
                .defineInRange("inkBlack", -16448509, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_INK_SOFT_COLOR = clientBuilder.comment("Soft ink color for secondary text.").defineInRange("inkSoft", -12766422, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_VERMILLION_COLOR = clientBuilder.comment("Vermillion (red-orange) accent color.").defineInRange("vermillion", -4703686, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BORDER_LIGHT_COLOR = clientBuilder.comment("Light border color for panels.").defineInRange("borderLight", -2504802, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BORDER_DARK_COLOR = clientBuilder.comment("Dark border color for panels.").defineInRange("borderDark", -10859978, Integer.MIN_VALUE, Integer.MAX_VALUE);
        clientBuilder.pop();

        clientBuilder.comment("Background plate customization for the cultivation panel.")
                      .push("background");
        CLIENT_BG_PAGE_COLOR = clientBuilder.comment("Background page color (the main backdrop).").defineInRange("pageColor", -923956, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BG_PANEL_COLOR = clientBuilder.comment("Inner panel background color.").defineInRange("panelColor", -1517128, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BG_OPACITY = clientBuilder.comment("Background opacity. 1.0 = fully opaque, 0.5 = semi-transparent.").defineInRange("opacity", 1.0, 0.0, 1.0);
        clientBuilder.pop();

        clientBuilder.comment("Accessibility options for visually impaired players.")
                      .push("accessibility");
        CLIENT_HIGH_CONTRAST_MODE = clientBuilder.comment("If true, enables high-contrast colors for better readability.").define("highContrastMode", false);
        clientBuilder.pop();

        clientBuilder.comment("Tooltip window position/shape memory. Format: title|x|y|w|h;title|x|y|w|h;...")
                      .push("tooltipMemory");
        CLIENT_TOOLTIP_MEMORY = clientBuilder.comment("Stores pinned tooltip window positions and sizes so they persist across game restarts.").define("memory", "");
        clientBuilder.pop();

        clientBuilder.comment("Accessibility and UI preferences.")
                      .push("uiPrefs");
        CLIENT_REDUCE_MOTION = clientBuilder.comment("If true, disables all animations (tab glow, value transitions, etc.) for users who prefer static UI or have motion sensitivity.").define("reduceMotion", false);
        CLIENT_FONT_SIZE_PERCENT = clientBuilder.comment("Font size percentage for the config screen (100 = default, 80 = compact, 120 = large).").defineInRange("fontSizePercent", 100, 50, 200);
        CLIENT_COMPACT_LAYOUT = clientBuilder.comment("If true, uses compact spacing to show more entries. If false, uses comfortable spacing with more breathing room.").define("compactLayout", false);
        CLIENT_ENABLE_CULTIVATION_PANEL_TOOLTIPS = clientBuilder.comment(
                "If true, hovering the character info fields on the Cultivation panel (G key) shows an explanatory tooltip. "
                + "Adds tooltips for Name, Race, Realm, Morality, Bone Age, Lifespan and the HP / Cultivation / Qi bars, "
                + "plus a fallback description for the Identity row before an identity has been drawn. "
                + "Gender, Spirit Root and Physique already have tooltips from the base mod and are left untouched.")
                .define("enableCultivationPanelTooltips", true);
        clientBuilder.pop();

        clientBuilder.comment("Config screen visual theme (the \"skin\" of the config menu itself - colors, "
                + "borders, chrome - not the game's own UI). Independent of reduceMotion above.")
                      .push("uiTheme");
        CLIENT_UI_THEME = clientBuilder.comment(
                "Which visual theme the config screen chrome uses. Valid values: classic, "
                + "cultivation_scroll, celestial_neon, medieval_manuscript, demonic_blood, "
                + "celestial_jade, formation_talisman, void_abyss. Unknown/empty values fall back "
                + "to classic. Also changeable in-game from the gear icon next to the config title.")
                .define("theme", "classic");
        CLIENT_UI_TAB_ANIMATION_STYLE = clientBuilder.comment(
                "Animation style played when a tab/sub-tab/group is clicked and its entries change. "
                + "Valid values: slide (default - rows fade + slide in from the left), "
                + "fade (rows fade in place, no slide), none (entries appear instantly).")
                .define("tabAnimationStyle", "slide");
        CLIENT_UI_TAB_ANIMATION_SPEED_PERCENT = clientBuilder.comment(
                "Speed of the tab-click entrance animation as a percentage of the default duration. "
                + "100 = default speed, 50 = twice as fast, 200 = twice as slow. Ignored (animation "
                + "skipped entirely) when reduceMotion above is enabled or tabAnimationStyle is 'none'.")
                .defineInRange("tabAnimationSpeedPercent", 100, 10, 400);
        CLIENT_UI_ACCENT_OVERRIDE_ENABLED = clientBuilder.comment(
                "If true, accentOverrideColor below replaces the current theme's primary accent color "
                + "(tab highlights, titles) instead of using the theme's own default.")
                .define("accentOverrideEnabled", false);
        CLIENT_UI_ACCENT_OVERRIDE_COLOR = clientBuilder.comment(
                "Custom primary accent color (ARGB hex, e.g. -1 = white) used when accentOverrideEnabled is true.")
                .defineInRange("accentOverrideColor", 0xFFFFD700, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_DESC_HIGHLIGHT_INTENSITY = clientBuilder.comment(
                "How much of CultivationTextStyler's automatic keyword bold/coloring (Qi, spell, damage, "
                + "realm, and similar terms) shows up in entry descriptions and tooltips across the whole "
                + "config screen. Valid values: full (every matching keyword styled, the original always-on "
                + "behavior), reduced (only the single most cultivation-specific keyword per sentence is "
                + "styled, so a sentence with several common words like \"spell\"/\"damage\" isn't wall-to-wall "
                + "bold), off (plain text, no keyword styling at all - number coloring still applies).")
                .define("descHighlightIntensity", "full");
        clientBuilder.pop();

        clientBuilder.comment("Config screen UI sound effects: hover/click feedback for tabs and entry "
                + "rows, and a sound when the config screen first opens. Each of the four slots below "
                + "takes a sound id from SoundPresets (in-game: the Sound Cycler in the theme gear popup) "
                + "or an empty string for silence. Tab hover and entry hover are intentionally separate "
                + "slots so they are never forced to share a sound, and click is separate from both so "
                + "hovering and clicking never sound the same either.")
                      .push("uiSound");
        CLIENT_UI_SOUND_ENABLED = clientBuilder.comment(
                "Master on/off switch for all config-screen UI sounds. When false, none of the four "
                + "sound slots below play, regardless of their individual settings.")
                .define("enabled", true);
        CLIENT_UI_SOUND_VOLUME_PERCENT = clientBuilder.comment(
                "Volume for config-screen UI sounds as a percentage (100 = full volume, 0 = silent). "
                + "Applies on top of the enabled toggle above and the game's own master/UI volume sliders.")
                .defineInRange("volumePercent", 60, 0, 100);
        CLIENT_UI_SOUND_TAB_HOVER = clientBuilder.comment(
                "Sound id played when the mouse first moves onto a main tab or sub-tab (not repeated "
                + "while it stays hovered). Empty string = silent. Deliberately a different slot from "
                + "entryHover below so the two can never be forced to share a sound.")
                .define("tabHover", "");
        CLIENT_UI_SOUND_ENTRY_HOVER = clientBuilder.comment(
                "Sound id played when the mouse first moves onto a configurable entry row (not repeated "
                + "while it stays hovered). Empty string = silent. Deliberately a different slot from "
                + "tabHover above so the two can never be forced to share a sound.")
                .define("entryHover", "");
        CLIENT_UI_SOUND_TAB_CLICK = clientBuilder.comment(
                "Sound id played when a main tab or sub-tab is clicked. Empty string = silent. "
                + "Deliberately a different slot from tabHover above so clicking never sounds the same "
                + "as hovering.")
                .define("tabClick", "");
        CLIENT_UI_SOUND_CONFIG_OPEN = clientBuilder.comment(
                "Sound id played once when the config screen first opens (e.g. from the mod list's "
                + "\"Config\" button), before any tab is shown. Empty string = silent.")
                .define("configOpen", "");
        clientBuilder.pop();

        clientBuilder.comment("User favorites and locks.")
                      .push("userData");
        CLIENT_FAVORITES = clientBuilder.comment("Comma-separated list of favorited config paths.").define("favorites", "");
        CLIENT_LOCKED_CONFIGS = clientBuilder.comment("Comma-separated list of locked config paths (cannot be accidentally changed).").define("lockedConfigs", "");
        CLIENT_CONFIG_HISTORY = clientBuilder.comment("JSON array of recent config changes for undo/history.").define("configHistory", "[]");
        CLIENT_SEARCH_HISTORY = clientBuilder.comment("Comma-separated list of recent search queries.").define("searchHistory", "");
        clientBuilder.pop();

        // Batch 11: Advanced UI features
        clientBuilder.comment("Advanced UI features: notifications, inline help, per-world overrides, custom tabs, tab reordering.")
                      .push("advanced");
        CLIENT_ENABLE_NOTIFICATIONS = clientBuilder.comment("If true, shows toast notifications when config values change.").define("enableNotifications", true);
        CLIENT_ENABLE_INLINE_HELP = clientBuilder.comment("If true, shows inline help (?) icons next to config entries with additional context.").define("enableInlineHelp", true);
        CLIENT_ENABLE_PER_WORLD_OVERRIDES = clientBuilder.comment("If true, allows per-world config overrides that apply only when a specific world is loaded.").define("enablePerWorldOverrides", false);
        CLIENT_TAB_ORDER = clientBuilder.comment("Comma-separated list of top-level tab names in custom display order. Empty = default order.").define("tabOrder", "");
        CLIENT_CUSTOM_TABS = clientBuilder.comment("JSON array of custom tab definitions: [{\"name\":\"My Tab\",\"paths\":[\"realms\",\"spells\"]}].").define("customTabs", "[]");
        CLIENT_PER_WORLD_OVERRIDES = clientBuilder.comment("JSON object of per-world config overrides: {\"world_name\":{\"config.path\":\"value\"}}.").define("perWorldOverrides", "{}");
        CLIENT_CONFIG_DEPENDENCIES = clientBuilder.comment("JSON array of config dependency rules: [{\"if\":\"pathA==value\",\"then\":\"pathB\",\"action\":\"show\"}].").define("configDependencies", "[]");
        clientBuilder.pop();

        clientBuilder.pop(); // client

        CLIENT_SPEC = clientBuilder.build();
    }

    private ExtendedConfig() {}
}
