# Xiaoxiang Config Extension 2.0.9

A spawning fix, a config-screen fix, and per-theme animation speed.

## Wandering cultivators are back

If you had played for hours without meeting a single wandering cultivator,
this is why, and it is fixed.

- **The population limit was counting the wrong cultivators.** The limit that
  keeps the roads from filling up was counting *every* wandering cultivator in
  the dimension — including the ones who live inside sect settlements. A world
  with a couple of populated sects nearby was already at the limit before you
  took a step, so no new cultivator was ever placed. The limit now counts only
  the travellers this mod puts on the roads. Sect residents, spawn eggs, command
  spawns and cultivators placed during world generation no longer use up the
  budget.
- **The spawn rate had been cut too far.** The previous version's answer to
  runaway spawning overshot by about three times. It is back to roughly a fifth
  of the old runaway rate, which is what was intended.
- **Two spawn chances were set above the original mod's own numbers.** They were
  raised by 50% to make the world busier. That is not what a default is for, and
  they are now the original mod's values exactly. If you want a busier world,
  raise them yourself under **Spawns**.

Your existing config file is corrected automatically on first launch, and every
change it makes is written to your log. A value you had deliberately tuned
yourself is left alone.

## Config screen

- **Description windows no longer leak.** With a description window open, moving
  the mouse across it was still highlighting the tabs and rows behind it and
  playing their hover sound, as though the window were not there. While the
  cursor is inside one of these windows, nothing underneath it reacts.
- **Right-clicking inside a description window** no longer toggles the
  favourite or lock state of whatever row happened to be behind it.

## Animation speed

- **Each theme can keep its own speed.** A galaxy turning wants a different pace
  from falling petals. Set a speed while a theme is active and that theme
  remembers it; switch themes and its own pace comes back with it. Under
  **UI → Appearance**, and it can be switched off if you would rather every
  theme ran at one speed.
- **Changing the speed no longer jumps.** Previously, nudging the speed slider
  partway through an animation threw it forwards or backwards, because the whole
  elapsed time was being rescaled at once. A speed change now only affects what
  happens next.

## Companion mods

Companion mods are picked up more reliably. Previously a mod's settings were
only found if they were declared on its main class, which is not where most mods
keep them; the usual places are now checked too.
