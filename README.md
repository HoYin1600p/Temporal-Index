# Temporal Index

Temporal Index is a Minecraft Forge addon for Vault Hunters that stores temporal shards and identified temporal relics in a compact, selectable book.

The Index is designed for players who want temporal items available without filling normal inventory slots. It collects supported drops before backpack pickup upgrades, lets the player cycle through occupied entries, and delegates relic behavior and shard loot generation back to Vault Hunters.

## Features

- One dedicated temporal shard slot and 16 dedicated temporal relic slots.
- Capacity of 65,000 items per slot.
- Automatic collection from anywhere in the player's hotbar or main inventory.
- Hotbar-first book priority when a player carries multiple Indexes.
- Sneak + scroll selection that skips empty slots and wraps at both ends.
- Main-hand activation of the selected shard or relic.
- Vault-owned shard loot rolls, instant identification, and relic activation.
- Automatic routing of identified relics into their matching Index slot.
- Safe overflow through normal inventory pickup, another Index, or a drop at the player's feet.
- A closed-book renderer with the selected item's sprite displayed on its cover in first person, third person, item frames, and dropped-item form.
- Compact `99+` slot labels with exact quantities available on hover.
- A book tooltip listing every stored item by its localized display name and exact quantity.

## Requirements

| Component | Requirement |
| --- | --- |
| Minecraft | 1.18.2 |
| Forge | 40.3.11 or another compatible 40.x build |
| Java | 17 |
| Vault Hunters | A compatible `the_vault` build containing temporal shards and relics |
| Installation side | Client and server |

Temporal Index has no runtime dependency on Wold's Vaults, Fruit Sac, Sophisticated Backpacks, or CMA. Vault Hunters is the only required content mod.

## Installation

1. Install Minecraft 1.18.2, Forge 40.3.11, and a compatible Vault Hunters pack.
2. Download `temporal_index-1.0.0.jar` from the GitHub release.
3. Place the JAR in the pack's `mods` directory on both the client and server.
4. Start the game and confirm that **Temporal Index** appears in Forge's Mods screen.

Back up a world before adding or updating any mod. Do not remove Temporal Index while Index items containing stored relics still exist in a world.

## Using the Index

The 1.0.0 release intentionally has no survival recipe. For testing or pack integration, obtain it from the creative inventory or run:

```text
/give @s temporal_index:temporal_index
```

The Index must be in the player's main hand for all direct interactions:

- **Sneak + right-click:** open the Index inventory.
- **Sneak + scroll up/down:** select the previous or next occupied entry.
- **Right-click:** activate the selected shard or relic.

Relic activation follows Vault Hunters' own restrictions. In normal play, temporal relics can only be consumed where Vault Hunters permits them, including inside an active Vault. Creative mode does not consume items, so activation testing should use survival mode.

See [Usage and behavior](docs/usage.md) for slot layout, pickup order, overflow rules, quantity display, and shard identification behavior.

## Compatibility

Version 1.0.0 was compiled against every distinct active Vault Hunters JAR found in the CMA test instances and was tested in CMA Remastered inside a generated Vault. See [Vault compatibility verification](docs/compatibility.md) for the tested matrix and repeatable verification command.

Vault Hunters remains authoritative for:

- temporal shard loot pools;
- generated relic modifier IDs and durations;
- instant identification;
- Vault-dimension and objective restrictions;
- relic effects, messages, sounds, and consumption.

## Building from source

Clone the repository, use Java 17, and provide a local `the_vault` JAR:

```powershell
.\gradlew.bat build -Pvault_mod_jar="X:\path\to\the_vault.jar"
```

The build also discovers Vault Hunters in a small set of local Prism development instances when no override is supplied. The distributable JAR is written to `build/libs/temporal_index-1.0.0.jar`.

For development guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md).

## Documentation

- [Usage and behavior](docs/usage.md)
- [Core design](docs/design.md)
- [Compatibility verification](docs/compatibility.md)
- [Client/server safety](docs/server-safety.md)
- [Reference behavior findings](docs/reference-findings.md)
- [Version 1.0.0 release notes](docs/releases/1.0.0.md)
- [Changelog](CHANGELOG.md)
- [Placeholder art notes](art/placeholder/README.md)

## License and attribution

Copyright (C) 2026 HoYin1600p.

Temporal Index is free software licensed under the [GNU General Public License v3.0 only](LICENSE), identified by SPDX as `GPL-3.0-only`.

Temporal Index is an independent addon and is not affiliated with or endorsed by Mojang Studios, Microsoft, Iskallia, or the Vault Hunters team. Minecraft and Vault Hunters names and assets belong to their respective owners. No source code or assets from the inspected reference mods are included in this repository.
