# Xiaoxiang Config Extension 2.1.3

Every player attribute now has a ceiling you can lift.

---

## The number you type is the number you get

Set Base Armor Toughness to 50 and you had 20. Set it to 500 and you had 20.
The setting was being saved correctly, applied correctly, and then thrown away
one layer further down — Minecraft gives each attribute a hard maximum of its
own and clamps to it without a word.

Those maximums, and they are lower than most people expect:

| | Minecraft's ceiling |
|---|---|
| Max Health | 1024 |
| Movement Speed | 1024 |
| Attack Damage | 2048 |
| Attack Speed | 1024 |
| **Armor** | **30** |
| **Armor Toughness** | **20** |
| Knockback Resistance | 1 |
| Luck | 1024 (floor −1024) |

Each of the eight now has an **Upper Limit** setting sitting beside it under
**Miscellaneous → Vanilla → Attributes**. Raise the limit and the value goes as
high as you set it. Armour toughness of 500 means 500.

Every Upper Limit ships at Minecraft's own number, so updating changes nothing
until you move one. And the limits go up to a million if that is the game you
want.

---

## What's worth knowing before you turn the dials to a million

None of these are warnings against doing it. They are the places where a bigger
number stops buying you anything, and it is better to know which is which.

- **Armor** — vanilla's damage formula stops improving at 20 points on its own.
  Past that, toughness is what makes the extra points matter against a big hit.
  Armour without toughness plateaus no matter how high the ceiling goes.
- **Armor Toughness** — no plateau. This is the one that was genuinely being
  cut off, and raising it does exactly what you expect.
- **Attack Speed** — around 20 the cooldown is already shorter than a single
  game tick, so the swing is fully charged every time. Higher is indistinguishable.
- **Movement Speed** — the ceiling was never what stopped you. Much past 1.0 you
  outrun chunk loading and fall through ground that has not arrived yet. No
  setting can fix that one.
- **Knockback Resistance** — its limit exists for consistency, not for use.
  Knockback is scaled by one minus your resistance and skipped entirely once that
  hits zero, so 1.0 is already total immovability and anything above behaves the
  same.
- **Luck** — feeds a weighting formula rather than a percentage, so it saturates.
  A luck of a million fishes much like a luck of a hundred. Only the ceiling
  lifts; negative luck still stops at Minecraft's floor of −1024.
- **Max Health** — no limit in the game, but the bar draws a row of hearts per 20
  health and stacks the rows up the screen. You will run out of screen before you
  run out of ceiling.

A raised ceiling belongs to the attribute rather than to you — there is one
Armor in the game and everything alive shares it. That gives nothing to anything:
no mob gets armour it did not have. It only means a value that would have been
clamped no longer is.

---

## Details

- Limits take hold within about two seconds of changing them, same as the values
  themselves. No relog.
- Only the ceiling moves. Every floor is left exactly as Minecraft set it.
- Setting an Upper Limit *below* Minecraft's own does nothing. These raise a
  ceiling; using one to tighten the game's own clamp would be a different feature
  wearing this one's name.
- With every limit at its default — the shipped state — this costs the game
  nothing measurable. The check that runs for every attribute of every entity is
  a single flag read that answers "no" and stops.

---

## Every treasure and every artifact, tier by tier

173 new settings, and not one of them is a number invented for this mod.

The base mod keeps each treasure's numbers in a five-rung ladder — Low, Mid, High,
Supreme, Immortal — and until now none of it was reachable. All of it is, under
**Equipment → Treasures**, one group per treasure:

- **Gourd** — how much qi a drink gives back, and its cooldown
- **Shield** — how many orbs, and how long they last
- **Rope** — bind duration, contact damage, cooldown
- **Seal** — size, contact damage, fusion damage, cooldown
- **Fire Orb** — bolt count, damage, terrain destruction, cooldown
- **Fan** — launch distance, flame reach, damage, burn duration
- **Dash** — distance and damage
- **Peach**, **Thunder Rift**, **Azure Rend** — their damage ladders

Three of those ladders get *shorter* as tier rises rather than longer — the rope,
seal and fire-orb cooldowns. That is the base mod's own design, not a slip: a
better treasure makes you wait less.

Refined artifacts get the same treatment under **Crafting → Artifact Tiers**:
matured attack damage, spell power bonus and spell qi discount; and for artifact
armour, durability, enchantability, toughness, knockback resistance, and defence
points broken out for all four slots at all five tiers.

Every default is the base mod's own value, read out of its compiled code array by
array and checked back against it — all 171 numbers, zero mismatches. Turning the
page on changes nothing until you move something.

Both pages open with their own on/off switch, and the tiers are listed in the order
they actually go in rather than alphabetically, so a ladder reads as a ladder.

---

## Your health bar can be a bar

Vanilla draws health as ten hearts. That works at 20 health and falls apart above
it: push Base Max Health to 1,000 and you get fifty rows of hearts climbing the
screen until you cannot see the game behind them. Armour and hunger do the same
thing. The higher this mod lets you tune those numbers, the worse it gets.

So under **UI → Player Bars** each of those rows can become a filling bar with
its number written on it. 1,000 health is a bar that says 1000/1000 and takes up
one row, the same row the hearts used to take.

**The bars sit exactly where the icons were.** Health on the left above the
hotbar, hunger on the right, armour above health, breath above hunger, the
experience bar across the middle. Nothing moves, so you keep looking where you
already look. If you would rather have them gathered together, one switch stacks
them all into a labelled column in the corner instead.

Each row is independent. Take a health bar and leave hunger as drumsticks if that
is what you want. Hiding the vanilla icons is a separate switch from drawing the
bar, so you can run both at once while you get used to it — and a row can never
end up hidden with nothing drawn in its place.

Bars slide to their new value rather than jumping, which is what lets you see how
big a hit was after it has already landed, and a dimmer trail marks where you
were a moment ago. The motion is timed in real seconds, so it looks the same at
30fps and at 240. Colours, width, thickness, position, whether numbers show as
18/20 or 90% — all of it is configurable, and all of it is off by default.

**Armour appears when you have armour** — worn, or granted by your realm, or
anything else that raises the stat. It does not sit there empty telling you that
you are unarmoured. It also reports your toughness beside the number, written as
12 (+8): toughness is what stops one huge hit punching through your armour, and
vanilla shows it nowhere at all, so most players never learn they have it.

Health warns you before it matters. Below a quarter — configurable, or off — the
bar changes colour and breathes, because movement catches the eye far better than
colour when you are busy looking at whatever is trying to kill you.

## The experience bar can be your qi instead

Qi is the resource this whole game runs on, and the base mod reports it in a
corner readout that is genuinely hard to read mid-fight. The strip under your
hotbar is 182 pixels of the most looked-at space on the screen.

One switch turns it into a qi bar, captioned with your realm and sub-stage —
"Golden Core - Middle" — so the single most useful thing in a cultivation game is
permanently on screen. Large amounts are shortened to stay readable: 1.2B, not
1204500000, because the long form is a wall rather than a number.

It is off by default, and if the qi reading is ever unavailable the bar quietly
shows experience instead rather than sitting there empty. One thing to know: with
the vanilla experience bar hidden you also lose the level number, which lives on
that bar — leave its "hide vanilla" switch off if you want both.

This is cosmetic and it is yours alone. These settings live in your own client
config, are never sent to a server, and change nothing for anyone else.

**One thing that was quietly broken.** The config already offered "Enable Bar
HUD", already described what it would do, and already defaulted to on — while the
code that registered the overlay had been removed and replaced with a comment
saying so. The renderer was still there; nothing ever called it. That setting has
been honest about its actual job for this release, and the new page is wired to
the screen properly.

---

## Every setting now explains itself

A third of this config had no description at all — 487 settings that showed you a
name and a number and left you to guess. Another 100 had one so short it said
nothing. You cannot configure what you cannot read, so that is fixed: **all 1,537
settings now carry a written description**, and none of them is a placeholder.

Each one tells you the same four things, in the same order:

- **what the system is** — what a Loose Immortal is, what a sect department does,
  what a spirit root actually governs
- **what this particular number controls**
- **how to read it** — ticks against seconds (20 a second), blocks against a
  placed cube, a probability against one-in-twenty, a multiplier against 1.0
- **what the base mod's own value is**, and how far the setting can move

Both directions are labelled too, so hovering tells you what raising or lowering
it will actually do rather than restating the name back at you.

A new setting can no longer arrive without a description, so this cannot
quietly slip again.

---

## Room for other mods

This screen was built to configure one mod, so every tab in it belonged to that
mod without needing to say so. That stops being true the moment a second mod's
settings arrive: "Equipment" means one thing under a cultivation mod and
something else entirely under a machinery mod, and two tabs with the same name in
one bar is not a naming problem to work around — it is a missing level of
structure.

So there is now a level above main tabs. Navigation reads **Mod → Main Tab →
Sub-tab → Group**. Each mod owns its tabs completely: selecting one swaps the
entire bar beneath it, nothing from one mod can appear under another, and two
mods may use the same tab name without either of them caring.

**You will not see it yet, and that is deliberate.** With one mod registered the
tier does not draw at all — a row of screen spent choosing between one option is
a row wasted, and the screen you have stays exactly the screen you have. It
appears by itself the moment a second mod registers a section. Nothing to switch
on, nothing to find.
