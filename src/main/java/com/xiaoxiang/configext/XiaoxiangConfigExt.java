package com.xiaoxiang.configext;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.configext.client.PerkNetwork;
import com.xiaoxiang.configext.api.ExpansionConfigRegistry;
import com.xiaoxiang.configext.api.ExpansionDiscovery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(XiaoxiangConfigExt.MOD_ID)
public class XiaoxiangConfigExt {
    public static final String MOD_ID = "xiaoxiang_config_ext";
    public static final String DEPENDENCY_MOD_ID = "xiaoxiang_cultivation";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XiaoxiangConfigExt(FMLJavaModLoadingContext context) {
        // Verify the original mod is present. The mods.toml mandatory dependency
        // already enforces this at loader level, but we log it for clarity.
        if (!ModList.get().isLoaded(DEPENDENCY_MOD_ID)) {
            LOGGER.error("[{}] CRITICAL: The original mod '{}' is not installed! "
                    + "This extension mod requires the Xiaoxiang Cultivation World mod to function. "
                    + "It does nothing on its own. Please install the original mod.",
                    MOD_ID, DEPENDENCY_MOD_ID);
            return;
        }

        // Register the perk network channel
        PerkNetwork.register();

        // Register the config screen's custom UI sound effects (tab hover/click,
        // entry hover, config-open - see UiSounds' class doc)
        com.xiaoxiang.configext.client.UiSounds.register(context.getModEventBus());

        // Register the extended config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExtendedConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ExtendedConfig.CLIENT_SPEC);

        // Register our own config with the expansion registry
        ExpansionConfigRegistry.registerBase(ExtendedConfig.SPEC);

        // Auto-discover expansion mods and register their configs
        // This runs during common setup so all mods are loaded by then
        context.getModEventBus().addListener(this::onCommonSetup);

        // The custom config screen (ConfigScreenHandler extension point) and the
        // crouch-meditation keybind (RegisterKeyMappingsEvent) both reference
        // client-only Forge types. They used to be registered directly and
        // unconditionally right here - which runs on BOTH client and dedicated
        // server - and that is exactly what was crashing the mod on a
        // dedicated server. Both are now registered via
        // @Mod.EventBusSubscriber(value = Dist.CLIENT, ...) classes instead
        // (ClientModEvents and CrouchMeditationKeybind itself), which FML
        // skips entirely on a dedicated server before any client-only class
        // is ever touched. See ClientModEvents' class doc for the full
        // explanation. Nothing needs to happen here for either of them any more.

        // Register event handlers for beast cultivation extension
        MinecraftForge.EVENT_BUS.register(new BeastCultivationExtensionHandler());

        // Register the bar-style HUD overlay
        context.getModEventBus().addListener(this::registerOverlays);

        // Register world load/unload events for per-world overrides
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldUnload);

        LOGGER.info("[{}] Xiaoxiang Config Extension initialized - requires '{}'",
                MOD_ID, DEPENDENCY_MOD_ID);
    }

    private void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            try {
                String worldName = Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
                com.xiaoxiang.configext.client.PerWorldOverrideManager.loadFromConfig(
                    ExtendedConfig.CLIENT_PER_WORLD_OVERRIDES.get());
                com.xiaoxiang.configext.client.PerWorldOverrideManager.onWorldLoad(worldName);
            } catch (Exception e) {
                // May be multiplayer or not yet fully loaded
            }
        }
    }

    private void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            com.xiaoxiang.configext.client.PerWorldOverrideManager.onWorldUnload();
        }
    }

    private void registerOverlays(RegisterGuiOverlaysEvent event) {
        // Custom overlay removed — the original mod's CultivationHud is repositioned
        // to the bottom-left via CultivationHudMixin instead.
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Auto-discover and register any installed expansion mods
        event.enqueueWork(() -> {
            ExpansionDiscovery.discoverAndRegister();
            int expansionCount = ExpansionConfigRegistry.getAll().size() - 1; // minus ourselves
            if (expansionCount > 0) {
                LOGGER.info("[{}] Found {} expansion mod(s) with registered configs.",
                        MOD_ID, expansionCount);
            } else {
                LOGGER.info("[{}] No expansion mods detected. Config system ready for expansions.",
                        MOD_ID);
            }
        });
    }
}
