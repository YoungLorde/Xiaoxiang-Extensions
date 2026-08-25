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

    /**
     * Maps an expansion mod's config path -> the root NightConfig tree that
     * backs it (the expansion's ForgeConfigSpec.childConfig).
     *
     * Expansion mods don't expose their ConfigValue objects the way the base
     * mod's ExtendedConfig does (there's no static-field-per-setting class we
     * can reflect on), so their paths are never present in PATH_TO_VALUE.
     * Reading/writing them instead goes straight through this map to the
     * underlying config tree, using the same path-list get/set calls
     * ForgeConfigSpec.ConfigValue itself uses internally.
     */
    private static final Map<String, com.electronwill.nightconfig.core.Config> EXPANSION_PATH_TO_ROOT = new HashMap<>();

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
     * Scan all registered expansion configs for their config paths.
     * Should be called after ExpansionDiscovery has run.
     * This is called lazily on first access if not already done.
     *
     * Expansion specs don't give us ConfigValue objects the way ExtendedConfig
     * does (there's no equivalent of its static fields to reflect on), so
     * instead of trying to construct/find ConfigValue instances, this reads
     * the expansion's live config tree directly - its ForgeConfigSpec has a
     * "childConfig" field (a com.electronwill.nightconfig.core.Config) that
     * ForgeConfigSpec.ConfigValue itself reads from and writes to internally
     * via path-list get/set calls. Walking that same tree and remembering,
     * per leaf path, which root Config to call get/set against lets every
     * expansion path become readable and editable without needing a
     * ConfigValue object at all.
     */
    public static void scanExpansionConfigs() {
        if (expansionsScanned) return;
        expansionsScanned = true;

        for (ExpansionConfigRegistry.ExpansionConfig expansion : ExpansionConfigRegistry.getAll()) {
            // Skip our own config - already scanned
            if (expansion.modId.equals("xiaoxiang_config_ext")) continue;

            try {
                java.lang.reflect.Field childConfigField = ForgeConfigSpec.class.getDeclaredField("childConfig");
                childConfigField.setAccessible(true);
                Object childConfig = childConfigField.get(expansion.spec);
                if (childConfig instanceof com.electronwill.nightconfig.core.Config) {
                    com.electronwill.nightconfig.core.Config root = (com.electronwill.nightconfig.core.Config) childConfig;
                    scanExpansionTree(root, root, "");
                }
            } catch (Exception e) {
                // The expansion's config may not be loaded yet, or its internals
                // may differ from what's reflected on here - its paths simply
                // won't be individually readable/editable in that case. The tab
                // itself stays visible regardless (CustomTabManager backs tab
                // visibility against ExpansionConfigRegistry.getAllPaths(), a
                // separate, independently-populated source).
            }
        }
    }

    /**
     * Recursively walk a NightConfig tree, remembering the root config for
     * every leaf path so it can be read/written later via path-list get/set
     * on that same root - exactly how ForgeConfigSpec.ConfigValue does it
     * internally (config.getOrElse(path, ...) / config.set(path, value)).
     */
    private static void scanExpansionTree(com.electronwill.nightconfig.core.Config root,
                                           com.electronwill.nightconfig.core.Config node,
                                           String prefix) {
        for (com.electronwill.nightconfig.core.Config.Entry entry : node.entrySet()) {
            String key = entry.getKey();
            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = entry.getValue();
            if (value instanceof com.electronwill.nightconfig.core.Config) {
                scanExpansionTree(root, (com.electronwill.nightconfig.core.Config) value, fullPath);
            } else {
                if (!PATH_TO_VALUE.containsKey(fullPath) && !EXPANSION_PATH_TO_ROOT.containsKey(fullPath)) {
                    ALL_PATHS.add(fullPath);
                }
                EXPANSION_PATH_TO_ROOT.put(fullPath, root);
            }
        }
    }

    /** Split a dotted config path into the List<String> form NightConfig's path-based get/set expect. */
    private static List<String> splitPath(String path) {
        return Arrays.asList(path.split("\\."));
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

    /** Read the current raw value for an expansion config path directly from its NightConfig tree. */
    private static Object getExpansionRawValue(String path) {
        com.electronwill.nightconfig.core.Config root = EXPANSION_PATH_TO_ROOT.get(path);
        if (root == null) return null;
        try {
            return root.get(splitPath(path));
        } catch (Exception e) {
            return null;
        }
    }

    /** Write a raw value for an expansion config path directly to its NightConfig tree. */
    private static boolean setExpansionRawValue(String path, Object value) {
        com.electronwill.nightconfig.core.Config root = EXPANSION_PATH_TO_ROOT.get(path);
        if (root == null) return false;
        try {
            root.set(splitPath(path), value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Get the current value as a string. */
    public static String getValueString(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
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
        Object v = getExpansionRawValue(path);
        if (v == null) return "???";
        if (v instanceof Double) {
            return String.format("%.2f", v);
        }
        return String.valueOf(v);
    }

    /** Get the DEFAULT value as a string (the value the config was created with). */
    public static String getDefaultValueString(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
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
        // Expansion paths have no separately-tracked default here (their
        // spec's default metadata lives in a different tree than the live
        // config values) - falling back to the current value is an honest
        // choice rather than showing a "???" that would look broken.
        if (EXPANSION_PATH_TO_ROOT.containsKey(path)) {
            return getValueString(path);
        }
        return "???";
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
        if (value != null) {
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
        Object current = getExpansionRawValue(path);
        if (current == null) return false;
        try {
            if (current instanceof Integer) {
                return setExpansionRawValue(path, Integer.parseInt(strValue));
            } else if (current instanceof Double) {
                return setExpansionRawValue(path, Double.parseDouble(strValue));
            } else if (current instanceof Long) {
                return setExpansionRawValue(path, Long.parseLong(strValue));
            } else if (current instanceof Boolean) {
                return setExpansionRawValue(path, Boolean.parseBoolean(strValue));
            } else if (current instanceof String) {
                return setExpansionRawValue(path, strValue);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Increment a numeric value by a step. */
    public static boolean increment(String path, double step) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
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
        try {
            Object current = getExpansionRawValue(path);
            if (current instanceof Integer) {
                return setExpansionRawValue(path, (Integer) current + (int) Math.round(step));
            } else if (current instanceof Double) {
                return setExpansionRawValue(path, (Double) current + step);
            } else if (current instanceof Long) {
                return setExpansionRawValue(path, (Long) current + (long) step);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Decrement a numeric value by a step. */
    public static boolean decrement(String path, double step) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
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
        try {
            Object current = getExpansionRawValue(path);
            if (current instanceof Integer) {
                return setExpansionRawValue(path, (Integer) current - (int) Math.round(step));
            } else if (current instanceof Double) {
                return setExpansionRawValue(path, (Double) current - step);
            } else if (current instanceof Long) {
                return setExpansionRawValue(path, (Long) current - (long) step);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Toggle a boolean value. */
    public static boolean toggle(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
            try {
                Object current = value.get();
                if (current instanceof Boolean) {
                    ((ForgeConfigSpec.BooleanValue) value).set(!(Boolean) current);
                    return true;
                }
            } catch (Exception e) {}
            return false;
        }
        Object current = getExpansionRawValue(path);
        if (current instanceof Boolean) {
            return setExpansionRawValue(path, !(Boolean) current);
        }
        return false;
    }

    /** Get the type of a config value. */
    public static String getType(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value != null) {
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
        Object v = getExpansionRawValue(path);
        if (v == null) return "unknown";
        if (v instanceof Integer) return "int";
        if (v instanceof Double) return "double";
        if (v instanceof Long) return "long";
        if (v instanceof Boolean) return "boolean";
        if (v instanceof String) return "string";
        return v.getClass().getSimpleName();
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
        return get(path) != null || EXPANSION_PATH_TO_ROOT.containsKey(path);
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
