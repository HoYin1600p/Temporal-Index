# Reference behavior findings

These notes record behavior only. Temporal Index does not contain copied source from the inspected mods.

## Fruit Sac 1.0.1

No public Fruit Sac source repository was found among ShiftTheDev's public GitHub repositories. The local Asgard JAR was inspected instead.

Observed behavior:

- Stores counts on the container item's NBT.
- Uses a selected registry ID and cycles only through occupied entries.
- Opens its menu on sneak + right-click.
- Delegates normal right-click use to its selected Vault fruit.
- Sends a cycle packet and overlays the selected item through item rendering.
- Intercepts `Inventory.add(ItemStack)` for automatic collection.

Temporal Index uses independently written storage, packets, validation, routing, and rendering.

## Vault Hunters 3.21.62

- Shards and relics are both `the_vault:temporal_shard`.
- An identified relic is distinguished by Vault Gear state plus `modifier` and `duration` tags.
- `IdentifiableItem.instantIdentify` invokes Vault's configured temporal loot pool.
- `TemporalShardItem.use` enforces Vault presence and special-vault restrictions before consuming and applying a modifier.
- The built-in Soul Shard Pouch intercepts `Inventory.add(ItemStack)` and stores an integer count with an effectively unbounded handler limit.
- The Identification Stand calls `IdentifiableItem.instantIdentify` for eligible inventory items.

## Wold's Vaults

Wold's Vaults makes identified temporal relics stackable by retaining a lightweight identified marker while removing `vaultGearData` and `clientCache`. Temporal Index uses its own compatibility implementation of the same data-minimization concept for reconstructed and book-generated relics.

Wold's Vaults is research material only. Temporal Index does not reference its classes, mod ID, assets, configuration, or runtime behavior, and it is not a build or runtime dependency.

## Sophisticated Backpacks

Sophisticated Backpacks processes `EntityItemPickupEvent` before vanilla inventory insertion. Temporal Index therefore handles supported ground items at highest event priority, then retains the Vault-style `Inventory.add(ItemStack)` interception for direct insertion and other pickup paths.
