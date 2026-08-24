# Xiaoxiang Config Extension - Build Instructions

## Version

Current version: 3.0 (full feature implementation - all 15 batches complete)
Version 1 backup: `backups/xiaoxiang_config_ext-v1-fallback.jar`
Version 1 source: `backups/v1-source/`

## v3.0 Changes (Batches 11-13)

- **Per-world overrides**: Different config values per world, auto-applied on world load (PerWorldOverrideManager)
- **Custom tabs**: User-defined tabs aggregating configs from multiple categories (CustomTabManager)
- **Tab reordering**: Right-click tab to move right, Shift+right-click to move left (persisted)
- **Config dependencies**: Show/hide/enable/disable configs based on other config values (ConfigDependencyManager)
- **Notifications**: Toast-style notifications in top-right corner for value changes, saves, warnings (NotificationSystem)
- **Inline help**: (?) icons next to entries with descriptions, hover for full help tooltip
- **Value context preview**: Tooltips show practical meaning of values (e.g., "Each hit deals 10 base damage")
- **Color wheel**: HSV color picker popup, right-click on color swatch to open (ColorWheelPopup)
- **Dropdown enums**: Dropdown popup for enum-like config values (DropdownEnumPopup)
- **Multi-line editor**: Full editor for long string values, right-click on value text (MultiLineEditor)
- **Auto-resize tooltips**: Tooltips automatically resize to fit content width
- **Modpack presets**: Save/load preset configurations, "SavePreset" and "Preset" buttons (ModpackPresets)
- **Minimap navigation**: Compact tree view showing current position in config hierarchy (MinimapNav)

## v2.1 Changes (Batches 1-10)

- **Origin crash fix**: `onCreate()` method now found via SRG name `m_100972_` (production) as well as dev name
- **GameTabMixin fix**: ClassCastException defensive handling for CycleButton value type
- **Layout fix**: Top tabs moved to Y=44 to clear filter row; vanilla reset button removed and merged into util buttons
- **Item picker**: Double-click-to-add, preview panel with item icon + tooltip, "All" mode hides sub-cat panel, expanded item categories (337 items scanned)
- **Tab animations**: Ping-pong wave (top tabs, cyan), orbital animation (sub-tabs, magenta), pulsing border glow (group buttons, yellow)
- **Tooltip keyword system**: Glossary of 50+ cultivation terms, keywords underlined+colored in tooltips, 3-second hover popup, click to pin
- **Value editing**: Alt = x100000 multiplier, auto-round decimals to 2 places, compact number display (K/M/B), max value display
- **Collapsible sections**: Group headers with collapse/expand, smart sorting (favorites > modified > realm order > alphabetical)
- **Value change flash**: Green flash animation when value changes
- **Save animation**: Green confirmation banner when Save button clicked
- **Font size + compact layout**: Wired to config entries (CLIENT_FONT_SIZE_PERCENT, CLIENT_COMPACT_LAYOUT)
- **Complete GoldenCoreDao configs**: Added bodyDefenseBonus, cultivationEfficiencyBonus, qiRecoveryPerSecondBonus, meleeDamageBonus, shatterCoreTrialReflectionRatio

## Project Location

`C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod`

## Prerequisites

- JDK 17 (Temurin) installed at `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot`
- Gradle 8.8 (included via wrapper)
- ForgeGradle 6.0+ with MixinGradle 0.7.38

## Build Commands

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
cd "C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod"
.\gradlew.bat clean build
```

## Output

The built JAR is at `build\libs\xiaoxiang_config_ext-1.0.0.jar`

## Installation

Copy the JAR to:
`C:\Users\YoungLorde\AppData\Roaming\ATLauncher\instances\XiaoCultivationWorld\mods\`

## Config File

After first launch, the config is generated at:
`C:\Users\YoungLorde\AppData\Roaming\ATLauncher\instances\XiaoCultivationWorld\config\xiaoxiang_config_ext-common.toml`

## Config Screen

The "Configured" mod (already in the modpack) provides a GUI config screen.
Access it from: Mods menu -> Xiaoxiang Config Extension -> Config button

## Mod Architecture

- **Main class**: `com.xiaoxiang.configext.XiaoxiangConfigExt`
- **Config**: `com.xiaoxiang.configext.config.ExtendedConfig` (ForgeConfigSpec with 300+ options across 25+ categories)
  - COMMON spec: all gameplay values (realms, spells, weapons, pills, alchemy, refining, spirit plants, spirit veins, techniques, spirit roots, physiques, Foundation/Golden Core Dao, identity, progression, NPC combat, formations, sects, loot, trials, qi system, passive spells, effects, morality, lifespan)
  - CLIENT spec: UI/accessibility options (text size, font, colors, background plate for cultivation panel)
- **Mixins**: `com.xiaoxiang.configext.mixin.*` (17 registered mixins intercepting hardcoded values)
- **Event handler**: `com.xiaoxiang.configext.BeastCultivationExtensionHandler` (extends beast cultivation to monsters)

## Mixin Targets

1. `RealmMixin` -> `Realm.maxQi()`, `baseLifespan()`, `qiShieldReductionPercent()`, `tribulationStrikeDamage()` (with global multipliers)
2. `BeastRealmMixin` -> `BeastRealm.advanceCost()`
3. `BeastCultivationHandlerMixin` -> `BeastCultivationHandler.updateBeast()`, `onLivingTick()` check interval
4. `CultivatorRealmRollerMixin` -> `CultivatorRealmRoller.roll()` (NPC realm distribution)
5. `WanderingCultivatorEntityMixin` -> spawn chance constants
6. `BiomeQiProfileMixin` -> `BiomeQiProfile.of()` (biome qi density)
7. `SpellMixin` -> `Spell.damage()`, `Spell.qiCost()` (global multipliers)
8. `AlchemyRankMixin` -> `AlchemyRank.xpGainFor()`, `xpGainForFailure()`
9. `RefiningRankMixin` -> `RefiningRank.xpGainFor()`, `xpGainForFailure()`
10. `SpiritVeinCoreTierMixin` -> `SpiritVeinCoreTier.maxQi()`, `orbGain()`, `supplyPerSecond()`
11. `PhysiqueMixin` -> `Physique.weightOf()` (rarity weights)
12. `IdentityMixin` -> `Identity.lifespanRange()` (starting lifespan by identity category)
13. `TieredWeaponMixin` -> `SpiritSwordItem.spellQiCostReductionPct()` (tier-based qi cost reduction)
14. `StorageBagItemMixin` -> `StorageBagItem.columsFor()`, `rowsFor()` (bag dimensions)
15. `AlchemyCoreBlockEntityMixin` -> `AlchemyCoreBlockEntity.getMaxQi()` (furnace qi storage)
16. `RefiningCoreBlockEntityMixin` -> `RefiningCoreBlockEntity.getMaxQi()` (furnace qi storage)
17. `SpiritRootBonusHelperMixin` -> `SpiritRootBonusHelper.qiAbsorptionMultiplier()` (SSR/SR multipliers)
18. `FoundationDaoMixin` -> `FoundationDao.lifespanBonus()`, `spellDamageMult()`, `spellQiCostMult()`, `hpMult()`, `bodyDefenseBonus()`, `cultivationEfficiencyBonus()`, `qiRecoveryPerSecondBonus()`, `meleeDamageBonus()`
19. `GoldenCoreDaoMixin` -> `GoldenCoreDao.lifespanBonus()`, `spellDamageMult()`, `spellQiCostMult()`, `hpMult()`, `bloodSpellDamageMult()`, `bloodSpellQiCostMult()`, `tribulationStrikes()`, `tribulationDamage()`, `shatterCoreTrialMaxHealth()`, `shatterCoreTrialRegenPerSecond()`
20. `LifespanHelperMixin` -> `LifespanHelper.isNearImmortal()`, `applyOrdinaryDeathPenalty()`
21. `MoralityHelperMixin` -> `MoralityHelper.tribulationDamageMultiplier()`, `isRighteous()`, `isEvil()`
22. `LooseImmortalBonusHelperMixin` -> `LooseImmortalBonusHelper.*ForLevel()` (all per-level bonus accessors)
23. `IdentityDrawDeckMixin` -> `IdentityDrawDeck.deckSize()`, `roundsRemaining()`
24. `PassiveSpellHandlerMixin` -> `PassiveSpellHandler` inlined constants (intervals, qi costs, radii)
25. `BloodBerserkEffectMixin` -> `BloodBerserkEffect` constructor attribute modifier values
26. `DaoHeartWoundEffectMixin` -> `DaoHeartWoundEffect` constructor attribute modifier values
27. `ShatterArmorEffectMixin` -> `ShatterArmorEffect` constructor attribute modifier values
28. `HeartDemonTrialProfileMixin` -> `HeartDemonTrialProfile.vitalityMultiplier()` (per-band overrides)
29. `InnerWorldTrialManagerMixin` -> `InnerWorldTrialManager` inlined constants (platform, soul wound, stasis)
30. `CultivationDataTimingMixin` -> `CultivationData.isAllowedTimeAccelerationMultiplier()`
31. `PhysiqueMixin` (2nd injector) -> `Physique.bonus()` (per-physique PhysiqueBonus record values: Immortal Body, Innate Sword Body, Sword Bone, Heavenly Fire Body, Mystic Ice Body, Broken Vein Body, Immortal Blood Body)
32. `PhysiqueBonusHelperMixin` -> `PhysiqueBonusHelper.spellDamageMultiplier(Physique|WanderingCultivatorEntity, Spell)` (Innate Sword non-sword penalty), `applySharedSpellDamageRules()` (Chaos Body spell damage), `cultivationRequirementMultiplier()` (Chaos Body cultivation requirement)
33. `SpiritRootMixin` -> `SpiritRoot.bonus()` (heavenly / dual / mutant / heavenly sword / heavenly hidden root multipliers)
34. `CultivationChestLootMixin` -> `CultivationChestLoot.fill()` (chest roll counts), `cultivationEntries()` (per-item loot weights)

## Config Categories (ExtendedConfig)

- **realm**: maxQi, lifespan, qiShield, tribulation damage per realm + global multipliers
- **beast**: beast cultivation advance costs and intervals
- **spawn**: NPC spawn chances and realm distribution weights
- **qi**: biome qi density profiles
- **spells**: global damage/qi cost/charge multipliers + per-spell constants
- **weapons**: global damage multiplier + tier-based qi cost reduction + special effect durations
- **pills**: qi recovery, spirit stone qi, blood burn pill, clear mind, divine stride, storage bag dimensions
- **alchemy**: furnace max qi, ticks per pill, XP gains per tier
- **refining**: furnace max qi, ticks per item, XP gains per tier, tier-up chances
- **spiritPlants**: max age, growth ticks, qi orb amounts, special plant effects
- **spiritVeins**: max qi, orb gain, supply per second per tier + radii
- **techniques**: global multipliers for qi absorb, attack, defense, HP, crit, element spell, move speed
- **spiritRoots**: heavenly/dual/mutant/sword/hidden element multipliers + qi absorption multipliers
- **physiques**: immortal body, sword body, fire/ice body, chaos body, broken vein + rarity weights
- **foundationDao**: lifespan, spell damage, qi cost, HP, defense, tribulation waves per Dao type
- **goldenCoreDao**: lifespan, spell damage, tribulation strikes, shatter core trial boss stats
- **identity**: starting lifespan ranges by identity category (martial, scholar, cultivator, abandoned)
- **progression**: bone age limits, estimate days, NPC tribulation death chance, gender edits
- **npcCombat**: dodge chance, scan ticks, reaction ticks, dodge cooldown per realm
- **formations**: core max qi, qi gathering/growth multipliers, barrier qi per damage, harvest intervals
- **sects**: power score, ancestor immortal chances, ambient scenes, shop pricing, task parameters
- **loot**: chest roll counts and item weights
- **trials**: heart demon vitality multipliers, inner world platform/soul wound/stasis
- **qiSystem**: attraction radius, meditation bonuses, qi shield, spirit stone ore max qi and regen
- **passiveSpells**: slow regen, bigu, qi mending, qi flight, item attraction, treasure seizing
- **effects**: blood berserk, dao heart wound, shatter armor, inverse five elements
- **morality**: alignment thresholds, tribulation damage scaling
- **lifespanHelper**: start bone age, age per day, near-immortal threshold, death penalty
- **client** (CLIENT spec): text size, font scale, panel colors, background plate settings

## Original Mod Dependency

The original mod JAR is in `libs/xiaoxiang_cultivation-0.1.1302.jar` for compilation.
At runtime, the original mod must be installed in the mods folder.
