<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━ 🪷 ━━━━━━━━━━━━━━━━━━━━━━━━━

# ✨ Xiaoxiang Config Extension ✨

### *Your Dao. Your Rules.* ⚔️🌙

━━━━━━━━━━━━━━━━━━━━━━━━━ 🪷 ━━━━━━━━━━━━━━━━━━━━━━━━━

</div>

Do you find your cultivation journey too smooth? Too easy? Every realm breezed through, every tribulation barely a scratch? Or maybe it's the opposite — you're dying to a wandering rogue cultivator by session two and just want to survive long enough to see a golden core. 💫

> ⭐ **Xiaoxiang Config Extension hands you the dial.** ⭐
> Want a brutal, unforgiving path where every breakthrough is earned and every tribulation might be your last? Or a peaceful cultivation life where the grind steps aside and the story takes the wheel? This is the tool that makes either one possible — and everything in between.

<br>

<div align="center">

## 📜 What This Mod Does

</div>

Xiaoxiang Config Extension is a companion mod for **Xiaoxiang Cultivation World**. It hooks into the original mod at runtime through Mixins — no files from the base mod are ever modified — and exposes **over 300** previously-hardcoded values as real, editable config options, all wrapped in a purpose-built in-game config screen.

<div align="center">

💎 Realm power curves · Spell damage · Alchemy timings · Sect economies · NPC behavior · Tribulation difficulty 💎

</div>

If it drives the cultivation experience, you can now tune it instead of living with the defaults.

🐉 *Actively maintained and now on its fourth release.* It's stable and feature-complete for daily use; a couple of forward-looking features (noted below) are intentionally held back for a future update rather than shipped half-finished.

<br>

<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━ ⚡ 𝑲𝒆𝒚 𝑭𝒆𝒂𝒕𝒖𝒓𝒆𝒔 ⚡ ━━━━━━━━━━━━━━━━━━━━━━━━━

## 🎛️ A Real Config Screen, Not Just a Wall of Numbers

</div>

- ✦ Custom in-game UI with top-level tabs, sub-tabs, and grouped sections — organized the way you'd actually look for something, not just a flat TOML dump
- ✦ Live search with per-tab memory and search history, plus filters for Modified / Boolean / Numeric / Color / String / Over-Max / Favorites
- ✦ Favorites and locking, so your most-tuned values are one click away and your finished settings can't be bumped by accident
- ✦ Undo/redo (Ctrl+Z / Ctrl+Y) with 50-step history, and a value-change flash so you can see exactly what just moved
- ✦ Config export/import to JSON via clipboard, a config checksum for sharing verified setups, and a full diff view of everything you've changed from default
- ✦ Modpack presets — save and load whole configurations, useful if you run more than one instance or want to share a balance pass
- ✦ Custom tabs that pull configs from multiple categories into one view, plus tab reordering, so the screen can match how *you* think about your settings
- ✦ Per-world config overrides, applied automatically when that world loads
- ✦ Config dependencies (show/hide/enable one setting based on another), inline help icons, and hover tooltips that explain what a value actually does in practical terms — an HP multiplier tells you it scales the real ~20 HP base, not some placeholder number
- ✦ Toast notifications for saves and value changes, and a minimap-style tree view so you always know where you are in the hierarchy
- ✦ **30 built-in visual themes** — from calm, high-contrast accessibility palettes to loud multi-hue showpieces — plus a full HSV color wheel for a custom accent that's entirely your own

<div align="center">

### 🐉 Realm Progression *(Mortal through True Immortal)*

</div>

Per-realm max Qi, base lifespan, Qi shield reduction, and tribulation strike damage, laid out in genuine breakthrough order — plus global multipliers to scale every realm at once without hand-editing all twelve.

<div align="center">

### ⚔️ Spells & Combat

</div>

Global damage/Qi-cost/charge-time multipliers, per-spell constants for named spells (sword flight upkeep, void step, palm thunder, Buddha Fire Lotus, glacier burial, and more), weapon damage and tier-based Qi cost reduction, and full NPC combat tuning — dodge chance, scan ticks, reaction time, and dodge cooldown, broken out per realm.

<div align="center">

### ⚗️ Crafting & Alchemy

</div>

Furnace Qi capacity and XP gain per tier for both alchemy and refining, individual pill values (Qi recovery, blood burn, clear mind, divine stride, and more), storage bag dimensions, and material Qi yields for foundation- and golden-core-tier ingredients.

<div align="center">

### 🌱 World & Growth

</div>

Biome Qi density, spirit vein tiers, spirit plant growth rates and yields, and formation cores — Qi gathering, growth, barrier strength, rejuvenation, and harvest intervals across all tiers.

<div align="center">

### 🧘 Cultivation Paths

</div>

Spirit root and physique rarity weights and bonuses, and full Foundation Dao / Golden Core Dao tuning — lifespan bonuses, spell multipliers, body defense, cultivation efficiency, and tribulation waves per path.

<div align="center">

### 🏯 Sects, NPCs & the Living World

</div>

Sect generation, shop pricing, departments, schedules, defense, ambient social behavior, and population; NPC AI behavior and trade tables; loot tables and drop weights; and trial tuning for the Heart Demon and Inner World trials.

<div align="center">

### 🎭 Identity & Custom Identities

</div>

Create your own custom cultivator identity — name, description, and a hand-picked starting item loadout pulled from a full item browser covering both vanilla Minecraft and Xiaoxiang Cultivation items. Identity duplication and adding custom identities into the in-game origin-draw roster are built but deliberately switched off for this release — they're coming in a future update once they're properly polished, rather than shipped rough.

<div align="center">

### 🎨 Accessibility & UI

</div>

Adjustable text scale and weight (Chinese and English tracked separately), full color customization across 30 themes, a bar-style HUD option, high-contrast mode, and a reduce-motion toggle for players sensitive to animation. Every theme is contrast-checked so descriptions stay legible whether or not you're hovering over them.

<br>

<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━ 📦 ━━━━━━━━━━━━━━━━━━━━━━━━━

## 📦 Requirements

</div>

- 🧱 **Minecraft** 1.20.1
- ⚙️ **Forge** 47.x (built and tested against 47.4.22)
- 🌍 **Xiaoxiang Cultivation World** — required, must be installed and loaded alongside this mod
- 🔧 **Configured** *(optional)* — provides the in-game button that opens this mod's config screen from the Mods menu; without it you can still edit the TOML files directly

<div align="center">

## 🛠️ Installation

</div>

1. Install **Xiaoxiang Cultivation World** first.
2. Drop this mod's jar into your `mods` folder alongside it.
3. Launch the game once to generate the config files.
4. Open the config screen from Mods menu → Xiaoxiang Config Extension → Config (needs the Configured mod), or edit `xiaoxiang_config_ext-common.toml` / `xiaoxiang_config_ext-client.toml` directly in your config folder.

<div align="center">

## ⚙️ How It Works

</div>

This mod never modifies a single file belonging to Xiaoxiang Cultivation World. Every override is applied through Mixins injecting into the original mod's compiled classes at runtime, and each category of override can be independently disabled via its own `enable...` flag in the config if you'd rather fall back to the original mod's defaults for that piece.

<br>

<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━ 📿 ━━━━━━━━━━━━━━━━━━━━━━━━━

## 📄 License

</div>

Licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

- ✓ Free to use, modify, and redistribute, including in modpacks
- ✓ Derivative works must also be licensed under GPL-3.0
- ✓ Source code and the original copyright notice must be preserved
- ✓ Full text in the LICENSE file included with the source

<div align="center">

## 🙏 Credits

</div>

- **Creator:** Young Lorde
- **Built on:** Xiaoxiang Cultivation World, created by the Xiaoxiang Cultivation team — this mod doesn't exist without their original work, and everyone who enjoys it owes them the credit. Go check out their mod if you haven't.
- **Powered by:** Minecraft Forge and Mixin

<div align="center">

## 🐛 Issues & Support

</div>

Found a bug or have a suggestion? Report it on the GitHub Issues page linked from this project's page.

<div align="center">

━━━━━━━━━━━━━━━━━━━━━━━━━ 🪷 ━━━━━━━━━━━━━━━━━━━━━━━━━

*✦ May your Dao be steady, your Qi be plentiful, and your tribulations survivable. ⛵🌌⚡*

━━━━━━━━━━━━━━━━━━━━━━━━━ 🪷 ━━━━━━━━━━━━━━━━━━━━━━━━━

</div>
