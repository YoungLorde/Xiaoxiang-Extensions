package com.xiaoxiang.configext.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import java.util.*;

/**
 * Custom config screen with:
 * - Search bar at top with live filtering
 * - 6 top-level tabs (Cultivation, World, NPCs, Crafting, System, UI)
 * - Nested sub-tabs within each top-level tab
 * - Group dropdowns within sub-tabs (3rd level navigation)
 * - Color theme dropdown for color configs
 * - Real-time preview panel for UI settings
 * - Detailed descriptions and tooltips for every option
 * - Editable values with +/- buttons and direct text input
 */
@OnlyIn(Dist.CLIENT)
public class CustomConfigScreen extends Screen {

    private final Screen parent;
    private EditBox searchBox;
    private String activeTopTab = "Cultivation";
    private String activeSubTab = "General";
    private String activeGroup = "";
    private int scrollOffset = 0;
    private List<DisplayEntry> filteredEntries = new ArrayList<>();
    private boolean searchMode = false;
    private int topTabRowsEndY = 52; // Y position where sub-tabs start (updated dynamically)
    private int selectedEntryIdx = -1; // Currently selected entry for Ctrl+scroll editing
    // Tooltip keyword system state
    private String hoveredKeyword = null;
    private long keywordHoverStart = 0;
    private boolean keywordPopupPinned = false;
    private String pinnedKeyword = null;
    private static final long KEYWORD_HOVER_DELAY_MS = 3000; // 3 seconds to show popup
    // Keyword popup draw is deferred to the very end of render() (after everything else,
    // including all later entry rows) so later-drawn rows can never paint over it and make
    // it look translucent/see-through. See renderEntryTooltip() / renderKeywordPopup().
    private boolean pendingKeywordPopup = false;
    private int pendingKwAnchorX, pendingKwAnchorY, pendingKwAnchorW, pendingKwAnchorH;
    // Search text persists across screen reopen within the same session
    private static String savedSearchText = "";
    // Per-tab search memory
    private static final java.util.Map<String, String> perTabSearchMemory = new java.util.HashMap<>();

    // ── Shared layout state (see LayoutEngine) ──
    // These four fields are the ONLY place the screen stores geometry. render(),
    // mouseClicked(), mouseScrolled() and the tooltip pass all read them; none of
    // them is allowed to recompute a Y or a height for the same visual row again.
    /** Group buttons (3rd level nav). Rects + labels are index-parallel. */
    private LayoutEngine.ButtonRow groupBar = null;
    /** Sub-tab bar rects, produced by the same wrap loop the renderer draws with. */
    private LayoutEngine.ButtonRow subTabBar = null;
    /** The definitive entry row list for the current filtered entry set. */
    private LayoutEngine.Layout entryLayout = null;
    /** Set whenever anything that affects the row list changes. */
    private boolean layoutDirty = true;
    /** Cheap change-detector so the layout is not rebuilt on every single frame. */
    private int layoutSignature = Integer.MIN_VALUE;

    /** Hover / tab-transition animation driver. */
    private final AnimationState anim = new AnimationState();

    // Track top-level tab button rects for custom rendering + click detection
    private final List<int[]> topTabRects = new ArrayList<>();

    // Track sub-tab button rects for custom rendering + click detection
    private final List<int[]> subTabRects = new ArrayList<>();

    // Item picker popup for starting items configs
    private final ItemPickerPopup itemPicker = new ItemPickerPopup();

    // Name input popup for duplicating identities
    private final NameInputPopup nameInput = new NameInputPopup();

    // Batch 12: Color wheel, dropdown enum, multi-line editor
    private final ColorWheelPopup colorWheel = new ColorWheelPopup();
    private final DropdownEnumPopup dropdownEnum = new DropdownEnumPopup();
    private final MultiLineEditor multiLineEditor = new MultiLineEditor();

    // Batch 13: Minimap navigation
    private final MinimapNav minimapNav = new MinimapNav();

    // ── Persistent draggable tooltip system ──
    // After hovering over a tab/sub-tab/group for 5 seconds, the tooltip becomes
    // a draggable, persistent window with a close button.
    private static class PinnedTooltip {
        String title;
        List<FormattedCharSequence> lines;
        int x, y;
        int width, height;
        boolean dragging;
        boolean resizing;
        int dragOffsetX, dragOffsetY;
        long pinTime;
        int scrollOffset = 0;

        PinnedTooltip(String title, List<FormattedCharSequence> lines, int x, int y, int width, int height) {
            this.title = title;
            this.lines = lines;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.pinTime = System.currentTimeMillis();
        }
    }
    // Support multiple pinned tooltips simultaneously
    private final List<PinnedTooltip> pinnedTooltips = new ArrayList<>();
    // Per-title memory: remembers position and shape for each tooltip title
    private static final java.util.Map<String, int[]> tooltipMemory = new java.util.HashMap<>();
    private static boolean tooltipMemoryLoaded = false;

    // ── Undo/Redo system for config changes ──
    private static final java.util.Deque<String[]> undoStack = new java.util.ArrayDeque<>(); // {path, oldValue}
    private static final java.util.Deque<String[]> redoStack = new java.util.ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    // ── Favorites and locked configs ──
    private static final java.util.Set<String> favorites = new java.util.HashSet<>();
    private static final java.util.Set<String> lockedConfigs = new java.util.HashSet<>();
    private static boolean userDataLoaded = false;

    // ── Collapsed sections (category prefixes that are collapsed) ──
    // Keyed by "topTab.subTab.group", not by the bare group name. Several different
    // tabs reuse the same group name (e.g. "General" appears as a group under multiple
    // categories), and this set is static (persists for the whole game session). With a
    // bare-name key, collapsing "General" in one tab silently collapsed - and hid every
    // entry in - every other tab's same-named group too, which is indistinguishable from
    // that other tab/section rendering as "completely empty".
    private static final java.util.Set<String> collapsedSections = new java.util.HashSet<>();

    private String collapseKey(String group) {
        return activeTopTab + "." + activeSubTab + "." + group;
    }

    // ── Slider drag state (for drag-to-scrub on numeric values) ──
    private boolean sliderDragging = false;
    private int sliderDragEntryIdx = -1;
    private double sliderDragStartX = 0;
    private double sliderDragStartVal = 0;

    // ── Value change flash animation ──
    // Tracks which entries recently changed and when (for green flash animation)
    private static final java.util.Map<String, Long> valueChangeFlash = new java.util.HashMap<>();
    private static final long FLASH_DURATION_MS = 600; // 600ms green flash

    // ── Smooth scroll state ──
    private double smoothScrollOffset = 0; // Interpolated scroll position
    private int targetScrollOffset = 0;    // Target scroll position

    // ── Save confirmation animation ──
    private long saveAnimStart = 0;
    private static final long SAVE_ANIM_DURATION_MS = 1500; // 1.5s save confirmation

    // ── Config change history (for recently changed panel) ──
    private static final java.util.Deque<String> recentChanges = new java.util.ArrayDeque<>();
    private static final int MAX_RECENT = 10;

    // ── Search history ──
    private static final java.util.Deque<String> searchHistory = new java.util.ArrayDeque<>();
    private static final int MAX_SEARCH_HISTORY = 5;

    // ── Search filters ──
    private enum SearchFilter { ALL, MODIFIED, BOOLEAN, NUMERIC, COLOR, STRING, OVERMAX, FAVORITES }
    private SearchFilter activeFilter = SearchFilter.ALL;
    private boolean globalSearch = false;

    /** Load tooltip memory from config file (called once on first screen open). */
    private void loadTooltipMemory() {
        if (tooltipMemoryLoaded) return;
        tooltipMemoryLoaded = true;
        try {
            String raw = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_TOOLTIP_MEMORY.get();
            if (raw != null && !raw.isEmpty()) {
                for (String entry : raw.split(";")) {
                    String[] parts = entry.split("\\|");
                    if (parts.length == 5) {
                        String title = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int w = Integer.parseInt(parts[3]);
                        int h = Integer.parseInt(parts[4]);
                        tooltipMemory.put(title, new int[]{x, y, w, h});
                    }
                }
            }
        } catch (Exception e) { /* ignore corrupt config */ }
        // Load user data (favorites, locks, search history)
        loadUserData();
    }

    /** Load favorites, locked configs, and search history from config. */
    private void loadUserData() {
        if (userDataLoaded) return;
        userDataLoaded = true;
        try {
            String favRaw = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_FAVORITES.get();
            if (favRaw != null && !favRaw.isEmpty()) {
                for (String f : favRaw.split(",")) {
                    if (!f.trim().isEmpty()) favorites.add(f.trim());
                }
            }
            String lockRaw = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_LOCKED_CONFIGS.get();
            if (lockRaw != null && !lockRaw.isEmpty()) {
                for (String l : lockRaw.split(",")) {
                    if (!l.trim().isEmpty()) lockedConfigs.add(l.trim());
                }
            }
            String searchRaw = com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_SEARCH_HISTORY.get();
            if (searchRaw != null && !searchRaw.isEmpty()) {
                for (String s : searchRaw.split(",")) {
                    if (!s.trim().isEmpty()) {
                        searchHistory.add(s.trim());
                        if (searchHistory.size() >= MAX_SEARCH_HISTORY) break;
                    }
                }
            }
        } catch (Exception e) { /* ignore */ }
    }

    /** Save favorites and locked configs to config file. */
    private void saveUserData() {
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_FAVORITES.set(String.join(",", favorites));
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_LOCKED_CONFIGS.set(String.join(",", lockedConfigs));
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_SEARCH_HISTORY.set(String.join(",", searchHistory));
        } catch (Exception e) { /* ignore */ }
    }

    /** Record a config change for undo. */
    private void recordConfigChange(String path, String oldValue) {
        undoStack.push(new String[]{path, oldValue});
        if (undoStack.size() > MAX_HISTORY) undoStack.removeLast();
        redoStack.clear();
        // Track recent changes
        recentChanges.remove(path);
        recentChanges.push(path);
        if (recentChanges.size() > MAX_RECENT) recentChanges.removeLast();
    }

    /** Undo the last config change. */
    private void undoConfigChange() {
        if (undoStack.isEmpty()) return;
        String[] entry = undoStack.pop();
        String path = entry[0];
        String oldValue = entry[1];
        String currentVal = ConfigValueAccessor.getValueString(path);
        redoStack.push(new String[]{path, currentVal});
        ConfigValueAccessor.setValueFromString(path, oldValue);
    }

    /** Redo the last undone config change. */
    private void redoConfigChange() {
        if (redoStack.isEmpty()) return;
        String[] entry = redoStack.pop();
        String path = entry[0];
        String newValue = entry[1];
        String currentVal = ConfigValueAccessor.getValueString(path);
        undoStack.push(new String[]{path, currentVal});
        ConfigValueAccessor.setValueFromString(path, newValue);
    }

    /** Toggle favorite status for a config path. */
    private void toggleFavorite(String path) {
        if (favorites.contains(path)) favorites.remove(path);
        else favorites.add(path);
        saveUserData();
    }

    /** Trigger a value change flash animation for a config path. */
    private void flashValueChange(String path) {
        valueChangeFlash.put(path, System.currentTimeMillis());
        // Show notification for value change
        String newVal = ConfigValueAccessor.getValueString(path);
        String displayName = prettifyLastSegment(path);
        NotificationSystem.showSuccess(displayName + " = " + newVal);
    }

    /** Check if a config path is currently flashing (recently changed). */
    private boolean isFlashing(String path) {
        Long flashTime = valueChangeFlash.get(path);
        if (flashTime == null) return false;
        long elapsed = System.currentTimeMillis() - flashTime;
        if (elapsed > FLASH_DURATION_MS) {
            valueChangeFlash.remove(path);
            return false;
        }
        return true;
    }

    /** Get the flash intensity (0..1) for a config path. */
    private float getFlashIntensity(String path) {
        Long flashTime = valueChangeFlash.get(path);
        if (flashTime == null) return 0f;
        long elapsed = System.currentTimeMillis() - flashTime;
        if (elapsed > FLASH_DURATION_MS) return 0f;
        return 1.0f - (float) elapsed / FLASH_DURATION_MS;
    }

    /** Toggle lock status for a config path. */
    private void toggleLock(String path) {
        if (lockedConfigs.contains(path)) lockedConfigs.remove(path);
        else lockedConfigs.add(path);
        saveUserData();
    }

    /** Check if a config is locked. */
    private boolean isLocked(String path) {
        return lockedConfigs.contains(path);
    }

    /** Wrapped config value setter that records the change for undo. */
    private void setConfigValue(String path, String newValue) {
        if (isLocked(path)) return; // Don't change locked configs
        String oldVal = ConfigValueAccessor.getValueString(path);
        ConfigValueAccessor.setValueFromString(path, newValue);
        recordConfigChange(path, oldVal);
    }

    /** Wrapped increment that records the change for undo. */
    private void incrementConfig(String path, double step) {
        if (isLocked(path)) return;
        String oldVal = ConfigValueAccessor.getValueString(path);
        ConfigValueAccessor.increment(path, step);
        recordConfigChange(path, oldVal);
    }

    /** Wrapped decrement that records the change for undo. */
    private void decrementConfig(String path, double step) {
        if (isLocked(path)) return;
        String oldVal = ConfigValueAccessor.getValueString(path);
        ConfigValueAccessor.decrement(path, step);
        recordConfigChange(path, oldVal);
    }

    /** Wrapped toggle that records the change for undo. */
    private void toggleConfig(String path) {
        if (isLocked(path)) return;
        String oldVal = ConfigValueAccessor.getValueString(path);
        ConfigValueAccessor.toggle(path);
        recordConfigChange(path, oldVal);
    }

    /** Save tooltip memory to config file. */
    private void saveTooltipMemory() {
        try {
            StringBuilder sb = new StringBuilder();
            for (var entry : tooltipMemory.entrySet()) {
                if (sb.length() > 0) sb.append(";");
                int[] v = entry.getValue();
                sb.append(entry.getKey()).append("|").append(v[0]).append("|").append(v[1])
                  .append("|").append(v[2]).append("|").append(v[3]);
            }
            com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_TOOLTIP_MEMORY.set(sb.toString());
        } catch (Exception e) { /* ignore */ }
    }
    private String hoverTooltipKey = null;
    private long hoverTooltipStartTime = 0;
    private static final long TOOLTIP_PIN_DELAY = 3000; // 3 seconds

    // Tab button positions
    private static final int TAB_WIDTH = 82;
    private static final int TAB_HEIGHT = 18;
    private static final int SUBTAB_WIDTH = 90;
    private static final int SUBTAB_HEIGHT = 18;
    private static final int TAB_GAP = 2;
    private static final int TAB_ROW_SPACING = 20;

    // ── Top-level tab -> sub-tab -> group mapping ──
    // This maps the actual config path prefixes to our tab structure
    private static final Map<String, String> PATH_PREFIX_TO_TAB = new LinkedHashMap<>();
    private static final Map<String, List<String>> TAB_TO_SUBTABS = new LinkedHashMap<>();
    private static final Map<String, String> SUBTAB_TO_PATH_PREFIX = new LinkedHashMap<>();

    // ── Sects -> Schedule: named times of day instead of raw ticks ─────────
    // Six evenly-spaced (4000-tick) anchors across the 0-24000 in-game day,
    // dawn through midnight, used to label the six sectSchedule fields that
    // represent a POINT in the day. sectSchedule.dayTicks is deliberately not
    // in this list - it's "ticks per sect day" (a duration), not a moment.
    private static final int[] SECT_TIME_ANCHOR_TICKS = {0, 4000, 8000, 12000, 16000, 20000};
    private static final String[] SECT_TIME_ANCHOR_NAMES = {"Dawn", "Morning", "Midday", "Dusk", "Evening", "Midnight"};
    private static final java.util.Set<String> SECT_SCHEDULE_TIME_PATHS = new java.util.HashSet<>(java.util.Arrays.asList(
            "sectSchedule.morningEndExclusive",
            "sectSchedule.nightStart",
            "sectSchedule.firstShiftStart",
            "sectSchedule.firstShiftEnd",
            "sectSchedule.secondShiftStart",
            "sectSchedule.secondShiftEnd"
    ));

    static {
        // ══════════════════════════════════════════════════════════════════
        //  MAIN TABS with sub-tabs — ALL paths correspond to real config sections
        // ══════════════════════════════════════════════════════════════════

        // ── 1. CULTIVATION ──
        PATH_PREFIX_TO_TAB.put("general", "Cultivation");
        PATH_PREFIX_TO_TAB.put("realms", "Cultivation");
        PATH_PREFIX_TO_TAB.put("spiritRoots", "Cultivation");
        PATH_PREFIX_TO_TAB.put("physiques", "Cultivation");
        PATH_PREFIX_TO_TAB.put("foundationDao", "Cultivation");
        PATH_PREFIX_TO_TAB.put("goldenCoreDao", "Cultivation");
        PATH_PREFIX_TO_TAB.put("techniques", "Cultivation");
        PATH_PREFIX_TO_TAB.put("progression", "Cultivation");
        PATH_PREFIX_TO_TAB.put("progressionRules", "Cultivation");
        PATH_PREFIX_TO_TAB.put("identity", "Cultivation");
        PATH_PREFIX_TO_TAB.put("identityDraw", "Cultivation");
        // GAP FIX: "cultivationData" and "goldenFinger" are real top-level sections in
        // ExtendedConfig (see the CULTIVATION DATA / goldenFinger push() blocks) that had
        // no tab at all, so every setting inside them was unreachable from this screen.
        PATH_PREFIX_TO_TAB.put("cultivationData", "Cultivation");
        PATH_PREFIX_TO_TAB.put("goldenFinger", "Cultivation");

        TAB_TO_SUBTABS.put("Cultivation", List.of(
            "General", "Realms", "Spirit Roots", "Physiques",
            "Foundation Dao", "Golden Core Dao", "Techniques",
            "Progression", "Progression Rules", "Core Data", "Golden Finger",
            "Identity", "Identity Draw"));

        SUBTAB_TO_PATH_PREFIX.put("Cultivation.General", "general");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Realms", "realms");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Spirit Roots", "spiritRoots");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Physiques", "physiques");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Foundation Dao", "foundationDao");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Golden Core Dao", "goldenCoreDao");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Techniques", "techniques");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Progression", "progression");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Progression Rules", "progressionRules");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Core Data", "cultivationData");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Golden Finger", "goldenFinger");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Identity", "identity");
        SUBTAB_TO_PATH_PREFIX.put("Cultivation.Identity Draw", "identityDraw");

        // ── 2. SPELLS & COMBAT ──
        PATH_PREFIX_TO_TAB.put("spells", "Spells & Combat");
        PATH_PREFIX_TO_TAB.put("passiveSpells", "Spells & Combat");
        PATH_PREFIX_TO_TAB.put("weapons", "Spells & Combat");

        TAB_TO_SUBTABS.put("Spells & Combat", List.of(
            "Spells", "Passive Spells", "Weapons"));

        SUBTAB_TO_PATH_PREFIX.put("Spells & Combat.Spells", "spells");
        SUBTAB_TO_PATH_PREFIX.put("Spells & Combat.Passive Spells", "passiveSpells");
        SUBTAB_TO_PATH_PREFIX.put("Spells & Combat.Weapons", "weapons");

        // ── 3. SECTS ──
        PATH_PREFIX_TO_TAB.put("sects", "Sects");
        PATH_PREFIX_TO_TAB.put("sectLife", "Sects");
        PATH_PREFIX_TO_TAB.put("sectTasks", "Sects");
        PATH_PREFIX_TO_TAB.put("sectDepartments", "Sects");
        PATH_PREFIX_TO_TAB.put("sectAmbient", "Sects");
        PATH_PREFIX_TO_TAB.put("sectJourney", "Sects");
        PATH_PREFIX_TO_TAB.put("sectDefense", "Sects");
        PATH_PREFIX_TO_TAB.put("sectSchedule", "Sects");
        PATH_PREFIX_TO_TAB.put("sectOverhead", "Sects");
        PATH_PREFIX_TO_TAB.put("sectProfile", "Sects");

        TAB_TO_SUBTABS.put("Sects", List.of(
            "Generation", "Ancestor Chances", "Shop Pricing",
            "Sect Ambient", "Sect Tasks", "Size Tiers", "NPC Population",
            "Crouch Meditation",
            "Life & Population", "Duty Tasks", "Departments",
            "Ambient Social", "Journeys", "Defense",
            "Schedule", "Overhead UI", "Cultivation Profile"));

        SUBTAB_TO_PATH_PREFIX.put("Sects.Generation", "sects.generation");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Ancestor Chances", "sects.ancestorChances");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Shop Pricing", "sects.shop");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Sect Ambient", "sects.ambient");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Sect Tasks", "sects.tasks");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Size Tiers", "sects.sizeTiers");
        SUBTAB_TO_PATH_PREFIX.put("Sects.NPC Population", "sects.npcPopulation");
        // GAP FIX: sects.crouchMeditation had no sub-tab, so the crouch-meditation
        // enable/multiplier settings were unreachable.
        SUBTAB_TO_PATH_PREFIX.put("Sects.Crouch Meditation", "sects.crouchMeditation");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Life & Population", "sectLife");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Duty Tasks", "sectTasks");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Departments", "sectDepartments");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Ambient Social", "sectAmbient");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Journeys", "sectJourney");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Defense", "sectDefense");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Schedule", "sectSchedule");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Overhead UI", "sectOverhead");
        SUBTAB_TO_PATH_PREFIX.put("Sects.Cultivation Profile", "sectProfile");

        // ── 4. NPCs ──
        PATH_PREFIX_TO_TAB.put("spawns", "NPCs");
        PATH_PREFIX_TO_TAB.put("npcCombat", "NPCs");
        PATH_PREFIX_TO_TAB.put("npcAi", "NPCs");
        PATH_PREFIX_TO_TAB.put("npcTrades", "NPCs");

        TAB_TO_SUBTABS.put("NPCs", List.of(
            "Spawns", "Combat Tactics", "AI Behavior", "Trades"));

        SUBTAB_TO_PATH_PREFIX.put("NPCs.Spawns", "spawns");
        SUBTAB_TO_PATH_PREFIX.put("NPCs.Combat Tactics", "npcCombat");
        SUBTAB_TO_PATH_PREFIX.put("NPCs.AI Behavior", "npcAi");
        SUBTAB_TO_PATH_PREFIX.put("NPCs.Trades", "npcTrades");

        // ── 5. BEASTS & MOBS ──
        PATH_PREFIX_TO_TAB.put("beasts", "Beasts & Mobs");
        // NOTE: "spawns" is deliberately NOT re-mapped here. It is already registered
        // above as NPCs, and this map is a LinkedHashMap - a second put() silently
        // overwrote the NPCs mapping, so global-search "which tab owns this path?"
        // lookups sent every spawns.* entry to Beasts & Mobs. The
        // "Beasts & Mobs.NPC Spawns" sub-tab below still routes to the same prefix.

        TAB_TO_SUBTABS.put("Beasts & Mobs", List.of(
            "Beast Cultivation", "Beast Advance Costs", "NPC Spawns"));

        SUBTAB_TO_PATH_PREFIX.put("Beasts & Mobs.Beast Cultivation", "beasts");
        SUBTAB_TO_PATH_PREFIX.put("Beasts & Mobs.Beast Advance Costs", "beasts.advanceCost");
        SUBTAB_TO_PATH_PREFIX.put("Beasts & Mobs.NPC Spawns", "spawns");

        // ── 6. WORLD ──
        PATH_PREFIX_TO_TAB.put("qiDensity", "World");
        PATH_PREFIX_TO_TAB.put("spiritVeins", "World");
        PATH_PREFIX_TO_TAB.put("spiritPlants", "World");
        PATH_PREFIX_TO_TAB.put("loot", "World");

        TAB_TO_SUBTABS.put("World", List.of(
            "Biome Qi", "Spirit Veins", "Spirit Plants",
            "Loot"));

        SUBTAB_TO_PATH_PREFIX.put("World.Biome Qi", "qiDensity");
        SUBTAB_TO_PATH_PREFIX.put("World.Spirit Veins", "spiritVeins");
        SUBTAB_TO_PATH_PREFIX.put("World.Spirit Plants", "spiritPlants");
        SUBTAB_TO_PATH_PREFIX.put("World.Loot", "loot");

        // ── 7. CRAFTING ──
        PATH_PREFIX_TO_TAB.put("alchemy", "Crafting");
        PATH_PREFIX_TO_TAB.put("refining", "Crafting");
        PATH_PREFIX_TO_TAB.put("pills", "Crafting");

        TAB_TO_SUBTABS.put("Crafting", List.of(
            "Alchemy", "Refining", "Pills"));

        SUBTAB_TO_PATH_PREFIX.put("Crafting.Alchemy", "alchemy");
        SUBTAB_TO_PATH_PREFIX.put("Crafting.Refining", "refining");
        SUBTAB_TO_PATH_PREFIX.put("Crafting.Pills", "pills");

        // ── 8. TRIALS ──
        PATH_PREFIX_TO_TAB.put("trials", "Trials");
        PATH_PREFIX_TO_TAB.put("looseImmortal", "Trials");

        TAB_TO_SUBTABS.put("Trials", List.of(
            "Heart Demon", "Inner World", "Loose Immortal"));

        SUBTAB_TO_PATH_PREFIX.put("Trials.Heart Demon", "trials.heartDemon");
        SUBTAB_TO_PATH_PREFIX.put("Trials.Inner World", "trials.innerWorld");
        SUBTAB_TO_PATH_PREFIX.put("Trials.Loose Immortal", "looseImmortal");

        // ── 9. QI SYSTEM ──
        PATH_PREFIX_TO_TAB.put("qiSystem", "Qi System");

        TAB_TO_SUBTABS.put("Qi System", List.of(
            "Qi Attraction", "Qi Shield", "Spirit Stone Ore"));

        SUBTAB_TO_PATH_PREFIX.put("Qi System.Qi Attraction", "qiSystem.playerConsumer");
        SUBTAB_TO_PATH_PREFIX.put("Qi System.Qi Shield", "qiSystem.qiShield");
        SUBTAB_TO_PATH_PREFIX.put("Qi System.Spirit Stone Ore", "qiSystem.spiritStoneOre");

        // ── 10. LIFESPAN ──
        PATH_PREFIX_TO_TAB.put("lifespanHelper", "Lifespan");

        TAB_TO_SUBTABS.put("Lifespan", List.of(
            "Aging & Death"));

        SUBTAB_TO_PATH_PREFIX.put("Lifespan.Aging & Death", "lifespanHelper");

        // ── 11. EFFECTS & MORALITY ──
        PATH_PREFIX_TO_TAB.put("effects", "Effects & Morality");
        PATH_PREFIX_TO_TAB.put("morality", "Effects & Morality");
        // GAP FIX: "moralityBounds" is its own top-level section in ExtendedConfig
        // (min/max morality value) and previously had nowhere to render.
        PATH_PREFIX_TO_TAB.put("moralityBounds", "Effects & Morality");

        TAB_TO_SUBTABS.put("Effects & Morality", List.of(
            "Status Effects", "Morality", "Morality Bounds"));

        SUBTAB_TO_PATH_PREFIX.put("Effects & Morality.Status Effects", "effects");
        SUBTAB_TO_PATH_PREFIX.put("Effects & Morality.Morality", "morality");
        SUBTAB_TO_PATH_PREFIX.put("Effects & Morality.Morality Bounds", "moralityBounds");

        // ── 12. FORMATIONS ──
        PATH_PREFIX_TO_TAB.put("formations", "Formations");
        // GAP FIX: "formationCore" (formation core plate timing / harvest tuning) is a
        // separate top-level section that had no tab entry at all.
        PATH_PREFIX_TO_TAB.put("formationCore", "Formations");

        TAB_TO_SUBTABS.put("Formations", List.of(
            "Core Max Qi", "Qi Gathering", "Growth",
            "Barriers", "Rejuvenation", "Harvest", "Formation Core"));

        SUBTAB_TO_PATH_PREFIX.put("Formations.Core Max Qi", "formations.coreMaxQi");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Qi Gathering", "formations.qiGathering");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Growth", "formations.growth");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Barriers", "formations.qiPerDamage");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Rejuvenation", "formations.rejuvenationAmplifier");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Harvest", "formations.harvestInterval");
        SUBTAB_TO_PATH_PREFIX.put("Formations.Formation Core", "formationCore");

        // ── 13. UI ──
        PATH_PREFIX_TO_TAB.put("client", "UI");

        // GAP FIX: client.general, client.statusBar, client.uiPrefs and client.advanced
        // are all real sections of the CLIENT spec that had no sub-tab, so the
        // "Remove vanilla difficulty button", status-bar row height, Reduce Motion /
        // font size / compact layout, and the whole advanced feature-toggle block were
        // unreachable from this screen. (client.tooltipMemory and client.userData are
        // deliberately still hidden - they are opaque serialised blobs, not settings.)
        // The UI general sub-tab is named "General Toggles" on purpose: refreshEntries()
        // has a special case that pulls in every top-level "general.*" path whenever the
        // ACTIVE SUB-TAB IS LITERALLY CALLED "General", so a second "General" sub-tab
        // here would drag the Cultivation general settings into the UI tab.
        TAB_TO_SUBTABS.put("UI", List.of(
            "General Toggles", "HUD Layout", "Status Bar", "Colors", "Background",
            "Element Pos", "Accessibility", "Preferences", "Advanced"));

        // UI sub-tabs map to client.* sub-categories
        SUBTAB_TO_PATH_PREFIX.put("UI.General Toggles", "client.general");
        SUBTAB_TO_PATH_PREFIX.put("UI.Status Bar", "client.statusBar");
        SUBTAB_TO_PATH_PREFIX.put("UI.Preferences", "client.uiPrefs");
        SUBTAB_TO_PATH_PREFIX.put("UI.Advanced", "client.advanced");
        SUBTAB_TO_PATH_PREFIX.put("UI.HUD Layout", "client.hud");
        SUBTAB_TO_PATH_PREFIX.put("UI.Colors", "client.colors");
        SUBTAB_TO_PATH_PREFIX.put("UI.Background", "client.background");
        SUBTAB_TO_PATH_PREFIX.put("UI.Element Pos", "client.elementPos");
        SUBTAB_TO_PATH_PREFIX.put("UI.Accessibility", "client.accessibility");
    }

    /** A single displayable config entry with its actual config value.
     *  Package-visible (not private) so {@link LayoutEngine} can measure it. */
    static class DisplayEntry {
        final String configPath;
        final ForgeConfigSpec.ConfigValue<?> configValue;
        final ConfigEntryInfo info; // may be null if no description registered
        final String group; // 3rd-level group name

        DisplayEntry(String configPath, ForgeConfigSpec.ConfigValue<?> configValue, ConfigEntryInfo info, String group) {
            this.configPath = configPath;
            this.configValue = configValue;
            this.info = info;
            this.group = group != null ? group : "";
        }

        String getDisplayName() {
            return info != null ? info.displayName : prettifyLastSegment(configPath);
        }

        String getDescription() {
            return info != null ? info.description : "Config option: " + configPath;
        }

        String getLargerEffect() {
            return info != null ? info.largerEffect : "Increase the value";
        }

        String getSmallerEffect() {
            return info != null ? info.smallerEffect : "Decrease the value";
        }

        List<String> getKeywords() {
            return info != null ? info.keywords : List.of(configPath);
        }

        boolean matches(String query) {
            if (query == null || query.isBlank()) return true;
            String q = query.toLowerCase();
            if (getDisplayName().toLowerCase().contains(q)) return true;
            if (configPath.toLowerCase().contains(q)) return true;
            if (getDescription().toLowerCase().contains(q)) return true;
            for (String kw : getKeywords()) {
                if (kw.toLowerCase().contains(q)) return true;
            }
            return false;
        }
    }

    private static String prettifyLastSegment(String path) {
        String[] parts = path.split("\\.");
        String last = parts[parts.length - 1];
        String pretty = last.replaceAll("([a-z])([A-Z])", "$1 $2");
        pretty = pretty.substring(0, 1).toUpperCase() + pretty.substring(1);

        // If the path has a parent category (e.g., npcCombat.dodgeChance.mortal),
        // include it to distinguish entries with the same last segment across categories
        if (parts.length >= 3) {
            String parent = parts[parts.length - 2];
            String parentPretty = parent.replaceAll("([a-z])([A-Z])", "$1 $2");
            parentPretty = parentPretty.substring(0, 1).toUpperCase() + parentPretty.substring(1);
            // Only add parent prefix if the last segment is generic (mortal, qiRefining, etc.)
            // and the parent is a meaningful category name
            if (isGenericSegment(last) && !parentPretty.equals(last)) {
                pretty = parentPretty + " - " + pretty;
            }
        }
        return pretty;
    }

    /** Check if a segment name is generic (used across multiple categories). */
    private static boolean isGenericSegment(String seg) {
        String lower = seg.toLowerCase();
        return lower.equals("mortal") || lower.equals("qirefining") || lower.equals("qi refining") ||
               lower.equals("foundation") || lower.equals("goldencore") || lower.equals("golden core") ||
               lower.equals("nascentsoul") || lower.equals("nascent soul") ||
               lower.equals("soulformation") || lower.equals("soul formation") ||
               lower.equals("voidrefining") || lower.equals("void refining") ||
               lower.equals("higher") || lower.equals("low") || lower.equals("mid") ||
               lower.equals("high") || lower.equals("supreme") || lower.equals("spring") ||
               lower.equals("human") || lower.equals("blood") || lower.equals("earth") ||
               lower.equals("heaven") || lower.equals("min") || lower.equals("max") ||
               lower.equals("default");
    }

    /** Determine the group (3rd-level) for a config path based on its sub-path. */
    private static String getGroupForPath(String path, String subTabPathPrefix) {
        if (subTabPathPrefix.startsWith("client.")) return "";
        String[] parts = path.split("\\.");
        if (parts.length <= 2) return "";
        if (parts[0].equals(subTabPathPrefix) && parts.length > 2) {
            return prettifySegment(parts[1]);
        }
        return "";
    }

    private static String prettifySegment(String seg) {
        return seg.replaceAll("([a-z])([A-Z])", "$1 $2")
                   .substring(0, 1).toUpperCase() + seg.substring(1).replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public CustomConfigScreen(Screen parent) {
        super(Component.literal("Xiaoxiang Config Extension"));
        this.parent = parent;

        // Wire up the duplicate callback - when the user presses Duplicate in the item picker,
        // close the item picker and open the name input popup
        itemPicker.setDuplicateCallback((identityId, baseIdentityId, startingItems) -> {
            // When the identity actually being duplicated is itself a CUSTOM identity
            // (e.g. "custom_academy_student_1234"), source its display name, lifespan,
            // and description from ITS OWN saved values via CustomIdentityManager -
            // otherwise duplicating a custom identity always reset back to the root
            // base identity's defaults (100-120 lifespan, base description) instead of
            // carrying over whatever the source custom identity actually had.
            CustomIdentityManager.CustomIdentity sourceCustom =
                    (identityId != null && identityId.startsWith("custom_"))
                            ? CustomIdentityManager.getById(identityId) : null;

            String baseDisplayName;
            String baseDesc = "";
            int[] lifespanRange;
            if (sourceCustom != null) {
                baseDisplayName = sourceCustom.displayName;
                baseDesc = sourceCustom.description;
                lifespanRange = new int[]{sourceCustom.minLifespan, sourceCustom.maxLifespan};
            } else {
                // Not a custom identity (or its entry couldn't be found) - fall back to
                // the root base identity's defaults, same as before.
                baseDisplayName = prettifyIdentityName(baseIdentityId);
                lifespanRange = getIdentityLifespanRange(baseIdentityId);
                try {
                    com.xiaoxiang.cultivation.cultivation.Identity baseIdentity =
                            com.xiaoxiang.cultivation.cultivation.Identity.byId(baseIdentityId);
                    if (baseIdentity != null) {
                        baseDesc = net.minecraft.network.chat.Component.translatable(
                                baseIdentity.descriptionKey()).getString();
                    }
                } catch (Exception e) { /* use empty */ }
            }
            // Open the name input popup with description
            nameInput.open(baseIdentityId, baseDisplayName, baseDesc, startingItems,
                    lifespanRange[0], lifespanRange[1],
                    (name, desc) -> {
                        // When the user confirms, add the custom identity with description.
                        // baseIdentityId (always the root, non-custom id) is what anchors
                        // the new custom_<baseId>_<timestamp> id to a portrait/translation
                        // key, regardless of whether this duplicate's source was itself
                        // custom or a root identity.
                        CustomIdentityManager.addCustomIdentity(baseIdentityId, name,
                                lifespanRange[0], lifespanRange[1], startingItems, desc);
                        // Refresh the config screen to show the new tab
                        refreshEntries();
                    });
        });

        // Delete callback: refresh entries after a custom identity is deleted
        itemPicker.setDeleteCallback((customId) -> {
            refreshEntries();
        });
    }

    /** Convert an identity ID to a display name (e.g., "academy_student" -> "Academy Student"). */
    private static String prettifyIdentityName(String identityId) {
        if (identityId == null || identityId.isEmpty()) return "Custom Identity";
        String[] parts = identityId.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return sb.toString();
    }

    /** Get the lifespan range [min, max] for an identity from config.
     *  Custom identities (created via a previous Duplicate) carry their OWN min/max
     *  lifespan - which may have been edited away from their base identity's values -
     *  so those must be read from CustomIdentityManager rather than falling through to
     *  the base-identity switch below (which would silently reset a re-duplicated
     *  custom identity back to the 100-120 default). */
    private static int[] getIdentityLifespanRange(String identityId) {
        try {
            if (identityId != null && identityId.startsWith("custom_")) {
                int[] customRange = CustomIdentityManager.getLifespanRange(identityId);
                if (customRange != null) return customRange;
                // Fall through to the base-identity lookup below using the extracted
                // base ID, in case the custom entry itself couldn't be found/parsed.
            }
            String lookupId = (identityId != null && identityId.startsWith("custom_"))
                    ? CustomIdentityManager.extractBaseId(identityId)
                    : identityId;
            String id = lookupId.toLowerCase();
            switch (id) {
                case "lone_cultivator":   return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_LONE_CULTIVATOR_MAX.get()};
                case "merchant_son":      return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_MERCHANT_SON_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_MERCHANT_SON_MAX.get()};
                case "bandit_leader":     return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_BANDIT_LEADER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_BANDIT_LEADER_MAX.get()};
                case "hunter":            return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_HUNTER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_HUNTER_MAX.get()};
                case "doctor_heir":       return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_DOCTOR_HEIR_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_DOCTOR_HEIR_MAX.get()};
                case "hermit_disciple":   return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MAX.get()};
                case "fisherman":         return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_FISHERMAN_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_FISHERMAN_MAX.get()};
                case "farmer":            return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_FARMER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_FARMER_MAX.get()};
                case "abandoned_infant":  return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_ABANDONED_INFANT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_ABANDONED_INFANT_MAX.get()};
                case "general_son":       return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_GENERAL_SON_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_GENERAL_SON_MAX.get()};
                case "exiled_princess":   return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_EXILED_PRINCESS_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_EXILED_PRINCESS_MAX.get()};
                case "pirate":            return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_PIRATE_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_PIRATE_MAX.get()};
                case "beast_descendant":  return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_BEAST_DESCENDANT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_BEAST_DESCENDANT_MAX.get()};
                case "taoist":            return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_TAOIST_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_TAOIST_MAX.get()};
                case "monk":              return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_MONK_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_MONK_MAX.get()};
                case "academy_student":   return new int[]{ExtendedConfig.IDENTITY_LIFESPAN_ACADEMY_STUDENT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_ACADEMY_STUDENT_MAX.get()};
                default:                  return new int[]{100, 120};
            }
        } catch (Exception e) {
            return new int[]{100, 120};
        }
    }

    @Override
    protected void init() {
        // Load tooltip memory from config on first open
        loadTooltipMemory();
        // Load custom tabs, tab order, and config dependencies
        CustomTabManager.loadFromConfig();
        // Drop any custom tab (player-made or expansion-registered) whose
        // path prefixes no longer match a single currently-registered config
        // value - e.g. left behind by an expansion mod that's since been
        // uninstalled. See CustomTabManager.pruneOrphanedTabs() for why this
        // has to run here, every time the screen opens.
        CustomTabManager.pruneOrphanedTabs();
        ConfigDependencyManager.loadFromConfig();
        // Load per-world overrides if enabled
        if (PerWorldOverrideManager.isEnabled()) {
            try {
                PerWorldOverrideManager.loadFromConfig(
                    com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_PER_WORLD_OVERRIDES.get());
            } catch (Exception e) { /* ignore */ }
        }

        // ── Search bar at top ──
        int searchW = Math.min(220, this.width - 40);
        searchBox = new EditBox(this.font, this.width / 2 - searchW / 2, 8, searchW, 18,
                Component.literal("Search..."));
        searchBox.setHint(Component.literal("Search settings..."));
        searchBox.setResponder(this::onSearchChanged);
        // Restore saved search text from previous session
        if (!savedSearchText.isEmpty()) {
            searchBox.setValue(savedSearchText);
            searchMode = true;
        }
        addWidget(searchBox);

        // ── Top-level tab buttons (custom rendered with glow style) ──
        topTabRects.clear();
        int tabX = 8;
        int tabY = 44; // Moved down to clear filter row (Y=28-40)
        int maxTabX = this.width - TAB_WIDTH - 8;
        // Use CustomTabManager order (same as rendering), filtered to valid tabs
        List<String> orderedTabs = new ArrayList<>(CustomTabManager.getTabOrder());
        orderedTabs.removeIf(name -> !TAB_TO_SUBTABS.containsKey(name) && !CustomTabManager.isCustomTab(name));
        for (String tab : orderedTabs) {
            // Wrap to next row if we'd overflow
            if (tabX > maxTabX) {
                tabX = 8;
                tabY += TAB_HEIGHT + TAB_GAP;
            }
            int x = tabX;
            int y = tabY;
            topTabRects.add(new int[]{x, y, TAB_WIDTH, TAB_HEIGHT});
            tabX += TAB_WIDTH + TAB_GAP;
        }
        // Calculate the Y position for sub-tabs (below the last row of main tabs)
        topTabRowsEndY = tabY + TAB_HEIGHT + 2;

        // ── Sub-tab buttons (second row) ──
        rebuildSubTabButtons();

        // ── Done button ──
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            onClose();
        }).bounds(this.width - 80, this.height - 28, 70, 20).build());

        // Note: Vanilla "Reset Tab" button removed - we have a custom-drawn one
        // in the utility button row at the bottom that handles reset functionality.

        refreshEntries();
    }

    /** Right-hand limit the sub-tab and group button rows wrap against. */
    private int getButtonBarMaxRight() {
        return this.width - 20;
    }

    /**
     * Lay out the sub-tab bar AND the group bar through {@link LayoutEngine}, and
     * store the resulting rects. Previously the sub-tab wrap loop lived here while a
     * separate approximate formula in getSubTabRowCount() estimated the same row
     * count, and the group bar was laid out three more times (render / click /
     * tooltip) with three slightly different loops. Everything now comes out of one
     * computation, so the bars can never disagree about where a button is.
     */
    private void rebuildSubTabButtons() {
        List<String> subTabs = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
        subTabBar = LayoutEngine.layoutFixedButtons(subTabs, 8, topTabRowsEndY + 4,
                getButtonBarMaxRight(), SUBTAB_WIDTH, SUBTAB_HEIGHT, 3, 2);

        // Keep the legacy rect list in sync - other code (tooltips) reads it.
        subTabRects.clear();
        subTabRects.addAll(subTabBar.rects);

        rebuildGroupButtons();
        invalidateLayout();
    }

    /**
     * Lay out the 3rd-level group bar. The "All" button is just the first label, so
     * it wraps and hit-tests with exactly the same rule as every other group button.
     */
    private void rebuildGroupButtons() {
        if (this.font == null) {
            groupBar = null;
            return;
        }
        if (searchMode) {
            groupBar = null;
            return;
        }
        List<String> groups = getGroupsForCurrentSubTab();
        if (groups.isEmpty()) {
            groupBar = null;
            return;
        }
        List<String> labels = new ArrayList<>();
        labels.add(GROUP_ALL_LABEL);
        labels.addAll(groups);
        groupBar = LayoutEngine.layoutTextButtons(labels, this.font, 8,
                getSubTabBarBottom() + 2, getButtonBarMaxRight(), 50, 8, 16, 3, 2);
    }

    /** Label used for the "show every group" button in the group bar. */
    private static final String GROUP_ALL_LABEL = "All";

    /** Y just past the sub-tab bar - the real value from the real wrap loop. */
    private int getSubTabBarBottom() {
        if (subTabBar == null) return topTabRowsEndY + 4 + SUBTAB_HEIGHT + 2;
        return subTabBar.bottomY;
    }

    /** Width available for entry rows: reserves space on the right for the Nav panel
     *  (when toggled on), so entry text is never drawn underneath it. */
    private int getEntryWidth() {
        int w = this.width - 20;
        if (minimapNav.isVisible()) {
            w = Math.min(w, this.width - 150);
        }
        return w;
    }

    /** Y where the entry list starts, derived from the real button-bar geometry. */
    private int getEntryYStart() {
        int y = (groupBar != null && groupBar.size() > 0)
                ? groupBar.bottomY + 4
                : getSubTabBarBottom() + 4;
        // Safety net: never let the computed start position get close enough to the bottom
        // of the screen to invert the entries scissor rect (clipBottom = this.height - 30).
        // If it does, entries would silently stop rendering - no exception, just a blank tab.
        return Math.min(y, this.height - 60);
    }

    /** Bottom edge (exclusive) of the entry viewport. */
    private int getEntryClipBottom() {
        return this.height - 30;
    }

    /** Visible pixel height of the entry viewport. */
    private int getEntryViewportHeight() {
        return Math.max(0, getEntryClipBottom() - getEntryYStart());
    }

    /** Force the shared row list to be rebuilt on the next access. */
    private void invalidateLayout() {
        layoutDirty = true;
    }

    /**
     * Return the shared row list, rebuilding it only when something that affects it
     * has actually changed. Every render / click / scroll path goes through here.
     */
    private LayoutEngine.Layout layout() {
        if (this.font == null) {
            if (entryLayout == null) {
                entryLayout = LayoutEngine.computeEntryLayout(
                        java.util.Collections.emptyList(), 0, 8, 8, null,
                        getEntryHeight(), false, null, null);
            }
            return entryLayout;
        }
        int entryYStart = getEntryYStart();
        int entryWidth = getEntryWidth();
        int sig = 17;
        sig = 31 * sig + filteredEntries.size();
        sig = 31 * sig + this.width;
        sig = 31 * sig + this.height;
        sig = 31 * sig + entryYStart;
        sig = 31 * sig + entryWidth;
        sig = 31 * sig + getEntryHeight();
        sig = 31 * sig + collapsedSections.size();
        sig = 31 * sig + (searchMode ? 1 : 0);
        sig = 31 * sig + activeTopTab.hashCode();
        sig = 31 * sig + activeSubTab.hashCode();
        sig = 31 * sig + activeGroup.hashCode();

        if (!layoutDirty && entryLayout != null && sig == layoutSignature) {
            return entryLayout;
        }
        layoutSignature = sig;
        layoutDirty = false;
        entryLayout = LayoutEngine.computeEntryLayout(
                filteredEntries, entryYStart, 8, entryWidth, this.font,
                getEntryHeight(), !searchMode,
                grp -> collapsedSections.contains(collapseKey(grp)),
                this::getGroupDescription);
        // Scroll may now point past the end of a shorter list.
        clampScroll();
        return entryLayout;
    }

    /** Keep scrollOffset (a ROW index into the shared row list) in range. */
    private void clampScroll() {
        if (entryLayout == null || entryLayout.isEmpty()) {
            scrollOffset = 0;
            return;
        }
        int max = entryLayout.maxFirstRow(getEntryViewportHeight());
        if (scrollOffset > max) scrollOffset = max;
        if (scrollOffset < 0) scrollOffset = 0;
    }

    private void onSearchChanged(String query) {
        savedSearchText = query != null ? query : "";
        String tabKey = activeTopTab + "." + activeSubTab;
        perTabSearchMemory.put(tabKey, savedSearchText);
        // Track search history when query is complete (non-blank, different from last)
        if (query != null && !query.isBlank()) {
            String trimmed = query.trim();
            if (searchHistory.isEmpty() || !searchHistory.peek().equals(trimmed)) {
                searchHistory.remove(trimmed);
                searchHistory.push(trimmed);
                if (searchHistory.size() > MAX_SEARCH_HISTORY) searchHistory.removeLast();
                saveUserData();
            }
        }
        if (query == null || query.isBlank()) {
            searchMode = false;
            refreshEntries();
        } else {
            searchMode = true;
            refreshEntries();
        }
    }

    /** Build the list of display entries from actual config values. */
    /**
     * Reset all config values in the current tab/sub-tab to their default values.
     */
    private void resetCurrentTab() {
        String subTabKey = activeTopTab + "." + activeSubTab;
        String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subTabKey, "");
        if (prefix.isEmpty()) return;

        int count = 0;
        for (String path : ConfigValueAccessor.getAllPaths()) {
            boolean belongs = false;
            if (prefix.startsWith("client.")) {
                if (path.startsWith(prefix + ".") || path.equals(prefix)) belongs = true;
            } else if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                belongs = true;
            }
            // Special case: "General" sub-tab shows all "general.*" entries
            if (activeSubTab.equals("General") && path.startsWith("general.")) {
                belongs = true;
            }
            if (belongs) {
                String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
                if (defaultVal != null) {
                    ConfigValueAccessor.setValueFromString(path, defaultVal);
                    count++;
                }
            }
        }
        refreshEntries();
    }

    /**
     * Reset ALL config values to their default values.
     */
    private void resetAllConfigs() {
        int count = 0;
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
            if (defaultVal != null) {
                ConfigValueAccessor.setValueFromString(path, defaultVal);
                count++;
            }
        }
        // Also clear custom identities
        try {
            com.xiaoxiang.configext.config.ExtendedConfig.IDENTITY_CUSTOM_IDENTITIES.set("");
            com.xiaoxiang.configext.config.ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.set("");
        } catch (Exception e) { /* ignore */ }
        refreshEntries();
    }

    /** Reset only configs belonging to the current tab/sub-tab scope. */
    private void resetCurrentTabConfigs() {
        // Determine the path prefix for the current scope
        String scope = activeSubTab != null && !activeSubTab.isEmpty()
                ? activeTopTab + "." + activeSubTab
                : activeTopTab;
        // Map tab names to config path prefixes
        // Sub-tabs map to specific config prefixes via SUBTAB_TO_PATH_PREFIX
        String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(scope, "");
        if (prefix.isEmpty()) {
            // Try just the top tab - find all sub-tab prefixes
            List<String> subs = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
            for (String sub : subs) {
                String subKey = activeTopTab + "." + sub;
                String subPrefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subKey, "");
                if (!subPrefix.isEmpty()) {
                    for (String path : ConfigValueAccessor.getAllPaths()) {
                        if (path.startsWith(subPrefix + ".") || path.equals(subPrefix)) {
                            String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
                            if (defaultVal != null) {
                                ConfigValueAccessor.setValueFromString(path, defaultVal);
                            }
                        }
                    }
                }
            }
        } else {
            // Reset only entries matching this sub-tab's prefix
            for (String path : ConfigValueAccessor.getAllPaths()) {
                if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                    String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
                    if (defaultVal != null) {
                        ConfigValueAccessor.setValueFromString(path, defaultVal);
                    }
                }
            }
        }
        refreshEntries();
    }

    /** Get total config count across all tabs. */
    private int getTotalConfigCount() {
        return ConfigValueAccessor.getAllPaths().size();
    }

    /** Check if a config path matches the active search filter. */
    private boolean matchesFilter(String path) {
        if (activeFilter == SearchFilter.ALL) return true;
        String type = ConfigValueAccessor.getType(path);
        String currentVal = ConfigValueAccessor.getValueString(path);
        String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
        switch (activeFilter) {
            case MODIFIED:
                return !currentVal.equals(defaultVal);
            case BOOLEAN:
                return type.equals("boolean");
            case NUMERIC:
                return !type.equals("boolean") && !type.equals("unknown") && !type.equals("string");
            case COLOR:
                return path.contains("color") || path.contains("Color") ||
                       (path.startsWith("client.colors.") && type.equals("int"));
            case STRING:
                return type.equals("string");
            case OVERMAX:
                String maxVal = ConfigValueAccessor.getMaxValueString(path);
                if (maxVal == null || maxVal.isEmpty()) return false;
                try {
                    double cur = Double.parseDouble(currentVal.trim());
                    double max = Double.parseDouble(maxVal.trim());
                    return cur > max;
                } catch (NumberFormatException e) { return false; }
            case FAVORITES:
                return favorites.contains(path);
            default:
                return true;
        }
    }

    /**
     * Rebuild the visible entry set, then re-derive everything that depends on it:
     * the 3rd-level group bar, the shared row layout, and the entrance animation.
     * The actual filtering lives in {@link #refreshEntriesInternal()}, which has
     * several early returns - doing the dependent work here means none of those
     * early returns can leave the group bar or the row list stale (a stale group
     * bar shifted getEntryYStart(), which is one of the ways a tab used to render
     * with its content pushed off-screen).
     */
    private void refreshEntries() {
        refreshEntriesInternal();
        rebuildGroupButtons();
        invalidateLayout();
        anim.startTransition();
    }

    private void refreshEntriesInternal() {
        filteredEntries.clear();
        scrollOffset = 0;
        selectedEntryIdx = -1;

        if (searchMode) {
            // Search across ALL config values (or current tab if not global)
            String query = searchBox.getValue();
            for (String path : ConfigValueAccessor.getAllPaths()) {
                if (!ConfigValueAccessor.exists(path)) continue;
                ForgeConfigSpec.ConfigValue<?> value = ConfigValueAccessor.get(path);
                // If not global search, filter to current tab
                if (!globalSearch) {
                    String subTabKey = activeTopTab + "." + activeSubTab;
                    String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subTabKey, "");
                    if (!prefix.isEmpty() && !path.startsWith(prefix + ".") && !path.equals(prefix)) continue;
                }
                // Apply search filter
                if (!matchesFilter(path)) continue;
                ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
                DisplayEntry entry = new DisplayEntry(path, value, info, getGroupForPath(path, getSubTabPrefix()));
                if (entry.matches(query)) {
                    filteredEntries.add(entry);
                }
            }
        } else {
            // Show entries for current tab/sub-tab
            // Check if this is a custom tab
            if (CustomTabManager.isCustomTab(activeTopTab)) {
                // Custom tab: aggregate entries from all path prefixes
                List<String> customPaths = CustomTabManager.getCustomTabPaths(activeTopTab);
                for (String path : ConfigValueAccessor.getAllPaths()) {
                    boolean belongs = false;
                    String group = "";
                    for (String cp : customPaths) {
                        if (path.startsWith(cp + ".") || path.equals(cp)) {
                            belongs = true;
                            // Prefer a real registered section name (e.g. "Golden Core")
                            // for the prefix that matched, so an expansion mod with one
                            // prefix per realm gets one real group per realm instead of
                            // every prefix collapsing into whatever the first path
                            // segment after it happens to be (e.g. every realm sharing
                            // a "layers" sub-key would otherwise all group as "Layers").
                            // Hand-built custom tabs have no registered sections, so
                            // this returns null for them and they keep the old behavior.
                            String sectionName = CustomTabManager.getSectionNameForPrefix(activeTopTab, cp);
                            if (sectionName != null) {
                                group = sectionName;
                            } else {
                                String afterPrefix = path.substring(cp.length() + 1);
                                String[] parts = afterPrefix.split("\\.");
                                if (parts.length > 1) group = prettifySegment(parts[0]);
                            }
                            break;
                        }
                    }
                    if (!belongs) continue;
                    if (!matchesFilter(path)) continue;
                    if (!activeGroup.isEmpty() && !group.equals(activeGroup)) continue;
                    // Check config dependency visibility
                    if (!ConfigDependencyManager.shouldBeVisible(path)) continue;
                    if (!ConfigValueAccessor.exists(path)) continue;
                    ForgeConfigSpec.ConfigValue<?> value = ConfigValueAccessor.get(path);
                    ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
                    if (info == null) info = findInfoByFuzzyMatch(path);
                    filteredEntries.add(new DisplayEntry(path, value, info, group));
                }
            } else {
            // Standard tab: show entries for current sub-tab
            String subTabKey = activeTopTab + "." + activeSubTab;
            String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subTabKey, "");

            if (prefix.isEmpty()) {
                return;
            }

            for (String path : ConfigValueAccessor.getAllPaths()) {
                // Check if this path belongs to the current sub-tab
                boolean belongs = false;
                String group = "";

                if (prefix.startsWith("client.")) {
                    if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                        belongs = true;
                    }
                } else if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                    belongs = true;
                    String afterPrefix = path.substring(prefix.length() + 1);
                    String[] parts = afterPrefix.split("\\.");
                    if (parts.length > 1) {
                        group = prettifySegment(parts[0]);
                    }
                }

                if (activeSubTab.equals("General") && path.startsWith("general.")) {
                    belongs = true;
                    group = "";
                }

                if (belongs) {
                    if (path.equals("identity.custom.identities") || path.equals("identity.custom.items")) {
                        continue;
                    }
                    // Apply search filter
                    if (!matchesFilter(path)) continue;
                    if (!activeGroup.isEmpty() && !group.equals(activeGroup)) {
                        continue;
                    }
                    // Check config dependency visibility
                    if (!ConfigDependencyManager.shouldBeVisible(path)) continue;
                    ForgeConfigSpec.ConfigValue<?> value = ConfigValueAccessor.get(path);
                    if (value == null) continue;
                    ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
                    // Also try fuzzy match for description
                    if (info == null) {
                        info = findInfoByFuzzyMatch(path);
                    }
                    filteredEntries.add(new DisplayEntry(path, value, info, group));
                }
            }

            // Add custom identity entries when viewing the Identity sub-tab
            if (prefix.equals("identity")) {
                addCustomIdentityEntries(filteredEntries);
            }
            } // end standard tab else block
        }

        // Sort: favorites first, then modified entries, then realm order, then entries with descriptions, then alphabetically
        filteredEntries.sort((a, b) -> {
            // Favorites always first
            boolean aFav = favorites.contains(a.configPath);
            boolean bFav = favorites.contains(b.configPath);
            if (aFav != bFav) return aFav ? -1 : 1;
            // Modified entries next
            String aDef = ConfigValueAccessor.getDefaultValueString(a.configPath);
            String aCur = ConfigValueAccessor.getValueString(a.configPath);
            boolean aMod = aDef != null && !aDef.equals(aCur);
            String bDef = ConfigValueAccessor.getDefaultValueString(b.configPath);
            String bCur = ConfigValueAccessor.getValueString(b.configPath);
            boolean bMod = bDef != null && !bDef.equals(bCur);
            if (aMod != bMod) return aMod ? -1 : 1;
            // Realm entries in breakthrough order
            int aOrder = getRealmOrder(a.configPath);
            int bOrder = getRealmOrder(b.configPath);
            if (aOrder != -1 || bOrder != -1) {
                if (aOrder != bOrder) return Integer.compare(aOrder, bOrder);
            }
            // Entries with descriptions first
            int aHas = a.info != null ? 0 : 1;
            int bHas = b.info != null ? 0 : 1;
            if (aHas != bHas) return aHas - bHas;
            // Group entries together
            if (!a.group.isEmpty() || !b.group.isEmpty()) {
                int gComp = a.group.compareToIgnoreCase(b.group);
                if (gComp != 0) return gComp;
            }
            return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
        });
    }

    /**
     * Add custom identity entries to the filtered entries list.
     * Each custom identity shows as an entry with its display name and an
     * "Item Picker" button for editing starting items, plus a "Delete" button.
     */
    private void addCustomIdentityEntries(List<DisplayEntry> entries) {
        for (CustomIdentityManager.CustomIdentity ci : CustomIdentityManager.loadAll()) {
            // Add a virtual entry for the custom identity's starting items
            String path = "identity." + ci.id + ".startingItems";
            String group = "Custom Identities";
            // Respect group filter
            if (!activeGroup.isEmpty() && !group.equals(activeGroup)) {
                continue;
            }
            ConfigEntryInfo info = new ConfigEntryInfo(
                    path, ci.displayName + " (Custom) - Starting Items",
                    "Starting items for " + ci.displayName + ". Click [Pick] to edit.",
                    null, null, "Cultivation", "Identity", group,
                    java.util.Collections.emptyList());
            DisplayEntry entry = new DisplayEntry(path, null, info, group);
            entries.add(entry);
        }
    }

    /** Realm breakthrough order for sorting. Returns -1 if not a realm entry. */
    private static final List<String> REALM_ORDER = List.of(
        "mortal", "qiRefiningEarly", "qiRefiningMiddle", "qiRefiningLate", "qiRefiningPeak",
        "foundationBuilding", "goldenCore", "nascentSoul", "soulFormation",
        "voidRefining", "bodyIntegration", "mahayana", "tribulationTranscendence",
        "trueImmortal", "looseImmortal"
    );

    private static int getRealmOrder(String path) {
        if (!path.startsWith("realms.")) return -1;
        String[] parts = path.split("\\.");
        if (parts.length < 3) return -1;
        String last = parts[parts.length - 1];
        int idx = REALM_ORDER.indexOf(last);
        if (idx >= 0) return idx;
        // Groups that track Qi Refining as a single realm (lifespan, shield %, tribulation
        // damage) use the bare key "qiRefining" instead of the per-substage keys used by
        // maxQi ("qiRefiningEarly" etc). Without this, "qiRefining" fell through to -1 and
        // only looked correctly-positioned by coincidence (it happened to be the sole realm
        // entry sorting before the rest in those groups). Map it to the same slot as the
        // Early substage so it's genuinely realm-ordered, not accidentally ordered.
        if (last.equals("qiRefining")) return REALM_ORDER.indexOf("qiRefiningEarly");
        // Check if it's a delta/substage entry — order after the base realm
        if (last.equals("deltaEarly")) return 1;
        if (last.equals("deltaMiddle")) return 2;
        if (last.equals("deltaLate")) return 3;
        if (last.equals("deltaPeak")) return 4;
        // Global multipliers go at the end of their category
        if (last.equals("globalMultiplier")) return 100;
        return -1;
    }

    /** Try to find description info by fuzzy matching the path. */
    private ConfigEntryInfo findInfoByFuzzyMatch(String path) {
        // Try exact match first
        ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
        if (info != null) return info;

        // Try matching by last segment
        String[] parts = path.split("\\.");
        if (parts.length == 0) return null;
        String lastSeg = parts[parts.length - 1].toLowerCase();

        // Search all registered entries for one whose key ends with the same segment
        for (ConfigEntryInfo candidate : ConfigDescriptionRegistry.ENTRIES.values()) {
            String[] candidateParts = candidate.key.split("\\.");
            if (candidateParts.length > 0 && candidateParts[candidateParts.length - 1].equalsIgnoreCase(lastSeg)) {
                // Check if the path prefixes also match
                if (path.toLowerCase().contains(candidateParts[0].toLowerCase())) {
                    return candidate;
                }
            }
        }

        // Try partial last-segment matching (one contains the other)
        for (ConfigEntryInfo candidate : ConfigDescriptionRegistry.ENTRIES.values()) {
            String[] candidateParts = candidate.key.split("\\.");
            if (candidateParts.length > 0) {
                String candLast = candidateParts[candidateParts.length - 1].toLowerCase();
                // Check if one last-segment contains the other (e.g. "completeVanillaMax" vs "completeVanillaRollsMax")
                if (lastSeg.contains(candLast) || candLast.contains(lastSeg)) {
                    // Verify path prefixes match
                    if (parts.length > 0 && candidateParts.length > 0 &&
                        parts[0].equalsIgnoreCase(candidateParts[0])) {
                        return candidate;
                    }
                }
            }
        }

        // Try matching by checking if all path parts appear in the candidate key
        for (ConfigEntryInfo candidate : ConfigDescriptionRegistry.ENTRIES.values()) {
            String candLower = candidate.key.toLowerCase();
            boolean allMatch = true;
            for (String part : parts) {
                if (!candLower.contains(part.toLowerCase())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return candidate;
        }

        return null;
    }

    private String getSubTabPrefix() {
        return SUBTAB_TO_PATH_PREFIX.getOrDefault(activeTopTab + "." + activeSubTab, "");
    }

    /** Get all unique groups in the current sub-tab (computed from ALL entries, not filtered). */
    private List<String> getGroupsForCurrentSubTab() {
        // Custom tabs with real registered sections (e.g. Realm Expansion's
        // "Expansion" tab, one section per realm) get their group bar from
        // those sections directly, instead of the generic
        // SUBTAB_TO_PATH_PREFIX lookup below, which only custom tabs
        // never populate (SUBTAB_TO_PATH_PREFIX is standard-tab only) - so
        // without this branch every custom tab's group bar is always empty.
        if (CustomTabManager.isCustomTab(activeTopTab) && CustomTabManager.hasSubTabs(activeTopTab)) {
            LinkedHashSet<String> customGroups = new LinkedHashSet<>();
            for (String subTab : CustomTabManager.getCustomSubTabNames(activeTopTab)) {
                customGroups.addAll(CustomTabManager.getCustomSubTabSections(activeTopTab, subTab));
            }
            return new ArrayList<>(customGroups);
        }

        LinkedHashSet<String> groups = new LinkedHashSet<>();
        String subTabKey = activeTopTab + "." + activeSubTab;
        String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subTabKey, "");

        if (prefix.isEmpty()) return new ArrayList<>();

        for (String path : ConfigValueAccessor.getAllPaths()) {
            boolean belongs = false;
            String group = "";

            if (prefix.startsWith("client.")) {
                if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                    belongs = true;
                }
            } else if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                belongs = true;
                String afterPrefix = path.substring(prefix.length() + 1);
                String[] parts = afterPrefix.split("\\.");
                if (parts.length > 1) {
                    group = prettifySegment(parts[0]);
                }
            }

            if (activeSubTab.equals("General") && path.startsWith("general.")) {
                belongs = true;
                group = "";
            }

            // No special case for Qi System - normal prefix matching handles sub-tabs

            // Skip the raw "Custom" group - custom identities are shown via
            // addCustomIdentityEntries() under "Custom Identities" group
            if (belongs && !group.isEmpty() && !group.equals("Custom")) {
                groups.add(group);
            }
        }

        // Add "Custom Identities" group if there are custom identities and we're on the Identity sub-tab
        if (prefix.equals("identity") && !CustomIdentityManager.loadAll().isEmpty()) {
            groups.add("Custom Identities");
        }

        return new ArrayList<>(groups);
    }

    private void resetCurrentTabToDefaults() {
        // Reset all entries in current view to their default values
        // ForgeConfigSpec stores defaults internally but doesn't expose them easily
        // For now, just refresh
        refreshEntries();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Advance the animation clock exactly once per frame. Everything that asks
        // AnimationState for a hover/entrance value below reads this frame's delta.
        anim.beginFrame(isReduceMotion());

        // ══════════════════════════════════════════════════════════════════
        //  POPUP MODE: When item picker or name input is open, render
        //  ONLY the popup on a dark background. No tabs, no entries.
        //  Pinned tooltip does NOT hide the screen - it floats on top.
        // ══════════════════════════════════════════════════════════════════
        if (itemPicker.isOpen() || nameInput.isOpen()) {
            // Dark semi-transparent background
            g.fill(0, 0, this.width, this.height, 0xC0000000);

            // Render only the popup(s)
            if (itemPicker.isOpen()) {
                itemPicker.render(g, this.width, this.height, mouseX, mouseY, this.font);
            }
            if (nameInput.isOpen()) {
                nameInput.render(g, this.font, mouseX, mouseY, this);
            }
            if (colorWheel.isOpen()) {
                colorWheel.render(g, this.width, this.height, mouseX, mouseY, this.font);
            }
            if (dropdownEnum.isOpen()) {
                dropdownEnum.render(g, this.width, this.height, mouseX, mouseY, this.font);
            }
            if (multiLineEditor.isOpen()) {
                multiLineEditor.render(g, mouseX, mouseY, this.font);
            }
            // Pinned tooltips can still show on top of popups
            renderAllPinnedTooltips(g, mouseX, mouseY);
            return;
        }

        // ══════════════════════════════════════════════════════════════════
        //  NORMAL MODE: Full screen rendering with window outline
        // ══════════════════════════════════════════════════════════════════

        // Background
        this.renderBackground(g);

        // ── Main config UI window outline and background ──
        int winX = 2;
        int winY = 2;
        int winW = this.width - 4;
        int winH = this.height - 4;
        // Window background
        g.fill(winX, winY, winX + winW, winY + winH, Theme.BACKGROUND);
        // Double border outline
        g.renderOutline(winX, winY, winW, winH, Theme.WINDOW_BORDER);
        g.renderOutline(winX + 1, winY + 1, winW - 2, winH - 2, Theme.WINDOW_BORDER_DIM);

        // Title - bold gold, positioned at top-left to avoid overlap with search bar
        g.drawString(this.font, "\u00A7e\u00A7lXiaoxiang Config Extension", 8, 4, 0xFFFFFF);

        // Search box - centered, below title
        searchBox.render(g, mouseX, mouseY, partialTick);

        // ── Search filter buttons (below search bar) ──
        renderSearchFilters(g, mouseX, mouseY);

        // ── Minimap navigation (bottom-right corner, small) ──
        // Off by default and toggled via the "Nav" utility button (bottom-left row).
        // It used to be forced visible on every frame with no way to close it and no
        // click handling wired up at all, so it permanently sat on top of the entries
        // list in the same bottom-right corner, with its own tab/sub-tab list text
        // bleeding into whatever entry rows happened to render underneath it.
        //
        // Height trimmed from 140 to 122: the vanilla "Done" button sits at
        // (width-80, height-28, 70, 20) - i.e. it occupies x=[width-80,width-10],
        // y=[height-28,height-8]. The box's x-range (width-130..width-10) already
        // covers Done's x-range, so with the old height=140 the box's bottom edge
        // (height-20) dipped 8px into Done's top edge (height-28). Ending the box
        // at height-38 leaves a clean 10px gap above Done instead.
        minimapNav.setPosition(this.width - 130, this.height - 160, 120, 122);
        // scrollOffset is a ROW index into the shared layout (rows include group
        // headers), so translate it back to an ENTRY index for the nav readout.
        minimapNav.render(g, this.font, activeTopTab, activeSubTab,
            new ArrayList<>(CustomTabManager.getTabOrder()), TAB_TO_SUBTABS,
            layout().firstVisibleEntryIndex(scrollOffset), filteredEntries.size(), mouseX, mouseY);

        // ── Custom render top-level tabs and sub-tabs ──
        renderCustomTabs(g, mouseX, mouseY);

        // ── Entry list ───────────────────────────────────────────────────
        // Everything below reads the SHARED row list produced by LayoutEngine.
        // render(), mouseClicked() and mouseScrolled() all consult the same rows,
        // so a row can never be painted at one Y and hit-tested at another - which
        // is what used to make entries look greyed-out / unclickable.
        int entryYStart = getEntryYStart();
        int entryX = 8;
        int entryWidth = getEntryWidth();
        LayoutEngine.Layout rowLayout = layout();
        int viewportH = getEntryViewportHeight();
        scrollOffset = Math.max(0, Math.min(scrollOffset, rowLayout.maxFirstRow(viewportH)));
        int scrollPx = rowLayout.pixelOffset(scrollOffset);
        int lastRow = rowLayout.lastVisibleRow(scrollOffset, viewportH);

        // Clip entries to their area so they don't overlap with the search dropdown
        int clipTop = entryYStart - 2;
        int clipBottom = getEntryClipBottom();
        g.enableScissor(entryX, clipTop, entryWidth, clipBottom);

        // Tracks whether any entry's tooltip (and therefore the keyword-hover state below)
        // was touched this frame. If the mouse isn't over any entry at all - e.g. it has
        // moved up to the tab bar to click a different top-level tab - none of the
        // per-entry keyword tracking in renderEntryTooltip() runs, so hoveredKeyword/
        // keywordHoverStart would otherwise keep whatever stale value they had from the
        // last entry that was hovered. mouseClicked() treats a stale-but-still-"elapsed"
        // hoveredKeyword as "the popup should pin/unpin", swallowing that click (and every
        // click after it, since pin/unpin alternates forever without ever clearing
        // hoveredKeyword) instead of letting it reach the top-tab-click handling further
        // down - locking the player out of switching tabs. Resetting here whenever no
        // entry was actually hovered this frame keeps the stale state from surviving past
        // the mouse leaving the entry list.
        boolean anyEntryTooltipHovered = false;

        for (int rowIdx = scrollOffset; rowIdx <= lastRow && rowIdx < rowLayout.size(); rowIdx++) {
            LayoutEngine.Row row = rowLayout.get(rowIdx);
            int y = row.y - scrollPx;
            int rowH = row.height;

            // Entrance animation: newly shown rows fade in and slide a few px right.
            float appear = anim.rowProgress(rowIdx - scrollOffset);
            int slide = anim.slideOffset(appear);

            // -- Group header row -----------------------------------------
            // A header is now a REAL row with its own reserved height, measured
            // from the actual wrapped text via LayoutEngine. It used to be drawn
            // into the PREVIOUS row's slot at a hard-coded 14px, which is exactly
            // why headers overlapped the row above them, and why a header whose
            // text needed more than 14px overlapped the row below it too.
            if (row.header) {
                int barY = row.barTop() - scrollPx;
                int barH = row.barHeight; // measured, not assumed
                boolean headerHover = mouseX >= entryX && mouseX < entryWidth
                        && mouseY >= barY && mouseY < barY + barH;
                float headerGlow = anim.hover("group-header:" + collapseKey(row.group), headerHover);
                String arrow = row.collapsed ? "\u25B6" : "\u25BC"; // right / down triangle

                g.fill(entryX + slide, barY, entryWidth, barY + barH,
                        Theme.withAlpha(Theme.lerp(Theme.HEADER_BG, Theme.HEADER_BG_HOVER, headerGlow), appear));
                g.renderOutline(entryX + slide, barY, entryWidth - entryX - slide, barH,
                        Theme.withAlpha(Theme.lerp(Theme.HEADER_BORDER, Theme.PANEL_BORDER_LIGHT, headerGlow), appear));
                // Left accent stripe so headers read as headers at a glance.
                g.fill(entryX + slide, barY, entryX + slide + 2, barY + barH,
                        Theme.withAlpha(row.collapsed ? Theme.TEXT_FAINT : Theme.ACCENT, appear));

                String headerTitle = arrow + " " + row.group;
                g.drawString(this.font, headerTitle, entryX + slide + 7, barY + 3,
                        Theme.textAlpha(row.collapsed ? Theme.TEXT_MUTED : Theme.HEADER_TEXT, appear));
                String countLabel = "(" + row.groupCount + ")";
                g.drawString(this.font, countLabel,
                        entryX + slide + 7 + this.font.width(headerTitle) + 6, barY + 3,
                        Theme.textAlpha(Theme.TEXT_FAINT, appear));

                // Group blurb, drawn into space the row already reserved for it.
                if (row.descLines > 0) {
                    String groupBlurb = getGroupDescription(row.group);
                    if (groupBlurb != null && !groupBlurb.isBlank()) {
                        int nlIdx = groupBlurb.indexOf('\n');
                        if (nlIdx >= 0) groupBlurb = groupBlurb.substring(0, nlIdx);
                        List<String> blurbLines = LayoutEngine.wrapLines(this.font, groupBlurb,
                                entryWidth - entryX - 12, row.descLines);
                        for (int li = 0; li < blurbLines.size(); li++) {
                            g.drawString(this.font, blurbLines.get(li), entryX + slide + 9,
                                    barY + barH + 1 + li * LayoutEngine.LINE_H,
                                    Theme.textAlpha(Theme.TEXT_FAINT, appear));
                        }
                    }
                }
                continue;
            }

            DisplayEntry entry = row.entry;
            int i = row.entryIndex;

            // Check hover state for the entire row
            boolean rowHover = mouseX >= entryX && mouseX < entryWidth && mouseY >= y && mouseY < y + rowH;
            boolean rowSelected = (i == selectedEntryIdx);
            if (rowHover) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.ITEM, i);

            // Entry background with hover/selection effect
            int bgColor = (i % 2 == 0) ? Theme.ROW_BG_EVEN : Theme.ROW_BG_ODD;
            g.fill(entryX + slide, y, entryWidth, y + rowH, Theme.withAlpha(bgColor, appear));

            // Value change flash animation (green flash overlay)
            if (isFlashing(entry.configPath)) {
                float intensity = getFlashIntensity(entry.configPath);
                int flashAlpha = (int)(intensity * 80);
                g.fill(entryX + slide, y, entryWidth, y + rowH, (flashAlpha << 24) | 0x00FF00);
            }

            // Check if value is over max (for red left-border accent)
            String entryType = ConfigValueAccessor.getType(entry.configPath);
            boolean overMax = false;
            if (!entryType.equals("boolean") && !entryType.equals("unknown") && !entryType.equals("string")) {
                String maxVal = ConfigValueAccessor.getMaxValueString(entry.configPath);
                String curVal = ConfigValueAccessor.getValueString(entry.configPath);
                if (maxVal != null && !maxVal.isEmpty()) {
                    try {
                        double cur = Double.parseDouble(curVal.trim());
                        double max = Double.parseDouble(maxVal.trim());
                        if (cur > max) overMax = true;
                    } catch (NumberFormatException e) { /* ignore */ }
                }
            }
            // Red left-border accent for over-max entries
            if (overMax) {
                g.fill(entryX + slide, y, entryX + slide + 4, y + rowH, Theme.withAlpha(Theme.ERROR, appear));
            }

            // Selection: bright outline + tint + multiplier badge
            if (rowSelected) {
                g.fill(entryX + slide, y, entryWidth, y + rowH, Theme.withAlpha(Theme.ROW_BG_SELECTED, appear));
                g.renderOutline(entryX + slide, y, entryWidth - entryX - slide, rowH,
                        Theme.withAlpha(Theme.ROW_BORDER_SEL, appear));
                g.renderOutline(entryX + slide + 1, y + 1, entryWidth - entryX - slide - 2, rowH - 2,
                        Theme.withAlpha(Theme.ROW_BORDER_SEL_IN, appear));
                // Draw "Ctrl+Scroll" hint and active multiplier badge
                String hint = "Ctrl+Scroll";
                int hintW = this.font.width(hint);
                int badgeX = entryWidth - hintW - 8;
                int badgeY = y + 1;
                g.fill(badgeX - 2, badgeY, badgeX + hintW + 2, badgeY + 8, 0x80000000);
                g.drawString(this.font, "\u00A7b" + hint, badgeX, badgeY, 0xFFFFFF);
                // Show active multiplier based on held keys
                String mult = "";
                int multColor = 0xFFFFFF;
                boolean ctrl = hasControlDown();
                boolean shift = hasShiftDown();
                boolean cKey = isCKeyDown();
                if (ctrl && shift && cKey) { mult = "x1000"; multColor = 0xFFFF4040; }
                else if (ctrl && cKey) { mult = "x0.01"; multColor = 0xFF40FFFF; }
                else if (ctrl && shift) { mult = "x10"; multColor = 0xFFFFFF40; }
                else if (shift) { mult = "x100"; multColor = 0xFFFFA040; }
                else if (ctrl) { mult = "x1"; multColor = 0xFFFFFFFF; }
                if (!mult.isEmpty()) {
                    int multW = this.font.width(mult);
                    g.fill(badgeX - 2, badgeY + 9, badgeX + multW + 2, badgeY + 17, 0x80000000);
                    g.drawString(this.font, "\u00A7l" + mult, badgeX, badgeY + 9, multColor);
                }
            }
            // Hover: outline + slight enlargement effect
            if (!rowSelected) {
                // Hover highlight eases in/out over ~120ms instead of snapping.
                float rowGlow = anim.hover("entry:" + entry.configPath, rowHover);
                if (rowGlow > 0.01f) {
                    g.fill(entryX + slide, y, entryWidth, y + rowH,
                            Theme.withAlpha(Theme.ROW_BG_HOVER, rowGlow * appear));
                    g.renderOutline(entryX + slide, y, entryWidth - entryX - slide, rowH,
                            Theme.withAlpha(Theme.ROW_BORDER_HOVER, rowGlow * appear));
                }
            }

            // Entry type indicator icon (small colored square before the name)
            // entryType already defined above for over-max check
            // Favorite/Lock indicators (right-click to toggle)
            if (favorites.contains(entry.configPath)) {
                g.drawString(this.font, "\u2605", entryWidth - 210, y + 2, Theme.textAlpha(Theme.FAVORITE, appear));
            }
            if (isLocked(entry.configPath)) {
                g.drawString(this.font, "\u26BF", entryWidth - 222, y + 2, Theme.textAlpha(Theme.LOCKED, appear));
            }

            String typeIcon = getTypeIcon(entryType, entry.configPath);
            int typeIconColor = getTypeIconColor(entryType, entry.configPath);
            g.drawString(this.font, typeIcon, entryX + slide + 2, y + 2, Theme.textAlpha(typeIconColor, appear));

            // Entry name. Wrapped/ellipsised against the SAME pixel budget the
            // LayoutEngine measured this row's height with, so the row is always
            // tall enough for exactly what gets drawn here - never an assumed constant.
            String rawName = CultivationTextStyler.styleDisplayName(entry.getDisplayName());
            int entryNameX = entryX + slide + LayoutEngine.NAME_INDENT;
            int nameColor = Theme.textAlpha(rowHover ? Theme.TEXT_ON_ACCENT : Theme.TEXT_PRIMARY, appear);
            List<String> nameLines = LayoutEngine.wrapLines(this.font, rawName,
                    rowLayout.nameBudget, row.nameLines);
            if (nameLines.isEmpty()) {
                nameLines = new ArrayList<>();
                nameLines.add(rawName);
            }
            for (int li = 0; li < nameLines.size(); li++) {
                String lineText = (rowHover && li == 0) ? "\u00A7l" + nameLines.get(li) : nameLines.get(li);
                int lineY = y + LayoutEngine.ROW_PAD_TOP + li * LayoutEngine.LINE_H;
                if (!searchBox.getValue().isBlank()) {
                    drawHighlightedText(g, this.font, lineText, entryNameX, lineY,
                        nameColor, searchBox.getValue().toLowerCase(), 0xFF30C0FF);
                } else {
                    g.drawString(this.font, lineText, entryNameX, lineY, nameColor);
                }
            }

            // Description, ellipsised against the measured budget rather than the
            // old fixed "cut at 70 characters" (which could still run underneath
            // the value controls on a narrow window).
            String desc = CultivationTextStyler.style(entry.getDescription());
            int descY = y + LayoutEngine.ROW_PAD_TOP + row.nameLines * LayoutEngine.LINE_H;
            if (row.descLines > 0) {
                desc = LayoutEngine.ellipsize(this.font, desc, rowLayout.descBudget);
                int descColor = Theme.textAlpha(Theme.TEXT_MUTED, appear);
                if (!searchBox.getValue().isBlank()) {
                    drawHighlightedText(g, this.font, desc, entryX + slide + LayoutEngine.DESC_INDENT, descY,
                        descColor, searchBox.getValue().toLowerCase(), 0xFF30A0E0);
                } else {
                    g.drawString(this.font, desc, entryX + slide + LayoutEngine.DESC_INDENT, descY, descColor);
                }
            }

            // ── Inline help (?) icon for entries with descriptions ──
            if (row.descLines > 0 && isInlineHelpEnabled() && entry.info != null
                    && entry.info.description != null && !entry.info.description.isEmpty()) {
                String helpIcon = "?";
                int helpX = entryX + slide + LayoutEngine.DESC_INDENT + this.font.width(desc) + 4;
                int helpY = descY;
                boolean helpHover = mouseX >= helpX && mouseX < helpX + 8 && mouseY >= helpY && mouseY < helpY + 10;
                int helpColor = helpHover ? Theme.INFO : Theme.PANEL_BORDER_LIGHT;
                g.drawString(this.font, "\u00A7b" + helpIcon, helpX, helpY, Theme.textAlpha(helpColor, appear));
                if (helpHover) {
                    // Show inline help tooltip with full description
                    List<FormattedCharSequence> helpLines = new ArrayList<>();
                    helpLines.addAll(this.font.split(net.minecraft.network.chat.Component.literal("\u00A7e" + entry.getDisplayName()), 250));
                    helpLines.addAll(this.font.split(net.minecraft.network.chat.Component.literal(""), 250));
                    for (String line : wrapText(entry.getDescription(), 50)) {
                        helpLines.addAll(this.font.split(net.minecraft.network.chat.Component.literal("\u00A77" + line), 250));
                    }
                    g.renderTooltip(this.font, helpLines, mouseX, mouseY);
                }
            }

            // ── Config dependency indicator ──
            String depReason = ConfigDependencyManager.getDependencyReason(entry.configPath);
            if (depReason != null) {
                g.drawString(this.font, "\u00A7c\u26A0", entryWidth - 235, y + 2, 0xFFFF4040);
            }

            // ── Editable value controls on the right side ──
            String type = ConfigValueAccessor.getType(entry.configPath);
            String valStr = ConfigValueAccessor.getValueString(entry.configPath);

            // Custom identity entries have no backing ConfigValue, treat as string
            if (type.equals("unknown") && entry.configPath.startsWith("identity.custom_") && entry.configPath.contains(".startingItems")) {
                type = "string";
                valStr = CustomIdentityManager.getStartingItems(
                        entry.configPath.split("\\.")[1]);
                if (valStr == null) valStr = "";
            }

            if (type.equals("boolean")) {
                // Toggle button with hover enlargement
                boolean current = valStr.equals("true");
                String label = current ? "\u00A7aON" : "\u00A7cOFF";
                int btnX = entryWidth - 60;
                int btnY = y + 2;
                int btnW = 50;
                int btnH = 18;
                boolean hovered = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= btnY && mouseY < btnY + btnH;
                // Hover enlargement
                int exp = hovered ? 2 : 0;
                int tbx = btnX - exp, tby = btnY - exp, tbw = btnW + exp * 2, tbh = btnH + exp * 2;
                int toggleBg = current ? 0xFF205020 : 0xFF502020;
                if (hovered) toggleBg |= 0x30FFFFFF;
                g.fill(tbx, tby, tbx + tbw, tby + tbh, toggleBg);
                g.renderOutline(tbx, tby, tbw, tbh, hovered ? 0xFFFFD700 : 0xFF808080);
                g.drawCenteredString(this.font, label, tbx + tbw / 2, tby + 5, 0xFFFFFF);
            } else if (!type.equals("unknown")) {
                // Check if this is a color config
                boolean isColor = entry.configPath.contains("color") || entry.configPath.contains("Color") ||
                                  (entry.configPath.startsWith("client.colors.") && type.equals("int"));

                if (isColor && type.equals("int")) {
                    // Parse current color value and find preset name
                    int currentColor;
                    try {
                        currentColor = Integer.parseInt(valStr.trim());
                    } catch (NumberFormatException e) {
                        currentColor = -1;
                    }
                    String presetName = ColorPresets.findName(currentColor);
                    if (presetName == null) presetName = "Custom";

                    // Color entry: << (prev) + swatch + preset name + >> (next)
                    int prevX = entryWidth - 200;
                    int prevW = 20;
                    int swatchX = entryWidth - 178;
                    int swatchY = y + 3;
                    int swatchW = 24;
                    int swatchH = 16;
                    int nameX = swatchX + swatchW + 4;
                    int nameW = 86;
                    int cycleX = entryWidth - 30;
                    int cycleW = 20;

                    // Previous button (<<)
                    boolean prevHover = mouseX >= prevX && mouseX < prevX + prevW && mouseY >= swatchY && mouseY < swatchY + swatchH;
                    int prevBg = prevHover ? 0xFF404060 : 0xFF303040;
                    g.fill(prevX, swatchY, prevX + prevW, swatchY + swatchH, prevBg);
                    g.renderOutline(prevX, swatchY, prevW, swatchH, 0xFF606080);
                    g.drawCenteredString(this.font, "\u00AB", prevX + prevW / 2, swatchY + 4, 0xFFFFFF);

                    // Color swatch
                    boolean swatchHover = mouseX >= swatchX && mouseX < swatchX + swatchW && mouseY >= swatchY && mouseY < swatchY + swatchH;
                    g.fill(swatchX, swatchY, swatchX + swatchW, swatchY + swatchH, currentColor);
                    g.renderOutline(swatchX, swatchY, swatchW, swatchH, swatchHover ? 0xFFFFFFFF : 0xFF808080);

                    // Preset name display
                    g.fill(nameX, swatchY, nameX + nameW, swatchY + swatchH, 0xFF303040);
                    g.renderOutline(nameX, swatchY, nameW, swatchH, 0xFF606080);
                    String presetDisplayName = presetName;
                    if (this.font.width(presetDisplayName) > nameW - 4) {
                        while (this.font.width(presetDisplayName + "...") > nameW - 4 && presetDisplayName.length() > 0) {
                            presetDisplayName = presetDisplayName.substring(0, presetDisplayName.length() - 1);
                        }
                        presetDisplayName = presetDisplayName + "...";
                    }
                    g.drawString(this.font, presetDisplayName, nameX + 2, swatchY + 4, 0xFFFFFF);

                    // Next button (>>)
                    boolean cycleHover = mouseX >= cycleX && mouseX < cycleX + cycleW && mouseY >= swatchY && mouseY < swatchY + swatchH;
                    int cycleBg = cycleHover ? 0xFF404060 : 0xFF303040;
                    g.fill(cycleX, swatchY, cycleX + cycleW, swatchY + swatchH, cycleBg);
                    g.renderOutline(cycleX, swatchY, cycleW, swatchH, 0xFF606080);
                    g.drawCenteredString(this.font, "\u00BB", cycleX + cycleW / 2, swatchY + 4, 0xFFFFFF);

                    // Hex value display below swatch
                    String hexVal = String.format("#%08X", currentColor);
                    g.drawString(this.font, hexVal, swatchX, swatchY + swatchH + 1, 0x888888);
                } else {
                    // Check if this is a starting items config (string type with items path)
                    boolean isItemsConfig = type.equals("string") &&
                            (entry.configPath.contains("startingItems") || entry.configPath.contains(".items") ||
                             entry.configPath.startsWith("identity.custom_") ||
                             entry.configPath.startsWith("identity.custom."));

                    if (isItemsConfig) {
                        // ── Item picker button for starting items configs (with hover enlargement) ──
                        int pickBtnX = entryWidth - 120;
                        int pickBtnY = y + 3;
                        int pickBtnW = 110;
                        int pickBtnH = 16;
                        boolean pickHover = mouseX >= pickBtnX && mouseX < pickBtnX + pickBtnW &&
                                mouseY >= pickBtnY && mouseY < pickBtnY + pickBtnH;
                        int pExp = pickHover ? 2 : 0;
                        int pX = pickBtnX - pExp, pY = pickBtnY - pExp, pW = pickBtnW + pExp * 2, pH = pickBtnH + pExp * 2;
                        int pickBg = pickHover ? 0xFF2A4868 : 0xFF181820;
                        g.fill(pX, pY, pX + pW, pY + pH, pickBg);
                        g.renderOutline(pX, pY, pW, pH, pickHover ? 0xFF80B0FF : 0xFF404060);
                        if (pickHover) g.fill(pX, pY, pX + pW, pY + pH, 0x30FFFF80);
                        g.drawCenteredString(this.font, pickHover ? "\u00A7l\u00A7bPick Items" : "\u00A7bPick Items", pX + pW / 2, pY + 4, 0xFFFFFF);
                        // Show item count below
                        int itemCount = 0;
                        if (valStr != null && !valStr.isEmpty()) {
                            itemCount = valStr.split(";").length;
                        }
                        g.drawString(this.font, itemCount + " items", pickBtnX, pickBtnY + pickBtnH + 1, 0x888888);
                    } else {
                        // ── Regular numeric/string value with - and + buttons (hover enlargement) ──
                        int minusX = entryWidth - 180;
                        int plusX = entryWidth - 30;
                        int valX = entryWidth - 160;
                        int btnY = y + 3;
                        int btnH = 16;

                        boolean minusHover = mouseX >= minusX && mouseX < minusX + 20 && mouseY >= btnY && mouseY < btnY + btnH;
                        boolean plusHover = mouseX >= plusX && mouseX < plusX + 20 && mouseY >= btnY && mouseY < btnY + btnH;

                        // Hover enlargement for minus button
                        int mExp = minusHover ? 2 : 0;
                        int mX = minusX - mExp, mY = btnY - mExp, mW = 20 + mExp * 2, mH = btnH + mExp * 2;
                        int minusBg = minusHover ? 0xFF503060 : 0xFF303040;
                        g.fill(mX, mY, mX + mW, mY + mH, minusBg);
                        g.renderOutline(mX, mY, mW, mH, minusHover ? 0xFFFFA0FF : 0xFF606080);
                        g.drawCenteredString(this.font, minusHover ? "\u00A7l-" : "-", mX + mW / 2, mY + 4, minusHover ? 0xFFFFA0FF : 0xFFFFFF);

                        // Hover enlargement for plus button
                        int pExp = plusHover ? 2 : 0;
                        int pX = plusX - pExp, pY = btnY - pExp, pW = 20 + pExp * 2, pH = btnH + pExp * 2;
                        int plusBg = plusHover ? 0xFF503060 : 0xFF303040;
                        g.fill(pX, pY, pX + pW, pY + pH, plusBg);
                        g.renderOutline(pX, pY, pW, pH, plusHover ? 0xFFFFA0FF : 0xFF606080);
                        g.drawCenteredString(this.font, plusHover ? "\u00A7l+" : "+", pX + pW / 2, pY + 4, plusHover ? 0xFFFFA0FF : 0xFFFFFF);

                        // Value display (formatted with commas, truncated if too long)
                        String displayVal = formatConfigValue(valStr, type, entry.configPath);
                        // Also get max value for display - skipped for the six Sects->Schedule
                        // time-of-day fields, where "Dusk (12000) / Dawn (24000)" reads as more
                        // confusing than useful once the value is already a named time.
                        if (!isSectScheduleTimeField(entry.configPath)) {
                            String maxVal = ConfigValueAccessor.getMaxValueString(entry.configPath);
                            if (maxVal != null && !maxVal.isEmpty() && !type.equals("string") && !type.equals("boolean")) {
                                displayVal = displayVal + " / " + formatConfigValue(maxVal, type, entry.configPath);
                            }
                        }
                        if (this.font.width(displayVal) > 120) {
                            while (this.font.width(displayVal + "...") > 120 && displayVal.length() > 0) {
                                displayVal = displayVal.substring(0, displayVal.length() - 1);
                            }
                            displayVal = displayVal + "...";
                        }
                        // Check if value differs from default (for diff indicator color)
                        String defaultVal = ConfigValueAccessor.getDefaultValueString(entry.configPath);
                        boolean isModified = !valStr.equals(defaultVal);
                        // overMax already computed above for the red left-border accent
                        int valColor = overMax ? 0xFFE34234 : (isModified ? 0xFFFFD700 : 0x55FF55);
                        g.drawCenteredString(this.font, displayVal, valX + 60, btnY + 4, valColor);

                        // Config diff indicator dot (yellow = modified, red = over max)
                        if (isModified || overMax) {
                            int dotColor = overMax ? 0xFFE34234 : 0xFFFFD700;
                            g.fill(valX + 60 - 2, btnY + 2, valX + 60, btnY + 4, dotColor);
                        }
                    }
                }
            } else {
                // Unknown type - just show value
                g.drawCenteredString(this.font, valStr, entryWidth - 80, y + 6, 0x55FF55);
            }

            // Hover tooltip with full description (only when hovering the left part)
            if (mouseX >= entryX && mouseX < entryWidth - 200 && mouseY >= y && mouseY < y + rowH) {
                renderEntryTooltip(g, entry, mouseX, mouseY);
                anyEntryTooltipHovered = true;
            }
        }

        // Mouse isn't over any entry this frame - clear stale keyword-hover state so a
        // click elsewhere (e.g. on a top-level tab) isn't misread as a click on a keyword
        // popup that's no longer actually being hovered. See comment above the loop.
        if (!anyEntryTooltipHovered) {
            hoveredKeyword = null;
            keywordHoverStart = 0;
            if (!keywordPopupPinned) pendingKeywordPopup = false;
        }

        g.disableScissor();

        // Scroll indicator - sized from the shared layout's real pixel height, so
        // it stays truthful even when rows have different heights (wrapped headers,
        // two-line labels). The old version assumed every row was getEntryHeight().
        if (rowLayout.totalHeight > viewportH && viewportH > 0) {
            int scrollBarHeight = viewportH;
            int thumbHeight = Math.max(10, (int) ((long) scrollBarHeight * viewportH / rowLayout.totalHeight));
            int scrollRange = rowLayout.totalHeight - viewportH;
            int thumbY = entryYStart + (scrollBarHeight - thumbHeight)
                    * Math.max(0, Math.min(scrollPx, scrollRange)) / Math.max(1, scrollRange);
            int scrollX = this.width - 12;
            boolean scrollHot = mouseX >= scrollX - 3 && mouseX < scrollX + 5
                    && mouseY >= entryYStart && mouseY < entryYStart + scrollBarHeight;
            float scrollGlow = anim.hover("scrollbar", scrollHot);
            g.fill(scrollX, entryYStart, scrollX + 2, entryYStart + scrollBarHeight, Theme.SCROLL_TRACK);
            g.fill(scrollX, thumbY, scrollX + 2, thumbY + thumbHeight,
                    Theme.lerp(Theme.SCROLL_THUMB, Theme.SCROLL_THUMB_HOT, scrollGlow));
        }

        // Entry count (yellow, at bottom-left with its own space)
        // Settings count at top-right (moved from bottom-left to avoid overlap with reset button)
        String settingsText = searchMode
            ? "\u00A7eShowing " + filteredEntries.size() + " of " + getTotalConfigCount() + " settings"
            : "\u00A7e" + filteredEntries.size() + " settings";
        int settingsW = this.font.width(settingsText.replaceAll("\u00A7.", ""));
        g.drawString(this.font, settingsText, this.width - settingsW - 8, 12, 0xFFAA00);

        // Modifier key hint for numeric entries (updated scheme)
        g.drawCenteredString(this.font, "\u00A7eCtrl:x1  Ctrl+C:x0.01  Ctrl+Shift:x10  Shift:x100  Ctrl+Shift+C:x1000  Alt:x100000  |  Ctrl+Z:Undo  Ctrl+Y:Redo",
                this.width / 2, this.height - 12, 0xFFAA00);

        // ── Export/Import/Diff/Checksum/ResetTab/Save/Nav buttons at bottom-left ──
        int utilBtnY = this.height - 28;
        int utilBtnH = 12;
        String[] utilLabels = {"Export", "Import", "Diff", "Checksum", "Reset Tab", "Save", "Preset", "SavePreset", "Nav"};
        int utilX = 8;
        for (String label : utilLabels) {
            int utilW = this.font.width(label) + 6;
            boolean utilHover = mouseX >= utilX && mouseX < utilX + utilW && mouseY >= utilBtnY && mouseY < utilBtnY + utilBtnH;
            boolean utilToggledOn = "Nav".equals(label) && minimapNav.isVisible();
            float utilGlow = anim.hover("util:" + label, utilHover);
            int utilBg = utilToggledOn
                    ? Theme.lerp(Theme.UTIL_ON_BG, Theme.UTIL_ON_BG_HOVER, utilGlow)
                    : Theme.lerp(Theme.UTIL_BG, Theme.UTIL_BG_HOVER, utilGlow);
            int utilBorder = utilToggledOn
                    ? Theme.UTIL_ON_BORDER
                    : Theme.lerp(Theme.UTIL_BORDER, Theme.UTIL_BORDER_HOVER, utilGlow);
            int utilText = utilToggledOn
                    ? Theme.UTIL_ON_TEXT
                    : Theme.lerp(Theme.UTIL_TEXT, Theme.TEXT_PRIMARY, utilGlow);
            g.fill(utilX, utilBtnY, utilX + utilW, utilBtnY + utilBtnH, utilBg);
            g.renderOutline(utilX, utilBtnY, utilW, utilBtnH, utilBorder);
            g.drawString(this.font, label, utilX + 3, utilBtnY + 2, utilText);
            utilX += utilW + 2;
        }

        // ── Group buttons (3rd level navigation) ──
        // Drawn straight from the shared groupBar rects. This block used to run its
        // own wrap loop, mouseClicked() ran a second copy and renderTabTooltips() a
        // third (which even wrapped to gx = 24 instead of gx = 8), so a wrapped group
        // button could be painted in one place and clicked in another.
        if (!searchMode && groupBar != null) {
            for (int gi = 0; gi < groupBar.size(); gi++) {
                int[] r = groupBar.rects.get(gi);
                String label = groupBar.labels.get(gi);
                boolean isAll = gi == 0;
                boolean grpActive = isAll ? activeGroup.isEmpty() : label.equals(activeGroup);
                boolean grpHover = mouseX >= r[0] && mouseX < r[0] + r[2]
                        && mouseY >= r[1] && mouseY < r[1] + r[3];
                float grpGlow = anim.hover("group:" + label, grpHover);
                int exp = anim.hoverExpand(grpGlow, 2);
                int gx = r[0] - exp, gy = r[1] - exp, gw = r[2] + exp * 2, gh = r[3] + exp * 2;

                int grpBg = grpActive
                        ? Theme.ACCENT3_BG_ACTIVE
                        : Theme.lerp(Theme.ACCENT3_BG, Theme.ACCENT3_BG_HOVER, grpGlow);
                int grpBorder = grpActive
                        ? Theme.ACCENT3_BORDER_ON
                        : Theme.lerp(Theme.ACCENT3_BORDER, Theme.ACCENT3, grpGlow);
                g.fill(gx, gy, gx + gw, gy + gh, grpBg);
                g.renderOutline(gx, gy, gw, gh, grpBorder);
                if (grpGlow > 0.01f) {
                    g.fill(gx, gy, gx + gw, gy + gh, Theme.withAlpha(0x30FF8040, grpGlow));
                }
                int grpText = Theme.lerp(Theme.ACCENT3, Theme.ACCENT3_HOVER, grpGlow);
                if (isAll) {
                    g.drawCenteredString(this.font, label, gx + gw / 2, gy + 4, grpText);
                } else {
                    g.drawString(this.font, label, gx + 4, gy + 4, grpText);
                }
                // 3rd-level group chips: a small cycling bagua trigram mark in the
                // corner (cinnabar/vermillion, matching this tier's existing color) -
                // distinct from the flame/taiji motifs used by the two tab tiers above,
                // since these chips are much smaller than a full tab.
                if (grpActive && !isReduceMotion()) {
                    drawBreathingGlow(g, gx, gy, gw, gh, 0xFFFF8060);
                    if (gw >= 16 && gh >= 10) {
                        drawTrigramMark(g, gx + 2, gy + 1, 0xFFFFB090);
                    }
                }
            }
        }

        // ── Custom render top-level tabs and sub-tabs are now rendered above ──

        // Render tab descriptions as tooltips when hovering tabs (transient only — pinned tooltip rendered later)
        renderTabTooltips(g, mouseX, mouseY);

        // Render edit field if active
        if (editField != null) {
            editField.render(g, mouseX, mouseY, partialTick);
        }

        super.render(g, mouseX, mouseY, partialTick);

        // ── Pinned tooltips render on TOP of everything (floating windows at higher Z) ──
        renderAllPinnedTooltips(g, mouseX, mouseY);

        // ── Keyword glossary hover-popup: disabled per explicit request - it was
        // showing as a second box stacked under/beside the main configurable
        // tooltip, which read as clutter rather than a feature. renderKeywordPopup()
        // and TooltipGlossary are left in place unused rather than deleted (nothing
        // in renderEntryTooltip() sets pendingKeywordPopup anymore, so this never
        // fires) in case a simpler, standalone version of this is wanted later. ──

        // ── Save confirmation animation ──
        if (saveAnimStart > 0) {
            long elapsed = System.currentTimeMillis() - saveAnimStart;
            if (elapsed < SAVE_ANIM_DURATION_MS) {
                float progress = (float) elapsed / SAVE_ANIM_DURATION_MS;
                // Fade in then out
                float alpha = progress < 0.3f ? progress / 0.3f : (1.0f - (progress - 0.3f) / 0.7f);
                int a = (int)(alpha * 200);
                // Green confirmation banner in center
                String msg = "\u00A7a\u00A7l\u2713 Config Saved!";
                int msgW = this.font.width(msg.replaceAll("\u00A7.", ""));
                int bannerW = msgW + 40;
                int bannerH = 24;
                int bx = this.width / 2 - bannerW / 2;
                int by = this.height / 2 - bannerH / 2;
                g.fill(bx, by, bx + bannerW, by + bannerH, (a << 24) | 0x004000);
                g.renderOutline(bx, by, bannerW, bannerH, (a << 24) | 0x00FF00);
                g.drawCenteredString(this.font, msg, this.width / 2, by + 8, (0xFF << 24) | 0x00FF00);
            } else {
                saveAnimStart = 0;
            }
        }

        // ── Notification system (toast-style, top-right corner) ──
        renderNotifications(g);

        // ── Batch 12 popups (render on top, without darkening) ──
        if (colorWheel.isOpen()) {
            g.fill(0, 0, this.width, this.height, 0x80000000);
            colorWheel.render(g, this.width, this.height, mouseX, mouseY, this.font);
        }
        if (dropdownEnum.isOpen()) {
            g.fill(0, 0, this.width, this.height, 0x80000000);
            dropdownEnum.render(g, this.width, this.height, mouseX, mouseY, this.font);
        }
        if (multiLineEditor.isOpen()) {
            g.fill(0, 0, this.width, this.height, 0x80000000);
            multiLineEditor.render(g, mouseX, mouseY, this.font);
        }
    }

    /** Render toast-style notifications in the top-right corner. */
    private void renderNotifications(GuiGraphics g) {
        java.util.List<NotificationSystem.Notification> notifs = NotificationSystem.getActive();
        if (notifs.isEmpty()) return;
        int notifW = 200;
        int notifH = 20;
        int notifX = this.width - notifW - 4;
        int notifY = 4;
        for (int i = 0; i < notifs.size(); i++) {
            NotificationSystem.Notification n = notifs.get(i);
            float alpha = n.getAlpha();
            int a = (int)(alpha * 220);
            int y = notifY + i * (notifH + 2);
            g.fill(notifX, y, notifX + notifW, y + notifH, (a << 24) | 0x202020);
            g.renderOutline(notifX, y, notifW, notifH, (a << 24) | n.color);
            String text = n.text;
            if (this.font.width(text) > notifW - 8) {
                while (this.font.width(text + "...") > notifW - 8 && text.length() > 0) {
                    text = text.substring(0, text.length() - 1);
                }
                text = text + "...";
            }
            g.drawString(this.font, text, notifX + 4, y + 6, ((a & 0xFF) << 24) | 0xFFFFFF);
        }
    }

    /** Render top-level tabs and sub-tabs with distinct color scheme + hover enlargement. */
    /** Format a config value for display: add commas to large numbers, trim decimals. */
    /** True for the six Sects->Schedule fields that mark a POINT in the in-game
     *  day (0-24000) rather than a duration - these get a named time-of-day
     *  label instead of a raw tick count wherever they're displayed. */
    private boolean isSectScheduleTimeField(String configPath) {
        return configPath != null && SECT_SCHEDULE_TIME_PATHS.contains(configPath);
    }

    /** Names the closest of six evenly-spaced (4000-tick) times of day, dawn
     *  through midnight, wrapping around the 0-24000 in-game day - e.g. tick
     *  12000 -> "Dusk", tick 24000 (same moment as 0) -> "Dawn". */
    private String describeTimeOfDay(int tick) {
        int wrapped = ((tick % 24000) + 24000) % 24000;
        int bestIdx = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < SECT_TIME_ANCHOR_TICKS.length; i++) {
            int anchor = SECT_TIME_ANCHOR_TICKS[i];
            int dist = Math.min(Math.abs(wrapped - anchor), 24000 - Math.abs(wrapped - anchor));
            if (dist < bestDist) {
                bestDist = dist;
                bestIdx = i;
            }
        }
        return SECT_TIME_ANCHOR_NAMES[bestIdx];
    }

    /** Full label used wherever a Sects->Schedule time field's value is shown:
     *  the named time plus the raw tick count, e.g. "Dusk (12000)", so the
     *  exact configured value stays visible alongside the friendly name. */
    private String sectScheduleTimeLabel(String rawValStr) {
        if (rawValStr == null || rawValStr.isEmpty()) return rawValStr;
        try {
            int tick = (int) Math.round(Double.parseDouble(rawValStr.trim()));
            return describeTimeOfDay(tick) + " (" + tick + ")";
        } catch (NumberFormatException e) {
            return rawValStr;
        }
    }

    private String formatConfigValue(String valStr, String type, String configPath) {
        if (valStr == null || valStr.isEmpty()) return valStr;
        if (isSectScheduleTimeField(configPath)) {
            return sectScheduleTimeLabel(valStr);
        }
        try {
            if (type.equals("int") || type.equals("long")) {
                long val = Long.parseLong(valStr.trim());
                // Auto-round large numbers to compact display
                if (Math.abs(val) >= 1000000000L) {
                    return String.format("%,.2fB", val / 1000000000.0);
                } else if (Math.abs(val) >= 1000000L) {
                    return String.format("%,.2fM", val / 1000000.0);
                } else if (Math.abs(val) >= 10000L) {
                    return String.format("%,.1fK", val / 1000.0);
                }
                return String.format("%,d", val);
            }
            if (type.equals("double")) {
                double val = Double.parseDouble(valStr.trim());
                if (val == (long) val) {
                    long lv = (long) val;
                    if (Math.abs(lv) >= 1000000000L) return String.format("%,.2fB", val / 1000000000.0);
                    if (Math.abs(lv) >= 1000000L) return String.format("%,.2fM", val / 1000000.0);
                    if (Math.abs(lv) >= 10000L) return String.format("%,.1fK", val / 1000.0);
                    return String.format("%,d", lv);
                }
                // Auto-round: if more than 2 decimal places, round to 2
                if (val != Math.round(val * 100.0) / 100.0) {
                    val = Math.round(val * 100.0) / 100.0;
                }
                return String.format("%,.2f", val);
            }
        } catch (NumberFormatException e) { /* fall through */ }
        return valStr;
    }

    /** Get a Unicode icon character for an entry type. */
    private String getTypeIcon(String type, String configPath) {
        if (type.equals("boolean")) return "\u26AB"; // ● (filled circle for toggle)
        boolean isColor = configPath.contains("color") || configPath.contains("Color") ||
                          (configPath.startsWith("client.colors.") && type.equals("int"));
        if (isColor) return "\u25CF"; // ● (larger circle for color)
        if (type.equals("string")) {
            if (configPath.contains("startingItems") || configPath.contains(".items") ||
                configPath.startsWith("identity.custom_"))
                return "\u2727"; // ✧ (items)
            return "\u270E"; // ✎ (text)
        }
        if (type.equals("int") || type.equals("long")) return "#";
        if (type.equals("double")) return "~";
        return "?";
    }

    /** Get a color for the type icon. */
    private int getTypeIconColor(String type, String configPath) {
        if (type.equals("boolean")) return 0xFF40C040;
        boolean isColor = configPath.contains("color") || configPath.contains("Color") ||
                          (configPath.startsWith("client.colors.") && type.equals("int"));
        if (isColor) return 0xFFFF40FF;
        if (type.equals("string")) {
            if (configPath.contains("startingItems") || configPath.contains(".items") ||
                configPath.startsWith("identity.custom_"))
                return 0xFFFFA040;
            return 0xFFA0A0FF;
        }
        if (type.equals("int") || type.equals("long")) return 0xFF40A0FF;
        if (type.equals("double")) return 0xFF40FFFF;
        return 0xFF808080;
    }

    /** Check if reduce motion is enabled (disables all animations). */
    private boolean isReduceMotion() {
        try {
            return com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_REDUCE_MOTION.get();
        } catch (Exception e) { return false; }
    }

    /** Get the entry row height (compact = 22, normal = 24).
     *  Every row's own background/hover/click box is a fixed 22px tall (see the many
     *  "y + 22" bounds checks throughout this class), but this used to return 18 for
     *  compact mode - 4px less than the row box it was supposed to space out. That made
     *  consecutive rows overlap by 4px (background fills and text bleeding into each
     *  other) whenever "Compact Layout" was turned on. 22 is the smallest value that
     *  still avoids that overlap while remaining more compact than normal mode. */
    private int getEntryHeight() {
        try {
            if (com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_COMPACT_LAYOUT.get()) return 22;
        } catch (Exception e) { /* fall through */ }
        return 24;
    }

    /** Get the font scale (50% to 200% based on config). */
    private float getFontScale() {
        try {
            return com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_FONT_SIZE_PERCENT.get() / 100.0f;
        } catch (Exception e) { return 1.0f; }
    }

    /** Check if inline help is enabled. */
    private boolean isInlineHelpEnabled() {
        try {
            return com.xiaoxiang.configext.config.ExtendedConfig.CLIENT_ENABLE_INLINE_HELP.get();
        } catch (Exception e) { return false; }
    }

    /** Render search filter buttons below the search bar. */
    private void renderSearchFilters(GuiGraphics g, int mouseX, int mouseY) {
        int searchW = Math.min(220, this.width - 40);
        int filterY = 28;
        int filterX = this.width / 2 - searchW / 2;
        String[] filterNames = {"All", "Modified", "Bool", "Num", "Color", "String", "OverMax", "Fav"};
        SearchFilter[] filterValues = SearchFilter.values();
        for (int i = 0; i < filterNames.length && i < filterValues.length; i++) {
            int fw = this.font.width(filterNames[i]) + 6;
            boolean active = activeFilter == filterValues[i];
            boolean hover = mouseX >= filterX && mouseX < filterX + fw && mouseY >= filterY && mouseY < filterY + 12;
            int bg = active ? 0xFF4060A0 : (hover ? 0xFF303050 : 0xFF202030);
            g.fill(filterX, filterY, filterX + fw, filterY + 12, bg);
            g.renderOutline(filterX, filterY, fw, 12, active ? 0xFF80A0FF : 0xFF404060);
            g.drawString(this.font, (active ? "\u00A7l" : "") + filterNames[i], filterX + 3, filterY + 2,
                active ? 0xFFFFFF : 0xFFA0A0A0);
            filterX += fw + 2;
        }
        // Global search toggle
        int gsX = filterX + 8;
        String gsLabel = globalSearch ? "\u00A7aGlobal" : "\u00A77Tab";
        int gsW = this.font.width(gsLabel.replaceAll("\u00A7.", "")) + 8;
        boolean gsHover = mouseX >= gsX && mouseX < gsX + gsW && mouseY >= filterY && mouseY < filterY + 12;
        int gsBg = globalSearch ? 0xFF205020 : (gsHover ? 0xFF303050 : 0xFF202030);
        g.fill(gsX, filterY, gsX + gsW, filterY + 12, gsBg);
        g.renderOutline(gsX, filterY, gsW, 12, globalSearch ? 0xFF40C040 : 0xFF404060);
        g.drawString(this.font, gsLabel, gsX + 4, filterY + 2, 0xFFFFFF);
    }

    /**
     * Draw a continuous converging four-corner wave animation on a tab border.
     * Phase 1: Glow starts at A (top-left), simultaneously travels to B (top-right) and C (bottom-left),
     *          then both converge at D (bottom-right).
     * Phase 2: Glow starts at D, simultaneously travels to B and C, then both converge back at A.
     * Continuous ping-pong converging pattern.
     */
    private void drawConvergingWaveAnimation(GuiGraphics g, int x, int y, int w, int h, int color) {
        long time = System.currentTimeMillis();
        double cycleDuration = 3000.0; // 3 seconds per phase
        double t = ((time % (cycleDuration * 2)) / cycleDuration); // 0..2, 0-1 = phase 1, 1-2 = phase 2
        boolean phase1 = t < 1.0;
        double progress = phase1 ? t : t - 1.0; // 0..1 within each phase

        // Extract RGB from color
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Corner coordinates: A=top-left, B=top-right, C=bottom-left, D=bottom-right
        int ax = x, ay = y;
        int bx = x + w, by = y;
        int cx = x, cy = y + h;
        int dx = x + w, dy = y + h;

        if (phase1) {
            // Phase 1: A → B and A → C, converging at D
            // Path 1: A to B (top edge), then B to D (right edge)
            // Path 2: A to C (left edge), then C to D (bottom edge)
            // At progress 0: both at A. At progress 0.5: at B and C. At progress 1.0: both at D.
            if (progress < 0.5) {
                double p = progress * 2; // 0..1 for first half
                // Path 1: A to B along top edge
                int p1x = ax + (int)((bx - ax) * p);
                int p1y = ay;
                // Path 2: A to C along left edge
                int p2x = ax;
                int p2y = ay + (int)((cy - ay) * p);
                drawGlowPoint(g, p1x, p1y, r, gc, b, 1.0f);
                drawGlowPoint(g, p2x, p2y, r, gc, b, 1.0f);
                // Draw trail along edges
                drawGlowLine(g, ax, ay, p1x, p1y, r, gc, b, 0.5f);
                drawGlowLine(g, ax, ay, p2x, p2y, r, gc, b, 0.5f);
            } else {
                double p = (progress - 0.5) * 2; // 0..1 for second half
                // Path 1: B to D along right edge
                int p1x = bx;
                int p1y = by + (int)((dy - by) * p);
                // Path 2: C to D along bottom edge
                int p2x = cx + (int)((dx - cx) * p);
                int p2y = cy;
                drawGlowPoint(g, p1x, p1y, r, gc, b, 1.0f);
                drawGlowPoint(g, p2x, p2y, r, gc, b, 1.0f);
                // Draw trails
                drawGlowLine(g, bx, by, p1x, p1y, r, gc, b, 0.5f);
                drawGlowLine(g, cx, cy, p2x, p2y, r, gc, b, 0.5f);
            }
        } else {
            // Phase 2: D → B and D → C, converging at A (reverse)
            if (progress < 0.5) {
                double p = progress * 2;
                // Path 1: D to B along right edge (reverse)
                int p1x = dx;
                int p1y = dy - (int)((dy - by) * p);
                // Path 2: D to C along bottom edge (reverse)
                int p2x = dx - (int)((dx - cx) * p);
                int p2y = dy;
                drawGlowPoint(g, p1x, p1y, r, gc, b, 1.0f);
                drawGlowPoint(g, p2x, p2y, r, gc, b, 1.0f);
                drawGlowLine(g, dx, dy, p1x, p1y, r, gc, b, 0.5f);
                drawGlowLine(g, dx, dy, p2x, p2y, r, gc, b, 0.5f);
            } else {
                double p = (progress - 0.5) * 2;
                // Path 1: B to A along top edge (reverse)
                int p1x = bx - (int)((bx - ax) * p);
                int p1y = by;
                // Path 2: C to A along left edge (reverse)
                int p2x = cx;
                int p2y = cy - (int)((cy - ay) * p);
                drawGlowPoint(g, p1x, p1y, r, gc, b, 1.0f);
                drawGlowPoint(g, p2x, p2y, r, gc, b, 1.0f);
                drawGlowLine(g, bx, by, p1x, p1y, r, gc, b, 0.5f);
                drawGlowLine(g, cx, cy, p2x, p2y, r, gc, b, 0.5f);
            }
        }
    }

    /** Draw a glowing point (3x3 with alpha falloff). */
    private void drawGlowPoint(GuiGraphics g, int x, int y, int r, int gc, int b, float intensity) {
        int alphaCore = (int)(255 * intensity);
        int alphaMid = (int)(120 * intensity);
        int alphaOuter = (int)(40 * intensity);
        g.fill(x - 1, y - 1, x + 2, y + 2, (alphaCore << 24) | (r << 16) | (gc << 8) | b);
        g.fill(x - 2, y - 2, x + 3, y + 3, (alphaMid << 24) | (r << 16) | (gc << 8) | b);
        g.fill(x - 3, y - 3, x + 4, y + 4, (alphaOuter << 24) | (r << 16) | (gc << 8) | b);
    }

    /** Draw a glowing line between two points (simple horizontal/vertical only). */
    private void drawGlowLine(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int gc, int b, float intensity) {
        int alpha = (int)(80 * intensity);
        int color = (alpha << 24) | (r << 16) | (gc << 8) | b;
        if (y1 == y2) {
            // Horizontal line
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            g.fill(minX, y1 - 1, maxX + 1, y1 + 2, color);
        } else if (x1 == x2) {
            // Vertical line
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            g.fill(x1 - 1, minY, x1 + 2, maxY + 1, color);
        }
    }

    /** Draw a breathing outer shadow glow around a tab (opacity oscillates 20%-40%). */
    private void drawBreathingGlow(GuiGraphics g, int x, int y, int w, int h, int color) {
        long time = System.currentTimeMillis();
        double breath = 0.5 + 0.5 * Math.sin(time / 800.0); // 0..1 oscillation
        int alpha = (int)(20 + breath * 20); // 20-40 alpha range
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int glowColor = (alpha << 24) | (r << 16) | (gc << 8) | b;
        // Draw glow border 2px outside the rect
        g.fill(x - 2, y - 2, x + w + 2, y - 1, glowColor); // top
        g.fill(x - 2, y + h + 1, x + w + 2, y + h + 2, glowColor); // bottom
        g.fill(x - 2, y - 1, x - 1, y + h + 1, glowColor); // left
        g.fill(x + w + 1, y - 1, x + w + 2, y + h + 1, glowColor); // right
    }

    /** Draw corner accent brackets (small L-shaped marks at each corner). */
    private void drawCornerAccents(GuiGraphics g, int x, int y, int w, int h, int color) {
        int len = 4;
        // Top-left corner
        g.fill(x - 1, y - 1, x + len, y, color);
        g.fill(x - 1, y - 1, x, y + len, color);
        // Top-right corner
        g.fill(x + w - len, y - 1, x + w + 1, y, color);
        g.fill(x + w, y - 1, x + w + 1, y + len, color);
        // Bottom-left corner
        g.fill(x - 1, y + h, x + len, y + h + 1, color);
        g.fill(x - 1, y + h - len, x, y + h + 1, color);
        // Bottom-right corner
        g.fill(x + w - len, y + h, x + w + 1, y + h + 1, color);
        g.fill(x + w, y + h - len, x + w + 1, y + h + 1, color);
    }

    /** Ping-pong wave animation: a highlight bar sweeps left-right across the top edge.
     *  Faster (1.5s cycle) with contrasting color. Used for top-level tabs. */
    private void drawPingPongWaveAnimation(GuiGraphics g, int x, int y, int w, int h, int color) {
        long time = System.currentTimeMillis();
        double cycleDuration = 1500.0; // 1.5 seconds - faster than converging wave
        double t = (time % cycleDuration) / cycleDuration; // 0..1
        // Ping-pong: 0->1->0 using triangle wave
        double progress = t < 0.5 ? t * 2 : 2 - t * 2; // 0..1..0
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        // Draw a moving highlight bar (width = 20% of tab) along the top edge
        int barW = Math.max(8, w / 5);
        int barX = x + (int)(progress * (w - barW));
        // Bright bar on top edge
        g.fill(barX, y, barX + barW, y + 2, color);
        // Trailing fade
        int trailLen = barW / 2;
        for (int i = 0; i < trailLen; i++) {
            float alpha = (1.0f - (float)i / trailLen) * 0.5f;
            int a = (int)(alpha * 255);
            int tx = barX - i - 1;
            if (tx >= x) g.fill(tx, y, tx + 1, y + 2, (a << 24) | (r << 16) | (gc << 8) | b);
        }
        // Bottom edge mirror
        int barX2 = x + (int)((1.0 - progress) * (w - barW));
        g.fill(barX2, y + h - 2, barX2 + barW, y + h, color);
    }

    /** Orbital animation: dots travel around the border of the rect in a circular path.
     *  Used for sub-tabs. Contrasting magenta color. */
    private void drawOrbitalAnimation(GuiGraphics g, int x, int y, int w, int h, int color) {
        long time = System.currentTimeMillis();
        double cycleDuration = 2000.0; // 2 seconds per loop
        double t = (time % cycleDuration) / cycleDuration; // 0..1
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        // Perimeter of the rect
        int perimeter = 2 * (w + h);
        // Draw 3 dots evenly spaced around the perimeter
        for (int d = 0; d < 3; d++) {
            double dotT = (t + d / 3.0) % 1.0;
            int dist = (int)(dotT * perimeter);
            int dx, dy;
            if (dist < w) {
                dx = x + dist; dy = y;
            } else if (dist < w + h) {
                dx = x + w; dy = y + (dist - w);
            } else if (dist < 2 * w + h) {
                dx = x + w - (dist - w - h); dy = y + h;
            } else {
                dx = x; dy = y + h - (dist - 2 * w - h);
            }
            drawGlowPoint(g, dx, dy, r, gc, b, 1.0f);
        }
    }

    /** Pulsing border glow: the outline brightness oscillates.
     *  Used for group buttons. Contrasting yellow color. */
    private void drawPulsingBorderGlow(GuiGraphics g, int x, int y, int w, int h, int color) {
        long time = System.currentTimeMillis();
        double cycleDuration = 1200.0; // 1.2 seconds - fast pulse
        double t = (time % cycleDuration) / cycleDuration;
        double pulse = 0.5 + 0.5 * Math.sin(t * Math.PI * 2); // 0..1
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        // Pulsing outline (brighter at peak)
        int alpha = (int)(100 + pulse * 155);
        int glowColor = (alpha << 24) | (r << 16) | (gc << 8) | b;
        // Draw 1px outside the rect
        g.renderOutline(x - 1, y - 1, w + 2, h + 2, glowColor);
        // Inner glow at peak
        if (pulse > 0.7) {
            int innerAlpha = (int)((pulse - 0.7) * 80);
            g.fill(x, y, x + w, y + 1, (innerAlpha << 24) | (r << 16) | (gc << 8) | b);
            g.fill(x, y + h - 1, x + w, y + h, (innerAlpha << 24) | (r << 16) | (gc << 8) | b);
        }
    }

    // ── Cultivator-theme animation set (legacy) ─────────────────────────────
    // NOTE: this original "flowing qi" set (one shared effect recolored per
    // tier) was replaced below by three genuinely distinct, tier-specific
    // motifs - rising flame for top tabs, a rotating taiji (yin-yang) medallion
    // for sub-tabs, and a cycling bagua trigram mark for group chips - per
    // explicit feedback that one repeated animation reading as three colors of
    // the same thing wasn't the "amazing / cultivation style" look wanted. The
    // functions below are kept only as shared low-level helpers (drawGlowPoint
    // via drawEmbers, etc.) or as unused reference code; none of the three tab
    // tiers call drawQiStreamBorder/drawQiMotes/drawInkBrushCorners anymore -
    // see the "Cultivator-theme animation set v2" block further down for the
    // functions actually wired into renderCustomTabs().

    /**
     * A point on the rect's perimeter, `dist` px clockwise from the top-left corner.
     * Shared by drawOrbitalAnimation and drawQiStreamBorder so both trace the same path.
     */
    private int[] pointOnPerimeter(int x, int y, int w, int h, int dist) {
        int dx, dy;
        if (dist < w) {
            dx = x + dist; dy = y;
        } else if (dist < w + h) {
            dx = x + w; dy = y + (dist - w);
        } else if (dist < 2 * w + h) {
            dx = x + w - (dist - w - h); dy = y + h;
        } else {
            dx = x; dy = y + h - (dist - 2 * w - h);
        }
        return new int[]{dx, dy};
    }

    /**
     * Qi stream: a bright head with a fading trail flows continuously around the whole
     * border, like circulating spiritual energy through a meridian. Generalizes the
     * old top-edge-only wave bar and the isolated orbital dots into one shared "energy
     * flowing around the frame" motif, reused at all three tab tiers with different
     * color/speed/trail length.
     */
    private void drawQiStreamBorder(GuiGraphics g, int x, int y, int w, int h, int r, int gc, int b, double cycleMs, int trailLen) {
        int perimeter = 2 * (w + h);
        if (perimeter <= 0) return;
        long time = System.currentTimeMillis();
        double t = (time % cycleMs) / cycleMs;
        int headDist = (int) (t * perimeter);
        for (int i = 0; i < trailLen; i++) {
            int dist = ((headDist - i) % perimeter + perimeter) % perimeter;
            float alpha = 1.0f - (float) i / trailLen;
            int[] p = pointOnPerimeter(x, y, w, h, dist);
            int a = (int) (alpha * 200);
            g.fill(p[0] - 1, p[1] - 1, p[0] + 2, p[1] + 2, (a << 24) | (r << 16) | (gc << 8) | b);
        }
    }

    /**
     * Rising qi motes: small glowing points drift upward from the bottom edge to the
     * top, swaying gently side to side, fading in near the bottom and out near the
     * top - like spirit energy or incense smoke rising off the tab. Staggered per-mote
     * phase so they don't all rise in lockstep.
     */
    private void drawQiMotes(GuiGraphics g, int x, int y, int w, int h, int r, int gc, int b, double cycleMs, int count) {
        if (count <= 0 || h <= 0) return;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            double phase = i / (double) count;
            double t = ((time / cycleMs) + phase) % 1.0; // 0..1, staggered per mote
            int moteY = y + h - (int) (t * h);
            double sway = Math.sin((t * 3.0 + phase * 7.0) * Math.PI * 2.0) * (w * 0.12);
            int moteX = (int) (x + w / 2.0 + sway);
            float alpha;
            if (t < 0.15) alpha = (float) (t / 0.15);
            else if (t > 0.75) alpha = (float) ((1.0 - t) / 0.25);
            else alpha = 1.0f;
            drawGlowPoint(g, moteX, moteY, r, gc, b, alpha * 0.8f);
        }
    }

    /**
     * Ink-brush corner flourishes: an L-bracket per corner (as drawCornerAccents drew)
     * plus a small breathing-synced glow dot at each tip, evoking a brush-stroke's
     * tapered end / seal-stamp corner mark. The tip glow pulses on the same sine cycle
     * as drawBreathingGlow so a tab's corners and border glow breathe together.
     */
    private void drawInkBrushCorners(GuiGraphics g, int x, int y, int w, int h, int color) {
        int len = 5;
        g.fill(x - 1, y - 1, x + len, y, color);
        g.fill(x - 1, y - 1, x, y + len, color);
        g.fill(x + w - len, y - 1, x + w + 1, y, color);
        g.fill(x + w, y - 1, x + w + 1, y + len, color);
        g.fill(x - 1, y + h, x + len, y + h + 1, color);
        g.fill(x - 1, y + h - len, x, y + h + 1, color);
        g.fill(x + w - len, y + h, x + w + 1, y + h + 1, color);
        g.fill(x + w, y + h - len, x + w + 1, y + h + 1, color);

        long time = System.currentTimeMillis();
        double breath = 0.5 + 0.5 * Math.sin(time / 800.0);
        int r = (color >> 16) & 0xFF;
        int gc = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        float tipAlpha = (float) (0.4 + breath * 0.6);
        drawGlowPoint(g, x, y, r, gc, b, tipAlpha);
        drawGlowPoint(g, x + w, y, r, gc, b, tipAlpha);
        drawGlowPoint(g, x, y + h, r, gc, b, tipAlpha);
        drawGlowPoint(g, x + w, y + h, r, gc, b, tipAlpha);
    }

    // ── Cultivator-theme animation set v2 ───────────────────────────────────
    // Three deliberately different motifs, one per tab tier, instead of one
    // shared effect recolored three times:
    //   - Top tabs (gold):  drawFlameBorder + drawEmbers   - flames rising off
    //     the tab, like a brazier at a sect gate, with drifting embers above.
    //   - Sub-tabs (jade):  drawTaijiMedallion              - a small taiji
    //     (yin-yang) roundel slowly rotating in the tab's corner.
    //   - Group chips (cinnabar): drawTrigramMark           - a cycling bagua
    //     trigram (3 stacked yang/yin lines) in the chip's corner.
    // All three are driven purely by System.currentTimeMillis(), same as the
    // legacy set above, so they need no per-frame state and stay cheap to call
    // from render().

    /**
     * Rising flame licks along the bottom edge of a tab: each flame is a
     * tapering stack of rows (wide base -> narrow tip) that flickers in
     * height/sway using a per-flame pseudo-random phase (hashed from its
     * index) so the flames don't all pulse in lockstep. Color interpolates
     * from the hot base color (r,gc,b) at the base to a bright near-white tip.
     */
    private void drawFlameBorder(GuiGraphics g, int x, int y, int w, int h, int r, int gc, int b, double cycleMs, int flameCount) {
        if (flameCount <= 0 || w <= 0 || h <= 0) return;
        long time = System.currentTimeMillis();
        int baseY = y + h - 1;
        double spacing = (double) w / flameCount;
        for (int i = 0; i < flameCount; i++) {
            double seed = Math.sin(i * 12.9898) * 43758.5453;
            seed -= Math.floor(seed); // deterministic 0..1 pseudo-random per flame index
            double t = ((time + seed * cycleMs) % cycleMs) / cycleMs;
            double flicker = 0.55 + 0.45 * Math.sin((time / 90.0) + i * 2.3 + seed * 6.28318);
            int flameH = (int) (h * 0.9 * (0.4 + 0.35 * flicker) * (0.75 + 0.25 * Math.sin(t * Math.PI * 2 + seed * 6.28318)));
            flameH = Math.max(2, Math.min(h + 2, flameH));
            int fx = x + (int) (spacing * i + spacing / 2);
            int segments = Math.max(3, flameH / 2);
            for (int s = 0; s < segments; s++) {
                double frac = (double) s / segments; // 0 at base, 1 at tip
                int rowY = baseY - (int) (frac * flameH);
                int rowW = Math.max(1, (int) ((1.0 - frac) * 3.0) + 1);
                double sway = Math.sin(time / 240.0 + i * 1.7 + frac * 3.0) * 1.6 * frac;
                int rowX = fx + (int) sway - rowW / 2;
                int rr = (int) (r + (255 - r) * frac);
                int rgg = (int) (gc + (255 - gc) * frac * 0.9);
                int rb = (int) (b * (1 - frac) + 190 * frac);
                int alpha = (int) (215 * (1.0 - frac * 0.4));
                g.fill(rowX, rowY - 1, rowX + rowW, rowY, (alpha << 24) | (rr << 16) | (rgg << 8) | rb);
            }
        }
    }

    /**
     * Drifting embers that rise up from a flame-bordered tab and fade out,
     * complementing drawFlameBorder. Reuses drawGlowPoint for the actual dot.
     */
    private void drawEmbers(GuiGraphics g, int x, int y, int w, int h, int r, int gc, int b, double cycleMs, int count) {
        if (count <= 0 || h <= 0) return;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            double phase = i / (double) count;
            double t = ((time / cycleMs) + phase) % 1.0;
            int emberY = (y + h) - (int) (t * (h + 6));
            double sway = Math.sin((t * 4.0 + phase * 9.0) * Math.PI * 2.0) * (w * 0.10);
            int emberX = (int) (x + w * (0.12 + 0.76 * ((phase * 5.0) % 1.0)) + sway);
            float alpha;
            if (t < 0.15) alpha = (float) (t / 0.15);
            else if (t > 0.75) alpha = (float) ((1.0 - t) / 0.25);
            else alpha = 1.0f;
            drawGlowPoint(g, emberX, emberY, r, gc, b, alpha * 0.85f);
        }
    }

    /**
     * A small taiji (yin-yang) medallion, rotating continuously, rendered
     * pixel-by-pixel using the classic two-half-circle + two-dot construction:
     * the disc is split by an S-curve made from two radius/2 circles centered
     * a quarter-turn apart, with a small dot of the opposite shade at each of
     * those circles' centers. `angle` rotates the whole construction.
     */
    private void drawTaijiMedallion(GuiGraphics g, int cx, int cy, int radius, int lightColor, int darkColor) {
        if (radius <= 0) return;
        long time = System.currentTimeMillis();
        double angle = (time % 4000L) / 4000.0 * Math.PI * 2.0;
        double cos = Math.cos(-angle), sin = Math.sin(-angle);
        int halfR = Math.max(1, radius / 2);
        int dotR = Math.max(1, radius / 5);
        for (int py = -radius; py <= radius; py++) {
            for (int px = -radius; px <= radius; px++) {
                double dist = Math.sqrt(px * px + py * py);
                if (dist > radius) continue;
                double lx = px * cos - py * sin;
                double ly = px * sin + py * cos;
                boolean upper = ly < 0;
                double distUpperSmall = Math.sqrt(lx * lx + (ly + halfR) * (ly + halfR));
                double distLowerSmall = Math.sqrt(lx * lx + (ly - halfR) * (ly - halfR));
                boolean light;
                if (upper) {
                    light = distUpperSmall <= halfR;
                } else {
                    light = !(distLowerSmall <= halfR);
                }
                if (distUpperSmall <= dotR) light = false;
                if (distLowerSmall <= dotR) light = true;
                int col = light ? lightColor : darkColor;
                g.fill(cx + px, cy + py, cx + px + 1, cy + py + 1, col);
            }
        }
        g.renderOutline(cx - radius, cy - radius, radius * 2 + 1, radius * 2 + 1, (darkColor & 0x00FFFFFF) | 0x90000000);
    }

    /**
     * The eight bagua trigrams, each three lines top-to-bottom, true = solid
     * yang line, false = broken (two half-bars) yin line. Real trigram order
     * (Qian, Dui, Li, Zhen, Xun, Kan, Gen, Kun) so the mark that cycles through
     * them on group chips is drawing actual trigrams, not arbitrary bars.
     */
    private static final boolean[][] TRIGRAM_LINES = {
        {true, true, true},    // Qian - Heaven
        {false, true, true},   // Dui - Lake
        {true, false, true},   // Li - Fire
        {false, false, true},  // Zhen - Thunder
        {true, true, false},   // Xun - Wind
        {false, true, false},  // Kan - Water
        {true, false, false},  // Gen - Mountain
        {false, false, false}, // Kun - Earth
    };

    /** Draws whichever trigram the shared clock currently selects, cycling every 900ms. */
    private void drawTrigramMark(GuiGraphics g, int x, int y, int color) {
        long time = System.currentTimeMillis();
        int idx = (int) ((time / 900L) % TRIGRAM_LINES.length);
        boolean[] lines = TRIGRAM_LINES[idx];
        int barW = 7, barH = 1, gap = 1, lineGap = 2;
        for (int i = 0; i < 3; i++) {
            int ly = y + i * (barH + lineGap);
            boolean yang = lines[i];
            if (yang) {
                g.fill(x, ly, x + barW, ly + barH, color);
            } else {
                int half = (barW - gap) / 2;
                g.fill(x, ly, x + half, ly + barH, color);
                g.fill(x + half + gap, ly, x + barW, ly + barH, color);
            }
        }
    }

    private void renderCustomTabs(GuiGraphics g, int mouseX, int mouseY) {
        // ── Top-level tabs (GOLDEN with bold text + search glow) ──
        // Use custom tab order from CustomTabManager
        List<String> topTabNames = new ArrayList<>(CustomTabManager.getTabOrder());
        topTabNames.removeIf(name -> !TAB_TO_SUBTABS.containsKey(name) && !CustomTabManager.isCustomTab(name));
        String topSearchQuery = searchBox.getValue();
        boolean topHasSearch = !topSearchQuery.isBlank();
        String topLowerQuery = topHasSearch ? topSearchQuery.toLowerCase() : "";

        for (int i = 0; i < topTabRects.size() && i < topTabNames.size(); i++) {
            int[] rect = topTabRects.get(i);
            String tabName = topTabNames.get(i);
            boolean isActive = tabName.equals(activeTopTab);
            boolean isHover = mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3];

            // Calculate search glow for top-level tab (count matches across all sub-tabs)
            float topGlow = 0f;
            int topMatchCount = 0;
            if (topHasSearch) {
                List<String> subs = TAB_TO_SUBTABS.getOrDefault(tabName, List.of());
                for (String sub : subs) {
                    String subKey = tabName + "." + sub;
                    String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subKey, "");
                    if (!prefix.isEmpty()) {
                        for (String path : ConfigValueAccessor.getAllPaths()) {
                            if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                                ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
                                String display = info != null ? info.displayName : path;
                                String desc = info != null ? info.description : "";
                                if (path.toLowerCase().contains(topLowerQuery) ||
                                    display.toLowerCase().contains(topLowerQuery) ||
                                    desc.toLowerCase().contains(topLowerQuery) ||
                                    path.replace(".", " ").replace("_", " ").toLowerCase().contains(topLowerQuery)) {
                                    topMatchCount++;
                                }
                            }
                        }
                    }
                }
                topGlow = Math.min(1.0f, 0.1f + topMatchCount * 0.05f);
            }

            // Hover enlargement + highlight now ease over ~120ms instead of snapping.
            float tabGlow = anim.hover("toptab:" + tabName, isHover);
            int expand = anim.hoverExpand(tabGlow, 2);
            int rx = rect[0] - expand;
            int ry = rect[1] - expand;
            int rw = rect[2] + expand * 2;
            int rh = rect[3] + expand * 2;

            // Golden color scheme
            int bg = isActive ? Theme.ACCENT_BG_ACTIVE : Theme.lerp(Theme.ACCENT_BG, Theme.ACCENT_BG_HOVER, tabGlow);
            int outline = isActive ? Theme.ACCENT : Theme.lerp(Theme.ACCENT_DIM, Theme.ACCENT_HOVER, tabGlow);
            g.fill(rx, ry, rx + rw, ry + rh, bg);

            // Search glow overlay for top-level tabs
            if (topHasSearch && topGlow > 0) {
                int glowAlpha = (int)(topGlow * 80);
                g.fill(rx, ry, rx + rw, ry + rh, (glowAlpha << 24) | 0x0040A0);
                // Brighter outline when glowing
                int glowOutline = (0xFF << 24) | ((int)(100 + topGlow * 80) << 16) | ((int)(150 + topGlow * 100) << 8) | 255;
                outline = glowOutline;
            }

            g.renderOutline(rx, ry, rw, rh, outline);
            if (tabGlow > 0.01f) g.fill(rx, ry, rx + rw, ry + rh, Theme.withAlpha(0x30FFFF80, tabGlow));

            // Truncate label - account for bold width (\u00A7l) and shadow effect (+2px)
            String label = tabName;
            int maxTextW = rw - 8; // padding for bold + shadow
            if (this.font.width("\u00A7l" + label) > maxTextW) {
                while (this.font.width("\u00A7l" + label + "...") > maxTextW && label.length() > 0) label = label.substring(0, label.length() - 1);
                label = label + "...";
            }
            // Bold golden text with dark outline for readability
            int textX = rx + rw / 2;
            int textY = ry + (rh - 8) / 2;
            // Draw shadow outline (5 directions for bold pop effect)
            g.drawCenteredString(this.font, "\u00A7l\u00A70" + label, textX, textY, 0xFF000000);
            g.drawCenteredString(this.font, "\u00A7l\u00A70" + label, textX + 1, textY, 0xFF000000);
            g.drawCenteredString(this.font, "\u00A7l\u00A70" + label, textX - 1, textY, 0xFF000000);
            g.drawCenteredString(this.font, "\u00A7l\u00A70" + label, textX, textY + 1, 0xFF000000);
            g.drawCenteredString(this.font, "\u00A7l\u00A70" + label, textX, textY - 1, 0xFF000000);
            // Main golden text on top
            int goldColor = isActive ? Theme.ACCENT_HOVER : Theme.lerp(Theme.ACCENT, Theme.ACCENT_HOVER, tabGlow);
            g.drawCenteredString(this.font, "\u00A7l" + label, textX, textY, goldColor);

            // Continuous animation for active top tab: flames rising off the bottom
            // edge (like a brazier at a sect gate) with embers drifting above, plus
            // the existing breathing glow.
            if (isActive && !isReduceMotion()) {
                drawBreathingGlow(g, rx, ry, rw, rh, 0xFFFFD700);
                drawFlameBorder(g, rx, ry, rw, rh, 255, 100, 20, 900, Math.max(3, rw / 14));
                drawEmbers(g, rx, ry, rw, rh, 255, 200, 90, 2400, 3);
            }
        }

        // ── Sub-tabs (indented, with hover enlargement + search glow) ──
        List<String> subTabNames = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
        String searchQuery = searchBox.getValue();
        boolean hasSearch = !searchQuery.isBlank();
        String lowerQuery = hasSearch ? searchQuery.toLowerCase() : "";

        for (int i = 0; i < subTabRects.size() && i < subTabNames.size(); i++) {
            int[] rect = subTabRects.get(i);
            String subName = subTabNames.get(i);
            boolean isActive = subName.equals(activeSubTab);
            boolean isHover = mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3];

            // Calculate search glow intensity for this sub-tab
            // glow = 0 (no match) to 1.0 (perfect match)
            float glowIntensity = 0f;
            int matchCount = 0;
            if (hasSearch) {
                String subTabKey = activeTopTab + "." + subName;
                String prefix = SUBTAB_TO_PATH_PREFIX.getOrDefault(subTabKey, "");
                if (!prefix.isEmpty()) {
                    for (String path : ConfigValueAccessor.getAllPaths()) {
                        if (path.startsWith(prefix + ".") || path.equals(prefix)) {
                            ConfigEntryInfo info = ConfigDescriptionRegistry.get(path);
                            String display = info != null ? info.displayName : path;
                            String desc = info != null ? info.description : "";
                            if (path.toLowerCase().contains(lowerQuery) ||
                                display.toLowerCase().contains(lowerQuery) ||
                                desc.toLowerCase().contains(lowerQuery) ||
                                path.replace(".", " ").replace("_", " ").toLowerCase().contains(lowerQuery)) {
                                matchCount++;
                            }
                        }
                    }
                }
                // Glow intensity scales with match count: 1 match = 0.3, 5+ matches = 1.0
                glowIntensity = Math.min(1.0f, 0.2f + matchCount * 0.15f);
                if (matchCount == 0) glowIntensity = 0f;
            }

            // Hover enlargement + highlight, eased the same way as the top tabs.
            float subGlow = anim.hover("subtab:" + activeTopTab + "." + subName, isHover);
            int expand = anim.hoverExpand(subGlow, 2);
            int rx = rect[0] - expand;
            int ry = rect[1] - expand;
            int rw = rect[2] + expand * 2;
            int rh = rect[3] + expand * 2;

            int bg = isActive ? Theme.ACCENT2_BG_ACTIVE : Theme.lerp(Theme.ACCENT2_BG, Theme.ACCENT2_BG_HOVER, subGlow);
            g.fill(rx, ry, rx + rw, ry + rh, bg);

            // Search glow overlay - brighter outline + inner glow when matches found
            int outlineColor;
            if (hasSearch && glowIntensity > 0) {
                // Blend from blue (low glow) to bright cyan-white (high glow)
                int glowAlpha = (int)(glowIntensity * 255);
                int glowR = (int)(40 + glowIntensity * 80);
                int glowG = (int)(120 + glowIntensity * 135);
                int glowB = (int)(200 + glowIntensity * 55);
                outlineColor = (0xFF << 24) | (glowR << 16) | (glowG << 8) | glowB;
                // Inner glow fill
                int innerGlowAlpha = (int)(glowIntensity * 60);
                g.fill(rx, ry, rx + rw, ry + rh, (innerGlowAlpha << 24) | (glowR << 16) | (glowG << 8) | glowB);
            } else if (isActive) {
                outlineColor = Theme.ACCENT2;
            } else {
                outlineColor = Theme.lerp(Theme.PANEL_BORDER, Theme.ACCENT2_HOVER, subGlow);
            }
            g.renderOutline(rx, ry, rw, rh, outlineColor);
            if (subGlow > 0.01f) g.fill(rx, ry, rx + rw, ry + rh, Theme.withAlpha(0x30FFFF80, subGlow));

            // Truncate with proper padding
            String label = subName;
            int maxTextW = rw - 8;
            if (this.font.width(label) > maxTextW) {
                while (this.font.width(label + "...") > maxTextW && label.length() > 0) label = label.substring(0, label.length() - 1);
                label = label + "...";
            }
            // Text color: bright cyan when glowing, white normally
            int textColor;
            if (hasSearch && glowIntensity > 0.3f) {
                // Bright glowing text
                int tR = (int)(180 + glowIntensity * 75);
                int tG = (int)(220 + glowIntensity * 35);
                int tB = 255;
                textColor = (0xFF << 24) | (tR << 16) | (tG << 8) | tB;
            } else if (isActive) {
                textColor = Theme.TEXT_PRIMARY;
            } else {
                textColor = Theme.lerp(Theme.TEXT_PRIMARY, Theme.ACCENT2_HOVER, subGlow);
            }
            g.drawCenteredString(this.font, label, rx + rw / 2, ry + (rh - 8) / 2, textColor);

            // Draw match count badge when searching
            if (hasSearch && matchCount > 0) {
                String badge = String.valueOf(matchCount);
                int badgeW = this.font.width(badge) + 4;
                int badgeH = 8;
                int badgeX = rx + rw - badgeW - 2;
                int badgeY = ry + 1;
                g.fill(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, 0xC0002040);
                g.renderOutline(badgeX, badgeY, badgeW, badgeH, outlineColor);
                g.drawString(this.font, badge, badgeX + 2, badgeY, 0xFF80C0FF);
            }

            // Continuous animation for active sub-tab: a small taiji (yin-yang)
            // medallion slowly rotating in the bottom-right corner, plus the
            // existing breathing glow - deliberately different from the top tab's
            // flames so each tier reads as its own motif rather than a recolor.
            if (isActive && !isReduceMotion()) {
                drawBreathingGlow(g, rx, ry, rw, rh, 0xFF30A040);
                int medallionRadius = Math.max(3, Math.min(7, (rh - 4) / 2));
                if (rw > medallionRadius * 2 + 20) {
                    int medCx = rx + rw - medallionRadius - 3;
                    int medCy = ry + rh - medallionRadius - 2;
                    drawTaijiMedallion(g, medCx, medCy, medallionRadius, 0xFFEFFFEF, 0xFF0E3A1E);
                }
            }
        }
    }

    /**
     * Draw text with all occurrences of the query highlighted in a different color.
     * Characters are drawn one at a time with proper x-offset tracking to prevent
     * overlap between bold (highlighted) and non-bold text.
     */
    private void drawHighlightedText(GuiGraphics g, Font font, String text, int x, int y,
                                      int baseColor, String lowerQuery, int highlightColor) {
        if (lowerQuery.isEmpty()) {
            g.drawString(font, text, x, y, baseColor);
            return;
        }
        // Strip formatting codes for matching purposes
        String stripped = text.replaceAll("\u00A7.", "");
        String lowerStripped = stripped.toLowerCase();
        int drawX = x;
        int stripIdx = 0; // position in stripped string
        int textIdx = 0;  // position in original string

        while (stripIdx < stripped.length()) {
            // Skip any formatting codes in the original text
            while (textIdx < text.length() && text.charAt(textIdx) == '\u00A7') {
                textIdx += 2;
            }
            if (textIdx >= text.length()) break;

            // Check if query matches at current position
            boolean isMatch = lowerStripped.startsWith(lowerQuery, stripIdx);
            char c = stripped.charAt(stripIdx);

            if (isMatch) {
                // Draw the entire matched portion as bold highlighted text
                String matched = stripped.substring(stripIdx, stripIdx + lowerQuery.length());
                g.drawString(font, "\u00A7l" + matched, drawX, y, highlightColor);
                // Advance by the BOLD width (bold is 1px wider per character)
                drawX += font.width("\u00A7l" + matched);
                stripIdx += lowerQuery.length();
                textIdx += lowerQuery.length();
            } else {
                // Draw single character in base color
                g.drawString(font, String.valueOf(c), drawX, y, baseColor);
                drawX += font.width(String.valueOf(c));
                textIdx++;
                stripIdx++;
            }
        }
    }

    private void renderEntryTooltip(GuiGraphics g, DisplayEntry entry, int x, int y) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        // Auto-resize: calculate optimal tooltip width based on content
        int tooltipWidth = getAutoTooltipWidth(entry);
        tooltip.addAll(this.font.split(Component.literal("\u00A7e" + CultivationTextStyler.styleDisplayName(entry.getDisplayName())), tooltipWidth));
        tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
        for (String line : wrapText(entry.getDescription(), 60)) {
            // Highlight glossary keywords in the description
            String styledLine = styleWithKeywordHighlight(CultivationTextStyler.style(line));
            tooltip.addAll(this.font.split(Component.literal("\u00A77" + styledLine), tooltipWidth));
        }
        String largerEffect = entry.getLargerEffect();
        if (largerEffect != null && !largerEffect.isEmpty() &&
                !largerEffect.equals("No effect (boolean)") && !largerEffect.equals("Increase the value")) {
            tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7a\u2191 Larger: " + entry.getLargerEffect()), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7c\u2193 Smaller: " + entry.getSmallerEffect()), tooltipWidth));
        }

        // Show current, default, and max values for numeric configs with color coding
        String type = ConfigValueAccessor.getType(entry.configPath);
        if (!type.equals("boolean") && !type.equals("unknown") && !type.equals("string")) {
            String currentVal = ConfigValueAccessor.getValueString(entry.configPath);
            String defaultVal = ConfigValueAccessor.getDefaultValueString(entry.configPath);
            String minVal = ConfigValueAccessor.getMinValueString(entry.configPath);
            String maxVal = ConfigValueAccessor.getMaxValueString(entry.configPath);
            tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));

            // Color-code current value: green (in range), yellow (near max), red (over max).
            // Numeric comparisons below always use the raw tick values, never the named
            // time-of-day label - only what's actually DISPLAYED changes for the six
            // Sects->Schedule time fields.
            boolean overMax = false;
            boolean atMax = false;
            if (maxVal != null && !maxVal.isEmpty()) {
                try {
                    double current = Double.parseDouble(currentVal.trim());
                    double max = Double.parseDouble(maxVal.trim());
                    if (current > max) overMax = true;
                    else if (current == max) atMax = true;
                } catch (NumberFormatException e) { /* ignore */ }
            }

            // For the six Sects->Schedule time-of-day fields, show a named time
            // ("Dusk (12000)") instead of the raw tick count everywhere below.
            boolean isSectTime = isSectScheduleTimeField(entry.configPath);
            String currentDisp = isSectTime ? sectScheduleTimeLabel(currentVal) : currentVal;
            String defaultDisp = isSectTime ? sectScheduleTimeLabel(defaultVal) : defaultVal;
            String minDisp = (isSectTime && minVal != null && !minVal.isEmpty()) ? sectScheduleTimeLabel(minVal) : minVal;
            String maxDisp = (isSectTime && maxVal != null && !maxVal.isEmpty()) ? sectScheduleTimeLabel(maxVal) : maxVal;

            String currentColor = overMax ? "\u00A7c" : (atMax ? "\u00A7a" : "\u00A7a");
            String currentLabel = atMax ? currentColor + "Current: \u00A7f" + currentDisp + " \u00A7a[MAX]"
                                        : currentColor + "Current: \u00A7f" + currentDisp;
            tooltip.addAll(this.font.split(Component.literal(currentLabel), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7bDefault: \u00A7f" + defaultDisp), tooltipWidth));
            if (maxVal != null && !maxVal.isEmpty() && !isSectTime) {
                tooltip.addAll(this.font.split(Component.literal("\u00A7bMax: \u00A7f" + maxDisp), tooltipWidth));
            }
            if (minVal != null && !minVal.isEmpty() && maxVal != null && !maxVal.isEmpty()) {
                if (isSectTime) {
                    tooltip.addAll(this.font.split(Component.literal("\u00A7bRange: \u00A7f" + minDisp + " ~ " + maxDisp + " \u00A77(wraps around the sect day)"), tooltipWidth));
                } else {
                    tooltip.addAll(this.font.split(Component.literal("\u00A7bRange: \u00A7f" + minDisp + " ~ " + maxDisp), tooltipWidth));
                }
            }

            // Over-max warning
            if (overMax) {
                tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
                String warning = getOverMaxWarning(entry.configPath);
                tooltip.addAll(this.font.split(Component.literal("\u00A7c\u00A7l\u26A0 " + warning), tooltipWidth));
            }
        } else if (type.equals("boolean")) {
            String currentVal = ConfigValueAccessor.getValueString(entry.configPath);
            String defaultVal = ConfigValueAccessor.getDefaultValueString(entry.configPath);
            tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7aCurrent: \u00A7f" + currentVal), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7bDefault: \u00A7f" + defaultVal), tooltipWidth));
        }

        // Value context preview - show what the value means in context
        String contextPreview = getValueContextPreview(entry.configPath, type);
        if (contextPreview != null && !contextPreview.isEmpty()) {
            tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
            tooltip.addAll(this.font.split(Component.literal("\u00A7d\u00A7l\u25C6 Context: \u00A7f" + contextPreview), tooltipWidth));
        }

        // Show config path for debugging
        tooltip.addAll(this.font.split(Component.literal(""), tooltipWidth));
        tooltip.addAll(this.font.split(Component.literal("\u00A78Path: " + entry.configPath), tooltipWidth));

        // Reverted back to vanilla GuiGraphics.renderTooltip(): this was the tooltip that
        // was already working correctly (renders on top of everything, opaque, no
        // bleed-through) before tonight's edits. The custom immediate-mode box that
        // briefly replaced this call made things worse, not better - drawing this box
        // mid-loop (while iterating entry rows) meant any row BELOW the hovered one that
        // still had to be drawn this frame painted its own background/text right over
        // the box afterward, since immediate-mode draw order is just source-code order.
        // Vanilla's tooltip render doesn't have that problem (it's genuinely drawn after
        // everything else), so it's the right tool for this specific tooltip - the
        // bleed-through the user actually reported turned out to be the separate keyword
        // popup below, not this one, so that's what got fixed/removed instead.
        g.renderTooltip(this.font, tooltip, x, y);

        // Keyword glossary hover-popup: removed per explicit request (it showed up as a
        // second box overlapping the one above, which read as clutter, not a feature).
        // hoveredKeyword/pendingKeywordPopup are always cleared now instead of ever being
        // set, which keeps this permanently inert without having to touch
        // renderKeywordPopup(), TooltipGlossary, or the mouseClicked() pin handling -
        // they're just unused dead code now, left in place in case a simpler standalone
        // version of word-definition popups is wanted later.
        hoveredKeyword = null;
        keywordHoverStart = 0;
        pendingKeywordPopup = false;
    }

    /** Style text with glossary keywords highlighted (underlined + colored). */
    private String styleWithKeywordHighlight(String text) {
        if (text == null || text.isEmpty()) return text;
        String result = text;
        // Sort keywords by length descending so longer keywords are replaced first
        java.util.List<String> sortedKeywords = new java.util.ArrayList<>(TooltipGlossary.getAllKeywords());
        sortedKeywords.sort((a, b) -> b.length() - a.length());
        for (String keyword : sortedKeywords) {
            if (result.toLowerCase().contains(keyword.toLowerCase())) {
                // Add underline + cyan color formatting around the keyword
                // \u00A7b = cyan, \u00A7n = underline, \u00A7r = reset
                String replacement = "\u00A7b\u00A7n" + keyword + "\u00A7r\u00A77";
                result = result.replaceAll("(?i)" + java.util.regex.Pattern.quote(keyword), replacement);
            }
        }
        return result;
    }

    /**
     * Render the keyword definition popup (appears after 3s hover, pins on click).
     * anchorX/Y/W/H describe the entry-detail tooltip box already on screen (see
     * renderEntryTooltip) - this popup is positioned clear of that box (below it,
     * or beside it if there's no room below) instead of at the same mouse-relative
     * offset, so the two popups never render stacked directly on top of each other.
     */
    private void renderKeywordPopup(GuiGraphics g, int anchorX, int anchorY, int anchorW, int anchorH) {
        String keyword = keywordPopupPinned ? pinnedKeyword : hoveredKeyword;
        if (keyword == null) return;

        long elapsed = System.currentTimeMillis() - keywordHoverStart;
        if (!keywordPopupPinned && elapsed < KEYWORD_HOVER_DELAY_MS) return;

        String definition = TooltipGlossary.getDefinition(keyword);
        if (definition == null) return;

        // Calculate popup dimensions
        int popupW = 250;
        java.util.List<String> wrappedDef = wrapText(definition, 40);
        int popupH = 20 + wrappedDef.size() * 12 + 8;

        // Prefer directly below the entry-detail tooltip (same left edge, small gap) so the
        // two boxes read as a clear top-to-bottom stack. If there isn't room below, place it
        // beside the tooltip instead - to the right if that fits, otherwise to the left.
        int px = anchorX;
        int py = anchorY + anchorH + 6;
        if (py + popupH > this.height - 4) {
            py = anchorY;
            px = anchorX + anchorW + 6;
            if (px + popupW > this.width - 4) {
                px = anchorX - popupW - 6;
            }
        }
        // Final clamp so the popup always stays fully on screen
        if (px < 4) px = 4;
        if (px + popupW > this.width - 4) px = this.width - popupW - 4;
        if (py < 4) py = 4;
        if (py + popupH > this.height - 4) py = this.height - popupH - 4;

        // Draw popup background
        g.fill(px, py, px + popupW, py + popupH, TooltipGlossary.POPUP_BG_COLOR);
        g.renderOutline(px, py, popupW, popupH, TooltipGlossary.POPUP_BORDER_COLOR);

        // Draw keyword title
        g.drawString(this.font, "\u00A7b\u00A7n" + keyword, px + 6, py + 6, TooltipGlossary.POPUP_TITLE_COLOR);

        // Draw definition text
        int textY = py + 20;
        for (String line : wrappedDef) {
            g.drawString(this.font, "\u00A77" + line, px + 6, textY, TooltipGlossary.POPUP_TEXT_COLOR);
            textY += 12;
        }

        // Draw hint at bottom
        if (keywordPopupPinned) {
            g.drawString(this.font, "\u00A78[Click to close]", px + 6, py + popupH - 12, 0xFF606060);
        } else {
            g.drawString(this.font, "\u00A78[Click to pin]", px + 6, py + popupH - 12, 0xFF606060);
        }
    }

    /** Calculate optimal tooltip width based on entry content (auto-resize). */
    private int getAutoTooltipWidth(DisplayEntry entry) {
        int maxWidth = 200; // minimum width
        // Check display name width
        maxWidth = Math.max(maxWidth, this.font.width(entry.getDisplayName()) + 20);
        // Check description lines
        for (String line : wrapText(entry.getDescription(), 60)) {
            maxWidth = Math.max(maxWidth, this.font.width(line) + 20);
        }
        // Check larger/smaller effect
        String largerEffect = entry.getLargerEffect();
        if (largerEffect != null && !largerEffect.isEmpty()) {
            maxWidth = Math.max(maxWidth, this.font.width("\u2191 Larger: " + largerEffect) + 20);
        }
        // Check config path
        maxWidth = Math.max(maxWidth, this.font.width("Path: " + entry.configPath) + 20);
        // Cap at 400 to prevent overly wide tooltips
        return Math.min(maxWidth, 400);
    }

    /** Get a value context preview - explains what the current value means in practical terms. */
    private String getValueContextPreview(String configPath, String type) {
        if (type.equals("boolean")) {
            String val = ConfigValueAccessor.getValueString(configPath);
            if (val.equals("true")) return "Enabled - this feature is active";
            return "Disabled - this feature is inactive";
        }
        if (type.equals("int") || type.equals("long") || type.equals("double")) {
            String valStr = ConfigValueAccessor.getValueString(configPath);
            String lower = configPath.toLowerCase();
            try {
                double val = Double.parseDouble(valStr.trim());
                // Provide context based on config path keywords.
                //
                // Multiplier fields are checked FIRST, before the more specific unit
                // checks below - a path like "npcHpMultiplier" or "attackSpeedMult"
                // also contains "hp"/"damage"/"speed", so without this ordering those
                // more specific (and wrong, for a multiplier) branches would fire
                // instead, producing nonsense like "Entity has 1.0 HP" for a field that
                // actually multiplies whatever the entity's real HP already is.
                if (lower.contains("mult")) {
                    if (lower.contains("hp") || lower.contains("health")) {
                        return "Base entity HP (~20) \u00D7 " + val + " = ~" + Math.round(20 * val) + " HP";
                    }
                    if (lower.contains("damage") || lower.contains("attack")) {
                        return "Multiplies attack/damage output by " + val + "x";
                    }
                    if (lower.contains("speed")) {
                        return "Multiplies movement/attack speed by " + val + "x";
                    }
                    if (lower.contains("qi") || lower.contains("mana")) {
                        return "Multiplies qi cost/gain by " + val + "x";
                    }
                    return "Multiplies base value by " + val + "x";
                }
                if (lower.contains("damage") || lower.contains("attack")) {
                    return "Each hit deals " + val + " base damage";
                }
                if (lower.contains("hp") || lower.contains("health")) {
                    return "Entity has " + val + " HP";
                }
                if (lower.contains("qi") || lower.contains("mana")) {
                    return val + " qi energy available";
                }
                if (lower.contains("speed")) {
                    return "Movement at " + val + "x speed";
                }
                if (lower.contains("chance") || lower.contains("probability")) {
                    return val + "% chance to trigger";
                }
                if (lower.contains("cooldown")) {
                    return "Triggers every " + val + " ticks (" + (val / 20.0) + "s)";
                }
                if (lower.contains("duration")) {
                    return "Lasts " + val + " ticks (" + (val / 20.0) + "s)";
                }
                if (lower.contains("range") || lower.contains("radius")) {
                    return "Affects " + val + " block radius";
                }
                if (lower.contains("lifespan") || lower.contains("age")) {
                    return "Lives for " + val + " days";
                }
                if (lower.contains("cost")) {
                    return "Costs " + val + " to use";
                }
                if (lower.contains("count") || lower.contains("amount")) {
                    return "Quantity: " + val;
                }
            } catch (NumberFormatException e) { /* ignore */ }
        }
        if (type.equals("string")) {
            String val = ConfigValueAccessor.getValueString(configPath);
            if (val == null || val.isEmpty()) return "Empty - no value set";
            if (val.length() > 50) return "Long text (" + val.length() + " chars)";
            return "Value: " + val;
        }
        return null;
    }

    /** Get a type-specific warning message for when a config value exceeds its max. */
    private String getOverMaxWarning(String configPath) {
        String lower = configPath.toLowerCase();
        if (lower.contains("spawn") || lower.contains("chance")) {
            return "Exceeding max may cause excessive spawning and lag!";
        }
        if (lower.contains("column") || lower.contains("row") || lower.contains("grid") || lower.contains("size")) {
            return "Exceeding max may cause UI overflow or crashes!";
        }
        if (lower.contains("damage") || lower.contains("attack") || lower.contains("power")) {
            return "Exceeding max may cause calculation errors or display glitches!";
        }
        if (lower.contains("qi") || lower.contains("mana")) {
            return "Exceeding max may cause energy system instability!";
        }
        if (lower.contains("speed") || lower.contains("tick") || lower.contains("interval")) {
            return "Exceeding max may cause timing issues or instability!";
        }
        return "Exceeding max may cause instability or unexpected behavior!";
    }

    private void renderTabTooltips(GuiGraphics g, int mouseX, int mouseY) {
        String currentHoverKey = null;
        String currentHoverTitle = null;
        List<FormattedCharSequence> currentHoverLines = null;

        // Check if hovering over a top-level tab (use tracked rects).
        // topTabRects is built from CustomTabManager.getTabOrder(), so the names must
        // come from the same list - reading TAB_TO_SUBTABS.keySet() here gave a
        // DIFFERENT order (and threw IndexOutOfBounds once a custom tab existed,
        // which aborted the whole render pass mid-frame).
        List<String> tooltipTabNames = new ArrayList<>(CustomTabManager.getTabOrder());
        tooltipTabNames.removeIf(name -> !TAB_TO_SUBTABS.containsKey(name) && !CustomTabManager.isCustomTab(name));
        for (int i = 0; i < topTabRects.size() && i < tooltipTabNames.size(); i++) {
            int[] rect = topTabRects.get(i);
            String tab = tooltipTabNames.get(i);
            if (mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3]) {
                String desc = ConfigDescriptionRegistry.TAB_DESCRIPTIONS.get(tab);
                if (desc != null) {
                    currentHoverKey = "tab:" + tab;
                    currentHoverTitle = tab;
                    currentHoverLines = new ArrayList<>();
                    currentHoverLines.addAll(this.font.split(Component.literal("\u00A7e\u00A7l" + tab), 300));
                    for (String line : desc.split("\\\\n")) {
                        currentHoverLines.addAll(this.font.split(Component.literal("\u00A77" + line.strip()), 300));
                    }
                }
                break;
            }
        }

        // Check sub-tabs (use tracked rects)
        if (currentHoverKey == null) {
            List<String> subTabs = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
            for (int i = 0; i < subTabRects.size() && i < subTabs.size(); i++) {
                int[] rect = subTabRects.get(i);
                String sub = subTabs.get(i);
                if (mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3]) {
                    String key = activeTopTab + "." + sub;
                    String desc = ConfigDescriptionRegistry.SUBTAB_DESCRIPTIONS.get(key);
                    if (desc != null) {
                        currentHoverKey = "subtab:" + key;
                        currentHoverTitle = sub;
                        currentHoverLines = new ArrayList<>();
                        currentHoverLines.addAll(this.font.split(Component.literal("\u00A7b" + sub), 300));
                        for (String line : desc.split("\\\\n")) {
                            currentHoverLines.addAll(this.font.split(Component.literal("\u00A77" + line.strip()), 300));
                        }
                    }
                    break;
                }
            }
        }

        // Check group buttons (sub-categories). This used to run a THIRD copy of the
        // group-bar wrap loop - and it wrapped to gx = 24 while render() and
        // mouseClicked() both wrapped to gx = 8, so on any sub-tab whose group bar
        // wrapped, the tooltip belonged to a different button than the one drawn
        // under the cursor. It now reads the one shared groupBar.
        if (currentHoverKey == null && !searchMode && groupBar != null) {
            int hitIdx = groupBar.indexAt(mouseX, mouseY);
            if (hitIdx == 0) {
                currentHoverKey = "group:All";
                currentHoverTitle = GROUP_ALL_LABEL;
                currentHoverLines = new ArrayList<>();
                currentHoverLines.addAll(this.font.split(Component.literal("\u00A7c\u00A7l" + GROUP_ALL_LABEL), 300));
                currentHoverLines.addAll(this.font.split(Component.literal("\u00A77Show all config entries in this sub-tab."), 300));
            } else if (hitIdx > 0) {
                String grp = groupBar.labels.get(hitIdx);
                currentHoverKey = "group:" + grp;
                currentHoverTitle = grp;
                currentHoverLines = new ArrayList<>();
                currentHoverLines.addAll(this.font.split(Component.literal("\u00A7c\u00A7l" + grp), 300));
                String grpDesc = getGroupDescription(grp);
                for (String line : grpDesc.split("\\n")) {
                    currentHoverLines.addAll(this.font.split(Component.literal("\u00A77" + line.strip()), 300));
                }
            }
        }

        // ── Track hover time for pinning ──
        if (currentHoverKey != null) {
            if (currentHoverKey.equals(hoverTooltipKey)) {
                // Still hovering the same item — check if we should pin
                long elapsed = System.currentTimeMillis() - hoverTooltipStartTime;
                // Check if this title is already pinned
                boolean alreadyPinned = false;
                for (PinnedTooltip pt : pinnedTooltips) {
                    if (pt.title.equals(currentHoverTitle)) { alreadyPinned = true; break; }
                }
                if (elapsed >= TOOLTIP_PIN_DELAY && !alreadyPinned) {
                    // Use remembered position/shape if available, else default
                    int pinW = 260, pinH = 120, pinX, pinY;
                    int[] memory = tooltipMemory.get(currentHoverTitle);
                    if (memory != null && memory.length == 4) {
                        pinX = memory[0]; pinY = memory[1]; pinW = memory[2]; pinH = memory[3];
                    } else {
                        pinX = Math.min(mouseX + 12, this.width - pinW - 4);
                        pinY = Math.min(mouseY + 12, this.height - pinH - 4);
                        if (pinX < 4) pinX = 4;
                        if (pinY < 4) pinY = 4;
                    }
                    pinnedTooltips.add(new PinnedTooltip(currentHoverTitle, currentHoverLines, pinX, pinY, pinW, pinH));
                }
            } else {
                // New hover target — reset timer
                hoverTooltipKey = currentHoverKey;
                hoverTooltipStartTime = System.currentTimeMillis();
            }

            // Check if this title is already pinned
            boolean alreadyPinned = false;
            for (PinnedTooltip pt : pinnedTooltips) {
                if (pt.title.equals(currentHoverTitle)) { alreadyPinned = true; break; }
            }

            // Show transient tooltip only if not already pinned for this item
            if (!alreadyPinned) {
                // Show a SMALL truncated tooltip - only first 5 lines + "..." if more
                List<FormattedCharSequence> truncatedLines = new ArrayList<>();
                int maxPreviewLines = 5;
                for (int i = 0; i < Math.min(maxPreviewLines, currentHoverLines.size()); i++) {
                    truncatedLines.add(currentHoverLines.get(i));
                }
                if (currentHoverLines.size() > maxPreviewLines) {
                    truncatedLines.add(this.font.split(Component.literal("\u00A77\u00A7o... (hover 3s to expand)"), 300).get(0));
                }
                // Calculate tooltip size
                int ttW = 0;
                int ttH = 0;
                for (FormattedCharSequence line : truncatedLines) {
                    ttW = Math.max(ttW, this.font.width(line));
                    ttH += 12;
                }
                ttW += 16;
                ttH += 8;
                int ttX = mouseX + 12;
                int ttY = mouseY - 12;
                if (ttX + ttW > this.width - 4) ttX = this.width - ttW - 4;
                if (ttY + ttH > this.height - 4) ttY = this.height - ttH - 4;
                if (ttX < 4) ttX = 4;
                if (ttY < 4) ttY = 4;

                // Render at higher Z-level so nothing bleeds through
                // Z=1000 ensures transient tooltip is above ALL pinned tooltips (500+i*10)
                PoseStack pose = g.pose();
                pose.pushPose();
                pose.translate(0, 0, 1000); // Above all pinned tooltips

                // Draw opaque background
                g.fill(ttX, ttY, ttX + ttW, ttY + ttH, 0xFF100018);
                g.renderOutline(ttX, ttY, ttW, ttH, 0xFF8060A0);
                g.renderOutline(ttX + 1, ttY + 1, ttW - 2, ttH - 2, 0xFF403060);

                // Draw tooltip text
                int lineY = ttY + 6;
                for (FormattedCharSequence line : truncatedLines) {
                    g.drawString(this.font, line, ttX + 8, lineY, 0xFFFFFF);
                    lineY += 12;
                }

                // Animated glow border timer: shows progress toward pinning
                long elapsed = System.currentTimeMillis() - hoverTooltipStartTime;
                if (elapsed < TOOLTIP_PIN_DELAY) {
                    float progress = (float) elapsed / TOOLTIP_PIN_DELAY;
                    drawAnimatedGlowBorder(g, ttX, ttY, ttW, ttH, progress);
                }

                pose.popPose();
            }
        } else {
            hoverTooltipKey = null;
        }

        // NOTE: Pinned tooltips are rendered separately at the end of render() for proper z-order
    }

    /**
     * Draw an animated glowing border that travels around the tooltip outline.
     * Starts at top-left corner and goes clockwise. When it completes the loop
     * (progress = 1.0), the tooltip solidifies into a movable window.
     */
    private void drawAnimatedGlowBorder(GuiGraphics g, int x, int y, int w, int h, float progress) {
        int perimeter = 2 * (w + h);
        int traveled = (int) (perimeter * progress);
        int glowColor = 0xFF40E0FF;
        int glowAlpha = 0xFF;

        // Draw segments along the perimeter up to 'traveled' pixels
        // Top edge: left to right (0 to w)
        // Right edge: top to bottom (w to w+h)
        // Bottom edge: right to left (w+h to 2w+h)
        // Left edge: bottom to top (2w+h to 2w+2h)
        int drawn = 0;
        // Top edge
        int topLen = w;
        if (drawn < traveled) {
            int segLen = Math.min(topLen, traveled - drawn);
            g.fill(x, y - 1, x + segLen, y + 1, (glowAlpha << 24) | glowColor);
            drawn += segLen;
        }
        // Right edge
        if (drawn < traveled) {
            int segLen = Math.min(h, traveled - drawn);
            g.fill(x + w - 1, y, x + w + 1, y + segLen, (glowAlpha << 24) | glowColor);
            drawn += segLen;
        }
        // Bottom edge (right to left)
        if (drawn < traveled) {
            int segLen = Math.min(w, traveled - drawn);
            g.fill(x + w - segLen, y + h - 1, x + w, y + h + 1, (glowAlpha << 24) | glowColor);
            drawn += segLen;
        }
        // Left edge (bottom to top)
        if (drawn < traveled) {
            int segLen = Math.min(h, traveled - drawn);
            g.fill(x - 1, y + h - segLen, x + 1, y + h, (glowAlpha << 24) | glowColor);
            drawn += segLen;
        }
    }

    /** Render all pinned tooltips at a high Z-level so nothing bleeds through. */
    private void renderAllPinnedTooltips(GuiGraphics g, int mouseX, int mouseY) {
        if (pinnedTooltips.isEmpty()) return;
        PoseStack pose = g.pose();
        // Render each tooltip at an incremental Z-level so they don't bleed through each other.
        // First tooltip in list = lowest Z, last (focused) = highest Z (on top).
        for (int i = 0; i < pinnedTooltips.size(); i++) {
            pose.pushPose();
            pose.translate(0, 0, 500 + i * 10); // Each window 10 Z-levels above the previous
            renderPinnedTooltip(g, pinnedTooltips.get(i), mouseX, mouseY);
            pose.popPose();
        }
    }

    /** Render a pinned/draggable tooltip window with a close button, resize handle, and scroll support. */
    private void renderPinnedTooltip(GuiGraphics g, PinnedTooltip pt, int mouseX, int mouseY) {
        int winW = pt.width;
        int winH = pt.height;
        int lineH = 11;

        // Ensure it fits on screen
        if (pt.x + winW > this.width) pt.x = this.width - winW - 4;
        if (pt.y + winH > this.height) pt.y = this.height - winH - 4;
        if (pt.x < 0) pt.x = 4;
        if (pt.y < 0) pt.y = 4;

        // Window background — fully opaque with border
        g.fill(pt.x, pt.y, pt.x + winW, pt.y + winH, 0xFF101018);
        g.renderOutline(pt.x, pt.y, winW, winH, 0xFFA0A060);
        g.renderOutline(pt.x + 1, pt.y + 1, winW - 2, winH - 2, 0xFF606040);

        // Title bar (draggable area)
        int titleBarH = 16;
        boolean titleHover = mouseX >= pt.x && mouseX < pt.x + winW - 16 && mouseY >= pt.y && mouseY < pt.y + titleBarH;
        g.fill(pt.x, pt.y, pt.x + winW, pt.y + titleBarH, titleHover ? 0xFF404060 : 0xFF202030);
        g.renderOutline(pt.x, pt.y, winW, titleBarH, 0xFFA0A060);
        // Truncate title if too long
        String titleStr = pt.title;
        int maxTitleW = winW - 24;
        while (this.font.width("\u00A7e\u00A7l" + titleStr) > maxTitleW && titleStr.length() > 3) {
            titleStr = titleStr.substring(0, titleStr.length() - 1);
        }
        if (!titleStr.equals(pt.title)) titleStr += "\u2026";
        g.drawString(this.font, "\u00A7e\u00A7l" + titleStr, pt.x + 4, pt.y + 4, 0xFFFFFF);

        // Close button [X]
        int closeX = pt.x + winW - 14;
        int closeY = pt.y + 2;
        int closeW = 12;
        int closeH = 12;
        boolean closeHover = mouseX >= closeX && mouseX < closeX + closeW && mouseY >= closeY && mouseY < closeY + closeH;
        g.fill(closeX, closeY, closeX + closeW, closeY + closeH, closeHover ? 0xFF602020 : 0xFF302010);
        g.renderOutline(closeX, closeY, closeW, closeH, closeHover ? 0xFFFF4040 : 0xFF804040);
        g.drawCenteredString(this.font, "\u00A7c\u00A7lX", closeX + closeW / 2, closeY + 2, 0xFFFFFF);

        // Content area — clip text to window bounds with scroll support
        int contentX = pt.x + 4;
        int contentY = pt.y + titleBarH + 4;
        int contentW = winW - 8;
        int contentH = winH - titleBarH - 8;
        int maxY = pt.y + winH - 4;

        // Calculate total wrapped lines for scroll
        List<String> allWrappedLines = new ArrayList<>();
        for (FormattedCharSequence line : pt.lines) {
            StringBuilder sb = new StringBuilder();
            line.accept((index, style, codePoint) -> { sb.appendCodePoint(codePoint); return true; });
            String lineStr = sb.toString();
            if (this.font.width(lineStr) > contentW) {
                // Wrap long lines
                String[] words = lineStr.split(" ");
                StringBuilder wrapped = new StringBuilder();
                for (String word : words) {
                    String test = wrapped.isEmpty() ? word : wrapped + " " + word;
                    if (this.font.width(test) > contentW && !wrapped.isEmpty()) {
                        allWrappedLines.add(wrapped.toString());
                        wrapped = new StringBuilder(word);
                    } else {
                        wrapped = new StringBuilder(test);
                    }
                }
                if (!wrapped.isEmpty()) allWrappedLines.add(wrapped.toString());
            } else {
                allWrappedLines.add(lineStr);
            }
        }

        // Clamp scroll offset
        int visibleLines = contentH / lineH;
        int maxScroll = Math.max(0, allWrappedLines.size() - visibleLines);
        pt.scrollOffset = Math.max(0, Math.min(pt.scrollOffset, maxScroll));

        // Enable scissor clipping to prevent text overflow
        g.enableScissor(pt.x + 2, contentY - 2, pt.x + winW - 2, maxY);

        int ly = contentY;
        int startLine = pt.scrollOffset;
        for (int i = startLine; i < allWrappedLines.size() && ly < maxY; i++) {
            g.drawString(this.font, allWrappedLines.get(i), contentX, ly, 0xFFFFFF);
            ly += lineH;
        }

        g.disableScissor();

        // Scroll bar (if content overflows)
        if (allWrappedLines.size() > visibleLines) {
            int scrollBarX = pt.x + winW - 6;
            int scrollBarY = contentY;
            int scrollBarH = contentH;
            int scrollBarW = 4;
            g.fill(scrollBarX, scrollBarY, scrollBarX + scrollBarW, scrollBarY + scrollBarH, 0xFF202020);
            int thumbH = Math.max(10, scrollBarH * visibleLines / allWrappedLines.size());
            int thumbY = scrollBarY + (scrollBarH - thumbH) * pt.scrollOffset / Math.max(1, maxScroll);
            g.fill(scrollBarX, thumbY, scrollBarX + scrollBarW, thumbY + thumbH, 0xFF6060A0);
        }

        // Resize handle (bottom-right corner)
        int rhSize = 8;
        int rhX = pt.x + winW - rhSize - 1;
        int rhY = pt.y + winH - rhSize - 1;
        boolean rhHover = mouseX >= rhX && mouseX < rhX + rhSize && mouseY >= rhY && mouseY < rhY + rhSize;
        g.fill(rhX, rhY, rhX + rhSize, rhY + rhSize, rhHover ? 0xFF6060A0 : 0xFF303060);
        g.renderOutline(rhX, rhY, rhSize, rhSize, rhHover ? 0xFFA0A0FF : 0xFF6060A0);
        // Draw diagonal lines for resize indicator
        g.fill(rhX + 2, rhY + 5, rhX + 3, rhY + 6, 0xFFA0A0C0);
        g.fill(rhX + 4, rhY + 3, rhX + 5, rhY + 4, 0xFFA0A0C0);
        g.fill(rhX + 4, rhY + 6, rhX + 5, rhY + 7, 0xFFA0A0C0);
    }

    /** Get a description for a group/sub-category name. */
    private String getGroupDescription(String grp) {
        String key = activeTopTab + "." + activeSubTab + "." + grp;
        String desc = ConfigDescriptionRegistry.SUBTAB_DESCRIPTIONS.get(key);
        if (desc != null) return desc.replace("\\n", "\n");
        // Auto-generate a basic description
        return "Config entries in the " + grp + " group under " + activeSubTab + ".";
    }

    /** Handle mouse clicks for pinned tooltips (drag + close + resize). Returns true if handled. */
    private boolean handlePinnedTooltipClick(double mouseX, double mouseY, int button) {
        // Iterate in reverse so topmost (last rendered) gets priority
        for (int idx = pinnedTooltips.size() - 1; idx >= 0; idx--) {
            PinnedTooltip pt = pinnedTooltips.get(idx);
            int winW = pt.width;
            int winH = pt.height;

            // Check close button
            int closeX = pt.x + winW - 14;
            int closeY = pt.y + 2;
            int closeW = 12;
            int closeH = 12;
            if (mouseX >= closeX && mouseX < closeX + closeW && mouseY >= closeY && mouseY < closeY + closeH) {
                // Save position/shape to memory before removing
                tooltipMemory.put(pt.title, new int[]{pt.x, pt.y, pt.width, pt.height});
                saveTooltipMemory();
                pinnedTooltips.remove(idx);
                return true;
            }

            // Check resize handle (bottom-right corner)
            int rhSize = 8;
            int rhX = pt.x + winW - rhSize - 1;
            int rhY = pt.y + winH - rhSize - 1;
            if (mouseX >= rhX && mouseX < rhX + rhSize && mouseY >= rhY && mouseY < rhY + rhSize) {
                pt.resizing = true;
                // Bring to front (move to end of list so it renders on top)
                if (idx != pinnedTooltips.size() - 1) {
                    pinnedTooltips.remove(idx);
                    pinnedTooltips.add(pt);
                }
                return true;
            }

            // Check title bar for dragging
            int titleBarH = 16;
            if (mouseX >= pt.x && mouseX < pt.x + winW - 16 && mouseY >= pt.y && mouseY < pt.y + titleBarH) {
                pt.dragging = true;
                pt.dragOffsetX = (int) mouseX - pt.x;
                pt.dragOffsetY = (int) mouseY - pt.y;
                // Bring to front (move to end of list so it renders on top)
                if (idx != pinnedTooltips.size() - 1) {
                    pinnedTooltips.remove(idx);
                    pinnedTooltips.add(pt);
                }
                return true;
            }

            // Check if click is anywhere inside the tooltip body (bring to front on focus)
            if (mouseX >= pt.x && mouseX < pt.x + winW && mouseY >= pt.y && mouseY < pt.y + winH) {
                // Click inside tooltip but not on a control — bring to front
                if (idx != pinnedTooltips.size() - 1) {
                    pinnedTooltips.remove(idx);
                    pinnedTooltips.add(pt);
                }
                return true;
            }
        }
        return false;
    }

    /** Handle mouse drag for pinned tooltips. */
    private void handlePinnedTooltipDrag(double mouseX, double mouseY) {
        for (PinnedTooltip pt : pinnedTooltips) {
            if (pt.dragging) {
                pt.x = (int) mouseX - pt.dragOffsetX;
                pt.y = (int) mouseY - pt.dragOffsetY;
            }
            if (pt.resizing) {
                int newW = (int) mouseX - pt.x + 4;
                int newH = (int) mouseY - pt.y + 4;
                pt.width = Math.max(120, Math.min(newW, this.width - pt.x - 4));
                pt.height = Math.max(60, Math.min(newH, this.height - pt.y - 4));
            }
        }
    }

    /** Handle mouse release for pinned tooltips. */
    private void handlePinnedTooltipRelease() {
        boolean anyChanged = false;
        for (PinnedTooltip pt : pinnedTooltips) {
            if (pt.dragging || pt.resizing) {
                // Save position/shape to memory when drag/resize ends
                tooltipMemory.put(pt.title, new int[]{pt.x, pt.y, pt.width, pt.height});
                anyChanged = true;
            }
            pt.dragging = false;
            pt.resizing = false;
        }
        if (anyChanged) saveTooltipMemory();
    }

    private List<String> wrapText(String text, int maxChars) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            if (current.length() + word.length() + 1 > maxChars) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (current.length() > 0) current.append(" ");
                current.append(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    // ── Inline text editing for direct value entry ──
    private EditBox editField;
    private String editingKey;

    private void startEditingEntry(String configPath, int x, int y, int w, int h) {
        if (editField != null) {
            editField.setFocused(false);
            this.children().remove(editField);
        }
        editingKey = configPath;
        String currentVal = ConfigValueAccessor.getValueString(configPath);
        editField = new EditBox(this.font, x, y, w, h, Component.literal("Value"));
        editField.setValue(currentVal);
        editField.setFocused(true);
        editField.setResponder(text -> {
            if (editingKey != null && !text.isEmpty()) {
                ConfigValueAccessor.setValueFromString(editingKey, text);
            }
        });
        addWidget(editField);
    }

    /** Cycle a color config to the next preset in the ColorPresets list. */
    private void cycleColorPreset(String configPath) {
        String valStr = ConfigValueAccessor.getValueString(configPath);
        int currentColor;
        try {
            currentColor = Integer.parseInt(valStr.trim());
        } catch (NumberFormatException e) {
            currentColor = -1; // white
        }
        int nextColor = ColorPresets.getNextPreset(currentColor);
        boolean success = ConfigValueAccessor.setValueFromString(configPath, String.valueOf(nextColor));
        System.out.println("[ConfigExt] cycleColor next: path=" + configPath + " current=" + currentColor + " next=" + nextColor + " success=" + success);
    }

    /** Cycle a color config to the previous preset in the ColorPresets list. */
    private void cycleColorPresetReverse(String configPath) {
        String valStr = ConfigValueAccessor.getValueString(configPath);
        int currentColor;
        try {
            currentColor = Integer.parseInt(valStr.trim());
        } catch (NumberFormatException e) {
            currentColor = -1;
        }
        int prevColor = ColorPresets.getPrevPreset(currentColor);
        ConfigValueAccessor.setValueFromString(configPath, String.valueOf(prevColor));
    }

    /** Determine appropriate step size for +/- buttons based on config type and value. */
    /** Check if the C key is currently held down. */
    private boolean isCKeyDown() {
        return net.minecraft.client.Minecraft.getInstance().options.keyInventory.isDown() ||
               org.lwjgl.glfw.GLFW.glfwGetKey(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(),
                   org.lwjgl.glfw.GLFW.GLFW_KEY_C) != 0;
    }

    private double getStepSize(String configPath, String type) {
        // The six Sects->Schedule time-of-day fields step by one whole named
        // slot (4000 ticks = Dawn->Morning->Midday->Dusk->Evening->Midnight)
        // per click, so +/- actually cycles between the named times shown in
        // their display instead of nudging by a single tick.
        if (isSectScheduleTimeField(configPath)) return 4000;
        if (configPath.contains("Color") || configPath.contains("color")) return 1;
        if (configPath.contains("Multiplier") || configPath.contains("mult") || configPath.contains("Mult")) return 0.1;
        if (configPath.contains("Chance") || configPath.contains("chance")) return 0.01;
        if (configPath.contains("Ticks") || configPath.contains("ticks")) return 1;
        if (type.equals("double")) return 0.1;
        return 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Keyword popup pin/unpin on click
        if (button == 0 && (keywordPopupPinned || (hoveredKeyword != null && System.currentTimeMillis() - keywordHoverStart >= KEYWORD_HOVER_DELAY_MS))) {
            if (keywordPopupPinned) {
                // Click anywhere to unpin
                keywordPopupPinned = false;
                pinnedKeyword = null;
                return true;
            } else {
                // Click to pin the current hovered keyword popup
                keywordPopupPinned = true;
                pinnedKeyword = hoveredKeyword;
                return true;
            }
        }

        // Right-click on entry: toggle favorite/lock/reset to default
        if (button == 1) {
            // Hit-test through the SHARED layout - never a re-derived Y.
            int entryWidthR = getEntryWidth();
            LayoutEngine.Layout lyR = layout();
            int hitR = lyR.rowIndexAt(scrollOffset, mouseY, getEntryYStart() - 2, getEntryClipBottom());
            if (hitR >= 0 && mouseX >= 8 && mouseX < entryWidthR) {
                LayoutEngine.Row rowR = lyR.get(hitR);
                if (!rowR.header) {
                    // Shift+right-click = toggle lock, plain right-click = toggle favorite
                    if (hasShiftDown()) {
                        toggleLock(rowR.entry.configPath);
                    } else {
                        toggleFavorite(rowR.entry.configPath);
                    }
                    invalidateLayout();
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        // Pinned tooltip takes priority (close button + dragging)
        if (handlePinnedTooltipClick(mouseX, mouseY, button)) {
            return true;
        }

        // Name input popup takes top priority when open
        if (nameInput.isOpen()) {
            nameInput.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        // Item picker popup takes priority when open
        if (itemPicker.isOpen()) {
            itemPicker.mouseClicked(mouseX, mouseY, button, this.width, this.height);
            return true;
        }

        // Batch 12 popups take priority when open
        if (colorWheel.isOpen()) {
            colorWheel.mouseClicked(mouseX, mouseY, button, this.width, this.height);
            return true;
        }
        if (dropdownEnum.isOpen()) {
            dropdownEnum.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (multiLineEditor.isOpen()) {
            multiLineEditor.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        // Minimap nav panel (bottom-right corner) - consume clicks that land on it so
        // they don't also register as a click on whatever entry row/button is underneath.
        if (minimapNav.isVisible() && minimapNav.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // ── Export/Import/Diff/Checksum buttons ──
        // ── Export/Import/Diff/Checksum/ResetTab/Save buttons at bottom-left ──
        int utilBtnY = this.height - 28;
        int utilBtnH = 12;
        String[] utilLabels = {"Export", "Import", "Diff", "Checksum", "Reset Tab", "Save", "Preset", "SavePreset", "Nav"};
        int utilX = 8;
        for (String label : utilLabels) {
            int utilW = this.font.width(label) + 6;
            if (mouseX >= utilX && mouseX < utilX + utilW && mouseY >= utilBtnY && mouseY < utilBtnY + utilBtnH) {
                switch (label) {
                    case "Nav":
                        minimapNav.setVisible(!minimapNav.isVisible());
                        break;
                    case "Export":
                        String json = ConfigProfileManager.exportToJson();
                        net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(json);
                        break;
                    case "Import":
                        String clip = net.minecraft.client.Minecraft.getInstance().keyboardHandler.getClipboard();
                        ConfigProfileManager.importFromJson(clip);
                        refreshEntries();
                        break;
                    case "Diff":
                        activeFilter = SearchFilter.MODIFIED;
                        searchMode = true;
                        globalSearch = true;
                        searchBox.setValue("modified");
                        refreshEntries();
                        break;
                    case "Checksum":
                        String checksum = ConfigProfileManager.generateChecksum();
                        net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(checksum);
                        break;
                    case "Reset Tab":
                        resetCurrentTabConfigs();
                        break;
                    case "Save":
                        saveAnimStart = System.currentTimeMillis();
                        // Force-save Forge configs to disk
                        try {
                            java.lang.reflect.Field configsField = net.minecraftforge.fml.config.ConfigTracker.class.getDeclaredField("configs");
                            configsField.setAccessible(true);
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, net.minecraftforge.fml.config.ModConfig> configs =
                                (java.util.Map<String, net.minecraftforge.fml.config.ModConfig>) configsField.get(net.minecraftforge.fml.config.ConfigTracker.INSTANCE);
                            for (var cfgEntry : configs.entrySet()) {
                                if (cfgEntry.getKey().startsWith("xiaoxiang_config_ext")) {
                                    cfgEntry.getValue().save();
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                        break;
                    case "Preset":
                        // Show first available preset or info
                        java.util.List<String> presetNames = ModpackPresets.getPresetNames();
                        if (presetNames.isEmpty()) {
                            NotificationSystem.showWarning("No presets saved. Use 'SavePreset' to create one.");
                        } else {
                            // Cycle through presets
                            ModpackPresets.applyPreset(presetNames.get(0));
                        }
                        break;
                    case "SavePreset":
                        // Save current config as a preset with timestamp name
                        String presetName = "Preset_" + System.currentTimeMillis();
                        ModpackPresets.saveCurrentAsPreset(presetName, "Auto-saved preset");
                        NotificationSystem.showSuccess("Saved preset: " + presetName);
                        break;
                }
                return true;
            }
            utilX += utilW + 2;
        }

        // ── Search filter buttons ──
        int searchW = Math.min(220, this.width - 40);
        int filterY = 28;
        int filterX = this.width / 2 - searchW / 2;
        String[] filterNames = {"All", "Modified", "Bool", "Num", "Color", "String", "OverMax", "Fav"};
        SearchFilter[] filterValues = SearchFilter.values();
        for (int i = 0; i < filterNames.length && i < filterValues.length; i++) {
            int fw = this.font.width(filterNames[i]) + 6;
            if (mouseX >= filterX && mouseX < filterX + fw && mouseY >= filterY && mouseY < filterY + 12) {
                activeFilter = filterValues[i];
                refreshEntries();
                return true;
            }
            filterX += fw + 2;
        }
        // Global search toggle
        int gsX = filterX + 8;
        String gsLabel = globalSearch ? "Global" : "Tab";
        int gsW = this.font.width(gsLabel) + 8;
        if (mouseX >= gsX && mouseX < gsX + gsW && mouseY >= filterY && mouseY < filterY + 12) {
            globalSearch = !globalSearch;
            refreshEntries();
            return true;
        }

        // ── Check top-level tab clicks FIRST ──
        List<String> topTabNames = new ArrayList<>(CustomTabManager.getTabOrder());
        // Filter to only tabs that exist in TAB_TO_SUBTABS or are custom tabs
        topTabNames.removeIf(name -> !TAB_TO_SUBTABS.containsKey(name) && !CustomTabManager.isCustomTab(name));
        for (int i = 0; i < topTabRects.size() && i < topTabNames.size(); i++) {
            int[] rect = topTabRects.get(i);
            if (mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3]) {
                String tabName = topTabNames.get(i);
                // Shift+right-click: move tab left (reorder)
                if (button == 1 && hasShiftDown()) {
                    if (i > 0) {
                        CustomTabManager.moveTabUp(tabName);
                        NotificationSystem.showInfo("Moved tab '" + tabName + "' left");
                    }
                    return true;
                }
                // Right-click: move tab right (reorder)
                if (button == 1) {
                    if (i < topTabNames.size() - 1) {
                        CustomTabManager.moveTabDown(tabName);
                        NotificationSystem.showInfo("Moved tab '" + tabName + "' right");
                    }
                    return true;
                }
                activeTopTab = tabName;
                if (CustomTabManager.isCustomTab(tabName)) {
                    // Custom tab: show all entries from its path prefixes
                    activeSubTab = "All";
                } else {
                    List<String> subs = TAB_TO_SUBTABS.get(activeTopTab);
                    if (!subs.isEmpty()) {
                        activeSubTab = subs.get(0);
                    }
                }
                activeGroup = "";
                // Exit search mode but keep the text in the search box
                // Text only clears when the screen is reopened
                searchMode = false;
                scrollOffset = 0;
                rebuildSubTabButtons();
                refreshEntries();
                return true;
            }
        }

        // ── Check sub-tab clicks (custom rendered) ──
        List<String> subTabNames = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
        for (int i = 0; i < subTabRects.size() && i < subTabNames.size(); i++) {
            int[] rect = subTabRects.get(i);
            if (mouseX >= rect[0] && mouseX < rect[0] + rect[2] && mouseY >= rect[1] && mouseY < rect[1] + rect[3]) {
                activeSubTab = subTabNames.get(i);
                activeGroup = "";
                // Exit search mode but keep the text in the search box
                searchMode = false;
                scrollOffset = 0;
                refreshEntries();
                return true;
            }
        }

        // ── Check group button clicks (3rd level nav) ──
        // Reads the same rects render() drew, so a wrapped group button can never
        // be painted in one place and clicked in another.
        if (!searchMode && groupBar != null) {
            int groupHit = groupBar.indexAt(mouseX, mouseY);
            if (groupHit == 0) {
                activeGroup = "";
                refreshEntries();
                return true;
            } else if (groupHit > 0) {
                activeGroup = groupBar.labels.get(groupHit);
                refreshEntries();
                return true;
            }
        }

        // ── Check config entry control buttons ──
        // ONE hit-test against the shared layout decides which visual row the click
        // landed on; the per-control boxes below are then positioned relative to
        // that row's real Y, exactly as render() positioned them.
        int entryYStart = getEntryYStart();
        int entryX = 8;
        int entryWidth = getEntryWidth();
        LayoutEngine.Layout ly = layout();
        int hitRow = ly.rowIndexAt(scrollOffset, mouseY, entryYStart - 2, getEntryClipBottom());

        if (hitRow >= 0) {
            LayoutEngine.Row row = ly.get(hitRow);

            // ── Group header click (collapse/expand) ──
            if (row.header) {
                if (mouseX >= entryX && mouseX < entryWidth) {
                    String key = collapseKey(row.group);
                    if (collapsedSections.contains(key)) {
                        collapsedSections.remove(key);
                    } else {
                        collapsedSections.add(key);
                    }
                    // The row list changes shape when a group collapses, so it must
                    // be rebuilt before the next render/hit-test.
                    invalidateLayout();
                    anim.startTransition();
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }

            DisplayEntry entry = row.entry;
            int i = row.entryIndex;
            int y = row.y - ly.pixelOffset(scrollOffset);
            int rowH = row.height;
            String type = ConfigValueAccessor.getType(entry.configPath);

            // Determine entry category for click handling
            boolean isBoolean = type.equals("boolean");
            boolean isNumeric = !type.equals("boolean") && !type.equals("unknown") && !type.equals("string");
            boolean isColor = isNumeric && (entry.configPath.contains("color") || entry.configPath.contains("Color") ||
                              (entry.configPath.startsWith("client.colors.") && type.equals("int")));
            boolean isItemsConfig = type.equals("string") &&
                    (entry.configPath.contains("startingItems") || entry.configPath.contains(".items") ||
                     entry.configPath.startsWith("identity.custom_") ||
                     entry.configPath.startsWith("identity.custom."));
            boolean isSelectable = isNumeric && !isColor; // Only plain numeric entries are selectable

            // ── Handle control clicks FIRST (before row selection) ──

            if (isBoolean) {
                int btnX = entryWidth - 60;
                int btnY = y + 2;
                int btnW = 50;
                int btnH = 18;
                if (mouseX >= btnX && mouseX < btnX + btnW && mouseY >= btnY && mouseY < btnY + btnH) {
                    ConfigValueAccessor.toggle(entry.configPath);
                    flashValueChange(entry.configPath);
                    return true;
                }
            } else if (isColor) {
                // Color entry: << (prev) + swatch + preset name + >> (next)
                int prevX = entryWidth - 200;
                int prevW = 20;
                int swatchX = entryWidth - 178;
                int swatchY = y + 3;
                int swatchW = 24;
                int swatchH = 16;
                int nameX = swatchX + swatchW + 4;
                int nameW = 86;
                int cycleX = entryWidth - 30;
                int cycleW = 20;

                if (mouseX >= prevX && mouseX < prevX + prevW && mouseY >= swatchY && mouseY < swatchY + swatchH) {
                    cycleColorPresetReverse(entry.configPath);
                    return true;
                }
                if ((mouseX >= swatchX && mouseX < swatchX + swatchW && mouseY >= swatchY && mouseY < swatchY + swatchH) ||
                    (mouseX >= cycleX && mouseX < cycleX + cycleW && mouseY >= swatchY && mouseY < swatchY + swatchH)) {
                    // Right-click on swatch opens color wheel
                    if (button == 1) {
                        try {
                            int currentColor = Integer.parseInt(ConfigValueAccessor.getValueString(entry.configPath).trim());
                            colorWheel.open(entry.configPath, currentColor);
                        } catch (Exception e) {
                            colorWheel.open(entry.configPath, 0xFFFFFFFF);
                        }
                        return true;
                    }
                    cycleColorPreset(entry.configPath);
                    return true;
                }
                if (mouseX >= nameX && mouseX < nameX + nameW && mouseY >= swatchY && mouseY < swatchY + swatchH) {
                    cycleColorPreset(entry.configPath);
                    return true;
                }
            } else if (isItemsConfig) {
                // Item picker button - check BEFORE row selection
                int pickBtnX = entryWidth - 120;
                int pickBtnY = y + 3;
                int pickBtnW = 110;
                int pickBtnH = 16;
                if (mouseX >= pickBtnX && mouseX < pickBtnX + pickBtnW &&
                        mouseY >= pickBtnY && mouseY < pickBtnY + pickBtnH) {
                    String currentVal;
                    if (entry.configPath.startsWith("identity.custom_")) {
                        String customId = entry.configPath.split("\\.")[1];
                        currentVal = CustomIdentityManager.getStartingItems(customId);
                        if (currentVal == null) currentVal = "";
                    } else {
                        currentVal = ConfigValueAccessor.getValueString(entry.configPath);
                    }
                    itemPicker.open(entry.configPath, currentVal);
                    return true;
                }
                // Items config rows are NOT selectable - don't select the row
            } else if (isNumeric) {
                // Regular numeric: - and + buttons, checked BEFORE row selection
                int minusX = entryWidth - 180;
                int plusX = entryWidth - 30;
                int btnY = y + 3;
                int btnH = 16;

                // Check modifier keys for step size (new scheme with Alt 100k)
                boolean shift = hasShiftDown();
                boolean ctrl = hasControlDown();
                boolean alt = hasAltDown();
                boolean cKey = isCKeyDown();
                double stepMult = 1.0;
                if (alt) stepMult = 100000.0;           // Alt = x100000
                else if (ctrl && shift && cKey) stepMult = 1000.0;
                else if (ctrl && cKey) stepMult = 0.01;
                else if (ctrl && shift) stepMult = 10.0;
                else if (shift) stepMult = 100.0;
                else if (ctrl) stepMult = 1.0;

                if (mouseX >= minusX && mouseX < minusX + 20 && mouseY >= btnY && mouseY < btnY + btnH) {
                    double step = getStepSize(entry.configPath, type) * stepMult;
                    ConfigValueAccessor.decrement(entry.configPath, step);
                    flashValueChange(entry.configPath);
                    return true;
                }
                if (mouseX >= plusX && mouseX < plusX + 20 && mouseY >= btnY && mouseY < btnY + btnH) {
                    double step = getStepSize(entry.configPath, type) * stepMult;
                    ConfigValueAccessor.increment(entry.configPath, step);
                    flashValueChange(entry.configPath);
                    return true;
                }

                // Click on value text to edit directly
                int valX = entryWidth - 160;
                if (mouseX >= valX && mouseX < valX + 120 && mouseY >= btnY && mouseY < btnY + btnH) {
                    // Right-click on long string values opens multi-line editor
                    if (button == 1 && type.equals("string")) {
                        String currentVal = ConfigValueAccessor.getValueString(entry.configPath);
                        if (currentVal != null && currentVal.length() > 40) {
                            multiLineEditor.open(entry.configPath, currentVal);
                            multiLineEditor.init(this);
                            return true;
                        }
                    }
                    startEditingEntry(entry.configPath, valX, btnY, 120, btnH);
                    return true;
                }

                // Click on the row (not on buttons) - select for Ctrl+scroll editing
                if (mouseX >= entryX && mouseX < entryWidth && mouseY >= y && mouseY < y + rowH) {
                    selectedEntryIdx = (selectedEntryIdx == i) ? -1 : i;
                    return true;
                }
            } else if (type.equals("string") && !isItemsConfig) {
                // Regular string: - and + buttons (for cycling string values if applicable)
                int minusX = entryWidth - 180;
                int plusX = entryWidth - 30;
                int btnY = y + 3;
                int btnH = 16;
                if (mouseX >= minusX && mouseX < minusX + 20 && mouseY >= btnY && mouseY < btnY + btnH) {
                    // String decrement - not standard, skip
                }
                if (mouseX >= plusX && mouseX < plusX + 20 && mouseY >= btnY && mouseY < btnY + btnH) {
                    // String increment - not standard, skip
                }
                // String entries are NOT selectable
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        handlePinnedTooltipDrag(mouseX, mouseY);
        if (itemPicker.isOpen()) {
            return itemPicker.mouseDragged(mouseX, mouseY, button, this.width, this.height);
        }
        if (colorWheel.isOpen()) {
            return colorWheel.mouseDragged(mouseX, mouseY, button);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        handlePinnedTooltipRelease();
        if (itemPicker.isOpen()) {
            itemPicker.mouseReleased(mouseX, mouseY, button);
        }
        if (colorWheel.isOpen()) {
            colorWheel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Batch 12 popup scroll
        if (dropdownEnum.isOpen()) {
            return dropdownEnum.mouseScrolled(mouseX, mouseY, delta);
        }
        // Pinned tooltip scroll takes priority - check if mouse is over any pinned tooltip
        for (int idx = pinnedTooltips.size() - 1; idx >= 0; idx--) {
            PinnedTooltip pt = pinnedTooltips.get(idx);
            if (mouseX >= pt.x && mouseX < pt.x + pt.width && mouseY >= pt.y && mouseY < pt.y + pt.height) {
                if (delta > 0) {
                    pt.scrollOffset = Math.max(0, pt.scrollOffset - 2);
                } else {
                    pt.scrollOffset += 2;
                }
                return true;
            }
        }
        // Item picker popup takes priority when open
        if (itemPicker.isOpen()) {
            return itemPicker.mouseScrolled(mouseX, mouseY, delta);
        }
        // ── Ctrl+scroll on selected entry: change numeric value ──
        // When Ctrl is held and an entry is selected, scroll ONLY adjusts the value.
        // It NEVER scrolls the list. Ctrl locks out tab scrolling entirely.
        if (hasControlDown() && selectedEntryIdx >= 0 && selectedEntryIdx < filteredEntries.size()) {
            DisplayEntry entry = filteredEntries.get(selectedEntryIdx);
            String type = ConfigValueAccessor.getType(entry.configPath);
            if (!type.equals("boolean") && !type.equals("unknown") && !type.equals("string")) {
                boolean isColor = entry.configPath.contains("color") || entry.configPath.contains("Color") ||
                                  (entry.configPath.startsWith("client.colors.") && type.equals("int"));
                if (!isColor) {
                    // New modifier scheme with Alt 100k:
                    // Ctrl only = x1, Ctrl+C = x0.01, Ctrl+Shift = x10, Shift only = x100, Ctrl+Shift+C = x1000, Alt = x100000
                    boolean shift = hasShiftDown();
                    boolean alt = hasAltDown();
                    boolean cKey = isCKeyDown();
                    double stepMult = 1.0; // Ctrl only = x1
                    if (alt) stepMult = 100000.0;             // Alt = x100000
                    else if (shift && cKey) stepMult = 1000.0; // Ctrl+Shift+C
                    else if (cKey) stepMult = 0.01;           // Ctrl+C
                    else if (shift) stepMult = 10.0;          // Ctrl+Shift
                    // else Ctrl only = x1 (default)
                    double step = getStepSize(entry.configPath, type) * stepMult;
                    if (delta > 0) {
                        ConfigValueAccessor.increment(entry.configPath, step);
                    } else {
                        ConfigValueAccessor.decrement(entry.configPath, step);
                    }
                    return true;
                }
            }
        }

        // When Ctrl is held but no entry selected, lock out scrolling entirely
        if (hasControlDown()) {
            return true; // Consume the event, don't scroll the list
        }

        // ── Check if mouse is hovering over a numeric config entry ──
        // If so, use the scroll wheel to increment/decrement the value.
        // Same shared hit-test as render() and mouseClicked(): the row the wheel
        // acts on is by construction the row drawn under the cursor.
        int entryYStart = getEntryYStart();
        int entryWidth = getEntryWidth();
        LayoutEngine.Layout ly = layout();
        int hoverRow = ly.rowIndexAt(scrollOffset, mouseY, entryYStart - 2, getEntryClipBottom());

        if (hoverRow >= 0) {
            LayoutEngine.Row row = ly.get(hoverRow);
            if (!row.header) {
                DisplayEntry entry = row.entry;
                int y = row.y - ly.pixelOffset(scrollOffset);
                String type = ConfigValueAccessor.getType(entry.configPath);

                boolean isColor = entry.configPath.contains("color") || entry.configPath.contains("Color") ||
                                  (entry.configPath.startsWith("client.colors.") && type.equals("int"));
                boolean isItemsConfig = type.equals("string") &&
                        (entry.configPath.contains("startingItems") || entry.configPath.contains(".items") ||
                         entry.configPath.startsWith("identity.custom."));
                boolean scrollable = !type.equals("boolean") && !type.equals("unknown")
                        && !isColor && !isItemsConfig;

                if (scrollable) {
                    // Check if mouse is over the value area (same area as +/- buttons)
                    int minusX = entryWidth - 180;
                    int plusX = entryWidth - 30;
                    int btnY = y + 3;
                    int btnH = 16;
                    int valAreaStart = minusX;
                    int valAreaEnd = plusX + 20;

                    if (mouseX >= valAreaStart && mouseX < valAreaEnd && mouseY >= btnY && mouseY < btnY + btnH) {
                        // Mouse wheel over a numeric entry — change the value
                        // New modifier scheme (without Ctrl, since Ctrl is handled above):
                        // Shift only = x100, Alt = x100000, plain = x1
                        boolean shift = hasShiftDown();
                        boolean alt = hasAltDown();
                        double stepMult = alt ? 100000.0 : (shift ? 100.0 : 1.0);

                        double step = getStepSize(entry.configPath, type) * stepMult;
                        if (delta > 0) {
                            ConfigValueAccessor.increment(entry.configPath, step);
                        } else {
                            ConfigValueAccessor.decrement(entry.configPath, step);
                        }
                        return true;
                    }
                }
            }
        }

        // No entry hovered — scroll the list. scrollOffset is a ROW index into the
        // shared layout, and the upper bound comes from cumulative pixel height, so
        // variable-height rows (wrapped headers, two-line labels) scroll correctly.
        int maxFirst = ly.maxFirstRow(getEntryViewportHeight());
        if (delta > 0) {
            scrollOffset = Math.max(0, scrollOffset - 2);
        } else {
            scrollOffset = Math.min(maxFirst, scrollOffset + 2);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Escape deselects entry (if selected) before doing anything else
        if (keyCode == 256 && selectedEntryIdx >= 0) {
            selectedEntryIdx = -1;
            return true;
        }
        // Batch 12 popups take priority
        if (colorWheel.isOpen()) {
            if (keyCode == 256) colorWheel.close();
            return true;
        }
        if (dropdownEnum.isOpen()) {
            if (keyCode == 256) dropdownEnum.close();
            return true;
        }
        if (multiLineEditor.isOpen()) {
            return multiLineEditor.keyPressed(keyCode, scanCode, modifiers);
        }
        // Name input popup takes priority
        if (nameInput.isOpen()) {
            return nameInput.keyPressed(keyCode, scanCode, modifiers);
        }
        // Item picker popup takes priority
        if (itemPicker.isOpen()) {
            if (itemPicker.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            // Escape closes item picker
            if (keyCode == 256) {
                itemPicker.close();
                return true;
            }
            return true; // Consume all keys while picker is open
        }
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (editField != null && editField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        // Enter or Escape closes edit field
        if (editField != null && (keyCode == 257 || keyCode == 256)) {
            editField.setFocused(false);
            this.children().remove(editField);
            editField = null;
            editingKey = null;
            return true;
        }

        // ── Keyboard navigation: 1-9 for top tabs, arrow keys for sub-tabs ──
        if (!this.searchBox.isFocused()) {
            // Number keys 1-9 jump to top-level tabs
            if (keyCode >= org.lwjgl.glfw.GLFW.GLFW_KEY_1 && keyCode <= org.lwjgl.glfw.GLFW.GLFW_KEY_9) {
                int tabIdx = keyCode - org.lwjgl.glfw.GLFW.GLFW_KEY_1;
                // Same ordered list the tab rects are built from. Reading
                // TAB_TO_SUBTABS.keySet() here made the 1-9 shortcuts address a
                // different tab than the Nth one actually drawn as soon as the
                // user reordered their tabs.
                List<String> topTabs = new ArrayList<>(CustomTabManager.getTabOrder());
                topTabs.removeIf(name -> !TAB_TO_SUBTABS.containsKey(name) && !CustomTabManager.isCustomTab(name));
                if (tabIdx < topTabs.size()) {
                    activeTopTab = topTabs.get(tabIdx);
                    List<String> subs = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
                    if (!subs.isEmpty()) activeSubTab = subs.get(0);
                    else if (CustomTabManager.isCustomTab(activeTopTab)) activeSubTab = "All";
                    activeGroup = "";
                    searchMode = false;
                    scrollOffset = 0;
                    rebuildSubTabButtons();
                    refreshEntries();
                    return true;
                }
            }
            // Arrow up/down to cycle sub-tabs
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_UP || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN) {
                List<String> subTabs = TAB_TO_SUBTABS.getOrDefault(activeTopTab, List.of());
                if (!subTabs.isEmpty()) {
                    int curIdx = subTabs.indexOf(activeSubTab);
                    int newIdx = keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN
                        ? (curIdx + 1) % subTabs.size()
                        : (curIdx - 1 + subTabs.size()) % subTabs.size();
                    activeSubTab = subTabs.get(newIdx);
                    activeGroup = "";
                    scrollOffset = 0;
                    refreshEntries();
                    return true;
                }
            }
            // Ctrl+Z = undo, Ctrl+Y = redo
            if (hasControlDown() && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_Z) {
                undoConfigChange();
                return true;
            }
            if (hasControlDown() && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_Y) {
                redoConfigChange();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Batch 12 multi-line editor takes priority
        if (multiLineEditor.isOpen()) {
            return multiLineEditor.charTyped(codePoint, modifiers);
        }
        // Name input popup takes priority
        if (nameInput.isOpen()) {
            return nameInput.charTyped(codePoint, modifiers);
        }
        // Item picker popup takes priority
        if (itemPicker.isOpen()) {
            return itemPicker.charTyped(codePoint, modifiers);
        }
        // Forward character input to the edit field when it's active
        if (editField != null && editField.isFocused()) {
            if (editField.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        // Forward to search box
        if (searchBox.isFocused()) {
            if (searchBox.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        // Save tooltip memory to config
        saveTooltipMemory();
        // Save user data (favorites, locks, search history)
        saveUserData();

        // Config validation: count issues
        int overMaxCount = 0;
        int modifiedCount = 0;
        for (String path : ConfigValueAccessor.getAllPaths()) {
            String currentVal = ConfigValueAccessor.getValueString(path);
            String defaultVal = ConfigValueAccessor.getDefaultValueString(path);
            String maxVal = ConfigValueAccessor.getMaxValueString(path);
            if (!currentVal.equals(defaultVal)) modifiedCount++;
            if (maxVal != null && !maxVal.isEmpty()) {
                try {
                    double cur = Double.parseDouble(currentVal.trim());
                    double max = Double.parseDouble(maxVal.trim());
                    if (cur > max) overMaxCount++;
                } catch (NumberFormatException e) { /* ignore */ }
            }
        }
        if (overMaxCount > 0) {
            System.out.println("[ConfigExt] Warning: " + overMaxCount + " config(s) exceed their max value. This may cause instability.");
        }
        System.out.println("[ConfigExt] Config saved. " + modifiedCount + " setting(s) modified from default.");

        // Force-save Forge configs to disk so changes persist across game restarts.
        try {
            java.lang.reflect.Field configsField = net.minecraftforge.fml.config.ConfigTracker.class.getDeclaredField("configs");
            configsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, net.minecraftforge.fml.config.ModConfig> configs =
                (java.util.Map<String, net.minecraftforge.fml.config.ModConfig>) configsField.get(net.minecraftforge.fml.config.ConfigTracker.INSTANCE);
            for (var entry : configs.entrySet()) {
                if (entry.getKey().startsWith("xiaoxiang_config_ext")) {
                    entry.getValue().save();
                }
            }
        } catch (Exception e) { /* ignore - reflection might fail on different Forge versions */ }
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }
}
