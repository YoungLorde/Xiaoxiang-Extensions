# Xiaoxiang Config Extension — 2.1.4

The release that grows alongside Realm Expansion 1.1.0. The six sections at
the top always say where things stand: what is new, what changed, what was
fixed, what is in but not finished, what you may run into, and what was caught
before you could.

---

## 🆕 New

### 🧭 A settings screen that keeps track for you

Every setting you have moved off its default now wears a gold stripe, and
the tabs and sub-tabs carry a small gold number counting what you changed
inside them. A ↺ on a changed row puts that one setting back with a click -
rest on it first to see the default it will return to. Reset Tab now resets
exactly the changed rows on screen, and Ctrl+Z undoes any change, a whole
reset included.

Start typing anywhere and the search looks through every page of every mod
at once - the cultivation mod, each Xiao mod and every other mod you have
installed - matching names, descriptions and the setting's own key, in any
word order. Each result names the page it lives on: click that, or press
Down then Enter, and the screen opens that page with the setting lit up.
The keyboard reaches everything: Up and Down (or Tab) move between rows,
Enter edits, Left and Right nudge a value, Page Up and Page Down jump a
screenful, Ctrl+Tab changes sub-tab and Escape steps back one level at a
time. Nothing steals a key while you are typing in a box.

Number boxes take 1.5k, 2m, 3b and beyond, show the number written out and
the allowed range underneath while you type, and keep what you enter inside
that range, saying so when they do. Pages are built the first time you open
them, so the screen opens and switches tabs faster.

### 🀄 The settings screen in Simplified Chinese

Press the language button under the title until it reads 简体中文 and every
tab, page, heading, setting name, button and glossary word is in Chinese,
in the cultivation mod's own words for realms, techniques and grades. Hover
explanations stay in English for now, as Spanish's do. Spanish players lose
the last English lines on their screen too: Default, Range, Done and the
reset messages now read in Spanish.

### ⏱️ Opens and resizes without redoing its work

What you arranged - theme, colours, order, sizes and locks - is read once
when the screen opens and again only when something changed; the red
warning marks are worked out again only when what they depend on changes;
and the small gold counts on the tabs fill in over the first second instead
of all in the opening frames. Nothing looks different; it only arrives
sooner.

### ⚙️ Black Tech's new machines on its page

Black Tech's entry now covers the Sun Gatherer, the Spirit Stone Generator,
the Spirit Grinder and the three Qi Batteries on three new tabs - Power,
Grinding and Batteries - every setting named and explained the way the
Unwinder's and the Condenser's are, with what raising and lowering it does
wherever that is certain.

### 🖼️ Pictures in the Mods row

The Xiao mods carry a small drawn picture beside their names in the Mods
row, so the row reads at a glance.

### 🎨 Every colour opens the colour wheel

Every colour on the screen now opens the wheel: the older health, hunger
and air bars' six gradient colours were plain numbers before, and the
named-colour steps work on Mekanism's HUD colours too.

### 📋 The notes open with six standing sections

What is new, what changed, what was fixed, what is in but not finished, what
you may run into, and what was caught before you could - in the same six places
every release. The Mods page's "New in" list is read from the first of them,
so the two never differ.

### 🏭 Mekanism, every setting readable

Mekanism keeps nine hundred settings in thirteen files, and this screen used
to arrange forty of them - the interface - and let the rest fall where their
keys put them: tabs named after file sections, rows called
"enrichmentChamber", energy figures in text boxes with no unit. Now every one of them is placed by
subject - Machines by family (ore processing, chemicals, lasers, pigments…),
Tiers by tier, Generators and reactors each on their own page, Gear by item,
Tools by material and by piece, World by ore and by vein - and named in words
with its unit: "Crusher - J per tick", "Capacity (J)", "Durability". The
author's own note stays under every row, and a sentence at the head of every
tab and page says what lives there. Mekanism Extras - its four extra tiers of
everything, the naquadah reactor, its ores - and Mekanism: Elements - its six
machines - are arranged the same way, and so are Carry On (carrying, lists,
interface) and Patchouli (the books).

### 🔢 Mekanism's energy figures are numbers

Mekanism stores every energy figure as quoted text - "50", "256000000" -
because its own energy type is wider than the game's numbers. The screen
showed a text box. It now treats them as the numbers they are: a slider
where there is a range, plus and minus steps, large figures shortened the
way every other number on this screen is, and written back exactly in the
form Mekanism reads. Only for mods known to do this; a string that merely
holds digits in some other mod is left a string.

### 🚀 Ad Astra, whole

Its client settings - the oxygen and energy bars, the area overlays, the jet
suit, the muffler, the radio - sat under raw keys in Other, which read as the
mod being half there. They have a page of their own now, Interface, and every
tab on the Ad Astra page carries a sentence saying what it holds.

### ⚙️ Black Tech, every setting readable

Black Tech's settings sat under its file's own section names, Energy and
Transformers, each row titled from its key. Its entry in the Mods row now
opens on two tabs that follow the mod's own split. Currents: what qi, Immortal
Chi, Spiritual Energy and Forge Energy are worth in each other, both ways.
Transformers: a page each for the Unwinder and the Condenser - what each holds,
how fast it moves energy, and which spirit stone the Condenser makes. Every row
is named in words ("FE from one SE", "Stones the tray holds") and says what it
does in play, what raising it and lowering it does wherever that is certain,
and where a number stops a machine outright - an Unwinder buffer smaller than
one SE's worth of FE never unwinds, a Condenser store smaller than a stone's
worth never makes that stone. Black Tech's own note stays under every row.

### ⚡ Sweep a column of switches in one stroke

Press a switch and keep the button down: drag up or down the list and every
switch you pass takes the side the first one flipped to - a whole column
turned on, or off, in a single sweep. Each change is still its own step, so
Ctrl+Z gives them back one at a time.

### 📜 Configure Identity: write the legend, then pack the bag

On the Identity page, the button beside an origin's starting items now reads
**Configure Identity**. Press it and a small choice unfolds: **Edit the Story**
opens the same story editor as the Origin Stories group, so the words on the
choose-origin card become yours; **Pick Items** opens the item picker exactly
as before.

### 🎒 Every origin packs its own bag

Seven of the twenty-three origins used to share one starting kit: Mortal's
Child, Fallen Noble, Blacksmith Apprentice, the three outer disciples and
Formation Apprentice all started with whatever the Default list held, so
changing one of them changed all seven. Now every origin has a list of its
own, and Pick Items in Configure Identity saves to the origin you opened it
from. An empty list still means the origin's own kit from the cultivation
mod, and an origin that a later version of the cultivation mod adds starts
with the Default list until it has a list here.

### 🗂️ An Identity tab, and one switch for one shared kit

Everything about origins has a tab of its own, right after Cultivation, with
five pages: Origins, Stories, Starting Items, Lifespans and Custom
Identities. Every starting kit sits on one page and every lifespan on
another, and settings you already saved stay exactly as they were.
**Same Items For Every Identity**, off unless you turn it on, hands every
origin the Default list: while it is on, each origin's row says it starts
with the Default list and keeps its own list for when you switch back. While
the Default list is empty the switch changes nothing, so a new player never
starts with an empty bag.

### 🌊 Every bar glides

Every bar this mod draws now slides to its new value instead of jumping: the
cultivation and qi bars on the cultivation panel, your health, hunger, air,
armour and experience bars, and the bars over creatures. The cultivation bar
no longer climbs in a staircase once a second while you meditate; it fills as
one smooth line. The movement runs on the game's own clock, so it looks the
same at any frame rate and holds still while the game is paused. A change
settles within about a quarter of a second, and a bar never runs past the
value it is heading for; death, respawning, changing worlds and any change
bigger than half a bar land straight away. Both numbers are on the Player
Bars page as Glide Time and Jump Above; a Glide Time of 0 switches the gliding
off. The old Animation Speed is now Damage Trail Speed.

### 🌱 Beginning without the Fate Plate

Lost your Reincarnation Fate Plate, threw it away, or never noticed it?
Until now a player without an origin could fight and level all day while
the cultivation bar never moved. Now cultivation begins on its own at
MORTAL, layer one, and a message tells you so once. **Begin At Experience
Level**, beside the switch on the Experience page, says when: the
experience that first carries you to that level begins it - level one
unless you choose another, anywhere up to one hundred - and until then
experience is ordinary experience. Once begun it stays begun: levels spent
on enchanting or lost in a death never take it away. From then on your
experience feeds your cultivation by the same rules as everyone else's,
without an equipped technique, since a technique comes with an origin.
Using the plate later still works and begins a new life exactly as before.
It applies while Convert Experience to Cultivation is on, and **Begin
Without the Fate Plate** beside it switches it off.

### 🖱️ Mouse moves in every container

Container screens now come with Mouse Tweaks' best-known moves, on from the
start. Hold shift, press on a slot and slide: every stack you cross moves to
the other inventory. Hold a stack, press the right button and slide: one
item drops into each slot you cross, the first one included. Click the
middle button over a chest or your own inventory and it is sorted. Every
move is the game's own click, so nothing lands where a click could not put
it, and crafting and trading results are never touched. Each move has its
own switch on the Inventory page, and **Needs Shift** brings back the old
slide that moved stacks without shift; in creative the middle button still
copies stacks. With Mouse Tweaks installed it keeps every screen it handles
exactly as before, and the middle-click sort works alongside it.

### 🗺️ Realm Expansion's wanderers, beside the spawner

With Realm Expansion installed, the Wandering Spawner group on the NPCs
tab's Spawns page also shows Realm Expansion's own settings for the wild
cultivators the spawner sends out, under a Realm Expansion heading split
into Climbing, Gathering and Gear, Greed and Trade, Cushions and Springs,
Followers, Home Worlds and Raids - how many wanderers appear and what they
do afterwards, tuned on one page. These rows are Realm Expansion's own
settings, not copies: a change here is the same change on Realm Expansion's
tab and in its file, and each row's hover text is Realm Expansion's own
note. A setting your version of Realm Expansion does not have yet is simply
left out, and without Realm Expansion the page shows no heading at all.

### ⌨️ Every key of the four mods on one page

The settings screen has a new Keys page under UI. It gathers every key of
the cultivation mod, Realm Expansion, Black Tech and this mod, with a heading
for each mod, and shows each key's name and what it is bound to, CTRL, SHIFT
or ALT included. A key that one of those mods adds later turns up on the page
by itself.

Click a binding, then press the new key or mouse button. For a combination,
hold CTRL, SHIFT or ALT and press the key. Escape leaves the key as it was. A
red stripe and a red binding mean another key in the game shares that
binding, the same clash the Controls screen marks in red, and hovering the
row names the other key. Reset puts one key back to its default and Reset All
puts back every key on the page. Your changes are kept with the rest of your
game options, as the Controls screen keeps them. The cultivation mod's own
settings window, opened from the Mods list, now has a Keys button beside it
that opens this page; closing the page takes you back to that window.

## 🔧 Changed and rebalanced

### ⌨️ Crouch Meditate moves to O

Crouch meditation starts on **O** now. It sat on G, which the cultivation
mod already uses to open its cultivation screen, so crouching and pressing
G opened that screen and meditation never began. O belongs to nothing in
the game, the cultivation mod or Realm Expansion. If you played an earlier
version your controls still remember G: Options, Controls, Key Binds, and
**Reset** beside Crouch Meditate.

### ✨ Experience goes wholly to cultivation

With Convert Experience to Cultivation switched on, the experience from mobs -
and from mining, smelting and everything else - now goes wholly to your realm
unless you choose a split: Share to Cultivation starts at 100 and Share Kept as
Levels at 0. They are still two views of one number that always add up to 100.
Settings saved by an earlier version, which started everyone on an even 50/50
split, are moved to the new default once, the first time this version starts,
and the game's log says which values moved. Set 50 again if you liked it; it
is never moved a second time, and a split you chose yourself is left alone.

### 🏮 A presets window worthy of the hall

The Presets window is redrawn in your chosen theme: each preset shows its
name, the line its maker wrote, and what applying it will change - which mod
rewrites which numbers, or how many of your settings it holds and on which
pages. Rows glow under the pointer and stay marked once pressed; Apply and
Delete stand in straight columns; Save current as... and Close sit at the
foot, and nothing overlaps at any window size.

### 🧭 Realm Expansion has an entry of its own in the Mods row

Realm Expansion's pages - Expansion, Wild Cultivators, Water Grades, Xiao
Universe, Followers and Weapon Aura - sat at the end of the cultivation mod's
tab row, six tabs past its own, while every other mod in the pack
had a button of its own in the Mods row above. Changing one of its settings
meant knowing to look under the cultivation mod's name and scrolling along to
the far end of its row.

Now Realm Expansion is an entry in the Mods row, beside Xiaoxiang Cultivation
and ahead of the rest of the pack, and everything of its lives behind that one
button: all six pages with their sub-tabs and groups, exactly as Realm
Expansion arranges them. The cultivation mod's row holds the cultivation mod's
tabs and nothing else. A mod that opens its pages to this screen the way Realm
Expansion does gets an entry of its own the day it is installed - nothing is
written down here mod by mod - and a mod whose settings reach this screen two
ways still gets one entry, never two. Remove the mod and its entry goes with
it as soon as the screen is opened again. Your tab order, colours and names
carry over: the six tabs keep whatever you gave them, in their new place.

## 🐛 Fixed

### The UI tab's Advanced page froze the whole screen

Opening the Advanced page under the cultivation mod's UI tab locked the
screen up, and even the click to leave it took seconds to land. That page
holds the settings that remember your tab layout, sizes, styles and locks -
a few thousand characters each - and every row was shortened to fit by
taking one letter off and measuring the whole line again, over and over,
every frame. Every line on every screen of this mod is now cut to fit in a
single pass, so the page opens at once and a click on another tab goes there
straight away.

### The container search box took no typing on many screens

Clicking the search strip above a container now gives it the keyboard on
every container screen, including the cultivation mod's own and other mods'
containers that answer their own clicks, where the box could not be focused
at all before. Letters, digits, Backspace, Delete, the arrows, Home and End,
select-all, copy, cut and paste all work, and no key reaches the screen
behind: E does not close it, Q does not throw, the number keys do not swap
slots. Escape or Enter hands the keyboard back and keeps your search;
clicking elsewhere does too, and that click still does what it always did.

### Choose Your Origin: the starter list would not scroll

The bar beside an origin's starting treasures could not be dragged, and the
wheel did nothing - neither over the icons nor in a long tooltip. The screen
now answers the hand itself: grab the bar and pull it, or roll the wheel over
the icons or over the bar, and the rows glide by one at a time; hover a
treasure or your physique and the wheel turns the pages of its tooltip again.

### The Default button sat on top of a setting's words

The little button that puts a setting back now sits right beside the switch
or the value control, inside the setting's own row - never on top of its
name, never past the row's edge. On a narrow window it steps down to the
row's lower line rather than over a word.

### A colour picked on the wheel made a bar's border or background invisible

The wheel wrote every colour without its see-through part, so any of this
mod's colours picked on it - a bar's border, its background, its fill - went
fully transparent the moment you chose it. The wheel keeps each setting's own
see-through value now, and writes none where a setting has no room for one.

### Mekanism's HUD colours refused the named colours

Mekanism's four HUD colours take no see-through part, and the named-colour
steps offered colours with one, so every step was refused and the swatch
drew nothing. They step through the named colours as the others do.

### Undo said "Undone" and changed nothing

Ctrl+Z and Ctrl+Y now work: every change - typed, clicked, scrolled or a
whole tab reset - is one step back, fifty deep.

### Up or Tab in the search box handed the keyboard to Done

The next Enter then closed the screen. The search box now keeps every key
but Escape, Down, Enter and Tab, and those move into the results.

### A number over a setting's maximum was taken, then quietly capped

It was stored and shown as typed, then replaced by the cap at the next
launch. A number box now keeps what you enter inside the setting's range
and says so when it does.

### Diff showed only settings with "modified" in their name

Diff now shows every setting you have changed, on every page.

### The cultivation mod's World and Effects tabs were missing with Mekanism

With Mekanism installed, two of the cultivation mod's own tabs - World and
Effects - were gone from its row. Mekanism's page has tabs with the same two
names, and a name on another mod's page was taken as a claim on every tab of
that name, so the cultivation mod's were filed under Mekanism, where nothing
draws them. Refined Storage's Crafting tab did the same to the cultivation
mod's Crafting. Another mod's tab names now belong to its own page and to
nothing else; every one of the cultivation mod's tabs is in its row whatever
else is installed.

### Other mods' pages came up in an order nobody chose

The tabs, pages, groups and rows of another mod's page appeared in a fixed
but meaningless order - Black Tech's machines before the currents they trade
in, a store listed after the rate it empties at. A page now follows the order
its mod writes its settings in, and a page arranged here by subject follows
that arrangement: Mekanism's client settings open on Interface, then
Effects, then General, as they were laid out, rather than wherever they
happened to fall.

### Settings of mods you had removed showed up on Mekanism's page

Removing a mod leaves its config files behind, and an add-on keeps some of
them in its parent's folder. Those were read as the parent's own: with Astral
Mekanism and Compact Mekanism Machines removed, their thirty settings -
machine energy figures, a compact reactor's tanks - sat on Mekanism's page as
if they were Mekanism's, editable and read by nothing. A file left by a mod
that is no longer installed now stays off every page. Nothing is deleted: the
game's log names each file left out, so you can tidy the folder if you want
to.

### The language setting's note named languages that are not there

The note above the language setting in the client config file still listed
twelve languages, and said a code it did not know would follow the game. The
button offers three - English, Portuguese (Brazil) and Spanish - and a code it
does not know reads as English. The note now says so, for anyone who sets the
language by editing the file.

### Your tab order survived only until the next launch beside Realm Expansion

Realm Expansion registers its pages as the game starts, before this screen
has read the arrangement you saved - and that registration was written out
over it. Every launch put the tab order back to the standard one with Realm
Expansion's tabs near the front, however you had arranged it the day before.
The saved arrangement is read first now and added to, so an order you set
stays set.

### Random on the origin screen closed the game before the world existed

Reported: pressing **Random** on the origin screen could close the game. It
happened on the origin screen this mod shows while a world is being created -
the one that lets you choose before you ever load in. That screen's Random
button asks the server to roll an origin, and before the world exists there
is no server to ask; the press ended in a crash. Confirm had long been made
safe there; Random had not.

Random before the world now rolls on the spot - the same draw the game makes
for the same button once you are in a world - stores it with your perks, and
goes on to create the world. Your identity, root and physique are applied the
moment you arrive, exactly as a chosen origin is. In a world, Random still
asks the server, as before.

### Full bar, breakthrough refused - fixed on the Realm Expansion side

Reported with Realm Expansion and this mod together. The cause and the fix
are in Realm Expansion 1.1.0, whose notes tell the story: the breakthrough is
judged by the server's requirement while the bar on your screen was drawn
from your own game's copy, and where the two sets of settings differed the
two totals did. Realm Expansion now sends the server's number with the record
and the bar reads it. Nothing in this mod changes for it; both mods at their
current versions is what fixes it.

## 🚧 Being worked on

Nothing half-made in this version: everything above is whole.

## ⚠️ Known problems

Nothing known. The two reports from the last update - the full bar refused
a breakthrough, and Random closing the game - are under Fixed above. If
either still happens to you, a report with your latest.log (and, for a
crash, the crash report) helps.

## 🛡️ Headed off

### 🤝 Two health bars beside Realm Expansion

Realm Expansion draws a health bar of its own in the hearts' place, and so
does this mod. Both took the heart row at the same moment, so with both
installed the bar you saw was whichever happened to get there first - a
coin toss made when the game started, and a different toss on a different
machine.

Settled, on both sides, before either version went out. This mod's bar is
the one drawn - it has the styles, the colours and the HUD editor - and
Realm Expansion's stands down at load while this mod is installed. Its
Display page has a switch that hands the row back to its own bar, and
that works because this mod's rule since 2.1.3.3 is to stand down the
moment another mod takes the hearts: one bar on the screen either way,
and never none. Needs Realm Expansion 1.1.0; an older one still tosses
the coin.
