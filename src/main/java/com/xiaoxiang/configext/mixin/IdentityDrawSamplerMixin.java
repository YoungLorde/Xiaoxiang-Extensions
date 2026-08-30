package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.client.CustomIdentityManager;
import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.Identity;
import com.xiaoxiang.cultivation.cultivation.SpiritRoot;
import com.xiaoxiang.cultivation.cultivation.draw.DrawCard;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawDeck;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Injects custom identities into the identity draw deck.
 *
 * When the game samples a new deck of identity cards, this mixin adds
 * custom identity cards (created by the user via the Duplicate button)
 * to the deck alongside the original mod's identity cards.
 *
 * Each custom identity gets a DrawCard with its custom ID and a random
 * spirit root. The custom identity's lifespan and starting items are
 * handled by IdentityDrawHandlerMixin when the player selects it.
 */
@Mixin(value = IdentityDrawSampler.class, remap = false)
public abstract class IdentityDrawSamplerMixin {

    // 1.0.3 test build: enabling this (together with IdentityDrawScreenMixin's copy
    // of this flag and the Duplicate button in ItemPickerPopup) to verify the virtual-
    // slot roster injection end to end before it ships in the named 1.0.4 release.
    // If testing turns up a blocking issue, flip this back to false rather than
    // deleting the work.
    private static final boolean CUSTOM_IDENTITIES_IN_ROSTER_ENABLED = true;

    @Inject(method = "sampleNew", at = @At("RETURN"), cancellable = true, remap = false)
    private static void configExt$injectCustomIdentities(Random random, CallbackInfoReturnable<IdentityDrawDeck> cir) {
        if (!CUSTOM_IDENTITIES_IN_ROSTER_ENABLED) return;
        IdentityDrawDeck originalDeck = cir.getReturnValue();
        if (originalDeck == null) return;

        // Load custom identities from config
        List<CustomIdentityManager.CustomIdentity> customIdentities = CustomIdentityManager.loadAll();
        if (customIdentities.isEmpty()) return;

        // Get the original cards
        List<DrawCard> originalCards = originalDeck.cards();
        List<DrawCard> newCards = new ArrayList<>(originalCards);

        // Add custom identity cards
        for (CustomIdentityManager.CustomIdentity ci : customIdentities) {
            // Pick a random spirit root for this card
            String spiritRootId = pickRandomSpiritRoot(random);
            DrawCard card = new DrawCard(ci.id, spiritRootId);
            newCards.add(card);
        }

        // Create a new deck with the combined cards
        // Preserve the original roundsUsed (0 for a new deck)
        IdentityDrawDeck newDeck = new IdentityDrawDeck(newCards, originalDeck.roundsUsed());
        cir.setReturnValue(newDeck);
    }

    /**
     * Pick a random spirit root ID.
     * Uses the original mod's randomSpiritRoot method to get a valid root.
     */
    private static String pickRandomSpiritRoot(Random random) {
        try {
            SpiritRoot root = IdentityDrawSampler.randomSpiritRoot(random);
            return root.id();
        } catch (Exception e) {
            // Fallback to a basic root
            return "heavenly_hidden";
        }
    }

    // SpiritRoot is imported at the top
}
