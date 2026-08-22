# Changelog

All notable changes to Temporal Index will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project uses semantic versioning for its own releases.

## [Unreleased]

## [1.0.3] - 2026-08-21

### Changed

- Canonicalized every identified Temporal Relic accepted by the Index to the minimal `identified`, `modifier`, and `duration` NBT required for Vault behavior.
- Applied canonicalization before manual and shift-click container merging so equivalent relics from different generation paths can share their reserved slot.
- Applied the same canonical representation to shard-generated relics, automatic pickup, partial insertion remainders, and relics extracted from the Index.
- Preserved positive Vault-provided durations and continued to use the 6,000-tick fallback when a relic has no valid duration.
- Kept the implementation independent of Wold's Vaults; Vault Hunters remains the only required content mod.

## [1.0.2] - 2026-08-11

### Render calibration tooling

- Added an independent JSON transform table covering all 17 temporal entries and four render contexts per entry.
- Added independent global book-model translation and rotation for first person, third person, dropped item, and item frame views.
- Added temporary, unbound calibration tooling with a mostly transparent, non-pausing layout; book/icon target selection; item/context dropdowns; sliders; numeric translation/rotation fields; and a live face-on book preview.
- Removed hard-coded per-sprite transform values; cover sprites now resolve translation X/Y/Z and rotation X/Y/Z from JSON.
- Promoted the finalized Lunar transforms to the packaged defaults for all 16 relic icons, retained separate finalized Temporal Shard transforms, and added the finalized book transforms for all four views.

### Added

- Temporal Shards activated from the Index now play The Vault's completed-identification sound without replaying the rolling sequence.
- Added a shaped 3×3 survival recipe using four Pogs, four Vault Essences, and one Temporal Shard.

### Changed

- Left the temporary render calibration menu in the mod but removed its default F8 assignment; **Open Render Calibration** can still be assigned from Minecraft's Controls menu.

### Fixed

- Removed a mandatory mixin injection into Vault's inherited `TemporalShardItem.getState` method that prevented Remastered build 6909 from starting; compact relic names are now resolved by Temporal Index without replacing Vault or Wold's state behavior.
- Centered the closed book on the third-person held-item transform so it remains in the player's hand.
- Mirrored the item-frame cover-sprite rotation so it matches the established first-person orientation.
- Restricted the selected relic or shard sprite to the front cover so it no longer renders through the back of the book.

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

[Unreleased]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.3...HEAD
[1.0.3]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/HoYin1600p/Temporal-Index/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/HoYin1600p/Temporal-Index/releases/tag/v1.0.0
