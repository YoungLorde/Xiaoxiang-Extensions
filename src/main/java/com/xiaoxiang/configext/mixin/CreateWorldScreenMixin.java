package com.xiaoxiang.configext.mixin;
import com.xiaoxiang.configext.PreWorldState;

import com.xiaoxiang.cultivation.client.screen.IdentityDrawScreen;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawDeck;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawSampler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Intercepts the "Create" button to show perk selection then origin selection
 * before world generation.
 *
 * Flow: Create World → Perks (Next) → Origin Selection (Confirm) → World Gen
 *
 * NOTE: PreWorldPerkScreen and PerkSelectionScreen are loaded via reflection to
 * avoid NoClassDefFoundError caused by classloader boundary issues between
 * Minecraft's classloader and the mod's classloader.
 */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    // Pre-world creation state is in PreWorldState (not here, because mixin
    // validation requires static fields to be private)

    @Inject(method = "onCreate", at = @At("HEAD"), cancellable = true)
    private void configExt$showPerkScreenBeforeCreate(CallbackInfo ci) {
        // Bypass flag: when we call onCreate() programmatically from the confirm
        // button handler, don't intercept again (would cause infinite loop)
        if (PreWorldState.bypassOnCreateInterceptor) {
            return;
        }

        LOGGER.info("[XiaoxiangConfigExt] onCreate intercepted - showing perk selection screen");
        ci.cancel();

        CreateWorldScreen self = (CreateWorldScreen) (Object) this;
        Screen screen = (Screen) (Object) self;

        setMaxPerkCount(com.xiaoxiang.configext.config.ExtendedConfig.GOLDEN_FINGER_PERK_COUNT.get());
        clearSelectedPerks();

        // Use reflection to create PreWorldPerkScreen to avoid classloader issues
        try {
            Class<?> perkScreenClass = Class.forName("com.xiaoxiang.configext.client.PreWorldPerkScreen");
            java.lang.reflect.Constructor<?> ctor = perkScreenClass.getConstructor(Screen.class, Runnable.class);
            Object perkScreen = ctor.newInstance(screen, (Runnable) () -> showOriginSelection(screen));
            Method setScreen = Minecraft.class.getMethod("setScreen", Screen.class);
            setScreen.invoke(Minecraft.getInstance(), perkScreen);
        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to create PreWorldPerkScreen via reflection", e);
            // Fallback: proceed directly to origin selection
            showOriginSelection(screen);
        }
    }

    private void showOriginSelection(Screen createWorldScreen) {
        try {
            setPreWorldMode(true);

            IdentityDrawDeck deck = IdentityDrawSampler.sampleNew(new Random());
            IdentityDrawScreen identityScreen = new IdentityDrawScreen(deck);

            PreWorldState.createWorldScreen = createWorldScreen;
            PreWorldState.pendingWorldCreation = true;

            Minecraft.getInstance().setScreen(identityScreen);
            LOGGER.info("[XiaoxiangConfigExt] Showing IdentityDrawScreen for pre-world origin selection");
        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to show IdentityDrawScreen, proceeding with world creation", e);
            setPreWorldMode(false);
            callOnCreate((CreateWorldScreen) (Object) createWorldScreen);
        }
    }

    /** Call onCreate() on the CreateWorldScreen via reflection.
     *  At runtime, the method may be named "onCreate" (dev) or "m_100972_" (production SRG).
     */
    private static void callOnCreate(CreateWorldScreen screen) {
        try {
            PreWorldState.bypassOnCreateInterceptor = true;
            Method onCreate = findMethod(screen.getClass(), "onCreate", "m_100972_");
            if (onCreate != null) {
                onCreate.setAccessible(true);
                onCreate.invoke(screen);
            } else {
                LOGGER.error("[XiaoxiangConfigExt] Could not find onCreate/m_100972_ method on CreateWorldScreen");
            }
        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to call onCreate via reflection", e);
        } finally {
            PreWorldState.bypassOnCreateInterceptor = false;
        }
    }

    /** Set PerkSelectionScreen.maxPerkCount via reflection to avoid classloader issues. */
    private static void setMaxPerkCount(int count) {
        try {
            Class<?> cls = Class.forName("com.xiaoxiang.configext.client.PerkSelectionScreen");
            java.lang.reflect.Field f = cls.getField("maxPerkCount");
            f.set(null, count);
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to set maxPerkCount via reflection", e);
        }
    }

    /** Clear PerkSelectionScreen.selectedPerkIds via reflection. */
    private static void clearSelectedPerks() {
        try {
            Class<?> cls = Class.forName("com.xiaoxiang.configext.client.PerkSelectionScreen");
            java.lang.reflect.Field f = cls.getField("selectedPerkIds");
            Object set = f.get(null);
            if (set instanceof Set) {
                ((Set<?>) set).clear();
            }
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to clear selectedPerkIds via reflection", e);
        }
    }

    /** Set PreWorldPerkScreen.inPreWorldMode via reflection. */
    private static void setPreWorldMode(boolean value) {
        try {
            Class<?> cls = Class.forName("com.xiaoxiang.configext.client.PreWorldPerkScreen");
            java.lang.reflect.Field f = cls.getField("inPreWorldMode");
            f.set(null, value);
        } catch (Exception e) {
            LOGGER.warn("[XiaoxiangConfigExt] Failed to set inPreWorldMode via reflection", e);
        }
    }

    private static Method findMethod(Class<?> clazz, String... names) {
        while (clazz != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                for (String name : names) {
                    if (m.getName().equals(name)) {
                        return m;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
