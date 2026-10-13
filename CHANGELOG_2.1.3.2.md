# Xiaoxiang Config Extension 2.1.3.2

Motion. Eight ways for the screen to come alive when you click something, and a
brief mark at the point of contact so a click always answers back.

---

## Eight entrance styles

**Tab Animation Style** had three values. It now has eight, and they are
genuinely different movements rather than the same one at different speeds.

- **slide** — the default, unchanged. Rows fade in and arrive from the left.
- **sweep** — from the right instead, and from further out.
- **cascade** — rows alternate left and right, so the list assembles itself from
  both edges at once. The busiest of the eight, and the one that most rewards a
  long page.
- **rise** — rows come up from below.
- **drop** — rows come down from above.
- **unfold** — up from further below, with a longer gap between each row, so the
  page reads as opening out rather than arriving. The slowest and most
  deliberate of the eight.
- **fade** — rows fade in without moving at all.
- **none** — instant. No animation of any kind.

The new vertical styles were the reason for a small piece of rework underneath.
A row's position is worked out once and everything it draws — background, border,
accent stripe, icons, text — is measured from that one number. Offsetting it
moves the whole row together and in step. The alternative was teaching every
draw call in the row about a second axis, which is how rows end up with their
text arriving a frame before their background.

**Two things still override your choice, on purpose.** Reduce Motion switches the
animation off whatever is set here. And a theme in the drift family always fades
without moving — that is the family's own character, and it wins.

An unrecognised value behaves as **fade**, which moves nothing and so can never
look broken.

---

## Click pulse

Clicking a tab, a sub-tab or a group now flashes it briefly.

A button that changes colour on hover gives you nothing on the click itself — the
cursor was already over it, so the hover state was showing before the press and
is still showing after. On a screen where one click can change the entire page
below, "did that register?" is a real question, and the honest answer is a mark
at the point of contact rather than waiting to see whether the list changed.

The flash brightens toward that element's own outline colour rather than toward a
fixed white, so a tab you have coloured yourself still flashes in its own colour
instead of looking painted over. It decays in a straight line rather than easing
out, because an eased fade reads as a glow that lingers — the opposite of "this
happened at that instant".

It is registered where the selection actually changes, not at each place you can
click from, so it behaves identically whether you used the mouse, the navigation
panel or the keyboard. Feedback that only one of three routes gives you is
feedback nobody can rely on.

Setting the style to **none** switches the flash off along with everything else.
Somebody who asked for no motion meant this too.

---

## Every theme is built on falling code now

This screen edits numbers for a living, and the digital rain was the one
background that was ever actually about that. So it stopped being one theme among
forty and became the floor all of them stand on.

**It does not replace anything.** The bamboo still sways, the embers still rise,
the galaxy still turns — they now do it through a fall of characters tinted to
that theme's own colours, drawn underneath at low opacity. And each theme falls
in its own alphabet, so the code reads as belonging to it: the bamboo grove in
pipes and braces that stack into stalks, the manuscript and the ledger in roman
numerals, the abyss in nothing but zeroes, the code themes in scrambled source.

A theme that is already busy is given less of it and a nearly still one more, so
the two layers never fight. **High Contrast gets none at all** — it is
deliberately motionless, and a background that moves is the one thing it must not
have.

Three new settings under **UI → Background**:

- **Code Underlay** — on or off.
- **Code Underlay Percent** — how strongly it shows, on top of each theme's own share.
- **Code Density Percent** — how many columns fall. This is the one to reach for on
  a weaker machine; it costs roughly what it says, because the work is per column.
  Changing it never moves the columns already falling, so it reads as thinning or
  thickening rather than the whole background jumping.

### Why it no longer looks janky

The old rain snapped every glyph to a whole pixel on a fixed grid, so a column
travelling at 20px/s visibly **stepped** down the screen instead of falling. That
was the jank.

The fix is to translate the whole column by the fraction between rows and draw
the glyphs at whole coordinates inside it, which is the only way to get sub-pixel
movement out of a bitmap font. The leading glyph also brightens across its own
cell rather than arriving already lit, so a column reads as continuous instead of
as a row of lights switching on. The original digital rain theme got the same
treatment.

---

## Tab size sliders

Three new sliders under **UI → Theme & Motion**, one per navigation tier: **Main
Tab Scale**, **Sub Tab Scale**, **Group Scale**. Each is a percentage of the size
a button works out for itself from its own label, 40% to 250%, and 100 is exactly
what the screen has always drawn.

Sub-tabs are the tier most worth shrinking. A tab with seventeen sub-tabs wraps
onto several rows, and every row the bars give up is a row the settings list
gains.

**They scale the natural size only.** Anything you resized by hand in edit mode
keeps the exact pixels you gave it — an exact number you chose is not a
suggestion to be multiplied, and if one slider drag silently undid every size you
had set, the sliders would be worse than not having them.

---

## A whole page that had no way in

`client.uiTheme` is a real section of the config — it holds the theme choice, the
per-theme animation speeds, the tab animation style and now the three size
sliders. **No sub-tab listed it.** Every setting in it existed, saved and worked,
and the only way to reach any of them was to edit the config file by hand.

It has a page now: **UI → Theme & Motion**, with a description like every other.

---

## The Overrides tab is a tree

It listed every master switch flat. It now indents them into the hierarchy they
actually have:

- **the whole subject** — *all of Sects*
- **one page of it** — *just Sect Ambient*
- **one section of that page**

So you can switch off one section of one page instead of an entire subject, which
is the point of having the tab at all.

Two things worth knowing about how it is built. The order is not sorted — it is
the order the tree was built in, parent before child, because a child sorted
above its parent is a child indented under the wrong thing. And **every row is a
switch that already gates something.** Where a level has no real control behind
it, nothing is shown there rather than a toggle that does nothing. The tree is
uneven as a result: Vanilla goes three deep, Sects two, some tabs are a single
row. That unevenness is honest — it is exactly how deep the real controls go.

---

## Navigation animates

Tabs, sub-tabs and groups use the same entrance progress, stagger and style as
the settings rows. Before this, half the screen arrived on a tab click and the
other half was simply already there — the group bar had no motion at all.

---

## Right-click, split in two

Out of edit mode a right-click on a tab, sub-tab or group offers **appearance
only**: rename, colour, text colour, shading, and the locks that protect them.
Size, the Tools submenu, every Move row and the two layout resets are gone,
replaced by one line saying where they went.

Colour and name are reversible in one click and visible the instant you make
them. Size and order change the **layout** — the bar rewraps, buttons move, and
the thing you were about to click next is somewhere else. A mis-click while
browsing should not be able to rearrange a screen you spent time arranging. Edit
mode is the deliberate act that says layout is what you came to change, and it is
where undo and reset-position already live.

The rows are removed rather than greyed, because a disabled row invites clicking
it to find out why.

---

## Search finds the switch

Searching **talisman** while the talisman section was switched off did not
surface the switch that turns it back on. Two things were wrong, and both are
fixed: a master switch is now never out of scope even when you are standing on a
different tab, and master switches **lead** every search. The row most likely to
be the actual answer is no longer buried below a hundred settings that are doing
nothing.

---

## Right-click the theme button to go backwards

With forty-four themes, overshooting the one you wanted cost forty-three more
clicks to reach it again — which defeats the point of a quick-cycle button. Left
goes forward, right goes back.

---

## The perk step is closed

Pressing Create World no longer opens the Golden Finger perk screen. A notice
appears and world creation goes straight on to the origin draw.

The screen worked; that is not why it is shut. The base mod's latest update
changed the systems those perks reach into, and **a perk that still looks right
while granting something the game no longer uses is worse than no perk at all** —
you spend your picks on it and find out later. The names, tiers and effects are
being rewritten against the new systems, so anything chosen now would be chosen
from a list that is about to stop existing.

A notice rather than a vanished button, because a button disappearing reads as
something broken. A toast rather than a dialog, because a dialog demanding a
click to tell you nothing is going to happen is only in the way. The screens
themselves are left in the source — they are being rebuilt, not abandoned.

---

## The in-game description

It opens in a cultivator's voice now, and it does not stay polite for long — it
starts with what an unchallenged world actually feels like, works through the
derision that deserves, and only then hands over the brush. Every section of
*What You Can Change* carries a line of that voice above its plain instructions,
so the lore and the manual are read in the same pass rather than in two
documents.

The "New in" section was a placeholder reading **"• Changes"**. It is now a real
account of the release. The *What you can change* section gained a symbol key,
two sections it never had — plain Minecraft, and the screen itself — and an
opening that says what the numbers mean. The header counts are recomputed rather
than carried forward: 1,555 settings, 22 tabs, 107 sub-tabs, 44 themes.

---

## Compatibility

Nothing moves, renames or resets a config path. Your settings carry over as they
are, and **Tab Animation Style** keeps whatever it was set to — the three old
values all still mean exactly what they did.

---

## All Off now means all off

The Overrides tab has **All On** and **All Off**. Press All Off and every tab
should leave the bar except Overrides itself — that is what the button says.
Three tabs stayed: Cultivation, NPCs and UI.

They stayed for an honest reason. A page is hidden when the switch governing it
is off, and four pages had no switch at all — Core Data, Golden Finger, NPC
Trades, and the handful of whole-game values on the Cultivation General page.
Those settings were applied unconditionally, so hiding those pages would have
hidden settings that were still doing something, which is worse than leaving a
page on screen.

So they have switches now, and each one is read at the place its values are
actually used rather than bolted onto the front of a screen:

- **Core Data** — the time-acceleration limit and the attribute points the game
  allocates for you on a breakthrough.
- **Golden Finger** — the perk count.
- **NPC Trades** — use count and price. Every trade in the mod passes through
  the same two places, so the switch sits on those two rather than on fourteen
  separate injection points that could drift apart.
- **Global Overrides** — cultivation speed, the three NPC stat multipliers, the
  meditation interval and both free-zhenyuan rewards. All seven fall back to the
  base mod's own figures. The other twenty-nine switches on that page are
  untouched; each still speaks for its own section.

**Identity Tooltips** is not a new switch. The scrollable-tooltip toggle has
always decided whether that section does anything — it was simply never listed,
so the page could not be hidden with it.

### The pages that override nothing

The UI tab's theme, background, accessibility, preferences and advanced pages do
not override anything in the game. They are this screen's own appearance, and
inventing a master switch for them would be inventing a lie about what the
switch does.

They follow one stated rule instead: **when every master switch is off, the
screen shows only the tab that can switch them back on.** With the mod set to
change nothing, eight pages of decoration plus a second copy of the switch list
is the screen arguing with you. One rule, written once, rather than eight
switches that do not mean what they say.

Nothing here is destructive. All Off writes booleans; All On writes them back.
No setting's value is touched, so a section switched off and on again returns
exactly as it was.

---

## Themes that are easier to read

**Main tab labels.** Every label was ringed in black to make it stand out. On a
dark theme that is exactly right — a gold label over a black ring reads sharply.
On a light theme the label is already dark, and ringing dark text in black does
not outline it. It thickens it, and the word turns into a smudge.

The ring is now chosen from the label's own brightness: a dark label gets a light
ring, a light label a dark one. That follows a theme, and it equally follows a
tab you have recoloured by hand.

**Hover.** Every navigation tier brightened its label under the pointer. On a
light theme "brighter" means closer to the page it is sitting on, so pointing at
a sub-tab made its name harder to read at exactly the moment you were pointing at
it. Hover now moves away from the panel rather than toward brightness — it still
changes, and it always gains contrast.

**Porcelain Blue & White** and **Ink Wash** have been sharpened in the middle of
their palettes only. Their washed-out mid-tones left the three navigation tiers
running into each other. The cobalt and the ink are untouched.

---

## Cherry Blossom

Cherry Blossom was borrowing another theme's background — a murmuration of
starlings, which has nothing of blossom in it — and drawing it faintly enough on
a near-white page that most people would never have seen it at all.

It has an orchard now. Two rows of trees running away to the horizon, canopies
packed the way a tree packs anything into a circle, and petals that leave the
crowns **in gusts** and drift toward you rather than across.

Two gusts in thirty seconds, with real quiet between them. A constant fall rate
is snow; a cherry tree stands still for most of a year and then lets go all at
once, and that is the thing worth watching.

Light themes also get a stronger background than dark ones now, rather than the
same amount. A pale scene on a pale page is drawn and invisible, which is not the
same as subtle.

---

## Two defaults

**Digital Rain is the default theme**, and now it actually is one. The config
file has said `digital_rain` for a while and the screen has agreed for a while —
but the palette itself was only corrected when the config screen was first
opened. Until then anything drawn outside it wore the old classic colours, so a
fresh install looked like the default had not taken. It takes now, from the first
frame, before anything is drawn.

**The vanilla difficulty button is removed by default.**

It is redundant — this mod and the cultivation mod both handle difficulty their
own way, so the vanilla toggle on the Create World screen offers a choice that
does not mean what it looks like it means.

More to the point, it is the one place this mod shows itself. Everything else it
does happens inside another mod's numbers and leaves no mark on any screen: no
splash, no toast, no watermark, no item in your inventory. A missing button on
the world-creation screen is a quiet signal that the install worked, and you see
it before you have loaded a world.

Turn it off on **UI → General Toggles** to put the button back. It also answers
to the **Vanilla** master switch, so All Off returns it along with everything
else — a mod that claims to be changing nothing should not still be hiding a
button.

### If you already had the mod

Changing a default only reaches a config file that does not already have the key
in it. Yours does, sitting at the old value, so the new default would have
reached new installs and nobody else.

It is applied to existing files too, once. The first time you open the Create
World screen after updating, the setting is switched and a single notice says so
and names the page that puts it back — **UI → General Toggles**. After that it is
never touched again, whatever you set it to.

It is announced rather than done quietly on purpose. The other one-time repairs
in this mod are safe to make silently because they only touch a value still
sitting on a number an old version got wrong, and a config still holding that
number is good evidence nobody chose it. A true/false setting gives no such
evidence: the old default was off, so every existing file reads off — the people
who never opened that page and the people who looked at it and decided they
wanted the vanilla button look exactly the same from here. Changing something
somebody may have chosen deserves a sentence, not a line in a log file nobody
reads.
