package com.xiaoxiang.configext.mixin;
import com.xiaoxiang.configext.PreWorldState;

import com.xiaoxiang.configext.client.AppliedPerkTracker;
import com.xiaoxiang.configext.client.PerkApplier;
import com.xiaoxiang.configext.client.PerkNetwork;
import com.xiaoxiang.configext.client.PerkSelectionScreen;
import com.xiaoxiang.cultivation.client.screen.IdentityDrawScreen;
import com.xiaoxiang.cultivation.cultivation.Identity;
import com.xiaoxiang.cultivation.cultivation.SpiritRoot;
import com.xiaoxiang.cultivation.cultivation.Physique;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;

/**
 * Hooks into the Identity Draw screen.
 *
 * In pre-world mode (before world generation):
 * - Intercepts the confirm button to extract origin selection
 * - Stores it for server-side application after world join
 * - Triggers world creation via CreateWorldScreen.onCreate()
 *
 * In normal in-game mode (reincarnation):
 * - Shows Golden Finger button for perk review/change
 * - When origin is confirmed, sends perk packet to server for item giving
 */
@Mixin(IdentityDrawScreen.class)
public abstract class IdentityDrawScreenMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    // 1.0.3 test build: enabling this (together with IdentityDrawSamplerMixin's copy
    // of this flag and the Duplicate button in ItemPickerPopup) to verify the virtual-
    // slot roster injection end to end before it ships in the named 1.0.4 release.
    // configExt$shiftSelectionWithCustom and configExt$currentCustomIdentity below
    // check this flag; every other hook in this file (selectedIdentity, the render
    // overlay, both confirm-button hooks) is inert unless
    // configExt$currentCustomIdentity() returns non-null. If testing turns up a
    // blocking issue, flip this back to false rather than deleting the work.
    private static final boolean CUSTOM_IDENTITIES_IN_ROSTER_ENABLED = true;

    @Inject(method = "init", at = @At("TAIL"))
    private void configExt$addGoldenFingerButton(CallbackInfo ci) {
        IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;
        Screen screen = (Screen) (Object) self;

        int btnX = 10;
        int btnY = screen.height - 24;
        int btnW = 140;
        int btnH = 20;

        String label = "\u00A76\u00A7oGolden Finger (" +
                PerkSelectionScreen.selectedPerkIds.size() + "/" +
                PerkSelectionScreen.maxPerkCount + ")";

        Button button = Button.builder(
            Component.literal(label),
            btn -> {
                Minecraft mc = Minecraft.getInstance();
                mc.setScreen(new PerkSelectionScreen(screen));
            }
        ).bounds(btnX, btnY, btnW, btnH).build();

        addWidgetViaReflection(screen, button);
    }

    /**
     * Extends identity browsing to include custom identities (created via the item
     * picker's Duplicate button) as extra "virtual" slots appended right after the real
     * Identity enum roster, so scrolling past the last base identity wraps into whatever
     * custom identities the user has created instead of them being invisible/unreachable
     * from this screen. This is the mechanism behind "when I go through scrolling it, I
     * should be able to see the new identities I created added in within."
     *
     * The original's identities field is List&lt;Identity&gt; populated from the fixed
     * Identity.selectableOrigins() (all real Identity enum constants) - custom identities
     * are plain config-driven data, not real enum constants, so they were never part of
     * that list and this screen's browse-through-everything UI never reached them at all
     * (the separate deck/DrawCard system this file's OTHER methods reference is a
     * differently-indexed, differently-sized list keyed by the same identityIndex field
     * only coincidentally, which is why the old custom-identity detection below and in
     * configExt$onConfirmButton was unreliable - see configExt$currentCustomIdentity).
     *
     * Only takes over the identity branch (type == 0); the physique branch is left
     * completely alone. Every reflective access is defensively guarded - on any
     * shape mismatch this simply doesn't cancel, so the original (custom-identity-blind,
     * but never crashing) behavior runs exactly as it did before this method existed.
     */
    @Inject(method = "shiftSelection", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$shiftSelectionWithCustom(int type, int delta, CallbackInfo ci) {
        if (!CUSTOM_IDENTITIES_IN_ROSTER_ENABLED) return;
        if (type != 0) return; // physique branch - untouched, let the original run
        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;

            java.util.List<com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity> customs =
                    com.xiaoxiang.configext.client.CustomIdentityManager.loadAll();
            if (customs.isEmpty()) return; // nothing to add - let the original's normal path run unchanged

            java.lang.reflect.Field identitiesField = findField(self.getClass(), "identities");
            java.lang.reflect.Field indexField = findField(self.getClass(), "identityIndex");
            if (identitiesField == null || indexField == null) return;
            identitiesField.setAccessible(true);
            indexField.setAccessible(true);

            java.util.List<?> identities = (java.util.List<?>) identitiesField.get(self);
            if (identities == null) return;
            int total = identities.size() + customs.size();
            if (total <= 0) return;

            int current = indexField.getInt(self);
            indexField.set(self, configExt$wrap(current + delta, total));

            Method refreshMethod = findMethod(self.getClass(), "refreshStarterPreview");
            if (refreshMethod != null) {
                refreshMethod.setAccessible(true);
                refreshMethod.invoke(self);
            }

            ci.cancel();
        } catch (Exception e) {
            // Reflection/shape mismatch - do not cancel; original runs as before.
        }
    }

    /**
     * When identityIndex (possibly extended by configExt$shiftSelectionWithCustom above)
     * has wrapped past the real Identity roster into "virtual custom identity" territory,
     * the original's own selectedIdentity() would throw IndexOutOfBoundsException trying
     * to index the shorter real list. Returns the custom identity's own BASE identity
     * instead - the same "carrier" identity already used everywhere else in this custom-
     * identity system (CustomIdentityManager, IdentityDrawHandlerMixin) for portrait and
     * translation-key purposes - so every other piece of original code that calls
     * selectedIdentity() (portrait rendering, tooltips, starter-item preview) keeps
     * working off a real, valid Identity exactly as before. Only the ID actually sent to
     * the server needs to differ, which the confirm-button hooks below handle separately
     * via configExt$currentCustomIdentity rather than trusting this carrier value.
     */
    @Inject(method = "selectedIdentity", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$selectedIdentityWithCustom(CallbackInfoReturnable<Identity> cir) {
        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;
            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity custom = configExt$currentCustomIdentity(self);
            if (custom == null) return; // a real base identity - let the original run unchanged
            Identity base = Identity.byId(custom.baseId);
            if (base == null) base = Identity.LONE_CULTIVATOR; // matches the original's own empty-list fallback constant
            cir.setReturnValue(base);
        } catch (Exception e) {
            // fall through to original
        }
    }

    /** value mod size, always non-negative, 0 when size &lt;= 0 - matches the original's
     *  own private static wrap(int,int) exactly (verified via javap disassembly). */
    private static int configExt$wrap(int value, int size) {
        if (size <= 0) return 0;
        int out = value % size;
        if (out < 0) out += size;
        return out;
    }

    /**
     * Single source of truth for "is the screen's current identity selection actually a
     * virtual custom-identity slot, and if so which one" - used by selectedIdentity(),
     * the render overlay, and both confirm-button hooks below, so none of them can ever
     * disagree with each other about what's actually selected. Recomputes directly from
     * identityIndex + identities.size() + the live custom identity list (the same bound
     * configExt$shiftSelectionWithCustom uses), rather than trying to read back through
     * the deck/DrawCard system, which is a differently-ordered, differently-sized list
     * that only coincidentally shares the identityIndex field name and does not actually
     * correspond to what this screen displays via identities/identityIndex.
     * Returns null (meaning "a normal base identity, nothing custom-specific to do") on
     * any failure or when the current selection genuinely isn't a custom identity.
     */
    private static com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity configExt$currentCustomIdentity(IdentityDrawScreen self) {
        if (!CUSTOM_IDENTITIES_IN_ROSTER_ENABLED) return null;
        try {
            java.util.List<com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity> customs =
                    com.xiaoxiang.configext.client.CustomIdentityManager.loadAll();
            if (customs.isEmpty()) return null;

            java.lang.reflect.Field identitiesField = findField(self.getClass(), "identities");
            java.lang.reflect.Field indexField = findField(self.getClass(), "identityIndex");
            if (identitiesField == null || indexField == null) return null;
            identitiesField.setAccessible(true);
            indexField.setAccessible(true);

            java.util.List<?> identities = (java.util.List<?>) identitiesField.get(self);
            if (identities == null) return null;
            int baseCount = identities.size();
            int total = baseCount + customs.size();
            if (total <= 0) return null;

            int idx = configExt$wrap(indexField.getInt(self), total);
            if (idx < baseCount) return null; // a real base identity
            int customIdx = idx - baseCount;
            if (customIdx < 0 || customIdx >= customs.size()) return null;
            return customs.get(customIdx);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Redirects the two Component.translatable(...) calls inside renderIdentityCard that
     * build the name and description shown on the real Identity card, substituting the
     * custom identity's own (free-typed) text when the browsed slot is a virtual custom
     * identity, instead of the carrier base identity's translated name/description.
     *
     * This replaces the old TAIL-of-render overlay approach (previously
     * configExt$renderCustomDescription), which drew a SECOND, separately-positioned box
     * of text approximated from a reflective field-scan for a "Layout"-named instance
     * field. That scan could never succeed: renderIdentityCard(GuiGraphics, Layout, int,
     * int) receives its real, per-frame Layout as a PARAMETER, not a stored field
     * (confirmed via javap disassembly - IdentityDrawScreen$Layout is only ever
     * constructed fresh inside the private layout() method and passed down, never
     * assigned to a field). The overlay therefore always fell back to a guessed
     * on-screen position, producing the reported "horrendous" result: the user's custom
     * text floating in the wrong place while the real card, underneath, simultaneously
     * still showed the carrier identity's own name and description.
     *
     * Redirecting the exact calls that produce that text is a strict improvement: the
     * custom identity's name/description now render through the SAME drawSmallCentered /
     * drawWrappedScaled calls, at the SAME verified pixel offsets, in the SAME style, as
     * every real identity already uses - so there is nothing left to align, and nothing
     * left to duplicate.
     *
     * ordinal is scoped to calls of this exact target method within renderIdentityCard
     * only (verified via javap - the method makes 4 Component.translatable(...) calls
     * total: the "IDENTITY" section label at ordinal 0, the name at ordinal 1, the
     * description at ordinal 2, and the starter-items label at ordinal 3). Only 1 and 2
     * are touched here; the section label and starter-items label are left alone for
     * every selection, custom or not.
     *
     * remap = false with the SRG name (m_237115_) matches this project's established
     * convention for @Redirect/@ModifyArg targets that are genuine vanilla methods
     * (see BloodBerserkEffectMixin) - this call site is Component.translatable itself,
     * not anything owned by the cultivation mod.
     */
    @Redirect(method = "renderIdentityCard",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;m_237115_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 1),
            remap = false, require = 0)
    private MutableComponent configExt$redirectIdentityCardName(String key) {
        if (!CUSTOM_IDENTITIES_IN_ROSTER_ENABLED) return Component.translatable(key);
        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;
            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity custom = configExt$currentCustomIdentity(self);
            if (custom == null) return Component.translatable(key);

            // Just the name - lifespan isn't shown here (or anywhere in the origin-choosing
            // flow at all; it plays no part in the actual choice, same as real identities
            // never show it on this card either).
            String name = custom.displayName != null && !custom.displayName.isEmpty()
                    ? custom.displayName : custom.id;
            return Component.literal(name);
        } catch (Exception e) {
            return Component.translatable(key);
        }
    }

    @Redirect(method = "renderIdentityCard",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;m_237115_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    ordinal = 2),
            remap = false, require = 0)
    private MutableComponent configExt$redirectIdentityCardDescription(String key) {
        if (!CUSTOM_IDENTITIES_IN_ROSTER_ENABLED) return Component.translatable(key);
        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;
            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity custom = configExt$currentCustomIdentity(self);
            if (custom == null) return Component.translatable(key);
            if (custom.description == null || custom.description.isEmpty()) {
                return Component.literal(""); // no user-written description - blank, not the carrier's
            }
            return Component.literal(custom.description);
        } catch (Exception e) {
            return Component.translatable(key);
        }
    }

    /**
     * Intercept the confirm button handler (lambda$init$2).
     * In pre-world mode, extract the origin selection, store it,
     * and trigger world creation instead of sending a network packet.
     */
    @Inject(method = "lambda$init$2", at = @At("HEAD"), cancellable = true, remap = false)
    private void configExt$onConfirmButton(Button button, CallbackInfo ci) {
        // Check if we're in pre-world mode OR if there's no server connection.
        // The original mod's confirm handler tries to send a network packet,
        // which crashes with NPE if there's no connection (during world creation).
        boolean noConnection = Minecraft.getInstance().getConnection() == null;
        if (PreWorldState.pendingWorldCreation || noConnection) {
            LOGGER.info("[XiaoxiangConfigExt] Confirm button intercepted (preWorld={}, noConnection={})",
                    PreWorldState.pendingWorldCreation, noConnection);

            // Cancel the original handler (which sends a network packet)
            ci.cancel();

            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;

            // Extract the selected origin via reflection
            try {
                Method selId = findMethod(self.getClass(), "selectedIdentity");
                Method selRoot = findMethod(self.getClass(), "selectedRoot");
                Method selPhysique = findMethod(self.getClass(), "selectedPhysique");

                if (selId != null && selRoot != null && selPhysique != null) {
                    selId.setAccessible(true);
                    selRoot.setAccessible(true);
                    selPhysique.setAccessible(true);

                    Identity identity = (Identity) selId.invoke(self);
                    SpiritRoot root = (SpiritRoot) selRoot.invoke(self);
                    Physique physique = (Physique) selPhysique.invoke(self);

                    if (identity != null && root != null && physique != null) {
                        // If the currently browsed slot is a virtual custom identity,
                        // use its real ID instead of the carrier base identity's ID that
                        // selectedIdentity() returned above. (Previously this checked
                        // deck.cardAt(identityIndex) against the deck/DrawCard system,
                        // which is a differently-ordered, differently-sized list that
                        // only coincidentally shared the identityIndex field name and did
                        // not actually correspond to what was being browsed here - see
                        // configExt$currentCustomIdentity's doc comment.)
                        String storedIdentityId = identity.id();
                        com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity currentCustom =
                                configExt$currentCustomIdentity(self);
                        if (currentCustom != null) {
                            storedIdentityId = currentCustom.id;
                            LOGGER.info("[XiaoxiangConfigExt] Using custom identity from virtual roster slot: {}", storedIdentityId);
                        }

                        PreWorldState.storedIdentityId = storedIdentityId;
                        PreWorldState.storedSpiritRootId = root.id();
                        PreWorldState.storedPhysiqueId = physique.id();
                        PreWorldState.hasPreWorldOrigin = true;

                        LOGGER.info("[XiaoxiangConfigExt] Stored pre-world origin: identity={}, root={}, physique={}",
                                PreWorldState.storedIdentityId,
                                PreWorldState.storedSpiritRootId,
                                PreWorldState.storedPhysiqueId);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[XiaoxiangConfigExt] Failed to extract origin selection", e);
            }

            // Store perks for post-login application
            PreWorldState.storedPerkIds = new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds);

            // Apply perks to config (in-memory, will be re-applied after login)
            if (!PerkSelectionScreen.selectedPerkIds.isEmpty()) {
                PerkApplier.applyPerks(new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds));
                AppliedPerkTracker.onPerksApplied(new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds));
            }

            // Get the CreateWorldScreen and call onCreate to start world generation
            Screen createWorldScreen = PreWorldState.createWorldScreen;
            if (createWorldScreen instanceof CreateWorldScreen) {
                callOnCreate((CreateWorldScreen) createWorldScreen);
            } else if (noConnection && !PreWorldState.pendingWorldCreation) {
                // No connection and not in pre-world mode - just close the screen.
                // The origin was stored above and will be applied when the player logs in.
                LOGGER.info("[XiaoxiangConfigExt] No CreateWorldScreen and no connection - closing screen");
                Minecraft.getInstance().setScreen(null);
            }

            // Reset state (but keep stored origin/perks for post-login)
            PreWorldState.reset();
        }
    }

    /**
     * Covers the one remaining case configExt$onConfirmButton above does NOT handle:
     * a REAL in-game confirm (server connection present, not pre-world creation) where
     * the currently browsed slot is a virtual custom identity. Left alone, the original
     * method would send the carrier BASE identity's id string to the server (since
     * selectedIdentity() must return a real Identity for the rest of the screen to keep
     * working - see configExt$selectedIdentityWithCustom), and the server has no way to
     * know a custom identity was actually intended: the player would silently receive
     * the base identity's own lifespan/starting items instead of their custom ones.
     * Reimplements exactly what the original's lambda$init$2 does (build and send a
     * ChooseOriginPacket, then close the screen) but with the real custom identity id
     * substituted in, via reflection since ChooseOriginPacket/ModNetwork are internal to
     * the cultivation mod's network package.
     */
    @Inject(method = "lambda$init$2", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$onConfirmButtonCustomLive(Button button, CallbackInfo ci) {
        boolean noConnection = Minecraft.getInstance().getConnection() == null;
        if (PreWorldState.pendingWorldCreation || noConnection) return; // handled by configExt$onConfirmButton above

        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;
            com.xiaoxiang.configext.client.CustomIdentityManager.CustomIdentity customIdentity =
                    configExt$currentCustomIdentity(self);
            if (customIdentity == null) return; // a real base identity - let the original run unchanged

            Method selRoot = findMethod(self.getClass(), "selectedRoot");
            Method selPhysique = findMethod(self.getClass(), "selectedPhysique");
            java.lang.reflect.Field reconfigureField = findField(self.getClass(), "reconfigureMode");
            if (selRoot == null || selPhysique == null || reconfigureField == null) return;
            selRoot.setAccessible(true);
            selPhysique.setAccessible(true);
            reconfigureField.setAccessible(true);

            SpiritRoot root = (SpiritRoot) selRoot.invoke(self);
            Physique physique = (Physique) selPhysique.invoke(self);
            boolean reconfigureMode = reconfigureField.getBoolean(self);
            if (root == null || physique == null) return;

            // Build and send the same packet the original would have sent, but with the
            // real custom identity id in place of the carrier base identity's id -
            // matches ChooseOriginPacket(boolean random, String identityId,
            // String spiritRootId, String physiqueId, boolean reconfigureMode) exactly.
            Class<?> packetClass = Class.forName("com.xiaoxiang.cultivation.network.ChooseOriginPacket");
            java.lang.reflect.Constructor<?> ctor = packetClass.getConstructor(
                    boolean.class, String.class, String.class, String.class, boolean.class);
            Object packet = ctor.newInstance(false, customIdentity.id, root.id(), physique.id(), reconfigureMode);

            Class<?> networkClass = Class.forName("com.xiaoxiang.cultivation.network.ModNetwork");
            java.lang.reflect.Field channelField = networkClass.getField("CHANNEL");
            Object channel = channelField.get(null);
            Method sendToServer = channel.getClass().getMethod("sendToServer", Object.class);
            sendToServer.invoke(channel, packet);

            LOGGER.info("[XiaoxiangConfigExt] Sent ChooseOriginPacket for custom identity: {}", customIdentity.id);

            ci.cancel();
            Screen screen = (Screen) (Object) self;
            screen.onClose();
        } catch (Exception e) {
            LOGGER.error("[XiaoxiangConfigExt] Failed to send custom identity choose-origin packet", e);
            // Do NOT cancel on failure - let the original run and send the (wrong but
            // non-crashing) base-identity packet rather than leaving the button dead.
        }
    }

    /**
     * Also intercept applyCurrentOriginSelection for the in-game case.
     * In normal mode, apply perks and send packet to server.
     */
    @Inject(method = "applyCurrentOriginSelection", at = @At("HEAD"), remap = false)
    private void configExt$onApplyOrigin(CallbackInfo ci) {
        if (!PreWorldState.pendingWorldCreation) {
            // Normal in-game mode: apply perks and send packet to server
            if (!PerkSelectionScreen.selectedPerkIds.isEmpty()) {
                LinkedHashSet<Integer> perkIds = new LinkedHashSet<>(PerkSelectionScreen.selectedPerkIds);
                PerkApplier.applyPerks(perkIds);
                AppliedPerkTracker.onPerksApplied(perkIds);

                PerkNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(),
                        new PerkNetwork.PerkSelectionPacket(perkIds));
            }
        }
    }

    /**
     * The original renderStarterItems(GuiGraphics, x, y, mouseX, mouseY) hardcodes
     * {@code count = Math.min(starterPreview.size(), 4)} and draws a single horizontal
     * row - any items beyond the 4th are silently never shown in this preview UI (though
     * still granted to the player on confirm; verified via javap disassembly of the
     * original mod's compiled class). This override shows every starter item, wrapping
     * into additional rows of 4 as needed, and grows the grid UPWARD from the same
     * bottom-anchored y the original used - so the row(s) needed for items 5+ push up
     * into the space above rather than spilling past the identity card's bottom border,
     * matching the requested "add a slot and push the box upward" behavior. For 4 or
     * fewer items this produces pixel-identical output to the original (1 row, same
     * pitch/anchor), so it's a safe superset, not a behavior change for the common case.
     *
     * drawItemSlot() (the 3-layer bordered-square background behind each item) is a
     * *private* method on the original mod's class, so it can't be called from here
     * without a @Shadow declaration; the three g.fill() colors below are reproduced
     * directly from that method's decompiled bytecode instead. renderItem()/
     * renderItemDecorations() are vanilla public GuiGraphics methods and are called
     * directly. Cancels the original body only once reflection succeeds and there's at
     * least one item to draw, so a reflection failure (e.g. a future obfuscation/field
     * rename) falls back to the original's old 4-item behavior instead of drawing
     * nothing or crashing the screen.
     */
    @Inject(method = "renderStarterItems", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void configExt$renderStarterItemsDynamic(GuiGraphics g, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        try {
            IdentityDrawScreen self = (IdentityDrawScreen) (Object) this;

            java.lang.reflect.Field previewField = findField(self.getClass(), "starterPreview");
            java.lang.reflect.Field hoveredField = findField(self.getClass(), "hoveredStarter");
            if (previewField == null || hoveredField == null) return;
            previewField.setAccessible(true);
            hoveredField.setAccessible(true);

            java.util.List<?> preview = (java.util.List<?>) previewField.get(self);
            if (preview == null) return;
            int count = preview.size();

            // Nothing to draw either way; let the original run its (also-empty) loop
            // rather than special-casing it here.
            if (count <= 0) return;

            // Past this point we commit to fully replacing the original's drawing, so
            // cancel it up front - if something below throws, we get an incomplete grid
            // instead of a doubled/overlapping draw from both this and the original.
            ci.cancel();

            final int PER_ROW = 4;
            final int COL_PITCH = 19;   // matches original: itemX = x + i*19
            final int ROW_PITCH = 20;   // slot is 20px tall (drawItemSlot draws x..x+20)
            int rows = (count + PER_ROW - 1) / PER_ROW;
            int extraRows = Math.max(0, rows - 1);
            int gridTopY = y - extraRows * ROW_PITCH;

            net.minecraft.client.gui.Font font = Minecraft.getInstance().font;
            Object newHovered = null;

            for (int i = 0; i < count; i++) {
                Object stackObj = preview.get(i);
                if (!(stackObj instanceof net.minecraft.world.item.ItemStack)) continue;
                net.minecraft.world.item.ItemStack stack = (net.minecraft.world.item.ItemStack) stackObj;

                int row = i / PER_ROW;
                int col = i % PER_ROW;
                int itemX = x + col * COL_PITCH;
                int itemY = gridTopY + row * ROW_PITCH;

                // Reproduces the original's private drawItemSlot(g, itemX-2, itemY-2):
                // three nested fill()s at the exact colors from its decompiled bytecode.
                int sx = itemX - 2;
                int sy = itemY - 2;
                g.fill(sx, sy, sx + 20, sy + 20, -1722136010);
                g.fill(sx + 1, sy + 1, sx + 19, sy + 19, -1426066220);
                g.fill(sx + 2, sy + 2, sx + 18, sy + 18, 1714168860);

                g.renderItem(stack, itemX, itemY);
                g.renderItemDecorations(font, stack, itemX, itemY);

                if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                    newHovered = stack;
                }
            }

            if (newHovered != null) {
                hoveredField.set(self, newHovered);
            }
        } catch (Exception e) {
            // Reflection/field-shape failure - do NOT cancel further, but ci.cancel()
            // may have already run above; worst case is a partially-drawn grid this
            // frame, never a crash. Silently ignored like the rest of this class's
            // reflection-based hooks.
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

    @SuppressWarnings("unchecked")
    private static void addWidgetViaReflection(Screen screen, Button button) {
        try {
            Method m = findMethod(Screen.class, "addRenderableWidget", "m_142416_");
            if (m != null) {
                m.setAccessible(true);
                m.invoke(screen, button);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private static java.lang.reflect.Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(name)) {
                    return f;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
