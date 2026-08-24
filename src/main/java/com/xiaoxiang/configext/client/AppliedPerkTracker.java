package com.xiaoxiang.configext.client;

import java.util.*;

/**
 * Tracks which perks have been applied to the player, for display in the cultivation screen.
 * Perks are stored by ID and can be queried for display in the stats panel.
 */
public class AppliedPerkTracker {

    private static final Set<Integer> appliedPerkIds = new LinkedHashSet<>();
    private static final List<String> appliedPerkNames = new ArrayList<>();

    /** Record that a set of perks has been applied. */
    public static void onPerksApplied(Set<Integer> perkIds) {
        appliedPerkIds.clear();
        appliedPerkNames.clear();
        appliedPerkIds.addAll(perkIds);
        for (int id : perkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(id);
            if (perk != null) {
                appliedPerkNames.add(perk.name);
            }
        }
    }

    /** Get all applied perk IDs. */
    public static Set<Integer> getAppliedPerkIds() {
        return Collections.unmodifiableSet(appliedPerkIds);
    }

    /** Get all applied perk display names. */
    public static List<String> getAppliedPerkNames() {
        return Collections.unmodifiableList(appliedPerkNames);
    }

    /** Get all applied perks as Perk objects. */
    public static List<GoldenFingerPerks.Perk> getAppliedPerks() {
        List<GoldenFingerPerks.Perk> result = new ArrayList<>();
        for (int id : appliedPerkIds) {
            GoldenFingerPerks.Perk perk = GoldenFingerPerks.getById(id);
            if (perk != null) result.add(perk);
        }
        return result;
    }

    /** Check if any perks are applied. */
    public static boolean hasPerks() {
        return !appliedPerkIds.isEmpty();
    }

    /** Clear all applied perks (e.g., on reincarnation). */
    public static void clear() {
        appliedPerkIds.clear();
        appliedPerkNames.clear();
    }
}
