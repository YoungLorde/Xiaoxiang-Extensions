package com.xiaoxiang.configext.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cultivation glossary for the tooltip keyword system.
 * Keywords found in tooltip text are underlined and colored.
 * Hovering a keyword for 3 seconds shows a popup with its definition.
 * Clicking solidifies the popup (pins it until clicked away).
 */
public class TooltipGlossary {

    // Keyword -> definition (ordered for consistent lookup)
    private static final Map<String, String> GLOSSARY = new LinkedHashMap<>();

    static {
        // ── Cultivation Realms ──
        GLOSSARY.put("Qi Refining", "The first realm of cultivation. Practitioners absorb ambient qi to refine their body and meridians.");
        GLOSSARY.put("Foundation Building", "The second realm. The cultivator builds a solid foundation of qi in their dantian, forming a qi lake.");
        GLOSSARY.put("Golden Core", "The third realm. The qi lake condenses into a golden core, dramatically increasing qi capacity and lifespan.");
        GLOSSARY.put("Nascent Soul", "The fourth realm. The golden core hatches into a nascent soul, granting the ability to survive body destruction.");
        GLOSSARY.put("Soul Formation", "The fifth realm. The nascent soul matures into a fully formed soul body.");
        GLOSSARY.put("Body Integration", "The sixth realm. Soul and body merge into one unified existence.");
        GLOSSARY.put("Tribulation Transcendence", "The seventh realm. The cultivator transcends heavenly tribulations.");
        GLOSSARY.put("Mahayana", "The eighth realm. The cultivator walks the Great Way, approaching immortality.");
        GLOSSARY.put("Loose Immortal", "A cultivator who failed their tribulation but survived by becoming a loose immortal. Powerful but vulnerable.");
        GLOSSARY.put("True Immortal", "The ninth and highest realm. The cultivator has achieved true immortality.");

        // ── Core Concepts ──
        GLOSSARY.put("Qi", "The fundamental energy of the universe. Cultivators absorb and refine qi to increase their power.");
        GLOSSARY.put("Dantian", "The energy center in the lower abdomen where cultivators store and refine qi.");
        GLOSSARY.put("Meridians", "The channels through which qi flows in the body. Must be opened and refined for cultivation.");
        GLOSSARY.put("Spirit Root", "An innate talent that determines a cultivator's affinity for elements and cultivation speed.");
        GLOSSARY.put("Physique", "An innate body constitution that grants special abilities or resistances.");
        GLOSSARY.put("Tribulation", "A heavenly trial that cultivators must survive to advance to higher realms. Involves lightning strikes.");
        GLOSSARY.put("Dao", "The Way; the fundamental principle of the universe. Cultivators seek to understand and embody the Dao.");
        GLOSSARY.put("Dao Foundation", "The foundation of a cultivator's understanding of the Dao. Determines their path and potential.");
        GLOSSARY.put("Realm", "A cultivator's overall stage of power (Qi Refining, Foundation Building, Golden Core, Nascent Soul, and so on). Advancing a realm requires a breakthrough.");
        GLOSSARY.put("Alchemy", "The art of refining pills from spirit herbs and materials. Alchemists brew pills that heal, restore qi, or aid breakthroughs.");
        GLOSSARY.put("Foundation", "Short for Foundation Building, the second cultivation realm - or generally, the base a cultivator's power and Dao understanding are built on.");
        GLOSSARY.put("Shatter Core", "A technique to break the golden core and attempt to form a nascent soul. Risky but powerful.");
        GLOSSARY.put("Heart Demon", "Inner demons that manifest during cultivation, especially at breakthroughs. Can cause deviation or death.");
        GLOSSARY.put("Inner World", "A personal dimension that high-realm cultivators can develop. Contains trials and resources.");

        // ── Items & Materials ──
        GLOSSARY.put("Spirit Stone", "A crystallized form of qi used as currency and cultivation fuel. Comes in tiers: low, mid, high, supreme.");
        GLOSSARY.put("Spirit Vein", "A natural formation that produces qi. Can be harvested for spirit stones and cultivation bonuses.");
        GLOSSARY.put("Pill", "A medicinal concoction that provides various effects: healing, qi recovery, breakthroughs, etc.");
        GLOSSARY.put("Zhuji Dan", "A foundation building pill that aids in breaking through to the Foundation Building realm.");
        GLOSSARY.put("Spell Book", "A book that teaches a cultivator a new spell. Single-use.");
        GLOSSARY.put("Technique Book", "A book that teaches a cultivation technique. Techniques provide passive bonuses.");
        GLOSSARY.put("Storage Bag", "A spatial item that provides extra inventory space. Size depends on tier.");
        GLOSSARY.put("Realm Token", "A token representing a cultivation realm. Used for various purposes including NPC interactions.");
        GLOSSARY.put("Formation", "A spatial array that produces effects in an area. Used for defense, gathering, traps, etc.");
        GLOSSARY.put("Talismans", "Paper-based items that store spells for one-time use.");

        // ── Combat & Skills ──
        GLOSSARY.put("Spell", "A technique that consumes qi to produce an effect: damage, healing, utility, etc.");
        GLOSSARY.put("Sword Aura", "A projection of sword intent that deals damage at range.");
        GLOSSARY.put("Qi Shield", "A protective barrier formed from qi. Reduces incoming damage.");
        GLOSSARY.put("Blood Spell", "A spell that consumes the cultivator's blood (HP) for increased power. Used by blood cultivators.");
        GLOSSARY.put("Divine Sense", "A perception ability that allows cultivators to sense their surroundings beyond normal sight.");

        // ── Social Systems ──
        GLOSSARY.put("Sect", "An organization of cultivators. Provides resources, protection, and cultivation guidance.");
        GLOSSARY.put("Identity", "The cultivator's background: martial, scholar, cultivator, or abandoned. Affects starting stats.");
        GLOSSARY.put("Morality", "A measure of a cultivator's alignment: righteous, neutral, or evil. Affects tribulation difficulty.");
        GLOSSARY.put("Lifespan", "How long a cultivator can live. Increases dramatically with each realm breakthrough.");
        GLOSSARY.put("NPC", "A non-player character: sect members, merchants, wandering cultivators, and other AI-controlled people in the world.");
        GLOSSARY.put("Qi Recovery", "The rate at which a cultivator's qi bar regenerates over time. Faster recovery lets spells and abilities be used more often.");

        // ── Elements ──
        GLOSSARY.put("Five Elements", "The five fundamental elements: Metal, Wood, Water, Fire, Earth. Each has strengths and weaknesses.");
        GLOSSARY.put("Spirit Root Rarity", "The rarity of a spirit root: Common, Uncommon, Rare, Epic, or Legendary. Rarer roots are stronger.");
        GLOSSARY.put("Heavenly Spirit Root", "The rarest single-element root. Extremely fast cultivation speed.");
        GLOSSARY.put("Dual Spirit Root", "A root with two elements. Balanced but slower than heavenly roots.");
        GLOSSARY.put("Mutant Spirit Root", "A root with mutated elements. Unique abilities but difficult to cultivate.");
    }

    /**
     * Find a glossary keyword in the given text.
     * Returns the keyword if found, null otherwise.
     * Longer keywords are checked first to avoid partial matches.
     */
    public static String findKeyword(String text) {
        if (text == null || text.isEmpty()) return null;
        String lower = text.toLowerCase();
        for (String keyword : GLOSSARY.keySet()) {
            if (lower.contains(keyword.toLowerCase())) {
                return keyword;
            }
        }
        return null;
    }

    /**
     * Find all glossary keywords in the given text.
     * Returns a list of keywords found, in order of appearance.
     */
    public static java.util.List<String> findAllKeywords(String text) {
        java.util.List<String> found = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) return found;
        String lower = text.toLowerCase();
        for (String keyword : GLOSSARY.keySet()) {
            if (lower.contains(keyword.toLowerCase())) {
                if (!found.contains(keyword)) found.add(keyword);
            }
        }
        return found;
    }

    /** Get the definition for a keyword. */
    public static String getDefinition(String keyword) {
        return GLOSSARY.get(keyword);
    }

    /** Get all keywords (for testing/debugging). */
    public static java.util.Set<String> getAllKeywords() {
        return GLOSSARY.keySet();
    }

    /** Color for keyword highlighting in tooltips. */
    public static final int KEYWORD_COLOR = 0xFF55FFFF; // Cyan with underline

    /** Color for the keyword popup background. Fully opaque (0xFF alpha) - was 0xF0 (94%),
     *  which combined with a paint-order bug (fixed in CustomConfigScreen) let later-drawn
     *  entry rows show through. Kept at max alpha now so it reads as a solid box like the
     *  vanilla-style entry-detail tooltip, matching it exactly rather than relying on blending. */
    public static final int POPUP_BG_COLOR = 0xFF181818;
    public static final int POPUP_BORDER_COLOR = 0xFF55FFFF;
    public static final int POPUP_TEXT_COLOR = 0xFFCCCCCC;
    public static final int POPUP_TITLE_COLOR = 0xFF55FFFF;
}
