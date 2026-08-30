package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.XiaoxiangConfigExt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registers every custom UI sound effect the config screen can play (tab
 * hover, entry hover, tab/subtab click, config-screen open) - the direct
 * answer to the "I would like the tabs to have sounds... theme based
 * sounds... let's do something like that" request.
 *
 * Registration pattern (DeferredRegister.create(IForgeRegistry, String),
 * ITEMS.register("name", supplier), ITEMS.register(modEventBus)) is copied
 * exactly from the base mod's own com.xiaoxiang.cultivation.registry.ModItems
 * (confirmed via javap on the installed xiaoxiang_cultivation jar - the only
 * proven DeferredRegister precedent anywhere in either codebase), swapping
 * ForgeRegistries.ITEMS for ForgeRegistries.SOUND_EVENTS and Item for
 * SoundEvent. XiaoxiangConfigExt's constructor calls {@link #register}.
 *
 * ONE CAVEAT WORTH FLAGGING HONESTLY: SoundEvent.createVariableRangeEvent(rl)
 * below (the standard Forge 1.19.3+/1.20.1 factory for a plain, non-attenuated
 * UI sound event) could NOT be bytecode-verified - no vanilla Minecraft jar
 * exists in this sandbox to javap net.minecraft.sounds.SoundEvent itself, and
 * neither codebase registers any custom sound to cross-reference. It is the
 * standard, widely-documented idiom for this Forge version, but it is the one
 * API call in this entire sound system that rests on general knowledge of the
 * Forge API rather than in-project verification - everything else here
 * (registration mechanics, playback call in UiSoundHelper) is proven.
 *
 * Each sound below is a real converted asset (assets/xiaoxiang_config_ext/
 * sounds/ui/*.ogg, from the .wav samples provided) registered under a bare
 * snake_case id matching both the sounds.json key and the file's own name -
 * one id used everywhere so there's no separate "event name" vs "file name"
 * to keep in sync by hand. SOUND_IDS is the ordered list SoundPresets cycles
 * through; adding a 17th+ sound later means: drop the .ogg in sounds/ui/, add
 * one line to sounds.json, add one line to REGISTER() below and to SOUND_IDS.
 */
public final class UiSounds {

    private UiSounds() {}

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, XiaoxiangConfigExt.MOD_ID);

    public static final RegistryObject<SoundEvent> LASSO_FAST_WHIP = register("lasso_fast_whip");
    public static final RegistryObject<SoundEvent> BONUS_WHIP = register("bonus_whip");
    public static final RegistryObject<SoundEvent> LASER_GAME_WHIP = register("laser_game_whip");
    public static final RegistryObject<SoundEvent> CINEMATIC_LASER_SWOOSH = register("cinematic_laser_swoosh");
    public static final RegistryObject<SoundEvent> CINEMATIC_WHOOSH_TRANSITION = register("cinematic_whoosh_transition");
    public static final RegistryObject<SoundEvent> FLYING_FAST_SWOOSH = register("flying_fast_swoosh");
    public static final RegistryObject<SoundEvent> TAPE_REWIND_TRANSITION = register("tape_rewind_transition");
    public static final RegistryObject<SoundEvent> FUTURISTIC_METAL_SWEEP = register("futuristic_metal_sweep");
    public static final RegistryObject<SoundEvent> DRONE_LOGO_PRESENTATION = register("drone_logo_presentation");
    public static final RegistryObject<SoundEvent> MAGIC_NOTIFICATION_RING = register("magic_notification_ring");
    public static final RegistryObject<SoundEvent> SMALL_ELECTRIC_GLITCH = register("small_electric_glitch");
    public static final RegistryObject<SoundEvent> TOY_TELEPHONE_RING = register("toy_telephone_ring");
    public static final RegistryObject<SoundEvent> OFFICE_TELEPHONE_RING = register("office_telephone_ring");
    public static final RegistryObject<SoundEvent> CAMERA_AUTOFOCUS = register("camera_autofocus");
    public static final RegistryObject<SoundEvent> CAMERA_LENS_SHUTTER = register("camera_lens_shutter");
    public static final RegistryObject<SoundEvent> CAMERA_LONG_SHUTTER = register("camera_long_shutter");

    /**
     * id -> RegistryObject lookup, preserving declaration order, so
     * SoundPresets can cycle next/prev and ThemeSettingsPopup's "None" state
     * (silence) can be represented as simply not being in this map rather
     * than needing its own registered no-op sound event.
     */
    public static final Map<String, RegistryObject<SoundEvent>> BY_ID = new LinkedHashMap<>();

    static {
        BY_ID.put("lasso_fast_whip", LASSO_FAST_WHIP);
        BY_ID.put("bonus_whip", BONUS_WHIP);
        BY_ID.put("laser_game_whip", LASER_GAME_WHIP);
        BY_ID.put("cinematic_laser_swoosh", CINEMATIC_LASER_SWOOSH);
        BY_ID.put("cinematic_whoosh_transition", CINEMATIC_WHOOSH_TRANSITION);
        BY_ID.put("flying_fast_swoosh", FLYING_FAST_SWOOSH);
        BY_ID.put("tape_rewind_transition", TAPE_REWIND_TRANSITION);
        BY_ID.put("futuristic_metal_sweep", FUTURISTIC_METAL_SWEEP);
        BY_ID.put("drone_logo_presentation", DRONE_LOGO_PRESENTATION);
        BY_ID.put("magic_notification_ring", MAGIC_NOTIFICATION_RING);
        BY_ID.put("small_electric_glitch", SMALL_ELECTRIC_GLITCH);
        BY_ID.put("toy_telephone_ring", TOY_TELEPHONE_RING);
        BY_ID.put("office_telephone_ring", OFFICE_TELEPHONE_RING);
        BY_ID.put("camera_autofocus", CAMERA_AUTOFOCUS);
        BY_ID.put("camera_lens_shutter", CAMERA_LENS_SHUTTER);
        BY_ID.put("camera_long_shutter", CAMERA_LONG_SHUTTER);
    }

    private static RegistryObject<SoundEvent> register(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(XiaoxiangConfigExt.MOD_ID, id)));
    }

    /** Call once from the mod constructor, same call shape as the base mod's own ITEMS.register(bus). */
    public static void register(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
