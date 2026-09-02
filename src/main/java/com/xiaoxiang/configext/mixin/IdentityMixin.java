package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.Identity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.logging.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Overrides identity starting lifespan ranges and starter items with config-driven ones.
 *
 * lifespanRange() fully replaces the vanilla method via @Inject/HEAD/cancellable, so it
 * does not need to match the real method's internal structure to work correctly. Verified
 * anyway (2026-09-01) via javap -p -c -s of Identity.class's lifespanRange() plus
 * Identity$1's $SwitchMap static initializer: the base mod does NOT give every identity an
 * independent range, it groups the 23 Identity constants into 5 shared ranges (14 named
 * identities across 4 buckets, plus a "default" bucket covering the other 9, including
 * FISHERMAN, FARMER, MORTAL_CHILD, FALLEN_NOBLE, SMITH_APPRENTICE, the 3 OUTER_DISCIPLE
 * identities, and FORMATION_APPRENTICE). All 17 ExtendedConfig default pairs were
 * previously guessed and wrong across the board; see the corrected values and full bucket
 * breakdown documented above IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN in ExtendedConfig.java.
 * This mixin keeps FISHERMAN and FARMER on their own dedicated config fields (a legitimate
 * enhancement over vanilla's coarser grouping) and correctly routes the 7 identities with
 * no dedicated field (MORTAL_CHILD, FALLEN_NOBLE, SMITH_APPRENTICE, QINGYUN/WANJIAN/
 * DANDING_OUTER_DISCIPLE, FORMATION_APPRENTICE) to the else-branch default, matching their
 * real vanilla bucket exactly.
 *
 * For starter items: if the player has configured custom starting items for an identity
 * in the config screen, those items are returned instead of the original mod's hardcoded
 * items. If the config is empty, the original items are returned (fallthrough).
 */
@Mixin(Identity.class)
public abstract class IdentityMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "lifespanRange", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$lifespanRange(CallbackInfoReturnable<int[]> cir) {
        if (!ExtendedConfig.ENABLE_IDENTITY_OVERRIDES.get()) {
            return;
        }
        Identity self = (Identity) (Object) this;
        int[] range;
        if (self == Identity.LONE_CULTIVATOR) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_LONE_CULTIVATOR_MAX.get()};
        } else if (self == Identity.MERCHANT_SON) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_MERCHANT_SON_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_MERCHANT_SON_MAX.get()};
        } else if (self == Identity.BANDIT_LEADER) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_BANDIT_LEADER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_BANDIT_LEADER_MAX.get()};
        } else if (self == Identity.HUNTER) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_HUNTER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_HUNTER_MAX.get()};
        } else if (self == Identity.DOCTOR_HEIR) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_DOCTOR_HEIR_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_DOCTOR_HEIR_MAX.get()};
        } else if (self == Identity.HERMIT_DISCIPLE) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_HERMIT_DISCIPLE_MAX.get()};
        } else if (self == Identity.FISHERMAN) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_FISHERMAN_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_FISHERMAN_MAX.get()};
        } else if (self == Identity.FARMER) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_FARMER_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_FARMER_MAX.get()};
        } else if (self == Identity.ABANDONED_INFANT) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_ABANDONED_INFANT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_ABANDONED_INFANT_MAX.get()};
        } else if (self == Identity.GENERAL_SON) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_GENERAL_SON_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_GENERAL_SON_MAX.get()};
        } else if (self == Identity.EXILED_PRINCESS) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_EXILED_PRINCESS_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_EXILED_PRINCESS_MAX.get()};
        } else if (self == Identity.PIRATE) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_PIRATE_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_PIRATE_MAX.get()};
        } else if (self == Identity.BEAST_DESCENDANT) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_BEAST_DESCENDANT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_BEAST_DESCENDANT_MAX.get()};
        } else if (self == Identity.TAOIST) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_TAOIST_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_TAOIST_MAX.get()};
        } else if (self == Identity.MONK) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_MONK_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_MONK_MAX.get()};
        } else if (self == Identity.ACADEMY_STUDENT) {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_ACADEMY_STUDENT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_ACADEMY_STUDENT_MAX.get()};
        } else {
            range = new int[]{ExtendedConfig.IDENTITY_LIFESPAN_DEFAULT_MIN.get(), ExtendedConfig.IDENTITY_LIFESPAN_DEFAULT_MAX.get()};
        }
        cir.setReturnValue(range);
    }

    /**
     * Override starterItems() to return config-driven items when configured.
     * If the config has items for this identity, return those instead of the
     * original mod's hardcoded items. If the config is empty, fall through
     * to the original method (which returns the original mod's items).
     */
    @Inject(method = "starterItems", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$starterItems(CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!ExtendedConfig.ENABLE_IDENTITY_OVERRIDES.get()) {
            return;
        }
        Identity self = (Identity) (Object) this;
        String identityId = self.id();

        // Get the config string for this identity
        String configItems = getConfigStartingItems(identityId);
        if (configItems == null || configItems.isEmpty()) {
            // No config items - fall through to original method
            return;
        }

        // Parse config string into ItemStack list
        List<ItemStack> items = parseItemStacks(configItems);
        if (items == null || items.isEmpty()) {
            // Parsing failed - fall through to original method
            return;
        }

        LOGGER.info("[XiaoxiangConfigExt] Overriding starter items for identity {}: {} items", identityId, items.size());
        cir.setReturnValue(items);
    }

    /**
     * Get the configured starting items string for an identity.
     * Returns null if no items are configured (fall back to original).
     */
    private static String getConfigStartingItems(String identityId) {
        if (identityId == null) return null;
        String id = identityId.toLowerCase();
        String items;
        switch (id) {
            case "lone_cultivator":   items = ExtendedConfig.IDENTITY_STARTING_ITEMS_LONE_CULTIVATOR.get(); break;
            case "merchant_son":      items = ExtendedConfig.IDENTITY_STARTING_ITEMS_MERCHANT_SON.get(); break;
            case "bandit_leader":     items = ExtendedConfig.IDENTITY_STARTING_ITEMS_BANDIT_LEADER.get(); break;
            case "hunter":            items = ExtendedConfig.IDENTITY_STARTING_ITEMS_HUNTER.get(); break;
            case "doctor_heir":       items = ExtendedConfig.IDENTITY_STARTING_ITEMS_DOCTOR_HEIR.get(); break;
            case "hermit_disciple":   items = ExtendedConfig.IDENTITY_STARTING_ITEMS_HERMIT_DISCIPLE.get(); break;
            case "fisherman":         items = ExtendedConfig.IDENTITY_STARTING_ITEMS_FISHERMAN.get(); break;
            case "farmer":            items = ExtendedConfig.IDENTITY_STARTING_ITEMS_FARMER.get(); break;
            case "abandoned_infant":  items = ExtendedConfig.IDENTITY_STARTING_ITEMS_ABANDONED_INFANT.get(); break;
            case "general_son":       items = ExtendedConfig.IDENTITY_STARTING_ITEMS_GENERAL_SON.get(); break;
            case "exiled_princess":   items = ExtendedConfig.IDENTITY_STARTING_ITEMS_EXILED_PRINCESS.get(); break;
            case "pirate":            items = ExtendedConfig.IDENTITY_STARTING_ITEMS_PIRATE.get(); break;
            case "beast_descendant":  items = ExtendedConfig.IDENTITY_STARTING_ITEMS_BEAST_DESCENDANT.get(); break;
            case "taoist":            items = ExtendedConfig.IDENTITY_STARTING_ITEMS_TAOIST.get(); break;
            case "monk":              items = ExtendedConfig.IDENTITY_STARTING_ITEMS_MONK.get(); break;
            case "academy_student":   items = ExtendedConfig.IDENTITY_STARTING_ITEMS_ACADEMY_STUDENT.get(); break;
            default:                  items = ExtendedConfig.IDENTITY_STARTING_ITEMS_DEFAULT.get(); break;
        }
        // Return null if empty (so original items are used)
        if (items == null || items.trim().isEmpty()) return null;
        return items.trim();
    }

    /**
     * Parse a config string "modid:item,count;modid:item,count" into a list of ItemStacks.
     * Returns null if parsing fails or no valid items are found.
     */
    private static List<ItemStack> parseItemStacks(String configStr) {
        if (configStr == null || configStr.isEmpty()) return null;
        List<ItemStack> result = new ArrayList<>();
        for (String entry : configStr.split(";")) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;
            String[] parts = entry.split(",");
            String itemId = parts[0].trim();
            int count = 1;
            if (parts.length > 1) {
                try { count = Integer.parseInt(parts[1].trim()); } catch (NumberFormatException ignored) {}
            }
            try {
                ResourceLocation rl = new ResourceLocation(itemId);
                Item item = ForgeRegistries.ITEMS.getValue(rl);
                if (item != null) {
                    result.add(new ItemStack(item, Math.max(1, count)));
                } else {
                    LOGGER.warn("[XiaoxiangConfigExt] Item not found in registry: {}", itemId);
                }
            } catch (Exception e) {
                LOGGER.warn("[XiaoxiangConfigExt] Failed to parse item '{}': {}", itemId, e.getMessage());
            }
        }
        return result.isEmpty() ? null : result;
    }
}
