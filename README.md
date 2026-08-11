# Temporal Index

Temporal Index is a Minecraft Forge addon for Vault Hunters that stores temporal shards and identified temporal relics in a selectable spellbook.

## Development target

- Minecraft 1.18.2
- Forge 40.3.11
- Java 17
- A pack-provided Vault Hunters `the_vault` build exposing the temporal shard APIs
- Runtime dependencies: Forge, Minecraft, and `the_vault` only
- Primary runtime test target: CMA Remastered
- Mod ID: `temporal_index`

## Current implementation

- One fixed 65,000-item slot for unidentified temporal shards.
- One fixed 65,000-item slot for each of Asgard's 16 temporal relic modifiers.
- Sneak + scroll selection with empty-slot skipping and wraparound.
- Sneak + right-click container access from the main hand.
- Right-click delegation to Vault's temporal relic activation behavior.
- Vault-owned instant identification when a stored shard is used.
- Compact internal counts without persistent Vault Gear data.
- Hotbar-first automatic collection before Sophisticated Backpack pickup upgrades.
- Oversized slot synchronization through Vault's container support.
- Original closed/open placeholder book textures and selected-relic rendering.

The first pass is intentionally recipe-free. Use the creative inventory or `/give temporal_index:temporal_index` during development.

## Building

The build automatically looks for an active `the_vault-*.jar` in a small set of local development instances, preferring Asgard-SMP. Override discovery for any pack or Vault build:

```powershell
.\gradlew.bat build -Pvault_mod_jar="X:\path\to\the_vault.jar"
```

The built mod JAR is written to `build/libs/`.

## Controls

- `Sneak + scroll up/down`: select the next or previous occupied page.
- `Sneak + right-click`: open the Temporal Index inventory.
- `Right-click`: use the selected shard or relic.

## Design and reference notes

- [Core design](docs/design.md)
- [Compatibility verification](docs/compatibility.md)
- [Reference findings](docs/reference-findings.md)
- [Placeholder art](art/placeholder/README.md)

## License

All rights reserved.
