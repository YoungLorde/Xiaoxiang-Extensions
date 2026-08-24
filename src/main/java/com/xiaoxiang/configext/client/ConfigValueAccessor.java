package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.api.ExpansionConfigRegistry;
import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Provides read/write access to ForgeConfigSpec values by config path.
 * Maps the config key (e.g. "realms.maxQi.mortal") to the corresponding
 * ForgeConfigSpec.ConfigValue field in ExtendedConfig.
 *
 * Also scans registered expansion mods' configs via ExpansionConfigRegistry.
 * When an expansion mod is installed and registered, its config values
 * automatically become accessible here.
 *
 * Uses fuzzy matching when exact path doesn't match - tries matching by
 * last segment, then by all segments in any order.
 */
public final class ConfigValueAccessor {

    /** Maps config path -> ForgeConfigSpec.ConfigValue */
    private static final Map<String, ForgeConfigSpec.ConfigValue<?>> PATH_TO_VALUE = new HashMap<>();

    /** Maps lowercase last segment -> list of config values (for fuzzy matching) */
    private static final Map<String, List<ForgeConfigSpec.ConfigValue<?>>> LAST_SEGMENT_INDEX = new HashMap<>();

    /** All registered paths for debugging */
    private static final List<String> ALL_PATHS = new ArrayList<>();

    /** Whether expansion configs have been scanned */
    private static boolean expansionsScanned = false;

    static {
        // Build the mapping by reflecting on ExtendedConfig fields
        scanBaseConfig();
    }

    /** Scan the base ExtendedConfig for all ConfigValue fields. */
    private static void scanBaseConfig() {
        try {
            for (Field field : ExtendedConfig.class.getDeclaredFields()) {
                if (ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) field.get(null);
                    if (value != null) {
                        registerValue(value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scan all registered expansion configs for ConfigValue fields.
     * Should be called after ExpansionDiscovery has run.
     * This is called lazily on first access if not already done.
     */
    public static void scanExpansionConfigs() {
        if (expansionsScanned) return;
        expansionsScanned = true;

        for (ExpansionConfigRegistry.ExpansionConfig expansion : ExpansionConfigRegistry.getAll()) {
            // Skip our own config - already scanned
            if (expansion.modId.equals("xiaoxiang_config_ext")) continue;

            try {
                // The expansion's spec has a values map we can traverse
                // But we need the actual ConfigValue objects, not just paths
                // Try to find them via the spec's values map
                java.lang.reflect.Field valuesField = ForgeConfigSpec.class.getDeclaredField("values");
                valuesField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<List<String>, ForgeConfigSpec.ConfigValue<?>> values =
                        (Map<List<String>, ForgeConfigSpec.ConfigValue<?>>) valuesField.get(expansion.spec);
                if (values != null) {
                    for (ForgeConfigSpec.ConfigValue<?> value : values.values()) {
                        registerValue(value);
                    }
                }
            } catch (Exception e) {
                // Fallback: try to find ConfigValue fields via reflection on the spec
                // This works if the expansion stores them as static fields
            }
        }
    }

    /** Register a single ConfigValue in all indexes. */
    private static void registerValue(ForgeConfigSpec.ConfigValue<?> value) {
        List<String> pathList = value.getPath();
        String path = String.join(".", pathList);
        PATH_TO_VALUE.put(path, value);
        ALL_PATHS.add(path);

        // Index by last segment for fuzzy matching
        if (!pathList.isEmpty()) {
            String lastSeg = pathList.get(pathList.size() - 1).toLowerCase();
            LAST_SEGMENT_INDEX.computeIfAbsent(lastSeg, k -> new ArrayList<>()).add(value);
        }
    }

    /**
     * Get the ForgeConfigSpec.ConfigValue for a given config path.
     * Tries exact match first, then fuzzy matching by last segment.
     */
    public static ForgeConfigSpec.ConfigValue<?> get(String path) {
        // Exact match
        ForgeConfigSpec.ConfigValue<?> result = PATH_TO_VALUE.get(path);
        if (result != null) return result;

        // Try: the key might be the last segment only
        if (LAST_SEGMENT_INDEX.containsKey(path.toLowerCase())) {
            List<ForgeConfigSpec.ConfigValue<?>> matches = LAST_SEGMENT_INDEX.get(path.toLowerCase());
            if (matches.size() == 1) return matches.get(0);
        }

        // Try: extract last segment from the key and match
        String[] parts = path.split("\\.");
        if (parts.length > 0) {
            String lastSeg = parts[parts.length - 1].toLowerCase();
            if (LAST_SEGMENT_INDEX.containsKey(lastSeg)) {
                List<ForgeConfigSpec.ConfigValue<?>> matches = LAST_SEGMENT_INDEX.get(lastSeg);
                if (matches.size() == 1) return matches.get(0);

                // Multiple matches - try to find best match by checking all segments
                for (ForgeConfigSpec.ConfigValue<?> v : matches) {
                    String actualPath = String.join(".", v.getPath()).toLowerCase();
                    // Check if all parts of the key are present in the actual path
                    boolean allMatch = true;
                    for (String part : parts) {
                        if (!actualPath.contains(part.toLowerCase())) {
                            allMatch = false;
                            break;
                        }
                    }
                    if (allMatch) return v;
                }
                // If no full match, return first match with same last segment
                return matches.get(0);
            }
        }

        return null;
    }

    /** Get the current value as a string. */
    public static String getValueString(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return "???";
        try {
            Object v = value.get();
            if (v instanceof Double) {
                return String.format("%.2f", v);
            }
            return String.valueOf(v);
        } catch (Exception e) {
            return "???";
        }
    }

    /** Get the DEFAULT value as a string (the value the config was created with). */
    public static String getDefaultValueString(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return "???";
        try {
            // ForgeConfigSpec.ConfigValue has a public getDefault() method
            Object v = value.getDefault();
            if (v instanceof Double) {
                return String.format("%.2f", v);
            }
            return String.valueOf(v);
        } catch (Exception e) {
            return getValueString(path);
        }
    }

    /** Get the default value as a double, for multiplier computation. */
    public static double getDefaultValueAsDouble(String path) {
        String s = getDefaultValueString(path);
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Set a value from a string. Parses the string to the appropriate type. */
    @SuppressWarnings("unchecked")
    public static boolean setValueFromString(String path, String strValue) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return false;
        try {
            Object current = value.get();
            if (current instanceof Integer) {
                int v = Integer.parseInt(strValue);
                ((ForgeConfigSpec.IntValue) value).set(v);
            } else if (current instanceof Double) {
                double v = Double.parseDouble(strValue);
                ((ForgeConfigSpec.DoubleValue) value).set(v);
            } else if (current instanceof Long) {
                long v = Long.parseLong(strValue);
                ((ForgeConfigSpec.LongValue) value).set(v);
            } else if (current instanceof Boolean) {
                boolean v = Boolean.parseBoolean(strValue);
                ((ForgeConfigSpec.BooleanValue) value).set(v);
            } else if (current instanceof String) {
                ((ForgeConfigSpec.ConfigValue<String>) value).set(strValue);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Increment a numeric value by a step. */
    public static boolean increment(String path, double step) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return false;
        try {
            Object current = value.get();
            if (current instanceof Integer) {
                int v = (Integer) current + (int) Math.round(step);
                ((ForgeConfigSpec.IntValue) value).set(v);
            } else if (current instanceof Double) {
                double v = (Double) current + step;
                ((ForgeConfigSpec.DoubleValue) value).set(v);
            } else if (current instanceof Long) {
                long v = (Long) current + (long) step;
                ((ForgeConfigSpec.LongValue) value).set(v);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Decrement a numeric value by a step. */
    public static boolean decrement(String path, double step) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return false;
        try {
            Object current = value.get();
            if (current instanceof Integer) {
                int v = (Integer) current - (int) Math.round(step);
                ((ForgeConfigSpec.IntValue) value).set(v);
            } else if (current instanceof Double) {
                double v = (Double) current - step;
                ((ForgeConfigSpec.DoubleValue) value).set(v);
            } else if (current instanceof Long) {
                long v = (Long) current - (long) step;
                ((ForgeConfigSpec.LongValue) value).set(v);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Toggle a boolean value. */
    public static boolean toggle(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return false;
        try {
            Object current = value.get();
            if (current instanceof Boolean) {
                ((ForgeConfigSpec.BooleanValue) value).set(!(Boolean) current);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    /** Get the type of a config value. */
    public static String getType(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return "unknown";
        try {
            Object v = value.get();
            if (v instanceof Integer) return "int";
            if (v instanceof Double) return "double";
            if (v instanceof Long) return "long";
            if (v instanceof Boolean) return "boolean";
            if (v instanceof String) return "string";
            return v.getClass().getSimpleName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /** Get the minimum allowed value as a String, or null if not a ranged value. */
    public static String getMinValueString(String path) {
        Object min = getRangeValue(path, true);
        if (min == null) return null;
        if (min instanceof Double) return String.format("%.2f", min);
        return String.valueOf(min);
    }

    /** Get the maximum allowed value as a String, or null if not a ranged value. */
    public static String getMaxValueString(String path) {
        Object max = getRangeValue(path, false);
        if (max == null) return null;
        if (max instanceof Double) return String.format("%.2f", max);
        return String.valueOf(max);
    }

    /**
     * Extract min or max from a ForgeConfigSpec ranged value via reflection.
     * ForgeConfigSpec stores the range in a field called "range" of type
     * Number[] { min, max } on the ConfigValue subclass.
     */
    private static Object getRangeValue(String path, boolean isMin) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return null;
        try {
            // Try to access the "range" field on the ConfigValue
            for (Field f : value.getClass().getDeclaredFields()) {
                if (f.getName().equals("range")) {
                    f.setAccessible(true);
                    Object range = f.get(value);
                    if (range instanceof Number[]) {
                        Number[] arr = (Number[]) range;
                        if (arr.length >= 2) {
                            return isMin ? arr[0] : arr[1];
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    /** Check if a config path exists in our mapping. */
    public static boolean exists(String path) {
        return get(path) != null;
    }

    /** Get all registered config paths (including expansion configs). */
    public static List<String> getAllPaths() {
        // Lazily scan expansion configs on first call
        scanExpansionConfigs();
        return ALL_PATHS;
    }

    /** Debug: dump all paths to console. */
    public static void dumpAllPaths() {
        System.out.println("[ConfigValueAccessor] Registered " + ALL_PATHS.size() + " config paths:");
        for (String p : ALL_PATHS) {
            System.out.println("  " + p);
        }
    }
}
