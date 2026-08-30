package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.api.ExpansionConfigRegistry;
import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

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

    private static final Logger LOGGER = LogUtils.getLogger();

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

    /**
     * Maps an expansion mod's config path -> its actual typed
     * ForgeConfigSpec.ConfigValue, when reflection can find one (mirrors the
     * same "values" field reflection ExpansionConfigRegistry.extractConfigPaths
     * already uses). EXPANSION_PATH_TO_ROOT above only ever sees the raw
     * NightConfig-backed value (a String for an EnumValue, since NightConfig
     * has no concept of a Java enum) - going through the real typed
     * ConfigValue instead is what lets get/getType/set/increment/decrement
     * treat an expansion's enum settings exactly like the base mod's own,
     * instead of silently no-oping on them.
     */
    private static final Map<String, ForgeConfigSpec.ConfigValue<?>> EXPANSION_PATH_TO_VALUE = new HashMap<>();

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
                //
                // This used to fail completely silently - no log line at all -
                // which is exactly how a real reflection break (a Forge/NightConfig
                // internal field getting renamed, say) could sit unnoticed while
                // every expansion path quietly stopped being individually
                // readable/writable. Logging it means the next time something in
                // this reflection breaks, it shows up in latest.log instead of
                // just looking like a mysteriously unresponsive config screen.
                LOGGER.warn("[XiaoxiangConfigExt] Could not walk '{}' expansion's raw NightConfig tree "
                        + "(childConfig reflection failed): {}", expansion.modId, e.toString());
            }

            // Also grab the expansion's real typed ConfigValue objects, the same
            // way ExpansionConfigRegistry.extractConfigPaths already does via the
            // spec's private "values" map - see EXPANSION_PATH_TO_VALUE's javadoc
            // for why this matters (enum support, mainly).
            int beforeCount = EXPANSION_PATH_TO_VALUE.size();
            try {
                java.lang.reflect.Field valuesField = ForgeConfigSpec.class.getDeclaredField("values");
                valuesField.setAccessible(true);
                Object rawValues = valuesField.get(expansion.spec);
                if (rawValues instanceof Map) {
                    for (Object entryObj : ((Map<?, ?>) rawValues).entrySet()) {
                        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObj;
                        Object keyPathObj = entry.getKey();
                        Object valueObj = entry.getValue();
                        if (keyPathObj instanceof List && valueObj instanceof ForgeConfigSpec.ConfigValue) {
                            // String.join's List overload needs Iterable<? extends
                            // CharSequence>, which a plain List<?> can't statically
                            // satisfy even though every element is really a String
                            // at runtime (ForgeConfigSpec's "values" map key type is
                            // List<String>, erased to List by this reflection). Same
                            // unchecked-cast pattern ExpansionConfigRegistry.extractConfigPaths
                            // already uses for this exact map.
                            @SuppressWarnings("unchecked")
                            List<String> keyPath = (List<String>) keyPathObj;
                            String fullPath = String.join(".", keyPath);
                            EXPANSION_PATH_TO_VALUE.put(fullPath, (ForgeConfigSpec.ConfigValue<?>) valueObj);
                        }
                    }
                }
            } catch (Exception e) {
                // Same reasoning as above - this expansion's paths just stay on
                // the raw NightConfig fallback for reading/writing instead, which
                // means no enum support for this expansion's dropdown/preset-style
                // settings. Also now logged rather than silent, same as above.
                LOGGER.warn("[XiaoxiangConfigExt] Could not read '{}' expansion's typed ConfigValue map "
                        + "(values-field reflection failed) - its enum settings will fall back to raw "
                        + "string reads with no dropdown/+-/- support: {}", expansion.modId, e.toString());
            }
            LOGGER.info("[XiaoxiangConfigExt] Indexed {} typed ConfigValue(s) for expansion '{}'.",
                    EXPANSION_PATH_TO_VALUE.size() - beforeCount, expansion.modId);
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

        // Exact match against a registered expansion mod's own typed ConfigValue.
        // Checked before the fuzzy PATH_TO_VALUE matching below so an expansion's
        // path (always fully-qualified already) can't accidentally get fuzzy-matched
        // against an unrelated base-mod field that happens to share a last segment.
        result = EXPANSION_PATH_TO_VALUE.get(path);
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
                if (v instanceof Enum) {
                    return ((Enum<?>) v).name();
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
        if (v instanceof Enum) {
            return ((Enum<?>) v).name();
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
                if (v instanceof Enum) {
                    return ((Enum<?>) v).name();
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
                } else if (current instanceof Enum) {
                    Object matched = matchEnumConstant((Enum<?>) current, strValue);
                    if (matched == null) return false;
                    ((ForgeConfigSpec.ConfigValue<Object>) value).set(matched);
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

    /**
     * Match a string against an enum's constants - exact name first (what
     * DropdownEnumPopup and every internally-generated value always pass),
     * then case-insensitive as a forgiving fallback. Returns null if nothing
     * matches, e.g. a stale value left over from a since-renamed constant.
     */
    private static Object matchEnumConstant(Enum<?> sample, String strValue) {
        Object[] constants = sample.getClass().getEnumConstants();
        for (Object c : constants) {
            if (((Enum<?>) c).name().equals(strValue)) return c;
        }
        for (Object c : constants) {
            if (((Enum<?>) c).name().equalsIgnoreCase(strValue)) return c;
        }
        return null;
    }

    /**
     * Step an enum value forward/backward through its declared constants,
     * wrapping around at either end. Used by the +/- buttons and Ctrl+scroll
     * on "enum" type entries (see CustomConfigScreen) - a dropdown pick isn't
     * the only way these should be changeable, per the same click/scroll
     * conventions every other entry type in the screen already supports.
     */
    @SuppressWarnings("unchecked")
    private static boolean cycleEnumValue(ForgeConfigSpec.ConfigValue<?> value, Enum<?> current, int direction) {
        Object[] constants = current.getClass().getEnumConstants();
        if (constants.length == 0) return false;
        int idx = current.ordinal() + direction;
        idx = ((idx % constants.length) + constants.length) % constants.length;
        ((ForgeConfigSpec.ConfigValue<Object>) value).set(constants[idx]);
        return true;
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
                } else if (current instanceof Enum) {
                    return cycleEnumValue(value, (Enum<?>) current, 1);
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
                } else if (current instanceof Enum) {
                    return cycleEnumValue(value, (Enum<?>) current, -1);
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
                if (v instanceof Enum) return "enum";
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
        if (v instanceof Enum) return "enum";
        if (v instanceof String) return "string";
        return v.getClass().getSimpleName();
    }

    /**
     * Get the list of valid option names for an "enum" type entry (see getType),
     * in declaration order. Returns an empty list if the path isn't an enum, or
     * if no typed ConfigValue could be resolved for it (a raw-NightConfig-only
     * expansion path has no Class to enumerate constants from).
     */
    public static List<String> getEnumOptions(String path) {
        ForgeConfigSpec.ConfigValue<?> value = get(path);
        if (value == null) return java.util.Collections.emptyList();
        try {
            Object v = value.get();
            if (!(v instanceof Enum)) return java.util.Collections.emptyList();
            Object[] constants = v.getClass().getEnumConstants();
            List<String> names = new ArrayList<>();
            for (Object c : constants) {
                names.add(((Enum<?>) c).name());
            }
            return names;
        } catch (Exception e) {
            return java.util.Collections.emptyList();
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
