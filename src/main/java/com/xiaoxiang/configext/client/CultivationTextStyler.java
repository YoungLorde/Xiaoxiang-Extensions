package com.xiaoxiang.configext.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Applies cultivation-themed colors and formatting to text based on keywords.
 * Used for display names, descriptions, tooltips, and tab descriptions.
 *
 * Color palette:
 * - Qi → jade green bold
 * - Realm/Cultivation → golden bold underlined
 * - Tribulation → cinnabar red bold
 * - Spirit Root/Physique → purple bold
 * - Spell/Technique → blue bold
 * - Sect/Formation → teal bold
 * - Pill/Alchemy → orange bold
 * - Lifespan/Age → silver bold
 * - Numbers → cyan
 * - Increase/Boost → green
 * - Decrease/Penalty → red
 */
public class CultivationTextStyler {

    // Keyword → formatting code mapping (checked in order, first match wins)
    private static final Map<Pattern, String> KEYWORD_STYLES = new LinkedHashMap<>();

    static {
        // Cultivation systems (bold + underlined + colored)
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\brealm\\b|\\bcultivation\\b"), "\u00A76\u00A7l\u00A7n"); // golden bold underlined
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\btribulation\\b"), "\u00A7c\u00A7l"); // cinnabar red bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bspirit root\\b|\\bspiritroot\\b"), "\u00A75\u00A7l"); // purple bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bphysique\\b"), "\u00A75\u00A7l"); // purple bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bspell\\b"), "\u00A79\u00A7l"); // blue bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\btechnique\\b"), "\u00A79\u00A7l"); // blue bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bsect\\b"), "\u00A73\u00A7l"); // teal bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bformation\\b"), "\u00A73\u00A7l"); // teal bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bpill\\b"), "\u00A76\u00A7l"); // orange bold (gold)
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\balchemy\\b"), "\u00A76\u00A7l"); // orange bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\brefin(?:e|ing)\\b"), "\u00A76\u00A7l"); // orange bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bqi\\b"), "\u00A7a\u00A7l"); // jade green bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\blifespan\\b|\\bbody age\\b|\\bbone age\\b|\\bage\\b"), "\u00A77\u00A7l"); // silver bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bspirit vein\\b|\\bspiritvein\\b"), "\u00A7b\u00A7l"); // aqua bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bspirit plant\\b"), "\u00A7a\u00A7l"); // green bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bidentity\\b"), "\u00A7d\u00A7l"); // light purple bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bfoundation\\b|\\bgolden core\\b|\\bdao\\b"), "\u00A76\u00A7l\u00A7n"); // golden bold underlined
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bnpc\\b|\\bwandering cultivator\\b"), "\u00A73\u00A7l"); // teal bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bbeast\\b"), "\u00A76\u00A7l"); // gold bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bbreakthrough\\b"), "\u00A7e\u00A7l"); // yellow bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\breincarnation\\b"), "\u00A7d\u00A7l"); // light purple bold

        // Action words
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bincrease\\b|\\bboost\\b|\\braise\\b|\\bamplify\\b"), "\u00A7a"); // green
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bdecrease\\b|\\bpenalty\\b|\\breduce\\b|\\bpenalty\\b"), "\u00A7c"); // red
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bmax\\b|\\bmaximum\\b"), "\u00A7e\u00A7l"); // yellow bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bmin\\b|\\bminimum\\b"), "\u00A77\u00A7l"); // silver bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bdamage\\b"), "\u00A7c\u00A7l"); // red bold
        KEYWORD_STYLES.put(Pattern.compile("(?i)\\bheal\\b|\\brecovery\\b"), "\u00A7a\u00A7l"); // green bold
    }

    // Number pattern for cyan coloring
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");

    /**
     * Reads ExtendedConfig.CLIENT_DESC_HIGHLIGHT_INTENSITY ("full" / "reduced" /
     * "off"), controlling how much of the styling below actually shows up -
     * see that field's own comment. Defaults to "full" (the original, only,
     * hardcoded behavior) if the config isn't loaded yet for any reason.
     */
    private static String highlightIntensity() {
        try {
            return com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_DESC_HIGHLIGHT_INTENSITY.get();
        } catch (Exception e) {
            return "full";
        }
    }

    /**
     * Style a plain text string with cultivation-themed formatting codes.
     * Returns a string with \u00A7 formatting codes inserted.
     *
     * "full" applies every matching keyword pattern - the original, always-on
     * behavior, which on a sentence with several common cultivation words
     * (e.g. "Qi required to ready the Buddha Fire Lotus spell. This is a
     * powerful fire spell.") bolds/colors most of the sentence rather than
     * highlighting anything in particular. "reduced" stops after the FIRST
     * keyword match - this is what this class's own doc comment already
     * claimed happened ("checked in order, first match wins"), which the
     * unconditional loop below never actually did until this intensity
     * setting was added. "off" applies no keyword styling at all.
     */
    public static String style(String text) {
        if (text == null || text.isEmpty()) return text;

        String intensity = highlightIntensity();
        if ("off".equalsIgnoreCase(intensity)) {
            return styleNumbers(text);
        }
        boolean reduced = "reduced".equalsIgnoreCase(intensity);

        String result = text;
        boolean styledOnce = false;
        // Apply keyword styling (case-insensitive, whole word)
        for (Map.Entry<Pattern, String> entry : KEYWORD_STYLES.entrySet()) {
            if (reduced && styledOnce) break;
            Matcher m = entry.getKey().matcher(result);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String replacement = entry.getValue() + m.group() + "\u00A7r";
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                if (reduced) {
                    styledOnce = true;
                    break; // only the first occurrence of this one keyword, then stop
                }
            }
            m.appendTail(sb);
            result = sb.toString();
        }

        // Style numbers as cyan (but don't double-style numbers inside existing formatting)
        if (!result.contains("\u00A7")) {
            result = styleNumbers(result);
        }

        return result;
    }

    /** Colors every bare number in the text cyan. Shared by style() and the "off" intensity. */
    private static String styleNumbers(String text) {
        Matcher nm = NUMBER_PATTERN.matcher(text);
        StringBuffer nsb = new StringBuffer();
        while (nm.find()) {
            nm.appendReplacement(nsb, "\u00A7b" + nm.group() + "\u00A7r");
        }
        nm.appendTail(nsb);
        return nsb.toString();
    }

    /**
     * Style text and return as a Component for rendering.
     */
    public static MutableComponent styleComponent(String text) {
        return Component.literal(style(text));
    }

    /**
     * Style only the display name (lighter touch - just colorize key terms).
     * Respects the same descHighlightIntensity setting as style() above, so
     * turning highlighting down/off applies everywhere text goes through this
     * class, not just descriptions.
     */
    public static String styleDisplayName(String name) {
        if (name == null || name.isEmpty()) return name;

        String intensity = highlightIntensity();
        if ("off".equalsIgnoreCase(intensity)) {
            return name;
        }
        boolean reduced = "reduced".equalsIgnoreCase(intensity);

        // Just apply keyword styling, no number styling for display names
        String result = name;
        boolean styledOnce = false;
        for (Map.Entry<Pattern, String> entry : KEYWORD_STYLES.entrySet()) {
            if (reduced && styledOnce) break;
            Matcher m = entry.getKey().matcher(result);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String replacement = entry.getValue() + m.group() + "\u00A7r";
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                if (reduced) {
                    styledOnce = true;
                    break;
                }
            }
            m.appendTail(sb);
            result = sb.toString();
        }
        return result;
    }
}
