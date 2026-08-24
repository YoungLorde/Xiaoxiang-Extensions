# Xiaoxiang Config Extension

Do you find your cultivation journey too smooth? Too easy? Every realm breezed through, every tribulation barely a scratch? Or maybe it's the opposite — you're dying to a wandering rogue cultivator by session two and just want to survive long enough to see a golden core.

Xiaoxiang Config Extension hands you the dial. Want a brutal, unforgiving path where every breakthrough is earned and every tribulation might be your last? Or a peaceful cultivation life where the grind steps aside and the story takes the wheel? This is the tool that makes either one possible — and everything in between.

## What This Mod Does

Xiaoxiang Config Extension is a companion mod for **Xiaoxiang Cultivation World**. It hooks into the original mod at runtime through Mixins — no files from the base mod are ever modified — and exposes over 300 previously-hardcoded values as real, editable config options, all wrapped in a purpose-built in-game config screen. Realm power curves, spell damage, alchemy timings, sect economies, NPC behavior, tribulation difficulty, the works: if it drives the cultivation experience, you can now tune it instead of living with the defaults.

## Key Features

### A Real Config Screen, Not Just a Wall of Numbers
- Custom in-game UI with top-level tabs, sub-tabs, and grouped sections
- Live search with per-tab memory and search history, plus filters for Modified / Boolean / Numeric / Color / String / Over-Max / Favorites
- Favorites and locking, undo/redo (Ctrl+Z / Ctrl+Y) with 50-step history, and a value-change flash
- Config export/import to JSON via clipboard, a config checksum for sharing verified setups, and a full diff view against defaults
- Modpack presets, custom tabs, tab reordering, per-world overrides, config dependencies, inline help, and hover tooltips that explain what a value actually does
- Toast notifications and a minimap-style tree view

### Realm Progression, Spells & Combat, Crafting & Alchemy, World & Growth, Cultivation Paths, Sects & NPCs
Full coverage across every system in the base mod — see the in-game config screen for the complete list, or the [CurseForge page](#links) for the detailed breakdown.

### Identity & Custom Identities
Create your own custom cultivator identity — name, description, and a hand-picked starting item loadout pulled from a full item browser covering both vanilla Minecraft and Xiaoxiang Cultivation items. Identity duplication and adding custom identities into the in-game origin-draw roster are built but deliberately switched off for this release — they're coming in a future update once they're properly polished.

### Accessibility & UI
Adjustable text scale and weight (Chinese and English tracked separately), full color customization, a bar-style HUD option, high-contrast mode, and a reduce-motion toggle.

## Requirements

- **Minecraft** 1.20.1
- **Forge** 47.x (built and tested against 47.4.22)
- **Xiaoxiang Cultivation World** — required, must be installed and loaded alongside this mod
- **Configured** (optional) — provides the in-game button that opens this mod's config screen from the Mods menu; without it you can still edit the TOML files directly

## Installation

1. Install **Xiaoxiang Cultivation World** first.
2. Drop this mod's jar into your `mods` folder alongside it.
3. Launch the game once to generate the config files.
4. Open the config screen from Mods menu → Xiaoxiang Config Extension → Config (needs the Configured mod), or edit `xiaoxiang_config_ext-common.toml` / `xiaoxiang_config_ext-client.toml` directly in your config folder.

## How It Works

This mod never modifies a single file belonging to Xiaoxiang Cultivation World. Every override is applied through Mixins injecting into the original mod's compiled classes at runtime, and each category of override can be independently disabled via its own `enable...` flag in the config if you'd rather fall back to the original mod's defaults for that piece.

## License

Licensed under the **GNU General Public License v3.0 (GPL-3.0)**. Free to use, modify, and redistribute, including in modpacks. Derivative works must also be licensed under GPL-3.0. Source code and the original copyright notice must be preserved. Full text in the [LICENSE](LICENSE) file.

## Credits

- **Creator:** Young Lorde
- **Built on:** Xiaoxiang Cultivation World, created by the Xiaoxiang Cultivation team — this mod doesn't exist without their original work.
- **Powered by:** Minecraft Forge and Mixin

## Links

- **CurseForge:** [Thy_YoungLorde's projects](https://legacy.curseforge.com/members/thy_younglorde/projects) — the mod's own CurseForge page will show up here once it clears moderation review (currently pending).
- **Issues:** Use this repo's [Issues](../../issues) tab to report bugs or request features.
