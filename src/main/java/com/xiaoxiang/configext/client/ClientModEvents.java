package com.xiaoxiang.configext.client;

import com.xiaoxiang.configext.XiaoxiangConfigExt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * CLIENT-ONLY mod-bus event handlers.
 *
 * The @Mod.EventBusSubscriber(value = Dist.CLIENT, ...) annotation below is
 * checked by FML during mod discovery, BEFORE this class is ever
 * classloaded or scanned for @SubscribeEvent methods - on a dedicated
 * server, FML simply skips registering this class as a listener, so the
 * client-only Forge types it references (ConfigScreenHandler,
 * FMLClientSetupEvent) are never touched there at all. Same proven pattern
 * already used by CrouchMeditationKeybind.ClientHandler in this codebase
 * (there on the FORGE bus for a tick event; here on the MOD bus for the
 * client-setup event), just applied to a second, previously-unguarded spot.
 *
 * THIS IS THE FIX for the reported dedicated-server crash ("crashes off
 * rip", "works on single player just fine"). XiaoxiangConfigExt's
 * constructor - which runs unconditionally on BOTH client and dedicated
 * server - used to call, directly and unconditionally:
 *
 *   ModLoadingContext.get().registerExtensionPoint(
 *       ConfigScreenHandler.ConfigScreenFactory.class,
 *       () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new CustomConfigScreen(parent)));
 *
 * ConfigScreenHandler.ConfigScreenFactory.class is a class LITERAL, so
 * simply evaluating that line forces the JVM to resolve
 * net.minecraftforge.client.ConfigScreenHandler$ConfigScreenFactory right
 * then and there, regardless of dist. That class lives in Forge's
 * client-only net.minecraftforge.client package (officially annotated
 * @OnlyIn(Dist.CLIENT) in Forge's own mappings), so Forge's
 * RuntimeDistCleaner strips/blocks it in a dedicated-server environment -
 * resolving it there crashes mod construction immediately, before any
 * world or player is even involved. Singleplayer's integrated server
 * shares the client's own classpath (Dist.CLIENT), so nothing gets
 * stripped there, which is exactly why it "works on single player just
 * fine" and crashes "off rip" on a real dedicated server.
 *
 * NOTE (honesty flag): this diagnosis is grounded directly in the source
 * of XiaoxiangConfigExt.java as it stood before this fix (an unguarded,
 * unconditional call touching a documented client-only Forge class from
 * common-run code) and matches the textbook, extremely well-documented
 * Forge failure mode for exactly this situation. It has NOT been confirmed
 * against an actual dedicated-server crash log/stack trace, since no
 * dedicated server can be run from this sandbox - if the crash persists
 * after this fix, the real crash-report/latest.log from the server would
 * pin down anything this diagnosis missed.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientModEvents {

    private ClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register the custom config screen with search, nested tabs, color themes, and live preview.
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new CustomConfigScreen(parent)));
    }
}
