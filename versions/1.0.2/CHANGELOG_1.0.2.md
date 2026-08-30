# Xiaoxiang Config Extension 1.0.2

## Fixed

- **Duplicate wording in entry names** (player-reported) - a short entry name like "Enable Alchemy Overrides" could render as "Enable Alchemy Overrides Enable Alchemy Overrides." Root cause was in the text-wrapping code: a line that fit on its first pass wasn't marking itself fully consumed, so a later overflow check re-appended the same text a second time. Fixed at the source.
- **LayerPreset (and other expansion-mod enum settings) now actually respond to clicks** - the +/- buttons and dropdown for picklist-style settings (like Realm Expansion's per-realm layer count) were previously inert. Config Extension now resolves an expansion mod's real typed config values instead of only a raw string fallback that has no concept of a Java enum.
- **Reset Tab now works on custom tabs** (e.g. Realm Expansion's "Expansion" tab) - it previously reset nothing there, and for expansion mods specifically it was quietly treating "current value" as "default value," so nothing visibly changed even when it ran.
- **Modified-value indicator now shows on every entry type**, including custom-tab entries and plain boolean toggles, which never showed it anywhere before.

## Added

- **Auto-detection for future Xiaoxiang-branded mods** - any mod whose ID or display name contains "xiao" is now automatically discovered and wired into the config screen via its own `ForgeConfigSpec`, with no per-mod integration code required.
- **True 3-tier navigation for custom tabs** - a custom tab (like Realm Expansion's "Expansion") can now have real, labeled sub-tabs (e.g. "Realm Layering") with their own grouped sections underneath, matching the same Tab -> Sub-Tab -> Group structure every standard tab already uses. Built for future expansion sub-tabs to slot into cleanly.
- **Distinct accent color for custom tabs** - a custom tab's top-level button, sub-tab bar, and group bar now use a dedicated amethyst color scheme instead of the base mod's gold/jade/cinnabar, so it reads as its own thing at a glance. Same animations throughout, different color only.
- **Diagnostic logging for expansion-mod config scanning** - if Config Extension ever fails to read an expansion mod's config values, that now shows up as a warning in the log instead of failing silently.

## Known limitation

- Search-highlighting (the glow effect showing match counts while searching) doesn't yet light up a custom tab's top-level button or sub-tab bar during an active search. Purely cosmetic - it doesn't affect finding or editing any setting.
