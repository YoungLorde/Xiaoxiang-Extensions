package com.xiaoxiang.configext.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * Pop-up item picker for selecting starting items from ALL installed mods.
 * Uses a 4-panel layout: Categories (mods) > Sub-categories > Items > Selected.
 * Each panel has a proper scroll bar. Items are dynamically scanned from the Forge registry.
 */
public class ItemPickerPopup {

    private boolean open = false;
    private String configPath = "";
    private int popupX, popupY, popupW, popupH;

    // Active selections
    private String activeMod = "All";
    private String activeSubCat = "All";
    private String activeSubSubCat = "All";

    // Scroll offsets for each panel
    private int catScroll = 0;
    private int subCatScroll = 0;
    private int subSubCatScroll = 0;
    private int itemScroll = 0;
    private int selectedScroll = 0;

    // Which scroll bar is being dragged (0=none, 1=cat, 2=subcat, 3=subsubcat, 4=items, 5=selected)
    private int scrollBarDrag = 0;
    private int scrollBarDragStartY = 0;
    private int scrollBarDragStartScroll = 0;

    // Popup drag/resize state
    private boolean dragging = false;
    private boolean resizing = false;
    private double dragOffsetX, dragOffsetY;

    // Search state
    private String searchQuery = "";
    private boolean searchFocused = false;
    private List<String> filteredItems = new ArrayList<>();
    private int searchCursorPos = 0;

    // Currently selected items: "modid:itemid" -> count
    private final LinkedHashMap<String, Integer> selectedItems = new LinkedHashMap<>();

    // Currently hovered/selected item in the items panel (for preview + double-click)
    private String hoveredItemId = null;
    // Hover-delay state for the item-list tooltip (renderHoveredItemTooltip): mirrors the
    // 3-second "solidify" delay used for the config screen's keyword glossary popup, so a
    // tooltip only appears once the player has actually paused on an item instead of firing
    // instantly while they scan down the list.
    private String tooltipHoveredItemId = null;
    private long tooltipHoverStart = 0L;
    private static final long ITEM_TOOLTIP_HOVER_DELAY_MS = 3000;
    private static final int ITEM_TOOLTIP_MAX_WIDTH = 220;
    private long lastClickTime = 0;
    private String lastClickedItemId = null;
    private static final long DOUBLE_CLICK_MS = 500;

    // ── Category hierarchy: modId -> (subCategory -> (subSubCategory -> list of item IDs)) ──
    // If a subCategory has only "All" as its subSubCategory, no 5th panel is shown.
    // If it has multiple subSubCategories, the 5th panel appears.
    private static final Map<String, Map<String, Map<String, List<String>>>> HIERARCHY = new LinkedHashMap<>();
    // "All" pseudo-mod: all items
    private static final List<String> ALL_ITEMS = new ArrayList<>();
    // Item ID -> display name
    private static final Map<String, String> ITEM_NAMES = new HashMap<>();
    // Mod id -> friendly display name
    private static final Map<String, String> MOD_NAMES = new HashMap<>();
    // Sub-category name -> friendly display name
    private static final Map<String, String> SUBCAT_NAMES = new HashMap<>();
    // Sub-sub-category name -> friendly display name
    private static final Map<String, String> SUBSUBCAT_NAMES = new HashMap<>();
    private static boolean categoriesBuilt = false;

    // ── Panel width constants ──
    private static final int CAT_W = 90;
    private static final int SUBCAT_W = 100;
    private static final int SUBSUBCAT_W = 90;
    private static final int PREVIEW_W = 80; // Item preview panel
    private static final int SEL_W = 160; // Reduced to make room for preview
    private static final int SCROLL_BAR_W = 6;
    private static final int SEARCH_H = 16;
    private static final int HEADER_H = 14;
    private static final int ITEM_H = 16;
    private static final int TITLE_BAR_H = 22;
    private static final int BUTTON_AREA_H = 30;

    // ── Callbacks ──
    private DuplicateCallback duplicateCallback;
    private DeleteCallback deleteCallback;

    /** Lazily build the category hierarchy by scanning the Forge item registry. */
    private static synchronized void ensureCategoriesBuilt() {
        if (categoriesBuilt) return;
        categoriesBuilt = true;
        try {
            var registry = net.minecraftforge.registries.ForgeRegistries.ITEMS;
            Map<String, List<String>> byMod = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (var entry : registry.getEntries()) {
                var rl = entry.getKey().location();
                String modId = rl.getNamespace();
                String path = rl.getPath();
                String itemId = rl.toString();
                ALL_ITEMS.add(itemId);
                ITEM_NAMES.put(itemId, prettifyName(path));
                byMod.computeIfAbsent(modId, k -> new ArrayList<>()).add(itemId);
            }

            java.util.Collections.sort(ALL_ITEMS);

            // Build 3-level hierarchy for each mod
            for (var modEntry : byMod.entrySet()) {
                String modId = modEntry.getKey();
                List<String> items = modEntry.getValue();
                java.util.Collections.sort(items);
                MOD_NAMES.put(modId, prettifyModName(modId));

                // subCat -> (subSubCat -> [items])
                Map<String, Map<String, List<String>>> subCats = new LinkedHashMap<>();
                for (String itemId : items) {
                    String[] cats = detectSubCategory(modId, itemId);
                    String subCat = cats[0];
                    String subSubCat = cats[1];
                    subCats.computeIfAbsent(subCat, k -> new LinkedHashMap<>())
                           .computeIfAbsent(subSubCat, k -> new ArrayList<>())
                           .add(itemId);
                    SUBCAT_NAMES.putIfAbsent(subCat, prettifySubCatName(subCat));
                    if (!subSubCat.equals("All")) {
                        SUBSUBCAT_NAMES.putIfAbsent(subSubCat, subSubCat);
                    }
                }

                // Sort sub-categories alphabetically but keep "Misc" last
                Map<String, Map<String, List<String>>> sortedSubCats = new LinkedHashMap<>();
                List<String> subCatKeys = new ArrayList<>(subCats.keySet());
                java.util.Collections.sort(subCatKeys, (a, b) -> {
                    if (a.equals("Misc")) return 1;
                    if (b.equals("Misc")) return -1;
                    return a.compareToIgnoreCase(b);
                });
                for (String key : subCatKeys) {
                    // For each subCat, if it only has "All", keep it flat.
                    // If it has multiple subSubCats, sort them with "All" first.
                    Map<String, List<String>> subSubCats = subCats.get(key);
                    if (subSubCats.size() > 1) {
                        Map<String, List<String>> sortedSubSub = new LinkedHashMap<>();
                        List<String> subSubKeys = new ArrayList<>(subSubCats.keySet());
                        java.util.Collections.sort(subSubKeys, (a, b) -> {
                            if (a.equals("All")) return -1;
                            if (b.equals("All")) return 1;
                            if (a.equals("Other")) return 1;
                            if (b.equals("Other")) return -1;
                            return a.compareToIgnoreCase(b);
                        });
                        for (String ssk : subSubKeys) {
                            sortedSubSub.put(ssk, subSubCats.get(ssk));
                        }
                        sortedSubCats.put(key, sortedSubSub);
                    } else {
                        sortedSubCats.put(key, subSubCats);
                    }
                }

                HIERARCHY.put(modId, sortedSubCats);
            }
        } catch (Throwable t) {
            // Registry not available yet
        }
    }

    /** Detect sub-category and sub-sub-category for an item. Returns {subCat, subSubCat}. */
    private static String[] detectSubCategory(String modId, String fullItemId) {
        String path = fullItemId.contains(":") ? fullItemId.substring(fullItemId.indexOf(":") + 1) : fullItemId;

        // Xiaoxiang Cultivation mod: use name patterns based on all 337 items in the mod
        if (modId.equals("xiaoxiang_cultivation")) {
            // Spawn eggs
            if (path.startsWith("spawn_egg_") || path.equals("soul_reaper_spawn_egg"))
                return new String[]{"Spawn Eggs", "All"};
            // Pills (all pill_* + special pill names)
            if (path.startsWith("pill_") || path.equals("zhuji_dan") || path.equals("jiedan_pill") ||
                path.equals("blood_jiedan_pill") || path.equals("blood_spirit_pill") ||
                path.equals("recall_pill") || path.equals("sex_change_pill") || path.equals("youth_pill"))
                return new String[]{"Pills", "All"};
            // Weapons (swords + blades + soul_hook)
            if (path.contains("sword") || path.startsWith("bloodthirst") || path.equals("soul_hook"))
                return new String[]{"Weapons", "All"};
            // Spell Books
            if (path.startsWith("spell_book_"))
                return new String[]{"Spell Books", "All"};
            // Technique Books
            if (path.startsWith("technique_book_"))
                return new String[]{"Technique Books", "All"};
            // Formations (flags, fields, plates, compass, knife)
            if (path.endsWith("_flag") || path.endsWith("_field") || path.startsWith("formation_") ||
                path.endsWith("_plate") || path.endsWith("_core_plate") || path.equals("formation_compass") ||
                path.equals("formation_inscription_knife"))
                return new String[]{"Formations", "All"};
            // Spirit Stones (stones + ores)
            if (path.contains("spirit_stone"))
                return new String[]{"Spirit Stones", "All"};
            // Spirit Veins (cores + springs)
            if (path.contains("spirit_vein") || path.equals("spirit_vein_spring"))
                return new String[]{"Spirit Veins", "All"};
            // Spirit Plants (seeds + herbs + flowers + grasses + ginseng + lotus + pepper + chrysanthemum + orchid + bloom)
            if (path.endsWith("_seeds") || path.equals("herb") || path.contains("flower") ||
                path.contains("bloom") || path.contains("grass") || path.contains("orchid") ||
                path.contains("ginseng") || path.contains("lotus") || path.contains("pepper") ||
                path.contains("chrysanthemum") || path.equals("blood_ganoderma"))
                return new String[]{"Spirit Plants", "All"};
            // Storage Bags
            if (path.startsWith("storage_"))
                return new String[]{"Storage Bags", "All"};
            // Realm Tokens
            if (path.startsWith("realm_token_") || path.equals("sect_token") || path.equals("soul_reaper_token"))
                return new String[]{"Realm Tokens", "All"};
            // Special Materials (cores + misc special items)
            if (path.equals("alchemy_core") || path.equals("refining_core") || path.equals("storage_core") ||
                path.equals("cushion") || path.equals("ink") || path.equals("talisman_paper") ||
                path.equals("cultivation_compendium") || path.equals("foundation_secret") ||
                path.equals("dao_foundation_fruit") || path.equals("ningzhen_creation_fruit") ||
                path.equals("origin_reconfiguration_token") || path.equals("divination_compass") ||
                path.equals("heaven_clear_qi") || path.equals("earth_evil_qi") ||
                path.equals("blood_transformation_talisman") || path.equals("reincarnation_fate_plate") ||
                path.equals("all_creatures_true_blood"))
                return new String[]{"Special Materials", "All"};
            // Blocks
            if (path.equals("bone_block"))
                return new String[]{"Blocks", "All"};
            return new String[]{"Misc", "All"};
        }

        // Minecraft: detailed categorization with sub-sub-categories
        if (modId.equals("minecraft")) {
            return detectMinecraftSubCategory(path);
        }

        // Other mods: try creative tab via reflection, fallback to name patterns
        try {
            Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(
                    net.minecraft.resources.ResourceLocation.tryParse(fullItemId));
            if (item != null) {
                java.lang.reflect.Method getTab = null;
                for (java.lang.reflect.Method m : Item.class.getMethods()) {
                    if (m.getName().equals("getCreativeModeTab") || m.getName().equals("getItemCategory")) {
                        getTab = m;
                        break;
                    }
                }
                if (getTab != null) {
                    Object tab = getTab.invoke(item);
                    if (tab != null) {
                        java.lang.reflect.Method getDisplayName = tab.getClass().getMethod("getDisplayName");
                        Object displayName = getDisplayName.invoke(tab);
                        String tabName = displayName.toString();
                        if (tabName.contains("Building")) return new String[]{"Building Blocks", "All"};
                        if (tabName.contains("Natural")) return new String[]{"Natural Blocks", "All"};
                        if (tabName.contains("Functional")) return new String[]{"Functional Blocks", "All"};
                        if (tabName.contains("Redstone")) return new String[]{"Redstone", "All"};
                        if (tabName.equals("Tools") || tabName.contains("Tool")) return new String[]{"Tools", "All"};
                        if (tabName.equals("Combat") || tabName.contains("Combat")) return new String[]{"Combat", "All"};
                        if (tabName.equals("Food") || tabName.contains("Food")) return new String[]{"Food", "All"};
                        if (tabName.contains("Ingredient")) return new String[]{"Ingredients", "All"};
                        if (tabName.contains("Brewing")) return new String[]{"Brewing", "All"};
                        if (tabName.contains("Spawn")) return new String[]{"Spawn Eggs", "All"};
                        if (tabName.length() <= 20) return new String[]{tabName, "All"};
                    }
                }
            }
        } catch (Throwable ignored) {}

        // Fallback: simple name-based heuristics
        if (path.contains("sword") || path.contains("blade") || path.contains("bow") ||
            path.contains("axe") || path.contains("weapon"))
            return new String[]{"Weapons", "All"};
        if (path.contains("pickaxe") || path.contains("shovel") || path.contains("hoe") ||
            path.contains("tool"))
            return new String[]{"Tools", "All"};
        if (path.contains("block") || path.contains("brick") || path.contains("plank") ||
            path.contains("stone"))
            return new String[]{"Blocks", "All"};
        if (path.contains("food") || path.contains("apple") || path.contains("bread"))
            return new String[]{"Food", "All"};
        return new String[]{"Misc", "All"};
    }

    // ── Minecraft wood type names for sub-sub-categorization ──
    private static final String[] WOOD_TYPES = {
        "oak", "birch", "spruce", "jungle", "acacia", "dark_oak", "mangrove",
        "cherry", "bamboo", "crimson", "warped"
    };

    /** Detect Minecraft sub-category with detailed sub-sub-categories. */
    private static String[] detectMinecraftSubCategory(String path) {
        // ── Wood types: sub-sub-categories by wood species ──
        for (String wood : WOOD_TYPES) {
            if (path.startsWith(wood + "_") || path.equals(wood)) {
                // Check it's actually a wood item
                if (path.contains("planks") || path.contains("log") || path.contains("wood") ||
                    path.contains("slab") || path.contains("stairs") || path.contains("fence") ||
                    path.contains("door") || path.contains("trapdoor") || path.contains("button") ||
                    path.contains("pressure_plate") || path.contains("sign") || path.contains("boat") ||
                    path.contains("hanging_sign") || path.contains("sapling") || path.contains("leaves") ||
                    path.contains("slab") || path.contains("fence_gate") || path.contains("boat")) {
                    String woodName = Character.toUpperCase(wood.charAt(0)) + wood.substring(1);
                    return new String[]{"Wood", woodName};
                }
            }
        }

        // ── Stone types: sub-sub-categories by stone type ──
        if (path.equals("stone") || path.startsWith("stone_") || path.equals("cobblestone") ||
            path.startsWith("cobblestone_") || path.equals("mossy_cobblestone") ||
            path.startsWith("mossy_cobblestone_") || path.equals("smooth_stone") ||
            path.startsWith("smooth_stone_") || path.equals("stone_bricks") ||
            path.startsWith("stone_brick_") || path.equals("mossy_stone_bricks") ||
            path.startsWith("mossy_stone_brick_") || path.equals("chiseled_stone_bricks") ||
            path.equals("infested_stone") || path.startsWith("infested_stone_")) {
            return new String[]{"Stone", "Stone"};
        }
        if (path.equals("granite") || path.startsWith("granite_") || path.equals("polished_granite") ||
            path.startsWith("polished_granite_")) {
            return new String[]{"Stone", "Granite"};
        }
        if (path.equals("diorite") || path.startsWith("diorite_") || path.equals("polished_diorite") ||
            path.startsWith("polished_diorite_")) {
            return new String[]{"Stone", "Diorite"};
        }
        if (path.equals("andesite") || path.startsWith("andesite_") || path.equals("polished_andesite") ||
            path.startsWith("polished_andesite_")) {
            return new String[]{"Stone", "Andesite"};
        }
        if (path.contains("deepslate")) {
            return new String[]{"Stone", "Deepslate"};
        }
        if (path.equals("basalt") || path.startsWith("basalt_") || path.equals("smooth_basalt") ||
            path.startsWith("smooth_basalt_") || path.equals("polished_basalt") ||
            path.startsWith("polished_basalt_")) {
            return new String[]{"Stone", "Basalt"};
        }
        if (path.equals("blackstone") || path.startsWith("blackstone_") || path.startsWith("polished_blackstone_") ||
            path.equals("gilded_blackstone")) {
            return new String[]{"Stone", "Blackstone"};
        }
        if (path.equals("sandstone") || path.startsWith("sandstone_") || path.equals("red_sandstone") ||
            path.startsWith("red_sandstone_") || path.equals("cut_sandstone") ||
            path.startsWith("cut_sandstone_") || path.equals("chiseled_sandstone") ||
            path.equals("smooth_sandstone") || path.startsWith("smooth_sandstone_")) {
            return new String[]{"Stone", "Sandstone"};
        }

        // ── Ores: sub-sub-categories by ore type ──
        if (path.contains("_ore") || path.equals("raw_iron") || path.equals("raw_gold") ||
            path.equals("raw_copper") || path.equals("coal") || path.equals("diamond") ||
            path.equals("emerald") || path.equals("redstone") || path.equals("lapis_lazuli") ||
            path.equals("quartz") || path.equals("nether_quartz") || path.equals("amethyst_shard") ||
            path.equals("echo_shard")) {
            if (path.contains("coal")) return new String[]{"Ores", "Coal"};
            if (path.contains("iron")) return new String[]{"Ores", "Iron"};
            if (path.contains("gold")) return new String[]{"Ores", "Gold"};
            if (path.contains("diamond")) return new String[]{"Ores", "Diamond"};
            if (path.contains("emerald")) return new String[]{"Ores", "Emerald"};
            if (path.contains("redstone")) return new String[]{"Ores", "Redstone"};
            if (path.contains("lapis")) return new String[]{"Ores", "Lapis"};
            if (path.contains("copper")) return new String[]{"Ores", "Copper"};
            if (path.contains("quartz")) return new String[]{"Ores", "Quartz"};
            return new String[]{"Ores", "Other"};
        }

        // ── Tools: sub-sub-categories by tool type ──
        if (path.contains("pickaxe")) return new String[]{"Tools", "Pickaxes"};
        if (path.contains("axe") && !path.contains("waxed") && !path.contains("block")) return new String[]{"Tools", "Axes"};
        if (path.contains("shovel")) return new String[]{"Tools", "Shovels"};
        if (path.contains("hoe")) return new String[]{"Tools", "Hoes"};
        if (path.equals("shears") || path.equals("flint_and_steel") || path.equals("fishing_rod") ||
            path.equals("compass") || path.equals("clock") || path.equals("spyglass") ||
            path.equals("recovery_compass") || path.equals("lodestone_compass") || path.equals("brush") ||
            path.equals("lead") || path.equals("name_tag") || path.equals("saddle") ||
            path.equals("book") || path.equals("writable_book") || path.equals("written_book") ||
            path.equals("enchanted_book") || path.equals("knowledge_book") || path.equals("wheat_seeds") ||
            path.equals("bone_meal") || path.equals("string")) {
            return new String[]{"Tools", "Utility"};
        }

        // ── Weapons: sub-sub-categories by weapon type ──
        if (path.contains("sword")) return new String[]{"Weapons", "Swords"};
        if (path.contains("bow")) return new String[]{"Weapons", "Bows"};
        if (path.contains("crossbow")) return new String[]{"Weapons", "Crossbows"};
        if (path.equals("trident") || path.equals("arrow") || path.equals("tipped_arrow") ||
            path.equals("spectral_arrow") || path.equals("firework_rocket") || path.equals("shield") ||
            path.equals("totem_of_undying") || path.equals("snowball") || path.equals("egg") ||
            path.equals("ender_pearl") || path.equals("eye_of_ender") || path.equals("fire_charge") ||
            path.equals("wind_charge")) {
            return new String[]{"Weapons", "Other"};
        }

        // ── Armor: sub-sub-categories by armor type ──
        if (path.contains("helmet") || path.equals("turtle_helmet") || path.contains("cap")) {
            return new String[]{"Armor", "Helmets"};
        }
        if (path.contains("chestplate")) return new String[]{"Armor", "Chestplates"};
        if (path.contains("leggings")) return new String[]{"Armor", "Leggings"};
        if (path.contains("boots")) return new String[]{"Armor", "Boots"};
        if (path.contains("horse_armor")) return new String[]{"Armor", "Horse Armor"};

        // ── Food: sub-sub-categories by food type ──
        if (path.equals("apple") || path.equals("golden_apple") || path.equals("enchanted_golden_apple") ||
            path.equals("chorus_fruit") || path.equals("sweet_berries") || path.equals("glow_berries")) {
            return new String[]{"Food", "Fruits"};
        }
        if (path.contains("bread") || path.equals("cake") || path.equals("cookie") ||
            path.equals("pumpkin_pie") || path.contains("wheat")) {
            return new String[]{"Food", "Baked"};
        }
        if (path.contains("beef") || path.contains("porkchop") || path.contains("chicken") ||
            path.contains("mutton") || path.contains("rabbit") || path.contains("cod") ||
            path.contains("salmon") || path.contains("tropical_fish") || path.contains("pufferfish")) {
            return new String[]{"Food", "Raw Meat"};
        }
        if (path.contains("cooked_")) {
            return new String[]{"Food", "Cooked Meat"};
        }
        if (path.equals("carrot") || path.equals("golden_carrot") || path.equals("potato") ||
            path.equals("baked_potato") || path.equals("poisonous_potato") || path.equals("beetroot") ||
            path.equals("beetroot_soup") || path.equals("mushroom_stew") || path.equals("suspicious_stew") ||
            path.equals("rabbit_stew") || path.equals("dried_kelp") || path.equals("melon_slice") ||
            path.equals("nether_wart") || path.equals("sugar") || path.equals("honey_bottle") ||
            path.equals("milk_bucket") || path.equals("egg")) {
            return new String[]{"Food", "Other"};
        }

        // ── Redstone: sub-sub-categories ──
        if (path.equals("redstone") || path.equals("redstone_torch") || path.equals("redstone_block") ||
            path.equals("repeater") || path.equals("comparator") || path.equals("piston") ||
            path.equals("sticky_piston") || path.equals("observer") || path.equals("dispenser") ||
            path.equals("dropper") || path.equals("hopper") || path.equals("lever") ||
            path.contains("button") || path.contains("pressure_plate") || path.contains("tripwire") ||
            path.equals("daylight_detector") || path.equals("target") || path.equals("note_block") ||
            path.equals("redstone_lamp") || path.contains("rail")) {
            return new String[]{"Redstone", "All"};
        }

        // ── Decorations: sub-sub-categories ──
        if (path.contains("flower") || path.contains("tulip") || path.contains("dandelion") ||
            path.contains("poppy") || path.contains("orchid") || path.contains("allium") ||
            path.contains("azure") || path.contains("cornflower") || path.contains("lily") ||
            path.equals("wither_rose") || path.equals("sunflower") || path.equals("rose_bush") ||
            path.equals("peony") || path.equals("lilac")) {
            return new String[]{"Decorations", "Flowers"};
        }
        if (path.contains("banner")) return new String[]{"Decorations", "Banners"};
        if (path.contains("painting") || path.contains("item_frame") || path.contains("glow_item_frame")) {
            return new String[]{"Decorations", "Wall Items"};
        }
        if (path.contains("torch") || path.contains("lantern") || path.contains("lamp") ||
            path.equals("glowstone") || path.contains("sea_lantern") || path.contains("shroomlight") ||
            path.contains("end_rod") || path.contains("froglight")) {
            return new String[]{"Decorations", "Lighting"};
        }
        if (path.contains("pot") || path.contains("flower_pot")) {
            return new String[]{"Decorations", "Pots"};
        }

        // ── Heads/Mob Heads ──
        if (path.contains("head") || path.contains("skull")) {
            return new String[]{"Decorations", "Heads"};
        }

        // ── Brewing ──
        if (path.contains("potion") || path.contains("brewing") || path.contains("blaze_powder") ||
            path.contains("nether_wart") || path.contains("fermented_spider_eye") ||
            path.contains("dragon_breath") || path.contains("ghast_tear") || path.contains("rabbit_foot") ||
            path.contains("spider_eye")) {
            return new String[]{"Brewing", "All"};
        }

        // ── Spawn Eggs ──
        if (path.contains("spawn_egg")) return new String[]{"Spawn Eggs", "All"};

        // ── Functional Blocks ──
        if (path.contains("crafting_table") || path.contains("crafting")) return new String[]{"Functional Blocks", "Crafting"};
        if (path.contains("furnace") || path.contains("blast_furnace") || path.contains("smoker")) {
            return new String[]{"Functional Blocks", "Furnaces"};
        }
        if (path.contains("chest") || path.contains("barrel") || path.contains("shulker_box") ||
            path.contains("ender_chest")) {
            return new String[]{"Functional Blocks", "Storage"};
        }
        if (path.contains("bed")) return new String[]{"Functional Blocks", "Beds"};
        if (path.contains("door") || path.contains("trapdoor") || path.contains("fence_gate")) {
            return new String[]{"Functional Blocks", "Doors"};
        }
        if (path.contains("anvil")) return new String[]{"Functional Blocks", "Anvils"};
        if (path.contains("enchanting") || path.contains("enchantment")) {
            return new String[]{"Functional Blocks", "Enchanting"};
        }
        if (path.contains("beacon")) return new String[]{"Functional Blocks", "Beacons"};
        if (path.contains("conduit")) return new String[]{"Functional Blocks", "Conduits"};

        // ── Building Blocks: sub-sub-categories ──
        if (path.contains("brick") || path.contains("bricks")) return new String[]{"Building Blocks", "Bricks"};
        if (path.contains("glass") || path.contains("glass_pane")) return new String[]{"Building Blocks", "Glass"};
        if (path.contains("concrete") || path.contains("concrete_powder")) {
            return new String[]{"Building Blocks", "Concrete"};
        }
        if (path.contains("terracotta") || path.contains("glazed_terracotta")) {
            return new String[]{"Building Blocks", "Terracotta"};
        }
        if (path.contains("wool") || path.contains("carpet")) return new String[]{"Building Blocks", "Wool"};
        if (path.contains("dirt") || path.equals("grass_block") || path.equals("podzol") ||
            path.equals("mycelium") || path.equals("rooted_dirt") || path.equals("coarse_dirt") ||
            path.equals("mud") || path.contains("mud_")) {
            return new String[]{"Building Blocks", "Dirt"};
        }
        if (path.contains("sand") || path.contains("gravel")) return new String[]{"Building Blocks", "Sand"};
        if (path.contains("ice") || path.contains("snow")) return new String[]{"Building Blocks", "Ice & Snow"};
        if (path.contains("prismarine")) return new String[]{"Building Blocks", "Prismarine"};
        if (path.contains("nether_bricks") || path.contains("netherrack") || path.contains("end_stone")) {
            return new String[]{"Building Blocks", "Nether & End"};
        }

        // ── Natural Blocks ──
        if (path.contains("sapling")) return new String[]{"Natural Blocks", "Saplings"};
        if (path.contains("leaves")) return new String[]{"Natural Blocks", "Leaves"};
        if (path.contains("mushroom") || path.contains("fungus")) return new String[]{"Natural Blocks", "Mushrooms"};
        if (path.contains("coral")) return new String[]{"Natural Blocks", "Coral"};
        if (path.contains("kelp") || path.contains("seagrass") || path.contains("sea_pickle")) {
            return new String[]{"Natural Blocks", "Aquatic Plants"};
        }

        // ── Ingots & Materials ──
        if (path.contains("ingot") || path.equals("nugget") || path.contains("nugget") ||
            path.equals("stick") || path.equals("blaze_rod") || path.equals("bone") ||
            path.equals("gunpowder") || path.equals("ender_eye") || path.equals("ender_pearl") ||
            path.equals("slime_ball") || path.equals("magma_cream") || path.equals("ghast_tear") ||
            path.equals("leather") || path.equals("feather") || path.equals("flint") ||
            path.equals("clay_ball") || path.equals("brick_item") || path.equals("paper") ||
            path.equals("iron_ingot") || path.equals("gold_ingot") || path.equals("copper_ingot") ||
            path.equals("netherite_ingot") || path.equals("netherite_scrap") || path.equals("diamond") ||
            path.equals("emerald") || path.equals("lapis_lazuli") || path.equals("quartz")) {
            return new String[]{"Materials", "Ingots & Nuggets"};
        }
        if (path.contains("dye")) return new String[]{"Materials", "Dyes"};

        // ── Music ──
        if (path.contains("music_disc") || path.contains("jukebox") || path.contains("goat_horn")) {
            return new String[]{"Misc", "Music"};
        }

        // ── Buckets ──
        if (path.contains("bucket")) return new String[]{"Misc", "Buckets"};

        // ── Spawn-related ──
        if (path.contains("boat")) return new String[]{"Transport", "Boats"};
        if (path.contains("minecart")) return new String[]{"Transport", "Minecarts"};
        if (path.contains("saddle") || path.contains("horse_armor")) return new String[]{"Transport", "Horse Gear"};

        // ── Books ──
        if (path.contains("book")) return new String[]{"Misc", "Books"};

        // ── Unclassified ──
        return new String[]{"Misc", "All"};
    }

    /** Convert a mod id to a friendly name. */
    private static String prettifyModName(String modId) {
        if (modId == null || modId.isEmpty()) return "Unknown";
        if (modId.equals("minecraft")) return "Minecraft";
        if (modId.equals("xiaoxiang_cultivation")) return "Xiaoxiang Cultivation";
        if (modId.equals("xiaoxiang_config_ext")) return "Config Extension";
        String[] parts = modId.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (sb.length() > 0) sb.append(" ");
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    /** Convert a sub-category key to a friendly name. */
    private static String prettifySubCatName(String subCat) {
        if (subCat == null || subCat.isEmpty()) return "Misc";
        return subCat;
    }

    /** Convert item ID (full "modid:path" or bare "path") to a readable display name. */
    private static String prettifyName(String itemId) {
        String path = itemId.contains(":") ? itemId.substring(itemId.indexOf(":") + 1) : itemId;
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return sb.toString();
    }

    /** Draw a string scaled to fit within a given width. */
    private static void drawScaledString(GuiGraphics g, net.minecraft.client.gui.Font font,
                                          String text, int x, int y, int maxWidth, int color) {
        int textWidth = font.width(text);
        if (textWidth <= maxWidth || textWidth == 0) {
            g.drawString(font, text, x, y, color);
            return;
        }
        float scale = (float) maxWidth / textWidth;
        scale = Math.max(scale, 0.5f);
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0f);
        g.drawString(font, text, 0, 0, color);
        pose.popPose();
    }

    public boolean isOpen() { return open; }

    public void open(String configPath, String currentValue) {
        ensureCategoriesBuilt();
        this.open = true;
        this.configPath = configPath;
        this.activeMod = "All";
        this.activeSubCat = "All";
        this.activeSubSubCat = "All";
        this.catScroll = 0;
        this.subCatScroll = 0;
        this.subSubCatScroll = 0;
        this.itemScroll = 0;
        this.selectedScroll = 0;
        this.dragging = false;
        this.searchQuery = "";
        this.searchFocused = false;
        this.searchCursorPos = 0;

        if (currentValue == null || currentValue.trim().isEmpty()) {
            String originalItems = getOriginalStarterItems(configPath);
            if (originalItems != null && !originalItems.isEmpty()) {
                currentValue = originalItems;
            }
        }
        parseCurrentValue(currentValue);
        rebuildFilteredItems();
    }

    public void close() {
        this.open = false;
        this.dragging = false;
        this.searchFocused = false;
    }

    /** Get a human-readable title based on the config path. */
    private String getTitle() {
        if (configPath == null || configPath.isEmpty()) return "Item Picker";
        if (configPath.startsWith("identity.startingItems.")) {
            String category = configPath.substring("identity.startingItems.".length());
            return prettifyName(category) + " Identity - Starting Items";
        }
        if (configPath.equals("identity.startingItemsDefault")) {
            return "Default Identity - Starting Items";
        }
        if (configPath.startsWith("identity.") && !configPath.startsWith("identity.lifespan.") && configPath.contains(".startingItems")) {
            String[] parts = configPath.split("\\.");
            if (parts.length >= 2) {
                return prettifyName(parts[1]) + " - Starting Items";
            }
        }
        if (configPath.startsWith("identity.custom.")) {
            String[] parts = configPath.split("\\.");
            if (parts.length >= 3) {
                return prettifyName(parts[2]) + " - Starting Items";
            }
        }
        String[] parts = configPath.split("\\.");
        if (parts.length > 0) {
            return prettifyName(parts[parts.length - 1]) + " - Item Picker";
        }
        return "Item Picker";
    }

    /** Parse current config value into selectedItems map. */
    private void parseCurrentValue(String value) {
        selectedItems.clear();
        if (value == null || value.isEmpty()) return;
        for (String pair : value.split(";")) {
            String[] parts = pair.trim().split(",");
            if (parts.length >= 2) {
                String itemId = parts[0].trim();
                int count = 1;
                try { count = Integer.parseInt(parts[1].trim()); } catch (NumberFormatException ignored) {}
                selectedItems.put(itemId, count);
            } else if (parts.length == 1 && !parts[0].trim().isEmpty()) {
                selectedItems.put(parts[0].trim(), 1);
            }
        }
    }

    /** Get the original mod's starter items for an identity. */
    private static String getOriginalStarterItems(String configPath) {
        try {
            if (configPath == null || !configPath.startsWith("identity.")) return null;
            String[] parts = configPath.split("\\.");
            if (parts.length < 3) return null;
            String identityId = parts[1];
            if (identityId.startsWith("custom_")) return null;

            com.xiaoxiang.cultivation.cultivation.Identity identity =
                    com.xiaoxiang.cultivation.cultivation.Identity.byId(identityId);
            if (identity == null) return null;

            java.util.List<net.minecraft.world.item.ItemStack> items = identity.starterItems();
            if (items == null || items.isEmpty()) return null;

            StringBuilder sb = new StringBuilder();
            for (net.minecraft.world.item.ItemStack stack : items) {
                if (stack == null || stack.isEmpty()) continue;
                String itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
                if (sb.length() > 0) sb.append(";");
                sb.append(itemId).append(",").append(stack.getCount());
            }
            return sb.length() > 0 ? sb.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Rebuild the filtered item list based on active mod + sub-category + sub-sub-category + search. */
    private void rebuildFilteredItems() {
        filteredItems.clear();
        List<String> base;
        if (activeMod.equals("All")) {
            base = ALL_ITEMS;
        } else {
            Map<String, Map<String, List<String>>> subCats = HIERARCHY.get(activeMod);
            if (subCats == null) { itemScroll = 0; return; }
            if (activeSubCat.equals("All")) {
                // "All" here is a synthetic UI entry (see getSubCategories()), not a real key
                // in the hierarchy map. It means "every sub-category under this mod" - union
                // everything below this mod, same idea as the activeSubSubCat "All" case below.
                // Previously this fell through to subCats.get("All"), which is never a real
                // key, so it always returned null and left the item list empty - this is the
                // bug behind "Xiaoxiang Cultivation > All (0)" / "Minecraft > All (0)".
                base = new ArrayList<>();
                for (Map<String, List<String>> subSubCats : subCats.values()) {
                    for (List<String> list : subSubCats.values()) base.addAll(list);
                }
            } else {
                Map<String, List<String>> subSubCats = subCats.get(activeSubCat);
                if (subSubCats == null) { itemScroll = 0; return; }
                if (activeSubSubCat.equals("All")) {
                    base = new ArrayList<>();
                    for (List<String> list : subSubCats.values()) base.addAll(list);
                } else {
                    base = subSubCats.getOrDefault(activeSubSubCat, java.util.Collections.emptyList());
                }
            }
        }
        if (searchQuery == null || searchQuery.isEmpty()) {
            filteredItems.addAll(base);
        } else {
            String q = searchQuery.toLowerCase(Locale.ROOT);
            for (String id : base) {
                String name = ITEM_NAMES.getOrDefault(id, prettifyName(id));
                if (id.toLowerCase(Locale.ROOT).contains(q) || name.toLowerCase(Locale.ROOT).contains(q)) {
                    filteredItems.add(id);
                }
            }
        }
        itemScroll = 0;
    }

    /** Get the list of sub-categories for the active mod. */
    private List<String> getSubCategories() {
        if (activeMod.equals("All")) return java.util.Collections.emptyList();
        Map<String, Map<String, List<String>>> subCats = HIERARCHY.get(activeMod);
        if (subCats == null) return java.util.Collections.emptyList();
        return new ArrayList<>(subCats.keySet());
    }

    /** Get the list of sub-sub-categories for the active mod + sub-category.
     *  Returns empty list if no sub-sub-categories exist (only "All"). */
    private List<String> getSubSubCategories() {
        if (activeMod.equals("All")) return java.util.Collections.emptyList();
        Map<String, Map<String, List<String>>> subCats = HIERARCHY.get(activeMod);
        if (subCats == null) return java.util.Collections.emptyList();
        Map<String, List<String>> subSubCats = subCats.get(activeSubCat);
        if (subSubCats == null) return java.util.Collections.emptyList();
        if (subSubCats.size() <= 1) return java.util.Collections.emptyList();
        return new ArrayList<>(subSubCats.keySet());
    }

    /** Check if the current sub-category has sub-sub-categories (5th panel needed). */
    private boolean hasSubSubCategories() {
        return !getSubSubCategories().isEmpty();
    }

    /** Get the list of all mod names (categories). */
    private List<String> getModList() {
        List<String> mods = new ArrayList<>();
        mods.add("All");
        mods.addAll(HIERARCHY.keySet());
        return mods;
    }

    /** Convert selectedItems back to config string format. */
    public String buildConfigString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : selectedItems.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(entry.getKey()).append(",").append(entry.getValue());
        }
        return sb.toString();
    }

    private String fullItemId(String itemId) {
        if (itemId == null) return "";
        if (itemId.contains(":")) return itemId;
        return "minecraft:" + itemId;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  LAYOUT HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    // Layout: Title bar (22) → Search bar (16+2) → Panel headers (14+2) → Panels → Buttons (30)
    private int getSearchRowY() { return popupY + TITLE_BAR_H + 2; }
    private int getHeaderRowY() { return getSearchRowY() + SEARCH_H + 2; }
    private int getPanelTopY() { return getHeaderRowY() + HEADER_H + 2; }
    private int getPanelBottomY() { return popupY + popupH - BUTTON_AREA_H; }
    private int getPanelHeight() { return getPanelBottomY() - getPanelTopY(); }

    // Panel X positions - conditional on whether sub-sub-categories exist
    private int getCatX() { return popupX + 4; }
    private int getSubCatX() { return getCatX() + CAT_W + 4; }
    private boolean showSubSubCatPanel() { return !activeMod.equals("All") && hasSubSubCategories(); }
    private int getSubSubCatX() { return getSubCatX() + SUBCAT_W + 4; }
    // Layout: Cats | [SubCats] | [SubSubCats] | Items | Preview | Selected
    // SubCats and SubSubCats are hidden in "All" mode
    private int getItemX() {
        if (activeMod.equals("All")) return getCatX() + CAT_W + 4;
        if (showSubSubCatPanel()) return getSubSubCatX() + SUBSUBCAT_W + 4;
        return getSubCatX() + SUBCAT_W + 4;
    }
    private int getPreviewX() { return getSelX() - PREVIEW_W - 4; }
    private int getSelX() { return popupX + popupW - SEL_W - 4; }
    private int getItemW() { return getPreviewX() - getItemX() - 4; }

    // Search bar position - spans full popup width minus margins
    private int getSearchX() { return popupX + 4; }
    private int getSearchY() { return getSearchRowY(); }
    private int getSearchW() { return popupW - 8; }

    // ════════════════════════════════════════════════════════════════════════════
    //  SCROLL BAR HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    /** Draw a scroll bar for a panel. Returns true if the mouse is over the scroll bar. */
    private boolean drawScrollBar(GuiGraphics g, int panelX, int panelW, int panelTopY, int panelBottomY,
                                   int scrollOffset, int totalItems, int visibleItems, int mouseX, int mouseY) {
        int trackX = panelX + panelW - SCROLL_BAR_W;
        int trackY = panelTopY;
        int trackH = panelBottomY - panelTopY;
        if (totalItems <= visibleItems) return false;

        // Track background
        g.fill(trackX, trackY, trackX + SCROLL_BAR_W, trackY + trackH, 0xFF1A1A24);
        g.renderOutline(trackX, trackY, SCROLL_BAR_W, trackH, 0xFF303040);

        // Thumb
        int thumbH = Math.max(12, trackH * visibleItems / totalItems);
        int maxScroll = totalItems - visibleItems;
        int thumbY = trackY + (trackH - thumbH) * scrollOffset / Math.max(1, maxScroll);
        boolean hover = mouseX >= trackX && mouseX < trackX + SCROLL_BAR_W && mouseY >= thumbY && mouseY < thumbY + thumbH;
        g.fill(trackX + 1, thumbY, trackX + SCROLL_BAR_W - 1, thumbY + thumbH, hover ? 0xFF606080 : 0xFF404060);
        g.renderOutline(trackX + 1, thumbY, SCROLL_BAR_W - 2, thumbH, hover ? 0xFF8080A0 : 0xFF505070);
        return true;
    }

    /** Check if mouse is on a panel's scroll bar track. */
    private boolean isOnScrollBar(int panelX, int panelW, int panelTopY, int panelBottomY, double mouseX, double mouseY) {
        int trackX = panelX + panelW - SCROLL_BAR_W;
        return mouseX >= trackX && mouseX < trackX + SCROLL_BAR_W && mouseY >= panelTopY && mouseY < panelBottomY;
    }

    /** Calculate scroll from thumb drag position. */
    private int calcScrollFromDrag(int panelTopY, int panelBottomY, int mouseY, int totalItems, int visibleItems) {
        int trackH = panelBottomY - panelTopY;
        int thumbH = Math.max(12, trackH * visibleItems / totalItems);
        int maxScroll = totalItems - visibleItems;
        int relativeY = mouseY - panelTopY - thumbH / 2;
        int scroll = relativeY * maxScroll / Math.max(1, trackH - thumbH);
        return Math.max(0, Math.min(maxScroll, scroll));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  MOUSE CLICKED
    // ════════════════════════════════════════════════════════════════════════════

    public boolean mouseClicked(double mouseX, double mouseY, int button, int screenWidth, int screenHeight) {
        if (!open || button != 0) return false;

        if (popupW == 0) {
            popupW = Math.min(820, screenWidth - 20);
            popupH = Math.min(420, screenHeight - 40);
            popupX = (screenWidth - popupW) / 2;
            popupY = (screenHeight - popupH) / 2;
        }

        // Click outside popup - close
        if (mouseX < popupX || mouseX > popupX + popupW || mouseY < popupY || mouseY > popupY + popupH) {
            close();
            return true;
        }

        // Title bar drag
        if (mouseY >= popupY && mouseY < popupY + TITLE_BAR_H) {
            int closeX = popupX + popupW - 20;
            int closeY = popupY + 4;
            if (!(mouseX >= closeX && mouseX < closeX + 16 && mouseY >= closeY && mouseY < closeY + 16)) {
                dragging = true;
                dragOffsetX = mouseX - popupX;
                dragOffsetY = mouseY - popupY;
                return true;
            }
        }

        // Close button
        int closeX = popupX + popupW - 20;
        int closeY = popupY + 4;
        if (mouseX >= closeX && mouseX < closeX + 16 && mouseY >= closeY && mouseY < closeY + 16) {
            close();
            return true;
        }

        int panelTopY = getPanelTopY();
        int panelBottomY = getPanelBottomY();

        // Search bar click
        int searchX = getSearchX();
        int searchY = getSearchY();
        int searchW = getSearchW();
        if (mouseX >= searchX && mouseX < searchX + searchW && mouseY >= searchY && mouseY < searchY + SEARCH_H) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }

        boolean shift = Screen.hasShiftDown();
        boolean ctrl = Screen.hasControlDown();

        // ── Panel 1: Categories (mods) ──
        int catX = getCatX();
        int catPW = CAT_W;
        // Check scroll bar first
        if (isOnScrollBar(catX, catPW, panelTopY, panelBottomY, mouseX, mouseY)) {
            List<String> mods = getModList();
            int visible = getPanelHeight() / ITEM_H;
            scrollBarDrag = 1;
            scrollBarDragStartY = (int) mouseY;
            scrollBarDragStartScroll = catScroll;
            catScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, mods.size(), visible);
            return true;
        }
        // Category items
        if (mouseX >= catX && mouseX < catX + catPW - SCROLL_BAR_W) {
            List<String> mods = getModList();
            for (int i = catScroll; i < mods.size(); i++) {
                int y = panelTopY + (i - catScroll) * ITEM_H;
                if (y >= panelBottomY) break;
                if (mouseY >= y && mouseY < y + ITEM_H) {
                    activeMod = mods.get(i);
                    activeSubCat = "All";
                    subCatScroll = 0;
                    rebuildFilteredItems();
                    return true;
                }
            }
        }

        // ── Panel 2: Sub-categories ──
        int subCatX = getSubCatX();
        int subCatPW = SUBCAT_W;
        List<String> subCats = getSubCategories();
        if (!subCats.isEmpty()) {
            if (isOnScrollBar(subCatX, subCatPW, panelTopY, panelBottomY, mouseX, mouseY)) {
                int visible = getPanelHeight() / ITEM_H;
                scrollBarDrag = 2;
                scrollBarDragStartY = (int) mouseY;
                scrollBarDragStartScroll = subCatScroll;
                subCatScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, subCats.size() + 1, visible);
                return true;
            }
            if (mouseX >= subCatX && mouseX < subCatX + subCatPW - SCROLL_BAR_W) {
                // "All" sub-category is first
                int visible = getPanelHeight() / ITEM_H;
                for (int i = subCatScroll; i <= subCats.size(); i++) {
                    int y = panelTopY + (i - subCatScroll) * ITEM_H;
                    if (y >= panelBottomY) break;
                    if (mouseY >= y && mouseY < y + ITEM_H) {
                        activeSubCat = (i == 0) ? "All" : subCats.get(i - 1);
                        activeSubSubCat = "All";
                        subSubCatScroll = 0;
                        rebuildFilteredItems();
                        return true;
                    }
                }
            }
        }

        // ── Panel 2.5: Sub-sub-categories (conditional, only if present) ──
        if (showSubSubCatPanel()) {
            int subSubCatX = getSubSubCatX();
            int subSubCatPW = SUBSUBCAT_W;
            List<String> subSubCats = getSubSubCategories();
            if (isOnScrollBar(subSubCatX, subSubCatPW, panelTopY, panelBottomY, mouseX, mouseY)) {
                int visible = getPanelHeight() / ITEM_H;
                scrollBarDrag = 3;
                scrollBarDragStartY = (int) mouseY;
                scrollBarDragStartScroll = subSubCatScroll;
                subSubCatScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, subSubCats.size() + 1, visible);
                return true;
            }
            if (mouseX >= subSubCatX && mouseX < subSubCatX + subSubCatPW - SCROLL_BAR_W) {
                int visible = getPanelHeight() / ITEM_H;
                for (int i = subSubCatScroll; i <= subSubCats.size(); i++) {
                    int y = panelTopY + (i - subSubCatScroll) * ITEM_H;
                    if (y >= panelBottomY) break;
                    if (mouseY >= y && mouseY < y + ITEM_H) {
                        activeSubSubCat = (i == 0) ? "All" : subSubCats.get(i - 1);
                        rebuildFilteredItems();
                        return true;
                    }
                }
            }
        }

        // ── Panel 3: Items ──
        int itemX = getItemX();
        int itemPW = getItemW();
        if (isOnScrollBar(itemX, itemPW, panelTopY, panelBottomY, mouseX, mouseY)) {
            int visible = getPanelHeight() / ITEM_H;
            scrollBarDrag = 4;
            scrollBarDragStartY = (int) mouseY;
            scrollBarDragStartScroll = itemScroll;
            itemScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, filteredItems.size(), visible);
            return true;
        }
        if (mouseX >= itemX && mouseX < itemX + itemPW - SCROLL_BAR_W) {
            int visible = getPanelHeight() / ITEM_H;
            for (int i = itemScroll; i < filteredItems.size(); i++) {
                int y = panelTopY + (i - itemScroll) * ITEM_H;
                if (y >= panelBottomY) break;
                if (mouseY >= y && mouseY < y + ITEM_H) {
                    String itemId = filteredItems.get(i);
                    String fullId = fullItemId(itemId);
                    // Single click = select (for preview), double-click = add to selected
                    long now = System.currentTimeMillis();
                    if (lastClickedItemId != null && lastClickedItemId.equals(fullId) && (now - lastClickTime) < DOUBLE_CLICK_MS) {
                        // Double-click: add to selected
                        if (shift) {
                            selectedItems.put(fullId, 64);
                        } else if (ctrl) {
                            int current = selectedItems.getOrDefault(fullId, 0);
                            selectedItems.put(fullId, Math.min(64, current + 10));
                        } else {
                            int current = selectedItems.getOrDefault(fullId, 0);
                            selectedItems.put(fullId, Math.max(1, current + 1));
                        }
                        lastClickedItemId = null; // Reset to prevent triple-click as another double
                    } else {
                        // Single click: just select for preview
                        hoveredItemId = fullId;
                        lastClickedItemId = fullId;
                        lastClickTime = now;
                    }
                    return true;
                }
            }
        }

        // ── Panel 4: Selected items ──
        int selX = getSelX();
        int selPW = SEL_W;
        if (isOnScrollBar(selX, selPW, panelTopY, panelBottomY, mouseX, mouseY)) {
            int visible = getPanelHeight() / ITEM_H;
            scrollBarDrag = 5;
            scrollBarDragStartY = (int) mouseY;
            scrollBarDragStartScroll = selectedScroll;
            selectedScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, selectedItems.size(), visible);
            return true;
        }
        if (mouseX >= selX && mouseX < selX + selPW - SCROLL_BAR_W) {
            List<String> selKeys = new ArrayList<>(selectedItems.keySet());
            for (int i = selectedScroll; i < selKeys.size(); i++) {
                int y = panelTopY + (i - selectedScroll) * ITEM_H;
                if (y >= panelBottomY) break;
                String key = selKeys.get(i);
                // Minus button
                int minusX = selX + selPW - 50 - SCROLL_BAR_W;
                if (mouseX >= minusX && mouseX < minusX + 16 && mouseY >= y && mouseY < y + ITEM_H) {
                    int step = 1;
                    if (shift) step = 64;
                    else if (ctrl) step = 10;
                    int count = selectedItems.get(key) - step;
                    if (count <= 0) selectedItems.remove(key);
                    else selectedItems.put(key, Math.min(64, count));
                    return true;
                }
                // Plus button
                int plusX = selX + selPW - 20 - SCROLL_BAR_W;
                if (mouseX >= plusX && mouseX < plusX + 16 && mouseY >= y && mouseY < y + ITEM_H) {
                    if (shift) selectedItems.put(key, 64);
                    else if (ctrl) selectedItems.put(key, Math.min(64, selectedItems.get(key) + 10));
                    else selectedItems.put(key, Math.min(64, selectedItems.get(key) + 1));
                    return true;
                }
                // Click on name area = remove
                if (mouseX >= selX && mouseX < minusX && mouseY >= y && mouseY < y + ITEM_H) {
                    selectedItems.remove(key);
                    return true;
                }
            }
        }

        // ── Bottom buttons: Confirm / Duplicate / Delete ──
        int confirmY = popupY + popupH - 24;
        int confirmH = 18;
        boolean isIdentityConfig = configPath != null &&
                (configPath.startsWith("identity.") && configPath.contains(".startingItems"));
        boolean isCustomIdentity = configPath != null && configPath.startsWith("identity.custom_");

        int confirmW = 80, dupW = 96, delW = 60;
        int confirmX, dupX = 0, delX = 0;
        if (isCustomIdentity) {
            int totalW = confirmW + dupW + delW + 12;
            int startX = popupX + popupW / 2 - totalW / 2;
            confirmX = startX; dupX = startX + confirmW + 6; delX = dupX + dupW + 6;
        } else if (isIdentityConfig) {
            int totalW = confirmW + dupW + 6;
            int startX = popupX + popupW / 2 - totalW / 2;
            confirmX = startX; dupX = startX + confirmW + 6;
        } else {
            confirmX = popupX + popupW / 2 - confirmW / 2;
        }

        if (mouseX >= confirmX && mouseX < confirmX + confirmW && mouseY >= confirmY && mouseY < confirmY + confirmH) {
            String configValue = buildConfigString();
            if (isCustomIdentity) {
                String customId = configPath.split("\\.")[1];
                updateCustomIdentityItems(customId, configValue);
            } else {
                ConfigValueAccessor.setValueFromString(configPath, configValue);
            }
            close();
            return true;
        }
        // 1.0.3 test build: custom identities are now wired into the in-game origin
        // roster (IdentityDrawSamplerMixin / IdentityDrawScreenMixin), so Duplicate
        // creates a real, selectable custom identity instead of showing a notice.
        if (isIdentityConfig && mouseX >= dupX && mouseX < dupX + dupW && mouseY >= confirmY && mouseY < confirmY + confirmH) {
            duplicateIdentity();
            return true;
        }
        if (isCustomIdentity && mouseX >= delX && mouseX < delX + delW && mouseY >= confirmY && mouseY < confirmY + confirmH) {
            String customId = configPath.split("\\.")[1];
            CustomIdentityManager.removeCustomIdentity(customId);
            close();
            if (deleteCallback != null) deleteCallback.onDelete(customId);
            return true;
        }

        // Resize handle
        int rhSize = 8;
        int rhX = popupX + popupW - rhSize - 1;
        int rhY = popupY + popupH - rhSize - 1;
        if (mouseX >= rhX && mouseX < rhX + rhSize && mouseY >= rhY && mouseY < rhY + rhSize) {
            resizing = true;
            return true;
        }

        return true;
    }

    private static void updateCustomIdentityItems(String customId, String items) {
        try {
            String itemsStr = com.xiaoxiang.configext.config.ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.get();
            if (itemsStr == null) itemsStr = "";
            StringBuilder newStr = new StringBuilder();
            boolean found = false;
            if (!itemsStr.isEmpty()) {
                for (String entry : itemsStr.split("\\|")) {
                    int colonIdx = entry.indexOf(':');
                    if (colonIdx > 0) {
                        String id = entry.substring(0, colonIdx).trim();
                        if (id.equals(customId)) {
                            newStr.append(customId).append(":").append(items);
                            found = true;
                        } else {
                            newStr.append(entry);
                        }
                        newStr.append("|");
                    }
                }
            }
            if (!found) newStr.append(customId).append(":").append(items);
            String result = newStr.toString();
            if (result.endsWith("|")) result = result.substring(0, result.length() - 1);
            com.xiaoxiang.configext.config.ExtendedConfig.IDENTITY_CUSTOM_STARTING_ITEMS.set(result);
        } catch (Exception e) { }
    }

    /** identityId is the identity actually being duplicated - this is the CUSTOM id
     *  (e.g. "custom_academy_student_1234") when duplicating a previously-created custom
     *  identity, so the caller can source that identity's own current display name,
     *  lifespan, and description instead of the ultimate base identity's defaults.
     *  baseIdentityId is always the root, non-custom identity id (portrait/translation
     *  key source), already unwrapped one level via CustomIdentityManager.extractBaseId
     *  when identityId is itself a custom id. */
    public interface DuplicateCallback { void onDuplicate(String identityId, String baseIdentityId, String startingItems); }
    public interface DeleteCallback { void onDelete(String customId); }

    public void setDuplicateCallback(DuplicateCallback callback) { this.duplicateCallback = callback; }
    public void setDeleteCallback(DeleteCallback callback) { this.deleteCallback = callback; }

    private void duplicateIdentity() {
        String[] parts = configPath.split("\\.");
        if (parts.length < 3) return;
        String identityId = parts[1];
        String baseId = identityId;
        if (identityId.startsWith("custom_")) baseId = CustomIdentityManager.extractBaseId(identityId);
        String configValue = buildConfigString();
        close();
        if (duplicateCallback != null) duplicateCallback.onDuplicate(identityId, baseId, configValue);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  MOUSE DRAG / RELEASE / SCROLL
    // ════════════════════════════════════════════════════════════════════════════

    public boolean mouseDragged(double mouseX, double mouseY, int button, int screenWidth, int screenHeight) {
        if (!open || button != 0) return false;
        if (scrollBarDrag > 0) {
            int panelTopY = getPanelTopY();
            int panelBottomY = getPanelBottomY();
            int visible = getPanelHeight() / ITEM_H;
            switch (scrollBarDrag) {
                case 1: { List<String> mods = getModList(); catScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, mods.size(), visible); break; }
                case 2: { List<String> sc = getSubCategories(); subCatScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, sc.size() + 1, visible); break; }
                case 3: { List<String> ssc = getSubSubCategories(); subSubCatScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, ssc.size() + 1, visible); break; }
                case 4: itemScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, filteredItems.size(), visible); break;
                case 5: selectedScroll = calcScrollFromDrag(panelTopY, panelBottomY, (int) mouseY, selectedItems.size(), visible); break;
            }
            return true;
        }
        if (dragging) {
            popupX = (int) Math.max(0, Math.min(screenWidth - popupW, mouseX - dragOffsetX));
            popupY = (int) Math.max(0, Math.min(screenHeight - popupH, mouseY - dragOffsetY));
            return true;
        }
        if (resizing) {
            popupW = Math.max(500, Math.min((int) mouseX - popupX + 4, screenWidth - popupX - 4));
            popupH = Math.max(250, Math.min((int) mouseY - popupY + 4, screenHeight - popupY - 4));
            return true;
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        resizing = false;
        scrollBarDrag = 0;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!open) return false;
        int panelTopY = getPanelTopY();
        int panelBottomY = getPanelBottomY();
        int visible = getPanelHeight() / ITEM_H;

        int catX = getCatX();
        int subCatX = getSubCatX();
        int subSubCatX = showSubSubCatPanel() ? getSubSubCatX() : -1;
        int itemX = getItemX();
        int selX = getSelX();
        int itemW = getItemW();

        // Category panel
        if (mouseX >= catX && mouseX < catX + CAT_W) {
            List<String> mods = getModList();
            int max = Math.max(0, mods.size() - visible);
            if (delta > 0) catScroll = Math.max(0, catScroll - 2);
            else catScroll = Math.min(max, catScroll + 2);
            return true;
        }
        // Sub-category panel
        if (mouseX >= subCatX && mouseX < subCatX + SUBCAT_W) {
            List<String> sc = getSubCategories();
            int max = Math.max(0, sc.size() + 1 - visible);
            if (delta > 0) subCatScroll = Math.max(0, subCatScroll - 2);
            else subCatScroll = Math.min(max, subCatScroll + 2);
            return true;
        }
        // Sub-sub-category panel (conditional)
        if (subSubCatX >= 0 && mouseX >= subSubCatX && mouseX < subSubCatX + SUBSUBCAT_W) {
            List<String> ssc = getSubSubCategories();
            int max = Math.max(0, ssc.size() + 1 - visible);
            if (delta > 0) subSubCatScroll = Math.max(0, subSubCatScroll - 2);
            else subSubCatScroll = Math.min(max, subSubCatScroll + 2);
            return true;
        }
        // Item panel
        if (mouseX >= itemX && mouseX < itemX + itemW) {
            int max = Math.max(0, filteredItems.size() - visible);
            if (delta > 0) itemScroll = Math.max(0, itemScroll - 3);
            else itemScroll = Math.min(max, itemScroll + 3);
            return true;
        }
        // Selected panel
        if (mouseX >= selX && mouseX < selX + SEL_W) {
            int max = Math.max(0, selectedItems.size() - visible);
            if (delta > 0) selectedScroll = Math.max(0, selectedScroll - 2);
            else selectedScroll = Math.min(max, selectedScroll + 2);
            return true;
        }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  KEYBOARD INPUT
    // ════════════════════════════════════════════════════════════════════════════

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open || !searchFocused) return false;
        if (keyCode == 259) { // Backspace
            if (searchCursorPos > 0 && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchCursorPos - 1) + searchQuery.substring(searchCursorPos);
                searchCursorPos--;
                rebuildFilteredItems();
            }
            return true;
        }
        if (keyCode == 261) { // Delete
            if (searchCursorPos < searchQuery.length()) {
                searchQuery = searchQuery.substring(0, searchCursorPos) + searchQuery.substring(searchCursorPos + 1);
                rebuildFilteredItems();
            }
            return true;
        }
        if (keyCode == 263) { searchCursorPos = Math.max(0, searchCursorPos - 1); return true; } // Left
        if (keyCode == 262) { searchCursorPos = Math.min(searchQuery.length(), searchCursorPos + 1); return true; } // Right
        if (keyCode == 268) { searchCursorPos = 0; return true; } // Home
        if (keyCode == 269) { searchCursorPos = searchQuery.length(); return true; } // End
        if (keyCode == 65 && Screen.hasControlDown()) { searchCursorPos = searchQuery.length(); return true; } // Ctrl+A
        if (keyCode == 86 && Screen.hasControlDown()) { // Ctrl+V
            try {
                String clip = net.minecraft.client.Minecraft.getInstance().keyboardHandler.getClipboard();
                if (clip != null) {
                    clip = clip.replaceAll("[\\r\\n]", "");
                    searchQuery = searchQuery.substring(0, searchCursorPos) + clip + searchQuery.substring(searchCursorPos);
                    searchCursorPos += clip.length();
                    rebuildFilteredItems();
                }
            } catch (Throwable ignored) {}
            return true;
        }
        if (keyCode == 256) { searchFocused = false; return true; } // Escape
        if (keyCode == 257 || keyCode == 335) { searchFocused = false; return true; } // Enter
        return true;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (!open || !searchFocused) return false;
        if (codePoint >= 32 && codePoint != 167) {
            searchQuery = searchQuery.substring(0, searchCursorPos) + codePoint + searchQuery.substring(searchCursorPos);
            searchCursorPos++;
            rebuildFilteredItems();
        }
        return true;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  RENDER
    // ════════════════════════════════════════════════════════════════════════════

    public void render(GuiGraphics g, int screenWidth, int screenHeight, int mouseX, int mouseY, net.minecraft.client.gui.Font font) {
        if (!open) return;

        if (popupW == 0) {
            popupW = Math.min(820, screenWidth - 20);
            popupH = Math.min(420, screenHeight - 40);
            popupX = (screenWidth - popupW) / 2;
            popupY = (screenHeight - popupH) / 2;
        }

        // Dark overlay
        g.fill(0, 0, screenWidth, screenHeight, 0xFF000000);

        // Popup background
        g.fill(popupX - 4, popupY - 4, popupX + popupW + 4, popupY + popupH + 4, 0xFF050508);
        g.fill(popupX - 3, popupY - 3, popupX + popupW + 3, popupY + popupH + 3, 0xFF0A0A14);
        g.fill(popupX, popupY, popupX + popupW, popupY + popupH, 0xFF12121C);
        g.renderOutline(popupX, popupY, popupW, popupH, 0xFF5060A0);
        g.renderOutline(popupX - 1, popupY - 1, popupW + 2, popupH + 2, 0xFF303048);

        // Title bar
        g.fill(popupX, popupY, popupX + popupW, popupY + TITLE_BAR_H, 0xFF1A1A2E);
        g.renderOutline(popupX, popupY, popupW, TITLE_BAR_H, 0xFF404060);
        String title = getTitle();
        drawScaledString(g, font, "\u00A7e\u00A7l" + title, popupX + 6, popupY + 7, popupW - 30, 0xFFFFFF);

        // Close button
        int closeX = popupX + popupW - 20;
        int closeY = popupY + 4;
        boolean closeHover = mouseX >= closeX && mouseX < closeX + 16 && mouseY >= closeY && mouseY < closeY + 16;
        g.fill(closeX, closeY, closeX + 16, closeY + 16, closeHover ? 0xFF502020 : 0xFF301018);
        g.renderOutline(closeX, closeY, 16, 16, closeHover ? 0xFFFF6060 : 0xFF804040);
        if (closeHover) g.fill(closeX, closeY, closeX + 16, closeY + 16, 0x30FFFF80);
        g.drawCenteredString(font, "\u00A7cX", closeX + 8, closeY + 4, 0xFFFFFF);

        // ── Search bar ──
        int searchX = getSearchX();
        int searchY = getSearchY();
        int searchW = getSearchW();
        g.fill(searchX, searchY, searchX + searchW, searchY + SEARCH_H, searchFocused ? 0xFF202030 : 0xFF141420);
        g.renderOutline(searchX, searchY, searchW, SEARCH_H, searchFocused ? 0xFF5080C0 : 0xFF303040);
        if (searchQuery.isEmpty() && !searchFocused) {
            g.drawString(font, "\u00A78Search items...", searchX + 4, searchY + 4, 0xFFFFFF);
        } else {
            g.drawString(font, searchQuery, searchX + 4, searchY + 4, 0xFFFFFF);
            if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cx = searchX + 4 + font.width(searchQuery.substring(0, Math.min(searchCursorPos, searchQuery.length())));
                g.fill(cx, searchY + 3, cx + 1, searchY + SEARCH_H - 3, 0xFFE0E0E0);
            }
        }

        int panelTopY = getPanelTopY();
        int panelBottomY = getPanelBottomY();
        int panelH = panelBottomY - panelTopY;
        int visibleItems = panelH / ITEM_H;

        // ── Panel 1: Categories (mods) ──
        int catX = getCatX();
        g.fill(catX - 2, panelTopY - 2, catX + CAT_W + 2, panelBottomY, 0xFF181820);
        g.renderOutline(catX - 2, panelTopY - 2, CAT_W + 4, panelH + 4, 0xFF303040);
        g.drawString(font, "\u00A7bMods", catX, getHeaderRowY(), 0xFFFFFF);

        List<String> mods = getModList();
        catScroll = Math.min(catScroll, Math.max(0, mods.size() - visibleItems));
        for (int i = catScroll; i < mods.size() && (i - catScroll) < visibleItems; i++) {
            String mod = mods.get(i);
            int y = panelTopY + (i - catScroll) * ITEM_H;
            boolean isActive = mod.equals(activeMod);
            boolean isHover = mouseX >= catX && mouseX < catX + CAT_W - SCROLL_BAR_W && mouseY >= y && mouseY < y + ITEM_H;
            if (isHover) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.CATEGORY, i);
            int bg = isActive ? 0xFF153020 : (isHover ? 0xFF2A3848 : 0xFF161620);
            g.fill(catX, y, catX + CAT_W - SCROLL_BAR_W, y + ITEM_H, bg);
            g.renderOutline(catX, y, CAT_W - SCROLL_BAR_W, ITEM_H, isActive ? 0xFF30A040 : (isHover ? 0xFF5080A0 : 0xFF303040));
            if (isHover) g.fill(catX, y, catX + CAT_W - SCROLL_BAR_W, y + ITEM_H, 0x30FFFF80);
            String name = MOD_NAMES.getOrDefault(mod, mod);
            drawScaledString(g, font, name, catX + 2, y + 4, CAT_W - SCROLL_BAR_W - 4, 0xFFFFFF);
        }
        drawScrollBar(g, catX, CAT_W, panelTopY, panelBottomY, catScroll, mods.size(), visibleItems, mouseX, mouseY);

        // ── Panel 2: Sub-categories (hidden in "All" mode) ──
        if (!activeMod.equals("All")) {
        int subCatX = getSubCatX();
        g.fill(subCatX - 2, panelTopY - 2, subCatX + SUBCAT_W + 2, panelBottomY, 0xFF181820);
        g.renderOutline(subCatX - 2, panelTopY - 2, SUBCAT_W + 4, panelH + 4, 0xFF303040);
        g.drawString(font, "\u00A7bSub-Categories", subCatX, getHeaderRowY(), 0xFFFFFF);

        List<String> subCats = getSubCategories();
        int subCatCount = subCats.size() + 1; // +1 for "All"
        subCatScroll = Math.min(subCatScroll, Math.max(0, subCatCount - visibleItems));
        for (int i = subCatScroll; i < subCatCount && (i - subCatScroll) < visibleItems; i++) {
            String subCat = (i == 0) ? "All" : subCats.get(i - 1);
            int y = panelTopY + (i - subCatScroll) * ITEM_H;
            boolean isActive = subCat.equals(activeSubCat);
            boolean isHover = mouseX >= subCatX && mouseX < subCatX + SUBCAT_W - SCROLL_BAR_W && mouseY >= y && mouseY < y + ITEM_H;
            if (isHover) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.SUBCATEGORY, i);
            int bg = isActive ? 0xFF153020 : (isHover ? 0xFF2A3848 : 0xFF161620);
            g.fill(subCatX, y, subCatX + SUBCAT_W - SCROLL_BAR_W, y + ITEM_H, bg);
            g.renderOutline(subCatX, y, SUBCAT_W - SCROLL_BAR_W, ITEM_H, isActive ? 0xFF30A040 : (isHover ? 0xFF5080A0 : 0xFF303040));
            if (isHover) g.fill(subCatX, y, subCatX + SUBCAT_W - SCROLL_BAR_W, y + ITEM_H, 0x30FFFF80);
            String name = SUBCAT_NAMES.getOrDefault(subCat, subCat);
            drawScaledString(g, font, name, subCatX + 2, y + 4, SUBCAT_W - SCROLL_BAR_W - 4, 0xFFFFFF);
        }
        if (subCatCount > 0) {
            drawScrollBar(g, subCatX, SUBCAT_W, panelTopY, panelBottomY, subCatScroll, subCatCount, visibleItems, mouseX, mouseY);
        }
        } // end if (!activeMod.equals("All"))

        // ── Panel 2.5: Sub-sub-categories (conditional) ──
        if (showSubSubCatPanel()) {
            int subSubCatX = getSubSubCatX();
            g.fill(subSubCatX - 2, panelTopY - 2, subSubCatX + SUBSUBCAT_W + 2, panelBottomY, 0xFF181820);
            g.renderOutline(subSubCatX - 2, panelTopY - 2, SUBSUBCAT_W + 4, panelH + 4, 0xFF303040);
            g.drawString(font, "\u00A7b" + SUBCAT_NAMES.getOrDefault(activeSubCat, activeSubCat), subSubCatX, getHeaderRowY(), 0xFFFFFF);

            List<String> subSubCats = getSubSubCategories();
            int subSubCatCount = subSubCats.size() + 1; // +1 for "All"
            subSubCatScroll = Math.min(subSubCatScroll, Math.max(0, subSubCatCount - visibleItems));
            for (int i = subSubCatScroll; i < subSubCatCount && (i - subSubCatScroll) < visibleItems; i++) {
                String subSubCat = (i == 0) ? "All" : subSubCats.get(i - 1);
                int y = panelTopY + (i - subSubCatScroll) * ITEM_H;
                boolean isActive = subSubCat.equals(activeSubSubCat);
                boolean isHover = mouseX >= subSubCatX && mouseX < subSubCatX + SUBSUBCAT_W - SCROLL_BAR_W && mouseY >= y && mouseY < y + ITEM_H;
                if (isHover) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.SUBCATEGORY, i + 1000);
                int bg = isActive ? 0xFF153020 : (isHover ? 0xFF2A3848 : 0xFF161620);
                g.fill(subSubCatX, y, subSubCatX + SUBSUBCAT_W - SCROLL_BAR_W, y + ITEM_H, bg);
                g.renderOutline(subSubCatX, y, SUBSUBCAT_W - SCROLL_BAR_W, ITEM_H, isActive ? 0xFF30A040 : (isHover ? 0xFF5080A0 : 0xFF303040));
                if (isHover) g.fill(subSubCatX, y, subSubCatX + SUBSUBCAT_W - SCROLL_BAR_W, y + ITEM_H, 0x30FFFF80);
                String name = SUBSUBCAT_NAMES.getOrDefault(subSubCat, subSubCat);
                drawScaledString(g, font, name, subSubCatX + 2, y + 4, SUBSUBCAT_W - SCROLL_BAR_W - 4, 0xFFFFFF);
            }
            drawScrollBar(g, subSubCatX, SUBSUBCAT_W, panelTopY, panelBottomY, subSubCatScroll, subSubCatCount, visibleItems, mouseX, mouseY);
        }

        // ── Panel 3: Items ──
        int itemX = getItemX();
        int itemW = getItemW();
        g.fill(itemX - 2, panelTopY - 2, itemX + itemW + 2, panelBottomY, 0xFF181820);
        g.renderOutline(itemX - 2, panelTopY - 2, itemW + 4, panelH + 4, 0xFF303040);
        // Item count label with breadcrumb
        String catLabel = MOD_NAMES.getOrDefault(activeMod, activeMod);
        if (activeMod.equals("All")) catLabel = "All";
        String subLabel = SUBCAT_NAMES.getOrDefault(activeSubCat, activeSubCat);
        String breadcrumb = catLabel + " > " + subLabel;
        if (showSubSubCatPanel() && !activeSubSubCat.equals("All")) {
            breadcrumb += " > " + SUBSUBCAT_NAMES.getOrDefault(activeSubSubCat, activeSubSubCat);
        }
        g.drawString(font, "\u00A7b" + breadcrumb + " (" + filteredItems.size() + ")", itemX, getHeaderRowY(), 0xFFFFFF);

        itemScroll = Math.min(itemScroll, Math.max(0, filteredItems.size() - visibleItems));
        for (int i = itemScroll; i < filteredItems.size() && (i - itemScroll) < visibleItems; i++) {
            String itemId = filteredItems.get(i);
            String fullId = fullItemId(itemId);
            int y = panelTopY + (i - itemScroll) * ITEM_H;
            boolean isSelected = selectedItems.containsKey(fullId);
            boolean isHover = mouseX >= itemX && mouseX < itemX + itemW - SCROLL_BAR_W && mouseY >= y && mouseY < y + ITEM_H;
            if (isHover) {
                HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.ITEM, i);
                hoveredItemId = fullId; // Track for preview panel
            }
            int bg = isSelected ? 0xFF153020 : (isHover ? 0xFF2A3848 : 0xFF161620);
            g.fill(itemX, y, itemX + itemW - SCROLL_BAR_W, y + ITEM_H, bg);
            g.renderOutline(itemX, y, itemW - SCROLL_BAR_W, ITEM_H, isSelected ? 0xFF30A040 : (isHover ? 0xFF5080A0 : 0xFF303040));
            if (isHover) g.fill(itemX, y, itemX + itemW - SCROLL_BAR_W, y + ITEM_H, 0x30FFFF80);
            String name = ITEM_NAMES.getOrDefault(itemId, prettifyName(itemId));
            drawScaledString(g, font, (isSelected ? "\u00A7a\u2713 " : "") + name, itemX + 2, y + 4, itemW - SCROLL_BAR_W - 8, 0xFFFFFF);
        }
        drawScrollBar(g, itemX, itemW, panelTopY, panelBottomY, itemScroll, filteredItems.size(), visibleItems, mouseX, mouseY);

        // ── Panel 3.5: Item Preview ──
        int previewX = getPreviewX();
        int previewW = PREVIEW_W;
        g.fill(previewX - 2, panelTopY - 2, previewX + previewW + 2, panelBottomY, 0xFF181820);
        g.renderOutline(previewX - 2, panelTopY - 2, previewW + 4, panelH + 4, 0xFF303040);
        g.drawString(font, "\u00A7bPreview", previewX, getHeaderRowY(), 0xFFFFFF);
        // Render the hovered/selected item's icon and name
        if (hoveredItemId != null && !hoveredItemId.isEmpty()) {
            try {
                net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.tryParse(hoveredItemId);
                if (rl != null) {
                    net.minecraft.world.item.Item mcItem = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(rl);
                    if (mcItem != null) {
                        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(mcItem, 1);
                        // Render the item icon centered in the preview panel
                        int iconX = previewX + (previewW - 32) / 2;
                        int iconY = panelTopY + 8;
                        g.renderItem(stack, iconX, iconY);
                        // Render item name below the icon (wrapped)
                        String itemName = stack.getHoverName().getString();
                        drawScaledString(g, font, itemName, previewX + 2, iconY + 36, previewW - 4, 0xFFFFFF);
                        // Render item ID below the name
                        String shortId = hoveredItemId.contains(":") ? hoveredItemId.substring(hoveredItemId.indexOf(":") + 1) : hoveredItemId;
                        drawScaledString(g, font, "\u00A77" + shortId, previewX + 2, iconY + 52, previewW - 4, 0xFFA0A0A0);
                        // Render tooltip lines if available
                        try {
                            java.util.List<net.minecraft.network.chat.Component> tooltipLines = stack.getTooltipLines(
                                net.minecraft.client.Minecraft.getInstance().player,
                                net.minecraft.world.item.TooltipFlag.NORMAL);
                            int tooltipY = iconY + 66;
                            for (int t = 0; t < Math.min(6, tooltipLines.size()); t++) {
                                if (tooltipY + 12 > panelBottomY) break;
                                drawScaledString(g, font, tooltipLines.get(t).getString(), previewX + 2, tooltipY, previewW - 4, 0xFFCCCCCC);
                                tooltipY += 12;
                            }
                        } catch (Exception e) { /* tooltip rendering is non-critical */ }
                    }
                }
            } catch (Exception e) { /* preview is non-critical */ }
        } else {
            // No item hovered - show hint
            g.drawCenteredString(font, "\u00A77Hover an", previewX + previewW / 2, panelTopY + 20, 0xFF808080);
            g.drawCenteredString(font, "\u00A77item to", previewX + previewW / 2, panelTopY + 32, 0xFF808080);
            g.drawCenteredString(font, "\u00A77preview", previewX + previewW / 2, panelTopY + 44, 0xFF808080);
            g.drawCenteredString(font, "\u00A77(Double-click", previewX + previewW / 2, panelTopY + 64, 0xFF606060);
            g.drawCenteredString(font, "\u00A77to add)", previewX + previewW / 2, panelTopY + 76, 0xFF606060);
        }

        // ── Panel 4: Selected items ──
        int selX = getSelX();
        int selW = SEL_W;
        g.fill(selX - 2, panelTopY - 2, selX + selW + 2, panelBottomY, 0xFF181820);
        g.renderOutline(selX - 2, panelTopY - 2, selW + 4, panelH + 4, 0xFF303040);
        g.drawString(font, "\u00A7bSelected (" + selectedItems.size() + ")", selX, getHeaderRowY(), 0xFFFFFF);

        List<String> selKeys = new ArrayList<>(selectedItems.keySet());
        selectedScroll = Math.min(selectedScroll, Math.max(0, selKeys.size() - visibleItems));
        for (int i = selectedScroll; i < selKeys.size() && (i - selectedScroll) < visibleItems; i++) {
            String key = selKeys.get(i);
            int count = selectedItems.get(key);
            int y = panelTopY + (i - selectedScroll) * ITEM_H;
            boolean isHover = mouseX >= selX && mouseX < selX + selW - SCROLL_BAR_W && mouseY >= y && mouseY < y + ITEM_H;
            if (isHover) HoverSoundHelper.playHoverSound(HoverSoundHelper.SoundType.SELECTED, i);
            int bg = isHover ? 0xFF2A3848 : 0xFF161620;
            g.fill(selX, y, selX + selW - SCROLL_BAR_W, y + ITEM_H, bg);
            g.renderOutline(selX, y, selW - SCROLL_BAR_W, ITEM_H, isHover ? 0xFF5080A0 : 0xFF303040);
            if (isHover) g.fill(selX, y, selX + selW - SCROLL_BAR_W, y + ITEM_H, 0x30FFFF80);

            String shortName = ITEM_NAMES.getOrDefault(key, prettifyName(key.contains(":") ? key.substring(key.indexOf(":") + 1) : key));
            int nameAreaW = selW - 56 - SCROLL_BAR_W;
            drawScaledString(g, font, shortName, selX + 2, y + 4, nameAreaW, 0xFFFFFF);

            // Minus button
            int minusX = selX + selW - 50 - SCROLL_BAR_W;
            boolean minusHover = mouseX >= minusX && mouseX < minusX + 16 && mouseY >= y && mouseY < y + ITEM_H;
            g.fill(minusX, y, minusX + 16, y + ITEM_H, minusHover ? 0xFF402020 : 0xFF201010);
            g.renderOutline(minusX, y, 16, ITEM_H, 0xFF604040);
            g.drawCenteredString(font, "-", minusX + 8, y + 4, 0xFFFFFF);

            // Count
            g.drawCenteredString(font, String.valueOf(count), minusX + 22, y + 4, 0x55FF55);

            // Plus button
            int plusX = selX + selW - 20 - SCROLL_BAR_W;
            boolean plusHover = mouseX >= plusX && mouseX < plusX + 16 && mouseY >= y && mouseY < y + ITEM_H;
            g.fill(plusX, y, plusX + 16, y + ITEM_H, plusHover ? 0xFF204020 : 0xFF102010);
            g.renderOutline(plusX, y, 16, ITEM_H, 0xFF406040);
            g.drawCenteredString(font, "+", plusX + 8, y + 4, 0xFFFFFF);
        }
        drawScrollBar(g, selX, selW, panelTopY, panelBottomY, selectedScroll, selKeys.size(), visibleItems, mouseX, mouseY);

        // ── Bottom buttons ──
        int confirmY = popupY + popupH - 24;
        int confirmH = 18;
        boolean isIdentityConfig = configPath != null &&
                (configPath.startsWith("identity.") && configPath.contains(".startingItems"));
        boolean isCustomIdentity = configPath != null && configPath.startsWith("identity.custom_");

        int confirmW = 80, dupW = 96, delW = 60;
        int confirmX, dupX = 0, delX = 0;
        if (isCustomIdentity) {
            int totalW = confirmW + dupW + delW + 12;
            int startX = popupX + popupW / 2 - totalW / 2;
            confirmX = startX; dupX = startX + confirmW + 6; delX = dupX + dupW + 6;
        } else if (isIdentityConfig) {
            int totalW = confirmW + dupW + 6;
            int startX = popupX + popupW / 2 - totalW / 2;
            confirmX = startX; dupX = startX + confirmW + 6;
        } else {
            confirmX = popupX + popupW / 2 - confirmW / 2;
        }

        boolean confirmHover = mouseX >= confirmX && mouseX < confirmX + confirmW && mouseY >= confirmY && mouseY < confirmY + confirmH;
        g.fill(confirmX, confirmY, confirmX + confirmW, confirmY + confirmH, confirmHover ? 0xFF204020 : 0xFF102018);
        g.renderOutline(confirmX, confirmY, confirmW, confirmH, confirmHover ? 0xFF40A040 : 0xFF306030);
        if (confirmHover) g.fill(confirmX, confirmY, confirmX + confirmW, confirmY + confirmH, 0x30FFFF80);
        g.drawCenteredString(font, "\u00A7aConfirm", confirmX + confirmW / 2, confirmY + 5, 0xFFFFFF);

        if (isIdentityConfig) {
            boolean dupHover = mouseX >= dupX && mouseX < dupX + dupW && mouseY >= confirmY && mouseY < confirmY + confirmH;
            g.fill(dupX, confirmY, dupX + dupW, confirmY + confirmH, dupHover ? 0xFF204040 : 0xFF102028);
            g.renderOutline(dupX, confirmY, dupW, confirmH, dupHover ? 0xFF40A0A0 : 0xFF306080);
            if (dupHover) g.fill(dupX, confirmY, dupX + dupW, confirmY + confirmH, 0x30FFFF80);
            g.drawCenteredString(font, "\u00A7bDuplicate", dupX + dupW / 2, confirmY + 5, 0xFFFFFF);
        }

        if (isCustomIdentity) {
            boolean delHover = mouseX >= delX && mouseX < delX + delW && mouseY >= confirmY && mouseY < confirmY + confirmH;
            g.fill(delX, confirmY, delX + delW, confirmY + confirmH, delHover ? 0xFF402020 : 0xFF201010);
            g.renderOutline(delX, confirmY, delW, confirmH, delHover ? 0xFFA04040 : 0xFF603030);
            if (delHover) g.fill(delX, confirmY, delX + delW, confirmY + confirmH, 0x30FFFF80);
            g.drawCenteredString(font, "\u00A7cDelete", delX + delW / 2, confirmY + 5, 0xFFFFFF);
        }

        // Resize handle
        int rhSize = 8;
        int rhX = popupX + popupW - rhSize - 1;
        int rhY = popupY + popupH - rhSize - 1;
        boolean rhHover = mouseX >= rhX && mouseX < rhX + rhSize && mouseY >= rhY && mouseY < rhY + rhSize;
        g.fill(rhX, rhY, rhX + rhSize, rhY + rhSize, rhHover ? 0xFF6060A0 : 0xFF303060);
        g.renderOutline(rhX, rhY, rhSize, rhSize, rhHover ? 0xFFA0A0FF : 0xFF6060A0);
        g.fill(rhX + 2, rhY + 5, rhX + 3, rhY + 6, 0xFFA0A0C0);
        g.fill(rhX + 4, rhY + 3, rhX + 5, rhY + 4, 0xFFA0A0C0);
        g.fill(rhX + 4, rhY + 6, rhX + 5, rhY + 7, 0xFFA0A0C0);

        // ── Item tooltip on hover ──
        // Show tooltip for hovered items in the items panel (panel 3), vanilla and modded
        // alike, after a 3-second hover delay and word-wrapped to a fixed width.
        renderHoveredItemTooltip(g, font, mouseX, mouseY, itemX, itemW, panelTopY, panelBottomY);
    }

    /** Render tooltip for the currently hovered item in the items panel. */
    private void renderHoveredItemTooltip(GuiGraphics g, net.minecraft.client.gui.Font font, int mouseX, int mouseY,
                                           int itemX, int itemW, int panelTopY, int panelBottomY) {
        if (!isOpen()) return;
        // Check if mouse is in the items panel
        if (mouseX < itemX || mouseX >= itemX + itemW - SCROLL_BAR_W) { tooltipHoveredItemId = null; return; }
        if (mouseY < panelTopY || mouseY >= panelBottomY) { tooltipHoveredItemId = null; return; }

        // Find which item is hovered
        int hoverIdx = (mouseY - panelTopY) / ITEM_H + itemScroll;
        if (hoverIdx < 0 || hoverIdx >= filteredItems.size()) {
            tooltipHoveredItemId = null;
            return;
        }

        String itemId = filteredItems.get(hoverIdx);
        String fullId = fullItemId(itemId);

        // Tooltips now show for every item, vanilla Minecraft included - only the
        // xiaoxiang-only skip is gone, the hover delay below still applies to all of them.

        // 3-second hover delay ("solidify"), same idea as the config screen's keyword
        // popup: reset the timer whenever the hovered item changes, and don't render
        // anything until the player has actually held still on one item for a while.
        if (!fullId.equals(tooltipHoveredItemId)) {
            tooltipHoveredItemId = fullId;
            tooltipHoverStart = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - tooltipHoverStart < ITEM_TOOLTIP_HOVER_DELAY_MS) return;

        // Try to get the actual Item from the registry
        try {
            net.minecraft.resources.ResourceLocation rl = new net.minecraft.resources.ResourceLocation(fullId);
            if (!net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(rl)) return;
            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
            if (item == null || item == net.minecraft.world.item.Items.AIR) return;

            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
            if (stack.isEmpty()) return;

            // Build tooltip lines, word-wrapped to a fixed max width via Font#split so a long
            // unwrapped lore line (e.g. a full spell description) can't force the tooltip box
            // to span most of the screen - it wraps into several compact lines instead, same
            // as a normal vanilla item tooltip.
            java.util.List<net.minecraft.util.FormattedCharSequence> formattedLines = new java.util.ArrayList<>();
            formattedLines.addAll(font.split(stack.getHoverName(), ITEM_TOOLTIP_MAX_WIDTH));
            formattedLines.addAll(font.split(net.minecraft.network.chat.Component.literal("\u00A78" + fullId), ITEM_TOOLTIP_MAX_WIDTH));
            String modId = fullId.contains(":") ? fullId.substring(0, fullId.indexOf(":")) : "";
            if (!modId.isEmpty()) {
                formattedLines.addAll(font.split(net.minecraft.network.chat.Component.literal("\u00A79Mod: " + modId), ITEM_TOOLTIP_MAX_WIDTH));
            }

            // Try to get tooltip lines safely - some items may crash with null player
            try {
                net.minecraft.world.item.TooltipFlag flag = net.minecraft.world.item.TooltipFlag.Default.NORMAL;
                java.util.List<net.minecraft.network.chat.Component> itemTooltips = stack.getTooltipLines(null, flag);
                for (int i = 1; i < itemTooltips.size() && i < 8; i++) {
                    formattedLines.addAll(font.split(itemTooltips.get(i), ITEM_TOOLTIP_MAX_WIDTH));
                }
            } catch (Throwable ignored) {
                // If getTooltipLines crashes, just use the basic tooltip we already built
            }

            g.renderTooltip(font, formattedLines, mouseX, mouseY);
        } catch (Throwable ignored) {
            // If anything goes wrong, skip the tooltip entirely
        }
    }
}
