package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.config.ExtendedConfig;

import java.util.*;

/**
 * Manages custom identities created by the user via the Duplicate button.
 *
 * Custom identities are stored in two config strings:
 * - IDENTITY_CUSTOM_IDENTITIES: "custom_id1~Display Name 1~minLifespan1,maxLifespan1~description1|custom_id2~..."
 * - IDENTITY_CUSTOM_STARTING_ITEMS: "custom_id1:modid:item,count;modid:item,count|custom_id2:..."
 *
 * Each custom identity has:
 * - A unique ID (e.g., "custom_academy_student_1234")
 * - A display name (e.g., "My Custom Identity")
 * - A lifespan range (min, max)
 * - A description (editable text shown in the identity draw screen)
 * - A list of starting items
 *
 * The base identity (which determines portrait, translation key, etc.)
 * is stored as part of the custom ID: "custom_<baseId>_<timestamp>".
 *
 * Format note: We use ~ as the field separator in the identities string because
 * display names and descriptions may contain colons and commas.
 */
public class CustomIdentityManager {

    /**
     * A custom identity entry.
     */
    public static class CustomIdentity {
        public final String id;          // e.g., "custom_academy_student_1234"
        public final String displayName; // e.g., "My Custom Identity"
        public final String baseId;      // e.g., "academy_student" (the original identity it was duplicated from)
        public final int minLifespan;
        public final int maxLifespan;
        public final String startingItems; // e.g., "minecraft:stick,1;minecraft:apple,5"
        public final String description;   // Editable description text

        public CustomIdentity(String id, String displayName, String baseId,
                              int minLifespan, int maxLifespan, String startingItems,
                              String description) {
            this.id = id;
            this.displayName = displayName;
            this.baseId = baseId;
            this.minLifespan = minLifespan;
            this.maxLifespan = maxLifespan;
            this.startingItems = startingItems;
            this.description = description != null ? description : "";
        }
    }

    /**
     * Parse all custom identities from config.
     */
    public static List<CustomIdentity> loadAll() {
        List<CustomIdentity> result = new ArrayList<>();
        String identitiesStr = ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.get();
        String itemsStr = ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.get();

        if (identitiesStr == null || identitiesStr.isEmpty()) return result;

        // Build a map of customId -> startingItems from the items config
        Map<String, String> itemsMap = new HashMap<>();
        if (itemsStr != null && !itemsStr.isEmpty()) {
            for (String entry : itemsStr.split("\\|")) {
                int colonIdx = entry.indexOf(':');
                if (colonIdx > 0) {
                    String id = entry.substring(0, colonIdx).trim();
                    String items = entry.substring(colonIdx + 1).trim();
                    itemsMap.put(id, items);
                }
            }
        }

        // Parse identities: "custom_id~Display Name~minLifespan,maxLifespan~description|..."
        // Also support old format with colons: "custom_id:Display Name:minLifespan,maxLifespan"
        for (String entry : identitiesStr.split("\\|")) {
            try {
                String id;
                String displayName;
                String lifespanStr;
                String description = "";

                if (entry.contains("~")) {
                    // New format: custom_id~Display Name~minLifespan,maxLifespan~description
                    String[] parts = entry.split("~", 4);
                    if (parts.length < 3) continue;
                    id = parts[0].trim();
                    displayName = parts[1].trim();
                    lifespanStr = parts[2].trim();
                    if (parts.length >= 4) description = parts[3].trim();
                } else {
                    // Old format: custom_id:Display Name:minLifespan,maxLifespan
                    int firstColon = entry.indexOf(':');
                    if (firstColon <= 0) continue;
                    id = entry.substring(0, firstColon).trim();
                    String rest = entry.substring(firstColon + 1);
                    int lastColon = rest.lastIndexOf(':');
                    if (lastColon <= 0) continue;
                    displayName = rest.substring(0, lastColon).trim();
                    lifespanStr = rest.substring(lastColon + 1).trim();
                }

                // Parse lifespan range
                int minLifespan = 100, maxLifespan = 120;
                String[] lifespanParts = lifespanStr.split(",");
                if (lifespanParts.length >= 2) {
                    minLifespan = Integer.parseInt(lifespanParts[0].trim());
                    maxLifespan = Integer.parseInt(lifespanParts[1].trim());
                }

                // Extract baseId from the custom ID: "custom_<baseId>_<timestamp>"
                String baseId = extractBaseId(id);

                // Get starting items
                String startingItems = itemsMap.getOrDefault(id, "");

                result.add(new CustomIdentity(id, displayName, baseId,
                        minLifespan, maxLifespan, startingItems, description));
            } catch (Exception e) {
                // Skip malformed entries
            }
        }

        return result;
    }

    /**
     * Extract the base identity ID from a custom ID.
     * "custom_academy_student_1234" -> "academy_student"
     * "custom_lone_cultivator_5678" -> "lone_cultivator"
     */
    public static String extractBaseId(String customId) {
        if (customId == null) return "farmer";
        String trimmed = customId;
        if (trimmed.startsWith("custom_")) {
            trimmed = trimmed.substring("custom_".length());
        }
        // Remove trailing timestamp: last segment after the last underscore if it's numeric
        int lastUnderscore = trimmed.lastIndexOf('_');
        if (lastUnderscore > 0) {
            String lastPart = trimmed.substring(lastUnderscore + 1);
            try {
                Long.parseLong(lastPart);
                trimmed = trimmed.substring(0, lastUnderscore);
            } catch (NumberFormatException e) {
                // Last part is not numeric, keep the full string
            }
        }
        return trimmed;
    }

    /**
     * Generate a unique custom ID from a base identity ID.
     */
    public static String generateCustomId(String baseId) {
        return "custom_" + baseId + "_" + System.currentTimeMillis() % 100000;
    }

    /**
     * Strips this store's two structural delimiters ("~" the field separator,
     * "|" the entry separator) out of free-typed player text before it's written
     * into the config string. Neither character is escaped anywhere else in this
     * class, so leaving one in would silently corrupt field alignment for this
     * entry (a stray "~") or splice two entries into garbage (a stray "|").
     */
    private static String sanitizeField(String value) {
        if (value == null) return "";
        return value.replace("~", "").replace("|", "");
    }

    /**
     * Add a new custom identity to the config.
     */
    public static void addCustomIdentity(String baseId, String displayName,
                                          int minLifespan, int maxLifespan,
                                          String startingItems, String description) {
        String customId = generateCustomId(baseId);

        // Append to identities string (using ~ as separator)
        String identitiesStr = ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.get();
        if (identitiesStr == null) identitiesStr = "";
        String desc = description != null ? description : "";
        // Sanitize: displayName/description are free-typed player text and can contain
        // "~" (this entry's own field separator) or "|" (the separator between whole
        // entries). Either one shifts every field after it during loadAll()'s split -
        // most visibly, a "~" inside the name silently pushes the minLifespan,maxLifespan
        // field out of position, so parsing falls through to the 100-120 defaults
        // (matches the reported "some custom identities are missing their lifespan").
        // Stripping both here, at the one place entries are ever written, keeps the
        // stored format unambiguous without touching the (already-correct) parser.
        String safeDisplayName = sanitizeField(displayName);
        desc = sanitizeField(desc);
        String newIdentityEntry = customId + "~" + safeDisplayName + "~" + minLifespan + "," + maxLifespan + "~" + desc;
        if (!identitiesStr.isEmpty()) identitiesStr += "|";
        identitiesStr += newIdentityEntry;
        ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.set(identitiesStr);

        // Append to items string
        String itemsStr = ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.get();
        if (itemsStr == null) itemsStr = "";
        String newItemEntry = customId + ":" + startingItems;
        if (!itemsStr.isEmpty()) itemsStr += "|";
        itemsStr += newItemEntry;
        ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.set(itemsStr);
    }

    /**
     * Remove a custom identity by ID.
     */
    public static void removeCustomIdentity(String customId) {
        // Remove from identities string
        String identitiesStr = ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.get();
        if (identitiesStr != null && !identitiesStr.isEmpty()) {
            List<String> entries = new ArrayList<>(Arrays.asList(identitiesStr.split("\\|")));
            entries.removeIf(e -> {
                // Match by ID (first field, separated by ~ or :)
                String eid = e.contains("~") ? e.split("~", 2)[0] : e.split(":", 2)[0];
                return eid.trim().equals(customId);
            });
            ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.set(String.join("|", entries));
        }

        // Remove from items string
        String itemsStr = ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.get();
        if (itemsStr != null && !itemsStr.isEmpty()) {
            List<String> entries = new ArrayList<>(Arrays.asList(itemsStr.split("\\|")));
            entries.removeIf(e -> e.startsWith(customId + ":"));
            ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.set(String.join("|", entries));
        }
    }

    /**
     * Get all custom identity IDs (for adding to the draw deck).
     */
    public static List<String> getAllCustomIds() {
        List<String> ids = new ArrayList<>();
        for (CustomIdentity ci : loadAll()) {
            ids.add(ci.id);
        }
        return ids;
    }

    /**
     * Get a custom identity by ID.
     */
    public static CustomIdentity getById(String id) {
        if (id == null) return null;
        for (CustomIdentity ci : loadAll()) {
            if (ci.id.equals(id)) return ci;
        }
        return null;
    }

    /**
     * Get the display name for a custom identity by ID.
     */
    public static String getDisplayName(String id) {
        CustomIdentity ci = getById(id);
        return ci != null ? ci.displayName : null;
    }

    /**
     * Get the description for a custom identity by ID.
     */
    public static String getDescription(String id) {
        CustomIdentity ci = getById(id);
        return ci != null ? ci.description : null;
    }

    /**
     * Get the lifespan range for a custom identity by ID.
     * Returns [min, max] or null if not found.
     */
    public static int[] getLifespanRange(String id) {
        CustomIdentity ci = getById(id);
        if (ci == null) return null;
        return new int[]{ci.minLifespan, ci.maxLifespan};
    }

    /**
     * Get the starting items string for a custom identity by ID.
     */
    public static String getStartingItems(String id) {
        CustomIdentity ci = getById(id);
        return ci != null ? ci.startingItems : null;
    }
}
