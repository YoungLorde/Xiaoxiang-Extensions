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
     * Style a plain text string with cultivation-themed formatting codes.
     * Returns a string with \u00A7 formatting codes inserted.
     */
    public static String style(String text) {
        if (text == null || text.isEmpty()) return text;

        String result = text;
        // Apply keyword styling (case-insensitive, whole word)
        for (Map.Entry<Pattern, String> entry : KEYWORD_STYLES.entrySet()) {
            Matcher m = entry.getKey().matcher(result);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String replacement = entry.getValue() + m.group() + "\u00A7r";
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            m.appendTail(sb);
            result = sb.toString();
        }

        // Style numbers as cyan (but don't double-style numbers inside existing formatting)
        if (!result.contains("\u00A7")) {
            Matcher nm = NUMBER_PATTERN.matcher(result);
            StringBuffer nsb = new StringBuffer();
            while (nm.find()) {
                nm.appendReplacement(nsb, "\u00A7b" + nm.group() + "\u00A7r");
            }
            nm.appendTail(nsb);
            result = nsb.toString();
        }

        return result;
    }

    /**
     * Style text and return as a Component for rendering.
     */
    public static MutableComponent styleComponent(String text) {
        return Component.literal(style(text));
    }

    /**
     * Style only the display name (lighter touch - just colorize key terms).
     */
    public static String styleDisplayName(String name) {
        if (name == null || name.isEmpty()) return name;
        // Just apply keyword styling, no number styling for display names
        String result = name;
        for (Map.Entry<Pattern, String> entry : KEYWORD_STYLES.entrySet()) {
            Matcher m = entry.getKey().matcher(result);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String replacement = entry.getValue() + m.group() + "\u00A7r";
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            m.appendTail(sb);
            result = sb.toString();
        }
        return result;
    }
}
