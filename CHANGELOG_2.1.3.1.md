# Xiaoxiang Config Extension 2.1.3.1

Switch a section off and its tab disappears. That has been true for a while, and
it is the right behaviour — a page of settings that save and do nothing is worse
than no page. What was not true, and should have been, is that you could always
switch it back on.

For five sections you could not. This release fixes that properly, and gives
every master switch in the mod a home of its own.

---

## The Overrides tab

There is a new tab, and it sits at the top of the list: **Overrides**.

It lists every master switch in the mod on one page, grouped by the tab each one
belongs to — one group per tab, in the same order the tabs are drawn. Nothing is
bundled together, so the switch for Weapons is under Weapons and nowhere else.

It is the one tab that can never be hidden. Whatever you switch off, it is still
there, and the switch that brings your section back is on it.

**Why it had to exist.** Most master switches live in a part of the config that
nothing governs, so they were always reachable. Five did not — talisman,
cultivation fruit, equipment, spell balance and bosses were each declared inside
the very section they control. Switch one off and the page it lived on vanished,
taking the switch with it. From inside the config screen there was no way back.
If you have a world where one of those systems stopped responding and you could
not find out why, that is the likely reason, and opening the Overrides tab will
show you the switch sitting at off.

Nothing is lost when a section is off. Every setting keeps its value; the mod
simply stops consulting it. Switch it back on and exactly what you had is in
force again.

### All On and All Off

Two controls sit at the top of the Overrides page, with a count beside them
showing how many sections are currently on.

They are the fast answer to "is this mod causing it?" — **All Off** hands every
system back to the base mod at once, and if whatever you were chasing is still
happening, it was never this mod. Bring them back a few at a time to find the one
that matters.

Neither control touches a single setting's value. All Off followed by All On
leaves you precisely where you started.

---

## The "which mod" chooser is gone

2.1.3 put a screen in front of the whole config that asked which mod you came
here to change, with the cultivation settings behind one button and plain
Minecraft behind another.

It has been removed. The Config button opens the settings directly again.

The question was already answered by the screen behind it — the Minecraft
settings have their own section in the tab bar and always did, so the chooser
added a click and took away the only way back to the main menu by mouse. The
sections themselves are untouched and work exactly as they did.

Building a real version of that chooser means two genuinely separate screens
rather than one screen with a door in front of it, and that is worth doing
properly rather than in a hurry.

---

## Fixes

**The NPC AI Behavior page was doing nothing while looking like it worked.** With
NPC combat overrides switched off, every setting on that page still opened, still
edited and still saved — and changed nothing, because the handlers behind it were
already checking that switch. The page now hides with the rest of the section.

**The Vanilla tab has a master switch.** It was the one page in the config that
could not be handed back to plain Minecraft in one move. Turning it off returns
attribute ceilings, the survival and experience changes, and village and villager
suppression to the game's own behaviour at once.

It does not make the tab disappear, and that is deliberate: it is the only tab in
the Minecraft section, so hiding it would leave that section with an empty tab
bar and put its own switch in a different section entirely. A tab that is the
last one in its section now stays put whatever its switch says — the switch still
works, only the vanishing act is skipped.

**Every main tab now has a master switch.** The Overrides tab lists one group per
tab, and there is no longer a tab missing from it. The last two to get one were
Vanilla and UI.

The UI one is named **Enable HUD Overrides** rather than "enable UI overrides",
and the name is the point. Most of the UI tab is this mod's own config screen -
themes, tab order, tooltips - and none of that overrides anything, so a switch
promising to hand it back would be promising something it cannot deliver. What
the UI tab really does override is the HUD drawn over Minecraft's own, and that
is exactly what this switch turns off: the player bars, the cultivation readout,
the status bar, and their colours and positions. The rest of the tab is
untouched.

**Dedicated servers no longer crash on startup.** One listener was being registered
from code that runs on both sides, which forced a client-only class to load on a
server. Anyone running this on a server could not get past the first few seconds.

**A default was wrong.** Sect cell spawn chance shipped at 0.51; the base mod's own
value is 0.34. New configs are correct, and existing config files are corrected on
load — a setting you deliberately changed yourself is left alone.

**The perk screen is marked.** The Golden Finger screen shown during world creation
now says **(WIP)** in its title. It works, but it is not finished, and it should
not have looked finished.

---

## Crash reporting is gone

Earlier versions could offer to send a crash report. That has been removed
completely, and it is not coming back in that form.

Making it work meant shipping a destination address inside the mod. An address
inside a downloadable file is an address anyone can read and send to, which makes
the channel it points at writable by anyone who downloads the mod. Beyond that, a
mod that carries a hardcoded remote address is a mod you have to take on trust,
and asking for trust is a poor trade for a convenience.

**This mod now makes no network connection of any kind.** No telemetry, no update
check, no crash reporting. Minecraft already writes crash reports to your
`crash-reports` folder; anyone who wants to send one can, deliberately, with the
file in front of them.

In its place there is a community link shown as plain text in the config screen,
for anyone who wants to give feedback. It is text — nothing opens, nothing
connects.

---

## The source ships with the release

The complete source is attached to this release as a separate `-sources.jar`.

If you have looked inside a Forge mod before and wondered why calls into Minecraft
appear as names like `m_280666_`, that is every Forge mod: they are written
against readable names and translated back to the game's internal ones so they can
run. It cannot be turned off. This mod looks unusually dense that way because it
is almost entirely a configuration screen, which is almost entirely Minecraft UI
calls.

The mod's own code is not obscured in any way — full debug information, real
class, method, field and variable names. The attached source is the easiest way to
confirm that, and a short note inside the jar explains the same thing to anyone
who opens it.

---

## Descriptions

Every main tab and every sub-tab in the config now has a written description, with
no gaps. Several pages had none at all — the Vanilla tab's Attributes, Survival,
Experience and Villagers pages among them, which are the pages most likely to
surprise you, since one of them can remove villages from your world.

The identity pages have been rewritten rather than patched. They now explain what
lifespan actually is in this world — a budget your character spends becoming
something — what each of the four background categories is for, and what the
re-roll count quietly decides about the kind of game you are running.

---

## Also in this release

- **Two kinds of lock.** Right-click a tab and you can lock its values and its
  appearance separately — a blue marker on the left for values, a gold one on the
  right for appearance. Locking a tab's appearance now genuinely excludes it from
  a bulk colour change instead of only appearing to.
- **Floating size dials.** Nudge controls open as small movable windows, several
  at once, each with its own title bar and close button.
- **Corner resizing.** Panels can be resized from any of their four corners.

---

## Compatibility

Nothing in this release moves, renames or resets a config path. Your settings
carry over as they are. The Overrides tab is added to the front of your tab order
without disturbing any arrangement you had; move it wherever you like and it
stays there.
