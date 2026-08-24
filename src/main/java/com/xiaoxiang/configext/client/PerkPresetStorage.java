package com.xiaoxiang.configext.client;

import java.util.*;
import java.nio.file.*;
import java.io.*;

/**
 * Stores up to 3 perk preset slots on disk so players can save and load
 * their Golden Finger perk selections between world creations.
 *
 * Saved to: .minecraft/config/xiaoxiang_config_ext/perk_presets.txt
 * Format: One line per slot, comma-separated perk IDs.
 */
public class PerkPresetStorage {

    private static final Path PRESET_DIR = Paths.get("config", "xiaoxiang_config_ext");
    private static final Path PRESET_FILE = PRESET_DIR.resolve("perk_presets.txt");

    public static final int NUM_SLOTS = 3;

    /** Slot names for display. */
    public static final String[] SLOT_NAMES = {"Slot 1", "Slot 2", "Slot 3"};

    /**
     * Load all preset slots from disk.
     * @return Map of slot index -> Set of perk IDs (empty sets if no save)
     */
    public static Map<Integer, Set<Integer>> loadAll() {
        Map<Integer, Set<Integer>> slots = new LinkedHashMap<>();
        for (int i = 0; i < NUM_SLOTS; i++) {
            slots.put(i, new LinkedHashSet<>());
        }

        try {
            if (!Files.exists(PRESET_FILE)) return slots;

            List<String> lines = Files.readAllLines(PRESET_FILE);
            for (int i = 0; i < Math.min(lines.size(), NUM_SLOTS); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                Set<Integer> perkIds = new LinkedHashSet<>();
                for (String part : line.split(",")) {
                    part = part.trim();
                    if (!part.isEmpty()) {
                        try {
                            perkIds.add(Integer.parseInt(part));
                        } catch (NumberFormatException e) { /* skip */ }
                    }
                }
                slots.put(i, perkIds);
            }
        } catch (IOException e) {
            // Ignore - return empty slots
        }

        return slots;
    }

    /**
     * Save a perk selection to a slot.
     * @param slot Slot index (0-2)
     * @param perkIds Set of perk IDs to save
     */
    public static void saveSlot(int slot, Set<Integer> perkIds) {
        try {
            Files.createDirectories(PRESET_DIR);

            // Load existing lines
            Map<Integer, Set<Integer>> existing = loadAll();
            existing.put(slot, new LinkedHashSet<>(perkIds));

            // Write all slots
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < NUM_SLOTS; i++) {
                Set<Integer> ids = existing.getOrDefault(i, new LinkedHashSet<>());
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (int id : ids) {
                    if (!first) sb.append(",");
                    sb.append(id);
                    first = false;
                }
                lines.add(sb.toString());
            }

            Files.write(PRESET_FILE, lines);
        } catch (IOException e) {
            // Ignore
        }
    }

    /**
     * Get a display name for a slot, showing what's saved in it.
     * @param slot Slot index
     * @return String like "Slot 1: Immortal Body, Sword Soul, ..." or "Slot 1: (empty)"
     */
    public static String getSlotDisplay(int slot) {
        Map<Integer, Set<Integer>> all = loadAll();
        Set<Integer> ids = all.getOrDefault(slot, new LinkedHashSet<>());

        if (ids.isEmpty()) {
            return SLOT_NAMES[slot] + ": \u00A77(empty)";
        }

        StringBuilder sb = new StringBuilder(SLOT_NAMES[slot] + ": \u00A7a");
        boolean first = true;
        for (int id : ids) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(id);
            if (perk != null) {
                if (!first) sb.append(", ");
                sb.append(perk.name);
                first = false;
            }
        }
        if (first) {
            return SLOT_NAMES[slot] + ": \u00A77(empty)";
        }
        return sb.toString();
    }
}
