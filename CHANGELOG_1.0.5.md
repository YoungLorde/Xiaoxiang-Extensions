# Xiaoxiang Config Extension 1.0.5

## Fixed

- **A "Reset Tab"-adjacent bug: 5 spell-qi-cost-reduction settings did nothing at all.** The `LOW`/`MID`/`HIGH`/`SUPREME`/`IMMORTAL` tiers under Weapons → spellQiCostReductionPercent were correctly coded against the base mod months ago, but the mixin that applies them was never actually registered with the mod loader — so it silently never ran, on any install, since it was written. Editing those 5 settings changed the number in the menu and nothing else. Registered it; it's live now.
- **Several Pills settings had no effect**, because the base mod moved pill effects to its own internal override system at some point and this mod's pill fields were never re-hooked to it. Qi-recovery amounts (5 tiers) and rejuvenation healing/regeneration/absorption values now hook directly into that system.
- **In-game HUD accessibility settings had no effect.** Show/hide HUD, the qi-bar and cultivation-bar colors, the HUD's text color, and the status-bar height were all editable in the menu but never actually reached the HUD itself. All six now apply live.
- **Weapon damage and Bloodthirst Blade multipliers had no effect.** The global weapon damage multiplier, and the Bloodthirst Blade's blood-spell damage bonus / qi-cost reduction / blood-capacity multipliers, are now live.

## Added

- **A "Reset All" button next to "Reset Tab."** Resets every configurable setting in the entire mod back to its original default value (the same default the base Xiaoxiang Cultivation World mod ships with) in one action, instead of having to reset tab by tab. Hold **Shift** while clicking it to confirm — a plain click just shows a reminder, so a stray click can't wipe every customization at once.

## Known limitation

- **Weapon attack speed and a handful of HUD position/size settings still have no effect.** Both are baked into the game at a point too early for a live setting to reach safely; the audit tracking these (`CONFIG_AUDIT.md`, shipped in the project folder, not the mod itself) explains exactly why for each one, and they're still being worked through.
