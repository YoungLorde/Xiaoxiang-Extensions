# Xiaoxiang Config Extension 2.0.8

### *The Overhaul.*

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

**This is the largest release this mod has ever had.**

2.0.0 is where the mod stops being "a big pile of config options" and starts being a *tool*. Three things happened at once: the option count went from roughly 970 to **1,256 real, individually-wired settings** offered on screen (1,268 defined, less the twelve Mortal-realm values withdrawn on purpose — see below); the config screen was rebuilt around how a cultivator actually thinks about the game (twenty single-subject tabs, ninety-eight sub-tabs, every realm in true breakthrough order, navigation you can fold out of your way); and a stack of long-standing correctness bugs — including the runaway NPC spawning several of you reported — were found, tracked down to their real cause and fixed.

The word *overhaul* is doing real work in that subtitle. What follows is the full accounting, in detail, of every one of those changes. A short **Coming Next** note sits at the very bottom for anyone who wants to know what is actually being worked on.

**Compatible with Xiaoxiang Cultivation World 0.1.1480** (and 0.1.1479). Every one of this mod's overrides was re-checked against the 0.1.1480 update, and nothing broke. Zero regressions.

**As always: not one file belonging to Xiaoxiang Cultivation World is modified.** Everything here is applied at runtime through Mixins, and every category can be switched off independently if you'd rather have the base mod's own numbers back.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## 2.0.8 — a clearer tab bar

A small release, and only one change. It is listed on its own rather than padded out.

### 🍑 Changed: "Cultivation Fruit" is now "Spirit Fruit"

Two tabs sat directly above and below each other reading **Cultivation** and **Cultivation Fruit** — and at most window sizes the second truncated into something almost identical to the first. It looked like the same tab listed twice.

**Spirit Fruit** is shorter, unmistakable at any width, and it is what the system is actually called.

- **Nothing moved in your config file.** This is the tab's display name only — no setting was renamed, relocated or reset.
- **If you had reordered your tabs, your order is kept.** The renamed tab lands exactly where the old one sat rather than being dropped at the end.
- Its description, sub-tabs and settings are unchanged.

That is the whole release. The file is barely larger than 2.0.7 because barely anything changed, which is what a text change should look like.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## 2.0.7 — the config screen was empty. It isn't now.

### 🛠️ Fixed: every setting disappeared from the config screen

If you opened the config screen in 2.0.6 you saw the window, the tabs, the theme and its background animation — and **not one setting.** Completely empty.

**What was wrong:** an internal list the screen builds from was being filtered by a check that ran before it was ready. The very first setting hit the problem, and because the whole scan sat inside a single error handler, that one failure abandoned the entire remaining list. Twelve hundred settings, gone, while everything else on the screen kept working perfectly — which is exactly why it looked broken rather than crashed.

**Fixed, and hardened so it cannot recur:**

- The ordering problem itself is corrected.
- **Each setting is now registered independently.** One that fails is named in the log and skipped; the other twelve hundred still load. A single failure can never take the whole list down again.
- **The filter now fails open.** If anything goes wrong deciding whether a setting should be hidden, it shows. Worst case is a setting appearing that was meant to be tucked away — never a screen with nothing on it.
- **The screen reports on itself.** How many settings loaded, and how many were listed, now appear in the log every launch. An empty screen can no longer be silent.
- An automatic check now guards this specific class of ordering mistake so it cannot slip back in.

Nothing was lost. No config file was touched. Every setting was always there — the screen simply stopped being able to see them.

### 🧭 Reaffirmed: every default is the base mod's own

Worth stating plainly, because it is the principle this whole mod is built on:

- **This mod invents no values.** Every default it ships is Xiaoxiang Cultivation World's own value, taken from the base mod itself.
- **It does not impose a balance.** It is a tool for setting *your* numbers, not a set of numbers chosen for you.
- **"Default" always means the original mod's default.** If you reset a setting, you get the base mod's behaviour — not this mod's opinion of it.
- Where a value cannot be reached safely, it is documented as unwired rather than shipped as a slider that quietly does nothing.

All thirteen values in the 2.0.1 repair were re-confirmed against the base mod before this release — the formation radius cap of 255, the farm scan budgets of 4096 / 1024 / 16384, the 16-tick effect duration, the flag default of 8, and the ±1,000,000 morality range are all the base mod's own numbers.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## 2.0.6 — every theme comes alive

### Added — 34 themes, 34 different background animations

The themes changed colour and they changed frame style. What they never changed was the *feeling* of the screen: every one of them was a still image. Now each theme has its own slow, continuous motion behind the interface — and **no two themes share one.**

**The cosmic set:**

- **Stellar Forge** — a **supernova in slow motion.** A core flash, then three shockwave rings launched a beat apart, each one *decelerating* as it expands the way a real blast front does, then an ember cloud thrown outward that cools from white to red as it travels. One detonation every 34 seconds.
- **Void Tribulation Abyss** — a **star collapsing inward.** Matter spirals in, and the radius eases *inward* — slow at first, fastest at the very end, which is the signature of a collapse rather than an explosion played backwards. It falls into a dark core with a thin accretion ring, and the whole thing breathes on a 42-second cycle so it never reads as a loop.
- **Nebula Drift** — a **spiral galaxy turning.** Two logarithmic arms over a still parallax starfield, one full rotation roughly every five minutes, with a warm core bulge.
- **Celestial Neon** — **starfall.** A slow meteor shower crossing a twinkling sky, each streak drawn with a nine-sample tail that dims behind the head.
- **Quantum Lotus** — five orbital shells at different tilts, each turning at its own rate.
- **Prism Spectrum** — white light refracting into a fan of ten spectral beams, turning slowly.

**And every other theme got its own too:** golden qi motes rising (Classic), ink clouds drifting across paper (Cultivation Scroll), dust turning in a shaft of window light (Medieval Manuscript), slow blood rain (Demonic Blood), a jade aurora breathing (Immortal Jade Court), counter-rotating rune rings (Formation Talisman), circuit pulses running along traces (Chrono Circuit), grid-quantised pixel rain (8-Bit Sect), scanlines and a marquee chase (Arcade Cultivator), pixel snow settling (Parchment Pixel), stained-glass light sweeping the nave (Gothic Cathedral), ruled lines being drawn and ink drying (Knight's Ledger), gold catching the light one coin at a time (Dragon's Hoard), cobalt glaze turning under the surface (Porcelain Blue & White), ink blooming into wet paper (Ink Wash), petals falling and turning edge-on (Cherry Blossom), sky lanterns rising (Imperial Vermillion), bamboo swaying in a slow wind (Bamboo Grove), heat shimmer over a low sun (Savanna Dusk), ripples spreading from a struck drumhead (Mask & Drum), embers lifting off a fire out of frame (Ancestral Fire), woven strips travelling diagonally (Kente Weave), dune crests creeping (Desert Shaman), snowfall with six-armed frost crystals (Frost Sanctum), and heat breathing through cracks in cooled rock (Obsidian Ember).

### The rules every one of them follows

This is where "smooth and simulated, not rigid" actually gets decided, so it is worth saying what was done:

- **Slow.** Nothing here is quick. Cycles run from about eight seconds to three minutes.
- **Continuous, not stepped.** Time is a real-valued number of seconds from the system clock — not ticks — so motion is independent of frame rate and tick rate and never stutters at 20 Hz. Every position is a smooth function of that clock.
- **Eased, not linear.** Particles fade in and out on a smooth curve; the supernova's rings decelerate; the collapsing star accelerates. That easing is most of the difference between "animated" and "simulated".
- **Allocation-free.** Particles are not objects. Each one's entire life is a pure function of its index and the clock via a cheap integer hash, so a hundred drifting motes cost a hundred sines per frame — not a hundred objects.
- **Always behind.** Drawn immediately after the window fill and before the frame and content, so it can never sit on top of text.
- **Palette-aware.** Colours come from the active theme's own accents, so retuning a theme's colours later keeps its background matching.

### Accessibility — the part that mattered most to get right

**Reduce Motion freezes these rather than slowing them.** The clock stops at a fixed instant, so the animation becomes a still composition instead of something moving at the edge of vision. That is the correct behaviour for a Reduce Motion setting, and it is not the same as merely turning the speed down.

The three accessibility themes get the quietest treatments in the set by design: **Monochrome** gets a barely-there grey tide, **Gentle Focus** gets a single soft breath, and **High Contrast gets no motion at all** — its entry exists explicitly to record that decision, rather than leaving a gap someone later fills with a starfield.

### Added — controls, on UI → Background

- **Living Backgrounds** — the master toggle.
- **Animation Strength** — scales opacity only, never speed, so lowering it makes motion subtler rather than slower. Zero is fully off.
- **Animation Speed** — 1.0 is the intended pace. Below is slower and suits the cosmic themes; much above and the slow-motion quality breaks down.

### Added — theme ambience (off by default)

Sparse ambient tones under the config screen, voiced to the active theme: low and resonant for the cosmic ones, brighter for the natural ones, near-silent for the accessibility ones. Tones arrive at long irregular intervals — 11 to 26 seconds — so it never becomes a metronome, and consecutive tones are never identical.

**Being straight about what this is:** it is not a music track. A real looping stellar score means shipping audio files, and this mod ships none of its own — every sound it makes is one the game already has. What you get is closer to a room tone than to music, which is the right target for a screen you are reading. It is **off by default**, because a settings screen that starts making noise unasked is a bad surprise, and Reduce Motion silences it too.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## 2.0.1 — the patch on top

### 🧱 Fixed: formations were capped to a 16-block radius

Reported on CurseForge by a player who had already done the hard part — they narrowed it down by adding and removing mods until they found the culprit. They were right, and the cause was subtler than a bad setting.

**What was happening, in short:**

- A number of settings shipped for a long time doing **nothing at all** — present in the config file, editable, connected to nothing. Their default numbers were placeholders.
- A later update connected those settings for real, and corrected each placeholder to the base mod's true value at the same time.
- **That correction only reached players with no config file yet.** Your config file is written once and then left alone — correctly, because it is yours. So anyone who had played an earlier version still had the old placeholder sitting in their file.
- The moment those settings went live, the old placeholder went live with them.

For the formation radius cap, the placeholder was **16** and the real value is **255**. Every formation flag in those worlds got clamped to 16 blocks.

**Thirteen settings were affected in total:**

| Setting | Was stuck at | Should be | What you saw |
|---|---|---|---|
| Max flag effect radius | 16 | **255** | formations clamped to 16 blocks |
| Default flag effect radius | 4 | 8 | new flags started at half their radius |
| Farm harvest max checks | 64 | 16384 | farm formations barely harvesting |
| Farm harvest min checks | 1 | 4096 | scan floor of a single tile |
| Farm harvest checks per target | 4 | 1024 | scan budget 256× too small |
| Effect duration | 200 | 16 | rejuvenation / flight-ban / maze lasting 12.5× too long |
| Effect interval | 20 | 10 | formation effects ticking half as often |
| Growth tick interval | 100 | 20 | growth formations 5× too slow |
| Generated array sync interval | 100 | 200 | Immortal cores syncing twice as often as intended |
| Reload flag validation grace | 200 | 100 | validation waiting twice as long after a reload |
| Morality minimum | -1000 | -1000000 | morality scale 1000× too narrow |
| Morality maximum | 1000 | 1000000 | as above |
| NPC higher-Dao lifespan reserve | 0.5 | 1.0 | NPCs taking higher Dao paths too readily |

**What happens when you update:**

- On first launch, the mod checks all thirteen.
- If one is **exactly** the old placeholder, it is set to the real value.
- **Every repair writes a line to your log** naming the setting, both values, and what the symptom was — nothing happens silently.
- It runs **once**. After that your config is never touched again.
- If you had deliberately picked one of those old numbers, you get it reset that one time. The log says which; set it again and it sticks for good.

Nothing changes on a fresh install — every target value is the base mod's own.

**Thank you to the reporter.** Narrowing a bug down to one mod by elimination is real work, and it is the only reason this was caught.

### 📖 Added: a description on every tab, sub-tab and group

The standing rule from here on, including for anything added in future updates:

- **Main tab** → a long description
- **Sub-tab** → a medium one
- **Group** → at least a short one

With over twelve hundred settings across twenty tabs, a page with no explanation is a page you get lost on.

**Seven main tabs had no description at all.** All seven now have full write-ups — what the system is in the fiction, why you would come to that tab, what it controls, how far its effects reach, and what to be careful with:

- 🗡️ **Weapons**
- 📜 **Talisman**
- ⚖️ **Spell Balance**
- 🛡️ **Equipment**
- 🍑 **Spirit Fruit**
- 🐉 **Bosses**
- ☯️ **Morality**

Also filled in:

- **29 sub-tabs** across those tabs
- **Cultivation → Reincarnation** and **UI → Identity Tooltips**, which were reachable but unexplained
- Group blurbs now fall back to a written sentence instead of a bare auto-generated line

### 🔊 Changed: the hover sounds are louder

They read correctly but sat too quietly under the game's own ambience.

- Overall level raised, every voice with it
- The **category panel** — where your cursor spends most of its time — is now the loudest, and lifted in pitch
- The three navigation tiers keep their rising-scale relationship, just further above the noise floor

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## 2.0.0 — the overhaul

## At a glance

| | 1.0.9 | **2.0.0** |
|---|---|---|
| Configurable settings | ~970 | **1,256** |
| Top-level tabs | 15 | **20** |
| Sub-tabs | 63 | **98** |
| Registered overrides | 101 | **131** |
| Injection points | ~600 | **796** |
| Written setting descriptions | — | **+387 new** |
| Visual themes | 34 | 34 |

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## Fixed — the Mortal realm is now off-limits, on purpose

**Mortal is no longer configurable, anywhere in this mod.** Every setting the realm ladder identifies as Mortal's — Max Qi, NPC spawn weight, all four NPC combat values, the equipped-technique base multiplier, and the four Mortal entries in the spell-balance tables — is gone from the config screen entirely: from the lists, from the group bars, from search, from per-tab reset, from modpack presets, from perk targeting, and from profile save/load.

This is a design decision, not an oversight, and it's worth explaining because it removes twelve things you could previously click on.

Mortal is not a stage you cultivate through. It is the state of *not having started* — the zero point every other realm is measured against. When the baseline itself can be retuned, no balance question in the mod has a stable answer any more: "is Golden Core too strong?" depends on what Mortal was set to that day. Worse, it was already actively causing harm. The config screen's description lookup matched the text `mortal` *inside* `immortal`, so a Mortal group would cheerfully display **"True Immortal Max Qi"** and its description — a player tuning what they thought was the mortal baseline was reading, and reasoning about, the highest realm in the game.

So the fix went in at both ends:

- **The realm ladder is the single source of truth for what belongs to which realm**, and it matches on camelCase word boundaries, longest key first — so `looseImmortal` can never again be mistaken for `mortal`, in *either* direction, anywhere in the mod.
- **One choke point hides Mortal's settings.** Every list, bar and search in the screen walks the same path registry; Mortal's entries simply aren't in it.

**Honest note about the TOML.** The values still exist in your config file with the base mod's own defaults, and they're still readable by the code that needs them, so nothing breaks. An admin determined to hand-edit `xiaoxiang_config_ext-common.toml` in a text editor is not stopped. That's deliberate — silently deleting keys out of existing config files would be worse. What's guaranteed is that **nothing in this mod writes them, offers them, or resets them**, and no preset or profile will ever touch them.

## Fixed — a whole class of wrong descriptions

The `mortal` / `immortal` collision above was one symptom of a more general bug: when a setting had no exact description entry, the screen fell back to fuzzy matching — same last path segment, then simple substring containment. That fallback had no idea what a realm was, so it would happily hand one realm's setting another realm's text.

Fuzzy matching is now **realm-aware**: a candidate description is only accepted if it belongs to the same realm as the setting it's describing, or if neither is realm-specific. Two genuinely stale registry keys left over from an earlier restructure (`realms.mortal.maxQi`, `realms.trueImmortalMaxQi`) were also corrected. One instance is fixed by the key; the entire class is fixed by the guard.

## Fixed — no more overlapping or redundant settings

You reported seeing two sets of controls that looked like they did the same thing. You were right, and it was worse than it looked.

**"Zhenyuan Rewards" was five settings that did nothing at all.** `realms.zhenyuanRewards.minor`, `.major`, `.attrMinor`, `.attrMajor` and `.statMultiplier` sat on the Cultivation page, read exactly like the real controls, sat right next to them — and no override read a single one of them. Turning them did nothing, forever. **They are removed.** A setting that looks like it works and doesn't is worse than no setting at all.

**And one live knob was secretly driving two different rewards.** A breakthrough grants you two separate things: *free* zhenyuan that lands in your unallocated pool for you to spend, and *auto-allocated* attribute points the game distributes for you. Until now, `general.zhenyuanRewardAmount` was wired to **both** — so setting it from 5 to 20 handed out 20 free points *and* 20 auto-allocated ones, which is not what the setting says it does. Meanwhile `cultivationData.zhenyuanAttrRewardMajor` and `.zhenyuanAttrRewardMinor` — the two settings that exist precisely to control the auto-allocated reward — were dead.

Now each reward has **exactly one owner**:

| What you get | Setting | Default |
|---|---|---|
| Free points, realm-up | `general.zhenyuanRewardAmount` | 5 |
| Free points, sub-stage | `general.zhenyuanMinorRewardAmount` | 1 |
| Auto-allocated attributes, realm-up | `cultivationData.zhenyuanAttrRewardMajor` | **5** *(was 3 — see below)* |
| Auto-allocated attributes, sub-stage | `cultivationData.zhenyuanAttrRewardMinor` | 1 |

**A default was corrected in the process.** `zhenyuanAttrRewardMajor` shipped with a default of 3. The base mod's own value is **5** on a realm-up and **1** on a sub-stage. It never mattered while the setting was dead; the moment it went live, a wrong default would have silently nerfed every realm-up for everyone who installed this mod. It's 5 now, which is the real value.

All four settings were also given proper written descriptions that say plainly which of the two rewards they control and name their counterpart, so the distinction is visible in-game rather than only in a changelog.

## Added — the Monotonicity settings finally explain themselves

The Monotonicity group is a **safety net, not a balance setting**, and nothing on screen said so — which is why it read as overlapping with the realm Max Qi settings sitting a group away. It now has three written descriptions that spell out exactly what it does and when it does it:

- **Never Let Requirements Go Down** — guarantees every breakthrough in the standard chain costs strictly more qi than the one before it. It exists because that guarantee can break three ways: your own Max Qi edits, the delta settings drifting out of order, and *the base mod itself* — its own `Realm.maxQi` returns 20,900 for True Immortal and then 18,000 for Loose Immortal, a genuine built-in decrease. It never touches a value that's already increasing.
- **Minimum Sub-Stage Increase** / **Minimum Realm-Up Increase** — the floors it settles for when it *does* have to repair a step, and the margin holding the two Immortal realms above Tribulation Transcendence Peak. Floors, not targets.

## Added — three tiers of hover sound across the navigation

Sweeping the mouse down the config screen's hierarchy is now audible as a rising scale, so you can tell which tier the cursor is on without looking at it:

- **Main tabs** — the heaviest, lowest voice.
- **Sub-tabs** — lighter, mid.
- **Group buttons** — the lightest, briefest, highest.

All three are one recognisable timbre pitched differently rather than three unrelated samples, which is the difference between a scale and three noises. They're keyed by element rather than by list position, so sliding from *Cultivation* to *Spells* actually sounds, where before an index-based check would sometimes swallow it.

## Fixed — dragging one item-picker divider moved a different one

Reported and reproducible: widening the Preview panel made the Selected panel narrower under your hands, and dragging further started eating Categories.

The fitter that keeps the picker's panels inside the window is the right behaviour when *the window* gets smaller — it reclaims space from the roomiest panels in a fixed order. It's exactly the wrong behaviour for a deliberate drag. A divider drag is a statement about the boundary between one panel and the **Items** column, and nothing else.

Drags are now **capped at the point where the Items column hits its own minimum**. Past that, the divider simply stops. Every other panel keeps the width *its* owner set, and window resizing still behaves exactly as before.

## Added — the item preview is big, centred, and zoomable

The Preview panel drew every item icon at a fixed 32 pixels, marooned in the middle of a panel you could now drag out to any width.

- **The icon is sized from the panel**, filling its width less a small margin, so widening the Preview divider actually gives you a bigger picture.
- **Mouse-wheel zoom while hovering the Preview panel**, from ½× to 4×, remembered until you close the game.
- **Text flows below whatever the icon actually took**, not below a hard-coded row — so a zoomed icon never draws over its own name.
- The empty-state hint now mentions the wheel, because an undiscoverable feature isn't a feature.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## Added — 298 new settings across five new systems

The base mod's 0.1.1479 update introduced several large systems this mod had no coverage for at all. All five are now configurable, and each one gets its **own top-level tab** rather than being buried as a sub-tab somewhere else.

### Talisman — 29 more settings (now 111 total)

1.0.9 shipped the per-glyph qi costs. 2.0.0 finishes the system:

- **Every structural cap** the talisman compiler enforces: max executable slots, max operation budget, batch-craft limit, glyph/mode/container ID lengths, and the full set of per-evolution stacking caps (split applications, pierce count, potency bonus, pull applications, duration and damage/destruction multipliers).
- **Delay and echo timing** — ticks added per delay glyph, the delay ceiling, and the echo delay pair.
- **Use-mode qi cost**, which lives on the talisman use-mode itself rather than in the glyph catalogue and needed a different kind of override to reach.
- **A newly-found complexity-cost group** (trigger, modifier, explosion, fireball) that no previous audit had catalogued — these are what the operation budget is actually spent on.

### Spell Balance — 94 settings (new tab)

The base mod keeps its spell maths in one balance table. All of it is now yours:

- **Custom spell creation** — the qi multiplier, the bone-age cost (base + per-slot) and the lifespan safety margin.
- **Custom spell capacity per realm** — how many custom spells a cultivator can hold at each of the nine realms that allow them.
- **Bonus caps** — aptitude, cultivation and status bonus ceilings, plus the overall power-bonus percentage cap.
- **Named mega-spell damage** — Sky Splitting (base + per-charged-qi), Mega Meteor (direct + shockwave), Five Peaks Seal, Lightning Bolt, Core Self-Destruct (flat + per-realm-power²) and the Heaven Piercing Cone cap.
- **Four full scaling tables, entry by entry**: realm spell-damage factors, realm max-health bonuses (both with a separate ten-entry Loose Immortal ladder indexed by tribulation count), and beast HP and attack multipliers across all twelve beast realms.

Those tables are real arrays in the base mod rather than inlined numbers, which needed an override technique this mod hadn't used before — the upside is they're now exposed one realm at a time instead of as a single all-or-nothing multiplier.

### Equipment — 83 settings (new tab)

- **Armor set bonuses** for all nine sets (Cangming, Four Symbols, Lihuo, Maoshan, Origin, Shihun, Taisu, Taixuan, Xuesha) — every percentage, duration and threshold each set grants, grouped one sub-section per set.
- **Set activation rules** — the piece counts at which bonuses switch on, the maximum armor pieces counted, and the alignment cap.
- **The item-tier multiplier ladder** (LOW/MID/HIGH/SUPREME/IMMORTAL) that scales every set bonus.
- **Accessory and blood-blade tier scaling**, plus the storm-imbue bonus per tier.
- **Set effect services** — the incoming-damage reduction ceiling, guard and ward timings, lethal-save rules, and the Cangming water-mastery durations.

### Spirit Fruit — 23 settings (new tab)

The new fruit-tree system, end to end:

- **Selection** — how rare a special fruit tree is, cluster size range, and the hash salts that decide *which* trees get picked (change a salt to reshuffle the world's fruit trees without changing how many there are).
- **Probing** — how many column probes each online player triggers, and their horizontal/vertical reach.
- **Tree detection** — the log-search box, connected-log thresholds, and canopy scan margins and volume cap.
- **Aging** — the day length fruit ages by, and the year thresholds for each harvest tier (Mid / High / Supreme / Immortal).

Two of these are consumed as bitmasks rather than plain numbers in the base mod, so their settings carry an explicit note: keep them powers of two or the distribution goes uneven. That caveat is written into the setting's own description in-game, not buried in a readme.

### Bosses — 69 settings (new tab)

The Xuan Gui boss encounter and the Kunlun Root Strike ability, end to end:

- **Xuan Gui stats** - shell duration and cooldown, shell damage reduction, rest threshold, retaliation range.
- **Xuan Gui combat** - hit timing, cooldown, reach, damage multiplier and knockback for each of the bite, slam and charge attacks, plus charge start range and rush speed.
- **Xuan Gui AI** - swim and land wander speeds and intervals, look range, sprint trigger range and speed, the health fraction at which it shell-guards, threat scan interval, the crowd size that triggers Tidal Shockwave, and the slam chance.
- **Xuan Gui spawning** - water search radius, the sea-level band it spawns in, minimum light.
- **Kunlun Root geometry** - start/end thickness and where the taper begins, thickness clamps, brush reach cap, surface erosion, lump chance / offset / size, and the per-segment block budget.
- **Kunlun Root timing** - plunge blend and angle, distance- and age-driven gravity onset and ramps, gravity per tick, aim range.
- **Beast spells** - cooldowns for Mystic Shell Guard, Demon Blood Surge and Tidal Shockwave, and the durations, knockback, radius and threat window of their effects.

## Changed — the config screen's tab layout

**Every tab is now a single subject.** Three tabs used to staple two unrelated systems together behind an "&":

- **Spells & Combat** → **Spells** (active + passive) and **Weapons**
- **Beasts & Mobs** → **Beasts** (its "NPC Spawns" sub-tab was a second doorway into the same paths the NPCs tab already owned, so it's gone from here)
- **Effects & Morality** → **Effects** and **Morality**

Together with the five new system tabs, that's **20 tabs and 98 sub-tabs**, each sub-tab further split into groups.

**Nothing moved in your config file.** This is purely how the screen organises what already exists — no setting was renamed, relocated or reset. If you'd previously reordered your tabs, your saved order is migrated in place: each split tab's replacements land where the original sat, instead of your order being scrambled and the new tabs dumped at the end.

**Two settings that were unreachable now have a home.** The reincarnation technique fix and the Identity-screen scrollable-tooltip settings both existed in the config file but had no tab to render on, so they could only be changed by editing the file by hand. They're now under Cultivation → Reincarnation and UI → Identity Tooltips.

## Changed — every realm, everywhere, in breakthrough order

This was the most-repeated piece of player feedback: the config had no sense of progression. Golden Core would sit near the top of a list purely because of alphabetical order, and a new player reasonably concluded that's where cultivation starts.

- **One realm ladder, used everywhere.** Mortal → Qi Refining → Foundation Building → Golden Core → Nascent Soul → Soul Formation → Void Refining → Body Integration → Mahayana → Tribulation Transcendence → True Immortal → Loose Immortal, taken from the base mod's own realm definition rather than guessed. Previously only the Realms page was ordered this way; beast advance costs, NPC realm weights, spell scaling tables, per-realm spell capacities, ancestor chances and everything else fell through to alphabetical. Now every realm-keyed setting in the mod sorts lowest-realm-first.
- **One group per realm.** Any group that was really several realms under one label ("Standard Realms", "NPC Realm Weights", the Realms page's own Max Qi / Lifespan / Tribulation tables) is now split into a group per realm, ordered up the ladder, so you can click "Golden Core" and see only Golden Core's settings - across every page that has them. This is decided automatically from the config's structure, so any future page that mixes realms gets the same treatment. General (non-realm) settings on those pages keep their original group and come first.
- **Beast tiers included.** The beast advance-cost page keeps its beast names (Spirit Soldier … Spirit Lord) but they now sit on the same ladder at the realms they correspond to, so that page reads bottom-to-top too.

## Changed — the item picker's panels are resizable

Every divider between the picker's panels (Mods | Sub-categories | Sub-sub-categories | Items | Preview | Selected) can be dragged left or right. The Items column flexes to absorb the difference; no panel can be dragged to nothing and Items always keeps at least 80px. Double-click any divider to restore the defaults. Your layout is remembered while the game runs. The Preview panel's default width also went from 80px to 130px, and a long item name that still didn't fit at the smallest text scale is now trimmed with an ellipsis instead of running over the neighbouring panel - the last cause of the "text spilling into the next box" report.

## Added — collapsible navigation

Each of the three navigation tiers gets its own clickable header bar: **MAIN TABS**, **SUB-TABS** and **GROUPS**. Click one to collapse that tier; click again to bring it back. Collapsed tiers take up no vertical space at all, so the settings list grows into whatever you free up — useful when you're deep in one sub-tab and want more rows on screen than navigation. The headers stay visible when collapsed (they're how you get back), and collapsing never changes which tab, sub-tab or group is selected.

The theme picker's header in the settings popup is now a matching clickable box rather than bare text, so the whole interface uses one visual language for "click this to expand".

## Added — 387 written setting descriptions

Every setting in the five new tabs (plus the new spawner page) now has a real name, a plain-English description of what it controls, and a "what happens if you raise or lower this" line — the same treatment the older tabs already had. Previously these would have shown a raw key name and a placeholder.

## Fixed

- **Far too many wandering cultivator NPCs, and turning the spawn settings down did nothing** (reported on CurseForge by Mauro023 - thank you). Root cause was this mod's own NPC spawner, which fills in for vanilla (vanilla never spawns these NPCs naturally, their spawn category has a cap of 0). Three things were wrong with it: it rolled a 15% chance every 5 seconds per player, times a 1.5 multiplier - roughly 2.7 spawns a minute per player; its only cap was "8 within 128 blocks of the player", measured from wherever the player currently was, so walking anywhere left a trail of NPCs behind with no world-wide limit at all; and none of the settings on the Spawns page reached it - the two spawn-chance sliders there only feed a base-mod check that vanilla never runs for this NPC, and the spawner's single real dial was tucked under Sects > NPC Population. So players turning the visible chances "insanely low" were adjusting a code path that never fired.
  Now: the spawner has a proper **Spawns > Wandering Spawner** page with every control - on/off, roll interval (default 10 s, was 5), chance per roll (default 3%, was 15%), crowd size near a player (default 6, was 8, over a wider 96-block radius), a **hard ceiling per dimension** (default 24 - the cap that was missing), and an Overworld-only option. The old multiplier still works on top and now defaults to 1.0 instead of 1.5. Net effect at defaults: about a fifth of the old rate, and a world that can never exceed the ceiling however far you travel. The spawner also finally respects the master "enable spawn overrides" switch.
- **A second contributor to the same problem**: the override that makes cultivators spawn on superflat worlds had quietly grown into a global bypass. It returned "yes, spawn" for every non-natural spawn type and every non-Overworld natural spawn, skipping the base mod's own vanilla light/ground check *and* its 6-per-96-blocks crowd cap on those paths, and turned chunk generation's 30% roll into 100%. It now touches only natural Overworld spawns on superflat worlds, and on those applies the same configured chance and the same crowd cap the base mod uses everywhere else.
- **True Immortal and Loose Immortal were swapped in three realm-indexed tables.** The base mod's realm ladder puts True Immortal *before* Loose Immortal — confirmed three separate ways against the base mod itself. Three of this mod's per-realm arrays had them the other way round: the NPC realm spawn weights, and the new beast HP and beast attack multipliers - so the "Loose Immortal" setting silently drove True Immortal and vice versa. Invisible at the defaults (the affected pairs happen to share the same default value), wrong the moment anyone tuned one. Fixed; no config keys changed.
- **Hover sounds never played on the config screen when opened from the main menu.** The sound helper's only playback path needed a loaded world, and bailed out silently when there wasn't one — which is exactly the case when you open the config from the mod list before loading a save. It now falls back to a non-positional UI sound in that situation; in-world playback is untouched.
- **A dead override in the chargeable-spell settings.** The Buddha Fire Lotus "ready qi" setting was pointed at a value the base mod doesn't actually have, so it silently did nothing. Found while re-checking a neighbouring value, and queued for a proper fix rather than papered over.
- **One new setting was cut rather than shipped broken.** A sky-splitting charge threshold turned out to collide with an already-wired value in the same class — both would have fought over the same number, with whichever applied last winning. Rather than ship a setting that quietly fights another, it was removed.

## Changed — the item picker

The Starting Items picker was flat and silent. Every row and button now has the same eased hover glow the main screen uses, hover sounds are wired across all of its controls, and its window chrome, panels and scroll bars now follow the active theme instead of a hardcoded palette — so all 30+ themes finally reach it. Reduce Motion is respected: the glow degrades to a static highlight rather than animating.

The recent fix that stopped long item descriptions spilling out of the Preview panel is untouched.

## Under the hood

- **30 new override systems**, bringing the mod to **131 overrides across 796 individual touch points**, every one of them checked against the base mod's real values rather than assumed.
- Every new override carries the mod's standing safety flag: if a future base-mod update removes or renames something an override hooks into, that one setting quietly stops applying instead of the whole modpack failing to load.
- Several values were deliberately **left unwired and documented as such** rather than guessed at — among them the blood-blade tier arrays (only read at item-registration time, before the config file is even loaded, so an override there would be inert), the fruit system's hash-avalanche constants, and a weighted starting-age distribution whose twelve interdependent values need their own pass.

## Known limitations

- Weapon attack speed and a few HUD position/size settings still have no effect — both are baked in too early for a live override to reach safely (unchanged since 1.0.5).
- Furnace slot counts and processing times for Alchemy and Refining are still not wired.
- Two 0.1.1479 systems remain catalogued but not yet configurable: the technique-stacking/resonance system and the NPC gift-diminishing economy, plus the remaining two equipment ability services (treasure abilities, storm rift).
- The Sects category (roughly 126 settings, the single largest) has still not been re-audited against current base-mod behaviour.
- Mortal-realm values remain editable by hand in the TOML (see the Mortal section above). This mod will not offer, write or reset them.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## Coming Next

No dates promised - but this is what is actually being worked on.

**The themes.** All 34 themes are being reworked to be more pronounced and more distinct from one another, each with its own steady background animation: starfall, a supernova in slow motion, a star collapsing inward, a galaxy turning. The config window itself is being rebuilt around better partitioning and a more professional layout to carry them. This is the very next thing being worked on.

**A per-tab "General" sub-tab.** Every main tab getting its own General page holding that section's master override toggle, so the switch that turns a whole system on or off lives with the system instead of on a shared page.

**The deep wiring waves.** A full audit of the base mod turned up **1,763 more values across 187 systems** this mod does not yet reach. They are queued in order of how much they'd actually change your game — the spell handlers first (Personal Spirit Shield, Blood Shadow Dash, Nine Heavens Thunder Prison, Flame Dash, Lightning Arc, Wither Touch, Soul Hook, Thunder Blink), then spell entities, then the blood and dao arts, then the remaining systems.

**The systems still catalogued but not yet configurable**: technique stacking and resonance, the NPC gift-diminishing economy, treasure abilities and storm rift.

**The Sects audit** — the largest single category in the mod, and the one most overdue a pass against current base-mod behaviour.

**The dead Buddha Fire Lotus "ready qi" override**, found during this release's verification and documented rather than papered over, gets a proper fix.

**A three-second hover on a main tab opening a movable, resizable info window** — designed, deferred, not forgotten.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

## Thank you

To **Mauro023**, who reported the wandering-cultivator flood on CurseForge and stuck with it — that report led to two separate root causes, one of them a spawn-rule bypass that had been quietly running since long before anyone noticed.

To everyone who wrote in about the config screen having "no order" — the realm ladder in this release exists because of you, and it's now the single source of truth the whole mod sorts by.

And to the **Xiaoxiang Cultivation World** team, whose mod this one exists only to serve. Every number in here is theirs first.

*✦ May your Dao be steady, your Qi be plentiful, and your tribulations survivable. ⛵🌌⚡*
