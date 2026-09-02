# Config Extension — Full Tooltip & Wiring Audit

Started 2026-09-01. This is the tracking doc for the "massive overhaul" pass:
every one of the ~876 config fields in `ExtendedConfig.java` gets its tooltip
verified against what the code actually does, and every field that turns out
to be dead (defined, shown in the config screen, but never actually read by
any mixin/handler) gets wired up for real rather than just re-worded.

Read this file first at the start of every session that continues this work.

**Priority note (added 2026-09-01, later same day):** the user clarified that
Client Config / accessibility (category 30) was the ORIGINAL priority behind
this whole config system — HUD visibility/position/color for players with
eye impairment or light sensitivity, safety-relevant, not cosmetic — and had
been intentionally deferred, not forgotten-forever. That category jumped the
queue ahead of Weapons/Alchemy/etc as a result; see its entry below for what
got done. Resume general category order (Weapons next) after any further
accessibility work is confirmed sufficient.

**Player-reported bug triage (2026-09-01, session after 1.0.6 build-fix):**
the user shifted priority away from the config audit to three concrete
reports from real stress testing: (1) a player-reported "cultivation
requirement decreases across breakthroughs after reincarnation" balance bug,
(2) a UI text-overflow bug near the identity/starter-items description box,
(3) a CurseForge tester report that "sects always being loaded" makes the
mod hard to run on servers. Per explicit instruction these are being worked
one at a time, players-first-then-balance. Standing config-audit scope
(remaining Sects/Loot/Trials/Identity/Qi System/Passive Spells fields,
self-critique rounds 2-4) is paused, not abandoned, behind these three.

**Bug #1 fixed — RealmMixin `configExt$maxQi` only applied
`MAX_QI_GLOBAL_MULTIPLIER` to realms above Qi Refining:** the field's own
tooltip promises "applied to all max qi values", but the code only scaled
the generic Foundation-Building-and-up branch — Mortal, every Qi Refining
substage, and both Immortal tiers were left unscaled. Traced the real
consequence with a small standalone simulation (not fabricated - reproduced
the exact formula from `CultivationProgressionRules.maxCultivation`,
`Realm.maxQi`, and this mixin, verified via javap earlier in the project):
with `globalMultiplier` away from 1.0, the Qi-Refining-Peak -> next-realm
seam can drop instead of rise (e.g. mult=0.1 makes Qi Refining Peak's 400
drop to Foundation Building Early's 150) - exactly the "requirement went
down when I broke through" symptom reported. Confirmed this class of bug
was actually triggered on the user's own save: their
`xiaoxiang_config_ext-common.PRE-QIBUG-BROKEN-backup.toml` (found via the
device bridge in their ATLauncher `XiaoCultivationWorld` instance) has
`globalMultiplier = 0.01` and `mortal = 201200` (the latter is a separate,
wildly out-of-range user-set value unrelated to this bug - flagged to the
user, not something code can validate). The CURRENT live config has
`globalMultiplier` back at the 1.0 default (all realm-section fields at
default), so this exact live save should not currently exhibit the bug, but
the code-level fix is real and needed regardless of what's live right now.
Fix: centralized the multiplier application into
`configExt$applyGlobalQiMult(int)`, called from every branch (Mortal, Qi
Refining, Immortal tiers, and the generic formula) so it always applies
uniformly, matching the tooltip and restoring monotonicity for any
multiplier value. Verified numerically across a wide range of multipliers
(0.01 to 10.0) that the fix produces zero decreases; verified the old code
reproduces a real decrease at the seam for several representative
multiplier values. Also added tooltip warnings on
`REALM_BASE_DELTA_EARLY/MIDDLE/LATE/PEAK` about the (pre-existing, separate)
risk of setting `deltaPeak` above 1300 or the deltas out of increasing
order, which is the other way this class of bug could occur even with
`globalMultiplier` left at 1.0. Stub-compiled clean (also had to correct
`Realm.java`/`SubStage.java`'s stub enum shapes to match the real ones,
verified via javap - `RealmMixin.java` appears to have never actually been
stub-compiled against accurate stubs before this).

**Bug #2 fixed — `IdentityDrawScreenMixin`'s dynamic starter-items grid
could grow up into the identity description text:**
`configExt$renderStarterItemsDynamic` (added in an earlier session to show
more than the original mod's hardcoded 4-item starter preview) grows the
item grid UPWARD, with no bound, from the card's bottom-anchored position
whenever an identity has 5+ configured starter items - and
`identity.*StartingItems` in `ExtendedConfig` is an open-ended, uncapped,
semicolon-separated item list, so any admin generosity there (a very
plausible "balance" tweak) could trigger it. Verified the real collision
geometry via javap against `renderIdentityCard`'s and `layout()`'s actual
bytecode: description text is drawn at `y=cardY+70`, 4 lines, 9px line
height, 0.58 scale (bottom edge `cardY+95`); `identityCardH` is a hardcoded
168, so the starter-items row anchors at `cardY+138` - only 43px of real
headroom. At the original fixed 20px row pitch, a 4th+ row (13+ items)
would already push above that line - exactly the reported "words leaving
the boundaries of their box." Fix: `configExt$renderStarterItemsDynamic`
now reads the screen's real per-frame `Layout` via reflection (it's
private/never a field - same fact already documented for the custom-
identity name/description redirects lower in the file) to get the real
`identityCardY`, and compresses the row pitch (floor 12px) so the grid
never rises above the description's bottom edge. Verified numerically:
fully collision-free through 16 starter items (4 rows) with the real
168px card height; beyond that the 12px floor is hit and a few px of
residual crowding can still occur - documented honestly in the code
rather than papering over it, since a "+N more" indicator would need
Minecraft text-drawing APIs this sandbox has no jar to verify against.
Stub-compiled the ENTIRE file clean (not just the changed method) against
a freshly hand-built stub set for `IdentityDrawScreen`, its `Layout`
record, `Identity`, `SpiritRoot`, and the Minecraft/Forge GUI surface this
file touches (`GuiGraphics`, `Screen`, `Button`, `Component`, `Minecraft`,
etc.) - this file (and `RealmMixin.java`, see bug #1) appear to have never
been stub-compiled before this session; the enum/field-shape stubs used
elsewhere in the project were themselves wrong in places (`Realm`/
`SubStage` had the wrong constants) until this pass corrected them. Note:
the Minecraft/Forge GUI type stubs (`GuiGraphics`, `Button`, `Screen`, etc.)
are reconstructed from usage, not verified against real Mojang bytecode
(this sandbox has no Minecraft/Forge jars) - flagged honestly, unlike the
mod's own classes which were all verified via javap against the real jar.

**Bug #3 triaged — CurseForge tester report "unbalanced and hard to run on
servers because of the sects always being loaded":** this one had no single
smoking-gun defect the way bugs #1 and #2 did, so it was investigated as
three separate questions rather than one fix. Traced all three of
`ExtendedConfig`'s sect-radius/chunk fields
(`SECT_FULL_SIMULATION_PLAYER_RADIUS`, `SECT_PERFORMANCE_PLAYER_RADIUS`,
`SECT_JOURNEY_CHUNK_RADIUS`) against the real `SectSavedData.class` and
`SectJourneyChunkTickets.class` bytecode via javap (including a `javap -v`
pass to read `ConstantValue` attributes for fields never referenced via
`getstatic`, which plain `javap -c` silently omits):

1. **`SECT_FULL_SIMULATION_PLAYER_RADIUS` is dead code, confirmed in the base
   mod itself, not just unwired here.** The real constant it was meant to
   track, `SectSavedData.FULL_SIMULATION_PLAYER_RADIUS = 192` (read via
   `javap -v`'s `ConstantValue` attribute — it's never `getstatic`'d so
   plain disassembly shows nothing), has **zero consumers anywhere** in the
   compiled base-mod jar: searched every class under `cultivation/entity`,
   `cultivation/event`, `cultivation/cultivation/sect`, and
   `cultivation/worldgen` for the literal `192` and found no use. This isn't
   a "full sect simulation only runs near players" toggle that got
   disconnected — the base mod appears to have defined the constant and
   never wired it to anything itself. Nothing here for this config mod to
   safely hook (a mixin against a nonexistent call site would be
   fabrication), so this field is left unwired and its tooltip now says so
   explicitly, including the fact that its old default of 64 never even
   matched the real constant's 192 — a sign it was set speculatively and
   never checked. **This is the honest, most likely core of the "always
   loaded" impression**: if a player believed lowering this radius would
   cut sect simulation cost near them, it can't, because it was never doing
   anything in the first place.

2. **`SECT_PERFORMANCE_PLAYER_RADIUS` was real but only partially reachable
   — now fully wired.** `SectSavedData` gates 7 separate `hasNearbyPlayer(
   level, npcPos, 48.0)` checks behind this exact literal, each skipping a
   *visible* in-world "performance" (pathfinding, animation sequence, corpse
   spawn) for a sect member's death, recruitment, expedition-return, or
   tribulation event when no player is within radius. Verified each of the
   7 sites individually against the real bytecode (not assumed from method
   naming) across 6 distinct private methods: `enqueuePerformance`,
   `queueExpeditionReturnPerformance` (2 occurrences, same method — one
   `@ModifyConstant` with no `ordinal` correctly covers both since they
   should track the same radius), `queueRecruitBReturnPerformance`,
   `startTerminalDeathPerformance`, `signalLoadedCultivationProgress`, and
   `tryRecruitLoadedLoneCultivator`. Added one `@ModifyConstant` per method
   in `SectSavedDataMixin.java` (6 total), each gated behind
   `ENABLE_SECT_OVERRIDES` per the file's existing convention. This is a
   real, safe lever: it does not touch the underlying sect simulation
   (breakthroughs, tasks, deaths, or `SectJourneyChunkTickets`
   chunk-loading for journeying members all keep running via the base
   mod's own distant-catch-up system regardless) — only whether a given
   event is worth spending entity/pathfinding/animation work on because a
   player is actually close enough to see it. A server admin lowering this
   now has a real effect for the first time.

3. **`SECT_JOURNEY_CHUNK_RADIUS` investigated, deliberately left
   unwired.** The real `SectJourneyChunkTickets.RADIUS = 1` is baked into
   raw loop-bound bytecode (`iconst_m1`/`iconst_1`), not a clean
   `getstatic` a mixin can safely retarget without risking silently wrong
   chunk math. Read the full disassembly of `move()` (called from
   `updateJourneyTicket`, 3 call sites) and `release()` (called from
   `releaseJourneyTickets`, 6 call sites) — more release sites than move
   sites, which is a sign of a deliberately-designed cleanup lifecycle
   rather than an obvious leak. `setForced` calls through to the real
   `ForgeChunkManager.forceChunk(...)` API correctly. Given this project's
   established caution around chunk-loading correctness (same reasoning
   that led to leaving `DistanceManagerMixin` alone earlier), this was not
   touched — the risk of a subtle forced-chunk leak or premature release
   bug outweighs the config-field completionism. Tooltip updated to say so
   honestly, including that its default of 2 doesn't match the real value
   of 1 either.

**Overall conclusion for the user:** the CurseForge tester's "always
loaded" complaint is most likely explained by finding #1 above — a
config knob that looked like it should reduce sect-simulation cost near
players but never did anything — combined with the base mod's sects being
an inherently always-on simulation across however many sects exist in a
world (not a single leak this config mod can patch away). Finding #2 is
the one real, verified lever now available and wired. Recommend the user
suggest the tester profile with a tool like Spark before further blind
optimization, since "always loaded" without a profile could equally mean
"correctly always-simulating, just costs more than expected with N
sects," which is a base-mod design question rather than a config-wiring
bug.

**Standing monotonicity guarantee added (2026-09-01, following the three-bug
triage above):** the user asked for more than a one-off fix to Bug #1 - a
permanent guarantee that every subsequent breakthrough always requires
strictly more cultivation than every previous one, standardized, regardless
of how any individual config field is set, with minor (sub-stage) steps
staying smaller than major (realm) steps. Added a new `[realms.monotonicity]`
config section (`enableMonotonicBreakthroughs` default true,
`minMinorIncrease` default 50, `minMajorIncrease` default 300) and a
substantial rewrite of `RealmMixin.java`'s `maxQi` handling:

- `configExt$rawMaxQi` now always computes a value regardless of
  `enableRealmOverrides` - config-driven when overrides are on, or an exact
  hand-reproduction of the vanilla formula (verified via javap) when they're
  off, so monotonicity can still be enforced even with per-field overrides
  disabled.
- `configExt$applyMonotonicClamp` walks the real breakthrough order (Mortal
  -> Qi Refining E/M/L/P -> Foundation Building E/M/L/P -> ... -> Tribulation
  Transcendence E/M/L/P, verified via javap against
  `CultivationProgressionRules.nextAfterSuccess`) and ONLY repairs a step
  when it would otherwise fail to increase over the previous (already
  clamped) step - repairing to `previous + minIncrease` (major or minor as
  appropriate). Caught and fixed a real bug in my own first draft here via a
  standalone Python simulation before shipping: an earlier version applied
  the minimum increase as an unconditional floor on every step, which would
  have silently altered already-correct default values (e.g. shoving Qi
  Refining Early from 200 up to 400). The shipped version is a pure repair-
  on-violation safety net, verified numerically to be a complete no-op
  against every default value.
- True Immortal and Loose Immortal are deliberately NOT chained linearly
  against EACH OTHER: verified via javap that both are excluded from the
  standard `canBreakthrough()`/`nextAfterSuccess()` progression and reached
  instead through a separate tribulation-count promotion mechanic
  (`LOOSE_IMMORTAL_BASE_REDUCTION_PERCENT` / `LOOSE_IMMORTAL_FULL_REDUCTION_
  TRIBULATIONS`) - real evidence this is an intentional branch (e.g. a
  lesser "loose" ascension vs. a full "true" one), not a simple continuation
  of the realm ordinal sequence. Forcing one above the other would be
  guessing at design intent, not fixing a verified bug, so each is only
  independently floored at Tribulation Transcendence Peak + one major
  increase - which does still catch the real, verified base-mod bug where
  vanilla's own hardcoded values flat-return True Immortal=20900 then Loose
  Immortal=18000 (a built-in decrease) when overrides are off.

**Newly discovered bug, found and fixed while re-verifying the above (2026-
09-01):** re-deriving `Realm.maxQi()`'s real formula directly from fresh
`javap -p -c -s` output (not from any prior session's notes) turned up a
second, independent, previously-undiscovered bug: the existing Qi Refining
max-qi defaults (`qiRefiningEarly/Middle/Late/Peak` = 100/200/300/400,
dated 2026-08-23 and described then as "fixed... to match the original
exactly") were themselves WRONG. The real bytecode computes
base(100)+delta(100/200/300/400) = **200/300/400/500**, not the flat
100/200/300/400 that shipped. That earlier "fix" was actually a regression:
with the wrong defaults, Mortal(100) -> Qi Refining Early(100) was a
plateau - the SAME value, zero increase - at the very first breakthrough in
the game, a milder version of the exact "requirement didn't go up" symptom
this whole triage exists to fix. Corrected the four defaults to
200/300/400/500 in `ExtendedConfig.java`, updated the comment to document
the correction plainly, and (since the user's own live
`xiaoxiang_config_ext-common.toml` had these fields at exactly the old
buggy default, confirming they'd never customized them - checked directly
via the device bridge) corrected the same four values in the user's live
world config so the fix actually takes effect there too, not just in future
installs. This is exactly the kind of thing the new monotonicity clamp
above is meant to catch even if a fix like this is ever wrong again in the
future - though in this specific case the plateau was a same-value tie, not
a decrease, so it's worth noting the clamp's "only repair on a
non-increase" rule (`raw > prevClamped`) does cover ties as well as
decreases.

**Bug #4 fixed - reincarnation not re-triggering after the first death
(2026-09-01):** player-reported: "died, respawned, reincarnated, chose an
origin again, died a second time, and nothing happened - no underworld
prompt at all, just a normal death." Root-caused via javap:
`SoulStateHandler.onLivingDeath` gates the entire Difu/reincarnation flow
behind `CultivationData.hasEquippedTechnique()`; `ReincarnationManager.
doReincarnate` wipes cultivation state via `copyFrom(new CultivationData())`
(which zeroes `equippedTechniqueId`) and never restores it afterward -
unlike its sibling `doReturnIntact`, which calls `TechniqueLoadoutHelper.
normalizeForCurrentState` to restore a real technique loadout after its own
state reset (verified that method really does call `setEquippedTechniqueId`
internally). So after a full reincarnation, `equippedTechniqueId` stays
blank until the player manually equips a technique, silently blocking Difu
on every subsequent death. This is a real asymmetry bug in the base mod
itself, not something either config mod introduced. Fixed via a new
`ReincarnationManagerMixin.java`, injecting at the TAIL of `doReincarnate`
to call the exact same `normalizeForCurrentState` + `notifyNormalization`
pair `doReturnIntact` already calls, gated behind a new
`enableReincarnationTechniqueFix` toggle (default true) under a new
`[reincarnation]` config section. Registered in `xiaoxiang_config_ext.
mixins.json` (a new mixin class silently never loads without this - caught
and fixed before delivery). One caveat flagged honestly: the vanilla
`ServerPlayer.getRandom()` call this fix relies on is standard, extremely
well-established Mojang-mapped API, but - unlike every other claim in this
entry - is NOT bytecode-verified in this sandbox (no Minecraft/Forge jars
available to javap against), the same limitation already documented for
`IdentityDrawScreenMixin`'s GUI-side stubs.

**Note on the separate "Xiaoxiang Realm Expansion" mod:** the user also
asked for a monotonic-breakthrough guarantee and a "layers" fix in what
turned out to be a THIRD, separate mod project (`XiaoxiangRealmExpansion`,
mod ID `xiaoxiang_realm_expansion_10layer` - "layers" is a real, distinct
concept there, not a mis-hearing of "sub-stages"). That work is tracked
separately and paused at the user's explicit request to finish this config
mod ("flagship") batch first.

**Bug #5 fixed - starter-item and physique tooltips could overflow the
screen (2026-09-01, same player report as Bug #2's starter-items grid):**
hovering a starter item or the physique icon on the Identity Draw screen
shows a normal Minecraft tooltip (verified via javap:
`GuiGraphics.m_280153_`/`renderTooltip(Font, ItemStack, int, int)` for
items, `GuiGraphics.m_280666_`/`renderTooltip(Font, List<Component>, int,
int)` for physique, the latter confirmed by `buildPhysiqueTooltip`'s own
declared `List<Component>` return type feeding directly into it) with no
height limit - a long description grows the box tall enough to cover the
rest of the screen. Rather than truncating, made it scrollable: added
`[identityScreenTooltips]` config (`enableScrollableItemTooltips` default
true, `maxVisibleLines` default 8). When a tooltip's line count exceeds the
limit, only a window of lines plus a "scroll for more" hint renders,
re-using the exact same vanilla tooltip call each time with a shorter list
(no custom clipping/scissor code needed at all). Mouse wheel shifts the
window via a new `mouseScrolled` override - confirmed via javap that
`IdentityDrawScreen` doesn't declare one itself, so this is a genuinely new
override Mixin merges in, not an injection into existing code, and it only
ever consumes the scroll event while an overflowing tooltip is showing;
otherwise it returns false, reproducing exactly the screen's current
behavior (no override at all) for every other interaction.

**Risk disclosure for Bug #5, worth flagging explicitly:** every other fix
this session was verified against this mod's own compiled bytecode via
javap. The two `renderTooltip` call sites this fix redirects into ARE
verified that way. But `ItemStack.getTooltipLines(Player, TooltipFlag)` and
`TooltipFlag.Default.NORMAL` - used to fetch a starter item's own tooltip
lines - are standard, well-established Minecraft 1.20.1 API from general
modding knowledge, NOT bytecode-verified, because no vanilla Minecraft/
Forge jar exists in this sandbox to check them against (only the base
mod's own compiled classes are available here). Every custom code path is
wrapped in try/catch falling back to the unmodified vanilla call on any
failure, so a wrong guess should surface as a compile error at the user's
real Gradle build (not a runtime crash) - if `IdentityDrawScreenMixin.java`
fails to compile, the exact javac error will pin down precisely which call
needs correcting, and should be reported back rather than guessed at
again blind.

**Sect shop pricing fixed - a real "config silently ignored" bug
(2026-09-01, later still):** started the Sects audit (the largest remaining
gap, ~126 fields) with the shop-pricing section (15 fields) and found the
existing `SectShopPricingMixin` was fundamentally broken, not just
incomplete. Verified via `javap -p -c -s` against `SectShopPricing.class`:
`basePrice(Item)` calls a private `tierPrice(ItemTier, low, mid, high,
supreme, immortal)` from 4 call sites (technique/spell/weapon/storage_bag),
each with its own hardcoded 5-int tuple exactly matching this mod's
technique/spell/weapon config defaults. The prior mixin injected at
`tierPrice`'s RETURN and derived a single scale ratio from ONE field
(`SECT_SHOP_TECHNIQUE_PRICE_LOW` vs. the hardcoded 100) and applied that ONE
ratio to every call — meaning 14 of the 15 configured price fields did
nothing at all, and spells/weapons could never be priced independently of
techniques. Replaced it: a `@Redirect` on all 4 `tierPrice` call sites
identifies which category is being priced by matching the incoming 5-int
tuple, swaps in the matching config fields, and calls back into the real
`tierPrice` (via `@Shadow`) so vanilla's own tier-selection logic still
runs unmodified rather than being reimplemented and risking a wrong guess.
Also fixed `sellPercent`: `quote()`'s sell-price math (`buyContribution *
60 / 100`) has the `60` baked in as a compile-time-inlined constant
(confirmed via `javap -v`: `SELL_PERCENT` carries a `ConstantValue`
attribute, so there is no `getstatic` anywhere to redirect) — wired via
`@ModifyConstant` instead, this project's established pattern for that
class of constant. `storage_bag` pricing has no corresponding config
section in `ExtendedConfig` at all (a real, separate, smaller gap) — left
passing through unmodified rather than mis-pricing it against the wrong
category. ~58 more `SECT_*` fields remain unresearched; this was one
well-scoped slice of the larger Sects backlog, not the whole thing.

**Sects audit continued - disciple gates, department shifts, overhead panel
fade, task market timing, ancestor profile (2026-09-01, later still):**
picked up where shop pricing left off, working class by class through the
remaining ~58 unwired `SECT_*` fields. This pass:

- **`SectDiscipleGateMixin` (new)** — wires `SECT_DISCIPLE_REALM_GATE` /
  `SECT_DISCIPLE_SUB_STAGE_GATE`. Both are enum-typed (`Realm`/`SubStage`)
  static fields on `SectSavedData`, so - unlike primitives - they are NOT
  compile-time-inlined, and 4 separate methods read them directly via
  `getstatic` (the core `meetsDiscipleRealmGate(Realm, SubStage)` gate
  check, `createDataLayerRecruit` when spawning a new disciple at the gate
  threshold, and two recruit-selection lambdas). Redirected all 4 read
  sites via `@At("FIELD")` to a config-built `Realm`/`SubStage` (config
  stores plain ordinals, clamped to the real enum's bounds). This is the
  first use of a `FIELD`-type Mixin redirect in this project (all prior
  fixes were `INVOKE`-based `@Redirect` or `@ModifyConstant`) - the
  mechanism itself is well-established, widely-documented Mixin/ASM API,
  flagged honestly as not bytecode-verifiable against the real Mixin/ASM
  library in this sandbox (only this mod's own compiled classes are
  available to javap here), same caveat tier as other vanilla-API guesses
  elsewhere in this audit.
- **`SectDepartmentShiftMixin` (new)** — wires all 4
  `SECT_DEPARTMENT_*_SHIFT_*` fields via `departmentShiftScheduled(UUID,
  long)`. Found the config defaults (6000/12000/12000/18000) never matched
  the real values (1000/6000/6000/11000) - corrected. `FIRST_SHIFT_END` and
  `SECOND_SHIFT_START` share the literal 6000 in the same method;
  disambiguated with `ordinal`.
- **`SectOverheadPanelFadeMixin` (new, client)** — wires
  `SECT_OVERHEAD_PANEL_FADE_IN_TICKS` / `FADE_OUT_TICKS` via the client-only
  `SectOverheadPanelVisibilityClient.onClientTick()`, which steps
  `currentAlpha` by a fixed `+-step` per tick (0.2f in = 5 ticks, 0.125f out
  = 8 ticks). Config defaults (10/10) corrected to 5/8. Distinct from the
  pre-existing `SectOverheadPanelRulesMixin`, which wires the server-safe
  distance/render fields on a different class.
- **`SectTaskMarketMixin` (new)** — wires `SECT_TASK_EXPEDITION_MIN_DAYS` /
  `MAX_DAYS` and `SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS` / `MAX_TIMEOUT_TICKS`
  via `expeditionWorkDays()` / `journeyTimeoutTicks()`. Corrected 3 default
  mismatches found along the way (`maxEscrowStacks` 16→8,
  `journeyMinTimeoutTicks` 1200→12000, `journeyMaxTimeoutTicks` 4800→36000).
  `SECT_TASK_MAX_REQUIRED_COUNT`/`MAX_ESCROW_STACKS`/`MAX_SYSTEM_PURCHASE_
  TASKS` all match real fields on `SectTaskMarketRules` but none of their
  literal values are consumed anywhere inside that class itself (so some
  other class, most likely `SectSavedData`, reads them) - that caller
  wasn't isolated this session, so those 3 stay unwired. `SECT_TASK_MAX_
  SYSTEM_PURCHASES` is a genuine duplicate of `SECT_TASK_MAX_SYSTEM_
  PURCHASE_TASKS` (identical comment, identical default, but only one real
  field exists) - left unwired, same treatment as other duplicates found
  in this audit.
- **`SectAncestorProfileMixin` (new)** — wires `SECT_PROFILE_MAX_POWER_
  SCORE`, `SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN`, the 5 `SECT_ANCESTOR_
  IMMORTAL_CHANCE_POWER_*` fields, and `SECT_ANCESTOR_LOOSE_IMMORTAL_
  CHANCE`, all on `GeneratedSectCultivationProfile`. Corrected the
  `maxOrdinaryRoleSpan` default (was 365, real value is 20). Found and
  documented, rather than guessed around, two real problems instead of
  wiring them blind:
    - `SECT_PROFILE_MIN_CULTIVATOR_PROGRESS` / `MAX_MORTAL_REALM_PROGRESS`
      / `MIN_MASTER_PROGRESS` are `DoubleValue` (0.0-1.0, described as
      fractions), but their real backing fields are raw `int`
      `Realm.progressIndex()` values with no natural 0-1 range. Wiring
      these would require inventing a fraction-to-int mapping the original
      mod never specified - left unwired rather than fabricated.
    - `SECT_MAX_POWER_SCORE` (a separate, older field) is a duplicate of
      `SECT_PROFILE_MAX_POWER_SCORE` - identical title, identical default;
      only the profile copy maps to a real field.
    - `SECT_PROFILE_MIN_ORDINARY_ROLE_SPAN`'s real counterpart (7) was not
      confidently isolated: the literal 7 does appear once nearby, but as
      an unrelated per-role rank index, not a role-span duration - left
      unwired rather than mis-target the wrong constant.

Net this pass: 7 more fields cleanly wired (disciple gates ×2, department
shifts ×4 minus the ones already counted... - see the individual mixins for
the authoritative per-field list), several genuine default-mismatch bugs
corrected regardless of wiring status (found while verifying, fixed for
honesty even where wiring itself was out of reach), and 3 duplicate/
type-mismatched fields documented rather than guessed at. Roughly 40+
`SECT_*` fields remain: ambient timing (`SECT_AMBIENT_CHECK_INTERVAL_
TICKS`/`MAX_SPECTATORS`/`NPC_COOLDOWN_TICKS` - confirmed to exist on
`SectAmbientInteractionRules` but their consuming call site lives somewhere
in the 48,000-line `SectSavedData` class and wasn't isolated this session),
defense responder limits, distant catch-up day cap, event limit, journey
timing (`SECT_JOURNEY_DATA_PHASE_TICKS` etc.), life-tick interval, max
physical journeys per sect, member/warehouse inventory slot limits,
recruit-chance fields (`RECRUIT_DISCIPLE_CHANCE`=0.3 real vs 0.1
configured - a genuine default mismatch found but not corrected since its
own use site is similarly unconfirmed), and the bubble min/max duration
fields (already investigated and honestly left unwired by a prior pass in
this same session - see the class doc on `SectNpcOverheadBubbleRulesMixin`).

**Registration bug caught and fixed - `SectDepartmentShiftMixin` was inert
(2026-09-01, later still):** `SectDepartmentShiftMixin` (documented above)
had been written, compiled clean, and delivered, but the line adding it to
`xiaoxiang_config_ext.mixins.json`'s `"mixins"` array never actually landed
in the file - meaning it would never have been loaded by Mixin at runtime,
so all 4 `SECT_DEPARTMENT_*_SHIFT_*` fields (and the corrected defaults
that go with them) would have silently done nothing. Caught by re-reading
the file rather than trusting the earlier edit, and fixed by adding the
missing `"SectDepartmentShiftMixin",` entry. This is exactly the kind of
"looks wired, isn't actually wired" failure the no-placeholders directive
is meant to catch, so it's called out here explicitly rather than folded
quietly into the fix.

**Sects audit continued further - life-tick interval, defense response
(2026-09-01, later still):**

- **`SectLifeTickIntervalMixin` (new)** — wires `SECT_LIFE_TICK_INTERVAL`.
  `SectSavedData.tickSectLife(MinecraftServer)` gates all sect-life
  processing behind `level.getGameTime() % 1200L == 0L` before calling the
  instance overload with a hardcoded `0.05d` "days progressed per
  tick-event". The `1200L` is the inlined form of `SectSavedData`'s own
  `SECT_LIFE_TICK_INTERVAL` field (confirmed via `javap -v`'s constant pool:
  `ConstantValue: int 1200`) - config's default of 20 was wrong and has been
  corrected to 1200. Important documented side effect: the `0.05d` days-per-
  event value has no config field of its own, so lowering the interval
  makes sect life progress proportionally *faster* than real time (more
  tick-events per Minecraft day, same days-per-event), and raising it slows
  progression down - this is an inherent consequence of the base mod's
  design, not something the mixin silently corrects, and it's documented in
  the mixin's class doc so it isn't a surprise later.
- **`SectDefenseMixin` (new)** — wires `SECT_DEFENSE_CRITICAL_HEALTH_RATIO`
  only, via a `0.25d` literal comparison in `handleSectMemberAttacked`
  (health-ratio ≤ 0.25 escalates to `handleSectMemberEmergency`). The other
  3 fields in this section were investigated and found genuinely
  unwireable, not just unresearched:
    - `SECT_DEFENSE_ESCAPE_RADIUS` - the real `DEFENSE_ESCAPE_RADIUS` field
      has zero `getstatic` references anywhere in the jar. The nearby AABB
      search-box inflation that coincidentally also uses `32.0` reads from
      two entirely separate, non-config-backed private fields instead
      (`DEFENSE_RESPONDER_SEARCH_RADIUS`/`_VERTICAL`).
    - `SECT_DEFENSE_CRITICAL_RESPONDER_LIMIT` (5) / `SECT_DEFENSE_DEATH_
      RESPONDER_LIMIT` (8) - their real fields are likewise never read
      anywhere. The actual responder cap traces to
      `DefenseIncident.create(...)`'s 4th argument, hardcoded at its one
      call site to a literal `2` or `3` (not either config field), which
      `create()` then clamps *again* to `max(1, min(3, arg))`. Even a
      successful redirect of the configured 5/8 would be immediately
      clamped down to 3 by this double clamp - wiring it would create the
      illusion of a working setting that can never actually exceed 3. Left
      documented and unwired rather than faked.

Net this pass: 2 more fields cleanly wired, 1 previously-invisible
registration bug fixed, and 3 more fields confirmed-dead-end and documented
(bringing the honestly-investigated-but-unwireable total for Sects to 7:
3 progress-fraction type mismatches, 2 duplicates, 1 unlocated role-span
field, and now these). Remaining backlog is unchanged from the list above
minus life-tick interval and defense/critical-health-ratio.

**Sects audit continued further still - operational limits (2026-09-01,
later still):**

- **`SectRecordEventLimitMixin` (new)** — wires `SECT_EVENT_LIMIT` via
  `SectRecord.addEvent()`, which trims the oldest events once the list
  exceeds a literal 128. Config default (100) corrected to 128.
- **`SectOperationsLimitsMixin` (new)** — wires 5 fields on `SectSavedData`
  itself:
    - `SECT_WAREHOUSE_SLOT_LIMIT` via `trimWarehouse()` (literal 324, used
      twice for the same concept - one handler covers both). Default
      corrected 54→324.
    - `SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT` via
      `insertPersonalStackWithOverflow()` (literal 18). Default corrected
      27→18.
    - `SECT_DISTANT_CATCH_UP_DAY_CAP` via `catchUpDistantSect()` (literal
      300L, used three times for the same concept). Default corrected
      7→300 - the biggest single mismatch found so far in this audit.
    - `SECT_PERFORMANCE_QUEUE_LIMIT` via `enqueuePerformance()` (literal 8).
      Config's default already matched exactly - wired as-is.
    - `SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT` via `admitPhysicalJourney()`
      (literal 3, `physicalJourneyCount(sect) < 3` gate). Default corrected
      2→3.
  `SECT_PERFORMANCE_TIMEOUT_TICKS` was investigated but not wired:
  `SectPerformanceRequest` carries no start-time/deadline field at all, and
  no dedicated timeout-checking method or entity-side goal class was
  located - left unresearched rather than guessed at.
  `SECT_MIN_SPACING_PER_TIER` was also investigated and deprioritized: its
  likely home, `SectSettlementFeature` (worldgen sect placement), disassembles
  to 16,800+ lines across 30+ nested classes, and a search of the outer
  class's own bytecode for a spacing/min-distance concept came back empty -
  same "costly needle-in-haystack" treatment as the `SECT_AMBIENT_*` fields.

Net this pass: 6 more fields cleanly wired (1 on `SectRecord`, 5 on
`SectSavedData`), 4 more default-mismatch bugs corrected (one of them,
`distantCatchUpDayCap` 7→300, the largest single miss found in the whole
Sects audit), and 2 more fields honestly documented as unwired (timeout
field with no consumer located, spacing field deprioritized as too costly
to search blind). Running honestly-investigated-but-unwireable total for
Sects: 9.

**Sects audit continued further still - journey timing, recruit chance
(2026-09-01, later still):**

- **`SectJourneyTimingMixin` (new)** — wires all 6 remaining `SECT_JOURNEY_*`
  timing fields on `SectSavedData`, traced by following each literal's
  surrounding bytecode (getfield/lsub/lcmp) back to the specific
  `JourneyState` timestamp it's measured against, since several of these
  methods share the exact same literal value and value alone wasn't enough
  to tell them apart:
    - `stuckTicks` via `updateJourneyProgress()` (600L against
      `lastProgressGameTime`). Corrected 200→600.
    - `returnFallbackTicks` via `runJourneyDataLayerPass()` (1200L against
      `returnStartedGameTime`). Already matched (1200).
    - `entityMissingGraceTicks` via the same method (200L against
      `missingSinceGameTime`). Corrected 100→200.
    - `dataPhaseTicks` via the same method again (100L against
      `unobservedSinceGameTime`, gating `demoteJourneyToDataLayer`).
      Corrected 20→100.
    - `queueFallbackTicks` via the same method a fourth time (36000L against
      `startedGameTime`, gating a force-return of a data-layer-stalled
      journey). Already matched (36000).
    - `entityReloadWaitTicks` via `tryRehydrateJourneyActor()` (100L against
      `actorReloadRequestedGameTime`, gating an NPC respawn) - a *different*
      method from `dataPhaseTicks` above, so the shared 100L value doesn't
      collide; method-scoping keeps the two handlers separate with no
      ordinal needed.
  `runJourneyDataLayerPass` alone needed 4 separate handlers for 4 distinct
  literal values in one method - the largest single-method fan-out of any
  mixin in this audit so far.
- **`SectRecruitChanceMixin` (new)** — wires `SECT_RECRUIT_DISCIPLE_CHANCE`.
  Resolves a mismatch this audit flagged earlier in the session but
  couldn't confirm a use site for at the time (`RECRUIT_DISCIPLE_CHANCE`=0.3
  real vs 0.1 configured). The literal 0.3d turned out to be the *only*
  occurrence of that value anywhere in `SectSavedData.class`, inside the
  6-argument `createDataLayerRecruit(...)` overload - the same method
  `SectDiscipleGateMixin` already touches for the realm-gate field redirect
  (a different instruction, so the two mixins coexist fine).
  `random.nextDouble() < 0.3` decides whether a data-layer recruit becomes
  an `OUTER_DISCIPLE` or a `SERVANT`. Corrected 0.1→0.3.

Net this pass: 7 more fields cleanly wired (bringing the Sects section from
"partially wired" to essentially complete for the fields that have real,
locatable consumers), 5 more default-mismatch bugs corrected. Remaining
Sects gaps are now down to the ones already documented as genuinely
unwireable or deprioritized: `SECT_PERFORMANCE_TIMEOUT_TICKS` (no consumer
located), `SECT_MIN_SPACING_PER_TIER` (worldgen, deprioritized), the
`SECT_AMBIENT_*` trio (deprioritized), the 3 `SECT_PROFILE_*` progress
fractions (type mismatch), 2 duplicate fields, 1 unlocated role-span field,
and the 3 `SECT_DEFENSE_*` fields confirmed dead/double-clamped.

**Sects audit continued further still - ambient interactions, closing out
the deprioritized fields (2026-09-01, later still):**

Went back to the 3 `SECT_AMBIENT_*` fields deprioritized earlier this
session as a "costly needle-in-haystack" - the field-tracing technique
developed for the journey-timing pass (following each literal's
surrounding getfield/lsub/lcmp back to a specific concept) made this
tractable after all:

- **`SectAmbientCheckIntervalMixin` (new)** — wires
  `SECT_AMBIENT_CHECK_INTERVAL_TICKS` via `tickDailyLife(MinecraftServer)`,
  which gates its whole body behind `Math.floorMod(gameTime, 20) == 0`.
  Corrected 100→20. Documented side effect: this same gate also controls
  `tickSectISystems`, an unrelated subsystem call made in the same guarded
  block - the base mod doesn't separate the two, so this setting isn't
  purely "ambient" despite its name.
- **Real discovery: 3 of the "unwired ambient" fields were never actually
  missing a consumer - they were duplicates.** `SECT_AMBIENT_MAX_SCENES` /
  `MIN_COOLDOWN_TICKS` / `MAX_COOLDOWN_TICKS` (the original "ambient"
  section) turned out to be exact duplicates - same titles, same defaults -
  of `SECT_AMBIENT_MAX_ACTIVE_SCENES_PER_LEVEL` / `MIN_SECT_COOLDOWN_TICKS`
  / `MAX_SECT_COOLDOWN_TICKS`, which a pre-existing `SectAmbientInteractionRulesMixin`
  from earlier in this session already wires correctly (`canStart()`'s
  scene-count gate and `nextSectCooldown()`'s `1800 + bounded(mix, 2401)`
  formula). All 3 duplicates now carry `NOTE:` comments rather than reading
  as silently broken.
- `SECT_AMBIENT_MAX_SPECTATORS`'s real consumer was found
  (`addSparringSpectators()`), but it builds a fixed 4-element hardcoded
  offset array (N/E/S/W standing positions), not a simple int comparison -
  making it configurable would mean generating a ring of N positions
  instead of 4 hardcoded ones, a full algorithm rewrite outside a
  config-wiring pass's scope. Default (4) already matches the array size.
- `SECT_AMBIENT_NPC_COOLDOWN_TICKS`'s literal (3600) does not appear
  anywhere in `SectSavedData.class` - real consumer not located. Default
  (3600) already matches the field's own declared value regardless.

Net this pass: 1 more field cleanly wired, 1 default mismatch corrected,
3 previously-mysterious "unwired" fields resolved to duplicates (not
missing consumers), and the remaining 2 given full documented traces
instead of a blanket "deprioritized" note. This closes out the last item
on the original "40+ fields remain" list from earlier in the Sects audit -
everything with a locatable, wireable real consumer in `SectSavedData`/
`SectAmbientInteractionRules` is now wired.

**Trials audit - Loot confirmed already complete, real bugs found and fixed
in Inner World Trial wiring (2026-09-01, later still):**

Checked the Loot section first: all 15 `LOOT_*` fields were already fully
wired (`CultivationChestLootMixin`, from earlier in this session) with
defaults already matching real values exactly - nothing to do there.

Trials turned up two real, silent bugs in `InnerWorldTrialManagerMixin`
(also written earlier this session) while re-verifying it against a full
disassembly instead of the single-method spot-check it was originally
written against:

- **`TRIAL_INNER_WORLD_TIME_STASIS_DURATION` was wired to the wrong class
  and silently did nothing.** The mixin targeted `InnerWorldTrialManager`,
  but that class has no `TIME_STASIS_DURATION`-shaped field at all, and the
  literal 600 never appears in its bytecode - `require = 0` meant the
  `@ModifyConstant` handler just quietly never fired. The real field
  (`DURATION_TICKS` = 600) lives on `TimeStasisHandler`, a general-purpose
  ability handler used well beyond trials. Moved to a new
  `TimeStasisDurationMixin` targeting the right class, covering all 4 real
  call sites (`onChargeStarted`/`castSingleOrRelease`/
  `releaseStoppedEntity`/`castDomain`). Documented that this setting isn't
  actually trial-exclusive despite its name, since the bytecode doesn't
  support narrowing it to trials only.
- **`TRIAL_INNER_WORLD_PLATFORM_Y` was only 1/13 wired.** The literal
  (`double 80.0d`, the inlined form of the real `int PLATFORM_Y = 80`
  field) appears at 13 separate call sites across platform-build/boundary/
  reset/teleport methods, but the mixin only redirected the one inside
  `begin()`. A configured custom platform height would have built the
  platform at the new height while every boundary check and re-teleport
  elsewhere kept using the hardcoded 80 - a real in-game inconsistency.
  Verified all 13 sites are genuinely platform-related (checked every
  enclosing method name) before widening the redirect to `method = "*"`.
- Added the previously-missing `TRIAL_INNER_WORLD_FAILURE_HEALTH_PENALTY_
  PERCENT` (found in `failTrial()`: `setHealth(max(1.0f, maxHealth *
  0.5f))`). Config default (0.5) already matched.

All 12 `GOLDEN_CORE_DAO_*_SHATTER_TRIAL_*` fields and all 5 `TRIAL_HEART_
DEMON_*` fields were confirmed already fully wired (via `GoldenCoreDaoMixin`
and `HeartDemonTrialProfileMixin`, both from earlier in this session) -
Trials is now completely wired, with the two silent bugs above resolved.

**Identity per-identity lifespan audit - every one of the 17 default pairs
was wrong (2026-09-01, later still):**

`IdentityMixin.lifespanRange()` (from earlier in this session) already
fully replaces `Identity.lifespanRange()` via `@Inject(at = "HEAD",
cancellable = true)`, so functionally it was never "unwired" - the
structural coverage was correct going in: all 16 identities with dedicated
config fields (`LONE_CULTIVATOR`, `MERCHANT_SON`, `BANDIT_LEADER`,
`HUNTER`, `DOCTOR_HEIR`, `HERMIT_DISCIPLE`, `FISHERMAN`, `FARMER`,
`ABANDONED_INFANT`, `GENERAL_SON`, `EXILED_PRINCESS`, `PIRATE`,
`BEAST_DESCENDANT`, `TAOIST`, `MONK`, `ACADEMY_STUDENT`) are matched by
identity constant, and the 7 identities with no dedicated field
(`MORTAL_CHILD`, `FALLEN_NOBLE`, `SMITH_APPRENTICE`, `QINGYUN_OUTER_
DISCIPLE`, `WANJIAN_OUTER_DISCIPLE`, `DANDING_OUTER_DISCIPLE`,
`FORMATION_APPRENTICE`) correctly fall through to `IDENTITY_LIFESPAN_
DEFAULT_MIN/MAX` via the else-branch.

But re-verifying the *default values themselves* against a full `javap -p
-c -s` disassembly of `Identity.class`'s `lifespanRange()` plus
`Identity$1`'s `$SwitchMap` static initializer (the compiled form of the
real mod's switch-on-enum-constant statement) turned up a much bigger
problem: every single one of the 17 `IDENTITY_LIFESPAN_*_MIN/MAX` default
pairs (34 fields total) was wrong. The real method doesn't give each
identity an independent range at all - it groups all 23 `Identity`
constants into 5 shared buckets:

- `{60, 85}` - `EXILED_PRINCESS`, `MERCHANT_SON`, `ACADEMY_STUDENT`, `TAOIST`
- `{90, 110}` - `BEAST_DESCENDANT`, `GENERAL_SON`, `HUNTER`, `BANDIT_LEADER`, `PIRATE`
- `{80, 105}` - `HERMIT_DISCIPLE`, `MONK`, `DOCTOR_HEIR`, `LONE_CULTIVATOR`
- `{55, 90}` - `ABANDONED_INFANT` (sole member)
- `{70, 100}` - the default bucket: every other constant, including
  `FISHERMAN`, `FARMER`, and the 7 identities with no dedicated config field

Every configured default had been guessed rather than verified and was
typically 20-40 years too high (e.g. `LONE_CULTIVATOR` was 110/140 instead
of the real 80/105; `MERCHANT_SON` was 100/130 instead of the real 60/85).
Corrected all 34 fields to match their real bucket exactly. `FISHERMAN` and
`FARMER` keep their own independently-configurable fields even though
vanilla lumps them into the shared default bucket - that's a legitimate
enhancement the mixin's full-replacement `@Inject` already supported, not
a bug, so their fields stay separate but now default to the real bucket's
values (70/100) instead of the wrong guessed ones. Added a class-doc note
to `IdentityMixin.java` and a section-header comment above
`IDENTITY_LIFESPAN_LONE_CULTIVATOR_MIN` in `ExtendedConfig.java` recording
the full bucket breakdown so this doesn't need re-deriving from bytecode
again. `starterItems()` (the other half of `IdentityMixin`) was
re-checked too - its config-driven override / empty-string-fallthrough
design was already correct and needed no changes.

**Qi System audit - qi shield perfect-reduction traced and wired, 10
spirit-stone-ore constants found and wired (2026-09-01, later still):**

`QI_SHIELD_QI_PER_DAMAGE` was already wired (earlier this session). Two
items remained in the Qi System section:

- **`QI_SHIELD_PERFECT_REDUCTION`**, previously left honestly unwired
  because the earlier pass couldn't trace how `qiPerDamage`'s early
  `return 1.0` for a perfect shield actually reduced damage downstream.
  Traced it this pass: there's no damage-reduction literal to redirect at
  all - `maxAbsorbableDamage(ServerPlayer/NPC, incomingDamage, Realm, int)`
  checks `grantsPerfectQiShield(...)` first, and if true returns
  `Math.max(0.0f, incomingDamage)` - i.e. the entire hit becomes eligible
  for absorption (still gated by available qi elsewhere), which is what
  "perfect" means. Wired by `@Redirect`-ing that one `Math.max(float,
  float)` call in each overload (confirmed exactly one per method via full
  disassembly) to scale `incomingDamage` by the config fraction before the
  max, so admins can now cap how much of a hit even a "perfect" shield can
  ever cover. Default (1.0) reproduces vanilla exactly.
- **10 previously-untouched fields**: `QI_STONE_ORE_MAX_QI_LOW/MID/HIGH/
  SUPREME/SPIRIT_VEIN_SPRING` and `QI_STONE_ORE_REGEN_LOW/MID/HIGH/SUPREME/
  SPRING`. No mixin referenced them at all going in. Traced the real
  target to `BlockQiSpecs.applyHardcodedDefaults()`, a ~1600-line method
  that registers a `Block -> BlockQiSpec` map for qi-emitting blocks; the 5
  spirit-stone-ore blocks (`LOW/MID/HIGH/SUPREME_SPIRIT_STONE_ORE`,
  `SPIRIT_VEIN_SPRING`) are registered with their `maxQi`/`regenPerSec`/
  `emitRate` args inlined directly at the call site rather than through a
  shared helper method. All 10 already-configured defaults matched the
  real bytecode exactly (2000/4000/8000/20000/50000 maxQi,
  5.0/10.0/20.0/50.0/80.0 regen) - a pure wiring gap, not a default-value
  bug this time. Deliberately did NOT wire this via `@ModifyConstant`/
  ordinal: the method reuses common literal values (5.0, 10.0, 0.1, 0.15,
  etc.) across dozens of unrelated block entries, so an ordinal-based
  redirect would be one off-by-one away from silently reconfiguring the
  wrong block. Instead added `BlockQiSpecsMixin`, injecting at the `TAIL`
  of `applyHardcodedDefaults()` and calling the class's own public
  `BlockQiSpecs.override(Block, BlockQiSpec)` (confirmed via javap to be a
  simple null-checked `SPECS.put(...)`) to replace just those 5 entries
  with config-driven values - safe against `resetToDefaults()` too, since
  that method clears the map and re-calls `applyHardcodedDefaults()`. Per-
  tier `emitRate` has no config field, so the real verified constant is
  kept per tier rather than guessed at.

Qi System is now fully wired: attraction radius, meditation bonuses (both),
qi shield qi-per-damage, qi shield perfect-reduction, and all 10 spirit-
stone-ore constants.

**Ability spells audit - 14 fields across 7 real classes (most not named
what the config field names suggest), 2 genuinely unwireable
(2026-09-01, later still):**

Worked through the SWORD_FLIGHT/VOID_STEP/PALM_THUNDER/VOID_ESCAPE/
BUDDHA_FIRE_LOTUS/CORE_SELF_DESTRUCT/SEA_CHANNEL/GLACIER_BURIAL fields.
None had a mixin going in. The real implementing classes turned out to be
scattered and, in three cases, not obviously named after the ability at
all - each required checking a per-Spell dispatch table
(`ChargeableSpellHandler.onPlayerTick()`'s `if (spell == Spell.X)` chain)
to find the actual class:

- `SWORD_FLIGHT_UPKEEP_QI_PER_SECOND` - wired via new
  `SwordFlightHandlerMixin`, targeting `SwordFlightHandler.tick()`'s
  per-second drain literal.
- `VOID_STEP_AIR_JUMP_QI_COST` / `VOID_STEP_DASH_QI_COST` - wired via new
  `VoidStepHandlerMixin`. `VOID_STEP_SLOW_FALL_QI_COST` - **not wired**:
  a full disassembly of `VoidStepHandler.class` found only 3 qi check/
  deduct pairs in the whole class, all accounted for by air jump and dash;
  auto slow-fall costs no qi in the base mod at all.
- `PALM_THUNDER_CHANNEL_QI_PER_SECOND` / `PALM_THUNDER_ARMING_TICKS` -
  wired via new `PalmThunderHandlerMixin`. The qi field needed a unit
  conversion: the real field drains a base amount every 4 ticks, while the
  config field (already correctly defaulted) represents the equivalent
  per-second rate, so the redirect divides by 5.
- `VOID_ESCAPE_CHARGE_TICKS` / `VOID_ESCAPE_CHARGE_QI_PER_TICK` /
  `VOID_ESCAPE_ACTIVE_QI_PER_TICK` - wired via new `VoidEscapeHandlerMixin`.
  None had dedicated named fields; all three were inlined directly at
  their single real use site, confirmed via full disassembly.
- `BUDDHA_FIRE_LOTUS_READY_QI` / `CORE_SELF_DESTRUCT_READY_QI` - **not** on
  `BuddhaFireLotusEntity`/`CoreSelfDestructHandler` as their names would
  suggest. Both are real fields on the shared `ChargeableSpellHandler`
  class (~3200 lines). Wired via new `ChargeableSpellHandlerMixin` using
  `method = "*"` - normally a red flag for a class this size, but safe
  here because every occurrence of each literal shares the same constant-
  pool index (proof, not assumption, that they're the same named field
  reused, not coincidentally-equal unrelated literals) - confirmed exactly
  5 occurrences of each across the class.
- `SEA_CHANNEL_TICKS` - not on any class with "Sea" in its name. "Sea of
  Overwhelming Blood" is implemented by `Blood07FormationHandler`, found
  via the dispatch table. Wired via new `Blood07FormationHandlerMixin`.
- `GLACIER_BURIAL_BASE_QI_PER_TICK` - the real field lives on
  `PlayerChargeTimeline.class`, feeding an exponential qi-cost ramp
  (`glacierBurialQiCostAtTick`) shared by both the player and NPC casting
  paths. Wired via new `PlayerChargeTimelineMixin`, scoped tightly to that
  one method rather than `method = "*"` - a same-valued but unrelated
  `BASE_DRAIN` field also inlines to the same literal elsewhere in that
  class, which a class-wide redirect would have silently corrupted.
  `GLACIER_BURIAL_NPC_CHANNEL_TICKS` - **not wired**: the real field
  exists (same class, same default value) but a full search of that class
  and the NPC channel driver (`NpcCombatChannelController`) found no
  reference to it anywhere - it appears to be declared but never actually
  read in the compiled mod.

Also found and fixed a UI-consistency gap while in `PlayerChargeTimeline`:
its `fullChargeTicks(Spell)` method - a separate progress-bar/UI helper -
duplicates the SEA/VOID_ESCAPE/PALM_THUNDER durations as its own inlined
literals rather than reading the handler classes' constants. Without
wiring this too, changing e.g. `PALM_THUNDER_ARMING_TICKS` would leave any
UI reading `fullChargeTicks()` still showing the old vanilla duration
while the real channel took a different amount of time. Wired all 3
branches in the same new mixin (the Palm Thunder one needed converting:
its literal is `armingTicks + tapTicks(5) + 1`, not `armingTicks` alone).

Net: 12 of 14 fields wired across 7 classes and 2 genuinely-dead fields
honestly documented, plus one extra UI-consistency fix found along the way
that wasn't on the original list.

**Effects Inverse audit (2026-09-01):** the 5 `inverseFiveElements` fields
(`EFFECT_INVERSE_MARK_DURATION_TICKS`, `EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT`,
`EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT`, `EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER`,
`EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER`) all had correct-looking existing
defaults (600/1.1/0.9/0.25/0.25) but none were wired. The obvious first guess -
a class named after "Inverse Five Elements" - was a dead end: `InverseFiveElementsEffect.class`
is just an empty `MobEffect` marker/tag with a bare constructor, no logic at all.
Searching for actual consumers of that marker led to the real target:
`PhysiqueBonusHelper.class`, which already has an existing mixin
(`PhysiqueBonusHelperMixin`, for the unrelated Chaos/Fire/Ice Body and Alchemy
Heart fields) - extended it rather than creating a second `@Mixin` on the same
class, which would risk a registration conflict.

- `EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT` - `applySharedSpellDamageRules
  (Physique,Spell,D)` - sole `1.1d` in that method, gated on
  `INVERSE_FIVE_ELEMENTS_BODY && isBasicFiveElementSpell(spell)`.
- `EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT` - `spellQiCostMultiplier
  (Physique,Spell)` - sole `0.9d` in that overload. The method is overloaded
  with `spellQiCostMultiplier(Player,Spell)`, so this and the next field both
  needed explicit method descriptors rather than the bare name the rest of
  the file uses, to avoid ambiguously targeting both overloads.
- `EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER` - `applyPlayerOnlySpellDamageRules
  (Player,Physique,Spell,D)` - sole `0.25d` in that method, formula
  `mult *= (1.0 + stacks * 0.25)`.
- `EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER` - `spellQiCostMultiplier
  (Player,Spell)` - sole `0.25d` in that overload, formula
  `mult *= Math.max(0.0, 1.0 - stacks * 0.25)`. This `0.25d` shares its
  constant-pool index with the one backing `STACK_DAMAGE_PER_LAYER` above -
  proof both trace to one named constant in the original source reused for
  two purposes - but scoping each redirect to its own distinct method still
  lets the two config fields tune independently without cross-contamination.
- `EFFECT_INVERSE_MARK_DURATION_TICKS` - all 3 occurrences of `600` in the
  entire class (confirmed via a class-wide grep, not assumed) live inside one
  synthetic lambda, `lambda$onSpellCast$1(Spell,ServerPlayer,CultivationData)`:
  two as `long 600l` (same constant-pool index, extending the stack-timeout
  and mark-expiry) and one as `sipush 600` (the applied `MobEffectInstance`'s
  duration argument - a raw immediate, not a constant-pool literal, so it
  needed its own `@ModifyConstant(intValue = 600)` handler alongside the
  `longValue = 600L` one).

Net: all 5 fields wired, zero dead fields in this section. This appears to be
the last remaining named section from the original backlog - a fresh grep of
`ExtendedConfig.java` should confirm before declaring the full audit closed.

**Version 1.0.7 note (2026-09-01, later still):** bumped for this session's
batch — Identity per-identity lifespan (17 default pairs corrected, all 34
fields wired via `IdentityMixin`), the rest of Qi System (qi shield perfect-
reduction + 10 spirit-stone-ore constants via new `BlockQiSpecsMixin`),
Passive Spells/Void Step/Palm Thunder/Glacier Burial (12 of 14 fields wired
across 7 new mixins, 2 genuinely dead), and Effects Inverse (all 5 fields,
extending the existing `PhysiqueBonusHelperMixin` rather than duplicating a
mixin on `PhysiqueBonusHelper.class`). `gradle.properties`,
`rebuild_and_install.bat`, and `rebuild_and_install.ps1` all bumped to 1.0.7
(same three-file pattern as every prior version bump).

**Version 1.0.6 note (2026-09-01, later still):** bumped for the large batch
documented below (Dao/Physique/Spirit Root/Spirit Plant/Spirit Vein/NPC
Combat/Formations/Progression/Morality/Lifespan/partial Qi System, plus the
`DistanceManagerMixin` resolution). `gradle.properties`, `rebuild_and_install.
bat`, and `rebuild_and_install.ps1` all bumped to 1.0.6 (same three-file
pattern as every prior version bump — each rebuild script keeps its own
separate copy of the version number by design).

**Version 1.0.5 note (2026-09-01, later same day):** shipped as 1.0.5. Also
added a "Reset All" button (next to "Reset Tab") in `CustomConfigScreen.java`
- it turned out `resetAllConfigs()` already existed, fully written and
correct, just never called from anywhere in the UI (same class of bug as
`TieredWeaponMixin` above, this time in application code instead of a
mixin). Wired it into both the render block and the click handler
(`mouseClicked`), gated behind holding Shift so a stray click can't wipe
every setting at once. It resets via `ConfigValueAccessor.getDefaultValueString()`,
which reads each field's real registered default (`ForgeConfigSpec.ConfigValue.
getDefault()`) - i.e. whatever default that field was given in
`ExtendedConfig.java`, which is meant to mirror the base mod's own default
for every field that was defined correctly. `gradle.properties`,
`rebuild_and_install.bat`, and `rebuild_and_install.ps1` all bumped to
1.0.5 (the two rebuild scripts each keep their own separate copy of the
version number by design - see their own comments - so both needed editing,
not just gradle.properties).

## Method (repeat per field, per category)

1. Find every real usage of the field's Java constant outside `ExtendedConfig.java`
   (`grep -rn FIELD_NAME src/main/java`, excluding ExtendedConfig.java itself).
2. If it has a real consumer: read that mixin/handler, confirm the comment
   matches — correct units, correct trigger condition (e.g. does it actually
   require the section's `ENABLE_*` toggle?), correct edge-case values (e.g.
   `-1 = full refill`). Fix the comment if it's vague, wrong, or generic.
3. If it has **no** consumer anywhere: it's dead. Before wiring it up,
   `javap -p -c` the relevant class(es) in the installed
   `xiaoxiang_cultivation-*.jar` (re-extract to `/tmp/xxjar/extracted` if
   missing) to find the base mod's real accessor/field for that value — do
   **not** guess a mixin target without disassembling it first. If a real,
   verifiable target exists, add a mixin (or, if the base mod exposes its own
   override API like `PillEffectSpecs.override()`/OVERRIDES-map pattern,
   prefer hooking the same accessor method instead of a brand-new mechanism).
   If no real target exists (the base mod doesn't have anything corresponding
   to that field any more — e.g. it moved to a datapack-driven system with a
   completely different shape), say so plainly in the comment and in this
   file rather than fabricating a connection.
4. Register any new mixin class name in `xiaoxiang_config_ext.mixins.json`.
   This file is `"required": true` — a bad method/class name is a hard crash
   on load for every user, so double- and triple-check every method
   signature against the javap output before adding it.
5. Update this file's checklist and dead-field list, then stop and report
   back with concrete counts (don't try to silently do the whole file in one
   sitting — many small verified batches, not one giant unverifiable one).

No decompiler / no network access to Maven/Forge in this sandbox — `javap -p
-c` bytecode disassembly of the installed jar is the only verification tool
available for the base mod's real behavior. This has been sufficient so far.
This project's own Gradle build cannot be run in this sandbox either (no
Forge/MDK toolchain cached, and it needs the user's Windows machine via
`rebuild_and_install.bat` anyway) — so no changes here are compile-verified
beyond careful manual reading + bytecode cross-checking. Flag any mixin that
is new/unusually risky so the user knows to watch for it on first launch
after the next build.

## Status legend

- ✅ done — every field's comment verified accurate; every field either has a
  real consumer or has been wired to one
- 🔶 partial — some fields in this category done, some still pending
- ⬜ not started

## Category checklist (30 top-level sections, ExtendedConfig.java)

| # | Category | Status | Notes |
|---|----------|--------|-------|
| 1 | General Toggles (`ENABLE_*`, 26 fields) | 🔶 | See "General Toggles" below |
| 2 | Realm Power Levels | ⬜ | |
| 3 | Beast Cultivation | ⬜ | |
| 4 | NPC Spawning | ⬜ | |
| 5 | Qi Density Per Biome | ⬜ | |
| 6 | Spells | ⬜ | |
| 7 | Weapons | 🔶 | Damage global multiplier + 3 Bloodthirst Blade multipliers wired 2026-09-01; spell-qi-cost-reduction tiers (5 fields) were already coded but the mixin was never registered - fixed that too. `WEAPON_ATTACK_SPEED_MODIFIER` still genuinely dead - see notes below. |
| 8 | Pills | 🔶 | Qi-recovery + rejuvenation wired 2026-09-01 (see below); blood burn/clear mind/divine stride/storage-bag-adjacent fields still pending |
| 9 | Alchemy | 🔶 | `ALCHEMY_HEART_QI_COST_MULT`/`ALCHEMY_HEART_SUCCESS_BONUS` (Alchemy Heart physique) wired 2026-09-01 via `PhysiqueBonusHelperMixin`. Furnace slot/timing fields (`ALCHEMY_TICKS_PER_PILL`, `ALCHEMY_INPUT_SLOTS`, `ALCHEMY_OUTPUT_SLOTS`) still genuinely dead — confirmed compile-time-constant, no safe live hook found this pass, see notes below. |
| 10 | Refining | 🔶 | Both per-technique tier-up-chance fields wired 2026-09-01 via `TechniqueBonusHelperMixin` — includes a real mislabeling fix, see session log below. Furnace slot/timing fields (`REFINING_TICKS_PER_ITEM`, `REFINING_INPUT_SLOTS`, `REFINING_OUTPUT_SLOTS`) still genuinely dead, same reason as Alchemy. |
| 11 | Spirit Plants | ✅ | All 12 `SPIRIT_PLANT_*` fields wired 2026-09-01 via full rewrite of `SpiritPlantBlockMixin` (was an empty, unregistered placeholder). See session log. |
| 12 | Spirit Veins | ✅ | `SPIRIT_VEIN_ATTRACT_RADIUS`/`SUPPLY_RADIUS` wired 2026-09-01 via new `SpiritVeinCoreBlockEntityMixin`; the other 15 fields were already covered by the pre-existing `SpiritVeinCoreTierMixin`. |
| 13 | Techniques | ⬜ | |
| 14 | Spirit Roots | ✅ | Pre-existing `SpiritRootMixin` already covered 10/13; the remaining qi-absorption fields wired 2026-09-01 via a rewrite of `SpiritRootBonusHelperMixin` that also fixed a real ambiguity bug — see session log. |
| 15 | Physiques | ✅ | Pre-existing `PhysiqueMixin` already covered the 6 rarity-weight fields; the other 12 wired 2026-09-01 via new `PhysiqueBonusMixin`. |
| 16 | Foundation Dao | ✅ | Pre-existing `FoundationDaoMixin` already covered 18/21; bone-age-limit and tribulation-waves (3 fields) wired 2026-09-01 via `CultivationProgressionRulesMixin`. |
| 17 | Golden Core Dao | ✅ | Pre-existing `GoldenCoreDaoMixin` already covered 44/45; bone-age-limit wired 2026-09-01 via `CultivationProgressionRulesMixin`. |
| 18 | Identity (per-identity lifespan) | ⬜ | Not researched this session. |
| 19 | Progression | 🔶 | Most fields wired 2026-09-01 across `CultivationProgressionRulesMixin`, `SectSavedDataMixin`, `CultivationDataMixin` — see session log for the full list, including a real 3-way-branch mislabeling fix and a genuine subagent-report error caught and corrected. `PROGRESSION_NPC_TRIBULATION_FAILURE_WEAKNESS_DAYS` is a duplicate field, honestly documented as such rather than wired. |
| 20 | NPC Combat | ✅ | Pre-existing `NpcCombatThreatDetectorMixin` already covered 33/36; the remaining 2 wired 2026-09-01, same mixin. `NPC_COMBAT_STALEMATE_TIMEOUT` confirmed genuinely dead (no "stalemate" mechanism anywhere in the jar) — tooltip says so. |
| 21 | Formations | ✅ | All 47 `FORMATION_*` fields wired 2026-09-01 via new `FormationCorePlateBlockEntityMixin`, plus default-value corrections for 12 Formation Core fields that were never wired before (see session log — a subagent report inaccuracy about `applyMaze`'s literal count was caught and corrected here). |
| 22 | Sects | 🔶 | Large section, still the biggest remaining gap. This session: all 15 `SECT_SHOP_*` fields (technique/spell/weapon prices × 5 tiers, plus sellPercent) rewired 2026-09-01 in `SectShopPricingMixin` — see the dedicated entry below, a real "config silently ignored" bug caught and fixed. ~58 other `SECT_*` fields still unresearched (ambient/overhead-bubble timings, defense responder limits, department shifts, disciple realm/sub-stage gates, journey timeouts, life-tick interval, power-score caps, profile stats, recruit chance, task escrow/timeout limits, warehouse/inventory slot limits). |
| 23 | Loot | ⬜ | Not researched this session. |
| 24 | Trials | ⬜ | Not researched this session. |
| 25 | Qi System | 🔶 | `QI_SYSTEM_ATTRACTION_RADIUS`/`MEDITATION_RANGE_BONUS`/`MEDITATION_EFFICIENCY_BONUS` wired 2026-09-01 via new `PlayerQiConsumerMixin`; `QI_SHIELD_QI_PER_DAMAGE` wired via new `QiShieldHandlerMixin` (type-mismatch noted: config field is a LongValue, real target is a double). `QI_SHIELD_PERFECT_REDUCTION` and all 10 `QI_STONE_ORE_*` fields (in `BlockQiSpecs`'s static initializer — many similar-looking constants needing careful per-site ordinal work) still pending. |
| 26 | Passive Spells | ⬜ | Researched (method-level targets identified) but not wired this session — ~25 `PASSIVE_*`/`VOID_*`/`PALM_THUNDER_*`/`GLACIER_BURIAL_*` fields beyond what `PassiveSpellHandlerMixin` already covers. |
| 27 | Effects | ⬜ | Researched (all in `PhysiqueBonusHelper`) but not wired this session — `EFFECT_INVERSE_*` fields (5). |
| 28 | Morality | ✅ | `MORALITY_MIN_VALUE`/`MAX_VALUE` wired 2026-09-01 via `MoralityHelperMixin` (`clamp`/`add`, 6 constant sites total); defaults corrected from ±1000 to the real ±1000000 since never wired before. |
| 29 | Lifespan Helper | ✅ | `LIFESPAN_START_BONE_AGE_MIN/MAX` wired 2026-09-01 via `IdentityDrawHandlerMixin` (both the base mod's own flow and this mod's own custom-identity code, which was found hardcoding the same literals); `LIFESPAN_AGE_PER_DAY` wired via new `LifespanHandlerMixin`; `LIFESPAN_GLOBAL_MULTIPLIER` wired via a RETURN-scaling injection added to `LifespanHelperMixin`. `LIFESPAN_AGE_PER_DAY_MEDITATING` confirmed genuinely dead (no meditating-specific aging path exists) — tooltip says so. |
| 30 | Client Config (UI/accessibility) | 🔶 | **Reprioritized to first** per user, 2026-09-01. Config-screen-chrome theming (`CLIENT_UI_THEME`, `Theme.java`) confirmed already fully working, including 3 dedicated accessibility themes (Monochrome/High Contrast/Gentle Focus) — nothing to fix there. In-game HUD (`CultivationHudMixin`): visibility toggle + qi/cult bar colors + HUD text color + bar row-height now wired for real (see session log). Position-offset fields and portrait size deliberately NOT wired yet — real bytecode-collision risk, needs dedicated careful pass, see mixin's own class doc for the exact reasons. `CLIENT_INK_BLACK_COLOR` confirmed dead even in the base mod itself — tooltip now says so honestly instead of promising an effect. |

Plus ~25 smaller "NEW:" sub-sections nested in the builder (Sect Life,
Sect Journey, Sect Defense, Sect Departments, Sect Tasks, NPC AI Combat,
Formation Core, Loose Immortal, Cultivation Data, Identity Draw, etc.) —
these fall under category 22 (Sects) and 20/21 (NPC/Formations) above and
will be swept up when those categories are done.

## Scope decision (confirmed with user, 2026-09-01)

When a field turns out to be dead: **wire it up for real** (not just an
honest "currently has no effect" tooltip), whenever a genuine, verified
target exists in the base mod. Only fall back to an honest "not currently
applicable" note when bytecode inspection turns up no real counterpart to
wire it to (e.g. the base mod moved the underlying system to something
structurally different, like pills moving to a datapack-driven effect
system — though even there, as the Pills fix below shows, there's often
still a clean override point).

## Done this session (2026-09-01)

**Pills category, partial:**
- Root cause found: `xiaoxiang_cultivation` resolves every pill's actual
  effect through its own `PillEffectSpecs.qiAmount/rejuvenationHeal/
  regenerationTicks/regenerationAmplifier/absorptionTicks/
  absorptionAmplifier` static methods (confirmed via javap bytecode —
  `PillItem.finishUsingItem` calls `PillEffectSpecs.qiAmount(this,
  this.qiAmount)` and treats a negative result as "refill to max qi",
  matching the base mod's own `pill_qi_recovery_immortal.json` datapack
  entry of `{"qi": -1}`). None of `PILL_QI_LOW/MID/HIGH/SUPREME/IMMORTAL` or
  `REJUVENATION_HEAL_LOW/MID`, `REJUVENATION_REGEN_TICKS_SUPREME`,
  `REJUVENATION_REGEN_AMP_SUPREME`, `REJUVENATION_ABSORPTION_TICKS_SUPREME`,
  `REJUVENATION_ABSORPTION_AMP_SUPREME` were ever read by anything — editing
  them in-game silently did nothing. Config defaults for every one of these
  fields exactly match the base mod's own `pill_effects/*.json` datapack
  values, confirming they were written to mirror this exact system and just
  never got hooked up.
- **Fix**: new `mixin/PillEffectSpecsMixin.java`, targeting
  `PillEffectSpecs`'s six static accessors directly (matched by item
  identity against `ModItems.PILL_QI_RECOVERY_*` / `ModItems.
  PILL_REJUVENATION_*`, not by tier alone — `qiAmount()` in particular is
  shared by every pill type, so a tier-only match would have wrongly applied
  "qi pill" values to blood burn/clear mind/divine stride pills too). Gated
  on `ENABLE_PILL_OVERRIDES`, same as the rest of the pills section.
  Registered in `xiaoxiang_config_ext.mixins.json`. **Not compile-verified**
  (no Forge toolchain in this sandbox) — reviewed carefully against javap
  output but flag this specifically if anything pill-related misbehaves
  after the next build.
- `ENABLE_PILL_OVERRIDES`'s comment corrected: it was only gating storage
  bag column/row counts before today (via `StorageBagItemMixin`) despite
  its comment claiming "pill qi recovery and effect values" — that promise
  is genuinely true now, but the storage-bag side-effect is real too, so the
  comment now says both explicitly rather than picking one.

**Still dead in Pills** (not touched today — no `PillEffectSpec` field
exists for these, so they need their own per-item mixins, not
`PillEffectSpecsMixin`): `BLOOD_BURN_PILL_DAMAGE_*`,
`BLOOD_BURN_PILL_DURATION_TICKS`, `CLEAR_MIND_PILL_DURATION_HIGH/SUPREME`,
`DIVINE_STRIDE_DURATION_*`, `DIVINE_STRIDE_SPEED_*`, `SHADOW_STEP_PILL_
DURATION_TICKS`, `YOUTH_PILL_MIN_BONE_AGE`, `YOUTH_PILL_BONE_AGE_REDUCTION`
(YouthPillItem doesn't even extend `PillItem`), `SPIRIT_STONE_QI_*`,
`STORAGE_BAG_VISIBLE_ROWS_MAX`, `PILL_USE_TICKS`, `SPIRIT_STONE_USE_TICKS`.

**General Toggles category, partial:** confirmed via full usage-site check
that 23 of the 26 `ENABLE_*_OVERRIDES` toggles are real and correctly wired.
`ENABLE_SPIRIT_PLANT_OVERRIDES`, `ENABLE_PROGRESSION_OVERRIDES`, and
`ENABLE_QI_SYSTEM_OVERRIDES` are dead — no mixin checks them at all, and
(worse) none of the value fields in those three sections have any consumer
either, so those are three entire categories that need the full
javap-and-wire treatment, not just these three toggles in isolation.
`ENABLE_PILL_OVERRIDES` comment corrected (see above).

**Client Config / in-game HUD accessibility (2026-09-01, later same day):**
- Confirmed `Theme.java`'s 30-theme config-screen system (including 3
  accessibility-purpose themes) is fully live — `Theme.loadFromConfig()` is
  called from `CustomConfigScreen` and `CLIENT_UI_THEME` genuinely persists
  the chosen theme. No work needed there.
- The separate, older `CLIENT_HIGH_CONTRAST_MODE` / `CLIENT_BG_OPACITY` /
  `CLIENT_BG_PAGE_COLOR` / `CLIENT_BG_PANEL_COLOR` / `CLIENT_BORDER_LIGHT_
  COLOR` / `CLIENT_BORDER_DARK_COLOR` / `CLIENT_GOLD_TEXT_COLOR` /
  `CLIENT_VERMILLION_COLOR` fields remain unresolved — not yet confirmed
  whether they're truly superseded leftovers from before the 30-theme system
  existed, or a still-intended separate thing. Not touched this session;
  still on the dead list below. Check next.
- `CultivationHud` (the in-game qi/cultivation bar overlay, repositioned by
  the already-existing `CultivationHudMixin`) disassembled in full via javap.
  Wired for real: `CLIENT_HUD_VISIBLE` (show/hide, was checked nowhere),
  `CLIENT_QI_BAR_TOP_COLOR`, `CLIENT_QI_BAR_BOTTOM_COLOR`,
  `CLIENT_CULT_BAR_TOP_COLOR`, `CLIENT_CULT_BAR_BOTTOM_COLOR` (each a
  bytecode-confirmed single-occurrence ARGB constant in `render()`),
  `CLIENT_HUD_TEXT_COLOR` (two separate occurrences of the same value, in
  `drawRealmText` and `renderSoulStatus` — both wired, each its own
  `@ModifyConstant` scoped to its own method), `CLIENT_STATUS_BAR_ROW_HEIGHT`
  (`drawBar`'s own single occurrence of the bar-height literal).
- Deliberately NOT wired, with verified reasons (see `CultivationHudMixin`'s
  class doc for the full explanation): `CLIENT_HUD_X`, `CLIENT_HUD_Y`,
  `CLIENT_PORTRAIT_X/Y/SIZE`, `CLIENT_REALM_NAME_X/Y`, `CLIENT_QI_BAR_X/Y`,
  `CLIENT_CULT_BAR_X/Y`, `CLIENT_SPELL_GRID_X/Y`, `CLIENT_INFO_TEXT_X/Y`.
  The X-offset fields all share one literal (`bipush 32`) used at 3+ call
  sites in `render()` — needs `@Slice`-scoped, per-call-site injections to do
  safely, not attempted yet. Portrait size isn't even read by `drawPortrait`
  as one value (three separate hardcoded literals: 25/25 outer ring, 23/23
  inner ring, 16 face icon) — resizing it correctly is a real design task,
  not a one-line fix. `CLIENT_SPELL_GRID_X/Y` and `CLIENT_INFO_TEXT_X/Y` may
  not even belong to `CultivationHud` at all — no spell-grid or info-text
  drawing method exists in this class's method list; likely belongs to a
  different screen/overlay entirely, not yet located.
- `CLIENT_INK_BLACK_COLOR`: confirmed its target value never appears
  anywhere in `CultivationHud`'s bytecode at all — the base mod's own
  matching constant is itself dead code. Tooltip corrected to say this
  plainly rather than claim an effect that isn't real on either side.
- All of the above done as edits to the mixin/config that were compared
  byte-for-byte against the user's live Desktop project before writing (see
  "Important note on sandbox vs. live project" below) and delivered via the
  device bridge.

**Weapons category, partial (2026-09-01, same session, continued after the
first check-in):**
- Found a real bug while investigating: `TieredWeaponMixin.java` (wires the 5
  `WEAPON_SPELL_QI_COST_REDUCTION_*` tier fields onto `SpiritSwordItem`) was
  **never registered** in `xiaoxiang_config_ext.mixins.json` — the file
  existed, was correctly written, and referenced the field names, so the
  text-search audit counted those 5 fields as "used." They weren't: an
  unregistered mixin is never loaded, so editing those 5 settings has done
  nothing since whenever that file was written. Registered it now. See the
  new caveat entry below — this is a real gap in how "dead" gets detected
  that the audit's methodology note needs to call out.
- `WEAPON_DAMAGE_GLOBAL_MULTIPLIER`: verified via javap that weapon attack
  damage is baked into a vanilla `SwordItem` `AttributeModifier` at item
  construction (the constructor's attack-damage int never appears anywhere
  else in the base mod's bytecode — grepped the whole jar). No live accessor
  to mixin into, and mixing the constructor itself risks a config-load-order
  problem plus needs a vanilla SRG method name we can't verify without a
  decompiler. Wired instead via a new `WeaponDamageHandler.java` — a plain
  `LivingHurtEvent` handler (not a mixin), matching the exact technique the
  base mod's own `com.xiaoxiang.cultivation.event.AttackBonusHandler`
  already uses for melee damage bonuses (confirmed by disassembling that
  class: same `getSource()/getAmount()/setAmount(F)` call shape). Applies
  live on every hit, no restart needed, and works for NPCs wielding these
  weapons too, not just players.
- `WEAPON_BLOOD_SPELL_DAMAGE_BONUS_MULT`, `WEAPON_BLOOD_SPELL_QI_REDUCTION_
  MULT`, `WEAPON_BLOOD_CAPACITY_MULTIPLIER`: verified via javap that
  `BloodthirstBladeItem` (final class, its own separate item, not a
  `SpiritSwordItem` subclass) exposes live accessor methods
  (`bloodSpellDamageBonusPct()`, `bloodSpellQiReductionPct()`,
  `bloodCapacity()`) that its own tooltip AND its blood-value bookkeeping
  code (`bloodValue`/`setBloodValue`/`addBloodValue`) both call live, not a
  cached copy. New `mixin/BloodthirstBladeItemMixin.java` intercepts each at
  `@At("RETURN")` and multiplies by the matching config value. Registered in
  mixins.json.
- `WEAPON_ATTACK_SPEED_MODIFIER` still genuinely dead: same
  baked-at-construction problem as the damage multiplier, but unlike damage
  there's no equivalent live "attack speed" event to intercept through
  (attack speed drives an internal swing-cooldown calculation read straight
  off the item's cached attribute map, not something dealt out per-hit).
  Tooltip corrected to say so honestly rather than guess at an unverifiable
  fix.
- All three new/changed files (`WeaponDamageHandler.java`,
  `BloodthirstBladeItemMixin.java`, the mixins.json registration) were test-
  compiled in this sandbox against hand-written stub classes matching the
  real, javap-verified method signatures (`javac` with no Forge toolchain
  available, so this only catches syntax/type errors, not real Forge
  linkage) — compiled clean, zero errors.

**Alchemy / Refining categories, partial (2026-09-01, same session, continued
after the Weapons check-in):**
- `ALCHEMY_HEART_QI_COST_MULT`, `ALCHEMY_HEART_SUCCESS_BONUS` (Alchemy Heart
  physique bonuses): verified via javap that `PhysiqueBonusHelper.
  alchemyQiCostMultiplier(Player)` and `.alchemySuccessChanceBonus(Player)`
  each read a single field off whichever ONE `PhysiqueBonus` record the
  player currently has equipped (`bonusOf(Player)` = `equippedOf(player).
  bonus()` — confirmed not an aggregate across physiques), baked at class-
  init to `0.5` and `0.1` respectively for `ALCHEMY_HEART_BODY` and to
  neutral values for every other physique. Also confirmed via disassembling
  `network/ExecuteAlchemyPacket.class` that both methods are called live
  while actually resolving an alchemy craft's qi cost and success roll, not
  just displayed as a tooltip. Wired via two new `@ModifyConstant` methods on
  the existing `mixin/PhysiqueBonusHelperMixin.java`, matching that file's
  established pattern exactly (same `require = 0` house convention). Gated
  on `ENABLE_ALCHEMY_OVERRIDES` (not `ENABLE_PHYSIQUE_OVERRIDES`) since both
  fields live in the config's "alchemy" section, matching the toggle
  `AlchemyCoreBlockEntityMixin` already uses for the rest of that section.
- `REFINING_TIER_UP_CHANCE_DIVINE_FORGE`: verified via javap disassembly of
  `Technique.class`'s static initializer that `DIVINE_FORGE`'s baked `Bonus`
  record really does set `.refiningTierUpChance(0.25)`, and via
  `RefiningRank.rollItemResult(ServerPlayer)` that `TechniqueBonusHelper.
  refiningTierUpChance(Player)` is read live during an actual refining roll.
  Wired via a new `@Inject(at = @At("RETURN"))` on the existing
  `mixin/TechniqueBonusHelperMixin.java`, scoped to players with
  `Technique.DIVINE_FORGE` equipped (mirrors `PillEffectSpecsMixin`'s
  match-by-identity pattern). Gated on `ENABLE_REFINING_OVERRIDES`.
- `REFINING_TIER_UP_CHANCE_HEAVENLY_ELIXIR` — **real mislabeling found and
  fixed, not just wired as-is**: the same `Technique.class` disassembly shows
  `HEAVENLY_ELIXIR`'s baked `Bonus` record never calls `.refiningTierUpChance
  (...)` at all — it calls `.alchemyTierUpChance(0.25)` instead. So despite
  its name and its place in the "refining" config section, this field never
  correlated with anything Heavenly Elixir actually does; the base mod's own
  real behavior for that technique is an ALCHEMY tier-up bonus of the exact
  same size. Rather than either leave it dead or wire it to a refining
  mechanic Heavenly Elixir doesn't control (which would silently invent a
  bonus the base mod never gave it, and would make "Reset All" restore a
  value that isn't the real original-mod default for anything), the field's
  name and TOML path were kept unchanged for backward compatibility but its
  wiring targets `alchemyTierUpChance()` instead, scoped to
  `Technique.HEAVENLY_ELIXIR`. Its default (`0.25`) already matched that
  mechanic's real default, so no default-value change was needed — only the
  tooltip, which now explains the discrepancy honestly instead of implying a
  refining effect that was never real.
- Furnace slot/timing fields left honestly unwired this pass:
  `ALCHEMY_TICKS_PER_PILL`, `ALCHEMY_INPUT_SLOTS`, `ALCHEMY_OUTPUT_SLOTS`,
  `REFINING_TICKS_PER_ITEM`, `REFINING_INPUT_SLOTS`, `REFINING_OUTPUT_SLOTS`.
  Verified via javap that each mirrors a `public static final int` constant
  on `AlchemyCoreBlockEntity`/`RefiningCoreBlockEntity` — javac inlines these
  as literals at every call site (JLS 13.4.9), so a mixin on the field itself
  is a no-op; overriding them for real would need per-call-site
  `@ModifyConstant` targeting at every consuming class, which needs careful
  occurrence-count verification this sandbox can't safely do without a
  compiler against the real project. Left as an honest "not yet wired"
  tooltip rather than a fragile guess. `ALCHEMY_MAX_PILLS_PER_BATCH` /
  `REFINING_MAX_ITEMS_PER_BATCH` also not yet investigated at all.
- New files stub-compiled clean in this sandbox against hand-written stubs
  matching the real, javap-verified method signatures (same caveat as the
  Weapons batch — catches syntax/type errors only, not real Forge linkage).
  This pass also confirmed `CallbackInfoReturnable.getReturnValueI()` /
  `getReturnValueD()` (and the other primitive convenience getters) are real,
  valid Mixin 0.8.5 API — found already in use in the pre-existing, shipped
  `TechniqueBonusHelperMixin.java` code, not something to avoid going
  forward.

**Large batch, 2026-09-01 (continuation, "complete everything" pass):**

Methodology shift that changed the scope of this whole pass: a registration
audit (mixin `.java` files vs. `mixins.json`'s arrays — see the Python
snippet under "Methodology caveat" below) plus direct reading of several
"⬜ not started" categories' existing mixin files found that most of them
were **already substantially wired** by mixins that simply predated this
audit and were never checked off: `FoundationDaoMixin` (18/21),
`GoldenCoreDaoMixin` (44/45), `PhysiqueMixin` (all 6 rarity-weight fields),
`SpiritRootMixin` (10/13), `SpiritVeinCoreTierMixin` (15/17),
`NpcCombatThreatDetectorMixin` (33/36), most `FormationType*Mixin` files,
`MoralityHelperMixin` (6/8), `LifespanHelperMixin` (2 of the pre-existing
fields), `SectSavedDataMixin` (7 unrelated sect fields). This turned the
task from "write everything from scratch" into "verify real coverage, then
close the genuine remaining gaps" — dispatched 4 parallel research
subagents to do bulk javap reconnaissance across ~230 fields in 9+
categories to speed this up, each returning bytecode-grounded verdicts,
then independently re-verified every finding myself before writing code
(this caught 3 real subagent inaccuracies before any code was written from
them — see below).

Closed for real this pass (bytecode-verified via my own `javap -p -c -s`
reads, not just subagent reports):

- **Foundation/Golden Core Dao** (24 fields): bone-age-eligibility limits
  and Foundation tribulation-wave counts, via 4 new `@ModifyConstant`/
  `@Inject` methods added to `CultivationProgressionRulesMixin`.
- **Physiques** (12 fields): new `PhysiqueBonusMixin`, following the
  existing `SpiritRootMixin` per-identity-record-rebuild pattern.
- **Spirit Roots** (3 remaining fields) + **a real bug fix**: the
  pre-existing `SpiritRootBonusHelperMixin` used a flawed value-heuristic
  (`if (original == 1.5) ...`) with a dead second branch and a genuine
  ambiguity — a common-rarity root with an active environment buff also
  produces exactly 1.5, indistinguishable from SSR rarity at the
  return-value level. Rewritten using 4 ordinal-scoped `@ModifyConstant`
  injectors targeting the exact bytecode sites instead, eliminating the
  ambiguity.
- **Spirit Plants** (12 fields, was 0/12): full rewrite of the abandoned
  placeholder — see "Other findings" below for why the old "too complex"
  excuse didn't hold up.
- **Spirit Veins** (2 remaining fields): new `SpiritVeinCoreBlockEntityMixin`.
- **NPC Combat** (2 remaining fields): extended the existing mixin;
  `NPC_COMBAT_STALEMATE_TIMEOUT` confirmed to have no real mechanism.
- **Formations** (47 fields, was 0 registered even though the config
  section existed): new `FormationCorePlateBlockEntityMixin`, plus default
  corrections for 12 Formation Core fields whose config defaults didn't
  match the base mod's real values (safe to correct since none were live
  before).
- **Progression** (most of the section): bone-age-limit duplicates
  documented rather than double-wired; a genuine 3-way branch mislabeling
  found and fixed for both Foundation and Golden Core "higher route
  estimate days" fields (see below); NPC tribulation death-chance/weakness-
  days wired via 2 new methods on `SectSavedDataMixin`; gender-edits-default
  wired via a from-scratch `CultivationDataMixin` (was an empty
  placeholder); NPC-higher-dao-lifespan-reserve-threshold wired (default
  corrected 0.5→1.0, never live before); mortal-equipped-technique-base-mult
  wired (a subagent had reported this as a dead constant — wrong, see
  below).
- **Morality** (2 fields): `MIN_VALUE`/`MAX_VALUE` wired via 6 constant
  sites across `clamp`/`add`; defaults corrected from ±1000 to the real
  ±1,000,000 (never live before, range widened to fit).
- **Lifespan Helper** (all remaining fields): start-bone-age min/max wired
  via `IdentityDrawHandlerMixin` — this also fixed a real gap in that file's
  own custom-identity code, which had been hardcoding `14 + RNG.nextInt(5)`
  in 3 places and silently ignoring these config fields even when set;
  age-per-day wired via new `LifespanHandlerMixin`; global-multiplier wired
  via a RETURN-scaling injection since `lifespanCap` has no single literal
  to target; age-per-day-while-meditating confirmed to have no real
  mechanism (base mod ages the same rate regardless).
- **Qi System** (4 of ~15 fields): player attraction radius / meditation
  range & efficiency bonuses via new `PlayerQiConsumerMixin`; qi-per-damage
  via new `QiShieldHandlerMixin` (noted a real type mismatch — the config
  field is a `LongValue`, the real target is a `double`). `QI_SHIELD_
  PERFECT_REDUCTION` and the 10 `QI_STONE_ORE_*` fields (a dense static
  initializer with many similar constants) still pending — left honestly
  unwired rather than rushed.

**Three subagent-report inaccuracies caught by independent re-verification
before any code was written from them** (this project's "no fabrication"
rule in action — parallel research subagents speed up reconnaissance but
their findings are treated as leads to verify, not ground truth):

1. `applyMaze()` was reported as having only 2 occurrences of its duration
   literal (both safe to blanket-override); direct javap showed 3 — the
   third is an unrelated maze-grid loop bound. Using an un-ordinaled
   override as reported would have corrupted maze generation. Fixed with
   explicit `ordinal=0`/`ordinal=1`, leaving the third alone.
2. `estimateHigherFoundationRouteDays`/`estimateHigherGoldenCoreRouteDays`
   were reported as simple 2-way branches (Earth-value / else-value);
   direct javap showed a 3-way branch (HEAVEN→0, EARTH→30/60, else→12/24)
   with the HEAVEN case omitted from the report entirely. Since the config
   fields are named after the dao they were *guessed* to represent (not the
   one they actually match), each was wired to whichever real branch its
   own default value matches, with the mismatch documented honestly in both
   the mixin and `ExtendedConfig.java` rather than renaming fields and
   breaking existing configs.
3. `PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT` was reported as a dead
   exported constant with no live reader anywhere in the jar. Re-verifying
   `baseAbsorbMultiplier(...)`'s own bytecode directly showed that was
   wrong: it has a real fallback path (a single `dconst_1`) used when a
   MORTAL-realm entity has an equipped technique but its realm defines no
   base absorb mult of its own. Wired for real; default already matched.

**Bug fixes made along the way (not new features, corrections to existing
shipped code):**
- `SECT_SAFE_TICK`'s tooltip described stale, superseded behavior — see
  "Other findings" below.
- `SpiritRootBonusHelperMixin`'s qi-absorption ambiguity — see above.
- `IdentityDrawHandlerMixin`'s 3 hardcoded `14 + RNG.nextInt(5)` bone-age
  rolls, found while wiring the Lifespan fields — see above. (Caught my own
  mistake mid-fix, too: the first `replace_all` edit only matched 2 of the
  3 call sites because the third lacked the preceding comment line the
  pattern matched on — found and fixed by grepping for the literal after
  editing, before moving on.)

**Verification this batch:** every new/edited file stub-compiled clean —
individually as written, and then all 14 touched mixin files plus the full
`ExtendedConfig.java` compiled together in one final consolidated pass
(zero errors). `ExtendedConfig.java` brace/paren-balance checked
(9/9 braces, 2318/2318 parens) after all edits. `xiaoxiang_config_ext.
mixins.json` validated as parseable JSON after each registration (now 78
`mixins` + 8 `client` = 86 total, up from 83 at the start of this batch: 6
new files — `PhysiqueBonusMixin`, `SpiritVeinCoreBlockEntityMixin`,
`FormationCorePlateBlockEntityMixin`, `LifespanHandlerMixin`,
`PlayerQiConsumerMixin`, `QiShieldHandlerMixin` — plus `SpiritPlantBlockMixin`
newly registered and `CultivationDataMixin` filled in. `DistanceManagerMixin`
and `CultivationScreenUIScaleMixin` remain correctly unregistered
placeholders.

**Not yet done, still real remaining scope:** Sects (~126 fields, the
largest remaining category, not researched at all this session), Loot (15),
Trials (10), Identity (19), the rest of Qi System (`QI_SHIELD_
PERFECT_REDUCTION` + 10 `QI_STONE_ORE_*` fields in a dense static
initializer), Passive Spells/Void/Palm Thunder/Glacier Burial (~25 fields,
method-level targets already identified but not wired), Effects Inverse (5
fields, all in `PhysiqueBonusHelper`, targets identified but not wired).

## Other findings worth the user's attention (2026-09-01)

**`DistanceManagerMixin.java` — RESOLVED (2026-09-01, later same day, with
the user's explicit go-ahead).** Was an abandoned, non-functional crash-fix
attempt: never registered, its own `@Inject` target method name didn't
resolve under this project's mappings (confirmed via `build_log.txt`), and
its body was empty besides. Investigated further: no vanilla Minecraft/Forge
jar is available in this sandbox to `javap` `DistanceManager` directly, so a
real, verified fix targeting vanilla chunk-ticket internals could not be
built here — guessing at vanilla `@Overwrite`/`@Inject` code without being
able to disassemble the real target risks corrupting chunk loading, which is
worse than an occasional crash. Converted the file to an honest "please
delete by hand" placeholder (matching the existing pattern already used by
`CultivationScreenUIScaleMixin.java`) documenting the investigation,
including a reasoned-but-unconfirmed inference that the base mod's own
`DeferredSectNpcSpawnerSafeTickMixin` (already registered, wraps
`tickSectIBackfill` — a world-gen call exactly of the kind that can reenter
chunk-ticket handling — in `try/catch(Throwable)`, gated by `SECT_SAFE_TICK`,
default on) may already be catching this exact crash. If it recurs with
`SECT_SAFE_TICK` on, the user was asked to send the real stack trace so the
actual call site can be pinpointed instead of inferred. Also caught and
fixed in passing: `SECT_SAFE_TICK`'s own tooltip described stale, superseded
behavior (implying it still touched NPC spawning) that no longer matches
`DeferredSectNpcSpawnerSafeTickMixin`'s current, narrower implementation.

**`SpiritPlantBlockMixin.java` — RESOLVED (2026-09-01, later same day).**
Was the empty, unregistered, intentional placeholder mentioned below, whose
own comment claimed the fix "requires constructor redirection which is
complex." That claim was independently checked via javap and found to be
simply wrong — the real target (`getMaxAge()`, `m_7419_()` under this
project's mappings) is a trivial `iconst_3; ireturn`, no constructor
involved at all. Fully rewritten (all 12 `SPIRIT_PLANT_*` fields) and
registered — see the category-11 row above and the session log.

## Methodology caveat: "referenced somewhere" isn't the same as "wired"

The `TieredWeaponMixin` discovery above exposes a real gap in the dead-field
detector: it only proves a field's name appears in some other `.java` file,
not that the code containing it actually runs. A `@Mixin` class that exists
on disk but is missing from `xiaoxiang_config_ext.mixins.json` is never
loaded by Forge/Mixin at all — so a field can pass the text-search check
while still doing nothing in-game, exactly like `WEAPON_SPELL_QI_COST_
REDUCTION_LOW/MID/HIGH/SUPREME/IMMORTAL` did until today. Ran a one-off
check this session comparing every `.java` file in `mixin/` against both
arrays in `xiaoxiang_config_ext.mixins.json`; besides `TieredWeaponMixin`
(now fixed), two more turned up unregistered: `SpiritPlantBlockMixin`
(already known — a self-documented, intentional placeholder, correctly not
registered) and `DistanceManagerMixin` (a genuine unfinished bug fix — see
"Other findings" above). Worth re-running this specific check (mixin
`.java` files vs. mixins.json's `mixins`+`client` arrays) at the start of
future sessions, since it catches a class of bug the regex-based dead-field
list cannot see at all:

```
python3 -c "
import json, glob, os
d = json.load(open('src/main/resources/xiaoxiang_config_ext.mixins.json'))
registered = set(d['mixins']) | set(d['client'])
files = set(os.path.splitext(os.path.basename(p))[0] for p in glob.glob('src/main/java/com/xiaoxiang/configext/mixin/*.java'))
print('Unregistered mixin files:', sorted(files - registered))
print('Registered names missing a file:', sorted(registered - files))
"
```

## Important note on sandbox vs. live project (2026-09-01)

This session's sandbox copy of the project can drift from what's actually on
the user's Desktop — found two examples this session: `SpiritPlantBlockMixin.
java` (present on both, just hadn't been read yet) and `CultivationScreenUI
ScaleMixin.java` (present in the sandbox, absent from the real device, not
registered in mixins.json either way — harmless orphan, not a delivery gap,
but worth a `device_list_dir` diff check before trusting the sandbox blindly
in a future session). Before editing a file that will be committed back:
compare its sandbox size/mtime against a fresh `device_list_dir` of the real
path first. If they don't line up, stage the real file fresh rather than
assume the sandbox is current.

## Full remaining dead-field list (278 fields, as of 2026-09-01)

Down from 303 at the start of the audit: 11 wired via `PillEffectSpecsMixin.
java` (Pills category), 6 via `CultivationHudMixin.java` (Client HUD
accessibility: visibility toggle, 4 bar colors, HUD text color used in 2
places counted as 1 field, bar row-height), 4 via `WeaponDamageHandler.
java` + `BloodthirstBladeItemMixin.java` (Weapons category: global damage
multiplier, blood spell damage/qi-reduction multipliers, blood capacity
multiplier), and 4 via extending `PhysiqueBonusHelperMixin.java` +
`TechniqueBonusHelperMixin.java` (Alchemy/Refining categories:
`ALCHEMY_HEART_QI_COST_MULT`, `ALCHEMY_HEART_SUCCESS_BONUS`,
`REFINING_TIER_UP_CHANCE_DIVINE_FORGE`, `REFINING_TIER_UP_CHANCE_HEAVENLY_
ELIXIR` — the last of which also got a real mislabeling fix, see session log
above). Separately, the 5 `WEAPON_SPELL_QI_COST_REDUCTION_*` fields
were already correctly coded but their mixin was never registered - now
fixed, but they weren't on this dead list before that fix either (the text
search doesn't know a mixin needs registering - see the methodology caveat
above), so registering them doesn't move the count.

Everything below was checked and confirmed to have **zero** references
anywhere outside `ExtendedConfig.java` — meaning changing it in the config
screen currently has no effect on the game at all. Regenerate this list any
time with (note: this version strips comments before searching — see the
caveat below for why that matters):

```
python3 -c "
import re, glob

def strip_comments(text):
    text = re.sub(r'/\*.*?\*/', ' ', text, flags=re.DOTALL)
    text = re.sub(r'//[^\n]*', ' ', text)
    return text

src_raw = open('src/main/java/com/xiaoxiang/configext/config/ExtendedConfig.java').read()
src = strip_comments(src_raw)
fields = sorted(set(re.findall(r'ForgeConfigSpec\.\w+Value(?:<[^>]*>)?\s+(\w+);', src)))
other = ''.join(strip_comments(open(p, encoding='utf-8', errors='ignore').read()) for p in glob.glob('src/main/java/**/*.java', recursive=True) if not p.endswith('ExtendedConfig.java'))
dead = [n for n in fields if not re.search(r'\\b'+re.escape(n)+r'\\b', other)]
print(len(dead)); print(', '.join(dead))
"
```

ALCHEMY_INPUT_SLOTS, ALCHEMY_MAX_PILLS_PER_BATCH, ALCHEMY_OUTPUT_SLOTS,
ALCHEMY_TICKS_PER_PILL, BLOOD_BURN_PILL_DAMAGE_HIGH,
BLOOD_BURN_PILL_DAMAGE_IMMORTAL, BLOOD_BURN_PILL_DAMAGE_LOW,
BLOOD_BURN_PILL_DAMAGE_MID, BLOOD_BURN_PILL_DAMAGE_SUPREME,
BLOOD_BURN_PILL_DURATION_TICKS, BUDDHA_FIRE_LOTUS_READY_QI,
CHIYAN_BURN_TICKS, CLEAR_MIND_PILL_DURATION_HIGH,
CLEAR_MIND_PILL_DURATION_SUPREME, CLIENT_BG_OPACITY, CLIENT_BG_PAGE_COLOR,
CLIENT_BG_PANEL_COLOR, CLIENT_BORDER_DARK_COLOR, CLIENT_BORDER_LIGHT_COLOR,
CLIENT_CONFIG_HISTORY, CLIENT_CULT_BAR_X, CLIENT_CULT_BAR_Y,
CLIENT_GOLD_TEXT_COLOR, CLIENT_HIGH_CONTRAST_MODE, CLIENT_HUD_X,
CLIENT_HUD_Y, CLIENT_INFO_TEXT_X, CLIENT_INFO_TEXT_Y, CLIENT_INK_BLACK_COLOR,
CLIENT_INK_SOFT_COLOR, CLIENT_PORTRAIT_SIZE, CLIENT_PORTRAIT_X,
CLIENT_PORTRAIT_Y, CLIENT_QI_BAR_X, CLIENT_QI_BAR_Y, CLIENT_REALM_NAME_X,
CLIENT_REALM_NAME_Y, CLIENT_REALM_TEXT_COLOR, CLIENT_SPELL_GRID_X,
CLIENT_SPELL_GRID_Y, CLIENT_VERMILLION_COLOR, CORE_SELF_DESTRUCT_READY_QI,
CULTIVATION_EQUIPPED_SLOT_COUNT, CULTIVATION_TRIBULATION_CHARGE_TICKS,
CULTIVATION_TRIBULATION_INTERVAL_TICKS,
CULTIVATION_ZHENYUAN_ATTR_REWARD_MAJOR,
CULTIVATION_ZHENYUAN_ATTR_REWARD_MINOR, DIVINE_STRIDE_DURATION_HIGH,
DIVINE_STRIDE_DURATION_LOW, DIVINE_STRIDE_DURATION_MID,
DIVINE_STRIDE_DURATION_SUPREME, DIVINE_STRIDE_SPEED_HIGH,
DIVINE_STRIDE_SPEED_LOW, DIVINE_STRIDE_SPEED_MID,
DIVINE_STRIDE_SPEED_SUPREME, EFFECT_INVERSE_BASE_FIVE_ELEMENT_COST_MULT,
EFFECT_INVERSE_BASE_FIVE_ELEMENT_DMG_MULT,
EFFECT_INVERSE_MARK_DURATION_TICKS,
EFFECT_INVERSE_STACK_COST_REDUCTION_PER_LAYER,
EFFECT_INVERSE_STACK_DAMAGE_PER_LAYER, ENABLE_PROGRESSION_OVERRIDES,
ENABLE_QI_SYSTEM_OVERRIDES, ENABLE_SPIRIT_PLANT_OVERRIDES,
FORMATION_DEFAULT_FLAG_EFFECT_RADIUS, FORMATION_EFFECT_DURATION_TICKS,
FORMATION_EFFECT_INTERVAL_TICKS, FORMATION_FARM_HARVEST_CHECKS_PER_TARGET,
FORMATION_FARM_HARVEST_MAX_CHECKS, FORMATION_FARM_HARVEST_MIN_CHECKS,
FORMATION_GENERATED_ARRAY_SYNC_INTERVAL_TICKS,
FORMATION_GROWTH_TICK_INTERVAL_TICKS, FORMATION_MAX_FLAG_EFFECT_RADIUS,
FORMATION_MIN_FLAG_EFFECT_RADIUS,
FORMATION_RELOAD_FLAG_VALIDATION_GRACE_TICKS,
FORMATION_STORAGE_CORE_INTERVAL_TICKS,
FOUNDATION_DAO_EARTH_TRIBULATION_WAVES, FOUNDATION_DAO_HEAVEN_BONE_AGE_LIMIT,
FOUNDATION_DAO_HEAVEN_TRIBULATION_WAVES, FOUNDATION_MATERIAL_DAO_FRUIT_QI,
FOUNDATION_MATERIAL_USE_TICKS, FOUNDATION_MATERIAL_ZHUJI_DAN_QI,
GLACIER_BURIAL_BASE_QI_PER_TICK, GLACIER_BURIAL_NPC_CHANNEL_TICKS,
GOLDEN_CORE_DAO_HEAVEN_BONE_AGE_LIMIT,
GOLDEN_CORE_MATERIAL_CREATION_FRUIT_QI, GOLDEN_CORE_MATERIAL_JIEDAN_PILL_QI,
GOLDEN_CORE_MATERIAL_USE_TICKS, HANBING_FROZEN_OVERFLOW_TICKS,
LIFESPAN_AGE_PER_DAY, LIFESPAN_AGE_PER_DAY_MEDITATING,
LIFESPAN_START_BONE_AGE_MAX, LIFESPAN_START_BONE_AGE_MIN,
LOOSE_IMMORTAL_BASE_REDUCTION_PERCENT,
LOOSE_IMMORTAL_FULL_REDUCTION_TRIBULATIONS, LOOSE_IMMORTAL_INTERVAL_YEARS,
LOOSE_IMMORTAL_MAX_TRIBULATIONS, LOOSE_IMMORTAL_WARNING_TICKS,
MORALITY_MAX_VALUE, MORALITY_MIN_VALUE, NPC_AI_DECISION_INTERVAL_TICKS,
NPC_AI_SURVIVAL_RESERVE_FRACTION, NPC_COMBAT_MAX_CANDIDATES,
NPC_COMBAT_PROJECTILE_SCAN_RADIUS, PALM_THUNDER_ARMING_TICKS,
PALM_THUNDER_CHANNEL_QI_PER_SECOND, PASSIVE_BIGU_SATURATION,
PASSIVE_ITEM_ATTRACTION_QI_PER_ITEM, PASSIVE_QI_FLIGHT_BASE_SPEED,
PASSIVE_QI_FLIGHT_DRAIN_INTERVAL, PASSIVE_QI_MENDING_INTERVAL,
PASSIVE_QI_MENDING_QI_PER_DURABILITY, PASSIVE_TREASURE_SEIZING_QI_PER_STACK,
PASSIVE_TREASURE_SEIZING_STACKS_PER_SECOND, PHYSIQUE_BROKEN_VEIN_HP_MULT,
PHYSIQUE_BROKEN_VEIN_MELEE_DMG_MULT,
PHYSIQUE_HEAVENLY_FIRE_BODY_FIRE_SPELL_MULT, PHYSIQUE_IMMORTAL_BLOOD_HP_MULT,
PHYSIQUE_IMMORTAL_BODY_DAMAGE_TAKEN_MULT,
PHYSIQUE_IMMORTAL_BODY_MAX_HP_BONUS, PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_MULT,
PHYSIQUE_IMMORTAL_BODY_QI_ABSORB_RANGE, PHYSIQUE_IMMORTAL_BODY_QI_COST_MULT,
PHYSIQUE_INNATE_SWORD_BODY_SWORD_SPELL_MULT,
PHYSIQUE_MYSTIC_ICE_BODY_WATER_SPELL_MULT,
PHYSIQUE_SWORD_BONE_SWORD_SPELL_MULT, PILL_USE_TICKS,
PROGRESSION_FOUNDATION_EARTH_ESTIMATE_DAYS,
PROGRESSION_FOUNDATION_HEAVEN_BONE_AGE_LIMIT,
PROGRESSION_FOUNDATION_HEAVEN_ESTIMATE_DAYS,
PROGRESSION_GENDER_EDITS_DEFAULT,
PROGRESSION_GOLDEN_CORE_EARTH_ESTIMATE_DAYS,
PROGRESSION_GOLDEN_CORE_HEAVEN_BONE_AGE_LIMIT,
PROGRESSION_GOLDEN_CORE_HEAVEN_ESTIMATE_DAYS,
PROGRESSION_MORTAL_EQUIPPED_TECHNIQUE_BASE_MULT,
PROGRESSION_NPC_HIGHER_DAO_LIFESPAN_RESERVE_THRESHOLD,
PROGRESSION_NPC_TRIBULATION_DEATH_CHANCE,
PROGRESSION_NPC_TRIBULATION_FAILURE_WEAKNESS_DAYS,
PROGRESSION_NPC_TRIBULATION_WEAKNESS_DAYS, QINGMU_POISON_TICKS,
QI_SHIELD_PERFECT_REDUCTION, QI_SHIELD_QI_PER_DAMAGE,
QI_STONE_ORE_MAX_QI_HIGH, QI_STONE_ORE_MAX_QI_LOW, QI_STONE_ORE_MAX_QI_MID,
QI_STONE_ORE_MAX_QI_SPIRIT_VEIN_SPRING, QI_STONE_ORE_MAX_QI_SUPREME,
QI_STONE_ORE_REGEN_HIGH, QI_STONE_ORE_REGEN_LOW, QI_STONE_ORE_REGEN_MID,
QI_STONE_ORE_REGEN_SPRING, QI_STONE_ORE_REGEN_SUPREME,
QI_SYSTEM_ATTRACTION_RADIUS, QI_SYSTEM_MEDITATION_EFFICIENCY_BONUS,
QI_SYSTEM_MEDITATION_RANGE_BONUS, REFINING_INPUT_SLOTS,
REFINING_MAX_ITEMS_PER_BATCH, REFINING_OUTPUT_SLOTS, REFINING_TICKS_PER_ITEM,
SEA_CHANNEL_TICKS, SECT_AMBIENT_CHECK_INTERVAL_TICKS,
SECT_AMBIENT_MAX_COOLDOWN_TICKS, SECT_AMBIENT_MAX_SCENES,
SECT_AMBIENT_MAX_SPECTATORS, SECT_AMBIENT_MIN_COOLDOWN_TICKS,
SECT_AMBIENT_NPC_COOLDOWN_TICKS, SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_0,
SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_1, SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_2,
SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_3, SECT_ANCESTOR_IMMORTAL_CHANCE_POWER_4,
SECT_ANCESTOR_LOOSE_IMMORTAL_CHANCE, SECT_BUBBLE_MAX_DURATION_TICKS,
SECT_BUBBLE_MIN_DURATION_TICKS, SECT_DEFENSE_CRITICAL_RESPONDER_LIMIT,
SECT_DEFENSE_DEATH_RESPONDER_LIMIT, SECT_DEFENSE_ESCAPE_RADIUS,
SECT_DEPARTMENT_FIRST_SHIFT_END, SECT_DEPARTMENT_FIRST_SHIFT_START,
SECT_DEPARTMENT_SECOND_SHIFT_END, SECT_DEPARTMENT_SECOND_SHIFT_START,
SECT_DEPT_WORK_CREDIT_INTERVAL_TICKS, SECT_DISCIPLE_REALM_GATE,
SECT_DISCIPLE_SUB_STAGE_GATE, SECT_DISTANT_CATCH_UP_DAY_CAP,
SECT_ELDER_DISCIPLE_TARGET, SECT_EVENT_LIMIT,
SECT_FULL_SIMULATION_PLAYER_RADIUS, SECT_JOURNEY_CHUNK_RADIUS,
SECT_JOURNEY_DATA_PHASE_TICKS, SECT_JOURNEY_ENTITY_MISSING_GRACE_TICKS,
SECT_JOURNEY_ENTITY_RELOAD_WAIT_TICKS, SECT_JOURNEY_RETURN_FALLBACK_TICKS,
SECT_JOURNEY_STUCK_TICKS, SECT_LIFE_TICK_INTERVAL,
SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT, SECT_MAX_POWER_SCORE,
SECT_MEMBER_PERSONAL_INVENTORY_SLOT_LIMIT, SECT_MIN_SPACING_PER_TIER,
SECT_OVERHEAD_PANEL_FADE_IN_TICKS, SECT_OVERHEAD_PANEL_FADE_OUT_TICKS,
SECT_PERFORMANCE_PLAYER_RADIUS, SECT_PERFORMANCE_QUEUE_LIMIT,
SECT_PERFORMANCE_TIMEOUT_TICKS, SECT_PROFILE_MAX_MORTAL_REALM_PROGRESS,
SECT_PROFILE_MAX_ORDINARY_ROLE_SPAN, SECT_PROFILE_MAX_POWER_SCORE,
SECT_PROFILE_MIN_CULTIVATOR_PROGRESS, SECT_PROFILE_MIN_MASTER_PROGRESS,
SECT_PROFILE_MIN_ORDINARY_ROLE_SPAN, SECT_RECRUIT_DISCIPLE_CHANCE,
SECT_SHOP_SELL_PERCENT, SECT_SHOP_SPELL_PRICE_HIGH,
SECT_SHOP_SPELL_PRICE_IMMORTAL, SECT_SHOP_SPELL_PRICE_LOW,
SECT_SHOP_SPELL_PRICE_MID, SECT_SHOP_SPELL_PRICE_SUPREME,
SECT_SHOP_TECHNIQUE_PRICE_HIGH, SECT_SHOP_TECHNIQUE_PRICE_IMMORTAL,
SECT_SHOP_TECHNIQUE_PRICE_MID, SECT_SHOP_TECHNIQUE_PRICE_SUPREME,
SECT_SHOP_WEAPON_PRICE_HIGH, SECT_SHOP_WEAPON_PRICE_IMMORTAL,
SECT_SHOP_WEAPON_PRICE_LOW, SECT_SHOP_WEAPON_PRICE_MID,
SECT_SHOP_WEAPON_PRICE_SUPREME, SECT_TASK_EXPEDITION_MAX_DAYS,
SECT_TASK_EXPEDITION_MIN_DAYS, SECT_TASK_JOURNEY_MAX_TIMEOUT_TICKS,
SECT_TASK_JOURNEY_MIN_TIMEOUT_TICKS, SECT_TASK_MAX_ESCROW_STACKS,
SECT_TASK_MAX_REQUIRED_COUNT, SECT_TASK_MAX_SYSTEM_PURCHASES,
SECT_TASK_MAX_SYSTEM_PURCHASE_TASKS, SECT_WAREHOUSE_SLOT_LIMIT,
SHADOW_STEP_PILL_DURATION_TICKS, SOUL_HOOK_ATTACK_DAMAGE,
SPELL_CHARGE_GLOBAL_MULTIPLIER, SPIRIT_PLANT_EARTH_MARROW_GROWTH_CHANCE,
SPIRIT_PLANT_EARTH_MARROW_GROWTH_RADIUS, SPIRIT_PLANT_FLAME_MELT_RADIUS,
SPIRIT_PLANT_GOLDEN_CHRYSANTHEMUM_DROP_CHANCE, SPIRIT_PLANT_GROWTH_TICK_BASE,
SPIRIT_PLANT_MAX_AGE, SPIRIT_PLANT_QI_ORB_AMOUNT, SPIRIT_PLANT_SKIP_COUNT,
SPIRIT_PLANT_SKIP_RADIUS, SPIRIT_PLANT_SNOW_MAX_LAYERS,
SPIRIT_PLANT_SNOW_PLACE_ATTEMPTS, SPIRIT_PLANT_SNOW_RADIUS,
SPIRIT_STONE_QI_HIGH, SPIRIT_STONE_QI_LOW, SPIRIT_STONE_QI_MID,
SPIRIT_STONE_QI_SUPREME, SPIRIT_STONE_USE_TICKS, SPIRIT_VEIN_ATTRACT_RADIUS,
SPIRIT_VEIN_SUPPLY_RADIUS, STORAGE_BAG_VISIBLE_ROWS_MAX,
SWORD_FLIGHT_UPKEEP_QI_PER_SECOND, TIME_ACCELERATION_MAX,
TIME_ACCELERATION_MIN, TRIAL_INNER_WORLD_FAILURE_HEALTH_PENALTY_PERCENT,
TRIBULATION_BOLT_COOLDOWN_TICKS, TRIBULATION_CHARGE_TICKS,
TRIBULATION_INTERVAL_TICKS, VOID_ESCAPE_ACTIVE_QI_PER_TICK,
VOID_ESCAPE_CHARGE_QI_PER_TICK, VOID_ESCAPE_CHARGE_TICKS,
VOID_STEP_AIR_JUMP_QI_COST, VOID_STEP_DASH_QI_COST,
VOID_STEP_SLOW_FALL_QI_COST, WEAPON_ATTACK_SPEED_MODIFIER,
YOUTH_PILL_BONE_AGE_REDUCTION, YOUTH_PILL_MIN_BONE_AGE,
ZHENYUAN_ATTR_REWARD_MAJOR, ZHENYUAN_ATTR_REWARD_MINOR,
ZHENYUAN_REWARD_MAJOR, ZHENYUAN_REWARD_MINOR, ZHENYUAN_STAT_MULTIPLIER

## Important caveat on this list

"No consumer found" is a static text-search result (regex word-boundary
match across every other `.java` file). It does **not** catch a value read
only via Java reflection by field name string (checked — the one generic
reflection scanner in this codebase, `ConfigValueAccessor`, iterates
`getDeclaredFields()` generically and never names individual fields, so it
does not hide any real consumer). It also does not by itself prove a field
*should* be wired — some of these may turn out, after javap inspection, to
correspond to a base-mod system that no longer exists in this form (like the
old hardcoded pill values did), in which case the honest fix is a comment
explaining that, not a fabricated mixin. Verify each one before wiring.

**Comment-pollution false-negative, found and fixed 2026-09-01:** the first
version of the detection script (a plain word-boundary regex over raw file
text) produced an inaccurate list once `CultivationHudMixin.java` grew a
class-level Javadoc that *names* the fields it deliberately did NOT wire
(e.g. writes "CLIENT_HUD_X" in prose to explain why). The regex matched
those names inside the comment text itself and wrongly counted the fields
as "used," making genuinely-still-dead fields silently vanish from the
list. Caught by spot-checking known-dead names against the generated
output. Fixed by stripping `/* */` block comments and `//` line comments
from every source file (including `ExtendedConfig.java` itself, whose
field-declaration regex also runs on the comment-stripped text) before
running the word-boundary search — that's the `strip_comments()` step in
the regen command above. Any future rewrite of this detector must keep that
step, or documenting a deliberately-unwired field by name (which is good
practice — see `CultivationHudMixin`'s own class doc) will quietly corrupt
this list again.

## Base mod migration to 0.1.1479, and new-configurables audit (2026-09-02)

The user uploaded a new build of the base mod, `xiaoxiang_cultivation-0.1.1479.jar`
(up from `0.1.1302.jar`), to replace in `libs/` and in `build.gradle`'s
`compileOnly fg.deobf(...)` version string. This section documents (a) everything
that broke and was fixed to keep the existing companion mod compiling/working
against the new jar, and (b) a first wave plus a full backlog from a "deep audit"
for brand-new tunable constants the update introduced, per the user's explicit
request to keep researching until every configurable-shaped value is found.

### Part A — compile/regression fixes for the 0.1.1479 jar

Methodology: extracted both jars, diffed the class lists (1116 classes added, 13
removed, 1018 of 1878 common classes have changed bytecode), cross-referenced every
`@Mixin` target against the removed-class list (found the one guaranteed
compile-blocker), then ran a scripted method/lambda-existence scan across the ~66
changed classes this project actually targets, manually verifying every flagged
item against the OLD jar too (to rule out inherited-method and constant-pool-shift
false positives) before treating anything as a real regression.

**Real regressions found and fixed:**

- `BeastRealm.class` was deleted outright (the whole class, not a rename). Its
  `advanceCost` logic moved to a new `com.xiaoxiang.cultivation.cultivation.beast.
  BeastProgressionRules` (a `public final class` of static methods, part of a
  redesigned 12-realm beast progression system). `BeastRealmMixin.java` was retired
  to an inert placeholder (ask the user to delete the file by hand, same convention
  as `DistanceManagerMixin.java`); a new `BeastProgressionRulesMixin.java` targets
  the real replacement class. 4 new config fields were added
  (`BEAST_BODY_INTEGRATION_ADVANCE_COST`, `BEAST_MAHAYANA_ADVANCE_COST`,
  `BEAST_TRIBULATION_TRANSCENDENCE_ADVANCE_COST`, `BEAST_TRUE_IMMORTAL_ADVANCE_COST`)
  since the new system has 4 more advanceable realms than the old 6-tier one.
- `CultivatorFlightCombatGoal.combatY` was split into `rangedCombatY` and
  `meleeCombatY`. `MIN_HEIGHT`/`MAX_HEIGHT` (3.0/15.0) moved intact into
  `rangedCombatY`; `meleeCombatY` is a genuinely new ground-hugging formula with no
  min/max-height concept at all. `CultivatorFlightCombatGoalMixin.java`'s method
  targets were updated to `rangedCombatY`.
- `CultivatorTrades.addHeldSwordOffer` was renamed to `addHeldWeaponOffer` (same
  signature, same 2 `MerchantOffer` call sites). `CultivatorTradesMixin.java`'s 5
  `@ModifyArg` targets and its prose comments were updated.
- `SectSavedData.trimWarehouse` was deleted; its logic folded into a new
  `addToWarehouse(SectRecord, List<ItemStack>)` (the 324 slot-limit literal is
  unchanged, still a single occurrence). `SectOperationsLimitsMixin.java`'s method
  target was updated from `trimWarehouse` to `addToWarehouse`.
- `SectSavedData.admitPhysicalJourney` gained a **second, unrelated** `iconst_3` (a
  3-element logger varargs array size, from a new failure-path log call) alongside
  the original physical-journey-count gate check. Without an ordinal, our existing
  `@ModifyConstant` would now also resize that logging array to the configured
  value, throwing `ArrayIndexOutOfBoundsException` whenever
  `SECT_MAX_PHYSICAL_JOURNEYS_PER_SECT` is set below 3. Fixed by pinning
  `ordinal = 0` in `SectOperationsLimitsMixin.java`. This one was subtle — it would
  not have shown up in any bytecode diff of the *target constant itself* (3 didn't
  change), only in a full re-read of the method body.
- Two lambda synthetic names shifted in `SectSavedData` (`lambda$
  tryRecruitLoadedLoneCultivator$75` → `$76`, `lambda$acquireJourneyTarget$55` →
  `$56`) because an unrelated lambda was added earlier in each enclosing method.
  Confirmed by matching each lambda's *parameter signature*, not just its index,
  between jars. `SectDiscipleGateMixin.java`'s two `@Redirect` method arrays were
  updated. With `require = 0` this would have failed silently (no crash, just a
  quietly-broken config option) rather than surfacing as a build error.
- The real Gradle build (run by the user, not this sandbox) failed on
  `IdentityDrawScreenMixin.java` with "no suitable method found for
  renderTooltip(Font,List<Component>,int,int)" — this was an unrelated,
  pre-existing bug (not caused by 0.1.1479) in a scrollable-tooltip feature added
  2026-09-01, now fixed: `GuiGraphics` only has 3 real `renderTooltip` overloads
  (`(Font,ItemStack,int,int)`, `(Font,Component,int,int)`,
  `(Font,List<? extends FormattedCharSequence>,int,int)` — confirmed by the real
  compiler's own error output). Fixed via a null-safe
  `Component.getVisualOrderText()` conversion helper
  (`configExt$toVisualOrder`), keeping the fix anchored to the
  compiler-confirmed overload rather than guessing a different method name.

**Re-verified, found to be false alarms (no fix needed):** `PhysiqueBonusHelper`
(flagged changed only because an unrelated new field shifted constant-pool
indices — every literal/opcode this project depends on is byte-for-byte
identical) and `IdentityDrawScreen` (same constant-pool-shift pattern from
several unrelated new methods being added to the class — `init`→`lambda$init$2`,
`renderIdentityCard`, `shiftSelection`, `selectedIdentity`, `layout()`,
`m_88315_`, the `ChooseOriginPacket` constructor, and every reflected field name
this mixin depends on are all confirmed byte-for-byte unchanged).

**Not yet investigated this session:** `SectAncestorProfile`, `SectDefense`,
`SectRecordEventLimit`, and the other ~60 changed-but-not-yet-flagged classes this
project targets were part of the original scripted scan and came back clean (no
missing methods/lambdas detected), but that scan only checks for
compile-breaking regressions (deleted/renamed methods, shifted lambda indices) —
it does **not** re-verify that every literal/numeric value inside those classes'
still-existing methods is unchanged. A class passing the scan means "this mixin
will still apply," not "every value it reads is still correct." Deeper
per-literal re-verification of all ~66 targeted-and-changed classes remains
future scope, same caveat as the rest of this document.

### Part B — deep audit for new configurable-shaped values (per user request)

The user explicitly asked for repeated, deep research into what new tunable
values the 0.1.1479 update itself introduces (not just regressions in existing
mixins) — i.e. brand-new hardcoded numbers in brand-new base-mod code that
deserve their own config options, same as everything else in this file.

**Method:** disassembled all 1116 newly-added classes with `javap -p -v` and
extracted every `public/private/protected static final int/long/float/double`
field carrying a `ConstantValue` attribute (compile-time constants — the only
kind that show up as literals in code and are worth exposing as config). That's
1439 raw candidates; filtering out UI/pixel-layout constants (everything under
the `.client.` package, plus obvious non-tunables like slot indices, storage
format versions, and array indices) leaves **888 non-trivial gameplay-shaped
candidates** across the new classes alone — before even considering the 1018
*changed* (pre-existing) classes, which weren't scanned this pass (a field added
to an existing class, like the `SECT_WAREHOUSE_SLOT_LIMIT` / `CHAOS_BODY_QI_
RECOVERY_MULTIPLIER` named constants noticed opportunistically in Part A, is
just as real a candidate and this backlog does not yet claim to have found all
of them).

**Top-line finding: base mod 0.1.1479 added an entire new "Talisman" glyph/rune
crafting subsystem** — 166 classes under `cultivation.talisman` /
`entity.talisman` / `cultivation.cultivation.talisman`, comparable in scope to
an existing major system of this mod. Its central constants class,
`TalismanLimits`, alone has **110 `public static final int/float` fields**: ~21
structural caps (`MAX_EXECUTABLE_SLOTS`, `MAX_GLYPH_ID_LENGTH`, `MAX_DELAY_TICKS`,
`MAX_PIERCE_COUNT`, `DEFAULT_EXPLOSION_RADIUS`, `MAX_EXPLOSION_RADIUS`,
`MAX_BATCH_CRAFTS`, etc.) and ~90 per-glyph `*_QI_COST` values (one for nearly
every spell/glyph type in the game, e.g. `FIREBALL_QI_COST`, `LIGHTNING_STRIKE_
QI_COST`, `STARFALL_QI_COST`...). **Confirmed via disassembly of the entire
166-class talisman package that there are zero `getstatic` references to
`TalismanLimits` fields anywhere** — every one of its 110 constants is inlined
at its use site(s) by javac, meaning each one requires individually locating and
verifying its real call site(s) before it can be wired, exactly like every other
fix in this project (no shortcut via a single `@Shadow`+`@Redirect` on the field
itself, since it's never actually read as a field at runtime).

**Wired Wave 1 (`TalismanCompilerMixin.java` + new `talisman` config section):**

- `TALISMAN_DEFAULT_EXPLOSION_RADIUS` (2.0, was `TalismanLimits.
  DEFAULT_EXPLOSION_RADIUS`) and `TALISMAN_MAX_EXPLOSION_RADIUS` (7.0, was
  `TalismanLimits.MAX_EXPLOSION_RADIUS`) — both traced to
  `TalismanCompiler.buildCompiledPlan(...)`'s `EXPLOSION` manifestation-type
  branch (verified: single, unambiguous occurrence of each literal within that
  one method).
- New `ENABLE_TALISMAN_OVERRIDES` toggle, following the same
  per-feature-toggle convention as every other system in this mod.

**Wired Wave 2 (2026-09-02, `TalismanGlyphCatalogMixin.java` + new
`talisman.glyphCosts` config section): 85 of the ~90 per-glyph `*_QI_COST`
fields, 82 distinct config fields.** Wave 1's own recommendation paid off:
`TalismanGlyphCatalog` (a sibling class to `TalismanCompiler`) turned out to
be exactly the single choke point predicted — its 7 private `register*(Map)`
methods each call one of 3 private factory methods (`trigger`/`evolution`/
`manifestation`) once per glyph, passing that glyph's cost as the factory's
FINAL int argument. Every one of the 85 call sites was extracted
*programmatically* (tracked each glyph's own `ResourceLocation` field read,
then the literal pushed immediately before the matching `trigger`/
`evolution`/`manifestation` invocation) and then cross-checked against
`TalismanLimits`' own declared field values by name — all 85 matched exactly
(76 by the per-glyph-prefixed naming convention, 9 "legacy" glyphs by a
shorter generic name: `TRIGGER_QI_COST` shared by 4 basic triggers,
`DAMAGE_QI_COST`/`DESTRUCTION_QI_COST`/`PIERCE_QI_COST`/`POTENCY_QI_COST`/
`DURATION_QI_COST` for 5 legacy evolutions). Each `@ModifyArg` is scoped with
an `ordinal` (the Nth call to that factory method within that one register
method) since every register method calls the same factory many times with
different literals. Stub-compiled clean against the full project tree
(including the real, current `ExtendedConfig.java`), not just this one file.

**Backlog — NOT yet wired (future waves, roughly ranked by how coherent/
self-contained the system looks, i.e. easiest to tackle next):**

1. **`TalismanLimits`'s remaining ~25 fields**: the ~21 structural caps
   (`MAX_EXECUTABLE_SLOTS`, `MAX_GLYPH_ID_LENGTH`, `MAX_DELAY_TICKS`,
   `MAX_PIERCE_COUNT`, `MAX_BATCH_CRAFTS`, etc. — 2 of these 21,
   `DEFAULT_EXPLOSION_RADIUS`/`MAX_EXPLOSION_RADIUS`, are already wired via
   Wave 1), plus a handful of `QI_COST`-named fields not found in
   `TalismanGlyphCatalog`'s call sites at all — e.g. `MODE_QI_COST`, which
   `TalismanQiCost.total()` reads via `TalismanUseMode.qiCost()`, a
   different, not-yet-traced instance-method source entirely (`TalismanUseMode`
   is an enum, so this may need a `$SwitchMap`-style approach like
   `BeastProgressionRulesMixin`, not a `@ModifyArg`/`@ModifyConstant`).
2. `SpellBalanceTable` (35 candidates) — likely a central spell-tuning table,
   possibly analogous to `TalismanLimits` (one lookup-style class, worth
   checking whether it's consumed similarly).
3. `ArmorSetBonusScaling` (29), `EquipmentTierScaling` (11),
   `AdvancedArmorSetEffectService` (11), `EquipmentSetEffectService` (9),
   `TreasureAbilityService` (16), `StormRiftAbilityService` (6) — a new
   equipment/armor-set-bonus system.
4. `KunlunRootStrikeGeometry` (23), `XuanGuiCombatController` (18),
   `XuanGuiEntity` (10), `BeastSpellCaster` (8) — a new boss/mini-boss ("Xuan
   Gui") with its own combat geometry constants.
5. `CultivationFruitGrowthHandler` (17) plus the new `CultivationFruitBlock` /
   `CultivationFruitBlockEntity` pair — a new fruit-growing block system (its
   `TICKS_PER_DAY`/`24000L` constant was already spot-checked, unrelated to
   growth tuning).
6. `NpcGiftDiminishingSavedData` (13), `NpcFavorabilityEconomy`,
   `NpcTalismanRules` (6), `NpcSectLoadoutRules` (6),
   `NpcObstacleBreachingGoal`/`NpcObstacleBreaching` (6+4),
   `NpcFlightRoutePlanner` (6) — several new NPC AI/economy subsystems.
7. `MainTechniqueDestiny` (10), `TechniqueStackingRules` (9),
   `TechniqueResonance` (9) — a new technique-stacking/resonance system.
8. `SectSiteSelector` (12, worldgen), `DharmaBodyProtectionGeometry` (12) +
   `DharmaBodyScaleRules` (7), `ThunderstruckPeachSwordItem` (10) +
   `PeachSwordItem` (9) + `PeachSwordStormImbueHandler` (8), `LiFireOrbEntity`
   (8), `FormationBarrierSnowCleanup` (5, already spot-checked for its own
   `MAX_SECTIONS_PER_TICK`/`MAX_CELLS_PER_TICK`/`MAX_MUTATIONS_PER_TICK` trio —
   not yet wired), `LightningWoodHandler` (4), `AzureFanGustHandler` (4).
9. `SpiritAltarVisualContract` (55) and `PoisonMistVisualContract` (25) — by
   name and field pattern (many `_Y`/`_X`/tilt/edge-style names) these look
   like pure VFX/particle geometry rather than gameplay balance, i.e. probably
   **not** worth wiring even in a later wave, but weren't individually
   inspected to confirm that judgment.
10. **Changed (pre-existing) classes were not scanned for newly-added constant
   fields at all this pass** (1018 classes) — only stumbled onto opportunistically
   during Part A's regression work. A systematic pass would diff each changed
   class's field list (old jar vs. new) the same way `SectSavedData`'s
   `SECT_WAREHOUSE_SLOT_LIMIT` was found, across all 1018.

**Honest scope statement:** this is genuinely a large task — the user's own
framing ("this will be a massive update as well for us") was accurate. Full
verified coverage of all ~888+ new-class candidates plus a systematic pass over
1018 changed classes is realistically many further sessions of work at the
verification rigor this project requires (every wiring decision individually
bytecode-traced, no fabrication). This section is meant to be the durable,
resumable backlog for that work, not a claim that it's done.

## Future-update hardening pass: `require = 0` audit (2026-09-02)

The user asked explicitly for this mod to "work on any future updates as
well" instead of just this one — i.e. when a *future* base-mod update
renames or removes a method this mod targets, the affected override should
silently degrade to "no override" instead of throwing a Mixin apply
exception that crashes the whole modpack at launch. This project's own
house convention (used everywhere a target was already known to be
uncertain) is `require = 0` on every `@Inject`/`@Redirect`/`@ModifyConstant`/
`@ModifyArg`/`@ModifyVariable` handler, since the global default in
`xiaoxiang_config_ext.mixins.json` (`injectors.defaultRequire: 1`) is
strict and crashes-on-missing-target unless overridden per-annotation.

**What was found:** a project-wide scan of every injection annotation
across all 112 mixin files (parsing each annotation's full paren-balanced
span, so nested annotations like `@Constant(...)` inside `@ModifyConstant`
don't get misread) found **126 injectors across 48 files** that had no
`require =` parameter at all, meaning they silently inherited the strict
global default of 1 — each one a single point of failure that would hard-
crash the entire mod (not just disable one override) the moment a future
base-mod update renamed or removed that specific target method.

**Fix:** mechanically inserted `, require = 0` immediately before each
affected annotation's closing paren, in reverse-offset order per file so
earlier insertions don't shift later ones. This is a purely syntactic
change — it adds one annotation attribute and references no new symbols,
targets, or logic — so it does not alter current behavior at all (every
target that exists today still applies exactly as before); it only changes
what happens the day a target stops existing, from "crash" to "that one
override quietly stops applying."

**Files patched (48 files, 126 injectors):**
`AlchemyCoreBlockEntityMixin`(1), `AlchemyRankMixin`(2),
`BeastCultivationHandlerMixin`(2), `BiomeQiProfileMixin`(1),
`BloodthirstBladeItemMixin`(3), `CreateWorldScreenMixin`(1),
`CultivationDataTimingMixin`(1), `CultivationHudMixin`(2),
`CultivationProgressionRulesMixin`(3), `CultivatorRealmRollerMixin`(1),
`FoundationDaoMixin`(8), `GameTabMixin`(1), `GoldenCoreDaoMixin`(15),
`GuiMixin`(1), `HeartDemonTrialProfileMixin`(1), `IdentityDrawDeckMixin`(3),
`IdentityDrawHandlerMixin`(2), `IdentityDrawSamplerMixin`(1),
`IdentityDrawScreenMixin`(3), `IdentityMixin`(2), `LifespanHelperMixin`(3),
`LooseImmortalBonusHelperMixin`(12), `ModCommonConfigMixin`(1),
`MoralityHelperMixin`(3), `NpcAttributeMixin`(1),
`NpcCombatThreatDetectorMixin`(2), `NpcSpellCasterMixin`(8),
`PhysiqueBonusMixin`(1), `PhysiqueMixin`(1), `PillEffectSpecsMixin`(6),
`QiSeaRecoveryMixin`(1), `QiTransferTickHandlerMixin`(3), `RealmMixin`(4),
`RefiningCoreBlockEntityMixin`(1), `RefiningRankMixin`(2),
`SectDepartmentTypeMixin`(3), `SectProtectionBarrierMixin`(1),
`SectSettlementFeatureMixin`(1), `SectSizeMixin`(1), `SpellMixin`(2),
`SpiritPlantBlockMixin`(2), `SpiritRootMixin`(1),
`SpiritVeinCoreBlockEntityMixin`(2), `SpiritVeinCoreTierMixin`(3),
`StorageBagItemMixin`(2), `VillageSuppressionMixin`(2),
`WanderingCultivatorEntityMixin`(2), `WanderingCultivatorSpawnMixin`(1).

**Deliberately left untouched:** `CultivationHudMixin.java` had 8 other
injectors with an *explicit* `require = 1` already written in by hand.
That's a deliberate author choice (those particular HUD hooks are meant to
hard-fail loudly if their target vanishes, rather than silently going
dark), not an oversight, so this pass respected it and did not touch them.
Every mixin file that already had `require = 0` (or an explicit
`require = 1`) on all of its own injectors needed no changes and isn't
listed above.

**Verification:** a full stub-compile of all 112 mixin files at once isn't
supported by this project's incremental stub tree (see the migration
section above), and building out ~25 additional files' worth of MC/Forge/
base-mod symbol stubs just to re-prove a change that references zero new
symbols would be disproportionate effort. Instead this pass was verified
two ways: (1) re-running the missing-`require=` detector after patching
confirmed **0 remaining** across all 112 files (was 126 before), with no
duplicate insertions; (2) a standalone paren-balance checker (every `(`
matched by a `)`, depth never negative, ends at 0) across all 48 patched
files confirmed every insertion landed inside its annotation's own
parens and didn't corrupt any file's syntax. This is sufficient for a
mechanical, symbol-free text insertion of this kind.

**Net effect:** previously, a base-mod update that renamed or removed any
one of these 126 targets would have crashed the entire modpack at launch
(strict Mixin apply failure). Now, the same event just quietly disables
that one specific override — the rest of the mod, including every other
override, keeps working — and it'll show up as a normal "candidate to
re-verify" during the next deep-audit pass rather than as a launch crash
in the user's face. This does not replace re-verifying overrides against
each new base-mod jar (that's still the only way to know an override is
still doing what it's supposed to) — it just changes the failure mode from
"the whole mod is broken" to "one override quietly isn't doing anything,"
which is what "work on any future update" should mean in practice.

## Post-sync fixes: real launch/compile failures found after rebuilding (2026-09-02)

Two real, distinct bugs surfaced only once the user actually ran the real
Forge/Gradle toolchain and then actually launched the game — both are
exactly the class of thing this sandbox's stub-compile tree can catch for
files it has stubs for, but couldn't catch here because the relevant stubs
were stale or nonexistent. Documenting both, since they're evidence for
*why* the deep-audit process has to keep going past "it stub-compiled
clean."

### 1. Compile failure: `PhysiqueBonus` record gained a field

Base mod 0.1.1479 added `lightningSpellMult` (a new Lightning spell
element) to the `PhysiqueBonus` record, inserted between `pureSpellMult`
and `qiAbsorbRange` — its canonical constructor went from 17 args to 18.
`PhysiqueBonusMixin.java` was still rebuilding the record with the old
17-arg call, so `javac` rejected it outright ("actual and formal argument
lists differ in length"). Confirmed via `javap -p -c -s` on the real
0.1.1479 `PhysiqueBonus.class`. Fix: pass `original.lightningSpellMult()`
straight through at the new position — this mixin doesn't configure
lightning-element bonuses, so it's a pure passthrough, not a behavior
change. Root cause of why this wasn't caught earlier: the sandbox's
stub-compile tree had a stale `PhysiqueBonus.java` stub still showing the
old 17-arg shape (never refreshed after the migration work), so no
stub-compile could have caught it — fixed the stub too.

### 2. Launch crash: `passesSectSiteBiomeGate`'s signature changed completely

This is the more important lesson. `SectSettlementFeatureMixin.java`'s
`@Inject` on `passesSectSiteBiomeGate` had `require = 0` set (from the
hardening pass above) — and it **still hard-crashed the game at launch**
with `MixinApplyError` / `InvalidInjectionException: Invalid descriptor`.

Base mod 0.1.1479 didn't just rename this method — it changed its entire
parameter list, from `(WorldGenLevel, BlockPos, int radius, long seed)` to
`(SectSiteSelector.TerrainSampler, int x, int z, int radius, long seed,
double treedBiomeSpawnChance)`, replacing direct world/blockpos access
with an abstracted `TerrainSampler` interface. Confirmed via `javap -p -c
-s` on `SectSettlementFeature.class`: found the method by its new
descriptor, and traced its sole call site (in `resolveCellSite`) to
confirm the exact argument order matches the field names used in the fix.
Fix: rewrote the mixin handler's parameter list to match exactly; the
handler is a deliberate no-op either way, so no logic changed.

**Why `require = 0` didn't save this one — important correction to the
hardening methodology above:** `require = 0` only suppresses the "no
method matching this name could be found on the target class" failure
case. It does **not** suppress "a method with this name was found, but the
handler's parameter list doesn't match that method's actual descriptor" —
Mixin treats a descriptor mismatch (`InvalidInjectionException`) as a
fatal configuration error unconditionally, regardless of `require`. In
other words: a target being *renamed or removed* degrades gracefully now;
a target *keeping its name but changing its signature* does not, and still
needs a source-level fix every time. This is a real gap in what
"future-proofed" can mean for a Mixin-based mod — there's no annotation
parameter that makes a signature change survive automatically, because the
handler's own bytecode has to match the real target's stack shape to be
injectable at all.

**Verification:** both fixes were verified via isolated stub-compiles
(copying only the exact files each mixin depends on into a scratch
directory, rather than fighting the main stub tree's unrelated gaps) —
both produced clean `.class` output with zero errors. The
`passesSectSiteBiomeGate` crash was also cross-referenced against two
separate crash reports from two consecutive launch attempts to confirm it
was the same single root cause both times, not two different problems.

### 3. Launch crash: `SectDiscipleGateMixin` — static/instance mismatch after the above fix

After fixing #2, the game got further but still hard-crashed at launch, on
a *different* mixin this time: `SectDiscipleGateMixin`, with
`MixinApplyError` / `InvalidInjectionException: 'static' modifier of
handler method does not match target`.

Root cause: `configExt$redirectRealmGate` and `configExt$redirectSubStageGate`
were each declared `static` and shared across four (later two) target
methods via `@Redirect`'s `method = {...}` array. In 0.1.1479, two of those
targets — the 6-arg `createDataLayerRecruit` overload and
`lambda$acquireJourneyTarget$56` — silently changed from `static` to
instance methods (confirmed via `javap -p`: both now lack the `static`
keyword, and their call sites use `invokevirtual` instead of
`invokestatic`). The other two targets (`meetsDiscipleRealmGate` and
`lambda$tryRecruitLoadedLoneCultivator$76`) are still static. Mixin
requires a `@Redirect` handler's static-ness to match *every* target it's
applied to, so mixing static and now-instance targets under one static
handler is fatal.

Fix: split each of the two original handlers into a static variant (kept
on the still-static targets) and a new non-static "Instance" variant
(covering the now-instance targets) — same logic in both, the instance
variants just don't use `this`, they exist purely to satisfy Mixin's
modifier check.

Two crash reports from consecutive launch attempts were checked and
confirmed to be the exact same root cause, not two different problems.
Verified via the same isolated stub-compile technique — clean, zero
errors.

**Pattern worth generalizing:** a base-mod refactor can silently flip a
method between static and instance without changing its name, parameter
types, or return type at all — nothing about the method's *descriptor*
changes, only its `ACC_STATIC` flag, so this is easy to miss even when
carefully re-verifying every target's signature after a migration (as the
first pass on this file did — it checked parameter types and confirmed the
field reads were unchanged, but didn't separately re-check each target's
static-ness). Worth adding "confirm static vs instance, not just the
descriptor" as an explicit checklist item for any future `@Redirect`/
`@Inject` re-verification pass, especially for methods with multiple
targets sharing one handler.

---

## Systematic full-codebase mixin cross-check sweep (2026-09-02)

After three consecutive launch-crash bugs were found and fixed one at a
time across three separate launch attempts (PhysiqueBonus record arity,
SectSettlementFeature signature change, SectDiscipleGateMixin
static/instance split — see above), the user asked explicitly to stop
fixing crashes reactively and instead "figure out every which way there
could be a failure... check in every which way and then fix them all."

### Methodology

Reactive one-crash-at-a-time fixing only ever surfaces the *first* mixin
Mixin fails to apply — it can't tell you what else is silently broken
further down the load order. To get ahead of that, a 4-step scriptable
pipeline was built (all in `/tmp/xxjar/`, analysis-only, not part of the
shipped mod) that mechanically re-verifies **every** `@Shadow`, `@Inject`,
`@Redirect`, `@ModifyConstant`, `@ModifyArg`, and `@ModifyVariable` target
across **all 110 registered mixin files** against the real 0.1.1479 jar in
one pass, instead of waiting for each one to crash in turn:

1. **`step1_targets.py`** — extracts each mixin's `@Mixin` target class(es)
   by regex over comment-stripped source, cross-referenced against
   `xiaoxiang_config_ext.mixins.json`'s registered file lists. Found 95
   distinct target classes; 3 files are intentionally-unregistered empty
   placeholders (not bugs). The base-mod jar was fully extracted (2994
   `.class` files) and 90 of the 95 target classes were disassembled via
   `javap -p -s`; the remaining 5 are vanilla Minecraft/Forge classes with
   no jar available in this sandbox and were explicitly flagged as
   unverifiable rather than silently skipped.
2. **`step2_extract.py`** — for each registered mixin file, parses every
   annotated member (target method list + the handler's own name/params/
   static-ness) via paren-balanced regex parsing. 487 annotated entries
   across 110 files. **Bug found and fixed in this step**: the initial
   identifier regex was `\w+`, which excludes `$` — this broke matching
   for the `configExt$xxx` naming convention used almost everywhere in
   this codebase, corrupting results for a much larger set of entries than
   the 5 that outright failed to parse (some entries were silently
   matching the *wrong* member). Fixed by widening the identifier pattern
   to `[A-Za-z0-9_$]+`; re-verified 100% parse success and spot-checked
   against 3 already-known-correct files.
3. **`step3_parse_javap.py`** — parses the raw `javap -p -s` text dumps
   into structured per-class `{fields, methods}` maps (name → list of
   `{descriptor, static}`, to handle overloads). Spot-checked against
   `SectSavedData` (984 methods) — correctly reproduced the exact ground
   truth from the SectDiscipleGateMixin static/instance investigation.
4. **`step4_crosscheck.py`** — cross-checks all 487 entries against the
   parsed bytecode: `@Shadow` targets need an exact field/method + static
   match; full-descriptor injector targets need an exact descriptor match;
   bare-name injector targets need name existence + static-match against
   *any* overload. Produced 27 candidate findings.

### Triage: 27 findings, 3 real bug classes, 2 checker-limitation classes

Every finding was individually re-verified by hand against the raw mixin
source and a fresh, targeted `javap -p -c -s` of its real target — the
checker surfaces *candidates*, it is not itself the source of truth.

**Real, source-fixed bugs (11 of 27 findings, 3 files):**

- **`BeastCultivationHandlerMixin`** (1 finding) — `@Shadow method
  announceAdvancement` does not exist in 0.1.1479's
  `BeastCultivationHandler` at all (confirmed independently by the user's
  own crash report, `InvalidMixinException`). `javap -p -c` of the real
  `updateBeast(...)` shows the method was renamed **and** resignatured:
  `emitAdvancementFeedback(ServerLevel, LivingEntity, Realm)` — the third
  parameter changed from the whole `BeastCultivationData` to just the
  resulting `Realm` (`data.getRealm()`, read *after* `data.advance()`
  mutates it — confirmed via `javap -c` of `BeastCultivationData.advance()`,
  which loops `realm = BeastProgressionRules.nextBeastRealm(realm)` while
  `canAdvance()`). Fixed by correcting the `@Shadow`'s name/signature and
  updating the one call site to pass `data.getRealm()`. Noted honestly in
  the file's doc comment that the real `updateBeast` also calls
  `syncBeastRealmAttributes` and heals the entity to full HP on advance,
  neither of which this override replicates — pre-existing scope, not
  touched by this fix.

- **`SpiritPlantBlockMixin`** (3 findings) — `spawnQiOrbIfNotCrowded`
  silently flipped from static to instance in 0.1.1479 (same descriptor,
  only the `ACC_STATIC` flag changed — the same failure class as the
  already-fixed `SectDiscipleGateMixin` bug, just not yet crashed into by
  the user because Mixin fails fast on the first bad target it processes
  in load order). The three `@ModifyConstant` handlers targeting it were
  still `static`. Since none of the three share their `method` list with
  any other (still-static) target, no split into separate handler
  variants was needed here (unlike `SectDiscipleGateMixin`) — simply
  dropping `static` from all three was sufficient and correct.

- **`CultivationChestLootMixin`** (7 findings) — the entire "ruined vs
  complete" chest-loot distinction was deleted from base mod 0.1.1479.
  `fill(RandomizableContainerBlockEntity, RandomSource, boolean ruined)`
  no longer exists at all; it was replaced by `fillSectContainer(...)`
  (2 params, no boolean, just delegates to `fillSectLoot`) and
  `fillSectLoot(Container, RandomSource)`, whose bytecode (`javap -p -c`)
  shows it unconditionally runs what used to be only the "complete"
  branch: `randomBetween(r, 3, 6)` for vanilla rolls, `randomBetween(r, 1,
  3)` for cultivation rolls. The "ruined" branch's literals (2, 4, and the
  `nextInt(3)` bound) do not appear anywhere in the new bytecode at all.
  Fixed by retargeting the 4 handlers whose literals still have a real
  target (`completeVanillaRollsMin`/`Max`, `completeCultivationRollsMin`,
  `completeCultivationRollsMax`) to `fillSectLoot` with re-verified
  ordinals (`completeCultivationRollsMax`'s ordinal shifted from 2 to 1,
  since the old middle "3" occurrence — the ruined branch's `nextInt`
  bound — no longer exists). The 3 handlers for the now-nonexistent
  "ruined" literals (`ruinedVanillaRollsMin`/`Max`, `ruinedCultivationRollsMax`)
  are permanently dead since their upstream code path was deleted; they
  are documented as such rather than deleted outright (their `ExtendedConfig`
  fields still exist), and `ruinedCultivationRollsMax` deliberately stays
  pointed at the nonexistent `fill` (not retargeted to `fillSectLoot`)
  because `fillSectLoot`'s only two remaining "3" occurrences are both
  real, live targets of the other two handlers — retargeting it there
  would have silently collided with one of them instead of staying inert.
  **Important distinction from the other fixes above: none of these 7 were
  ever a launch-crash risk** — `require = 0` already degraded a
  name-not-found target (`fill`) to a silent no-op. This was purely a
  "config knob quietly does nothing" correctness bug, found and fixed as
  part of the same sweep rather than because it threatened to crash
  anything.

**Checker false positives — no source change needed (16 of 27 findings, 2
distinct causes, both confirmed by reading the raw annotation source):**

1. **`method = "<init>"` (constructor targets), 4 files, 7 findings**
   (`BloodBerserkEffectMixin` ×2, `DaoHeartWoundEffectMixin` ×2,
   `ShatterArmorEffectMixin` ×2, `CultivationDataMixin` ×1). `"<init>"` is
   Mixin's real, correct way to target a constructor. The checker's
   `step3_parse_javap.py` indexes methods by the name `javap` prints them
   under, and `javap` prints a constructor as `ClassName(args)`, never as
   the literal string `"<init>"` — so `step4`'s lookup for the literal name
   `<init>` always fails, regardless of whether the constructor and its
   `@ModifyArg`/`@ModifyConstant` target inside it are actually fine. All
   7 of these already carry detailed, bytecode-cited doc comments from
   earlier verification passes and `require = 0`; re-confirmed correct,
   no source change made. **Checker limitation, not investigated further
   since it doesn't affect correctness of the checker's other 20 findings.**

2. **Vanilla/inherited override points targeted by their MCP name with
   default `remap = true`, 3 files, 4 findings** (`CultivationScreenTooltipMixin`'s
   `render` ×2, `IdentityDrawScreenMixin`'s `init` ×1,
   `WildMeditationMixin`'s `registerGoals` ×1). All three target a method
   inherited from a vanilla class (`Screen.render`/`Screen.init`/
   `Mob.registerGoals`) using its normal MCP/dev-time name, with no
   `remap = false` override — i.e. `remap = true` (the default), which is
   the standard, correct way to target a vanilla override: Mixin's own
   annotation processor generates a refmap at build time mapping the MCP
   name to the real SRG name the compiled class actually uses in
   production (`m_88315_`, `m_7856_`, `m_8099_` respectively — confirmed
   via `javap -p` on the real target classes, all three obfuscated names
   are present and match the expected override signature). The checker's
   `javap`-based cross-check compares literal names against the already-
   SRG-obfuscated compiled class and has no notion of refmap remapping, so
   it necessarily reports these as "not found." `CultivationScreenTooltipMixin`'s
   own doc comment already documents this exact mapping
   (`CultivationScreen.render -> m_88315_`) from when it was written,
   confirming this was already understood and handled correctly, not an
   oversight. **Checker limitation** — a `javap`-only cross-check
   fundamentally cannot validate a `remap = true` target without also
   having the project's generated refmap available, which this sandbox
   does not have (no Gradle/Mixin AP run possible here). No source change.

### Verification

All three fixed files (`BeastCultivationHandlerMixin.java`,
`SpiritPlantBlockMixin.java`, `CultivationChestLootMixin.java`) were
verified via the isolated stub-compile technique — a fresh scratch
directory per file containing only the exact classes/interfaces that file
references (hand-written stubs matching real signatures, confirmed via
`javap`, plus the real `ExtendedConfig.java` reused directly rather than
stubbed) — each compiled with `javac` and zero errors.

### Caveat

This sweep is a static, bytecode-descriptor-and-staticness cross-check —
it cannot catch a target that still resolves by name/descriptor/static-ness
but whose *semantics* silently changed (a parameter that means something
different now, a method that got a new required precondition, etc.). The
`CultivationChestLootMixin` finding above is itself an example of a
category the checker's raw output framed as "not found" but which actually
required understanding a real behavioral change (the ruined/complete
distinction being deleted) to fix correctly rather than just re-pointing a
name. A real launch test after this sweep is still the ultimate
verification, not a substitute for it.
