# Temporal Index

**Keep every Temporal Shard and Temporal Relic in one compact, selectable book.**

Temporal Index is a Minecraft 1.18.2 Forge addon for Vault Hunters. It automatically collects supported temporal items, stores each type in a dedicated high-capacity slot, and lets players select and use them without moving stacks through their normal inventory.

## Features

- One dedicated Temporal Shard slot and 16 dedicated Temporal Relic slots.
- Capacity of 65,000 items in every slot.
- Priority pickup before backpack pickup upgrades.
- Hotbar-first priority when a player carries more than one Index.
- Sneak-scroll selection that skips empty entries and wraps around the list.
- Direct Temporal Shard and Relic use while the Index is held in the main hand.
- Vault-owned shard loot rolls and relic effects.
- Instant identification and automatic routing of shard-generated relics.
- Canonical relic data so matching relics from different generation paths share one slot.
- Safe overflow to normal inventory, another Index, or a drop at the player's feet.
- Exact quantities in tooltips and compact `99+` labels in the container.
- Selected-item artwork on the book cover in first person, third person, item frames, and dropped-item form.

## Crafting

Craft the Temporal Index with four Pogs, four Vault Essences, and one Temporal Shard:

```text
P E P
E S E
P E P
```

`P` is a Pog, `E` is Vault Essence, and `S` is a Temporal Shard.

## Using the Index

The Index must be in the player's main hand for direct interaction.

- **Sneak + right-click:** open the Index inventory.
- **Sneak + scroll:** select the previous or next occupied entry.
- **Right-click:** activate the selected shard or relic.

Temporal Relic activation follows Vault Hunters' restrictions. Creative mode does not consume relics, so activation testing should use survival mode.

## Compatibility

- **Minecraft:** 1.18.2
- **Mod loader:** Forge 40.3.11 or another compatible Forge 40.x build
- **Java:** 17
- **Environment:** Client and server
- **Required mod:** Vault Hunters

Install the same Temporal Index version on both the client and server. Vault Hunters is the only required content mod. Wold's Vaults is optional; Temporal Index does not import or require Wold's code.

## Installation

1. Stop the client and server.
2. Remove older Temporal Index JARs.
3. Place `temporal_index-1.0.3.jar` in the `mods` directory on both sides.
4. Start the game and confirm that **Temporal Index** appears in Forge's Mods screen.

Back up worlds before installing or updating. Do not remove Temporal Index while stored Index items still exist in a world.

## Render calibration

Version 1.0.3 retains the temporary render calibration menu for aligning future artwork. It has no default key assignment. Advanced users can assign **Open Render Calibration** in Minecraft's Controls menu.

External values saved at `config/temporal_index/item_render_transforms.json` override the packaged defaults.

## Source and license

- [Source code](https://github.com/HoYin1600p/Temporal-Index)
- [Issue tracker](https://github.com/HoYin1600p/Temporal-Index/issues)
- License: GNU General Public License v3.0 only (`GPL-3.0-only`)

Temporal Index was independently implemented and contains no source code or assets from the inspected reference mods. It is not affiliated with or endorsed by Mojang Studios, Microsoft, Iskallia, or the Vault Hunters team. Minecraft and Vault Hunters names and assets belong to their respective owners.

The project icon is original AI-assisted branding and is not an in-game screenshot or a modified official Vault Hunters logo.
