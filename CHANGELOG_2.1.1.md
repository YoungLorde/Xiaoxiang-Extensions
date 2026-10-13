# Xiaoxiang Config Extension 2.1.1

Villages come back, experience can be split between levels and cultivation, and
the config screen only shows you settings that can actually do something.

---

## Villages and villagers return

**If your world has no villagers, no wandering traders and no villages, this mod
was the reason.** Earlier versions removed all of them from every world, along with
pillager outposts, and gave you no way to switch it off. That took villager
trading, village iron golems, raids and the wandering trader's stock with it.

They are back, and on by default. Update, and the next world you generate has
villages in it again. Worlds you have already explored keep whatever they
generated, because chunks are only built once — new land will have them.

If you liked the village-free world, it is one tick away under
**Miscellaneous → Vanilla → Villagers**, and you get finer control than before:

- **Villagers and Sects Together** — the master switch. Off gives you exactly the
  old behaviour.
- **Villagers** — keep the villages but empty them, if that is the world you want.
- **Wandering Traders** — separate, because he is the only source of several
  items that has nothing to do with villages.
- **Village Structures** — whether villages are built at all.
- **Pillager Outposts** — kept apart from villages, because outposts are where
  raids come from.

One thing worth knowing if you turn it on: sects and villages are both placed
during world generation and neither checks for the other, so they can occasionally
land close together. It looks odd where it happens; it does not break anything.

---

## Experience can be split between levels and cultivation

The experience-to-cultivation conversion is no longer all-or-nothing.

**Split: Share to Cultivation** decides how much of each gain feeds your realm and
how much stays in your bar as ordinary levels.

- **100** — everything becomes cultivation. Your levels stop rising entirely.
- **50** (the default) — a clean half-and-half. Your realm advances and you can
  still enchant.
- **0** — nothing converts.

Nothing is lost to rounding. A five-experience zombie at a fifty percent split is
worth a quarter of a cultivation point, and that quarter is carried to your next
kill rather than thrown away.

---

## Settings appear when they can do something

Switch on **Convert Experience to Cultivation** and the four settings that shape
it appear underneath. Switch it off and they fold away again.

That now works across most of the screen. Turn off a master switch — Sects,
Loot, Formations, Physiques, Bosses, Talisman, Equipment and about two dozen
more — and the settings it governs get out of the way instead of sitting there
doing nothing.

Some sections deliberately do **not** fold away, and that is not an oversight.
In those, at least one setting still takes effect even with the master switch
off, so hiding them would hide something that is still working. A screen that
hides a live setting is worse than one that shows a quiet one, so those stay put
until each is sorted out properly.

A master switch never hides itself, whichever section it lives in.

---

## Smoother

The whole screen moves rather than snaps. Hover highlights fade out more gently
than they fade in, so sweeping across the tabs leaves a wake instead of a row of
hard flickers, and the settings list cascades in with enough travel to actually
be seen. Everything still finishes in under a third of a second — it is more
visible, not slower.

The animation speed slider under **UI → Appearance** scales all of it, and
Reduce Motion still turns it off completely.

---

## Fixed

- **Some tabs were silent on hover.** Moving quickly across the tab bar could
  swallow the sound for the tab you landed on, and it would stay silent until you
  moved away and came back. Every tab sounds every time now.
