package com.xiaoxiang.configext.client;

import java.util.*;

/**
 * Registry of detailed descriptions for every config option.
 * Used by the custom config screen to show rich tooltips and searchable metadata.
 *
 * Top-level tabs (see CustomConfigScreen.TAB_TO_SUBTABS for the authoritative list
 * and sub-tab breakdown - this comment only summarizes it):
 *   1. Cultivation       - realms, spells, techniques, spirit roots, physiques, Dao paths, identity
 *   2. Spells & Combat   - spells, passive spells, weapons
 *   3. Sects             - generation, shop pricing, life & population, tasks, departments, defense
 *   4. NPCs              - spawns, combat tactics, AI behavior, trades
 *   5. Beasts & Mobs     - beast cultivation, advance costs, spawns
 *   6. World             - biome qi, spirit veins, spirit plants, loot
 *   7. Crafting          - alchemy, refining, pills
 *   8. Trials            - heart demon, inner world, loose immortal
 *   9. Qi System         - qi attraction, qi shield, spirit stone ore
 *   10. Lifespan         - aging & death
 *   11. Effects & Morality - status effects, morality, morality bounds
 *   12. Formations       - core max qi, qi gathering, growth, barriers, rejuvenation, harvest
 *   13. UI               - HUD layout, colors, background, accessibility, advanced
 */
public final class ConfigDescriptionRegistry {

    /** All config entry info, keyed by config path. */
    public static final Map<String, ConfigEntryInfo> ENTRIES = new LinkedHashMap<>();

    /** Top-level tab descriptions (shown as tooltips when hovering tabs). */
    public static final Map<String, String> TAB_DESCRIPTIONS = new LinkedHashMap<>();

    /** Sub-tab descriptions within each top tab. */
    public static final Map<String, String> SUBTAB_DESCRIPTIONS = new LinkedHashMap<>();

    static {
        // ════════════════════════════════════════════════════════════════
        //  TOP-LEVEL TAB DESCRIPTIONS (shown when hovering tab buttons)
        // ════════════════════════════════════════════════════════════════
        TAB_DESCRIPTIONS.put("Cultivation",
            "The heart of the Xiaoxiang cultivation world - a journey from mortal fragility to immortal transcendence.\\n" +
            "\\n" +
            "THE 12 CULTIVATION REALMS:\\n" +
            "1. Mortal - Ordinary humans with no Qi. They live brief lives of 80 years, subject to disease, violence, and time.\\n" +
            "   Most of the world's population are mortals. They farm, trade, and serve - unaware of the power that cultivators wield.\\n" +
            "2. Qi Refining - The first step into cultivation. The cultivator learns to sense Qi of heaven and earth and absorb it into their body.\\n" +
            "   Max Qi is small, but they gain a Qi shield, basic spells, and extended lifespan. This is where every cultivator begins.\\n" +
            "   Sub-stages: Early, Middle, Late, Peak - each increasing Qi capacity and spell power.\\n" +
            "3. Foundation Building - Spiritual roots stabilize. The cultivator builds a foundation of Qi within their dantian.\\n" +
            "   Spell power surges dramatically. They must choose a Dao path: Human (balanced), Blood (physical combat),\\n" +
            "   Earth (defensive cultivation), or Heaven (supreme spellcasting). This choice shapes their entire cultivation journey.\\n" +
            "   Heaven Dao is the strongest but faces the hardest tribulations. Bone age limits apply - the old cannot attempt Heaven Dao.\\n" +
            "4. Golden Core - The cultivator compresses their Qi into a Golden Core, a massive power jump.\\n" +
            "   Qi storage multiplies, spell damage increases, and the Dao path is reinforced with enhanced bonuses.\\n" +
            "   Formation requires surviving the Shatter Core Trial - a boss fight against an inner demon.\\n" +
            "   Golden Core cultivators are considered experts, leading small sects or serving as elders in larger ones.\\n" +
            "5. Nascent Soul - The cultivator nurtures an independent soul body within their Golden Core.\\n" +
            "   Flight becomes effortless, spell power is world-shaking, and Qi capacity is vast.\\n" +
            "   Nascent Soul cultivators are major figures - sect masters, wandering seniors, or reclusive hermits.\\n" +
            "6. Soul Formation - The cultivator severs their soul from their body, gaining vast magical power.\\n" +
            "   They can master the most powerful spells and exist in a state between life and death.\\n" +
            "   Soul Formation cultivators are legendary - grand elders of great sects or ancient hermits on sacred mountains.\\n" +
            "7. Void Refining - The cultivator merges void and reality, existing beyond the reach of mortals.\\n" +
            "   They can manipulate space itself and are essentially myths to the common world.\\n" +
            "   Void Refining cultivators are the ancestors of ancient sects, rarely involving themselves in worldly affairs.\\n" +
            "8. Body Integration - Body and soul unify as one, achieving near-perfection.\\n" +
            "   These cultivators have surpassed mortal understanding and operate on a level that defies conventional comprehension.\\n" +
            "9. Mahayana - Near-immortal power. The cultivator stands at the threshold of true immortality.\\n" +
            "   They have survived countless tribulations and lived for centuries or millennia.\\n" +
            "10. Tribulation Transcendence - The cultivator attempts to transcend the final heavenly tribulation.\\n" +
            "    This is the last trial before immortality. Success means ascension; failure means becoming a Loose Immortal.\\n" +
            "11. Loose Immortal - Failed ascension but survived. Still immensely powerful but barred from true immortality.\\n" +
            "    They gain per-level bonuses making them stronger with each level, but can never achieve True Immortal status.\\n" +
            "12. True Immortal - Eternal life achieved. No aging, virtually limitless Qi, reality-shaping power.\\n" +
            "    True Immortals are gods walking the earth - the rarest and most powerful beings in existence.\\n" +
            "\\n" +
            "EACH REALM HAS 4 SUB-STAGES: Early, Middle, Late, Peak (44 total milestones).\\n" +
            "\\n" +
            "THIS TAB CONTROLS:\\n" +
            "- Max Qi, Lifespan, Qi Shield %, and Tribulation Damage for every realm and sub-stage\\n" +
            "- Global multipliers for quick scaling of all values at once\\n" +
            "- NPC Spawn Weights per realm (how often each realm's NPCs appear in the world)\\n" +
            "- Spell damage, Qi cost, and charge time multipliers (global and per-spell)\\n" +
            "- Technique bonus multipliers (23 technique types with Qi absorb, attack, defense, HP, crit, element, speed)\\n" +
            "- Spirit Root multipliers (Heavenly, Dual, Mutant, Sword, Hidden - with elemental affinities)\\n" +
            "- Physique bonuses and rarity weights (Immortal Body, Sword Body, Fire/Ice Body, Chaos Body, Broken Vein, etc.)\\n" +
            "- Foundation Dao path bonuses (Human, Blood, Earth, Heaven - spell damage, Qi cost, HP, defense, efficiency, recovery)\\n" +
            "- Golden Core Dao path bonuses (enhanced Foundation Dao + Shatter Core Trial boss stats + tribulation strikes)\\n" +
            "- Zhenyuan (stat point) rewards per breakthrough\\n" +
            "- Time acceleration limits for cultivation speed\\n" +
            "- Tribulation timing (interval, charge, bolt cooldown)\\n" +
            "- Loose Immortal per-level bonuses (body defense, cultivation efficiency, Qi recovery, damage, max Qi, free zhenyuan)");
        TAB_DESCRIPTIONS.put("World",
            "The natural environment that sustains all cultivation. Qi permeates the world but is not evenly distributed -\\n" +
            "it flows through spiritual mountains, sacred forests, and mystical biomes while barely touching deserts and oceans.\\n" +
            "\\n" +
            "BIOME QI DENSITY:\\n" +
            "Every biome has a Qi density profile that determines how much Qi is available for cultivation.\\n" +
            "Spiritual mountains and sacred forests are Qi-rich, accelerating cultivation dramatically.\\n" +
            "Deserts, oceans, and barren wastelands are Qi-poor, slowing cultivation to a crawl.\\n" +
            "Each profile defines: max Qi (storage capacity), orb gain (Qi per orb collected),\\n" +
            "and supply per second (passive Qi generation rate).\\n" +
            "Sects built in Qi-rich areas produce stronger cultivators - location matters enormously.\\n" +
            "Biome Qi density stacks with Spirit Vein and Formation bonuses for compound cultivation speed.\\n" +
            "\\n" +
            "SPIRIT VEINS:\\n" +
            "Spirit Veins are geological formations that generate Qi continuously, serving as natural cultivation resource nodes.\\n" +
            "They come in 5 tiers, each providing exponentially more Qi:\\n" +
            "  Low (100 Qi) - Found in common areas, provides basic Qi for beginning cultivators.\\n" +
            "  Mid (1,000 Qi) - Found in spiritual locations, supports Qi Refining and Foundation Building cultivators.\\n" +
            "  High (10,000 Qi) - Found in sacred mountains, supports Golden Core and Nascent Soul cultivation.\\n" +
            "  Supreme (100,000 Qi) - Found in legendary locations, supports Soul Formation and above.\\n" +
            "  Immortal (1,000,000 Qi) - The rarest veins, capable of sustaining True Immortal cultivation.\\n" +
            "Max Qi is the storage capacity. Orb gain is Qi per orb. Supply per second is passive generation rate.\\n" +
            "Vein radii determine how far their Qi influence reaches - larger radii benefit more cultivators.\\n" +
            "Spirit Veins are the foundation of sect placement - a sect on an Immortal Spirit Vein has a massive advantage.\\n" +
            "\\n" +
            "SPIRIT PLANTS:\\n" +
            "Spirit Plants are cultivable flora with mystical properties beyond normal Minecraft crops.\\n" +
            "Each has a max age (growth stages) and growth tick rate, plus unique special effects:\\n" +
            "  Spirit Gathering Flower - spawns Qi orbs in its vicinity, boosting nearby cultivation speed.\\n" +
            "  Flame Pepper - melts snow and ice around it, creating warm microclimates for cold-sensitive plants.\\n" +
            "  Snow Soul Lotus - places snow layers around it, creating cold microclimates for heat-sensitive plants.\\n" +
            "  Golden Chrysanthemum - drops gold nuggets when harvested, a source of wealth for cultivators.\\n" +
            "Spirit plants are essential for alchemy ingredients and cultivation resources.\\n" +
            "Growth formations can accelerate their growth, and harvest formations can auto-collect them.\\n" +
            "\\n" +
            "FORMATIONS:\\n" +
            "Formation arrays are placed structures that provide persistent area effects, powered by Formation Core blocks.\\n" +
            "Formation types and their effects:\\n" +
            "  Qi Gathering - boosts cultivation speed for all cultivators in the area. Essential for sect efficiency.\\n" +
            "  Growth - accelerates spirit plant growth. Vital for spirit plant farming operations.\\n" +
            "  Barrier - absorbs damage to protect sect grounds. Uses Qi-per-damage ratio. Essential for sect defense.\\n" +
            "  Rejuvenation - heals cultivators in range. Useful for recovery after tribulations or combat.\\n" +
            "  Harvest - automatically collects mature plants. Reduces manual farming labor.\\n" +
            "Each formation type has per-tier multipliers (Low to Immortal), effect intervals, and flag effect radius (1-16 blocks).\\n" +
            "Formation arrays are essential for sect defense and efficient cultivation - a well-placed Qi Gathering formation\\n" +
            "can dramatically accelerate an entire sect's cultivators.\\n" +
            "\\n" +
            "LOOT:\\n" +
            "Chest loot settings control what cultivation-themed items appear in dungeon chests, sect treasuries,\\n" +
            "and special structures. Roll counts determine how many items appear per chest.\\n" +
            "Item weights determine relative probability - higher weight = more likely to appear.\\n" +
            "Cultivation loot includes: technique books, spell scrolls, pills, spirit stones, formation cores,\\n" +
            "spirit plant seeds, weapon templates, and rare artifacts.\\n" +
            "\\n" +
            "BEAST CULTIVATION:\\n" +
            "The beast cultivation system allows monsters and animals to accumulate Qi and advance through beast realms.\\n" +
            "This creates a living world where even wild creatures become dangerous as they cultivate.\\n" +
            "Beast advance costs control how much Qi a beast needs to break through.\\n" +
            "Check intervals determine how often the system evaluates beast cultivation progress.\\n" +
            "Qi density threshold sets the minimum biome Qi needed for beasts to cultivate.\\n" +
            "Enable cultivation for monsters (zombies, skeletons) or all mobs (including passive animals).");
        TAB_DESCRIPTIONS.put("NPCs",
            "The living world of cultivators, sects, and social structures that populate the Xiaoxiang cultivation world.\\n" +
            "\\n" +
            "IDENTITY SYSTEM:\\n" +
            "When a player begins their journey, they draw an Identity from a deck of 16 backgrounds, each determining\\n" +
            "starting items and a lifespan range. The 16 identities represent different walks of life in the cultivation world:\\n" +
            "  Lone Cultivator - Independent practitioner, balanced starting stats.\\n" +
            "  Merchant Son - Wealthy background, more starting resources.\\n" +
            "  Bandit Leader - Martial background, combat-oriented but shorter lifespan.\\n" +
            "  Hunter - Wilderness survival, moderate stats.\\n" +
            "  Doctor Heir - Medical family, longer lifespan, healing items.\\n" +
            "  Hermit Disciple - Reclusive master, cultivation-oriented.\\n" +
            "  Fisherman - Common folk, modest starting items.\\n" +
            "  Farmer - Agricultural background, spirit plant related items.\\n" +
            "  Abandoned Infant - Mysterious origins, unique potential.\\n" +
            "  General Son - Military family, strong combat stats, shorter life.\\n" +
            "  Exiled Princess - Royal background, rare starting items.\\n" +
            "  Pirate - Seafaring combatant, aggressive playstyle.\\n" +
            "  Beast Descendant - Beast bloodline, unique physique potential.\\n" +
            "  Taoist - Mystical practitioner, cultivation bonuses.\\n" +
            "  Monk - Spiritual practitioner, meditation bonuses.\\n" +
            "  Academy Student - Scholarly background, longest lifespan, technique bonuses.\\n" +
            "Martial identities (Bandit Leader, Pirate, General Son) live shorter but start stronger.\\n" +
            "Scholarly identities (Academy Student, Doctor Heir) live longer. Mystical identities (Taoist, Monk) have unique advantages.\\n" +
            "\\n" +
            "NPC SPAWN SYSTEM:\\n" +
            "NPC cultivators roam the world with realm distribution controlled by weighted rolls.\\n" +
            "Each realm has a configurable spawn weight - mortals are most common (1000), True Immortals are rarest (1).\\n" +
            "Wandering cultivators spawn near preferred structures with configurable spawn chances.\\n" +
            "The spawn chance near structures is much higher than far from them, creating cultivation hubs.\\n" +
            "\\n" +
            "NPC COMBAT SYSTEM:\\n" +
            "NPC cultivators fight using a tactical AI with dodge mechanics, threat scanning, and reaction timing.\\n" +
            "Dodge chance scales with realm - higher realm NPCs dodge more frequently, making them harder to hit.\\n" +
            "Scan ticks control how often NPCs look for threats - lower values mean more responsive detection.\\n" +
            "Reaction ticks control how fast NPCs respond after detecting a threat - lower is faster.\\n" +
            "Dodge cooldown prevents infinite dodging. These create a combat difficulty curve where higher realm NPCs\\n" +
            "are noticeably more skilled. NPC spells have separate damage and cost multipliers for balanced PvE.\\n" +
            "Additional AI behaviors include stalemate retreat (NPCs flee unwinnable fights) and wild meditation\\n" +
            "(NPCs meditate to recover Qi when not in combat).\\n" +
            "\\n" +
            "SECT SYSTEM:\\n" +
            "Sects are procedurally generated with 13 size tiers from Humble Cottage (tier 0) to Grand Immortal Sect (tier 12).\\n" +
            "Each sect has a power score (0-4) determining its strength, ancestor immortal chances (likelihood the\\n" +
            "founding ancestor achieved immortality), and building generation based on size tier.\\n" +
            "Sects contain shops selling techniques, spells, and weapons at spirit stone prices by tier.\\n" +
            "The task system assigns expeditions and duties to sect members.\\n" +
            "Departments (Herbal, Alchemy, Refining, etc.) have work points, output caps, and buffer targets.\\n" +
            "Ambient social behaviors include spectator counts, NPC cooldowns, and social interactions.\\n" +
            "Defense barriers protect sects from attack with configurable strength.\\n" +
            "Daily schedules control NPC routines with day/night tick boundaries.\\n" +
            "Overhead UI shows sect names above NPCs. Cultivation profiles control how sect NPCs progress.\\n" +
            "Physical journeys send NPCs on expeditions with configurable durations and rewards.\\n" +
            "Sect spawn chances per size tier control how often each type of sect generates in the world.\\n" +
            "\\n" +
            "PROGRESSION:\\n" +
            "Bone age limits determine the maximum age to attempt certain Dao paths - if too old, Heaven Dao is locked.\\n" +
            "NPC tribulation death chance controls how many NPCs survive breakthrough attempts.\\n" +
            "Max cultivation cap limits how far NPCs can progress, preventing overpowered worlds.\\n" +
            "Gender edit settings control whether players can change gender.\\n" +
            "Time estimate days are shown in the UI as rough timeframes for breakthrough preparation.");
        TAB_DESCRIPTIONS.put("Crafting",
            "The economic engine of the cultivation world - where raw spiritual materials become powerful pills,\\n" +
            "weapons, and tools through the arts of Alchemy and Refining.\\n" +
            "\\n" +
            "ALCHEMY SYSTEM:\\n" +
            "Alchemy is the art of refining pills from spiritual ingredients using an Alchemy Furnace.\\n" +
            "There are 5 alchemy ranks, each unlocking higher tier pill recipes:\\n" +
            "  Low Rank - Can craft basic Qi recovery pills and simple remedies. Entry-level alchemy.\\n" +
            "  Mid Rank - Can craft mid-tier pills with moderate effects. Suitable for Qi Refining cultivators.\\n" +
            "  High Rank - Can craft high-tier pills with strong effects. Suitable for Foundation Building and Golden Core.\\n" +
            "  Supreme Rank - Can craft supreme pills with powerful effects. Suitable for Nascent Soul and above.\\n" +
            "  Immortal Rank - Can craft immortal pills with world-shaking effects. The pinnacle of alchemy.\\n" +
            "The Alchemy Furnace has configurable Max Qi (storage for crafting), ticks per pill (crafting speed),\\n" +
            "and XP gains for success and failure. Higher ranks require more XP but unlock better recipes.\\n" +
            "Failure XP is gained at a reduced rate - even failed crafts contribute to progression.\\n" +
            "Both success and failure contribute to rank progression, but success is much faster.\\n" +
            "\\n" +
            "REFINING SYSTEM:\\n" +
            "Refining is the art of forging spiritual weapons and armor using a Refining Furnace.\\n" +
            "Similar to Alchemy with 5 ranks: Low, Mid, High, Supreme, and Immortal.\\n" +
            "The Refining Furnace has Max Qi storage, ticks per item (crafting speed), and XP gains.\\n" +
            "Tier-up chances are bonus probabilities granted by specific techniques - having the right\\n" +
            "technique can significantly boost your chances of producing higher-tier equipment.\\n" +
            "Refined weapons have tier-based Qi cost reduction - higher tier weapons reduce the Qi cost\\n" +
            "of spells cast while holding them, creating a synergy between Refining and spellcasting.\\n" +
            "Refined armor provides enhanced defense and may have special effects like burn, freeze, or poison.\\n" +
            "\\n" +
            "PILL TYPES AND EFFECTS:\\n" +
            "Pills are consumable items that provide various effects when eaten:\\n" +
            "  Qi Pills - Restore Qi when consumed. Higher tier pills restore more Qi. Essential for combat sustain.\\n" +
            "  Spirit Stones - Consumed for Qi recovery, acting as portable Qi batteries. Mined from Spirit Stone Ore.\\n" +
            "  Blood Burn Pills - Trade health for temporary combat power. Desperate measures for tough fights.\\n" +
            "  Clear Mind Pills - May boost cultivation focus or remove debuffs. Useful for meditation sessions.\\n" +
            "  Divine Stride Pills - Grant temporary speed buffs for fast travel.\\n" +
            "  Youth Pills - Reduce bone age, effectively making the cultivator younger.\\n" +
            "    Useful for extending the window to attempt Dao path breakthroughs with age limits.\\n" +
            "    A cultivator whose bone age exceeds the Heaven Dao limit can take Youth Pills to become young enough.\\n" +
            "\\n" +
            "WEAPON SYSTEM:\\n" +
            "Spiritual weapons are forged through the Refining system and provide combat advantages:\\n" +
            "  Global damage multiplier affects ALL weapons - increase for deadlier combat overall.\\n" +
            "  Qi cost reduction % lowers the Qi cost of spells when holding a weapon.\\n" +
            "    Higher tier weapons provide more reduction, making them essential for spellcasters.\\n" +
            "    This creates a powerful synergy: a well-forged weapon not only deals more damage\\n" +
            "    but also makes spells cheaper to cast. A Supreme tier weapon can reduce spell costs by 50%+.\\n" +
            "  Special effects trigger on hit and apply status effects to the target:\\n" +
            "    Burn - deals fire damage over time, useful against high-HP targets.\\n" +
            "    Freeze - slows the target, reducing their movement and attack speed.\\n" +
            "    Poison - deals damage over time with reduced healing, preventing recovery.\\n" +
            "  Each special effect has configurable duration - longer durations mean more sustained damage.\\n" +
            "\\n" +
            "STORAGE BAGS:\\n" +
            "Storage Bags are spatial storage items with configurable grid dimensions per tier.\\n" +
            "Higher tier bags have more columns and rows, providing more inventory space.\\n" +
            "From small pouches (3x3) to vast spatial storage (9x18), storage bags are essential\\n" +
            "for cultivators who accumulate many pills, materials, and equipment.");
        TAB_DESCRIPTIONS.put("UI",
            "Visual and accessibility settings for the cultivation HUD and panels. The mod displays cultivation info\\n" +
            "via an in-game HUD (top-left overlay) and a main cultivation panel (opened with G key).\\n" +
            "This tab provides full control over every visual aspect of the mod's interface.\\n" +
            "\\n" +
            "TEXT AND SCALING:\\n" +
            "The mod displays text in both English and Chinese, and each can be scaled independently.\\n" +
            "Increase text size for better readability on high-resolution displays.\\n" +
            "Decrease text size to fit more information on screen at once.\\n" +
            "Text scaling affects: realm names, spell names, technique names, stat values,\\n" +
            "lifespan displays, Qi amounts, and all other text in the cultivation panel.\\n" +
            "The HUD (top-left overlay) also respects these scaling settings.\\n" +
            "Find a balance that's readable on your screen without taking too much space.\\n" +
            "\\n" +
            "HUD LAYOUT:\\n" +
            "The in-game HUD displays cultivation info in the top-left corner of the screen.\\n" +
            "Adjust X position to move it left/right and Y position to move it up/down.\\n" +
            "Bar width controls how wide the Qi bar and other bars are.\\n" +
            "Portrait size controls how large the cultivator portrait is.\\n" +
            "Move the HUD to any corner of the screen by adjusting X/Y coordinates.\\n" +
            "On smaller screens, reduce bar width and portrait size to avoid covering gameplay.\\n" +
            "The HUD shows: current realm and sub-stage, Qi amount, lifespan/bone age,\\n" +
            "active passive spells, and morality alignment.\\n" +
            "\\n" +
            "PANEL SIZE:\\n" +
            "The main cultivation panel is opened with the G key and shows detailed cultivation info.\\n" +
            "Panel width and height control the dimensions of this panel.\\n" +
            "Larger panels show more content at once - more spells, more stats, more techniques.\\n" +
            "Smaller panels take less screen space but may require scrolling to see everything.\\n" +
            "On smaller screens (1280x720), use smaller panel dimensions to avoid covering the game.\\n" +
            "On larger screens (1920x1080+), larger panels provide a better overview.\\n" +
            "The panel contains: realm info, spell grid, technique list, stat breakdown,\\n" +
            "morality display, lifespan info, and active effects.\\n" +
            "\\n" +
            "SPELL GRID:\\n" +
            "The spell grid displays all learned spells in the cultivation panel.\\n" +
            "Cell size controls how large each spell slot is - larger cells are easier to click.\\n" +
            "Icon size controls how big the spell icon is within each cell.\\n" +
            "Columns and rows control the grid layout - more columns = wider grid, more rows = taller.\\n" +
            "Total visible spells = columns x rows. If you have more spells than visible slots,\\n" +
            "the grid will scroll. Adjust these values to fit your screen and spell count.\\n" +
            "A compact grid (small cells, many columns) shows many spells at once.\\n" +
            "A spacious grid (large cells, fewer columns) is easier to read and click.\\n" +
            "\\n" +
            "COLORS:\\n" +
            "Full color customization for all UI elements in the cultivation panel and HUD.\\n" +
            "Use the color preset dropdown to pick from named themes instead of typing hex values:\\n" +
            "  Purple (mystical), Gold (imperial), Crimson (aggressive), Jade (serene),\\n" +
            "  Azure (scholarly), and more. Each preset sets coordinated colors for all elements.\\n" +
            "Alternatively, set individual colors manually for full control.\\n" +
            "Colorable elements include: panel borders, text colors, bar colors (Qi, HP, XP),\\n" +
            "spell grid borders, technique list colors, and HUD elements.\\n" +
            "A real-time preview panel shows how your color choices look as you change them.\\n" +
            "\\n" +
            "BACKGROUND:\\n" +
            "Background plate settings for the cultivation panel's visual backdrop.\\n" +
            "Page color is the main background behind the entire panel.\\n" +
            "Panel color is the inner panels within the main panel (spell grid, stats, etc.).\\n" +
            "Opacity controls transparency: 0.5 = semi-transparent (see game through panel),\\n" +
            "1.0 = fully solid (no see-through). Semi-transparent panels let you keep an eye\\n" +
            "on the game while managing cultivation, but may be harder to read.\\n" +
            "Solid panels are easier to read but block more of the screen.\\n" +
            "Find a balance that works for your playstyle and screen size.\\n" +
            "\\n" +
            "ACCESSIBILITY:\\n" +
            "Accessibility options include high-contrast mode and HUD visibility toggles.\\n" +
            "High-contrast mode uses brighter, more distinct colors for better readability.\\n" +
            "HUD visibility toggles the in-game cultivation info display on or off.\\n" +
            "Useful for players who find the HUD distracting or who prefer minimal UI.\\n" +
            "\\n" +
            "REAL-TIME PREVIEW:\\n" +
            "The UI tab features a real-time preview panel that shows how your settings look\\n" +
            "in the actual cultivation UI as you change them. Adjust colors, text size, panel dimensions,\\n" +
            "and see the results instantly without needing to open the in-game panel.");

        // New top-level tabs from reorganization
        TAB_DESCRIPTIONS.put("Spells & Combat",
            "The combat system of the cultivation world - where Qi becomes power and spells shape the battlefield.\\n" +
            "\\n" +
            "SPELL SYSTEM:\\n" +
            "Spells are the primary offensive and defensive tools of cultivators, consuming Qi to cast.\\n" +
            "Global multipliers affect ALL spells simultaneously:\\n" +
            "  Damage Multiplier - scales how much hurt all spells deal. 2.0 = double damage.\\n" +
            "  Qi Cost Multiplier - scales how much Qi all spells consume. 0.5 = half cost.\\n" +
            "  Charge Time Multiplier - scales how long spells take to cast. 0.5 = instant cast.\\n" +
            "These global multipliers are the quickest way to rebalance all combat at once.\\n" +
            "\\n" +
            "INDIVIDUAL SPELL OVERRIDES:\\n" +
            "Beyond global multipliers, specific spells can be individually tuned:\\n" +
            "  Sword Flight - Aerial movement ability. Qi per second upkeep controls how long you can fly.\\n" +
            "    Higher upkeep = shorter flight time. Essential for Nascent Soul+ cultivators who fly regularly.\\n" +
            "  Void Step - Air jump, dash, and slow fall. Separate Qi costs for each mode.\\n" +
            "    Air jump lets you double-jump in mid-air. Dash is a quick burst of movement.\\n" +
            "    Slow fall reduces fall speed for safe descent.\\n" +
            "  Palm Thunder - Channeled lightning spell. Qi per second and arming ticks.\\n" +
            "    A devastating single-target spell that channels lightning through the cultivator's palm.\\n" +
            "  Void Escape - Teleportation spell. Charge ticks and Qi per tick.\\n" +
            "    Allows instant relocation, useful for dodging tribulations or escaping combat.\\n" +
            "  Buddha Fire Lotus - Defensive formation spell. Ready Qi requirement.\\n" +
            "    Creates a lotus of protective fire around the cultivator.\\n" +
            "  Core Self Destruct - Sacrificial explosion. Ready Qi requirement.\\n" +
            "    The cultivator detonates their own Qi core for massive area damage. Desperate last resort.\\n" +
            "  Sea of Overwhelming Blood - Area denial spell. Channel ticks.\\n" +
            "    Creates a sea of blood that damages all enemies in the area over time.\\n" +
            "  Glacier Burial - Ice area effect. Channel ticks with Qi per tick.\\n" +
            "    Freezes the area, dealing ice damage and slowing all enemies.\\n" +
            "\\n" +
            "PASSIVE SPELLS:\\n" +
            "Passive spells are always-active abilities that drain Qi over time:\\n" +
            "  Slow Regen - Passive HP healing. Check interval and Qi cost per tick.\\n" +
            "  Bigu (Fasting) - Prevents hunger. No need to eat.\\n" +
            "  Qi Mending - Repairs tool/armor durability using Qi.\\n" +
            "  Qi Flight - Fly using Qi. Qi per second upkeep.\\n" +
            "  Item Attraction - Pulls dropped items toward you. Configurable radius.\\n" +
            "  Treasure Seizing - Grabs entire item stacks from a distance.\\n" +
            "\\n" +
            "WEAPON SYSTEM:\\n" +
            "Spiritual weapons forged through Refining provide combat advantages:\\n" +
            "  Global damage multiplier affects ALL weapons.\\n" +
            "  Tier-based Qi cost reduction - higher tier weapons reduce spell Qi costs when held.\\n" +
            "    This creates a powerful synergy: a Supreme tier weapon can reduce spell costs by 50%+,\\n" +
            "    making it essential for spellcasters who cast frequently.\\n" +
            "  Special effects on hit: Burn (fire DoT), Freeze (slow), Poison (DoT + reduced healing).\\n" +
            "  Each special effect has configurable duration.\\n" +
            "\\n" +
            "NPC COMBAT BALANCE:\\n" +
            "NPC spells have separate damage and Qi cost multipliers for balanced PvE.\\n" +
            "This allows different damage scaling for player vs NPC spells.\\n" +
            "For example, on Chaos difficulty: player spells do 25% damage, NPC spells do 400% damage.\\n" +
            "This creates a challenging PvE experience where NPCs are genuinely dangerous.");
        TAB_DESCRIPTIONS.put("Sects",
            "Sects are the major social structures of the cultivation world - organized communities of cultivators\\n" +
            "that provide training, resources, commerce, and protection. They are the backbone of cultivation society.\\n" +
            "\\n" +
            "SECT SIZE TIERS (13 levels):\\n" +
            "Sects are procedurally generated with 13 size tiers, each progressively larger and more powerful:\\n" +
            "  Tier 0: Humble Cottage - The smallest sect, just a few cultivators in a small building.\\n" +
            "  Tier 1: Minor Gathering - A small group with basic facilities.\\n" +
            "  Tier 2: Small Sect - A modest sect with a few buildings and members.\\n" +
            "  Tier 3: Modest Sect - Growing sect with more resources.\\n" +
            "  Tier 4: Established Sect - A solid mid-tier sect with good facilities.\\n" +
            "  Tier 5: Prominent Sect - Well-known in the region, with multiple departments.\\n" +
            "  Tier 6: Renowned Sect - Famous sect, attracts talented cultivators.\\n" +
            "  Tier 7: Great Sect - A major power in the cultivation world.\\n" +
            "  Tier 8: Ancient Sect - Centuries old, with deep traditions and powerful elders.\\n" +
            "  Tier 9: Supreme Sect - One of the strongest sects in the world.\\n" +
            "  Tier 10: Legendary Sect - Spoken of in legends, with immortal-level ancestors.\\n" +
            "  Tier 11: Mythic Sect - Beyond legends, with power that shapes the world.\\n" +
            "  Tier 12: Grand Immortal Sect - The pinnacle, with True Immortal ancestors and supreme power.\\n" +
            "Each tier has a configurable spawn chance - higher tiers are rarer by default.\\n" +
            "\\n" +
            "POWER AND ANCESTORS:\\n" +
            "Each sect has a power score (0-4) determining its overall strength.\\n" +
            "Ancestor immortal chances control how likely a sect's founding ancestor achieved immortality.\\n" +
            "Higher chances mean more powerful sects with immortal backing - a sect with a True Immortal ancestor\\n" +
            "is essentially unassailable by lower-realm cultivators.\\n" +
            "\\n" +
            "SHOPS AND PRICING:\\n" +
            "Sects contain shops selling techniques, spells, and weapons at spirit stone prices by tier.\\n" +
            "Higher tier sects sell better items but at higher prices.\\n" +
            "Sell percent controls what you get back when selling items to sect shops.\\n" +
            "This creates a cultivation economy where spirit stones are the primary currency.\\n" +
            "\\n" +
            "NPC POPULATION AND RECRUITMENT:\\n" +
            "NPC population settings control how many cultivators live in each sect.\\n" +
            "Recruitment settings control how sects gain new members over time.\\n" +
            "Larger sects have more NPCs, creating bustling communities of cultivators.\\n" +
            "\\n" +
            "TASKS AND DUTIES:\\n" +
            "The task system assigns expeditions and duties to sect members.\\n" +
            "Task parameters control expedition durations and required counts.\\n" +
            "Sect NPCs go on journeys, gather resources, and complete missions.\\n" +
            "\\n" +
            "DEPARTMENTS:\\n" +
            "Sects have specialized departments, each with its own function:\\n" +
            "  Herbal Department - Gathers and cultivates spirit plants.\\n" +
            "  Alchemy Department - Refines pills for sect members.\\n" +
            "  Refining Department - Forges weapons and armor.\\n" +
            "Each department has work points, output caps, and buffer targets.\\n" +
            "\\n" +
            "AMBIENT AND SOCIAL:\\n" +
            "Sect ambient scenes and social behaviors create a living world.\\n" +
            "Spectator counts control how many NPCs observe events.\\n" +
            "NPC cooldowns and social interactions make sects feel alive.\\n" +
            "\\n" +
            "DEFENSE:\\n" +
            "Defense barriers protect sects from attack with configurable strength.\\n" +
            "Protection dome parameters control the area and strength of sect defenses.\\n" +
            "\\n" +
            "DAILY SCHEDULE:\\n" +
            "Day/night tick boundaries control when sect NPCs switch between activities.\\n" +
            "During the day, NPCs may train, work, or socialize. At night, they may meditate or rest.\\n" +
            "\\n" +
            "OVERHEAD UI:\\n" +
            "Sect overhead nameplate display settings control visibility and formatting\\n" +
            "of sect names above NPCs, helping identify which sect an NPC belongs to.\\n" +
            "\\n" +
            "CULTIVATION PROFILE:\\n" +
            "Controls how sect NPCs' cultivation progress is displayed and managed.\\n" +
            "Determines how quickly sect NPCs advance through realms.\\n" +
            "\\n" +
            "JOURNEYS:\\n" +
            "Physical journeys send NPCs on expeditions with configurable durations and rewards.\\n" +
            "These create dynamic world events where sect members travel and return with resources.");
        TAB_DESCRIPTIONS.put("Beasts & Mobs",
            "The beast cultivation system - where monsters and animals walk the path of cultivation alongside humans.\\n" +
            "\\n" +
            "BEAST CULTIVATION SYSTEM:\\n" +
            "In the Xiaoxiang cultivation world, it is not only humans who can cultivate. Beasts - monsters, animals,\\n" +
            "and creatures of all kinds - can also accumulate Qi and advance through beast realms, gaining power over time.\\n" +
            "This creates a living world where the wilderness itself becomes increasingly dangerous as beasts cultivate.\\n" +
            "A simple zombie encountered early game may, over time, accumulate enough Qi to advance through beast realms\\n" +
            "and become a terrifying spirit beast capable of challenging Foundation Building or even Golden Core cultivators.\\n" +
            "\\n" +
            "BEAST ADVANCE COSTS:\\n" +
            "Each beast realm has a configurable advance cost - the amount of Qi a beast needs to accumulate\\n" +
            "before it can break through to the next realm. Higher costs mean slower beast progression,\\n" +
            "keeping the wilderness safer for longer. Lower costs mean beasts advance quickly, creating\\n" +
            "a more dangerous world where even common monsters become threats.\\n" +
            "\\n" +
            "CHECK INTERVALS:\\n" +
            "The check interval determines how often (in ticks) the system evaluates beast cultivation progress.\\n" +
            "20 ticks = 1 second. Lower intervals mean beasts gain Qi more frequently, accelerating their progression.\\n" +
            "Higher intervals mean slower beast progression but less CPU usage, important on servers with many entities.\\n" +
            "Default is balanced for single-player; servers may want higher intervals for performance.\\n" +
            "\\n" +
            "QI DENSITY THRESHOLD:\\n" +
            "The minimum biome Qi density required for beasts to gain Qi. If a biome's Qi density is below this threshold,\\n" +
            "beasts there won't cultivate. This creates 'safe zones' in Qi-poor biomes where beasts remain ordinary.\\n" +
            "Higher thresholds mean beasts only cultivate in Qi-rich areas (spiritual mountains, sacred forests).\\n" +
            "Lower thresholds mean beasts cultivate almost everywhere, making the entire world dangerous.\\n" +
            "\\n" +
            "CULTIVATION SCOPE:\\n" +
            "  Cultivation for Monsters - If enabled, monsters (zombies, skeletons, creepers, etc.) also gain beast cultivation.\\n" +
            "    They can advance through beast realms and become stronger. This makes hostile mobs progressively more dangerous.\\n" +
            "  Cultivation for All Mobs - If enabled, ALL living entities (except players) get beast cultivation,\\n" +
            "    including passive animals like cows, pigs, and sheep. This creates a truly living world where\\n" +
            "    even farm animals can become spirit beasts. Imagine a cow that has cultivated to Golden Core level!\\n" +
            "\\n" +
            "BEAST SPAWNS:\\n" +
            "Beast spawn settings control which mobs can cultivate and their spawn rates.\\n" +
            "Configure the distribution and frequency of cultivating beasts in the world.\\n" +
            "This system extends the original mod's beast cultivation handler beyond specifically tagged entities,\\n" +
            "creating a world where any creature can potentially become a powerful spirit beast through Qi accumulation.\\n" +
            "\\n" +
            "STRATEGIC IMPLICATIONS:\\n" +
            "Beast cultivation adds a dynamic difficulty curve to the world. Areas near Spirit Veins or in Qi-rich biomes\\n" +
            "become increasingly dangerous over time as beasts cultivate faster. Players must clear beast populations\\n" +
            "regularly or risk facing spirit beasts that rival their own cultivation level. This creates a natural\\n" +
            "progression where the world grows more dangerous alongside the player, always providing a challenge.");
        TAB_DESCRIPTIONS.put("Trials",
            "Trials and tribulations are the ultimate tests of a cultivator's worth - heavenly trials that determine\\n" +
            "whether a cultivator is worthy of advancing to the next realm. These are the most dramatic and dangerous\\n" +
            "moments in a cultivator's journey, where years of preparation meet the judgment of the heavens.\\n" +
            "\\n" +
            "HEAVENLY TRIBULATION:\\n" +
            "When a cultivator attempts a major realm breakthrough, the heavens send lightning strikes to test them.\\n" +
            "The number of strikes and damage per strike scales with the realm being attempted - higher realms face\\n" +
            "more strikes with more damage. Foundation Dao and Golden Core Dao paths affect tribulation strike count:\\n" +
            "  Human Dao - Fewest strikes, easiest tribulation. The safe path.\\n" +
            "  Blood Dao - Moderate strikes. The warrior's path.\\n" +
            "  Earth Dao - More strikes. The cultivator's path.\\n" +
            "  Heaven Dao - Most strikes, hardest tribulation. The supreme path with the best rewards but highest risk.\\n" +
            "Tribulation timing is configurable: interval ticks (time between strikes), charge ticks (warning period),\\n" +
            "and bolt cooldown (recovery time between waves).\\n" +
            "Qi Damage Immunity passive spell can reduce tribulation damage. Qi attribute combinations can also reduce it.\\n" +
            "\\n" +
            "HEART DEMON TRIAL:\\n" +
            "The Heart Demon Trial is a mental confrontation where the cultivator faces their inner demon.\\n" +
            "The demon's vitality scales with the cultivator's morality alignment:\\n" +
            "  Very righteous cultivators face strong demons - their conviction attracts severe tests.\\n" +
            "  Very evil cultivators face strong demons - their corruption empowers their inner darkness.\\n" +
            "  Neutral cultivators face weaker demons - but they also lack the conviction of extremes.\\n" +
            "Vitality multipliers are configurable per morality band, allowing fine-tuned difficulty.\\n" +
            "This trial tests whether the cultivator's resolve can overcome their own nature.\\n" +
            "\\n" +
            "SHATTER CORE TRIAL:\\n" +
            "The Shatter Core Trial is a boss fight during Golden Core breakthrough. The cultivator must defeat\\n" +
            "a powerful boss to form their Golden Core. Boss stats are configurable per Dao path:\\n" +
            "  Max Health - How much HP the boss has. Higher = longer fight.\\n" +
            "  Regen Per Second - How fast the boss heals. Higher = must DPS faster than it regenerates.\\n" +
            "Heaven Dao faces the strongest boss, reflecting the greater power they will receive.\\n" +
            "\\n" +
            "INNER WORLD TRIAL:\\n" +
            "The Inner World Trial is a dimension-based challenge where the cultivator enters an inner world.\\n" +
            "Configurable parameters include:\\n" +
            "  Platform size - The arena dimensions. Smaller platforms are harder (less room to maneuver).\\n" +
            "  Soul wound penalties - Applied on failure, reducing cultivation stats.\\n" +
            "  Stasis mechanics - May freeze the cultivator during the trial, adding complexity.\\n" +
            "\\n" +
            "LOOSE IMMORTAL BONUSES:\\n" +
            "Loose Immortals gain per-level bonuses for each level achieved in the Loose Immortal realm:\\n" +
            "  Body defense bonus, cultivation efficiency, Qi recovery per second, melee damage, spell damage,\\n" +
            "  max Qi, free zhenyuan (stat points), and tribulation stats.\\n" +
            "These bonuses make Loose Immortals progressively stronger, compensating for their failed ascension.\\n" +
            "Each level is a meaningful power increase, making Loose Immortals dangerous opponents.");
        TAB_DESCRIPTIONS.put("Qi System",
            "Qi is the fundamental energy of the cultivation world - the life force that flows through all living things\\n" +
            "and the environment itself. Understanding and controlling Qi is the essence of cultivation.\\n" +
            "\\n" +
            "QI ATTRIBUTES:\\n" +
            "Qi comes in 7 attributes, each with elemental relationships:\\n" +
            "  None (Pure) - Neutral Qi with no elemental affinity. The default for most cultivators.\\n" +
            "  Gold - Metallic Qi. Strong against Wood, weak against Fire.\\n" +
            "  Wood - Nature Qi. Strong against Earth, weak against Gold. Feeds Fire.\\n" +
            "  Water - Flowing Qi. Strong against Fire, weak against Earth. Absorbed by Earth.\\n" +
            "  Fire - Burning Qi. Strong against Gold, weak against Water. Fed by Wood.\\n" +
            "  Earth - Grounding Qi. Strong against Water, weak against Wood. Absorbs Water.\\n" +
            "  Ice - Frozen Qi. A variant of Water with freezing properties.\\n" +
            "These relationships affect spell damage and Qi cost. Inverse Five Elements can invert these relationships.\\n" +
            "\\n" +
            "QI SOURCES:\\n" +
            "  Biome Qi Density - Varies by biome. Spiritual mountains are Qi-rich, deserts are Qi-poor.\\n" +
            "  Spirit Vein Cores - Tiered generation from Low to Immortal. The primary Qi source for sects.\\n" +
            "  Spirit Stone Ores - Consumable Qi sources mined from the world. Configurable capacity and regen.\\n" +
            "  Floating Qi Orbs - Generated in the world, collected by walking near them.\\n" +
            "  Meditation Mats - Provide 10x normal Qi absorption speed when meditating on them.\\n" +
            "\\n" +
            "QI ABSORPTION:\\n" +
            "The Qi attraction radius controls how far a cultivator pulls in floating Qi orbs.\\n" +
            "Larger radius = faster Qi gathering from a wider area. Essential for efficient cultivation.\\n" +
            "Spirit root absorption multipliers scale gathering speed based on root rarity:\\n" +
            "  SSR (Heavenly) roots gather Qi fastest - 5-10x normal speed.\\n" +
            "  SR (Dual) roots gather Qi faster than normal - 1.25x normal speed.\\n" +
            "  Standard roots gather at normal speed.\\n" +
            "Meditation bonuses boost gathering speed when meditating - 10x faster on a meditation mat.\\n" +
            "\\n" +
            "QI SHIELD:\\n" +
            "The Qi shield absorbs incoming damage using Qi before it reaches HP.\\n" +
            "The reduction percentage scales with realm - higher realm cultivators have stronger shields.\\n" +
            "At high realms, the Qi shield can absorb 90%+ of incoming damage, making cultivators extremely tanky.\\n" +
            "The shield depletes Qi when absorbing damage - once Qi is gone, damage goes directly to HP.\\n" +
            "\\n" +
            "SPIRIT STONE ORE:\\n" +
            "Spirit Stone Ore is a minable Qi source found in the world.\\n" +
            "Max Qi is the storage capacity - how much Qi each ore block can provide.\\n" +
            "Regeneration rate controls how fast the ore refills after being mined.\\n" +
            "High regen rates create sustainable Qi farms. Low rates make ore a finite resource.\\n" +
            "Spirit stones are the primary currency in the cultivation world, used in sect shops.");
        TAB_DESCRIPTIONS.put("Lifespan",
            "Lifespan is the measure of a cultivator's longevity - the ticking clock that drives every cultivator\\n" +
            "to seek breakthroughs before time runs out. In the cultivation world, death by old age is the greatest fear,\\n" +
            "and extending lifespan is one of the primary motivations for cultivation.\\n" +
            "\\n" +
            "LIFESPAN SOURCES:\\n" +
            "A cultivator's total lifespan is determined by multiple factors that stack:\\n" +
            "  Base Lifespan (per realm) - Each realm grants a base lifespan. Mortals live ~80 years.\\n" +
            "    Qi Refining extends it to ~120. Foundation Building to ~200. Golden Core to ~500.\\n" +
            "    By the time a cultivator reaches Soul Formation, they can live thousands of years.\\n" +
            "    True Immortals are eternal - their lifespan is effectively infinite.\\n" +
            "  Foundation Dao Bonus - Each Dao path grants bonus years. Heaven Dao grants the most.\\n" +
            "  Golden Core Dao Bonus - Further bonus years from the Golden Core Dao path.\\n" +
            "  Identity Starting Lifespan - Rolled at character creation within a range based on background.\\n" +
            "    Martial identities (Bandit Leader, Pirate) live shorter. Scholarly identities (Academy Student) live longer.\\n" +
            "  Technique Bonuses - Some techniques grant additional lifespan.\\n" +
            "\\n" +
            "BONE AGE SYSTEM:\\n" +
            "Bone age is the cultivator's apparent age, not the actual number of years they have lived.\\n" +
            "It starts at 14-18 years at character creation and advances at a configurable rate per game day.\\n" +
            "  Start Bone Age - The initial age. Lower = more time before age-related restrictions kick in.\\n" +
            "  Age Per Day - How fast bone age advances. Lower = slower aging, more time to cultivate.\\n" +
            "  Higher = faster aging, more urgency to break through.\\n" +
            "Bone age is important because Dao path choices have age limits:\\n" +
            "  Heaven Dao has a maximum bone age limit - if you're too old, you can't choose Heaven Dao.\\n" +
            "  This creates urgency for cultivators who want the strongest path - they must break through while young.\\n" +
            "  Youth Pills can reduce bone age, extending the window for Dao path choices.\\n" +
            "\\n" +
            "NEAR-IMMORTAL THRESHOLD:\\n" +
            "When a cultivator's bone age approaches the near-immortal threshold, aging dramatically slows down.\\n" +
            "This represents the cultivator's body becoming so saturated with Qi that time itself has less effect.\\n" +
            "Below the threshold, aging proceeds at the normal rate. Above it, aging crawls to a near-stop.\\n" +
            "At the highest realms (Loose Immortal, True Immortal), aging essentially stops entirely.\\n" +
            "True Immortals cease aging completely, having achieved true eternal life.\\n" +
            "\\n" +
            "DEATH PENALTY:\\n" +
            "The ordinary death penalty determines how many years of bone age are lost when a cultivator\\n" +
            "dies from non-tribulation causes (combat, environmental hazards, etc.).\\n" +
            "Higher penalty = more years lost per death, making death more punishing.\\n" +
            "Lower penalty = less years lost, making death less impactful on cultivation timeline.\\n" +
            "This does not apply to tribulation deaths, which are handled separately.");
        TAB_DESCRIPTIONS.put("Effects & Morality",
            "Status effects and morality shape the consequences of cultivation choices - the buffs and debuffs that\\n" +
            "define a cultivator's condition, and the moral alignment that determines their relationship with the heavens.\\n" +
            "\\n" +
            "STATUS EFFECTS:\\n" +
            "  Blood Berserk - A powerful combat buff representing a cultivator entering a battle frenzy.\\n" +
            "    Boosts attack speed and movement speed significantly.\\n" +
            "    In cultivation lore, blood berserk is a double-edged technique - it grants immense power\\n" +
            "    but may have drawbacks when it wears off, such as exhaustion or Qi depletion.\\n" +
            "    The cultivator channels their blood energy into raw combat power, sacrificing control for strength.\\n" +
            "    Configurable attribute modifiers control how strong the buff is.\\n" +
            "  Dao Heart Wound - A debuff from failed tribulations, representing shattered confidence.\\n" +
            "    When a cultivator fails a heavenly tribulation, their Dao Heart is wounded -\\n" +
            "    their belief in their own path is shaken, reducing attack and movement speed.\\n" +
            "    This is both a mechanical penalty and a narrative consequence -\\n" +
            "    the cultivator must recover their confidence before they can fight at full power again.\\n" +
            "    Can take a long time to recover from, making failed tribulations doubly punishing.\\n" +
            "  Shatter Armor - Reduces armor and toughness, leaving the cultivator vulnerable to physical attacks.\\n" +
            "    A debilitating debuff that strips away the cultivator's physical defenses.\\n" +
            "    Particularly dangerous for spellcasters who rely on Qi shield rather than armor.\\n" +
            "  Inverse Five Elements - Inverts elemental damage and cost relationships, creating chaos.\\n" +
            "    Fire spells become weak against Water, Water spells cost more Qi, etc.\\n" +
            "    This can be devastating for cultivators who specialized in one element.\\n" +
            "    Creates unpredictable combat scenarios where familiar strategies no longer work.\\n" +
            "    Each effect has configurable attribute modifier values that determine how strong the buff/debuff is.\\n" +
            "\\n" +
            "MORALITY SYSTEM:\\n" +
            "The Morality System tracks a cultivator's alignment on a scale from evil (negative) to righteous (positive).\\n" +
            "Every action has moral consequences:\\n" +
            "  Killing innocents lowers morality (toward evil).\\n" +
            "  Helping others raises morality (toward righteous).\\n" +
            "  Moral neutrality is also a valid path - the uncaring cultivator who pursues only power.\\n" +
            "Thresholds define when alignment labels change:\\n" +
            "  Below the neutral minimum = 'Evil' alignment.\\n" +
            "  Above the neutral maximum = 'Righteous' alignment.\\n" +
            "  Between the thresholds = 'Neutral' alignment.\\n" +
            "\\n" +
            "MORALITY EFFECTS ON TRIBULATIONS:\\n" +
            "Tribulation damage scales with the ABSOLUTE value of morality:\\n" +
            "  Very righteous cultivators face harder tribulations - the heavens test the virtuous more harshly.\\n" +
            "  Very evil cultivators face harder tribulations - the heavens punish the wicked.\\n" +
            "  Neutral cultivators face easier tribulations - but they also lack the conviction of extremes.\\n" +
            "This creates a strategic choice: extreme alignment gives stronger conviction but harder trials,\\n" +
            "while neutrality gives easier trials but less narrative weight.\\n" +
            "\\n" +
            "MORALITY EFFECTS ON HEART DEMON:\\n" +
            "The Heart Demon Trial's boss vitality also scales with morality alignment.\\n" +
            "More extreme alignment = stronger inner demon = harder trial.\\n" +
            "The cultivator must overcome their own nature, whatever that nature may be.");
        TAB_DESCRIPTIONS.put("Formations",
            "Formation arrays are placed structures that provide persistent area effects - the cultivation world's\\n" +
            "equivalent of magical infrastructure. Powered by Formation Core blocks, formations are essential for\\n" +
            "sect defense, efficient cultivation, and resource gathering.\\n" +
            "\\n" +
            "FORMATION CORE TIERS:\\n" +
            "Formation Cores come in 5 tiers, each with exponentially increasing Qi storage capacity:\\n" +
            "  Low (100 Qi) - Basic formations for small areas. Entry-level formation crafting.\\n" +
            "  Mid (1,000 Qi) - Moderate formations for Qi Refining and Foundation Building sects.\\n" +
            "  High (10,000 Qi) - Powerful formations for Golden Core and Nascent Soul sects.\\n" +
            "  Supreme (100,000 Qi) - Advanced formations for Soul Formation and above.\\n" +
            "  Immortal (1,000,000 Qi) - The pinnacle, sustaining True Immortal-level formations.\\n" +
            "Higher tier cores store more Qi, allowing formations to run longer without recharging.\\n" +
            "\\n" +
            "FORMATION TYPES:\\n" +
            "  Qi Gathering Formation - Boosts cultivation speed for all cultivators in the area.\\n" +
            "    This is the most important formation for sects. A well-placed Qi Gathering formation\\n" +
            "    can dramatically accelerate the progress of an entire sect's cultivators.\\n" +
            "    Per-tier multipliers control how much the boost is. Higher tiers = bigger boost.\\n" +
            "    Effect intervals control how often the boost is applied.\\n" +
            "  Growth Formation - Accelerates spirit plant growth in the area.\\n" +
            "    Vital for spirit plant farming operations. Combined with Qi Gathering,\\n" +
            "    creates optimal conditions for alchemy ingredient production.\\n" +
            "    Per-tier multipliers control how much faster plants grow.\\n" +
            "  Barrier Formation - Absorbs damage to protect sect grounds from attack.\\n" +
            "    Uses a Qi-per-damage ratio - how much Qi is consumed per point of damage blocked.\\n" +
            "    Lower ratio = more efficient (blocks more damage per Qi). Higher ratio = less efficient.\\n" +
            "    Essential for sect defense. A strong barrier can make a sect nearly unassailable.\\n" +
            "    Per-tier configurations allow stronger barriers at higher tiers.\\n" +
            "  Rejuvenation Formation - Heals cultivators in range.\\n" +
            "    Useful for recovery after tribulations or combat.\\n" +
            "    Rejuvenation amplifier controls how much healing is provided per tick.\\n" +
            "    Higher tier amplifiers provide faster, stronger healing.\\n" +
            "  Harvest Formation - Automatically collects mature plants in the area.\\n" +
            "    Reduces manual farming labor. Set the harvest interval - how often it checks for mature plants.\\n" +
            "    Lower intervals = more frequent harvesting = less chance of overripe plants.\\n" +
            "    Higher intervals = less frequent but lower CPU usage.\\n" +
            "\\n" +
            "FLAG EFFECT RADIUS:\\n" +
            "All formations have a flag effect radius (1-16 blocks) that determines their area of effect.\\n" +
            "Larger radius = covers more area but may require more Qi to sustain.\\n" +
            "Smaller radius = more focused effect, less Qi consumption.\\n" +
            "Position formation flags strategically to maximize coverage of key areas.\\n" +
            "\\n" +
            "STRATEGIC IMPORTANCE:\\n" +
            "Formations are what separate a disorganized group of cultivators from a proper sect.\\n" +
            "A sect with Qi Gathering formations produces stronger cultivators faster.\\n" +
            "A sect with Barrier formations is protected from attack.\\n" +
            "A sect with Growth and Harvest formations has a sustainable spirit plant economy.\\n" +
            "A sect with Rejuvenation formations can recover quickly from tribulations and combat.\\n" +
            "Investing in formations is investing in the sect's future power and security.");

        // ════════════════════════════════════════════════════════════════
        //  SUB-TAB DESCRIPTIONS
        // ════════════════════════════════════════════════════════════════
        SUBTAB_DESCRIPTIONS.put("Cultivation.Realms",
            "The 12 cultivation realms are the backbone of power progression. From Mortal (no Qi) through\\n" +
            "Qi Refining, Foundation Building, Golden Core, Nascent Soul, Soul Formation, Void Refining,\\n" +
            "Body Integration, Mahayana, Tribulation Transcendence, Loose Immortal, to True Immortal.\\n" +
            "Each realm has 4 sub-stages: Early, Middle, Late, Peak (44 total milestones).\\n" +
            "Max Qi determines storage capacity - mortals have almost none, True Immortals have millions.\\n" +
            "Base Lifespan determines longevity in game years - mortals live ~80, immortals live forever.\\n" +
            "Qi Shield % determines how much incoming damage is absorbed by Qi before reaching HP.\\n" +
            "Tribulation Damage determines how much lightning hurts during realm breakthroughs.\\n" +
            "Global multipliers at the bottom allow quick scaling of all values at once.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Techniques",
            "Techniques are learned skills that provide passive bonuses to a cultivator's capabilities.\\n" +
            "There are 23 technique types, each boosting different attributes: Qi absorption speed,\\n" +
            "attack power, defense, max HP, critical hit rate, elemental spell damage, and movement speed.\\n" +
            "These global multipliers scale the bonuses granted by all techniques.\\n" +
            "Techniques are also prerequisites for cultivation - they unlock the HUD, enable meditation,\\n" +
            "and allow spell casting. A cultivator without techniques cannot progress.\\n" +
            "Techniques are purchased from sect shops, found as loot, or learned from technique books.\\n" +
            "Higher quality techniques provide larger base bonuses, which these multipliers then scale.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Spirit Roots",
            "Spirit Roots determine a cultivator's elemental affinities and cultivation talent.\\n" +
            "They are tested via a Spirit Testing Stone ceremony at character creation.\\n" +
            "Heavenly Roots (single-element) are the rarest and strongest, providing 5-10x cultivation speed\\n" +
            "and the highest elemental spell bonuses. Dual Roots (two elements) are balanced but weaker.\\n" +
            "Mutant Roots have unusual element combinations and get environmental buffs.\\n" +
            "Sword Roots specialize in sword-type spells with high non-elemental spell modifiers.\\n" +
            "Hidden Roots specialize in stealth and utility spells.\\n" +
            "Standard elements: Gold, Wood, Water, Fire, Earth, Ice, and None (Pure).\\n" +
            "Qi absorption multipliers scale gathering speed based on root rarity (SSR and SR gather faster).");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Physiques",
            "Physiques are special body constitutions that grant passive bonuses, rolled at character creation.\\n" +
            "Immortal Body (Immortal tier): 10x Qi absorption, 0.5x Qi cost, 0.5x damage taken, +HP - the strongest.\\n" +
            "Innate Sword Body: 2x sword spell damage, 0.2x non-sword spell damage - sword specialist.\\n" +
            "Heavenly Fire Body: 1.2x fire spell damage - fire specialist.\\n" +
            "Mystic Ice Body: 1.2x water/ice spell damage - ice specialist.\\n" +
            "Sword Bone: 1.2x sword spell damage - lesser sword physique.\\n" +
            "Chaos Body: 1.3x all spell damage with combo system, but 10x cultivation requirement - high risk/reward.\\n" +
            "Broken Vein Body: 2x HP and melee damage but damaged meridians - physical fighter.\\n" +
            "Immortal Blood Body: 2x HP - tank physique.\\n" +
            "Rarity weights control how often each tier appears: LOW (70), MID (20), HIGH (8), SUPREME (3),\\n" +
            "IMMORTAL (1), SPECIAL (1) by default. Higher weights = more common.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Foundation Dao",
            "When a cultivator reaches Foundation Building, they must choose a Dao path that shapes their\\n" +
            "cultivation journey. There are 4 paths, each with distinct bonuses:\\n" +
            "Human Dao: Balanced path - grants lifespan bonus and body defense bonus. The safest choice.\\n" +
            "Blood Dao: Physical combat path - grants lifespan bonus, HP multiplier, body defense, and melee\\n" +
            "damage bonus. Favors melee fighters and body cultivators.\\n" +
            "Earth Dao: Defensive cultivation path - grants lifespan, spell damage multiplier, spell Qi cost\\n" +
            "reduction, body defense, cultivation efficiency bonus, and Qi recovery per second.\\n" +
            "Heaven Dao: Supreme cultivation path - same bonuses as Earth Dao but stronger. The most powerful\\n" +
            "path but also faces the hardest tribulations. Heaven Dao cultivators are the strongest spellcasters.\\n" +
            "Earth and Heaven Dao affect spell damage and Qi cost multipliers for elemental spells,\\n" +
            "while Blood Dao focuses on physical combat and HP. Human Dao is the balanced fallback.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Golden Core Dao",
            "When forming a Golden Core, the cultivator reinforces their Dao path with greater power.\\n" +
            "The same 4 paths are available (Human, Blood, Earth, Heaven) with enhanced bonuses:\\n" +
            "All paths gain lifespan bonus, tribulation strike count, and Shatter Core Trial boss stats\\n" +
            "(max health and regen per second - the boss you fight to form your core).\\n" +
            "Blood Dao additionally grants HP multiplier, blood spell damage multiplier, and blood spell\\n" +
            "Qi cost multiplier - enhancing blood-type spells specifically.\\n" +
            "Earth and Heaven Dao grant spell damage multiplier and spell Qi cost multiplier for elemental\\n" +
            "spells - Heaven Dao has the strongest spell bonuses but the most tribulation strikes.\\n" +
            "The Shatter Core Trial is a boss fight during Golden Core breakthrough. Configurable boss\\n" +
            "stats determine how difficult this fight is. Higher Dao paths face stronger bosses.\\n" +
            "A NONE option exists for cultivators who skip the Dao path choice, gaining no bonuses.");

        SUBTAB_DESCRIPTIONS.put("World.Biome Qi",
            "Qi is not evenly distributed across the world - different biomes have different Qi density profiles.\\n" +
            "Spiritual mountains, sacred forests, and mystical biomes may be Qi-rich, accelerating cultivation.\\n" +
            "Deserts, oceans, and barren biomes may be Qi-poor, slowing cultivation to a crawl.\\n" +
            "Each biome profile defines: max Qi (storage capacity), orb gain (Qi per orb collected),\\n" +
            "and supply per second (passive Qi generation rate).\\n" +
            "Higher density biomes are highly sought after - sects built in Qi-rich areas produce stronger cultivators.\\n" +
            "Biome Qi density stacks with Spirit Vein and Formation bonuses for compound cultivation speed.");
        SUBTAB_DESCRIPTIONS.put("World.Spirit Veins",
            "Spirit Veins are geological formations that generate Qi continuously, serving as the natural cultivation\\n" +
            "resource nodes of the world. They come in 5 tiers from Low to Immortal, each providing exponentially more Qi:\\n" +
            "Low (100 Qi), Mid (1,000), High (10,000), Supreme (100,000), Immortal (1,000,000).\\n" +
            "Max Qi is the storage capacity of the vein. Orb gain is how much Qi each orb provides when collected.\\n" +
            "Supply per second is the passive generation rate - how fast the vein refills.\\n" +
            "Spirit Veins are the foundation of sect placement - a sect built on an Immortal Spirit Vein\\n" +
            "has a massive cultivation advantage. Vein radii determine how far their Qi influence reaches.");
        SUBTAB_DESCRIPTIONS.put("World.Spirit Plants",
            "Spirit Plants are cultivable flora with mystical properties beyond normal Minecraft crops.\\n" +
            "Each has a max age (growth stages) and growth tick rate, plus unique special effects:\\n" +
            "Spirit Gathering Flower - spawns Qi orbs in its vicinity, boosting nearby cultivation.\\n" +
            "Flame Pepper - melts snow and ice around it, creating warm microclimates.\\n" +
            "Snow Soul Lotus - places snow layers around it, creating cold microclimates.\\n" +
            "Golden Chrysanthemum - drops gold nuggets when harvested, a source of wealth.\\n" +
            "Growth ticks control how fast plants mature. Qi orb amounts control how much Qi\\n" +
            "Qi-producing plants generate. Spirit plants are essential for alchemy ingredients\\n" +
            "and cultivation resources.");
        SUBTAB_DESCRIPTIONS.put("World.Loot",
            "Chest loot settings control what cultivation-themed items appear in dungeon chests, sect treasuries,\\n" +
            "and special structures. Roll counts determine how many items appear per chest - more rolls = more items.\\n" +
            "Item weights determine the relative probability of each item type appearing - higher weight = more likely.\\n" +
            "Cultivation loot includes: technique books, spell scrolls, pills, spirit stones, formation cores,\\n" +
            "spirit plant seeds, weapon templates, and rare artifacts.\\n" +
            "Increase weights for items you want to see more often, decrease for rarer items.\\n" +
            "More rolls per chest makes loot richer overall, useful for high-reward dungeon configs.");


        SUBTAB_DESCRIPTIONS.put("Crafting.Alchemy",
            "Alchemy is the art of refining pills from spiritual ingredients using an Alchemy Furnace.\\n" +
            "There are 5 alchemy ranks: Low, Mid, High, Supreme, and Immortal. Higher ranks unlock\\n" +
            "higher tier pill recipes. Max Qi is the furnace's Qi storage - pills require Qi to craft.\\n" +
            "Ticks per pill is the crafting speed - lower values mean faster crafting.\\n" +
            "XP gains determine how fast you level up your alchemy rank. Success XP is gained\\n" +
            "for successful crafts, Failure XP is gained for failed attempts (at a reduced rate).\\n" +
            "Both success and failure contribute to rank progression, but success is much faster.\\n" +
            "Higher tier pills require higher alchemy ranks and rarer ingredients,\\n" +
            "but provide stronger effects (Qi recovery, cultivation boosts, special buffs).");
        SUBTAB_DESCRIPTIONS.put("Crafting.Refining",
            "Refining is the art of forging spiritual weapons and armor using a Refining Furnace.\\n" +
            "Similar to Alchemy with 5 ranks: Low, Mid, High, Supreme, and Immortal.\\n" +
            "Max Qi is the furnace's Qi storage. Ticks per item is the crafting speed.\\n" +
            "XP gains for success and failure determine rank progression speed.\\n" +
            "Tier-up chances are bonus probabilities granted by specific techniques -\\n" +
            "having the right technique can significantly boost your chances of producing\\n" +
            "higher-tier equipment. Refined weapons have tier-based Qi cost reduction,\\n" +
            "meaning higher-tier weapons reduce the Qi cost of spells cast while holding them.\\n" +
            "Refined armor provides enhanced defense and may have special effects\\n" +
            "like burn, freeze, or poison on attackers.");
        SUBTAB_DESCRIPTIONS.put("Crafting.Pills",
            "Pills are consumable items that provide various effects when eaten.\\n" +
            "Qi Pills restore Qi when consumed - higher tier pills restore more.\\n" +
            "Spirit Stones are consumed for Qi recovery, acting as portable Qi batteries.\\n" +
            "Blood Burn Pills trade health for temporary combat power - desperate measures.\\n" +
            "Clear Mind Pills may boost cultivation focus or remove debuffs.\\n" +
            "Divine Stride Pills grant temporary speed buffs for fast travel.\\n" +
            "Youth Pills reduce bone age, effectively making the cultivator younger -\\n" +
            "useful for extending the window to attempt Dao path breakthroughs with age limits.\\n" +
            "Storage Bags have configurable grid dimensions per tier - higher tier bags\\n" +
            "have more columns and rows, providing more inventory space.");


        SUBTAB_DESCRIPTIONS.put("UI.HUD Layout",
            "The in-game HUD displays cultivation info in the top-left corner of the screen.\\n" +
            "Adjust X position to move it left/right and Y position to move it up/down.\\n" +
            "Bar width controls how wide the Qi bar and other bars are.\\n" +
            "Portrait size controls how large the cultivator portrait is.\\n" +
            "Move the HUD to any corner of the screen by adjusting X/Y coordinates.\\n" +
            "On smaller screens, reduce bar width and portrait size to avoid covering gameplay.\\n" +
            "The HUD shows: current realm and sub-stage, Qi amount, lifespan/bone age,\\n" +
            "active passive spells, and morality alignment.");
        SUBTAB_DESCRIPTIONS.put("UI.Colors",
            "Full color customization for all UI elements in the cultivation panel and HUD.\\n" +
            "Use the color preset dropdown to pick from named themes instead of typing hex values:\\n" +
            "Purple (mystical), Gold (imperial), Crimson (aggressive), Jade (serene),\\n" +
            "Azure (scholarly), and more. Each preset sets coordinated colors for all elements.\\n" +
            "Alternatively, set individual colors manually for full control.\\n" +
            "Colorable elements include: panel borders, text colors, bar colors (Qi, HP, XP),\\n" +
            "spell grid borders, technique list colors, and HUD elements.\\n" +
            "A real-time preview panel shows how your color choices look as you change them.");
        SUBTAB_DESCRIPTIONS.put("UI.Background",
            "Background plate settings for the cultivation panel's visual backdrop.\\n" +
            "Page color is the main background behind the entire panel.\\n" +
            "Panel color is the inner panels within the main panel (spell grid, stats, etc.).\\n" +
            "Opacity controls transparency: 0.5 = semi-transparent (see game through panel),\\n" +
            "1.0 = fully solid (no see-through). Semi-transparent panels let you keep an eye\\n" +
            "on the game while managing cultivation, but may be harder to read.\\n" +
            "Solid panels are easier to read but block more of the screen.\\n" +
            "Find a balance that works for your playstyle and screen size.");
        SUBTAB_DESCRIPTIONS.put("UI.Accessibility",
            "Accessibility options including high-contrast mode and HUD visibility.\\n" +
            "High-contrast mode uses brighter, more distinct colors for better readability.\\n" +
            "HUD visibility toggles the in-game cultivation info display.");
        SUBTAB_DESCRIPTIONS.put("UI.Element Pos",
            "Per-element positioning within the cultivation panel.\\n" +
            "Adjust X/Y offsets to move individual elements independently:\\n" +
            "  Realm Name - The cultivation realm text (e.g., 'Qi Refining').\\n" +
            "  Qi Bar - The Qi level progress bar.\\n" +
            "  Cultivation Bar - The cultivation progress bar.\\n" +
            "  Spell Grid - The grid of learned spells.\\n" +
            "  Info Text - The block of info lines (Bone Age, Lifespan, Spirit Root, etc.).\\n" +
            "  Portrait - The cultivator portrait icon.\\n" +
            "Use these to fix overlaps caused by text scale changes, or to create custom layouts.\\n" +
            "0 = default position. Positive = right/down. Negative = left/up.");

        // ── New/updated sub-tab descriptions for reorganized tab structure ──
        SUBTAB_DESCRIPTIONS.put("Cultivation.General",
            "Master on/off switches for each category of config overrides.\\n" +
            "Disable a toggle to revert that category to the original mod's hardcoded values.\\n" +
            "Useful for troubleshooting or if you only want to override specific systems.\\n" +
            "Each toggle controls whether our config extension overrides the original mod's values for that category.\\n" +
            "When a toggle is OFF, all config values in that category are ignored and the original mod's defaults are used.\\n" +
            "When a toggle is ON, our config values take effect, allowing full customization of the cultivation experience.\\n" +
            "Categories include: realms, spells, weapons, pills, alchemy, refining, spirit plants, spirit veins,\\n" +
            "techniques, spirit roots, physiques, Foundation/Golden Core Dao, identity, progression, NPC combat,\\n" +
            "formations, sects, loot, trials, Qi system, passive spells, effects, morality, lifespan, and beast cultivation.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Progression",
            "Cultivation progression rewards and time settings.\\n" +
            "Zhenyuan (stat points) are awarded per breakthrough - both minor (sub-stage) and major (realm).\\n" +
            "Some zhenyuan is auto-assigned to attributes, while the rest is free for the player to spend.\\n" +
            "Time acceleration limits control how fast cultivation can be sped up.\\n" +
            "Bone age progression settings control how quickly the cultivator ages.\\n" +
            "These settings determine the pace of cultivation progression and the reward structure for breakthroughs.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Progression Rules",
            "Rules governing cultivation progression and boundaries.\\n" +
            "Max cultivation cap limits how far cultivators can progress, preventing overpowered worlds.\\n" +
            "Base absorb multiplier scales how efficiently all cultivators absorb Qi from the environment.\\n" +
            "NPC tribulation death chance controls how many NPCs survive breakthrough attempts - higher means more NPC deaths.\\n" +
            "Gender edit settings control whether players can change their character's gender.\\n" +
            "Time estimate days are shown in the UI as rough timeframes for breakthrough preparation.\\n" +
            "These rules shape the overall difficulty and pacing of the cultivation journey.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Identity",
            "Starting lifespan ranges by identity category.\\n" +
            "When a character is created, they draw an identity from a deck of 16 backgrounds.\\n" +
            "Each identity category (martial, scholar, cultivator, abandoned) has a lifespan range.\\n" +
            "Martial identities (Bandit Leader, Pirate, General Son) live shorter but start stronger.\\n" +
            "Scholarly identities (Academy Student, Doctor Heir) live longer.\\n" +
            "Cultivator identities (Lone Cultivator, Hermit Disciple) have balanced lifespans.\\n" +
            "Abandoned identities (Abandoned Infant) have unique potential with variable lifespans.\\n" +
            "These ranges determine how long new characters live based on their background.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Identity Draw",
            "Identity draw deck settings for character creation.\\n" +
            "When a new character is created, they draw identity cards from a deck.\\n" +
            "Deck size controls how many identity cards are available in the draw pool.\\n" +
            "Rounds remaining controls how many re-rolls are allowed - more rounds means more chances to get a desired identity.\\n" +
            "This system adds replayability and variety to character creation,\\n" +
            "as each playthrough starts with a different identity that shapes the early game experience.");
        SUBTAB_DESCRIPTIONS.put("Spells & Combat.Spells",
            "Spell damage, Qi cost, and charge time values.\\n" +
            "Global multipliers affect ALL spells at once - the quickest way to rebalance combat.\\n" +
            "Damage multiplier scales how much hurt all spells deal (2.0 = double damage).\\n" +
            "Qi cost multiplier scales how much Qi all spells consume (0.5 = half cost).\\n" +
            "Charge time multiplier scales how long spells take to cast (0.5 = instant cast).\\n" +
            "Per-spell values control individual spell mechanics for fine-tuned balance.\\n" +
            "Individual spells include: Sword Flight, Void Step, Palm Thunder, Void Escape,\\n" +
            "Buddha Fire Lotus, Core Self Destruct, Sea of Overwhelming Blood, and Glacier Burial.");
        SUBTAB_DESCRIPTIONS.put("Spells & Combat.Passive Spells",
            "Passive spell intervals and Qi costs for always-active abilities.\\n" +
            "Passive spells drain Qi over time to provide persistent effects:\\n" +
            "  Slow Regen - Passively heals HP over time, reducing need for food or potions.\\n" +
            "  Bigu (Fasting) - Prevents hunger, allowing the cultivator to skip eating entirely.\\n" +
            "  Qi Mending - Passively repairs tool and armor durability using Qi.\\n" +
            "  Qi Flight - Allows the cultivator to fly using Qi, with per-second Qi upkeep.\\n" +
            "  Item Attraction - Pulls dropped items toward the cultivator within a configurable radius.\\n" +
            "  Treasure Seizing - Grabs entire item stacks from a distance, more powerful than Item Attraction.\\n" +
            "Each passive spell has a check interval (how often it ticks) and Qi cost per tick.");
        SUBTAB_DESCRIPTIONS.put("Spells & Combat.Weapons",
            "Weapon damage multiplier and tier-based Qi cost reduction.\\n" +
            "Global damage multiplier affects ALL weapons - increase for deadlier combat overall.\\n" +
            "Tier-based Qi cost reduction is the key weapon mechanic: higher-tier weapons reduce\\n" +
            "the Qi cost of spells cast while holding them. This creates a powerful synergy between\\n" +
            "Refining and spellcasting - a well-forged weapon not only deals more damage but also\\n" +
            "makes spells cheaper to cast. A Supreme tier weapon can reduce spell costs by 50%+.\\n" +
            "Special effects on hit include Burn (fire DoT), Freeze (slow), and Poison (DoT + reduced healing).\\n" +
            "Each special effect has configurable duration for sustained damage.");
        SUBTAB_DESCRIPTIONS.put("Sects.Generation",
            "Sect generation parameters for procedural world building.\\n" +
            "Size tiers (13 levels from Humble Cottage to Grand Immortal Sect) control sect scale.\\n" +
            "Building counts determine how many structures each sect generates with.\\n" +
            "Protection arrays define the defensive perimeter of generated sects.\\n" +
            "Settlement cell spawn chance controls how often sects generate in each world chunk.\\n" +
            "Per-tier spawn chances control the distribution of sect sizes - higher tiers are rarer by default.\\n" +
            "These settings shape the cultivation world's social geography.");
        SUBTAB_DESCRIPTIONS.put("Sects.Life & Population",
            "Sect NPC population and recruitment settings.\\n" +
            "Controls how many NPCs live in sects and how they recruit new members.\\n" +
            "Larger sects have more NPCs, creating bustling communities of cultivators.\\n" +
            "Recruitment settings control how sects gain new members over time.\\n" +
            "Higher recruitment rates mean sects grow faster, creating more powerful sects.\\n" +
            "Lower rates mean sects remain small and exclusive.\\n" +
            "These settings determine the social density of the cultivation world.");
        SUBTAB_DESCRIPTIONS.put("Sects.Departments",
            "Sect department settings for specialized operations.\\n" +
            "Each department has a specific function within the sect:\\n" +
            "  Herbal Department - Gathers and cultivates spirit plants.\\n" +
            "  Alchemy Department - Refines pills for sect members.\\n" +
            "  Refining Department - Forges weapons and armor.\\n" +
            "Each department has work points (labor capacity), output caps (max production),\\n" +
            "and buffer targets (desired stock levels). These settings control how efficiently\\n" +
            "each department operates, affecting the sect's overall resource production.");
        SUBTAB_DESCRIPTIONS.put("Sects.Journeys",
            "Sect journey parameters for NPC expeditions.\\n" +
            "Physical journeys send NPCs on expeditions with configurable durations and rewards.\\n" +
            "These create dynamic world events where sect members travel and return with resources.\\n" +
            "Journey durations control how long NPCs are away from the sect.\\n" +
            "Longer journeys may yield greater rewards but leave the sect with fewer members.\\n" +
            "These settings create a dynamic, living world where sect activity is visible.");
        SUBTAB_DESCRIPTIONS.put("Sects.Defense",
            "Sect defense settings for protection against attacks.\\n" +
            "Protection barrier strength controls how much damage the sect's defensive barrier can absorb.\\n" +
            "Dome parameters control the area and shape of the defensive perimeter.\\n" +
            "Stronger barriers make sects harder to raid, protecting the cultivators within.\\n" +
            "Weaker barriers make sects vulnerable, creating opportunities for aggressive players.\\n" +
            "These settings determine the military defensibility of sects in the cultivation world.");
        SUBTAB_DESCRIPTIONS.put("Sects.Schedule",
            "Sect daily schedule with day/night tick boundaries.\\n" +
            "Controls when sect NPCs switch between day and night activities.\\n" +
            "During the day, NPCs may train, work in departments, or socialize.\\n" +
            "At night, they may meditate, rest, or engage in different activities.\\n" +
            "The tick boundaries define when the transition occurs (20 ticks = 1 second).\\n" +
            "These settings create a rhythmic daily cycle that makes sects feel alive\\n" +
            "with NPCs following a believable routine.");
        SUBTAB_DESCRIPTIONS.put("Sects.Overhead UI",
            "Sect overhead nameplate display settings.\\n" +
            "Controls visibility and formatting of sect names above NPCs.\\n" +
            "When enabled, sect affiliation is displayed as a nameplate above each NPC,\\n" +
            "helping players identify which sect an NPC belongs to at a glance.\\n" +
            "Formatting options control the appearance of these nameplates.\\n" +
            "This is a client-side visual setting that doesn't affect gameplay\\n" +
            "but improves information visibility in the cultivation world.");
        SUBTAB_DESCRIPTIONS.put("Sects.Cultivation Profile",
            "Sect cultivation profile settings for NPC progression.\\n" +
            "Controls how sect NPCs' cultivation progress is displayed and managed.\\n" +
            "Determines how quickly sect NPCs advance through realms.\\n" +
            "Faster progression means sects produce powerful cultivators more quickly.\\n" +
            "Slower progression means sects remain at lower power levels longer.\\n" +
            "These settings shape the power curve of sect NPCs over time,\\n" +
            "affecting how challenging sect encounters become as the world ages.");
        SUBTAB_DESCRIPTIONS.put("NPCs.Spawns",
            "NPC spawn chances and realm distribution weights.\\n" +
            "Controls how often wandering cultivators spawn in the world.\\n" +
            "Spawn chance near structures is much higher than far from them, creating cultivation hubs.\\n" +
            "Realm distribution weights control what realm spawned NPCs tend to be.\\n" +
            "Mortals are most common by default, True Immortals are rarest.\\n" +
            "Adjust weights to create a world where high-realm cultivators are more or less common.\\n" +
            "Wandering cultivator spawn multiplier scales the overall spawn rate.");
        SUBTAB_DESCRIPTIONS.put("NPCs.Combat Tactics",
            "NPC combat behavior settings for tactical AI.\\n" +
            "Dodge chance scales with realm - higher realm NPCs dodge more frequently.\\n" +
            "Scan ticks control how often NPCs look for threats - lower is more responsive.\\n" +
            "Reaction ticks control how fast NPCs respond after detecting a threat.\\n" +
            "Dodge cooldown prevents infinite dodging, creating openings for the player.\\n" +
            "Each setting is per-realm, allowing you to scale NPC combat skill with cultivation level.\\n" +
            "This creates a combat difficulty curve where higher realm NPCs are noticeably more skilled.");
        SUBTAB_DESCRIPTIONS.put("NPCs.AI Behavior",
            "NPC AI behavior settings for realistic combat and meditation.\\n" +
            "Stalemate timeout controls how long NPCs will continue an unwinnable fight before retreating.\\n" +
            "Retreat thresholds determine when NPCs decide to flee based on HP or Qi levels.\\n" +
            "Meditation behavior controls whether and how often NPCs meditate to recover Qi when not in combat.\\n" +
            "These settings make NPC behavior more realistic and dynamic,\\n" +
            "creating a world where NPCs act intelligently rather than mindlessly fighting to the death.");
        SUBTAB_DESCRIPTIONS.put("NPCs.Trades",
            "NPC trading settings for commerce with wandering cultivators.\\n" +
            "Controls what items NPCs offer for trade and at what prices.\\n" +
            "Wandering cultivators may sell techniques, pills, spirit stones, and other cultivation items.\\n" +
            "Trade prices are in spirit stones, the cultivation world's primary currency.\\n" +
            "Adjust these settings to control the availability and cost of items from NPC traders.\\n" +
            "This creates an alternative to sect shops for acquiring cultivation resources.");
        SUBTAB_DESCRIPTIONS.put("Beasts & Mobs.Beast Cultivation",
            "Beast (mob) cultivation system for a living, dangerous world.\\n" +
            "Beast advance costs control how much Qi a beast needs to break through to the next realm.\\n" +
            "Check intervals determine how often the system evaluates beast cultivation progress.\\n" +
            "Shorter intervals mean faster beast progression but more CPU usage.\\n" +
            "Mobs accumulate Qi and advance through beast realms, gaining power over time.\\n" +
            "This creates a world where the wilderness becomes increasingly dangerous\\n" +
            "as beasts cultivate, requiring players to clear populations regularly.");
        SUBTAB_DESCRIPTIONS.put("Trials.Heart Demon",
            "Heart Demon trial vitality multipliers by morality band.\\n" +
            "The Heart Demon is a boss that tests your morality - its strength scales with alignment.\\n" +
            "Very righteous cultivators face strong demons - their conviction attracts severe tests.\\n" +
            "Very evil cultivators face strong demons - their corruption empowers their inner darkness.\\n" +
            "Neutral cultivators face weaker demons but lack the conviction of extremes.\\n" +
            "Vitality multipliers are configurable per morality band for fine-tuned difficulty.\\n" +
            "This trial tests whether the cultivator's resolve can overcome their own nature.");
        SUBTAB_DESCRIPTIONS.put("Trials.Loose Immortal",
            "Loose Immortal per-level bonus settings for failed ascenders.\\n" +
            "Loose Immortals gain per-level bonuses for each level achieved in the realm:\\n" +
            "  Body defense bonus, cultivation efficiency, Qi recovery per second.\\n" +
            "  Melee damage, spell damage, max Qi, free zhenyuan (stat points).\\n" +
            "  Tribulation stats for their ongoing tribulation attempts.\\n" +
            "These bonuses make Loose Immortals progressively stronger with each level,\\n" +
            "compensating for their failed ascension. Each level is a meaningful power increase,\\n" +
            "making Loose Immortals dangerous opponents despite not achieving True Immortal status.");
        SUBTAB_DESCRIPTIONS.put("Qi System.Qi Shield",
            "Qi shield mechanics for damage absorption.\\n" +
            "The Qi shield absorbs incoming damage using Qi before it reaches HP.\\n" +
            "The reduction percentage scales with realm - higher realm cultivators have stronger shields.\\n" +
            "At high realms, the Qi shield can absorb 90%+ of incoming damage, making cultivators extremely tanky.\\n" +
            "The shield depletes Qi when absorbing damage - once Qi is gone, damage goes directly to HP.\\n" +
            "This creates a dynamic where Qi management is crucial for survival in combat.\\n" +
            "A cultivator with full Qi can tank many hits, but once Qi is depleted, they become vulnerable.");
        SUBTAB_DESCRIPTIONS.put("Qi System.Spirit Stone Ore",
            "Spirit Stone Ore settings for sustainable Qi harvesting.\\n" +
            "Spirit Stone Ore is a minable Qi source found in the world.\\n" +
            "Max Qi is the storage capacity - how much Qi each ore block can provide.\\n" +
            "Regeneration rate controls how fast the ore refills after being mined.\\n" +
            "High regen rates create sustainable Qi farms that never run out.\\n" +
            "Low rates make ore a finite resource that must be conserved.\\n" +
            "Spirit stones are the primary currency in the cultivation world,\\n" +
            "used in sect shops and for trading. These settings control the economy's foundation.");
        SUBTAB_DESCRIPTIONS.put("Effects & Morality.Status Effects",
            "Status effect values for combat buffs and debuffs.\\n" +
            "Blood Berserk - A powerful combat buff that boosts attack and movement speed.\\n" +
            "  Represents a cultivator entering a battle frenzy. May have drawbacks when it wears off.\\n" +
            "Dao Heart Wound - A debuff from failed tribulations, reducing attack and movement speed.\\n" +
            "  Represents the cultivator's shattered confidence after failing a heavenly trial.\\n" +
            "Shatter Armor - Reduces armor and toughness, leaving the cultivator vulnerable.\\n" +
            "Inverse Five Elements - Inverts elemental damage and cost relationships, creating chaos.\\n" +
            "Each effect has configurable attribute modifier values that determine how strong the effect is.");
        SUBTAB_DESCRIPTIONS.put("Effects & Morality.Morality",
            "Morality alignment thresholds and tribulation damage scaling.\\n" +
            "The Morality System tracks alignment from evil (negative) to righteous (positive).\\n" +
            "Thresholds define when alignment labels change:\\n" +
            "  Below the neutral minimum = 'Evil' alignment.\\n" +
            "  Above the neutral maximum = 'Righteous' alignment.\\n" +
            "  Between the thresholds = 'Neutral' alignment.\\n" +
            "Tribulation damage scales with ABSOLUTE morality - very righteous or very evil = harder tribulations.\\n" +
            "The heavens test those with extreme conviction more harshly.\\n" +
            "Neutral cultivators face easier trials but lack the conviction of extremes.");
        SUBTAB_DESCRIPTIONS.put("Formations.Barriers",
            "Barrier formation Qi-per-damage by tier for sect defense.\\n" +
            "Barrier formations absorb damage to protect sect grounds from attack.\\n" +
            "Qi-per-damage ratio controls how efficiently the barrier converts Qi into damage absorption.\\n" +
            "Lower ratio = more efficient (blocks more damage per Qi spent).\\n" +
            "Higher ratio = less efficient (blocks less damage per Qi).\\n" +
            "Essential for sect defense - a strong barrier can make a sect nearly unassailable.\\n" +
            "Per-tier configurations allow stronger barriers at higher tiers,\\n" +
            "making high-tier sects significantly harder to raid.");
        SUBTAB_DESCRIPTIONS.put("Formations.Rejuvenation",
            "Rejuvenation formation amplifier by tier for healing and recovery.\\n" +
            "Rejuvenation formations heal cultivators in range, useful for recovery after tribulations or combat.\\n" +
            "The amplifier controls how much healing is provided per tick.\\n" +
            "Higher tier amplifiers provide faster, stronger healing.\\n" +
            "A well-placed Rejuvenation formation can keep a sect's cultivators healthy\\n" +
            "without needing pills or meditation, reducing resource consumption\\n" +
            "and allowing cultivators to focus on training rather than recovery.");
        SUBTAB_DESCRIPTIONS.put("Formations.Harvest",
            "Harvest formation interval by tier for automated plant collection.\\n" +
            "Harvest formations automatically collect mature plants in the area.\\n" +
            "The harvest interval controls how often the formation checks for and collects mature plants.\\n" +
            "Lower intervals = more frequent harvesting = less chance of overripe plants being wasted.\\n" +
            "Higher intervals = less frequent but lower CPU usage.\\n" +
            "This reduces manual farming labor, making it essential for large-scale\\n" +
            "spirit plant farming operations. Combined with Growth formations,\\n" +
            "creates a fully automated spirit plant production pipeline.");

        // ════════════════════════════════════════════════════════════════
        //  SUB-TAB DESCRIPTIONS ADDED FOR PREVIOUSLY-UNREACHABLE TOOLTIPS
        //  Every sub-tab below is a real, live entry in CustomConfigScreen's
        //  TAB_TO_SUBTABS map that had no matching key here, so hovering its
        //  button showed nothing. Grounded directly in the real ExtendedConfig
        //  fields for each path prefix.
        // ════════════════════════════════════════════════════════════════
        SUBTAB_DESCRIPTIONS.put("Sects.Ancestor Chances",
            "Chance for a sect's founding ancestor to be an immortal, by sect power score.\\n" +
            "Power 0 (weakest) through Power 4 (strongest) sects each have their own independent chance.\\n" +
            "Higher-power sects roll a higher chance by default, since a stronger sect is more likely\\n" +
            "to have been founded by a more accomplished cultivator.\\n" +
            "A separate chance controls whether that immortal ancestor is specifically a Loose Immortal\\n" +
            "rather than a True Immortal.\\n" +
            "These settings shape how many generated sects have a legendary founder watching over them.");
        SUBTAB_DESCRIPTIONS.put("Sects.Shop Pricing",
            "Sect shop pricing for techniques, spells, and weapons by tier.\\n" +
            "Controls the cultivation economy within sects.\\n" +
            "Each tier of sect sells items at different spirit stone prices.\\n" +
            "Higher tier sects sell better items but at higher prices.\\n" +
            "Sell percent controls what you get back when selling items to sect shops.\\n" +
            "This creates a cultivation economy where spirit stones are the primary currency.\\n" +
            "Adjust prices to make the economy easier (lower prices) or harder (higher prices).");
        SUBTAB_DESCRIPTIONS.put("Sects.Sect Ambient",
            "Ambient scene limits for a sect's background NPC activity.\\n" +
            "Max scenes controls how many ambient scenes (small vignettes of NPCs interacting) can run\\n" +
            "at once per level, and max spectators controls how many NPCs can gather to watch one.\\n" +
            "Min/max cooldown ticks space out how often a sect can start a new ambient scene,\\n" +
            "while the NPC cooldown keeps any one NPC from being pulled into scenes back-to-back.\\n" +
            "These settings tune how lively a sect feels without letting ambient activity\\n" +
            "dominate every NPC's time.");
        SUBTAB_DESCRIPTIONS.put("Sects.Sect Tasks",
            "Sect task parameters for NPC expeditions and duties.\\n" +
            "The task system assigns expeditions and duties to sect members.\\n" +
            "Max required count caps how many items a single task can ask for, and max system\\n" +
            "purchases limits how many purchase-type tasks a sect can have queued at once.\\n" +
            "Expedition min/max days set how long a sect NPC is away on an expedition task.\\n" +
            "Longer expeditions can be tuned to feel like a bigger commitment; shorter ones keep\\n" +
            "sect members cycling back into daily sect life more quickly.");
        SUBTAB_DESCRIPTIONS.put("Sects.Size Tiers",
            "Spawn chance and scaling for each of the 13 sect size tiers, from Humble Cottage up to\\n" +
            "Grand Immortal Sect. All 13 spawn chances are relative to each other - raising one tier's\\n" +
            "value makes it more common relative to the rest, and setting a tier to 0 removes it from\\n" +
            "world generation entirely.\\n" +
            "Building count multiplier controls how many extra structures larger sects get, and minimum\\n" +
            "spacing per tier keeps bigger sects from generating too close together.\\n" +
            "Safe tick is a stability setting - it cancels the original mod's sect NPC repair pass to\\n" +
            "prevent a crash near sects when chunks unload, at the cost of NPCs not self-repairing.");
        SUBTAB_DESCRIPTIONS.put("Sects.NPC Population",
            "Global multiplier for how often wandering cultivator NPCs spawn in the world.\\n" +
            "2.0 doubles the spawn rate, 0.5 halves it.\\n" +
            "This is a single dial for overall cultivator population density - raise it for a busier,\\n" +
            "more populated world, or lower it if wandering cultivators feel too frequent.");
        SUBTAB_DESCRIPTIONS.put("Sects.Crouch Meditation",
            "Enables crouch-meditation - crouching and pressing G to meditate without needing a cushion.\\n" +
            "Crouch-meditation grants the same kind of cultivation progress as cushion meditation, but\\n" +
            "at a lower rate: its cultivation and Qi-drain multipliers both default to 3x, versus\\n" +
            "cushion meditation's 10x, so using an actual cushion stays the more efficient choice.\\n" +
            "This gives players a way to meditate on the move without replacing dedicated meditation.");
        SUBTAB_DESCRIPTIONS.put("Sects.Duty Tasks",
            "Sect task market parameters for escrow and journey timeouts.\\n" +
            "Max escrow stacks caps how many item stacks a task can hold in escrow at once, and max\\n" +
            "system purchase tasks limits how many auto-generated purchase tasks a sect can post.\\n" +
            "Journey min/max timeout ticks bound how long a task-related journey is allowed to run\\n" +
            "before it's considered overdue.\\n" +
            "These settings keep the sect task market from accumulating stuck or abandoned tasks.");
        SUBTAB_DESCRIPTIONS.put("Sects.Ambient Social",
            "Sect ambient social interaction scheduling - a second layer on top of Sect Ambient's\\n" +
            "scene limits, controlling how the game finds and paces NPC social interactions.\\n" +
            "Check interval controls how often the system scans for new interaction opportunities, and\\n" +
            "max active scenes per level caps concurrent interactions the same way Sect Ambient does.\\n" +
            "Min/max sect cooldown ticks space out how often a given sect can start a new interaction,\\n" +
            "and pair search distance sets how far apart two NPCs can be and still be paired up.\\n" +
            "These settings don't affect gameplay mechanics directly but shape how alive sects feel.");
        SUBTAB_DESCRIPTIONS.put("Beasts & Mobs.Beast Advance Costs",
            "Qi cost required for a beast to advance up each rank of beast cultivation, from Mortal\\n" +
            "Beast through Spirit Soldier, General, Marshal, King, Emperor, and finally Spirit Lord.\\n" +
            "Each rank costs roughly ten times the previous one by default, mirroring how demanding\\n" +
            "cultivator realm breakthroughs become at higher stages.\\n" +
            "Lower these to let beasts climb the ranks faster, or raise them to make a fully-ranked\\n" +
            "beast companion or foe a much rarer sight.");
        SUBTAB_DESCRIPTIONS.put("Beasts & Mobs.NPC Spawns",
            "The same wandering cultivator spawn chances and realm distribution weights as the NPCs\\n" +
            "tab's Spawns page, shown here as well since spawn behavior is just as relevant to beast\\n" +
            "and mob balance - beasts and cultivator NPCs share the same world spawn budget.\\n" +
            "Adjust it from either tab; both edit the exact same settings.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Core Data",
            "Core cultivation timing and reward constants shared across every realm.\\n" +
            "Max time acceleration multiplier caps how fast meditation time-skip can run.\\n" +
            "Tribulation interval and charge ticks control the pacing of tribulation events during a\\n" +
            "breakthrough attempt, and equipped slot count sets how many equippable item slots a\\n" +
            "cultivator has.\\n" +
            "Zhenyuan attribute rewards control how many free stat points a character gains for a minor\\n" +
            "sub-stage advancement versus a full major realm breakthrough.");
        SUBTAB_DESCRIPTIONS.put("Cultivation.Golden Finger",
            "Number of Golden Finger perks a player selects during world creation.\\n" +
            "Golden Finger perks are the special starting advantages offered at character creation.\\n" +
            "The default of 3 lets a new character pick three; raising it allows more powerful starts,\\n" +
            "lowering it makes the early game harder.");
        SUBTAB_DESCRIPTIONS.put("Effects & Morality.Morality Bounds",
            "The minimum and maximum values a character's morality score can ever reach.\\n" +
            "Morality drifts up or down based on a character's actions and is what the Morality tab's\\n" +
            "alignment thresholds (neutral, righteous, evil, and their great variants) are measured\\n" +
            "against. These bounds simply clamp how far morality can drift in either direction before\\n" +
            "it stops changing.");
        SUBTAB_DESCRIPTIONS.put("Formations.Core Max Qi",
            "Maximum Qi storage capacity for formation cores, by tier: LOW, MID, HIGH, SUPREME, and\\n" +
            "IMMORTAL.\\n" +
            "A formation core's max Qi determines how much Qi it can bank before it needs to be\\n" +
            "harvested or before it stops absorbing more.\\n" +
            "Higher tiers store dramatically more by default - each tier is roughly an order of\\n" +
            "magnitude above the last, so a formation's tier is the main lever on its usefulness.");
        SUBTAB_DESCRIPTIONS.put("Formations.Qi Gathering",
            "Qi gathering multiplier for Qi-gathering formations, by tier: LOW, MID, HIGH, SUPREME, and\\n" +
            "IMMORTAL, plus a shared max multiplier cap.\\n" +
            "These formations speed up passive Qi accumulation for cultivators meditating nearby.\\n" +
            "Higher-tier formations gather Qi faster; the max multiplier is a ceiling that applies\\n" +
            "across all tiers so gathering speed can never exceed a sane upper bound.");
        SUBTAB_DESCRIPTIONS.put("Formations.Growth",
            "Growth multiplier for Growth formations, by tier: LOW, MID, HIGH, SUPREME, and IMMORTAL.\\n" +
            "Growth formations speed up the maturation of spirit plants planted within their range.\\n" +
            "Higher tiers grow plants faster - the IMMORTAL tier's default 10x is five times the LOW\\n" +
            "tier's 2x - making formation tier the main investment for a serious spirit plant farm.");
        SUBTAB_DESCRIPTIONS.put("Formations.Formation Core",
            "Low-level timing constants for how formation cores tick and validate themselves.\\n" +
            "Effect interval and duration control how often and how long a formation's visual/gameplay\\n" +
            "effect runs; the generated-array sync interval and reload-flag validation grace period\\n" +
            "govern how formations recover their state after a chunk reload.\\n" +
            "Growth tick interval and storage core interval pace those specific formation types, and\\n" +
            "the farm-harvest and flag-effect-radius settings tune how Harvest formations scan for\\n" +
            "mature crops and how far a formation flag's effect reaches.\\n" +
            "These are tuning knobs for formation performance and responsiveness, not their core power.");
        SUBTAB_DESCRIPTIONS.put("Lifespan.Aging & Death",
            "Core aging constants: starting bone age, aging rate, and the penalty for dying of old age.\\n" +
            "New characters start with a random bone age between the configured min and max.\\n" +
            "Age per day controls normal aging speed; a separate, independently-tunable rate applies\\n" +
            "while a character is meditating.\\n" +
            "The near-immortal threshold marks the bone age where aging behavior changes for characters\\n" +
            "approaching the top of the lifespan curve, and the ordinary death penalty sets how many\\n" +
            "years of lifespan are lost on a normal (non-tribulation) death.");
        SUBTAB_DESCRIPTIONS.put("Qi System.Qi Attraction",
            "Base radius and meditation bonuses for how players passively draw in ambient Qi.\\n" +
            "Attraction radius sets the base range around a player that pulls in nearby Qi.\\n" +
            "Meditation range bonus extends that radius while meditating, and meditation efficiency\\n" +
            "bonus increases how effectively the Qi within range is absorbed.\\n" +
            "Together these control how quickly a meditating character refills their Qi compared to\\n" +
            "one who is simply standing around.");
        SUBTAB_DESCRIPTIONS.put("Trials.Inner World",
            "Arena parameters for the Inner World trial.\\n" +
            "Platform diameter and Y level define the size and height of the trial arena itself.\\n" +
            "Soul wound ticks sets how long the lingering soul-wound effect lasts after the trial, and\\n" +
            "failure health penalty percent is how much of a character's health is docked if the trial\\n" +
            "is failed.\\n" +
            "Time stasis duration controls how long the arena's time-freeze effect holds during the\\n" +
            "trial sequence.");
        SUBTAB_DESCRIPTIONS.put("UI.General Toggles",
            "General, top-level toggles for the config extension's client-side behavior.\\n" +
            "Currently controls whether the vanilla Minecraft difficulty button is removed from the\\n" +
            "Create World screen - useful if you want to steer new players toward this mod's own\\n" +
            "difficulty-relevant settings instead.");
        SUBTAB_DESCRIPTIONS.put("UI.Status Bar",
            "Row height, in pixels, for the status bars shown in the cultivation panel.\\n" +
            "This is a single layout dial - raise it for taller, easier-to-read bars, or lower it to\\n" +
            "fit more information into the same panel space.");
        SUBTAB_DESCRIPTIONS.put("UI.Preferences",
            "Accessibility and comfort preferences for the config screen and cultivation panel.\\n" +
            "Reduce motion turns off tab glow and value-transition animations for players who prefer a\\n" +
            "static UI or have motion sensitivity.\\n" +
            "Font size percent scales config screen text up or down, and compact layout tightens entry\\n" +
            "spacing to fit more on screen at once.\\n" +
            "Cultivation panel tooltips toggles the explanatory hover-tooltips on the in-game character\\n" +
            "panel's Name, Race, Realm, Morality, Bone Age, Lifespan, and stat bar fields.");
        SUBTAB_DESCRIPTIONS.put("UI.Advanced",
            "Advanced, opt-in UI features layered on top of the base config screen.\\n" +
            "Notifications shows a toast when a config value changes; inline help shows a (?) icon with\\n" +
            "extra context next to supported entries.\\n" +
            "Per-world overrides let a config value apply only while a specific world is loaded, tab\\n" +
            "order lets you rearrange the top-level tab buttons, and custom tabs let you group your own\\n" +
            "chosen config paths under a tab of your own naming.\\n" +
            "Config dependencies is the backing store for rules that show or hide one setting based on\\n" +
            "another setting's value.");

        // ════════════════════════════════════════════════════════════════
        //  INDIVIDUAL CONFIG ENTRY DESCRIPTIONS
        // ════════════════════════════════════════════════════════════════

        // ── General Toggles ──
        add("enableRealmOverrides", "Enable Realm Overrides", "Master switch for realm power level overrides. When enabled, all realm settings below take effect. When disabled, the original mod's hardcoded values are used.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("realm", "enable", "toggle", "master", "switch"));
        add("enableSpellOverrides", "Enable Spell Overrides", "Master switch for spell damage and Qi cost overrides.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("spell", "enable", "toggle", "master"));
        add("enableWeaponOverrides", "Enable Weapon Overrides", "Master switch for weapon damage and special effect overrides.",
            "No effect (boolean)", "No effect (boolean)", "Crafting", "General Toggles", "",
            List.of("weapon", "enable", "toggle", "master"));
        add("enablePillOverrides", "Enable Pill Overrides", "Master switch for pill Qi recovery and effect overrides.",
            "No effect (boolean)", "No effect (boolean)", "Crafting", "General Toggles", "",
            List.of("pill", "enable", "toggle", "master"));
        add("enableAlchemyOverrides", "Enable Alchemy Overrides", "Master switch for alchemy furnace and XP gain overrides.",
            "No effect (boolean)", "No effect (boolean)", "Crafting", "General Toggles", "",
            List.of("alchemy", "enable", "toggle", "master"));
        add("enableRefiningOverrides", "Enable Refining Overrides", "Master switch for refining furnace and XP gain overrides.",
            "No effect (boolean)", "No effect (boolean)", "Crafting", "General Toggles", "",
            List.of("refining", "enable", "toggle", "master"));
        add("enableSpiritPlantOverrides", "Enable Spirit Plant Overrides", "Master switch for spirit plant growth and effect overrides.",
            "No effect (boolean)", "No effect (boolean)", "World", "General Toggles", "",
            List.of("plant", "spirit", "enable", "toggle"));
        add("enableSpiritVeinOverrides", "Enable Spirit Vein Overrides", "Master switch for spirit vein core tier overrides.",
            "No effect (boolean)", "No effect (boolean)", "World", "General Toggles", "",
            List.of("vein", "spirit", "enable", "toggle"));
        add("enableTechniqueOverrides", "Enable Technique Overrides", "Master switch for technique bonus multiplier overrides.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("technique", "enable", "toggle"));
        add("enableSpiritRootOverrides", "Enable Spirit Root Overrides", "Master switch for spirit root bonus multiplier overrides.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("root", "spirit", "enable", "toggle"));
        add("enablePhysiqueOverrides", "Enable Physique Overrides", "Master switch for physique bonus and rarity weight overrides.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("physique", "body", "enable", "toggle"));
        add("enableDaoOverrides", "Enable Dao Overrides", "Master switch for Foundation and Golden Core Dao path overrides.",
            "No effect (boolean)", "No effect (boolean)", "Cultivation", "General Toggles", "",
            List.of("dao", "foundation", "golden core", "enable"));
        add("enableIdentityOverrides", "Enable Identity Overrides", "Master switch for starting identity lifespan range overrides.",
            "No effect (boolean)", "No effect (boolean)", "NPCs", "General Toggles", "",
            List.of("identity", "lifespan", "enable", "toggle"));
        add("enableNpcCombatOverrides", "Enable NPC Combat Overrides", "Master switch for NPC combat tactic and dodge profile overrides.",
            "No effect (boolean)", "No effect (boolean)", "NPCs", "General Toggles", "",
            List.of("npc", "combat", "dodge", "enable"));
        add("enableFormationOverrides", "Enable Formation Overrides", "Master switch for formation power and effect overrides.",
            "No effect (boolean)", "No effect (boolean)", "World", "General Toggles", "",
            List.of("formation", "array", "enable", "toggle"));
        add("enableSectOverrides", "Enable Sect Overrides", "Master switch for sect generation and shop pricing overrides.",
            "No effect (boolean)", "No effect (boolean)", "NPCs", "General Toggles", "",
            List.of("sect", "shop", "enable", "toggle"));
        add("enableLootOverrides", "Enable Loot Overrides", "Master switch for chest loot drop weight and roll count overrides.",
            "No effect (boolean)", "No effect (boolean)", "World", "General Toggles", "",
            List.of("loot", "chest", "drop", "enable"));
        add("enableTrialOverrides", "Enable Trial Overrides", "Master switch for heart demon and inner world trial overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("trial", "demon", "heart", "enable"));
        add("enableQiSystemOverrides", "Enable Qi System Overrides", "Master switch for Qi attraction, shield, and ore overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("qi", "system", "shield", "ore", "enable"));
        add("enablePassiveSpellOverrides", "Enable Passive Spell Overrides", "Master switch for passive spell interval and cost overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("passive", "spell", "enable", "toggle"));
        add("enableEffectOverrides", "Enable Effect Overrides", "Master switch for status effect value overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("effect", "status", "enable", "toggle"));
        add("enableMoralityOverrides", "Enable Morality Overrides", "Master switch for morality threshold and tribulation scaling overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("morality", "alignment", "enable", "toggle"));
        add("enableLifespanOverrides", "Enable Lifespan Overrides", "Master switch for lifespan aging constant overrides.",
            "No effect (boolean)", "No effect (boolean)", "System", "General Toggles", "",
            List.of("lifespan", "age", "enable", "toggle"));

        // ── Realm: Max Qi ──
        add("realms.mortal.maxQi", "Mortal Max Qi", "Maximum Qi a Mortal (non-cultivator) can store. Mortals are ordinary people with no cultivation. They typically have very little or no Qi.",
            "More Qi storage for mortals", "Less Qi storage for mortals", "Cultivation", "Realms", "Max Qi",
            List.of("mortal", "qi", "max", "capacity", "realm"));
        add("realms.qiRefining.earlyMaxQi", "Qi Refining (Early) Max Qi", "Max Qi for a Qi Refining cultivator in the Early sub-stage. Qi Refining is the first real cultivation realm where cultivators begin gathering Qi.",
            "More Qi capacity = can cast more spells before running out", "Less Qi capacity = must meditate more often", "Cultivation", "Realms", "Max Qi",
            List.of("qi refining", "early", "max qi", "capacity", "realm"));
        add("realms.qiRefining.middleMaxQi", "Qi Refining (Middle) Max Qi", "Max Qi for Qi Refining Middle sub-stage.",
            "More Qi capacity", "Less Qi capacity", "Cultivation", "Realms", "Max Qi",
            List.of("qi refining", "middle", "max qi", "capacity"));
        add("realms.qiRefining.lateMaxQi", "Qi Refining (Late) Max Qi", "Max Qi for Qi Refining Late sub-stage.",
            "More Qi capacity", "Less Qi capacity", "Cultivation", "Realms", "Max Qi",
            List.of("qi refining", "late", "max qi", "capacity"));
        add("realms.qiRefining.peakMaxQi", "Qi Refining (Peak) Max Qi", "Max Qi for Qi Refining Peak sub-stage. This is the cap before breaking through to Foundation Building.",
            "More Qi capacity = stronger peak Qi Refining cultivators", "Less Qi capacity = faster breakthrough needed", "Cultivation", "Realms", "Max Qi",
            List.of("qi refining", "peak", "max qi", "capacity", "breakthrough"));
        add("realms.baseDeltaEarly", "Realm Base Delta (Early)", "Base Qi increase for the Early sub-stage of all realms above Qi Refining. This is added to the previous realm's peak Qi.",
            "Larger jump in Qi between realms = faster power growth", "Smaller jump = more gradual progression", "Cultivation", "Realms", "Max Qi",
            List.of("delta", "base", "early", "realm", "progression"));
        add("realms.baseDeltaMiddle", "Realm Base Delta (Middle)", "Base Qi increase for Middle sub-stage of all realms above Qi Refining.",
            "Larger Qi increase", "Smaller Qi increase", "Cultivation", "Realms", "Max Qi",
            List.of("delta", "base", "middle", "realm"));
        add("realms.baseDeltaLate", "Realm Base Delta (Late)", "Base Qi increase for Late sub-stage of all realms above Qi Refining.",
            "Larger Qi increase", "Smaller Qi increase", "Cultivation", "Realms", "Max Qi",
            List.of("delta", "base", "late", "realm"));
        add("realms.baseDeltaPeak", "Realm Base Delta (Peak)", "Base Qi increase for Peak sub-stage of all realms above Qi Refining.",
            "Larger Qi increase = stronger peak cultivators", "Smaller Qi increase", "Cultivation", "Realms", "Max Qi",
            List.of("delta", "base", "peak", "realm"));
        add("realms.trueImmortalMaxQi", "True Immortal Max Qi", "Max Qi for a True Immortal - the highest cultivation realm. True Immortals are essentially gods.",
            "Enormous Qi capacity for immortal-level beings", "Less Qi for immortals", "Cultivation", "Realms", "Max Qi",
            List.of("true immortal", "max qi", "capacity", "god", "immortal"));
        add("realms.looseImmortalMaxQi", "Loose Immortal Max Qi", "Max Qi for a Loose Immortal - a being who failed to ascend but achieved immortality through alternative means.",
            "More Qi for loose immortals", "Less Qi for loose immortals", "Cultivation", "Realms", "Max Qi",
            List.of("loose immortal", "max qi", "capacity", "immortal"));
        add("realms.globalMultiplier", "Max Qi Global Multiplier", "A multiplier applied to ALL realm Max Qi values after individual calculations. 1.0 = no change. This is the quickest way to scale all Qi capacity up or down.",
            "2.0 = double all Qi capacity (more spell casting, slower Qi depletion)", "0.5 = half all Qi capacity (faster depletion, more meditation needed)", "Cultivation", "Realms", "Max Qi",
            List.of("global", "multiplier", "qi", "scale", "all", "quick"));

        // ── Realm: Lifespan ──
        addL("realms.lifespan.qiRefining", "Qi Refining Lifespan", "Lifespan (in game years) for a Qi Refining cultivator. When a cultivator exceeds this age, they die of old age unless they break through to a higher realm.",
            "Longer lifespan = more time to cultivate before aging", "Shorter lifespan = must break through faster", "Cultivation", "Realms", "Lifespan", "qi refining lifespan age");
        addL("realms.lifespan.foundationBuilding", "Foundation Building Lifespan", "Lifespan for Foundation Building realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "foundation lifespan age");
        addL("realms.lifespan.goldenCore", "Golden Core Lifespan", "Lifespan for Golden Core realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "golden core lifespan age");
        addL("realms.lifespan.nascentSoul", "Nascent Soul Lifespan", "Lifespan for Nascent Soul realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "nascent soul lifespan age");
        addL("realms.lifespan.soulFormation", "Soul Formation Lifespan", "Lifespan for Soul Formation realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "soul formation lifespan");
        addL("realms.lifespan.voidRefining", "Void Refining Lifespan", "Lifespan for Void Refining realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "void refining lifespan");
        addL("realms.lifespan.bodyIntegration", "Body Integration Lifespan", "Lifespan for Body Integration realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "body integration lifespan");
        addL("realms.lifespan.mahayana", "Mahayana Lifespan", "Lifespan for Mahayana realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "mahayana lifespan");
        addL("realms.lifespan.tribulationTranscendence", "Tribulation Transcendence Lifespan", "Lifespan for Tribulation Transcendence realm cultivators.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "tribulation transcendence lifespan");
        addL("realms.lifespan.looseImmortal", "Loose Immortal Lifespan", "Lifespan for Loose Immortals.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "loose immortal lifespan");
        addL("realms.lifespan.trueImmortal", "True Immortal Lifespan", "Lifespan for True Immortals. They are essentially ageless.",
            "Longer lifespan", "Shorter lifespan", "Cultivation", "Realms", "Lifespan", "true immortal lifespan");
        add("realms.lifespan.globalMultiplier", "Lifespan Global Multiplier", "Multiplier applied to ALL realm lifespan values. 1.0 = no change. Quick way to make everyone live longer or shorter.",
            "2.0 = double all lifespans", "0.5 = half all lifespans", "Cultivation", "Realms", "Lifespan",
            List.of("global", "multiplier", "lifespan", "scale", "all"));

        // ── Realm: Qi Shield ──
        addS("realms.qiShieldReductionPercent.qiRefining", "Qi Refining Shield %", "Percentage of damage absorbed by Qi shield for Qi Refining cultivators. When attacked, this % of damage is absorbed using Qi instead of health.",
            "More damage absorbed by Qi = less health damage taken", "Less damage absorbed = more health damage taken", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.foundationBuilding", "Foundation Building Shield %", "Qi shield damage absorption % for Foundation Building.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.goldenCore", "Golden Core Shield %", "Qi shield damage absorption % for Golden Core.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.nascentSoul", "Nascent Soul Shield %", "Qi shield damage absorption % for Nascent Soul.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.soulFormation", "Soul Formation Shield %", "Qi shield damage absorption % for Soul Formation.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.voidRefining", "Void Refining Shield %", "Qi shield damage absorption % for Void Refining.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.bodyIntegration", "Body Integration Shield %", "Qi shield damage absorption % for Body Integration.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.mahayana", "Mahayana Shield %", "Qi shield damage absorption % for Mahayana.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.tribulationTranscendence", "Tribulation Transcendence Shield %", "Qi shield damage absorption % for Tribulation Transcendence.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.trueImmortal", "True Immortal Shield %", "Qi shield damage absorption % for True Immortal. At 100%, all damage is absorbed by Qi.",
            "More damage absorbed (100 = invulnerable while Qi lasts)", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");
        addS("realms.qiShieldReductionPercent.looseImmortal", "Loose Immortal Shield %", "Qi shield damage absorption % for Loose Immortal.",
            "More damage absorbed", "Less damage absorbed", "Cultivation", "Realms", "Qi Shield");

        // ── Realm: Tribulation Damage ──
        addT("realms.tribulationDamage.qiRefining", "Qi Refining Tribulation Damage", "Damage per tribulation strike when a Qi Refining cultivator faces heavenly tribulation. Tribulations occur when breaking through to the next realm.",
            "More damage = harder tribulations", "Less damage = easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.foundationBuilding", "Foundation Building Tribulation Damage", "Tribulation strike damage for Foundation Building breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.goldenCore", "Golden Core Tribulation Damage", "Tribulation strike damage for Golden Core breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.nascentSoul", "Nascent Soul Tribulation Damage", "Tribulation strike damage for Nascent Soul breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.soulFormation", "Soul Formation Tribulation Damage", "Tribulation strike damage for Soul Formation breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.voidRefining", "Void Refining Tribulation Damage", "Tribulation strike damage for Void Refining breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.bodyIntegration", "Body Integration Tribulation Damage", "Tribulation strike damage for Body Integration breakthrough.",
            "Harder tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        addT("realms.tribulationDamage.tribulationTranscendence", "Tribulation Transcendence Damage", "Tribulation strike damage for Tribulation Transcendence breakthrough - the final tribulation before immortality.",
            "Extremely hard tribulations", "Easier tribulations", "Cultivation", "Realms", "Tribulation");
        add("realms.tribulationDamage.globalMultiplier", "Tribulation Damage Global Multiplier", "Multiplier for all tribulation damage values. 1.0 = no change.",
            "2.0 = double tribulation damage (much harder)", "0.5 = half tribulation damage (much easier)", "Cultivation", "Realms", "Tribulation",
            List.of("global", "multiplier", "tribulation", "damage", "scale"));

        // ── Realm: Loose Immortal ──
        add("realms.looseImmortal.baseReductionPercent", "Loose Immortal Base Reduction %", "Base damage reduction % for Loose Immortals. They take reduced damage from all sources due to their semi-immortal state.",
            "More damage reduction = tankier loose immortals", "Less damage reduction = more vulnerable", "Cultivation", "Realms", "Loose Immortal",
            List.of("loose immortal", "reduction", "damage", "defense"));
        add("realms.looseImmortal.fullReductionTribulations", "Loose Immortal Full Reduction Tribulations", "Number of tribulations a Loose Immortal fully reduces. After this many tribulations, their damage reduction decreases.",
            "More tribulations with full reduction = safer", "Fewer = reduction drops sooner", "Cultivation", "Realms", "Loose Immortal",
            List.of("loose immortal", "tribulation", "reduction", "full"));

        // ── Realm: Tribulation Timing ──
        add("realms.tribulationTiming.intervalTicks", "Tribulation Interval Ticks", "Ticks between each tribulation strike. 20 ticks = 1 second. Lower = faster strikes.",
            "More time between strikes = easier to react", "Less time = strikes come faster (harder)", "Cultivation", "Realms", "Tribulation Timing",
            List.of("tribulation", "timing", "interval", "ticks", "speed"));
        add("realms.tribulationTiming.chargeTicks", "Tribulation Charge Ticks", "Ticks of charging before tribulation begins. This is the warning period before lightning starts falling.",
            "Longer charge = more time to prepare", "Shorter charge = less warning time", "Cultivation", "Realms", "Tribulation Timing",
            List.of("tribulation", "charge", "ticks", "warning", "delay"));
        add("realms.tribulationTiming.boltCooldownTicks", "Tribulation Bolt Cooldown", "Cooldown ticks after each tribulation wave before the next wave starts.",
            "Longer cooldown = more recovery time between waves", "Shorter cooldown = waves come faster", "Cultivation", "Realms", "Tribulation Timing",
            List.of("tribulation", "bolt", "cooldown", "wave", "delay"));

        // ── Realm: Zhenyuan Rewards ──
        add("realms.zhenyuanRewards.minor", "Zhenyuan Reward (Minor)", "Free Zhenyuan (cultivation points) awarded per minor breakthrough (sub-stage advancement). Zhenyuan can be spent on attributes.",
            "More free points per breakthrough = faster attribute growth", "Fewer points = slower attribute growth", "Cultivation", "Realms", "Zhenyuan",
            List.of("zhenyuan", "reward", "minor", "breakthrough", "points"));
        add("realms.zhenyuanRewards.major", "Zhenyuan Reward (Major)", "Free Zhenyuan points awarded per major realm breakthrough (e.g., Qi Refining to Foundation Building).",
            "More free points per major breakthrough", "Fewer points", "Cultivation", "Realms", "Zhenyuan",
            List.of("zhenyuan", "reward", "major", "realm", "breakthrough"));
        add("realms.zhenyuanRewards.attrMinor", "Attribute Reward (Minor)", "Auto-assigned Zhenyuan attribute points per minor breakthrough. These are distributed automatically rather than letting you choose.",
            "More auto-assigned attributes", "Fewer auto-assigned attributes", "Cultivation", "Realms", "Zhenyuan",
            List.of("zhenyuan", "attribute", "auto", "minor", "breakthrough"));
        add("realms.zhenyuanRewards.attrMajor", "Attribute Reward (Major)", "Auto-assigned Zhenyuan attribute points per major realm breakthrough.",
            "More auto-assigned attributes", "Fewer auto-assigned attributes", "Cultivation", "Realms", "Zhenyuan",
            List.of("zhenyuan", "attribute", "auto", "major", "breakthrough"));

        // ── Realm: Time Acceleration ──
        add("realms.timeAcceleration.min", "Time Acceleration Min", "Minimum time acceleration multiplier. Time acceleration speeds up cultivation progress.",
            "Higher minimum = faster base cultivation", "Lower minimum = slower base cultivation", "Cultivation", "Realms", "Time Acceleration",
            List.of("time", "acceleration", "min", "speed", "cultivation"));
        add("realms.timeAcceleration.max", "Time Acceleration Max", "Maximum time acceleration multiplier.",
            "Higher maximum = can accelerate more", "Lower maximum = less acceleration possible", "Cultivation", "Realms", "Time Acceleration",
            List.of("time", "acceleration", "max", "speed", "cultivation"));

        // ── Realm: NPC Spawn Weights ──
        // These control how often NPCs of each realm appear in the world.
        // Higher weight = more common. Lower weight = rarer.
        add("spawn.npcWeight.mortal", "NPC Spawn Weight: Mortal",
            "Spawn weight for Mortal NPCs in the world. Mortals are ordinary humans with no cultivation ability. " +
            "They form the bulk of the population in any cultivation world - farmers, merchants, soldiers, and common folk. " +
            "In the original mod, mortals make up the vast majority of NPC spawns. " +
            "At default weight 1000, mortals are by far the most common NPC type. " +
            "Reduce this to create a world where cultivators are more common relative to mortals, " +
            "or increase it for a more realistic cultivation world where mortals dominate and cultivators are rare exceptions.",
            "Higher = more mortal NPCs (ordinary humans everywhere)", "Lower = fewer mortals (cultivators more visible)",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "mortal", "common", "human"));
        add("spawn.npcWeight.qiRefining", "NPC Spawn Weight: Qi Refining",
            "Spawn weight for Qi Refining NPCs. Qi Refining cultivators have taken the first step into cultivation - " +
            "they can sense Qi of heaven and earth and begin absorbing it. They are the most common type of cultivator. " +
            "At default weight 500, they are the second most common NPC after mortals. " +
            "Qi Refining NPCs can cast basic spells and have a Qi shield, making them slightly dangerous to mortals. " +
            "They typically live in small sects or as wandering cultivators, gathering Qi to break through to Foundation Building.",
            "Higher = more Qi Refining cultivators in the world", "Lower = fewer entry-level cultivators",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "qi", "refining", "cultivator"));
        add("spawn.npcWeight.foundationBuilding", "NPC Spawn Weight: Foundation Building",
            "Spawn weight for Foundation Building NPCs. These cultivators have stabilized their spiritual roots " +
            "and formed a foundation of Qi within their body. They are significantly more powerful than Qi Refining " +
            "cultivators, with higher spell damage, larger Qi capacity, and longer lifespans. " +
            "At default weight 250, they are moderately common. " +
            "Foundation Building NPCs have chosen a Dao path (Human, Blood, Earth, or Heaven), " +
            "which shapes their combat style and bonuses. They are typically found in established sects.",
            "Higher = more Foundation Building cultivators (mid-tier power)", "Lower = fewer mid-tier cultivators",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "foundation", "building", "dao"));
        add("spawn.npcWeight.goldenCore", "NPC Spawn Weight: Golden Core",
            "Spawn weight for Golden Core NPCs. These cultivators have compressed their Qi into a Golden Core, " +
            "a massive power jump that multiplies their Qi storage and spell power. " +
            "At default weight 125, they are uncommon but not rare. " +
            "Golden Core NPCs have reinforced their Foundation Dao path with enhanced bonuses. " +
            "They can cast devastating spells and have significant Qi shields. " +
            "In the cultivation world, Golden Core cultivators are considered experts - " +
            "they lead small sects or serve as elders in larger ones.",
            "Higher = more Golden Core cultivators (powerful experts)", "Lower = Golden Core cultivators are rare",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "golden", "core", "expert"));
        add("spawn.npcWeight.nascentSoul", "NPC Spawn Weight: Nascent Soul",
            "Spawn weight for Nascent Soul NPCs. These cultivators have nurtured an independent soul body within " +
            "their Golden Core, gaining the ability to fly effortlessly and wield world-shaking power. " +
            "At default weight 60, they are rare. " +
            "Nascent Soul NPCs are major figures in the cultivation world - sect masters, wandering seniors, " +
            "or reclusive hermits. Their spells can reshape terrain and their Qi capacity is vast. " +
            "Encountering a Nascent Soul cultivator is a significant event for any player.",
            "Higher = more Nascent Soul cultivators (sect master level)", "Lower = Nascent Soul cultivators are very rare",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "nascent", "soul", "master"));
        add("spawn.npcWeight.soulFormation", "NPC Spawn Weight: Soul Formation",
            "Spawn weight for Soul Formation NPCs. These cultivators have severed their soul from their body, " +
            "gaining vast magical power and the ability to master the most powerful spells. " +
            "At default weight 30, they are very rare. " +
            "Soul Formation cultivators are legendary figures - grand elders of great sects, " +
            "or ancient hermits living in secluded spiritual mountains. " +
            "Their power is such that they can single-handedly destroy small sects.",
            "Higher = more Soul Formation cultivators (legendary elders)", "Lower = Soul Formation cultivators are extremely rare",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "soul", "formation", "legendary"));
        add("spawn.npcWeight.voidRefining", "NPC Spawn Weight: Void Refining",
            "Spawn weight for Void Refining NPCs. These cultivators merge void and reality, existing beyond the reach " +
            "of mortals. At default weight 15, they are extremely rare. " +
            "Void Refining cultivators are myths to most - they have surpassed the mortal world's understanding " +
            "and operate on a level that defies conventional comprehension. " +
            "They are the ancestors of ancient sects, beings of immense power who rarely involve themselves " +
            "in worldly affairs.",
            "Higher = more Void Refining cultivators (mythical ancestors)", "Lower = Void Refining cultivators are nearly nonexistent",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "void", "refining", "ancestor"));
        add("spawn.npcWeight.bodyIntegration", "NPC Spawn Weight: Body Integration",
            "Spawn weight for Body Integration NPCs. These cultivators have unified body and soul as one, " +
            "achieving a state of near-perfection. At default weight 8, they are almost never seen. " +
            "Body Integration cultivators are beings of supreme power, rarely encountered by anyone " +
            "below Nascent Soul realm. They spend most of their time in deep meditation or traversing " +
            "higher realms of existence.",
            "Higher = more Body Integration cultivators (supreme beings)", "Lower = Body Integration cultivators are virtually unseen",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "body", "integration", "supreme"));
        add("spawn.npcWeight.mahayana", "NPC Spawn Weight: Mahayana",
            "Spawn weight for Mahayana NPCs. Mahayana cultivators have reached near-immortal power, " +
            "standing at the threshold of true immortality. At default weight 4, they are extraordinarily rare. " +
            "Mahayana cultivators are beings of immense wisdom and power, having survived countless tribulations " +
            "and lived for centuries or millennia. They are the stuff of legends, spoken of in hushed tones " +
            "by even Nascent Soul cultivators.",
            "Higher = more Mahayana cultivators (near-immortal legends)", "Lower = Mahayana cultivators are once-in-a-lifetime encounters",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "mahayana", "immortal", "legend"));
        add("spawn.npcWeight.tribulationTranscendence", "NPC Spawn Weight: Tribulation Transcendence",
            "Spawn weight for Tribulation Transcendence NPCs. These cultivators are attempting to transcend " +
            "the final heavenly tribulation to achieve true immortality. At default weight 2, they are almost mythical. " +
            "Tribulation Transcendence cultivators are the most powerful beings in the mortal world, " +
            "standing at the very precipice of immortality. They spend their time preparing for the final trial " +
            "that will determine whether they ascend or perish.",
            "Higher = more Tribulation Transcendence cultivators (on the verge of immortality)", "Lower = they are purely mythical",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "tribulation", "transcendence", "ascension"));
        add("spawn.npcWeight.looseImmortal", "NPC Spawn Weight: Loose Immortal",
            "Spawn weight for Loose Immortal NPCs. Loose Immortals are cultivators who failed their final ascension " +
            "but survived, retaining immense power without achieving true immortality. At default weight 1, they are " +
            "virtually never encountered. Loose Immortals are tragic figures - immensely powerful but forever barred " +
            "from true immortality. They possess per-level bonuses that make them stronger with each level gained, " +
            "making even a single Loose Immortal a force to be reckoned with. " +
            "They are the most dangerous NPCs that can spawn in the world.",
            "Higher = more Loose Immortal cultivators (failed ascenders, very dangerous)", "Lower = Loose Immortals are essentially nonexistent",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "loose", "immortal", "failed", "ascension"));
        add("spawn.npcWeight.trueImmortal", "NPC Spawn Weight: True Immortal",
            "Spawn weight for True Immortal NPCs. True Immortals have achieved eternal life, transcending all mortal " +
            "limitations. At default weight 1, they are the rarest of all NPCs. " +
            "True Immortals are beings of supreme power who have overcome every trial the heavens could devise. " +
            "They are essentially gods walking the earth - their Qi capacity is virtually limitless, " +
            "their spells can reshape reality, and they no longer age. " +
            "Encountering a True Immortal NPC is a once-in-a-world event. " +
            "Setting this above 0 creates a world where immortals occasionally walk among mortals.",
            "Higher = True Immortals occasionally appear (world-altering power)", "Lower = True Immortals never spawn",
            "Cultivation", "Realms", "NPC Spawn Weights",
            List.of("npc", "spawn", "weight", "true", "immortal", "eternal", "god"));

        // ── Beasts ──
        add("beasts.cultivationForMonsters", "Beast Cultivation for Monsters", "If true, monsters (zombies, skeletons, etc.) also gain beast cultivation. They can advance through beast realms and become stronger.",
            "No effect (boolean)", "No effect (boolean)", "World", "Beasts", "",
            List.of("beast", "monster", "cultivation", "enable", "mob"));
        add("beasts.cultivationForAllMobs", "Beast Cultivation for All Mobs", "If true, ALL living entities (except players) get beast cultivation - including passive animals like cows, pigs, etc.",
            "No effect (boolean)", "No effect (boolean)", "World", "Beasts", "",
            List.of("beast", "all", "mob", "cultivation", "animal"));
        add("beasts.checkIntervalTicks", "Beast Check Interval", "How often (in ticks) beasts accumulate Qi. 20 ticks = 1 second. Lower = faster Qi gain.",
            "Higher = slower Qi gain (less frequent checks)", "Lower = faster Qi gain (more frequent checks)", "World", "Beasts", "",
            List.of("beast", "check", "interval", "ticks", "frequency", "qi"));
        add("beasts.qiDensityThreshold", "Beast Qi Density Threshold", "Minimum biome Qi density required for beasts to gain Qi. If a biome's density is below this, beasts there won't cultivate.",
            "Higher threshold = beasts only cultivate in rich biomes", "Lower threshold = beasts cultivate almost anywhere", "World", "Beasts", "",
            List.of("beast", "qi", "density", "threshold", "biome", "minimum"));
        add("beasts.qiGainMultiplier", "Beast Qi Gain Multiplier", "Multiplier for how much Qi beasts gain per check interval. Higher = faster beast cultivation.",
            "More Qi per check = faster beast progression", "Less Qi per check = slower beast progression", "World", "Beasts", "",
            List.of("beast", "qi", "gain", "multiplier", "speed"));
        add("beasts.advanceCost.spiritSoldier", "Spirit Soldier Advance Cost", "Qi required for a beast to advance from Mortal Beast to Spirit Soldier (the first beast realm).",
            "More Qi needed = harder to advance", "Less Qi needed = easier to advance", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit soldier", "qi"));
        add("beasts.advanceCost.spiritGeneral", "Spirit General Advance Cost", "Qi required to advance from Spirit Soldier to Spirit General.",
            "More Qi needed", "Less Qi needed", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit general", "qi"));
        add("beasts.advanceCost.spiritMarshal", "Spirit Marshal Advance Cost", "Qi required to advance from Spirit General to Spirit Marshal.",
            "More Qi needed", "Less Qi needed", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit marshal", "qi"));
        add("beasts.advanceCost.spiritKing", "Spirit King Advance Cost", "Qi required to advance from Spirit Marshal to Spirit King.",
            "More Qi needed", "Less Qi needed", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit king", "qi"));
        add("beasts.advanceCost.spiritEmperor", "Spirit Emperor Advance Cost", "Qi required to advance from Spirit King to Spirit Emperor.",
            "More Qi needed", "Less Qi needed", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit emperor", "qi"));
        add("beasts.advanceCost.spiritLord", "Spirit Lord Advance Cost", "Qi required to advance from Spirit Emperor to Spirit Lord - the highest beast realm.",
            "More Qi needed", "Less Qi needed", "World", "Beasts", "Advance Costs",
            List.of("beast", "advance", "cost", "spirit lord", "qi"));

        // ── Spawns ──
        add("spawns.cultivatorSpawnChanceNear", "Cultivator Spawn Chance (Near)", "Chance for wandering cultivator NPCs to spawn near preferred structures (like temples, sects). 0.0002 = 0.02% per attempt.",
            "Higher = more NPCs spawn near structures", "Lower = fewer NPCs", "NPCs", "Spawns", "",
            List.of("spawn", "cultivator", "npc", "chance", "near", "structure"));
        add("spawns.cultivatorSpawnChanceFar", "Cultivator Spawn Chance (Far)", "Chance for wandering cultivator NPCs to spawn away from structures. Usually lower than near chance.",
            "Higher = more NPCs spawn in the wild", "Lower = fewer wild NPCs", "NPCs", "Spawns", "",
            List.of("spawn", "cultivator", "npc", "chance", "far", "wild"));
        addW("spawns.npcRealmWeights.mortal", "NPC Weight: Mortal", "Spawn weight for Mortal NPCs. Higher weight = more common. Mortals are non-cultivators.",
            "More mortal NPCs", "Fewer mortal NPCs", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.qiRefining", "NPC Weight: Qi Refining", "Spawn weight for Qi Refining NPCs.",
            "More Qi Refining NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.foundationBuilding", "NPC Weight: Foundation Building", "Spawn weight for Foundation Building NPCs.",
            "More Foundation Building NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.goldenCore", "NPC Weight: Golden Core", "Spawn weight for Golden Core NPCs.",
            "More Golden Core NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.nascentSoul", "NPC Weight: Nascent Soul", "Spawn weight for Nascent Soul NPCs.",
            "More Nascent Soul NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.soulFormation", "NPC Weight: Soul Formation", "Spawn weight for Soul Formation NPCs.",
            "More Soul Formation NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.voidRefining", "NPC Weight: Void Refining", "Spawn weight for Void Refining NPCs.",
            "More Void Refining NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.bodyIntegration", "NPC Weight: Body Integration", "Spawn weight for Body Integration NPCs.",
            "More Body Integration NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.mahayana", "NPC Weight: Mahayana", "Spawn weight for Mahayana NPCs.",
            "More Mahayana NPCs", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.tribulationTranscendence", "NPC Weight: Tribulation Transcendence", "Spawn weight for Tribulation Transcendence NPCs. Very rare by default.",
            "More TT NPCs (very powerful)", "Fewer", "NPCs", "Spawns", "Realm Weights");
        addW("spawns.npcRealmWeights.trueImmortal", "NPC Weight: True Immortal", "Spawn weight for True Immortal NPCs. Extremely rare by default (1 in a million).",
            "More immortal NPCs (game-breaking)", "Fewer", "NPCs", "Spawns", "Realm Weights");

        // ── Qi Density ──
        addD("qiDensity.sparse", "Qi Density: Sparse", "Qi density for sparse biomes (desert, badlands). Very little Qi available for cultivation.",
            "More Qi in deserts", "Less Qi in deserts", "World", "Biome Qi", "");
        addD("qiDensity.normal", "Qi Density: Normal", "Qi density for normal biomes (plains, etc.). Default Qi availability.",
            "More Qi in plains", "Less Qi in plains", "World", "Biome Qi", "");
        addD("qiDensity.woodRich", "Qi Density: Wood Rich", "Qi density for wood-rich biomes (forest, jungle, taiga). Rich in Qi.",
            "More Qi in forests", "Less Qi in forests", "World", "Biome Qi", "");
        addD("qiDensity.waterRich", "Qi Density: Water Rich", "Qi density for water-rich biomes (ocean, river, swamp).",
            "More Qi in oceans", "Less Qi in oceans", "World", "Biome Qi", "");
        addD("qiDensity.fireRich", "Qi Density: Fire Rich", "Qi density for fire-rich biomes (Nether).",
            "More Qi in Nether", "Less Qi in Nether", "World", "Biome Qi", "");
        addD("qiDensity.earthRich", "Qi Density: Earth Rich", "Qi density for earth-rich biomes (mountains).",
            "More Qi in mountains", "Less Qi in mountains", "World", "Biome Qi", "");
        addD("qiDensity.iceRich", "Qi Density: Ice Rich", "Qi density for ice-rich biomes (snowy, frozen).",
            "More Qi in snowy biomes", "Less Qi in snowy biomes", "World", "Biome Qi", "");
        addD("qiDensity.endPure", "Qi Density: End", "Qi density for End biomes.",
            "More Qi in the End", "Less Qi in the End", "World", "Biome Qi", "");

        // ── Spells ──
        add("spells.damageGlobalMultiplier", "Spell Damage Global Multiplier", "Multiplier applied to ALL spell damage. 1.0 = no change. This is the quickest way to make spells stronger or weaker.",
            "2.0 = double all spell damage", "0.5 = half all spell damage", "Cultivation", "Spells", "",
            List.of("spell", "damage", "global", "multiplier", "scale"));
        add("spells.qiCostGlobalMultiplier", "Spell Qi Cost Global Multiplier", "Multiplier for all spell Qi costs. 1.0 = no change.",
            "Higher = spells cost more Qi", "Lower = spells cost less Qi (easier to cast)", "Cultivation", "Spells", "",
            List.of("spell", "qi", "cost", "global", "multiplier"));
        add("spells.chargeGlobalMultiplier", "Spell Charge Multiplier", "Multiplier for spell charge requirements. Some spells need to be charged before use.",
            "Higher = longer charge time", "Lower = shorter charge time (faster casting)", "Cultivation", "Spells", "",
            List.of("spell", "charge", "multiplier", "cast", "time"));
        add("spells.swordFlightUpkeepQiPerSecond", "Sword Flight Upkeep Qi/sec", "Qi consumed per second while flying on a sword. Sword Flight is a movement spell.",
            "More Qi per second = shorter flight time", "Less Qi per second = longer flight", "Cultivation", "Spells", "",
            List.of("sword", "flight", "upkeep", "qi", "cost", "fly"));
        add("spells.voidStepAirJumpQiCost", "Void Step Air Jump Qi Cost", "Qi cost for each air jump when using Void Step spell.",
            "More Qi per air jump", "Less Qi per air jump", "Cultivation", "Spells", "",
            List.of("void", "step", "air", "jump", "qi", "cost"));
        add("spells.voidStepDashQiCost", "Void Step Dash Qi Cost", "Qi cost for dashing with Void Step.",
            "More Qi per dash", "Less Qi per dash", "Cultivation", "Spells", "",
            List.of("void", "step", "dash", "qi", "cost"));
        add("spells.voidStepSlowFallQiCost", "Void Step Slow Fall Qi Cost", "Qi cost for slow fall effect with Void Step.",
            "More Qi for slow fall", "Less Qi for slow fall", "Cultivation", "Spells", "",
            List.of("void", "step", "slow", "fall", "qi"));
        add("spells.palmThunderChannelQiPerSecond", "Palm Thunder Channel Qi/sec", "Qi consumed per second while channeling Palm Thunder spell.",
            "More Qi per second = shorter channeling", "Less Qi = longer channeling possible", "Cultivation", "Spells", "",
            List.of("palm", "thunder", "channel", "qi", "lightning"));
        add("spells.palmThunderArmingTicks", "Palm Thunder Arming Ticks", "Ticks required to arm Palm Thunder before it can be released. This is the wind-up time.",
            "Longer arming = more delay before strike", "Shorter arming = faster strike", "Cultivation", "Spells", "",
            List.of("palm", "thunder", "arming", "ticks", "delay"));
        add("spells.voidEscapeChargeTicks", "Void Escape Charge Ticks", "Ticks to charge Void Escape spell before it activates. Void Escape teleports you away from danger.",
            "Longer charge = slower escape", "Shorter charge = faster escape", "Cultivation", "Spells", "",
            List.of("void", "escape", "charge", "ticks", "teleport"));
        add("spells.voidEscapeChargeQiPerTick", "Void Escape Charge Qi/tick", "Qi consumed per tick while charging Void Escape.",
            "More Qi per tick = more expensive", "Less Qi per tick = cheaper", "Cultivation", "Spells", "",
            List.of("void", "escape", "charge", "qi", "cost"));
        add("spells.voidEscapeActiveQiPerTick", "Void Escape Active Qi/tick", "Qi consumed per tick while Void Escape is active (after charging).",
            "More Qi per tick = shorter active time", "Less Qi = longer active time", "Cultivation", "Spells", "",
            List.of("void", "escape", "active", "qi", "cost"));
        add("spells.buddhaFireLotusReadyQi", "Buddha Fire Lotus Ready Qi", "Qi required to ready the Buddha Fire Lotus spell. This is a powerful fire spell.",
            "More Qi required = harder to use", "Less Qi required = easier to use", "Cultivation", "Spells", "",
            List.of("buddha", "fire", "lotus", "ready", "qi"));
        add("spells.coreSelfDestructReadyQi", "Core Self Destruct Ready Qi", "Qi required to ready the Core Self Destruct spell. This is a suicidal attack that destroys your golden core.",
            "More Qi required", "Less Qi required", "Cultivation", "Spells", "",
            List.of("core", "self", "destruct", "ready", "qi", "suicide"));
        add("spells.seaChannelTicks", "Sea Channel Ticks", "Channeling ticks for the Sea spell (a water-element spell).",
            "Longer channel = more powerful but slower", "Shorter channel = faster but may be weaker", "Cultivation", "Spells", "",
            List.of("sea", "channel", "ticks", "water", "spell"));
        add("spells.glacierBurialNpcChannelTicks", "Glacier Burial NPC Channel Ticks", "Channeling ticks for NPC-cast Glacier Burial spell. This is an ice spell that freezes enemies.",
            "Longer channel = NPCs take longer to cast", "Shorter channel = NPCs cast faster", "Cultivation", "Spells", "",
            List.of("glacier", "burial", "npc", "channel", "ice", "freeze"));
        add("spells.glacierBurialBaseQiPerTick", "Glacier Burial Qi/tick", "Base Qi consumed per tick while channeling Glacier Burial.",
            "More Qi per tick = more expensive", "Less Qi per tick = cheaper", "Cultivation", "Spells", "",
            List.of("glacier", "burial", "qi", "cost", "ice"));

        // ── Weapons ──
        add("weapons.damageGlobalMultiplier", "Weapon Damage Global Multiplier", "Multiplier for all weapon attack damage. 1.0 = no change.",
            "2.0 = double all weapon damage", "0.5 = half all weapon damage", "Crafting", "Weapons", "",
            List.of("weapon", "damage", "global", "multiplier", "scale"));
        add("weapons.bloodSpellDamageBonusMult", "Blood Spell Damage Bonus Mult", "Damage multiplier bonus for blood spells when using blood-type weapons.",
            "Higher = blood spells hit harder", "Lower = blood spells weaker", "Crafting", "Weapons", "",
            List.of("weapon", "blood", "spell", "damage", "bonus"));
        add("weapons.bloodSpellQiReductionMult", "Blood Spell Qi Reduction Mult", "Qi cost reduction multiplier for blood spells when using blood-type weapons.",
            "Higher = blood spells cost less Qi", "Lower = blood spells cost more Qi", "Crafting", "Weapons", "",
            List.of("weapon", "blood", "spell", "qi", "reduction"));
        add("weapons.bloodCapacityMultiplier", "Blood Capacity Multiplier", "Multiplier for blood capacity (HP used for blood spells) when using blood-type weapons.",
            "Higher = more blood available for blood spells", "Lower = less blood available", "Crafting", "Weapons", "",
            List.of("weapon", "blood", "capacity", "hp"));
        add("weapons.chiyanBurnTicks", "ChiYan Burn Ticks", "Duration (in ticks) of the burn effect when hit by a ChiYan (flame) weapon. 20 ticks = 1 second.",
            "Longer burn = more fire damage over time", "Shorter burn = less fire damage", "Crafting", "Weapons", "",
            List.of("chiyan", "burn", "ticks", "fire", "duration"));
        add("weapons.hanbingFrozenOverflowTicks", "HanBing Frozen Overflow Ticks", "Duration of the freeze effect overflow when hit by a HanBing (ice) weapon.",
            "Longer freeze = enemies frozen longer", "Shorter freeze = enemies frozen briefly", "Crafting", "Weapons", "",
            List.of("hanbing", "frozen", "ice", "freeze", "duration"));
        add("weapons.qingmuPoisonTicks", "QingMu Poison Ticks", "Duration of poison effect when hit by a QingMu (wood/poison) weapon.",
            "Longer poison = more damage over time", "Shorter poison = less damage", "Crafting", "Weapons", "",
            List.of("qingmu", "poison", "ticks", "wood", "duration"));
        add("weapons.soulHookAttackDamage", "Soul Hook Attack Damage", "Attack damage of the Soul Hook weapon. This is a special weapon that hooks enemies.",
            "More damage per hit", "Less damage per hit", "Crafting", "Weapons", "",
            List.of("soul", "hook", "attack", "damage"));
        add("weapons.attackSpeedModifier", "Weapon Attack Speed Modifier", "Modifier for weapon attack speed. Higher = faster attacks. 1.0 = normal speed.",
            "Higher = faster attack speed", "Lower = slower attacks", "Crafting", "Weapons", "",
            List.of("weapon", "attack", "speed", "modifier"));
        add("weapons.spellQiCostReductionLow", "Spell Qi Cost Reduction (Low Tier)", "Percentage reduction in spell Qi cost when holding a Low-tier weapon. 0 = no reduction.",
            "Higher = spells cost less Qi when holding low-tier weapons", "Lower = less reduction", "Crafting", "Weapons", "Qi Cost Reduction",
            List.of("weapon", "spell", "qi", "cost", "reduction", "low", "tier"));
        add("weapons.spellQiCostReductionMid", "Spell Qi Cost Reduction (Mid Tier)", "Percentage reduction in spell Qi cost when holding a Mid-tier weapon.",
            "Higher = spells cost less Qi", "Lower = less reduction", "Crafting", "Weapons", "Qi Cost Reduction",
            List.of("weapon", "spell", "qi", "cost", "reduction", "mid"));
        add("weapons.spellQiCostReductionHigh", "Spell Qi Cost Reduction (High Tier)", "Percentage reduction in spell Qi cost when holding a High-tier weapon.",
            "Higher = spells cost less Qi", "Lower = less reduction", "Crafting", "Weapons", "Qi Cost Reduction",
            List.of("weapon", "spell", "qi", "cost", "reduction", "high"));
        add("weapons.spellQiCostReductionSupreme", "Spell Qi Cost Reduction (Supreme Tier)", "Percentage reduction in spell Qi cost when holding a Supreme-tier weapon.",
            "Higher = spells cost less Qi", "Lower = less reduction", "Crafting", "Weapons", "Qi Cost Reduction",
            List.of("weapon", "spell", "qi", "cost", "reduction", "supreme"));
        add("weapons.spellQiCostReductionImmortal", "Spell Qi Cost Reduction (Immortal Tier)", "Percentage reduction in spell Qi cost when holding an Immortal-tier weapon.",
            "Higher = spells cost less Qi (up to 100% = free spells)", "Lower = less reduction", "Crafting", "Weapons", "Qi Cost Reduction",
            List.of("weapon", "spell", "qi", "cost", "reduction", "immortal"));

        // ── Pills ──
        add("pills.useTicks", "Pill Use Ticks", "Time (in ticks) it takes to consume a pill. 20 ticks = 1 second.",
            "Longer = slower pill consumption", "Shorter = faster pill consumption", "Crafting", "Pills", "",
            List.of("pill", "use", "ticks", "consume", "time"));
        add("pills.spiritStoneUseTicks", "Spirit Stone Use Ticks", "Time to consume a spirit stone for Qi.",
            "Longer = slower", "Shorter = faster", "Crafting", "Pills", "",
            List.of("spirit", "stone", "use", "ticks", "consume"));
        addP("pills.qiLow", "Pill Qi (Low)", "Qi recovered when consuming a Low-tier Qi recovery pill.",
            "More Qi recovered", "Less Qi recovered", "Crafting", "Pills", "Qi Recovery");
        addP("pills.qiMid", "Pill Qi (Mid)", "Qi recovered from Mid-tier Qi pill.",
            "More Qi recovered", "Less Qi recovered", "Crafting", "Pills", "Qi Recovery");
        addP("pills.qiHigh", "Pill Qi (High)", "Qi recovered from High-tier Qi pill.",
            "More Qi recovered", "Less Qi recovered", "Crafting", "Pills", "Qi Recovery");
        addP("pills.qiSupreme", "Pill Qi (Supreme)", "Qi recovered from Supreme-tier Qi pill.",
            "More Qi recovered", "Less Qi recovered", "Crafting", "Pills", "Qi Recovery");
        addP("pills.qiImmortal", "Pill Qi (Immortal)", "Qi recovered from Immortal-tier Qi pill.",
            "More Qi recovered", "Less Qi recovered", "Crafting", "Pills", "Qi Recovery");
        addP("pills.spiritStoneQiLow", "Spirit Stone Qi (Low)", "Qi gained from consuming a Low-tier spirit stone.",
            "More Qi per stone", "Less Qi per stone", "Crafting", "Pills", "Spirit Stone Qi");
        addP("pills.spiritStoneQiMid", "Spirit Stone Qi (Mid)", "Qi from Mid-tier spirit stone.",
            "More Qi per stone", "Less Qi per stone", "Crafting", "Pills", "Spirit Stone Qi");
        addP("pills.spiritStoneQiHigh", "Spirit Stone Qi (High)", "Qi from High-tier spirit stone.",
            "More Qi per stone", "Less Qi per stone", "Crafting", "Pills", "Spirit Stone Qi");
        addP("pills.spiritStoneQiSupreme", "Spirit Stone Qi (Supreme)", "Qi from Supreme-tier spirit stone.",
            "More Qi per stone", "Less Qi per stone", "Crafting", "Pills", "Spirit Stone Qi");
        add("pills.bloodBurnDamageLow", "Blood Burn Damage (Low)", "Self-damage taken when using a Low-tier Blood Burn pill. Blood Burn pills trade health for temporary power.",
            "More self-damage = riskier to use", "Less self-damage = safer", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "damage", "low", "self", "hp"));
        add("pills.bloodBurnDamageMid", "Blood Burn Damage (Mid)", "Self-damage from Mid-tier Blood Burn pill.",
            "More self-damage", "Less self-damage", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "damage", "mid"));
        add("pills.bloodBurnDamageHigh", "Blood Burn Damage (High)", "Self-damage from High-tier Blood Burn pill.",
            "More self-damage", "Less self-damage", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "damage", "high"));
        add("pills.bloodBurnDamageSupreme", "Blood Burn Damage (Supreme)", "Self-damage from Supreme-tier Blood Burn pill.",
            "More self-damage", "Less self-damage", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "damage", "supreme"));
        add("pills.bloodBurnDamageImmortal", "Blood Burn Damage (Immortal)", "Self-damage from Immortal-tier Blood Burn pill.",
            "More self-damage", "Less self-damage", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "damage", "immortal"));
        add("pills.bloodBurnDurationTicks", "Blood Burn Duration", "Duration (in ticks) of the Blood Burn buff.",
            "Longer buff = more time with power boost", "Shorter buff", "Crafting", "Pills", "Blood Burn",
            List.of("blood", "burn", "duration", "ticks", "buff"));
        add("pills.clearMindDurationHigh", "Clear Mind Duration (High)", "Duration of Clear Mind effect from High-tier pill. Clear Mind improves cultivation focus.",
            "Longer focus bonus", "Shorter focus bonus", "Crafting", "Pills", "Clear Mind",
            List.of("clear", "mind", "duration", "focus", "high"));
        add("pills.clearMindDurationSupreme", "Clear Mind Duration (Supreme)", "Duration of Clear Mind from Supreme-tier pill.",
            "Longer focus bonus", "Shorter focus bonus", "Crafting", "Pills", "Clear Mind",
            List.of("clear", "mind", "duration", "supreme"));
        add("pills.divineStrideDurationLow", "Divine Stride Duration (Low)", "Duration of speed boost from Low-tier Divine Stride pill.",
            "Longer speed boost", "Shorter speed boost", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "duration", "speed", "low"));
        add("pills.divineStrideDurationMid", "Divine Stride Duration (Mid)", "Duration from Mid-tier Divine Stride pill.",
            "Longer speed boost", "Shorter speed boost", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "duration", "mid"));
        add("pills.divineStrideDurationHigh", "Divine Stride Duration (High)", "Duration from High-tier Divine Stride pill.",
            "Longer speed boost", "Shorter speed boost", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "duration", "high"));
        add("pills.divineStrideDurationSupreme", "Divine Stride Duration (Supreme)", "Duration from Supreme-tier Divine Stride pill.",
            "Longer speed boost", "Shorter speed boost", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "duration", "supreme"));
        add("pills.divineStrideSpeedLow", "Divine Stride Speed (Low)", "Speed bonus from Low-tier Divine Stride pill.",
            "Faster movement", "Slower movement", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "speed", "low"));
        add("pills.divineStrideSpeedMid", "Divine Stride Speed (Mid)", "Speed bonus from Mid-tier Divine Stride pill.",
            "Faster movement", "Slower movement", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "speed", "mid"));
        add("pills.divineStrideSpeedHigh", "Divine Stride Speed (High)", "Speed bonus from High-tier Divine Stride pill.",
            "Faster movement", "Slower movement", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "speed", "high"));
        add("pills.divineStrideSpeedSupreme", "Divine Stride Speed (Supreme)", "Speed bonus from Supreme-tier Divine Stride pill.",
            "Faster movement", "Slower movement", "Crafting", "Pills", "Divine Stride",
            List.of("divine", "stride", "speed", "supreme"));
        add("pills.shadowStepDurationTicks", "Shadow Step Duration", "Duration of Shadow Step invisibility effect from pill.",
            "Longer invisibility", "Shorter invisibility", "Crafting", "Pills", "",
            List.of("shadow", "step", "duration", "invisibility", "stealth"));
        add("pills.youthPillMinBoneAge", "Youth Pill Min Bone Age", "Minimum bone age that can be achieved with a Youth Pill. Youth Pills reduce your apparent age.",
            "Lower = can look younger", "Higher = minimum age is higher", "Crafting", "Pills", "",
            List.of("youth", "pill", "bone", "age", "min"));
        add("pills.youthPillBoneAgeReduction", "Youth Pill Bone Age Reduction", "How much bone age is reduced by a Youth Pill.",
            "More reduction = looks much younger", "Less reduction = smaller age change", "Crafting", "Pills", "",
            List.of("youth", "pill", "bone", "age", "reduction"));
        add("pills.foundationMaterialUseTicks", "Foundation Material Use Ticks", "Time to consume Foundation Building materials (Zhuji Dan, Dao Fruit).",
            "Longer = slower consumption", "Shorter = faster", "Crafting", "Pills", "Foundation Materials",
            List.of("foundation", "material", "use", "ticks", "consume"));
        add("pills.foundationMaterialZhujiDanQi", "Zhuji Dan Qi", "Qi gained from consuming a Zhuji Dan (Foundation Pill). Used to break through to Foundation Building.",
            "More Qi = easier breakthrough", "Less Qi = harder breakthrough", "Crafting", "Pills", "Foundation Materials",
            List.of("zhuji", "dan", "foundation", "pill", "qi"));
        add("pills.foundationMaterialDaoFruitQi", "Dao Fruit Qi", "Qi gained from consuming a Dao Fruit. Alternative foundation material.",
            "More Qi", "Less Qi", "Crafting", "Pills", "Foundation Materials",
            List.of("dao", "fruit", "foundation", "qi"));
        add("pills.goldenCoreMaterialUseTicks", "Golden Core Material Use Ticks", "Time to consume Golden Core materials (Jiedan Pill, Creation Fruit).",
            "Longer = slower", "Shorter = faster", "Crafting", "Pills", "Golden Core Materials",
            List.of("golden", "core", "material", "use", "ticks"));
        add("pills.goldenCoreMaterialJiedanPillQi", "Jiedan Pill Qi", "Qi gained from Jiedan Pill (Core Formation Pill). Used to form a Golden Core.",
            "More Qi = easier core formation", "Less Qi = harder", "Crafting", "Pills", "Golden Core Materials",
            List.of("jiedan", "pill", "golden", "core", "qi"));
        add("pills.goldenCoreMaterialCreationFruitQi", "Creation Fruit Qi", "Qi gained from Creation Fruit. Alternative golden core material.",
            "More Qi", "Less Qi", "Crafting", "Pills", "Golden Core Materials",
            List.of("creation", "fruit", "golden", "core", "qi"));
        add("pills.rejuvenationHealLow", "Rejuvenation Heal (Low)", "Instant heal amount from Low-tier Rejuvenation Pill.",
            "More healing", "Less healing", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "heal", "low", "hp"));
        add("pills.rejuvenationHealMid", "Rejuvenation Heal (Mid)", "Instant heal from Mid-tier Rejuvenation Pill.",
            "More healing", "Less healing", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "heal", "mid"));
        add("pills.rejuvenationRegenTicksSupreme", "Rejuvenation Regen Ticks (Supreme)", "Duration of regeneration effect from Supreme Rejuvenation Pill.",
            "Longer regen = more healing over time", "Shorter regen", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "regen", "ticks", "supreme"));
        add("pills.rejuvenationRegenAmpSupreme", "Rejuvenation Regen Amp (Supreme)", "Amplifier (power) of regeneration effect from Supreme Rejuvenation Pill.",
            "Stronger regen = heals faster", "Weaker regen", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "regen", "amp", "supreme"));
        add("pills.rejuvenationAbsorptionTicksSupreme", "Rejuvenation Absorption Ticks (Supreme)", "Duration of absorption (damage absorption) effect from Supreme Rejuvenation Pill.",
            "Longer absorption shield", "Shorter absorption", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "absorption", "ticks", "supreme"));
        add("pills.rejuvenationAbsorptionAmpSupreme", "Rejuvenation Absorption Amp (Supreme)", "Amplifier of absorption effect from Supreme Rejuvenation Pill.",
            "Stronger absorption = absorbs more damage", "Weaker absorption", "Crafting", "Pills", "Rejuvenation",
            List.of("rejuvenation", "absorption", "amp", "supreme"));
        // Storage bag
        add("pills.storageBagColumnsLow", "Storage Bag Columns (Low)", "Number of inventory columns in a Low-tier storage bag.",
            "More columns = wider inventory", "Fewer columns = narrower", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "columns", "low", "inventory", "width"));
        add("pills.storageBagColumnsMid", "Storage Bag Columns (Mid)", "Columns in Mid-tier storage bag.",
            "More columns", "Fewer columns", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "columns", "mid"));
        add("pills.storageBagColumnsHigh", "Storage Bag Columns (High)", "Columns in High-tier storage bag.",
            "More columns", "Fewer columns", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "columns", "high"));
        add("pills.storageBagColumnsSupreme", "Storage Bag Columns (Supreme)", "Columns in Supreme-tier storage bag.",
            "More columns", "Fewer columns", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "columns", "supreme"));
        add("pills.storageBagColumnsImmortal", "Storage Bag Columns (Immortal)", "Columns in Immortal-tier storage bag.",
            "More columns", "Fewer columns", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "columns", "immortal"));
        add("pills.storageBagRowsLow", "Storage Bag Rows (Low)", "Number of inventory rows in a Low-tier storage bag.",
            "More rows = taller inventory", "Fewer rows", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "rows", "low", "inventory", "height"));
        add("pills.storageBagRowsMid", "Storage Bag Rows (Mid)", "Rows in Mid-tier storage bag.",
            "More rows", "Fewer rows", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "rows", "mid"));
        add("pills.storageBagRowsHigh", "Storage Bag Rows (High)", "Rows in High-tier storage bag.",
            "More rows", "Fewer rows", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "rows", "high"));
        add("pills.storageBagRowsSupreme", "Storage Bag Rows (Supreme)", "Rows in Supreme-tier storage bag.",
            "More rows", "Fewer rows", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "rows", "supreme"));
        add("pills.storageBagRowsImmortal", "Storage Bag Rows (Immortal)", "Rows in Immortal-tier storage bag.",
            "More rows", "Fewer rows", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "rows", "immortal"));
        add("pills.storageBagVisibleRowsMax", "Storage Bag Max Visible Rows", "Maximum number of rows visible at once in any storage bag. Extra rows require scrolling.",
            "More visible rows = less scrolling", "Fewer visible rows = more scrolling", "Crafting", "Pills", "Storage Bag",
            List.of("storage", "bag", "visible", "rows", "max", "scroll"));

        // ── Alchemy ──
        add("alchemy.furnaceMaxQi", "Alchemy Furnace Max Qi", "Maximum Qi storage of an alchemy furnace block. The furnace needs Qi to craft pills.",
            "More Qi storage = can craft more pills before refueling", "Less Qi = must refuel more often", "Crafting", "Alchemy", "",
            List.of("alchemy", "furnace", "max", "qi", "storage"));
        add("alchemy.ticksPerPill", "Ticks Per Pill", "Crafting time (in ticks) per pill in the alchemy furnace. 20 ticks = 1 second.",
            "Longer = slower crafting", "Shorter = faster crafting", "Crafting", "Alchemy", "",
            List.of("alchemy", "ticks", "pill", "craft", "time", "speed"));
        add("alchemy.maxPillsPerBatch", "Max Pills Per Batch", "Maximum number of pills that can be crafted in one batch.",
            "More pills per batch = more efficient", "Fewer pills = less efficient", "Crafting", "Alchemy", "",
            List.of("alchemy", "max", "pills", "batch", "craft"));
        add("alchemy.inputSlots", "Alchemy Input Slots", "Number of input ingredient slots in the alchemy furnace UI.",
            "More slots = can use more ingredients", "Fewer slots = limited recipes", "Crafting", "Alchemy", "",
            List.of("alchemy", "input", "slots", "ingredients"));
        add("alchemy.outputSlots", "Alchemy Output Slots", "Number of output slots in the alchemy furnace UI.",
            "More slots = can store more output", "Fewer slots = must empty more often", "Crafting", "Alchemy", "",
            List.of("alchemy", "output", "slots"));
        addA("alchemy.xpGainLow", "Alchemy XP Gain (Low)", "XP gained from successfully crafting a Low-tier pill. XP levels up your alchemy rank.",
            "More XP = faster rank progression", "Less XP = slower progression", "Crafting", "Alchemy", "XP Gains");
        addA("alchemy.xpGainMid", "Alchemy XP Gain (Mid)", "XP from Mid-tier pills.",
            "More XP", "Less XP", "Crafting", "Alchemy", "XP Gains");
        addA("alchemy.xpGainHigh", "Alchemy XP Gain (High)", "XP from High-tier pills.",
            "More XP", "Less XP", "Crafting", "Alchemy", "XP Gains");
        addA("alchemy.xpGainSupreme", "Alchemy XP Gain (Supreme)", "XP from Supreme-tier pills.",
            "More XP", "Less XP", "Crafting", "Alchemy", "XP Gains");
        addA("alchemy.xpGainImmortal", "Alchemy XP Gain (Immortal)", "XP from Immortal-tier pills.",
            "More XP", "Less XP", "Crafting", "Alchemy", "XP Gains");
        add("alchemy.xpGainFailure", "Alchemy XP on Failure", "XP gained when a crafting attempt fails. Provides some consolation for wasted ingredients.",
            "More XP on failure = less punishing", "Less XP = failures are more punishing", "Crafting", "Alchemy", "",
            List.of("alchemy", "xp", "gain", "failure", "fail"));
        add("alchemy.heartSuccessBonus", "Alchemy Heart Success Bonus", "Success rate bonus from having an Alchemy Heart (a special item that improves alchemy).",
            "Higher bonus = more successful crafts", "Lower bonus = more failures", "Crafting", "Alchemy", "",
            List.of("alchemy", "heart", "success", "bonus", "rate"));
        add("alchemy.heartQiCostMult", "Alchemy Heart Qi Cost Mult", "Qi cost multiplier when using an Alchemy Heart. Hearts make crafting more Qi-efficient.",
            "Higher = costs more Qi (but better success)", "Lower = costs less Qi", "Crafting", "Alchemy", "",
            List.of("alchemy", "heart", "qi", "cost", "mult"));

        // ── Refining ──
        add("refining.furnaceMaxQi", "Refining Furnace Max Qi", "Maximum Qi storage of a refining furnace. Used for crafting weapons and armor.",
            "More Qi storage", "Less Qi storage", "Crafting", "Refining", "",
            List.of("refining", "furnace", "max", "qi", "storage"));
        add("refining.ticksPerItem", "Ticks Per Item", "Crafting time per item in the refining furnace.",
            "Longer = slower crafting", "Shorter = faster crafting", "Crafting", "Refining", "",
            List.of("refining", "ticks", "item", "craft", "time"));
        add("refining.maxItemsPerBatch", "Max Items Per Batch", "Maximum items craftable in one refining batch.",
            "More items per batch", "Fewer items", "Crafting", "Refining", "",
            List.of("refining", "max", "items", "batch"));
        add("refining.inputSlots", "Refining Input Slots", "Number of input slots in the refining furnace.",
            "More input slots", "Fewer input slots", "Crafting", "Refining", "",
            List.of("refining", "input", "slots"));
        add("refining.outputSlots", "Refining Output Slots", "Number of output slots in the refining furnace.",
            "More output slots", "Fewer output slots", "Crafting", "Refining", "",
            List.of("refining", "output", "slots"));
        addR("refining.xpGainLow", "Refining XP Gain (Low)", "XP from crafting Low-tier refined items.",
            "More XP = faster rank up", "Less XP", "Crafting", "Refining", "XP Gains");
        addR("refining.xpGainMid", "Refining XP Gain (Mid)", "XP from Mid-tier items.",
            "More XP", "Less XP", "Crafting", "Refining", "XP Gains");
        addR("refining.xpGainHigh", "Refining XP Gain (High)", "XP from High-tier items.",
            "More XP", "Less XP", "Crafting", "Refining", "XP Gains");
        addR("refining.xpGainSupreme", "Refining XP Gain (Supreme)", "XP from Supreme-tier items.",
            "More XP", "Less XP", "Crafting", "Refining", "XP Gains");
        addR("refining.xpGainImmortal", "Refining XP Gain (Immortal)", "XP from Immortal-tier items.",
            "More XP", "Less XP", "Crafting", "Refining", "XP Gains");
        add("refining.xpGainFailure", "Refining XP on Failure", "XP gained on failed refining attempts.",
            "More XP on failure", "Less XP on failure", "Crafting", "Refining", "",
            List.of("refining", "xp", "failure", "fail"));
        add("refining.tierUpChanceDivineForge", "Tier-Up Chance: Divine Forge", "Bonus probability of tiering up when using the Divine Forge technique. Higher = more likely to craft higher-tier items.",
            "Higher chance = easier to get better items", "Lower chance = harder", "Crafting", "Refining", "",
            List.of("refining", "tier", "up", "chance", "divine", "forge", "technique"));
        add("refining.tierUpChanceHeavenlyElixir", "Tier-Up Chance: Heavenly Elixir", "Bonus probability of tiering up with the Heavenly Elixir technique.",
            "Higher chance", "Lower chance", "Crafting", "Refining", "",
            List.of("refining", "tier", "up", "chance", "heavenly", "elixir", "technique"));

        // ── Spirit Plants ──
        add("spiritPlants.maxAge", "Spirit Plant Max Age", "Maximum growth age for spirit plants. Plants grow from 0 to this value.",
            "Higher = plants take longer to fully grow", "Lower = plants mature faster", "World", "Spirit Plants", "",
            List.of("spirit", "plant", "max", "age", "growth"));
        add("spiritPlants.growthTickBase", "Spirit Plant Growth Tick Base", "Base growth tick value. Determines how fast spirit plants grow per tick.",
            "Higher = faster growth per tick", "Lower = slower growth", "World", "Spirit Plants", "",
            List.of("spirit", "plant", "growth", "tick", "base", "speed"));
        add("spiritPlants.qiOrbAmount", "Spirit Gathering Flower Qi Orb Amount", "Amount of Qi in each orb spawned by the Spirit Gathering Flower plant.",
            "More Qi per orb = more Qi gathered", "Less Qi per orb", "World", "Spirit Plants", "",
            List.of("spirit", "plant", "qi", "orb", "amount", "gathering", "flower"));
        add("spiritPlants.skipRadius", "Spirit Gathering Flower Skip Radius", "Radius within which the Spirit Gathering Flower skips Qi orb spawning (to avoid overlap).",
            "Larger radius = orbs spread out more", "Smaller radius = orbs closer together", "World", "Spirit Plants", "",
            List.of("spirit", "plant", "skip", "radius", "gathering", "flower"));
        add("spiritPlants.skipCount", "Spirit Gathering Flower Skip Count", "Max number of nearby orbs to skip when spawning new ones.",
            "Higher = more orbs can be skipped", "Lower = fewer skipped", "World", "Spirit Plants", "",
            List.of("spirit", "plant", "skip", "count", "gathering", "flower"));
        add("spiritPlants.flameMeltRadius", "Flame Pepper Melt Radius", "Radius in which the Flame Pepper plant melts snow and ice.",
            "Larger radius = melts more area", "Smaller radius = melts less", "World", "Spirit Plants", "",
            List.of("flame", "pepper", "melt", "radius", "snow", "ice", "fire"));
        add("spiritPlants.earthMarrowGrowthChance", "Earth Marrow Ginseng Growth Chance", "Chance for Earth Marrow Ginseng to spread/grow each tick.",
            "Higher chance = spreads faster", "Lower chance = spreads slower", "World", "Spirit Plants", "",
            List.of("earth", "marrow", "ginseng", "growth", "chance", "spread"));
        add("spiritPlants.earthMarrowGrowthRadius", "Earth Marrow Ginseng Growth Radius", "Radius in which Earth Marrow Ginseng can spread.",
            "Larger radius = spreads further", "Smaller radius = stays compact", "World", "Spirit Plants", "",
            List.of("earth", "marrow", "ginseng", "growth", "radius", "spread"));
        add("spiritPlants.goldenChrysanthemumDropChance", "Golden Chrysanthemum Drop Chance", "Chance for Golden Chrysanthemum to drop gold nuggets/items.",
            "Higher chance = more drops", "Lower chance = fewer drops", "World", "Spirit Plants", "",
            List.of("golden", "chrysanthemum", "drop", "chance", "gold"));
        add("spiritPlants.snowRadius", "Snow Soul Lotus Snow Radius", "Radius in which the Snow Soul Lotus places snow.",
            "Larger radius = more snow placed", "Smaller radius = less snow", "World", "Spirit Plants", "",
            List.of("snow", "soul", "lotus", "radius", "snow", "ice"));
        add("spiritPlants.snowMaxLayers", "Snow Soul Lotus Max Snow Layers", "Maximum snow layers placed by Snow Soul Lotus.",
            "More layers = thicker snow", "Fewer layers = thinner snow", "World", "Spirit Plants", "",
            List.of("snow", "soul", "lotus", "max", "layers"));
        add("spiritPlants.snowPlaceAttempts", "Snow Soul Lotus Place Attempts", "Number of snow placement attempts per tick by Snow Soul Lotus.",
            "More attempts = faster snow coverage", "Fewer attempts = slower", "World", "Spirit Plants", "",
            List.of("snow", "soul", "lotus", "place", "attempts"));

        // ── Spirit Veins ──
        addSV("spiritVeins.maxQi.low", "Spirit Vein Max Qi (Low)", "Maximum Qi storage of a Low-tier Spirit Vein core. Spirit Veins are world Qi sources.",
            "More Qi storage = longer lasting", "Less Qi = depletes faster", "World", "Spirit Veins", "Max Qi");
        addSV("spiritVeins.maxQi.mid", "Spirit Vein Max Qi (Mid)", "Max Qi of Mid-tier Spirit Vein.",
            "More Qi storage", "Less Qi storage", "World", "Spirit Veins", "Max Qi");
        addSV("spiritVeins.maxQi.high", "Spirit Vein Max Qi (High)", "Max Qi of High-tier Spirit Vein.",
            "More Qi storage", "Less Qi storage", "World", "Spirit Veins", "Max Qi");
        addSV("spiritVeins.maxQi.supreme", "Spirit Vein Max Qi (Supreme)", "Max Qi of Supreme-tier Spirit Vein.",
            "More Qi storage", "Less Qi storage", "World", "Spirit Veins", "Max Qi");
        addSV("spiritVeins.maxQi.immortal", "Spirit Vein Max Qi (Immortal)", "Max Qi of Immortal-tier Spirit Vein.",
            "Enormous Qi storage", "Less Qi storage", "World", "Spirit Veins", "Max Qi");
        add("spiritVeins.orbGain.low", "Spirit Vein Orb Gain (Low)", "Qi gained per orb from a Low-tier Spirit Vein.",
            "More Qi per orb", "Less Qi per orb", "World", "Spirit Veins", "Orb Gain",
            List.of("spirit", "vein", "orb", "gain", "low", "qi"));
        add("spiritVeins.orbGain.mid", "Spirit Vein Orb Gain (Mid)", "Qi per orb from Mid-tier vein.",
            "More Qi per orb", "Less Qi per orb", "World", "Spirit Veins", "Orb Gain",
            List.of("spirit", "vein", "orb", "gain", "mid"));
        add("spiritVeins.orbGain.high", "Spirit Vein Orb Gain (High)", "Qi per orb from High-tier vein.",
            "More Qi per orb", "Less Qi per orb", "World", "Spirit Veins", "Orb Gain",
            List.of("spirit", "vein", "orb", "gain", "high"));
        add("spiritVeins.orbGain.supreme", "Spirit Vein Orb Gain (Supreme)", "Qi per orb from Supreme-tier vein.",
            "More Qi per orb", "Less Qi per orb", "World", "Spirit Veins", "Orb Gain",
            List.of("spirit", "vein", "orb", "gain", "supreme"));
        add("spiritVeins.orbGain.immortal", "Spirit Vein Orb Gain (Immortal)", "Qi per orb from Immortal-tier vein.",
            "More Qi per orb", "Less Qi per orb", "World", "Spirit Veins", "Orb Gain",
            List.of("spirit", "vein", "orb", "gain", "immortal"));
        add("spiritVeins.supplyPerSecond.low", "Spirit Vein Supply/sec (Low)", "Passive Qi supply per second from Low-tier vein. This is Qi generated even without orbs.",
            "More Qi per second", "Less Qi per second", "World", "Spirit Veins", "Supply Per Second",
            List.of("spirit", "vein", "supply", "low", "qi", "per", "second"));
        add("spiritVeins.supplyPerSecond.mid", "Spirit Vein Supply/sec (Mid)", "Passive Qi/sec from Mid-tier vein.",
            "More Qi/sec", "Less Qi/sec", "World", "Spirit Veins", "Supply Per Second",
            List.of("spirit", "vein", "supply", "mid"));
        add("spiritVeins.supplyPerSecond.high", "Spirit Vein Supply/sec (High)", "Passive Qi/sec from High-tier vein.",
            "More Qi/sec", "Less Qi/sec", "World", "Spirit Veins", "Supply Per Second",
            List.of("spirit", "vein", "supply", "high"));
        add("spiritVeins.supplyPerSecond.supreme", "Spirit Vein Supply/sec (Supreme)", "Passive Qi/sec from Supreme-tier vein.",
            "More Qi/sec", "Less Qi/sec", "World", "Spirit Veins", "Supply Per Second",
            List.of("spirit", "vein", "supply", "supreme"));
        add("spiritVeins.supplyPerSecond.immortal", "Spirit Vein Supply/sec (Immortal)", "Passive Qi/sec from Immortal-tier vein.",
            "More Qi/sec", "Less Qi/sec", "World", "Spirit Veins", "Supply Per Second",
            List.of("spirit", "vein", "supply", "immortal"));
        add("spiritVeins.attractRadius", "Spirit Vein Attract Radius", "Radius within which the Spirit Vein attracts Qi to nearby cultivators.",
            "Larger radius = attracts from further away", "Smaller radius = must be closer", "World", "Spirit Veins", "",
            List.of("spirit", "vein", "attract", "radius", "range"));
        add("spiritVeins.supplyRadius", "Spirit Vein Supply Radius", "Radius within which the Spirit Vein provides passive Qi supply.",
            "Larger radius = supplies Qi to wider area", "Smaller radius = smaller area", "World", "Spirit Veins", "",
            List.of("spirit", "vein", "supply", "radius", "range"));

        // ── Techniques ──
        add("techniques.qiAbsorbMultGlobal", "Technique Qi Absorb Mult", "Global multiplier for Qi absorption bonus from techniques. Techniques are learned skills that provide passive bonuses.",
            "Higher = techniques boost Qi absorption more", "Lower = weaker Qi absorption bonus", "Cultivation", "Techniques", "",
            List.of("technique", "qi", "absorb", "mult", "global", "bonus"));
        add("techniques.attackBonusGlobal", "Technique Attack Bonus", "Global multiplier for attack bonus from techniques.",
            "Higher = techniques boost attack more", "Lower = weaker attack bonus", "Cultivation", "Techniques", "",
            List.of("technique", "attack", "bonus", "global", "mult"));
        add("techniques.defenseBonusGlobal", "Technique Defense Bonus", "Global multiplier for defense bonus from techniques.",
            "Higher = techniques boost defense more", "Lower = weaker defense", "Cultivation", "Techniques", "",
            List.of("technique", "defense", "bonus", "global", "mult"));
        add("techniques.maxHpBonusGlobal", "Technique Max HP Bonus", "Global multiplier for max HP bonus from techniques.",
            "Higher = techniques boost HP more", "Lower = weaker HP bonus", "Cultivation", "Techniques", "",
            List.of("technique", "max", "hp", "bonus", "global", "mult"));
        add("techniques.critRateBonusGlobal", "Technique Crit Rate Bonus", "Global multiplier for critical hit rate bonus from techniques.",
            "Higher = techniques boost crit rate more", "Lower = weaker crit bonus", "Cultivation", "Techniques", "",
            List.of("technique", "crit", "rate", "bonus", "global"));
        add("techniques.elementSpellMultGlobal", "Technique Element Spell Mult", "Global multiplier for elemental spell damage bonus from techniques.",
            "Higher = techniques boost element spells more", "Lower = weaker element bonus", "Cultivation", "Techniques", "",
            List.of("technique", "element", "spell", "mult", "global"));
        add("techniques.moveSpeedBonusGlobal", "Technique Move Speed Bonus", "Global multiplier for movement speed bonus from techniques.",
            "Higher = techniques boost speed more", "Lower = weaker speed bonus", "Cultivation", "Techniques", "",
            List.of("technique", "move", "speed", "bonus", "global"));

        // ── Spirit Roots ──
        add("spiritRoots.heavenlyPrimaryElementMult", "Heavenly Primary Element Mult", "Elemental spell damage multiplier for the primary element of a Heavenly Spirit Root. Heavenly roots are the strongest type.",
            "Higher = primary element spells much stronger", "Lower = weaker primary spells", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "primary", "element", "mult", "spell"));
        add("spiritRoots.heavenlyCounterElementMult", "Heavenly Counter Element Mult", "Spell damage multiplier for the counter element of a Heavenly Spirit Root. Counter elements are normally weak but Heavenly roots reduce the penalty.",
            "Higher = less penalty for counter elements", "Lower = more penalty", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "counter", "element", "mult"));
        add("spiritRoots.heavenlyExtraZhenyuanPerSubLevel", "Heavenly Extra Zhenyuan/Sub-Level", "Extra Zhenyuan points per sub-level for cultivators with a Heavenly Spirit Root. Heavenly roots get bonus attribute points.",
            "More bonus points = faster attribute growth", "Fewer bonus points", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "extra", "zhenyuan", "sub", "level", "attribute"));
        add("spiritRoots.dualPrimaryElementMult", "Dual Primary Element Mult", "Elemental spell damage multiplier for the primary element of a Dual Spirit Root. Dual roots have two elements.",
            "Higher = primary element stronger", "Lower = weaker", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "dual", "primary", "element", "mult"));
        add("spiritRoots.dualSecondaryElementMult", "Dual Secondary Element Mult", "Spell damage multiplier for the secondary element of a Dual Spirit Root.",
            "Higher = secondary element stronger", "Lower = weaker", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "dual", "secondary", "element", "mult"));
        add("spiritRoots.dualOffElementMult", "Dual Off Element Mult", "Spell damage multiplier for non-aligned elements of a Dual Spirit Root.",
            "Higher = off elements less penalized", "Lower = off elements more penalized", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "dual", "off", "element", "mult"));
        add("spiritRoots.mutantPrimaryElementMult", "Mutant Primary Element Mult", "Elemental spell damage multiplier for Mutant Spirit Root. Mutant roots have unusual element combinations.",
            "Higher = mutant spells stronger", "Lower = weaker", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "mutant", "primary", "element", "mult"));
        add("spiritRoots.heavenlySwordSwordDmgMult", "Heavenly Sword Root Sword Dmg Mult", "Sword spell damage multiplier for Heavenly Sword Spirit Root. This root specializes in sword-type spells.",
            "Higher = sword spells much stronger", "Lower = weaker sword spells", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "sword", "damage", "mult"));
        add("spiritRoots.heavenlySwordNonElementSpellMult", "Heavenly Sword Non-Element Spell Mult", "Non-elemental spell damage multiplier for Heavenly Sword Root.",
            "Higher = non-elemental spells stronger", "Lower = weaker", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "sword", "non", "element", "spell"));
        add("spiritRoots.heavenlyHiddenNonElementSpellMult", "Heavenly Hidden Non-Element Spell Mult", "Non-elemental spell damage multiplier for Heavenly Hidden Spirit Root. Hidden roots specialize in stealth/utility spells.",
            "Higher = non-elemental spells stronger", "Lower = weaker", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "heavenly", "hidden", "non", "element", "spell"));
        add("spiritRoots.qiAbsorbSSRMult", "Qi Absorb SSR Mult", "Qi absorption multiplier for SSR-rarity Spirit Roots. SSR is the highest rarity.",
            "Higher = SSR roots absorb Qi much faster", "Lower = slower absorption", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "qi", "absorb", "ssr", "rarity", "mult"));
        add("spiritRoots.qiAbsorbSRMult", "Qi Absorb SR Mult", "Qi absorption multiplier for SR-rarity Spirit Roots.",
            "Higher = SR roots absorb Qi faster", "Lower = slower", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "qi", "absorb", "sr", "rarity", "mult"));
        add("spiritRoots.environmentBuffMult", "Environment Buff Mult", "Multiplier for environment-based Qi buffs. Certain spirit roots get bonuses in matching biomes.",
            "Higher = bigger environment bonuses", "Lower = smaller bonuses", "Cultivation", "Spirit Roots", "",
            List.of("spirit", "root", "environment", "buff", "mult", "biome"));

        // ── Physiques ──
        add("physiques.immortalBodyQiAbsorbMult", "Immortal Body Qi Absorb Mult", "Qi absorption multiplier for the Immortal Body physique. Immortal Body is the strongest physique - it passively absorbs Qi from the environment.",
            "Higher = absorbs Qi much faster (passive Qi gain)", "Lower = slower Qi absorption", "Cultivation", "Physiques", "Immortal Body",
            List.of("physique", "immortal", "body", "qi", "absorb", "mult"));
        add("physiques.immortalBodyQiAbsorbRange", "Immortal Body Qi Absorb Range", "Range (in blocks) within which the Immortal Body absorbs Qi from the environment.",
            "Larger range = absorbs Qi from further away", "Smaller range = must be closer to Qi sources", "Cultivation", "Physiques", "Immortal Body",
            List.of("physique", "immortal", "body", "qi", "absorb", "range"));
        add("physiques.immortalBodyQiCostMult", "Immortal Body Qi Cost Mult", "Multiplier for Qi costs when using the Immortal Body physique. Immortal Body makes spells cheaper.",
            "Lower = spells cost less Qi (cheaper)", "Higher = spells cost more Qi", "Cultivation", "Physiques", "Immortal Body",
            List.of("physique", "immortal", "body", "qi", "cost", "mult"));
        add("physiques.immortalBodyDamageTakenMult", "Immortal Body Damage Taken Mult", "Multiplier for damage taken when using the Immortal Body physique. Immortal Body reduces incoming damage.",
            "Lower = takes less damage (tankier)", "Higher = takes more damage", "Cultivation", "Physiques", "Immortal Body",
            List.of("physique", "immortal", "body", "damage", "taken", "mult", "defense"));
        add("physiques.immortalBodyMaxHpBonus", "Immortal Body Max HP Bonus", "Flat bonus to max HP from the Immortal Body physique.",
            "More HP = harder to kill", "Less HP bonus", "Cultivation", "Physiques", "Immortal Body",
            List.of("physique", "immortal", "body", "max", "hp", "bonus"));
        add("physiques.innateSwordBodySwordSpellMult", "Innate Sword Body Sword Spell Mult", "Sword spell damage multiplier for the Innate Sword Body physique. This physique specializes in sword-type spells.",
            "Higher = sword spells much stronger", "Lower = weaker sword spells", "Cultivation", "Physiques", "Innate Sword Body",
            List.of("physique", "innate", "sword", "body", "spell", "mult", "damage"));
        add("physiques.innateSwordNonSwordPenalty", "Innate Sword Non-Sword Penalty", "Damage penalty for non-sword spells when using Innate Sword Body. This physique is specialized - non-sword spells are weaker.",
            "Higher = less penalty for non-sword spells", "Lower = non-sword spells much weaker", "Cultivation", "Physiques", "Innate Sword Body",
            List.of("physique", "innate", "sword", "body", "non", "sword", "penalty"));
        add("physiques.heavenlyFireBodyFireSpellMult", "Heavenly Fire Body Fire Spell Mult", "Fire spell damage multiplier for the Heavenly Fire Body physique. This physique boosts fire-element spells.",
            "Higher = fire spells much stronger", "Lower = weaker fire spells", "Cultivation", "Physiques", "Heavenly Fire Body",
            List.of("physique", "heavenly", "fire", "body", "spell", "mult", "fire"));
        add("physiques.mysticIceBodyWaterSpellMult", "Mystic Ice Body Water Spell Mult", "Water/ice spell damage multiplier for the Mystic Ice Body physique. This physique boosts water-element spells.",
            "Higher = water spells much stronger", "Lower = weaker water spells", "Cultivation", "Physiques", "Mystic Ice Body",
            List.of("physique", "mystic", "ice", "body", "water", "spell", "mult"));
        add("physiques.swordBoneSwordSpellMult", "Sword Bone Sword Spell Mult", "Sword spell damage multiplier for the Sword Bone physique. Similar to Innate Sword Body but a different constitution.",
            "Higher = sword spells stronger", "Lower = weaker", "Cultivation", "Physiques", "Sword Bone",
            List.of("physique", "sword", "bone", "spell", "mult", "damage"));
        add("physiques.chaosBodySpellDamageMult", "Chaos Body Spell Damage Mult", "All spell damage multiplier for the Chaos Body physique. Chaos Body boosts ALL spells equally.",
            "Higher = all spells stronger", "Lower = all spells weaker", "Cultivation", "Physiques", "Chaos Body",
            List.of("physique", "chaos", "body", "spell", "damage", "mult", "all"));
        add("physiques.chaosBodyCultivationReqMult", "Chaos Body Cultivation Req Mult", "Multiplier for cultivation requirements when using Chaos Body. Chaos Body is powerful but harder to cultivate.",
            "Higher = needs more Qi to break through (harder)", "Lower = easier to cultivate", "Cultivation", "Physiques", "Chaos Body",
            List.of("physique", "chaos", "body", "cultivation", "requirement", "mult"));
        add("physiques.brokenVeinHpMult", "Broken Vein HP Mult", "HP multiplier for the Broken Vein physique. Broken Vein is a weak physique with damaged meridians.",
            "Higher = more HP (less penalty)", "Lower = less HP (more fragile)", "Cultivation", "Physiques", "Broken Vein",
            List.of("physique", "broken", "vein", "hp", "mult", "weak"));
        add("physiques.brokenVeinMeleeDmgMult", "Broken Vein Melee Dmg Mult", "Melee damage multiplier for the Broken Vein physique.",
            "Higher = more melee damage", "Lower = less melee damage", "Cultivation", "Physiques", "Broken Vein",
            List.of("physique", "broken", "vein", "melee", "damage", "mult"));
        add("physiques.immortalBloodHpMult", "Immortal Blood HP Mult", "HP multiplier for the Immortal Blood physique. This physique grants massive HP.",
            "Higher = much more HP", "Lower = less HP bonus", "Cultivation", "Physiques", "Immortal Blood",
            List.of("physique", "immortal", "blood", "hp", "mult"));
        // Physique rarity weights - these control how often each rarity tier appears when a physique is rolled
        // Think of this like a loot table: higher weight = more common, lower weight = rarer
        // The tiers map to: Low=Common, Mid=Uncommon, High=Rare, Supreme=Epic, Immortal=Legendary, Special=Mythic
        add("physiques.rarityWeights.low", "Physique Rarity: Common (Low)", "Spawn weight for Common (Low-tier) physiques. These are the most basic physiques with minor bonuses. Think of this as the 'Common' rarity tier - like gray/white items in RPGs.",
            "Higher = Common physiques appear more often (most NPCs get weak physiques)", "Lower = Common physiques are rarer (NPCs more likely to get better physiques)", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "low", "common", "spawn", "chance", "tier"));
        add("physiques.rarityWeights.mid", "Physique Rarity: Uncommon (Mid)", "Spawn weight for Uncommon (Mid-tier) physiques. These have moderate bonuses. Think of this as the 'Uncommon' rarity tier - like green items in RPGs.",
            "Higher = Uncommon physiques appear more often", "Lower = Uncommon physiques are rarer", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "mid", "uncommon", "spawn", "chance", "tier"));
        add("physiques.rarityWeights.high", "Physique Rarity: Rare (High)", "Spawn weight for Rare (High-tier) physiques. These have significant bonuses like sword spell boosts or fire spell boosts. Think of this as the 'Rare' rarity tier - like blue items in RPGs.",
            "Higher = Rare physiques appear more often (more strong NPCs)", "Lower = Rare physiques are very uncommon", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "high", "rare", "spawn", "chance", "tier"));
        add("physiques.rarityWeights.supreme", "Physique Rarity: Epic (Supreme)", "Spawn weight for Epic (Supreme-tier) physiques. These have powerful bonuses. Think of this as the 'Epic' rarity tier - like purple items in RPGs.",
            "Higher = Epic physiques appear more often (very powerful NPCs)", "Lower = Epic physiques are extremely rare", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "supreme", "epic", "spawn", "chance", "tier"));
        add("physiques.rarityWeights.immortal", "Physique Rarity: Legendary (Immortal)", "Spawn weight for Legendary (Immortal-tier) physiques. The strongest physiques with massive bonuses like Chaos Body (all spells boosted). Think of this as the 'Legendary' rarity tier - like gold/orange items in RPGs.",
            "Higher = Legendary physiques appear more often (extremely powerful NPCs)", "Lower = Legendary physiques are almost never seen", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "immortal", "legendary", "spawn", "chance", "tier"));
        add("physiques.rarityWeights.special", "Physique Rarity: Mythic (Special)", "Spawn weight for Mythic (Special-tier) physiques. These are unique physiques that don't fit standard tiers - they may have custom or unusual effects. Think of this as the 'Mythic' rarity tier - the rarest of all.",
            "Higher = Mythic physiques appear more often (unique NPCs)", "Lower = Mythic physiques are virtually never seen", "Cultivation", "Physiques", "Rarity Weights",
            List.of("physique", "rarity", "weight", "special", "mythic", "unique", "spawn", "chance", "tier"));

        // ── Foundation Dao ──
        add("foundationDao.lifespanBonus.human", "Human Dao Lifespan Bonus", "Bonus lifespan (in years) when choosing Human Dao for Foundation Building. Human Dao is the safest path with moderate bonuses.",
            "More bonus years = longer life", "Fewer bonus years", "Cultivation", "Foundation Dao", "Lifespan Bonuses",
            List.of("foundation", "dao", "human", "lifespan", "bonus"));
        add("foundationDao.lifespanBonus.blood", "Blood Dao Lifespan Bonus", "Bonus lifespan for Blood Dao. Blood Dao focuses on combat and HP at the cost of lifespan.",
            "More bonus years", "Fewer bonus years", "Cultivation", "Foundation Dao", "Lifespan Bonuses",
            List.of("foundation", "dao", "blood", "lifespan", "bonus"));
        add("foundationDao.lifespanBonus.earth", "Earth Dao Lifespan Bonus", "Bonus lifespan for Earth Dao. Earth Dao balances defense and cultivation efficiency.",
            "More bonus years", "Fewer bonus years", "Cultivation", "Foundation Dao", "Lifespan Bonuses",
            List.of("foundation", "dao", "earth", "lifespan", "bonus"));
        add("foundationDao.lifespanBonus.heaven", "Heaven Dao Lifespan Bonus", "Bonus lifespan for Heaven Dao. Heaven Dao is the strongest but hardest path with the best bonuses.",
            "More bonus years", "Fewer bonus years", "Cultivation", "Foundation Dao", "Lifespan Bonuses",
            List.of("foundation", "dao", "heaven", "lifespan", "bonus"));
        add("foundationDao.spellMultipliers.earthSpellDamageMult", "Earth Dao Spell Damage Mult",
            "Earth Dao cultivators draw power from the land itself, channeling the steady, enduring energy of mountains and valleys. " +
            "This multiplier scales the damage of ALL spells cast by an Earth Dao Foundation Building cultivator. " +
            "At default 1.0, Earth Dao spells deal base damage. At 1.5, they deal 50% more. " +
            "Earth Dao is the defensive cultivation path - its spell damage is moderate but its cultivators are hard to kill, " +
            "with strong body defense, cultivation efficiency, and Qi recovery bonuses. " +
            "Increase this if you want Earth Dao to be a stronger spellcasting path; decrease it to keep Earth Dao focused on defense.",
            "Higher = Earth Dao spells hit much harder (offensive Earth Dao build)", "Lower = Earth Dao spells are weaker (defensive focus)",
            "Cultivation", "Foundation Dao", "Spell Multipliers",
            List.of("foundation", "dao", "earth", "spell", "damage", "mult"));
        add("foundationDao.spellMultipliers.heavenSpellDamageMult", "Heaven Dao Spell Damage Mult",
            "Heaven Dao cultivators draw power from the celestial realm itself, channeling the supreme authority of the heavens. " +
            "This multiplier scales the damage of ALL spells cast by a Heaven Dao Foundation Building cultivator. " +
            "Heaven Dao is the strongest cultivation path - its cultivators are the most powerful spellcasters, " +
            "with the highest spell damage, lowest spell Qi costs, best cultivation efficiency, and fastest Qi recovery. " +
            "However, Heaven Dao also faces the most tribulation waves and the hardest breakthrough trials. " +
            "This is the path of those who seek ultimate power at ultimate risk. " +
            "Increase this to make Heaven Dao overwhelmingly powerful; decrease it to balance against other paths.",
            "Higher = Heaven Dao spells are devastatingly powerful (supreme spellcaster)", "Lower = Heaven Dao spell advantage is reduced",
            "Cultivation", "Foundation Dao", "Spell Multipliers",
            List.of("foundation", "dao", "heaven", "spell", "damage", "mult"));
        add("foundationDao.spellMultipliers.earthSpellQiCostMult", "Earth Dao Spell Qi Cost Mult",
            "Earth Dao cultivators channel the steady energy of the earth, which is abundant but not refined. " +
            "This multiplier scales the Qi cost of ALL spells cast by an Earth Dao Foundation Building cultivator. " +
            "At default 1.0, Earth Dao spells cost base Qi. At 0.8, they cost 20% less Qi, allowing more spells before depletion. " +
            "Earth Dao already has good Qi recovery, so reducing spell costs further extends sustained combat ability. " +
            "This is separate from the Heaven Dao cost multiplier - Earth Dao spells are generally cheaper than Heaven Dao " +
            "but deal less damage. Balance this with the damage multiplier to tune Earth Dao's combat style: " +
            "cheap weak spells (high damage mult, low cost mult) or expensive strong spells (both high).",
            "Higher = Earth Dao spells cost more Qi (less sustainable)", "Lower = Earth Dao spells cost less Qi (more sustainable)",
            "Cultivation", "Foundation Dao", "Spell Multipliers",
            List.of("foundation", "dao", "earth", "spell", "qi", "cost", "mult"));
        add("foundationDao.spellMultipliers.heavenSpellQiCostMult", "Heaven Dao Spell Qi Cost Mult",
            "Heaven Dao cultivators channel refined celestial Qi, which is extremely potent but also extremely efficient. " +
            "This multiplier scales the Qi cost of ALL spells cast by a Heaven Dao Foundation Building cultivator. " +
            "At default 1.0, Heaven Dao spells cost base Qi. At 0.7, they cost 30% less, making Heaven Dao the most " +
            "Qi-efficient spellcasting path. Combined with the highest spell damage multiplier, Heaven Dao cultivators " +
            "can cast devastating spells cheaply, making them the supreme spellcasters. " +
            "This efficiency reflects the heavenly origin of their power - celestial Qi is refined and pure, " +
            "wasting nothing. Reduce this further to make Heaven Dao overwhelmingly efficient; increase it to " +
            "balance the power gap between Heaven Dao and other paths.",
            "Higher = Heaven Dao spells cost more Qi (balances the power gap)", "Lower = Heaven Dao spells are extremely cheap (supreme efficiency)",
            "Cultivation", "Foundation Dao", "Spell Multipliers",
            List.of("foundation", "dao", "heaven", "spell", "qi", "cost", "mult"));
        add("foundationDao.bloodHpMult", "Blood Dao HP Mult", "HP multiplier for Blood Dao Foundation. Blood Dao grants significantly more HP.",
            "Higher = much more HP (tankier)", "Lower = less HP bonus", "Cultivation", "Foundation Dao", "Combat",
            List.of("foundation", "dao", "blood", "hp", "mult"));
        add("foundationDao.bodyDefense.human", "Human Dao Body Defense Bonus", "Flat body defense bonus for Human Dao.",
            "More defense = take less physical damage", "Less defense", "Cultivation", "Foundation Dao", "Body Defense",
            List.of("foundation", "dao", "human", "body", "defense", "bonus"));
        add("foundationDao.bodyDefense.blood", "Blood Dao Body Defense Bonus", "Body defense bonus for Blood Dao.",
            "More defense", "Less defense", "Cultivation", "Foundation Dao", "Body Defense",
            List.of("foundation", "dao", "blood", "body", "defense", "bonus"));
        add("foundationDao.bodyDefense.earth", "Earth Dao Body Defense Bonus", "Body defense bonus for Earth Dao. Earth Dao has the best defense.",
            "More defense = tankier", "Less defense", "Cultivation", "Foundation Dao", "Body Defense",
            List.of("foundation", "dao", "earth", "body", "defense", "bonus"));
        add("foundationDao.bodyDefense.heaven", "Heaven Dao Body Defense Bonus", "Body defense bonus for Heaven Dao.",
            "More defense", "Less defense", "Cultivation", "Foundation Dao", "Body Defense",
            List.of("foundation", "dao", "heaven", "body", "defense", "bonus"));
        add("foundationDao.cultivationEfficiency.earth", "Earth Dao Cultivation Efficiency", "Cultivation efficiency bonus for Earth Dao. Higher = faster Qi gathering.",
            "More efficiency = faster cultivation", "Less efficiency = slower", "Cultivation", "Foundation Dao", "Cultivation Efficiency",
            List.of("foundation", "dao", "earth", "cultivation", "efficiency", "bonus"));
        add("foundationDao.cultivationEfficiency.heaven", "Heaven Dao Cultivation Efficiency", "Cultivation efficiency bonus for Heaven Dao.",
            "More efficiency = faster cultivation", "Less efficiency", "Cultivation", "Foundation Dao", "Cultivation Efficiency",
            List.of("foundation", "dao", "heaven", "cultivation", "efficiency"));
        add("foundationDao.qiRecovery.earth", "Earth Dao Qi Recovery Bonus", "Qi recovery rate bonus for Earth Dao.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Foundation Dao", "Qi Recovery",
            List.of("foundation", "dao", "earth", "qi", "recovery", "bonus"));
        add("foundationDao.qiRecovery.heaven", "Heaven Dao Qi Recovery Bonus", "Qi recovery rate bonus for Heaven Dao.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Foundation Dao", "Qi Recovery",
            List.of("foundation", "dao", "heaven", "qi", "recovery"));
        add("foundationDao.bloodMeleeDamageBonus", "Blood Dao Melee Damage Bonus", "Flat melee damage bonus for Blood Dao. Blood Dao excels at physical combat.",
            "More melee damage", "Less melee damage", "Cultivation", "Foundation Dao", "Combat",
            List.of("foundation", "dao", "blood", "melee", "damage", "bonus"));
        add("foundationDao.heavenBoneAgeLimit", "Heaven Dao Bone Age Limit", "Maximum bone age to attempt Heaven Dao Foundation. If your bone age exceeds this, you can't choose Heaven Dao.",
            "Higher limit = more time to attempt Heaven Dao", "Lower limit = must attempt earlier", "Cultivation", "Foundation Dao", "Limits",
            List.of("foundation", "dao", "heaven", "bone", "age", "limit", "max"));
        add("foundationDao.tribulationWaves.heaven", "Heaven Dao Tribulation Waves", "Number of tribulation waves when breaking through with Heaven Dao. More waves = harder tribulation.",
            "More waves = harder tribulation", "Fewer waves = easier", "Cultivation", "Foundation Dao", "Tribulation Waves",
            List.of("foundation", "dao", "heaven", "tribulation", "waves"));
        add("foundationDao.tribulationWaves.earth", "Earth Dao Tribulation Waves", "Number of tribulation waves for Earth Dao breakthrough.",
            "More waves = harder", "Fewer waves = easier", "Cultivation", "Foundation Dao", "Tribulation Waves",
            List.of("foundation", "dao", "earth", "tribulation", "waves"));

        // ── Golden Core Dao ──
        add("goldenCoreDao.lifespanBonus.human", "Golden Core Human Dao Lifespan", "Bonus lifespan for Human Dao Golden Core.",
            "More years", "Fewer years", "Cultivation", "Golden Core Dao", "Lifespan Bonus");
        add("goldenCoreDao.lifespanBonus.blood", "Golden Core Blood Dao Lifespan", "Bonus lifespan for Blood Dao Golden Core.",
            "More years", "Fewer years", "Cultivation", "Golden Core Dao", "Lifespan Bonus");
        add("goldenCoreDao.lifespanBonus.earth", "Golden Core Earth Dao Lifespan", "Bonus lifespan for Earth Dao Golden Core.",
            "More years", "Fewer years", "Cultivation", "Golden Core Dao", "Lifespan Bonus");
        add("goldenCoreDao.lifespanBonus.heaven", "Golden Core Heaven Dao Lifespan", "Bonus lifespan for Heaven Dao Golden Core. The largest lifespan bonus.",
            "Many more years", "Fewer years", "Cultivation", "Golden Core Dao", "Lifespan Bonus");
        add("goldenCoreDao.spellMultipliers.earthSpellDamageMult", "Golden Core Earth Spell Dmg Mult",
            "When an Earth Dao cultivator forms their Golden Core, the earth's power crystallizes into the core itself, " +
            "amplifying spell damage beyond what Foundation Building granted. This multiplier scales ALL spell damage " +
            "for an Earth Dao Golden Core cultivator. The Golden Core stage is a massive power jump - the compressed Qi " +
            "core acts as a reservoir and amplifier for all spellcasting. Earth Dao Golden Core cultivators are " +
            "durable spellcasters with strong defense, efficient cultivation, and steady Qi recovery. " +
            "Their spell damage is moderate compared to Heaven Dao but they survive longer in sustained combat. " +
            "Increase this to make Earth Dao Golden Core a stronger offensive path; decrease to keep it defensive.",
            "Higher = Earth Dao Golden Core spells hit much harder", "Lower = weaker Golden Core spells",
            "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.spellMultipliers.heavenSpellDamageMult", "Golden Core Heaven Spell Dmg Mult",
            "When a Heaven Dao cultivator forms their Golden Core, celestial Qi compresses into a core of supreme purity, " +
            "creating the most powerful spellcasting amplifier in the cultivation world. This multiplier scales ALL spell " +
            "damage for a Heaven Dao Golden Core cultivator. Heaven Dao Golden Core is the pinnacle of spellcasting power - " +
            "combined with the lowest spell Qi costs, highest cultivation efficiency, and fastest Qi recovery, " +
            "Heaven Dao Golden Core cultivators are the most formidable spellcasters in existence. " +
            "However, they face the most tribulation strikes and the strongest Shatter Core Trial boss. " +
            "The Shatter Core Trial is a boss fight during Golden Core formation - Heaven Dao faces the toughest boss. " +
            "Increase this to make Heaven Dao Golden Core overwhelmingly powerful; decrease to balance against other paths.",
            "Higher = Heaven Dao Golden Core spells are devastatingly powerful (supreme spellcaster)", "Lower = Heaven Dao advantage reduced",
            "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.spellMultipliers.earthSpellQiCostMult", "Golden Core Earth Qi Cost Mult",
            "The Earth Dao Golden Core refines the earth's abundant Qi into a compact, efficient form. " +
            "This multiplier scales the Qi cost of ALL spells for an Earth Dao Golden Core cultivator. " +
            "At default 1.0, spells cost base Qi. At 0.8, they cost 20% less, extending combat endurance. " +
            "The Golden Core's compressed Qi storage means cultivators can cast more spells before depleting, " +
            "and Earth Dao's natural Qi recovery further extends sustained combat. " +
            "This makes Earth Dao Golden Core the most sustainable spellcasting path - not the hardest hitting, " +
            "but able to cast spells all day without running dry. Balance with the damage multiplier to tune " +
            "Earth Dao's combat identity: cheap and steady vs. expensive and bursty.",
            "Higher = Earth Dao Golden Core spells cost more Qi (less sustainable)", "Lower = spells are cheaper (more sustainable)",
            "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.spellMultipliers.heavenSpellQiCostMult", "Golden Core Heaven Qi Cost Mult",
            "The Heaven Dao Golden Core is forged from the purest celestial Qi, creating a core of such refinement " +
            "that spellcasting becomes almost effortless. This multiplier scales the Qi cost of ALL spells for " +
            "a Heaven Dao Golden Core cultivator. At default 1.0, spells cost base Qi. At 0.7, they cost 30% less. " +
            "Combined with the highest spell damage multiplier, Heaven Dao Golden Core cultivators can cast " +
            "devastating spells at minimal Qi cost, making them the most efficient and powerful spellcasters. " +
            "This supreme efficiency reflects the heavenly origin of their power - celestial Qi is so refined " +
            "that it amplifies spell power while reducing cost simultaneously. " +
            "Reduce this further to make Heaven Dao overwhelmingly efficient; increase it to balance the gap " +
            "between Heaven Dao and other paths. This is the most impactful single stat for Heaven Dao balance.",
            "Higher = Heaven Dao Golden Core spells cost more Qi (balances supreme power)", "Lower = spells extremely cheap (supreme efficiency)",
            "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.bloodHpMult", "Golden Core Blood HP Mult", "HP multiplier for Blood Dao Golden Core.",
            "Higher = much more HP", "Lower = less HP", "Cultivation", "Golden Core Dao", "Combat");
        add("goldenCoreDao.bloodBloodSpellDamageMult", "Blood Spell Damage Mult (Blood Dao)", "Blood spell damage multiplier for Blood Dao Golden Core. Blood spells are special spells that cost HP. Filed under Spell Multipliers alongside Earth/Heaven's spell multipliers - previously misfiled under Combat, which put Blood Dao's own spell-scaling controls in a different group than every other Dao's.",
            "Higher = blood spells devastating", "Lower = weaker blood spells", "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.bloodBloodSpellQiCostMult", "Blood Spell Qi Cost Mult (Blood Dao)", "Blood spell Qi cost multiplier for Blood Dao. Filed under Spell Multipliers alongside Earth/Heaven's spell multipliers - previously misfiled under Combat.",
            "Higher = blood spells cost more Qi", "Lower = cheaper", "Cultivation", "Golden Core Dao", "Spell Multipliers");
        add("goldenCoreDao.tribulation.humanStrikes", "Human Dao Tribulation Strikes", "Number of tribulation lightning strikes for Human Dao Golden Core breakthrough.",
            "More strikes = harder", "Fewer = easier", "Cultivation", "Golden Core Dao", "Tribulation");
        add("goldenCoreDao.tribulation.bloodStrikes", "Blood Dao Tribulation Strikes", "Tribulation strikes for Blood Dao.",
            "More = harder", "Fewer = easier", "Cultivation", "Golden Core Dao", "Tribulation");
        add("goldenCoreDao.tribulation.earthStrikes", "Earth Dao Tribulation Strikes", "Tribulation strikes for Earth Dao.",
            "More = harder", "Fewer = easier", "Cultivation", "Golden Core Dao", "Tribulation");
        add("goldenCoreDao.tribulation.heavenStrikes", "Heaven Dao Tribulation Strikes", "Tribulation strikes for Heaven Dao. The most strikes.",
            "Many more = extremely hard", "Fewer = easier", "Cultivation", "Golden Core Dao", "Tribulation");
        add("goldenCoreDao.tribulation.damage", "Golden Core Tribulation Damage", "Damage per tribulation strike during Golden Core breakthrough.",
            "More damage = deadlier", "Less damage = easier", "Cultivation", "Golden Core Dao", "Tribulation");
        add("goldenCoreDao.heavenBoneAgeLimit", "Heaven Dao Bone Age Limit (Golden Core)", "Max bone age to attempt Heaven Dao Golden Core.",
            "Higher = more time to attempt", "Lower = must attempt earlier", "Cultivation", "Golden Core Dao", "Limits");
        add("goldenCoreDao.shatterCoreTrial.humanMaxHealth", "Human Dao Shatter Trial Boss HP", "Max health of the shatter core trial boss for Human Dao. The boss must be defeated to form your golden core.",
            "More HP = harder boss fight", "Less HP = easier fight", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.bloodMaxHealth", "Blood Dao Shatter Trial Boss HP", "Max health of Blood Dao shatter trial boss.",
            "More HP = harder", "Less HP = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.earthMaxHealth", "Earth Dao Shatter Trial Boss HP", "Max health of Earth Dao shatter trial boss.",
            "More HP = harder", "Less HP = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.heavenMaxHealth", "Heaven Dao Shatter Trial Boss HP", "Max health of Heaven Dao shatter trial boss. The hardest boss.",
            "Much more HP = very hard fight", "Less HP = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.humanRegen", "Human Dao Shatter Trial Boss Regen", "Health regeneration per second of the Human Dao trial boss.",
            "Higher regen = harder to kill (must DPS faster)", "Lower = easier to out-damage", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.bloodRegen", "Blood Dao Shatter Trial Boss Regen", "Regen of Blood Dao trial boss.",
            "Higher = harder", "Lower = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.earthRegen", "Earth Dao Shatter Trial Boss Regen", "Regen of Earth Dao trial boss.",
            "Higher = harder", "Lower = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");
        add("goldenCoreDao.shatterCoreTrial.heavenRegen", "Heaven Dao Shatter Trial Boss Regen", "Regen of Heaven Dao trial boss.",
            "Higher = very hard to kill", "Lower = easier", "Cultivation", "Golden Core Dao", "Shatter Core Trial");

        // ── Golden Core Dao - previously-undescribed bonuses (ExtendedConfig.java's own
        // "Missing GoldenCoreDao bonuses" comment) - these 20 fields had real config values
        // but no registry entry, so they rendered with only an auto-derived generic name and
        // no description. Descriptions below are grounded in each field's actual defineInRange
        // default from ExtendedConfig.java (not invented), including the melee bonus's
        // deliberately non-monotonic 0/4/2/0 spread (Blood Dao is the melee-focused path). ──
        add("goldenCoreDao.bodyDefenseBonus.human", "Golden Core Human Body Defense Bonus", "Additional flat body defense bonus for Human Dao at Golden Core, on top of the Foundation Dao bonus.",
            "More defense", "Less defense", "Cultivation", "Golden Core Dao", "Body Defense Bonus");
        add("goldenCoreDao.bodyDefenseBonus.blood", "Golden Core Blood Body Defense Bonus", "Additional flat body defense bonus for Blood Dao at Golden Core.",
            "More defense", "Less defense", "Cultivation", "Golden Core Dao", "Body Defense Bonus");
        add("goldenCoreDao.bodyDefenseBonus.earth", "Golden Core Earth Body Defense Bonus", "Additional flat body defense bonus for Earth Dao at Golden Core. Earth Dao's defense keeps scaling up.",
            "More defense", "Less defense", "Cultivation", "Golden Core Dao", "Body Defense Bonus");
        add("goldenCoreDao.bodyDefenseBonus.heaven", "Golden Core Heaven Body Defense Bonus", "Additional flat body defense bonus for Heaven Dao at Golden Core. The highest of the four.",
            "More defense", "Less defense", "Cultivation", "Golden Core Dao", "Body Defense Bonus");
        add("goldenCoreDao.cultivationEfficiencyBonus.human", "Golden Core Human Cultivation Efficiency", "Additional cultivation efficiency bonus for Human Dao at Golden Core. Human Dao gets no extra efficiency here.",
            "More efficiency = faster cultivation", "Less efficiency", "Cultivation", "Golden Core Dao", "Cultivation Efficiency Bonus");
        add("goldenCoreDao.cultivationEfficiencyBonus.blood", "Golden Core Blood Cultivation Efficiency", "Additional cultivation efficiency bonus for Blood Dao at Golden Core. Blood Dao gets no extra efficiency here - its focus is combat.",
            "More efficiency", "Less efficiency", "Cultivation", "Golden Core Dao", "Cultivation Efficiency Bonus");
        add("goldenCoreDao.cultivationEfficiencyBonus.earth", "Golden Core Earth Cultivation Efficiency", "Additional cultivation efficiency bonus for Earth Dao at Golden Core.",
            "More efficiency = faster cultivation", "Less efficiency", "Cultivation", "Golden Core Dao", "Cultivation Efficiency Bonus");
        add("goldenCoreDao.cultivationEfficiencyBonus.heaven", "Golden Core Heaven Cultivation Efficiency", "Additional cultivation efficiency bonus for Heaven Dao at Golden Core. The largest of the four.",
            "More efficiency = fastest cultivation", "Less efficiency", "Cultivation", "Golden Core Dao", "Cultivation Efficiency Bonus");
        add("goldenCoreDao.qiRecoveryBonus.human", "Golden Core Human Qi Recovery Bonus", "Additional Qi recovery rate bonus for Human Dao at Golden Core. Human Dao gets no extra recovery here.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Golden Core Dao", "Qi Recovery Bonus");
        add("goldenCoreDao.qiRecoveryBonus.blood", "Golden Core Blood Qi Recovery Bonus", "Additional Qi recovery rate bonus for Blood Dao at Golden Core. Blood Dao gets no extra recovery here.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Golden Core Dao", "Qi Recovery Bonus");
        add("goldenCoreDao.qiRecoveryBonus.earth", "Golden Core Earth Qi Recovery Bonus", "Additional Qi recovery rate bonus for Earth Dao at Golden Core.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Golden Core Dao", "Qi Recovery Bonus");
        add("goldenCoreDao.qiRecoveryBonus.heaven", "Golden Core Heaven Qi Recovery Bonus", "Additional Qi recovery rate bonus for Heaven Dao at Golden Core. The fastest of the four.",
            "Faster Qi recovery", "Slower Qi recovery", "Cultivation", "Golden Core Dao", "Qi Recovery Bonus");
        add("goldenCoreDao.meleeDamageBonus.human", "Golden Core Human Melee Damage Bonus", "Additional flat melee damage bonus for Human Dao at Golden Core. Human Dao gets no extra melee here.",
            "More melee damage", "Less melee damage", "Cultivation", "Golden Core Dao", "Melee Damage Bonus");
        add("goldenCoreDao.meleeDamageBonus.blood", "Golden Core Blood Melee Damage Bonus", "Additional flat melee damage bonus for Blood Dao at Golden Core. The highest of the four - Blood Dao is the melee-focused path.",
            "More melee damage", "Less melee damage", "Cultivation", "Golden Core Dao", "Melee Damage Bonus");
        add("goldenCoreDao.meleeDamageBonus.earth", "Golden Core Earth Melee Damage Bonus", "Additional flat melee damage bonus for Earth Dao at Golden Core.",
            "More melee damage", "Less melee damage", "Cultivation", "Golden Core Dao", "Melee Damage Bonus");
        add("goldenCoreDao.meleeDamageBonus.heaven", "Golden Core Heaven Melee Damage Bonus", "Additional flat melee damage bonus for Heaven Dao at Golden Core. Heaven Dao gets no extra melee here - its focus is spellcasting.",
            "More melee damage", "Less melee damage", "Cultivation", "Golden Core Dao", "Melee Damage Bonus");
        add("goldenCoreDao.shatterTrialReflection.human", "Golden Core Human Shatter Trial Reflection", "Fraction of damage reflected back at the shatter core trial boss for Human Dao.",
            "More reflection = boss hurts itself more", "Less reflection", "Cultivation", "Golden Core Dao", "Shatter Trial Reflection");
        add("goldenCoreDao.shatterTrialReflection.blood", "Golden Core Blood Shatter Trial Reflection", "Fraction of damage reflected back at the shatter core trial boss for Blood Dao.",
            "More reflection", "Less reflection", "Cultivation", "Golden Core Dao", "Shatter Trial Reflection");
        add("goldenCoreDao.shatterTrialReflection.earth", "Golden Core Earth Shatter Trial Reflection", "Fraction of damage reflected back at the shatter core trial boss for Earth Dao.",
            "More reflection", "Less reflection", "Cultivation", "Golden Core Dao", "Shatter Trial Reflection");
        add("goldenCoreDao.shatterTrialReflection.heaven", "Golden Core Heaven Shatter Trial Reflection", "Fraction of damage reflected back at the shatter core trial boss for Heaven Dao. The highest of the four.",
            "More reflection", "Less reflection", "Cultivation", "Golden Core Dao", "Shatter Trial Reflection");

        // ── Identity ──
        add("identity.lifespanMartialMin", "Martial Lifespan Min", "Minimum starting lifespan for Martial identity characters. Martial identities (like Beast Descendant, Warrior) tend to have shorter lifespans but stronger bodies.",
            "Higher minimum = guaranteed longer life", "Lower = can start very short-lived", "NPCs", "Identity", "Martial");
        add("identity.lifespanMartialMax", "Martial Lifespan Max", "Maximum starting lifespan for Martial identity.",
            "Higher = can roll longer life", "Lower = capped shorter", "NPCs", "Identity", "Martial");
        add("identity.lifespanScholarMin", "Scholar Lifespan Min", "Minimum starting lifespan for Scholar identity. Scholars tend to live moderate lifespans.",
            "Higher minimum", "Lower minimum", "NPCs", "Identity", "Scholar");
        add("identity.lifespanScholarMax", "Scholar Lifespan Max", "Maximum starting lifespan for Scholar identity.",
            "Higher max", "Lower max", "NPCs", "Identity", "Scholar");
        add("identity.lifespanCultivatorMin", "Cultivator Lifespan Min", "Minimum starting lifespan for Cultivator identity. Cultivators tend to live the longest.",
            "Higher minimum", "Lower minimum", "NPCs", "Identity", "Cultivator");
        add("identity.lifespanCultivatorMax", "Cultivator Lifespan Max", "Maximum starting lifespan for Cultivator identity.",
            "Higher max = potentially very long-lived", "Lower max", "NPCs", "Identity", "Cultivator");
        add("identity.lifespanAbandonedMin", "Abandoned Lifespan Min", "Minimum starting lifespan for Abandoned identity (orphan, abandoned child). Usually the shortest lifespans.",
            "Higher = less harsh start", "Lower = very harsh start", "NPCs", "Identity", "Abandoned");
        add("identity.lifespanAbandonedMax", "Abandoned Lifespan Max", "Maximum starting lifespan for Abandoned identity.",
            "Higher max", "Lower max", "NPCs", "Identity", "Abandoned");
        add("identity.lifespanDefaultMin", "Default Lifespan Min", "Minimum starting lifespan for default/unspecified identity.",
            "Higher minimum", "Lower minimum", "NPCs", "Identity", "Default");
        add("identity.lifespanDefaultMax", "Default Lifespan Max", "Maximum starting lifespan for default identity.",
            "Higher max", "Lower max", "NPCs", "Identity", "Default");

        // ── Progression ──
        add("progression.foundationHeavenBoneAgeLimit", "Foundation Heaven Bone Age Limit", "Maximum bone age to attempt Heaven Dao Foundation Building. If your bone age exceeds this, you cannot choose Heaven Dao.",
            "Higher = more time to prepare for Heaven Dao", "Lower = must rush to attempt", "NPCs", "Progression", "Bone Age Limits");
        add("progression.goldenCoreHeavenBoneAgeLimit", "Golden Core Heaven Bone Age Limit", "Maximum bone age to attempt Heaven Dao Golden Core.",
            "Higher = more time", "Lower = must rush", "NPCs", "Progression", "Bone Age Limits");
        add("progression.foundationHeavenEstimateDays", "Foundation Heaven Estimate Days", "Estimated days shown in the UI for Heaven Dao Foundation progression. This is a display value only.",
            "Higher = UI shows more days (slower expected)", "Lower = UI shows fewer days", "NPCs", "Progression", "Estimates");
        add("progression.foundationEarthEstimateDays", "Foundation Earth Estimate Days", "Estimated days for Earth Dao Foundation.",
            "Higher = slower estimate", "Lower = faster estimate", "NPCs", "Progression", "Estimates");
        add("progression.goldenCoreHeavenEstimateDays", "Golden Core Heaven Estimate Days", "Estimated days for Heaven Dao Golden Core.",
            "Higher = slower", "Lower = faster", "NPCs", "Progression", "Estimates");
        add("progression.goldenCoreEarthEstimateDays", "Golden Core Earth Estimate Days", "Estimated days for Earth Dao Golden Core.",
            "Higher = slower", "Lower = faster", "NPCs", "Progression", "Estimates");
        add("progression.npcTribulationDeathChance", "NPC Tribulation Death Chance", "Chance (0.0-1.0) that an NPC dies when failing a tribulation. 1.0 = always dies, 0.0 = never dies.",
            "Higher = NPCs more likely to die from tribulations", "Lower = NPCs survive tribulations more often", "NPCs", "Progression", "NPC Tribulation");
        add("progression.npcTribulationWeaknessDays", "NPC Tribulation Weakness Days", "Duration (in days) of weakness debuff after an NPC fails a tribulation but survives.",
            "Longer weakness = NPCs weakened for longer", "Shorter = recover faster", "NPCs", "Progression", "NPC Tribulation");
        add("progression.genderEditsDefault", "Gender Edits Default", "Default number of times a player can edit their gender. Gender changes are limited.",
            "More edits allowed = can change gender more times", "Fewer = more restricted", "NPCs", "Progression", "");

        // ── NPC Combat ──
        add("npcCombat.hardDodgeCap", "Hard Dodge Cap", "Maximum dodge chance any NPC can have. Even if calculated dodge is higher, it's capped at this value. 1.0 = 100% dodge.",
            "Higher cap = NPCs can become nearly unhittable", "Lower = NPCs always somewhat hittable", "NPCs", "NPC Combat", "");
        add("npcCombat.projectileScanRadius", "Projectile Scan Radius", "Radius in which NPCs scan for incoming projectiles to dodge.",
            "Larger radius = NPCs detect projectiles earlier = easier to dodge", "Smaller = less reaction time", "NPCs", "NPC Combat", "");
        add("npcCombat.maxCandidates", "Max Candidates", "Maximum number of threat candidates an NPC can track at once.",
            "Higher = NPCs aware of more threats", "Lower = NPCs track fewer threats", "NPCs", "NPC Combat", "");
        add("npcCombat.dodgeChance.mortal", "Dodge Chance: Mortal", "Dodge probability (0.0-1.0) for Mortal NPCs. Mortals rarely dodge.",
            "Higher = mortal NPCs dodge more", "Lower = mortal NPCs rarely dodge", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.qiRefining", "Dodge Chance: Qi Refining", "Dodge probability for Qi Refining NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.foundation", "Dodge Chance: Foundation", "Dodge probability for Foundation Building NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.goldenCore", "Dodge Chance: Golden Core", "Dodge probability for Golden Core NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.nascentSoul", "Dodge Chance: Nascent Soul", "Dodge probability for Nascent Soul NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.soulFormation", "Dodge Chance: Soul Formation", "Dodge probability for Soul Formation NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.voidRefining", "Dodge Chance: Void Refining", "Dodge probability for Void Refining NPCs.",
            "Higher = dodge more", "Lower = dodge less", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.dodgeChance.higher", "Dodge Chance: Higher Realms", "Dodge probability for Body Integration and above NPCs.",
            "Higher = top-tier NPCs nearly untouchable", "Lower = more hittable", "NPCs", "NPC Combat", "Dodge Chance");
        add("npcCombat.scanTicks.mortal", "Scan Ticks: Mortal", "How often (in ticks) Mortal NPCs scan for threats. Lower = more alert.",
            "Higher = scans less often (slower reactions)", "Lower = scans more often (faster reactions)", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.qiRefining", "Scan Ticks: Qi Refining", "Scan frequency for Qi Refining NPCs.",
            "Higher = slower scans", "Lower = faster scans", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.foundation", "Scan Ticks: Foundation", "Scan frequency for Foundation NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.goldenCore", "Scan Ticks: Golden Core", "Scan frequency for Golden Core NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.nascentSoul", "Scan Ticks: Nascent Soul", "Scan frequency for Nascent Soul NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.soulFormation", "Scan Ticks: Soul Formation", "Scan frequency for Soul Formation NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.voidRefining", "Scan Ticks: Void Refining", "Scan frequency for Void Refining NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.scanTicks.higher", "Scan Ticks: Higher Realms", "Scan frequency for Body Integration+ NPCs.",
            "Higher = slower", "Lower = faster (very alert)", "NPCs", "NPC Combat", "Scan Ticks");
        add("npcCombat.reactionTicks.mortal", "Reaction Ticks: Mortal", "Ticks it takes for a Mortal NPC to react after detecting a threat. Lower = faster response.",
            "Higher = slower reaction (easier to hit)", "Lower = faster reaction (harder to hit)", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.qiRefining", "Reaction Ticks: Qi Refining", "Reaction time for Qi Refining NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.foundation", "Reaction Ticks: Foundation", "Reaction time for Foundation NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.goldenCore", "Reaction Ticks: Golden Core", "Reaction time for Golden Core NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.nascentSoul", "Reaction Ticks: Nascent Soul", "Reaction time for Nascent Soul NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.soulFormation", "Reaction Ticks: Soul Formation", "Reaction time for Soul Formation NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.voidRefining", "Reaction Ticks: Void Refining", "Reaction time for Void Refining NPCs.",
            "Higher = slower", "Lower = faster", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.reactionTicks.higher", "Reaction Ticks: Higher Realms", "Reaction time for Body Integration+ NPCs.",
            "Higher = slower", "Lower = near-instant reaction", "NPCs", "NPC Combat", "Reaction Ticks");
        add("npcCombat.dodgeCooldown.mortal", "Dodge Cooldown: Mortal", "Cooldown ticks after a Mortal NPC dodges before it can dodge again.",
            "Longer cooldown = NPC vulnerable after dodging", "Shorter = can dodge again quickly", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.qiRefining", "Dodge Cooldown: Qi Refining", "Dodge cooldown for Qi Refining NPCs.",
            "Longer = vulnerable longer", "Shorter = dodges more often", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.foundation", "Dodge Cooldown: Foundation", "Dodge cooldown for Foundation NPCs.",
            "Longer = vulnerable longer", "Shorter = dodges more", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.goldenCore", "Dodge Cooldown: Golden Core", "Dodge cooldown for Golden Core NPCs.",
            "Longer = vulnerable", "Shorter = dodges more", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.nascentSoul", "Dodge Cooldown: Nascent Soul", "Dodge cooldown for Nascent Soul NPCs.",
            "Longer = vulnerable", "Shorter = dodges more", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.soulFormation", "Dodge Cooldown: Soul Formation", "Dodge cooldown for Soul Formation NPCs.",
            "Longer = vulnerable", "Shorter = dodges more", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.voidRefining", "Dodge Cooldown: Void Refining", "Dodge cooldown for Void Refining NPCs.",
            "Longer = vulnerable", "Shorter = dodges more", "NPCs", "NPC Combat", "Dodge Cooldown");
        add("npcCombat.dodgeCooldown.higher", "Dodge Cooldown: Higher Realms", "Dodge cooldown for Body Integration+ NPCs.",
            "Longer = vulnerable", "Shorter = dodges constantly", "NPCs", "NPC Combat", "Dodge Cooldown");

        // ── Formations ──
        add("formations.coreMaxQi.low", "Formation Core Max Qi (Low)", "Max Qi of a Low-tier formation core. Formations are arrays placed in the world that provide area effects.",
            "More Qi = formation lasts longer", "Less Qi = depletes faster", "World", "Formations", "Core Max Qi");
        add("formations.coreMaxQi.mid", "Formation Core Max Qi (Mid)", "Max Qi of Mid-tier formation core.",
            "More Qi", "Less Qi", "World", "Formations", "Core Max Qi");
        add("formations.coreMaxQi.high", "Formation Core Max Qi (High)", "Max Qi of High-tier formation core.",
            "More Qi", "Less Qi", "World", "Formations", "Core Max Qi");
        add("formations.coreMaxQi.supreme", "Formation Core Max Qi (Supreme)", "Max Qi of Supreme-tier formation core.",
            "More Qi", "Less Qi", "World", "Formations", "Core Max Qi");
        add("formations.coreMaxQi.immortal", "Formation Core Max Qi (Immortal)", "Max Qi of Immortal-tier formation core.",
            "Enormous Qi", "Less Qi", "World", "Formations", "Core Max Qi");
        add("formations.qiGathering.low", "Qi Gathering Mult (Low)", "Qi gathering multiplier for Low-tier Qi Gathering formations. These formations boost cultivation speed in their area.",
            "Higher = bigger cultivation speed boost", "Lower = smaller boost", "World", "Formations", "Qi Gathering");
        add("formations.qiGathering.mid", "Qi Gathering Mult (Mid)", "Qi gathering multiplier for Mid-tier.",
            "Higher = bigger boost", "Lower = smaller", "World", "Formations", "Qi Gathering");
        add("formations.qiGathering.high", "Qi Gathering Mult (High)", "Qi gathering multiplier for High-tier.",
            "Higher = bigger boost", "Lower = smaller", "World", "Formations", "Qi Gathering");
        add("formations.qiGathering.supreme", "Qi Gathering Mult (Supreme)", "Qi gathering multiplier for Supreme-tier.",
            "Higher = bigger boost", "Lower = smaller", "World", "Formations", "Qi Gathering");
        add("formations.qiGathering.immortal", "Qi Gathering Mult (Immortal)", "Qi gathering multiplier for Immortal-tier.",
            "Higher = massive cultivation boost", "Lower = smaller", "World", "Formations", "Qi Gathering");
        add("formations.qiGathering.maxMult", "Qi Gathering Max Mult", "Maximum Qi gathering multiplier cap. No matter how many formations overlap, the total bonus is capped at this value.",
            "Higher cap = allows stacking more formations", "Lower = limits stacking", "World", "Formations", "Qi Gathering");
        add("formations.growth.low", "Growth Mult (Low)", "Plant growth multiplier for Low-tier Growth formations. These formations speed up spirit plant growth in their area.",
            "Higher = plants grow faster", "Lower = plants grow slower", "World", "Formations", "Growth");
        add("formations.growth.mid", "Growth Mult (Mid)", "Growth multiplier for Mid-tier.",
            "Higher = faster growth", "Lower = slower", "World", "Formations", "Growth");
        add("formations.growth.high", "Growth Mult (High)", "Growth multiplier for High-tier.",
            "Higher = faster", "Lower = slower", "World", "Formations", "Growth");
        add("formations.growth.supreme", "Growth Mult (Supreme)", "Growth multiplier for Supreme-tier.",
            "Higher = faster", "Lower = slower", "World", "Formations", "Growth");
        add("formations.growth.immortal", "Growth Mult (Immortal)", "Growth multiplier for Immortal-tier.",
            "Higher = very fast growth", "Lower = slower", "World", "Formations", "Growth");
        add("formations.qiPerDamage.low", "Barrier Qi/Damage (Low)", "Qi consumed per point of damage blocked by a Low-tier Barrier formation. Lower = more efficient blocking.",
            "Higher = barrier drains Qi faster when hit", "Lower = barrier blocks more damage per Qi", "World", "Formations", "Barriers");
        add("formations.qiPerDamage.mid", "Barrier Qi/Damage (Mid)", "Qi per damage for Mid-tier barriers.",
            "Higher = less efficient", "Lower = more efficient", "World", "Formations", "Barriers");
        add("formations.qiPerDamage.high", "Barrier Qi/Damage (High)", "Qi per damage for High-tier barriers.",
            "Higher = less efficient", "Lower = more efficient", "World", "Formations", "Barriers");
        add("formations.qiPerDamage.supreme", "Barrier Qi/Damage (Supreme)", "Qi per damage for Supreme-tier barriers.",
            "Higher = less efficient", "Lower = more efficient", "World", "Formations", "Barriers");
        add("formations.qiPerDamage.immortal", "Barrier Qi/Damage (Immortal)", "Qi per damage for Immortal-tier barriers.",
            "Higher = less efficient", "Lower = extremely efficient", "World", "Formations", "Barriers");
        add("formations.barrierDamageImmortal", "Immortal Barrier Damage", "Damage absorbed per hit by Immortal-tier barrier formations.",
            "Higher = absorbs more damage per hit", "Lower = absorbs less", "World", "Formations", "Barriers");
        add("formations.rejuvenationAmplifier.low", "Rejuvenation Amp (Low)", "Healing amplifier for Low-tier Rejuvenation formations. These formations heal players in their area.",
            "Higher = heals more", "Lower = heals less", "World", "Formations", "Rejuvenation");
        add("formations.rejuvenationAmplifier.mid", "Rejuvenation Amp (Mid)", "Healing amplifier for Mid-tier.",
            "Higher = heals more", "Lower = heals less", "World", "Formations", "Rejuvenation");
        add("formations.rejuvenationAmplifier.high", "Rejuvenation Amp (High)", "Healing amplifier for High-tier.",
            "Higher = heals more", "Lower = heals less", "World", "Formations", "Rejuvenation");
        add("formations.rejuvenationAmplifier.supreme", "Rejuvenation Amp (Supreme)", "Healing amplifier for Supreme-tier.",
            "Higher = heals more", "Lower = heals less", "World", "Formations", "Rejuvenation");
        add("formations.rejuvenationAmplifier.immortal", "Rejuvenation Amp (Immortal)", "Healing amplifier for Immortal-tier.",
            "Higher = massive healing", "Lower = heals less", "World", "Formations", "Rejuvenation");
        add("formations.harvestInterval.low", "Harvest Interval (Low)", "Interval (in ticks) between auto-harvests for Low-tier Harvest formations. These formations automatically collect nearby crops/resources.",
            "Longer interval = harvests less often", "Shorter = harvests more frequently", "World", "Formations", "Harvest");
        add("formations.harvestInterval.mid", "Harvest Interval (Mid)", "Harvest interval for Mid-tier.",
            "Longer = less often", "Shorter = more often", "World", "Formations", "Harvest");
        add("formations.harvestInterval.high", "Harvest Interval (High)", "Harvest interval for High-tier.",
            "Longer = less often", "Shorter = more often", "World", "Formations", "Harvest");
        add("formations.harvestInterval.supreme", "Harvest Interval (Supreme)", "Harvest interval for Supreme-tier.",
            "Longer = less often", "Shorter = more often", "World", "Formations", "Harvest");
        add("formations.harvestInterval.immortalBatchSize", "Harvest Batch Size (Immortal)", "Number of items harvested per batch by Immortal-tier Harvest formations.",
            "More items per batch = more efficient", "Fewer = less efficient", "World", "Formations", "Harvest");

        // ── Sects ──
        add("sects.maxPowerScore", "Sect Max Power Score", "Maximum power score a sect can have. Power score (0-4 by default) determines sect strength tier. Higher tiers have stronger NPCs and better shops.",
            "Higher max = allows stronger sects", "Lower = caps sect strength", "NPCs", "Sects", "");
        add("sects.ancestorImmortalChancePower0", "Ancestor Immortal Chance (Power 0)", "Chance that the ancestor of a power-0 (weakest) sect is an immortal. Very low by default.",
            "Higher = weak sects more likely to have immortal ancestors", "Lower = rare", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ancestorImmortalChancePower1", "Ancestor Immortal Chance (Power 1)", "Immortal ancestor chance for power-1 sects.",
            "Higher = more likely", "Lower = rarer", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ancestorImmortalChancePower2", "Ancestor Immortal Chance (Power 2)", "Immortal ancestor chance for power-2 sects.",
            "Higher = more likely", "Lower = rarer", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ancestorImmortalChancePower3", "Ancestor Immortal Chance (Power 3)", "Immortal ancestor chance for power-3 sects.",
            "Higher = more likely", "Lower = rarer", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ancestorImmortalChancePower4", "Ancestor Immortal Chance (Power 4)", "Immortal ancestor chance for power-4 (strongest) sects. Highest chance by default.",
            "Higher = strong sects very likely to have immortal ancestors", "Lower = rarer", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ancestorLooseImmortalChance", "Ancestor Loose Immortal Chance", "Chance that a sect's ancestor is a Loose Immortal (failed to ascend but achieved immortality).",
            "Higher = more loose immortal ancestors", "Lower = rarer", "NPCs", "Sects", "Ancestor Chances");
        add("sects.ambientMaxScenes", "Ambient Max Scenes", "Maximum number of ambient scenes (background activity) visible in a sect at once.",
            "More scenes = livelier sect", "Fewer = quieter sect", "NPCs", "Sects", "Ambient");
        add("sects.ambientMaxSpectators", "Ambient Max Spectators", "Maximum number of spectator NPCs watching ambient scenes.",
            "More spectators = busier sect", "Fewer = less crowded", "NPCs", "Sects", "Ambient");
        add("sects.ambientMinCooldownTicks", "Ambient Min Cooldown", "Minimum cooldown ticks between ambient scene changes.",
            "Higher = scenes change less often", "Lower = scenes change faster", "NPCs", "Sects", "Ambient");
        add("sects.ambientMaxCooldownTicks", "Ambient Max Cooldown", "Maximum cooldown ticks between ambient scene changes.",
            "Higher = slower scene changes", "Lower = faster", "NPCs", "Sects", "Ambient");
        add("sects.ambientNpcCooldownTicks", "Ambient NPC Cooldown", "Cooldown for NPC participation in ambient scenes.",
            "Higher = NPCs participate less often", "Lower = NPCs more active", "NPCs", "Sects", "Ambient");
        add("sects.shop.sellPercent", "Shop Sell Percent", "Percentage of item value you get back when selling to sect shops. 100 = full refund, 50 = half value.",
            "Higher = better sell prices", "Lower = worse sell prices", "NPCs", "Sects", "Shop");
        add("sects.shop.techniquePrices.low", "Technique Price (Low)", "Price (in spirit stones) for Low-tier techniques in sect shops.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Techniques");
        add("sects.shop.techniquePrices.mid", "Technique Price (Mid)", "Price for Mid-tier techniques.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Techniques");
        add("sects.shop.techniquePrices.high", "Technique Price (High)", "Price for High-tier techniques.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Techniques");
        add("sects.shop.techniquePrices.supreme", "Technique Price (Supreme)", "Price for Supreme-tier techniques.",
            "Higher = very expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Techniques");
        add("sects.shop.techniquePrices.immortal", "Technique Price (Immortal)", "Price for Immortal-tier techniques.",
            "Higher = extremely expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Techniques");
        add("sects.shop.spellPrices.low", "Spell Price (Low)", "Price for Low-tier spells in sect shops.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Spells");
        add("sects.shop.spellPrices.mid", "Spell Price (Mid)", "Price for Mid-tier spells.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Spells");
        add("sects.shop.spellPrices.high", "Spell Price (High)", "Price for High-tier spells.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Spells");
        add("sects.shop.spellPrices.supreme", "Spell Price (Supreme)", "Price for Supreme-tier spells.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Spells");
        add("sects.shop.spellPrices.immortal", "Spell Price (Immortal)", "Price for Immortal-tier spells.",
            "Higher = extremely expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Spells");
        add("sects.shop.weaponPrices.low", "Weapon Price (Low)", "Price for Low-tier weapons in sect shops.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Weapons");
        add("sects.shop.weaponPrices.mid", "Weapon Price (Mid)", "Price for Mid-tier weapons.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Weapons");
        add("sects.shop.weaponPrices.high", "Weapon Price (High)", "Price for High-tier weapons.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Weapons");
        add("sects.shop.weaponPrices.supreme", "Weapon Price (Supreme)", "Price for Supreme-tier weapons.",
            "Higher = more expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Weapons");
        add("sects.shop.weaponPrices.immortal", "Weapon Price (Immortal)", "Price for Immortal-tier weapons.",
            "Higher = extremely expensive", "Lower = cheaper", "NPCs", "Sects", "Shop: Weapons");
        add("sects.taskMaxRequiredCount", "Task Max Required Count", "Maximum number of items required for a sect task. Tasks are quests given by sects.",
            "Higher = tasks can require more items (harder)", "Lower = tasks are easier", "NPCs", "Sects", "Tasks");
        add("sects.taskMaxSystemPurchases", "Task Max System Purchases", "Maximum number of system purchases allowed for sect tasks.",
            "Higher = more purchases allowed", "Lower = fewer", "NPCs", "Sects", "Tasks");
        add("sects.taskExpeditionMinDays", "Expedition Min Days", "Minimum duration (in days) for sect expedition tasks.",
            "Higher = expeditions take longer minimum", "Lower = faster minimum", "NPCs", "Sects", "Tasks");
        add("sects.taskExpeditionMaxDays", "Expedition Max Days", "Maximum duration for sect expedition tasks.",
            "Higher = expeditions can take longer", "Lower = capped shorter", "NPCs", "Sects", "Tasks");

        // ── Loot ──
        add("loot.rolls.ruinedVanillaMin", "Ruined Vanilla Rolls Min", "Minimum number of loot rolls for ruined (broken) vanilla chests. Vanilla chests are standard Minecraft chests found in dungeons, strongholds, etc. 'Ruined' means the chest structure is partially damaged. More rolls = more items generated.",
            "More rolls = more items in ruined chests", "Fewer rolls = less loot", "World", "Loot", "Roll Counts");
        add("loot.rolls.ruinedVanillaMax", "Ruined Vanilla Rolls Max", "Maximum loot rolls for ruined vanilla chests. The actual roll count is random between min and max.",
            "More rolls = potentially more items", "Fewer = less loot", "World", "Loot", "Roll Counts");
        add("loot.rolls.completeVanillaMin", "Complete Vanilla Rolls Min", "Minimum rolls for complete (unbroken) vanilla chests. Complete chests are intact structures with better loot.",
            "More items in complete chests", "Fewer items", "World", "Loot", "Roll Counts");
        add("loot.rolls.completeVanillaMax", "Complete Vanilla Rolls Max", "Maximum rolls for complete vanilla chests. The actual roll count is random between min and max.",
            "More items in complete chests", "Fewer items", "World", "Loot", "Roll Counts");
        add("loot.rolls.ruinedCultivationMax", "Ruined Cultivation Rolls Max", "Maximum loot rolls for ruined cultivation-themed chests (e.g., ancient cultivator ruins). These contain cultivation items like spirit stones and pills.",
            "More cultivation items in ruined chests", "Fewer items", "World", "Loot", "Roll Counts");
        add("loot.rolls.completeCultivationMin", "Complete Cultivation Rolls Min", "Minimum rolls for complete cultivation chests. These have better cultivation loot than ruined ones.",
            "More cultivation items", "Fewer items", "World", "Loot", "Roll Counts");
        add("loot.rolls.completeCultivationMax", "Complete Cultivation Rolls Max", "Maximum rolls for complete cultivation chests.",
            "More cultivation items", "Fewer items", "World", "Loot", "Roll Counts");
        add("loot.weights.lowSpiritStone", "Low Spirit Stone Weight", "Drop weight for Low-tier spirit stones in cultivation chests. Spirit stones are cultivation currency that restores Qi. Higher weight = more likely to appear in loot. Weight is relative to all other items' weights.",
            "Higher = low spirit stones more common in chests", "Lower = rarer", "World", "Loot", "Item Weights");
        add("loot.weights.midSpiritStone", "Mid Spirit Stone Weight", "Drop weight for Mid-tier spirit stones. Mid-tier stones restore more Qi than low-tier.",
            "Higher = more common", "Lower = rarer", "World", "Loot", "Item Weights");
        add("loot.weights.highSpiritStone", "High Spirit Stone Weight", "Drop weight for High-tier spirit stones. These are valuable and restore significant Qi.",
            "Higher = more common", "Lower = rarer", "World", "Loot", "Item Weights");
        add("loot.weights.supremeSpiritStone", "Supreme Spirit Stone Weight", "Drop weight for Supreme-tier spirit stones. The highest tier, very valuable.",
            "Higher = more common (very generous)", "Lower = rarer (default)", "World", "Loot", "Item Weights");
        add("loot.weights.herb", "Herb Weight", "Drop weight for herbs in cultivation chests. Herbs are ingredients used in alchemy to craft pills.",
            "Higher = herbs more common in chests", "Lower = herbs rarer", "World", "Loot", "Item Weights");
        add("loot.weights.techniqueFragment", "Technique Fragment Weight", "Drop weight for technique fragments. These are pieces needed to learn cultivation techniques (passive skill books). Collect enough fragments to learn a technique.",
            "Higher = fragments more common (easier to learn techniques)", "Lower = rarer (harder to learn techniques)", "World", "Loot", "Item Weights");
        add("loot.weights.zhujiDanRuined", "Zhuji Dan Weight (Ruined)", "Drop weight for Zhuji Dan (Foundation Building Pill) in ruined cultivation chests. This pill helps cultivators break through to the Foundation Building realm.",
            "Higher = foundation pills more common in ruined chests", "Lower = rarer", "World", "Loot", "Item Weights");
        add("loot.weights.zhujiDanComplete", "Zhuji Dan Weight (Complete)", "Drop weight for Zhuji Dan in complete cultivation chests. Complete chests have better loot tables.",
            "Higher = foundation pills more common", "Lower = rarer", "World", "Loot", "Item Weights");

        // ── Trials ──
        add("trials.heartDemonGreatRighteousVitalityMult", "Heart Demon Vitality (Great Righteous)", "Vitality multiplier for the Heart Demon boss when the player has Great Righteous morality. The Heart Demon is a boss that tests your morality - its strength scales with your alignment.",
            "Higher = boss has more health (harder)", "Lower = boss weaker (easier)", "System", "Trials", "Heart Demon");
        add("trials.heartDemonRighteousVitalityMult", "Heart Demon Vitality (Righteous)", "Vitality multiplier for Heart Demon with Righteous morality.",
            "Higher = harder", "Lower = easier", "System", "Trials", "Heart Demon");
        add("trials.heartDemonNeutralVitalityMult", "Heart Demon Vitality (Neutral)", "Vitality multiplier for Heart Demon with Neutral morality.",
            "Higher = harder", "Lower = easier", "System", "Trials", "Heart Demon");
        add("trials.heartDemonEvilVitalityMult", "Heart Demon Vitality (Evil)", "Vitality multiplier for Heart Demon with Evil morality.",
            "Higher = harder", "Lower = easier", "System", "Trials", "Heart Demon");
        add("trials.heartDemonGreatEvilVitalityMult", "Heart Demon Vitality (Great Evil)", "Vitality multiplier for Heart Demon with Great Evil morality. The strongest heart demon.",
            "Higher = extremely hard boss", "Lower = easier", "System", "Trials", "Heart Demon");
        add("trials.innerWorldPlatformDiameter", "Inner World Platform Diameter", "Diameter (in blocks) of the platform in the Inner World trial dimension.",
            "Larger platform = more room to fight", "Smaller = more cramped", "System", "Trials", "Inner World");
        add("trials.innerWorldPlatformY", "Inner World Platform Y", "Y-level (height) of the platform in the Inner World trial.",
            "Higher = platform is higher up", "Lower = platform is lower", "System", "Trials", "Inner World");
        add("trials.innerWorldSoulWoundTicks", "Inner World Soul Wound Ticks", "Duration (in ticks) of the Soul Wound debuff after failing the Inner World trial.",
            "Longer = debuff lasts longer", "Shorter = recovers faster", "System", "Trials", "Inner World");
        add("trials.innerWorldFailureHealthPenaltyPercent", "Failure Health Penalty %", "Percentage of max health lost as penalty when failing the Inner World trial.",
            "Higher = bigger HP loss on failure", "Lower = smaller penalty", "System", "Trials", "Inner World");
        add("trials.innerWorldTimeStasisDuration", "Time Stasis Duration", "Duration of time stasis (paused time) during the Inner World trial.",
            "Longer stasis = more frozen time", "Shorter = less stasis", "System", "Trials", "Inner World");

        // ── Qi System ──
        add("qiSystem.playerConsumer.attractionRadius", "Qi Attraction Radius", "Radius (in blocks) within which the player attracts Qi orbs. Qi orbs are generated by spirit veins and plants.",
            "Larger radius = pull Qi from further away", "Smaller = must be closer to Qi sources", "System", "Qi System", "Player Consumer");
        add("qiSystem.playerConsumer.meditationRangeBonus", "Meditation Range Bonus", "Bonus attraction range when meditating. Meditation increases your Qi gathering range.",
            "Larger bonus = meditation pulls Qi from further away", "Smaller = less bonus", "System", "Qi System", "Player Consumer");
        add("qiSystem.playerConsumer.meditationEfficiencyBonus", "Meditation Efficiency Bonus", "Bonus Qi gathering efficiency when meditating. Higher = faster Qi gain while meditating.",
            "Higher = meditate to gain Qi much faster", "Lower = smaller bonus", "System", "Qi System", "Player Consumer");
        add("qiSystem.qiShield.qiPerDamage", "Qi Shield Qi/Damage", "Qi consumed per point of damage blocked by the Qi shield. Lower = shield is more efficient.",
            "Higher = shield drains Qi faster when hit", "Lower = shield blocks more damage per Qi", "System", "Qi System", "Qi Shield");
        add("qiSystem.qiShield.perfectReduction", "Qi Shield Perfect Reduction", "Damage reduction percentage when the Qi shield perfectly blocks. At 1.0 (100%), all damage is absorbed.",
            "Higher = perfect blocks absorb all damage", "Lower = some damage still gets through", "System", "Qi System", "Qi Shield");
        add("qiSystem.spiritStoneOre.maxQiLow", "Qi Stone Ore Max Qi (Low)", "Max Qi storage of Low-tier Qi Stone Ore blocks. These ores generate Qi that can be harvested.",
            "More Qi = ore lasts longer before depleting", "Less Qi = depletes faster", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.maxQiMid", "Qi Stone Ore Max Qi (Mid)", "Max Qi of Mid-tier Qi Stone Ore.",
            "More Qi", "Less Qi", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.maxQiHigh", "Qi Stone Ore Max Qi (High)", "Max Qi of High-tier Qi Stone Ore.",
            "More Qi", "Less Qi", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.maxQiSupreme", "Qi Stone Ore Max Qi (Supreme)", "Max Qi of Supreme-tier Qi Stone Ore.",
            "More Qi", "Less Qi", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.maxQiSpring", "Spirit Vein Spring Ore Max Qi", "Max Qi of Spirit Vein Spring Qi Stone Ore. The richest ore type.",
            "Enormous Qi = very long-lasting", "Less Qi", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.regenLow", "Qi Stone Ore Regen (Low)", "Qi regeneration rate per tick for Low-tier Qi Stone Ore. Determines how fast depleted ore refills.",
            "Higher = ore refills faster", "Lower = slower refill", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.regenMid", "Qi Stone Ore Regen (Mid)", "Regen rate for Mid-tier ore.",
            "Higher = faster refill", "Lower = slower", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.regenHigh", "Qi Stone Ore Regen (High)", "Regen rate for High-tier ore.",
            "Higher = faster", "Lower = slower", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.regenSupreme", "Qi Stone Ore Regen (Supreme)", "Regen rate for Supreme-tier ore.",
            "Higher = faster", "Lower = slower", "System", "Qi System", "Spirit Stone Ore");
        add("qiSystem.spiritStoneOre.regenSpring", "Spirit Vein Spring Ore Regen", "Regen rate for Spirit Vein Spring ore.",
            "Higher = very fast refill", "Lower = slower", "System", "Qi System", "Spirit Stone Ore");

        // ── Passive Spells ──
        add("passiveSpells.slowRegenInterval", "Slow Regen Interval", "Interval (in ticks) between slow regeneration heals. Slow Regen passively restores health using Qi.",
            "Lower = heals more often", "Higher = heals less often", "System", "Passive Spells", "Slow Regen");
        add("passiveSpells.slowRegenQiCost", "Slow Regen Qi Cost", "Qi consumed per slow regen heal.",
            "Higher = costs more Qi per heal", "Lower = cheaper", "System", "Passive Spells", "Slow Regen");
        add("passiveSpells.biguInterval", "Bigu Interval", "Interval between Bigu (fasting) checks. Bigu prevents hunger by consuming Qi instead of food.",
            "Lower = checks hunger more often", "Higher = less frequent", "System", "Passive Spells", "Bigu");
        add("passiveSpells.biguQiCost", "Bigu Qi Cost", "Qi consumed per Bigu check to prevent hunger.",
            "Higher = costs more Qi to stay fed", "Lower = cheaper", "System", "Passive Spells", "Bigu");
        add("passiveSpells.biguSaturation", "Bigu Saturation", "Saturation level restored by Bigu. Higher = more hunger filled per activation.",
            "Higher = fully prevents hunger", "Lower = partially fills hunger", "System", "Passive Spells", "Bigu");
        add("passiveSpells.qiMendingInterval", "Qi Mending Interval", "Interval between Qi Mending checks. Qi Mending passively repairs item durability using Qi.",
            "Lower = repairs more often", "Higher = less frequent", "System", "Passive Spells", "Qi Mending");
        add("passiveSpells.qiMendingQiPerDurability", "Qi Mending Qi/Durability", "Qi consumed per point of durability repaired.",
            "Higher = repairs cost more Qi", "Lower = cheaper repairs", "System", "Passive Spells", "Qi Mending");
        add("passiveSpells.qiFlightDrainInterval", "Qi Flight Drain Interval", "Interval between Qi drain ticks while using Qi Flight. Qi Flight lets you fly using Qi.",
            "Lower = drains Qi more frequently", "Higher = drains less often", "System", "Passive Spells", "Qi Flight");
        add("passiveSpells.qiFlightDrainPerSecond", "Qi Flight Drain/sec", "Qi consumed per second while flying with Qi Flight.",
            "Higher = flight costs more Qi per second", "Lower = cheaper flight", "System", "Passive Spells", "Qi Flight");
        add("passiveSpells.qiFlightBaseSpeed", "Qi Flight Base Speed", "Base flight speed when using Qi Flight.",
            "Higher = fly faster", "Lower = fly slower", "System", "Passive Spells", "Qi Flight");
        add("passiveSpells.itemAttractionRadius", "Item Attraction Radius", "Radius within which the Item Attraction passive pulls dropped items toward you.",
            "Larger = pulls items from further away", "Smaller = must be closer", "System", "Passive Spells", "Item Attraction");
        add("passiveSpells.itemAttractionQiPerItem", "Item Attraction Qi/Item", "Qi consumed per item attracted.",
            "Higher = attracting items costs more Qi", "Lower = cheaper", "System", "Passive Spells", "Item Attraction");
        add("passiveSpells.treasureSeizingRadius", "Treasure Seizing Radius", "Radius within which Treasure Seizing pulls entire item stacks toward you. More powerful than Item Attraction.",
            "Larger = grabs stacks from further away", "Smaller = shorter range", "System", "Passive Spells", "Treasure Seizing");
        add("passiveSpells.treasureSeizingQiPerStack", "Treasure Seizing Qi/Stack", "Qi consumed per item stack seized.",
            "Higher = costs more Qi per stack", "Lower = cheaper", "System", "Passive Spells", "Treasure Seizing");
        add("passiveSpells.treasureSeizingStacksPerSecond", "Treasure Seizing Stacks/sec", "Maximum number of item stacks seized per second.",
            "Higher = grabs stacks faster", "Lower = slower grabbing", "System", "Passive Spells", "Treasure Seizing");

        // ── Effects ──
        add("effects.bloodBerserkAttackSpeedMult", "Blood Berserk Attack Speed Mult", "Attack speed multiplier when under the Blood Berserk effect. Blood Berserk is a buff that increases combat speed at a cost.",
            "Higher = attack much faster", "Lower = less speed boost", "System", "Effects", "Blood Berserk");
        add("effects.bloodBerserkMoveSpeedMult", "Blood Berserk Move Speed Mult", "Movement speed multiplier when under Blood Berserk.",
            "Higher = move faster", "Lower = less speed boost", "System", "Effects", "Blood Berserk");
        add("effects.daoHeartWoundAttackPenalty", "Dao Heart Wound Attack Penalty", "Attack penalty when suffering from Dao Heart Wound (caused by failed tribulations). Reduces your attack power.",
            "Higher penalty = attacks much weaker", "Lower = less attack reduction", "System", "Effects", "Dao Heart Wound");
        add("effects.daoHeartWoundMoveSpeedPenalty", "Dao Heart Wound Move Speed Penalty", "Movement speed penalty from Dao Heart Wound.",
            "Higher = move much slower", "Lower = less speed reduction", "System", "Effects", "Dao Heart Wound");
        add("effects.shatterArmorArmorPenalty", "Shatter Armor Armor Penalty", "Armor reduction when afflicted with Shatter Armor. Reduces your armor stat.",
            "Higher penalty = less armor (more vulnerable)", "Lower = less armor loss", "System", "Effects", "Shatter Armor");
        add("effects.shatterArmorToughnessPenalty", "Shatter Armor Toughness Penalty", "Toughness reduction from Shatter Armor.",
            "Higher = less toughness (more vulnerable)", "Lower = less toughness loss", "System", "Effects", "Shatter Armor");
        add("effects.inverseMarkDurationTicks", "Inverse Five Elements Mark Duration", "Duration (in ticks) of the Inverse Five Elements mark. This mark inverts elemental damage relationships.",
            "Longer = mark lasts longer", "Shorter = expires faster", "System", "Effects", "Inverse Five Elements");
        add("effects.inverseBaseFiveElementDmgMult", "Inverse Five Element Damage Mult", "Base damage multiplier when Inverse Five Elements is active. Element strengths/weaknesses are inverted.",
            "Higher = inverted elements hit harder", "Lower = weaker", "System", "Effects", "Inverse Five Elements");
        add("effects.inverseBaseFiveElementCostMult", "Inverse Five Element Cost Mult", "Base Qi cost multiplier when Inverse Five Elements is active.",
            "Higher = spells cost more Qi while inverted", "Lower = cheaper", "System", "Effects", "Inverse Five Elements");
        add("effects.inverseStackDamagePerLayer", "Inverse Stack Damage/Layer", "Additional damage per stack layer of Inverse Five Elements. The effect stacks, getting stronger.",
            "Higher = each stack adds more damage", "Lower = less per stack", "System", "Effects", "Inverse Five Elements");
        add("effects.inverseStackCostReductionPerLayer", "Inverse Stack Cost Reduction/Layer", "Qi cost reduction per stack layer of Inverse Five Elements.",
            "Higher = each stack reduces Qi cost more", "Lower = less reduction", "System", "Effects", "Inverse Five Elements");

        // ── Morality ──
        add("morality.neutralMin", "Neutral Min", "Minimum morality value for Neutral alignment. Below this = Evil alignment. Morality ranges from negative (evil) to positive (righteous).",
            "Higher = Neutral range starts higher (more easily Evil)", "Lower = more room for Neutral", "System", "Morality", "Thresholds");
        add("morality.neutralMax", "Neutral Max", "Maximum morality value for Neutral alignment. Above this = Righteous.",
            "Higher = more room for Neutral", "Lower = Righteous starts earlier", "System", "Morality", "Thresholds");
        add("morality.righteousMin", "Righteous Min", "Minimum morality for Righteous alignment. Must be above Neutral Max.",
            "Higher = harder to be Righteous", "Lower = easier to be Righteous", "System", "Morality", "Thresholds");
        add("morality.evilMax", "Evil Max", "Maximum morality for Evil alignment. Must be below Neutral Min.",
            "Higher = Evil range is larger", "Lower = Evil is more extreme only", "System", "Morality", "Thresholds");
        add("morality.greatRighteousMin", "Great Righteous Min", "Minimum morality for Great Righteous alignment. The highest positive morality tier.",
            "Higher = harder to achieve Great Righteous", "Lower = easier", "System", "Morality", "Thresholds");
        add("morality.greatEvilMax", "Great Evil Max", "Maximum morality for Great Evil alignment. The most extreme negative morality.",
            "Higher = Great Evil range is larger", "Lower = must be more extreme", "System", "Morality", "Thresholds");
        add("morality.tribulationDamageCoefficient", "Tribulation Damage Coefficient", "Coefficient that scales tribulation damage based on absolute morality. More extreme morality (very good or very evil) = harder tribulations.",
            "Higher = extreme morality causes much harder tribulations", "Lower = morality has less impact on tribulations", "System", "Morality", "Tribulation Scaling");
        add("morality.tribulationDamageMax", "Tribulation Damage Max", "Maximum tribulation damage cap from morality scaling. No matter how extreme your morality, tribulation damage is capped at this value.",
            "Higher = tribulations can be much harder", "Lower = caps tribulation difficulty", "System", "Morality", "Tribulation Scaling");

        // ── Lifespan Helper ──
        add("lifespan.startBoneAgeMin", "Start Bone Age Min", "Minimum starting bone age when a character is created. Bone age is the apparent physical age, not actual years lived.",
            "Higher = characters start older", "Lower = characters start younger", "System", "Lifespan", "");
        add("lifespan.startBoneAgeMax", "Start Bone Age Max", "Maximum starting bone age.",
            "Higher = can start older", "Lower = starts younger", "System", "Lifespan", "");
        add("lifespan.agePerDay", "Age Per Day", "How much bone age increases per day under normal conditions. 1.0 = 1 year per day.",
            "Higher = ages faster (less time to cultivate)", "Lower = ages slower (more time)", "System", "Lifespan", "");
        add("lifespan.agePerDayMeditating", "Age Per Day (Meditating)", "Bone age increase per day while meditating. Usually lower than normal to reward meditation.",
            "Higher = still ages while meditating", "Lower = meditation slows aging", "System", "Lifespan", "");
        add("lifespan.nearImmortalThreshold", "Near Immortal Threshold", "Bone age threshold at which aging dramatically slows down. After reaching this age, you age much slower.",
            "Higher = slow-aging kicks in later", "Lower = slow-aging starts earlier", "System", "Lifespan", "");
        add("lifespan.ordinaryDeathPenaltyYears", "Ordinary Death Penalty Years", "Years of bone age added as penalty when dying normally (not from old age). Dying makes you look older.",
            "Higher = dying ages you more", "Lower = less aging penalty from death", "System", "Lifespan", "");

        // ── UI: General ──
        add("client.general.removeVanillaDifficultyButton", "Remove Vanilla Difficulty Button",
            "If true, removes the vanilla Minecraft difficulty button from the Create World screen.\\n" +
            "When enabled, the difficulty button is hidden to avoid confusion with the mod's difficulty system.\\n" +
            "When disabled (default), the vanilla difficulty button remains in the Game tab.",
            "Enabled = vanilla button hidden", "Disabled = vanilla button shown", "UI", "Text & Scale", "");

        // ── UI: Text & Scale ──
        add("client.textScaling.textScale", "Text Scale", "Global text scale for the cultivation panel (opened with G key). 0.85 = default size, 1.0 = larger, 1.5 = 1.5x size. Increase for better readability.",
            "Higher = larger text (easier to read)", "Lower = smaller text (fits more on screen)", "UI", "Text & Scale", "");
        add("client.textScaling.textScaleEn", "English Text Scale", "Text scale specifically for English text in the panel. Can be set differently from Chinese text scale. 0.7 = default.",
            "Higher = English text larger", "Lower = English text smaller", "UI", "Text & Scale", "");
        add("client.textScaling.hudScale", "HUD Scale", "Scale of the in-game HUD (top-left cultivation info display). 1.0 = default.",
            "Higher = HUD is larger", "Lower = HUD is smaller (less screen space used)", "UI", "Text & Scale", "");
        add("client.textScaling.breakthroughRowScalePercent", "Breakthrough Row Scale %", "Text scale percentage for the breakthrough row in the panel. 70 = 70% of normal size.",
            "Higher = breakthrough text larger", "Lower = smaller", "UI", "Text & Scale", "");
        add("client.textScaling.filterTextScalePercent", "Filter Chip Text Scale %", "Text scale percentage for filter chips in the panel. 65 = 65% of normal size.",
            "Higher = filter text larger", "Lower = smaller", "UI", "Text & Scale", "");
        add("client.textScaling.textWeight", "Text Weight / Stroke Thickness",
            "Controls the weight (thickness) of text rendering in the cultivation panel.\\n" +
            "0 = Thin (light) - Thinner strokes, good for compact displays. May appear blurry on some screens.\\n" +
            "1 = Normal - Default Minecraft text weight. Balanced readability.\\n" +
            "2 = Bold - Thicker strokes, better readability on high-resolution displays.\\n" +
            "3 = Extra Bold - Heaviest weight, maximum readability. May look too thick at small sizes.\\n" +
            "Use this to fix blurry or hard-to-read text. Higher values make text sharper and more visible.",
            "Higher = thicker, bolder text (sharper)", "Lower = thinner, lighter text (may be blurry)",
            "UI", "Text & Scale", "");
        add("client.textScaling.textShadowOffset", "Text Shadow Offset",
            "Controls the shadow offset beneath text in the cultivation panel.\\n" +
            "0.0 = No shadow (flat text, cleanest look, may be hard to read on bright backgrounds).\\n" +
            "1.0 = Full shadow (default Minecraft style, maximum contrast).\\n" +
            "2.0 = Double shadow (very heavy shadow, dramatic look).\\n" +
            "Lower values reduce the blurry shadow effect that some users find distracting.\\n" +
            "Higher values increase text contrast against the background.",
            "Higher = more shadow (more contrast)", "Lower = less shadow (cleaner, flatter look)",
            "UI", "Text & Scale", "");
        add("client.textScaling.textAntiAliasing", "Text Anti-Aliasing",
            "If true, enables text anti-aliasing for smoother, less blurry text edges.\\n" +
            "Anti-aliasing smooths the jagged edges of text characters, making them appear cleaner.\\n" +
            "May slightly impact performance on older GPUs.\\n" +
            "Recommended for high-resolution displays where text appears pixelated.",
            "Enabled = smoother text edges", "Disabled = sharper but potentially jagged edges",
            "UI", "Text & Scale", "");
        add("client.textScaling.textLineSpacing", "Text Line Spacing",
            "Extra spacing between text lines in the cultivation panel, in pixels.\\n" +
            "0 = Default (compact, lines close together).\\n" +
            "2 = More spacious (easier to read, less cramped).\\n" +
            "5 = Very spacious (maximum readability, takes more vertical space).\\n" +
            "Increase if text lines overlap or are hard to distinguish from each other.\\n" +
            "Especially useful when using larger text scales.",
            "Higher = more space between lines (easier to read)", "Lower = less space (more compact)",
            "UI", "Text & Scale", "");
        add("client.textScaling.autoFitPanel", "Auto-Fit Panel to Text",
            "If true, the cultivation panel automatically adjusts its size to contain all text within bounds.\\n" +
            "This prevents text from breaking panel boundaries when text scale is increased.\\n" +
            "When enabled, increasing text scale will also increase panel size proportionally.\\n" +
            "When disabled, panel size stays fixed and text may overflow if scale is too high.\\n" +
            "Recommended: enabled (true) for best synchronization between text and panel size.",
            "Enabled = panel grows with text (no overflow)", "Disabled = fixed panel size (text may overflow)",
            "UI", "Text & Scale", "");

        // ── UI: HUD Layout ──
        add("client.hud.visible", "HUD Visible", "Show or hide the in-game cultivation HUD. When hidden, the top-left cultivation info is not displayed.",
            "No effect (boolean)", "No effect (boolean)", "UI", "HUD Layout", "");
        add("client.hud.x", "HUD X Position", "X (horizontal) position of the HUD on screen. 0 = far left. Increase to move right.",
            "Higher = HUD moves right", "Lower = HUD moves left", "UI", "HUD Layout", "");
        add("client.hud.y", "HUD Y Position", "Y (vertical) position of the HUD on screen. 0 = top. Increase to move down.",
            "Higher = HUD moves down", "Lower = HUD moves up", "UI", "HUD Layout", "");
        add("client.hud.barWidth", "HUD Bar Width", "Width of the Qi and cultivation bars in the HUD, in pixels.",
            "Wider bars = more visible progress", "Narrower = more compact", "UI", "HUD Layout", "");
        add("client.hud.portraitSize", "Portrait Size", "Size of the portrait icon in the HUD, in pixels.",
            "Larger = bigger portrait", "Smaller = more compact", "UI", "HUD Layout", "");
        add("client.hud.enableBarHud", "Enable Bar HUD",
            "If true, replaces vanilla Minecraft hearts/food bar with a bar-style HUD matching the cultivation mod aesthetic.\\n" +
            "The bar HUD shows health, hunger, and air as sleek bars instead of icons.\\n" +
            "When disabled, the vanilla Minecraft HUD is used instead.",
            "Enabled = cultivation-style bar HUD", "Disabled = vanilla Minecraft HUD", "UI", "HUD Layout", "");
        add("client.hud.healthBarTop", "Health Bar Top Color",
            "Top gradient color of the health bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");
        add("client.hud.healthBarBottom", "Health Bar Bottom Color",
            "Bottom gradient color of the health bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");
        add("client.hud.hungerBarTop", "Hunger Bar Top Color",
            "Top gradient color of the hunger bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");
        add("client.hud.hungerBarBottom", "Hunger Bar Bottom Color",
            "Bottom gradient color of the hunger bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");
        add("client.hud.airBarTop", "Air Bar Top Color",
            "Top gradient color of the air (breath) bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");
        add("client.hud.airBarBottom", "Air Bar Bottom Color",
            "Bottom gradient color of the air (breath) bar in the bar-style HUD.",
            "Change color = different appearance", "Change color = different appearance", "UI", "HUD Layout", "");

        // ── UI: Panel Size ──
        add("client.panel.width", "Panel Width", "Width of the main cultivation panel (opened with G key) in pixels.",
            "Wider = more content visible", "Narrower = less screen space used", "UI", "Panel Size", "");
        add("client.panel.height", "Panel Height", "Height of the cultivation panel in pixels.",
            "Taller = more content visible", "Shorter = less screen space", "UI", "Panel Size", "");

        // ── UI: Element Positioning ──
        add("client.elementPos.realmNameX", "Realm Name X Offset",
            "X (horizontal) offset for the realm name text within the cultivation panel.\\n" +
            "0 = default position. Positive = move right. Negative = move left.\\n" +
            "Use this to fine-tune the position of the realm name (e.g., 'Qi Refining').\\n" +
            "Useful when adjusting text scale causes overlap with other elements.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.realmNameY", "Realm Name Y Offset",
            "Y (vertical) offset for the realm name text within the cultivation panel.\\n" +
            "0 = default position. Positive = move down. Negative = move up.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");
        add("client.elementPos.qiBarX", "Qi Bar X Offset",
            "X offset for the Qi bar within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the Qi bar left or right.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.qiBarY", "Qi Bar Y Offset",
            "Y offset for the Qi bar within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the Qi bar up or down.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");
        add("client.elementPos.cultBarX", "Cultivation Bar X Offset",
            "X offset for the cultivation progress bar within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the cultivation bar left or right.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.cultBarY", "Cultivation Bar Y Offset",
            "Y offset for the cultivation progress bar within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the cultivation bar up or down.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");
        add("client.elementPos.spellGridX", "Spell Grid X Offset",
            "X offset for the spell grid within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the entire spell grid left or right.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.spellGridY", "Spell Grid Y Offset",
            "Y offset for the spell grid within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the entire spell grid up or down.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");
        add("client.elementPos.infoTextX", "Info Text X Offset",
            "X offset for the info text block (Bone Age, Lifespan, Spirit Root, etc.) within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the info text block left or right.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.infoTextY", "Info Text Y Offset",
            "Y offset for the info text block within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the info text block up or down.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");
        add("client.elementPos.portraitX", "Portrait X Offset",
            "X offset for the portrait icon within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the portrait left or right.",
            "Positive = move right", "Negative = move left", "UI", "Element Pos", "");
        add("client.elementPos.portraitY", "Portrait Y Offset",
            "Y offset for the portrait icon within the cultivation panel.\\n" +
            "0 = default position. Adjust to move the portrait up or down.",
            "Positive = move down", "Negative = move up", "UI", "Element Pos", "");

        // ── UI: Spell Grid ──
        add("client.spellGrid.cellSize", "Spell Cell Size", "Size of each spell cell in the learned spell grid, in pixels.",
            "Larger = bigger spell slots", "Smaller = more compact grid", "UI", "Spell Grid", "");
        add("client.spellGrid.iconSize", "Spell Icon Size", "Size of spell icons within each cell, in pixels.",
            "Larger = bigger icons", "Smaller = smaller icons", "UI", "Spell Grid", "");
        add("client.spellGrid.columns", "Spell Grid Columns", "Number of columns in the learned spell grid.",
            "More columns = more spells visible per row", "Fewer = narrower grid", "UI", "Spell Grid", "");
        add("client.spellGrid.rows", "Spell Grid Rows", "Number of rows in the learned spell grid.",
            "More rows = more spells visible", "Fewer = less spells visible", "UI", "Spell Grid", "");

        // ── UI: Status Bar ──
        add("client.statusBar.rowHeight", "Status Bar Row Height", "Row height for status bars in the panel, in pixels.",
            "Taller rows = thicker bars", "Shorter = more compact", "UI", "Panel Size", "");

        // ── UI: Colors ──
        add("client.colors.hudText", "HUD Text Color", "Default text color for the in-game HUD. Use the color preset dropdown to pick a named color.",
            "Different color = changes HUD text appearance", "Different color", "UI", "Colors", "Text Colors");
        add("client.colors.realmText", "Realm Name Text Color", "Color of the realm name text in the cultivation panel.",
            "Different color = realm name changes color", "Different color", "UI", "Colors", "Text Colors");
        add("client.colors.qiBarTop", "Qi Bar Top Color", "Top gradient color of the Qi bar. The Qi bar shows your current Qi level.",
            "Different color = changes Qi bar top", "Different color", "UI", "Colors", "Bar Colors");
        add("client.colors.qiBarBottom", "Qi Bar Bottom Color", "Bottom gradient color of the Qi bar.",
            "Different color = changes Qi bar bottom", "Different color", "UI", "Colors", "Bar Colors");
        add("client.colors.cultBarTop", "Cultivation Bar Top Color", "Top gradient color of the cultivation progress bar.",
            "Different color = changes cultivation bar top", "Different color", "UI", "Colors", "Bar Colors");
        add("client.colors.cultBarBottom", "Cultivation Bar Bottom Color", "Bottom gradient color of the cultivation progress bar.",
            "Different color = changes cultivation bar bottom", "Different color", "UI", "Colors", "Bar Colors");
        add("client.colors.goldText", "Gold Text Color", "Gold-colored text used for headings and emphasis in the panel.",
            "Different color = headings change color", "Different color", "UI", "Colors", "Text Colors");
        add("client.colors.inkBlack", "Ink Black Text Color", "Primary text color (ink black) for main panel content.",
            "Different color = main text changes color", "Different color", "UI", "Colors", "Text Colors");
        add("client.colors.inkSoft", "Soft Ink Text Color", "Secondary text color (soft ink) for less important text.",
            "Different color = secondary text changes color", "Different color", "UI", "Colors", "Text Colors");
        add("client.colors.vermillion", "Vermillion Accent Color", "Vermillion (red-orange) accent color used for special highlights.",
            "Different color = accent changes color", "Different color", "UI", "Colors", "Accents");
        add("client.colors.borderLight", "Border Light Color", "Light border color for panels and frames.",
            "Different color = light borders change", "Different color", "UI", "Colors", "Borders");
        add("client.colors.borderDark", "Border Dark Color", "Dark border color for panels and frames.",
            "Different color = dark borders change", "Different color", "UI", "Colors", "Borders");

        // ── UI: Background ──
        add("client.background.pageColor", "Page Background Color", "Background color of the main page/backdrop of the cultivation panel.",
            "Different color = panel background changes", "Different color", "UI", "Background", "");
        add("client.background.panelColor", "Inner Panel Background Color", "Background color of inner panels within the cultivation panel.",
            "Different color = inner panels change", "Different color", "UI", "Background", "");
        add("client.background.opacity", "Background Opacity", "Opacity of the panel background. 1.0 = fully opaque (solid), 0.5 = semi-transparent, 0.0 = invisible.",
            "Higher = more solid/opaque background", "Lower = more transparent (see-through)", "UI", "Background", "");

        // ── UI: Accessibility ──
        add("client.accessibility.highContrastMode", "High Contrast Mode", "If true, enables high-contrast colors for better readability. Useful for visually impaired players or bright screens.",
            "No effect (boolean)", "No effect (boolean)", "UI", "Accessibility", "");
    }

    // ── Helper methods for common patterns ──
    private static void addL(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group, String keywords) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of(keywords.split(" ")));
    }
    private static void addS(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("shield", "qi", "reduction", "damage", "absorb", key.split("\\.")[2]));
    }
    private static void addT(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("tribulation", "damage", "strike", "heaven", key.split("\\.")[2]));
    }
    private static void addW(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("npc", "weight", "spawn", "realm", key.split("\\.")[2]));
    }
    private static void addD(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("qi", "density", "biome", key.split("\\.")[1]));
    }
    private static void addP(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("pill", "qi", key.split("\\.")[1]));
    }
    private static void addA(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("alchemy", "xp", "gain", key.split("\\.")[1]));
    }
    private static void addR(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("refining", "xp", "gain", key.split("\\.")[1]));
    }
    private static void addSV(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        add(key, name, desc, larger, smaller, tab, sub, group, List.of("spirit", "vein", "max", "qi", key.split("\\.")[1]));
    }
    /** Short form for Golden Core Dao entries - auto-generates keywords from the key. */
    private static void add(String key, String name, String desc, String larger, String smaller, String tab, String sub, String group) {
        List<String> kw = new ArrayList<>();
        for (String part : key.split("(?=[A-Z])|\\.")) {
            if (part.length() > 1) kw.add(part.toLowerCase());
        }
        add(key, name, desc, larger, smaller, tab, sub, group, kw);
    }

    /** Helper to add an entry. */
    private static void add(String key, String displayName, String description,
                            String largerEffect, String smallerEffect,
                            String topTab, String subTab, String group,
                            List<String> keywords) {
        ENTRIES.put(key, new ConfigEntryInfo(key, displayName, description,
                largerEffect, smallerEffect, topTab, subTab, group, keywords));
    }

    /** Get info for a config key, or null if not registered. */
    public static ConfigEntryInfo get(String key) {
        return ENTRIES.get(key);
    }

    /** Get all entries matching a search query. */
    public static List<ConfigEntryInfo> search(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>(ENTRIES.values());
        }
        List<ConfigEntryInfo> results = new ArrayList<>();
        for (ConfigEntryInfo info : ENTRIES.values()) {
            if (info.matches(query)) {
                results.add(info);
            }
        }
        return results;
    }

    /** Get all entries in a specific tab/subtab. */
    public static List<ConfigEntryInfo> getInTab(String topTab, String subTab) {
        List<ConfigEntryInfo> results = new ArrayList<>();
        for (ConfigEntryInfo info : ENTRIES.values()) {
            if (info.topTab.equals(topTab) && (subTab == null || info.subTab.equals(subTab))) {
                results.add(info);
            }
        }
        return results;
    }

    /** Get all sub-tabs within a top-level tab. */
    public static List<String> getSubTabs(String topTab) {
        LinkedHashSet<String> tabs = new LinkedHashSet<>();
        for (ConfigEntryInfo info : ENTRIES.values()) {
            if (info.topTab.equals(topTab)) {
                tabs.add(info.subTab);
            }
        }
        return new ArrayList<>(tabs);
    }

    /** Get all top-level tabs. */
    public static List<String> getTopTabs() {
        LinkedHashSet<String> tabs = new LinkedHashSet<>();
        for (ConfigEntryInfo info : ENTRIES.values()) {
            tabs.add(info.topTab);
        }
        return new ArrayList<>(tabs);
    }
}
