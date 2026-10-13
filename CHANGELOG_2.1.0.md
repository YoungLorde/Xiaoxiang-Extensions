# Xiaoxiang Config Extension 2.1.0

A full audit of every override in the mod, a save-data bug fixed, seven dead
settings brought back to life, and a new Miscellaneous tab.

---

## A note on version numbers

The previous release, which was numbered 2.1.3, is **2.0.9**. It followed 2.0.8
and the work in it did not justify skipping four numbers. Versions from here move
one step at a time — 2.0.9 to 2.1.0 — and a larger jump only ever happens
deliberately. Nothing about that release's contents has changed; only its number.

---

## A setting was damaging save data

**`progression.genderEditsDefault` is fixed.** If you had ever changed it away
from 5, it was not only setting your gender edits — it was also rebuilding both
of your spell-loadout tables at the new size. Set it to 3 and your loadouts were
built with three rows instead of five, so the fourth and fifth loadout slots no
longer existed. Using one of them threw an error from deep inside the cultivation
system, a long way from the setting you had actually touched.

The setting now changes only what its name says. If you had lowered it and have
been seeing odd behaviour around spell loadouts, raise it back to 5 once, load
your world, and set it wherever you want it.

---

## Seven settings that were doing nothing

Every override in this mod is written to fail quietly rather than crash your game
if the original mod changes underneath it. That is the right trade, but it has a
cost: when one of them goes stale, nothing tells you. The slider still moves. The
number still saves. It just stops meaning anything.

This release checked all of them, one at a time, against what the original mod
actually does today. Seven had gone stale. All seven work again.

- **Meditation efficiency bonus** — the original mod moved where this lives. It
  is reconnected, and meditating is once again worth what this setting says.
- **Sword flight upkeep** — the original mod lowered the cost from 20 qi per
  second to 5. Ours was still pointing at 20 and matching nothing.
- **Void escape, both drains** — charging cost 10 qi per tick and staying in the
  void cost 5; both are now 1 in the original mod.
- **Qi shield cost** — the original mod cut it from 10 qi per point of damage
  absorbed to 4. Shielding is considerably cheaper than this mod believed.
- **Spirit gathering flower orb value** — orbs are worth 3 now, not 10.
- **Bone age rate** — the original mod split the method this was attached to.
- **Mutant root weather bonus** — we shipped 1.5; the real value is 1.2.

Each of those had a matching default that was also out of date, and a default
that no longer matches the original mod is a bug in its own right. Every one is
corrected, and **your existing config file is repaired automatically on first
launch**, with each change named in your log. A value you had deliberately tuned
yourself is left exactly as you set it.

### One setting was quietly driving two things

Alongside the orb value: the number 3 now appears twice in the same piece of the
original mod's code — once as the crowd limit for spirit gathering flowers, once
as the value of the orb itself. Our **`skipCount`** setting was changing both.
Raising it to allow denser orbs also made every orb worth more. They are separate
again.

---

## Spirit root absorption, rebuilt

The **Qi Absorb SSR** and **Qi Absorb SR** settings described a split the original
mod no longer has. Both were pointing at numbers that are simply not there any
more, so both had been doing nothing.

They are replaced by **seven settings, one per root family**, because "SSR" is
three different speeds now:

| Family | Cultivation efficiency |
|---|---|
| Heavenly Metal / Wood / Water / Fire / Earth | 1.35 |
| Mutant Ice / Mutant Lightning | 1.4 |
| Heavenly Hidden | 1.5 |
| Dual (all ten) | 1.25 |
| Triple | 1.15 |
| Quadruple | 1.1 |
| Five Root | 1.05 |

Every default above is the original mod's own number. **If you had tuned the old
SR setting, that number carries over exactly** — every SR-rarity root is a Dual
root, and every Dual root is 1.25, so it maps across without loss. The old SSR
number does not carry over, because there is nothing single for it to carry to.

This matters more than a normal setting: root efficiency is the first multiplier
in the chain that decides whether meditating does anything at all. Set a family to
zero and that family cannot cultivate by absorption, however good its technique.

Roots with no family-level number — None, Heavenly Sword, Five Element Chaos and
Broken Vein Body — are left alone rather than given an invented one.

---

## New: the Miscellaneous tab

A twenty-first tab, and the first one that is not about cultivation at all. It is
the home for everything outside the original mod, kept deliberately apart so that
looking for a realm setting never lands you in plain Minecraft by accident.

Its first sub-tab is **Vanilla**.

### Vanilla → Attributes

Your character's actual baseline — the numbers everything else is added on top
of. Armour adds to base armour; a Strength potion adds to base attack damage; a
realm adds to base health. Changing a baseline shifts the whole game rather than
one moment of it.

Base max health, movement speed, attack damage, attack speed, armor, armor
toughness, knockback resistance and luck.

### Vanilla → Survival

Fall damage and block-breaking speed, as plain multipliers where 1.0 is exactly
vanilla.

### Vanilla → Experience

Its own switch, separate from the tab's, because of what the last setting in it
can do.

- **Experience gain multiplier** — scales everything that earns you experience.
  What you spend is untouched, so this changes how long a level takes to earn,
  not what it costs. A gain is never rounded away to nothing.
- **Convert experience to cultivation** — with this on, killing, mining, smelting
  and fishing all become a slow second path up the realms, running alongside
  meditation rather than replacing it.

  The default is **10 experience per point of cultivation**, and that number is
  measured rather than invented. An early Qi Refining sub-stage needs about 200
  cultivation, so roughly 2,000 experience carries you through one. By True
  Immortal a sub-stage needs around 20,900, so the same effort barely registers.
  That curve is the point: a reason for a new cultivator to go out into the
  world, and never a way for a mob farm to replace meditation.

  Four more settings shape it — the ratio, whether the experience is spent or
  kept, whether the original mod's "you need an equipped technique" rule still
  applies, and a ceiling per pickup for anyone who plays with farms. Fractions
  are carried rather than dropped, so a five-experience zombie is not worth
  nothing.

**This is the one place the Vanilla tab touches the cultivation mod**, and it is
why it has its own switch. Everything else under Vanilla runs on Minecraft's own
hooks and cannot affect a cultivation save.

### Settings that appear when they become relevant

The experience group reveals itself a layer at a time. Switch the group on and
the multiplier and the conversion switch appear. Switch conversion on and the
four settings that shape it appear. Until then they are not greyed out — they are
not there at all. A page of controls that cannot do anything is worse than a
short page.

### The whole tab starts switched off

Every default on it is Minecraft's own value, so turning it on changes nothing
until you move something. Turning it back off restores vanilla completely the
next time you log in, respawn or change dimension. Nothing here is written
permanently into your save.

---

## Version numbers from here

Ordinary updates move the last number by one and nothing else. A bigger jump is a
decision, not something that happens on its own because a release felt large.
Numbers only ever go up.

---

## Not changed, and worth saying plainly

**Vanilla villages and villagers are still suppressed**, and that suppression is
in this mod, not the original one. It is unconditional and there is no setting
for it. That is a deliberate choice for now rather than an oversight, and if it
is why you have no villages, that is the reason.

Three old files in the mixin folder — `BeastRealmMixin`, `DistanceManagerMixin`
and `CultivationScreenUIScaleMixin` — are empty placeholders left from retired
features. They contain no code and change nothing; they are just clutter.
