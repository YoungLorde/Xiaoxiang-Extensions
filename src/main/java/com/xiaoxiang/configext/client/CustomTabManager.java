package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages custom tabs (user-defined tabs that aggregate configs from multiple categories)
 * and tab reordering (custom display order for top-level tabs).
 */
public class CustomTabManager {

    // Custom tab definition: name + list of config path prefixes
    public static class CustomTab {
        public String name;
        public List<String> pathPrefixes;

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

    /** Remove a custom tab. */
    public static void removeCustomTab(String name) {
        customTabs.removeIf(ct -> ct.name.equals(name));
        tabOrder.remove(name);
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
            sb.append("]}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void parseCustomTabs(String json) {
        // Lightweight parser for [{"name":"...","paths":["...","..."]}]
        int i = 1; // skip [
        while (i < json.length() && json.charAt(i) != ']') {
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) != '{') break;
            i++; // skip {
            String name = "";
            List<String> paths = new ArrayList<>();
            while (i < json.length() && json.charAt(i) != '}') {
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) == '}') break;
                // Parse key
                if (json.charAt(i) != '"') break;
                i++;
                int keyStart = i;
                while (i < json.length() && json.charAt(i) != '"') i++;
                String key = json.substring(keyStart, i);
                i++; // skip closing quote
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) != ':') break;
                i++; // skip colon
                i = skipWhitespace(json, i);
                if (key.equals("name")) {
                    if (json.charAt(i) != '"') break;
                    i++;
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != '"') {
                        if (json.charAt(i) == '\\') i++;
                        i++;
                    }
                    name = unescape(json.substring(valStart, i));
                    i++;
                } else if (key.equals("paths")) {
                    if (json.charAt(i) != '[') break;
                    i++; // skip [
                    while (i < json.length() && json.charAt(i) != ']') {
                        i = skipWhitespace(json, i);
                        if (i >= json.length() || json.charAt(i) == ']') break;
                        if (json.charAt(i) != '"') break;
                        i++;
                        int pathStart = i;
                        while (i < json.length() && json.charAt(i) != '"') {
                            if (json.charAt(i) == '\\') i++;
                            i++;
                        }
                        paths.add(unescape(json.substring(pathStart, i)));
                        i++; // skip closing quote
                        i = skipWhitespace(json, i);
                        if (i < json.length() && json.charAt(i) == ',') i++;
                    }
                    if (i < json.length() && json.charAt(i) == ']') i++; // skip ]
                }
                i = skipWhitespace(json, i);
                if (i < json.length() && json.charAt(i) == ',') i++;
            }
            if (i < json.length() && json.charAt(i) == '}') i++; // skip }
            if (!name.isEmpty()) customTabs.add(new CustomTab(name, paths));
            i = skipWhitespace(json, i);
            if (i < json.length() && json.charAt(i) == ',') i++;
        }
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
