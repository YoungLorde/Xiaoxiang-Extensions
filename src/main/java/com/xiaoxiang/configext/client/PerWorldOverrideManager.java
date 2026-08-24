package com.xiaoxiang.configext.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages per-world config overrides.
 * Stores a JSON map of {worldName: {configPath: value}} and applies/revokes
 * overrides when worlds load/unload.
 */
public class PerWorldOverrideManager {

    // worldName -> (configPath -> overrideValue)
    private static final Map<String, Map<String, String>> overrides = new HashMap<>();
    private static String currentWorld = "";
    // Backup of original values before override was applied
    private static final Map<String, String> originalValues = new HashMap<>();

    /** Load overrides from the config string (JSON-like format). */
    public static void loadFromConfig(String json) {
        overrides.clear();
        if (json == null || json.isEmpty() || json.equals("{}")) return;
        try {
            // Simple parsing: {"world":{"path":"val","path2":"val2"},...}
            // This is a lightweight parser to avoid external JSON deps
            parseOverrides(json);
        } catch (Exception e) {
            System.err.println("[ConfigExt] Failed to parse per-world overrides: " + e.getMessage());
        }
    }

    /** Save overrides to a config string. */
    public static String saveToConfig() {
        if (overrides.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean firstWorld = true;
        for (Map.Entry<String, Map<String, String>> worldEntry : overrides.entrySet()) {
            if (!firstWorld) sb.append(",");
            firstWorld = false;
            sb.append("\"").append(escape(worldEntry.getKey())).append("\":{");
            boolean firstPath = true;
            for (Map.Entry<String, String> pathEntry : worldEntry.getValue().entrySet()) {
                if (!firstPath) sb.append(",");
                firstPath = false;
                sb.append("\"").append(escape(pathEntry.getKey())).append("\":\"")
                  .append(escape(pathEntry.getValue())).append("\"");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    /** Set an override for a specific world + config path. */
    public static void setOverride(String worldName, String configPath, String value) {
        overrides.computeIfAbsent(worldName, k -> new HashMap<>()).put(configPath, value);
        saveToExtendedConfig();
        if (worldName.equals(currentWorld)) {
            applyOverride(configPath, value);
        }
    }

    /** Remove an override for a specific world + config path. */
    public static void removeOverride(String worldName, String configPath) {
        Map<String, String> worldOverrides = overrides.get(worldName);
        if (worldOverrides != null) {
            worldOverrides.remove(configPath);
            if (worldOverrides.isEmpty()) overrides.remove(worldName);
        }
        saveToExtendedConfig();
        if (worldName.equals(currentWorld)) {
            revokeOverride(configPath);
        }
    }

    /** Get all overrides for a specific world. */
    public static Map<String, String> getOverridesForWorld(String worldName) {
        return overrides.getOrDefault(worldName, new HashMap<>());
    }

    /** Get all world names that have overrides. */
    public static java.util.Set<String> getWorldsWithOverrides() {
        return overrides.keySet();
    }

    /** Called when a world is loaded. Applies all overrides for that world. */
    public static void onWorldLoad(String worldName) {
        // First revoke any previous world's overrides
        if (!currentWorld.isEmpty()) {
            revokeAllOverrides();
        }
        currentWorld = worldName;
        Map<String, String> worldOverrides = overrides.get(worldName);
        if (worldOverrides != null) {
            for (Map.Entry<String, String> entry : worldOverrides.entrySet()) {
                applyOverride(entry.getKey(), entry.getValue());
            }
            NotificationSystem.showInfo("Applied " + worldOverrides.size() + " per-world override(s) for '" + worldName + "'");
        }
    }

    /** Called when a world is unloaded. Revokes all overrides. */
    public static void onWorldUnload() {
        if (!currentWorld.isEmpty()) {
            revokeAllOverrides();
            currentWorld = "";
        }
    }

    /** Get the current world name. */
    public static String getCurrentWorld() {
        return currentWorld;
    }

    /** Check if per-world overrides are enabled. */
    public static boolean isEnabled() {
        try {
            return com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_ENABLE_PER_WORLD_OVERRIDES.get();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Internal methods ──

    private static void applyOverride(String configPath, String value) {
        // Backup original value
        String original = ConfigValueAccessor.getValueString(configPath);
        originalValues.put(configPath, original);
        // Apply override
        ConfigValueAccessor.setValueFromString(configPath, value);
    }

    private static void revokeOverride(String configPath) {
        String original = originalValues.remove(configPath);
        if (original != null) {
            ConfigValueAccessor.setValueFromString(configPath, original);
        }
    }

    private static void revokeAllOverrides() {
        for (Map.Entry<String, String> entry : originalValues.entrySet()) {
            ConfigValueAccessor.setValueFromString(entry.getKey(), entry.getValue());
        }
        originalValues.clear();
    }

    private static void saveToExtendedConfig() {
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_PER_WORLD_OVERRIDES.set(saveToConfig());
        } catch (Exception e) { /* ignore */ }
    }

    // Lightweight JSON parser for {"world":{"path":"val"}}
    private static void parseOverrides(String json) {
        int i = 1; // skip opening {
        while (i < json.length() && json.charAt(i) != '}') {
            // Parse world name
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) == '}') break;
            if (json.charAt(i) != '"') break;
            i++; // skip opening quote
            int nameStart = i;
            while (i < json.length() && json.charAt(i) != '"') {
                if (json.charAt(i) == '\\') i++;
                i++;
            }
            String worldName = unescape(json.substring(nameStart, i));
            i++; // skip closing quote
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) != ':') break;
            i++; // skip colon
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) != '{') break;
            i++; // skip opening brace
            Map<String, String> worldOverrides = new HashMap<>();
            while (i < json.length() && json.charAt(i) != '}') {
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) == '}') break;
                if (json.charAt(i) != '"') break;
                i++; // skip opening quote
                int pathStart = i;
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') i++;
                    i++;
                }
                String path = unescape(json.substring(pathStart, i));
                i++; // skip closing quote
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) != ':') break;
                i++; // skip colon
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) != '"') break;
                i++; // skip opening quote
                int valStart = i;
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\') i++;
                    i++;
                }
                String val = unescape(json.substring(valStart, i));
                i++; // skip closing quote
                worldOverrides.put(path, val);
                i = skipWhitespace(json, i);
                if (i < json.length() && json.charAt(i) == ',') i++;
            }
            if (i < json.length() && json.charAt(i) == '}') i++; // skip closing brace
            overrides.put(worldName, worldOverrides);
            i = skipWhitespace(json, i);
            if (i < json.length() && json.charAt(i) == ',') i++;
        }
    }

    private static int skipWhitespace(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace(",", "\\c");
    }

    private static String unescape(String s) {
        return s.replace("\\c", ",").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
