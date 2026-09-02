# Xiaoxiang Config Extension 1.0.5 – 1.0.9

*Versions 1.0.5, 1.0.6, 1.0.7, and 1.0.8 were internal iterations with no separate published changelog — this note covers everything that changed since 1.0.5, all rolled into this 1.0.9 release.*

## Compatibility — updated for Xiaoxiang Cultivation World 0.1.1479

The base mod was updated from `0.1.1302` to `0.1.1479`, a large upstream release (1,116 classes added, 13 removed, over 1,000 existing classes changed). Every override this mod ships was individually re-verified against the new version, and everything the update actually broke was fixed:

- **`BeastRealm` was deleted from the base mod outright.** Its beast-progression logic moved to a new, larger 12-tier system (`BeastProgressionRules`). Rebuilt this mod's beast-realm override against the new class, and added **4 new config fields** (`beastBodyIntegrationAdvanceCost`, `beastMahayanaAdvanceCost`, `beastTribulationTranscendenceAdvanceCost`, `beastTrueImmortalAdvanceCost`) to cover the 4 additional advanceable realms the new system introduces.
- **Several base-mod methods this mod hooks into were renamed or restructured** by the update: the flight-combat AI's height logic split into separate ranged/melee formulas, a trade-offer method was renamed, a sect-warehouse method was renamed, and two internal lambda references shifted their generated names. All of this mod's overrides that touched these were updated to match; nothing here required a config change on your end.
- **Fixed a subtle bug the update could have caused**: a sect "physical journey" limit check in the base mod picked up an unrelated second use of the same numeric literal nearby (from an unrelated new log message). Left unpatched, setting the physical-journeys-per-sect limit below 3 would have thrown an error. Pinned the override to the correct, single occurrence.
- **Fixed a real compile bug in the scrollable-tooltip feature** (added just before this update, unrelated to it) that the base mod's stricter build caught: the tooltip renderer was calling an overload of `renderTooltip` that doesn't actually exist on this Minecraft version's GUI API. Fixed to use a real, confirmed overload.

**New: this update added an entire new "Talisman" glyph/rune-crafting subsystem** to the base mod (166 new classes) — a whole new spell-crafting system comparable in scope to an existing major feature of the base mod. This mod now has a brand-new **Talisman** config section for it:
- A master **enable/disable toggle** for all talisman overrides.
- **Explosion-radius tuning** (default and max) for explosive talisman effects.
- **Qi-cost overrides for 82 individual talisman glyphs** — nearly every spell/glyph type craftable in the new system (fireball, lightning strike, starfall, and so on), each individually configurable rather than one blanket multiplier.

A further ~25 structural talisman caps (max executable slots, max delay ticks, max pierce count, batch-craft limits, and similar) plus several other brand-new subsystems the update introduced (a new equipment/armor-set-bonus system, a new "Xuan Gui" boss encounter, a new fruit-tree growing block, several new NPC AI/economy systems, and more) have been identified and catalogued for wiring in a future update, but are not yet configurable.

## Fixed — the game failing to load

The two most recent test builds against the updated base mod failed to load entirely. Both root causes are fixed:

- **A shadowed method this mod calls no longer existed under that name.** The base mod renamed and changed the signature of the method used to announce a beast's cultivation-realm advancement. Fixed to target the real, current method.
- **A worldgen biome-gate override's entire method signature changed** in the update (different parameter set). Fixed to match.
- **Two sect-recruitment overrides silently changed from static to instance methods** upstream, which Mixin refuses to apply without a matching handler shape — this is a subtle class of bug where a method's name, parameters, and return type all stay identical and only its "static-ness" changes, so it's easy to miss even when re-checking a signature carefully. Fixed by splitting the affected overrides into matching static/instance pairs.
- **The same static-vs-instance issue was found and pre-emptively fixed in the spirit-plant qi-orb spawning override** before it had a chance to cause the same kind of crash.
- **A record class this mod rebuilds gained a new field upstream** (a lightning-spell multiplier), which broke the constructor call rebuilding it. Fixed to pass the new field through.

Beyond these, this release went through a full systematic re-verification pass: every single override across all 110 of this mod's mixin files (487 individually annotated targets) was cross-checked against the updated base mod's real compiled code in one pass, rather than waiting for each problem to surface as its own crash report. Two more real issues turned up this way and are already fixed above; everything else checked out.

## Fixed — a real (non-crashing) loot bug

The updated base mod quietly removed the "ruined vs. complete" chest distinction this mod's loot-roll-count settings depended on — the two chest variants now use identical, unconditional loot logic. This didn't crash anything (a safety mechanism already in place degrades a missing target to "no effect" instead of an error), but it did mean 7 loot-roll config fields had silently stopped doing anything. 4 of the 7 are now correctly re-wired to the surviving, unified loot logic; the remaining 3 (specific to the now-removed "ruined chest" variant) are documented as permanently retired rather than silently orphaned, since there's no longer any corresponding behavior in the base mod to attach them to.

## Fixed — a UI bug in the starting-items picker

**Long item descriptions in the Starting Items preview panel spilled out of their box and overlapped the Selected panel next to it.** The preview text renderer only ever shrank a single line down to a fixed minimum size, which wasn't enough for longer tooltip lines (full-sentence effect descriptions in particular). It now properly wraps across multiple lines instead, so nothing can spill outside its panel regardless of how long the text is.

## Fixed — a large batch of config settings that were silently doing nothing

A deep, from-scratch verification pass went through the mod's configuration category by category, checking every setting against the base mod's actual compiled behavior rather than trusting that a setting existing in the menu meant it worked. A substantial number turned out to be dead — either never connected to anything, or connected to a base-mod system that no longer matched what the setting's description claimed. All of the following are now genuinely live:

- **Alchemy**: the Alchemy Heart physique's qi-cost and success-chance bonuses.
- **Refining**: the Divine Forge technique's tier-up chance. (Also caught and fixed a mislabeling: the "Heavenly Elixir refining tier-up chance" setting never actually affected refining at all — the base mod only ever used that value for an *alchemy* tier-up bonus for that technique. Rather than silently invent a refining effect that was never real, the setting now wires to the real mechanic it actually controls; its name was left unchanged for compatibility with existing configs.)
- **Foundation & Golden Core Dao**: 24 settings covering bone-age eligibility limits and tribulation wave counts.
- **Physiques**: all 12 physique-bonus settings.
- **Spirit Roots**: the 3 remaining settings, plus a real bug fix — the qi-absorption bonus override previously used a fragile value-guessing heuristic that could misfire (a common-rarity root with an active environment buff could be mistaken for the rarest tier); rewritten to target the exact underlying code directly, removing the ambiguity entirely.
- **Spirit Plants**: a full rewrite. This entire 12-setting category had been shipping as an empty placeholder whose own internal note claimed the fix was "too complex" — that turned out to be incorrect, and it's now fully wired.
- **Spirit Veins**: the 2 remaining settings.
- **NPC Combat**: the 2 remaining settings.
- **Formations**: an entire 47-setting category that existed in the menu but had zero effect, because no code was ever registered to apply it. Now fully wired, and 12 of its default values were corrected to match the base mod's real numbers along the way.
- **Progression**: NPC tribulation death-chance and weakness-duration, the default starting number of gender edits, an NPC lifespan-reserve threshold (default corrected), a mortal-cultivator technique multiplier, and a mislabeling fix for the Foundation/Golden Core "higher route" time estimates (each was quietly pointed at the wrong internal branch; now each points at the real branch matching its own default value).
- **Morality**: the min/max morality bounds. Their defaults were also corrected — they'd been set roughly 1,000x too small (±1,000 instead of the base mod's real ±1,000,000) and had never been live before, so this is a meaningful behavior change if you rely on this setting.
- **Lifespan**: starting bone-age range, aging rate, and the global lifespan multiplier. Fixing the starting bone-age range also fixed a real bug in the custom-identity system, which had been hardcoding its own random bone-age roll in 3 places and ignoring this setting even when it was changed.
- **Qi System**: player qi-attraction radius, and meditation range/efficiency bonuses (4 of roughly 15 settings in this category — the rest are still being worked through).

## Stability — hardened against future base-mod updates

126 individual overrides across 48 files were missing a safety flag that lets an override quietly do nothing if its target ever disappears in a future base-mod update, instead of crashing the entire mod at launch. All 126 are now protected. This doesn't change anything about how the mod behaves today — it only changes what happens if a *future* Xiaoxiang Cultivation World update renames or removes something this mod hooks into: instead of the whole modpack failing to load, only that one specific setting quietly stops applying until it's re-verified and fixed.

## Known limitations

- Weapon attack speed, and a handful of HUD element position/size settings, still have no effect — both are baked into the game too early for a live override to reach safely (unchanged since 1.0.5).
- Furnace slot counts and processing-time settings for Alchemy and Refining are not yet wired — the base mod inlines these values at every point they're used, which needs a more careful, individually-verified fix than a simple field override.
- The new Talisman system's ~25 remaining structural caps, and several other brand-new subsystems the 0.1.1479 update introduced (equipment/armor-set bonuses, the new "Xuan Gui" encounter, the cultivation-fruit growing system, and others), have been catalogued but are not yet configurable — planned for a future update.
- A large portion of the Sects category (roughly 126 settings, the single largest remaining category) has not yet been re-audited against current base-mod behavior.
