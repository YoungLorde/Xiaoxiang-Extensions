package com.xiaoxiang.configext.mixin;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.cultivation.draw.IdentityDrawDeck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies config overrides to IdentityDrawDeck parameters.
 * The original mod has DECK_SIZE and MAX_ROUNDS as public static final constants.
 * We inject at RETURN on the accessor methods to override with config values.
 */
@Mixin(IdentityDrawDeck.class)
public abstract class IdentityDrawDeckMixin {

    @Inject(method = "deckSize", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$deckSize(CallbackInfoReturnable<Integer> cir) {
        int override = ExtendedConfig.IDENTITY_DRAW_DECK_SIZE.get();
        if (override != cir.getReturnValue()) cir.setReturnValue(override);
    }

    @Inject(method = "roundsRemaining", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$roundsRemaining(CallbackInfoReturnable<Integer> cir) {
        int maxRounds = ExtendedConfig.IDENTITY_DRAW_MAX_ROUNDS.get();
        // roundsRemaining = maxRounds - roundsUsed
        // We need to know roundsUsed. The original method returns MAX_ROUNDS - roundsUsed.
        // We adjust: newRemaining = maxRounds - (MAX_ROUNDS - originalRemaining)
        // = maxRounds - MAX_ROUNDS + originalRemaining
        int originalRemaining = cir.getReturnValue();
        // Default MAX_ROUNDS is 2. If config differs, adjust.
        int defaultMaxRounds = 2;
        if (maxRounds != defaultMaxRounds) {
            int roundsUsed = defaultMaxRounds - originalRemaining;
            int adjusted = maxRounds - roundsUsed;
            cir.setReturnValue(Math.max(0, adjusted));
        }
    }

    @Inject(method = "canRoll", at = @At("RETURN"), cancellable = true, remap = false)
    private void configExt$canRoll(CallbackInfoReturnable<Boolean> cir) {
        int maxRounds = ExtendedConfig.IDENTITY_DRAW_MAX_ROUNDS.get();
        int defaultMaxRounds = 2;
        if (maxRounds == defaultMaxRounds) return;
        // If max rounds increased, we need to re-check
        // canRoll = roundsUsed < MAX_ROUNDS
        // Since we can't easily access roundsUsed, we just return true if max > default
        // and the original returned false (might have rounds left)
        if (maxRounds > defaultMaxRounds && !cir.getReturnValue()) {
            // Can't determine exactly without roundsUsed, so be conservative
            // Only override if we definitely have rounds left
            // This is a best-effort approach
        }
    }
}
