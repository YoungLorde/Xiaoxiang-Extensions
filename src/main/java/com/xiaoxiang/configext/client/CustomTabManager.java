package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xiaoxiang.configext.api.ExpansionConfigRegistry;

/**
 * Manages custom tabs (user-defined tabs that aggregate configs from multiple categories)
 * and tab reordering (custom display order for top-level tabs).
 */
public class CustomTabManager {

    // Custom tab definition: name + list of config path prefixes
    public static class CustomTab {
        public String name;
        public List<String> pathPrefixes;

        /**
         * Optional real sub-tab structure: subTabName -> (sectionName -> the
         * path prefixes that belong to that section). Empty (the default) means
         * a flat tab with no sub-tabs - exactly the original behavior, so every
         * hand-built player tab is completely unaffected by this field's
         * existence. When non-empty, the tab gets a real sub-tab bar (one
         * button per key of this map) and, within each sub-tab, a section bar
         * (one button per key of that sub-tab's own map) - e.g. Realm
         * Expansion's "Expansion" tab registers a single "Layers" sub-tab whose
         * sections are its nine layered realms.
         */
        public LinkedHashMap<String, LinkedHashMap<String, List<String>>> subTabs = new LinkedHashMap<>();

        public CustomTab(String name, List<String> pathPrefixes) {
            this.name = name;
            this.pathPrefixes = pathPrefixes;
        }
    }

    private static final List<CustomTab> customTabs = new ArrayList<>();
    private static List<String> tabOrder = new ArrayList<>();

    // Default tab order (the built-in tabs)
    public static final List<String> DEFAULT_TABS = Arrays.asList(
        "Cultivation", "Spells & Combat", "Sects", "NPCs", "Beasts & Mobs",
        "World", "Crafting", "Trials", "Qi System", "Lifespan",
        "Effects & Morality", "Formations", "UI"
    );

    /** Load custom tabs and tab order from config. */
    public static void loadFromConfig() {
        // Load tab order
        try {
            String orderStr = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_TAB_ORDER.get();
            if (orderStr != null && !orderStr.isEmpty()) {
                tabOrder = new ArrayList<>(Arrays.asList(orderStr.split(",")));
                // Add any new default tabs that aren't in the saved order
                for (String tab : DEFAULT_TABS) {
                    if (!tabOrder.contains(tab)) tabOrder.add(tab);
                }
            } else {
                tabOrder = new ArrayList<>(DEFAULT_TABS);
            }
        } catch (Exception e) {
            // Config might not be loaded yet - use defaults
            tabOrder = new ArrayList<>(DEFAULT_TABS);
        }
        // Safety: ensure tabOrder is never empty
        if (tabOrder == null || tabOrder.isEmpty()) {
            tabOrder = new ArrayList<>(DEFAULT_TABS);
        }

        // Load custom tabs
        customTabs.clear();
        try {
            String json = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_CUSTOM_TABS.get();
            if (json != null && !json.isEmpty() && !json.equals("[]")) {
                parseCustomTabs(json);
            }
        } catch (Exception e) {
            System.err.println("[ConfigExt] Failed to parse custom tabs: " + e.getMessage());
        }
    }

    /** Save tab order to config. */
    public static void saveTabOrder() {
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_TAB_ORDER.set(String.join(",", tabOrder));
        } catch (Exception e) { /* ignore */ }
    }

    /** Save custom tabs to config. */
    public static void saveCustomTabs() {
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_CUSTOM_TABS.set(customTabsToJson());
        } catch (Exception e) { /* ignore */ }
    }

    /** Get the current tab order (including custom tabs at the end). */
    public static List<String> getTabOrder() {
        // Safety: ensure tabOrder is initialized
        if (tabOrder == null || tabOrder.isEmpty()) {
            tabOrder = new ArrayList<>(DEFAULT_TABS);
        }
        List<String> result = new ArrayList<>(tabOrder);
        for (CustomTab ct : customTabs) {
            if (!result.contains(ct.name)) result.add(ct.name);
        }
        return result;
    }

    /** Move a tab up in the order. */
    public static void moveTabUp(String tabName) {
        int idx = tabOrder.indexOf(tabName);
        if (idx > 0) {
            tabOrder.remove(idx);
            tabOrder.add(idx - 1, tabName);
            saveTabOrder();
        }
    }

    /** Move a tab down in the order. */
    public static void moveTabDown(String tabName) {
        int idx = tabOrder.indexOf(tabName);
        if (idx >= 0 && idx < tabOrder.size() - 1) {
            tabOrder.remove(idx);
            tabOrder.add(idx + 1, tabName);
            saveTabOrder();
        }
    }

    /** Reset tab order to default. */
    public static void resetTabOrder() {
        tabOrder = new ArrayList<>(DEFAULT_TABS);
        saveTabOrder();
    }

    /** Add a custom tab. */
    public static void addCustomTab(String name, List<String> pathPrefixes) {
        // Remove existing tab with same name
        customTabs.removeIf(ct -> ct.name.equals(name));
        customTabs.add(new CustomTab(name, pathPrefixes));
        if (!tabOrder.contains(name)) tabOrder.add(name);
        saveCustomTabs();
        saveTabOrder();
    }

    /**
     * Add a custom tab with a real sub-tab/section structure - e.g. Realm
     * Expansion's "Expansion" tab, whose "Layers" sub-tab has one section per
     * layered realm. subTabs maps subTabName -> (sectionName -> the path
     * prefixes that belong to that section). Pass an empty or null map to get
     * the same flat "All" behavior as the 2-arg overload.
     */
    public static void addCustomTab(String name, List<String> pathPrefixes,
            LinkedHashMap<String, LinkedHashMap<String, List<String>>> subTabs) {
        customTabs.removeIf(ct -> ct.name.equals(name));
        CustomTab tab = new CustomTab(name, pathPrefixes);
        if (subTabs != null) tab.subTabs = subTabs;
        customTabs.add(tab);
        if (!tabOrder.contains(name)) tabOrder.add(name);
        saveCustomTabs();
        saveTabOrder();
    }

    /** Remove a custom tab. */
    public static void removeCustomTab(String name) {
        customTabs.removeIf(ct -> ct.name.equals(name));
        tabOrder.remove(name);
        saveCustomTabs();
        saveTabOrder();
    }

    /**
     * Remove any custom tab whose path prefixes no longer match a single
     * currently-registered config value.
     *
     * A custom tab (whether hand-built by a player, or auto-created by an
     * expansion mod on startup - see ExpansionDiscovery/ExpansionConfigRegistry)
     * only has content because SOME config path currently starts with one of
     * its saved prefixes. If the mod that owned those paths gets uninstalled,
     * that backing disappears - but the tab's own entry was already saved to
     * disk from a previous session, so it would otherwise keep reappearing
     * forever, empty, with nothing behind it and no way for the player to
     * know why.
     *
     * This runs every time the config screen opens (right after
     * loadFromConfig(), so it always sees the current session's real set of
     * installed expansions - discovery/registration always finishes long
     * before the player ever opens this screen) and quietly drops any tab
     * that's gone stale, persisting the cleanup so it doesn't have to repeat.
     * A tab a player built by hand is treated exactly the same way as one an
     * expansion mod registered automatically - if none of its prefixes match
     * anything anymore, there's nothing left for it to show either way.
     */
    public static void pruneOrphanedTabs() {
        if (customTabs.isEmpty()) return;

        // ConfigValueAccessor.getAllPaths() covers the base mod's own config
        // values directly, plus expansion mods' values via a separate scan of
        // each expansion's live NightConfig tree. ExpansionConfigRegistry.getAllPaths()
        // is a second, independently-populated source of the same expansion
        // paths (built when each expansion registers). Checking both here -
        // a tab counts as backed if either source has a matching path - means
        // an expansion-registered tab (e.g. Realm Expansion's "Expansion" tab)
        // stays visible even if one of the two scanning mechanisms ever fails
        // for a particular expansion's config shape.
        java.util.List<String> allPaths = new ArrayList<>(ConfigValueAccessor.getAllPaths());
        allPaths.addAll(ExpansionConfigRegistry.getAllPaths());
        java.util.List<String> orphaned = new ArrayList<>();

        for (CustomTab ct : customTabs) {
            boolean hasBacking = false;
            for (String prefix : ct.pathPrefixes) {
                for (String path : allPaths) {
                    if (path.equals(prefix) || path.startsWith(prefix + ".")) {
                        hasBacking = true;
                        break;
                    }
                }
                if (hasBacking) break;
            }
            if (!hasBacking) orphaned.add(ct.name);
        }

        if (orphaned.isEmpty()) return;

        for (String name : orphaned) {
            customTabs.removeIf(ct -> ct.name.equals(name));
            tabOrder.remove(name);
        }
        saveCustomTabs();
        saveTabOrder();
    }

    /** Get all custom tabs. */
    public static List<CustomTab> getCustomTabs() {
        return customTabs;
    }

    /** Get a custom tab by name. */
    public static CustomTab getCustomTab(String name) {
        for (CustomTab ct : customTabs) {
            if (ct.name.equals(name)) return ct;
        }
        return null;
    }

    /** Check if a tab name is a custom tab. */
    public static boolean isCustomTab(String tabName) {
        return customTabs.stream().anyMatch(ct -> ct.name.equals(tabName));
    }

    /** Get path prefixes for a custom tab. */
    public static List<String> getCustomTabPaths(String tabName) {
        CustomTab ct = getCustomTab(tabName);
        return ct != null ? ct.pathPrefixes : new ArrayList<>();
    }

    /** Whether a custom tab has a real sub-tab structure (vs. the flat "All" default). */
    public static boolean hasSubTabs(String tabName) {
        CustomTab ct = getCustomTab(tabName);
        return ct != null && ct.subTabs != null && !ct.subTabs.isEmpty();
    }

    /** Get the ordered sub-tab names for a custom tab (empty list if it has none). */
    public static List<String> getCustomSubTabNames(String tabName) {
        CustomTab ct = getCustomTab(tabName);
        if (ct == null || ct.subTabs == null) return new ArrayList<>();
        return new ArrayList<>(ct.subTabs.keySet());
    }

    /** Get the ordered section names (e.g. per-realm names) within one of a custom tab's sub-tabs. */
    public static List<String> getCustomSubTabSections(String tabName, String subTabName) {
        CustomTab ct = getCustomTab(tabName);
        if (ct == null || ct.subTabs == null) return new ArrayList<>();
        LinkedHashMap<String, List<String>> sections = ct.subTabs.get(subTabName);
        if (sections == null) return new ArrayList<>();
        return new ArrayList<>(sections.keySet());
    }

    /** Get the path prefixes backing one specific section of one sub-tab of a custom tab. */
    public static List<String> getCustomSubTabSectionPrefixes(String tabName, String subTabName, String sectionName) {
        CustomTab ct = getCustomTab(tabName);
        if (ct == null || ct.subTabs == null) return new ArrayList<>();
        LinkedHashMap<String, List<String>> sections = ct.subTabs.get(subTabName);
        if (sections == null) return new ArrayList<>();
        List<String> prefixes = sections.get(sectionName);
        return prefixes != null ? prefixes : new ArrayList<>();
    }

    /** Get the union of every section's path prefixes within one sub-tab of a custom tab. */
    public static List<String> getCustomSubTabPrefixes(String tabName, String subTabName) {
        CustomTab ct = getCustomTab(tabName);
        List<String> result = new ArrayList<>();
        if (ct == null || ct.subTabs == null) return result;
        LinkedHashMap<String, List<String>> sections = ct.subTabs.get(subTabName);
        if (sections == null) return result;
        for (List<String> prefixes : sections.values()) {
            for (String prefix : prefixes) {
                if (!result.contains(prefix)) result.add(prefix);
            }
        }
        return result;
    }

    /**
     * Reverse lookup: given a custom tab and one exact config-path prefix
     * (one of the tab's own pathPrefixes), find which section - across all of
     * that tab's registered sub-tabs - owns that prefix, and return the
     * section's display name (e.g. "Golden Core"). Returns null when the tab
     * has no sub-tab structure, or the prefix isn't listed in any section -
     * the caller's existing fallback behavior then applies unchanged, so
     * every hand-built custom tab (no subTabs data) is completely unaffected.
     *
     * This is what lets CustomConfigScreen show one real section per realm
     * for an expansion-registered tab (e.g. Realm Expansion's "Expansion"
     * tab) instead of every realm collapsing into a single group, without
     * needing a whole new navigation tier: the config screen's existing
     * group bar already does exactly this job for standard tabs, so custom
     * tabs just need their groups sourced from real section names instead of
     * a generic "first path segment after the prefix" guess.
     */
    public static String getSectionNameForPrefix(String tabName, String prefix) {
        CustomTab ct = getCustomTab(tabName);
        if (ct == null || ct.subTabs == null) return null;
        for (LinkedHashMap<String, List<String>> sections : ct.subTabs.values()) {
            for (Map.Entry<String, List<String>> entry : sections.entrySet()) {
                if (entry.getValue().contains(prefix)) return entry.getKey();
            }
        }
        return null;
    }

    // ── JSON serialization ──

    private static String customTabsToJson() {
        if (customTabs.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < customTabs.size(); i++) {
            if (i > 0) sb.append(",");
            CustomTab ct = customTabs.get(i);
            sb.append("{\"name\":\"").append(escape(ct.name)).append("\",\"paths\":[");
            for (int j = 0; j < ct.pathPrefixes.size(); j++) {
                if (j > 0) sb.append(",");
                sb.append("\"").append(escape(ct.pathPrefixes.get(j))).append("\"");
            }
            sb.append("]");
            if (ct.subTabs != null && !ct.subTabs.isEmpty()) {
                sb.append(",\"subTabs\":{");
                boolean firstSub = true;
                for (Map.Entry<String, LinkedHashMap<String, List<String>>> subEntry : ct.subTabs.entrySet()) {
                    if (!firstSub) sb.append(",");
                    firstSub = false;
                    sb.append("\"").append(escape(subEntry.getKey())).append("\":{");
                    boolean firstSec = true;
                    for (Map.Entry<String, List<String>> secEntry : subEntry.getValue().entrySet()) {
                        if (!firstSec) sb.append(",");
                        firstSec = false;
                        sb.append("\"").append(escape(secEntry.getKey())).append("\":[");
                        List<String> prefixes = secEntry.getValue();
                        for (int k = 0; k < prefixes.size(); k++) {
                            if (k > 0) sb.append(",");
                            sb.append("\"").append(escape(prefixes.get(k))).append("\"");
                        }
                        sb.append("]");
                    }
                    sb.append("}");
                }
                sb.append("}");
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    /** Mutable cursor used by the nested subTabs parser so helper methods can advance a shared index. */
    private static final class Cursor {
        int i;
        Cursor(int i) { this.i = i; }
    }

    private static void parseCustomTabs(String json) {
        // Lightweight parser for [{"name":"...","paths":["...","..."],"subTabs":{"Sub":{"Section":["..."]}}}]
        Cursor c = new Cursor(1); // skip [
        while (c.i < json.length() && json.charAt(c.i) != ']') {
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != '{') break;
            c.i++; // skip {
            String name = "";
            List<String> paths = new ArrayList<>();
            LinkedHashMap<String, LinkedHashMap<String, List<String>>> subTabs = new LinkedHashMap<>();
            while (c.i < json.length() && json.charAt(c.i) != '}') {
                c.i = skipWhitespace(json, c.i);
                if (c.i >= json.length() || json.charAt(c.i) == '}') break;
                // Parse key
                if (json.charAt(c.i) != '"') break;
                c.i++;
                int keyStart = c.i;
                while (c.i < json.length() && json.charAt(c.i) != '"') c.i++;
                String key = json.substring(keyStart, c.i);
                c.i++; // skip closing quote
                c.i = skipWhitespace(json, c.i);
                if (c.i >= json.length() || json.charAt(c.i) != ':') break;
                c.i++; // skip colon
                c.i = skipWhitespace(json, c.i);
                if (key.equals("name")) {
                    if (json.charAt(c.i) != '"') break;
                    c.i++;
                    int valStart = c.i;
                    while (c.i < json.length() && json.charAt(c.i) != '"') {
                        if (json.charAt(c.i) == '\\') c.i++;
                        c.i++;
                    }
                    name = unescape(json.substring(valStart, c.i));
                    c.i++;
                } else if (key.equals("paths")) {
                    if (json.charAt(c.i) != '[') break;
                    c.i++; // skip [
                    while (c.i < json.length() && json.charAt(c.i) != ']') {
                        c.i = skipWhitespace(json, c.i);
                        if (c.i >= json.length() || json.charAt(c.i) == ']') break;
                        if (json.charAt(c.i) != '"') break;
                        c.i++;
                        int pathStart = c.i;
                        while (c.i < json.length() && json.charAt(c.i) != '"') {
                            if (json.charAt(c.i) == '\\') c.i++;
                            c.i++;
                        }
                        paths.add(unescape(json.substring(pathStart, c.i)));
                        c.i++; // skip closing quote
                        c.i = skipWhitespace(json, c.i);
                        if (c.i < json.length() && json.charAt(c.i) == ',') c.i++;
                    }
                    if (c.i < json.length() && json.charAt(c.i) == ']') c.i++; // skip ]
                } else if (key.equals("subTabs")) {
                    if (c.i < json.length() && json.charAt(c.i) == '{') {
                        subTabs = parseSubTabs(json, c);
                    }
                }
                c.i = skipWhitespace(json, c.i);
                if (c.i < json.length() && json.charAt(c.i) == ',') c.i++;
            }
            if (c.i < json.length() && json.charAt(c.i) == '}') c.i++; // skip }
            if (!name.isEmpty()) {
                CustomTab tab = new CustomTab(name, paths);
                tab.subTabs = subTabs;
                customTabs.add(tab);
            }
            c.i = skipWhitespace(json, c.i);
            if (c.i < json.length() && json.charAt(c.i) == ',') c.i++;
        }
    }

    /** Parses a subTabs object: {"SubTabName":{"SectionName":["prefix","prefix"], ...}, ...}. */
    private static LinkedHashMap<String, LinkedHashMap<String, List<String>>> parseSubTabs(String json, Cursor c) {
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> subTabs = new LinkedHashMap<>();
        c.i++; // skip {
        c.i = skipWhitespace(json, c.i);
        while (c.i < json.length() && json.charAt(c.i) != '}') {
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != '"') break;
            c.i++;
            int keyStart = c.i;
            while (c.i < json.length() && json.charAt(c.i) != '"') {
                if (json.charAt(c.i) == '\\') c.i++;
                c.i++;
            }
            String subTabName = unescape(json.substring(keyStart, c.i));
            c.i++; // skip closing quote
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != ':') break;
            c.i++; // skip colon
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != '{') break;
            LinkedHashMap<String, List<String>> sections = parseSections(json, c);
            subTabs.put(subTabName, sections);
            c.i = skipWhitespace(json, c.i);
            if (c.i < json.length() && json.charAt(c.i) == ',') {
                c.i++;
                c.i = skipWhitespace(json, c.i);
            }
        }
        if (c.i < json.length() && json.charAt(c.i) == '}') c.i++; // skip closing }
        return subTabs;
    }

    /** Parses a sections object: {"SectionName":["prefix","prefix"], ...}. */
    private static LinkedHashMap<String, List<String>> parseSections(String json, Cursor c) {
        LinkedHashMap<String, List<String>> sections = new LinkedHashMap<>();
        c.i++; // skip {
        c.i = skipWhitespace(json, c.i);
        while (c.i < json.length() && json.charAt(c.i) != '}') {
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != '"') break;
            c.i++;
            int keyStart = c.i;
            while (c.i < json.length() && json.charAt(c.i) != '"') {
                if (json.charAt(c.i) == '\\') c.i++;
                c.i++;
            }
            String sectionName = unescape(json.substring(keyStart, c.i));
            c.i++; // skip closing quote
            c.i = skipWhitespace(json, c.i);
            if (c.i >= json.length() || json.charAt(c.i) != ':') break;
            c.i++; // skip colon
            c.i = skipWhitespace(json, c.i);
            List<String> prefixes = new ArrayList<>();
            if (c.i < json.length() && json.charAt(c.i) == '[') {
                c.i++; // skip [
                while (c.i < json.length() && json.charAt(c.i) != ']') {
                    c.i = skipWhitespace(json, c.i);
                    if (c.i >= json.length() || json.charAt(c.i) == ']') break;
                    if (json.charAt(c.i) != '"') break;
                    c.i++;
                    int pathStart = c.i;
                    while (c.i < json.length() && json.charAt(c.i) != '"') {
                        if (json.charAt(c.i) == '\\') c.i++;
                        c.i++;
                    }
                    prefixes.add(unescape(json.substring(pathStart, c.i)));
                    c.i++; // skip closing quote
                    c.i = skipWhitespace(json, c.i);
                    if (c.i < json.length() && json.charAt(c.i) == ',') c.i++;
                }
                if (c.i < json.length() && json.charAt(c.i) == ']') c.i++; // skip ]
            }
            sections.put(sectionName, prefixes);
            c.i = skipWhitespace(json, c.i);
            if (c.i < json.length() && json.charAt(c.i) == ',') {
                c.i++;
                c.i = skipWhitespace(json, c.i);
            }
        }
        if (c.i < json.length() && json.charAt(c.i) == '}') c.i++; // skip closing }
        return sections;
    }

    private static int skipWhitespace(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
