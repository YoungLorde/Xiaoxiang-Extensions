package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.world.Difficulty;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Mixin into CreateWorldScreen$GameTab to remove the vanilla difficulty
 * CycleButton when the config option is enabled.
 *
 * After removal, remaining buttons have their rows adjusted to close
 * the gap, and arrangeElements() is called to re-position everything.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class GameTabMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void configExt$removeDifficultyButton(CallbackInfo ci) {
        try {
            if (ExtendedConfig.CLIENT_REMOVE_VANILLA_DIFFICULTY_BUTTON == null
                    || !ExtendedConfig.CLIENT_REMOVE_VANILLA_DIFFICULTY_BUTTON.get()) {
                return;
            }

            Object gridLayout = getGridLayout(this);
            if (gridLayout == null) return;

            // Get cellInhabitants - the layout wrappers
            List<Object> cellInhabitants = getListField(gridLayout, "cellInhabitants", "f_263660_");
            if (cellInhabitants == null) return;

            // Find the vanilla difficulty CycleButton and its row
            Object toRemove = null;
            int removedRow = -1;

            for (Object inhabitant : cellInhabitants) {
                Object child = extractLayoutElement(inhabitant);
                if (child instanceof CycleButton) {
                    CycleButton<?> cb = (CycleButton<?>) child;
                    try {
                        Object val = cb.getValue();
                        if (val instanceof Difficulty) {
                            toRemove = inhabitant;
                            removedRow = getIntField(inhabitant, "row", "f_263652_");
                            break;
                        }
                    } catch (ClassCastException e) {
                        // Type mismatch in CycleButton - skip this button
                        continue;
                    }
                }
            }

            if (toRemove == null) {
                LOGGER.warn("[XiaoxiangConfigExt] GameTabMixin: No difficulty button found to remove");
                return;
            }

            LOGGER.info("[XiaoxiangConfigExt] GameTabMixin: Removing difficulty button at row {}", removedRow);

            // Remove from cellInhabitants
            cellInhabitants.remove(toRemove);

            // Also try to remove from the 'children' list if it exists separately
            List<Object> childrenList = getListField(gridLayout, "children", "f_263670_");
            if (childrenList != null && childrenList != cellInhabitants) {
                Object childToRemove = extractLayoutElement(toRemove);
                childrenList.removeIf(item -> {
                    if (item == childToRemove) return true;
                    if (item instanceof LayoutElement && item == childToRemove) return true;
                    // Check if item wraps the same child
                    Object inner = extractLayoutElement(item);
                    return inner == childToRemove;
                });
            }

            // Decrement the row of all inhabitants with row > removedRow
            // This closes the gap left by the removed button
            for (Object inhabitant : cellInhabitants) {
                int row = getIntField(inhabitant, "row", "f_263652_");
                if (row > removedRow) {
                    setIntField(inhabitant, row - 1, "row", "f_263652_");
                }
            }

            // Call arrangeElements() to re-position all remaining elements
            callMethod(gridLayout, "arrangeElements", "m_264036_");

            LOGGER.info("[XiaoxiangConfigExt] GameTabMixin: Removed vanilla difficulty button, re-arranged layout");

        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] GameTabMixin: Error removing difficulty button", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  Reflection helpers
    // ════════════════════════════════════════════════════════════════

    private static Object getGridLayout(Object tab) {
        for (String name : new String[]{"layout", "f_267367_"}) {
            Field f = findField(tab.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    return f.get(tab);
                } catch (Exception e) { /* try next */ }
            }
        }
        Class<?> cls = tab.getClass();
        while (cls != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                if (f.getType().getName().contains("GridLayout")) {
                    try {
                        f.setAccessible(true);
                        return f.get(tab);
                    } catch (Exception e) { /* continue */ }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getListField(Object obj, String... names) {
        for (String name : names) {
            Field f = findField(obj.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    return (List<Object>) f.get(obj);
                } catch (Exception e) { /* try next */ }
            }
        }
        return null;
    }

    private static int getIntField(Object obj, String... names) {
        for (String name : names) {
            Field f = findField(obj.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    return f.getInt(obj);
                } catch (Exception e) { /* try next */ }
            }
        }
        return -1;
    }

    private static boolean setIntField(Object obj, int value, String... names) {
        for (String name : names) {
            Field f = findField(obj.getClass(), name);
            if (f != null) {
                try {
                    f.setAccessible(true);
                    f.setInt(obj, value);
                    return true;
                } catch (Exception e) { /* try next */ }
            }
        }
        return false;
    }

    private static void callMethod(Object obj, String... names) {
        for (String name : names) {
            Method m = findMethod(obj.getClass(), name);
            if (m != null) {
                try {
                    m.setAccessible(true);
                    m.invoke(obj);
                    return;
                } catch (Exception e) { /* try next */ }
            }
        }
    }

    private static Object extractLayoutElement(Object inhabitant) {
        if (inhabitant instanceof LayoutElement) return inhabitant;
        // Look in the class itself
        for (Field f : inhabitant.getClass().getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            if (LayoutElement.class.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    return f.get(inhabitant);
                } catch (Exception e) { /* continue */ }
            }
        }
        // Look in superclass
        Class<?> cls = inhabitant.getClass().getSuperclass();
        while (cls != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                if (LayoutElement.class.isAssignableFrom(f.getType())) {
                    try {
                        f.setAccessible(true);
                        return f.get(inhabitant);
                    } catch (Exception e) { /* continue */ }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String... names) {
        while (clazz != null) {
            for (String name : names) {
                try {
                    return clazz.getDeclaredField(name);
                } catch (NoSuchFieldException e) { /* try next */ }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static Method findMethod(Class<?> clazz, String name) {
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 0) {
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
