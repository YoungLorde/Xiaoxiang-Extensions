# Xiaoxiang Config Extension — 2.1.3.3

Scroll bars that drag, a search bar on every container, an inventory that
gathers itself and grows past vanilla, and a HUD position you pick from a grid.

---

## 🖱️ Every scroll bar can be dragged

The override tree had no scroll bar at all, and the pinned description windows
drew one that did nothing when you grabbed it. Both are fixed, and so are five
more that only answered the mouse wheel: the dropdown lists, the navigation
panel, both halves of the pre-world perk screen and the theme picker. Every bar
in the mod now drags, and the wheel still works everywhere it did.

## 🧰 The item picker adds slots, not counts

Double-click an item in the picker and it adds **another slot** of that item
rather than raising the count on the one you already had — which is what the
identity and loadout lists actually needed.

**Realm Expansion's gear is grouped by grade** in the picker — Inferior Gear
up to Immortal Gear, in that order, each split into weapons, tools and armour —
the same way the expansion's own creative tabs are laid out, and its demonic
gear the same way after it. Its spiritual farmland and paths sit with the
rest of its land, and its Inferior and Top pills have a group of their own.

## 🔍 A search bar on every container

Your inventory, chests, furnaces, other mods' screens: a search box sits above
each one. Type and the slots holding a match stay lit while the rest go dark.
Click it or press the search key to type; Escape, Enter or a click elsewhere
hands the keyboard back and keeps the text. While you are typing, no key
reaches the screen, so a letter cannot close the inventory or swap your hotbar.
**Vanilla → Inventory → Search Bar On Every Container**, on by default. The creative inventory keeps
its own box.

## 🔤 Typing into a screen's own box no longer closes it

A container screen with a text box of its own — the refining search, the name
of a talisman or a formation — let the keys fall through to the container: the
**E** typed into a search closed the screen mid-word, a **Q** threw the stack
under the pointer, and a digit swapped it into the hotbar. While such a box has
the cursor, every key now stays in it, and Escape still closes the screen.
Click anywhere else and the box lets go, so the E closes the screen again once
you are done typing.

Two smaller fixes in the same place: the search key, bound to a letter, no
longer types that letter into the bar it opens, and the sort and quick-stack
keys, bound to letters, stay quiet while you type.

## 🖐️ Drag a stack to gather it — no mouse mod needed

Pick a stack up and keep the left button held: every stack of the same item
you sweep the pointer across is **gathered onto the cursor**, as long as it all
fits in one stack. Hold **Shift** while you sweep and those stacks **move to the
other inventory** instead. **Vanilla → Inventory → Drag To Move → Drag A Stack
To Gather**, on whenever Drag To Move is on.

With **Drag To Move** and **Scroll To Move** switched on, the inventory handles
the mouse the way the popular mouse-tweak mods do: drag with an empty hand to
move each slot you pass, drag a stack to gather more of it, Shift-drag to move
every stack of one item, and the wheel to send items across or bring them back.
Each gather is the game's own two clicks — put down, pick up — so nothing can
be lost or made out of thin air. Both features stand down on their own while
such a mod is installed, so switch them on first and remove it after.

A drag or the wheel **never touches a crafting result, a villager's offer, or an
anvil's, smithing table's or stonecutter's output** any more — shift-clicking
one of those crafts or trades as much as it can, and a drag passing over it
never meant that. A furnace's finished output still moves.

## 🎒 Slots that gather, and rows past vanilla — inside the window now

With inventory slots limited, the slots you still have now **close up and
centre themselves** instead of leaving holes: one slot sits in the middle of
the grid, two sit side by side, a full set is exactly where the game puts it.
The empty squares are painted over so only the slots that exist are drawn.
**Vanilla → Inventory → Slots → Gather The Slots You Have**, on by default. A
container's own slots are never moved.

**A screen that hides your slots itself is left alone.** A page that shows
something else where your inventory goes - a cultivator's Details page, for
one - used to get grey boxes painted over it while slots were limited. Only
the holes the slot limit makes are painted now.

The main grid can also **continue below the three rows Minecraft draws** — up
to **sixty extra rows** — and the extra rows now sit **inside the inventory
window**, directly under the usual three, with the hotbar beneath them, so the
grid reads as one grid that is longer. The window grows by the rows shown;
past what your screen has room for, the rows scroll — the wheel over them, or
the bar in the window's right edge. Everything behind those rows is kept whole
whatever the setting says, so turning rows off never loses what was in them —
a row that no longer exists stays reachable until it is emptied.

**Slots unlock one at a time now.** **Vanilla → Inventory → Slots → Slots You
Always Have** counts in slots beside the old rows setting; the smaller of the
two wins, so a pack can start a player with five slots and let levels bring
the rest.

**Locked extra rows say so, and can be opened outright.** With slot locking on,
the extra rows used to open only through experience levels, and a locked slot
was drawn as an ordinary empty frame that ignored every click - so at level 0
the whole extension looked broken. A locked slot now shows a padlock, and
pointing at it says the experience level it opens at, or how to open it.
**Rows You Always Have** and **Slots You Always Have** now count the extra rows
too: with 15 rows in total, set them to 15 and 135 and every row is yours from
the start.

## 🔢 Big numbers keep going past B

Values on the config screen used to stop at B, so eighty-three trillion read
"83,773.00B" and kept getting longer. The letters now go on - **T, Qa, Qi**,
and for decimals Sx, Sp, Oc, No and Dc - and the figure in front of the letter
is always under a thousand: **83.77T**, and the largest whole number a setting
can hold is **9.22Qi**. A value that rounds up to a thousand takes the next
letter, so 999,999 reads 1.00M rather than 1000.0K. The Player Bars and the
cultivation panel use the same letters.

## 🔍 Search by tag

The container search bar now takes **#tag** the way a storage mod's does:
`#ingots` lights every ingot whatever mod made it, `#forge:ores` the ores,
`-#logs` everything that is not a log. `@mod` and `-word` work as before, and
they combine.

## 🧭 Starting items no longer pile up on the identity card

An identity with a long starter kit used to squeeze its item icons together
until they drew over each other. The grid keeps its spacing now, shows as many
rows as fit under the description, and the rest scroll — the wheel over the
grid, or the bar beside it. (The wheel and the bar did nothing on the
choose-origin screen at first; both work now.)

## 📜 Write your own origin stories

Every identity's story on the choose-origin card can be rewritten. The Identity
page has an **Origin Stories** group listing every identity the cultivation mod
has, each with an **Edit** button; write your own and the card tells yours when
a world is made, clear it and the original comes back. Custom identities get an
**Edit** button for their name and story too — the portrait, lifespan and
starting items stay as they were.

## 📐 HUD position is a grid you click, and it moves the cultivation panel

**UI → HUD → HUD Position** is a three-by-three grid instead of two pixel
numbers. Click a cell and that is where the base mod's cultivation panel — the
one with the portrait, the realm name and the bars — goes: top left, top
centre, top right; middle left, centre, middle right; bottom left, bottom
centre, bottom right. The hovered cell's name shows beside the grid; the bottom
row keeps clear of the hotbar.

It only applies with **Use HUD Position** switched on. Off — the default — the
base mod places its own panel exactly as it always has. **HUD X** and **HUD Y**
are now a small nudge in from the anchored edge.

The grid had been drawn as a plain text box in the last version: the setting is
stored as a name, and the screen took a name for text. Fixed.

## 🖐️ Drag the HUD from the inventory screen

Press E and there is a small **HUD** button just outside the inventory
window's top-right corner. It opens the game dimmed, with a rectangle where the
cultivation panel is. **Drag it.** When you let go it snaps to the nearest of
the nine places and names it; **Done** saves that place as the HUD Position and
switches Use HUD Position on, so the panel is there from the next frame. Escape
leaves without saving.

**The Player Bars drag the same way.** With the bars on, a second rectangle sits
where they are drawn - vanilla's rows, or your stacked column. Drag it and let
go: the bars go exactly there, in either layout. Done saves only what you
moved, so moving the bars never touches the panel's place.

## 🧘 The base mod's cultivation panel is back where it belongs

The panel with the portrait and the realm name - the base mod's own - had been
pushed to a spot a hundred pixels above the bottom of the screen, where it read
as a stray bubble over the hotbar. It now draws exactly where the base mod puts
it.

## 🗂️ Presets are a window

**Presets** sits at the top now, beside **Hide All** and **Show All**, so it
is there the moment the screen opens - the bottom bar starts folded away, and
the button is gone from it. **SavePreset** stays down there as the quick save.

It opens a window, the same shape as the item picker: every preset in a list,
and **Save current as...** which asks you for a name. A mod that has its own
presets lists them first, under *Difficulty* and that mod's name - Realm
Expansion's nine curves, Nine Heavens among them, gentlest first, each with an
**Apply**. A mod can also keep presets for one part of itself under a heading
of their own - Realm Expansion's *Wild Cultivators* follow its curves. Your own
presets follow with **Apply** and **Delete**. What a press did shows along the
bottom of the window as well as in the corner.

The window is solid and the screen behind it is dimmed and asleep: nothing
shows through the window or lights up under the pointer, the wheel scrolls
only its list, typing reaches nothing behind it, and it sits in front of
pinned tooltips and item icons. A click outside it closes it and does nothing
else; **Esc** closes it too.

It used to apply the first preset it found without saying which, and
**Save Preset** used to name them by the clock. Presets are kept in a file now,
too. They used to live only until the game closed.

## 🧹 Twelve settings that did nothing are gone from the screen

Ten element-position settings, the portrait size and one colour never had any
effect on the screen, and now they are not offered. Their lines are left alone
in the file for anyone who had them.

## ❤️ The hearts can now wait their turn

**UI → Player Bars → Health** gains one setting: **Only Above This Much Health**.

Player Bars could already turn the heart row into a bar with the figure written
on it, in the hearts' own place, at the hearts' own width. What it could not do
was wait. It was on or off, for twenty health and for twenty thousand alike.

Twenty hearts is not the problem. Two rows of hearts is readable — you can see
three hearts missing without reading anything, which a bar cannot do. The
problem starts further up, where the rows climb over the hotbar, over the chat,
and over whatever you were trying to look at, and counting them stops being
possible at all.

So set a number and the swap happens there:

```
onlyAboveMaxHealth = 40
```

40 is twenty hearts — two full rows. Below it you get vanilla's own hearts,
untouched. Above it you get the bar reading `12,480 / 18,000`. **0 means always,
and 0 is the default**, so the bar behaves exactly as it did before unless you
set a threshold yourself.

It governs the hiding as well as the drawing, so there is never a frame with the
hearts gone and nothing in their place. Health only — hunger, armour and breath
are always twenty, so they have nothing to outgrow.

### 🤝 And it stands down for other mods

Several mods replace the heart row, and two of them doing it on the same screen
means two bars drawn on top of each other in the same place. Neither mod is
wrong; they simply cannot see each other.

This one now asks the question of the frame rather than of a mod list: **has
anybody else already taken the hearts this frame?** If so, the health row stands
down and the rest of the HUD carries on as normal. That needs no mod names, no
version checks and no agreement with anybody — it is right for whichever mod is
doing it, including ones that do not exist yet, and it stops being true the
moment they turn their own bar off.

> **Where to find it:** **UI → Player Bars → Health → Only Above This Much
> Health**. **Draw Bars In Place** is what puts the bar where the hearts were.

---

## 🩺 Player Bars are on out of the box

**Enable Player Bars** now defaults to **on**, and the cultivation bar overlay
defaults to **off**.

This is the one default that changes, and it is the one worth explaining. The
bars existed to solve a problem you meet by playing: raise max health past a
hundred and vanilla answers with rows of hearts climbing over the hotbar and the
chat. Shipping the fix switched off meant the people who hit that problem were
exactly the people who had no reason to go looking for a setting that would have
prevented it.

A display that swaps itself out halfway through a save is one you have to learn
twice, so it is better on from the first moment of a new world than turned on in
the middle of one.

If you preferred the hearts, **UI → Player Bars → Enable Player Bars** turns the
whole page back off and nothing else about your HUD moves.

---

## 🐺 Health bars over creatures

Creatures now carry a health bar over their heads, with the figure written on
it - **12/20**, **7.5/20**, **15K/30K** once the numbers get big. It is drawn
the way the cultivation mod draws the bars over its cultivators, at the same
scale and in the same colours, so the two read as one.

- **Only the hurt, and the one you look at.** A creature at full health has no
  bar until it is hit, so a field of animals stays a field. The creature you are
  looking at gets one anyway, out to the bar range and never through a wall, so
  you can size something up before the fight. Switch **Only When Hurt** off for
  a bar on everything in range.
- **Never two bars over one head.** Players get none, and neither does anything
  the cultivation mod already puts its own panel over. With **Neat** installed
  the page draws nothing at all - the list of mods it gives way to is a setting.
- **Never over these** takes creature ids, or a whole mod with a star:
  `somemod:*`. The armour stand is on it by default.

All of it is on the new **UI → Creature Bars** page, and it follows **Enable
HUD Overrides** like the player bars.

---

## 🛡️ Armour keeps protecting past the game's limits

Two numbers in Minecraft's damage formula decide what high armour is worth,
and both are now settings on the Vanilla tab, next to the armour limit:

- **Armour's Best: Most Of A Hit Stopped** - the game stops at most 80% of a
  hit, however much armour you wear. Raise it and armour keeps paying: at 95 a
  hit loses all but a twentieth. It stops at 99, so armour never makes you
  untouchable.
- **Armour Against A Big Hit** - against a hard enough blow only a fifth of
  your armour counts, so against a huge one 30 armour is worth 6. Raise it
  and big hits meet more of your armour.

Both take effect with the Vanilla tab's overrides on and are the game's own
figures out of the box.

**And a fault in the game's formula is set right.** With the armour limit
raised past 100, a fifth of your armour was more than the formula's cap, and
a big enough hit came out below zero - it did nothing at all. Now no hit is
cut by more than the most armour can stop.

---

## 💬 /configext - the settings from the chat box

Fifteen commands, for a server with nobody at the config screen or for finding
a setting fast. Setting names come out in your own language.

**Anybody** can read:

- `/configext get <path>` - the value, the default and the limits
- `/configext describe <path>` - what it does
- `/configext find <words>` - every setting with all those words in its name or
  path; click a result to look it up
- `/configext list <section>` - a whole section with its values
- `/configext changed` - everything that is not at its default
- `/configext tabs`, `overrides`, `version`, `standdown`, `help`

**Operators** can write:

- `/configext set <path> <value>` - on/off, a number, one of the choices, or a
  list with commas (`none` empties it)
- `/configext reset <path>` and `/configext toggle <path>`
- `/configext override <system> on|off` - one whole system, by its short name
- `/configext backup` - a copy of this mod's settings files, in
  `config/xiaoxiang_config_ext/backups`

A path is matched exactly, never guessed; a value outside the setting's own
range is refused; and every change is saved to the file and written to the
server log with who made it. Client settings - your HUD, your bars - stay on
the config screen, since they live on each player's own computer.

---

## ⌨️ Typing in a value box works properly

Three fixes to the same box, all of them things you would hit within a minute of
editing a number by hand.

**Backspace and Delete.** The search field was being offered the keystroke
before the box you were actually typing in, so Delete went to the search and the
value box never saw it. The box you are editing is asked first now.

**It no longer rewrites the value on every keystroke.** Clearing `1000` to type
`250` used to pass `100`, `10`, `1` and then an empty box to the setting on the
way through — four values you never meant, each one applied. The value is read
when you finish, not while you are still typing.

**Tooltips wait longer.** The hover panel and its pinning now wait 2.5 seconds
instead of 1.5, so passing over a row on the way somewhere else no longer opens
a panel on top of what you were reading.

---

## 🇪🇸 Spanish joins the language cycle

The language button under the title now offers **Spanish**, alongside English
and Brazilian Portuguese.

Every word of the screen itself is translated — all 21 tabs, all 103 sub-tabs,
the groups, the buttons, the glossary keywords and every setting name. The
explanations behind a hover are still being written, and until they are finished
they fall back to English rather than showing a blank or a raw key.

Brazilian Portuguese is complete again, hover text included, and Spanish has
every name: both now cover the settings added in this version — the HUD
position grid, the hearts threshold, the extra inventory rows, the search bar,
gathering your slots, saved origin stories and dragging a stack to gather.

That split is now the rule rather than a one-off. **No language is offered until
every word drawn on the screen is translated in it** — a screen you cannot
navigate is worse than one you have to read twice — and the hover text is
allowed to lag behind, measured and reported rather than quietly missing. Nine
more languages are written and parked, and each joins the cycle the same way.

---

## 💧 Infinite Sources reach every fluid

The switches on the **Infinite Sources** page did nothing for a fluid built on
Forge's own fluid class - and most mods build their liquids on it. Such a fluid
answers "may I become a source?" by itself, and the answer never passed the
point the switch was listening at. The switch now also listens where Forge asks
it of every fluid, so a fluid you switch on refills whatever it is built on.
A mod's flowing half no longer gets a switch of its own, however the mod named
it: one substance, one switch.

## 📄 A sources jar, and a note inside the jar

Every release now comes with a `-sources.jar` beside the mod, and the mod jar
carries `ABOUT_THIS_JAR.txt` at its root.

Both exist to answer one question that keeps coming up. Decompile this mod and
calls into Minecraft read as `m_280666_` and `f_91074_`, which looks like
deliberate scrambling and is not: every Forge mod is compiled against readable
Minecraft names and reobfuscated back to intermediary ones so it can run against
the shipped game. This mod's own classes keep full debug information, real
method and field names, and original source filenames throughout.

A configuration screen is almost nothing but Minecraft UI calls, so it shows a
denser wall of those names than most mods do. The ratio is unusual; the
mechanism is not. The note in the jar says so, and the sources jar settles it.

## 📋 The Mods page lists everything new

The mod's entry on the Mods screen used to name only the first seven things
new in a version, so later additions never showed up there. It now lists
every one.
