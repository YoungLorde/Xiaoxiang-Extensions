# Xiaoxiang Config Extension 2.1.2

Villages come back, your vanilla stats actually change when you change them,
experience can be split between levels and cultivation, and every section of the
config screen now opens and closes from its own switch.

---

## The Vanilla page works

Setting **Base Max Health** to 100 used to do nothing at all. Nor did armour, nor
movement speed, nor anything else on that page — you could set a number, close
the screen, and find your character exactly as it was. That is fixed, and the
page now behaves the way it reads.

Two things changed underneath.

**Only what you actually move is applied.** A setting still sitting at vanilla's
own number is left alone entirely — the mod does not write that stat at all, so
anything else on your load order that wants to own it still can. Move one, and it
takes effect within a couple of seconds. No relog, no new world.

**Putting a value back puts the stat back.** Not back to a hard-coded number —
back to exactly what the stat held before this page first touched it. So if
another mod set your baseline armour and you borrow the page for a while, you get
that mod's number returned to you, not a zero.

The page's switch is still there and still works as a kill switch for the whole
group, and it now starts **on**. It can, because with every value at its default,
on and off do exactly the same thing: nothing.

Lowering max health still trims you to the new ceiling so the hearts and the bar
agree. Raising it still hands you the new hearts empty, the same way a health
boost does.

---

## Villages and villagers return

**If your world has no villagers, no wandering traders and no villages, this mod
was the reason.** Earlier versions removed all of them from every world, along with
pillager outposts, and gave you no way to switch it off. That took villager
trading, village iron golems, raids and the wandering trader's stock with it.

They are back, and on by default. Update, and the next world you generate has
villages in it again. Land you have already explored keeps whatever it generated,
because chunks are only built once — new ground will have them.

If you liked the village-free world, it is one tick away under
**Miscellaneous → Vanilla**, and you get finer control than before:

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

The experience-to-cultivation conversion is no longer all-or-nothing. Two settings
decide the split, and they are two views of the same number:

- **Split: Share to Cultivation** — how much of each gain feeds your realm.
- **Split: Share Kept as Levels** — how much stays in your bar for enchanting.

They always add up to 100. Move either one and the other follows immediately — set
one to 51 and the other becomes 49 on its own. Both exist because which side you
naturally think in depends on what you are trying to protect.

At 100 to cultivation your levels stop rising entirely. At 50/50, the default, your
realm advances and you can still enchant.

Nothing is lost to rounding. A five-experience zombie at a fifty percent split is
worth a quarter of a cultivation point, and that quarter is carried to your next
kill rather than thrown away.

---

## Every section opens and closes from its own switch

Where a section has a master switch, that switch is now the **first row of the
section**, under its own heading — not somewhere further up the page, and not on
a different page entirely. Turn it off and the section folds up behind it. Turn it
on and its settings cascade back down. About seven in ten sections across the
whole screen work this way now.

The rest deliberately do not fold, and that is not an oversight: in those, at
least one setting still takes effect even with the master switch off, so hiding
them would hide something that is still working. Those sections still collapse —
click any section heading to fold or unfold it, anywhere on the screen — they just
do not have a switch that claims to govern more than it does.

A switch can never hide itself, so a folded section always keeps the row that
unfolds it. There is no way to close something you cannot reopen.

**Sections also stop splitting in two.** A setting you had favourited or changed
could pull itself above the rest of its section, which drew that section's heading
twice, once above each half. That is what people were seeing as headings
"repeating themselves". Sections are now kept whole, in the same order as the
buttons above the list, and each heading is drawn exactly once.

---

## Descriptions read properly on on/off settings

Hovering an on/off setting used to explain it in terms of **larger** and
**smaller** values — which a switch does not have. Twenty-two settings read that
way, including every villager setting and every master switch. They now say **On**
and **Off**, and the sentence that follows no longer repeats the word back at you.

Settings whose two halves said the same thing, or said nothing ("no effect"), now
say nothing at all rather than filling two lines with it. **Base Luck** has been
rewritten to say what luck genuinely does — it moves fishing and a handful of
chests, and does nothing whatever to mob drops, ore, or this mod's own tables — so
it is not mistaken for a general "better loot" dial.

---

## Smoother

The whole screen moves rather than snaps. Hover highlights fade out more gently
than they fade in, so sweeping across the tabs leaves a wake instead of a row of
hard flickers, and the settings list cascades in with enough travel to actually be
seen. Everything still finishes in under a third of a second — it is more visible,
not slower.

The animation speed slider under **UI → Appearance** scales all of it, and Reduce
Motion still turns it off completely.

---

## Fixed

- **Some tabs were silent on hover.** Moving quickly across the tab bar could
  swallow the sound for the tab you landed on, and it stayed silent until you moved
  away and came back. Every tab sounds every time now.
- **The Vanilla page is laid out properly.** Attributes, Survival, Experience and
  Villagers are four sections on one page, each led by the switch that governs it.
- **Existing configs are corrected on first load.** The Vanilla page's switch was
  written into every config file generated by an earlier version, and a config file
  is never rewritten once it exists — so updating alone would have left the page
  just as dead. It is corrected once, automatically, and noted in the log.
