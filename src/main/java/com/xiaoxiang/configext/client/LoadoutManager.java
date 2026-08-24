package com.xiaoxiang.configext.client;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages 5 loadout slots for Golden Finger world creation.
 * Each loadout stores: perk IDs, identity ID, starting items string, golden finger count.
 * Loadouts are persisted to a file in the game config directory.
 */
public class LoadoutManager {

    public static class Loadout {
        public String name = "";
        public List<Integer> perkIds = new ArrayList<>();
        public String identityId = "";
        public String startingItems = "";
        public int goldenFingerCount = 3;
        public boolean empty = true;

        public Loadout copy() {
            Loadout copy = new Loadout();
            copy.name = this.name;
            copy.perkIds = new ArrayList<>(this.perkIds);
            copy.identityId = this.identityId;
            copy.startingItems = this.startingItems;
            copy.goldenFingerCount = this.goldenFingerCount;
            copy.empty = this.empty;
            return copy;
        }
    }

    private static final int NUM_SLOTS = 5;
    private static final Loadout[] slots = new Loadout[NUM_SLOTS];
    private static Path loadoutFile;

    static {
        for (int i = 0; i < NUM_SLOTS; i++) {
            slots[i] = new Loadout();
        }
    }

    /** Initialize the loadout file path. Called from the client setup event. */
    public static void init(Path configDir) {
        loadoutFile = configDir.resolve("xiaoxiang_loadouts.txt");
        loadFromFile();
    }

    private static Path getLoadoutFile() {
        if (loadoutFile == null) {
            try {
                Path configDir = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
                loadoutFile = configDir.resolve("xiaoxiang_loadouts.txt");
            } catch (Throwable t) {
                loadoutFile = Paths.get("xiaoxiang_loadouts.txt");
            }
        }
        return loadoutFile;
    }

    /** Load all slots from the file. */
    public static void loadFromFile() {
        Path file = getLoadoutFile();
        if (!Files.exists(file)) return;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int currentSlot = -1;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[slot")) {
                    String numStr = line.substring(5, line.length() - 1);
                    currentSlot = Integer.parseInt(numStr);
                    if (currentSlot >= 0 && currentSlot < NUM_SLOTS) {
                        slots[currentSlot] = new Loadout();
                    }
                } else if (currentSlot >= 0 && currentSlot < NUM_SLOTS) {
                    Loadout slot = slots[currentSlot];
                    if (line.startsWith("name=")) {
                        slot.name = line.substring(5);
                        slot.empty = false;
                    } else if (line.startsWith("perks=")) {
                        String[] parts = line.substring(6).split(",");
                        for (String p : parts) {
                            p = p.trim();
                            if (!p.isEmpty()) {
                                try { slot.perkIds.add(Integer.parseInt(p)); } catch (NumberFormatException ignored) {}
                            }
                        }
                        slot.empty = false;
                    } else if (line.startsWith("identity=")) {
                        slot.identityId = line.substring(9);
                        slot.empty = false;
                    } else if (line.startsWith("items=")) {
                        slot.startingItems = line.substring(6);
                        slot.empty = false;
                    } else if (line.startsWith("gfCount=")) {
                        try { slot.goldenFingerCount = Integer.parseInt(line.substring(8)); } catch (NumberFormatException ignored) {}
                        slot.empty = false;
                    }
                }
            }
        } catch (IOException e) {
            // Ignore - start fresh
        }
    }

    /** Save all slots to the file. */
    public static void saveToFile() {
        Path file = getLoadoutFile();
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < NUM_SLOTS; i++) {
                Loadout slot = slots[i];
                writer.write("[slot" + i + "]");
                writer.newLine();
                if (!slot.empty) {
                    writer.write("name=" + (slot.name == null ? "" : slot.name));
                    writer.newLine();
                    StringBuilder perks = new StringBuilder();
                    for (int j = 0; j < slot.perkIds.size(); j++) {
                        if (j > 0) perks.append(",");
                        perks.append(slot.perkIds.get(j));
                    }
                    writer.write("perks=" + perks.toString());
                    writer.newLine();
                    writer.write("identity=" + (slot.identityId == null ? "" : slot.identityId));
                    writer.newLine();
                    writer.write("items=" + (slot.startingItems == null ? "" : slot.startingItems));
                    writer.newLine();
                    writer.write("gfCount=" + slot.goldenFingerCount);
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    /** Get a loadout slot (0-indexed). Returns a copy. */
    public static Loadout getSlot(int index) {
        if (index < 0 || index >= NUM_SLOTS) return new Loadout();
        return slots[index].copy();
    }

    /** Save data to a slot. */
    public static void saveSlot(int index, String name, List<Integer> perkIds,
                                 String identityId, String startingItems, int gfCount) {
        if (index < 0 || index >= NUM_SLOTS) return;
        Loadout slot = slots[index];
        slot.name = name;
        slot.perkIds = new ArrayList<>(perkIds);
        slot.identityId = identityId != null ? identityId : "";
        slot.startingItems = startingItems != null ? startingItems : "";
        slot.goldenFingerCount = gfCount;
        slot.empty = false;
        saveToFile();
    }

    /** Delete a slot. */
    public static void deleteSlot(int index) {
        if (index < 0 || index >= NUM_SLOTS) return;
        slots[index] = new Loadout();
        saveToFile();
    }

    /** Check if a slot is empty. */
    public static boolean isSlotEmpty(int index) {
        if (index < 0 || index >= NUM_SLOTS) return true;
        return slots[index].empty;
    }

    /** Get the number of slots. */
    public static int getNumSlots() {
        return NUM_SLOTS;
    }
}
