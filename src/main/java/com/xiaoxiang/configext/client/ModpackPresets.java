package com.xiaoxiang.configext.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages modpack presets - saved configurations that can be applied with one click.
 * Presets store all config values and can be shared between players.
 */
public class ModpackPresets {

    public static class Preset {
        public String name;
        public String description;
        public Map<String, String> values; // configPath -> value
        public long createdMs;

        public Preset(String name, String description) {
            this.name = name;
            this.description = description;
            this.values = new HashMap<>();
            this.createdMs = System.currentTimeMillis();
        }
    }

    private static final List<Preset> presets = new ArrayList<>();

    /** Load presets from a JSON string. */
    public static void loadFromJson(String json) {
        presets.clear();
        if (json == null || json.isEmpty() || json.equals("[]")) return;
        try {
            parsePresets(json);
        } catch (Exception e) {
            System.err.println("[ConfigExt] Failed to parse modpack presets: " + e.getMessage());
        }
    }

    /** Save presets to a JSON string. */
    public static String toJson() {
        if (presets.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < presets.size(); i++) {
            if (i > 0) sb.append(",");
            Preset p = presets.get(i);
            sb.append("{\"name\":\"").append(escape(p.name))
              .append("\",\"desc\":\"").append(escape(p.description))
              .append("\",\"values\":{");
            boolean first = true;
            for (Map.Entry<String, String> e : p.values.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escape(e.getKey())).append("\":\"")
                  .append(escape(e.getValue())).append("\"");
            }
            sb.append("}}");
        }
        sb.append("]");
        return sb.toString();
    }

    /** Save current config state as a preset. */
    public static Preset saveCurrentAsPreset(String name, String description) {
        Preset preset = new Preset(name, description);
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String val = ConfigValueAccessor.getValueString(path);
            String def = ConfigValueAccessor.getDefaultValueString(path);
            // Only save non-default values to keep presets compact
            if (val != null && !val.equals(def)) {
                preset.values.put(path, val);
            }
        }
        // Remove existing preset with same name
        presets.removeIf(p -> p.name.equals(name));
        presets.add(preset);
        return preset;
    }

    /** Apply a preset to the current config. */
    public static void applyPreset(String name) {
        Preset preset = getPreset(name);
        if (preset == null) return;
        for (Map.Entry<String, String> e : preset.values.entrySet()) {
            ConfigValueAccessor.setValueFromString(e.getKey(), e.getValue());
        }
        NotificationSystem.showSuccess("Applied preset: " + name);
    }

    /** Delete a preset. */
    public static void deletePreset(String name) {
        presets.removeIf(p -> p.name.equals(name));
    }

    /** Get a preset by name. */
    public static Preset getPreset(String name) {
        for (Preset p : presets) {
            if (p.name.equals(name)) return p;
        }
        return null;
    }

    /** Get all preset names. */
    public static List<String> getPresetNames() {
        List<String> names = new ArrayList<>();
        for (Preset p : presets) names.add(p.name);
        return names;
    }

    /** Get all presets. */
    public static List<Preset> getPresets() {
        return presets;
    }

    /** Export a preset to clipboard-friendly JSON. */
    public static String exportPreset(String name) {
        Preset p = getPreset(name);
        if (p == null) return "{}";
        return toJsonSingle(p);
    }

    /** Import a preset from JSON string. */
    public static boolean importPreset(String json) {
        try {
            List<Preset> imported = new ArrayList<>();
            parsePresets(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String toJsonSingle(Preset p) {
        StringBuilder sb = new StringBuilder("[{\"name\":\"").append(escape(p.name))
            .append("\",\"desc\":\"").append(escape(p.description))
            .append("\",\"values\":{");
        boolean first = true;
        for (Map.Entry<String, String> e : p.values.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(e.getKey())).append("\":\"")
              .append(escape(e.getValue())).append("\"");
        }
        sb.append("}}]");
        return sb.toString();
    }

    // ── JSON parsing ──

    private static void parsePresets(String json) {
        int i = 1; // skip [
        while (i < json.length() && json.charAt(i) != ']') {
            i = skipWhitespace(json, i);
            if (i >= json.length() || json.charAt(i) != '{') break;
            i++; // skip {
            String name = "";
            String desc = "";
            Map<String, String> values = new HashMap<>();
            while (i < json.length() && json.charAt(i) != '}') {
                i = skipWhitespace(json, i);
                if (i >= json.length() || json.charAt(i) == '}') break;
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
                if (key.equals("values")) {
                    if (json.charAt(i) != '{') break;
                    i++; // skip {
                    while (i < json.length() && json.charAt(i) != '}') {
                        i = skipWhitespace(json, i);
                        if (i >= json.length() || json.charAt(i) == '}') break;
                        if (json.charAt(i) != '"') break;
                        i++;
                        int vKeyStart = i;
                        while (i < json.length() && json.charAt(i) != '"') {
                            if (json.charAt(i) == '\\') i++;
                            i++;
                        }
                        String vKey = unescape(json.substring(vKeyStart, i));
                        i++; // skip closing quote
                        i = skipWhitespace(json, i);
                        if (i >= json.length() || json.charAt(i) != ':') break;
                        i++; // skip colon
                        i = skipWhitespace(json, i);
                        if (json.charAt(i) != '"') break;
                        i++;
                        int vValStart = i;
                        while (i < json.length() && json.charAt(i) != '"') {
                            if (json.charAt(i) == '\\') i++;
                            i++;
                        }
                        String vVal = unescape(json.substring(vValStart, i));
                        i++; // skip closing quote
                        values.put(vKey, vVal);
                        i = skipWhitespace(json, i);
                        if (i < json.length() && json.charAt(i) == ',') i++;
                    }
                    if (i < json.length() && json.charAt(i) == '}') i++; // skip }
                } else {
                    if (json.charAt(i) != '"') break;
                    i++;
                    int valStart = i;
                    while (i < json.length() && json.charAt(i) != '"') {
                        if (json.charAt(i) == '\\') i++;
                        i++;
                    }
                    String val = unescape(json.substring(valStart, i));
                    i++; // skip closing quote
                    if (key.equals("name")) name = val;
                    else if (key.equals("desc")) desc = val;
                }
                i = skipWhitespace(json, i);
                if (i < json.length() && json.charAt(i) == ',') i++;
            }
            if (i < json.length() && json.charAt(i) == '}') i++; // skip }
            if (!name.isEmpty()) {
                Preset p = new Preset(name, desc);
                p.values = values;
                presets.add(p);
            }
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
