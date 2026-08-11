# Changelog

All notable changes to Temporal Index will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project uses semantic versioning for its own releases.

## [Unreleased]

## [1.0.1] - 2026-08-10

### Wolds Comparability Pass

- Made lightweight temporal-relic state handling compose safely with Wold's Vaults instead of competing with its `TemporalShardItem` method overwrite.
- Preserved Wold's Vaults as an optional mod: Temporal Index still requires only Vault Hunters and does not import or call Wold's code.
- Verified Wold's Vaults 0.33.1 shard generation end to end and verified conflict-free startup with Wold's Vaults 0.32.3.

## [1.0.0] - 2026-08-10

### Added

- A portable 17-slot Temporal Index for temporal shards and 16 temporal relic types.
- Fixed 65,000-item capacity for every reserved slot.
- Main-hand sneak-scroll selection with empty-slot skipping and wraparound.
- Main-hand container access through sneak + right-click.
- Vault-owned temporal relic activation and temporal shard loot generation.
- Instant identification and automatic routing of shard-generated relics.
- Hotbar-first automatic pickup before backpack pickup upgrades.
- Compact internal relic data without persistent Vault Gear caches.
- Closed-book rendering with the selected temporal sprite on the cover.
- Cover rendering in GUI, first person, third person, item frames, and dropped-item form.
- Compact `99+` slot counts, exact slot quantities, and a complete localized contents tooltip.
- Compatibility verification against four distinct Vault Hunters builds represented across five CMA instances.
- Dedicated-server side segregation and lifecycle verification without Wold's Vaults.
- Explicit client-to-server direction enforcement for the selection packet.

### Known limitations

- Version 1.0.0 does not include a survival crafting recipe.
- Book textures and models are functional placeholder assets intended to be replaced by commissioned artwork.
- Compatibility depends on Vault Hunters' temporal item interfaces remaining compatible with the tested builds.

[Unreleased]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/HoYin1600p/Temporal-Index/releases/tag/v1.0.0
