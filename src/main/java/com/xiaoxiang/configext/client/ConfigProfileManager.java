package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.configext.client.ConfigValueAccessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles config export/import to JSON and config profiles.
 */
public class ConfigProfileManager {

    /**
     * Export all config values to a JSON string.
     */
    public static String exportToJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        java.util.List<String> paths = ConfigValueAccessor.getAllPaths();
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            String value = ConfigValueAccessor.getValueString(path);
            String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
            // Only export non-default values to keep the file small
            if (!value.equals(defaultVal)) {
                sb.append("  \"").append(escapeJson(path)).append("\": \"").append(escapeJson(value)).append("\"");
                if (i < paths.size() - 1) sb.append(",");
                sb.append("\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Export ALL config values (including defaults) to a JSON string.
     */
    public static String exportAllToJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        java.util.List<String> paths = ConfigValueAccessor.getAllPaths();
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            String value = ConfigValueAccessor.getValueString(path);
            sb.append("  \"").append(escapeJson(path)).append("\": \"").append(escapeJson(value)).append("\"");
            if (i < paths.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Import config values from a JSON string.
     * Returns the number of values imported.
     */
    public static int importFromJson(String json) {
        if (json == null || json.trim().isEmpty()) return 0;
        int count = 0;
        // Simple JSON parsing (key: "value" pairs)
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        String[] lines = json.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals(",")) continue;
            // Remove trailing comma
            if (line.endsWith(",")) line = line.substring(0, line.length() - 1);
            // Parse "key": "value"
            int colonIdx = line.indexOf(':');
            if (colonIdx < 0) continue;
            String key = line.substring(0, colonIdx).trim();
            String value = line.substring(colonIdx + 1).trim();
            // Remove quotes
            if (key.startsWith("\"") && key.endsWith("\"")) key = key.substring(1, key.length() - 1);
            if (value.startsWith("\"") && value.endsWith("\"")) value = value.substring(1, value.length() - 1);
            key = unescapeJson(key);
            value = unescapeJson(value);
            if (ConfigValueAccessor.setValueFromString(key, value)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Generate a checksum of current config values.
     */
    public static String generateChecksum() {
        java.util.List<String> paths = ConfigValueAccessor.getAllPaths();
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(path).append("=").append(ConfigValueAccessor.getValueString(path)).append(";");
        }
        return Integer.toHexString(sb.toString().hashCode());
    }

    /**
     * Get a list of all configs that differ from their default values.
     * Returns a map of path → {current, default}.
     */
    public static Map<String, String[]> getModifiedConfigs() {
        Map<String, String[]> modified = new LinkedHashMap<>();
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String current = ConfigValueAccessor.getValueString(path);
            String def = ConfigValueAccessor.getDefaultValueString(path);
            if (!current.equals(def)) {
                modified.put(path, new String[]{current, def});
            }
        }
        return modified;
    }

    /**
     * Reset all configs to their default values.
     */
    public static int resetAllToDefault() {
        int count = 0;
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String def = ConfigValueAccessor.getDefaultValueString(path);
            if (def != null && ConfigValueAccessor.setValueFromString(path, def)) {
                count++;
            }
        }
        return count;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescapeJson(String s) {
        return s.replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r").replace("\\\\", "\\");
    }

    /**
     * Built-in recommended presets.
     */
    public static Map<String, String> getRecommendedPresets() {
        Map<String, String> presets = new LinkedHashMap<>();
        presets.put("Balanced", "Default values with moderate difficulty.");
        presets.put("Hardcore", "Higher tribulation damage, lower Qi, longer cultivation times.");
        presets.put("Casual", "Lower difficulty, faster progression, more forgiving.");
        presets.put("PvP", "Balanced combat, reduced PvE difficulty.");
        presets.put("Sandbox", "Everything maxed, creative mode feel.");
        presets.put("Authentic", "Closest to original mod's hardcoded values.");
        return presets;
    }
}
